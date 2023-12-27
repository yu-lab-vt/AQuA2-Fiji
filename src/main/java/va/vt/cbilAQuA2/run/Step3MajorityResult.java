package va.vt.cbilAQuA2.run;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Step3MajorityResult implements Serializable{
	int t0;
	int t1;
	float[] curve;
	HashMap<Integer, Integer> ihwDelays = null;
	HashSet<Integer> ihw = null;
	private static final long serialVersionUID = 1L;
	
	public Step3MajorityResult() {
		
	}
	
	public Step3MajorityResult(HashMap<Integer, Integer> ihwDelays, int t0, int t1, float[] curve) {
		this.ihwDelays = ihwDelays;
		this.t0 = t0;
		this.t1 = t1;
		this.curve = curve;
	}
	
}