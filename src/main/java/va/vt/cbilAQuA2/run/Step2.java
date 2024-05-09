package va.vt.cbilAQuA2.run;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.ui.GridBagPut;

/**
 * The second step of the whole software, grow the event seeds 
 * we find in the first step. First we grow the seeds in time dimension,
 * then grow them in each frame. All the points extended from one seed 
 * we call them one super voxel. According the seeds, we could find many
 * super voxels and consider them as the prelimiary events. After this step
 * finish, we show them in interface with different colors. 
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class Step2 extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Step2");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	int W = 0;
	int H = 0;
	boolean[][] evtSpatialMask = null;
	String proPath = null;
	int[][] regionMarkLabel = null;
	int ch;
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public Step2(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		W = (int) imageDealer.getOrigWidth();
		H = (int) imageDealer.getOrigHeight();
		evtSpatialMask = new boolean[H][W];
		for(int x=0;x<H;x++) {
			for(int y=0;y<W;y++) {
				evtSpatialMask[x][y] = imageDealer.regionMark[y][x];
			}
		}
		regionMarkLabel = imageDealer.regionMarkLabel;
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
	 * Grow the seeds in first step, form the super voxels
	 * 
	 * @return return the labels of different super voxels
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
		// valid Region		
		publish(1);
		// ------------------------ Read Data ----------------------------- //
		float[][][] dF;
		if (ch == 1)
			dF = imageDealer.dF1;
		else
			dF = imageDealer.dF2;
		Opts opts = imageDealer.opts;
		
		// ---------------------- Algorithm -------------------------------- //		
		// default parameters
		// load setting
		System.out.println("AcDetect");
		
		int H = dF.length;
		int W = dF[0].length;
		int T = dF[0][0].length;
		int[][][] activeMap = new int[H][W][T];		
		float maxdF = 0; 
		float thrsNum, thr;
		double circularity;
		int nReg = 0, boundary = 0, curSz;
		float[] thrsVec = null;
		boolean[][] curMap2D;
		boolean[][][] selectMap;
		selectMap = new boolean[H][W][T];
		HashMap<Integer, ArrayList<int[]>> curRegions, arLst;
		arLst = new HashMap<Integer, ArrayList<int[]>>();
		int[] p;
		int minT, maxT;
		
//		Helper.viewMatrix(10, 10, 2, "dF", dF);
		
		if (ch==1) {
			maxdF = opts.maxdF1;
		} else if (ch==2) {
			maxdF = opts.maxdF2;
		}
		
		if (opts.thrARScl > maxdF || (opts.maxSize >= H*W && opts.circularityThr == 0)) {
			thrsNum = opts.thrARScl;
			thrsVec = new float[1];
			thrsVec[0] = thrsNum;
		} else {
			thrsVec = new float[11];
			float step = (maxdF - opts.thrARScl) / 10;
			for (int i = 0; i < 11; i++) {
				thrsVec[i] = opts.thrARScl + i * step;
			}
		}
		
		publish(1);
		
		// valid region
		for (int x = 0; x < thrsVec.length; x++) {
			thr = thrsVec[x];
			for (int i = 0; i < H; i++) {
			    for (int j = 0; j < W; j++) {
			    	if (evtSpatialMask[i][j]) {
				        for (int k = 0; k < T; k++) {
				            selectMap[i][j][k] = dF[i][j][k] > thr && activeMap[i][j][k] == 0;
				        }
			    	}
			    }
			}
			// curRegions = act.bw2Reg(selectMap,opts);
			curRegions = new HashMap<Integer, ArrayList<int[]>>();
			Helper.bfsConn3D(selectMap, curRegions); 
			boolean[] valid = new boolean[curRegions.size()];
//					System.out.printf("n curRegions: %d \n", curRegions.size());
			for (int i = 1; i <= curRegions.size(); i++) {
				ArrayList<int[]> pixLst = curRegions.get(i);
				if (pixLst.size() < opts.minSize) {
					continue;
				}
				
				curMap2D = new boolean[H][W];
				minT = Integer.MAX_VALUE;
				maxT = 0;
				curSz = 0;
				for (int pId = 0; pId < pixLst.size(); pId++) {
					p = pixLst.get(pId);
					if (!curMap2D[p[0]][p[1]]) {
						curSz ++;
					}
					curMap2D[p[0]][p[1]] = true;
					minT = Math.min(minT, p[2]);
					maxT = Math.max(maxT, p[2]);
				}
				
		        // size, duration limitation
		        if (curSz > opts.maxSize || curSz < opts.minSize || maxT - minT + 1 < opts.minDur)
		            continue;
		        
		        if (opts.circularityThr==0) {
		        	valid[i - 1] = true;
		        }
		        
		        // calculate the circularity
		        boolean[][] erodeMap = Helper.erodeImage(curMap2D);
		        boundary = 0;
		        for (boolean[] row : erodeMap) {
		            for (boolean value : row) {
		                if (value) {
		                    boundary++;
		                }
		            }
		        }
		        boundary = curSz - boundary;
		        circularity = (4 * Math.PI * curSz) / (boundary * boundary);
		        if(circularity>opts.circularityThr) {
		        	valid[i - 1] = true;
		        }
	        }
			
			for (int i = 1; i <= curRegions.size(); i++) {
				if (valid[i - 1]) {
					nReg ++;
					ArrayList<int[]> pixLst = curRegions.get(i);
					arLst.put(nReg, pixLst);
					for (int pId = 0; pId < pixLst.size(); pId++) {
						p = pixLst.get(pId);
						activeMap[p[0]][p[1]][p[2]] = nReg;
					}
				}	
			}
		}
			
		System.out.printf("nReg: %d \n", arLst.size());
		
		// Save
		publish(2);
		int colorBase = imageDealer.colorBase;
		Random rv = new Random();
		Color[] labelColors = new Color[arLst.size() + 1];
		for(int i=0;i<labelColors.length;i++) {
			labelColors[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
		}
		imageDealer.center.nEvt.setText("nAct");
		if(	ch == 1) {
			try {
				Helper.writeObjectToFile(proPath, "arLst1.ser", arLst);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			imageDealer.labelColors1 = labelColors;
			imageDealer.center.EvtNumber.setText(arLst.size()+"");
			imageDealer.label1 = activeMap;
		} else {
			try {
				Helper.writeObjectToFile(proPath, "arLst2.ser", arLst);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			imageDealer.labelColors2 = labelColors;
			imageDealer.center.EvtNumber.setText(imageDealer.center.EvtNumber.getText() + "|" + arLst.size());
			imageDealer.label2 = activeMap;
		}
	}
	/** 
	 * Report the progress.
	 */
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		switch(value) {
		case 1:
			str = "Obtain the active regions 1/2 Channel " + this.ch;
			break;
		case 2:
			str = "Save the Results 2/2 Channel " + this.ch;
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
//		JOptionPane.showMessageDialog(null, "Step2 Finish!");
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		if(imageDealer.left.jTPStatus<2) {
			imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 2);;
			imageDealer.right.typeJCB.addItem("Step2: Active regions");
		}
		imageDealer.left.jTP.setEnabledAt(2, true);
		imageDealer.changeSignalDrawRegionStatus();
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
		imageDealer.saveStatus();
		imageDealer.running = false;
		imageDealer.center.rightJCB.setSelectedIndex(5);
		if (!imageDealer.opts.singleChannel)
			imageDealer.center.leftJCB.setSelectedIndex(1);
	}
	
	/**
	 * For return mid-result
	 * 
	 * @author Xuelong Mi
	 */
	public class MidResult{
		HashMap<Integer, ArrayList<int[]>> map = null;
		int[][] riseX = null;
		int[][][] lblMapEx = null;
		
		public MidResult(HashMap<Integer, ArrayList<int[]>> map, int[][] riseX, int[][][] lblMapEx) {
			this.map = map;
			this.riseX = riseX;
			this.lblMapEx = lblMapEx;
		}
	}
	
	
}
