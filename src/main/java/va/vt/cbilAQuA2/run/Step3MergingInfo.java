package va.vt.cbilAQuA2.run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Step3MergingInfo{
	HashMap<Integer, HashSet<Integer>> neibLst = null;
	HashMap<Integer, HashMap<Integer, Integer>> delayDif = null;
	int[] evtCCLabel = null;
	HashMap<Integer, ArrayList<Integer>> labelsInActRegs = null;
	
	public Step3MergingInfo(HashMap<Integer, HashSet<Integer>> neibLst, HashMap<Integer, HashMap<Integer, Integer>> delayDif,
			int[] evtCCLabel, HashMap<Integer, ArrayList<Integer>> labelsInActRegs) {
		this.neibLst = neibLst;
		this.delayDif = delayDif;
		this.evtCCLabel = evtCCLabel;
		this.labelsInActRegs = labelsInActRegs;
	}	
}