package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LandMark implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, boolean[][]> mask = null;
	public HashMap<Integer, ArrayList<int[]>> border = null;
	public float[][] center = null;
	public float[] centerBorderAvgDist = null;
	
}