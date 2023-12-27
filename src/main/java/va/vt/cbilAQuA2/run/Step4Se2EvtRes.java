package va.vt.cbilAQuA2.run;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Step4Se2EvtRes implements Serializable{
	float[][][] evtRecon = null;
	int[][][] evtL = null;
	float[][][] dlyMaps = null;
	int nEvt0 = 0;
	int[] svLabel = null;
	public Step4Se2EvtRes() {
		
	}

	public Step4Se2EvtRes(float[][][] evtRecon, int[][][] evtL, float[][][] dlyMaps, int nEvt0, int[] svLabel) {
		this.evtRecon = evtRecon;
		this.evtL = evtL;
		this.dlyMaps = dlyMaps;
		this.nEvt0 = nEvt0;
		this.svLabel = svLabel;
	}

	
}