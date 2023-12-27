package va.vt.cbilAQuA2.run;

import java.util.ArrayList;
import java.util.HashMap;

public class Step3HelperResult {

	HashMap<Integer, ArrayList<int[]>> sdLst = null;
	HashMap<Integer, ArrayList<int[]>> seLst = null;
	HashMap<Integer, ArrayList<int[]>> evtLst = null;
	HashMap<Integer, Step3MajorityResult> majorInfo = null;
	
	int[] seLabel = null;
	int[][][] Map = null;
	
	
	public Step3HelperResult(HashMap<Integer, ArrayList<int[]>> sdLst, 
			HashMap<Integer, ArrayList<int[]>> evtLst, HashMap<Integer, ArrayList<int[]>> seLst, 
			HashMap<Integer, Step3MajorityResult> majorInfo, int[] seLabel, int[][][] Map) {
		this.sdLst = sdLst;
		this.seLst = seLst;
		this.evtLst = evtLst;
		this.majorInfo = majorInfo;
		this.Map = Map;
		this.seLabel = seLabel;
	}	
	
}
