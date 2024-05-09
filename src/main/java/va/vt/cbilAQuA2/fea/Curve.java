package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.HashMap;

public class Curve implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<Integer, Float> dffMaxZ = null;
	public HashMap<Integer, Float> dffMaxPval = null;
	public HashMap<Integer, int[]> rgt1 = null;
	public HashMap<Integer, Integer> tBegin = null;
	public HashMap<Integer, Integer> tEnd = null;
	public HashMap<Integer, Float> dfMax = null;
	public HashMap<Integer, Float> dffMax = null;
	public HashMap<Integer, Float> dffMax2 = null;
	public HashMap<Integer, Integer> dffMaxFrame = null;
	public HashMap<Integer, Float> rise19 = null;
	public HashMap<Integer, Float> fall91 = null;
	public HashMap<Integer, Float> width55 = null;
	public HashMap<Integer, Float> width11 = null;
	public HashMap<Integer, Float> decayTau = null;
	public HashMap<Integer, Float> duration = null;
	public HashMap<Integer, Float> riseTime = null;
	public HashMap<Integer, Integer> dff1Begin = null;
	public HashMap<Integer, Integer> dff1End = null;
	public HashMap<Integer, Float> datAUC = null;
	public HashMap<Integer, Float> dfAUC = null;
	public HashMap<Integer, Float> dffAUC = null;
	
	public Curve() {
		dffMaxZ = new HashMap<>();
		dffMaxPval = new HashMap<>();
		rgt1 = new HashMap<>();
		tBegin = new HashMap<>();
		tEnd = new HashMap<>();
		dfMax = new HashMap<>();
		dffMax = new HashMap<>();
		dffMax2 = new HashMap<>();
		dffMaxFrame = new HashMap<>();
		rise19 = new HashMap<>();
		fall91 = new HashMap<>();
		width55 = new HashMap<>();
		width11 = new HashMap<>();
		decayTau = new HashMap<>();
		duration = new HashMap<>();
		riseTime = new HashMap<>();
		dff1Begin = new HashMap<>();
		dff1End = new HashMap<>();
		datAUC = new HashMap<>();
		dfAUC = new HashMap<>();
		dffAUC = new HashMap<>();
	}
	
	public void addQuickFeature(int label, float dffMaxZ, float dffMaxPval, int[] rgT1, int its, int ite) {
		this.dffMaxZ.put(label, dffMaxZ);
		this.dffMaxPval.put(label, dffMaxPval);
		this.rgt1.put(label, rgT1);
		this.tBegin.put(label, its);
		this.tEnd.put(label, ite);
		duration.put(label, (ite-its+1)*1f);
	}
	
}