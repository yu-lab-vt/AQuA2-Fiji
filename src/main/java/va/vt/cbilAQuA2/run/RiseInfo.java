package va.vt.cbilAQuA2.run;

import java.io.Serializable;

public class RiseInfo implements Serializable{
	public float[][][] dlyMaps = null;
	public float maxDly = 0;
	public float minDly = 0;
	public int rgh0 = 0;
	public int rgh1 = 0;
	public int rgw0 = 0;
	public int rgw1 = 0;
	public RiseInfo() {
		
	}

	public RiseInfo(float[][][] dlyMaps, float maxDly, float minDly, int rgh0, int rgh1, int rgw0, int rgw1) {
		this.dlyMaps = dlyMaps;
		this.maxDly = maxDly;
		this.minDly = minDly;
		this.rgh0 = rgh0;
		this.rgh1 = rgh1;
		this.rgw0 = rgw0;
		this.rgw1 = rgw1;
	}
}
