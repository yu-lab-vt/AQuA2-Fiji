package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NetWork implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int[][] nOccurSameLoc = null;
	public int[] nOccurSameTime = null;
	HashMap<Integer,ArrayList<Integer>> occurSameLocList = null;
	HashMap<Integer,ArrayList<Integer>> occurSameLocList2 = null;
	HashMap<Integer,HashSet<Integer>> occurSameTimeList = null;
	
	public NetWork(int[][] nOccurSameLoc, int[] nOccurSameTime, HashMap<Integer,ArrayList<Integer>> occurSameLocList,
			HashMap<Integer,ArrayList<Integer>> occurSameLocList2, HashMap<Integer,HashSet<Integer>> occurSameTimeList) {
		this.nOccurSameLoc = nOccurSameLoc;
		this.nOccurSameTime = nOccurSameTime;
		this.occurSameLocList = occurSameLocList;
		this.occurSameLocList2 = occurSameLocList2;
		this.occurSameTimeList = occurSameTimeList;
	}
}