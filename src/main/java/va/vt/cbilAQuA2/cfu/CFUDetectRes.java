package va.vt.cbilAQuA2.cfu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CFUDetectRes implements Serializable{
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, ArrayList<Integer>> CFU_lst = null;
	public HashMap<Integer, float[][]> CFU_region = null;
	public HashMap<Integer, HashSet<Integer>> CFU_pixLst = null;
	public CFUDetectRes() {
		
	}
	public CFUDetectRes(HashMap<Integer, ArrayList<Integer>> CFU_lst, HashMap<Integer, float[][]> CFU_region, HashMap<Integer, HashSet<Integer>> CFU_pixLst) {
		// TODO Auto-generated constructor stub
		this.CFU_lst = CFU_lst;
		this.CFU_region = CFU_region;
		this.CFU_pixLst = CFU_pixLst;
	}
	
}