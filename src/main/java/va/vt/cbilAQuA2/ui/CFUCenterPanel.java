package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.ImageDealer;

public class CFUCenterPanel {
	JPanel panel = new JPanel();
	CFUDealer cfuDealer = null;
	public CFUImageLabel imageLabel = null;
	public CFUImageLabel imageLabelLeft = null;
	public CFUImageLabel imageLabelRight = null;
	public CFUCurveLabel resultsLabel = null; 
	JPanel imagePanel = new JPanel();
	JPanel blankRow = new JPanel();
	JPanel curvePanel = new JPanel();
	public CFUCenterPanel(CFUDealer cfuDealer) {
		this.cfuDealer = cfuDealer;
		if (cfuDealer.opts.singleChannel)
			imageLabel = new CFUImageLabel(cfuDealer, 1);
		else {
			imageLabelLeft = new CFUImageLabel(cfuDealer, 1);
			imageLabelRight = new CFUImageLabel(cfuDealer, 2);
		}
		cfuDealer.center = this;
	}
	
	public JPanel createPanel() {
		resultsLabel = new CFUCurveLabel(cfuDealer, false);
		setting();
		layout();
		return panel;
	}
	
	public void setting() {
		
		// imageLabel
//		float scal = 1;
		float scal = (float)cfuDealer.opts.W/cfuDealer.opts.H;
		
		int width = 0;
		int height = 0;
		
		if (cfuDealer.opts.singleChannel) {
			if(610 * scal >800) {
				width = 800;
				height = (int) (800/scal);
			}else {
				width = (int) (610*scal);
				height = 610;
			}
			imageLabel.setPreferredSize(new Dimension(width,height));
			cfuDealer.maxImageWidth = width;
			cfuDealer.maxImageHeight = height;
			imageLabel.setMaxSize(width, height);
		} else {
			if(610 * scal > 400) {
				width = 400;
				height = (int) (400/scal);
			}else {
				width = (int) (610*scal);
				height = 610;
			}
			cfuDealer.maxImageWidth = width;
			cfuDealer.maxImageHeight = height;
			imageLabelLeft.setPreferredSize(new Dimension(width,height));
			imageLabelLeft.setMaxSize(width, height);
			imageLabelRight.setPreferredSize(new Dimension(width,height));
			imageLabelRight.setMaxSize(width, height);
		}
		
		imagePanel.setPreferredSize(new Dimension(820,630));
		
		blankRow.setPreferredSize(new Dimension(720,10));
		
		resultsLabel.setPreferredSize(new Dimension(810,200));
		resultsLabel.setOpaque(true);
		resultsLabel.setBackground(Color.WHITE);
		
		curvePanel.setPreferredSize(new Dimension(820,210));
		
		panel.setPreferredSize(new Dimension(830,850));
	}
	
	public void layout() {
		
//		GridBagPut settingImagePanel = new GridBagPut(imagePanel);
//		settingImagePanel.putGridBag(imageLabel, imagePanel, 0, 0);
		if (cfuDealer.opts.singleChannel) {
			imagePanel.add(imageLabel);
		} else {
			imagePanel.add(imageLabelLeft);
			imagePanel.add(imageLabelRight);
		}
		curvePanel.add(resultsLabel);
		
		
		imagePanel.setBorder(BorderFactory.createEtchedBorder());
		curvePanel.setBorder(BorderFactory.createEtchedBorder());

		// centerGroup
		GridBagPut settingCenterGroup = new GridBagPut(panel);
		settingCenterGroup.fillBoth();
		settingCenterGroup.putGridBag(imagePanel, panel, 0, 0);
		settingCenterGroup.putGridBag(blankRow, panel, 0, 1);
		settingCenterGroup.putGridBag(curvePanel, panel, 0, 2);
//		panel.setBorder(BorderFactory.createEtchedBorder());		
		
	}
}
