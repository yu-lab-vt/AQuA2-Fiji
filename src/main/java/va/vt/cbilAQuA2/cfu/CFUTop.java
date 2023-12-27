package va.vt.cbilAQuA2.cfu;

import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.ui.GridBagPut;

/**
 * The first step of the whole software, preprocessing
 * registration, photobleaching correct, and calculate dF
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class CFUTop extends SwingWorker<HashMap<Integer, CFUInfo>, Integer>{
//  public class Step1_a extends SwingWorker<float[][][], Float>{
//	claim variables
	JFrame frame = new JFrame("Step1");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	ImageDealer imageDealer = null;
	ImagePlus imgPlus = null;
	int W, H, T = 0; 
	Opts opts = null;
	float[][][] dat = null;
	boolean[][] evtSpatialMask = null;
	String proPath = null;
	float[][] stdMap1 = null;
	float[][] stdMap2 = null;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public CFUTop(ImageDealer imageDealer){
		this.imageDealer = imageDealer;
		imgPlus = imageDealer.imgPlus1.duplicate();
		W = imageDealer.width;
		H = imageDealer.height;
		T = imageDealer.pages;
		System.out.println("Size of image: H " + H + " x W " + W + " x T " + T);
		opts = imageDealer.opts;
        proPath = imageDealer.proPath;
        imageDealer.running = true;
	}
	
	   /**
     * Premiliary processing for data, get the active region and event seeds
     * 
     * @return return the labels of different active region
     */
    protected HashMap<Integer, CFUInfo> doInBackground() throws Exception {
    	float alpha = 0.5f;
		int minNumEvt = 3;
		int maxDist = 10;
		float thr = 0.001f;
		int cfuNumThr = 3;
		
//		HashMap<Integer, CFUInfo> cfuInfo = CFUHelper.getCFUInfo(imageDealer, alpha, minNumEvt);
//		ArrayList<float[]> relation = CFUHelper.calAllDependency(imageDealer, cfuInfo, maxDist);
//		
//		HashMap<Integer, GroupInfo> groupInfo = CFUHelper.getGroupInfo(imageDealer, 
//				relation, cfuInfo, thr, cfuNumThr);		
		
		return null;
    }

	/** 
     * Report the progress
     */
    protected void process(List<Integer> chunks) {
        int value = chunks.get(chunks.size()-1);
        String str = "";
        switch(value) {
        case 1:
            str = "Smooth the Data 1/8";
            break;
        case 2:
            str = "Calculate the Variance 2/8";
            break;
        case 3:
            str = "Subtract Background 3/8";
            break;
        case 4:
            str = "Detect the Connected Region 4/8";
            break;
        }
        jLabel.setText(str);
    }
    
    /** 
     * Adjust the interface, save the status, and let the interface show the active regions
     */
    @Override
    protected void done() {
  
    	
    	
    	
    	
    	
    	
    	
    }
    
    // helper below
    /**
     * Set the Jframe and its content, used to show the progress bar.
     */
    public void setting() {
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(1);
        
        progressBar.setIndeterminate(true);
        progressBar.setOrientation(SwingConstants.HORIZONTAL);
        progressBar.setPreferredSize(new Dimension(300,20));
        
        jLabel.setPreferredSize(new Dimension(300,30));
        jLabel.setFont(new Font("Dialog",1,15));
        jLabel.setHorizontalAlignment(JLabel.CENTER);;
        GridBagPut settingPanel = new GridBagPut(curPanel);
        settingPanel.fillBoth();
        settingPanel.putGridBag(progressBar, curPanel, 0, 0);
        settingPanel.putGridBag(jLabel, curPanel, 0, 1);
        frame.setContentPane(curPanel);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}