package va.vt.cbilAQuA2.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;

public class LabelRead extends SwingWorker<int[][][], Integer> {
	JFrame frame = new JFrame("Reading the labels");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	int index = 0;
	String proPath = null;
	
	public LabelRead(ImageDealer imageDealer, int index) {
		this.imageDealer = imageDealer;
		this.index = index;
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
	
	@Override
	protected int[][][] doInBackground() throws Exception {

		publish(1);
		String file = null;
		imageDealer.center.nEvt.setText("nEvt");
		switch(index) {
			case 1:
				file = "arLst"; 
				imageDealer.center.nEvt.setText("nAct");
				break;
			case 2:
				file = "subEvtLst"; break;
			case 3:
				file = "sdLst"; break;
			case 4:
				file = "seLst"; 
				imageDealer.center.nEvt.setText("nSe");
				break;
			case 5:
				file = "evt"; break;
			case 6:
				file = "gloEvt"; break;
			default: 
				file  = null; break;
		}
		doBehind(file, 1);
		if (!imageDealer.opts.singleChannel)
			doBehind(file, 2);
		return null;
	}
	
	private void doBehind(String file, int ch) {
		if (file == null)
			return;
		int H = imageDealer.opts.H;
		int W = imageDealer.opts.W;
		int T = imageDealer.opts.T;
		int[][][] labels = new int[H][W][T];
		
		String ext;
		if (ch == 1)
			ext = "1.ser";
		else
			ext = "2.ser";
		
		HashMap<Integer, ArrayList<int[]>> evts = null;
		if (file!=null) {
			evts = new HashMap<Integer, ArrayList<int[]>>();

			try {
				evts = Helper.readObjectFromFile(proPath, file + ext, evts.getClass());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			for (Map.Entry<Integer, ArrayList<int[]>> entry : evts.entrySet()) {
				Helper.setValue(labels, entry.getValue(), entry.getKey());
			}
		}
		if (ch == 1) {
			imageDealer.label1 = labels;
			imageDealer.center.EvtNumber.setText(evts.size() +"");
		} else {
			imageDealer.label2 = labels;
			imageDealer.center.EvtNumber.setText(imageDealer.center.EvtNumber.getText() + "|" + evts.size());
		}
			
	}
	
	protected void process(List<Integer> chunks) {

		String str = "Reading the data";
		jLabel.setText(str);
	}
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish!");
		imageDealer.dealImage();
	}
}
