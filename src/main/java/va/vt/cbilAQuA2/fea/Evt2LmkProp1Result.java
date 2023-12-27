package va.vt.cbilAQuA2.fea;

import java.io.Serializable;

public class Evt2LmkProp1Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public float[][][] chgTowardThrFrame = null;
	public float[][][] chgAwayThrFrame = null;
	public float[][] chgTowardThr = null;
	public float[][] chgAwayThr = null;
	public float[] chgToward = null;
	public float[] chgAway = null;
	public float[][][] pixTwd = null;
	public float[][][] pixAwy = null;
	
	public Evt2LmkProp1Result(float[][][] chgTowardThrFrame, float[][][] chgAwayThrFrame, float[][] chgTowardThr, 
			float[][] chgAwayThr, float[] chgToward, float[] chgAway, float[][][] pixTwd, float[][][] pixAwy) {
		this.chgTowardThrFrame = chgTowardThrFrame;
		this.chgAwayThrFrame = chgAwayThrFrame;
		this.chgTowardThr = chgTowardThr;
		this.chgAwayThr = chgAwayThr;
		this.chgToward = chgToward;
		this.chgAway = chgAway;
		this.pixTwd = pixTwd;
		this.pixAwy = pixAwy;
	}
	
}
