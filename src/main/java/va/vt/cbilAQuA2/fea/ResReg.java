package va.vt.cbilAQuA2.fea;

import java.io.Serializable;

public class ResReg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LandMark landMark = null;
	public LandMarkDist landmarkDist = null;
	public LandMarkDir landmarkDir = null;
	public Cell cell = null;
	public ResReg() {
		landMark = new LandMark();
		cell = new Cell();
	}
}