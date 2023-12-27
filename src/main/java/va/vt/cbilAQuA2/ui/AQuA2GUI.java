package va.vt.cbilAQuA2.ui;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import va.vt.cbilAQuA2.ImageDealer;


public class AQuA2GUI{
	public void start(String path, String path2, String proPath, boolean load, float ts, float ss, int border, int index) {
		// Show the window
		JFrame aquaWindow = new JFrame("AQuA2");
		aquaWindow.setSize(1650,850);
		aquaWindow.setUndecorated(false);
		aquaWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		aquaWindow.setLocationRelativeTo(null);
		aquaWindow.setMinimumSize(new Dimension(1660,880));
		aquaWindow.setResizable(false);
		
		/*  ----------------------------- ImagePlus ----------------------------------- */
		MyImageLabel imageLabel = new MyImageLabel(true);
		ImageDealer imageDealer = new ImageDealer(path, path2, proPath,border,index);
		MaskBuilderLabel builderImageLabel = new MaskBuilderLabel(imageDealer);
		
		imageDealer.setImageLabel(imageLabel);
		imageLabel.setImageDealer(imageDealer);
		imageDealer.setBuilderImageLabel(builderImageLabel);
		imageDealer.setWindow(aquaWindow);
		
		/*  ------------------------------ For Test ----------------------------------- */
		LeftGroupPanel left = new LeftGroupPanel(imageDealer);
		CenterGroupPanel center = new CenterGroupPanel(imageDealer);
		RightGroupPanel right = new RightGroupPanel(imageDealer);
		
		imageDealer.setPanelGroup(left, center, right,aquaWindow);
		if(!Float.isNaN(ts))
			imageDealer.setImageConfig(ts,ss);
		
		JPanel centerGroup = center.createPanel();
		JPanel leftGroup = left.createPanel();
		JPanel rightGroup = right.createPanel();		

		// Window set
		GridBagPut settingWindow = new GridBagPut(aquaWindow);
		settingWindow.setAnchorNorthWest();
		settingWindow.putGridBag(leftGroup, aquaWindow, 0, 0);
		settingWindow.putGridBag(centerGroup, aquaWindow, 1, 0);
		settingWindow.putGridBag(rightGroup, aquaWindow, 2, 0);
		
		if (!imageDealer.opts.singleChannel) {
			imageDealer.center.sideButton.setSelected(true);
			imageDealer.center.sideButton.setEnabled(false);
			imageDealer.right.chLeftJCB.setEnabled(true);
			imageDealer.right.chRightJCB.setEnabled(true);
			imageDealer.right.chRightJCB.setSelectedIndex(1);
		}
		
		
		
		if(load)
			imageDealer.load(proPath);
		
		aquaWindow.setTitle("AQuA2: " + imageDealer.opts.filename1);

		aquaWindow.setVisible(true);
		try {
			imageDealer.dealImage();
			Thread.sleep(200);
			imageLabel.repaint();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// TO BE detelted
//		imageDealer.left.left3.setVisible(true);
//		imageDealer.left.left4.setVisible(true);
//		imageDealer.right.allFinished();
	}
	
	public static void  main(String[] args) {
		String path = "F:\\Test_data\\example_channel1.tif";
		String path2 = "F:\\Test_data\\example_channel2.tif";
		String propath = "D:\\Test\\";
		AQuA2GUI aq = new AQuA2GUI();
		aq.start(path,path2,propath,false,1,1,0,1);
		
		
	}
}
