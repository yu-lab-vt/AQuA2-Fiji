package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.run.RiseInfo;

public class LoadProject extends SwingWorker<Void, Integer> {
	JFrame frame = new JFrame("Read");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Read the Project");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	String proPath = null;
	public LoadProject(ImageDealer imageDealer) {
		this.imageDealer = imageDealer;
		proPath = imageDealer.proPath;
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
		jLabel.setHorizontalAlignment(JLabel.CENTER);;
		GridBagPut settingPanel = new GridBagPut(curPanel);
		settingPanel.fillBoth();
		settingPanel.putGridBag(progressBar, curPanel, 0, 0);
		settingPanel.putGridBag(jLabel, curPanel, 0, 1);
		frame.setContentPane(curPanel);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground() throws Exception {
		imageDealer.drawRegion = true;
		Status status = new Status();
		try {
			status = Helper.readObjectFromFile(proPath, "Status.ser", status.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Opts opts = status.opts;
		imageDealer.labelColors1 = status.labelColors1;
		imageDealer.labelColors2 = status.labelColors2;
		imageDealer.left.curStatus = status.curStatus;
		imageDealer.left.jTPStatus = status.jTPStatus;
		imageDealer.regionMark = status.regionMark;
		imageDealer.regionMarkLabel = status.regionMarkLabel;
		imageDealer.landMark = status.landMark;
		imageDealer.landMarkLabel = status.landMarkLabel;
		
		imageDealer.left.drawlistener.setRegion(imageDealer.regionMark);
		imageDealer.left.drawlistener2.setRegion(imageDealer.landMark);
		imageDealer.left.removeListener.setRegion(imageDealer.regionMark);
		imageDealer.left.removeListener.setRegionLabel(imageDealer.regionMarkLabel);
		imageDealer.left.removeListener2.setRegion(imageDealer.landMark);
		imageDealer.opts = opts;

		imageDealer.left.jTFthr.setText(imageDealer.opts.thrARScl+"");
		imageDealer.left.jTFsmo.setText(imageDealer.opts.smoXY + "");
		imageDealer.left.jTFminsize.setText(imageDealer.opts.minSize + "");
		imageDealer.left.jTFminDur.setText(imageDealer.opts.minDur + "");
		
		imageDealer.left.jTFseedSize.setText(imageDealer.opts.seedSzRatio + "");
		imageDealer.left.jTFzScore.setText(imageDealer.opts.sigThr + "");
		imageDealer.left.jTFmergeDis.setText(imageDealer.opts.maxDelay + "");
		
		imageDealer.left.jTFsourceRatio.setText(imageDealer.opts.sourceSzRatio + "");
		imageDealer.left.jTFsensitivity.setText(imageDealer.opts.sourceSensitivity + "");
		
		imageDealer.left.jTFdetectGlo.setSelected(imageDealer.opts.detectGlo);
		imageDealer.left.jTFgloDur.setText(imageDealer.opts.gloDur + "");
		
		imageDealer.left.jTFignoreTau.setSelected(imageDealer.opts.ignoreTau);
		imageDealer.left.jTFcheckProp.setSelected(imageDealer.opts.checkProp);
		imageDealer.left.jTFcheckNetwork.setSelected(imageDealer.opts.checkNetwork);
		
		if(imageDealer.left.jTPStatus>=1) {
			try {
				imageDealer.dF1 = new float[1][1][1];
				imageDealer.dF1 = Helper.readObjectFromFile(proPath, "dF1.ser", imageDealer.dF1.getClass());
				if (!imageDealer.opts.singleChannel)
					imageDealer.dF2 = Helper.readObjectFromFile(proPath, "dF2.ser", imageDealer.dF1.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		int index = 0;
				
		if(imageDealer.left.jTPStatus>=2) {
			imageDealer.right.typeJCB.addItem("Step2: Active regions");
			index = 1;
		}
		if(imageDealer.left.jTPStatus>=3) {
			imageDealer.right.typeJCB.addItem("Step3a: Watershed results");
			imageDealer.right.typeJCB.addItem("Step3aa: seeds");
			imageDealer.right.typeJCB.addItem("Step3b: Super Events");
			index = 4;
		}
		if(imageDealer.left.jTPStatus>=4) {
			imageDealer.right.typeJCB.addItem("Events");
			index = 5;
		}
		if(imageDealer.left.jTPStatus>=5) {
			imageDealer.right.typeJCB.addItem("Global Events");
			index = 5;
		}
		if(imageDealer.left.jTPStatus>=6) {
			try {
				imageDealer.riseLst1 = new HashMap<Integer, RiseInfo>();
				imageDealer.riseLst1 = Helper.readObjectFromFile(proPath, "riseLst1.ser", imageDealer.riseLst1.getClass());
				if (!imageDealer.opts.singleChannel)
					imageDealer.riseLst2 = Helper.readObjectFromFile(proPath, "riseLst2.ser", imageDealer.riseLst1.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
//		imageDealer.right.typeJCB.setSelectedIndex(Math.min(6, imageDealer.left.jTPStatus));
		
		
		if(index!=0) {
			try {
				LabelRead labelread = new LabelRead(imageDealer,index);
				labelread.setting();
				labelread.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
		
		
		if(imageDealer.left.jTPStatus>=4) {
			try {
				imageDealer.datR1 = new int[1][1][1];
				imageDealer.datR1 = Helper.readObjectFromFile(proPath, "datR1.ser", imageDealer.datR1.getClass());
				if (!imageDealer.opts.singleChannel)
					imageDealer.datR2 = Helper.readObjectFromFile(proPath, "datR2.ser", imageDealer.datR1.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
		
		
		for(int i=0;i<=imageDealer.left.jTPStatus;i++) {
			if(i!=6)
				imageDealer.left.jTP.setEnabledAt(i, true);
		}
		imageDealer.left.dealBuilder();
		imageDealer.left.jTP.setSelectedIndex(Math.min(imageDealer.left.jTPStatus,5));
		
		if(imageDealer.left.jTPStatus==6) {
			imageDealer.left.nextButton.setText("CFU detect");
			imageDealer.left.backButton.setEnabled(true);
			imageDealer.left.left3.setVisible(true);
			imageDealer.left.left4.setVisible(true);
			imageDealer.right.allFinished();
			try {
				float[] ftsTable = new float[1];
				ftsTable = Helper.readObjectFromFile(proPath, "FtsTableParameters.ser", ftsTable.getClass());
				imageDealer.left.tableValueSetting(ftsTable[0],ftsTable[1],ftsTable[2],ftsTable[3],ftsTable[4],ftsTable[5],ftsTable[6],ftsTable[7],ftsTable[8],ftsTable[9]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				imageDealer.dffMat1 = new float[1][1][1];
				imageDealer.dffMat1 = Helper.readObjectFromFile(proPath, "dffMat1.ser", imageDealer.dffMat1.getClass());
				if (!imageDealer.opts.singleChannel)
					imageDealer.dffMat2 = Helper.readObjectFromFile(proPath, "dffMat2.ser", imageDealer.dffMat1.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				imageDealer.fts1 = new FtsLst();
				imageDealer.fts1 = Helper.readObjectFromFile(proPath, "Fts1.ser", imageDealer.fts1.getClass());
				if (!imageDealer.opts.singleChannel)
					imageDealer.fts2 = Helper.readObjectFromFile(proPath, "Fts2.ser", imageDealer.fts1.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		imageDealer.center.ts = opts.frameRate;
		imageDealer.right.typeJCB.setEnabled(true);
		
		imageDealer.center.rightJCB.setSelectedIndex(1);
		if (!imageDealer.opts.singleChannel)
			imageDealer.center.leftJCB.setSelectedIndex(1);

		new Thread(new Runnable() {

			@Override
			public void run() {
				imageDealer.imageLabel.repaint();
				imageDealer.dealImage();
			}
			
		}).start();
		
		return null;
	}
	
	protected void process(List<Integer> chunks) {
	}
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish Reading");
	}
}
