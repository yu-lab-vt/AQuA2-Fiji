package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PolyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HashMap<Integer, boolean[][]> polyMask = null;
	public HashMap<Integer, ArrayList<int[]>> polyBorder = null;
	public float[][] polyCenter = null;
	public float[] polyAvgDist = null;
	public PolyInfo(HashMap<Integer, boolean[][]> polyMask, float[][] polyCenter,
			HashMap<Integer, ArrayList<int[]>> polyBorder, float[] polyAvgDist) {
		this.polyMask = polyMask;
		this.polyCenter = polyCenter;
		this.polyAvgDist = polyAvgDist;
		this.polyBorder = polyBorder;
	}
	
}