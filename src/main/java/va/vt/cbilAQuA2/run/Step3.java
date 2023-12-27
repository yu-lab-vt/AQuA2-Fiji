package va.vt.cbilAQuA2.run;

import java.awt.Dimension;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.ui.GridBagPut;

/**
 * The third and the most important step of the whole software, to
 * detect the events. Find the super event first, then split it into
 * several events.Then according to the events detected, extract 
 * premiliary features. After this step finish, we show them in 
 * interface with different colors. 
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class Step3 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step3");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start;
	static long end;
//	static Opts opts = null;
	static ImagePlus image = null;
	static ImageDealer imageDealer = null;
	String proPath = null;
	int ch;
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	@SuppressWarnings("static-access")
	public Step3(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		if(imageDealer!=null)
			image = imageDealer.imgPlus1;
		imageDealer.running = true;
	}
	
	
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
	
	/**
	 * Detect the events
	 * 
	 * @return return the labels of different events
	 */
	@Override
	protected int[][][] doInBackground() throws Exception {
		doBehind(1);
		if (!imageDealer.opts.singleChannel)
			doBehind(2);
		return null;
	}
	
	private void doBehind(int ch) {
		this.ch = ch;

		publish(1);
		// ------------------------ Read Data ----------------------------- //
		start = System.currentTimeMillis();
		long start0 = start;
		Opts opts = imageDealer.opts;
		opts.step = 0.5f;
		float[][][] datOrg, dF;
		HashMap<Integer, ArrayList<int[]>> arLst = new HashMap<Integer, ArrayList<int[]>>();
		
		if (ch == 1) {
			datOrg = imageDealer.dat1;
			dF = imageDealer.dF1;
			try {
				arLst = Helper.readObjectFromFile(proPath, "arLst1.ser", arLst.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			datOrg = imageDealer.dat2;
			dF = imageDealer.dF2;
			try {
				arLst = Helper.readObjectFromFile(proPath, "arLst2.ser", arLst.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		showTime();
//		Step3HelperResult res = Step3Helper.seDetection(dF, dat, arLst1, opts);
		
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		int[][][] Map = new int[H][W][T]; 
		int[] seLstInfoLabel;
		HashMap<Integer, ArrayList<int[]>> sdLst, curRegions, evtLst, seLst;
        
		// seed detection
		publish(2);
        System.out.println("Seed detection");
        start = System.currentTimeMillis();
        curRegions = Step3Helper.seedDetect2DSAccelerate(Helper.copy3Darray(dF), datOrg, Map, arLst, opts);
        sdLst = Helper.label2idx(Map);
        
        showTime();
        
        // marker controlled watershed
        publish(3);
        System.out.println("Watershed grow");
        curRegions = Step3Helper.markerControlledSplitting_Ac(Map, sdLst, curRegions, dF, opts);
        
//        // load from matlab result -- to be deleted
//        Map = Helper.loadMatlabStep2Result();       
        
        evtLst = Helper.label2idx(Map);
        
        // remove empty
        boolean[] nonEmpty = new boolean[evtLst.size()];
        for (int i = 1; i <= evtLst.size(); i++) {
        	nonEmpty[i - 1] = evtLst.get(i).size() > 0;
        }
        sdLst = Helper.filterWithMask(sdLst, nonEmpty);
        evtLst = Helper.filterWithMask(evtLst, nonEmpty);
        
        publish(4);
        // select major part
        System.out.println("Select majority part");
        HashMap<Integer, Step3MajorityResult> majorityEvt0 = Step3Helper.getMajority_Ac(sdLst, evtLst, dF, opts);
        
        
        // according to curve, refine
        System.out.println("Refining");
        boolean[] isGood = Step3Helper.majorCurveFilter2(datOrg, dF, sdLst, evtLst, majorityEvt0, opts);
        sdLst = Helper.filterWithMask(sdLst, isGood);
        evtLst = Helper.filterWithMask(evtLst, isGood);
        majorityEvt0 = Helper.filterWithMaskMajor(majorityEvt0, isGood);
        
        // merge to super event
        System.out.println("Merging signals with similar temporal patterns");
        Step3MergingInfo mergingInfo = Step3Helper.createMergingInfo(evtLst, majorityEvt0, curRegions, opts);
        seLst = new HashMap<Integer, ArrayList<int[]>>();
        seLstInfoLabel = Step3Helper.mergingSEbyInfo_UpdateSpa(evtLst, majorityEvt0, mergingInfo, curRegions, opts, seLst);
        
        // label to Map
        Map = new int[H][W][T]; 
        ArrayList<int[]> pix;
        for (int i = 1; i <= seLst.size(); i++) {
        	pix = seLst.get(i);
        	Helper.setValue(Map, pix, i);
        }
        Step3HelperResult res = new Step3HelperResult(sdLst, evtLst, seLst, majorityEvt0, seLstInfoLabel, Map);
        
		end = System.currentTimeMillis();
		System.out.println("Total time" + (end-start0) + "ms");
		
		publish(5);
		imageDealer.center.nEvt.setText("nSe");
		if (ch == 1) {
			imageDealer.center.EvtNumber.setText(res.seLst.size() +"");
			try {
				Helper.writeObjectToFile(proPath, "sdLst1.ser", res.sdLst);  
	   			Helper.writeObjectToFile(proPath, "subEvtLst1.ser", res.evtLst);  
	   			Helper.writeObjectToFile(proPath, "seLst1.ser", res.seLst);  
	   			Helper.writeObjectToFile(proPath, "majorInfo1.ser", res.majorInfo);  
	   			Helper.writeObjectToFile(proPath, "seLabel1.ser", res.seLabel);  
	   			Helper.writeObjectToFile(proPath, "opts.ser", opts);  
			} catch (Exception e) {
				e.printStackTrace();
			}
			imageDealer.label1 = res.Map;
		} else {
			imageDealer.center.EvtNumber.setText(imageDealer.center.EvtNumber.getText() + "|" + res.seLst.size());
			try {
				Helper.writeObjectToFile(proPath, "sdLst2.ser", res.sdLst);  
	   			Helper.writeObjectToFile(proPath, "subEvtLst2.ser", res.evtLst);  
	   			Helper.writeObjectToFile(proPath, "seLst2.ser", res.seLst);  
	   			Helper.writeObjectToFile(proPath, "majorInfo2.ser", res.majorInfo);  
	   			Helper.writeObjectToFile(proPath, "seLabel2.ser", res.seLabel);  
	   			Helper.writeObjectToFile(proPath, "opts.ser", opts);  
			} catch (Exception e) {
				e.printStackTrace();
			}
			imageDealer.label2 = res.Map;
		}
		
	}
		
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		int total = 5;
		String channelInfo = " Channel " + this.ch;
		switch(value) {
			case 1:
				str = "Load Previous Result " + value + "/" + total + channelInfo;
				break;
			case 2:
				str = "Seed Detection " + value + "/" + total + channelInfo;
				break;
			case 3:
				str = "Region Grow " + value + "/" + total + channelInfo;
				break;
			case 4:
				str = "Merging " + value + "/" + total + channelInfo;
				break;
			case 5:
				str = "Save the results " + value + "/" + total + channelInfo;
				break;
		}
		jLabel.setText(str);
	}
		
	/** 
	 * Adjust the interface, save the status, and let the interface show the super voxels
	 */
	@Override
	protected void done() {
		frame.setVisible(false);
//		JOptionPane.showMessageDialog(null, "Step3 Finish!");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(3, true);
		
		if(imageDealer.left.jTPStatus<3) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 3);
			imageDealer.right.typeJCB.addItem("Step3a: Watershed results");
			imageDealer.right.typeJCB.addItem("Step3aa: seeds");
			imageDealer.right.typeJCB.addItem("Step3b: Super Events");
		}
//		imageDealer.right.typeJCB.setSelectedIndex(3);
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
		imageDealer.saveStatus();
		imageDealer.running = false;
	}
	
	/**
	 * show the current time
	 */
	static void showTime() {
		end = System.currentTimeMillis();
		System.out.println((end-start) + "ms");
		start = end;
	}
	
}
