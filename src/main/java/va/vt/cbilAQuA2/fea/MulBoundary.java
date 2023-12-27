package va.vt.cbilAQuA2.fea;

import java.util.ArrayList;
import java.util.HashMap;

public class MulBoundary{
	public HashMap<Integer, ArrayList<int[]>> cc = null;
	public HashMap<Integer, ArrayList<int[]>> boundaries = null;
	public MulBoundary(HashMap<Integer, ArrayList<int[]>> cc, HashMap<Integer, ArrayList<int[]>> boundaries) {
		this.cc = cc;
		this.boundaries = boundaries;
	}
} 