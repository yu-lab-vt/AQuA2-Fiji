package va.vt.cbilAQuA2.run;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ij.ImagePlus;
import inra.ijpb.watershed.MarkerControlledWatershedTransform3D;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.tools.GaussFilter;

public class Step4Helper {
	static long start;
	static long end;
	
	public static Step4Res se2evtTop(float[][][] dF, HashMap<Integer, ArrayList<int[]>> seLst,
			HashMap<Integer, ArrayList<int[]>> svLst, int[] seLabel,
			HashMap<Integer, Step3MajorityResult> majorInfo, Opts opts) {
		int gaptxx = opts.gapExt;
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		int[][][] seMap = new int[H][W][T];
		ArrayList<int[]> pix, se0, pix0;
		HashMap<Integer, Step3MajorityResult> major0;
		for (int i = 1; i <= seLst.size(); i++) {
			pix = seLst.get(i);
			Helper.setValue(seMap, pix, i);
		}
		
		System.out.println("Detecting events ...");
		HashMap<Integer, RiseInfo> riseLst = new HashMap<Integer, RiseInfo>(); 
		int[][][] datR = new int[H][W][T];
		int[][][] datL = new int[H][W][T];
		int nEvt = 0;
		HashSet<Integer> ihw0;
		ArrayList<Integer> svLabels;
		HashMap<Integer, ArrayList<int[]>> superVoxels;
		int x0, x1, y0, y1, t0, t1, H0, W0, T0, gapt;
		int[] p;
		Step3MajorityResult curMajor;
		float[][][] dF0;
		int[][][] seMap0;
		for(int nn = 1; nn <= seLst.size(); nn++) {
			se0 = seLst.get(nn);
			
			// super event pixel transform
			x0 = H; x1 = 0; y0 = W; y1 = 0; t0 = T; t1 = 0;
			
			for (int i = 0; i < se0.size(); i++) {
				p = se0.get(i);
				x0 = Math.min(x0, p[0]); x1 = Math.max(x1, p[0]);
				y0 = Math.min(y0, p[1]); y1 = Math.max(y1, p[1]);
				t0 = Math.min(t0, p[2]); t1 = Math.max(t1, p[2]);
			}
			gapt = Math.min(t1 - t0, gaptxx);
			t0 = Math.max(t0 - gapt, 0); t1 = Math.min(t1 + gapt, T - 1);
			H0 = x1 - x0 + 1; W0 = y1 - y0 + 1; T0 = t1 - t0 + 1;
			ihw0 = new HashSet<Integer>();
			for (int i = 0; i < se0.size(); i++) {
				p = se0.get(i);
				ihw0.add(Helper.sub2ind(H0, W0, p[0] - x0, p[1] - y0));
			} 
			
			// sub event pixel transform
			svLabels = new ArrayList<Integer>();
			major0 = new HashMap<Integer, Step3MajorityResult>();
			for (int i = 1; i < seLabel.length; i++) {
				if (seLabel[i] == nn)
					svLabels.add(i);
			}
			superVoxels = new HashMap<Integer, ArrayList<int[]>>();
			
			for (int k = 0; k < svLabels.size(); k++) {
				pix = svLst.get(svLabels.get(k));
				pix0 = new ArrayList<int[]>();
				for (int i = 0; i < pix.size(); i++) {
					p = pix.get(i);
					pix0.add(new int[] {p[0] - x0, p[1] - y0, p[2] - t0});
				}
				superVoxels.put(k + 1, pix0);
				
				curMajor = majorInfo.get(svLabels.get(k));
				curMajor.t0 -= t0;
				curMajor.t1 -= t0;
				curMajor.ihw = new HashSet<Integer>();
				if(curMajor.ihwDelays.size() >= opts.minSize) {
					for (Map.Entry<Integer, Integer> entry : curMajor.ihwDelays.entrySet()) {
						p = Helper.ind2sub(H, W, entry.getKey());
						curMajor.ihw.add(Helper.sub2ind(H0, W0, p[0] - x0, p[1] - y0));
					}
					
				}				
				major0.put(k + 1, curMajor);
			}
			
			System.out.println("SE: " + nn);
			dF0 = Helper.crop3D(dF, x0, x1, y0, y1, t0, t1);
			seMap0 = Helper.crop3D(seMap, x0, x1, y0, y1, t0, t1);
			Step4Se2EvtRes curRes = se2evt(dF0, seMap0, nn, ihw0, t0, t1, superVoxels, major0, opts);
			addToRisingMap(riseLst, curRes, nEvt, x0, x1, y0, y1);
			
			// update
			for (int x = 0; x < H0; x++) {
				for (int y = 0; y < W0; y++) {
					for (int t = 0; t < T0; t++) {
						if (curRes.evtL[x][y][t] > 0) {
							seMap[x + x0][y + y0][t + t0] = nn;
							datL[x + x0][y + y0][t + t0] = Math.max(datL[x + x0][y + y0][t + t0], curRes.evtL[x][y][t] + nEvt);
						}
						datR[x + x0][y + y0][t + t0] = (int) Math.max(datR[x + x0][y + y0][t + t0], curRes.evtRecon[x][y][t] * 255);
					}
				}
			}
			nEvt += curRes.nEvt0;
		}
		HashMap<Integer, ArrayList<int[]>> evtLst = Helper.label2idx(datL);
		return new Step4Res(riseLst, evtLst, datR, datL);
	}
	
	public static void addToRisingMap(HashMap<Integer, RiseInfo> riseLst, Step4Se2EvtRes curRes, int nEvt, int x0, int x1,
			int y0, int y1) {
		int nEvt0 = curRes.nEvt0;
		int[][][] evtL = curRes.evtL;
		int H = evtL.length;
		int W = evtL[0].length;
		int T = evtL[0][0].length;
		HashMap<Integer, ArrayList<int[]>> evtLst = Helper.label2idx(evtL);
		ArrayList<int[]> pix;
		HashSet<Integer> ihw;
		float[][][] dlyMaps;
		int[] p;
		int rgh0, rgh1, rgw0, rgw1;
		boolean[][] mask;
		float maxDly;
		float minDly;
		ArrayList<Float> dly80;
		ArrayList<Float> dly20;
		for (int ii = 1; ii <= nEvt0; ii++) {
			pix = evtLst.get(ii);
			
			dlyMaps = Helper.copy3Darray(curRes.dlyMaps);
			mask = new boolean[H][W];
			rgh0 = H; rgh1 = 0;
			rgw0 = W; rgw1 = 0;		
			for (int i = 0; i < pix.size(); i++) {
				p = pix.get(i);
				mask[p[0]][p[1]] = true;
				rgh0 = Math.min(rgh0, p[0]);
				rgh1 = Math.max(rgh1, p[0]);
				rgw0 = Math.min(rgw0, p[1]);
				rgw1 = Math.max(rgw1, p[1]);
			}
			
			dly80 = new ArrayList<Float>();
			dly20 = new ArrayList<Float>();
			for (int x = 0; x < H; x++) {
				for (int y = 0; y < W; y++) {
					if (!mask[x][y]) {
						dlyMaps[0][x][y] = Float.NaN;
						dlyMaps[1][x][y] = Float.NaN;
						dlyMaps[2][x][y] = Float.NaN;
					}else {
						dly80.add(dlyMaps[2][x][y]);
						dly20.add(dlyMaps[0][x][y]);
					}
				}
			}
			dlyMaps = Helper.crop3D(dlyMaps, 0, 2, rgh0, rgh1, rgw0, rgw1);
			rgh0 += x0; rgh1 += x0;
			rgw0 += y0; rgw1 += y0;
			maxDly = (float) Math.ceil(quantile(dly80, 0.98f));
			minDly = (float) Math.floor(quantile(dly20, 0.02f));
			riseLst.put(nEvt + ii, new RiseInfo(dlyMaps, maxDly, minDly, rgh0, rgh1, rgw0, rgw1));
		}
		
	}

	private static double quantile(ArrayList<Float> dly, float f) {
		Collections.sort(dly);
		int n = dly.size();
		int index = Math.round(n * f - 1);
		index = Math.max(0, Math.min(n - 1, index));
		return dly.get(index);
	}

	static Step4Se2EvtRes se2evt(float[][][] dF0, int[][][] seMap0, int seSel, HashSet<Integer> ihw0, int t0,
			int t1, HashMap<Integer, ArrayList<int[]>> superVoxels, HashMap<Integer, Step3MajorityResult> major0,
			Opts opts) {
		// GTW on super pixels
		// Group super pixels into events
		int H = dF0.length;
		int W = dF0[0].length;
		int T = dF0[0][0].length;
		int minTs = 0;
		opts.nRoughPixel = 2000;
		int[] svLabel;
		SpgtwRes res;
		if (ihw0.size() > 30 && T > 1) {
			res = spgtw(dF0, seMap0, seSel, superVoxels, major0, opts);
			if (superVoxels.size() > 1) {
				svLabel = delayMap2evt(res.dlyMaps[1],major0,opts);
			}else {
				svLabel = new int[] {1};
			}			
		}else {
			int t_scl = 1;
			svLabel = new int[superVoxels.size()];
			for (int i = 0; i < superVoxels.size(); i++)
				svLabel[i] = 1;
			float[][][] dlyMaps = new float[3][H][W];
//			for (int i = 0; i < 3; i++) {
//				for(int x = 0; x < H; x++) {
//					for (int y = 0; y < W; y++) {
//						dlyMaps[i][x][y] = 1;
//					}
//				}
//			}
			HashMap<Integer, ArrayList<int[]>> spLst = new HashMap<Integer, ArrayList<int[]>>();
			ArrayList<int[]> l = new ArrayList<int[]>();
			for (int index : ihw0) {
				l.add(Helper.ind2sub(H, W, index));
			}
			spLst.put(1, l);
			float[] cx0 = Helper.getAvgCurve(dF0, ihw0);
			cx0 = GaussFilter.gaussFilter(cx0, 2);
			Helper.normalizeCurve(cx0);
			float[][] cx = new float[1][T];
			cx[0] = cx0;
			res = new SpgtwRes(dlyMaps, cx, t_scl, minTs, spLst);
		}
		
		
		// events
		int[][][] evtL = new int[H][W][T];
		for (int i = 1; i <= superVoxels.size(); i++) {
			Helper.setValue(evtL, superVoxels.get(i), svLabel[i - 1]);
		}
		float[][][] evtRecon = getEvtRecon(evtL, Helper.copy3Darray(seMap0), dF0, seSel, res.spLst, res.cx, opts);
		int nEvt0 = 0;
		for (int i = 0; i < svLabel.length; i++)
			nEvt0 = Math.max(nEvt0, svLabel[i]);
		
		// correct delaymap
		float[][][] dlyMaps = res.dlyMaps;
		for (int i = 0; i < 3; i++) {
			for (int x = 0; x < H; x++) {
				for (int y = 0; y < W; y++) {
					dlyMaps[i][x][y] = (dlyMaps[i][x][y] + 1) * res.tempRatio + 0.5f - (float)(res.tempRatio) / 2 + minTs + t0;
				}
			}
		}
		
		
		return new Step4Se2EvtRes(evtRecon, evtL, dlyMaps, nEvt0, svLabel);
	}

	private static float[][][] getEvtRecon(int[][][] evtL, int[][][] seMap0, float[][][] dF, int seSel,
			HashMap<Integer, ArrayList<int[]>> spLst, float[][] cx, Opts opts) {
		int H = seMap0.length;
		int W = seMap0[0].length;
		int T = seMap0[0][0].length;
		float[][][] evtRecon = new float[H][W][T];
		
		int nSp = spLst.size();
		float[] x0;
		int[] p;
		ArrayList<int[]> sp0;
		for (int k = 1; k <= spLst.size(); k++) {
			sp0 = spLst.get(k);
			x0 = cx[k - 1];
			for (int i = 0; i < sp0.size(); i++) {
				p = sp0.get(i);
				for (int t = 0; t < T; t++) {
					evtRecon[p[0]][p[1]][t] = x0[t];
				}
			}
		}
		
		if (opts.whetherExtend) {
			int[][] spMap = new int[H][W];
			for (int k = 1; k <= spLst.size(); k++) {
				Helper.setValue(spMap, spLst.get(k), k);
			}
			
			// extend
			HashMap<Integer, ArrayList<int[]>> evtLst = Helper.label2idx(evtL);
			float[][][] dF0 = new float[H][W][T];
			for (int tt = 0; tt < T; tt++) {
			    float[][] slice = Helper.getSlice(dF, tt);
			    float[][] filteredSlice = GaussFilter.gaussFilter(slice, 2, 2);
			    Helper.setSlice(dF0, filteredSlice, tt); 
			}   
			
			ArrayList<int[]> pix;
			HashSet<Integer> curEvtIhw, spLabels, curSp;
			boolean[] curTemporal;
			int t0, t1, tPeak, ts, te;
			float maxV;
			boolean avaliable;
			for (int ii = 1; ii <= evtLst.size(); ii ++) {
				pix = evtLst.get(ii);
				curEvtIhw = Helper.getUniqueSpa(pix, H, W);
				spLabels = new HashSet<Integer>();
				for (int index : curEvtIhw) {
					p = Helper.ind2sub(H, W, index);
					spLabels.add(spMap[p[0]][p[1]]);
				}
				spLabels.remove(0);
				
				for (int spLabel : spLabels) {
					curSp = Helper.getUniqueSpa(spLst.get(spLabel), H, W);
					curSp.retainAll(curEvtIhw);
					x0 = Helper.getAvgCurve(dF0, curSp);
					
					curTemporal = new boolean[T];
					t0 = T; t1 = 0;
					for (int t = 0; t < T; t++) {
						for (int index : curSp) {
							p = Helper.ind2sub(H, W, index);
							if (evtL[p[0]][p[1]][t] == ii) {
								curTemporal[t] = true;
								break;
							}
						}
						if (curTemporal[t]) {
							t0 = Math.min(t0, t);
							t1 = Math.max(t1, t);
						}
					}
					
					maxV = Float.NEGATIVE_INFINITY;
					tPeak = 0;
					for (int t = t0; t <= t1; t++) {
						if (x0[t] > maxV) {
							maxV = x0[t];
							tPeak = t;
						}
					}
					
					ts = 0;
					for (int t = tPeak; t >= 0; t--) {
						if (x0[t] < maxV * opts.minShow1) {
							ts = t + 1;
							break;
						}
					}
					te = T - 1;
					for (int t = tPeak; t < T; t++) {
						if (x0[t] < maxV * opts.minShow1) {
							te = t - 1;
							break;
						}
					}
					
					// update evtL and evtRecon
					for (int t = ts; t <= te; t++) {
						for (int index : curSp) {
							p = Helper.ind2sub(H, W, index);
							avaliable = (seMap0[p[0]][p[1]][t] == 0) & (evtL[p[0]][p[1]][t] == 0);
							if (avaliable) {
								evtL[p[0]][p[1]][t] = ii;
								seMap0[p[0]][p[1]][t] = seSel;
							}
						}
					}
				}
			}
		}
		
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int t = 0; t < T; t++) {
					if (seMap0[x][y][t] != seSel) {
						evtRecon[x][y][t] = 0;
					}
				}
			}
		}
		
		return evtRecon;
	}

	private static int[] delayMap2evt(float[][] dlyMap, HashMap<Integer, Step3MajorityResult> major0, Opts opts) {
		int H = dlyMap.length;
		int W = dlyMap[0].length;
		int[][] dirs = Helper.dirGenerate(8);
		float minDly = Float.POSITIVE_INFINITY;
		float maxDly = Float.NEGATIVE_INFINITY;
		ArrayList<int[]> ihw = new ArrayList<int[]>();
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				if (!Float.isNaN(dlyMap[x][y])) {
					minDly = Math.min(minDly, dlyMap[x][y]);
					maxDly = Math.max(maxDly, dlyMap[x][y]);
					ihw.add(new int[] {x,y});
				}					
			}
		}
		
		float delayThr;
		if (opts.sourceSensitivity == 10)
			delayThr = 1;
		else
			delayThr = opts.TPatch*(10-opts.sourceSensitivity)/10;
		
		int[] svLabel = new int[major0.size()];
		if (maxDly - minDly < delayThr) {
			for (int i = 0; i < svLabel.length; i++)
				svLabel[i] = 1;
			return svLabel;
		}
		
		
		float[] thrs = new float[opts.TPatch + 1];
		for (int i = 0; i < thrs.length; i++)
			thrs[i] = minDly + (maxDly - minDly) / opts.TPatch * i;
		int[][] seedMap = new int[H][W];
		int nSeed = 0;
		
		float thr, mean1, mean2;
		boolean[][] BW2D;
		int[] p;
		HashMap<Integer, ArrayList<int[]>> candidateRegions;
		ArrayList<int[]> pix;
		boolean[] select;
		int round, ih, iw;
		HashSet<Integer> seedsInRegion, pixHash, pixGrow, newAdd, neighbor, newAdd0;
		for (int k = 0; k < thrs.length; k++) {
			thr = thrs[k];
			BW2D = new boolean[H][W];
			for (int i = 0; i < ihw.size(); i++) {
				p = ihw.get(i);
				BW2D[p[0]][p[1]] = dlyMap[p[0]][p[1]] < thr;
			}
			candidateRegions = new HashMap<Integer, ArrayList<int[]>>();
			Helper.bfsConn2D(BW2D, candidateRegions);
			select = new boolean[candidateRegions.size()];
			for (int i = 1; i <= candidateRegions.size(); i++) {
				select[i - 1] = candidateRegions.get(i).size() > Math.max(ihw.size() * opts.sourceSzRatio, opts.minSize);
			}
			candidateRegions = Helper.filterWithMask(candidateRegions, select);
			for (int i = 1; i <= candidateRegions.size(); i++) {
				pix = candidateRegions.get(i);
				seedsInRegion = new HashSet<Integer>();
				for (int j = 0; j < pix.size(); j++) {
					p = pix.get(j);
					seedsInRegion.add(seedMap[p[0]][p[1]]);
				}
				seedsInRegion.remove(0);
				if (seedsInRegion.size() == 1) {
					Integer[] labelArray = seedsInRegion.toArray(new Integer[1]);
					Helper.setValue(seedMap, pix, labelArray[0]);
				}else if(seedsInRegion.size() == 0) {
					pixHash = new HashSet<Integer>();
					for (int j = 0; j < pix.size(); j++) {
						p = pix.get(j);
						pixHash.add(Helper.sub2ind(H, W, p[0], p[1]));
					}					
					newAdd = new HashSet<Integer>(pixHash);
					pixGrow = new HashSet<Integer>(pixHash);
					neighbor = new HashSet<Integer>();
					round = 0;
					while (round < 100 && newAdd.size() > 0 && neighbor.size() < pixHash.size()) {
						newAdd0 = new HashSet<Integer>();
						for (int index : newAdd) {
							p = Helper.ind2sub(H, W, index);
							for (int ii = 0; ii < dirs.length; ii++) {
								ih = Math.max(0, Math.min(H - 1, p[0] + dirs[ii][0]));
								iw = Math.max(0, Math.min(W - 1, p[1] + dirs[ii][1]));
								if (!Float.isNaN(dlyMap[ih][iw]))
									newAdd0.add(Helper.sub2ind(H, W, ih, iw));
							}
						}
						newAdd = newAdd0;
						newAdd.removeAll(pixGrow);
						neighbor.addAll(newAdd);
						pixGrow.addAll(newAdd);
						round++;
					}
					mean1 = 0;
					for (int index : neighbor) {
						p = Helper.ind2sub(H, W, index);
						mean1 += dlyMap[p[0]][p[1]];
					}
					mean1 /= neighbor.size();
					mean2 = 0;
					for (int j = 0; j < pix.size(); j++) {
						p = pix.get(j);
						mean2 += dlyMap[p[0]][p[1]];
					}	
					mean2 /= pix.size();
					if (mean1 - mean2 > delayThr) {
						nSeed++;
						Helper.setValue(seedMap, pix, nSeed);
					}
				}
			}
		}
		
		if (nSeed <= 1) {
			for (int i = 0; i < svLabel.length; i++)
				svLabel[i] = 1;
			return svLabel;
		}


		boolean[][][] BW = new boolean[1][H][W];
		for (int pId = 0; pId < ihw.size(); pId++) {
			p = ihw.get(pId);
			BW[0][p[0]][p[1]] = true;
		}
		int[][] shifts = new int[13][2];
		shifts[0] = new int[] {0, -2, 0};
		shifts[1] = new int[] {0, -1, -1};
		shifts[2] = new int[] {0, -1, 0};
		shifts[3] = new int[] {0, -1, 1};
		shifts[4] = new int[] {0, 0, -2};
		shifts[5] = new int[] {0, 0, -1};
		shifts[6] = new int[] {0, 0, 0};
		shifts[7] = new int[] {0, 0, 1};
		shifts[8] = new int[] {0, 0, 2};
		shifts[9] = new int[] {0, 1, -1};
		shifts[10] = new int[] {0, 1, 0};
		shifts[11] = new int[] {0, 1, 1};
		shifts[12] = new int[] {0, 2, 0};
		
		float[][][] scoreMap0 = new float[1][H][W];
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				if(Float.isNaN(dlyMap[x][y]))
					scoreMap0[0][x][y] = maxDly;
				else
					scoreMap0[0][x][y] = dlyMap[x][y];
				
				BW[0][x][y] = !BW[0][x][y];
			}
		}
		boolean[][][] BW2 = Helper.erode(BW, shifts);
		
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				if (BW[0][x][y])
					scoreMap0[0][x][y] = maxDly + 100;
				if (BW2[0][x][y])
					scoreMap0[0][x][y] = 0;
				BW2[0][x][y] |= seedMap[x][y] > 0;
			}
		}
		
		scoreMap0 = Helper.imimposemin(scoreMap0, BW2);
		
//		Helper.viewMatrix(1, 20, 20, null, scoreMap0);
		
		int[][][] MapOut3D = Helper.watershed(scoreMap0);
		int[][] MapOut = new int[H][W];
		
		// update
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				MapOut[x][y] = MapOut3D[0][x][y];
				if (BW[0][x][y])
					MapOut[x][y] = 0;
			}
		}
		HashMap<Integer, ArrayList<int[]>> waterLst = Helper.label2idx(MapOut);
		int seedLabel;
		for (Map.Entry<Integer, ArrayList<int[]>> entry : waterLst.entrySet()) {
			pix = entry.getValue();
			seedLabel = 0;
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				if (seedMap[p[0]][p[1]] > 0) {
					seedLabel = seedMap[p[0]][p[1]];
					break;
				}
			}
			if (seedLabel == 0)
				continue;
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				seedMap[p[0]][p[1]] = seedLabel;
			}
		}
		
		// fill the gap
		ArrayList<int[]> newPix;
		pix = new ArrayList<int[]>();
		for (int pId = 0; pId < ihw.size(); pId++) {
			p = ihw.get(pId);
			if (seedMap[p[0]][p[1]] == 0)
				pix.add(p);
		}
		
		boolean[] labeled;
		while (pix.size() > 0) {
			labeled = new boolean[pix.size()];
			for (int k = 0; k < dirs.length; k++) {
				for (int pId = 0; pId < pix.size(); pId++) {
					if (labeled[pId])
						continue;
					p = pix.get(pId);
					ih = Math.max(Math.min(H - 1, p[0] + dirs[k][0]), 0);
					iw = Math.max(Math.min(W - 1, p[1] + dirs[k][1]), 0);
					if (MapOut[ih][iw] > 0) {
						MapOut[p[0]][p[1]] = MapOut[ih][iw];
						labeled[pId] = true;
					}
				}
			}
			
			newPix = new ArrayList<int[]>();
			for (int pId = 0; pId < pix.size(); pId++) {
				if (!labeled[pId])
					newPix.add(pix.get(pId));
			}
			pix = newPix;
		}	
		
		
		// find the corresponding source for each subevent
		HashMap<Integer, Integer> counter;
		int label, maxCount, modeLabel;
		for (int i = 1; i <= major0.size(); i++) {
			pixHash = major0.get(i).ihw;
			counter = new HashMap<>();
			modeLabel = 0;
			maxCount = 0;
			for (int index : pixHash) {
				p = Helper.ind2sub(H, W, index);
				label = (int) MapOut[p[0]][p[1]];
				counter.put(label, counter.getOrDefault(label, 0) + 1); 
			}
			for (Map.Entry<Integer, Integer> entry : counter.entrySet()) {
				if (entry.getValue() > maxCount) {
					maxCount = entry.getValue();
					modeLabel = entry.getKey();
				}
			}
			svLabel[i - 1] = modeLabel;
		}
		nSeed = 1;
		HashMap<Integer, ArrayList<Integer>> lst = new HashMap<>();
		ArrayList<Integer> l;
		int maxLabel = 0;
		for (int i = 0; i < svLabel.length; i++) {
			l = lst.getOrDefault(svLabel[i], new ArrayList<>());
			l.add(i);
			lst.put(svLabel[i], l); 
			maxLabel = Math.max(svLabel[i], maxLabel);
		}
			
		for (int i = 1; i <= maxLabel; i++) {
			l = lst.get(i);
			if (l != null) {
				for (int j : l) {
					svLabel[j] = nSeed;
				}
				nSeed++;
			}
		}
		return svLabel;
	}

	private static SpgtwRes spgtw(float[][][] dF, int[][][] seMap0, int seSel,
			HashMap<Integer, ArrayList<int[]>> superVoxels, HashMap<Integer, Step3MajorityResult> major0, Opts opts) {
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		int nRoughPixel = opts.nRoughPixel;
		
		// get spatial downsample ratio
		ArrayList<int[]> pix = new ArrayList<int[]>();
		HashSet<Integer> ihw = new HashSet<>();
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W ; y++) {
				for (int t = 0; t < T; t++) {
					if (seMap0[x][y][t] == seSel) {
						pix.add(new int[] {x, y, t});
						ihw.add(Helper.sub2ind(H, W, x, y));
					}
				}
			}
		}
		int spaRatio = (int) Math.max(2, Math.round(Math.sqrt(((float)ihw.size()) / nRoughPixel)));
		float smoBase = opts.gtwSmo *  spaRatio;
		
		// spatial downsample as super pixels
		int H0 = (int) Math.ceil((float) H / spaRatio);
		int W0 = (int) Math.ceil((float) W / spaRatio);
		boolean[][][] mask = new boolean[H0][W0][T];
		int[] p;
		HashSet<Integer> pixDSHash = new HashSet<Integer>();
		HashSet<Integer> superPixTmp = new HashSet<Integer>();
		ArrayList<int[]> pixDs = new ArrayList<int[]>();
		for (int i = 0; i < pix.size(); i++) {
			p = pix.get(i);
			pixDSHash.add(Helper.sub2ind(H0, W0, T, p[0] / spaRatio, p[1] / spaRatio, p[2]));
		}
		for (int index : pixDSHash) {
			p = Helper.ind2sub(H0, W0, T, index);
			pixDs.add(p);
			mask[p[0]][p[1]][p[2]] = true;
			superPixTmp.add(Helper.sub2ind(H0, W0, p[0], p[1]));
		}
		ArrayList<int[]> superPix = Helper.convertSet2Array(superPixTmp, H0, W0);
		
		// only focus on the major time window
		boolean[][][] mask2 = new boolean[H0][W0][T];
		ArrayList<int[]> tmp;
		for (int i = 1; i <= superVoxels.size(); i++) {
			tmp = superVoxels.get(i);
			for (int j = 0; j < tmp.size(); j++) {
				p = tmp.get(j);
				for (int t = major0.get(i).t0; t <= major0.get(i).t1; t++) {
					mask2[p[0] / spaRatio][p[1] / spaRatio][t] = true;
				}
			}			
		}
		
		boolean[][][] mask0 = Helper.AndOperation(mask, mask2);
		int nSp = superPix.size();
		int[][] mapping = new int[H0][W0];
		int cnt = 1;
		for (int i = 0; i < superPix.size(); i ++) {
			p = superPix.get(i);
			mapping[p[0]][p[1]] = cnt;
			cnt ++;
		}
		
		int[][] spMap = new int[H][W];
		for(int index : ihw) {
			p = Helper.ind2sub(H, W, index);
			spMap[p[0]][p[1]] = mapping[p[0] / spaRatio][p[1] / spaRatio];
		}
		HashMap<Integer, ArrayList<int[]>> spLst = Helper.label2idx(spMap);
		
		// GTW prepare
		float[][] tstOrg = new float[nSp][T];
		float[][] tst = new float[nSp][T];
		int ext = 5;
		int minTs = T;
		float[] curve;
		ArrayList<Integer> TW;
		float maxV, minV;
		int tPeak, ts;
		int maxTPeak = 0;
		for (int k = 0; k < nSp; k ++) {
			curve = Helper.getAvgCurve(dF, spLst.get(k + 1));
			Helper.scaleCurve(curve, Math.sqrt(spLst.get(k + 1).size()));
			curve = GaussFilter.gaussFilter(curve, 2);
			TW = new ArrayList<Integer>();
			p = superPix.get(k);
			for (int t = 0; t < T; t++) {
				if (mask0[p[0]][p[1]][t])
					TW.add(t);
			}
			if (TW.size() == 0) {
				for (int t = 0; t < T; t++) {
					if (mask[p[0]][p[1]][t])
						TW.add(t);
				}
			}
			
			maxV = Float.NEGATIVE_INFINITY;
			minV = Float.POSITIVE_INFINITY;
			tPeak = 0;
			for (int t : TW) {
				if (curve[t] > maxV) {
					maxV = curve[t];
					tPeak = t;
				}
			}
			
			ts = 0;
			for (int t = Math.max(0, TW.get(0) - ext); t <= tPeak; t++) {
				if (curve[t] < minV) {
					minV = curve[t];
					ts = t;
				}
			}
			minTs = Math.min(minTs, ts);
			for(int t = 0; t <= ts; t++)
				curve[t] = minV;
			tstOrg[k] = curve;
			curve = Helper.copy1Darray(curve);
			for (int t = tPeak; t < curve.length; t++)
				curve[t] = maxV;
			for (int t = 0; t < curve.length; t++)
				curve[t] -= minV;
			tst[k] = curve;		
			maxTPeak = Math.max(maxTPeak, tPeak);
		}
		
		// temporal downsample
		float[][] tstOrgCrop = Helper.crop2D(tst, 0, tst.length - 1, minTs, maxTPeak);
		int T1 = maxTPeak - minTs + 1;
		int tempRatio = Math.max(1, Math.round(((float) T1) / opts.TPatch));
		float[][][] dlyMaps = new float[3][H][W];
		for (int i = 0; i < 3; i++) {
			for(int x = 0; x < H; x++) {
				for (int y = 0; y < W; y++) {
					dlyMaps[i][x][y] = Float.NaN;
				}
			}
		}
		float[][] cx = new float[nSp][T];
		if (T1 == 1) {
			for (int i = 0; i < nSp; i++){
				cx[i][maxTPeak] = 1;
			}
			return new SpgtwRes(dlyMaps, cx, tempRatio, minTs, spLst);
		}
			
		int T0 = (int) Math.ceil(((float) T1) / tempRatio);
		tst = new float[nSp][T0];
		int t0, t1;
		float sum;
		float[] maxTst = new float[nSp];
		for (int i = 0; i < nSp; i++)
			maxTst[i] = Float.NEGATIVE_INFINITY;
		for (int k = 0; k < T0; k++) {
			t0 = k * tempRatio;
			t1 = Math.min(T1 - 1, (k + 1) * tempRatio - 1);
			for (int i = 0; i < nSp; i ++) {
				sum = 0;
				for(int t = t0; t <= t1; t++) {
					sum += tstOrgCrop[i][t];
				}
				tst[i][k] = (float) (sum / (t1 - t0 + 1) * Math.sqrt(tempRatio));
				maxTst[i] = Math.max(maxTst[i], tst[i][k]);
			}
		}
		
		// reference curve
		float[] refBase = new float[T0];
		float[][] ref = new float[nSp][T0];
		for (int t = 0; t < T0; t++) {
			refBase[t] = ((float) t) / (T0 - 1);
		}
		for (int k = 0; k < nSp; k++) {
			curve = Helper.copy1Darray(refBase);
			Helper.scaleCurve(curve, maxTst[k]);
			ref[k] = curve;
		}
		
		// get neighbor relation
		int[][] dirs = Helper.dirGenerate(8);
		HashMap<Integer, HashSet<Integer>> GijTemp = new HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> l;
		int id1, id2, ih1, iw1, id01, id02;
		for (int i = 0; i < superPix.size(); i++) {
			p = superPix.get(i);
			for (int k = 0; k < dirs.length; k++) {
				ih1 = Math.max(Math.min(H0 - 1, p[0] + dirs[k][0]), 0);
				iw1 = Math.max(Math.min(W0 - 1, p[1] + dirs[k][1]), 0);
				id2 = mapping[ih1][iw1];
				id1 = mapping[p[0]][p[1]];
				if (id2 > 0 && id1 != id2) {
					id01 = Math.min(id1, id2);
					id02 = Math.max(id1, id2);
					l = GijTemp.get(id01);
					if (l == null) {
						l = new HashSet<Integer>();
						GijTemp.put(id01, l);
					}
					l.add(id02);
				}
			}
		}
		int neiSize = 0;
		for (Map.Entry<Integer, HashSet<Integer>> entry : GijTemp.entrySet())
			neiSize += entry.getValue().size();
		int[][] Gij = new int[neiSize][2];
		cnt = 0;
		for (Map.Entry<Integer, HashSet<Integer>> entry : GijTemp.entrySet()) {
			id1 = entry.getKey();
			for (int index : entry.getValue()) {
				Gij[cnt][0] = id1;
				Gij[cnt][1] = index;
				cnt++;
			}
		}
		
		// GTW: BILCO
		float[][] midPoints = null;
		float[][][] distMatrix = new float[T0][T0][nSp];
		for (int k = 0; k < nSp; k++) {
			for (int i = 0; i < T0; i++) {
				for (int j = 0; j < T0; j++) {
					distMatrix[j][i][k] = (ref[k][i] - tst[k][j]) * (ref[k][i] - tst[k][j]);
				}
			}
		}
		
		if (smoBase == 0 || Gij.length == 0) {
			// -----------
			midPoints = new float[nSp][T0 - 1];
			for (int i = 0; i < nSp; i++)
				midPoints[i] = Helper.DTW_Edge_input(Helper.getSlice(distMatrix, i));
		}else {
			// -----------
			float[][] avgDist = new float[T0][T0];
			for (int i = 0; i < T0; i++) {
				for(int j = 0; j < T0; j++) {
					for (int k = 0; k < nSp; k++)
						avgDist[i][j] += distMatrix[i][j][k] / nSp;
				}
			}
			float[] initialCut0 = Helper.DTW_Edge_input(avgDist);
			float[][] initialCut = new float[nSp][T0 - 1];
			for (int k = 0; k < nSp; k++) {
				for (int i = 0; i < T0 - 1; i++)
					initialCut[k][i] = initialCut0[i];
			}
			midPoints = Helper.BILCO(ref, tst, Gij, smoBase, initialCut);
		}
		
		// path + warping curves
		ArrayList<int[]> path = null;
		float[][] cxAlign = new float[nSp][T0];
		for (int i = 0; i < nSp; i++) {
			path = midPoint2path(midPoints[i],T0);
			cxAlign[i] = warpRef2Tst(path,refBase,T0);
		}
		
		// delay time
		float[] thrVec = new float[19];
		for (int i = 0; i < thrVec.length; i++)
			thrVec[i] = (float) (0.05 + 0.05 * i);
		int[][] tAch = new int[nSp][thrVec.length];
		for (int nn = 0; nn < nSp; nn++) {
			curve = cxAlign[nn];
			maxV = Float.NEGATIVE_INFINITY;
			t0 = 0;
			for (int t = 0; t < curve.length; t++) {
				if (curve[t] > maxV) {
					maxV = curve[t];
					t0 = t;
				}
			}
			for (int ii = 0; ii < thrVec.length; ii++) {
				t1 = t0;
				for(int t = 0; t <= t0; t++) {
					if (curve[t] >= thrVec[ii]) {
						t1 = t;
						break;
					}
				}
				tAch[nn][ii] = t1;
			}
		}
		
		float[][] tDlys = new float[nSp][3];
		for (int i = 0; i < nSp; i++) {
			curve = new float[thrVec.length];
			curve[0] = tAch[i][0];
			for (int ii = 1; ii < thrVec.length; ii++)
				curve[ii] = curve[ii - 1] + tAch[i][ii];
			tDlys[i][0] = curve[6] / 7;
			tDlys[i][1] = curve[18] / 19;
			tDlys[i][2] = (curve[18] - curve[11]) / 7;
		}
		for (int i = 0; i < 3; i++) {
			for(int index : ihw) {
				p = Helper.ind2sub(H, W, index);
				dlyMaps[i][p[0]][p[1]] = tDlys[spMap[p[0]][p[1]] - 1][i];
			}
		}

		
		// warping back, obtained value is the middle point of each downsampled window. Recover it
		float[][] cxAlignTmpOrg = new float[nSp][T1];
		float preMidT = ((float)tempRatio - 1)/2;
		float preMidV, curMidT;
		int tLeft, tRight;
		for (int i = 0; i < nSp; i++) {
			preMidV = cxAlign[i][0];
			for (int t = 0; t <= preMidT; t++) {
				cxAlignTmpOrg[i][t] = 0 + preMidV / preMidT * t;
			}
			for(int t = 1; t < T0; t++) {
				 t0 = (int) Math.ceil(preMidT);
				 tLeft = t * tempRatio;
				 tRight = Math.min(tLeft + tempRatio - 1, T1 - 1);
				 curMidT = (float)(tLeft + tRight)/2;
				 t1 = (int) Math.floor(curMidT);
				 for (int tt = t0; tt <= t1; tt++) {
					 cxAlignTmpOrg[i][tt] = preMidV + (cxAlign[i][t] - preMidV) / (curMidT - preMidT) * (tt - preMidT);
				 }
				 preMidV = cxAlign[i][t];
				 preMidT = curMidT;
			}
			if (T1 - 1 != preMidT) {
				t0 = (int) Math.ceil(preMidT);
				for(int tt = t0; tt < T1; tt++) {
					cxAlignTmpOrg[i][tt] = preMidV + (1 - preMidV) / (T1 - 1 - preMidT) * (tt - preMidT);
				}
			}
		}
		
		cx = new float[nSp][T];
		int alignedPeak, alignedPeak2, id;
		for (int k = 0; k < nSp; k++) {
			// assign rising part
			curve = cxAlignTmpOrg[k];
			alignedPeak = 0;
			for (int t = 0; t < curve.length; t++) {
				if (curve[t] == 1) {
					alignedPeak = t;
					break;
				}
			}
			alignedPeak2 = minTs + alignedPeak;
			for (int t = minTs; t <= alignedPeak2; t++)
				cx[k][t] = curve[t - minTs];
			// assign decay part
			// make the first point of x1 has the same intensity of last point of x0
			// scale x1, make it between range(0,1)
			curve = Helper.crop1D(tstOrg[k], alignedPeak2, T - 1);
			minV = Float.MAX_VALUE;
			id = 0;
			for (int t = 0; t < curve.length; t++) {
				if (curve[t] < minV) {
					minV = curve[t];
					id = t;
				}
			}
			maxV = tstOrg[k][alignedPeak2];
			for (int t = 0; t < curve.length; t++) {
				if (t < id) {
					curve[t] = (curve[t] - minV) / (maxV - minV);
					curve[t] = Math.min(curve[t], 1);
				}else {
					curve[t] = 0;
				}
				cx[k][t + alignedPeak2] = curve[t];
			}			
		}
		

		return new SpgtwRes(dlyMaps, cx, tempRatio, minTs, spLst);
	}

	private static float[] warpRef2Tst(ArrayList<int[]> p0, float[] ref, int T) {
		float[] x0 = new float[T];
		for (int t = 0; t < T; t++)
			x0[t] = Float.NaN;
		int[] c0 = new int[T];
		int[] p;
		int p_ref, p_tst;
		for (int t = 0; t < p0.size(); t++) {
			p = p0.get(t);
			p_ref = p[0];
			p_tst = p[1];
			if (!Float.isNaN(ref[p_ref])) {
				if (Float.isNaN(x0[p_tst])) 
					x0[p_tst] = ref[p_ref];
				else
					x0[p_tst] += ref[p_ref];
				c0[p_tst]++;
			}
		}
	    for (int t = 0; t < T; t++) {
	    	if (c0[t] == 0)
	    		c0[t] = 1;
	    	x0[t] /= c0[t];
	    }
		return x0;
	}

	private static ArrayList<int[]> midPoint2path(float[] midPoint, int T0) {
		int x = 0;
	    int y = 0;
	    ArrayList<int[]> path = new ArrayList<int[]>();
	    while(y < T0-1) {
	    	for (; x <= Math.floor(midPoint[y]); x++) {
	    		path.add(new int[] {y, x});
	    	}
	    	x = (int) Math.floor(midPoint[y]);
	    	if (Math.floor(midPoint[y]) != midPoint[y])
	    		x++;
	    	y++;
	    }
	    for (; x <= T0 - 1; x++) {
	    	path.add(new int[] {T0 - 1, x});
	    }
		return path;
	}

	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	public static void main(String[] args) {
		float[][][] dF1 = new float[1][1][1];
		Opts opts = new Opts(1);
		String proPath = "D:\\Test\\";
		
		HashMap<Integer, ArrayList<int[]>> seLst1 = new HashMap<Integer, ArrayList<int[]>>();
		HashMap<Integer, ArrayList<int[]>> subEvtLst1 = new HashMap<Integer, ArrayList<int[]>>();
		HashMap<Integer, Step3MajorityResult> majorInfo1 = new HashMap<Integer, Step3MajorityResult>();
		int[] seLabel1 = new int[1];
		try {
			seLst1 = Helper.readObjectFromFile(proPath, "seLst1.ser", seLst1.getClass());
			subEvtLst1 = Helper.readObjectFromFile(proPath, "subEvtLst1.ser", subEvtLst1.getClass());
			seLabel1 = Helper.readObjectFromFile(proPath, "seLabel1.ser", seLabel1.getClass());
			majorInfo1 = Helper.readObjectFromFile(proPath, "majorInfo1.ser", majorInfo1.getClass());
			dF1 = Helper.readObjectFromFile(proPath, "dF1.ser", dF1.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		opts.gtwSmo = 0.2f;
		Step4Res res = Step4Helper.se2evtTop(dF1, seLst1, subEvtLst1, seLabel1, majorInfo1, opts);
		
	}

	
	
}
