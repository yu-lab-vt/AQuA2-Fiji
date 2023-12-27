package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Cell implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, boolean[][]> mask = null;
	public float[][] center = null;
	public HashMap<Integer, ArrayList<int[]>> border = null;
	public float[] centerBorderAvgDist = null;
	public boolean[][] incluLmk = null;
	public boolean[][] memberIdx = null;
	public float[][] dist2border = null;
	public float[][] dist2borderNorm = null;

}