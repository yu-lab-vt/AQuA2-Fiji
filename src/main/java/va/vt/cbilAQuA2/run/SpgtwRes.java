package va.vt.cbilAQuA2.run;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpgtwRes implements Serializable{
	float[][][] dlyMaps = null;
	float[][] cx = null;
	int tempRatio = 0;
	int minTs = 0;
	HashMap<Integer, ArrayList<int[]>> spLst = null;
	public SpgtwRes(float[][][] dlyMaps, float[][] cx, int tempRatio, int minTs, HashMap<Integer, ArrayList<int[]>> spLst) {
		this.dlyMaps = dlyMaps;
		this.cx = cx;
		this.tempRatio = tempRatio;
		this.minTs = minTs;
		this.spLst = spLst;
	}

	
}