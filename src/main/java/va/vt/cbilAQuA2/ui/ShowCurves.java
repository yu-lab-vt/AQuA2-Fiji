package va.vt.cbilAQuA2.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.ui.*;

public class ShowCurves {
	
	
	public static void showCurves(ArrayList<Integer> indexLst, ImageDealer imageDealer) {		
		JFrame frame = new JFrame("Features for favorite events");
		frame.setSize(850,750);
		frame.setUndecorated(false);
		frame.setLocationRelativeTo(null);
		
		JPanel pane = new JPanel();
		
		DrawCurveLabel2 label = new DrawCurveLabel2(indexLst,imageDealer);
		label.setPreferredSize(new Dimension(850,650));
		JButton saveCurves = new JButton("SaveCurves") ;
		pane.add(label);
		pane.add(saveCurves);
		pane.setPreferredSize(new Dimension(850,750));
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		
		frame.add(pane);
		
		
		saveCurves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String savePath = chooser.getSelectedFile().getPath();
				    savePath = savePath.replaceAll("\\\\", "\\\\\\\\");
					System.out.println(savePath);
					JLabel canvas = label;
					BufferedImage img=new BufferedImage(canvas.getWidth(),canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = img.createGraphics();
					canvas.printAll(g2d);
					File f=new File(savePath + ".jpg");
					try {
						ImageIO.write(img, "jpg", f);
					} catch (IOException ee) {
						ee.printStackTrace();
					}
					g2d.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "No Selection ");
				}
				
			}
		});
		
		
		
		frame.setVisible(true);
		
	}

	
	
}
