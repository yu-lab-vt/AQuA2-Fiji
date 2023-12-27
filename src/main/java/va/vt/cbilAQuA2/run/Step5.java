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
public class Step5 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step5");
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
	public Step5(ImageDealer imageDealer) {
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
	@SuppressWarnings("unchecked")
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
		Opts opts = new Opts(1);
		int H = imageDealer.dF1.length;
		int W = imageDealer.dF1[0].length;
		int T = imageDealer.dF1[0][0].length;
		boolean[][] evtSpatialMask = new boolean[H][W]; 
		for(int x=0;x<H;x++) {
			for(int y=0;y<W;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[y][x];
			}
		}	
		
		if (opts.detectGlo) {
			HashMap<Integer, ArrayList<int[]>> evtLocalLst = new HashMap<Integer, ArrayList<int[]>>();
			float[][][] dF;
			if (ch == 1) {
				dF = imageDealer.dF1;
				try {
					evtLocalLst = Helper.readObjectFromFile(proPath, "evt1.ser", evtLocalLst.getClass());
					opts = Helper.readObjectFromFile(proPath, "opts.ser", opts.getClass());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				dF = imageDealer.dF2;
				try {
					evtLocalLst = Helper.readObjectFromFile(proPath, "evt2.ser", evtLocalLst.getClass());
					opts = Helper.readObjectFromFile(proPath, "opts.ser", opts.getClass());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			publish(2);
			System.out.println("Remove detected local events from dF");
			float[][][] dF_glo = Step5Helper.removeDetected(dF, evtLocalLst);
//			Helper.viewMatrix(10, 10, 3, "dF_glo1", Helper.crop3D(dF_glo1, 368, 379, 266, 279, 0, 3));
			int[][][] activeMap = new int[H][W][T];		
			
			// load setting
			opts.minDur = opts.gloDur;
			HashMap<Integer, ArrayList<int[]>> arLst = Step2Helper.acDetect(dF_glo, activeMap, evtSpatialMask, 1, opts);
			Step3HelperResult res3 = Step3Helper.seDetection(dF_glo, imageDealer.dat1, arLst, opts);
			Step4Res res = Step4Helper.se2evtTop(dF_glo, res3.seLst, res3.evtLst, res3.seLabel, res3.majorInfo, opts);
			
			showTime();
			
			publish(3);
			if (ch == 1) {
				try {
					Helper.writeObjectToFile(proPath, "gloRiseLst1.ser", res.riseLst);  
		   			Helper.writeObjectToFile(proPath, "gloEvt1.ser", res.evtLst);  
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageDealer.label1 = res.datL;
			} else {
				try {
					Helper.writeObjectToFile(proPath, "gloRiseLst2.ser", res.riseLst);  
		   			Helper.writeObjectToFile(proPath, "gloEvt2.ser", res.evtLst);  
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageDealer.label2 = res.datL;
			}
		}
	}
		
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		int total = 3;
		String channelInfo = " Channel " + this.ch;
		switch(value) {
			case 1:
				str = "Load Previous Results " + value + "/" + total + channelInfo;
				break;
			case 2:
				str = "Detect Global Events " + value + "/" + total + channelInfo;
				break;
			case 3:
				str = "Save the Results " + value + "/" + total + channelInfo;
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
		imageDealer.left.jTP.setEnabledAt(5, true);
		
		if(imageDealer.left.jTPStatus<5) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 5);
			if (imageDealer.opts.detectGlo) {
				imageDealer.right.typeJCB.addItem("Global Events");
			}
		}

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
