package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.cfu.CFUHelper;
import va.vt.cbilAQuA2.cfu.CFUInfo;
import va.vt.cbilAQuA2.run.CFUCalDep;
import va.vt.cbilAQuA2.run.CFUDetect;
import va.vt.cbilAQuA2.run.CFUGroupRun;
import va.vt.cbilAQuA2.run.Step1;

public class CFULeftPanel {
	JPanel panel = new JPanel();
	CFUDealer cfuDealer = null;
	GridBagLayout gbl;
	GridBagConstraints gbc;
	
	JPanel left1 = new JPanel();
	public JPanel left2 = new JPanel();
	public JPanel left3 = new JPanel();	
	public JPanel left4 = new JPanel();	
	JLabel rowBlank1 = new JLabel();
	JLabel rowBlank2 = new JLabel();
	JLabel rowBlank3 = new JLabel();
	JLabel rowBlank4 = new JLabel();
	
	// left 1
	JLabel lDetect = new JLabel(" CFU detections");
	public JTextField jTFoverThr = new JTextField("0.3");
	public JTextField jTFminEvt = new JTextField("2");
	JLabel jTPLoverThr = new JLabel(" Overlap threshold");
	JLabel jTPLminEvt = new JLabel(" Minimum number of events of CFU");
	
	public JTextField jTFoverThr2 = new JTextField("0.3");
	public JTextField jTFminEvt2 = new JTextField("2");
	JLabel jTPLoverThr2 = new JLabel(" Overlap threshold (CH2)");
	JLabel jTPLminEvt2 = new JLabel(" Minimum number of events of CFU (CH2)");
	
	public JCheckBox jTFspaOption = new JCheckBox("",true);
	JLabel jTPLspaOption = new JLabel(" Use spatial weighted map (yes) | spatial footprint (no)");
	JButton detectRun = new JButton("Run");
	
	// left 2
	JLabel lOper = new JLabel(" Operations");
	public JToggleButton viewFavourite = new JToggleButton("view/favourite");
	public JButton addAll = new JButton("add all");
	JLabel jTPLviewFavourite = new JLabel(" Add CFU to favortie table");
	JLabel jTPLaddAll = new JLabel(" Add all CFUs to favortie table");
	JLabel jTPLwindowSize = new JLabel(" --- Window size for calculating depedency ---");
	public JTextField jTFwinSize = new JTextField("0");
	public JSlider winSizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	public JToggleButton pick = new JToggleButton("Pick CFUs");
	public JButton alldep = new JButton("All dependencies");
	JLabel jTPLpick = new JLabel(" Pick CFUs (onlye keep 2)");
	JLabel jTPLalldep = new JLabel(" All dependencies");
	
	// left 3
	JLabel lGroup = new JLabel(" Group");
	public JTextField jTFpThr = new JTextField("0.01");
	public JTextField jTFminCFU = new JTextField("2");
	JLabel jTPLpThr = new JLabel(" p value significance of dependency threshold");
	JLabel jTPLminCFU = new JLabel(" Minimum number of CFUs in each group");
	public JButton groupRun = new JButton("Run");
	
	// left 4
	JLabel lOthers = new JLabel(" Others");
	public JButton returnButton = new JButton("Return");
	public JButton outputButton = new JButton("Output");
	
	boolean sliderChanging = false;
	boolean txtChanging = false;
	
	public CFULeftPanel(CFUDealer cfuDealer) {
		this.cfuDealer = cfuDealer;
		cfuDealer.left = this;
	}
	
	public JPanel createPanel() {
		setting();
		addButtonListeners();
		layout();
		return panel;
	}
	
	public void setting() {
		
		// left 1
		lDetect.setOpaque(true);
		lDetect.setBackground(Beauty.blue);
		lDetect.setForeground(Color.WHITE);
		lDetect.setPreferredSize(new Dimension(400,20));
		
		jTFoverThr.setPreferredSize(new Dimension(80,20));
		jTFoverThr.setHorizontalAlignment(JTextField.CENTER);
//		jTPLoverThr.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLoverThr.setPreferredSize(new Dimension(270,20));
		
		jTFminEvt.setPreferredSize(new Dimension(80,20));
		jTFminEvt.setHorizontalAlignment(JTextField.CENTER);
//		jTPLminEvt.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLminEvt.setPreferredSize(new Dimension(270,20));
		
		jTFoverThr2.setPreferredSize(new Dimension(80,20));
		jTFoverThr2.setHorizontalAlignment(JTextField.CENTER);
//		jTPLoverThr2.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLoverThr2.setPreferredSize(new Dimension(270,20));
		
		jTFminEvt2.setPreferredSize(new Dimension(80,20));
		jTFminEvt2.setHorizontalAlignment(JTextField.CENTER);
//		jTPLminEvt2.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLminEvt2.setPreferredSize(new Dimension(270,20));
		
		jTFspaOption.setPreferredSize(new Dimension(80,20));
		jTFspaOption.setHorizontalAlignment(JTextField.CENTER);
//		jTPLspaOption.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLspaOption.setPreferredSize(new Dimension(270,20));
		
		// left 2
		lOper.setOpaque(true);
		lOper.setBackground(Beauty.blue);
		lOper.setForeground(Color.WHITE);
		lOper.setPreferredSize(new Dimension(400,20));
		
		viewFavourite.setPreferredSize(new Dimension(140,20));
//		jTPLviewFavourite.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLviewFavourite.setPreferredSize(new Dimension(240,20));
		
		addAll.setPreferredSize(new Dimension(140,20));
//		jTPLaddAll.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLaddAll.setPreferredSize(new Dimension(240,20));
		
//		jTPLwindowSize.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLwindowSize.setPreferredSize(new Dimension(350,20));

		jTFwinSize.setPreferredSize(new Dimension(140,20));
		jTFwinSize.setHorizontalAlignment(JTextField.CENTER);
		winSizeSlider.setPreferredSize(new Dimension(240,20));

		pick.setPreferredSize(new Dimension(140,20));
//		jTPLpick.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLpick.setPreferredSize(new Dimension(240,20));
		
		alldep.setPreferredSize(new Dimension(140,20));
//		jTPLalldep.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLalldep.setPreferredSize(new Dimension(240,20));
		
		
		// left 3
		lGroup.setOpaque(true);
		lGroup.setBackground(Beauty.blue);
		lGroup.setForeground(Color.WHITE);
		lGroup.setPreferredSize(new Dimension(400,20));
		
		jTFpThr.setPreferredSize(new Dimension(80,20));
		jTFpThr.setHorizontalAlignment(JTextField.CENTER);
//		jTPLpThr.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLpThr.setPreferredSize(new Dimension(270,20));
		
		jTFminCFU.setPreferredSize(new Dimension(80,20));
		jTFminCFU.setHorizontalAlignment(JTextField.CENTER);
//		jTPLminCFU.setFont(new Font("Courier", Font.BOLD, 13));
		jTPLminCFU.setPreferredSize(new Dimension(270,20));
		
		// left 4
		lOthers.setOpaque(true);
		lOthers.setBackground(Beauty.blue);
		lOthers.setForeground(Color.WHITE);
		lOthers.setPreferredSize(new Dimension(400,20));
		
		// panel
		left1.setPreferredSize(new Dimension(400,200));
		left2.setPreferredSize(new Dimension(400,200));
		left3.setPreferredSize(new Dimension(400,120));
		left4.setPreferredSize(new Dimension(400,70));
		rowBlank1.setPreferredSize(new Dimension(400,10));
		rowBlank2.setPreferredSize(new Dimension(400,10));
		rowBlank3.setPreferredSize(new Dimension(400,10));
		rowBlank4.setPreferredSize(new Dimension(400,230));
		panel.setPreferredSize(new Dimension(400,850));
	}
	
	public void layout() {
		
		// left 1		
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
//		left1.setLayout(gbl);
		ComponentLayOut.add(left1, gbl, lDetect, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(left1, gbl, jTFoverThr, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTPLoverThr, gbc, 1, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTFminEvt, gbc, 0, 2, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTPLminEvt, gbc, 1, 2, 1, 1, 0, 0);    
		ComponentLayOut.add(left1, gbl, jTFoverThr2, gbc, 0, 3, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTPLoverThr2, gbc, 1, 3, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTFminEvt2, gbc, 0, 4, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTPLminEvt2, gbc, 1, 4, 1, 1, 0, 0);    
		ComponentLayOut.add(left1, gbl, jTFspaOption, gbc, 0, 5, 1, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, jTPLspaOption, gbc, 1, 5, 1, 1, 0, 0);   
		ComponentLayOut.add(left1, gbl, detectRun, gbc, 1, 6, 1, 1, 0, 0);      
		left1.setBorder(BorderFactory.createEtchedBorder());

		
		// left 2
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left2, gbl, lOper, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(left2, gbl, viewFavourite, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, jTPLviewFavourite, gbc, 1, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, addAll, gbc, 0, 2, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, jTPLaddAll, gbc, 1, 2, 1, 1, 0, 0);    
		ComponentLayOut.add(left2, gbl, jTPLwindowSize, gbc, 0, 3, 2, 1, 0, 0);
		ComponentLayOut.add(left2, gbl, jTFwinSize, gbc, 0, 4, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, winSizeSlider, gbc, 1, 4, 1, 1, 0, 0); 
		ComponentLayOut.add(left2, gbl, pick, gbc, 0, 5, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, jTPLpick, gbc, 1, 5, 1, 1, 0, 0);  
		ComponentLayOut.add(left2, gbl, alldep, gbc, 0, 6, 1, 1, 0, 0);    	
		ComponentLayOut.add(left2, gbl, jTPLalldep, gbc, 1, 6, 1, 1, 0, 0);  
		left2.setBorder(BorderFactory.createEtchedBorder());
		
		
		// left 3
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left3, gbl, lGroup, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(left3, gbl, jTFpThr, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left3, gbl, jTPLpThr, gbc, 1, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left3, gbl, jTFminCFU, gbc, 0, 2, 1, 1, 0, 0);    	
		ComponentLayOut.add(left3, gbl, jTPLminCFU, gbc, 1, 2, 1, 1, 0, 0);    
		ComponentLayOut.add(left3, gbl, groupRun, gbc, 1, 3, 1, 1, 0, 0);    	
		left3.setBorder(BorderFactory.createEtchedBorder());
		
		// left 4
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left4, gbl, lOthers, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(left4, gbl, returnButton, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left4, gbl, outputButton, gbc, 1, 1, 1, 1, 0, 0);    	
		left4.setBorder(BorderFactory.createEtchedBorder());
		
		
		GridBagPut settingLeftGroup = new GridBagPut(panel);
		settingLeftGroup.putGridBag(left1, panel, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, panel, 0, 1);
		settingLeftGroup.putGridBag(left2, panel, 0, 2);
		settingLeftGroup.putGridBag(rowBlank2, panel, 0, 3);
		settingLeftGroup.putGridBag(left3, panel, 0, 4);
		settingLeftGroup.putGridBag(rowBlank3, panel, 0, 5);
		settingLeftGroup.putGridBag(left4, panel, 0, 6);
		settingLeftGroup.putGridBag(rowBlank4, panel, 0, 7);
		
		
		left2.setEnabled(false);
		left3.setEnabled(false);
		left4.setEnabled(false);
		
		// left 2
		addAll.setEnabled(false);
		viewFavourite.setEnabled(false);
		jTFwinSize.setEnabled(false);
		winSizeSlider.setEnabled(false);
		pick.setEnabled(false);
		alldep.setEnabled(false);
		
		// left 3
		jTFpThr.setEnabled(false);
		jTFminCFU.setEnabled(false);
		groupRun.setEnabled(false);
		
		// left 4
		returnButton.setEnabled(false);
		outputButton.setEnabled(false);

	}
	
	public void addButtonListeners() {
		
		detectRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float alpha = Float.parseFloat(jTFoverThr.getText());
				int minNumEvt = Integer.parseInt(jTFminEvt.getText());
				float alpha2 = Float.parseFloat(jTFoverThr2.getText());
				int minNumEvt2 = Integer.parseInt(jTFminEvt2.getText());
				EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                try {
		                	CFUDetect task = new CFUDetect(cfuDealer, alpha, minNumEvt, alpha2, minNumEvt2);
		            		task.setting();
		            		task.execute();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
		        });
			}
		});
		
		
		
		
		if (cfuDealer.opts.singleChannel) {
			CFUViewListener viewListener = new CFUViewListener(cfuDealer,cfuDealer.center.imageLabel);
			cfuDealer.center.imageLabel.addMouseListener(viewListener);
			cfuDealer.center.imageLabel.setListener(viewListener);
		} else {
			CFUViewListener viewListenerL = new CFUViewListener(cfuDealer,cfuDealer.center.imageLabelLeft);
			CFUViewListener viewListenerR = new CFUViewListener(cfuDealer,cfuDealer.center.imageLabelRight);
			cfuDealer.center.imageLabelLeft.addMouseListener(viewListenerL);
			cfuDealer.center.imageLabelRight.addMouseListener(viewListenerR);
			cfuDealer.center.imageLabelLeft.setListener(viewListenerL);
			cfuDealer.center.imageLabelRight.setListener(viewListenerR);
			// TODO
		}
		viewFavourite.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				
				if(viewFavourite.isSelected()) {
					pick.setSelected(false);
					if (cfuDealer.opts.singleChannel) {
						cfuDealer.center.imageLabel.listener.setValid(true);
						cfuDealer.center.imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					} else {
						cfuDealer.center.imageLabelLeft.listener.setValid(true);
						cfuDealer.center.imageLabelLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						cfuDealer.center.imageLabelRight.listener.setValid(true);
						cfuDealer.center.imageLabelRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					cfuDealer.viewPick = true;
				}else {
					if (cfuDealer.opts.singleChannel) {
						cfuDealer.center.imageLabel.listener.setValid(false);
						cfuDealer.center.imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} else {
						cfuDealer.center.imageLabelLeft.listener.setValid(false);
						cfuDealer.center.imageLabelLeft.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						cfuDealer.center.imageLabelRight.listener.setValid(false);
						cfuDealer.center.imageLabelRight.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
				
			}
			
		});
		
		addAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nCFU = cfuDealer.cfuInfo1.size();
				for(int i = 1; i <= nCFU; i++) {
					cfuDealer.favCFUList1.add(i);
				}
				if (!cfuDealer.opts.singleChannel) {
					nCFU = cfuDealer.cfuInfo2.size();
					for(int i = 1; i <= nCFU; i++) {
						cfuDealer.favCFUList2.add(i);
					}
				}
				CFUHelper.updateCFUTable(cfuDealer);
			}
		});
		
		pick.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(pick.isSelected()) {
					viewFavourite.setSelected(false);
					if (cfuDealer.opts.singleChannel) {
						cfuDealer.center.imageLabel.listener.setValid(true);
						cfuDealer.center.imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					} else {
						cfuDealer.center.imageLabelLeft.listener.setValid(true);
						cfuDealer.center.imageLabelLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						cfuDealer.center.imageLabelRight.listener.setValid(true);
						cfuDealer.center.imageLabelRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					cfuDealer.viewPick = false;
					
				}else {
					if (cfuDealer.opts.singleChannel) {
						cfuDealer.center.imageLabel.listener.setValid(false);
						cfuDealer.center.imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} else {
						cfuDealer.center.imageLabelLeft.listener.setValid(false);
						cfuDealer.center.imageLabelLeft.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						cfuDealer.center.imageLabelRight.listener.setValid(false);
						cfuDealer.center.imageLabelRight.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
			
		});
		
		alldep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int winSize = Integer.parseInt(jTFwinSize.getText());
				
				EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                try {
		                	CFUCalDep task = new CFUCalDep(cfuDealer, winSize);
		            		task.setting();
		            		task.execute();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
		        });
			}
		});
		
		groupRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				int minCFU = Integer.parseInt(jTFminCFU.getText());
				float pThr = Float.parseFloat(jTFpThr.getText());				
				
				EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                try {
		                	CFUGroupRun task = new CFUGroupRun(cfuDealer, minCFU, pThr);
		            		task.setting();
		            		task.execute();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
		        });
				
			}
		});
		
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cfuDealer.returnResults();
				cfuDealer.close();
			}
		});
		
		outputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				cfuDealer.returnResults();
				cfuDealer.close();
			}
		});
		
		winSizeSlider.setMinorTickSpacing(1);
		winSizeSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!sliderChanging && !winSizeSlider.getValueIsAdjusting()){
					int winSize = winSizeSlider.getValue();
					txtChanging = true;
					jTFwinSize.setText("" + winSize);
					txtChanging = false;
				}
			}
		});
		
		
		jTFwinSize.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
//				changedUpdate(e);
				
			}
            @Override
            public void changedUpdate(DocumentEvent e) {
            	if (!txtChanging) {
            		int winSize = Integer.parseInt(jTFwinSize.getText());
                	sliderChanging = true;
                	winSizeSlider.setValue(winSize);
                	sliderChanging = false;
            	}	
            }

			
        });
	}
}
