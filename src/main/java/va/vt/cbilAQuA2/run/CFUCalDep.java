package va.vt.cbilAQuA2.run;

import java.awt.Dimension;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.cfu.CFUHelper;
import va.vt.cbilAQuA2.cfu.depRes;
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
public class CFUCalDep extends SwingWorker<Void, Integer>{
	JFrame frame = new JFrame("CFU Calculate Dependency");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");

	CFUDealer cfuDealer = null;
	int winSize = 0;
	int shift = 0;
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public CFUCalDep(CFUDealer cfuDealer, int winSize, int shift){
		this.cfuDealer = cfuDealer;
		this.winSize = winSize;
		this.shift = shift;
	}
	
	   /**
     * Premiliary processing for data, get the active region and event seeds
     * 
     * @return return the labels of different active region
     */
    protected Void doInBackground() throws Exception {
    	String proPath = cfuDealer.proPath;

    	int nCFU1 = cfuDealer.cfuInfo1.size();
    	int nCFU2 = 0;
    	if (!cfuDealer.opts.singleChannel)
    		nCFU2 = cfuDealer.cfuInfo2.size();
    	int maxDist = winSize;
		ArrayList<float[]> relation = new ArrayList<>();
		int T = cfuDealer.opts.T;
		
		boolean[][] seqs = new boolean[nCFU1 + nCFU2][T];
		for (int i = 1; i <= nCFU1; i++) {
			seqs[i - 1] = cfuDealer.cfuInfo1.get(i).occurrence;
		}
		if (!cfuDealer.opts.singleChannel) {
			for (int i = 1; i <= nCFU2; i++) {
				seqs[i + nCFU1 - 1] = cfuDealer.cfuInfo2.get(i).occurrence;
			}
		}
		
		publish(1);
		
		boolean[] seq1;
		boolean[] seq2;
		depRes curRes;
		float delay;
		int cnt;
		int sign = 0;
		float thr = 0.1f;
		for (int i = 1; i <= nCFU1 + nCFU2; i++) {
			for (int j = i + 1; j <= nCFU1 + nCFU2; j++) {
				seq1 = seqs[i - 1];
				seq2 = seqs[j - 1];
				depRes depRes1 = CFUHelper.calDependency(seq1, seq2, shift, maxDist);
				depRes depRes2 = CFUHelper.calDependency(seq2, seq1, shift, maxDist);
				
				if (depRes1.p < depRes2.p) {
					curRes = depRes1;
					sign = 1;
				}else {
					curRes = depRes2;
					sign = -1;
				}
				
				if (curRes.p < thr) {
					cnt = 0;
					delay = 0;
					for (Map.Entry<Integer, Integer> entry : curRes.delays.entrySet()) {
						delay += entry.getKey() * entry.getValue();
						cnt += entry.getValue();
					}
					delay /= cnt;
					delay *= sign;
					if (delay >= 0)
						relation.add(new float[] {i, j, curRes.p, delay});
					else
						relation.add(new float[] {j, i, curRes.p, - delay});
				}
			}
		}
		
		try {
			Helper.writeObjectToFile(proPath, "relation.ser", relation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
            str = "Calculate all dependencies of CFUs";
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
        cfuDealer.left.left3.setEnabled(true);
        
        cfuDealer.left.jTFpThr.setEnabled(true);
        cfuDealer.left.jTFminCFU.setEnabled(true);
        cfuDealer.left.groupRun.setEnabled(true);
        
//        imageDealer.center.gaussfilter.setEnabled(true);
//        imageDealer.left.nextButton.setEnabled(true);
//        imageDealer.left.backButton.setEnabled(true);
//        imageDealer.left.jTP.setEnabledAt(1, true);
//        
//        imageDealer.right.typeJCB.setEnabled(true);
//        if(imageDealer.left.jTPStatus<1) {
//            imageDealer.right.typeJCB.addItem("Step1: Active Voxels");
//            imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 1);
//        }
//        imageDealer.saveStatus();
//        imageDealer.running = false;
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