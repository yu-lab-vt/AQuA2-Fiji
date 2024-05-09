package va.vt.cbilAQuA2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.cfu.CFUHelper;
import va.vt.cbilAQuA2.cfu.depRes;
import va.vt.cbilAQuA2.fea.FtsLst;

public class CFUCurveLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int lineWidth = 3;
	Graphics2D g = null;
	float[][] curves = null;
	CFUDealer cfuDealer = null;
	ArrayList<Integer> cfuIds = new ArrayList<Integer>();
	boolean calculateP = false;
	boolean drawStatus = false;
	public CFUCurveLabel(CFUDealer cfuDealer, boolean calculateP) {
		// TODO Auto-generated constructor stub
		this.cfuDealer = cfuDealer;
		this.calculateP = calculateP;
	}

	public void drawCurve(ArrayList<Integer> labels) {
		cfuIds = labels;
		repaint();
	}
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);

		g = (Graphics2D) gr;
		g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(lineWidth));
		int T = 0;
		int width = this.getWidth();
		int height = this.getHeight();
		float offSet = 0.3f;
		float totalMax = 1 + (cfuIds.size() - 1) * offSet;
		float curMulativeOffset = 0;
		for (int id : cfuIds) {
			float[] curve;
			if (id <= cfuDealer.nCFUch1) {
				curve = cfuDealer.cfuInfo1.get(id).curve;
				g.setColor(cfuDealer.CFUColors1[id - 1]);
			} else {
				curve = cfuDealer.cfuInfo2.get(id - cfuDealer.nCFUch1).curve;
				g.setColor(cfuDealer.CFUColors2[id - cfuDealer.nCFUch1 - 1]);
			}
				
			Helper.normalizeCurve(curve);
			T = curve.length;
			
			for(int t=1;t<T;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve[t-1] + curMulativeOffset)*height / totalMax), t*width/T, (int)((1-curve[t] + curMulativeOffset)*height / totalMax));
			}
			
			curMulativeOffset += offSet;
			
		}
		
		if (calculateP) {
			int label1 = cfuIds.get(0);
			int label2 = cfuIds.get(1);
			int maxDist = Integer.parseInt(cfuDealer.left.jTFwinSize.getText());
			int shift = Integer.parseInt(cfuDealer.left.jTFshift.getText());
			boolean[] seq1, seq2;
			if (label1 <= cfuDealer.nCFUch1)
				seq1 = cfuDealer.cfuInfo1.get(label1).occurrence;
			else
				seq1 = cfuDealer.cfuInfo2.get(label1 - cfuDealer.nCFUch1).occurrence;
			if (label2 <= cfuDealer.nCFUch1)
				seq2 = cfuDealer.cfuInfo1.get(label2).occurrence;
			else
				seq2 = cfuDealer.cfuInfo2.get(label2 - cfuDealer.nCFUch1).occurrence;
			
			depRes depRes1 = CFUHelper.calDependency(seq1, seq2, shift, maxDist);
			depRes depRes2 = CFUHelper.calDependency(seq2, seq1, shift, maxDist);
			float pValue = Math.min(depRes1.p,depRes2.p);
			g.setColor(Color.BLACK);
			g.drawString(" p-value dependency: " + pValue, width-200, 50);
		}
		
	}
	
}
