package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.HashMap;

public class Propagation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer,float[][]> propGrow = null;
	public HashMap<Integer,float[]> propGrowOverall = null;
	public HashMap<Integer,float[][]> propShrink = null;
	public HashMap<Integer,float[]> propShrinkOverall = null;
	public HashMap<Integer,float[][]> areaChange = null;
	public HashMap<Integer,float[]> areaChangeRate = null;
	public HashMap<Integer,float[][]> areaFrame = null;
	public HashMap<Integer,float[][]> propMaxSpeed = null;
	
	public Propagation() {
		propGrow = new HashMap<>();
		propGrowOverall = new HashMap<>();
		propShrink = new HashMap<>();
		propShrinkOverall = new HashMap<>();
		areaChange = new HashMap<>();
		areaChangeRate = new HashMap<>();
		areaFrame = new HashMap<>();
		propMaxSpeed = new HashMap<>();
	}
}