package va.vt.cbilAQuA2.cfu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CFUInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Integer> lst = null;
	public float[][] region = null;
	public boolean[] occurrence = null;
	public float[] curve = null;
	public float[] dFFcurve = null;
	public boolean[] timeWindow = null;
	public boolean[] nonTimeWindow = null;
	public HashSet<Integer> pixLst = null;
	
	public CFUInfo() {
		
	}
	public CFUInfo(ArrayList<Integer> lst, float[][] region, boolean[] occurrence, float[] curve, float[] dFFcurve,
			boolean[] timeWindow, boolean[] nonTimeWindow, HashSet<Integer> pixLst) {
		// TODO Auto-generated constructor stub
		this.lst = lst;
		this.region = region;
		this.occurrence = occurrence;
		this.curve = curve;
		this.dFFcurve = dFFcurve;
		this.timeWindow = timeWindow;
		this.nonTimeWindow = nonTimeWindow;
		this.pixLst = pixLst;
	}
	
}