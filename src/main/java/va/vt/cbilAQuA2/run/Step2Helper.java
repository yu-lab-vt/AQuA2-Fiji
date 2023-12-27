package va.vt.cbilAQuA2.run;

import java.util.ArrayList;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.Opts;

import java.util.HashMap;

public class Step2Helper {
	
	// function [arLst] = acDetect(dF,opts,evtSpatialMask,ch,ff)
	public static HashMap<Integer, ArrayList<int[]>> acDetect(float[][][] dF, int[][][] activeMap, boolean[][] evtSpatialMask, int ch, Opts opts) {	
		System.out.println("acDetect");
		
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		float maxdF = 0; 
		float thrsNum, thr;
		double circularity;
		int nReg = 0, boundary = 0, curSz;
		float[] thrsVec = null;
		boolean[][] curMap2D;
		boolean[][][] selectMap;
		selectMap = new boolean[H][W][T];
		HashMap<Integer, ArrayList<int[]>> curRegions, arLst;
		arLst = new HashMap<Integer, ArrayList<int[]>>();
		int[] p;
		int minT, maxT;
		
		if (ch==1) {
			maxdF = opts.maxdF1;
		} else if (ch==2) {
			maxdF = opts.maxdF2;
		}
		
		if (opts.thrARScl > maxdF || (opts.maxSize >= H*W && opts.circularityThr == 0)) {
			thrsNum = opts.thrARScl;
			thrsVec = new float[1];
			thrsVec[0] = thrsNum;
		} else {
			thrsVec = new float[11];
			float step = (maxdF - opts.thrARScl) / 10;
			for (int i = 0; i < 11; i++) {
				thrsVec[i] = opts.thrARScl + i * step;
			}
		}
		
		
		// valid region
		for (int x = 0; x < thrsVec.length; x++) {
			thr = thrsVec[x];
			for (int i = 0; i < H; i++) {
			    for (int j = 0; j < W; j++) {
			    	if (evtSpatialMask[i][j]) {
				        for (int k = 0; k < T; k++) {
				            selectMap[i][j][k] = dF[i][j][k] > thr && activeMap[i][j][k] == 0;
				        }
			    	}
			    }
			}
			// curRegions = act.bw2Reg(selectMap,opts);
			curRegions = new HashMap<Integer, ArrayList<int[]>>();
			Helper.bfsConn3D(selectMap, curRegions); 
			boolean[] valid = new boolean[curRegions.size()];
//			System.out.printf("n curRegions: %d \n", curRegions.size());
			for (int i = 1; i <= curRegions.size(); i++) {
				ArrayList<int[]> pixLst = curRegions.get(i);
				if (pixLst.size() < opts.minSize) {
					continue;
				}
				
				curMap2D = new boolean[H][W];
				minT = Integer.MAX_VALUE;
				maxT = 0;
				curSz = 0;
				for (int pId = 0; pId < pixLst.size(); pId++) {
					p = pixLst.get(pId);
					if (!curMap2D[p[0]][p[1]]) {
						curSz ++;
					}
					curMap2D[p[0]][p[1]] = true;
					minT = Math.min(minT, p[2]);
					maxT = Math.max(maxT, p[2]);
				}
				
		        // size, duration limitation
		        if (curSz > opts.maxSize || curSz < opts.minSize || maxT - minT + 1 < opts.minDur)
		            continue;
		        
		        if (opts.circularityThr==0) {
		        	valid[i - 1] = true;
		        }
		        
		        // calculate the circularity
		        boolean[][] erodeMap = Helper.erodeImage(curMap2D);
		        boundary = 0;
		        for (boolean[] row : erodeMap) {
		            for (boolean value : row) {
		                if (value) {
		                    boundary++;
		                }
		            }
		        }
		        boundary = curSz - boundary;
		        circularity = (4 * Math.PI * curSz) / (boundary * boundary);
		        if(circularity>opts.circularityThr) {
		        	valid[i - 1] = true;
		        }
	        }
			
			for (int i = 1; i <= curRegions.size(); i++) {
				if (valid[i - 1]) {
					nReg ++;
					ArrayList<int[]> pixLst = curRegions.get(i);
					arLst.put(nReg, pixLst);
					for (int pId = 0; pId < pixLst.size(); pId++) {
						p = pixLst.get(pId);
						activeMap[p[0]][p[1]][p[2]] = nReg;
					}
				}	
			}
		}
			
		System.out.printf("nReg: %d \n", arLst.size());
		return arLst;
	}	
}