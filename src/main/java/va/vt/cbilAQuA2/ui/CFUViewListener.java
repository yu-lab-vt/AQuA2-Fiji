package va.vt.cbilAQuA2.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JLabel;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.cfu.CFUHelper;

public class CFUViewListener implements MouseListener {
	CFUDealer cfuDealer = null;
    private Point location;
    private boolean valid = false;
    int width = 0;
    int height = 0;
    CFUImageLabel imageLabel = null;
    
	public CFUViewListener(CFUDealer cfuDealer, CFUImageLabel imageLabel) {
		this.cfuDealer = cfuDealer;
		this.imageLabel = imageLabel;
	}
	
	public void setValid(boolean valid) {
    	this.valid = valid;
//    	System.out.println("Listener True");
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(valid) {
			location = imageLabel.getLocation();
			Point p = e.getPoint();
			double x = p.getX();
			double y = p.getY();
			y = y / cfuDealer.maxImageHeight * cfuDealer.height;
			x = x / cfuDealer.maxImageWidth * cfuDealer.width;
			
			int ch = imageLabel.ch;
			int[][] cfuMap;
			HashSet<Integer> favCFUList;
			ArrayList<Integer> pickList;
			pickList = cfuDealer.pickList;
			if (ch == 1) {
				cfuMap = cfuDealer.cfuMap1;
				favCFUList = cfuDealer.favCFUList1;
			} else {
				cfuMap = cfuDealer.cfuMap2;
				favCFUList = cfuDealer.favCFUList2;
			}
			
			int label = cfuMap[(int) y][(int) x];
			if (label > 0) {
				if (cfuDealer.viewPick) {
					if (!favCFUList.contains(label)) {
						favCFUList.add(label);
						ArrayList<Integer> indexLst = new ArrayList<>();
						indexLst.add(label);
						cfuDealer.center.resultsLabel.drawCurve(indexLst);
					}else
						favCFUList.remove(label);
					CFUHelper.updateCFUTable(cfuDealer);
					
				} else {
					pickList.add(label + (ch - 1) * cfuDealer.nCFUch1);
					CFUHelper.pickShow(cfuDealer);
					cfuDealer.dealImage();
				}
			}
		}
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
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
