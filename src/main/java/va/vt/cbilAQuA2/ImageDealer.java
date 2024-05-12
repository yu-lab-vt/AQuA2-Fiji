package va.vt.cbilAQuA2;
/**
 * Deal with Image, change the brightness, change the window size, and other functions 
 */
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.cfu.CFUOpts;
import va.vt.cbilAQuA2.cfu.CFUInfo;
import va.vt.cbilAQuA2.cfu.CFUPreResult;
import va.vt.cbilAQuA2.cfu.GroupInfo;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.io.IO_AQuALoadOpts;
import va.vt.cbilAQuA2.io.IO_AQuAOutPut;
import va.vt.cbilAQuA2.io.IO_AQuAOutPutOpts;
import va.vt.cbilAQuA2.run.RiseInfo;
import va.vt.cbilAQuA2.run.Step1;
import va.vt.cbilAQuA2.run.Step2;
import va.vt.cbilAQuA2.run.Step3;
import va.vt.cbilAQuA2.run.Step4;
import va.vt.cbilAQuA2.run.Step5;
import va.vt.cbilAQuA2.run.Step6;
import va.vt.cbilAQuA2.ui.CenterGroupPanel;
import va.vt.cbilAQuA2.ui.ColorLabel2;
import va.vt.cbilAQuA2.ui.DrawCurveLabel;
import va.vt.cbilAQuA2.ui.LeftGroupPanel;
import va.vt.cbilAQuA2.ui.LoadProject;
import va.vt.cbilAQuA2.ui.MaskBuilderLabel;
import va.vt.cbilAQuA2.ui.MyImageLabel;
import va.vt.cbilAQuA2.ui.RightGroupPanel;
import va.vt.cbilAQuA2.ui.Status;

public class ImageDealer {

	public static final int DEFAULT_CONTRAST_BRIGHTNESS = 100;
	public static final Point DEFAULT_STARTPOINT= null;
	public static final Point DEFAULT_ENDPOINT= null;
	
	public float minDat = Float.POSITIVE_INFINITY;
	public float maxDat = 0;
	public int contrast = DEFAULT_CONTRAST_BRIGHTNESS;
	public int contrastl = DEFAULT_CONTRAST_BRIGHTNESS;
	public int contrastr = DEFAULT_CONTRAST_BRIGHTNESS;
	public Point startPoint = null;
	public Point endPoint = null;
	public int length = 1;	// little images's size	
	public float colorScale = 0.5f;	// adjust color brightness
	public boolean running = false;
	public int colorBase = 80;
	int border = 0;
	public int maxImageWidth = 0;
	public int maxImageHeight = 0;
	int sw = 0;
	int sh = 0;
	public int curFrame = 0;
	public boolean gaussStatus = false;
	
	
	// channel1
	public int[][][] datR1 = null;
	public float[][] curBuilderImage1 = null;
	public float[][] avgImage1 = null;
	float[][] maxImage1 = null;
	public float[][][] dat1 = null;
	public float[][][] dF1 = null;
	public int[][][] label1 = null;
	public Color[] labelColors1 = null;
	public float[][][] dffMat1 = null;
	public HashSet<Integer> featureTableList1 = new HashSet<>();
	public FtsLst fts1 = null;
	public CFUPreResult cfu_pre1 = null;
	public HashMap<Integer, CFUInfo> cfuInfo1 = null;
	public HashMap<Integer, GroupInfo> groupInfo = null;
	public CFUOpts cfuOpts = null;
	public ImagePlus imgPlus1 = null;
	ImageProcessor imgProcessor1 = null;
	public HashMap<Integer, RiseInfo> riseLst1 = null;
	
	// channel1
	public int[][][] datR2 = null;
	public float[][] curBuilderImage2 = null;
	public float[][] avgImage2 = null;
	float[][] maxImage2 = null;
	public float[][][] dat2 = null;
	public float[][][] dF2 = null;
	public int[][][] label2 = null;
	public Color[] labelColors2 = null;
	public float[][][] dffMat2 = null;
	public HashSet<Integer> featureTableList2 = new HashSet<>();
	public FtsLst fts2 = null;
	public CFUPreResult cfu_pre2 = null;
	public HashMap<Integer, CFUInfo> cfuInfo2 = null;
	public ImagePlus imgPlus2 = null;
	ImageProcessor imgProcessor2 = null;
	public HashMap<Integer, RiseInfo> riseLst2 = null;
	
	
	public int maxBuilderWidth = 0;
	public int maxBuilderHeight = 0;
	public MyImageLabel imageLabel = null;
	public MyImageLabel CFULabel = null;
	public MaskBuilderLabel builderImageLabel = null;
	MyImageLabel leftImageLabel = null;
	MyImageLabel rightImageLabel = null;
	DrawCurveLabel curveLabel = null;
	
	public LeftGroupPanel left = null;
	public CenterGroupPanel center = null;
	public RightGroupPanel right = null;
	
	public JFrame window = null;
	public boolean status = true;
	

	// Step1 variables
	public int pages = 0;
    public int width = 0;
    public int height = 0;
	public boolean drawRegion = false;
	public Opts opts = null;
	public HashSet<Integer> deleteColorSet = new HashSet<>();
	public HashSet<Integer> deleteColorSet2 = new HashSet<>();
	String path = null;
	String path2 = null;
	public String proPath = null;
	public int[][] cfuMap = null;
	
	// mark
	public boolean[][] regionMark = null;
	public int[][] regionMarkLabel = null;
	public boolean[][] landMark = null;
	public int[][] landMarkLabel = null;
	ArrayList<ArrayList<Point>> regionMarkLst = null;
	ArrayList<ArrayList<Point>> landMarkLst = null;
	public HashMap<Integer,String> nameLst = null;
	public HashMap<Integer,String> nameLstLandMark = null;
	
	// result Curve
	public boolean step6Stg = false;
	
	// ROI
	public Point ROIstart = null;
	public Point ROIend = null;
	public boolean drawROI = false;
	public int nEvtCh1;
	
	public ImageDealer() {
	}
	
	public ImageDealer(String path, String path2, String proPath, int border, int index) {
		this.path = path;
		this.path2 = path2;
		this.proPath = proPath;
		this.border = border;

		opts = new Opts(index);
		
		// filename
		imgPlus1 = new ImagePlus(path);
		String[] words = path.split("\\\\");
		opts.filename1 = words[words.length-1];
		if (!path2.isEmpty()) {
			opts.singleChannel = false;
			imgPlus2 = new ImagePlus(path2);
			String[] words2 = path.split("\\\\");
			opts.filename2 = words2[words2.length-1];
		}
				
		// data propertry
		opts.BitDepth = imgPlus1.getBitDepth();
		width = imgPlus1.getWidth();
		height = imgPlus1.getHeight();
		System.out.println("H: " + height + " W: " + width);
		pages = imgPlus1.getImageStackSize();
		opts.W = width;
		opts.H = height;
		opts.T = pages;
		
		ImageStack stk = imgPlus1.getStack().convertToFloat();
		imgPlus1.setStack(stk);
		imgProcessor1 = imgPlus1.getProcessor();
		regionMark = new boolean[width][height];
		landMark = new boolean[width][height];
		regionMarkLabel = new int[width][height];
		landMarkLabel = new int[width][height];
		label1 = new int[height][width][pages];
		
		avgImage1 = new float[width][height];
		maxImage1 = new float[width][height];
		curBuilderImage1 = new float[width][height];
		
		nameLst = new HashMap<>();
		nameLstLandMark = new HashMap<>();
		for(int x = border;x<width-border;x++) {
			for(int y = border;y<height-border;y++) {
				regionMark[x][y] = true;
				regionMarkLabel[x][y] = 1;
			}
		}
		nameLst.put(1,1+"");
		
		// get range
		for(int k = 1;k<=pages;k++) {
			imgPlus1.setPosition(k);
			float[][] f = imgProcessor1.getFloatArray();
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					if (regionMark[i][j]) {
						maxDat = (int) Math.max(maxDat, f[i][j]);
						minDat = (int) Math.min(minDat, f[i][j]);
					}
				}
			}
		}
		
		if (!opts.singleChannel) {
			stk = imgPlus2.getStack().convertToFloat();
			imgPlus2.setStack(stk);
			imgProcessor2 = imgPlus2.getProcessor();
			label2 = new int[height][width][pages];
			avgImage2 = new float[width][height];
			maxImage2 = new float[width][height];
			curBuilderImage2 = new float[width][height];
			
			// get range
			for(int k = 1;k<=pages;k++) {
				imgPlus2.setPosition(k);
				float[][] f = imgProcessor2.getFloatArray();
				for(int i = 0;i<width;i++) {
					for(int j=0;j<height;j++) {
						if (regionMark[i][j]) {
							maxDat = (int) Math.max(maxDat, f[i][j]);
							minDat = (int) Math.min(minDat, f[i][j]);
						}
					}
				}
			}
		}
		
		// scale these
		float range = maxDat - minDat;
		for(int i = 0;i<width;i++) {
			for(int j=0;j<height;j++) {
				curBuilderImage1[i][j] = (curBuilderImage1[i][j] - minDat) / range;
				avgImage1[i][j] = (avgImage1[i][j] - minDat) / range;
				maxImage1[i][j] = (maxImage1[i][j] - minDat) / range;
			}
		}
		
		dat1 = new float[height][width][pages];
		for(int k = 1;k<=pages;k++) {
			imgPlus1.setPosition(k);
			float[][] f = imgProcessor1.getFloatArray();
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					f[i][j] = (f[i][j] - minDat) / range;
					curBuilderImage1[i][j] += f[i][j]/pages;
					avgImage1[i][j] += f[i][j]/pages;
					maxImage1[i][j] = Math.max(maxImage1[i][j], f[i][j]);
					dat1[j][i][k - 1] = f[i][j];
				}
			}
			imgProcessor1.setFloatArray(f);
		}
		
		imgPlus1.setPosition(1);
		
		if (!opts.singleChannel) {
			// scale these
			for(int i = 0;i<width;i++) {
				for(int j=0;j<height;j++) {
					curBuilderImage2[i][j] = (curBuilderImage2[i][j] - minDat) / range;
					avgImage2[i][j] = (avgImage2[i][j] - minDat) / range;
					maxImage2[i][j] = (maxImage2[i][j] - minDat) / range;
				}
			}
			
			dat2 = new float[height][width][pages];
			for(int k = 1;k<=pages;k++) {
				imgPlus2.setPosition(k);
				float[][] f = imgProcessor2.getFloatArray();
				for(int i = 0;i<width;i++) {
					for(int j=0;j<height;j++) {
						f[i][j] = (f[i][j] - minDat) / range;
						curBuilderImage2[i][j] += f[i][j]/pages;
						avgImage2[i][j] += f[i][j]/pages;
						maxImage2[i][j] = Math.max(maxImage2[i][j], f[i][j]);
						dat2[j][i][k - 1] = f[i][j];
					}
				}
				imgProcessor2.setFloatArray(f);
			}
			
			imgPlus2.setPosition(1);
		}
		
		
		opts.minValueDat = minDat;
		opts.maxValueDat = maxDat;
		minDat = 0;
		maxDat = 1;
		
	}
	
	public float getMin() {
		return minDat;
	}
	
	public float getMax() {
		return maxDat;
	}
	
	public void setMin(int min) {
		System.out.println(min);
		this.minDat = (float) min/100;
	}
	public void setMax(int max) {
		System.out.println(max);
		this.maxDat = (float) max/100;
	}
	public void setConstrast(int contrast) {
		this.contrast = contrast;
	}
	
	public void setConstrastl(int contrast) {
		this.contrastl = contrast;
	}
	
	public void setConstrastr(int contrast) {
		this.contrastr = contrast;
	}
	public void setArea(Point dragStartScreen, Point dragEndScreen) {
		startPoint = dragStartScreen;
		endPoint = dragEndScreen;
	}
	public void setImageLabel(MyImageLabel imageLabel) {
		this.imageLabel = imageLabel;
		regionMarkLst = imageLabel.getList1();
		landMarkLst = imageLabel.getList2();
	}
	public void setCFUImageLabel(MyImageLabel imageLabel) {
		this.CFULabel = imageLabel;
	}
	public void setTwoLabels (MyImageLabel left, MyImageLabel right) {
		leftImageLabel = left;
		rightImageLabel = right;
	}
	public void setLength(int sw, int sh) {
		this.sw = sw;
		this.sh = sh;
	}
	public void setPanelGroup(LeftGroupPanel left, CenterGroupPanel center, RightGroupPanel right, JFrame aquaWindow) {
		this.left = left;
		this.center = center;
		this.right = right;
		window = aquaWindow;
	}
	public void setWindow(JFrame window) {
		this.window = window;
	}
	public void setPage(int page) {
		curFrame = page;
		imgPlus1.setPosition(page+1);
	}
	public void setStep1(float sigma) {
		opts.smoXY = sigma;
	}
	public void changeSignalDrawRegionStatus() {
		drawRegion = true;
	}
	public void changeStatus() {
		status = !status;
	}
	
	public Point getStartPoint() {
		return startPoint;
	}
	public Point getEndPoint() {
		return endPoint;
	}
	public double getWidth() {
		if(endPoint == null)
			return 500;
		else
			return endPoint.getX() - startPoint.getX();
	}
	public double getHeight() {
		return endPoint.getY() - startPoint.getY();
	}
	public double getOrigWidth() {
		return width;
	}
	public double getOrigHeight() {
		return height;
	}
	public MyImageLabel getImageLabel() {
		return imageLabel;
	}
	public int getPages() {
		return pages;
	}
	public MyImageLabel getLeftLabel() {
		return center.leftImageLabel;
	}
	public MyImageLabel getRightLabel() {
		return center.rightImageLabel;
	}
	
	public void reset() {
		minDat = 0;
		maxDat = 1;
		contrast = DEFAULT_CONTRAST_BRIGHTNESS;
		startPoint = DEFAULT_STARTPOINT;
		endPoint = DEFAULT_ENDPOINT;
		right.minSlider.setValue(0);
		right.maxSlider.setValue(100);
		right.contrastSlider.setValue(contrast);
		dealImage();
	}	
	public void adjustPoint(BufferedImage origImage) {
		if(startPoint == null || endPoint==null) {
			startPoint = new Point(0,0);
			endPoint = new Point(origImage.getWidth(), origImage.getHeight());
		}
		
		int startX = (int)startPoint.getX();
		int startY = (int)startPoint.getY();
		int endX = (int)endPoint.getX();
		int endY = (int)endPoint.getY();
		
		if(startX == endX) {
			endX = startX + 1;
		}
		if(startY == endY) {
			endY = startY + 1;
		}
		if(startX < 0) {
			startX = 0;
		}
		if(startY < 0) {
			startY = 0;
		}
		if(endX > width) {
			endX = width;
		}
		if(endY > height) {
			endY = height;
		}
		
		int width = endX - startX;
		int height = endY - startY;
//		if(width < height) {
//			height = width;
//		}
//		if(width > height) {
//			width = height;
//		}
		endX = startX + width;
		endY = startY + height;
		
		startPoint.setLocation(startX, startY);
		endPoint.setLocation(endX, endY);
	}	
	public void dealImage() {
		if(status) {
			BufferedImage result = null;
			if(gaussStatus)
				result = dealGauss(contrast, true);
			else
				result = dealRaw(contrast, true);
			
			if(drawRegion)
				result = addColor(result, true);
			
			if(imageLabel!=null) {
				imageLabel.setIcon(new ImageIcon(result.getScaledInstance(maxImageWidth, maxImageHeight, Image.SCALE_DEFAULT)));
				imageLabel.repaint();
			}
		}else {
			leftImageLabel = center.leftImageLabel;
			rightImageLabel = center.rightImageLabel;
			leftImageLabel.setDrawBorder(true);
			rightImageLabel.setDrawBorder(true);
			center.colorbarleft.drawColor = false;
			center.colorbarright.drawColor = false;
			BufferedImage leftResult = null;
			BufferedImage rightResult = null;
			switch(center.leftJCB.getSelectedIndex()){
				case 0: 
					if(gaussStatus)
						leftResult = dealGauss(contrastl, true);
					else
						leftResult = dealRaw(contrastl, true);
					leftImageLabel.setDrawBorder(false);
					break;
				case 1:
					if(gaussStatus)
						leftResult = dealGauss(contrastl, true);
					else
						leftResult = dealRaw(contrastl, true);
					leftResult = addColor(leftResult, true);
					break;
				case 2:
					if(left.jTPStatus>=6) {
						leftImageLabel.setDrawBorder(false);
						leftResult = dealRisingMap(center.colorbarleft, true);
						center.colorbarleft.drawColor = true;
					}
					break;
				case 3:
					leftResult = dealRaw(maxImage1,contrastl);
					break;
				case 4:
					leftResult = dealRaw(avgImage1,contrastl);
					break;
				case 5:
					if(left.jTPStatus>=1) {
						leftResult = dealDF(contrastl, true);
					}
					break;
				case 6:
					if(left.jTPStatus>=1) {
						leftResult = dealRaw(contrastl, true);
						leftResult = addColorThreshold(leftResult, true);
					}
					break;
			}
			switch(center.rightJCB.getSelectedIndex()){
				case 0: 
					if(gaussStatus)
						rightResult = dealGauss(contrastr, false);
					else
						rightResult = dealRaw(contrastr, false);
					rightImageLabel.setDrawBorder(false);
					break;
				case 1:
					if(gaussStatus)
						rightResult = dealGauss(contrastr, false);
					else
						rightResult = dealRaw(contrastr, false);
					rightResult = addColor(rightResult, false);
					break;
				case 2:
					if(left.jTPStatus>=6) {
						rightImageLabel.setDrawBorder(false);
						rightResult = dealRisingMap(center.colorbarright, false);
						center.colorbarright.drawColor = true;
					}
					break;
				case 3:
					rightResult = dealRaw(maxImage2,contrastr);
					break;
				case 4:
					rightResult = dealRaw(avgImage2,contrastr);
					break;
				case 5:
					if(left.jTPStatus>=1) {
						rightResult = dealDF(contrastr, false);
					}
					break;
				case 6:
					if(left.jTPStatus>=1) {
						rightResult = dealRaw(contrastr, false);
						rightResult = addColorThreshold(rightResult, false);
					}
					break;
			}
			center.colorbarleft.repaint();
			center.colorbarright.repaint();
			
			// Size
			
			if(leftResult!=null) {
				leftImageLabel.setIcon(new ImageIcon(leftResult.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
				leftImageLabel.repaint();
			}
			if(rightResult!=null) {
				rightImageLabel.setIcon(new ImageIcon(rightResult.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
				rightImageLabel.repaint();
			}
		}
	}

	public BufferedImage dealRaw(int contrast, boolean leftChannel) {
		BufferedImage origImage = imgPlus1.getBufferedImage();
		adjustPoint(origImage);
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		ImageProcessor imgProcessor;
		if (ch == 1) {
			imgProcessor = imgProcessor1;
		}else {
			imgProcessor = imgProcessor2;
		}
		
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());

//		System.out.println(startX + " " + startY + " " + endX + " " + endY);
		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = imgProcessor.getf(i, j);
				if (gray >= maxDat) {
	                prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= minDat) {
	                prod.setRGB(i - startX, j - startY, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - minDat) * 255/(maxDat - minDat));
	                prod.setRGB(i - startX, j - startY, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
	
	public BufferedImage dealRisingMap(ColorLabel2 colorbar, boolean leftChannel) {
		BufferedImage origImage = imgPlus1.getBufferedImage();
		adjustPoint(origImage);
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		HashSet<Integer> featureTableList;
		FtsLst fts;
		HashMap<Integer, RiseInfo> riseLst;
		float[][][] dat;
		if (ch == 1) {
			featureTableList = featureTableList1;
			riseLst = riseLst1;
			fts = fts1;
		}else {
			featureTableList = featureTableList2;
			riseLst = riseLst2;
			fts = fts2;
		}
		
		int w = width;
		int h = height;
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());

		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);
		if (riseLst == null) {
			return prod;
		}
		
		float[][] riseMapCol = new float[w][h];
		for(int x=0;x<w;x++) {
			for(int y=0;y<h;y++) {
				riseMapCol[x][y] = -1;
			}
		}
		
		for(int label:featureTableList) {
			if(fts.loc.t0.get(label)<=curFrame && fts.loc.t1.get(label)>=curFrame) {
				RiseInfo rr = riseLst.get(label);
				for(int x=rr.rgw0;x<=rr.rgw1;x++) {
					for(int y=rr.rgh0;y<=rr.rgh1;y++) {
						if (!Float.isNaN(rr.dlyMaps[1][y-rr.rgh0][x-rr.rgw0]))
							riseMapCol[x][y] = Math.max(riseMapCol[x][y], rr.dlyMaps[1][y-rr.rgh0][x-rr.rgw0]);
					}
				}
			}
		}
		
		float minV = Float.MAX_VALUE;
		float maxV = -1;
		
		for(int x=0;x<w;x++) {
			for(int y=0;y<h;y++) {
				float v = riseMapCol[x][y];
				if(v!=-1) {
					minV = Math.min(v, minV);
					maxV = Math.max(v, maxV);
				}
			}
		}
		
		colorbar.setText(" ");
		if(maxV!=-1) {
			String v1 = String.format("%.1f", minV);
			String v2 = String.format("%.1f", (minV+maxV)/2);
			String v3 = String.format("%.1f", maxV);
			String blank = "                                                   ";
			colorbar.setText(v1 + blank + v2 + blank + v3);
		}
		
		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = riseMapCol[i][j];
				if(gray<0) {
					prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else {
//					cStart = new Color(255,0,0); cEnd = new Color(0,0,255); cMid = new Color(255,255,0); break;
					gray = (gray-minV)/(maxV - minV);
					int red = 0;
					int green = 0;
					int blue = 0;
					
					// red
					if(gray<0.375f) 
						red = 0;
					else if(gray<0.625f)
							red = Math.round((gray-0.375f)*1020);
					else if(gray<0.875f)
							red = 255;
					else
							red = 255 - Math.round(1020*(gray-0.875f));

					// green
					if(gray<0.125f) 
						green = 0;
					else if(gray<0.375f)
						green = Math.round((gray-0.125f)*1020);
					else if(gray<0.625f)
						green = 255;
					else if(gray<0.875f)
						green = 255 - Math.round(1020*(gray-0.625f));
					else 
						green = 0;
						
					// blue
					if(gray<0.125f)
						blue = 127 + Math.round(gray*1020);
					else if(gray<0.375f)
						blue = 255;
					else if(gray<0.625f)
						blue = 255 - Math.round(1020*(gray-0.375f));
					else
						blue = 0;
					
					prod.setRGB(i - startX, j - startY, new Color(red, green, blue, 255).getRGB());
			
				}
			}
		}
		
		return prod;
	}
	
	public BufferedImage dealGauss(int contrast, boolean leftChannel) {
		BufferedImage origImage = imgPlus1.getBufferedImage();
		adjustPoint(origImage);
		
		int w = width;
		int h = height;
		int k = curFrame;
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());
		
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		float[][][] dat;
		if (ch == 1) {
			dat = dat1;
		}else {
			dat = dat2;
		}

//		System.out.println(startX + " " + startY + " " + endX + " " + endY);
		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = dat[i][j][k];
//				gray = gray*opts.maxValueDat;
				if (gray >= maxDat) {
	                prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= minDat) {
	                prod.setRGB(i - startX, j - startY, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - minDat) * 255/(maxDat - minDat));
	                prod.setRGB(i - startX, j - startY, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
	
	public BufferedImage dealDF(int contrast, boolean leftChannel) {
//		System.out.println("Show dF");
		BufferedImage origImage = imgPlus1.getBufferedImage();
		adjustPoint(origImage);
		
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		float[][][] dF;
		if (ch == 1) {
			dF = dF1;
		}else {
			dF = dF2;
		}
		
		
		int w = width;
		int h = height;
		int k = curFrame;
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());

//		System.out.println(startX + " " + startY + " " + endX + " " + endY);
		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = dF[j][i][k] / opts.maxdF1;
				if (gray >= maxDat) {
	                prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= minDat) {
	                prod.setRGB(i - startX, j - startY, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - minDat) * 255/(maxDat - minDat));
	                prod.setRGB(i - startX, j - startY, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
	
	public BufferedImage dealRaw(float[][] image, int contrast) {	
		
		BufferedImage origImage = imgPlus1.getBufferedImage();
		adjustPoint(origImage);
		
		int w = width;
		int h = height;
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());

//		System.out.println(startX + " " + startY + " " + endX + " " + endY);
		BufferedImage prod = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=startX;i<endX;i++) {
			for(int j=startY;j<endY;j++) {
				float gray = image[i][j];
				if (gray >= maxDat) {
	                prod.setRGB(i - startX, j - startY, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= minDat) {
	                prod.setRGB(i - startX, j - startY, new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - minDat) * 255/(maxDat - minDat));
	                prod.setRGB(i - startX, j - startY, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		
		float cont = (float)contrast / 100;
		RescaleOp rescaleOp = new RescaleOp(cont, 1.0f, null);
		BufferedImage result = rescaleOp.filter(prod, null);
		return result;
	}
	
	public void dealBuilderImageLabel() {
		int width = curBuilderImage1.length;
		int height = curBuilderImage1[0].length;
		BufferedImage prod = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				float gray = curBuilderImage1[i][j];
				gray = gray*gray*opts.maxValueDat;
				if (gray >= maxDat) {
	                prod.setRGB(i, j, new Color(255, 255, 255, 255).getRGB());
				}else if(gray <= minDat) {
	                prod.setRGB(i, j , new Color(0, 0, 0, 255).getRGB());
	            }else {
	                gray = (float)((gray - minDat) * 255/(maxDat - minDat));
	                prod.setRGB(i - 0, j - 0, new Color((int)gray, (int)gray, (int)gray, 255).getRGB());
				}
			}
		}
		int sw = 0;
		int sh = 0;
		float scal = (float)width/height;
		if(700*scal >700) {
			sw = 700;
			sh = (int) (700/scal);
		}else {
			sw = (int) (700*scal);
			sh = 700;
		}
		

		builderImageLabel.setIcon(new ImageIcon(prod.getScaledInstance(sw, sh, Image.SCALE_DEFAULT)));
	}
	
	public BufferedImage addColor(BufferedImage curImg, boolean leftChannel) {
		adjustPoint(curImg);	
		
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		int[][][] label;
		int[][][] datR;
		Color[] labelColors;
		if (ch == 1) {
			label = label1;
			datR = datR1;
			labelColors = labelColors1;
		}else {
			label = label2;
			datR = datR2;
			labelColors = labelColors2;
		}
		
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());
		
		for(int i=startY;i<endY;i++) {
			for(int j=startX;j<endX;j++) {
				int k = imgPlus1.getCurrentSlice()-1;
				if(label[i][j][k]!=0) {
					if(deleteColorSet.contains(label[i][j][k])){
						continue;
					}
					if (ch == 1 && deleteColorSet2.contains(label[i][j][k]))
						continue;
					if (ch == 2 && deleteColorSet2.contains(label[i][j][k] + nEvtCh1))
						continue;
					Color curColor = labelColors[label[i][j][k]-1];
					Color curImgColor = new Color(curImg.getRGB(j-startY, i-startX));
					float saturation = 1;
					if(datR!=null)
						saturation = (float)datR[i][j][k]/255;
					int red = (int)(curColor.getRed()*colorScale*saturation + curImgColor.getRed()*1.2);
					int green = (int)(curColor.getGreen()*colorScale*saturation + curImgColor.getGreen()*1.2);
					int blue = (int)(curColor.getBlue()*colorScale*saturation + curImgColor.getBlue()*1.2);
					red = red>255?255:red;
					green = green>255?255:green;
					blue = blue>255?255:blue;
					curImg.setRGB(j-startY, i-startX, new Color(red,green,blue,255).getRGB());
					
				}
			}
		}
		return curImg;
		
	}
	
	public BufferedImage addColorThreshold(BufferedImage curImg, boolean leftChannel) {
		adjustPoint(curImg);	
		
		if(dF1 == null)
			return curImg;
		
		int ch = 0;
		if (leftChannel) {
			ch = right.chLeftJCB.getSelectedIndex() + 1;
		} else {
			ch = right.chRightJCB.getSelectedIndex() + 1;
		}
		
		float[][][] dF;
		if (ch == 1) {
			dF = dF1;
		}else {
			dF = dF2;
		}
		
		int startX = (int)(startPoint.getX());
		int startY = (int)(startPoint.getY());
		int endX = (int)(endPoint.getX());
		int endY = (int)(endPoint.getY());
		float thrArscl = Float.parseFloat(left.jTFthr.getText());
		Color curColor = new Color(200, 160, 70);
		for(int i=startY;i<endY;i++) {
			for(int j=startX;j<endX;j++) {
				int k = imgPlus1.getCurrentSlice()-1;
				if(dF[i][j][k] > thrArscl) {
					Color curImgColor = new Color(curImg.getRGB(j-startY, i-startX));
					float saturation = 1;
					int red = (int)(curColor.getRed()*colorScale*saturation + curImgColor.getRed()*1.2);
					int green = (int)(curColor.getGreen()*colorScale*saturation + curImgColor.getGreen()*1.2);
					int blue = (int)(curColor.getBlue()*colorScale*saturation + curImgColor.getBlue()*1.2);
					red = red>255?255:red;
					green = green>255?255:green;
					blue = blue>255?255:blue;
					curImg.setRGB(j-startY, i-startX, new Color(red,green,blue,255).getRGB());
					
				}
			}
		}
		return curImg;
	}
	
	public void step1Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step1 task = new Step1(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	public void step2Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step2 task = new Step2(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	public void step3Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step3 task = new Step3(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                	System.out.println("Step3 Error");
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	public void setStep2(float thr, int minDur, int minSize, int maxSize, float circularity) {
		opts.thrARScl = thr;
		opts.minDur = minDur;
		opts.minSize = minSize;
		opts.maxSize = maxSize;
		opts.circularityThr = circularity;
		
	} 
	
	public void setStep3(boolean needTemp, float seedSzRatio, float sigThr, float maxDelay) {
		opts.needTemp = needTemp;
		opts.seedSzRatio = seedSzRatio;
		opts.sigThr = sigThr;
		opts.maxDelay = maxDelay;
		
	}
	public void step4Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step4 task = new Step4(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	public void setStep4(boolean needSpa, float sourceSzRatio, int sourceSensitivity, boolean whetherExtend) {
		opts.needSpa = needSpa;
		opts.sourceSzRatio = sourceSzRatio;
		opts.sourceSensitivity = sourceSensitivity;
		opts.whetherExtend = whetherExtend;
	}
	
	
	
	public void export(boolean eventsExtract, boolean movieExtract, String savePath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	
                	IO_AQuAOutPut task = new IO_AQuAOutPut(dealer,eventsExtract,movieExtract,path, 1, savePath);
            		task.setting();
            		task.execute();
            		if(!opts.singleChannel) {
            			IO_AQuAOutPut task2 = new IO_AQuAOutPut(dealer,eventsExtract,movieExtract,path2, 2, savePath);
                		task2.setting();
                		task2.execute();
            		}
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}
	
	public void exportOpts(String savePath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	IO_AQuAOutPutOpts task = new IO_AQuAOutPutOpts(dealer,savePath);
//                	task.doInBackground();
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}

	public void loadOpts(String savePath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	IO_AQuALoadOpts task = new IO_AQuALoadOpts(dealer,savePath);
//                	task.doInBackground();
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}

	
	public void setCurveLabel(DrawCurveLabel resultsLabel) {
		curveLabel = resultsLabel;
		
	} 
	
	public void drawCurve(Point p) {
		int x = (int)p.getX();
		int y = (int)p.getY();
		int t = imgPlus1.getCurrentSlice()-1;
		int nEvt;
		nEvt = label1[y][x][t];
		if(nEvt!=0) {
			featureTableList1.add(nEvt);
//			System.out.println(featureTableList.size());
			dealImage();
			curveLabel.drawCurve(dffMat1, nEvt,fts1);
			int rowNumber = right.table.getRowCount();
			for(int r=0;r<rowNumber;r++) {
				int label = (Integer) right.table.getValueAt(r, 2);
				if(label==nEvt)
					return;
			}
			int frame = fts1.curve.tBegin.get(nEvt);
			float size = fts1.basic.area.get(nEvt);
			float duration = fts1.curve.duration.get(nEvt);
			float dffMax = fts1.curve.dffMax.get(nEvt);
			float tau = fts1.curve.decayTau.get(nEvt); 
			right.model.addRow(new Object[] {new Boolean(false),new Integer(1),new Integer(nEvt),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});
		}
		
		if (!opts.singleChannel) {
			nEvt = label2[y][x][t];
			if(nEvt!=0) {
				featureTableList2.add(nEvt);
//				System.out.println(featureTableList.size());
				dealImage();
				curveLabel.drawCurve(dffMat2, nEvt,fts2);
				int rowNumber = right.table.getRowCount();
				for(int r=0;r<rowNumber;r++) {
					int label = (Integer) right.table.getValueAt(r, 2);
					if(label==nEvt)
						return;
				}
				int frame = fts2.curve.tBegin.get(nEvt);
				float size = fts2.basic.area.get(nEvt);
				float duration = fts2.curve.duration.get(nEvt);
				float dffMax = fts2.curve.dffMax.get(nEvt);
				float tau = fts2.curve.decayTau.get(nEvt); 
				right.model.addRow(new Object[] {new Boolean(false),new Integer(2),new Integer(nEvt),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});
			}
		}
		
	}
	
	public void drawCurve(int nEvt, int ch) {
		if (ch == 1) {
			if(nEvt!=0 && fts1.curve.dffMax.get(nEvt)!=null) {
				curveLabel.drawCurve(dffMat1, nEvt, fts1);
			}
		} else {
			if(nEvt!=0 && fts2.curve.dffMax.get(nEvt)!=null) {
				curveLabel.drawCurve(dffMat2, nEvt, fts2);
			}
		}
		
	}
	
	public void setColorConstrast(int contr) {
		colorScale = ((float)contr)/100;
		
	}
	public void addCurve(int nEvt, int ch) {
		FtsLst fts;
		float[][][] dffMat;
		HashSet<Integer> featureTableList;
		if (ch == 1) {
			fts = fts1;
			dffMat = dffMat1;
			featureTableList = featureTableList1;
		}else {
			fts = fts2;
			dffMat = dffMat2;
			featureTableList = featureTableList2;
		}
		
		if(nEvt!=0 && fts.curve.dffMax.get(nEvt)!=null) {
			curveLabel.drawCurve(dffMat, nEvt,fts);
			int rowNumber = right.table.getRowCount();
			for(int r=0;r<rowNumber;r++) {
				int label = (Integer) right.table.getValueAt(r, 2);
				if(label==nEvt)
					return;
			}
			
			featureTableList.add(nEvt);
			int frame = fts.curve.tBegin.get(nEvt);
			center.imageSlider.setValue(frame);
			float size = fts.basic.area.get(nEvt);
			float duration = fts.curve.duration.get(nEvt);
			float dffMax = fts.curve.dffMax.get(nEvt);
			float tau = fts.curve.decayTau.get(nEvt); 
			right.model.addRow(new Object[] {new Boolean(false),new Integer(ch),new Integer(nEvt),new Integer(frame+1),new Float(size),new Float(duration),new Float(dffMax),new Float(tau)});
		}
		
	}
	public void deleteShowEvent(Point p) {
		int x = (int)p.getX();
		int y = (int)p.getY();
		int t = imgPlus1.getCurrentSlice()-1;
		int nEvt = label1[x][y][t];
		if(deleteColorSet.contains(nEvt)) {
			deleteColorSet.remove(nEvt);
		}else
			deleteColorSet.add(nEvt);
		dealImage();
		
	}
	public void setBuilderImageLabel(MaskBuilderLabel builderImageLabel) {
		this.builderImageLabel = builderImageLabel;
	}
	
	public void load(String proPath) {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	LoadProject task = new LoadProject(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}
	
	public void saveStatus() {
		int curStatus = left.curStatus;
		int jTPStatus = left.jTPStatus;
		
		
		try {
			Status status = new Status(curStatus, jTPStatus, labelColors1, labelColors2
					, opts, regionMark, regionMarkLabel, landMark, landMarkLabel, path, path2);
			Helper.writeObjectToFile(proPath, "status.ser", status);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setImageConfig(float ts, float ss) {
		opts.spatialRes = ss;
		opts.frameRate = ts;
		center.ts = ts;
	}

	public void setStep5(boolean detectGlo, int gloDur) {
		opts.detectGlo = detectGlo;
		opts.gloDur = gloDur;
	}

	
	public void step5Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step5 task = new Step5(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	public void setStep6(boolean ignoreTau, boolean checkProp, boolean checkNetwork, boolean step6Stg) {
		opts.ignoreTau = ignoreTau;
		opts.checkProp = checkProp;
		opts.checkNetwork = checkNetwork;
		this.step6Stg = step6Stg;
		
	}

	public void step6Start() {
		ImageDealer dealer = this;
		
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	Step6 task = new Step6(dealer);
            		task.setting();
            		task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
		
	}
	
	

	public void runAllSteps() {
		ImageDealer dealer = this;
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ExecutorService executor = Executors.newSingleThreadExecutor();
                	Step1 task1 = new Step1(dealer);
            		task1.setting();
            		executor.submit(task1);
            		
            		Step2 task2 = new Step2(dealer);
                	task2.setting();
                	executor.submit(task2);
                	
            		Step3 task3 = new Step3(dealer);
                	task3.setting();
                	executor.submit(task3);
                	
                	Step4 task4 = new Step4(dealer);
                	task4.setting();
                	executor.submit(task4);
                	
                	Step5 task5 = new Step5(dealer);
                	task5.setting();
                	executor.submit(task5);
                	
                	Step6 task6 = new Step6(dealer);
            		task6.setting();
            		executor.submit(task6);

            		executor.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        		
            }
        });
	}

	
	
	
}
