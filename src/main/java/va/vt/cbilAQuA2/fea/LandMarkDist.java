package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.HashMap;

public class LandMarkDist implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<Integer, float[][]> distPerFrame = null;
	public float[][] distAvg = null;
	public float[][] distMin = null;
	
	public LandMarkDist(HashMap<Integer, float[][]> distPerFrame, float[][] distAvg, float[][] distMin){
		this.distPerFrame = distPerFrame;
		this.distAvg = distAvg;
		this.distMin = distMin;
	}

	public LandMarkDist() {
		// TODO Auto-generated constructor stub
	}
}