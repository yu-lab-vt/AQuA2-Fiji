package va.vt.cbilAQuA2.cfu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import va.vt.cbilAQuA2.CFUDealer;

public class CFUOpts implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public float alpha = 0;
	public float alpha2 = 0;
	public int minNumEvt = 0;
	public int minNumEvt2 = 0;
	public int winSize = 0;
	public int shift = 0;
	public float pThr = 0;
	public int minCFU = 0;
	
	public CFUOpts() {
		
	}
	public CFUOpts(CFUDealer cfuDealer) {
		// TODO Auto-generated constructor stub

		this.alpha = Float.parseFloat(cfuDealer.left.jTFoverThr.getText());
		this.minNumEvt = Integer.parseInt(cfuDealer.left.jTFminEvt.getText());
		this.alpha2 = Float.parseFloat(cfuDealer.left.jTFoverThr2.getText());
		this.minNumEvt2 = Integer.parseInt(cfuDealer.left.jTFminEvt2.getText());
		
		this.winSize = Integer.parseInt(cfuDealer.left.jTFwinSize.getText());
		this.shift = Integer.parseInt(cfuDealer.left.jTFshift.getText());
		
		this.minCFU = Integer.parseInt(cfuDealer.left.jTFminCFU.getText());
		this.pThr = Float.parseFloat(cfuDealer.left.jTFpThr.getText());		

	}
	
}