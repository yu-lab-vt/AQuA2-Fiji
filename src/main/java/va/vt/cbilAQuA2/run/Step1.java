package va.vt.cbilAQuA2.run;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.tools.GaussFilter;
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
public class Step1 extends SwingWorker<int[][][], Integer>{
//  public class Step1_a extends SwingWorker<float[][][], Float>{
//	claim variables
	JFrame frame = new JFrame("Step1");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	ImageDealer imageDealer = null;
	Opts opts = null;
	boolean[][] evtSpatialMask = null;
	String proPath = null;
	int ch;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public Step1(ImageDealer imageDealer){
		this.imageDealer = imageDealer;
		int H = imageDealer.dat1.length;
		int W = imageDealer.dat1[0].length;
		int T = imageDealer.dat1[0][0].length;
		System.out.println("Size of image: H " + H + " x W " + W + " x T " + T);
		opts = imageDealer.opts;
        proPath = imageDealer.proPath;
        imageDealer.running = true;
		
		evtSpatialMask = new boolean[H][W]; 
		for(int x=0;x<H;x++) {
			for(int y=0;y<W;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[y][x];
			}
		}	
	}
	
	   /**
     * Premiliary processing for data, get the active region and event seeds
     * 
     * @return return the labels of different active region
     */
    protected int[][][] doInBackground() throws Exception {
    	doBehind(1);
    	if (!opts.singleChannel) {
    		doBehind(2);
    	}
    	return null;
    }
    
    private void doBehind(int ch) {
    	// ------------------------ Load Data ----------------------------- //
    	this.ch = ch;
        publish(1);
        float[][][] datOrg;
        if (ch == 1)
          	datOrg = imageDealer.dat1;
        else
        	datOrg = imageDealer.dat2;
        
//        baseline estimation and noise estimation      
        float bias, maxdF;
        long startTime, stopTime, elapsedTime;
        float[][] stdMapOrg, stdMapSmo, tempVarOrg, correctPars;
        float[][][] datSmo, F0, dF;
        
        int H = datOrg.length;
        int W = datOrg[0].length;
        int T = datOrg[0][0].length;
        System.out.println("H: " + H + " W " + W + " T: " + T);
        // Calculate standard variance: Noise for smoothed data
        publish(2);
        
        // smooth the data -- checked
        System.out.println("smooth the data"); 
        datSmo = new float[H][W][T];
       
        for (int tt = 0; tt < T; tt++) {
  		    float[][] slice = Helper.getSlice(datOrg, tt);
  		    float[][] filteredSlice;
  		    if (opts.smoXY > 0) {
  		    	filteredSlice = GaussFilter.gaussFilter(slice, opts.smoXY, opts.smoXY);
  		    }else {
  		    	filteredSlice = slice;
  		    }
  		    Helper.setSlice(datSmo, filteredSlice, tt); 
        }      
        // linear estimation of F0 - checked
        publish(3);
        System.out.println("baselineLinearEstimate"); 
        opts.cut = Math.min(opts.cut, T);
        startTime = System.currentTimeMillis();
        F0 = Step1Helper.baselineLinearEstimate(datSmo, opts.cut, opts.movAvgWin);
//        Helper.viewMatrix(10, 10, 1, "F0", F0);
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + "ms");      
        
        // get projection
        float[][] F0Pro = new float[H][W];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                float sum = 0;
                for (int k = 0; k < T; k++) {
                    sum += F0[i][j][k];
                }
                F0Pro[i][j] = sum / T;
            }
        }
        
        // noise estimation - piece wise linear function to model
        publish(4);
        startTime = System.currentTimeMillis();
        stdMapOrg = new float[H][W];
        stdMapSmo = new float[H][W];
        tempVarOrg = new float[H][W];
        correctPars = new float[H][W];
        Step1Helper.noiseEstimation(F0Pro, datOrg, datSmo, opts.smoXY, evtSpatialMask, stdMapOrg, stdMapSmo, tempVarOrg, correctPars);
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + "ms");
        
//        
//      Helper.viewMatrix(10, 10, "stdMapOrg", stdMapOrg);
//      Helper.viewMatrix(10, 10, "stdMapSmo", stdMapSmo);
//      Helper.viewMatrix(10, 10, "tempVarOrg", tempVarOrg);
//      Helper.viewMatrix(10, 10, "correctPars", correctPars); 
        
        // correct bias during noise estimation. Bias does not impact noise
        
        // Done !!! Now in jar, the configurations in resources can be read.
        bias = Step1Helper.obtainBias(opts.movAvgWin, opts.cut);
        System.out.printf("bias % f", bias);
        
        dF = new float[H][W][T];
        maxdF = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < H; i++) {
      	  for (int j = 0; j < W; j++) {
      		  for (int k = 0; k < T; k++) {
      			  F0[i][j][k] = F0[i][j][k] - bias * stdMapSmo[i][j];
      			  dF[i][j][k] = (datSmo[i][j][k] - F0[i][j][k]) / stdMapSmo[i][j];
      			  maxdF = Math.max(dF[i][j][k], maxdF);
      		  }
      	  }
        }

        publish(5);
        if (ch == 1) {
	  	  	opts.stdMap1 = stdMapSmo;
	  	  	opts.stdMapOrg1 = stdMapOrg;
	  	  	opts.tempVarOrg1 = tempVarOrg;
	  	  	opts.correctPars1 = correctPars;
	  	  	opts.maxdF1 = maxdF;
        
	        // ------------------------ Save Data ----------------------------- //
	        try {
	     			Helper.writeObjectToFile(proPath, "dF1.ser", dF);  
	     			Helper.writeObjectToFile(proPath, "datOrg1.ser", datOrg);  
	     			Helper.writeObjectToFile(proPath, "opts.ser", opts);  
	        } catch (Exception e) {
	     			e.printStackTrace();
	        }
	        imageDealer.dat1 = datOrg;
	        imageDealer.dF1 = dF;
    	}else {
    		opts.stdMap2 = stdMapSmo;
	  	  	opts.stdMapOrg2 = stdMapOrg;
	  	  	opts.tempVarOrg2 = tempVarOrg;
	  	  	opts.correctPars2 = correctPars;
	  	  	opts.maxdF2 = maxdF;
        
	        // ------------------------ Save Data ----------------------------- //
	        try {
	     			Helper.writeObjectToFile(proPath, "dF2.ser", dF);  
	     			Helper.writeObjectToFile(proPath, "datOrg2.ser", datOrg);  
	     			Helper.writeObjectToFile(proPath, "opts.ser", opts);  
	        } catch (Exception e) {
	     			e.printStackTrace();
	        }
	        imageDealer.dat2 = datOrg;
	        imageDealer.dF2 = dF;
    	}
    }
    
    /** 
     * Report the progress
     */
    protected void process(List<Integer> chunks) {
        int value = chunks.get(chunks.size()-1);
        String str = "";
        switch(value) {
        case 1:
            str = "Load the Data 1/5 Channel " + this.ch;
            break;
        case 2:
            str = "Smooth the Data 2/5 Channel " + this.ch;
            break;
        case 3:
            str = "Baseline Estimation 3/5 Channel " + this.ch;
            break;
        case 4:
            str = "Noise Estimation 4/5 Channel " + this.ch;
            break;
        case 5:
            str = "Save Temporal Results 5/5 Channel " + this.ch;
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
                imageDealer.dealImage();
                imageDealer.imageLabel.repaint();
            }
            
        }).start();
        imageDealer.center.gaussfilter.setEnabled(true);
        imageDealer.left.nextButton.setEnabled(true);
        imageDealer.left.backButton.setEnabled(true);
        imageDealer.left.jTP.setEnabledAt(1, true);
        if(imageDealer.left.jTPStatus<1) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 1);
		}
        imageDealer.right.typeJCB.setEnabled(true);

        if(opts.singleChannel) {
        	imageDealer.center.leftJCB.setSelectedIndex(1);
        	imageDealer.center.rightJCB.setSelectedIndex(5);
    		imageDealer.center.sideButton.setSelected(true);
        }else {
        	imageDealer.center.leftJCB.setSelectedIndex(1);
        	imageDealer.center.rightJCB.setSelectedIndex(1);
        }
        
        imageDealer.saveStatus();
        imageDealer.running = false;
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