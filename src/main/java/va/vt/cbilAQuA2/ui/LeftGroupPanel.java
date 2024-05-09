package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;

public class LeftGroupPanel {
	ImageDealer imageDealer = null;
	MyImageLabel imageLabel = null;
	
	JPanel leftGroup = new JPanel();
	JPanel left1 = new JPanel();
	JPanel left2 = new JPanel();	
	
	// left1
	JLabel dirLabel = new JLabel(" Direction, region, landmarks");
	JLabel cellBoundary = new JLabel("Cell boundary");
	JLabel landmark = new JLabel("Landmark (soma)");
	JToggleButton addLeft11 = new JToggleButton("+");
	JToggleButton addLeft12 = new JToggleButton("+");
	JToggleButton removeLeft11 = new JToggleButton("-");
	JToggleButton removeLeft12 = new JToggleButton("-");
	JToggleButton name11 = new JToggleButton("Name");
	JToggleButton name12 = new JToggleButton("Name");
	JToggleButton drawAnterior = new JToggleButton("Draw anterior");
	JButton save11 = new JButton("Save");
	JButton save12 = new JButton("Save");
	JButton load11 = new JButton("Load");
	JButton load12 = new JButton("Load");
	JButton maskBuilder = new JButton("Mask builder");
	public JToggleButton checkROI = new JToggleButton("Check ROI");
	JPanel left11 = new JPanel();
	JPanel left12 = new JPanel();
	JPanel left13 = new JPanel();
	
	JLabel rowBlank1 = new JLabel();
	
	// left2
	JLabel detLabel = new JLabel(" Detection pipeline");
	public JTabbedPane jTP = new JTabbedPane();
	JPanel jTP1 = new JPanel();	
	JPanel jTP2 = new JPanel();	
	JPanel jTP3 = new JPanel();
	JPanel jTP4 = new JPanel();
	JPanel jTP5 = new JPanel();
	JPanel jTP6 = new JPanel();
	
	GridBagLayout gbl;
	GridBagConstraints gbc;
	
	public int curStatus = 0;
	public int jTPStatus = 0;
	public JButton backButton = new JButton("Back");
	JButton runButton = new JButton("Run");
	public JButton nextButton = new JButton("Next");
	JPanel left2buttons = new JPanel();
	JButton saveopts = new JButton("SaveOpts");
	JButton loadopts = new JButton("LoadOpts");
	JButton runAllButton = new JButton("RunAllSteps");
	JPanel jTPButtons = new JPanel();
	JLabel blankLabel = new JLabel();
	
	// jTP1
	JLabel jTPLbaseline = new JLabel(" --- Baseline modeling and noise modeling ---");
	public JTextField jTFsmo = new JTextField("0.5");
	JLabel jTPLsmo = new JLabel(" Gaussian filter radius");
	
	
	// jTP2
	JLabel jTPLthresholding = new JLabel(" --------------- Thresholding ---------------");
	JLabel jTPLfiltering = new JLabel(" ------------------ Filter ------------------");
	public JTextField jTFthr = new JTextField("3");
	public JTextField jTFminDur = new JTextField("5");
	public JTextField jTFminsize = new JTextField("20");
	JLabel jTPLthr = new JLabel(" Intensity threshold scaling factor");
	JLabel jTPLminsize = new JLabel(" Minimum size (pixels)");
	JLabel jTPLminDur = new JLabel(" Minimum duration");
	public JSlider thrSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
	public JTextField jTFmaxSize = new JTextField("inf");
	public JTextField jTFcircularity = new JTextField("0");
	JLabel jTPLmaxSize = new JLabel(" Maximum size (pixels)");
	JLabel jTPLcircularity = new JLabel(" Circularity threshold for active region");
	
	// jTP3
	public JCheckBox jTFneedTemp = new JCheckBox("",true);
	JLabel jTPneedTemp = new JLabel(" Enable temporal segmentation?");
	JLabel jTPLseed = new JLabel(" -------------- Seed Detection --------------");
	JLabel jTPLmerge = new JLabel(" ---- Merge regions with similar signals ----");
	public JTextField jTFseedSize = new JTextField("0.01");
	public JTextField jTFzScore = new JTextField("3.5");
	public JTextField jTFmergeDis = new JTextField("0.6");
	JLabel jTPLseedSize = new JLabel(" Minimum seed size / active region");
	JLabel jTPLzScore = new JLabel(" Zscore of seed significance");
	JLabel jTPLmergeDis = new JLabel(" Maximum dissimilarity allowed in merging");
	
	// jTP4
	public JCheckBox jTFneedSpa = new JCheckBox("",true);
	JLabel jTPneedSpa = new JLabel(" Enable spatial segmentation?");
	JLabel jTPLspaSeg = new JLabel(" -------- Spatial segmentation setting --------");
	public JTextField jTFsourceRatio = new JTextField("0.01");
	public JTextField jTFsensitivity = new JTextField("8");
	JLabel jTPLsourceRatio = new JLabel(" Minimum source size / super event");
	JLabel jTPLsensitivity = new JLabel(" Sensitivity to detect source (Level 1 to 10)");
	public JCheckBox jTFwhetherExtend = new JCheckBox("",true);
	JLabel jTPwhetherExtend = new JLabel(" Enable temporal extension for events?");
	
	// jTP5
	public JCheckBox jTFdetectGlo = new JCheckBox("",false);
	public JTextField jTFgloDur = new JTextField("20");
	JLabel jTPLdetectGlo = new JLabel(" Enable global signal detection?");
	JLabel jTPLgloDur = new JLabel(" Global signal duration");

	// jTP6
	
	public JCheckBox jTFignoreTau = new JCheckBox("",true);
	public JCheckBox jTFcheckProp = new JCheckBox("",true);
	public JCheckBox jTFcheckNetwork = new JCheckBox("",true);
	JLabel jTPignoreTau = new JLabel(" Ignore calculating decay speed");
	JLabel jTPcheckProp = new JLabel(" Propagation-related metrics");
	JLabel jTPcheckProp01 = new JLabel("  ");
//	JLabel jTPcheckProp02 = new JLabel("  ");
	JLabel jTPcheckProp1 = new JLabel(" (Rising map is already calculated)");
//	JLabel jTPcheckProp2 = new JLabel(" (Propagation map is already calculated)");
	JLabel jTPcheckNetwork = new JLabel(" Network features");
	
	JLabel rowBlank2 = new JLabel();
	
	boolean sliderChanging = false;
	boolean txtChanging = false;
	
	// Left 3
	JLabel proofReading = new JLabel(" Proof reading");
	JToggleButton viewFavourite = new JToggleButton("view/favourite");
	JToggleButton deleteRestore = new JToggleButton("delete/restore");
	JButton addAllFiltered = new JButton("addAllFiltered");
	JButton featuresPlot = new JButton("featuresPlot");
	public JPanel left3 = new JPanel();
	JPanel left3Buttons = new JPanel();
	JPanel left3Buttons2 = new JPanel();
	DefaultTableModel model = null;
	JTable table = null;
	JScrollPane tablePane = null;
	Object[][] tableData = null;

	JLabel rowBlank3 = new JLabel();
	
	// Left 4
	public JPanel left4 = new JPanel();
	JLabel export = new JLabel(" Export");
	JCheckBox events = new JCheckBox(" Events and features",true);
	JCheckBox movie = new JCheckBox(" Movies with overlay",true);
	JButton exportButton = new JButton("Export/Save");
	JButton restart = new JButton("Restart");
	JLabel blank2 = new JLabel();
	
	MyImageLabel leftImageLabel = null;
	MyImageLabel rightImageLabel = null;
	

	// Make Builder
	JPanel builderLeft1 = new JPanel();
	JLabel loadMasks = new JLabel(" Load masks");
	JLabel region = new JLabel(" Region");
	JLabel regionMaker = new JLabel(" Region maker"); 
	JLabel landMark = new JLabel(" Landmark");
	JButton self1 = new JButton("Self");
	JButton self2 = new JButton("Self");
	JButton self3 = new JButton("Self");
	JButton folder1 = new JButton("Folder");
	JButton folder2 = new JButton("Folder");
	JButton folder3 = new JButton("Folder");
	JButton file1 = new JButton("File");
	JButton file2 = new JButton("File");
	JButton file3 = new JButton("File");
	JPanel builderLeft11 = new JPanel();
	JPanel builderLeft12 = new JPanel();
	JPanel builderLeft13 = new JPanel();
	JScrollPane builderTablePane = null;
	JTable builderTable = null;
	DefaultTableModel builderTableModel = null;
	JButton builderRemove = new JButton("Remove");
	JPanel builderManual = new JPanel();
	JLabel buiderManualLabel = new JLabel("Manually Select");
	JButton builderMClear = new JButton("Clear");
	JToggleButton builderMAdd = new JToggleButton("Add");
	JToggleButton builderMRemove = new JToggleButton("Remove");
	JPanel builderLeft1Buttons = new JPanel();
	
	JPanel builderLeft2 = new JPanel();
	JLabel saveRegionsLanmarks = new JLabel(" Save regions/landmarks");
	JLabel role = new JLabel(" Role of region markers");
	JLabel combineRegion = new JLabel(" Combine region masks");
	JLabel combineLandmark = new JLabel(" Combine landmark masks");
	String[] roleString = new String[] {"Segment region","Remove region"};
	JComboBox<String> roleJCB = new JComboBox<String>(roleString);
	String[] combineRegionString = new String[] {"Or","And"};
	JComboBox<String> combineRegionJCB = new JComboBox<String>(combineRegionString);
	String[] combineLandmarkString = new String[] {"Or","And"};
	JComboBox<String> combineLandmarkJCB = new JComboBox<String>(combineLandmarkString);
	JPanel builderLeft21 = new JPanel();
	JPanel builderLeft22 = new JPanel();
	JPanel builderLeft23 = new JPanel();
	JButton apply = new JButton("Apply & Back");
	JButton discard = new JButton("Discard & Back");
	JPanel builderLeft2Buttons = new JPanel();
	
	ArrayList<BuilderTableItem> builderMap = new ArrayList<>();
	ArrayList<Integer> intensityThreshold = new ArrayList<>();
	ArrayList<Integer> minSize = new ArrayList<>();
	ArrayList<Integer> maxSize = new ArrayList<>();
	
	DrawListener drawlistener = null;
	DrawListener drawlistener2 = null;
	BuilderDrawListener builderDrawListener = null;
	RemoveListener removeListener = null;
	RemoveListener removeListener2 = null;
	NameListenerRegion nameListenerRegion = null;
	NameListenerLandMark nameListenerLandMark = null;
	BuilderRemoveListener builderRemoveListener = null;
	
	public LeftGroupPanel(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		imageLabel = imageDealer.getImageLabel();
	}
	
	public void setting() {
		jTFsmo.setText(imageDealer.opts.smoXY + "");
		
		jTFthr.setText(imageDealer.opts.thrARScl+"");
		jTFminDur.setText(imageDealer.opts.minDur + "");
		jTFminsize.setText(imageDealer.opts.minSize + "");
		
		jTFseedSize.setText(imageDealer.opts.seedSzRatio + "");
		jTFzScore.setText(imageDealer.opts.sigThr + "");
		jTFmergeDis.setText(imageDealer.opts.maxDelay + "");
				
		jTFsourceRatio.setText(imageDealer.opts.sourceSzRatio + "");
		jTFsensitivity.setText(imageDealer.opts.sourceSensitivity + "");
		
		jTFdetectGlo.setSelected(imageDealer.opts.detectGlo);
		jTFgloDur.setText(imageDealer.opts.gloDur + "");
		
		jTFignoreTau.setSelected(imageDealer.opts.ignoreTau);
		jTFcheckProp.setSelected(imageDealer.opts.checkProp);
		jTFcheckNetwork.setSelected(imageDealer.opts.checkNetwork);
		
		addLeft11.setMargin(new Insets(0,0,0,0));
		addLeft12.setMargin(new Insets(0,0,0,0));
		removeLeft11.setMargin(new Insets(0,0,0,0));
		removeLeft12.setMargin(new Insets(0,0,0,0));
		name11.setMargin(new Insets(0,0,0,0));
		name12.setMargin(new Insets(0,0,0,0));
		save11.setMargin(new Insets(0,0,0,0));
		save12.setMargin(new Insets(0,0,0,0));
		load11.setMargin(new Insets(0,0,0,0));
		load12.setMargin(new Insets(0,0,0,0));
		
		// left1
		dirLabel.setOpaque(true);
		dirLabel.setBackground(Beauty.blue);
		dirLabel.setForeground(Color.WHITE);
		dirLabel.setPreferredSize(new Dimension(400,20));
		cellBoundary.setPreferredSize(new Dimension(120,20));
		landmark.setPreferredSize(new Dimension(120,20));
		addLeft11.setPreferredSize(new Dimension(30,20));
		addLeft12.setPreferredSize(new Dimension(30,20));
		save11.setPreferredSize(new Dimension(60,20));
		save12.setPreferredSize(new Dimension(60,20));
		load11.setPreferredSize(new Dimension(60,20));
		load12.setPreferredSize(new Dimension(60,20));
		removeLeft11.setPreferredSize(new Dimension(30,20));
		removeLeft12.setPreferredSize(new Dimension(30,20));
		name11.setPreferredSize(new Dimension(65,20));
		name12.setPreferredSize(new Dimension(65,20));
		drawAnterior.setPreferredSize(new Dimension(125,20));
		maskBuilder.setPreferredSize(new Dimension(125,20));
		checkROI.setPreferredSize(new Dimension(125,20));
		
		// left2
		detLabel.setOpaque(true);
		detLabel.setBackground(Beauty.blue);
		detLabel.setForeground(Color.WHITE);
		detLabel.setPreferredSize(new Dimension(400,20));
			
		// TabbedPane
		jTP.setPreferredSize(new Dimension(400,250));
		
		blankLabel.setPreferredSize(new Dimension(150,20));
    	backButton.setEnabled(false);
    	nextButton.setEnabled(false);
    	
		// jTP1
    	jTPLbaseline.setPreferredSize(new Dimension(350,20));
    	jTPLbaseline.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLbaseline.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFsmo.setPreferredSize(new Dimension(80,20));
    	jTFsmo.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLsmo.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPLsmo.setPreferredSize(new Dimension(270,20));
    	
		// jTP2
    	jTPLthresholding.setPreferredSize(new Dimension(350,20));
    	jTPLthresholding.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLthresholding.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTPLfiltering.setPreferredSize(new Dimension(350,20));
    	jTPLfiltering.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLfiltering.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	
    	jTFthr.setPreferredSize(new Dimension(80,20));
    	jTFthr.setHorizontalAlignment(JTextField.CENTER);
    	jTPLthr.setPreferredSize(new Dimension(270,20));
//    	jTPLthr.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFminDur.setPreferredSize(new Dimension(80,20));
    	jTFminDur.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLminDur.setPreferredSize(new Dimension(270,20));
//    	jTPLminDur.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFminsize.setPreferredSize(new Dimension(80,20));
    	jTFminsize.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLminsize.setPreferredSize(new Dimension(270,20));
//    	jTPLminsize.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFmaxSize.setPreferredSize(new Dimension(80,20));
    	jTFmaxSize.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLmaxSize.setPreferredSize(new Dimension(270,20));
//    	jTPLmaxSize.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFcircularity.setPreferredSize(new Dimension(80,20));
    	jTFcircularity.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLcircularity.setPreferredSize(new Dimension(270,20));
//    	jTPLcircularity.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	
    	thrSlider.setPreferredSize(new Dimension(350,20));

    	
		// jTP3
    	jTFneedTemp.setPreferredSize(new Dimension(80,20));
    	jTFneedTemp.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPneedTemp.setPreferredSize(new Dimension(270,20));
    	
    	jTPLseed.setPreferredSize(new Dimension(350,20));
    	jTPLseed.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLseed.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTPLmerge.setPreferredSize(new Dimension(350,20));
    	jTPLmerge.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLmerge.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFseedSize.setPreferredSize(new Dimension(80,20));
    	jTFseedSize.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLseedSize.setPreferredSize(new Dimension(270,20));
//    	jTPLseedSize.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFzScore.setPreferredSize(new Dimension(80,20));
    	jTFzScore.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLzScore.setPreferredSize(new Dimension(270,20));
//    	jTPLzScore.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFmergeDis.setPreferredSize(new Dimension(80,20));
    	jTFmergeDis.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLmergeDis.setPreferredSize(new Dimension(270,20));
//    	jTPLmergeDis.setFont(new Font("Courier", Font.BOLD, 13));
    	
		// jTP4
    	jTFneedSpa.setPreferredSize(new Dimension(80,20));
    	jTFneedSpa.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPneedSpa.setPreferredSize(new Dimension(270,20));
    	
    	jTPLspaSeg.setPreferredSize(new Dimension(350,20));
    	jTPLspaSeg.setHorizontalAlignment(JTextField.CENTER);
//    	jTPLspaSeg.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFsourceRatio.setPreferredSize(new Dimension(80,20));
    	jTFsourceRatio.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLsourceRatio.setPreferredSize(new Dimension(270,20));
//    	jTPLsourceRatio.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFsensitivity.setPreferredSize(new Dimension(80,20));
    	jTFsensitivity.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLsensitivity.setPreferredSize(new Dimension(270,20));
//    	jTPLsensitivity.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFwhetherExtend.setPreferredSize(new Dimension(80,20));
    	jTFwhetherExtend.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPwhetherExtend.setPreferredSize(new Dimension(270,20));
    	
		// jTP5
    	jTFdetectGlo.setPreferredSize(new Dimension(80,20));
    	jTFdetectGlo.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLdetectGlo.setPreferredSize(new Dimension(270,20));
//    	jTPLdetectGlo.setFont(new Font("Courier", Font.BOLD, 13));
    	
    	jTFgloDur.setPreferredSize(new Dimension(80,20));
    	jTFgloDur.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPLgloDur.setPreferredSize(new Dimension(270,20));
//    	jTPLgloDur.setFont(new Font("Courier", Font.BOLD, 13));
    	
		// jTP6
    	jTFignoreTau.setPreferredSize(new Dimension(80,20));
    	jTFignoreTau.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPignoreTau.setPreferredSize(new Dimension(270,20));
//    	jTPignoreTau.setFont(new Font("Courier", Font.BOLD, 13));

    	jTFcheckProp.setPreferredSize(new Dimension(80,20));
    	jTFcheckProp.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPcheckProp.setPreferredSize(new Dimension(270,20));
//    	jTPcheckProp.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPcheckProp1.setPreferredSize(new Dimension(270,20));
//    	jTPcheckProp1.setFont(new Font("Courier", Font.BOLD, 13));
//    	jTPcheckProp2.setPreferredSize(new Dimension(270,20));
//    	jTPcheckProp2.setFont(new Font("Courier", Font.BOLD, 13));
    	jTPcheckProp01.setPreferredSize(new Dimension(80,20));
//    	jTPcheckProp02.setPreferredSize(new Dimension(80,20));
    	
    	jTFcheckNetwork.setPreferredSize(new Dimension(80,20));
    	jTFcheckNetwork.setHorizontalAlignment(JTextField.CENTER);    	
    	jTPcheckNetwork.setPreferredSize(new Dimension(270,20));
//    	jTPcheckNetwork.setFont(new Font("Courier", Font.BOLD, 13));    	
    	
    	// left2
    	
    	jTP.add("Pre", jTP1);
    	jTP.add("Act", jTP2);
    	jTP.add("Temp", jTP3);
    	jTP.add("Spa", jTP4);
    	jTP.add("Glo", jTP5);
    	jTP.add("Fea", jTP6);
    	
//    	jTP.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	jTP.setEnabledAt(1, false);
    	jTP.setEnabledAt(2, false);
    	jTP.setEnabledAt(3, false);
    	jTP.setEnabledAt(4, false);
    	jTP.setEnabledAt(5, false);
    	
    	
    	
    	backButton.setPreferredSize(new Dimension(120, 25));
    	runButton.setPreferredSize(new Dimension(120, 25));
    	nextButton.setPreferredSize(new Dimension(120, 25));
    	saveopts.setPreferredSize(new Dimension(120, 25));
    	loadopts.setPreferredSize(new Dimension(120, 25));
    	runAllButton.setPreferredSize(new Dimension(120, 25));
//    	backButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	runButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	nextButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	saveopts.setFont(new Font("Courier", Font.BOLD, 12));
//    	loadopts.setFont(new Font("Courier", Font.BOLD, 12));
//    	runAllButton.setFont(new Font("Courier", Font.BOLD, 12));

    	// left 3
    	proofReading.setOpaque(true);
    	proofReading.setBackground(Beauty.blue);
    	proofReading.setForeground(Color.WHITE);
    	proofReading.setPreferredSize(new Dimension(400,20));
    	viewFavourite.setPreferredSize(new Dimension(190,25));
    	deleteRestore.setPreferredSize(new Dimension(190,25));
    	addAllFiltered.setPreferredSize(new Dimension(190,25));
    	featuresPlot.setPreferredSize(new Dimension(190,25));
    	
//    	viewFavourite.setMargin(new Insets(0,0,0,0));
//    	deleteRestore.setMargin(new Insets(0,0,0,0));
//    	deleteRestore.setMargin(new Insets(0,0,0,0));
//    	addAllFiltered.setMargin(new Insets(0,0,0,0));
//    	featuresPlot.setMargin(new Insets(0,0,0,0));

//    	viewFavourite.setFont(new Font("Courier", Font.BOLD, 12));
//    	deleteRestore.setFont(new Font("Courier", Font.BOLD, 12));
//    	addAllFiltered.setFont(new Font("Courier", Font.BOLD, 12));
//    	featuresPlot.setFont(new Font("Courier", Font.BOLD, 12));
    		// Table
    	setTable();
    	
    	// left 4
    	export.setOpaque(true);
    	export.setBackground(Beauty.blue);
    	export.setForeground(Color.WHITE);
    	exportButton.setPreferredSize(new Dimension(120,25));
    	blank2.setPreferredSize(new Dimension(120,25));
    	restart.setPreferredSize(new Dimension(120,25));
    	export.setPreferredSize(new Dimension(400,20));
    	events.setPreferredSize(new Dimension(400,30));
    	movie.setPreferredSize(new Dimension(400,30));
    	
//    	events.setFont(new Font("Courier", Font.BOLD, 14));
//    	movie.setFont(new Font("Courier", Font.BOLD, 14));
    	
//    	exportButton.setMargin(new Insets(0,0,0,0));
//    	restart.setMargin(new Insets(0,0,0,0));
//    	
//    	exportButton.setFont(new Font("Courier", Font.BOLD, 12));
//    	restart.setFont(new Font("Courier", Font.BOLD, 12));
//    	exportButton.setPreferredSize(new Dimension(190,20));
//    	restart.setPreferredSize(new Dimension(190,20));
    	
    	rowBlank1.setPreferredSize(new Dimension(400,7));
    	rowBlank2.setPreferredSize(new Dimension(400,7));
    	rowBlank3.setPreferredSize(new Dimension(400,8));
    	
    	
    	
    	
    	// Builder
    	loadMasks.setOpaque(true);
    	loadMasks.setBackground(Beauty.blue);
    	loadMasks.setForeground(Color.WHITE);
    	loadMasks.setPreferredSize(new Dimension(400,20));
    	
    	saveRegionsLanmarks.setOpaque(true);
    	saveRegionsLanmarks.setBackground(Beauty.blue);
    	saveRegionsLanmarks.setForeground(Color.WHITE);
    	saveRegionsLanmarks.setPreferredSize(new Dimension(400,20));
    	
    	region.setPreferredSize(new Dimension(100,20));
    	self1.setPreferredSize(new Dimension(80,20));
    	folder1.setPreferredSize(new Dimension(80,20));
    	file1.setPreferredSize(new Dimension(80,20));
//    	region.setFont(new Font("Courier", Font.BOLD, 12));
//    	self1.setFont(new Font("Courier", Font.BOLD, 12));
//    	folder1.setFont(new Font("Courier", Font.BOLD, 12));
//    	file1.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	regionMaker.setPreferredSize(new Dimension(100,20));
    	self2.setPreferredSize(new Dimension(80,20));
    	folder2.setPreferredSize(new Dimension(80,20));
    	file2.setPreferredSize(new Dimension(80,20));
//    	regionMaker.setFont(new Font("Courier", Font.BOLD, 12));
//    	self2.setFont(new Font("Courier", Font.BOLD, 12));
//    	folder2.setFont(new Font("Courier", Font.BOLD, 12));
//    	file2.setFont(new Font("Courier", Font.BOLD, 12));
//    	
    	landMark.setPreferredSize(new Dimension(100,20));
    	self3.setPreferredSize(new Dimension(80,20));
    	folder3.setPreferredSize(new Dimension(80,20));
    	file3.setPreferredSize(new Dimension(80,20));
//    	landMark.setFont(new Font("Courier", Font.BOLD, 12));
//    	self3.setFont(new Font("Courier", Font.BOLD, 12));
//    	folder3.setFont(new Font("Courier", Font.BOLD, 12));
//    	file3.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	setBuilderTable();
    	builderRemove.setPreferredSize(new Dimension(100,20));
    	buiderManualLabel.setPreferredSize(new Dimension(100,20));
    	builderMClear.setPreferredSize(new Dimension(80,20));
    	builderMAdd.setPreferredSize(new Dimension(80,20));
    	builderMRemove.setPreferredSize(new Dimension(80,20));
    	
//    	builderRemove.setFont(new Font("Courier", Font.BOLD, 12));
//    	buiderManualLabel.setFont(new Font("Courier", Font.BOLD, 10));
//    	builderMClear.setFont(new Font("Courier", Font.BOLD, 12));
//    	builderMAdd.setFont(new Font("Courier", Font.BOLD, 12));
//    	builderMRemove.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	role.setPreferredSize(new Dimension(180,20));
    	combineRegion.setPreferredSize(new Dimension(180,20));
    	combineLandmark.setPreferredSize(new Dimension(180,20));
//    	role.setFont(new Font("Courier", Font.BOLD, 12));
//    	combineRegion.setFont(new Font("Courier", Font.BOLD, 12));
//    	combineLandmark.setFont(new Font("Courier", Font.BOLD, 12));
    	
    	roleJCB.setPreferredSize(new Dimension(180,20));
    	combineRegionJCB.setPreferredSize(new Dimension(180,20));
    	combineLandmarkJCB.setPreferredSize(new Dimension(180,20));
    	roleJCB.setBackground(Color.WHITE);
    	combineRegionJCB.setBackground(Color.WHITE);
    	combineLandmarkJCB.setBackground(Color.WHITE);
    	apply.setPreferredSize(new Dimension(150,20));
    	discard.setPreferredSize(new Dimension(150,20));
//    	apply.setFont(new Font("Courier", Font.BOLD, 12));
//    	discard.setFont(new Font("Courier", Font.BOLD, 12));

	}
	
	private void setBuilderTable(){
		builderTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 1)
    				return true;
    			else
    				return false;
    		}
    	};
    	builderTableModel.addColumn("");
    	builderTableModel.addColumn("");
    	builderTableModel.addColumn("Mask name");
    	builderTableModel.addColumn("Type");
    	builderTable = new JTable(builderTableModel){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0:	return String.class;
					case 1: return Boolean.class;
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
    	builderTable.setDefaultRenderer(Object.class, tcr);
    	builderTable.getColumnModel().getColumn(0).setPreferredWidth(15);
    	builderTable.getColumnModel().getColumn(1).setPreferredWidth(15);
    	builderTable.setSize(new Dimension(380,300));
    	builderTablePane = new JScrollPane(builderTable);
    	builderTablePane.setPreferredSize(new Dimension(380,303));
    	builderTablePane.setOpaque(true);
    	builderTablePane.setBackground(Color.WHITE);
    	
    	builderMap.add(new BuilderTableItem(imageDealer.avgImage1,"foreground",imageDealer));
    	builderMap.add(new BuilderTableItem(imageDealer.avgImage1,"background",imageDealer));
	}
	
	private void setTable(){
		// Table
    	tableData = new Object[5][5];
    	tableData[0] = new Object[] {new Boolean(false),"Area (um^2)",new Float(0),new Float(0)};
    	tableData[1] = new Object[] {new Boolean(false),"dF/F",new Float(0),new Float(0)};
    	tableData[2] = new Object[] {new Boolean(false),"Durations (s)",new Float(0),new Float(0)};
    	tableData[3] = new Object[] {new Boolean(false),"P value(dffMax)",new Float(0),new Float(0)};
    	tableData[4] = new Object[] {new Boolean(false),"Decay Tau",new Float(0),new Float(0)};
		String[] columnNames = {"","Feature","Min","Max"};
    	model = new DefaultTableModel(tableData,columnNames) {
			private static final long serialVersionUID = 1L;
    		@Override
    		public boolean isCellEditable(int row, int column) {
    			if(column == 0)
    				return true;
    			else
    				return true;
    		}
    	};
    	table = new JTable(model){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
					case 0: return Boolean.class;
					case 1: return String.class;
					case 2: return Float.class;
					case 3: return Float.class;
					default: return Boolean.class;
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
    	table.setDefaultRenderer(Object.class, tcr);
    	table.getColumnModel().getColumn(0).setPreferredWidth(15);
    	table.setSize(new Dimension(380,100));
    	tablePane = new JScrollPane(table);
    	tablePane.setPreferredSize(new Dimension(380,110));
    	tablePane.setOpaque(true);
    	tablePane.setBackground(Color.WHITE);
	}
	
	public void layout2() {
		leftGroup.removeAll();
		
		GridBagPut settingLeftGroup = new GridBagPut(leftGroup);
		settingLeftGroup.putGridBag(builderLeft1, leftGroup, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, leftGroup, 0, 1);
		settingLeftGroup.putGridBag(builderLeft2, leftGroup, 0, 2);

	}
	
	public void layout1() {
		leftGroup.removeAll();
		
		GridBagPut settingLeftGroup = new GridBagPut(leftGroup);
		settingLeftGroup.putGridBag(left1, leftGroup, 0, 0);
		settingLeftGroup.putGridBag(rowBlank1, leftGroup, 0, 1);
		settingLeftGroup.putGridBag(left2, leftGroup, 0, 2);
		settingLeftGroup.putGridBag(rowBlank2, leftGroup, 0, 3);
		settingLeftGroup.putGridBag(left3, leftGroup, 0, 4);
		settingLeftGroup.putGridBag(rowBlank3, leftGroup, 0, 5);
		settingLeftGroup.putGridBag(left4, leftGroup, 0, 6);

	}
	
	public void layout() {
		// left1
		left11.add(cellBoundary);
		left11.add(addLeft11);
		left11.add(removeLeft11);
		left11.add(name11);
		left11.add(save11);
		left11.add(load11);
		
		left12.add(landmark);
		left12.add(addLeft12);
		left12.add(removeLeft12);
		left12.add(name12);
		left12.add(save12);
		left12.add(load12);
		left13.add(drawAnterior);
		left13.add(maskBuilder);
		left13.add(checkROI);
		
		GridBagPut settingleft1 = new GridBagPut(left1);
		settingleft1.putGridBag(dirLabel, left1, 0, 0);
		settingleft1.putGridBag(left11, left1, 0, 1);
		settingleft1.putGridBag(left12, left1, 0, 2);
		settingleft1.putGridBag(left13, left1, 0, 3);
		left1.setBorder(BorderFactory.createEtchedBorder());
		
		// jTP1	
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP1, gbl, jTPLbaseline, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(jTP1, gbl, jTFsmo, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP1, gbl, jTPLsmo, gbc, 1, 1, 1, 1, 0, 0);    	
    	jTP1.setBorder(BorderFactory.createTitledBorder("Preprocessing"));
    	
		// jTP2
    	gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP2, gbl, jTPLthresholding, gbc, 0, 0, 2, 1, 0, 0);
		ComponentLayOut.add(jTP2, gbl, jTFthr, gbc, 0, 1, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTPLthr, gbc, 1, 1, 1, 1, 0, 0);     	
		ComponentLayOut.add(jTP2, gbl, thrSlider, gbc, 0, 2, 2, 1, 0, 0);
		ComponentLayOut.add(jTP2, gbl, jTPLfiltering, gbc, 0, 3, 2, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTFminDur, gbc, 0, 4, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTPLminDur, gbc, 1, 4, 1, 1, 0, 0);   
		ComponentLayOut.add(jTP2, gbl, jTFminsize, gbc, 0, 5, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTPLminsize, gbc, 1, 5, 1, 1, 0, 1);    
		ComponentLayOut.add(jTP2, gbl, jTFmaxSize, gbc, 0, 6, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTPLmaxSize, gbc, 1, 6, 1, 1, 0, 1);   
		ComponentLayOut.add(jTP2, gbl, jTFcircularity, gbc, 0, 7, 1, 1, 0, 0);    	
		ComponentLayOut.add(jTP2, gbl, jTPLcircularity, gbc, 1, 7, 1, 1, 0, 1);   
		
//
    	jTP2.setBorder(BorderFactory.createTitledBorder("Active regions"));
    	
    	// jTP3
    	gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP3, gbl, jTFneedTemp, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPneedTemp, gbc, 1, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPLseed, gbc, 0, 1, 2, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTFseedSize, gbc, 0, 2, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPLseedSize, gbc, 1, 2, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTFzScore, gbc, 0, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPLzScore, gbc, 1, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPLmerge, gbc, 0, 4, 2, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTFmergeDis, gbc, 0, 5, 1, 1, 0, 0);
		ComponentLayOut.add(jTP3, gbl, jTPLmergeDis, gbc, 1, 5, 1, 1, 0, 0);
		
    	jTP3.setBorder(BorderFactory.createTitledBorder("Super events and events"));
    	
    	// jTP4
    	gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP4, gbl, jTFneedSpa, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTPneedSpa, gbc, 1, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTPLspaSeg, gbc, 0, 1, 2, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTFsourceRatio, gbc, 0, 2, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTPLsourceRatio, gbc, 1, 2, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTFsensitivity, gbc, 0, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTPLsensitivity, gbc, 1, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTFwhetherExtend, gbc, 0, 4, 1, 1, 0, 0);
		ComponentLayOut.add(jTP4, gbl, jTPwhetherExtend, gbc, 1, 4, 1, 1, 0, 0);
		
    	jTP4.setBorder(BorderFactory.createTitledBorder("Events"));
    	
    	// jTP5
		gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP5, gbl, jTFdetectGlo, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP5, gbl, jTPLdetectGlo, gbc, 1, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP5, gbl, jTFgloDur, gbc, 0, 1, 1, 1, 0, 0);
		ComponentLayOut.add(jTP5, gbl, jTPLgloDur, gbc, 1, 1, 1, 1, 0, 0);
    	jTP5.setBorder(BorderFactory.createTitledBorder("Global events"));
    	
    	// jTP6
    	gbl = ComponentLayOut.iniGridBagLayout();
		gbc = ComponentLayOut.iniGridBagConstraints();
		ComponentLayOut.add(jTP6, gbl, jTFignoreTau, gbc, 0, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTPignoreTau, gbc, 1, 0, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTFcheckProp, gbc, 0, 1, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTPcheckProp, gbc, 1, 1, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTPcheckProp01, gbc, 0, 2, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTPcheckProp1, gbc, 1, 2, 1, 1, 0, 0);
//		ComponentLayOut.add(jTP6, gbl, jTPcheckProp02, gbc, 0, 3, 1, 1, 0, 0);
//		ComponentLayOut.add(jTP6, gbl, jTPcheckProp2, gbc, 1, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTFcheckNetwork, gbc, 0, 3, 1, 1, 0, 0);
		ComponentLayOut.add(jTP6, gbl, jTPcheckNetwork, gbc, 1, 3, 1, 1, 0, 0);
    	jTP6.setBorder(BorderFactory.createTitledBorder("Feature extraction"));
		
		// left2
    	
//		jTPButtons.add(blankLabel);
    	jTPButtons.add(backButton);
		jTPButtons.add(runButton);
		jTPButtons.add(nextButton);
		left2buttons.add(saveopts);
		left2buttons.add(loadopts);
		left2buttons.add(runAllButton);
		GridBagPut settingleft2 = new GridBagPut(left2);
		settingleft2.putGridBag(detLabel, left2, 0, 0);
		settingleft2.putGridBag(jTP, left2, 0, 1);
		settingleft2.putGridBag(jTPButtons, left2, 0, 2);
		settingleft2.putGridBag(left2buttons, left2, 0, 3);
		left2.setBorder(BorderFactory.createEtchedBorder());
		
		// left 3
		left3Buttons.add(viewFavourite);
		left3Buttons.add(deleteRestore);
		left3Buttons2.add(addAllFiltered);
		left3Buttons2.add(featuresPlot);
		GridBagPut settingleft3 = new GridBagPut(left3);
		settingleft3.putGridBag(proofReading, left3, 0, 0);
		settingleft3.putGridBag(left3Buttons, left3, 0, 1);
		settingleft3.putGridBag(tablePane, left3, 0, 2);
		settingleft3.putGridBag(left3Buttons2, left3, 0, 3);


    	
		left3.setBorder(BorderFactory.createEtchedBorder());
		
		// left 4
		JPanel exportPanel = new JPanel();
		exportPanel.add(exportButton);
		exportPanel.add(blank2);
		exportPanel.add(restart);
		GridBagPut settingleft4 = new GridBagPut(left4);
		settingleft4.putGridBag(export, left4, 0, 0);
		settingleft4.putGridBag(events, left4, 0, 1);
		settingleft4.putGridBag(movie, left4, 0, 2);
		settingleft4.putGridBag(exportPanel, left4, 0, 3);
		
		left4.setBorder(BorderFactory.createEtchedBorder());
		
		// Whole
		
		
		left3.setVisible(false);
		left4.setVisible(false);
		
		builderLeft11.add(region);
		builderLeft11.add(self1);
		builderLeft11.add(folder1);
		builderLeft11.add(file1);
		builderLeft12.add(regionMaker);
		builderLeft12.add(self2);
		builderLeft12.add(folder2);
		builderLeft12.add(file2);
		builderLeft13.add(landMark);
		builderLeft13.add(self3);
		builderLeft13.add(folder3);
		builderLeft13.add(file3);
		builderLeft1Buttons.add(builderRemove);
		builderManual.add(buiderManualLabel);
		builderManual.add(builderMClear);
		builderManual.add(builderMAdd);
		builderManual.add(builderMRemove);
		GridBagPut builderSettint1 = new GridBagPut(builderLeft1);
		builderSettint1.setAnchorNorthWest();
		builderSettint1.fillBoth();
		builderSettint1.putGridBag(loadMasks, builderLeft1, 0, 0);
		builderSettint1.putGridBag(builderLeft11, builderLeft1, 0, 1);
		builderSettint1.putGridBag(builderLeft12, builderLeft1, 0, 2);
		builderSettint1.putGridBag(builderLeft13, builderLeft1, 0, 3);
		builderSettint1.putGridBag(builderTablePane, builderLeft1, 0, 4);
		builderSettint1.putGridBag(builderLeft1Buttons, builderLeft1, 0, 5);
		builderSettint1.putGridBag(builderManual, builderLeft1, 0, 6);
		builderLeft1.setBorder(BorderFactory.createEtchedBorder());
		
		

		
		builderLeft21.add(role);
		builderLeft21.add(roleJCB);
		builderLeft22.add(combineRegion);
		builderLeft22.add(combineRegionJCB);
		builderLeft23.add(combineLandmark);
		builderLeft23.add(combineLandmarkJCB);
		builderLeft2Buttons.add(apply);
		builderLeft2Buttons.add(discard);
		
		GridBagPut builderSettint2 = new GridBagPut(builderLeft2);
		builderSettint2.setAnchorNorthWest();
		builderSettint2.fillBoth();
		builderSettint2.putGridBag(saveRegionsLanmarks, builderLeft2, 0, 0);
		builderSettint2.putGridBag(builderLeft21, builderLeft2, 0, 1);
		builderSettint2.putGridBag(builderLeft22, builderLeft2, 0, 2);
		builderSettint2.putGridBag(builderLeft23, builderLeft2, 0, 3);
		builderSettint2.putGridBag(builderLeft2Buttons, builderLeft2, 0, 4);
		builderLeft2.setBorder(BorderFactory.createEtchedBorder());
	}
	
		
	public void addButtonListeners() {
//		ArrayList<ArrayList<Point>> list1 = imageLabel.getList1();
		drawlistener = new DrawListener(imageLabel,addLeft11,imageDealer,imageDealer.regionMark);
		Color color1 = new Color(0,191,255);
		drawlistener.setColor(color1);
		imageLabel.addMouseListener(drawlistener);
		imageLabel.addMouseMotionListener(drawlistener);
		addLeft11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(addLeft11.isSelected()) {
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					drawlistener.setValid(true);
					imageLabel.setValid1(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawlistener.setValid(false);
					drawlistener.clearPoints();
					imageLabel.setValid1(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//					changeRegionStatus(imageDealer.regionMark,list1);
					imageLabel.repaint();
					dealBuilderRegion();
				}
			}
			
		});		
		
//		ArrayList<ArrayList<Point>> list2 = imageLabel.getList2();
		drawlistener2 = new DrawListener(imageLabel,addLeft12,imageDealer,imageDealer.landMark);
		Color color2 = new Color(255,185,15);
		drawlistener2.setColor(color2);
		imageLabel.addMouseListener(drawlistener2);
		imageLabel.addMouseMotionListener(drawlistener2);
		addLeft12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(addLeft12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					drawlistener2.setValid(true);
					imageLabel.setValid2(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawlistener2.setValid(false);
					imageLabel.setValid2(false);
					drawlistener2.clearPoints();
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					dealBuilderLandMark();
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});	
		
		removeListener = new RemoveListener(imageLabel, imageDealer,imageDealer.regionMark, imageDealer.regionMarkLabel);
		imageLabel.addMouseListener(removeListener);
		removeLeft11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(removeLeft11.isSelected()) {
					addLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					removeListener.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					removeListener.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.regionMark,list1);
				}
			}
			
		});		
			
		
		removeListener2 = new RemoveListener(imageLabel, imageDealer, imageDealer.landMark,imageDealer.landMarkLabel);
		imageLabel.addMouseListener(removeListener2);
		removeLeft12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(removeLeft12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					name12.setSelected(false);
					removeListener2.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					removeListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		nameListenerRegion = new NameListenerRegion(imageLabel, imageDealer, imageDealer.regionMarkLabel);
		imageLabel.addMouseListener(nameListenerRegion);
		name11.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(name11.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					name12.setSelected(false);
					nameListenerRegion.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					nameListenerRegion.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		nameListenerLandMark = new NameListenerLandMark(imageLabel, imageDealer, imageDealer.landMarkLabel);
		imageLabel.addMouseListener(nameListenerLandMark);
		name12.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
				if(name12.isSelected()) {
					addLeft11.setSelected(false);
					removeLeft11.setSelected(false);
					name11.setSelected(false);
					addLeft12.setSelected(false);
					removeLeft12.setSelected(false);
					nameListenerLandMark.setValid(true);
					imageDealer.center.pauseButton.doClick();
					imageDealer.center.panButton.setSelected(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					nameListenerLandMark.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageLabel.repaint();
//					changeRegionStatus(imageDealer.landMark,list2);
				}
			}
			
		});		
		
		
		jTP.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				curStatus = jTP.getSelectedIndex();
				
				if(curStatus==jTPStatus || curStatus==6)
					nextButton.setEnabled(false);
				else
					nextButton.setEnabled(true);
				
				if(curStatus==0)
					backButton.setEnabled(false);
				else
					backButton.setEnabled(true);
				
				if(curStatus == 5) {
					runButton.setText("Extract");
					nextButton.setText("CFU detect");
				}else {
					runButton.setText("Run");
					nextButton.setText("Next");
				}
				
				if(curStatus == 6) {
					nextButton.setText("CFU detect");
					backButton.setEnabled(true);
					left3.setVisible(true);
					left4.setVisible(true);
					imageDealer.right.allFinished();
					try {
						float[] ftsTable = new float[1];
						ftsTable = Helper.readObjectFromFile(imageDealer.proPath, "FtsTableParameters.ser", ftsTable.getClass());
						imageDealer.left.tableValueSetting(ftsTable[0],ftsTable[1],ftsTable[2],ftsTable[3],ftsTable[4],ftsTable[5],ftsTable[6],ftsTable[7],ftsTable[8],ftsTable[9]);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		
		maskBuilder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				layout2();
//				imageDealer.center.layout2();
//				imageDealer.right.layout2();
//				imageDealer.window.revalidate();
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout2();
						imageDealer.center.layout2();
						imageDealer.right.layout2();
						imageDealer.window.revalidate();
					}
					
				}).start();
			}
		});
		
		discard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				layout1();
//				imageDealer.center.layout1();
//				imageDealer.right.layout1();
//				imageDealer.window.revalidate();
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout1();
						imageDealer.center.layout1();
						imageDealer.right.layout1();
						imageDealer.window.revalidate();
						imageDealer.dealImage();
					}
					
				}).start();
			}
		});
		
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dealBuilder();
				new Thread(new Runnable() {

					@Override
					public void run() {
						layout1();
						imageDealer.center.layout1();
						imageDealer.right.layout1();
						dealBuilder();
						imageDealer.window.revalidate();
						imageDealer.dealImage();
					}
					
				}).start();
				
				
			}
		});
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(curStatus) {
					case 0:
						float sigma = Float.parseFloat(jTFsmo.getText());
						imageDealer.setStep1(sigma);
						imageDealer.step1Start();
						break;
					case 1:
						float thrArscl = Float.parseFloat(jTFthr.getText());
						int minDur = Integer.parseInt(jTFminDur.getText());
						int minSize = Integer.parseInt(jTFminsize.getText());
						int maxSize = Integer.MAX_VALUE;
						if (!jTFmaxSize.getText().equalsIgnoreCase("inf"))
							maxSize = Integer.parseInt(jTFmaxSize.getText());
						float circularity = Float.parseFloat(jTFcircularity.getText());
						
						imageDealer.setStep2(thrArscl, minDur, minSize, maxSize, circularity);
						imageDealer.step2Start();
						break;
					case 2:
						boolean needTemp = jTFneedTemp.isSelected();
						float seedSzRatio = Float.parseFloat(jTFseedSize.getText());
						float sigThr = Float.parseFloat(jTFzScore.getText());
						float maxDelay = Float.parseFloat(jTFmergeDis.getText());
						imageDealer.setStep3(needTemp, seedSzRatio, sigThr, maxDelay);
						imageDealer.step3Start();
						break;
					case 3:
						boolean needSpa = jTFneedSpa.isSelected();
						float sourceSzRatio = Float.parseFloat(jTFsourceRatio.getText());
						int sourceSensitivity = Integer.parseInt(jTFsensitivity.getText());
						boolean whetherExtend = jTFwhetherExtend.isSelected();
						imageDealer.setStep4(needSpa, sourceSzRatio, sourceSensitivity,whetherExtend);
						imageDealer.step4Start();
						break;
					case 4:
						boolean detectGlo = jTFdetectGlo.isSelected();
						int gloDur = Integer.parseInt(jTFgloDur.getText());
						imageDealer.setStep5(detectGlo, gloDur);
						imageDealer.step5Start();
						break;
					case 5:
						boolean ignoreTau = jTFignoreTau.isSelected();
						boolean checkProp = jTFcheckProp.isSelected();
						boolean checkNetwork = jTFcheckNetwork.isSelected();
					
						imageDealer.setStep6(ignoreTau, checkProp, checkNetwork, false);
						imageDealer.step6Start();
						break;
//					case 6:
//						boolean isChecked = jTF71.isSelected();
//						imageDealer.setStep7(isChecked);
//						imageDealer.step7Start();
//						break;
				}	
			}
		});
		
		
		checkROIListener roiListener = new checkROIListener(imageDealer,imageDealer.imageLabel);
		checkROIListener roiListener1 = new checkROIListener(imageDealer,imageDealer.center.leftImageLabel);
		checkROIListener roiListener2 = new checkROIListener(imageDealer,imageDealer.center.rightImageLabel);
		imageLabel.addMouseListener(roiListener);
		imageDealer.center.leftImageLabel.addMouseListener(roiListener1);
		imageDealer.center.rightImageLabel.addMouseListener(roiListener2);
		imageLabel.addMouseMotionListener(roiListener);
		imageDealer.center.leftImageLabel.addMouseMotionListener(roiListener1);
		imageDealer.center.rightImageLabel.addMouseMotionListener(roiListener2);
		checkROI.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(checkROI.isSelected()) {
					viewFavourite.setSelected(false);
					deleteRestore.setSelected(false);
					roiListener.setValid(true);
					roiListener1.setValid(true);
					roiListener2.setValid(true);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					roiListener.setValid(false);
					roiListener1.setValid(false);
					roiListener2.setValid(false);
					imageDealer.drawROI = false;
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (curStatus == 5) {
					CFUGUI cfuGui = new CFUGUI();
					cfuGui.start(imageDealer);
				} else {
					curStatus++;
					jTP.setSelectedIndex(curStatus);
				}
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				curStatus--;
				jTP.setSelectedIndex(curStatus);
			}
		});
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean eventsExtract = events.isSelected();
				boolean movieExtract = movie.isSelected();
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					imageDealer.export(eventsExtract,movieExtract,savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		saveopts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    if (!savePath.endsWith(".ser"))
				    	savePath = savePath + ".ser";
					imageDealer.exportOpts(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		loadopts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
//				chooser.setDialogTitle("Select Option File Path");
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					imageDealer.loadOpts(savePath);
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		save11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    String outputPath = null;
				    if(savePath.substring(savePath.length()-4).equals(".ser"))
						outputPath = savePath;
					else
						outputPath = savePath + ".ser";
				    try {
						FileOutputStream f = null;
						ObjectOutputStream o = null;
						
						f = new FileOutputStream(new File(outputPath));
						o = new ObjectOutputStream(f);
						o.writeObject(imageDealer.regionMark);
						System.out.println("Save Region");
						o.close();
						f.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		load11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				boolean[][] regionMark = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    try {
				    	FileInputStream fi = null;
						ObjectInputStream oi = null;
						
						fi = new FileInputStream(new File(savePath));	
						oi = new ObjectInputStream(fi);
						regionMark = (boolean[][])oi.readObject();
						System.out.println("Load Region");
						oi.close();
						fi.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				    int width = regionMark.length;
				    int height = regionMark[0].length;
				    boolean[][] region = imageDealer.regionMark;
				    for(int x=0;x<width;x++) {
				    	for(int y=0;y<height;y++) {
				    		region[x][y] = regionMark[x][y];
				    	}
				    }
					imageLabel.repaint();
					dealBuilderRegion();
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		save12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    String outputPath = null;
				    if(savePath.substring(savePath.length()-4).equals(".ser"))
						outputPath = savePath;
					else
						outputPath = savePath + ".ser";
				    try {
						FileOutputStream f = null;
						ObjectOutputStream o = null;
						
						f = new FileOutputStream(new File(outputPath));
						o = new ObjectOutputStream(f);
						o.writeObject(imageDealer.landMark);
						System.out.println("Save LandMark");
						o.close();
						f.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
		load12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ser","ser");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed(false);
				boolean[][] regionMark = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				    try {
				    	FileInputStream fi = null;
						ObjectInputStream oi = null;
						
						fi = new FileInputStream(new File(savePath));	
						oi = new ObjectInputStream(fi);
						regionMark = (boolean[][])oi.readObject();
						System.out.println("Load LandMark");
						oi.close();
						fi.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				    int width = regionMark.length;
				    int height = regionMark[0].length;
				    boolean[][] region = imageDealer.landMark;
				    for(int x=0;x<width;x++) {
				    	for(int y=0;y<height;y++) {
				    		region[x][y] = regionMark[x][y];
				    	}
				    }
					imageLabel.repaint();
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
			}
		});
		
			
		
		runAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				float sigma = Float.parseFloat(jTFsmo.getText());
				
				imageDealer.setStep1(sigma);
				
				float thrArscl = Float.parseFloat(jTFthr.getText());
				int minDur = Integer.parseInt(jTFminDur.getText());
				int minSize = Integer.parseInt(jTFminsize.getText());
				int maxSize = Integer.MAX_VALUE;
				if (!jTFmaxSize.getText().equalsIgnoreCase("inf"))
					maxSize = Integer.parseInt(jTFmaxSize.getText());
				float circularity = Float.parseFloat(jTFcircularity.getText());
				
				imageDealer.setStep2(thrArscl, minDur, minSize, maxSize, circularity);
				
				boolean needTemp = jTFneedTemp.isSelected();
				float seedSzRatio = Float.parseFloat(jTFseedSize.getText());
				float sigThr = Float.parseFloat(jTFzScore.getText());
				float maxDelay = Float.parseFloat(jTFmergeDis.getText());
				imageDealer.setStep3(needTemp, seedSzRatio, sigThr, maxDelay);
				
				boolean needSpa = jTFneedSpa.isSelected();
				float sourceSzRatio = Float.parseFloat(jTFsourceRatio.getText());
				int sourceSensitivity = Integer.parseInt(jTFsensitivity.getText());
				boolean whetherExtend = jTFwhetherExtend.isSelected();
				imageDealer.setStep4(needSpa, sourceSzRatio, sourceSensitivity,whetherExtend);
				
				boolean detectGlo = jTFdetectGlo.isSelected();
				int gloDur = Integer.parseInt(jTFgloDur.getText());
				imageDealer.setStep5(detectGlo, gloDur);

				boolean ignoreTau = jTFignoreTau.isSelected();
				boolean checkProp = jTFcheckProp.isSelected();
				boolean checkNetwork = jTFcheckNetwork.isSelected();
			
				imageDealer.setStep6(ignoreTau, checkProp, checkNetwork, false);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						jTP.setSelectedIndex(0);
						boolean startRun = true;
						while(true) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(!imageDealer.running) {
//								System.out.println("run");						
								try {
									if(startRun) {
										runButton.doClick();
										Thread.sleep(200);
										startRun = false;
									}else {
										nextButton.doClick();
										Thread.sleep(200);
										runButton.doClick();
										Thread.sleep(200);
									}
									
									if(curStatus==5)
										break;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//								System.out.println("next");
								
								
//								Thread.sleep(100);
								
							}
						}
					}
				}).start();
				
//				imageDealer.runAllSteps();
				
			}
		});
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO:
				imageDealer.window.dispose();
				AQuAWelcome begin = new AQuAWelcome();
		    	try {
					begin.run();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		ViewFeatureListener viewListener = new ViewFeatureListener(imageDealer,imageDealer.imageLabel);
		ViewFeatureListener viewListener1 = new ViewFeatureListener(imageDealer,imageDealer.center.leftImageLabel);
		ViewFeatureListener viewListener2 = new ViewFeatureListener(imageDealer,imageDealer.center.rightImageLabel);
		imageLabel.addMouseListener(viewListener);
		imageDealer.center.leftImageLabel.addMouseListener(viewListener1);
		imageDealer.center.rightImageLabel.addMouseListener(viewListener2);
		viewFavourite.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(viewFavourite.isSelected()) {
					deleteRestore.setSelected(false);
					checkROI.setSelected(false);
					viewListener.setValid(true);
					viewListener1.setValid(true);
					viewListener2.setValid(true);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					viewListener.setValid(false);
					viewListener1.setValid(false);
					viewListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		
		DeleteButtonListener deleteListener = new DeleteButtonListener(imageDealer,imageDealer.imageLabel);
		DeleteButtonListener deleteListener1 = new DeleteButtonListener(imageDealer,imageDealer.center.leftImageLabel);
		DeleteButtonListener deleteListener2 = new DeleteButtonListener(imageDealer,imageDealer.center.rightImageLabel);
		imageLabel.addMouseListener(deleteListener);
		imageDealer.center.leftImageLabel.addMouseListener(deleteListener1);
		imageDealer.center.rightImageLabel.addMouseListener(deleteListener2);
		deleteRestore.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(deleteRestore.isSelected()) {
					viewFavourite.setSelected(false);
					checkROI.setSelected(false);
					deleteListener.setValid(true);
					deleteListener1.setValid(true);
					deleteListener2.setValid(true);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else {
					deleteListener.setValid(false);
					deleteListener1.setValid(false);
					deleteListener2.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.leftImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.center.rightImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				}
			}
			
		});
		
		DrawAnteriorListener drawAnteriorListener = new DrawAnteriorListener(imageLabel,imageDealer);
		imageLabel.addMouseListener(drawAnteriorListener);
		imageLabel.addMouseMotionListener(drawAnteriorListener);
		drawAnterior.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(drawAnterior.isSelected()) {
					drawAnteriorListener.setValid(true);

					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					drawAnteriorListener.setValid(false);
					imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		
		
		self1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage1,"region", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		self2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage1,"region mark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		self3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BuilderTableItem item = new BuilderTableItem(imageDealer.avgImage1,"landmark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"region", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"region mark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		file3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose file (same size)");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				BuilderTableItem item = new BuilderTableItem(savePath,"landmark", imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
		});
		
		folder1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"region",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		folder2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"region mark",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		folder3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				String savePath = null;
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				File folder = new File(savePath);
				File[] listOfFiles = folder.listFiles();
				BuilderTableItem item = new BuilderTableItem(savePath,listOfFiles,"landmark",(int)imageDealer.getOrigWidth(),(int)imageDealer.getOrigHeight(), imageDealer);
				imageDealer.builderImageLabel.getComponentBorder(item.region);
				builderMap.add(item);
			}
			
		});
		
		
		builderTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int r = builderTable.getSelectedRow();
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					builderTableModel.setValueAt(new Boolean(false), i, 1);
				}
				builderTableModel.setValueAt(new Boolean(true), r, 1);
				imageDealer.curBuilderImage1 = builderMap.get(r).image;
				imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				imageDealer.dealBuilderImageLabel();
//				imageDealer.right.intensitySlider.setValue(intensityThreshold.get(r));
//				imageDealer.right.sizeMinSlider.setValue(minSize.get(r));
//				imageDealer.right.sizeMaxSlider.setValue(maxSize.get(r));
				
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int nEvt;
				nEvt = imageDealer.fts1.basic.area.size();
				imageDealer.deleteColorSet2 = new HashSet<>();
				if((boolean) table.getValueAt(0, 0)) {
					float min = (float) table.getValueAt(0, 2);
					float max = (float) table.getValueAt(0, 3);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts1.basic.area.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(1, 0)) {
					float min = (float) table.getValueAt(1, 2);
					float max = (float) table.getValueAt(1, 3);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts1.curve.dffMax.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(2, 0)) {
					float min = (float) table.getValueAt(2, 2);
					float max = (float) table.getValueAt(2, 3);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts1.curve.duration.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(3, 0)) {
					float min = (float) table.getValueAt(3, 2);
					float max = (float) table.getValueAt(3, 3);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts1.curve.dffMaxPval.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				if((boolean) table.getValueAt(4, 0)) {
					float min = (float) table.getValueAt(4, 2);
					float max = (float) table.getValueAt(4, 3);
					for(int i=1;i<=nEvt;i++) {
						float value = imageDealer.fts1.curve.decayTau.get(i);
						if(value<min || value > max) {
							imageDealer.deleteColorSet2.add(i);
						}
					}
				}
				
				if (!imageDealer.opts.singleChannel) {
					nEvt = imageDealer.fts2.basic.area.size();
					if((boolean) table.getValueAt(0, 0)) {
						float min = (float) table.getValueAt(0, 2);
						float max = (float) table.getValueAt(0, 3);
						for(int i=1;i<=nEvt;i++) {
							float value = imageDealer.fts2.basic.area.get(i);
							if(value<min || value > max) {
								imageDealer.deleteColorSet2.add(i + imageDealer.nEvtCh1);
							}
						}
					}
					if((boolean) table.getValueAt(1, 0)) {
						float min = (float) table.getValueAt(1, 2);
						float max = (float) table.getValueAt(1, 3);
						for(int i=1;i<=nEvt;i++) {
							float value = imageDealer.fts2.curve.dffMax.get(i);
							if(value<min || value > max) {
								imageDealer.deleteColorSet2.add(i + imageDealer.nEvtCh1);
							}
						}
					}
					if((boolean) table.getValueAt(2, 0)) {
						float min = (float) table.getValueAt(2, 2);
						float max = (float) table.getValueAt(2, 3);
						for(int i=1;i<=nEvt;i++) {
							float value = imageDealer.fts2.curve.duration.get(i);
							if(value<min || value > max) {
								imageDealer.deleteColorSet2.add(i + imageDealer.nEvtCh1);
							}
						}
					}
					if((boolean) table.getValueAt(3, 0)) {
						float min = (float) table.getValueAt(3, 2);
						float max = (float) table.getValueAt(3, 3);
						for(int i=1;i<=nEvt;i++) {
							float value = imageDealer.fts2.curve.dffMaxPval.get(i);
							if(value<min || value > max) {
								imageDealer.deleteColorSet2.add(i + imageDealer.nEvtCh1);
							}
						}
					}
					if((boolean) table.getValueAt(4, 0)) {
						float min = (float) table.getValueAt(4, 2);
						float max = (float) table.getValueAt(4, 3);
						for(int i=1;i<=nEvt;i++) {
							float value = imageDealer.fts2.curve.decayTau.get(i);
							if(value<min || value > max) {
								imageDealer.deleteColorSet2.add(i + imageDealer.nEvtCh1);
							}
						}
					}
				}
				imageDealer.dealImage();
			}
		});
		
		builderRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				builderTableModel.removeRow(r);
				builderMap.remove(r);
				intensityThreshold.remove(r);
				minSize.remove(r);
				maxSize.remove(r);
				
				for(int i=0;i<rNumber-1;i++) {
					builderTableModel.setValueAt(new Integer(i+1), i, 0);
				}
			}
		});
		
		builderMClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				int width = region.length;
				int height = region[0].length;
				builderMap.get(r).region = new boolean[width][height];
				imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				imageDealer.dealBuilderImageLabel();
			}
		});
		
		builderDrawListener = new BuilderDrawListener(imageDealer.builderImageLabel,builderMAdd,imageDealer);
		drawlistener.setColor(color1);
		imageDealer.builderImageLabel.addMouseListener(builderDrawListener);
		imageDealer.builderImageLabel.addMouseMotionListener(builderDrawListener);
		builderMAdd.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				builderDrawListener.setRegion(region);
				// TODO Auto-generated method stub
				if(builderMAdd.isSelected()) {
					builderMRemove.setSelected(false);
					builderDrawListener.setValid(true);
					imageDealer.builderImageLabel.setValid1(true);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					builderDrawListener.setValid(false);
					builderDrawListener.clearPoints();
					imageDealer.builderImageLabel.setValid1(false);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					builderMap.get(r).region = region;
					imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
				}
			}
			
		});		
		
		builderRemoveListener = new BuilderRemoveListener(imageDealer.builderImageLabel);
		imageDealer.builderImageLabel.addMouseListener(builderRemoveListener);
		builderMRemove.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int r = -1;
				int rNumber = builderTable.getRowCount();
				for(int i=0;i<rNumber;i++) {
					if((boolean) builderTableModel.getValueAt(i, 1))
						r = i;
				}
				
				if(r==-1)
					return;
				
				boolean[][] region = builderMap.get(r).region;
				builderRemoveListener.setRegion(region);
				// TODO Auto-generated method stub
				if(builderMRemove.isSelected()) {
					builderMAdd.setSelected(false);
					builderRemoveListener.setValid(true);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				}else {
					builderRemoveListener.setValid(false);
					imageDealer.builderImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					imageDealer.builderImageLabel.repaint();
					builderMap.get(r).region = region;
					imageDealer.builderImageLabel.getComponentBorder(builderMap.get(r).region);
//					changeRegionStatus(imageDealer.regionMark,list1);
				}
			}
			
		});
		
		
		addAllFiltered.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int nEvt = imageDealer.fts1.basic.area.size();
				for(int i=1;i<=nEvt;i++) {
					if((imageDealer.deleteColorSet2.contains(i))|| (imageDealer.featureTableList1.contains(i)))
						continue;
					
					imageDealer.featureTableList1.add(i);
					int frame = imageDealer.fts1.curve.tBegin.get(i);
					float size = imageDealer.fts1.basic.area.get(i);
					float duration = imageDealer.fts1.curve.duration.get(i);
					float dffMax = imageDealer.fts1.curve.dffMax.get(i);
					float tau = imageDealer.fts1.curve.decayTau.get(i); 
					imageDealer.right.model.addRow(new Object[] {new Boolean(false),new Integer(1),new Integer(i),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});

				}
				if (!imageDealer.opts.singleChannel) {
					nEvt = imageDealer.fts2.basic.area.size();
					for(int i=1;i<=nEvt;i++) {
						if((imageDealer.deleteColorSet2.contains(i + imageDealer.nEvtCh1)|| (imageDealer.featureTableList2.contains(i))))
							continue;
						
						imageDealer.featureTableList2.add(i);
						int frame = imageDealer.fts2.curve.tBegin.get(i);
						float size = imageDealer.fts2.basic.area.get(i);
						float duration = imageDealer.fts2.curve.duration.get(i);
						float dffMax = imageDealer.fts2.curve.dffMax.get(i);
						float tau = imageDealer.fts2.curve.decayTau.get(i); 
						imageDealer.right.model.addRow(new Object[] {new Boolean(false),new Integer(2),new Integer(i),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});

					}
				}
				imageDealer.dealImage();
			}
			
		});
		
		featuresPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DrawFeatures draw = new DrawFeatures(imageDealer);
			}
			
		});		
		
		thrSlider.setMinorTickSpacing(1);
		thrSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt){
				if(!sliderChanging && !thrSlider.getValueIsAdjusting()){
					float thr = (float) thrSlider.getValue() / 10;
					txtChanging = true;
					jTFthr.setText("" + thr);
					txtChanging = false;
				}
				if (imageDealer.opts.singleChannel) {
					if (imageDealer.center.rightJCB.getSelectedIndex() != 6) {
						imageDealer.center.rightJCB.setSelectedIndex(6);
					}
				} else {
					if (imageDealer.center.leftJCB.getSelectedIndex() != 6) {
						imageDealer.center.leftJCB.setSelectedIndex(6);
					}
					if (imageDealer.center.rightJCB.getSelectedIndex() != 6) {
						imageDealer.center.rightJCB.setSelectedIndex(6);
					}
				}
				imageDealer.dealImage();
			}
		});
		
		
		jTFthr.getDocument().addDocumentListener(new DocumentListener() {
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
            		int value = (int) (Float.parseFloat(jTFthr.getText()) * 10);
                	sliderChanging = true;
                	thrSlider.setValue(value);
                	sliderChanging = false;
            	}	
            	if (imageDealer.opts.singleChannel) {
					if (imageDealer.center.rightJCB.getSelectedIndex() != 6) {
						imageDealer.center.rightJCB.setSelectedIndex(6);
					}
				} else {
					if (imageDealer.center.leftJCB.getSelectedIndex() != 6) {
						imageDealer.center.leftJCB.setSelectedIndex(6);
					}
					if (imageDealer.center.rightJCB.getSelectedIndex() != 6) {
						imageDealer.center.rightJCB.setSelectedIndex(6);
					}
				}
            	imageDealer.dealImage();
            }

			
        });
	}
	
//	public void changeRegionStatus(boolean[][] region, ArrayList<ArrayList<Point>> list) {
//		for(ArrayList<Point> points:list) {
//			int minX = Integer.MAX_VALUE;
//			int minY = Integer.MAX_VALUE;
//			int maxX = Integer.MIN_VALUE;
//			int maxY = Integer.MIN_VALUE;
//			for(Point p:points) {
//				minX = (int) Math.min(minX, p.getX());
//				minY = (int) Math.min(minY, p.getY());
//				maxX = (int) Math.max(maxX, p.getX());
//				maxY = (int) Math.max(maxY, p.getY());
//			}
//			
//			for(int i=minX;i<=maxX;i++) {
//				for(int j=minY;j<=maxY;j++) {
//					if(!region[i][j]&&judgePointInPolygon(i,j,points))
//						region[i][j] = true;
//				}
//			}
//		}
//		
//		HashMap<Integer, ArrayList<Integer>> regionCC = ConnectedComponents.twoPassConnect2D_ForBuilder(region,4);
//		
//		list = new ArrayList<>();
//		
//		imageLabel.repaint();
//	}
	
	public boolean judgePointInPolygon(int xx, int yy, ArrayList<Point> list) {
		boolean result = false;
		
		int number = list.size();
		double px = xx;
		double py = yy;
		
		for(int i = 0,j = number-1;i<number;j=i,i++) {
			double sx = list.get(i).getX();
			double sy = list.get(i).getY();
			double tx = list.get(j).getX();
			double ty = list.get(j).getY();
			
			if((sx == px && sy == py)||(tx==px && ty == py)) {
				return true;
			}
			
			if((sy < py && ty >= py) || (sy >= py && ty < py)) {
				double x = sx + (py - sy) * (tx - sx) / (ty - sy);
				if(x == px)
					return true;
				if(x > px)
					result = !result;
			}
		}	
		return result;	
	}
	
	public JPanel createPanel() {
		setting();
		layout();
		layout1();
		
		addButtonListeners();
		
		// Test

//		jTPStatus = 6;
////		nextButton.setEnabled(true);
//		Random rv = new Random();
//		imageDealer.changeSignalDrawRegionStatus();
//		imageDealer.labelColors = new Color[40000];
//		for(int i=0;i<imageDealer.labelColors.length;i++) {
//			imageDealer.labelColors[i] = new Color(rv.nextInt(256),rv.nextInt(256),rv.nextInt(256));
//		}
//		jTP.setEnabledAt(6, true);
//		imageDealer.left.left3.setVisible(true);
//		imageDealer.left.left4.setVisible(true);
		 
		return leftGroup;
	}

	public void tableValueSetting(float minArea, float maxArea, float minPvalue, float maxPvalue, float minDecayTau,
			float maxDecayTau, float minDuration, float maxDuration, float mindffMax, float maxdffMax) {
		table.setValueAt(new Float(minArea), 0, 2);
		table.setValueAt(new Float(maxArea), 0, 3);
		table.setValueAt(new Float(mindffMax), 1, 2);
		table.setValueAt(new Float(maxdffMax), 1, 3);
		table.setValueAt(new Float(minDuration), 2, 2);
		table.setValueAt(new Float(maxDuration), 2, 3);
		table.setValueAt(new Float(minPvalue), 3, 2);
		table.setValueAt(new Float(maxPvalue), 3, 3);
		table.setValueAt(new Float(minDecayTau), 4, 2);
		table.setValueAt(new Float(maxDecayTau), 4, 3);
		
	}
	
	public void dealBuilder() {
		dealBuilderRegion();
		dealBuilderLandMark();
		
	}

	private void dealBuilderRegion() {
		boolean[][] region = imageDealer.regionMark;
//		System.out.println(ConnectedComponents.twoPassConnect2D_ForBuilder(region,4).size());
		int width = region.length;
		int height = region[0].length;
		int status = combineRegionJCB.getSelectedIndex();
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("region")) 
				continue;	
			boolean[][] curRegion = item.region;
					//getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
			if(status==0) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						region[x][y] |= curRegion[x][y];
					}
				}
			}
			if(status==1) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						region[x][y] &= curRegion[x][y];
					}
				}
			}
		}
		
		int[][] regionLabel = imageDealer.regionMarkLabel;
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				regionLabel[x][y] = 0;
			}
		}
		HashMap<Integer, ArrayList<Integer>> regionCC = Helper.twoPassConnect2D_ForBuilder(region,4);
		
		int changeParameter = Math.max(width, height);
		
		for(Entry<Integer, ArrayList<Integer>> entry:regionCC.entrySet()) {
			int label = entry.getKey();
			ArrayList<Integer> points = entry.getValue();
			for(int xy:points) {
				int x = xy/changeParameter;
				int y = xy%changeParameter;
				regionLabel[x][y] = label;
//				if(label!=0)
//					System.out.println(label);
			}
		}
		
		boolean[][] regionMark = getRegionMarker();
		
		if(regionMark!=null) {
			
			HashMap<Integer, ArrayList<Integer>> marker = Helper.twoPassConnect2D_ForBuilder(regionMark);
			int opertaion = roleJCB.getSelectedIndex();
			
			int cnt = regionCC.size();
			for(int i=1;i<=regionCC.size();i++) {
				ArrayList<Integer> curRegion = regionCC.get(i);
				ArrayList<Integer> interLabel = new ArrayList<>();
				for(int j=1;j<=marker.size();j++) {
					ArrayList<Integer> interSection = new ArrayList<>(marker.get(j));
					interSection.retainAll(curRegion);
					if(interSection.size()>0)
						interLabel.add(j);
				}
				
				if(opertaion==1 && interLabel.size()>0) {
					for(int xy:curRegion) {
						int x = xy/changeParameter;
						int y = xy%changeParameter;
						regionLabel[x][y] = 0;
						region[x][y] = false;
					}
				}
				
				if(opertaion==0 && interLabel.size()>1) {
					for(int xy:curRegion) {
						int x = xy/changeParameter;
						int y = xy%changeParameter;
						float distance = Float.MAX_VALUE;
						int curLabel = 0;
						for(int j=0;j<interLabel.size();j++) {
							int label = interLabel.get(j);
							for(int xy2:marker.get(label)) {
								int x2 = xy2/changeParameter;
								int y2 = xy2%changeParameter;
								if((x-x2)*(x-x2) + (y-y2)*(y-y2)<distance) {
									distance = (x-x2)*(x-x2) + (y-y2)*(y-y2);
									curLabel = cnt + j;
								}
							}
						}
						
						if(curLabel!=cnt)
							regionLabel[x][y] = curLabel;
					}
					cnt += interLabel.size()-1;
				}
			}
		}
		
		
		HashMap<Integer,String> nameLst = new HashMap<>();
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				int label = regionLabel[x][y];
				if(label>0 && !nameLst.containsKey(label)) {
					nameLst.put(label, label+"");
				}
			}
		}
		imageDealer.nameLst = nameLst;
		
	}
	
	private boolean[][] getRegionMarker() {
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("region mark")) 
				continue;
			
			return getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
		}
//		System.out.println("No region marker");
		return null;
	}
	
	private void dealBuilderLandMark() {
		boolean[][] landMark = imageDealer.landMark;
		int width = landMark.length;
		int height = landMark[0].length;
		int status = combineLandmarkJCB.getSelectedIndex();
		for(int i=0;i<builderMap.size();i++) {
			BuilderTableItem item = builderMap.get(i);
			if(!item.type.equals("landmark")) 
				continue;
			
			boolean[][] curRegion = getRegion(item.image,minSize.get(i),maxSize.get(i),intensityThreshold.get(i));
			if(status==0) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						landMark[x][y] |= curRegion[x][y];
					}
				}
			}
			if(status==1) {
				for(int x=0;x<width;x++) {
					for(int y=0;y<height;y++) {
						landMark[x][y] &= curRegion[x][y];
					}
				}
			}
		}
		
		int[][] landMarkLabel = imageDealer.landMarkLabel;
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				landMarkLabel[x][y] = 0;
			}
		}
		HashMap<Integer, ArrayList<Integer>> regionCC = Helper.twoPassConnect2D_ForBuilder(landMark,4);
		HashMap<Integer,String> nameLst = new HashMap<>();
		int changeParameter = Math.max(width, height);
		for(Entry<Integer, ArrayList<Integer>> entry:regionCC.entrySet()) {
			int label = entry.getKey();
			ArrayList<Integer> points = entry.getValue();
			for(int xy:points) {
				int x = xy/changeParameter;
				int y = xy%changeParameter;
				landMarkLabel[x][y] = label;
			}
			nameLst.put(label, label+"");
		}
		imageDealer.nameLstLandMark = nameLst;
		
		
	}
	
	public boolean[][] getRegion(float[][] curImage, int minSize, int maxSize, float threshold) {
		int width = curImage.length;
		int height = curImage[0].length;
		boolean[][] thresholdMap = new boolean[width][height];
		threshold = (float) Math.sqrt(threshold/imageDealer.opts.maxValueDat);
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++)
				if(curImage[i][j]>=threshold)
					thresholdMap[i][j] = true;
		}
		
		HashMap<Integer, ArrayList<int[]>> connectedMap = new HashMap<>();
		Helper.bfsConn2D(thresholdMap, connectedMap);
		
		boolean[][] result = new boolean[width][height];
		
		int max = imageDealer.right.sizeMaxSlider.getMaximum();
		int bit = (int) (Math.log10(max)/Math.log10(2))+1;
		
		minSize = (int) Math.pow(2,((double)minSize*bit)/max);
		maxSize = (int) Math.pow(2,((double)maxSize*bit)/max);
		
		for(Entry<Integer, ArrayList<int[]>> entry:connectedMap.entrySet()) {
			ArrayList<int[]> points = entry.getValue();
			
			if(points.size()<minSize || points.size()>maxSize)
				continue;
			
			for(int[] p:points) {
				result[p[0]][p[1]] = true;
			}
		}
		return result;
	}
}
