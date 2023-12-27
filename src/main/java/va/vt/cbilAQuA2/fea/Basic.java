package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.HashMap;

public class Basic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, boolean[][]> map = null;
	public HashMap<Integer, Float> area = null;
	public HashMap<Integer, Float> perimeter = null;
	public HashMap<Integer, Float> circMetric = null;
	public HashMap<Integer, int[]> center = null;
	public Basic() {
		map = new HashMap<>();
		area = new HashMap<>();
		perimeter = new HashMap<>();
		circMetric = new HashMap<>();
		center = new HashMap<>();
	}
	
}