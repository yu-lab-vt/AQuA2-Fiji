package va.vt.cbilAQuA2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.JLabel;

import va.vt.cbilAQuA2.BasicFeatureDealer;
import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.ImageDealer;

public class CFUImageLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8045037061621519253L;
	CFUDealer cfuDealer = null;
	
	ArrayList<ArrayList<Point>> list1 = null;
	ArrayList<ArrayList<Point>> list2 = null;
	private Color color1 = new Color(0,191,255);
	private Color color1S = new Color(0,230,230);
	private Color color2 = new Color(255,185,15);
	
	private int width = 1;
	Graphics2D g = null;
	
	private Point startPoint = null;
	private Point endPoint = null;
	private int maxImageWidth = 0;
	private int maxImageHeight = 0;
	
	
	private Point startAnterior = null;
	private Point endAnterior = null;
	private boolean anteriorRelease = false;
	
	private boolean valid1 = false;
	private boolean valid2 = false;
	public CFUViewListener listener = null;
	public int ch;
	
	
	boolean drawBorder = true;
	
	public void setDrawBorder(boolean a) {
		drawBorder = a;
	}
	
	public CFUImageLabel(CFUDealer cfuDealer, int ch) {
		this.cfuDealer = cfuDealer;
		this.ch = ch;
	}
	
	public void setMaxSize(int width,int height) {
		maxImageWidth = width;
		maxImageHeight = height;
	}
	
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);
		if(gr!=null) {
			draw(gr);
		}
		
	}
	
	public void setListener(CFUViewListener listener) {
		this.listener = listener;
	}
	
	private void draw(Graphics gr) {
		g = (Graphics2D) gr;
		
		// AffineTransform
		final AffineTransform affineTransform = new AffineTransform();
		g.setTransform(affineTransform);
		startPoint = null;
		endPoint = null;
		if(startPoint==null || endPoint==null) {
			startPoint = new Point();
			startPoint.setLocation(0, 0);
			endPoint = new Point();
			endPoint.setLocation(cfuDealer.width,cfuDealer.height);
		}
		double tx = startPoint.getX();
		double ty = startPoint.getY();
		
		double sx = maxImageWidth/(endPoint.getX() - startPoint.getX());
		double sy = maxImageHeight/(endPoint.getY() - startPoint.getY());
		g.scale(sx, sy);
		g.translate(-tx, -ty);
		
//		System.out.println(tx + " " + ty);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(color1);
		g.setStroke(new BasicStroke(width));
		
		if(drawBorder) {
			if (ch == 1) {
				for(int label:cfuDealer.favCFUList1) {
					drawEventBorder(g, label, false);
				}
				
				for (int label:cfuDealer.pickList) {
					if (label <= cfuDealer.nCFUch1)
						drawEventBorder(g, label, true);
				}
			} else {
				for(int label:cfuDealer.favCFUList2) {
					drawEventBorder(g, label, false);
				}
				
				for (int label:cfuDealer.pickList) {
					if (label > cfuDealer.nCFUch1)
						drawEventBorder(g, label - cfuDealer.nCFUch1, true);
				}
			}
			
		}
	}
	
	private void drawEventBorder(Graphics2D g2, int label, boolean picked) {
		ArrayList<int[]> boundary;
		if (ch == 1)
			boundary = cfuDealer.CFUboundary1.get(label);
		else
			boundary = cfuDealer.CFUboundary2.get(label);
		
		if (!picked)
			g.setColor(new Color(237,217,25));
		else
			g.setColor(new Color(255,138,134));

		int x = boundary.get(0)[0];
		int y = boundary.get(0)[1];
		for(int i=1;i<boundary.size();i++) {			
			g.drawLine(boundary.get(i-1)[1], boundary.get(i-1)[0], boundary.get(i)[1], boundary.get(i)[0]);
			x += boundary.get(i)[0];
			y += boundary.get(i)[1];
		}
		
		x /= boundary.size();
		y /= boundary.size();
		g.drawString("" + label, y, x);
		
		
	}
}
