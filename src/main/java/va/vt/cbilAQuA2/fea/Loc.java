package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Loc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, Integer> t0 = null;
	public HashMap<Integer, Integer> t1 = null;
	public HashMap<Integer, ArrayList<int[]>> xSpaTemp = null;
	public HashMap<Integer, HashSet<Integer>> xSpa = null;
	
	public Loc(HashMap<Integer, Integer> t0, HashMap<Integer, Integer> t1, HashMap<Integer, ArrayList<int[]>> x3D, HashMap<Integer, HashSet<Integer>> x2D ) {
		this.t0 = t0;
		this.t1 = t1;
		this.xSpaTemp = x3D;
		this.xSpa = x2D;
	}

	public Loc() {
		t0 = new HashMap<>();
		t1 = new HashMap<>();
		xSpaTemp = new HashMap<>();
		xSpa = new HashMap<>();
	}
	
	
}