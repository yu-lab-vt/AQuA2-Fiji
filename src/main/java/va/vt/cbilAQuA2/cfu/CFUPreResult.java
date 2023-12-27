package va.vt.cbilAQuA2.cfu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CFUPreResult implements Serializable{
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, HashMap<Integer, Float>> evtIhw = null;
	public ArrayList<float[]> s_t0 = null;
	public int[] maxCounts = null;
	public HashMap<Integer, HashSet<Integer>> overlapLst = null;
	public CFUPreResult() {
		
	}
	public CFUPreResult(ArrayList<float[]> s_t0, HashMap<Integer, HashMap<Integer, Float>> evtIhw, int[] maxCounts,
			HashMap<Integer, HashSet<Integer>> overlapLst) {
		// TODO Auto-generated constructor stub
		this.s_t0 = s_t0;
		this.evtIhw = evtIhw;
		this.maxCounts = maxCounts;
		this.overlapLst = overlapLst;
	}
	
}