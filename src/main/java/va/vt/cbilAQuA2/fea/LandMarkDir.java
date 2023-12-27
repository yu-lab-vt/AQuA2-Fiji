package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.HashMap;

public class LandMarkDir implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, HashMap<Integer, float[][]>> minDistToLandMark = null;
	HashMap<Integer, HashMap<Integer, float[][]>> maxDistToLandMark = null;
	
	public LandMarkDir(HashMap<Integer, HashMap<Integer, float[][]>> minDistToLandMark, HashMap<Integer, HashMap<Integer, float[][]>> maxDistToLandMark) {
		this.minDistToLandMark = minDistToLandMark;
		this.maxDistToLandMark = maxDistToLandMark;
	}

	public LandMarkDir() {
		// TODO Auto-generated constructor stub
	}
	
}