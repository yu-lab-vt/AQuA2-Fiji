package va.vt.cbilAQuA2.run;

import java.util.ArrayList;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;

import ij.ImagePlus;
import inra.ijpb.watershed.MarkerControlledWatershedTransform3D;
import net.sourceforge.jdistlib.NonCentralT;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.tools.GaussFilter;

import java.util.Collections;
import java.util.Comparator;

public class Step3Helper {
	/**
	 * set seLst1, subEvtLst1, seLabel1, majorInfo1, sdLst1
	 */
	static long start;
	static long end;
	
	public static Step3HelperResult seDetection(float[][][] dF, float[][][] datOrg, HashMap<Integer, ArrayList<int[]>> arLst, Opts opts) {
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		int[][][] Map = new int[H][W][T]; 
		int[] seLstInfoLabel;
		HashMap<Integer, ArrayList<int[]>> sdLst, curRegions, evtLst, seLst;
        
		// seed detection
        System.out.println("Seed detection");
        start = System.currentTimeMillis();
        curRegions = seedDetect2DSAccelerate(Helper.copy3Darray(dF), datOrg, Map, arLst, opts);
        sdLst = Helper.label2idx(Map);
        
        showTime();
        
        // marker controlled watershed
        System.out.println("Watershed grow");
        curRegions = markerControlledSplitting_Ac(Map, sdLst, curRegions, dF, opts);
        
//        // load from matlab result -- to be deleted
        Map = Helper.loadMatlabStep2Result();       
        
        evtLst = Helper.label2idx(Map);
        
        // remove empty
        boolean[] nonEmpty = new boolean[evtLst.size()];
        for (int i = 1; i <= evtLst.size(); i++) {
        	nonEmpty[i - 1] = evtLst.get(i).size() > 0;
        }
        sdLst = Helper.filterWithMask(sdLst, nonEmpty);
        evtLst = Helper.filterWithMask(evtLst, nonEmpty);
        
        // select major part
        System.out.println("Select majority part");
        HashMap<Integer, Step3MajorityResult> majorityEvt0 = getMajority_Ac(sdLst, evtLst, dF, opts);
        
        
        // according to curve, refine
        System.out.println("Refining");
        boolean[] isGood = majorCurveFilter2(datOrg, dF, sdLst, evtLst, majorityEvt0, opts);
        sdLst = Helper.filterWithMask(sdLst, isGood);
        evtLst = Helper.filterWithMask(evtLst, isGood);
        majorityEvt0 = Helper.filterWithMaskMajor(majorityEvt0, isGood);
        
        // merge to super event
        System.out.println("Merging signals with similar temporal patterns");
        Step3MergingInfo mergingInfo = createMergingInfo(evtLst, majorityEvt0, curRegions, opts);
        seLst = new HashMap<Integer, ArrayList<int[]>>();
        seLstInfoLabel = mergingSEbyInfo_UpdateSpa(evtLst, majorityEvt0, mergingInfo, curRegions, opts, seLst);
        
        // label to Map
        Map = new int[H][W][T]; 
        ArrayList<int[]> pix;
        for (int i = 1; i <= seLst.size(); i++) {
        	pix = seLst.get(i);
        	Helper.setValue(Map, pix, i);
        }
        Step3HelperResult res = new Step3HelperResult(sdLst, evtLst, seLst, majorityEvt0, seLstInfoLabel, Map);
        
        return res;
	}
	
	public static int[] mergingSEbyInfo_UpdateSpa(HashMap<Integer, ArrayList<int[]>> evtLst,
			HashMap<Integer, Step3MajorityResult> majorityEvt, Step3MergingInfo mergingInfo,
			HashMap<Integer, ArrayList<int[]>> CC, Opts opts, HashMap<Integer, ArrayList<int[]>> seLst) {
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		int N = evtLst.size();
		HashMap<Integer, HashSet<Integer>> neibLst = mergingInfo.neibLst;
		HashMap<Integer, HashMap<Integer, Integer>> delayDif = mergingInfo.delayDif;
		
		// if detected gap, events cannot merge
		HashMap<Integer, HashSet<Integer>> spaFoot = new HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> ihw;
		int[] seLabel = new int[N + 1];
		for (int i = 1; i <= N; i++) {
			seLabel[i] = i;
			Step3MajorityResult major = majorityEvt.get(i);
			ihw = new HashSet<Integer>();
			for (Map.Entry<Integer, Integer> entry : major.ihwDelays.entrySet()) {
				ihw.add(entry.getKey());
			}
			spaFoot.put(i, ihw);
		}
		
		int curLabel;
		HashSet<Integer> neib0, mIhw1, mIhw2, tmpIhw;
		HashMap<Integer, Integer> curIhwDelays, neiIhwDelays;
		int id, id1, id2, root;
		int[] shift, seL0;
		float[] curve1, curve2;
		float timeDelay, n0, n1, n2;
		float maxDelay = opts.maxDelay;
		float[] row;
		for (int iReg = 1; iReg <= CC.size(); iReg ++) {
			ArrayList<Integer> labelsInActReg = mergingInfo.labelsInActRegs.get(iReg);
			
			// delays
			ArrayList<float[]> delayMatrix = new ArrayList<float[]>();
			for (int i = 0; i < labelsInActReg.size(); i ++) {
				curLabel = labelsInActReg.get(i);
				neib0 = neibLst.get(curLabel);
				curIhwDelays = majorityEvt.get(curLabel).ihwDelays;
				for (int nLabel : neib0) {
					if (nLabel < curLabel)
						continue;
					neiIhwDelays = majorityEvt.get(nLabel).ihwDelays;
					shift = getRelativeDelay(curIhwDelays, neiIhwDelays, opts);
					curve1 = majorityEvt.get(curLabel).curve;
					curve2 = majorityEvt.get(nLabel).curve;
					timeDelay = avgDist(curve1, curve2, majorityEvt.get(curLabel).t0, majorityEvt.get(nLabel).t0, shift);
					if (timeDelay <= maxDelay)
						delayMatrix.add(new float[]{curLabel, nLabel, timeDelay});
				}
			}
			if (delayMatrix.size() == 0)
				continue;
			
			// merging
			Collections.sort(delayMatrix, new Comparator<float[]>() {
				@Override
				public int compare(float[] e1, float[] e2) {
					if(e1[2]<e2[2])
						return -1;
					else if (e1[2]>e2[2])
						return 1;
					else
						return 0;
				}
			});
			
			seL0 = new int[N + 1];
			for (int i = 0; i <= N; i++)
				seL0[i] = i;
			
			for (int i = 0; i < delayMatrix.size(); i++) {
				row = delayMatrix.get(i);
				id1 = (int) row[0];
				id2 = (int) row[1];
				id1 = UF_find(seL0, id1);
				id2 = UF_find(seL0, id2);
				
				//
				mIhw1 = spaFoot.get(id1);
				mIhw2 = spaFoot.get(id2);
				
				tmpIhw = new HashSet<Integer>(mIhw1);
				tmpIhw.retainAll(mIhw2);
				
				n0 = tmpIhw.size();
				n1 = mIhw1.size();
				n2 = mIhw2.size();
				
				// large spatial overlap will evidence they do not belong to the same signal.
				if (n0 / n1 <= opts.overlap && n0 / n2 <= opts.overlap) {
					tmpIhw = new HashSet<Integer>(mIhw1);
					tmpIhw.addAll(mIhw2);
					if (id1 < id2) {
						seL0[id2] = id1;
						spaFoot.put(id1, tmpIhw);
					}else {
						seL0[id1] = id2;
						spaFoot.put(id2, tmpIhw);
					}
				}
			}
			
			//
			for (int i = 0; i < labelsInActReg.size(); i++) {
				id = labelsInActReg.get(i);
				root = UF_find(seL0, id);
				seL0[id] = root;
				seLabel[id] = seL0[id];
			}			
		}
		
		// final output
		HashSet<Integer> group = new HashSet<Integer>();
		for (int i = 1; i <= N; i++) {
			group.add(seLabel[i]);
		}
		ArrayList<Integer> sortGroup = new ArrayList<Integer>(group);
		Collections.sort(sortGroup);
		int[] mapping = new int[N + 1];
		int cnt = 1;
		for (int i = 0; i < sortGroup.size(); i++) {
			root = sortGroup.get(i);
			mapping[root] = cnt;
			cnt++;
		}
		ArrayList<int[]> pix;
		for (int i = 1; i <= N; i++) {
			root = seLabel[i];
			id = mapping[root];
			if (!seLst.containsKey(id)) {
				seLst.put(id, new ArrayList<int[]>());
			}
			pix = seLst.get(id);
			pix.addAll(evtLst.get(i));
			seLabel[i] = id;
		}
		
		return seLabel;
	}

	private static int UF_find(int[] labels, int id) {
		if (labels[id] != id) {
			labels[id] = UF_find(labels, labels[id]);
		}
		return labels[id];
	}

	private static float avgDist(float[] curve1, float[] curve2, int ts1, int ts2, int[] shift) {
		// align same part
		float maxV1, maxV2,  minVL1, minVR1, minVL2, minVR2;
		int tPeak1, tPeak2, t00, t11, te1, te2;
		ts1 += shift[0]; 
		ts2 += shift[1]; 		
		
		// curve 1
		tPeak1 = 0;
		maxV1 = Float.NEGATIVE_INFINITY;
		for (int t = 0; t < curve1.length; t++) {
			if (curve1[t] > maxV1) {
				maxV1 = curve1[t];
				tPeak1 = t;
			}
		}
		minVL1 = Float.POSITIVE_INFINITY;
		minVR1 = Float.POSITIVE_INFINITY;
		for (int t = 0; t <= tPeak1; t++) 
			minVL1 = Math.min(curve1[t], minVL1);
		for (int t = tPeak1; t < curve1.length; t++) 
			minVR1 = Math.min(curve1[t], minVR1);
		
		// curve2
		tPeak2 = 0;
		maxV2 = Float.NEGATIVE_INFINITY;
		for (int t = 0; t < curve2.length; t++) {
			if (curve2[t] > maxV2) {
				maxV2 = curve2[t];
				tPeak2 = t;
			}
		}
		minVL2 = Float.POSITIVE_INFINITY;
		minVR2 = Float.POSITIVE_INFINITY;
		for (int t = 0; t <= tPeak2; t++) 
			minVL2 = Math.min(curve2[t], minVL2);
		for (int t = tPeak2; t < curve2.length; t++) 
			minVR2 = Math.min(curve2[t], minVR2);
		
		// TW limitation: only check the common part that larger than 10%
		float thrL = Math.max(Math.max(minVL1, minVL2), 0.1f);
		float thrR = Math.max(Math.max(minVR1, minVR2), 0.1f);
		
		// curve1
		t00 = 0;
		t11 = curve1.length - 1;
		for (int t = tPeak1; t >= 0; t--) {
			if (curve1[t] <= thrL) {
				t00 = t + 1;
				break;
			}
		}
		for (int t = tPeak1; t < curve1.length; t++) {
			if (curve1[t] <= thrR) {
				t11 = t - 1;
				break;
			}
		}
		curve1 = Helper.crop1D(curve1, t00, t11);
		te1 = ts1 + t11; 
		ts1 += t00;
		if (t11 > t00) {
			for (int t = 0; t < curve1.length; t++) {
				curve1[t] -= Math.min(thrL, thrR);
			}
		}
		maxV1 -= Math.min(thrL, thrR);
		for (int t = 0; t < curve1.length; t++) {
			curve1[t] /= maxV1;
		}
		
		// curve2
		t00 = 0;
		t11 = curve2.length - 1;
		for (int t = tPeak2; t >= 0; t--) {
			if (curve2[t] <= thrL) {
				t00 = t + 1;
				break;
			}
		}
		for (int t = tPeak2; t < curve2.length; t++) {
			if (curve2[t] <= thrR) {
				t11 = t - 1;
				break;
			}
		}
		curve2 = Helper.crop1D(curve2, t00, t11);
		te2 = ts2 + t11; 
		ts2 += t00;
		if (t11 > t00) {
			for (int t = 0; t < curve2.length; t++) {
				curve2[t] -= Math.min(thrL, thrR);
			}
		}
		maxV2 -= Math.min(thrL, thrR);
		for (int t = 0; t < curve2.length; t++) {
			curve2[t] /= maxV2;
		}
		
		// other work
		if (ts1 < ts2)
			curve2 = Helper.fill0(curve2, ts2 - ts1, 0);
		else
			curve1 = Helper.fill0(curve1, ts1 - ts2, 0);
		
		if (te1 < te2)
			curve1 = Helper.fill0(curve1, 0, te2 - te1);
		else
			curve2 = Helper.fill0(curve2, 0, te1 - te2);
		curve1 = Helper.fill0(curve1, 1, 1);
		curve2 = Helper.fill0(curve2, 1, 1);
		ArrayList<int[]> xy = Helper.dtw(curve1, curve2);
		
		// minDur
		int cnt = 0;
		for (int t = 0; t < curve1.length; t++) {
			if (curve1[t] > 0)
				cnt++;
		}
		int minDur = cnt;
		cnt = 0;
		for (int t = 0; t < curve2.length; t++) {
			if (curve2[t] > 0)
				cnt++;
		}
		minDur = Math.min(minDur, cnt) + 2;
		
		// get relative delay
		cnt = 0;
		int[] p;
		float res = 0;
		for (int i = 0; i < xy.size(); i++) {
			p = xy.get(i);
			if (curve1[p[0]] > 0) {
				cnt ++;
				res += Math.abs(p[0] - p[1]);
			}
			if (curve2[p[1]] > 0) {
				cnt ++;
				res += Math.abs(p[0] - p[1]);
			}
		}
		res = res / cnt / minDur;
		return res;
	}

	private static int[] getRelativeDelay(HashMap<Integer, Integer> curIhwDelays,
			HashMap<Integer, Integer> neiIhwDelays, Opts opts) {
		int H = opts.H;
		int W = opts.W;
		float x0, y0, x1, y1;
		float xAvg = 0; float yAvg = 0;
		float xAvgD0 = 0; float yAvgD0 = 0;
		int nCur = curIhwDelays.size();
		int[] p;
		float dist = 0;
		int cnt = 0;
		int[] shifts = new int[2];
		
		// cur
		for (Map.Entry<Integer, Integer> entry : curIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			xAvg += p[0]; yAvg += p[1];
			if (entry.getValue() == 0) {
				xAvgD0 += p[0]; yAvgD0 += p[1];
				cnt ++;
			}
		}
		if (cnt > 0) {
			x0 = xAvgD0 / cnt; y0 = yAvgD0 / cnt;
		} else {
			x0 = xAvg / nCur; y0 = yAvg / nCur;
		}
		
		// nei
		int nNei = neiIhwDelays.size();
		xAvg = 0; yAvg = 0; xAvgD0 = 0; yAvgD0 = 0;
		cnt = 0;
		for (Map.Entry<Integer, Integer> entry : neiIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			xAvg += p[0]; yAvg += p[1];
			if (entry.getValue() == 0) {
				xAvgD0 += p[0]; yAvgD0 += p[1];
				cnt ++;
			}
		}
		if (cnt > 0) {
			x1 = xAvgD0 / cnt; y1 = yAvgD0 / cnt;
		} else {
			x1 = xAvg / nNei; y1 = yAvg / nNei;
		}
		
		
		float maxDist = 0;
		for (Map.Entry<Integer, Integer> entry : curIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			dist = (p[0] - x0) * (x1 - x0) + (p[1] - y0) * (y1 - y0);
			maxDist = Math.max(dist, maxDist);
		}
		float shift1 = 0;
		cnt = 0;
		for (Map.Entry<Integer, Integer> entry : curIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			dist = (p[0] - x0) * (x1 - x0) + (p[1] - y0) * (y1 - y0);
			if (dist >= 0.8 * maxDist) {
				shift1 += entry.getValue();
				cnt++;
			}
		}
		shifts[0] = getRound(shift1 / cnt);
		
		
		maxDist = 0;
		for (Map.Entry<Integer, Integer> entry : neiIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			dist = (p[0] - x1) * (x0 - x1) + (p[1] - y1) * (y0 - y1);
			maxDist = Math.max(dist, maxDist);
		}
		float shift2 = 0;
		cnt = 0;
		for (Map.Entry<Integer, Integer> entry : neiIhwDelays.entrySet()) {
			p = Helper.ind2sub(H, W, entry.getKey());
			dist = (p[0] - x1) * (x0 - x1) + (p[1] - y1) * (y0 - y1);
			if (dist >= 0.8 * maxDist) {
				shift2 += entry.getValue();
				cnt++;
			}
		}
		shifts[1] = getRound(shift2 / cnt);
		return shifts;
	}
	
	private static int getRound(double x) {
		if (x>=0)
			return (int) Math.round(x);
		else
			return (int) - Math.round(-x);
	}

	public static Step3MergingInfo createMergingInfo(HashMap<Integer, ArrayList<int[]>> evtLst,
			HashMap<Integer, Step3MajorityResult> majorityEvt, HashMap<Integer, ArrayList<int[]>> curRegions,
			Opts opts) {
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		int N = evtLst.size();
		
		// get neighbor relation
		int[][] dirs = Helper.dirGenerate(8);
		int[][][] Map = new int[H][W][T];
		for (int i = 1; i <= N; i++) {
			Helper.setValue(Map, evtLst.get(i), i);
		}
		
		HashMap<Integer, HashSet<Integer>> neibLst = new HashMap<Integer, HashSet<Integer>>();
		HashMap<Integer, HashMap<Integer, Integer>> delayDif = new HashMap<Integer, HashMap<Integer, Integer>>();
		int[] evtCCLabel = new int[N];
		HashMap<Integer, ArrayList<Integer>> labelsInActRegs = new HashMap<Integer, ArrayList<Integer>>();
		
		ArrayList<int[]> pix;
		int[] p;
		HashSet<Integer> neib0;
		int round, xx, yy, zz;
		for (int i = 1; i <= N; i++) {
			pix = evtLst.get(i);
			round = 0;
			neib0 = new HashSet<Integer>();
//			while (round <= 2 * opts.spaMergeDist) {
			for (int k = 0; k < dirs.length; k++) {
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					xx = Math.max(Math.min(H - 1, p[0] + dirs[k][0]), 0);
					yy = Math.max(Math.min(W - 1, p[1] + dirs[k][1]), 0);
					neib0.add(Map[xx][yy][p[2]]);
				}
			}
			round++;
//			}
			neib0.remove(0);
			neib0.remove(i);
			neibLst.put(i, neib0);
		}
		
		// get the corresponding relation between actReg and event
		HashSet<Integer> labelsInActReg;
		int curLabel;
		for (int i = 1; i <= curRegions.size(); i++) {
			pix = curRegions.get(i);
			labelsInActReg = new HashSet<Integer>();
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				curLabel = Map[p[0]][p[1]][p[2]];
				labelsInActReg.add(curLabel);
			}
			labelsInActReg.remove(0);
			ArrayList<Integer> labelArray = new ArrayList<>(labelsInActReg);
			Collections.sort(labelArray);
			for (int label : labelsInActReg) {
				evtCCLabel[label - 1] = i;
			}
			labelsInActRegs.put(i, labelArray);			
		}
		
		return new Step3MergingInfo(neibLst, delayDif, evtCCLabel, labelsInActRegs);
	}

	public static boolean[] majorCurveFilter2(float[][][] datOrg, float[][][] dF, 
			HashMap<Integer, ArrayList<int[]>> sdLst, HashMap<Integer, ArrayList<int[]>> evtLst,
			HashMap<Integer, Step3MajorityResult> majorityEvt0, Opts opts) {
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		opts.spaSmo = 3;
		
		float[][][] scoreMap = new float[H][W][T];
		for (int tt = 0; tt < T; tt++) {
		    float[][] slice = Helper.getSlice(dF, tt);
		    float[][] filteredSlice = GaussFilter.gaussFilter(slice, opts.spaSmo, opts.spaSmo);
		    Helper.setSliceRev(scoreMap, filteredSlice, tt); 
		}      
//		Helper.viewMatrix(10, 10,  1, "", scoreMap);
		
		int[][][] Map = new int[H][W][T];
		ArrayList<int[]> pix, pix1, newPix;
		int[] p;
		for (int i = 1; i <= evtLst.size(); i++) {
			pix = evtLst.get(i);
			Helper.setValue(Map, pix, i);
		}
		
		int[][] dirs = Helper.dirGenerate(26);
		HashMap<Integer, float[]> mus = new HashMap<Integer, float[]>();
		HashMap<Integer, float[][]> covMatrixs = new HashMap<Integer, float[][]>();
		try {
			mus = Helper.readObjectFromFile(ImageDealer.class.getClassLoader().getResourceAsStream("mus.ser"), mus.getClass());
			covMatrixs = Helper.readObjectFromFile(ImageDealer.class.getClassLoader().getResourceAsStream("covMatrixs.ser"), covMatrixs.getClass());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<Integer> majorUpdate = new HashSet<Integer>();
		
		// update
		int t00, t11, t0, t1, t_scl, x0, x1, y0, y1, H0, W0, T0, xx, yy, zz, curLabel;
		int ext = 1;
		int[][][] Map0, Mask0;
		float[][][] scoreMap0, MapOut;
		HashSet<Integer> ihw, neib0;
		float[] evtCurve, curve0;
		boolean hasPeak;
		boolean[] labeled;
		for (int i = 1; i <= evtLst.size(); i++) {
			t00 = T; t11 = 0;
			pix = sdLst.get(i);
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				t00 = Math.min(t00, p[2]);
				t11 = Math.max(t11, p[2]);	
			}
			
			// check curve
			pix = evtLst.get(i);
			ihw = new HashSet<Integer>();
			t0 = T; t1 = 0;
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				t0 = Math.min(t0, p[2]);
				t1 = Math.max(t1, p[2]);	
				if (p[2] >= t00 && p[2] <= t11) {
					ihw.add(Helper.sub2ind(H, W, p[0], p[1]));
					
				}
			}
			evtCurve = Helper.getAvgCurve(datOrg, ihw);
			t_scl = Math.max(1, Math.round(((float) t1 - t0 + 1) / opts.TPatch));
			curve0 = Helper.tempDownSample(evtCurve, t_scl);
			hasPeak = eventCurveSignificance(curve0, Math.max(0, ((t0 + 1) / t_scl) - 1), (int) Math.ceil(((float) (t1 + 1)) / t_scl) - 1,
					Math.max(0, ((t00 + 1) / t_scl) - 1), (int) Math.ceil(((float) (t11 + 1)) / t_scl) - 1, opts.sigThr, mus, covMatrixs);
			if (hasPeak)
				continue;
			
			evtLst.put(i, new ArrayList<int[]>());
			
			// find neib
			neib0 = new HashSet<Integer>();
			for (int k = 0; k < dirs.length; k++) {
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					xx = Math.max(Math.min(H - 1, p[0] + dirs[k][0]), 0);
					yy = Math.max(Math.min(W - 1, p[1] + dirs[k][1]), 0);
					zz = Math.max(Math.min(T - 1, p[2] + dirs[k][2]), 0);
					if (Map[xx][yy][zz] > 0 && Map[xx][yy][zz] != i) {
						neib0.add(Map[xx][yy][zz]);
					}
				}
			}
			if (neib0.size() == 0) {
				Helper.setValue(Map, pix, 0);
				continue;
			}else if (neib0.size() == 1) {
				Integer[] labelArray = neib0.toArray(new Integer[1]);
				curLabel = labelArray[0];
				Helper.setValue(Map, pix, curLabel);
				evtLst.get(curLabel).addAll(pix);
				majorUpdate.add(curLabel);
				continue;
			}
			
			// watershed
			x0 = H; y0 = W; x1 = 0; y1 = 0;
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				x0 = Math.min(x0, p[0]);
				x1 = Math.max(x1, p[0]);
				y0 = Math.min(y0, p[1]);
				y1 = Math.max(y1, p[1]);
			}
			x0 = Math.max(0, x0 - ext);
			x1 = Math.min(H - 1, x1 + ext);
			y0 = Math.max(0, y0 - ext);
			y1 = Math.min(W - 1, y1 + ext);			
			t0 = Math.max(0, t0 - ext);
			t1 = Math.min(T - 1, t1 + ext);
			
			H0 = x1 - x0 + 1;
			W0 = y1 - y0 + 1;
			T0 = t1 - t0 + 1;
			
			Map0 = new int[H0][W0][T0];
			Mask0 = new int[H0][W0][T0];
			scoreMap0 = new float[H0][W0][T0];
			for (int x = x0; x <= x1; x++) {
				for (int y = y0; y <= y1; y++) {
					for (int z = t0; z <= t1; z++) {
						scoreMap0[x - x0][y - y0][z - t0] = scoreMap[x][y][z];
						curLabel = Map[x][y][z];
						if (neib0.contains(curLabel)) {
							Map0[x - x0][y - y0][z - t0] = curLabel;
							Mask0[x - x0][y - y0][z - t0] = 1;
						}
						if (curLabel == i)
							Mask0[x - x0][y - y0][z - t0] = 1;			
					}
				}
			}
			
			// here no need to separate region since watershed algorithm is different 
			// marker controlled watershed
			ImagePlus input = Helper.convertToImgPlus(scoreMap0);
			ImagePlus marker = Helper.convertToImgPlus(Map0);
			ImagePlus mask = Helper.convertToImgPlus(Mask0);
			MarkerControlledWatershedTransform3D watershed = new MarkerControlledWatershedTransform3D (input, marker, mask, 26);
			ImagePlus result = watershed.applyWithSortedListAndDams();
			MapOut = Helper.convertImgPlusToArray(result);
			
			// fill the gap
			pix1 = new ArrayList<int[]>();
			for (int x = 0; x < H0; x++) {
				for (int y = 0; y < W0; y++) {
					for (int z = 0; z < T0; z++) {
						if ((int) MapOut[x][y][z] == 0 && Mask0[x][y][z] > 0) {
							pix1.add(new int[] {x, y, z});
						}		
					}
				}
			}
			
			while (pix1.size() > 0) {
				labeled = new boolean[pix1.size()];
				for (int k = 0; k < dirs.length; k++) {
					for (int pId = 0; pId < pix1.size(); pId++) {
						if (labeled[pId])
							continue;
						p = pix1.get(pId);
						xx = Math.max(Math.min(H0 - 1, p[0] + dirs[k][0]), 0);
						yy = Math.max(Math.min(W0 - 1, p[1] + dirs[k][1]), 0);
						zz = Math.max(Math.min(T0 - 1, p[2] + dirs[k][2]), 0);
						if (MapOut[xx][yy][zz] > 0) {
							MapOut[p[0]][p[1]][p[2]] = MapOut[xx][yy][zz];
							labeled[pId] = true;
						}
					}
				}
				
				newPix = new ArrayList<int[]>();
				for (int pId = 0; pId < pix1.size(); pId++) {
					if (!labeled[pId])
						newPix.add(pix1.get(pId));
				}
				pix1 = newPix;
			}			
			
			// update
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				curLabel = (int) MapOut[p[0] - x0][p[1] - y0][p[2] - t0];
				evtLst.get(curLabel).add(p);
				Map[p[0]][p[1]][p[2]] = curLabel;
				majorUpdate.add(curLabel);
			}
		}
		
		// update majority
		float[] curve00;
		for (int i : majorUpdate) {
			if (evtLst.get(i).size() == 0)
				continue;
			pix = sdLst.get(i);
			t0 = T; t1 = 0;
			ihw = Helper.getUniqueSpa(pix, H, W);
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				t0 = Math.min(t0, p[2]);
				t1 = Math.max(t1, p[2]);
			}
			Step3MajorityResult res = new Step3MajorityResult();
			seed2Majority(ihw, dF, evtLst.get(i), t0, t1, opts, res);
			
			if (res.t1 - res.t0 > 0) {
				for(HashMap.Entry<Integer, Integer> entry: res.ihwDelays.entrySet()) {
					if (entry.getValue() == 0) {
						ihw.add(entry.getKey());
					}
				}
				curve00 = Helper.getAvgCurve(dF, ihw);
				curve00 = Helper.crop1D(curve00, res.t0, res.t1);
				Helper.normalizeCurve(curve00);
			}else {
				curve00 = new float[1];
				curve00[0] = 1;
			}
			res.curve = curve00;
			majorityEvt0.put(i, res);
		}
		boolean[] isGood = new boolean[evtLst.size()];
		for (int i = 1; i <= evtLst.size(); i++) {
			isGood[i - 1] = evtLst.get(i).size() > 0;
		}
		
		return isGood;
	}

	

	public static HashMap<Integer, Step3MajorityResult> getMajority_Ac(HashMap<Integer, ArrayList<int[]>> sdLst,
			HashMap<Integer, ArrayList<int[]>> evtLst, float[][][] dF, Opts opts) {
		// TODO Auto-generated method stub
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		HashMap<Integer, Step3MajorityResult> majorityEvt0 = new HashMap<Integer, Step3MajorityResult>();
		ArrayList<int[]> pix;
		HashSet<Integer> ihw;
		int t0, t1;
		int[] p;
		float[] curve00;
		for (int i = 1; i <= sdLst.size(); i++) {
			pix = sdLst.get(i);
			t0 = T; t1 = 0;
			ihw = Helper.getUniqueSpa(pix, H, W);
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				t0 = Math.min(t0, p[2]);
				t1 = Math.max(t1, p[2]);
			}
			Step3MajorityResult res = new Step3MajorityResult();
			seed2Majority(ihw, dF, evtLst.get(i), t0, t1, opts, res);
			
			if (res.t1 - res.t0 > 0) {
				for(HashMap.Entry<Integer, Integer> entry: res.ihwDelays.entrySet()) {
					if (entry.getValue() == 0) {
						ihw.add(entry.getKey());
					}
				}
				curve00 = Helper.getAvgCurve(dF, ihw);
				curve00 = Helper.crop1D(curve00, res.t0, res.t1);
				Helper.normalizeCurve(curve00);
			}else {
				curve00 = new float[1];
				curve00[0] = 1;
			}
			res.curve = curve00;
			majorityEvt0.put(i, res);
		}
		
		return majorityEvt0;
	}

	private static void seed2Majority(HashSet<Integer> ihw0, float[][][] dF, ArrayList<int[]> curEvt, int t00, int t11,
			Opts opts, Step3MajorityResult res) {
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		int[][] dirs = Helper.dirGenerate(8);
		
		// reference curve, normalize
		float[] refCurve0 = Helper.getAvgCurve(dF, ihw0);
		float noise = Helper.estimateNoiseByMean(refCurve0);
		Helper.normalizeCurveByNoise(refCurve0, noise);
		int t0 = T;
		int t1 = 0;
		int[] p;
		HashSet<Integer> curEvtSet = new HashSet<Integer>();
		for (int pId = 0; pId < curEvt.size(); pId++) {
			p = curEvt.get(pId);
			t0 = Math.min(t0, p[2]);
			t1 = Math.max(t1, p[2]);
			curEvtSet.add(Helper.sub2ind(H, W, T, p[0], p[1], p[2]));
		}
		int[] tws = getMajorityTem(GaussFilter.gaussFilter(refCurve0, 2), t00, t11, t0, t1);		
		t00 = tws[0];
		t11 = tws[1];
		res.t0 = t00;
		res.t1 = t11;
		int tPeak = tws[2];
		int[][] delayMap = new int[H][W];
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < W; j++) {
				delayMap[i][j] = Integer.MIN_VALUE;
			}
		}
		for (int index : ihw0) {
			p = Helper.ind2sub(H, W, index);
			delayMap[p[0]][p[1]] = 0;
		}
		
		// temporal downsample
		int durOrg = t11 - t00 + 1;
		int t_scl = Math.max(1, Math.round(((float) durOrg) / opts.TPatch));
		float[] refCurve = Helper.tempDownSample(Helper.crop1D(refCurve0, t00, t11), t_scl);
		int dur = refCurve.length;
		
		float rThr = 0.7f;
		HashSet<Integer> ihw = Helper.getUniqueSpa(curEvt, H, W);
		float[][] maxCor = new float[H][W];
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				maxCor[x][y] = -1f;
			}
		}
		boolean positiveShift_checked = false;
		int preShift = 0;
		int[] posibleShifts = new int[dur * 2 + 1];
		for (int i = 0; i <= dur; i++) {
			posibleShifts[i] = i;
		}
		for (int i = dur + 1; i < posibleShifts.length; i++) {
			posibleShifts[i] = - (i - dur);
		}
		int shift, tStart, tEnd;
		float r;
		float[] curCurve;
		boolean[][] BW;
		HashMap<Integer, Float> tmpCor;
		HashMap<Integer, ArrayList<int[]>> cc;
		ArrayList<int[]> curGroup;
		int ih1, iw1;
		boolean findConnected;
		ArrayList<int[]> selectedPix;
		for (int shiftId = 0; shiftId < posibleShifts.length; shiftId ++) {
			shift = posibleShifts[shiftId];
			tStart = t00 + shift * t_scl;
			tEnd = tStart + durOrg - 1;
			
			if (tStart < 0 || tEnd >= T) 
				continue;
			if (shift > 0 && positiveShift_checked)
				continue;
			if (shift == -1)
				preShift = 0;
			
			// find correlated curves under current delay, and check whether largest correlation
			tmpCor = new HashMap<Integer, Float>();
			BW = new boolean[H][W];
			
			for (int index : ihw) {
				p = Helper.ind2sub(H, W, index);
				curCurve = Helper.crop1D(dF[p[0]][p[1]], tStart, tEnd) ;
				curCurve = Helper.tempDownSample(curCurve, t_scl);
				r = Helper.corr(refCurve, curCurve);
				if (r > rThr && r > maxCor[p[0]][p[1]]) {
					tmpCor.put(index, r);
					BW[p[0]][p[1]] = true;
				}
			}
			
			// check continuity
			cc = new HashMap<Integer, ArrayList<int[]>>();
			Helper.bfsConn2D(BW, cc);
			selectedPix = new ArrayList<int[]>();
			for (int k = 1; k <= cc.size(); k++) {
				findConnected = false;
				curGroup = cc.get(k);
				for (int j = 0; j < dirs.length; j++) {
					for (int pId = 0; pId < curGroup.size(); pId ++ ) {
						p = curGroup.get(pId);
						ih1 = Math.max(0, Math.min(H - 1, p[0] + dirs[j][0]));
						iw1 = Math.max(0, Math.min(W - 1, p[1] + dirs[j][1]));
						if (delayMap[ih1][iw1] == preShift) {
							findConnected = true;
							break;
						}
					}
					if (findConnected) {
						for (int pId = 0; pId < curGroup.size(); pId ++ ) {
							p = curGroup.get(pId);
							maxCor[p[0]][p[1]] = tmpCor.get(Helper.sub2ind(H, W, p[0], p[1]));
							if (curEvtSet.contains(Helper.sub2ind(H, W, T, p[0], p[1], tPeak + shift * t_scl))) {
								selectedPix.add(p);
							}							
						}						
						break;
					}
				}
			}
			
			// update
			if (selectedPix.isEmpty()) {
				if (!positiveShift_checked) {
					positiveShift_checked = true;
				}else {
					break;
				}
			}
			
			for (int pId = 0; pId < selectedPix.size(); pId ++ ) {
				p = selectedPix.get(pId);
				delayMap[p[0]][p[1]] = shift;
			}
			
			preShift = shift;
		}
		
		HashMap<Integer, Integer> ihwDelays = new HashMap<Integer, Integer>();
		for (int i = 0; i < H; i ++) {
			for (int j = 0; j < W; j ++) {
				if (delayMap[i][j] > Integer.MIN_VALUE) {
					ihwDelays.put(Helper.sub2ind(H, W, i, j), delayMap[i][j] * t_scl);
				}
			}
		}
		res.ihwDelays = ihwDelays;
		

	}

	private static int[] getMajorityTem(float[] curve, int t00, int t11, int t0, int t1) {
		// TODO Auto-generated method stub
		float s0 = 1;
		int tPeak = 0;
		// get Peak
		float maxV = Float.NEGATIVE_INFINITY;
		for (int t = t00; t <= t11; t++) {
			if (curve[t] > maxV) {
				maxV = curve[t];
				tPeak = t;
			}
		}
		
		// start
		int tw0 = tPeak;
		float minV = Float.POSITIVE_INFINITY;
		for (int t = t00; t <= tPeak; t++) {
			if (curve[t] < minV) {
				minV = curve[t];
				tw0 = t;
			}
		}
		
		int ts = tw0;
		
		for (int t = tw0; t >= t0; t--) {
			if (curve[t] < minV) {
				minV = curve[t];
				ts = t;
			}else {
				if (curve[t] - minV >= 3 * s0) {
					break;
				}
			}
		}
		
		// end
		int tw1 = tPeak;
		minV = Float.POSITIVE_INFINITY;
		for (int t = tPeak; t <= t11; t++) {
			if (curve[t] < minV) {
				minV = curve[t];
				tw1 = t;
			}
		}
		
		int te = tw1;
		
		for (int t = tw1; t <= t1; t++) {
			if (curve[t] < minV) {
				minV = curve[t];
				te = t;
			}else {
				if (curve[t] - minV >= 3 * s0) {
					break;
				}
			}
		}
		
		int[] tw = new int[] {ts, te, tPeak};
		
		return tw;
	}

	public static HashMap<Integer, ArrayList<int[]>> markerControlledSplitting_Ac(int[][][] Map,
			HashMap<Integer, ArrayList<int[]>> sdLst, HashMap<Integer, ArrayList<int[]>> curRegions, float[][][] dF,
			Opts opts) {
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		opts.spaSmo = 3;
		
		float[][][] scoreMap = new float[H][W][T];
		for (int tt = 0; tt < T; tt++) {
		    float[][] slice = Helper.getSlice(dF, tt);
//		    Helper.viewMatrix(10, 10, "", slice);
		    float[][] filteredSlice = GaussFilter.gaussFilter(slice, opts.spaSmo, opts.spaSmo);
//		    Helper.viewMatrix(10, 10, "", filteredSlice);
		    Helper.setSliceRev(scoreMap, filteredSlice, tt); 
		}      
//		Helper.viewMatrix(10, 10, 3, "scoreMap", scoreMap);
		
		int[] p;
		HashMap<Integer, HashSet<Integer>> seedsInRegion = new HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> labels;
		ArrayList<int[]> pix;
		for (int i = 1; i <= curRegions.size(); i++) {
			pix = curRegions.get(i);
			labels = new HashSet<Integer>();
			for (int pId = 0; pId < pix.size(); pId++) {
				p = pix.get(pId);
				if (Map[p[0]][p[1]][p[2]] > 0) {
					labels.add(Map[p[0]][p[1]][p[2]]);
				}
			}
			
			if (labels.size() == 1) {
				Integer[] labelArray = labels.toArray(new Integer[1]);
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					Map[p[0]][p[1]][p[2]] = labelArray[0];
				}
			}
			seedsInRegion.put(i, labels);
		}
		
		// watershed
		int x0, x1, y0, y1, t0, t1;
		int H0, W0, T0;
		int[][][] Map0;
		int[][][] Mask0;
		float[][][] scoreMap0, MapOut;
		for (int i = 1; i <= curRegions.size(); i++) {
			labels = seedsInRegion.get(i);
			if (labels.size() > 1) {
				pix = curRegions.get(i);
				x0 = H; x1 = 0; y0 = W; y1 = 0; t0 = T; t1 = 0;
				
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					x0 = Math.min(p[0], x0);
					x1 = Math.max(p[0], x1);
					y0 = Math.min(p[1], y0);
					y1 = Math.max(p[1], y1);
					t0 = Math.min(p[2], t0);
					t1 = Math.max(p[2], t1);
				}
				H0 = x1 - x0 + 1;
				W0 = y1 - y0 + 1;
				T0 = t1 - t0 + 1;
				
				// input of watershed algorithm
				Map0 = new int[H0][W0][T0];
				Mask0 = new int[H0][W0][T0];
				scoreMap0 = new float[H0][W0][T0];
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					Mask0[p[0] - x0][p[1] - y0][p[2] - t0] = 1;
					Map0[p[0] - x0][p[1] - y0][p[2] - t0] = Map[p[0]][p[1]][p[2]];
					scoreMap0[p[0] - x0][p[1] - y0][p[2] - t0] = scoreMap[p[0]][p[1]][p[2]];
				}
				
//				Helper.viewMatrix(100, 100, 1, "scoreMap0", scoreMap0);
				
				// marker controlled watershed
				ImagePlus input = Helper.convertToImgPlus(scoreMap0);
				ImagePlus marker = Helper.convertToImgPlus(Map0);
				ImagePlus mask = Helper.convertToImgPlus(Mask0);
				
				System.out.println("Watershed class");
				MarkerControlledWatershedTransform3D watershed = new MarkerControlledWatershedTransform3D (input, marker, mask, 26);
				System.out.println("Watershed return");
				ImagePlus result = watershed.applyWithSortedListAndDams();
				System.out.println("Watershed done");
				MapOut = Helper.convertImgPlusToArray(result);
				
				// assign label back
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					Map[p[0]][p[1]][p[2]] = (int) MapOut[p[0] - x0][p[1] - y0][p[2] - t0];
				}
			}
		}
		
		// update Map, grow one circle to remove gap
		boolean[] validRegions = new boolean[curRegions.size()];
		boolean[] labeled;
		ArrayList<int[]> newPix;
		int[][] dirs = Helper.dirGenerate(26);
		int xx, yy, zz;
		for (int i = 1; i <= curRegions.size(); i++) {
			labels = seedsInRegion.get(i);
			if (labels.size() > 0) {
				validRegions[i - 1] = true;
			}
			if (labels.size() > 1) {
				pix = curRegions.get(i);
				newPix = new ArrayList<int[]>();
				for (int pId = 0; pId < pix.size(); pId++) {
					p = pix.get(pId);
					if(Map[p[0]][p[1]][p[2]] == 0) {
						newPix.add(p);
					}
				}
				pix = newPix;
				while (pix.size() > 0) {
					labeled = new boolean[pix.size()];
					for (int k = 0; k < dirs.length; k++) {
						for (int pId = 0; pId < pix.size(); pId++) {
							if (labeled[pId])
								continue;
							p = pix.get(pId);
							xx = Math.max(Math.min(H - 1, p[0] + dirs[k][0]), 0);
							yy = Math.max(Math.min(W - 1, p[1] + dirs[k][1]), 0);
							zz = Math.max(Math.min(T - 1, p[2] + dirs[k][2]), 0);
							if (Map[xx][yy][zz] > 0) {
								Map[p[0]][p[1]][p[2]] = Map[xx][yy][zz];
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
			}
		}
		
		HashMap<Integer, ArrayList<int[]>> filterCurRegions = new HashMap<Integer, ArrayList<int[]>>();
		int cnt = 1;
		for (int i = 1; i <= curRegions.size(); i++) {
			if (validRegions[i - 1]) {
				filterCurRegions.put(cnt, curRegions.get(i));
				cnt++;
			}
		}
		
		return filterCurRegions;
	}

	/**
	 * set seLst1, subEvtLst1, seLabel1, majorInfo1, sdLst1
	 */
	public static HashMap<Integer, ArrayList<int[]>> seedDetect2DSAccelerate(float[][][] dF, float[][][] datOrg, int[][][] Map, HashMap<Integer, ArrayList<int[]>> arLst, Opts opts){
	    int H = dF.length;
	    int W = dF[0].length;
	    int T = dF[0][0].length;
	    opts.scaleRatios = new int[] {2, 4, 8};
	    ArrayList<Float> Thrs = new ArrayList<>();
        for (float i = opts.maxdF1; i >= opts.thrARScl; i -= opts.step) {
        	Thrs.add(i);
        }    
        
	    // assign saturation part can always be selected when checking seed
        if (opts.maxValueDat == Math.pow(2, opts.BitDepth) - 1) {
			for (int i = 0; i < H; i++) {
	            for (int j = 0; j < W; j++) {
	                for (int k = 0; k < T; k++) {
	                    if (datOrg[i][j][k] == 1) {
	                        dF[i][j][k] = Float.POSITIVE_INFINITY;
	                    }
	                }
	            }
	        }
		}
	    
	    
        int[] regSz = new int[arLst.size()];
        int[][][] activeMap = new int[H][W][T];
        for (int actId = 1; actId <= arLst.size(); actId++) {
        	ArrayList<int[]> l = arLst.get(actId);
        	boolean[][] curMap = new boolean[H][W];
        	int cnt = 0;
        	for (int pId = 0; pId < l.size(); pId++) {
        		int[] p = l.get(pId);
        		activeMap[p[0]][p[1]][p[2]] = actId;
        		if (!curMap[p[0]][p[1]]) {
        			cnt++;
        			curMap[p[0]][p[1]] = true;
        		}
        	}
        	regSz[actId - 1] = cnt;
        }
        
        // downsampled data - checked
        ArrayList<boolean[][][]> validMaps = new ArrayList<boolean[][][]>();
        ArrayList<float[][][]> datResize = normalizeAndResize(datOrg, opts);
        ArrayList<float[][][]> dFResize = new ArrayList<float[][][]>();
        int[] H0s = new int[opts.scaleRatios.length];
        int[] W0s = new int[opts.scaleRatios.length];
        for (int j = 0; j < opts.scaleRatios.length; j++) {
        	dFResize.add(Helper.imResize(dF, opts.scaleRatios[j]));
        	validMaps.add(Helper.imResizeAcitveMap(activeMap, opts.scaleRatios[j]));
        	H0s[j] = (int) Math.ceil((float) H / opts.scaleRatios[j]);
        	W0s[j] = (int) Math.ceil((float) W / opts.scaleRatios[j]);
        }
        
        float[][][] zscoreMap = new float[H][W][T];
        float curThr, curScore;
        boolean[][][] selectMap;
        boolean[][][] zscoreMapLarge0 = new boolean[H][W][T];
        boolean alreadyChecked, hasPeak;
        float[] scores, curve;
        int H0, W0, scaleRatio, cnt, t0, t1, arLabel, dur, orgSz, t_scl;
        int[] pix0;
        HashMap<Integer, ArrayList<int[]>> curRegions, filteredRegions;
        ArrayList<int[]> pix;
        
        
    	HashMap<Integer, float[]> mus = new HashMap<Integer, float[]>();
		HashMap<Integer, float[][]> covMatrixs = new HashMap<Integer, float[][]>();
		try {
			mus = Helper.readObjectFromFile(ImageDealer.class.getClassLoader().getResourceAsStream("mus.ser"), mus.getClass());
			covMatrixs = Helper.readObjectFromFile(ImageDealer.class.getClassLoader().getResourceAsStream("covMatrixs.ser"), covMatrixs.getClass());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        
	    for (int k = 0; k < Thrs.size(); k++) {
	    	curThr = Thrs.get(k);
//	    	System.out.println("k: " + k);
	    	for (int j = 0; j < opts.scaleRatios.length; j++) {
	    		 H0 = H0s[j];	    		
		    	 W0 = W0s[j];
	             scaleRatio = opts.scaleRatios[j];
	             selectMap = new boolean[H0][W0][T];
	             for (int x = 0; x < H0; x++) {
	 			    for (int y = 0; y < W0; y++) {
	 			        for (int z = 0; z < T; z++) {
	 			            selectMap[x][y][z] = dFResize.get(j)[x][y][z] > curThr && validMaps.get(j)[x][y][z];
	 			        }
	 			    }
	 			 }
	             
	             // filter
	             curRegions = new HashMap<Integer, ArrayList<int[]>>();
	             filteredRegions = new HashMap<Integer, ArrayList<int[]>>();
 				 Helper.bfsConn3D(selectMap, curRegions);
 				 cnt = 1;
 				 for (int cId = 1; cId <= curRegions.size(); cId++) {
 					 if (curRegions.get(cId).size() > ((float) opts.minSize) * opts.minDur / scaleRatio / scaleRatio / 3) {
 						filteredRegions.put(cnt, curRegions.get(cId));
 						cnt ++;
 					 }
 				 }
 				 
 				 // Work
 				curRegions = filteredRegions;
 				HashSet<Integer> ihw;
 				for (int i = 1; i <= curRegions.size(); i++) {
 					pix = curRegions.get(i);
 					ihw = Helper.getUniqueSpa(pix, H0, W0);
 					t0 = T;
 					t1 = 0;
 					for (int pId = 0; pId < pix.size(); pId++) {
 						int[] p = pix.get(pId);
 						t0 = Math.min(t0, p[2]);
 						t1 = Math.max(t1, p[2]);
 					}
 					
 					pix0 = pix.get(0);
 					dur = t1 - t0 + 1;
 					arLabel = 0;
 					for (int x = pix0[0] * scaleRatio; x < Math.min(H, (pix0[0] + 1) * scaleRatio); x ++) {
 						for (int y = pix0[1] * scaleRatio; y < Math.min(W, (pix0[1] + 1) * scaleRatio); y ++) {
 							if (arLabel == 0) {
 								arLabel = activeMap[x][y][pix0[2]];
 							}
 						}
 					}
 					// filter according to size and duration, also check seed detected or not
 					if (dur < opts.minDur || ihw.size() < Math.max(opts.minSize, regSz[arLabel - 1] * opts.seedSzRatio) / scaleRatio / scaleRatio) {
 						continue;
 					}
 					
 					alreadyChecked = false;
 					orgSz = 0;
 					for (int pId = 0; pId < pix.size(); pId++) {
 						int[] p = pix.get(pId);
 						for (int x = p[0] * scaleRatio; x < Math.min(H, (p[0] + 1) * scaleRatio); x ++) {
 	 						for (int y = p[1] * scaleRatio; y < Math.min(W, (p[1] + 1) * scaleRatio); y ++) {
 	 							orgSz++;
 	 							if (zscoreMap[x][y][p[2]] > 0) alreadyChecked = true;
 	 						}
 	 					}
 					}
 					
 					if (orgSz < Math.max(opts.minSize, regSz[arLabel - 1] * opts.seedSzRatio) || alreadyChecked) {
 						continue;
 					}
 					
 					// calculate significance
 					t_scl = Math.max(1, Math.round(((float) dur )/ opts.TPatch));
 					scores = getSeedScore_DS4(pix, t_scl, datResize.get(j), mus, covMatrixs);


 					if (getMin(scores) > opts.sigThr) {
 						curve = Helper.getAvgCurve(datResize.get(j), ihw);
 						curve = Helper.tempDownSample(curve, t_scl);
 						hasPeak = curveSignificance3(curve, Math.max(0, ((t0 + 1) / t_scl) - 1), (int) Math.ceil(((float) (t1 + 1)) / t_scl) - 1, opts.sigThr, mus, covMatrixs);
 						if (hasPeak) {
 							curScore = Math.min(scores[0], scores[1]);
 							// update
 							for (int pId = 0; pId < pix.size(); pId++) {
 		 						int[] p = pix.get(pId);
 		 						for (int x = p[0] * scaleRatio; x < Math.min(H, (p[0] + 1) * scaleRatio); x ++) {
 		 	 						for (int y = p[1] * scaleRatio; y < Math.min(W, (p[1] + 1) * scaleRatio); y ++) {
 		 	 							zscoreMap[x][y][p[2]] = Math.max(zscoreMap[x][y][p[2]], curScore);
 		 	 							if (zscoreMap[x][y][p[2]] > 0) {
 		 	 								zscoreMapLarge0[x][y][p[2]] = true;
 		 	 							}
 		 	 						}
 		 	 					}
 		 					}
 						}							
 					}
 				}
	    	}	    	
	    }

	    HashMap<Integer, ArrayList<int[]>> sdLst = new HashMap<Integer, ArrayList<int[]>>();
	    Helper.bfsConn3D(zscoreMapLarge0, sdLst); 
	    float maxScore;
	    for (int i = 1; i <= sdLst.size(); i++) {
	    	pix = sdLst.get(i);
	    	maxScore = 0;
	    	for (int pId = 0; pId < pix.size(); pId++) {
	    		int[] p = pix.get(pId);
	    		maxScore = Math.max(maxScore, zscoreMap[p[0]][p[1]][p[2]]);
	    	}
	    	for (int pId = 0; pId < pix.size(); pId++) {
	    		int[] p = pix.get(pId);
	    		if (zscoreMap[p[0]][p[1]][p[2]] == maxScore) {
	    			Map[p[0]][p[1]][p[2]] = i;
	    		}
	    	}
	    }
	    
	    selectMap = new boolean[H][W][T];
	    HashMap<Integer, ArrayList<int[]>> newArLst = new HashMap<Integer, ArrayList<int[]>>();
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		for (int z = 0; z < T; z++) {
	    			selectMap[x][y][z] = Map[x][y][z] > 0 | activeMap[x][y][z] > 0;
	    		}
	    	}
	    }
	    Helper.bfsConn3D(selectMap, newArLst); 
		return newArLst;
	}
	
	private static boolean eventCurveSignificance(float[] curve, int t0, int t1, int t00, int t11, float sigThr,
			HashMap<Integer, float[]> mus, HashMap<Integer, float[][]> covMatrixs) {
		boolean isSigLeft = false;
		boolean isSigRight = false;
		int T = curve.length;
		float maxThr = Float.NEGATIVE_INFINITY;
		float sigma0 = Helper.estimateNoiseByMedian(curve);
		Helper.normalizeCurveByNoise(curve, sigma0);
		int tPeak = 0;
		for (int t = t00; t <= t11; t++) {
			if (curve[t] > maxThr) {
				maxThr = curve[t];
				tPeak = t;
			}
		}
		t0 = Math.max(0, t0 - 1);
		t1 = Math.min(T - 1, t1 + 1);
		int tmp = t0;
		float minL = Float.POSITIVE_INFINITY;
		float minR = Float.POSITIVE_INFINITY;
		for (int t = t0; t <= tPeak; t++) {
			if (curve[t] < minL) {
				minL = curve[t];
				tmp = t;
			}
		}
		t0 = tmp;
		for (int t = tPeak; t <= t1; t++) {
			if (curve[t] < minR) {
				minR = curve[t];
				tmp = t;
			}
		}
		t1 = tmp;
		
		if (minL < minR) {
			// move t0
			tmp = tPeak;
			for (int t = tPeak; t >= t0; t--) {
				if (curve[t] < minR) {
					tmp = t;
					break;
				}
			}
			t0 = tmp;
		}else if (minL > minR) {
			tmp = tPeak;
			for (int t = tPeak; t <= t1; t++) {
				if (curve[t] < minL) {
					tmp = t;
					break;
				}
			}
			t1 = tmp;
		}
		
		float minThr = Float.POSITIVE_INFINITY;
		for (int t = Math.max(0, t0 - 5); t <= Math.min(T - 1, t1 + 5); t++) {
			minThr = Math.min(curve[t], minThr);
		}
		float[] thrs;
		if (maxThr == minThr) {
			thrs = new float[] {maxThr};
		}else {
			thrs = new float[21];
			float step = (maxThr - minThr) / 20;
			for (int k = 0; k < 21; k ++) {
				thrs[k] = maxThr - k * step;
			}
		}
		
		double curThr, tScoreL, tScoreR, L, z_Left, z_Right;
		double[] order_par;
		int ts, te, dur, t_Left_start, t_Right_end;
		ArrayList<Float> fg, bgL, bgR;
		for (int k = 0; k < thrs.length; k++) {
			if (isSigLeft && isSigRight) {
				return true;
			}
						
			curThr = thrs[k];
			ts = t0; te = t1;
			for (int t = tPeak; t >= t0; t--) {
				if (curve[t] < curThr) {
					ts = t + 1;
					break;
				}
			}
			for (int t = tPeak; t <= t1; t++) {
				if (curve[t] < curThr) {
					te = t - 1;
					break;
				}
			}
			
			dur = te - ts + 1;
			fg = new ArrayList<Float>();
			for (int t = ts; t <= te; t++) {
				fg.add(curve[t]);
			}
			
			// left
			t_Left_start = ts - 1;
			while (t_Left_start >= 0 && curve[t_Left_start] >= curThr)
				t_Left_start -= 1;
			tmp = 0;
			for (int t = t_Left_start; t >= 0; t--) {
				if (curve[t] >= curThr) {
					tmp = t + 1;
					break;
				}
			}
			t_Left_start = tmp;
			t_Left_start = Math.max(t_Left_start, ts - dur);
			bgL = new ArrayList<Float>();
			for (int t = t_Left_start; t <= ts - 1; t++) {
				bgL.add(curve[t]);
			}
			
			// right
			t_Right_end = te + 1;
			while (t_Right_end < T && curve[t_Right_end] >= curThr)
				t_Right_end += 1;
			tmp = T - 1;
			for (int t = t_Right_end; t < T; t++) {
				if (curve[t] >= curThr) {
					tmp = t - 1;
					break;
				}
			}
			t_Right_end = tmp;
			t_Right_end = Math.min(t_Right_end, te + dur);
			bgR = new ArrayList<Float>();
			for (int t = te + 1; t <= t_Right_end; t++) {
				bgR.add(curve[t]);
			}
			
			// left score
			if (bgL.size() > 0) {
				L = Helper.getMean(fg) - Helper.getMean(bgL);
				tScoreL = (L / Math.sqrt(1f / fg.size() + 1f / bgL.size()));
				if (tScoreL >= sigThr) {
					order_par = ordStatSmallSampleWith0s(fg, bgL, bgR, new ArrayList<Float>(), mus, covMatrixs);
					z_Left = (L - order_par[0]) / order_par[1];
					if (z_Left >= sigThr) {
						isSigLeft = true;
					}
				}
			}
			
			// right score
			if (bgR.size() > 0) {
				L = Helper.getMean(fg) - Helper.getMean(bgR);
				tScoreR = (L / Math.sqrt(1f / fg.size() + 1f / bgR.size()));
				if (tScoreR >= sigThr) {
					order_par = ordStatSmallSampleWith0s(fg, bgR, bgL, new ArrayList<Float>(), mus, covMatrixs);
					z_Right = (L - order_par[0]) / order_par[1];
					if (z_Right >= sigThr) {
						isSigRight = true;
					}
				}
			}
		}
		
		return isSigLeft & isSigRight;
	}
	
	private static boolean curveSignificance3(float[] curve, int t0, int t1, float sigThr, HashMap<Integer, float[]> mus, HashMap<Integer, float[][]> covMatrixs) {
		boolean isSigLeft = false;
		boolean isSigRight = false;
		int T = curve.length;
		float maxThr = Float.NEGATIVE_INFINITY;
		float minThr = Float.POSITIVE_INFINITY;
		float sigma0 = Helper.estimateNoiseByMedian(curve);
		Helper.normalizeCurveByNoise(curve, sigma0);
		for (int t = t0; t <= t1; t++) {
			maxThr = Math.max(maxThr, curve[t]);
			minThr = Math.min(minThr, curve[t]);
		}
		float[] thrs;
		if (maxThr == minThr) {
			thrs = new float[] {maxThr};
		}else {
			thrs = new float[6];
			float step = (maxThr - minThr) / 5;
			for (int k = 0; k < 6; k ++) {
				thrs[k] = maxThr - k * step;
			}
		}
		
		double curThr, tScoreL, tScoreR, L, z_Left, z_Right;
		double[] order_par;
		int ts, te, dur, t_Left_start, t_Right_end;
		ArrayList<Float> fg, bgL, bgR;
		for (int k = 0; k < thrs.length; k++) {
			curThr = thrs[k];
			ts = t0; te = t1;
			for (int t = t0; t <= t1; t++) {
				if (curve[t] >= curThr) {
					ts = t;
					break;
				}
			}
			for (int t = t1; t >= t0; t--) {
				if (curve[t] >= curThr) {
					te = t;
					break;
				}
			}
			dur = te - ts + 1;
			fg = new ArrayList<Float>();
			for (int t = ts; t <= te; t++) {
				fg.add(curve[t]);
			}
			t_Left_start = 1;
			t_Right_end = T - 1;
			// left
			for (int t = ts - 1; t >= 0; t--) {
				if (curve[t] >= curThr) {
					t_Left_start = t + 1;
					break;
				}
			}
			t_Left_start = Math.max(t_Left_start, ts - dur);
			bgL = new ArrayList<Float>();
			for (int t = t_Left_start; t <= ts - 1; t++) {
				bgL.add(curve[t]);
			}
			// right
			for (int t = te + 1; t < T; t++) {
				if (curve[t] >= curThr) {
					t_Right_end = t - 1;
					break;
				}
			}
			t_Right_end = Math.min(t_Right_end, te + dur);
			bgR = new ArrayList<Float>();
			for (int t = te + 1; t <= t_Right_end; t++) {
				bgR.add(curve[t]);
			}
			
			// left score
			if (bgL.size() > 0) {
				L = Helper.getMean(fg) - Helper.getMean(bgL);
				tScoreL = (L / Math.sqrt(1f / fg.size() + 1f / bgL.size()));
				if (tScoreL >= sigThr) {
					order_par = ordStatSmallSampleWith0s(fg, bgL, bgR, new ArrayList<Float>(), mus, covMatrixs);
					z_Left = (L - order_par[0]) / order_par[1];
					if (z_Left >= sigThr) {
						isSigLeft = true;
					}
				}
			}
			
			// right score
			if (bgR.size() > 0) {
				L = Helper.getMean(fg) - Helper.getMean(bgR);
				tScoreR = (L / Math.sqrt(1f / fg.size() + 1f / bgR.size()));
				if (tScoreR >= sigThr) {
					order_par = ordStatSmallSampleWith0s(fg, bgR, bgL, new ArrayList<Float>(), mus, covMatrixs);
					z_Right = (L - order_par[0]) / order_par[1];
					if (z_Right >= sigThr) {
						isSigRight = true;
					}
				}
			}
		}
		
		return isSigLeft & isSigRight;
	}

	private static float getMin(float[] scores) {
		float res = Float.MAX_VALUE;
		for (int i = 0; i < scores.length; i++) {
			res = Math.min(res, scores[i]);
		}
		return res;
	}

	private static float[] getSeedScore_DS4(ArrayList<int[]> pix, int t_scl, float[][][] dat, HashMap<Integer, float[]> mus, HashMap<Integer, float[][]> covMatrixs) {
		int H = dat.length;
		int W = dat[0].length;
		int T = dat[0][0].length;
		int T0 = Math.floorDiv(T, t_scl);
		
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
		float half_tscl = ((float) t_scl) / 2;
		int cnt, cnt2;
		int tDS;
		int index;
		for (int pId = 0; pId < pix.size(); pId++) {
			int[] p = pix.get(pId);
			tDS = (int) Math.ceil(((float)(p[2] + 1)) / t_scl) - 1;
			if (tDS < T0) {
				index = Helper.sub2ind(H, W, T0, p[0], p[1], tDS);
				cnt = count.getOrDefault(index, 0);
				count.put(index, cnt + 1);
			}
		}
		
		HashMap<Integer, HashSet<Integer>> ihw = new HashMap<Integer, HashSet<Integer>>();
		for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
			index = entry.getKey();
			cnt = entry.getValue();
			if (cnt > half_tscl) {
				int[] p = Helper.ind2sub(H, W, T0, index);
				index = Helper.sub2ind(H, W, p[0], p[1]);
				HashSet<Integer> l = ihw.getOrDefault(index, new HashSet<Integer>());
				l.add(p[2]);
				ihw.put(index, l);
			}
			
		}
		
		float[] scores = new float[4];
		if (ihw.size() == 0) {
			return scores;
		}
		
		int curIhw, t0, t1, dur, id, t00, t11;
		int nIhw = ihw.size();
		int[] curP, sz;
		sz = new int[nIhw];
		HashSet<Integer> curIt;
		float[] curve0, curve;
		float sqrt_t_scl = (float) Math.sqrt(t_scl);
		float dif;
		id = 0; cnt = 0; cnt2 = 0;
		ArrayList<Float>[] fgAll = new ArrayList[nIhw];
		ArrayList<Float>[] bgL = new ArrayList[nIhw];
		ArrayList<Float>[] bgR = new ArrayList[nIhw];
		ArrayList<Float>[] nanVec = new ArrayList[nIhw];
		ArrayList<Float> noise = new ArrayList<Float>();
		ArrayList<Integer> degreeOfFreedoms = new ArrayList<Integer>();
		
		for (Map.Entry<Integer, HashSet<Integer>> entry : ihw.entrySet()) {
			curIhw = entry.getKey();
			curIt = entry.getValue();
			curP = Helper.ind2sub(H, W, curIhw);
			curve0 = dat[curP[0]][curP[1]];
			curve = Helper.tempDownSample(curve0, t_scl); // * sqrt(t_scl)
			t0 = Integer.MAX_VALUE;
			t1 = 0;
			for (int t : curIt) {
				t0 = Math.min(t0, t);
				t1 = Math.max(t1, t);
			}
			dur = curIt.size();
			sz[id] = dur;
			fgAll[id] = new ArrayList<Float>();
			bgL[id] = new ArrayList<Float>();
			bgR[id] = new ArrayList<Float>();
			nanVec[id] = new ArrayList<Float>();
			
			// t0 - t1
			for (int t = t0; t <= t1; t++) {
				if (curIt.contains(t)){
					fgAll[id].add(curve[t] * sqrt_t_scl);
				}else {
					nanVec[id].add(curve[t] * sqrt_t_scl);
				}
			}
			
			// t_left
			for (int t = t0 - 1; t >= Math.max(0, t0 - dur); t--) {
				bgL[id].add(curve[t] * sqrt_t_scl);
			}
			t00 = Math.max(1, (t0 - dur - 1) * t_scl);
			t11 = (t0 - 2) * t_scl;
			if (t11 >= t00) {
				for (int t = t00; t <= t11; t++) {
					dif = (curve0[t] - curve0[t - 1]) * (curve0[t] - curve0[t - 1]);
					noise.add(dif);
					if (curve0[t - 1] == 0) cnt++;
				}
				if (curve0[t11] == 0) cnt++;
				cnt2 += t11 - t00 + 2;
				degreeOfFreedoms.add(t11 - t00 + 1);
			}else {
				degreeOfFreedoms.add(0);
			}
			
			
			// t_right
			for (int t = t1 + 1; t <= Math.min(T0 - 1, t1 + dur); t++) {
				bgR[id].add(curve[t] * sqrt_t_scl);
			}
			t00 = (t1 + 2) * t_scl;
			t11 = Math.min(T - 2, (t1 + dur + 1) * t_scl);
			if (t11 >= t00) {
				for (int t = t00; t <= t11; t++) {
					dif = (curve0[t] - curve0[t + 1]) * (curve0[t] - curve0[t + 1]);
					noise.add(dif);
					if (curve0[t + 1] == 0) cnt++;
				}
				if (curve0[t00] == 0) cnt++;
				cnt2 += t11 - t00 + 2;
				degreeOfFreedoms.add(t11 - t00 + 1);
			}else {
				degreeOfFreedoms.add(0);
			}

			id++;
		}
		
		
		ArrayList<Float> fg0 = Helper.flatten(fgAll);
		ArrayList<Float> bkL = Helper.flatten(bgL);
		ArrayList<Float> bkR = Helper.flatten(bgR);
		int n1 = bkL.size();
		int n2 = bkR.size();
		if (n1 + n2 <= 2 || n1 <= 1 || n2 <= 1 || noise.size() <= 2) {
			return scores;
		}
		
		float correctPar = Helper.truncatedKeptVar(((float) cnt) / cnt2);
		double sigma0 = Math.sqrt(Helper.getMean(noise) / correctPar / t_scl);
		double[] mus_Left = new double[nIhw];
		double[] mus_Right = new double[nIhw];
		double[] L_left = new double[nIhw];
		double[] L_right = new double[nIhw];
		double[] z_Left = new double[nIhw];
		double[] z_Right = new double[nIhw];
		double L; 
		double[] order_par;
		for (int i = 0; i < nIhw; i++) {
			L_left[i] = Float.NEGATIVE_INFINITY;
			L_right[i] = Float.NEGATIVE_INFINITY;
			
			if (bgL[i].size() > 0) {
				L = (Helper.getMean(fgAll[i]) - Helper.getMean(bgL[i])) / sigma0;
				order_par = ordStatSmallSampleWith0s(fgAll[i], bgL[i], bgR[i], nanVec[i], mus, covMatrixs);
				mus_Left[i] = order_par[0] / order_par[1];
				L_left[i] = L / order_par[1];
			}
			if (bgR[i].size() > 0) {
				L = (Helper.getMean(fgAll[i]) - Helper.getMean(bgR[i])) / sigma0;
				order_par = ordStatSmallSampleWith0s(fgAll[i], bgR[i], bgL[i], nanVec[i], mus, covMatrixs);
				mus_Right[i] = order_par[0] / order_par[1];
				L_right[i] = L / order_par[1];
			}
		}
		
		float sumDegree = Helper.getSumInteger(degreeOfFreedoms);
		int degree = Math.round(2 * sumDegree * sumDegree / (3 * sumDegree - degreeOfFreedoms.size()));
		double z_score1 = 0;
		double z_score2 = 0;
		int sumSz = 0;
		// --------- score may be different from MATLAB version
		// --------- since when p value is very small, like 1e-12, getScoreNonCentral will return a different value
		// --------- that may be caused by the issue that noncentral t distribution fitting method is different

		for (int i = 0; i < nIhw; i++) {
//			if (checkK == 0)
//				System.out.println("Score stage5 Left" + i + " " + L_left[i] + " " + degree + " " + mus_Left[i]);
			z_Left[i] = getScoreNonCentral(L_left[i], degree, mus_Left[i]);
//			z_Left[i] = L_left[i] - mus_Left[i];
//			if (checkK == 0)
//				System.out.println("Score stage5 Right" + i + " " + L_right[i] + " " + degree + " " + mus_Right[i]);
			z_Right[i] = getScoreNonCentral(L_right[i], degree, mus_Right[i]);
//			z_Right[i] = L_right[i] - mus_Right[i];
			z_score1 += Math.sqrt(sz[i]) * z_Left[i];
			z_score2 += Math.sqrt(sz[i]) * z_Right[i];
			sumSz += sz[i];
		}
		z_score1 /= Math.sqrt(sumSz);
		z_score2 /= Math.sqrt(sumSz);
		
		// t test
		double t_score1 = 0;
		double t_score2 = 0;
		if (bkL.size() > 0) {
			t_score1 = ((Helper.getMean(fg0) - Helper.getMean(bkL)) / sigma0 / Math.sqrt( 1f/ fg0.size() + 1f / bkL.size()));
		}
		if (bkR.size() > 0) {
			t_score2 = ((Helper.getMean(fg0) - Helper.getMean(bkR)) / sigma0 / Math.sqrt( 1f/ fg0.size() + 1f / bkR.size()));
		}
		if (noise.size() < 100) {
			t_score1 = transferTscore(t_score1, noise.size() - 1);
			t_score2 = transferTscore(t_score2, noise.size() - 1);
		}
		scores[0] = (float) z_score1;
		scores[1] = (float) z_score2;
		scores[2] = (float) t_score1;
		scores[3] = (float) t_score2;
		return scores;
	}

	public static float getScoreNonCentral(double l_left, int nu, double mus_Left) {
//		mus_Left = 0;
		NonCentralT nctcdf = new NonCentralT(nu, (float)mus_Left);
		double p = nctcdf.cumulative((float)l_left, false, false);
		NormalDistribution dist = new NormalDistribution(0, 1);
		float res = - (float) dist.inverseCumulativeProbability(p);
		return res;
	}
	
	private static float transferTscore(double t_score1, int nu) {
		TDistribution tDist = new TDistribution(nu);
		double p = 1 - tDist.cumulativeProbability(t_score1);
		NormalDistribution dist = new NormalDistribution(0, 1);
		return - (float) dist.inverseCumulativeProbability(p);
	}

	private static double[] ordStatSmallSampleWith0s(ArrayList<Float> fg, ArrayList<Float> bg,
			ArrayList<Float> nanVec, ArrayList<Float> nanVec2, HashMap<Integer, float[]> mus, HashMap<Integer, float[][]> covMatrixs) {
		double[] order_par = new double[2];
		if (fg.size() == 0 && bg.size() == 0) {
			order_par[0] = Float.NaN;
			order_par[1] = Float.NaN;
			return order_par;
		}
		int M = fg.size();
		int N = bg.size();
		int nanLen = nanVec.size() + nanVec2.size();
		int n = M + N + nanLen;
		
		ArrayList<float[]> all = new ArrayList<float[]>();
		for (int i = 0; i < M; i++) {
			all.add(new float[] {fg.get(i), 1});
		}
		for (int i = 0; i < N; i++) {
			all.add(new float[] {bg.get(i), -1});
		}
		for (int i = 0; i < nanVec.size(); i++) {
			all.add(new float[] {nanVec.get(i), 0});
		}
		for (int i = 0; i < nanVec2.size(); i++) {
			all.add(new float[] {nanVec2.get(i), 0});
		}
		
		Collections.sort(all, new Comparator<float[]>() {
			@Override
			public int compare(float[] e1, float[] e2) {
				if(e1[0]<e2[0])
					return -1;
				else if (e1[0]>e2[0])
					return 1;
				else
					return 0;
			}
		});
		
		float[] muVec = mus.get(n);
		float[][] covMatrix = covMatrixs.get(n);
		double mu, sigma;
		mu = 0;
		sigma = 0;
		for (int i = 0; i < n; i++) {
			if (all.get(i)[1] == 1) {
				mu += muVec[i] / M;
			}else if (all.get(i)[1] == -1) {
				mu -= muVec[i] / N;
			}
		}
		
		int label1, label2;
		double par1, par2;
		for (int i = 0; i < n; i++) {
			label1 = (int) all.get(i)[1];
			if (label1 == 0)
				par1 = 0;
			else if (label1 == 1)
				par1 = 1f / M;
			else
				par1 = - 1f / N;
			
			for (int j = 0; j < n; j++) {
				label2 = (int) all.get(j)[1];
				if (label2 == 0)
					par2 = 0;
				else if (label2 == 1)
					par2 = 1f / M;
				else
					par2 = - 1f / N;
				
				sigma += covMatrix[i][j] * par1 * par2;
			}
		}
		sigma = Math.sqrt(sigma);
		order_par[0] = mu;
		order_par[1] = sigma;
		
		return order_par;
	}

	/**
	 * set seLst1, subEvtLst1, seLabel1, majorInfo1, sdLst1
	 */
	private static ArrayList<float[][][]> normalizeAndResize(float[][][] datOrg, Opts opts) {
		int[] scaleRatios = opts.scaleRatios;       
		float[][] curVarMap, var1, var2, curStdMap;
		float[][][] datDS; 
		int scaleRatio;
		int T = datOrg[0][0].length;
		ArrayList<float[][][]> datResize = new ArrayList<>();

		// downsample, and estimate the noise
		//  ------------ checked ------------
        for (int i = 0; i < scaleRatios.length; i++) {
        	scaleRatio = scaleRatios[i];
        	datDS = Helper.imResize(datOrg, scaleRatio);
        	curVarMap = Helper.divideByScalar(Helper.meanSquaredDifferences(datDS), 2.0F);
        	
        	var1 = Helper.imResize(opts.tempVarOrg1, scaleRatio); 
        	var2 = Helper.imResize(Helper.elementWiseMultiplyAndDivide(opts.tempVarOrg1, 2, opts.correctPars1), scaleRatio); 
        	curStdMap = Helper.calculateSquareRoot(Helper.elementWiseMultiplyAndDivide(curVarMap, var2, var1));
        	
        	for (int t = 0; t < T; t++) {
        		for (int x = 0; x < datDS.length; x++) {
        			for (int y = 0; y < datDS[0].length; y++) {
        				datDS[x][y][t] /= curStdMap[x][y];
        			}
        		}
        	}
        	datResize.add(datDS);
        }        
        return datResize;
	}
	
	/**
     * Accesses the elements from a 3D matrix based on the provided indices.
     *
     * @param mat The 3D matrix.
     * @param lst The indices of the elements to be accessed.
     * @return A list of accessed elements.
     */

    
	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	public static void main(String[] args) {
		float[][][] dat = new float[1][1][1];
		float[][][] dF = new float[1][1][1];
		Opts opts = new Opts(1);
		
		HashMap<Integer, ArrayList<int[]>> arLst1 = new HashMap<Integer, ArrayList<int[]>>();
		String propath = "D:\\Test\\";
		try {
			arLst1 = Helper.readObjectFromFile(propath, "arLst1.ser", arLst1.getClass());
			dat = Helper.readObjectFromFile(propath, "datOrg1.ser", dat.getClass());
			dF = Helper.readObjectFromFile(propath, "dF1.ser", dF.getClass());
			opts = Helper.readObjectFromFile(propath, "opts.ser", opts.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		opts.maxDelay = 0.6f;
		opts.step = 0.5f;
		opts.overlap = 0.5f;
		
		seDetection(dF, dat, arLst1, opts);
	}
	
    
}