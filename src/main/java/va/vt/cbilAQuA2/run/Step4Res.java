package va.vt.cbilAQuA2.run;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Step4Res implements Serializable{
	HashMap<Integer, RiseInfo> riseLst = null;
	HashMap<Integer, ArrayList<int[]>> evtLst = null;
	int[][][] datR;
	int[][][] datL;
	
	public Step4Res(HashMap<Integer, RiseInfo> riseLst, HashMap<Integer, ArrayList<int[]>> evtLst, int[][][] datR, int[][][] datL) {
		this.riseLst = riseLst;
		this.evtLst = evtLst;
		this.datR = datR;
		this.datL = datL;
	}

	
}