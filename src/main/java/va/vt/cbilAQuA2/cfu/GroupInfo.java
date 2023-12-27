package va.vt.cbilAQuA2.cfu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GroupInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
//	ArrayList<Integer> lst = null;
//	float[][] region = null;
//	boolean[] occurrence = null;
	public int[] labels = null;
	public float[] delays = null;
	float[] addedPvalue = null;
//	boolean[] timeWindow = null;
//	boolean[] nonTimeWindow = null;
//	HashSet<Integer> pixLst = null;
	
	public GroupInfo() {
		
	}
//	public GroupInfo(ArrayList<Integer> lst, float[][] region, boolean[] occurrence, float[] curve, float[] dFcurve,
//			boolean[] timeWindow, boolean[] nonTimeWindow, HashSet<Integer> pixLst) {
//		// TODO Auto-generated constructor stub
//		this.lst = lst;
//		this.region = region;
//		this.occurrence = occurrence;
//		this.curve = curve;
//		this.dFcurve = dFcurve;
//		this.timeWindow = timeWindow;
//		this.nonTimeWindow = nonTimeWindow;
//		this.pixLst = pixLst;
//	}

	public GroupInfo(ArrayList<Integer> labelsOrg, float[] delaysOrg, float[] addedPvalueOrg) {
		this.labels = new int[labelsOrg.size()];
		this.delays = new float[labelsOrg.size()];
		this.addedPvalue = new float[labelsOrg.size()];
		int curLabel;
		for (int i = 0; i < labelsOrg.size(); i++) {
			curLabel = labelsOrg.get(i);
			this.labels[i] = curLabel;
			this.delays[i] = delaysOrg[curLabel];
			this.addedPvalue[i] = addedPvalueOrg[curLabel];
		}
		
	}
	
}