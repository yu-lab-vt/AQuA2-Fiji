package va.vt.cbilAQuA2.run;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import va.vt.cbilAQuA2.CFUDealer;
import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.cfu.CFUDetectRes;
import va.vt.cbilAQuA2.cfu.CFUHelper;
import va.vt.cbilAQuA2.cfu.CFUInfo;
import va.vt.cbilAQuA2.cfu.CFUPreResult;
import va.vt.cbilAQuA2.cfu.GroupInfo;
import va.vt.cbilAQuA2.cfu.depRes;
import va.vt.cbilAQuA2.tools.GaussFilter;
import va.vt.cbilAQuA2.ui.GridBagPut;

/**
 * The first step of the whole software, preprocessing
 * registration, photobleaching correct, and calculate dF
 * 
 * @author Xuelong Mi
 * @version 1.0
 */
/**
 * @author Xuelong Mi
 *
 */
public class CFUGroupRun extends SwingWorker<Void, Integer>{
	JFrame frame = new JFrame("CFU Calculate Dependency");
	JPanel curPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();
	JLabel jLabel = new JLabel("Running");

	CFUDealer cfuDealer = null;
	int minCFU = 0;
	float pThr = 0;	
	
	/**
	 * Construct the class by imageDealer. 
	 * 
	 * @param imageDealer used to read the parameter
	 */
	public CFUGroupRun(CFUDealer cfuDealer, int minCFU, float pThr){
		this.cfuDealer = cfuDealer;
		this.minCFU = minCFU;
		this.pThr = pThr;
	}
	
	   /**
     * Premiliary processing for data, get the active region and event seeds
     * 
     * @return return the labels of different active region
     */
    protected Void doInBackground() throws Exception {
    	HashMap<Integer, CFUInfo> cfuInfo = new HashMap<>();
    	int nCFU1 = cfuDealer.cfuInfo1.size();
    	for (int i = 1; i <= nCFU1; i++) {
    		cfuInfo.put(i, cfuDealer.cfuInfo1.get(i));
    	}
    	if (!cfuDealer.opts.singleChannel) {
    		int nCFU2 = cfuDealer.cfuInfo2.size();
        	for (int i = 1; i <= nCFU2; i++) {
        		cfuInfo.put(i + nCFU1, cfuDealer.cfuInfo2.get(i));
        	}
    	}
    	int nCFU = cfuInfo.size();
		
    	ArrayList<float[]> relationRaw = new ArrayList<float[]>();
		try {
			relationRaw = Helper.readObjectFromFile(cfuDealer.proPath, "relation.ser", relationRaw.getClass());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
		ArrayList<float[]> relation = new ArrayList<>();
		float[] curPair;
		for (int k = 0; k < relationRaw.size(); k++) {
			curPair = relationRaw.get(k);
			if (curPair[2] < pThr) {
				relation.add(curPair);
				// delay is already nonnegative from last step
			}
		}
		
		Collections.sort(relation, new Comparator<float[]>() {
			@Override
			public int compare(float[] e1, float[] e2) {
				if(e1[2]<e2[2])
					return -1;
				else if (e1[2]>e2[2])
					return 1;
				else
					return 0;
			}
		});
		
		
		int[] groupLabels = new int[nCFU + 1];
		for (int i = 0; i <= nCFU; i++)
			groupLabels[i] = i;
		
		// clustering
		int id1;
		int id2;
		for (int k = 0; k < relation.size(); k++) {
			curPair = relation.get(k);
			id1 = (int) curPair[0];
			id2 = (int) curPair[1];
			id1 = CFUHelper.findRootLabel(groupLabels, id1); 
			id2 = CFUHelper.findRootLabel(groupLabels, id2);
			groupLabels[id1] = Math.min(id1, id2);
			groupLabels[id2] = Math.min(id1, id2);
		}
		
		// findSameGroup
		HashMap<Integer, ArrayList<Integer>> groupLst = new HashMap<>();
		ArrayList<Integer> l;
		int root;
		for (int k = 1; k <= nCFU; k++) {
			root = CFUHelper.findRootLabel(groupLabels, k);
			l = groupLst.getOrDefault(root, new ArrayList<Integer>());
			l.add(k);
			groupLst.put(root, l);
		}
		
		ArrayList<ArrayList<Integer>> groupLst2 = new ArrayList<ArrayList<Integer>>();
		for (Map.Entry<Integer, ArrayList<Integer>> entry : groupLst.entrySet()) {
			if (entry.getValue().size() >= minCFU)
				groupLst2.add(entry.getValue());
		}
		
		Collections.sort(groupLst2, new Comparator<ArrayList<Integer>>() {
			@Override
			public int compare(ArrayList<Integer> e1, ArrayList<Integer> e2) {
				if(e1.size() < e2.size())
					return 1;
				else if (e1.size() > e2.size())
					return -1;
				else
					return 0;
			}
		});
		
		
		groupLst = new HashMap<>();
		for (int k = 0; k < groupLst2.size(); k++) {
			groupLst.put(k + 1, groupLst2.get(k));
		}
		
		// agg groupInfo
		boolean[] visited = new boolean[nCFU + 1];
		float[] delays = new float[nCFU + 1];
		float[] addedPvalue = new float[nCFU + 1];
		HashMap<Integer, GroupInfo> groupInfo = new HashMap<>();
		ArrayList<Integer> labels;
		float minPValue;
		float sumP;		
		
		HashMap<Integer, float[]> meanPValue = new HashMap<>();
		float[] tmp;
		for (int j = 0; j < relation.size(); j++) {
			curPair = relation.get(j);
			tmp = meanPValue.getOrDefault((int) curPair[0], new float[] {0f, 0f});
			tmp[0] += curPair[2];
			tmp[1] += 1;
			meanPValue.put((int) curPair[0], tmp);
			
			tmp = meanPValue.getOrDefault((int) curPair[1], new float[] {0f, 0f});
			tmp[0] += curPair[2];
			tmp[1] += 1;
			meanPValue.put((int) curPair[1], tmp);
		}
		
		int curLabel, cnt, id;
		int labelWithMin = 0;
		ArrayList<Integer> addLst, preAddLst;
		for (int k = 1; k <= groupLst.size(); k++) {
			labels = groupLst.get(k);
			minPValue = 1;
			
			for (int i = 0; i < labels.size(); i++) {
				curLabel = labels.get(i);
				tmp = meanPValue.getOrDefault(curLabel, new float[] {0f, 1f});
				if (tmp[0] / tmp[1] < minPValue) {
					minPValue = tmp[0] / tmp[1];
					labelWithMin = curLabel;
				}
			}
			
			curLabel = labelWithMin;
			delays[curLabel] = 0;
			visited[curLabel] = true;
			addedPvalue[curLabel] = minPValue;
			preAddLst = new ArrayList<>();
			preAddLst.add(curLabel);
			
			while(preAddLst.size() > 0) {
				addLst = new ArrayList<>();
				
				for (int i = 0; i < preAddLst.size(); i++) {
					curLabel = preAddLst.get(i);
					for (int j = 0; j < relation.size(); j++) {
						curPair = relation.get(j);
						if (curPair[0] != curLabel && curPair[1] != curLabel) {
							continue;
						}else if (curPair[0] == curLabel)
							id = (int) curPair[1];
						else
							id = (int) curPair[0];
						
						if (!visited[id]) {
							delays[id] = curPair[3] + delays[curLabel];
							addedPvalue[id] = curPair[2];
							addLst.add(id);
							visited[id] = true;
						}
					}					
				}
				
				preAddLst = addLst;
			}
			
			
			groupInfo.put(k, new GroupInfo(labels, delays, addedPvalue));
		}
		
		cfuDealer.groupInfo = groupInfo;
		try {
			Helper.writeObjectToFile(cfuDealer.proPath, "groupInfo.ser", groupInfo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
    }
    
    /** 
     * Report the progress
     */
    protected void process(List<Integer> chunks) {
        int value = chunks.get(chunks.size()-1);
        String str = "";
        switch(value) {
        case 1:
            str = "Calculate all dependencies of CFUs";
            break;
        }
        jLabel.setText(str);
    }
    
    /** 
     * Adjust the interface, save the status, and let the interface show the active regions
     */
    @Override
    protected void done() {
        frame.setVisible(false);
        cfuDealer.left.left3.setEnabled(true);
        cfuDealer.left.returnButton.setEnabled(true);
        cfuDealer.left.outputButton.setEnabled(true);
        
        
        cfuDealer.right.tablemode2.setRowCount(0);
		for(int i = 1; i <= cfuDealer.groupInfo.size(); i++) {
			int[] lst = cfuDealer.groupInfo.get(i).labels;
			String str = CFUHelper.lst2String(lst);
			cfuDealer.right.tablemode2.addRow(new Object[] {new Boolean(false), new Integer(i),new Integer(lst.length),str});

		}
		
		cfuDealer.dealImage();
        
    }
    
    // helper below
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
    
}