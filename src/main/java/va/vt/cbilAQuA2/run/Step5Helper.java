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

public class Step5Helper {
	static long start;
	static long end;
	
	

	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
	public static void main(String[] args) {
		float[][][] dF1 = new float[1][1][1];
//		Opts opts = new Opts(1);
//		String proPath = "D:\\Test\\";
//		
//		HashMap<Integer, ArrayList<int[]>> seLst1 = new HashMap<Integer, ArrayList<int[]>>();
//		HashMap<Integer, ArrayList<int[]>> subEvtLst1 = new HashMap<Integer, ArrayList<int[]>>();
//		HashMap<Integer, Step3MajorityResult> majorInfo1 = new HashMap<Integer, Step3MajorityResult>();
//		int[] seLabel1 = new int[1];
//		try {
//			seLst1 = Helper.readObjectFromFile(proPath, "seLst1.ser", seLst1.getClass());
//			subEvtLst1 = Helper.readObjectFromFile(proPath, "subEvtLst1.ser", subEvtLst1.getClass());
//			seLabel1 = Helper.readObjectFromFile(proPath, "seLabel1.ser", seLabel1.getClass());
//			majorInfo1 = Helper.readObjectFromFile(proPath, "majorInfo1.ser", majorInfo1.getClass());
//			dF1 = Helper.readObjectFromFile(proPath, "dF1.ser", dF1.getClass());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		opts.gtwSmo = 0.2f;
//		Step4Res res = Step4Helper.se2evtTop(dF1, seLst1, subEvtLst1, seLabel1, majorInfo1, opts);
		
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
