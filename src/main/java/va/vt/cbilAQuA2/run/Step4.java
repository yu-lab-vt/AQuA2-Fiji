package va.vt.cbilAQuA2.run;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
public class Step4 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step4");
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
	public Step4(ImageDealer imageDealer) {
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
		HashMap<Integer, ArrayList<int[]>> seLst = new HashMap<Integer, ArrayList<int[]>>();
		HashMap<Integer, ArrayList<int[]>> svLst = new HashMap<Integer, ArrayList<int[]>>();
		HashMap<Integer, Step3MajorityResult> majorInfo = new HashMap<Integer, Step3MajorityResult>();
		int[] seLabel = new int[0];
		float[][][] dF;
		if(ch == 1) {
			dF = imageDealer.dF1;
			try {
				seLst = Helper.readObjectFromFile(proPath, "seLst1.ser", seLst.getClass());
				svLst = Helper.readObjectFromFile(proPath, "subEvtLst1.ser", svLst.getClass());
				seLabel = Helper.readObjectFromFile(proPath, "seLabel1.ser", seLabel.getClass());
				majorInfo = Helper.readObjectFromFile(proPath, "majorInfo1.ser", majorInfo.getClass());
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
				seLst = Helper.readObjectFromFile(proPath, "seLst2.ser", seLst.getClass());
				svLst = Helper.readObjectFromFile(proPath, "subEvtLst2.ser", svLst.getClass());
				seLabel = Helper.readObjectFromFile(proPath, "seLabel2.ser", seLabel.getClass());
				majorInfo = Helper.readObjectFromFile(proPath, "majorInfo2.ser", majorInfo.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		HashMap<Integer, ArrayList<int[]>> evtLst;
		HashMap<Integer, RiseInfo> riseLst;
		int gaptxx = opts.gapExt;
		int H = opts.H;
		int W = opts.W;
		int T = opts.T;
		int[][][] datR = new int[H][W][T];
		int[][][] datL = new int[H][W][T];
		
		if (opts.needSpa)
		{
			
			int[][][] seMap = new int[H][W][T];
			ArrayList<int[]> pix, se0, pix0;
			HashMap<Integer, Step3MajorityResult> major0;
			for (int i = 1; i <= seLst.size(); i++) {
				pix = seLst.get(i);
				Helper.setValue(seMap, pix, i);
			}
		
		
		
			publish(2);
			System.out.println("Detecting events ...");
			riseLst = new HashMap<Integer, RiseInfo>(); 
			datR = new int[H][W][T];
			datL = new int[H][W][T];
			int nEvt = 0;
			HashSet<Integer> ihw0;
			ArrayList<Integer> svLabels;
			HashMap<Integer, ArrayList<int[]>> superVoxels;
			int x0, x1, y0, y1, t0, t1, H0, W0, T0, gapt;
			int[] p;
			Step3MajorityResult curMajor;
			float[][][] dF0;
			int[][][] seMap0;
			for(int nn = 1; nn <= seLst.size(); nn++) {
				se0 = seLst.get(nn);
				
				// super event pixel transform
				x0 = H; x1 = 0; y0 = W; y1 = 0; t0 = T; t1 = 0;
				
				for (int i = 0; i < se0.size(); i++) {
					p = se0.get(i);
					x0 = Math.min(x0, p[0]); x1 = Math.max(x1, p[0]);
					y0 = Math.min(y0, p[1]); y1 = Math.max(y1, p[1]);
					t0 = Math.min(t0, p[2]); t1 = Math.max(t1, p[2]);
				}
				gapt = Math.min(t1 - t0, gaptxx);
				t0 = Math.max(t0 - gapt, 0); t1 = Math.min(t1 + gapt, T - 1);
				H0 = x1 - x0 + 1; W0 = y1 - y0 + 1; T0 = t1 - t0 + 1;
				ihw0 = new HashSet<Integer>();
				for (int i = 0; i < se0.size(); i++) {
					p = se0.get(i);
					ihw0.add(Helper.sub2ind(H0, W0, p[0] - x0, p[1] - y0));
				} 
				
				// sub event pixel transform
				svLabels = new ArrayList<Integer>();
				major0 = new HashMap<Integer, Step3MajorityResult>();
				for (int i = 1; i < seLabel.length; i++) {
					if (seLabel[i] == nn)
						svLabels.add(i);
				}
				superVoxels = new HashMap<Integer, ArrayList<int[]>>();
				
				for (int k = 0; k < svLabels.size(); k++) {
					pix = svLst.get(svLabels.get(k));
					pix0 = new ArrayList<int[]>();
					for (int i = 0; i < pix.size(); i++) {
						p = pix.get(i);
						pix0.add(new int[] {p[0] - x0, p[1] - y0, p[2] - t0});
					}
					superVoxels.put(k + 1, pix0);
					
					curMajor = majorInfo.get(svLabels.get(k));
					curMajor.t0 -= t0;
					curMajor.t1 -= t0;
					curMajor.ihw = new HashSet<Integer>();
					if(curMajor.ihwDelays.size() >= opts.minSize) {
						for (Map.Entry<Integer, Integer> entry : curMajor.ihwDelays.entrySet()) {
							p = Helper.ind2sub(H, W, entry.getKey());
							curMajor.ihw.add(Helper.sub2ind(H0, W0, p[0] - x0, p[1] - y0));
						}
						
					}				
					major0.put(k + 1, curMajor);
				}
				
				System.out.println("SE: " + nn);
				dF0 = Helper.crop3D(dF, x0, x1, y0, y1, t0, t1);
				seMap0 = Helper.crop3D(seMap, x0, x1, y0, y1, t0, t1);
				Step4Se2EvtRes curRes = Step4Helper.se2evt(dF0, seMap0, nn, ihw0, t0, t1, superVoxels, major0, opts);
				Step4Helper.addToRisingMap(riseLst, curRes, nEvt, x0, x1, y0, y1);
				
				// update
				for (int x = 0; x < H0; x++) {
					for (int y = 0; y < W0; y++) {
						for (int t = 0; t < T0; t++) {
							if (curRes.evtL[x][y][t] > 0) {
								seMap[x + x0][y + y0][t + t0] = nn;
								datL[x + x0][y + y0][t + t0] = Math.max(datL[x + x0][y + y0][t + t0], curRes.evtL[x][y][t] + nEvt);
							}
							datR[x + x0][y + y0][t + t0] = (int) Math.max(datR[x + x0][y + y0][t + t0], curRes.evtRecon[x][y][t] * 255);
						}
					}
				}
				nEvt += curRes.nEvt0;
			}
			evtLst = Helper.label2idx(datL);
			showTime();
		
		} else {
			evtLst = seLst;
			datR = null;
			riseLst = null;
			datL = new int[H][W][T]; 
	        ArrayList<int[]> pix;
	        for (int i = 1; i <= evtLst.size(); i++) {
	        	pix = evtLst.get(i);
	        	Helper.setValue(datL, pix, i);
	        }
		}
		
		publish(3);
		imageDealer.center.nEvt.setText("nEvt");
		// Save
		int colorBase = imageDealer.colorBase;
		Random rv = new Random();
		Color[] labelColors = new Color[evtLst.size() + 1];
		for(int i=0;i<labelColors.length;i++) {
			labelColors[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
		}
		
		
		
		if (ch == 1) {
			imageDealer.riseLst1 = riseLst;
			imageDealer.center.EvtNumber.setText(evtLst.size() +"");
			imageDealer.label1 = datL;
			imageDealer.datR1 = datR;
			imageDealer.labelColors1 = labelColors;
			try {
				Helper.writeObjectToFile(proPath, "riseLst1.ser", riseLst);  
	   			Helper.writeObjectToFile(proPath, "evt1.ser", evtLst);  
	   			Helper.writeObjectToFile(proPath, "datR1.ser", datR);  
	   			Helper.writeObjectToFile(proPath, "evtColor1.ser", labelColors);  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			imageDealer.riseLst2 = riseLst;
			imageDealer.center.EvtNumber.setText(imageDealer.center.EvtNumber.getText() + "|" +  evtLst.size());
			imageDealer.label2 = datL;
			imageDealer.datR2 = datR;
			imageDealer.labelColors2 = labelColors;
			try {
				Helper.writeObjectToFile(proPath, "riseLst2.ser", riseLst);  
	   			Helper.writeObjectToFile(proPath, "evt2.ser", evtLst);  
	   			Helper.writeObjectToFile(proPath, "datR2.ser", datR);  
	   			Helper.writeObjectToFile(proPath, "evtColor2.ser", labelColors);  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
//		return datL;
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
				str = "Spatially Segmentation " + value + "/" + total + channelInfo;
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
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.jTP.setEnabledAt(4, true);
		
		if(imageDealer.left.jTPStatus<4) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 4);
			imageDealer.right.typeJCB.addItem("Events");
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
