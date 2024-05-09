package va.vt.cbilAQuA2.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.watershed.MarkerControlledWatershedTransform3D;
import va.vt.cbilAQuA2.BasicFeatureDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.fea.Evt2LmkProp1Result;
import va.vt.cbilAQuA2.fea.PolyInfo;
import va.vt.cbilAQuA2.fea.Basic;
import va.vt.cbilAQuA2.fea.FeatureTopResult;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.fea.LandMark;
import va.vt.cbilAQuA2.fea.LandMarkDir;
import va.vt.cbilAQuA2.fea.LandMarkDist;
import va.vt.cbilAQuA2.fea.MulBoundary;
import va.vt.cbilAQuA2.fea.NetWork;
import va.vt.cbilAQuA2.fea.Propagation;
import va.vt.cbilAQuA2.fea.ResReg;
import va.vt.cbilAQuA2.run.Step6GetCurveResult;
import va.vt.cbilAQuA2.tools.GaussFilter;

public class Step6Helper {
	static long start;
	static long end;
	
	
	public static Step6Result updateFeature(Opts opts, boolean stg, HashMap<Integer, ArrayList<int[]>> evtLst, ImageDealer imageDealer, int[][][] datR) {
		// update network features after user draw regions
		// regions are all in x, y coordinate, where y need to be flipped for matrix manipulation
		System.out.println("Updating basic, network, region and landmark features");
		float[][][] datOrg = imageDealer.dat1;
		String proPath = imageDealer.proPath;
		
		System.out.println("Updating basic features");
		FtsLst ftsLstE = new FtsLst();
		float[][][] dffMat = null;
		
		if(!stg) {
			FeatureTopResult featureTopResult = getFeaturesTop(datOrg, evtLst, opts);
			ftsLstE = featureTopResult.ftsLst;
			imageDealer.left.tableValueSetting(featureTopResult.minArea, featureTopResult.maxArea, featureTopResult.minPvalue,
					featureTopResult.maxPvalue, featureTopResult.minDecayTau, featureTopResult.maxDecayTau,
					featureTopResult.minDuration, featureTopResult.maxDuration, featureTopResult.mindffMax, featureTopResult.maxdffMax);
			float[] featureTable = new float[] {featureTopResult.minArea, featureTopResult.maxArea, featureTopResult.minPvalue, 
					featureTopResult.maxPvalue, featureTopResult.minDecayTau, featureTopResult.maxDecayTau,
					featureTopResult.minDuration, featureTopResult.maxDuration, featureTopResult.mindffMax, featureTopResult.maxdffMax};
			
			try {
				Helper.writeObjectToFile(proPath, "dffMat.ser", featureTopResult.dffMat);  
	   			Helper.writeObjectToFile(proPath, "dMat.ser", featureTopResult.dMat);  
	   			Helper.writeObjectToFile(proPath, "dffAlignedMat.ser", featureTopResult.dffAlignedMat);  
	   			Helper.writeObjectToFile(proPath, "FtsTableParameters.ser", featureTable);  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				ftsLstE = Helper.readObjectFromFile(proPath, "ftsLstE.ser", ftsLstE.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		
		// propagation features
		getFeaturesProTop(datR,evtLst,ftsLstE,opts);
		
		// region, landmark, network and save results
		int ch = 0;
		updtFeatureRegionLandmarkNetworkShow(datR,evtLst,ftsLstE,opts, imageDealer, ch);
//		System.out.println("ftsE network length " + ftsLstE.network.nOccurSameLoc.length);
		
		
		
		try {
			FileOutputStream f = new FileOutputStream(new File(proPath + "Fts.ser"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(ftsLstE);
			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Step6Result(ftsLstE,dffMat);
	}
	
	public static void updtFeatureRegionLandmarkNetworkShow(int[][][] datR, HashMap<Integer, ArrayList<int[]>> evtLst,
			FtsLst ftsLstE, Opts opts, ImageDealer imageDealer, int ch) {
		int H = datR.length;
		int W = datR[0].length;
		int T = datR[0][0].length;
		
		float muPerPix = opts.spatialRes;
		
		 HashMap<Integer, ArrayList<int[]>> evtx = new HashMap<Integer, ArrayList<int[]>>();
		 for (int i = 1; i <= evtLst.size(); i++) {
			 if(imageDealer.deleteColorSet.contains(i)){
					continue;
			 }
			 if (ch == 1 && imageDealer.deleteColorSet2.contains(i))
				continue;
			 if (ch == 2 && imageDealer.deleteColorSet2.contains(i + imageDealer.nEvtCh1))
				continue;
			 evtx.put(i, evtLst.get(i));
		 }		
		
		boolean[][] evtSpatialMask = new boolean[H][W];
		for(int x = 0; x < H; x++) {
			for(int y = 0; y < W; y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[y][x];
			}
		}	
		
		int[][] regionLabel = imageDealer.regionMarkLabel;
		HashMap<Integer, ArrayList<int[]>> regLst = new HashMap<>();
		for(int x = 0; x < H; x++) {
			for(int y=0; y < W; y++) {
				int label = regionLabel[y][x];
				if(label>0) {
					ArrayList<int[]> l = regLst.get(label);
					if(l == null)
						l = new ArrayList<>();
					
					l.add(new int[] {x,y});
					regLst.put(label, l);
				}
			}
		}
		
		int[][] landMark = new int[H][W]; 
		for(int x = 0; x < H; x++) {
			for(int y=0; y < W; y++) {
				landMark[x][y] = imageDealer.landMarkLabel[y][x];
			}
		}
		HashMap<Integer, ArrayList<int[]>> lmkLst = Helper.label2idx(landMark);
		System.out.println(lmkLst.size());
		
		// LandMark Features
		if(regLst.size()>0 || lmkLst.size()>0) {
			System.out.println("Updating region and landmark features ...");
			ftsLstE.region = getDistRegionBorderMIMO(evtx,datR,regLst,lmkLst,muPerPix,opts.minShow1);
		}
		
		
		// update network feature
		HashMap<Integer, ArrayList<int[]>> evtx1 = new HashMap<>();
		for(int i=1;i<=evtLst.size();i++) {
			evtx1.put(i, evtLst.get(i));
		}
		
		if(regLst.size()>0) {
			for(int i=1;i<=evtLst.size();i++) {
				ArrayList<int[]> loc00 = evtLst.get(i);
				boolean inclu = false;
				for(int[] p:loc00) {
					if(evtSpatialMask[p[0]][p[1]]) {
						inclu = true;
						break;
					}
				}
				if(!inclu) {
					evtx1.put(i, new ArrayList<>());
				}
			}
		}
		
		System.out.println("Output Network");
		ftsLstE.networkAll = getEvtNetworkFeatures(evtLst,H,W,T);
		ftsLstE.network = getEvtNetworkFeatures(evtx1,H,W,T);
		
		System.out.println("fts network length " + ftsLstE.network.nOccurSameLoc.length);
	}
	
	public static NetWork getEvtNetworkFeatures(HashMap<Integer, ArrayList<int[]>> evts, int H, int W, int T) {
		// getEvtNetworkFeatures get network work level features for each event
		// Not include event level features like size and brightness
		// Pre-filter with bounding box overlapping

		int nEvt = evts.size();
		int[] evtSize = new int[nEvt];
		boolean[] idxBad = new boolean[nEvt];
		for(int i=0;i<nEvt;i++)
			idxBad[i] = true;
		HashMap<Integer, HashSet<Integer>> tIdx = new HashMap<>();
		int[][][] evtMap = new int[H][W][T];
		HashMap<Integer, HashSet<Integer>> evtIhw = new HashMap<>();
		int[][] evtTW = new int[nEvt][2];
		
		ArrayList<int[]> pix0;
		HashSet<Integer> ihw;
		int t0, t1;
		
		HashSet<Integer> tSet;
		for (int t = 0; t < T; t++) {
			tSet = new HashSet<>();
			tIdx.put(t, tSet);
		}
		
		for (int nn = 1; nn <= nEvt; nn++) {
			pix0 = evts.get(nn);
			if (pix0.size() > 0) {
				Helper.setValue(evtMap, pix0, nn);
				idxBad[nn - 1] = false;
				ihw = Helper.getUniqueSpa(pix0, H, W);
				evtIhw.put(nn, ihw);
				evtSize[nn - 1] = ihw.size();
				t0 = T;
				t1 = 0;
				for (int[] p:pix0) {
					t0 = Math.min(t0, p[2]);
					t1 = Math.max(t1, p[2]);
					tIdx.get(p[2]).add(nn);
				}				
				evtTW[nn - 1][0] = t0;
				evtTW[nn - 1][1] = t1;
			}
		}
		
		// all events and events with similar size
		int[][] nOccurSameLoc = new int[nEvt][2];
		int[] nOccurSameTime = new int[nEvt];
		HashMap<Integer,ArrayList<Integer>> occurSameLocList = new HashMap<>();
		HashMap<Integer,ArrayList<Integer>> occurSameLocList2 = new HashMap<>();
		HashMap<Integer,HashSet<Integer>> occurSameTimeList = new HashMap<>();
		HashSet<Integer> lst, lst2;
		int[] p;
		float szMe, szCo;
		for(int i = 1;i <= nEvt; i++) {
			if(i % 1000 == 0)
				System.out.println(i+1);
			if(idxBad[i - 1])
				continue;
			
			ihw = evtIhw.get(i);
			lst = new HashSet<>();
			for (int index:ihw) {
				p = Helper.ind2sub(H, W, index);
				for (int t = 0; t < T; t++) {
					if (evtMap[p[0]][p[1]][t] > 0)
						lst.add(evtMap[p[0]][p[1]][t]);
				}
			}
			occurSameLocList.put(i, new ArrayList(lst));
			nOccurSameLoc[i - 1][0] = lst.size();
			
			lst2 = new HashSet<>();
			szMe = evtSize[i - 1];
			for (int index : lst) {
				szCo = evtSize[index - 1];
				if (szMe / szCo < 2 && szMe / szCo > 0.5) {
					lst2.add(index);
				}
			}
			occurSameLocList2.put(i, new ArrayList(lst2));
			nOccurSameLoc[i - 1][1] = lst2.size();
			
			// occur at same time
			t0 = evtTW[i - 1][0];
			t1 = evtTW[i - 1][1];
			lst = new HashSet<>();
			for (int t = t0; t <= t1; t++) {
				lst.addAll(tIdx.get(t));
			}
			occurSameTimeList.put(i, lst);
			nOccurSameTime[i - 1] = lst.size();
		}
		
		// output ------
		return new NetWork(nOccurSameLoc,nOccurSameTime,occurSameLocList,occurSameLocList2,occurSameTimeList);
	}
	
	public static ResReg getDistRegionBorderMIMO(HashMap<Integer, ArrayList<int[]>> evts, int[][][] datS,
			HashMap<Integer, ArrayList<int[]>> regLst, HashMap<Integer, ArrayList<int[]>> lmkLst, float muPerPix,
			float minThr) {
		int H = datS.length;
		int W = datS[0].length;
		int T = datS[0][0].length;
		
		int nEvts = evts.size();
		int nReg = regLst.size();
		int nLmk = lmkLst.size();
		
		ResReg resReg = new ResReg();
		// landmarks
		PolyInfo lmkInfo = null;
		if(nLmk>0) {
			// regions are flipped here
			lmkInfo = getPolyInfo(lmkLst,H,W,T);
			
			resReg.landMark.mask = lmkInfo.polyMask;
			resReg.landMark.center = lmkInfo.polyCenter;
			resReg.landMark.border = lmkInfo.polyBorder;
			
			for(int i=0;i<lmkInfo.polyAvgDist.length;i++) {
				lmkInfo.polyAvgDist[i] *= muPerPix;
			}
			resReg.landMark.centerBorderAvgDist = lmkInfo.polyAvgDist;
			
			// distances to landmarks
			resReg.landmarkDist = evt2lmkProp(evts,lmkInfo.polyBorder,H,W,T,muPerPix);
			
			// frontier based propagation features related to landmark
			resReg.landmarkDir = evt2lmkPropWrap(datS, evts, lmkInfo.polyMask, muPerPix, minThr);
		}else {
			resReg.landMark = new LandMark();
			resReg.landmarkDist = new LandMarkDist();
			resReg.landmarkDir = new LandMarkDir();
		}
		
		// -------------------------------------------------------------------------------------------
		// regions
		if(nReg>0) {
			PolyInfo regionInfo = getPolyInfo(regLst,H,W,T);
			
			// landmark and region relationships
			boolean[][] incluLmk = null;
			if(nLmk>0) {
				incluLmk = new boolean[nReg][nLmk];
				for(int i=0;i<nReg;i++) {
					boolean[][] map00 = regionInfo.polyMask.get(i+1);
					for(int j=0;j<nLmk;j++) {
						boolean[][] map11 = lmkInfo.polyMask.get(j+1);
						boolean inclu = false;
						for(int x=0;x<H;x++) {
							for(int y=0;y<W;y++) {
								if(map00[x][y] && map11[x][y]) {
									inclu = true;
									break;
								}
							}
							if(inclu)
								break;
						}
						incluLmk[i][j] = inclu;
					}
				}
			}
			
			// distance to region boundary for events in a region
			boolean[][] memberIdx = new boolean[nEvts][nReg];
			float[][] dist2border = new float[nEvts][nReg];
			float[][] dist2borderNorm = new float[nEvts][nReg];
			for(int i=0;i<nEvts;i++) {
				ArrayList<int[]> loc0 = evts.get(i+1);
				boolean flag = false;
				float centerX = 0;
				float centerY = 0;
				
				
				
				for(int j=0;j<nReg;j++) {
					boolean[][] msk0 = regionInfo.polyMask.get(j+1);
					boolean contains = false;
					for(int[] p:loc0) {
						if(msk0[p[0]][p[1]]) {
							contains = true;
							break;
						}
					}
					if(contains) {
						memberIdx[i][j] = true;
						if(!flag) {
							float sumX = 0;
							float sumY = 0;
							for(int[] p:loc0) {
								sumX += p[0];
								sumY += p[1];
							}
							centerX = Math.round(sumX/loc0.size());
							centerY = Math.round(sumY/loc0.size());
						}
						flag = true;
						ArrayList<int[]> cc = regionInfo.polyBorder.get(j+1);
						float minDist = Float.MAX_VALUE;
						for(int[] p:cc) {
							float dist = (p[0]-centerX)*(p[0]-centerX) + (p[1]-centerY)*(p[1]-centerY);
							minDist = Math.min(minDist, dist);
						}
						dist2border[i][j] = (float) Math.sqrt(minDist) * muPerPix;
						dist2borderNorm[i][j] = dist2border[i][j]/regionInfo.polyAvgDist[j];
					}
				}
			}
			
			for(int i=0;i<regionInfo.polyAvgDist.length;i++) {
				regionInfo.polyAvgDist[i] *= muPerPix;
			}
			
			
			resReg.cell.mask = regionInfo.polyMask;
			resReg.cell.center = regionInfo.polyCenter;
			resReg.cell.border = regionInfo.polyBorder;
			resReg.cell.centerBorderAvgDist = regionInfo.polyAvgDist;
			resReg.cell.incluLmk = incluLmk;
			resReg.cell.memberIdx = memberIdx;
			resReg.cell.dist2border = dist2border;
			resReg.cell.dist2borderNorm = dist2borderNorm;
			
		}
		
		return resReg;
		
	}
	
	public static LandMarkDir evt2lmkPropWrap(int[][][] dRecon, HashMap<Integer, ArrayList<int[]>> evts,
			HashMap<Integer, boolean[][]> lmkMsk, float muPerPix, float minThr) {
		int H = dRecon.length;
		int W = dRecon[0].length;
//		int T = dRecon[0][0].length;
		
//		float m3 = muPerPix*muPerPix*muPerPix;
		
		float[] thrRg = new float[(int) (Math.round((0.9-minThr)*10))+1];
		thrRg[0] = minThr;
		for(int i=1;i<thrRg.length;i++) {
			thrRg[i] = thrRg[i-1] + 0.1f;
		}
		
		// landmarks
		int nEvts = evts.size();
		int nLmk = lmkMsk.size();
		
		HashMap<Integer, ArrayList<int[]>> lmkLst = new HashMap<>();
		for(int i=1;i<=nLmk;i++) {
			ArrayList<int[]> points = new ArrayList<>();
			boolean[][] msk = lmkMsk.get(i);
			for(int x=0;x<H;x++) {
				for(int y=0;y<W;y++) {
					if(msk[x][y]) {
						points.add(new int[] {x,y});
					}
				}
			}
			lmkLst.put(i, points);
		}
		
		// extract blocks
		HashMap<Integer, HashMap<Integer, float[][]>> minDistToLandMark = new HashMap<>();
		HashMap<Integer, HashMap<Integer, float[][]>> maxDistToLandMark = new HashMap<>();
		
		ArrayList<int[]> lmkPix;
		for(int nn=1;nn<=nEvts;nn++) {
			if(nn%100 == 0)
				System.out.println("EvtLmk: "+ nn);
			
			ArrayList<int[]> evt0 = evts.get(nn);
			if(evt0==null)
				continue;
			
			minDistToLandMark.put(nn, new HashMap<Integer, float[][]>());
			maxDistToLandMark.put(nn, new HashMap<Integer, float[][]>());
			
			for(int ii = 1; ii <= nLmk; ii++) {
				lmkPix = lmkLst.get(ii);
				int rghs = Integer.MAX_VALUE;
				int rgws = Integer.MAX_VALUE;
				int rgts = Integer.MAX_VALUE;
				int rghe = Integer.MIN_VALUE;
				int rgwe = Integer.MIN_VALUE;
				int rgte = Integer.MIN_VALUE;
				for (int[] p:evt0) {
					rghs = Math.min(rghs, p[0]);
					rgws = Math.min(rgws, p[1]);
					rgts = Math.min(rgts, p[2]);
					rghe = Math.max(rghe, p[0]);
					rgwe = Math.max(rgwe, p[1]);
					rgte = Math.max(rgte, p[2]);
				}
				for (int[] p:lmkPix) {
					rghs = Math.min(rghs, p[0]);
					rgws = Math.min(rgws, p[1]);
					rghe = Math.max(rghe, p[0]);
					rgwe = Math.max(rgwe, p[1]);
				}
				
				int rgHs = Math.max(rghs-2, 0);
				int rgHe = Math.min(rghe+2, H-1);
				int rgWs = Math.max(rgws-2, 0);
				int rgWe = Math.min(rgwe+2, W-1);
				int rgTs = rgts;
				int rgTe = rgte;
				int H1 = rgHe - rgHs + 1;
				int W1 = rgWe - rgWs + 1;
				int T1 = rgTe - rgTs + 1;
				float[][][] datS = new float[H1][W1][T1];
				for (int[] p:evt0) {
					datS[p[0] - rgHs][p[1] - rgWs][p[2] - rgTs] = ((float) (dRecon[p[0]][p[1]][p[2]])) / 255; 
				}
				boolean[][] msk0 = new boolean[H1][W1];
				for (int[] p:lmkPix) {
					msk0[p[0] - rgHs][p[1] - rgWs] = true;
				}
				float[][][] res = evt2lmkProp2(datS,msk0,thrRg,muPerPix);
				minDistToLandMark.get(nn).put(ii, res[0]);
				maxDistToLandMark.get(nn).put(ii, res[1]);
			}
		}
		
		return new LandMarkDir(minDistToLandMark, maxDistToLandMark);
	}
	
	public static float[][][] evt2lmkProp2(float[][][] datS, boolean[][] msk0, float[] thrRg, float muPerPix) {
		int H = datS.length;
		int W = datS[0].length;
		int sck = (int) Math.max(1, Math.round(Math.sqrt(((float)H*W)/10000)));
		float[][][] datSx = Helper.imResize(datS, sck);
		boolean[][] lmkMsk = Helper.imResize(msk0, sck);
		ArrayList<int[]> lmkMskBd = BasicFeatureDealer.findBoundary(lmkMsk);
		
		H = datSx.length;
		W = datSx[0].length;
		int T = datSx[0][0].length;
		int nThr = thrRg.length;
		
		ArrayList<int[]> curPix = new ArrayList<>();
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int t = 0; t < T; t++) {
					if (datSx[x][y][t] > 0)
						curPix.add(new int[] {x, y, t});
				}
			}
		}
		
		float[] maxDist, minDist;
		float dist;
		float[][][] res = new float[2][T][nThr];
		for (int kk = 0; kk < nThr; kk++) {
			
			minDist = new float[T];
			maxDist = new float[T];
			for (int t = 0; t < T; t++) {
				res[0][t][kk] = Float.NaN;
				res[1][t][kk] = Float.NaN;
				minDist[t] = Float.POSITIVE_INFINITY;
				maxDist[t] = 0;
			}
			
			for (int[] p : curPix) {
				if (datSx[p[0]][p[1]][p[2]] > thrRg[kk]) {
					for (int[] p1 : lmkMskBd) {
						dist = (p[0] - p1[0]) * (p[0] - p1[0]) + (p[1] - p1[1]) * (p[1] - p1[1]);
						dist = (float) Math.sqrt(dist) * muPerPix * sck;
						maxDist[p[2]] = Math.max(dist, maxDist[p[2]]);
						minDist[p[2]] = Math.min(dist, minDist[p[2]]);
						if (lmkMsk[p[0]][p[1]])
							minDist[p[2]] = 0;
					}
					
				}
			}
			
			for (int t = 0; t < T; t++) {
				if (minDist[t] < Float.POSITIVE_INFINITY)
					res[0][t][kk] = minDist[t];
				if (maxDist[t] > 0)
					res[1][t][kk] = maxDist[t];
			}
		}
		
		return res;
	}
	
	public static LandMarkDist evt2lmkProp(HashMap<Integer, ArrayList<int[]>> evts, HashMap<Integer, ArrayList<int[]>> lmkBorder,
			int H, int W, int T, float muPerPix) {
		int nEvt = evts.size();
		int nLmk = lmkBorder.size();
		
		// distance to landmark
		HashMap<Integer, float[][]> d2lmk = new HashMap<>();
		float[][] d2lmkAvg = new float[nEvt][nLmk];
		float[][] d2lmkMin = new float[nEvt][nLmk];
		
		
		for(int i=1;i<=evts.size();i++) {
			if(i%100==0)
				System.out.println("lmkDist: " + i);
			
			ArrayList<int[]> loc0 = evts.get(i);
			int tRgs = Integer.MAX_VALUE;
			int tRge = Integer.MIN_VALUE;
			for(int[] p:loc0) {
				tRgs = Math.min(tRgs, p[2]);
				tRge = Math.max(tRge, p[2]);
			}
			float[][] distPix = new float[tRge-tRgs+1][nLmk];
			float[][] distPixMax = new float[tRge-tRgs+1][nLmk];
			
			
			for(int t=0;t<=tRge-tRgs;t++) {
				for(int j=0;j<nLmk;j++) {
					distPix[t][j] = Float.NaN;
					ArrayList<int[]> cc = lmkBorder.get(j+1);
					float xx = Float.MAX_VALUE;
					float xx2 = Float.MAX_VALUE;
					for(int[] xy:cc) {
						float maxDist = Float.NEGATIVE_INFINITY;
						for(int[] p:loc0) {
							if(p[2] == t + tRgs) {
								float tmp = (p[0]-xy[0])*(p[0]-xy[0]) + (p[1]-xy[1])*(p[1]-xy[1]);
								xx = Math.min(xx, tmp);
								maxDist = Math.min(maxDist, tmp);
							}
						}
						xx2 = Math.min(xx2, maxDist);
					}
					distPix[t][j] = (float) Math.sqrt(xx) * muPerPix;
					distPixMax[t][j] = (float) Math.sqrt(xx2) * muPerPix;
					// cleaning
					if(Float.isNaN(distPix[t][j])) {
						if(t>0) {
							distPix[t][j] = distPix[t-1][j];
							distPixMax[t][j] = distPixMax[t-1][j];
						}
					}
				}
			}
			
			// distance to landmark
			d2lmk.put(i, distPix); 					// shortest distance to landmark at each frame
			for(int j=0;j<nLmk;j++) {
				float sum = 0;
				int cnt = 0;
				float min = Float.MAX_VALUE;
				for(int t=0;t<distPix.length;t++) {
					if(!Float.isNaN(distPix[t][j])) {
						sum += distPix[t][j];
						cnt ++;
						min = Math.min(min, distPix[t][j]);
					}
				}
				d2lmkAvg[i-1][j] = sum/cnt;			// average distance to the landmark
				d2lmkMin[i-1][j] = min;				// minimum distance to the landmark
			}
		}
		
		return new LandMarkDist(d2lmk,d2lmkAvg,d2lmkMin);
		
	}
	
	public static PolyInfo getPolyInfo(HashMap<Integer, ArrayList<int[]>> lmkLst, int H, int W, int T) {
		int nPoly = lmkLst.size();
		float[][] polyCenter = new float[nPoly][2];
		HashMap<Integer, boolean[][]> polyMask = new HashMap<>();
		HashMap<Integer, ArrayList<int[]>> polyBorder = new HashMap<>();
		float[] polyAvgDist = new float[nPoly];
		
		for(int i=1;i<=nPoly;i++) {
			ArrayList<int[]> poly0 = lmkLst.get(i);
			boolean[][] msk = new boolean[H][W];
			
			float sumX = 0;
			float sumY = 0;
			
			for(int[] p:poly0) {
				msk[p[0]][p[1]] = true;
				sumX += p[0];
				sumY += p[1];
			}
			
			float centerX = Math.round(sumX/poly0.size());
			float centerY = Math.round(sumY/poly0.size());
			
			polyCenter[i-1][0] = centerX;
			polyCenter[i-1][1] = centerY;
			
			polyMask.put(i, msk);
			ArrayList<int[]> boundary = BasicFeatureDealer.findBoundary(msk);
			polyBorder.put(i, boundary);
			
			ArrayList<Double> dist = new ArrayList<>();
			for(int[] p:boundary) {
				double dt = (p[0]-centerX)*(p[0]-centerX) + (p[1]-centerY)*(p[1]-centerY);
				dist.add(dt);
			}
			float avgDist = (float)Math.max(1, Math.round(Math.sqrt(Helper.getMedian(dist))));
			polyAvgDist[i-1] = avgDist;
		}
		
		return new PolyInfo(polyMask,polyCenter,polyBorder,polyAvgDist);
	}
	
	public static void getFeaturesProTop(int[][][] evtRec, HashMap<Integer, ArrayList<int[]>> evtLst,
			FtsLst ftsLst, Opts opts) {
		int H = evtRec.length;
		int W = evtRec[0].length;
		int T = evtRec[0][0].length;
		
		float norm = (float) Math.sqrt(opts.northx * opts.northx + opts.northy * opts.northy);
		float[] northDi = new float[] {opts.northx / norm, opts.northy / norm};	
		float muPix = opts.spatialRes;
		
		for(int i = 1;i <= evtLst.size();i++) {
			if(i % 100 == 0)
				System.out.println(i + "/" + evtLst.size());
			
			ArrayList<int[]> pix0 = evtLst.get(i);
			if(pix0==null || pix0.size()==0)
				continue;
			
			int rghs = Integer.MAX_VALUE;
			int rgws = Integer.MAX_VALUE;
			int rghe = Integer.MIN_VALUE;
			int rgwe = Integer.MIN_VALUE;
			for(int[] p:pix0) {
				rghs = Math.min(rghs, p[0]);
				rghe = Math.max(rghe, p[0]);
				rgws = Math.min(rgws, p[1]);
				rgwe = Math.max(rgwe, p[1]);
			}

			rghs = Math.max(rghs - 1, 0);
			rgws = Math.max(rgws - 1, 0);
			rghe = Math.min(rghe + 1, H - 1);
			rgwe = Math.min(rgwe + 1, W - 1);
			
			int rgts = ftsLst.curve.tBegin.get(i);
			int rgte = ftsLst.curve.tEnd.get(i);
			
			// basic and propagation features
			float[][][] voxr = new float[rghe-rghs+1][rgwe-rgws+1][rgte-rgts+1];
			
			for(int[] p: pix0) {
				int x = p[0];
				int y = p[1];
				int t = p[2];
				voxr[x-rghs][y-rgws][t-rgts] = (float)(evtRec[x][y][t])/255;
			}
			
			getPropagationCentroidQuad(voxr,muPix,i,ftsLst.propagation,northDi,opts);
			
		}
		
		ftsLst.notes.propDirectionOrder = new String[] {"Anterior", "Posterior", "Left", "Right"};
		
	}
	
	private static void getPropagationCentroidQuad(float[][][] volr1, float muPerPix, int nEvt,
			Propagation ftsPg, float[] northDi, Opts opts) {
		// getFeatures extract local features from events
		// specify direction of 'north', or anterior
		// not good at tracking complex propagation
		
		int H = volr1.length;
		int W = volr1[0].length;
		int T = volr1[0][0].length;
				
		// make coordinate correct
		float[][][] volr0 = new float[H][W][T];
		
		for(int x=0;x<H;x++) {
			for(int y=0;y<W;y++) {
				for(int t=0;t<T;t++) {
					volr0[x][y][t] = volr1[H - x - 1][y][t];
				}
			}
		}
		
		float a = northDi[0];
		float b = northDi[1];
		
		float[][] kDi = new float[4][2];
		kDi[0] = new float[] {a,b};
		kDi[1] = new float[] {-a,-b};
		kDi[2] = new float[] {-b,a};
		kDi[3] = new float[] {b,-a};
		
		// propagation features
		float[] thr0 = new float[(int) ((opts.propthrmax - opts.propthrmin) / opts.propthrstep) + 1];	// significant propagation (increase of reconstructed signal)
		
		for(int i = 0; i < thr0.length; i++) {
			thr0[i] = opts.propthrmin + opts.propthrstep*i;
		}
		
		int nThr = thr0.length;
		int nPix = 0;
		int[][] sigMap = new int[H][W];
		for(int x = 0; x < H; x++) {
			for(int y = 0; y < W; y++) {
				for(int t=0;t<T;t++) {
					if(volr0[x][y][t] >= thr0[0]) {
						sigMap[x][y]++;
					}
				}
				if(sigMap[x][y] > 0)
					nPix ++;
			}
		}
		
		// time window for propagation
		int t0 = Integer.MAX_VALUE;
		int t1 = Integer.MIN_VALUE;
		for(int t=0;t<T;t++) {
			float max = -Float.MAX_VALUE;
			for(int x = 0; x < H; x++) {
				for(int y = 0; y < W; y++) {
					max = Math.max(max,volr0[x][y][t]);
				}
			}
			if(max >= thr0[0]) {
				t0 = Math.min(t0, t);
				t1 = Math.max(t1, t);
			}
		}
		
		// centroid of earliest frame as starting point 
		float sumWeight = 0;
		float sumXWeight = 0;
		float sumYWeight = 0;
		for(int x = 0; x < H; x++) {
			for(int y = 0; y < W; y++) {
				if(t0!=Integer.MAX_VALUE && volr0[x][y][t0] >= thr0[0]) {
					sumWeight += volr0[x][y][t0];
					sumXWeight += x * volr0[x][y][t0];
					sumYWeight += y * volr0[x][y][t0];
				}
			}
		}

		float seedhInit = sumXWeight/sumWeight;
		float seedwInit = sumYWeight/sumWeight;
		int h0 = Math.max(Math.round(seedhInit),0);
		int w0 = Math.max(Math.round(seedwInit),0);
		
		// mask for directions: north, south, west, east
		boolean[][][] msk = new boolean[H][W][4];
		for(int i = 0; i < 4; i++) {
			for(int y = 0; y < H; y++) {
				for(int x = 0; x < W; x++) {
					boolean ixSel = false;
					switch(i) {
						case 0:
							ixSel = (float)y > -((float)a)/((float)b)*(x-w0) + h0; break;
						case 1:
							ixSel = (float)y < -((float)a)/((float)b)*(x-w0) + h0; break;
						case 2:
							ixSel = (float)y > ((float)b)/((float)a)*(x-w0) + h0; break;
						case 3:
							ixSel = (float)y < ((float)b)/((float)a)*(x-w0) + h0; break;
					}
					msk[y][x][i] = ixSel;
				}
			}
		}
		
		// locations of centroid
		float[][][] sigDist = new float[T][4][nThr];		// weighted distance for each frame (four directions)
		int[][] pixNum = new int[T][nThr];					// pixel number increase
		for(int t = 0; t < T; t++) {
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < nThr; j++) {
					sigDist[t][i][j] = Float.NaN;
				}
			}
		}
		
		for(int t = t0; t <= t1; t++) {
			for(int k = 0; k < nThr; k++) {
				
				for(int x = 0; x < H; x++) {
					for(int y = 0; y < W; y++) {
						if(volr0[x][y][t] >= thr0[k])
							pixNum[t][k]++;
					}
				}
				
				for(int i = 0; i < 4; i++) {
					float sumX = 0;
					float sumY = 0;
					int cnt = 0;
					for(int x = 0; x < H; x++) {
						for(int y = 0; y < W; y++) {
							if(volr0[x][y][t] >= thr0[k] && msk[x][y][i]) {
								sumX += x;
								sumY += y;
								cnt++;
							}
						}
					}
					
					if(cnt < 4)
						continue;
					
					float seedh = sumX / cnt;
					float seedw = sumY / cnt;
					float dh = seedh - seedhInit;
					float dw = seedw - seedwInit;
					
					sigDist[t][i][k] = dw * kDi[i][0] + dh*kDi[i][1];
				}
			}
		}
		
		float[][][] prop = new float[T][4][nThr];

		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < nThr; j++) {
				for(int t = 0; t < T; t++) {
					if(t>0) {
						prop[t][i][j] = sigDist[t][i][j] - sigDist[t - 1][i][j];
					}else {
						prop[t][i][j] = Float.NaN;
					}
				}
			}
		}
		
		// propGrowMultiThr
		float[][] propGrow = new float[T][4];
		for(int t = 0; t < T; t++) {
			for(int i = 0; i < 4; i++) {
				float max = -Float.MAX_VALUE;
				for(int j = 0; j < nThr; j++) {
					if(!Float.isNaN(prop[t][i][j]) && prop[t][i][j]>=0) {
						max = Math.max(max, prop[t][i][j]);
					}
				}
				if(max==-Float.MAX_VALUE)
					max = 0;
				propGrow[t][i] = max;
			}
		}
		float[] propGrowOverall = new float[4];
		for(int i = 0; i < 4; i++) {
			for(int t = 0; t < T; t++) {
				propGrowOverall[i] += propGrow[t][i];
			}
		}
		
		// propShrinkMultiThr
		float[][] propShrink = new float[T][4];
		for(int t = 0; t < T; t++) {
			for(int i = 0; i < 4; i++) {
				float max = -Float.MAX_VALUE;
				for(int j = 0;j < nThr; j++) {
					if(!Float.isNaN(prop[t][i][j]) && prop[t][i][j] <= 0) {
						max = Math.max(max, prop[t][i][j]);
					}
				}
				if(max == -Float.MAX_VALUE)
					max = 0;
				propShrink[t][i] = max;
			}
		}
		float[] propShrinkOverall = new float[4];
		for(int i = 0; i < 4; i++) {
			for(int t = 0; t < T; t++) {
				propShrinkOverall[i] += propShrink[t][i];
			}
		}
		
		// pixNumChange
		int[][] pixNumChange = new int[T][nThr];
		float[] pixNumChangeRate = new float[T];
		for(int j=0;j<nThr;j++) {
			for(int t=1;t<T;t++) {
				pixNumChange[t][j] = pixNum[t][j] - pixNum[t-1][j];
			}
		}
		float absRate;
		int id = 0;
		for(int t=0;t<T;t++) {
			float max = -Float.MAX_VALUE;
			for(int j=0;j<nThr;j++) {
				absRate = Math.abs((float)(pixNumChange[t][j])/nPix);  
				if (absRate > max) {
					max = absRate;
					id = j;
				}
			}
			pixNumChangeRate[t] = Math.abs((float)(pixNumChange[t][id])/nPix);  
		}
		
		// output
		for(int t=0;t<T;t++) {
			for(int i=0;i<4;i++) {
				propGrow[t][i] = propGrow[t][i] * muPerPix;
				propShrink[t][i] = propShrink[t][i] * muPerPix;
			}
		}
		for(int i=0;i<4;i++) {
			propGrowOverall[i] = propGrowOverall[i] * muPerPix;
			propShrinkOverall[i] = propShrinkOverall[i] * muPerPix;
		}
		float[][] areaChange = new float[T][nThr];
		float[][] areaFrame = new float[T][nThr];
		for(int t=0;t<T;t++) {
			for(int j=0;j<nThr;j++) {
				areaChange[t][j] = pixNumChange[t][j] * muPerPix * muPerPix;
				areaFrame[t][j] = pixNum[t][j] * muPerPix * muPerPix;
			}
		}
		
		ftsPg.propGrow.put(nEvt, propGrow);
		ftsPg.propGrowOverall.put(nEvt, propGrowOverall);
		ftsPg.propShrink.put(nEvt, propShrink);
		ftsPg.propShrinkOverall.put(nEvt, propShrinkOverall);
		ftsPg.areaChange.put(nEvt, areaChange);
		ftsPg.areaChangeRate.put(nEvt, pixNumChangeRate);
		ftsPg.areaFrame.put(nEvt, areaFrame);
//		ftsPg.propMaxSpeed.put(nEvt, propMaxSpeed);
	}
	
	public static FeatureTopResult getFeaturesTop(float[][][] dat, HashMap<Integer, ArrayList<int[]>> evtLst, Opts opts) {
		int H = dat.length;
		int W = dat[0].length;
		int T = dat[0][0].length;
		
		float minArea = Float.MAX_VALUE;
		float maxArea = -Float.MAX_VALUE;
		float minPvalue = Float.MAX_VALUE;
		float maxPvalue = -Float.MAX_VALUE;
		float minDecayTau = Float.MAX_VALUE;
		float maxDecayTau = -Float.MAX_VALUE;
		float mindffMax = Float.MAX_VALUE;
		float maxdffMax = -Float.MAX_VALUE;
		float minDuration = Float.MAX_VALUE;
		float maxDuration = -Float.MAX_VALUE;		
		
		int[][][] evtMap = new int[H][W][T];
		for (int i = 1; i <= evtLst.size(); i++) {
			Helper.setValue(evtMap, evtLst.get(i), i);
		}
		
		float secondPerFrame = opts.frameRate;
		float muPerPix = opts.spatialRes;
		
		float[][][] datx = Helper.copy3Darray(dat);
		for(int t = 0;t < T;t++) {
			for(int y = 0;y < W;y++) {
				for(int x = 0;x < H;x++) {
					if(evtMap[x][y][t]>0)
						datx[x][y][t] = Float.NaN;
				}
			}
		}
		
		// impute events
		System.out.println("Imputing");
		imputeMov(datx);
		
		int Tww = opts.movAvgWin;
		float baselineBias = Step1Helper.obtainBias(Tww, opts.cut);
		FtsLst ftsLst = new FtsLst();
				
		int nEvt = evtLst.size();
		float[][][] dMat = new float[nEvt][T][2];
		float[][][] dffMat = new float[nEvt][T][2];
		float[][] dffAlignedMat = new float[nEvt][111];
		boolean[][] voxi = null;
		
		ArrayList<int[]> pix0;
		HashSet<Integer> ihw;
		float sigma1;
		float[] charx1, charxBg1, dff1, charx2, dff2, dff1e, df1;
		boolean[] sigxOthers;
		int[] rgT1;
		int rghs, rghe, rgws, rgwe, rgts, rgte;
		for(int i=1;i<=nEvt;i++) {
			if(i%100==0)
				System.out.println(i + "/" + nEvt);
			
			pix0 = evtLst.get(i);
			if(pix0==null || pix0.size()==0)
				continue;
			
			ihw = Helper.getUniqueSpa(pix0, H, W);
			
			rghs = Integer.MAX_VALUE;
			rghe = Integer.MIN_VALUE;
			rgws = Integer.MAX_VALUE;
			rgwe = Integer.MIN_VALUE;
			rgts = Integer.MAX_VALUE;
			rgte = Integer.MIN_VALUE;
			for(int[] p:pix0) {
				rghs = Math.min(rghs, p[0]);
				rghe = Math.max(rghe, p[0]);
				rgws = Math.min(rgws, p[1]);
				rgwe = Math.max(rgwe, p[1]);
				rgts = Math.min(rgts, p[2]);
				rgte = Math.max(rgte, p[2]);
			}
			
			int its = rgts;
			int ite = rgte;
			charx1 = Helper.getAvgCurve(dat, ihw);
			for (int t = 0; t < T; t++) {
				charx1[t] = charx1[t] * (opts.maxValueDat - opts.minValueDat) + opts.minValueDat;	
			}
			sigma1 = avgDatNoiseEst(charx1,ihw,opts);
			
			// correct baseline method
			charxBg1 = getBaseline(charx1, Tww, opts.cut);
			dff1 = new float[T];
			df1 = new float[T];
			for (int t = 0; t < T; t++) {
				charxBg1[t] -= baselineBias * sigma1;
				df1[t] = charx1[t] - charxBg1[t];
				dff1[t] = (charx1[t] - charxBg1[t]) / (charxBg1[t] + 0.0001f);
			}
			float sigma1dff = Math.max(0.0001f, Helper.estimateNoiseByMean(dff1));
			float dfMax1 = Float.NEGATIVE_INFINITY;
			float dffMax1 = Float.NEGATIVE_INFINITY;
			charx2 = Helper.getAvgCurve(datx, ihw);
			for (int t = 0; t < T; t++) {
				charx2[t] = charx2[t] * (opts.maxValueDat - opts.minValueDat) + opts.minValueDat;	
			}
			for (int t = rgts; t <= rgte; t++) {
				dfMax1 = Math.max(dfMax1, df1[t]);
				dffMax1 = Math.max(dffMax1, dff1[t]);
				charx2[t] = charx1[t];
			}
			dff2 = new float[T];
			for (int t = 0; t < T; t++) {
				dff2[t] = (charx2[t] - charxBg1[t]) / (charxBg1[t] + 0.0001f);
			}			
			
			// for p values
			float[] dff2Sel = new float[rgte - rgts + 1];
			for(int t = rgts;t <= rgte;t++) {
				dff2Sel[t - rgts] = dff2[t];
			}
			int tMax = 0;
			float dffMax2 = -Float.MAX_VALUE;
			for(int t=0;t < dff2Sel.length;t++) {
				if(dffMax2 < dff2Sel[t]) {
					tMax = t;
					dffMax2 = dff2Sel[t];
				}
			}
			float xMinPre = Float.MAX_VALUE;
			float xMinPost = Float.MAX_VALUE;
			for(int t = 0;t <= tMax;t++) {
				xMinPre = Math.min(dff2Sel[t], xMinPre);
			}
			for(int t = tMax;t < dff2Sel.length;t++) {
				xMinPost = Math.min(dff2Sel[t], xMinPost);
			}
//			float xMinPre = Math.max(dff2SelMinPre, sigma1dff);
//			float xMinPost = Math.max(dff2SelMinPos, sigma1dff);
			float dffMaxZ = Math.max((dffMax2-xMinPre+dffMax2-xMinPost)/sigma1dff/2, 0);
			NormalDistribution normal = new NormalDistribution();
			float dffMaxPval = (float) (1 - normal.cumulativeProbability(dffMaxZ));
			
			// extend event window in the curve
			sigxOthers = new boolean[T];
			for(int t=0;t<T;t++) {
				sigxOthers[t] = charx2[t]!=charx1[t];
			}
			
			rgT1 = extendEventTimeRangeByCurve(dff1,sigxOthers,its,ite);
			int rgT1s = rgT1[0];
			int rgT1e = rgT1[1];
			dff1e = new float[rgT1e - rgT1s + 1];
			for(int t = rgT1s; t <= rgT1e;t++) {
				dff1e[t - rgT1s] = dff2[t];
			}
			
			// curve features
			Step6GetCurveResult curveResult = getCurveStat(dff1e, secondPerFrame, opts.ignoreTau);
			curveResult.riseTime += rgT1s; 
			
			
			for(int t=0;t<T;t++) {
				dffMat[i-1][t][0] = dff1[t];
				dffMat[i-1][t][1] = dff2[t];
				dMat[i-1][t][0] = charx1[t];
				dMat[i-1][t][1] = charx2[t];
			}
			
			// table value
			minDuration = Math.min(minDuration, curveResult.width55);
			maxDuration = Math.max(maxDuration, curveResult.width55);
			mindffMax = Math.min(mindffMax, dffMax1);
			maxdffMax = Math.max(maxdffMax, dffMax1);
			minPvalue = Math.min(dffMaxPval, minPvalue);
			maxPvalue = Math.max(dffMaxPval, maxPvalue);
			minDecayTau = Math.min(minDecayTau, curveResult.decayTau);
			maxDecayTau = Math.max(maxDecayTau, curveResult.decayTau);
			
			// update
			ftsLst.loc.t0.put(i, its);
			ftsLst.loc.t1.put(i, ite);
			ftsLst.curve.tBegin.put(i, its);
			ftsLst.curve.tEnd.put(i, ite);
			ftsLst.loc.xSpaTemp.put(i, pix0);
			ftsLst.loc.xSpa.put(i, ihw);
			ftsLst.curve.rgt1.put(i, rgT1);
			ftsLst.curve.dfMax.put(i, dfMax1);
			ftsLst.curve.dffMax.put(i, dffMax1);
			ftsLst.curve.dffMax2.put(i, dffMax2);
			ftsLst.curve.dffMaxFrame.put(i, tMax + rgts);
			ftsLst.curve.dffMaxZ.put(i, dffMaxZ);
			ftsLst.curve.dffMaxPval.put(i, dffMaxPval);
			ftsLst.curve.duration.put(i,(ite-its+1)*secondPerFrame);
			ftsLst.curve.rise19.put(i, curveResult.rise19);
			ftsLst.curve.fall91.put(i, curveResult.fall91);
			ftsLst.curve.width55.put(i, curveResult.width55);
			ftsLst.curve.width11.put(i, curveResult.width11);
			ftsLst.curve.decayTau.put(i, curveResult.decayTau);
			ftsLst.curve.riseTime.put(i, curveResult.riseTime);
			ftsLst.curve.dff1Begin.put(i, rgT1s + curveResult.pp[0][0]);
			ftsLst.curve.dff1End.put(i, rgT1s + curveResult.pp[0][1]);
			
			// curve from 10 frame previous to 10% rising, to next 100 frames following 10% rising
			int t0 =  rgT1s + curveResult.pp[0][0];
			for (int t = -10; t <= 100; t++) {
				if (t + t0 >= 0 && t + t0 < T)
					dffAlignedMat[i - 1][t + 10] = dff1[t + t0];
				else
					dffAlignedMat[i - 1][t + 10] = Float.NaN;
			}
			
			// AUC
			float datAUC = 0;
			float dfAUC = 0;
			float dffAUC = 0;
			for (int t = its; t <= ite; t++) {
				datAUC += charx1[t];
				dfAUC += df1[t];
				dffAUC += dff1[t];
			}
			ftsLst.curve.datAUC.put(i, datAUC);
			ftsLst.curve.dfAUC.put(i, dfAUC);
			ftsLst.curve.dffAUC.put(i, dffAUC);
			
			// basic features
			rghs = Math.max(rghs, 0);
			rghe = Math.min(rghe, H - 1);
			rgws = Math.max(rgws, 0);
			rgwe = Math.min(rgwe, W - 1);
			
			voxi = new boolean[rghe-rghs+1][rgwe-rgws+1];
			float hCenter = 0;
			float wCenter = 0;
			
			for(int[] p:pix0) {
				int x = p[0] - rghs;
				int y = p[1] - rgws;
				voxi[x][y] = true;
				hCenter += p[0];
				wCenter += p[1];
			}
			hCenter /= pix0.size();
			wCenter /= pix0.size();
			ftsLst.basic.center.put(i, new int[]{Math.round(hCenter), Math.round(wCenter)});
			
			getBasicFeatures(voxi, muPerPix, i, ftsLst.basic);
			minArea = Math.min(ftsLst.basic.area.get(i), minArea);
			maxArea = Math.max(ftsLst.basic.area.get(i), maxArea);
			
			// border
			ArrayList<int[]> boundary = BasicFeatureDealer.findBoundary(voxi, rghs, rgws);
			ftsLst.border.put(i, boundary);			
		}
		
		
		return new FeatureTopResult(ftsLst, dffMat, dMat, dffAlignedMat, minArea, maxArea, minPvalue, 
				maxPvalue, minDuration, maxDuration, mindffMax, maxdffMax, minDecayTau, maxDecayTau);
		
	}
	
	private static void getBasicFeatures(boolean[][] voxli0, float muPerPix, int nEvt, Basic ftsBase) {
		// basic features
		int H = voxli0.length;
		int W = voxli0[0].length;
		
		ftsBase.map.put(nEvt, voxli0);

		float area = 0;
		for(int x = 0; x < H; x++) {
			for(int y = 0;y < W; y++) {
				if(voxli0[x][y])
					area++;
			}
		}
		
		area = area * muPerPix * muPerPix;		
		ftsBase.area.put(nEvt, area);
		float perimeter = BasicFeatureDealer.calculatePerimeter(voxli0);
		perimeter = perimeter * muPerPix;
		ftsBase.perimeter.put(nEvt, perimeter);
		float circMetric = (float) ((4*Math.PI*area) / (perimeter*perimeter));
		ftsBase.circMetric.put(nEvt, circMetric);
	}
	
	private static Step6GetCurveResult getCurveStat(float[] x0, float spf, boolean ignoreTau) {
		float xPeak = -Float.MAX_VALUE;
		int tPeak =0;
		for(int t=0;t<x0.length;t++) {
			if(x0[t]>xPeak) {
				xPeak = x0[t];
				tPeak = t;
			}
		}
		
		int[][] pp = new int[3][2];		// 10&, 50%, 90% by start/end
		float[] thrVec = new float[] {0.1f,0.5f,0.9f};
		
		for(int n=0;n<3;n++) {
			int tPre = 0;
			for(int t=tPeak;t>=0;t--) {
				if(x0[t]<xPeak*thrVec[n]) {
					tPre = t;
					break;
				}
			}
			
			int tPost = x0.length-1;
			for(int t=tPeak;t<x0.length;t++) {
				if(x0[t]<xPeak*thrVec[n]) {
					tPost = t;
					break;
				}
			}
			
			pp[n][0] = tPre;
			pp[n][1] = tPost;
		}
		
		float rise19 = (pp[2][0]-pp[0][0]+1)*spf;
		float fall91 = (pp[0][1]-pp[2][1]+1)*spf;
		float width55 = (pp[1][1]-pp[1][0])*spf;
		float width11 = (pp[0][1]-pp[0][0])*spf;
		
		// exponential decay time constant, in ms
		float[] y = new float[pp[0][1]-tPeak+1];
		for(int t=tPeak;t<=pp[0][1];t++) {
			y[t-tPeak] = x0[t];
		}
		float decayTau = Float.NaN;
		
		if(y.length>=2 &&	!ignoreTau) {
			float minY = Float.MAX_VALUE;
			float maxY = -Float.MAX_VALUE;
			for(int t=0;t<y.length;t++) {
				minY = Math.min(minY, y[t]);
				maxY = Math.max(maxY, y[t]);
			}
			for(int t=0;t<y.length;t++) {
				y[t] = (y[t] - minY)/(maxY - minY) + 0.05f;
			}
			
			// exponential fitter
			WeightedObservedPoints obs = new WeightedObservedPoints();
			for(int t=0;t<y.length;t++) {
				double ytmp = Math.log(y[t]);
				obs.add(t,ytmp);
			}
			PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
			double[] coeff = fitter.fit(obs.toList());
			
			decayTau = (float) (-1/coeff[1]*spf);
		}
		
		float rise_50 = 0;
		thrVec = new float[] {0.4f,0.5f,0.6f};
		for(int n=0;n<3;n++) {
			float ixPre = 0;
			for(int t = tPeak; t >= 0; t--) {
				if(x0[t] <= xPeak*thrVec[n]) {
					ixPre = t;
					break;
				}
			}			
			rise_50 += ixPre / 3;
		}
		
		return new Step6GetCurveResult(rise19,fall91,width55,width11,decayTau,pp,rise_50);
	}
	
	private static int[] extendEventTimeRangeByCurve(float[] dff, boolean[] sigxOthers, int t0, int t1) {
		int T = dff.length;
		
		// begin and end of nearest others
		int i0 = 0;	
		for(int t = t0;t >= 0; t--) {
			if(sigxOthers[t]) {
				i0 = t;
				break;
			}
		}
		
		int i1 = T - 1;
		for(int t = t1; t < T; t++) {
			if(sigxOthers[t]) {
				i1 = t;
				break;
			}
		}
		
		// minimum point
		int t0a = t0;
		float min0 = Float.MAX_VALUE;
		for(int t = i0;t <= t0;t++) {
			if(min0 > dff[t]) {
				t0a = t;
				min0 = dff[t];
			}
		}
		
		int t1a = t1;
		float min1 = Float.MAX_VALUE;
		for(int t = t1;t<=i1;t++) {
			if(min1>dff[t]) {
				t1a = t;
				min1 = dff[t];
			}
		}
		
		return new int[] {t0a,t1a};
	}
	
	private static float[] getBaseline(float[] x0, int window, int cut) {
		int step, nSegment, t0, t1, curMinT, preMinT;
		float deltaV, preMinV, curMinV;
		  
		float[] datMA = Helper.movingMean(x0, window);
		int T = x0.length; // number of slices
		// moving average - checked
		step = (int) Math.round(0.5 * cut);	  
		nSegment = Math.max(1, (int) Math.ceil((float) T / step) - 1);		  
		float[] F0 = new float[T];
		preMinT = 0; preMinV = 0;
		for(int k = 0; k < nSegment; k++) {
			t0 = k * step;
	        t1 = Math.min(T - 1, t0 + cut);
	        curMinV = Float.MAX_VALUE;
            curMinT = -1;
            for (int t = t0; t <= t1; t++) {
            	if (datMA[t] < curMinV) {
            		curMinV = datMA[t];
            		curMinT = t;
            	}
            }
	        if (k == 0) {
	        	for (int t = 0; t < curMinT; t++) {
					F0[t] = curMinV;
				}
	        } else {
				deltaV = (curMinV - preMinV) / (curMinT - preMinT);
				for (int t = preMinT; t < curMinT; t++) {
					F0[t] = preMinV + (t - preMinT) * deltaV;
				}
	        }
	        
	        if (k == nSegment - 1) {
	        	for (int t = preMinT; t < T; t++) {
	        		F0[t] = curMinV;
				}
	        }
	        preMinT = curMinT;
	        preMinV = curMinV;
	        
		}
		return F0;
	}

	private static float avgDatNoiseEst(float[] curve, HashSet<Integer> ihw, Opts opts) {
		
		int T = curve.length;
		float varBefCorr = 0;
		for (int t = 1; t < T; t++) {
			varBefCorr += (curve[t] - curve[t - 1]) * (curve[t] - curve[t - 1]) / 2;
		}
		varBefCorr /= (T-1);
		int[] p;
		float var1 = 0;
		float var2 = 0;
		for (int index:ihw) {
			p = Helper.ind2sub(opts.H, opts.W, index);
			var1 += opts.tempVarOrg1[p[0]][p[1]];
			var2 += opts.tempVarOrg1[p[0]][p[1]] * 2 / opts.correctPars1[p[0]][p[1]];
		}
		float varAfterCorrect = varBefCorr * var2 / var1;
		
		
		return (float) Math.sqrt(varAfterCorrect);
	}

	public static void imputeMov(float[][][] datx) {
		int H = datx.length;
		int W = datx[0].length;
		int T = datx[0][0].length;
		// fill
		for(int i = 0;i < H;i++) {
			for(int j = 0;j < W;j++) {
				for(int k = 1;k < T;k++) {
					if(Float.isNaN(datx[i][j][k]))
						datx[i][j][k] = datx[i][j][k - 1];
				}
				for(int k=T-2;k>=0;k--) {
					if(Float.isNaN(datx[i][j][k]))
						datx[i][j][k] = datx[i][j][k + 1];
				}
			}
		}
	}

	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	public static void main(String[] args) {
		float[][][] dF1 = new float[1][1][1];
	}

	public static float[][][] removeDetected(float[][][] dF, HashMap<Integer, ArrayList<int[]>> evtLst) {
		// TODO Auto-generated method stub
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		
		int[][][] evtMap = new int[H][W][T];
		for (int i = 1; i <= evtLst.size(); i++) {
			Helper.setValue(evtMap, evtLst.get(i), i);
		}
		
		float[][][] dF_glo = Helper.copy3Darray(dF);
		boolean[][][] select = new boolean[H][W][T]; 
		for(int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int t = 0; t < T - 1; t++) {
					if (evtMap[x][y][t] > 0 && evtMap[x][y][t + 1] >0 && evtMap[x][y][t + 1] != evtMap[x][y][t])
						select[x][y][t] = true;
				}
			}
		}
		
		for(int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int t = 0; t < T; t++) {
					if (evtMap[x][y][t] > 0 && !select[x][y][t])
						dF_glo[x][y][t] = Float.NaN;
				}
			}
		}
		
		int t0, t1;
		boolean flag;
		float preV;
		float nextV;
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				t0 = 0; t1 = 0;
				flag = false;
				for (int t = 0; t < T; t++) {
					if (!flag && Float.isNaN(dF_glo[x][y][t])) {
						flag = true;
						t0 = t;
					}
					
					if (flag && !Float.isNaN(dF_glo[x][y][t])) {
						flag = false;
						t1 = t - 1;
						
						if (t0 == 0) {
							for (int tt = 0; tt <= t1; tt++)
								dF_glo[x][y][tt] = dF_glo[x][y][t1 + 1];
						}else {
							preV = dF_glo[x][y][t0 - 1];
							nextV = dF_glo[x][y][t1 + 1];
							for (int tt = t0; tt <= t1; tt++)
								dF_glo[x][y][tt] = preV + (nextV - preV) / (t1 - t0 + 2) * (tt - t0 + 1);
						}
					}
				}
				
				if (flag) {
					for (int tt = t0; tt < T; tt++)
						dF_glo[x][y][tt] = dF_glo[x][y][t0 - 1];
				}
			}
		}
		
		return dF_glo;
	}


	
}
