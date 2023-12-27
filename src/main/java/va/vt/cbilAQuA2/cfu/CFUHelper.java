package va.vt.cbilAQuA2.cfu;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import net.sourceforge.jdistlib.Normal;
import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
//import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.run.CFUDetect;
import va.vt.cbilAQuA2.run.Step1Helper;
import va.vt.cbilAQuA2.ui.AQuA2GUI;
import va.vt.cbilAQuA2.ui.CFUCurveLabel;

import java.util.Collections;
import java.util.Comparator;

public class CFUHelper {
	/**
	 * set seLst1, subEvtLst1, seLabel1, majorInfo1, sdLst1
	 */
	static long start;
	static long end;
	
	public static depRes calDependency(boolean[] seq1, boolean[] seq2, int maxDist) {
		// TODO Auto-generated method stub
		
		int T = seq1.length;
		float lambda = 0;
		ArrayList<Integer> seq1Occur = new ArrayList<>();
		for (int t = 0; t < T; t++) {
			if (seq2[t])
				lambda += 1;
			if (seq1[t])
				seq1Occur.add(t);
		}
		lambda /= T;
		float minPvalue = 1;
		int L, t0, t1, tCenter, tNext, minDelay, curDelay, count, winSz;
//		boolean[] window;
		HashMap<Integer, Integer> delays;
		HashMap<Integer, Integer> delaysMinPValue = null;
		int distMinPvalue = 0;
		float[] p;
		float curPvalue ;
//		ArrayList<int[]> windows;
		for (int dist = 0; dist <= maxDist; dist++) {
			L = 2 * dist + 1; 
//			windows = new ArrayList<>();
			delays = new HashMap<>();
			count = 0;
			t1 = -1;
			p = new float[seq1Occur.size()];
			for (int k = 0; k < seq1Occur.size(); k++) {
				tCenter = seq1Occur.get(k);
				t0 = Math.max(tCenter - dist, t1 + 1);
				if (k < seq1Occur.size() - 1) {
					tNext = seq1Occur.get(k + 1);
					if (tNext - dist <= tCenter + dist) {
						t1 = Math.round((float)(tCenter + tNext) / 2);
					}else {
						t1 = Math.min(tCenter + dist, T - 1);
					}
				}else {
					t1 = Math.min(tCenter + dist, T - 1);
				}
//				windows.add(new int[] {t0, t1, tCenter});
				
				winSz = t1 - t0 + 1;
				p[k] = (float) (1 - Math.exp(- lambda * winSz));
				
				minDelay = Integer.MAX_VALUE;
				for (int t = t0; t <= t1; t++) {
					if (seq2[t]) {
						curDelay = t - tCenter;
						if (Math.abs(curDelay) < Math.abs(minDelay)) {
							minDelay = curDelay;
						}
					}
				}
				
				if (minDelay < Integer.MAX_VALUE) {
					count++;
					delays.put(minDelay, delays.getOrDefault(minDelay, 0) + 1);
				}
			}
			
			curPvalue = myBinomialCDF(p, count);
			
			if (curPvalue < minPvalue) {
				minPvalue = curPvalue;
				delaysMinPValue = delays;
				distMinPvalue = dist;
			}
		}
		
		return new depRes(minPvalue, distMinPvalue, delaysMinPValue);
	}

	public static float myBinomialCDF(float[] p, int m) {
		float estiP = 1;
		
		if(m==0)
	        return estiP;
	    else if(m == p.length) {
	    	estiP = 0;
	    	for (int k = 0; k < p.length; k++)
	    		estiP += Math.log(p[k]);
	    	estiP = (float) Math.exp(estiP);
	    	return estiP;
	    }
		
		float sump = 0;
		for (int k = 0; k < p.length; k++)
			sump += p[k];
		
		float K_3d = 0;
		float K_2d = 0;
		if (Math.abs(m - sump) < 0.001) {
			for (int k = 0; k < p.length; k++) {
				K_3d += p[k] - 2 * p[k] * p[k] + 2 * p[k] * p[k] * p[k];
				K_2d += p[k] * (1 - p[k]);
			}
			estiP = (float) (0.5 - 1 / Math.sqrt(2 * Math.PI) * (K_3d / 6 / Math.pow(K_2d, 1.5) - 1 / 2 / Math.sqrt(K_2d)));
			return estiP;
		}
		
		double t_hat = solveK1d(p, m);
		double t_hat_exp = Math.exp(t_hat);
		float tmp;
		float K_function = 0;
	    for (int k = 0; k < p.length; k++) {
	    	tmp = (float) (p[k] * t_hat_exp + 1 - p[k]);
			K_2d += p[k] * (1 - p[k]) * t_hat_exp / tmp / tmp;
			K_function += Math.log(p[k] * t_hat_exp + 1 - p[k]);
		}
	    float w_hat = (float) Math.sqrt(2 * (t_hat * m - K_function));
	    if (t_hat < 0)
	    	w_hat = - w_hat;
	    else if (t_hat == 0)
	    	w_hat = 0;
	    
	    float u_hat = (float) ((1 - Math.exp( - t_hat)) * Math.sqrt(K_2d));
	    estiP =  (float) ((1 - Normal.cumulative(w_hat, 0, 1)) - Normal.density(w_hat, 0, 1, false) * (1 / w_hat - 1 / u_hat));
		return estiP;
	}

	private static double solveK1d(float[] p, int m) {
		if(m == p.length)
	        return Float.POSITIVE_INFINITY;
		
		double s = -1000;
		double t = 1000;
		double mid = 0;
	    double eps = 0.0000000001;
	    double mV = getValue(p, m, mid);
	    while(Math.abs(mV) > eps) {
	        if(mV > 0)
	            t = mid;
	        else
	            s = mid;
	        
	        mid = (s+t)/2;
	        mV = getValue(p,m,mid);
	    }
	    return mid;
	}

	private static double getValue(float[] p, int m, double t) {
		// TODO Auto-generated method stub
		double value = -m;
		double t_exp = Math.exp(t);
		for (int k = 0; k < p.length; k++)
			value += 1 - (1 - p[k]) / (p[k] * t_exp + 1 - p[k]);
		return value;
	}

	public static int getRisingTime(float[] x0, int t0, int t1, boolean[] occupy, float[] thrVec) {
		float maxV = Float.NEGATIVE_INFINITY;
		int tMax = 0;
		for (int t = t0; t <= t1; t++) {
			if (x0[t] > maxV) {
				maxV = x0[t];
				tMax = t;
			}
		}
		
		int tMin = t0;
		for (int t = t0; t <= tMax; t++) {
			if (x0[t] < x0[tMin])
				tMin = t;
		}
		// extension
		int tPre = 0;
		for (int t = t0; t >= 0; t--) {
			if (occupy[t]) {
				tPre = t;
				break;
			}
		}
		t0 = Math.max(t0 - 10, tPre);
		
		tMin = t0;
		float minV = Float.POSITIVE_INFINITY;
		for(int t = t0; t <= tMax; t++) {
			if (x0[t] < minV) {
				minV = x0[t];
				tMin = t;
			}
		}
		
		if (tMin == tMax) {
			return tMax;
		}
		
		int[] riseTs = new int[thrVec.length];
		float sumT = 0;
		float thr;
		for (int i = 0; i < thrVec.length; i++) {
			thr = thrVec[i];
			for (int t = tMin; t <= tMax; t++) {
				if ((x0[t] - minV) / (maxV - minV) > thr) {
					riseTs[i] = t;
					break;
				}
			}
			sumT += riseTs[i];
		}
		return Math.round(sumT / thrVec.length);
	}  
	
	public static CFUDetectRes CFU_minMeasure(CFUPreResult cfu_pre, Opts opts, float thr, int minNumEvt) {
		// TODO Auto-generated method stub
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		ArrayList<float[]> linkage = cfu_pre.s_t0;
		float[] link;
		for (int i = 0; i < linkage.size(); i++) {
			link = linkage.get(i);
			link[2] = 1 - link[2];
		}
		
		Collections.sort(linkage, new Comparator<float[]>() {
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
		
		int nNode = cfu_pre.evtIhw.size();
		HashMap<Integer, ArrayList<Integer>> CFU_lst = hierarchicalClusteringMinMeasure(linkage, nNode, 1 - thr);
		HashMap<Integer, float[][]> CFU_region = new HashMap<>();
		HashMap<Integer, HashSet<Integer>> CFU_pixLst = new HashMap<>();
		
		if (CFU_lst.size() == 0) {
			return new CFUDetectRes(CFU_lst, CFU_region, CFU_pixLst);
		}
		
		CFU_lst = sizeFilter(CFU_lst, minNumEvt);
		ArrayList<Integer> l;
		float[][] weightMap;
		int label;
		int[] p;
		float maxWeight;
		HashSet<Integer> pixLst;
		for (int i = 1; i <= CFU_lst.size(); i++) {
			l = CFU_lst.get(i);
			weightMap = new float[H][W];
			maxWeight = Float.NEGATIVE_INFINITY;
			pixLst = new HashSet<>();
			
			for (int j = 0; j < l.size(); j++) {
				label = l.get(j);
				for (Map.Entry<Integer, Float> entry : cfu_pre.evtIhw.get(label).entrySet()) {
					pixLst.add(entry.getKey());
					p = Helper.ind2sub(H, W, entry.getKey());
					weightMap[p[0]][p[1]] += entry.getValue() * cfu_pre.maxCounts[label - 1];
					maxWeight = Math.max(maxWeight, weightMap[p[0]][p[1]]);
				}	
			}
			
			for (int x = 0; x < H; x++) {
				for (int y = 0; y < W; y++) {
					weightMap[x][y] /= maxWeight;
				}
			}
			
			CFU_region.put(i, weightMap);
			CFU_pixLst.put(i, pixLst);
		}
		
		return new CFUDetectRes(CFU_lst, CFU_region, CFU_pixLst);
	}
	
	public static float[] weightedAvg(float[][] ws, HashSet<Integer> pix, float[][][] dat) {
		// TODO Auto-generated method stub
		int H = dat.length;
		int W = dat[0].length;
		int T = dat[0][0].length;
		int[] p;
		float sumWeight = 0;
		for (int index : pix) {
			p = Helper.ind2sub(H, W, index);
			sumWeight += ws[p[0]][p[1]];
		}
		
		float[] curve = new float[T];
		for (int t = 0; t < T; t++) {
			for (int index : pix) {
				p = Helper.ind2sub(H, W, index);
				curve[t] += ws[p[0]][p[1]] * dat[p[0]][p[1]][t] / sumWeight;
			}
		}
		
		return curve;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> sizeFilter(HashMap<Integer, ArrayList<Integer>> lst, int minNumEvt) {
		HashMap<Integer, ArrayList<Integer>> newLst = new HashMap<Integer, ArrayList<Integer>>();
		int cnt = 1;
		for (Map.Entry<Integer, ArrayList<Integer>> entry : lst.entrySet()) {
			if (entry.getValue().size() >= minNumEvt) {
				newLst.put(cnt, entry.getValue());
				cnt += 1;
			}
		}		
		return newLst;
	}
	
	
	private static HashMap<Integer, ArrayList<Integer>> hierarchicalClusteringMinMeasure(ArrayList<float[]> linkageOrg,
			int nNode, float threshold) {
		
		int[] groupLabels = new int[nNode + 1];
		for (int i = 0; i <= nNode; i++)
			groupLabels[i] = i;
		
		ArrayList<float[]> linkage = new ArrayList<>();
		float[] link;
		for (int i = 0; i < linkageOrg.size(); i++) {
			link = linkageOrg.get(i);
			if (link[2] < threshold && link[0] != link[1])
				linkage.add(link);
		}
		
		// linking
		int id1;
		int id2;
		for (int k = 0; k < linkage.size(); k++) {
			link = linkage.get(k);
			id1 = (int) link[0];
			id2 = (int) link[1];
			id1 = findRootLabel(groupLabels, id1); 
			id2 = findRootLabel(groupLabels, id2);
			groupLabels[id1] = Math.min(id1, id2);
			groupLabels[id2] = Math.min(id1, id2);
		}
		
		// findSameGroup
		HashMap<Integer, ArrayList<Integer>> groupLst = new HashMap<>();
		ArrayList<Integer> l;
		int root;
		for (int k = 1; k <= nNode; k++) {
			root = findRootLabel(groupLabels, k);
			l = groupLst.getOrDefault(root, new ArrayList<Integer>());
			l.add(k);
			groupLst.put(root, l);
		}
		
		ArrayList<ArrayList<Integer>> groupLst2 = new ArrayList<ArrayList<Integer>>();
		for (Map.Entry<Integer, ArrayList<Integer>> entry : groupLst.entrySet()) {
			groupLst2.add(entry.getValue());
		}
		
		Collections.sort(groupLst2, new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> e1, ArrayList<Integer> e2) {
				if(e1.size() < e2.size())
					return 1;
				else if (e1.size() > e2.size())
					return -1;
				else
					return 0;
			}
		});
		
		
		groupLst = new HashMap<>();
		for (int k = 0; k < groupLst2.size(); k++) {
			groupLst.put(k + 1, groupLst2.get(k));
		}
		
		return groupLst;
	}
	
	public static int findRootLabel(int[] labels, int x) {
		if (labels[x] != x)
			labels[x] = findRootLabel(labels, labels[x]);
		
		return labels[x];
	}


	public static CFUPreResult CFU_tmp_function(HashMap<Integer, ArrayList<int[]>> evts, boolean spaOption, int H, int W, int T) {
		int[][][] evtMap = new int[H][W][T];
		for (int i = 1; i <= evts.size(); i++) {
			Helper.setValue(evtMap, evts.get(i), i);
		}
		
		// tracking setting
		int nNode = evts.size();
		HashMap<Integer, HashMap<Integer, Float>> evtIhw = new HashMap<>();
		
		int[] maxCounts = new int[nNode];
		ArrayList<int[]> pix;
		int index;
		float maxCount;
		HashMap<Integer, Float> ihw, ihw1, ihw2;
		for (int i = 1; i <= nNode; i++) {
			pix = evts.get(i);
			ihw = new HashMap<>();
			maxCount = 0;
			for (int[] p : pix) {
				index = Helper.sub2ind(H, W, p[0], p[1]);
				ihw.put(index, ihw.getOrDefault(index, 0f) + 1);
				maxCount = Math.max(maxCount, ihw.get(index));
			}
			if (spaOption) {
				for (int j : ihw.keySet()) {
					ihw.put(j, ihw.get(j) / maxCount);
				}
			}else {
				for (int j : ihw.keySet()) {
					ihw.put(j, 1f);
				}
			}
			maxCounts[i - 1] = (int) maxCount;
			evtIhw.put(i, ihw);
		}
		
		// linkage
		ArrayList<float[]> s_t0 = new ArrayList<>();
		HashMap<Integer, HashSet<Integer>> overlapLst = new HashMap<>();
		HashSet<Integer> possibleCandidates, Un;
		ArrayList<Integer> possibleCandidatesFilter;
		int label1, label2;
		int[] p;
		int nPC;
		float overlap;
		float sumMin;
		float sumMax;
		float w1, w2;
		for (int i = 1; i <= nNode; i++) {
			label1 = i;
			ihw1 = evtIhw.get(i);
			possibleCandidates = new HashSet<Integer>();
			for (Map.Entry<Integer, Float> entry : ihw1.entrySet()) {
				p = Helper.ind2sub(H, W, entry.getKey());
				for (int t = 0; t < T; t++) {
					possibleCandidates.add(evtMap[p[0]][p[1]][t]);
				}
			}
			possibleCandidates.remove(0);
			possibleCandidates.remove(i);
			overlapLst.put(i, possibleCandidates);
			
			possibleCandidatesFilter = new ArrayList<>();
			for (int j : possibleCandidates) {
				if (j > i)
					possibleCandidatesFilter.add(j);
			}
			nPC = possibleCandidatesFilter.size();
			
			for (int j = 0; j < nPC; j++) {
				label2 = possibleCandidatesFilter.get(j);
				ihw2 = evtIhw.get(label2);
				Un = new HashSet(ihw1.keySet());
				Un.addAll(ihw2.keySet());
				sumMin = 0;
				sumMax = 0;
				for (int k : Un) {
					w1 = ihw1.getOrDefault(k, 0f);
					w2 = ihw2.getOrDefault(k, 0f);
					sumMin += Math.min(w1, w2);
					sumMax += Math.max(w1, w2);
				}
				overlap = sumMin / sumMax;
				s_t0.add(new float[] {label1, label2, overlap});
			}
		}
		
        return new CFUPreResult(s_t0, evtIhw, maxCounts, overlapLst);
	}
	
	
	public static float[] getdFF(float[] x0, int window, int cut, float bias) {
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
		
		float sigma = Math.max(Helper.estimateNoiseByMean(x0), 0.0001f);
		float[] dff = new float[T];
		for (int t = 0; t < T; t++) {
			F0[t] -= sigma * bias;
			dff[t] = (x0[t] - F0[t]) / (F0[t] + 0.0001f);
		}
		return dff;
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


	public static void updateCFUTable(CFUDealer cfuDealer) {
		cfuDealer.right.tablemode1.setRowCount(0);
		ArrayList<Integer> favLst = new ArrayList(cfuDealer.favCFUList1);
		Collections.sort(favLst);
		
		for(int i : favLst) {
			ArrayList<Integer> lst = cfuDealer.cfuInfo1.get(i).lst;
			String str = lst2String(lst, 1, 0);
			cfuDealer.right.tablemode1.addRow(new Object[] {new Boolean(false), new Integer(1),new Integer(i),new Integer(lst.size()),str});

		}
		
		favLst = new ArrayList(cfuDealer.favCFUList2);
		Collections.sort(favLst);
		
		for(int i : favLst) {
			ArrayList<Integer> lst = cfuDealer.cfuInfo2.get(i).lst;
			String str = lst2String(lst, 2, 0);
			cfuDealer.right.tablemode1.addRow(new Object[] {new Boolean(false), new Integer(2),new Integer(i),new Integer(lst.size()),str});

		}
		
		cfuDealer.dealImage();
		
	}


	public static String lst2String(ArrayList<Integer> lst, int ch, int nCFUch1) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append(lst.get(0));
        for (int i = 1; i < lst.size(); i++) {
            sb.append(",").append(lst.get(i) + (ch - 1) * nCFUch1); // Appending each number followed by a space
        }
        
		return sb.toString();
	}
	
	public static String lst2String(int[] lst) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append(lst[0]);
        for (int i = 1; i < lst.length; i++) {
            sb.append(",").append(lst[i]); // Appending each number followed by a space
        }
        
		return sb.toString();
	}


	public static void showCurves(CFUDealer cfuDealer, ArrayList<Integer> indexLst) {
		// TODO Auto-generated method stub
		cfuDealer.center.resultsLabel.drawCurve(indexLst);
	}

	public static void pickShow(CFUDealer cfuDealer) {
		if (cfuDealer.pickList.size() == 2) {
			JFrame frame = new JFrame("Curve Graph");
			CFUCurveLabel curveGraph = new CFUCurveLabel(cfuDealer, true);
			
			curveGraph.drawCurve(cfuDealer.pickList);
			curveGraph.setBackground(Color.WHITE);
			curveGraph.setOpaque(true);
			curveGraph.setBorder(BorderFactory.createEtchedBorder());


            frame.add(curveGraph);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
			cfuDealer.pickList = new ArrayList<>();
		}
	}

	public static void getDelayColor(CFUDealer cfuDealer, int groupId) {
		int nCFU = cfuDealer.cfuInfo1.size();
		if (!cfuDealer.opts.singleChannel)
			nCFU += cfuDealer.cfuInfo2.size();
		Color[] delayColors = new Color[nCFU];
		
		int[] groupLabels = cfuDealer.selectedGroupEvts;
		float[] delays = cfuDealer.groupInfo.get(groupId).delays;
		float maxDelay = Float.NEGATIVE_INFINITY;
		float minDelay = Float.POSITIVE_INFINITY;
		for (float delay : delays) {
			maxDelay = Math.max(maxDelay, delay);
			minDelay = Math.min(minDelay, delay);
		}
		float midDelay = (minDelay + maxDelay) / 2;
		
		Color cStart = new Color(62,38,168); 
		Color cMid = new Color(16,190,180);
		Color cEnd = new Color(248,248,24);
		
		for (int i = 0; i < groupLabels.length; i++) {
			int r = 0;
			int g = 0;
			int b = 0;
			if (delays[i] < midDelay) {
				r = (int) (cStart.getRed() + (cMid.getRed() - cStart.getRed()) / (midDelay - minDelay) * (delays[i] - minDelay));
				g = (int) (cStart.getGreen() + (cMid.getGreen() - cStart.getGreen()) / (midDelay - minDelay) * (delays[i] - minDelay));
				b = (int) (cStart.getBlue() + (cMid.getBlue() - cStart.getBlue()) / (midDelay - minDelay) * (delays[i] - minDelay));
			}else {
				r = (int) (cMid.getRed() + (cEnd.getRed() - cMid.getRed()) / (maxDelay - midDelay) * (delays[i] - midDelay));
				g = (int) (cMid.getGreen() + (cEnd.getGreen() - cMid.getGreen()) / (maxDelay - midDelay) * (delays[i] - midDelay));
				b = (int) (cMid.getBlue() + (cEnd.getBlue() - cMid.getBlue()) / (maxDelay - midDelay) * (delays[i] - midDelay));
			}
			delayColors[groupLabels[i] - 1] = new Color(r, g, b);
		}
		
		cfuDealer.delayColors = delayColors;
		
	}
	
//	public static void main(String[] args) {
//		String propath = "D:\\Test\\";
////		AQuA2GUI aq = new AQuA2GUI();
//		HashMap<Integer, CFUInfo> cfuInfo = getCFUInfo(propath, 0.2f, 2);
//		
//		ArrayList<float[]> relation =  calAllDependency(propath, cfuInfo, 10);
//		HashMap<Integer, GroupInfo> groupInfo =  getGroupInfo(propath, 
//				relation, cfuInfo, 0.1f, 1);
//	}  
}