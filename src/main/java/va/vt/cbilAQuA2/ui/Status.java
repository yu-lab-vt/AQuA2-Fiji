package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import va.vt.cbilAQuA2.Opts;

public class Status implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int curStatus = 0;
	public int jTPStatus = 0;
	public Color[] labelColors1 = null;
	public Color[] labelColors2 = null;
	public Opts opts = null;
	public boolean[][] regionMark = null;
	public int[][] regionMarkLabel = null;
	public boolean[][] landMark = null;
	public int[][] landMarkLabel = null;
	public String path = null;
	public String path2 = null;
	
	public Status() {
		
	}
	
	public Status(int curStatus, int jTPStatus, Color[] labelColors1, Color[] labelColors2, Opts opts, boolean[][] regionMark, int[][] regionMarkLabel, boolean[][] landMark, int[][] landMarkLabel, String path, String path2) {
		this.curStatus = curStatus;
		this.jTPStatus = jTPStatus;
		this.labelColors1 = labelColors1;
		this.labelColors2 = labelColors2;
		this.opts = opts;
		this.regionMark = regionMark;
		this.regionMarkLabel = regionMarkLabel;
		this.landMark = landMark;
		this.landMarkLabel = landMarkLabel;
		this.path = path;
		this.path2 = path2;
	}
	
}