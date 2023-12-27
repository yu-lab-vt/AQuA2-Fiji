package va.vt.cbilAQuA2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.fea.FtsLst;

public class DrawCurveLabel2 extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int lineWidth = 1;
	Graphics2D g = null;
	Color[] colors = null;
	ArrayList<Integer> indexLst = null;
	float[][][] dffMat = null;
	FtsLst fts = null;
	ImageDealer imageDealer = null;
	public DrawCurveLabel2(ArrayList<Integer> indexLst, ImageDealer imageDealer) {
		this.indexLst = indexLst;
		this.imageDealer = imageDealer;
		colors = new Color[indexLst.size()];
		Random rv = new Random();
		for(int i=0;i<indexLst.size();i++) {
			colors[i] = new Color(rv.nextInt(120),rv.nextInt(120),rv.nextInt(120));
		}
	}

	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		
		int T = dffMat[0].length;
		
		if(indexLst!=null) {
			g = (Graphics2D) gr;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setStroke(new BasicStroke(lineWidth));
			
			int width = this.getWidth();
			int height = this.getHeight();
			int dh = height/indexLst.size(); 
			int ddh = 0;
			if(indexLst.size()==1) {
				dh = height;
				ddh = 0;
			}else {
				dh = 160;
				ddh = (height-dh)/(indexLst.size()-1);
			}
			
			FtsLst fts;
			float[][][] dffMat;
			int nEvtCh1 = imageDealer.nEvtCh1;
			for(int i=0;i<indexLst.size();i++) {
				int nEvt = indexLst.get(i);
				g.setColor(colors[i]);
				 
				if (nEvt <= nEvtCh1) {
					fts = imageDealer.fts1;
					dffMat = imageDealer.dffMat1;
				} else {
					nEvt -= nEvtCh1;
					fts = imageDealer.fts2;
					dffMat = imageDealer.dffMat2;
				}
				
				float min = Float.MAX_VALUE;
				float max = -Float.MAX_VALUE;
				for(int t = 0;t<T;t++) {
					min = Math.min(min, dffMat[nEvt-1][t][1]);
					max = Math.max(max, dffMat[nEvt-1][t][1]);
				}	
				
				float[] curve = new float[T];

				for(int t = 0;t<T;t++) {
					curve[t] = (dffMat[nEvt-1][t][1] - min)/(max-min);
				}
				
				int dh2 = i*ddh;
				
				
				for(int t=1;t<T;t++) {
					g.drawLine((t-1)*width/T, (int)((1-curve[t-1])*dh + dh2), t*width/T, (int)((1-curve[t])*dh)+dh2);
				}
				
				int tBegin = fts.curve.tBegin.get(nEvt);
				int tEnd = fts.curve.tEnd.get(nEvt);
				float dffMax = fts.curve.dffMax.get(nEvt);
				int x = (tBegin+tEnd)/2*width/T;
				if(x+120>width)
					x = width-100;
				
				g.drawString(nEvt + " dff: " + dffMax, x, dh2 + 40);
				
			}
		}
	}
	
}
