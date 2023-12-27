package va.vt.cbilAQuA2.ui;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;


public class CFUGUI{
	public void start(ImageDealer imageDealer) {
		// Show the window
		JFrame cfuWindow = new JFrame("AQuA2 CFU");
		cfuWindow.setSize(1650,850);
		cfuWindow.setUndecorated(false);
		cfuWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		cfuWindow.setLocationRelativeTo(null);
		cfuWindow.setMinimumSize(new Dimension(1660,900));
		cfuWindow.setResizable(false);
		
		/*  ----------------------------- ImagePlus ----------------------------------- */
		CFUDealer cfuDealer = new CFUDealer(imageDealer, cfuWindow);
		
		/*  ------------------------------ For Test ----------------------------------- */
		CFULeftPanel left = new CFULeftPanel(cfuDealer);
		CFUCenterPanel center = new CFUCenterPanel(cfuDealer);
		CFURightPanel right = new CFURightPanel(cfuDealer);
		
		JPanel centerGroup = center.createPanel();
		JPanel leftGroup = left.createPanel();
		JPanel rightGroup = right.createPanel();		

		// Window set
		GridBagPut settingWindow = new GridBagPut(cfuWindow);
		settingWindow.setAnchorNorthWest();
		settingWindow.putGridBag(leftGroup, cfuWindow, 0, 0);
		settingWindow.putGridBag(centerGroup, cfuWindow, 1, 0);
		settingWindow.putGridBag(rightGroup, cfuWindow, 2, 0);
		
		
		cfuWindow.setTitle("AQuA2 CFU detection" + cfuDealer.opts.filename1);
 
		cfuWindow.setVisible(true);
		try {
			cfuDealer.dealImage();
			Thread.sleep(200);
			if (cfuDealer.opts.singleChannel)
				cfuDealer.center.imageLabel.repaint();
			else {
				cfuDealer.center.imageLabelLeft.repaint();
				cfuDealer.center.imageLabelRight.repaint();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void  main(String[] args) {
		ImageDealer imageDealer = new ImageDealer();
		float[][][] dat1 = new float[1][1][1];
		float[][][] dat2 = new float[1][1][1];
		float[][][] dF1 = new float[1][1][1];
		float[][][] dF2 = new float[1][1][1];
		String proPath = "D:\\Test\\";
		Opts opts = new Opts(1);
		try {
			dat1 = Helper.readObjectFromFile(proPath, "datOrg1.ser", dat1.getClass());
			dF1 = Helper.readObjectFromFile(proPath, "dF1.ser", dF1.getClass());
			dat2 = Helper.readObjectFromFile(proPath, "datOrg2.ser", dat2.getClass());
			dF2 = Helper.readObjectFromFile(proPath, "dF2.ser", dF2.getClass());
			opts = Helper.readObjectFromFile(proPath, "opts.ser", opts.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		opts.singleChannel = true;
		
		imageDealer.dat1 = dat1;
		imageDealer.dF1 = dF1;
		imageDealer.dat2 = dat2;
		imageDealer.dF2 = dF2;
		imageDealer.proPath = proPath;
		imageDealer.opts = opts;
		

		CFUGUI cfuGui = new CFUGUI();
		cfuGui.start(imageDealer);
	}
}
