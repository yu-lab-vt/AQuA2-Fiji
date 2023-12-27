package va.vt.cbilAQuA2.fea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FtsLst implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Curve curve = null;
	public Loc loc = null;
	public Basic basic = null;
	public Notes notes = null;
	public Propagation propagation = null;
	public ResReg region = null;
	public NetWork networkAll = null;
	public NetWork network = null;
	public HashMap<Integer, ArrayList<int[]>> border = null;
	public FtsLst() {
		curve = new Curve();
		loc = new Loc();
		basic = new Basic();
		propagation = new Propagation();
		notes = new Notes();
		region = new ResReg();
		border = new HashMap<>();
	}
	
	public void addQuickFeature(int label, float dffMaxZ, float dffMaxPval, int[] rgT1, int its, int ite) {
		curve.addQuickFeature(label, dffMaxZ, dffMaxPval, rgT1, its, ite);
	}
}
