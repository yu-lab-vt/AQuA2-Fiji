package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.cfu.CFUHelper;

public class CFURightPanel {
	JPanel panel = new JPanel();
	CFUDealer cfuDealer = null;
	GridBagLayout gbl;
	GridBagConstraints gbc;
	
	JPanel left1 = new JPanel();
	JPanel left2 = new JPanel();
	JPanel left3 = new JPanel();	
	JLabel rowBlank1 = new JLabel();
	JLabel rowBlank2 = new JLabel();
	JLabel rowBlank3 = new JLabel();

	
	// left 1
	JLabel lAdjust = new JLabel(" Adjustment");
	JLabel jTPbackBright = new JLabel(" --- Background brightness / contrast ---");
	public JSlider brightSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
	JLabel jTPcolorBright = new JLabel(" --- Color brightness / contrast ---");
	public JSlider colorSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
	
	JButton reassignColor = new JButton("Reassign Color");
	JButton showDelay = new JButton("Show relative delay");
	
	// left 2
	
	JPanel left20 = new JPanel();	
	JLabel lFavorite = new JLabel(" Favourite");
	JButton selectAll = new JButton("Select all");
	JButton delete = new JButton("Delete");
	JButton showCurves = new JButton("Show curves");
	JButton saveCurves = new JButton("Save curves");
	JButton saveWaves = new JButton("Save waves");
	JPanel empty = new JPanel();	
	JTable table1 = null;
	JScrollPane pTable1 = null;
	public DefaultTableModel tablemode1 = null;
	
	JLabel lCh1 = new JLabel(" Ch1 ID");
	public JTextField jTFCh1 = new JTextField("");
	JButton addCh1 = new JButton("Add");
	
	JPanel left21 = new JPanel();	
	JLabel lCh2 = new JLabel(" Ch2 ID");
	public JTextField jTFCh2 = new JTextField("");
	JButton addCh2 = new JButton("Add");
	
	// left 3
	JLabel lGroupTable = new JLabel(" Group Table");
	JTable table2 = null;
	JScrollPane pTable2 = null;
	public DefaultTableModel tablemode2 = null;
	
	public CFURightPanel(CFUDealer cfuDealer) {
		this.cfuDealer = cfuDealer;
		cfuDealer.right = this;
	}
	
	public JPanel createPanel() {
		setting();
		addButtonListeners();
		layout();
		return panel;
	}
	
	public void setting() {
		
		// left 1
		lAdjust.setOpaque(true);
		lAdjust.setBackground(Beauty.blue);
		lAdjust.setForeground(Color.WHITE);
		lAdjust.setPreferredSize(new Dimension(400,20));
		
		jTPbackBright.setPreferredSize(new Dimension(400,20));
		jTPbackBright.setHorizontalAlignment(JTextField.CENTER);
		brightSlider.setPreferredSize(new Dimension(390,20));
		
		jTPcolorBright.setPreferredSize(new Dimension(400,20));
		jTPcolorBright.setHorizontalAlignment(JTextField.CENTER);
		colorSlider.setPreferredSize(new Dimension(390,20));
		
		showDelay.setEnabled(false);
		
		// left 2
		
		lFavorite.setOpaque(true);
		lFavorite.setBackground(Beauty.blue);
		lFavorite.setForeground(Color.WHITE);
		lFavorite.setPreferredSize(new Dimension(400,20));
		
		
		selectAll.setPreferredSize(new Dimension(120,20));
		delete.setPreferredSize(new Dimension(120,20));
		showCurves.setPreferredSize(new Dimension(120,20));
		saveCurves.setPreferredSize(new Dimension(120,20));
		saveWaves.setPreferredSize(new Dimension(120,20));
		empty.setPreferredSize(new Dimension(120,20));
		addCh1.setPreferredSize(new Dimension(60,20));
		addCh2.setPreferredSize(new Dimension(60,20));
	
		left20.setPreferredSize(new Dimension(390,50));
		left21.setPreferredSize(new Dimension(390,25));
		
		jTFCh1.setPreferredSize(new Dimension(50,20));
		jTFCh1.setHorizontalAlignment(JTextField.CENTER);
//		lCh1.setFont(new Font("Courier", Font.BOLD, 13));
		lCh1.setPreferredSize(new Dimension(50,20));
		
		jTFCh2.setPreferredSize(new Dimension(50,20));
		jTFCh2.setHorizontalAlignment(JTextField.CENTER);
//		lCh2.setFont(new Font("Courier", Font.BOLD, 13));
		lCh2.setPreferredSize(new Dimension(50,20));
		
		setTable1();
		
		// left 3
		lGroupTable.setOpaque(true);
		lGroupTable.setBackground(Beauty.blue);
		lGroupTable.setForeground(Color.WHITE);
		lGroupTable.setPreferredSize(new Dimension(400,20));
		
		setTable2();
		
		// panel
		left1.setPreferredSize(new Dimension(400,170));
		left2.setPreferredSize(new Dimension(400,360));
		left3.setPreferredSize(new Dimension(400,300));
		rowBlank1.setPreferredSize(new Dimension(400,10));
		rowBlank2.setPreferredSize(new Dimension(400,10));
//		rowBlank3.setPreferredSize(new Dimension(400,10));
		panel.setPreferredSize(new Dimension(400,850));
	}
	
	public void layout() {
		
		// left 1		
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left1, gbl, lAdjust, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(left1, gbl, jTPbackBright, gbc, 0, 1, 2, 1, 0, 0);    	
		ComponentLayOut.add(left1, gbl, brightSlider, gbc, 0, 2, 2, 1, 0, 0);  
		ComponentLayOut.add(left1, gbl, jTPcolorBright, gbc, 0, 2, 2, 1, 0, 0);  
		ComponentLayOut.add(left1, gbl, colorSlider, gbc, 0, 2, 2, 1, 0, 0);  
		ComponentLayOut.add(left1, gbl, reassignColor, gbc, 0, 3, 1, 1, 0, 0);  
		ComponentLayOut.add(left1, gbl, showDelay, gbc, 1, 3, 1, 1, 0, 0);
		left1.setBorder(BorderFactory.createEtchedBorder());
//
//		
//		// left 2
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left20, gbl, selectAll, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(left20, gbl, delete, gbc, 1, 0, 1, 1, 0, 0);    	
		ComponentLayOut.add(left20, gbl, showCurves, gbc, 2, 0, 1, 1, 0, 0);    	
		ComponentLayOut.add(left20, gbl, saveCurves, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(left20, gbl, saveWaves, gbc, 1, 1, 1, 1, 0, 0);    
		ComponentLayOut.add(left20, gbl, empty, gbc, 2, 1, 1, 1, 0, 0); 
		
		left21.add(lCh1);
		left21.add(jTFCh1);
		left21.add(addCh1);
		left21.add(lCh2);
		left21.add(jTFCh2);
		left21.add(addCh2);

		
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left2, gbl, lFavorite, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(left2, gbl, left20, gbc, 0, 1, 1, 2, 0, 0);    	
		ComponentLayOut.add(left2, gbl, pTable1, gbc, 0, 2, 1, 2, 0, 0);    	
		ComponentLayOut.add(left2, gbl, left21, gbc, 0, 4, 1, 1, 0, 0);    
		 	

		left2.setBorder(BorderFactory.createEtchedBorder());
//		
//		
//		// left 3
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(left3, gbl, lGroupTable, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(left3, gbl, pTable2, gbc, 0, 1, 1, 2, 0, 0);    	
		left3.setBorder(BorderFactory.createEtchedBorder());

		
		
		GridBagPut settingLeftGroup = new GridBagPut(panel);
		settingLeftGroup.putGridBag(left1, panel, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, panel, 0, 1);
		settingLeftGroup.putGridBag(left2, panel, 0, 2);
		settingLeftGroup.putGridBag(rowBlank2, panel, 0, 3);
		settingLeftGroup.putGridBag(left3, panel, 0, 4);
		settingLeftGroup.putGridBag(rowBlank3, panel, 0, 5);

	}
	
	private void setTable1(){
		tablemode1 = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 0)
    				return true;
    			else
    				return false;
    		}
    	};
    	tablemode1.addColumn("");
    	tablemode1.addColumn("CH");
    	tablemode1.addColumn("Id");
    	tablemode1.addColumn("# Evt");
    	tablemode1.addColumn("# Event List");
    	table1 = new JTable(tablemode1){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0: return Boolean.class;
					case 1: return String.class;
					case 2: return String.class;
					case 3: return String.class;
					case 4: return String.class;
					default: return String.class;
				}
			}
    		
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(column==0)
					component.setBackground(new Color(238, 238, 238));
				else {
					if(row%2==1)
						component.setBackground(new Color(245, 245, 250));
					else
						component.setBackground(Color.white);
				}
				return component;
			}
    	};
    	DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    	tcr.setHorizontalAlignment(JLabel.CENTER);
    	table1.setDefaultRenderer(Object.class, tcr);
    	table1.getColumnModel().getColumn(0).setPreferredWidth(15);
    	table1.getColumnModel().getColumn(1).setPreferredWidth(15);
    	table1.setSize(new Dimension(380,240));
    	pTable1 = new JScrollPane(table1);
    	pTable1.setPreferredSize(new Dimension(380,235));
    	pTable1.setOpaque(true);
    	pTable1.setBackground(Color.WHITE);
    	pTable1.getViewport().setBackground(Color.WHITE);
	}
	
	private void setTable2(){
		tablemode2 = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 0)
    				return true;
    			else
    				return false;
    		}
    	};
    	tablemode2.addColumn("");
    	tablemode2.addColumn("Group Index");
    	tablemode2.addColumn("CFU number");
    	tablemode2.addColumn("CFU index");
    	table2 = new JTable(tablemode2){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0: return Boolean.class;
					case 1: return String.class;
					case 2: return String.class;
					case 3: return String.class;
					default: return String.class;
				}
			}
    		
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(column==0)
					component.setBackground(new Color(238, 238, 238));
				else {
					if(row%2==1)
						component.setBackground(new Color(245, 245, 250));
					else
						component.setBackground(Color.white);
				}
				return component;
			}
    	};
    	DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    	tcr.setHorizontalAlignment(JLabel.CENTER);
    	table2.setDefaultRenderer(Object.class, tcr);
    	table2.getColumnModel().getColumn(0).setPreferredWidth(15);
    	table2.getColumnModel().getColumn(1).setPreferredWidth(15);
    	table2.setSize(new Dimension(380,240));
    	pTable2 = new JScrollPane(table2);
    	pTable2.setPreferredSize(new Dimension(380,255));
    	pTable2.setOpaque(true);
    	pTable2.setBackground(Color.WHITE);
    	pTable2.getViewport().setBackground(Color.WHITE);
	}
	
	public void addButtonListeners() {
		
		brightSlider.setMinorTickSpacing(1);
		brightSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!brightSlider.getValueIsAdjusting()){
					cfuDealer.dealImage();
				}
			}
		});
		
		colorSlider.setMinorTickSpacing(1);
		colorSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!colorSlider.getValueIsAdjusting()){
					cfuDealer.dealImage();
				}
			}
		});
		
		reassignColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cfuDealer.CFUColors1 = null;
				cfuDealer.CFUColors2 = null;
				cfuDealer.useDelayColor = false;
				cfuDealer.dealImage();
			}
		});
		
		showDelay.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cfuDealer.useDelayColor = true;
				cfuDealer.dealImage();
			}
		});

		selectAll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rowNumber = table1.getRowCount();
				for(int r = 0; r < rowNumber; r++) {
					table1.setValueAt(new Boolean(true), r, 0);
				}
				cfuDealer.dealImage();
			}
		});
		
		delete.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int rowNumber = table1.getRowCount();
				for(int r = rowNumber - 1; r >= 0; r--) {
					boolean selected = (boolean) table1.getValueAt(r, 0);
					if(selected) {
						int ch = (Integer) table1.getValueAt(r, 1);
						int label = (Integer) table1.getValueAt(r, 2);
						if (ch == 1)
							cfuDealer.favCFUList1.remove(label);
						else
							cfuDealer.favCFUList2.remove(label);
						tablemode1.removeRow(r);
					}
				}
				cfuDealer.dealImage();
			}
		});
		
		showCurves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Integer> indexLst = new ArrayList<>();
				int rowNumber = table1.getRowCount();
				for(int r = 0; r < rowNumber; r++) {
					boolean selected = (boolean) table1.getValueAt(r, 0);
					if(selected) {
						int nEvt = (Integer) table1.getValueAt(r, 2);
						indexLst.add(nEvt);
					}
				}
				if(indexLst.size()>0)
					CFUHelper.showCurves(cfuDealer, indexLst);
				else
					JOptionPane.showMessageDialog(null, "Need Select First! ");
			}
		});
		
		saveCurves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					saveCurve(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		
		saveWaves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					saveWave(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		addCh1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int label =  Integer.parseInt(jTFCh1.getText());
				int nCFU = cfuDealer.cfuInfo1.size();
				if (label > 0 && label <= nCFU && !cfuDealer.favCFUList1.contains(label)) {
					cfuDealer.favCFUList1.add(label);
					CFUHelper.updateCFUTable(cfuDealer);
				}
						
			}
		});
		
		addCh2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int label =  Integer.parseInt(jTFCh2.getText());
				int nCFU = cfuDealer.cfuInfo2.size();
				// TODO: channel 2 modification
				if (label > 0 && label <= nCFU && !cfuDealer.favCFUList2.contains(label)) {
					cfuDealer.favCFUList2.add(label);
					CFUHelper.updateCFUTable(cfuDealer);
				}
			}
		});
		
		table1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int r = table1.getSelectedRow();
				int nEvt = (int) table1.getValueAt(r, 2);
				boolean select = (boolean) table1.getValueAt(r, 0);
				table1.setValueAt(!select, r, 0);
			}
		});
		
		table2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int r = table2.getSelectedRow();
				int groupId = (int) table2.getValueAt(r, 1);
				boolean select = (boolean) table2.getValueAt(r, 0);
				int rowNumber = table2.getRowCount();
				for(int i = 0; i < rowNumber; i++) {
					table2.setValueAt(new Boolean(false), i, 0);
				}
				int c = table2.getSelectedColumn();
				if (c != 0)
					select = !select;
				table2.setValueAt(select, r, 0);
				
				if (select) {
					cfuDealer.selectedGroupEvts = cfuDealer.groupInfo.get(groupId).labels;
					CFUHelper.getDelayColor(cfuDealer, groupId);
					showDelay.setEnabled(true);
				} else {
					cfuDealer.selectedGroupEvts = null;
					cfuDealer.useDelayColor = false;
					showDelay.setEnabled(false);
				}
				
				cfuDealer.dealImage();
				cfuDealer.center.resultsLabel.drawCurve(Helper.array2list(cfuDealer.selectedGroupEvts));
			}
		});
	}
	
	public void saveCurve(String savePath) {
		JLabel canvas = cfuDealer.center.resultsLabel;
		BufferedImage img = new BufferedImage(canvas.getWidth(),canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		canvas.printAll(g2d);
		File f=new File(savePath + ".jpg");
		try {
			ImageIO.write(img, "jpg", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		g2d.dispose();
	}
	
	public void saveWave(String savePath) {
		// TODO: Write table
	}
}
