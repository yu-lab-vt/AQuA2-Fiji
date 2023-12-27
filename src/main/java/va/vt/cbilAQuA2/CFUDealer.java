package va.vt.cbilAQuA2;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import va.vt.cbilAQuA2.cfu.CFUInfo;
import va.vt.cbilAQuA2.cfu.CFUPreResult;
import va.vt.cbilAQuA2.cfu.GroupInfo;
import va.vt.cbilAQuA2.ui.CFUCenterPanel;
import va.vt.cbilAQuA2.ui.CFULeftPanel;
import va.vt.cbilAQuA2.ui.CFURightPanel;

public class CFUDealer {
	// channel1
	public float[][][] dat1 = null;
	public float[][][] dF1 = null;
	public int[][] cfuMap1 = null;
	float[][] avgPro1 = null;
	public CFUPreResult cfu_pre1 = null;
	public HashMap<Integer, CFUInfo> cfuInfo1 = null;
	public HashMap<Integer, GroupInfo> groupInfo = null;
	public int[] selectedGroupEvts = null;
	public HashSet<Integer> favCFUList1 = new HashSet<>();
	public ArrayList<Integer> pickList = new ArrayList<>();
	public HashMap<Integer, ArrayList<int[]>> CFUboundary1 = null;
	public Color[] CFUColors1 = null;
	public Color[] delayColors = null;
	
	// channel2
	public float[][][] dat2 = null;
	public float[][][] dF2 = null;
	public int[][] cfuMap2 = null;
	float[][] avgPro2 = null;
	public CFUPreResult cfu_pre2 = null;
	public HashMap<Integer, CFUInfo> cfuInfo2 = null;
	public HashSet<Integer> favCFUList2 = new HashSet<>();
	public HashMap<Integer, ArrayList<int[]>> CFUboundary2 = null;
	public Color[] CFUColors2 = null;
	
	public int nCFUch1 = 0;
	
	public Opts opts = null;
	public String proPath = null;
	ImageDealer imageDealer = null;
	JFrame cfuWindow = null;
	public CFULeftPanel left = null;
	public CFUCenterPanel center = null;
	public CFURightPanel right = null;
	public boolean viewPick = false;
	public boolean useDelayColor = false;
	public int width = 0;
	public int height = 0;
	public int maxImageWidth = 0;
	public int maxImageHeight = 0;
	
	public CFUDealer() {
		
	}
	
	public CFUDealer(ImageDealer imageDealer, JFrame cfuWindow) {
		this.dat1 = imageDealer.dat1;
		this.dF1 = imageDealer.dF1;
		this.dat2 = imageDealer.dat2;
		this.dF2 = imageDealer.dF2;
		this.proPath = imageDealer.proPath;
		this.opts = imageDealer.opts;
		this.imageDealer = imageDealer;
		this.cfuWindow = cfuWindow;
		getAvgData();
	}
	
	public void getAvgData() {
		int H = dat1.length;
		int W = dat1[0].length;
		int T = dat1[0][0].length;
		width = W;
		height = H;
		
		avgPro1 = imageDealer.avgImage1;
		avgPro2 = imageDealer.avgImage2;
		
//		avgPro1 = new float[H][W];
//		for(int x = 0; x < H; x++) {
//			for (int y = 0; y < W; y++) {
//				for (int t = 0; t < T; t++) {
//					avgPro1[x][y] += dat1[x][y][t] / T;
//				}
//			}
//		}
//		
//		if (!opts.singleChannel) {
//			avgPro2 = new float[H][W];
//			for(int x = 0; x < H; x++) {
//				for (int y = 0; y < W; y++) {
//					for (int t = 0; t < T; t++) {
//						avgPro2[x][y] += dat2[x][y][t] / T;
//					}
//				}
//			}
//		}
		
	}
	
	public void returnResults() {
		imageDealer.cfuInfo1 = this.cfuInfo1;
		imageDealer.groupInfo = this.groupInfo;
		imageDealer.cfuInfo2 = this.cfuInfo2;
	}

	public void close() {
		this.cfuWindow.dispose();
	}

	public void dealImage() {
		
		if (opts.singleChannel) {
			BufferedImage result = dealRaw(right.brightSlider.getValue(), 1);
			if (cfuInfo1!=null && cfuInfo1.size() > 0)
				result = addColor(result, 1);
			center.imageLabel.setIcon(new ImageIcon(result.getScaledInstance(maxImageWidth, maxImageHeight, Image.SCALE_DEFAULT)));
			center.imageLabel.repaint();
		} else {
			BufferedImage result = dealRaw(right.brightSlider.getValue(), 1);
			if (cfuInfo1!=null && cfuInfo1.size() > 0)
				result = addColor(result, 1);
			center.imageLabelLeft.setIcon(new ImageIcon(result.getScaledInstance(maxImageWidth, maxImageHeight, Image.SCALE_DEFAULT)));
			center.imageLabelLeft.repaint();
			
			BufferedImage result2 = dealRaw(right.brightSlider.getValue(), 2);
			if (cfuInfo2!=null && cfuInfo2.size() > 0)
				result2 = addColor(result2, 2);
			center.imageLabelRight.setIcon(new ImageIcon(result2.getScaledInstance(maxImageWidth, maxImageHeight, Image.SCALE_DEFAULT)));
			center.imageLabelRight.repaint();
		}
		
		
	}
	
	private BufferedImage addColor(BufferedImage result, int ch) {
		
		CFUInfo curCFU;
		int W = width;
		int H = height;
		HashMap<Integer, CFUInfo> cfuInfo;
		Color[] CFUColors;
		if (ch == 1) {
			cfuInfo = cfuInfo1;
			CFUColors = CFUColors1;
		} else {
			cfuInfo = cfuInfo2;
			CFUColors = CFUColors2;
		}
		
		int nCFU =  cfuInfo.size();
		if (CFUColors == null || CFUColors.length < nCFU) {
			CFUColors = randomColor(nCFU, ch);
		}
		
		int[] p;
		Color curColor, curImgColor;
		float colorScale = right.colorSlider.getValue() / 100;
		
		HashSet<Integer> CFUinGroup = new HashSet<Integer>();
		if (selectedGroupEvts!=null) {
			for (int i = 0; i < selectedGroupEvts.length; i++) {
				CFUinGroup.add(selectedGroupEvts[i]);
			}
		}
		
		for (Map.Entry<Integer, CFUInfo> entry : cfuInfo.entrySet()) {
			curCFU = entry.getValue();
			int label = entry.getKey();
			if (CFUinGroup.size() > 0 && (!CFUinGroup.contains(label  + (ch - 1) * nCFUch1)))
				continue;
			HashSet<Integer> pixLst = curCFU.pixLst;
			float[][] region = curCFU.region;
			for (int index : pixLst) {
				p = Helper.ind2sub(H, W, index);
				if (useDelayColor)
					curColor = delayColors[label-1 + (ch - 1) * nCFUch1];
				else
					curColor = CFUColors[label-1];
				float saturation = region[p[0]][p[1]];
				curImgColor = new Color(result.getRGB(p[1], p[0]));
				int red = (int)(curColor.getRed()*colorScale*saturation + curImgColor.getRed()*1.2);
				int green = (int)(curColor.getGreen()*colorScale*saturation + curImgColor.getGreen()*1.2);
				int blue = (int)(curColor.getBlue()*colorScale*saturation + curImgColor.getBlue()*1.2);
				red = red>255?255:red;
				green = green>255?255:green;
				blue = blue>255?255:blue;
				result.setRGB(p[1], p[0], new Color(red,green,blue,255).getRGB());
			}
		} 
		
		return result;
	}

	private Color[] randomColor(int nCFU, int ch) {
		// TODO Auto-generated method stub
		if (ch == 1) {
			CFUColors1 = new Color[nCFU];
			int colorBase = 80;
			Random rv = new Random();
			for(int i=0;i<nCFU;i++) {
				CFUColors1[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
			}
			return CFUColors1;
		} else {
			CFUColors2 = new Color[nCFU];
			int colorBase = 80;
			Random rv = new Random();
			for(int i=0;i<nCFU;i++) {
				CFUColors2[i] = new Color(colorBase + rv.nextInt(256-colorBase), colorBase + rv.nextInt(256-colorBase),colorBase + rv.nextInt(256-colorBase));
			}
			return CFUColors2;
		}
		
	}

	public BufferedImage dealRaw(int contrast, int ch) {
		int H = dat1.length;
		int W = dat1[0].length;
		
		float[][] avgPro;
		if (ch == 1) {
			avgPro = avgPro1;
		}else {
			avgPro = avgPro2;
		}

		BufferedImage prod = new BufferedImage(W, H, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=0;i<W;i++) {
			for(int j=0;j<H;j++) {
				float gray = 255 * avgPro[i][j];
				if (gray >= 255) {
	                prod.setRGB(i, j, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= 0) {
	                prod.setRGB(i, j, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                prod.setRGB(i, j, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
}
