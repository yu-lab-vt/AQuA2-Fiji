package va.vt.cbilAQuA2.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class ROICurveLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	float[] curve = null;
	float[] curve2 = null;
	Graphics2D g;
	int lineWidth = 2;
	public ROICurveLabel(float[] curve, float[] curve2) {
		// TODO Auto-generated constructor stub
		this.curve = curve;
		this.curve2 = curve2;
	}
	
	@Override
	public void paint(Graphics gr) {
		super.paint(gr);

		g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(lineWidth));
		if (curve != null) {
			int T = curve.length;
			int width = this.getWidth();
			int height = this.getHeight();
			g.setColor(Color.BLACK);
			for(int t=1;t<T;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve[t-1])*height), t*width/T, (int)((1-curve[t])*height));
			}
			g.drawString("CH1", width - 100, height - 100);
		}
		
		
		if (curve2 != null) {
			int T = curve.length;
			int width = this.getWidth();
			int height = this.getHeight();
			g.setColor(Color.BLUE);
			for(int t=1;t<T;t++) {
				g.drawLine((t-1)*width/T, (int)((1-curve2[t-1])*height), t*width/T, (int)((1-curve2[t])*height));
			}
			g.drawString("CH2", width - 100, height - 140);
		}
//		
		
	}
	
}
