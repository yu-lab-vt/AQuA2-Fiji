package va.vt.cbilAQuA2.io;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
//import org.apache.poi.xssf.usermodel.*;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.fea.FtsLst;
import va.vt.cbilAQuA2.ui.GridBagPut;


public class IO_AQuAOutPut extends SwingWorker<Void, Integer>{
	
	JFrame frame = new JFrame("Extract");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");
	
	static long start = System.currentTimeMillis();;
	static long end;
	ImageDealer imageDealer = null;
	boolean eventsExtract = false;
	boolean movieExtract = false;
	String savePath = null;
	String orgPath = null;
	int[][][] label = null;
	Color[] labelColors = null;
	String proPath = null;
	FtsLst fts;
	int ch;
	
	public IO_AQuAOutPut(ImageDealer imageDealer, boolean eventsExtract, boolean movieExtract, 
			String path, int ch, String savePath) {
		this.imageDealer = imageDealer;
		this.eventsExtract = eventsExtract;
		this.movieExtract = movieExtract;
		this.ch = ch;
		if (ch == 1) {
			label = imageDealer.label1;
			labelColors = imageDealer.labelColors1;
			fts = imageDealer.fts1;
			
		}else {
			label = imageDealer.label2;
			labelColors = imageDealer.labelColors2;
			fts = imageDealer.fts2;
		}
		
		this.savePath = savePath;
		orgPath = path;
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
	
	public void getFeaTureTable() {
		Opts opts = imageDealer.opts;
		System.out.println("Finish read");
		
		
		int nEvt = 10000;
		if(fts!=null)
			nEvt = fts.basic.area.size();
		
		ArrayList<Integer> evtLst = new ArrayList<>();
		for(int i=1;i<=nEvt;i++) {
			if(imageDealer.deleteColorSet.contains(i)){
				continue;
			}
			if (ch == 1 && imageDealer.deleteColorSet2.contains(i))
				continue;
			if (ch == 2 && imageDealer.deleteColorSet2.contains(i + imageDealer.nEvtCh1))
				continue;
			evtLst.add(i);
		}
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(savePath + "\\AQuA2_Output_Excel_CH" + this.ch + ".csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        // Index
        sb.append("Index");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(i);
        	sb.append(',');
        }
        sb.append('\n');
        
        // Starting Frame
        sb.append("Starting Frame");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.tBegin.get(i) + 1);
        	sb.append(',');
        }
        sb.append('\n');
        
        // Area
		sb.append("Basic - Area");
    	sb.append(',');
        for(int i:evtLst) {
        	sb.append(fts.basic.area.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Perimeter
		sb.append("Basic - Perimeter");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.basic.perimeter.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Circularity
        sb.append("Basic - Circularity");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.basic.circMetric.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // P Value
        sb.append("Curve - P Value on max Dff (-log10)");
    	sb.append(',');
    	for(int i:evtLst) {
        	double dffMaxPval = fts.curve.dffMaxPval.get(i);
        	sb.append(-Math.log10(dffMaxPval));
        	sb.append(',');
        }
        sb.append('\n');
        
     // Max Dff
        sb.append("Curve - Max Df");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.dfMax.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // Max Dff
        sb.append("Curve - Max Dff");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.dffMax.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
      //Curve - Duration 50% to 50%
        sb.append("Curve - Duration of visualized event overlay");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.duration.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        //Curve - Duration 50% to 50%
        sb.append("Curve - Duration 50% to 50%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.width55.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        //Curve - Duration 10% to 10%
        sb.append("Curve - Duration 10% to 10%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.width11.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // rise19
        sb.append("Curve - Rising Duration 10% to 90%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.rise19.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // fall91
        sb.append("Curve - Decaying Duration 90% to 10%");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.fall91.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // datAUC
        sb.append("Curve - dat AUC");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.datAUC.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // dfAUC
        sb.append("Curve - df AUC");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.dfAUC.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // dffAUC
        sb.append("Curve - dff AUC");
    	sb.append(',');
    	for(int i:evtLst) {
        	sb.append(fts.curve.dffAUC.get(i));
        	sb.append(',');
        }
        sb.append('\n');
        
        // decayTau
        sb.append("Curve - Decay Tau");
    	sb.append(',');
    	for(int i:evtLst) {
        	
        	if(!opts.ignoreTau) {
        		Float decayTau = fts.curve.decayTau.get(i);
        		sb.append(decayTau);
        	}
        	sb.append(',');
        }
        sb.append('\n');
        
        if(opts.checkProp) {
        	// sum onset
            float[] sumOnset = new float[nEvt];
            // onset overall
            sb.append("Propagation - Onset - Overall");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
    			float sum = 0;
    			for(int k=0;k<x0.length;k++)
    				sum += x0[k];
    			sumOnset[i-1] = sum;
            	sb.append(sum);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Anterior
            sb.append("Propagation - Onset - One Direction - Anterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	sb.append(x0[0]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Posterior
            sb.append("Propagation - Onset - One Direction - Posterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	sb.append(x0[1]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Left
            sb.append("Propagation - Onset - One Direction - Left");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	sb.append(x0[2]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Right
            sb.append("Propagation - Onset - One Direction - Right");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	sb.append(x0[3]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Ratio Anterior
            sb.append("Propagation - Onset - One Direction - Ratio - Anterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	if(sumOnset[i-1]!=0)
            		sb.append(x0[0]/sumOnset[i-1]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Ratio Posterior
            sb.append("Propagation - Onset - One Direction - Ratio - Posterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	if(sumOnset[i-1]!=0)
            		sb.append(x0[1]/sumOnset[i-1]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Ratio Left
            sb.append("Propagation - Onset - One Direction - Ratio - Left");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	if(sumOnset[i-1]!=0)
            		sb.append(x0[2]/sumOnset[i-1]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // onset Ratio Right
            sb.append("Propagation - Onset - One Direction - Ratio - Right");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propGrowOverall.get(i);
            	if(sumOnset[i-1]!=0)
            		sb.append(x0[3]/sumOnset[i-1]);
            	sb.append(',');
            }
            sb.append('\n');
            
            // sum offset
            float[] sumOffset = new float[nEvt];
            // offset overall
            sb.append("Propagation - Offset - Overall");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
    			float sum = 0;
    			for(int k=0;k<x0.length;k++)
    				sum += Math.abs(x0[k]);
    			sumOffset[i-1] = sum;
            	sb.append(sum);
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Anterior
            sb.append("Propagation - Offset - One Direction - Anterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	sb.append(Math.abs(x0[0]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Posterior
            sb.append("Propagation - Offset - One Direction - Posterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	sb.append(Math.abs(x0[1]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Left
            sb.append("Propagation - Offset - One Direction - Left");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	sb.append(Math.abs(x0[2]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Right
            sb.append("Propagation - Offset - One Direction - Right");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	sb.append(Math.abs(x0[3]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Ratio Anterior
            sb.append("Propagation - Offset - One Direction - Ratio - Anterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	if(sumOffset[i-1]!=0)
            		sb.append(Math.abs(x0[0]/sumOffset[i-1]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Ratio Posterior
            sb.append("Propagation - Offset - One Direction - Ratio - Posterior");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	if(sumOffset[i-1]!=0)
            		sb.append(Math.abs(x0[1]/sumOffset[i-1]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Ratio Left
            sb.append("Propagation - Offset - One Direction - Ratio - Left");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	if(sumOffset[i-1]!=0)
            		sb.append(Math.abs(x0[2]/sumOffset[i-1]));
            	sb.append(',');
            }
            sb.append('\n');
            
            // offset Ratio Right
            sb.append("Propagation - Offset - One Direction - Ratio - Right");
        	sb.append(',');
        	for(int i:evtLst) {
            	float[] x0 = fts.propagation.propShrinkOverall.get(i);
            	if(sumOffset[i-1]!=0)
            		sb.append(Math.abs(x0[3]/sumOffset[i-1]));
            	sb.append(',');
            }
            sb.append('\n');
        }
        
        if (opts.checkNetwork) {
        	int nLmk = 0;
    		if(fts.region!=null && fts.region.landMark!=null&& fts.region.landMark.center!=null)
    			nLmk = fts.region.landMark.center.length;
    		
    		boolean regionExist = false;
    		if(fts.region.cell!=null && fts.region.cell.border!=null)
    			regionExist = true;

    		System.out.println("LandMark Number: " + nLmk);
    		System.out.println("RegionExist: " + regionExist);
    		
    		// LandMark - average
    		for(int k=1;k<=nLmk;k++) {
    			sb.append("Landmark - Event Average Distance - Landmark " + k);
    			sb.append(',');
    			for(int i:evtLst) {
    				sb.append(fts.region.landmarkDist.distAvg[i-1][k-1]);
    				sb.append(',');
    			}
    			sb.append('\n');
    		}
    		
    		// LandMark - min
    		for(int k=1;k<=nLmk;k++) {
    			sb.append("Landmark - Event Minimum Distance - Landmark " + k);
    			sb.append(',');
    			for(int i:evtLst) {
    				sb.append(fts.region.landmarkDist.distMin[i-1][k-1]);
    				sb.append(',');
    			}
    			sb.append('\n');
    		}
    		

    		if(regionExist) {
    			sb.append("Region - Event Centroid Distance To Border");
    			sb.append(',');
    			for(int i:evtLst) {
    				float[] x0 = fts.region.cell.dist2border[i-1];
    				float minX0 = Float.MAX_VALUE;
    				for(int t=0;t<x0.length;t++) {
    					if(!Float.isNaN(x0[t]))
    						minX0 = Math.min(minX0, x0[t]);
    				}
    				sb.append(minX0);
    				sb.append(',');
    			}
    			sb.append('\n');
    			
    			sb.append("Region - Event Centroid Distance To Border - Normalized By Region Radius");
    			sb.append(',');
    			for(int i:evtLst) {
    				float[] x0 = fts.region.cell.dist2borderNorm[i-1];
    				float minX0 = Float.MAX_VALUE;
    				for(int t=0;t<x0.length;t++) {
    					if(!Float.isNaN(x0[t]))
    						minX0 = Math.min(minX0, x0[t]);
    				}
    				sb.append(minX0);
    				sb.append(',');
    			}
    			sb.append('\n');
    		}
    		
    		// network
    		sb.append("Network - number of events in the same location");
    		sb.append(',');
    		for(int i:evtLst) {
    			sb.append(fts.network.nOccurSameLoc[i-1][0]);
    			sb.append(',');
    		}
    		sb.append('\n');
    		
    		// network
    		sb.append("Network - number of events in the same location with similar size only");
    		sb.append(',');
    		for(int i:evtLst) {
    			sb.append(fts.network.nOccurSameLoc[i-1][1]);
    			sb.append(',');
    		}
    		sb.append('\n');
    		
    		// network
    		sb.append("NetWork - maximum number of events appearing at the same time");
    		sb.append(',');
    		for(int i:evtLst) {
    			sb.append(fts.network.nOccurSameTime[i-1]);
    			sb.append(',');
    		}
    		sb.append('\n');
        }
        
        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
	}
	

	@Override
	protected Void doInBackground(){
		
		if(eventsExtract) {
			publish(1);
			getFeaTureTable();
			getCurveOutput();
		}
		if(movieExtract) {
			publish(2);
			exportMovie();
		}
		return null;
	} 
	
	private void getCurveOutput() {
		Opts opts = imageDealer.opts;
		
		System.out.println("Finish read");
		
		
		int nEvt = 10000;
		if(fts!=null)
			nEvt = fts.basic.area.size();
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(savePath + "\\AQuA2_Curve_Output_CH" + this.ch + ".csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float[][][] dffMat;
		if (ch == 1)
			dffMat = imageDealer.dffMat1;
		else
			dffMat = imageDealer.dffMat2;
		int T = dffMat[0].length;
		
        StringBuilder sb = new StringBuilder();
        // Title
        sb.append("Event Index");
        sb.append(',');
        sb.append("Start Frame");
        sb.append(',');
        sb.append("End Frame");
        sb.append(',');
        for(int t=0;t<T;t++) {
        	sb.append("Frame " + (t+1));
            sb.append(',');
        }
        sb.append('\n');
        
        for(int i=0;i<nEvt;i++) {
        	sb.append("Event " + (i+1));
            sb.append(',');
            sb.append(fts.curve.tBegin.get(i+1)+1);
            sb.append(',');
            sb.append(fts.curve.tEnd.get(i+1)+1);
            sb.append(',');
            
            float min = Float.MAX_VALUE;
			float max = -Float.MAX_VALUE;
			for(int t = 0;t<T;t++) {
				min = Math.min(min, dffMat[i][t][1]);
				max = Math.max(max, dffMat[i][t][1]);
			}	
			
			float[] curve = new float[T];

			for(int t = 0;t<T;t++) {
				curve[t] = (dffMat[i][t][1] - min)/(max-min);
				sb.append(curve[t]);
                sb.append(',');
			}
            sb.append('\n');
        }
        
        pw.write(sb.toString());
        pw.close();
        System.out.println("done!");
        
		
	}

	private void exportMovie() {
		// TODO Auto-generated method stub
		System.out.println(savePath + "\\AQuA2_Output_Movie");
		ImagePlus img = new ImagePlus(orgPath);
		ImageConverter converter = new ImageConverter(img);
		converter.convertToGray8();
		converter.convertToRGB();
		int H = label.length;
		int W = label[0].length;
		int T = label[0][0].length;
		ImageProcessor imgProcessor = img.getProcessor();
		
		for(int k = 1;k<=T;k++) {
			img.setPosition(k);
			for(int x=0;x<H;x++) {
				for(int y=0;y<W;y++) {
					int gray = (int) ((imgProcessor.getPixel(y, x)&255)*0.6);
					if(label[x][y][k-1]!=0) {
						Color curColor = labelColors[label[x][y][k-1]-1];
						
						int red = (int)(curColor.getRed() + gray*1.2);
						int green = (int)(curColor.getGreen() + gray*1.2);
						int blue = (int)(curColor.getBlue() + gray*1.2);
						red = red>255?255:red;
						green = green>255?255:green;
						blue = blue>255?255:blue;
						imgProcessor.setColor(new Color(red,green,blue));
						Roi roi = new Roi(y,x,1,1);
						imgProcessor.fill(roi);
					}else {
						imgProcessor.setColor(new Color(gray,gray,gray));
						Roi roi = new Roi(y,x,1,1);
						imgProcessor.fill(roi);
					}
				}
			}
		}
		FileSaver fs = new FileSaver(img);
		fs.saveAsTiff(savePath + "\\AQuA2_Output_Movie_CH" + this.ch + ".tif");

	}

	protected void process(List<Integer> chunks) {
		int value = chunks.get(chunks.size()-1);
		String str = "";
		switch(value) {
		case 1:
			str = "Extract Events and Features as Excel";
			break;
		case 2:
			str = "Extract Movies";
			break;
		}
		jLabel.setText(str);
	}
	
	static public void showDetails(ArrayList<Integer> indexLst, FtsLst fts, Opts opts) {
		JFrame frame = new JFrame("Features for favorite events");
		frame.setSize(850,700);
		frame.setUndecorated(false);
		frame.setLocationRelativeTo(null);
		
		JScrollPane tablePane = new JScrollPane();
		
		DefaultTableModel model = null;
		JTable table = null;
		
    	model = new DefaultTableModel();

    	model.addColumn("");
    	model.addRow(new Object[] {"Index"});
    	model.addRow(new Object[] {"Basic - Area"});
    	model.addRow(new Object[] {"Basic - Perimeter"});
    	model.addRow(new Object[] {"Basic - Circularity"});
    	model.addRow(new Object[] {"Curve - P Value on max Dff (-log10)"});
    	model.addRow(new Object[] {"Curve - Max Dff"});
    	model.addRow(new Object[] {"Curve - Duration 50% to 50%"});
    	model.addRow(new Object[] {"Curve - Duration 10% to 10%"});
    	model.addRow(new Object[] {"Curve - Rising Duration 10% to 90%"});
    	model.addRow(new Object[] {"Curve - Decaying Duration 90% to 10%"});
    	model.addRow(new Object[] {"Curve - Decay Tau"});
    	model.addRow(new Object[] {"Propagation - Onset - Overall"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Anterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Posterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Left"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Right"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Anterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Posterior"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Left"});
    	model.addRow(new Object[] {"Propagation - Onset - One Direction - Ratio - Right"});
    	model.addRow(new Object[] {"Propagation - Offset - Overall"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Anterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Posterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Left"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Right"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Anterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Posterior"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Left"});
    	model.addRow(new Object[] {"Propagation - Offset - One Direction - Ratio - Right"});
    	
    	int nLmk = 0;
		if(fts.region!=null && fts.region.landMark!=null&& fts.region.landMark.center!=null)
			nLmk = fts.region.landMark.center.length;
		
		for(int k=1;k<=nLmk;k++) {
			model.addRow(new Object[] {"Landmark - event average distance - landmark " + k});
			model.addRow(new Object[] {"Landmark - event minimum distance - landmark " + k});
			model.addRow(new Object[] {"Landmark - event toward landmark - landmark " + k});
			model.addRow(new Object[] {"Landmark - event away from landmark - landmark " + k});
		}
		
		boolean regionExist = false;
		if(fts.region.cell!=null && fts.region.cell.border!=null)
			regionExist = true;
    	
    	model.addRow(new Object[] {"Region - event centroid distance to border"});
    	model.addRow(new Object[] {"Region - event centroid distance to border -normalize"});
    	model.addRow(new Object[] {"Network - Temporal density"});
    	model.addRow(new Object[] {"Network - Temporal density with similar size only"});
    	model.addRow(new Object[] {"Network - Spatial density"});
    	
    	
    	
    	table = new JTable(model){
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int column) {
				return String.class;
			}
    		
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				
				if(column==0)
					component.setBackground(new Color(238, 238, 238));
				else {
					if(row%2==1)
						component.setBackground(new Color(245, 245, 250));
					else
						component.setBackground(Color.white);
				}
				return component;
			}
    	};
    	
    	DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
    	tcr.setHorizontalAlignment(JLabel.LEFT);
    	table.setDefaultRenderer(Object.class, tcr);
    	table.getColumnModel().getColumn(0).setPreferredWidth(80);
    	table.setSize(new Dimension(850,550));
    	
    	tablePane = new JScrollPane(table);
    	
    	tablePane.setPreferredSize(new Dimension(850,550));
    	frame.add(tablePane);
    	frame.setVisible(true);
    	
    	tablePane.setOpaque(true);
    	tablePane.setBackground(Color.WHITE);
    	table.setOpaque(true);
    	table.setBackground(Color.white);
    	
    	for(int i=0;i<indexLst.size();i++) {
    		model.addColumn("" + (i+1));
    		int nEvt = indexLst.get(i);
    		System.out.println(""+nEvt);
			// Index
			table.setValueAt(new Integer(nEvt), 0, i+1);
			// Area
			table.setValueAt(new Float(fts.basic.area.get(nEvt)), 1, i+1);
			// Perimeter
			table.setValueAt(new Float(fts.basic.perimeter.get(nEvt)), 2, i+1);
			// Circularity
			table.setValueAt(new Float(fts.basic.circMetric.get(nEvt)), 3, i+1);
			// P value
			double dffMaxPval = fts.curve.dffMaxPval.get(nEvt);
			if(dffMaxPval>0)
				table.setValueAt(new Float(-Math.log10(dffMaxPval)), 4, i+1);
			else
				table.setValueAt(new Float(opts.maxValueDat), 4, i+1);
			// Max Dff
			table.setValueAt(new Float(fts.curve.dffMax.get(nEvt)), 5, i+1);
			// width55
			table.setValueAt(new Float(fts.curve.width55.get(nEvt)), 6, i+1);
			// width11
			table.setValueAt(new Float(fts.curve.width11.get(nEvt)), 7, i+1);
			// rise19
			table.setValueAt(new Float(fts.curve.rise19.get(nEvt)), 8, i+1);
			// fall91
			table.setValueAt(new Float(fts.curve.fall91.get(nEvt)), 9, i+1);
			// decayTau
			Float decayTau = fts.curve.decayTau.get(nEvt);
			if(!opts.ignoreTau)
				table.setValueAt(new Float(decayTau), 10, i+1);
			// onset overall
			
			if (opts.checkProp) {
				float sum = 0;
				float[] x0 = fts.propagation.propGrowOverall.get(nEvt);
				for(int k=0;k<x0.length;k++)
					sum += x0[k];
				table.setValueAt(new Float(sum), 11, i+1);
				// one direction - Anterior
				table.setValueAt(new Float(x0[0]), 12, i+1);
				// one direction - Posterior
				table.setValueAt(new Float(x0[1]), 13, i+1);
				// one direction - Left
				table.setValueAt(new Float(x0[2]), 14, i+1);
				// one direction - Right
				table.setValueAt(new Float(x0[3]), 15, i+1);
				if(sum!=0) {
					// One Direction - Ratio - Anterior
					table.setValueAt(new Float(x0[0]/sum), 16, i+1);
					// One Direction - Ratio - Posterior
					table.setValueAt(new Float(x0[1]/sum), 17, i+1);
					// One Direction - Ratio - Left
					table.setValueAt(new Float(x0[2]/sum), 18, i+1);
					// One Direction - Ratio - Right
					table.setValueAt(new Float(x0[3]/sum), 19, i+1);
				}
				
				// offset
				x0 = fts.propagation.propShrinkOverall.get(nEvt);
				sum = 0;
				for(int k=0;k<x0.length;k++) {
					x0[k] = Math.abs(x0[k]);
					sum += x0[k];
				}
				table.setValueAt(new Float(sum), 20, i+1);
				// one direction - Anterior
				table.setValueAt(new Float(x0[0]), 21, i+1);
				// one direction - Posterior
				table.setValueAt(new Float(x0[1]), 22, i+1);
				// one direction - Left
				table.setValueAt(new Float(x0[2]), 23, i+1);
				// one direction - Right
				table.setValueAt(new Float(x0[3]), 24, i+1);
				if(sum!=0) {
					// One Direction - Ratio - Anterior
					table.setValueAt(new Float(x0[0]/sum), 25, i+1);
					// One Direction - Ratio - Posterior
					table.setValueAt(new Float(x0[1]/sum), 26, i+1);
					// One Direction - Ratio - Left
					table.setValueAt(new Float(x0[0]/sum), 27, i+1);
					// One Direction - Ratio - Right
					table.setValueAt(new Float(x0[0]/sum), 28, i+1);
				}
			}
			
			int curRow = 29;
			if (opts.checkNetwork) {
				for(int k=1;k<=nLmk;k++) {
					table.setValueAt(new Float(fts.region.landmarkDist.distAvg[nEvt-1][k-1]), curRow, i+1);
					table.setValueAt(new Float(fts.region.landmarkDist.distMin[nEvt-1][k-1]), curRow+1, i+1);
	//				table.setValueAt(new Float(fts.region.landmarkDir.chgToward[nEvt-1][k-1]), curRow+2, i+1);
	//				table.setValueAt(new Float(fts.region.landmarkDir.chgAway[nEvt-1][k-1]), curRow+3, i+1);
					curRow += 2;
				}
				
				float minX0 = Float.NaN;
				float minX1 = Float.NaN;
				if(regionExist) {
					minX0 = Float.MAX_VALUE;
					float[] xx0 = fts.region.cell.dist2border[nEvt-1];
					for(int t=0;t<xx0.length;t++) {
						if(!Float.isNaN(xx0[t]))
							minX0 = Math.min(minX0, xx0[t]);
					}
					
					float[] x1 = fts.region.cell.dist2borderNorm[nEvt-1];
					minX1 = Float.MAX_VALUE;
					for(int t=0;t<x1.length;t++) {
						if(!Float.isNaN(x1[t]))
							minX1 = Math.min(minX1, x1[t]);
					}
				}
				table.setValueAt(new Float(minX0), curRow, i+1);
				curRow++;
				table.setValueAt(new Float(minX1), curRow, i+1);
				curRow++;
				table.setValueAt(new Integer(fts.network.nOccurSameLoc[nEvt-1][0]), curRow, i+1);
				curRow++;
				table.setValueAt(new Integer(fts.network.nOccurSameLoc[nEvt-1][1]), curRow, i+1);
				curRow++;
			table.setValueAt(new Integer(fts.network.nOccurSameTime[nEvt-1]), curRow, i+1);
			
			}
		}
    	
    	
    	
    	
		
	} 
	
	@Override
	protected void done() {
		frame.setVisible(false);
		JOptionPane.showMessageDialog(null, "Finish");
	}
}
