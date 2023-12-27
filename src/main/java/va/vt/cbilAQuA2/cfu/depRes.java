package va.vt.cbilAQuA2.cfu;

import java.util.HashMap;

public class depRes {
	public float p = 1;
	public int dist = 0;
	public HashMap<Integer, Integer> delays = null;
	
	public depRes(float p, int dist, HashMap<Integer, Integer> delays) {
		this.p = p;
		this.dist = dist;
		this.delays = delays;
	}
}
