package va.vt.cbilAQuA2.run;

import java.awt.Dimension;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbilAQuA2.BasicFeatureDealer;
import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.cfu.CFUDetectRes;
import va.vt.cbilAQuA2.cfu.CFUHelper;
import va.vt.cbilAQuA2.cfu.CFUInfo;
import va.vt.cbilAQuA2.cfu.CFUPreResult;
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
public class CFUDetect extends SwingWorker<Void, Integer>{
	JFrame frame = new JFrame("Step1");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");

	CFUDealer cfuDealer = null;
	float alpha1 = 0;
	int minNumEvt1 = 0;
	float alpha2 = 0;
	int minNumEvt2 = 0;
	int ch;
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public CFUDetect(CFUDealer cfuDealer, float alpha, int minNumEvt, float alpha2, int minNumEvt2){
		this.cfuDealer = cfuDealer;
		this.alpha1 = alpha;
		this.minNumEvt1 = minNumEvt;
		this.alpha2 = alpha2;
		this.minNumEvt2 = minNumEvt2;
	}
	
	   /**
     * Premiliary processing for data, get the active region and event seeds
     * 
     * @return return the labels of different active region
     */
    protected Void doInBackground() throws Exception {
    	doBehind(1);
    	if (!cfuDealer.opts.singleChannel)
    		doBehind(2);
    	return null;
    }
    
    private void doBehind(int ch) {
    	this.ch = ch;
    	Opts opts = cfuDealer.opts;

		HashMap<Integer, ArrayList<int[]>> evtLst = new HashMap<>();
		boolean spaOption = cfuDealer.left.jTFspaOption.isSelected(); 
		float alpha;
		int minNumEvt;
		CFUPreResult cfu_pre;
		float[][][] dat;
		float[][][] dF; 
		publish(1);
		if (ch == 1) {
			alpha = alpha1;
			minNumEvt = minNumEvt1;
			cfu_pre = cfuDealer.cfu_pre1;
			dat = cfuDealer.dat1;
			dF = cfuDealer.dF1; 
			try {
				evtLst = Helper.readObjectFromFile(cfuDealer.proPath, "evt1.ser", evtLst.getClass());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} else {
			alpha = alpha2;
			minNumEvt = minNumEvt2;
			cfu_pre = cfuDealer.cfu_pre2;
			dat = cfuDealer.dat2;
			dF = cfuDealer.dF2; 
			try {
				evtLst = Helper.readObjectFromFile(cfuDealer.proPath, "evt2.ser", evtLst.getClass());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		
		if (cfu_pre == null || cfu_pre.evtIhw == null) {
			cfu_pre = CFUHelper.CFU_tmp_function(evtLst, spaOption, H, W, T);
		}
		
		publish(2);
		
		CFUDetectRes cfuRes = CFUHelper.CFU_minMeasure(cfu_pre, opts, alpha, minNumEvt);
		
		publish(3);
		
		
		
		float bias = Step1Helper.obtainBias(opts.movAvgWin, opts.cut);
		float[][] cfuCurves = new float[cfuRes.CFU_region.size()][T];
		float[][] cfuDffCurves = new float[cfuRes.CFU_region.size()][T];
		
		for (int i = 1; i <= cfuRes.CFU_region.size(); i++) {
			cfuCurves[i - 1] = CFUHelper.weightedAvg(cfuRes.CFU_region.get(i), cfuRes.CFU_pixLst.get(i), dat);
			cfuDffCurves[i - 1] = CFUHelper.getdFF(cfuCurves[i - 1], opts.movAvgWin, opts.cut, bias);
	//		cfuDffCurves[i - 1] = CFUHelper.weightedAvg(cfuRes.CFU_region.get(i), cfuRes.CFU_pixLst.get(i), dF);
			
			for (int t = 0; t < T; t++) {
				cfuCurves[i - 1][t] = opts.minValueDat + cfuCurves[i - 1][t] * (opts.maxValueDat - opts.minValueDat);
			}
		}
		
		float[] thrVec = new float[] {0.4f, 0.5f, 0.6f};
		int[][][] cfuMapVideo = new int[H][W][T];
		for (int i = 1; i <= cfuRes.CFU_region.size(); i++) {
			for (int j : cfuRes.CFU_lst.get(i)) {
				Helper.setValue(cfuMapVideo, evtLst.get(j), i);
			}
		}
		
		boolean[][] cfuOccurrence = new boolean[cfuRes.CFU_region.size()][T];
		boolean[][] cfuTimeWindow = new boolean[cfuRes.CFU_region.size()][T];
		boolean[][] cfuNonTimeWindow = new boolean[cfuRes.CFU_region.size()][T];
		ArrayList<int[]> pix;
		ArrayList<Integer> lst;
		int[] p;
		float[][] ws;
		float[] x0;
		int label;
		int t0, t1;
		int riseT;
		HashMap<Integer, CFUInfo> cfuInfo = new HashMap<>();
		int[][] cfuMap = new int[H][W];
		
		for (int i = 1; i <= cfuRes.CFU_region.size(); i++) {
			pix = new ArrayList<int[]>();
			ws = cfuRes.CFU_region.get(i);
			for (int index : cfuRes.CFU_pixLst.get(i)) {
				p = Helper.ind2sub(H, W, index);
				if (ws[p[0]][p[1]] > 0.1) {
					cfuMap[p[0]][p[1]] = i;
					pix.add(p);
				}
			}
			
			for (int t = 0; t < T; t++) {
				for(int j = 0; j < pix.size(); j++) {
					p = pix.get(j);
					if (cfuMapVideo[p[0]][p[1]][t] == i) {
						cfuTimeWindow[i - 1][t] = true;
						break;
					}
				}
				
				for(int j = 0; j < pix.size(); j++) {
					p = pix.get(j);
					if (cfuMapVideo[p[0]][p[1]][t] > 0 && cfuMapVideo[p[0]][p[1]][t] != i) {
						cfuNonTimeWindow[i - 1][t] = true;
						break;
					}
				}
				
				if (cfuTimeWindow[i - 1][t])
					cfuNonTimeWindow[i - 1][t] = false;
			}
			
			x0 = Helper.movingMean(cfuCurves[i - 1], 2);
			lst = cfuRes.CFU_lst.get(i);
			for (int j = 0; j < lst.size(); j++) {
				label = lst.get(j);
				t0 = T;
				t1 = 0;
				for (int[] p0 : evtLst.get(label)) {
					t0 = Math.min(t0, p0[2]);
					t1 = Math.max(t1, p0[2]);
				}
				riseT = CFUHelper.getRisingTime(x0, t0, t1, cfuTimeWindow[i - 1], thrVec);
				cfuOccurrence[i - 1][riseT] = true;				
			}
			
			CFUInfo curCFUInfo = new CFUInfo(cfuRes.CFU_lst.get(i), ws, cfuOccurrence[i - 1], cfuCurves[i - 1], cfuDffCurves[i - 1],
					cfuTimeWindow[i - 1], cfuNonTimeWindow[i - 1], cfuRes.CFU_pixLst.get(i));
			
			cfuInfo.put(i, curCFUInfo);
		}
		
		// get boundary of CFU
		boolean[][] msk;
		ArrayList<int[]> boundary;
		HashMap<Integer, ArrayList<int[]>> CFUboundary = new HashMap<>();
		for (int i = 1; i <= cfuRes.CFU_region.size(); i++) {
			msk = new boolean[H][W];
			for (int index : cfuInfo.get(i).pixLst) {
				p = Helper.ind2sub(H, W, index);
				msk[p[0]][p[1]] = true;
			}
			boundary = BasicFeatureDealer.findBoundary(msk);
			CFUboundary.put(i, boundary);
		}
		
		if (ch == 1) {
			cfuDealer.cfuInfo1 = cfuInfo;
			cfuDealer.cfuMap1 = cfuMap;
			cfuDealer.CFUboundary1 = CFUboundary;
			try {
				Helper.writeObjectToFile(cfuDealer.proPath, "cfuInfo1.ser", cfuInfo);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cfuDealer.nCFUch1 = cfuInfo.size();
		} else {
			cfuDealer.cfuInfo2 = cfuInfo;
			cfuDealer.cfuMap2 = cfuMap;
			cfuDealer.CFUboundary2 = CFUboundary;
			try {
				Helper.writeObjectToFile(cfuDealer.proPath, "cfuInfo2.ser", cfuInfo);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    /** 
     * Report the progress
     */
    protected void process(List<Integer> chunks) {
        int value = chunks.get(chunks.size()-1);
        String str = "";
        String channelInfo = " Channel " + this.ch;
        switch(value) {
        case 1:
            str = "Calculate spatial similarity of events 1/3" + channelInfo;
            break;
        case 2:
            str = "Extract CFU 2/3" + channelInfo;
            break;
        case 3:
            str = "Extract CFU Features 3/3" + channelInfo;
            break;
        }
        jLabel.setText(str);
    }
    
    /** 
     * Adjust the interface, save the status, and let the interface show the active regions
     */
    @Override
    protected void done() {
        frame.setVisible(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
            	cfuDealer.dealImage();
            }
            
        }).start();
        cfuDealer.left.left2.setEnabled(true);
        
        cfuDealer.left.addAll.setEnabled(true);
        cfuDealer.left.viewFavourite.setEnabled(true);
        cfuDealer.left.jTFwinSize.setEnabled(true);
        cfuDealer.left.winSizeSlider.setEnabled(true);
        cfuDealer.left.pick.setEnabled(true);
        cfuDealer.left.alldep.setEnabled(true);
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