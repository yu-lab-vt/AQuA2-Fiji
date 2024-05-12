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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.BasicFeatureDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.fea.Basic;
import va.vt.cbilAQuA2.fea.FeatureTopResult;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.fea.LandMark;
import va.vt.cbilAQuA2.fea.LandMarkDir;
import va.vt.cbilAQuA2.fea.LandMarkDist;
import va.vt.cbilAQuA2.fea.MulBoundary;
import va.vt.cbilAQuA2.fea.NetWork;
import va.vt.cbilAQuA2.fea.Propagation;
import va.vt.cbilAQuA2.fea.ResReg;
import va.vt.cbilAQuA2.tools.GaussFilter;
import va.vt.cbilAQuA2.ui.GridBagPut;

public class Step6 extends SwingWorker<Step6Result, Integer> {
	JFrame frame = new JFrame("Step7");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	int ch;
	
	float minArea = Float.MAX_VALUE;
	float maxArea = -Float.MAX_VALUE;
	float minPvalue = Float.MAX_VALUE;
	float maxPvalue = -Float.MAX_VALUE;
	float minDecayTau = Float.MAX_VALUE;
	float maxDecayTau = -Float.MAX_VALUE;
	float mindffMax = Float.MAX_VALUE;
	float maxdffMax = -Float.MAX_VALUE;
	float minDuration = Float.MAX_VALUE;
	float maxDuration = -Float.MAX_VALUE;
	String proPath = null;
	
	public Step6(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
		imageDealer.running = true;
	}
	
	public Step6() {

	}
	
	public void setting() {
		frame.setSize(400, 200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(1);
		
		progressBar.setIndeterminate(true);
		progressBar.setOrientation(SwingConstants.HORIZONTAL);
		progressBar.setPreferredSize(new Dimension(300,20));
		
		jLabel.setPreferredSize(new Dimension(300,30));
		jLabel.setFont(new Font("Dialog",1,15));
		jLabel.setHorizontalAlignment(JLabel.CENTER);
		GridBagPut settingPanel = new GridBagPut(curPanel);
		settingPanel.fillBoth();
		settingPanel.putGridBag(progressBar, curPanel, 0, 0);
		settingPanel.putGridBag(jLabel, curPanel, 0, 1);
		frame.setContentPane(curPanel);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		
	}
	
	@Override
	protected Step6Result doInBackground() throws Exception {
		doBehind(1);
		if (!imageDealer.opts.singleChannel)
			doBehind(2);
		return null;
	}
	
	private void doBehind(int ch) {
		this.ch = ch;
		// ------------------------ Read Data ----------------------------- //
		Opts opts = imageDealer.opts;
		
		HashMap<Integer, ArrayList<int[]>> evtLst = new HashMap<>();
		int[][][] datR = new int[1][1][1];
		// read data
		publish(1);
		float[][][] datOrg;
		int[][][] label = new int[opts.H][opts.W][opts.T];
		Color[] labelColors = new Color[1];
		
		if (ch == 1) {
			datOrg = imageDealer.dat1;
			try {
				evtLst = Helper.readObjectFromFile(proPath, "evt1.ser", evtLst.getClass());
				datR = Helper.readObjectFromFile(proPath, "datR1.ser", datR.getClass());
				labelColors = Helper.readObjectFromFile(proPath, "evtColor1.ser", labelColors.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			int nEvt = evtLst.size();
			imageDealer.center.EvtNumber.setText(nEvt+"");
			for (int i = 1; i <= nEvt; i++) {
				Helper.setValue(label, evtLst.get(i), i);
			}
			imageDealer.label1 = label;
			imageDealer.nEvtCh1 = evtLst.size();
			imageDealer.labelColors1 = labelColors;
			imageDealer.datR1 = datR;
		} else {
			datOrg = imageDealer.dat2;
			try {
				evtLst = Helper.readObjectFromFile(proPath, "evt2.ser", evtLst.getClass());
				datR = Helper.readObjectFromFile(proPath, "datR2.ser", datR.getClass());
				labelColors = Helper.readObjectFromFile(proPath, "evtColor2.ser", labelColors.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			int nEvt = evtLst.size();
			imageDealer.center.EvtNumber.setText(imageDealer.center.EvtNumber.getText() + "|" + nEvt);
			for (int i = 1; i <= nEvt; i++) {
				Helper.setValue(label, evtLst.get(i), i);
			}
			imageDealer.label2 = label;
			imageDealer.labelColors2 = labelColors;
			imageDealer.datR2 = datR;
		}
		
		imageDealer.center.nEvt.setText("nEvt");
		
		// features
		System.out.println("Updating basic, network, region and landmark features");
		String proPath = imageDealer.proPath;
		System.out.println("Updating basic features");
		FtsLst ftsLstE = new FtsLst();
		
		publish(2);
		if(!imageDealer.step6Stg) {
			FeatureTopResult featureTopResult = Step6Helper.getFeaturesTop(datOrg, evtLst, opts);
			ftsLstE = featureTopResult.ftsLst;
			imageDealer.left.tableValueSetting(featureTopResult.minArea, featureTopResult.maxArea, featureTopResult.minPvalue,
					featureTopResult.maxPvalue, featureTopResult.minDecayTau, featureTopResult.maxDecayTau,
					featureTopResult.minDuration, featureTopResult.maxDuration, featureTopResult.mindffMax, featureTopResult.maxdffMax);
			float[] featureTable = new float[] {featureTopResult.minArea, featureTopResult.maxArea, featureTopResult.minPvalue, 
					featureTopResult.maxPvalue, featureTopResult.minDecayTau, featureTopResult.maxDecayTau,
					featureTopResult.minDuration, featureTopResult.maxDuration, featureTopResult.mindffMax, featureTopResult.maxdffMax};
			
			
			if (ch == 1) {
				imageDealer.dffMat1 = featureTopResult.dffMat;
				try {
					Helper.writeObjectToFile(proPath, "dffMat1.ser", featureTopResult.dffMat);  
		   			Helper.writeObjectToFile(proPath, "dMat1.ser", featureTopResult.dMat);  
		   			Helper.writeObjectToFile(proPath, "dffAlignedMat1.ser", featureTopResult.dffAlignedMat);  
		   			Helper.writeObjectToFile(proPath, "FtsTableParameters.ser", featureTable);  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				imageDealer.dffMat2 = featureTopResult.dffMat;
				try {
					Helper.writeObjectToFile(proPath, "dffMat2.ser", featureTopResult.dffMat);  
		   			Helper.writeObjectToFile(proPath, "dMat2.ser", featureTopResult.dMat);  
		   			Helper.writeObjectToFile(proPath, "dffAlignedMat2.ser", featureTopResult.dffAlignedMat);  
		   			Helper.writeObjectToFile(proPath, "FtsTableParameters.ser", featureTable);  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		}else {
			if (ch == 1) {
				try {
					ftsLstE = Helper.readObjectFromFile(proPath, "Fts1.ser", ftsLstE.getClass());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				try {
					ftsLstE = Helper.readObjectFromFile(proPath, "Fts2.ser", ftsLstE.getClass());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		// propagation features
		publish(3);
		if(opts.checkProp)
			Step6Helper.getFeaturesProTop(datR,evtLst,ftsLstE,opts);
		
		// region, landmark, network and save results
		publish(4);
		if(opts.checkNetwork)
			Step6Helper.updtFeatureRegionLandmarkNetworkShow(datR,evtLst,ftsLstE,opts, imageDealer, ch);
//		System.out.println("ftsE network length " + ftsLstE.network.nOccurSameLoc.length);
		
		
		publish(5);
		Step6Result result = new Step6Result(ftsLstE, null);
		if (ch == 1) {
			imageDealer.fts1 = ftsLstE;
			try {
				Helper.writeObjectToFile(proPath, "Fts1.ser", ftsLstE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			imageDealer.fts2 = ftsLstE;
			try {
				Helper.writeObjectToFile(proPath, "Fts2.ser", ftsLstE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		int total = 5;
		String str = "";
		String channelInfo = " Channel " + this.ch;
		switch(value) {
			case 1:
				str = "Load Previous Results " + value + "/" + total + channelInfo;
				break;
			case 2:
				str = "Updating basic features " + value + "/" + total + channelInfo;
				break;
			case 3:
				str = "Updating propagation features " + value + "/" + total + channelInfo;
				break;
			case 4:
				str = "Updating the region/network features " + value + "/" + total + channelInfo;
				break;
			case 5:
				str = "Save the Results " + value + "/" + total + channelInfo;
				break;
		}
		jLabel.setText(str);
	}
	
	@SuppressWarnings("resource")
	@Override
	protected void done() {
		frame.setVisible(false);
//		JOptionPane.showMessageDialog(null, "Step6 Finish!");
		imageDealer.left.jTPStatus = Math.max(imageDealer.left.jTPStatus, 6);
		imageDealer.left.nextButton.setEnabled(true);
		imageDealer.left.backButton.setEnabled(true);
		imageDealer.left.left3.setVisible(true);
		imageDealer.left.left4.setVisible(true);
		imageDealer.right.allFinished();
		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.dealImage();
				imageDealer.imageLabel.repaint();
			}
			
		}).start();
//		imageDealer.left.tableValueSetting(minArea,maxArea,minPvalue,maxPvalue,minDecayTau,maxDecayTau,minDuration,maxDuration,mindffMax,maxdffMax);
//		
		imageDealer.saveStatus();
		
		imageDealer.running = false;
	}	
	
}
