package va.vt.cbilAQuA2.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;

public class checkROIListener implements MouseListener, MouseMotionListener {
    ImageDealer imageDealer = null;
    private Point location;
    private boolean valid = false;
    int width = 0;
    int height = 0;
    JLabel imageLabel = null;
    
	public checkROIListener(ImageDealer imageDealer, JLabel imageLabel) {
		this.imageDealer = imageDealer;
		this.imageLabel = imageLabel;
	}
	
	public void setValid(boolean valid) {
    	this.valid = valid;
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	

	public Point transformPoint2(Point p, double dx, double dy) {
		double resultX = imageDealer.getStartPoint().getX() + p.getX()*dx/width;
		double resultY = imageDealer.getStartPoint().getY() + p.getY()*dy/height;
		Point result = new Point();
		result.setLocation(resultX, resultY);
		return result;
	}
	
	public Point transformPoint(Point p) {
		double resultX = p.getX() - location.getX();
		double resultY = p.getY() - location.getY();
		Point result = new Point();
		result.setLocation(resultX, resultY);
		return result;
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(valid) {
			location = imageLabel.getLocation();
			width = imageLabel.getWidth();
			height = imageLabel.getHeight();
			Point p = e.getPoint();
			p = transformPoint(p);
			Point start = imageDealer.getStartPoint();
			Point end = imageDealer.getEndPoint();
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
			p  = transformPoint2(p,dx,dy);
			imageDealer.ROIstart = p;
			imageDealer.drawROI = true;
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(valid) {
			location = imageLabel.getLocation();
			width = imageLabel.getWidth();
			height = imageLabel.getHeight();
			Point p = e.getPoint();
			p = transformPoint(p);
			Point start = imageDealer.getStartPoint();
			Point end = imageDealer.getEndPoint();
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
			p  = transformPoint2(p,dx,dy);
			imageDealer.ROIend = p;
			imageDealer.drawROI = false;
			
			int x0 = Math.min(imageDealer.ROIstart.y, imageDealer.ROIend.y);
            int y0 = Math.min(imageDealer.ROIstart.x, imageDealer.ROIend.x);
            int x1 = Math.max(imageDealer.ROIstart.y, imageDealer.ROIend.y);
            int y1 = Math.max(imageDealer.ROIstart.x, imageDealer.ROIend.x);
			
			int T = imageDealer.opts.T;
			float[] curve = new float[T];
			for (int t = 0; t < T; t++) {
				for (int x = x0; x <= x1; x++) {
					for (int y = y0; y <= y1; y++) {
						curve[t] += imageDealer.dat1[x][y][t];
					}
				}
			}
			Helper.normalizeCurve(curve);
			float[] curve2 = null;
			if (!imageDealer.opts.singleChannel) {
				curve2 = new float[T];
				for (int t = 0; t < T; t++) {
					for (int x = x0; x <= x1; x++) {
						for (int y = y0; y <= y1; y++) {
							curve2[t] += imageDealer.dat2[x][y][t];
						}
					}
				}
				Helper.normalizeCurve(curve2);
			}
			
			JFrame frame = new JFrame();
			ROICurveLabel curveLabel = new ROICurveLabel(curve, curve2);
			curveLabel.setSize(850,600);
			
			frame.setSize(850,600);
			frame.add(curveLabel);
	        frame.setVisible(true);
	        
	        imageLabel.repaint();
			
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(valid) {
			location = imageLabel.getLocation();
			width = imageLabel.getWidth();
			height = imageLabel.getHeight();
			Point p = e.getPoint();
			p = transformPoint(p);
			Point start = imageDealer.getStartPoint();
			Point end = imageDealer.getEndPoint();
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
			p  = transformPoint2(p,dx,dy);
			imageDealer.ROIend = p;
			imageLabel.repaint();
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
