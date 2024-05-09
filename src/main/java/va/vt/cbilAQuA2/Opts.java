package va.vt.cbilAQuA2;

import java.io.Serializable;

public class Opts implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int TPatch = 20;
	public double BitDepth = 0;
	public int minSize = 20;
	public float smoXY = 0.5f;
	public float thrARScl = 3;
	public float thrTWScl = 2;
	public float thrExtZ = 1;
	public int cDelay = 2;
	public int cRise = 2;
	public float gtwSmo = 0.2f;
	public int maxStp = 11;
	public int zThr = 2;
	public int ignoreMerge = 1;
	public int mergeEventDiscon = 0;
	public float mergeEventCorr = 0;
	public int mergeEventMaxTimeDif = 2;
	public boolean singleChannel = true;
	
	public int regMaskGap = 5;
	public boolean usePG = true;
	public int cut = 200;
	public int movAvgWin = 25;
	public int extendSV = 1;
	public int legacyModeActRun = 1;
	public int getTimeWindowExt = 50;
	public int seedNeib = 1;
	public int seedRemoveNeib = 2;
	public int thrSvSig = 4;
	public int superEventdensityFirst = 1;
	public int gtwGapSeedRatio = 4;
	public int gtwGapSeedMin = 5;
	public float cOver = 0.2f;
	public float minShow1 = 0.2f;
	public float minShowEvtGUI = 0;
	
	public int correctTrend = 1;
	public int extendEvtRe = 0;
	
	public float frameRate = 0.5f;
	public float spatialRes = 0.5f;
	public float varEst = 0.02f;
	public int fgFluo = 0;
	public int bgFluo = 0;
	public float northx = 0;
	public float northy = 1;
	
	public int W = 501;
	public int H = 500;
	public int T = 1614;
	public float maxValueDat = 65535;
	
	public String filename1 = null;
	public String filename2 = null;
	
    public float[][] tempVarOrg1;
    public float[][] correctPars1;
    public float[][] stdMap1;
    public float[][] stdMapOrg1;
    public float[][] tempVarOrg2;
    public float[][] correctPars2;
    public float[][] stdMap2;
    public float[][] stdMapOrg2;
    
	public int maxSize = Integer.MAX_VALUE;
	public float maxdF1;
	public float maxdF2;
	public float compress;
	public int minDur = 5;
	public int gloDur = 20;
	public double circularityThr = 0;
	public int[] scaleRatios;
	public float step = 0.5f;
	public int spaMergeDist = 0;
	public float seedSzRatio = 0.01f;
	public float sigThr = 3.5f;
	public float minValueDat;
	public float spaSmo;
	public float overlap = 0.5f;
	public float maxDelay = 0.6f;
	public int gapExt = 5;
	public int nRoughPixel = 2000;
	public int sourceSensitivity = 8;
	public float sourceSzRatio = 0.01f;
	public boolean whetherExtend = true;
	public float propthrmin = 0.5f;
	public float propthrmax = 0.5f;
	public float propthrstep = 0.1f;
	
	public boolean ignoreTau = false;
	public boolean detectGlo = false;
	public boolean checkProp = false;
	public boolean checkNetwork = false;
	public boolean needTemp = true;
	public boolean needSpa = true;
	
	@Override
	public String toString() {
		return null;
	}
	
	public Opts( int index) {
		switch(index) {
			case 1:
				preset1();
				break;
			case 2:
				preset2();
				break;
			case 3:
				preset3();
				break;
			case 4:
				preset4();
				break;
			default:
				break;
		}
	}
	
	public void preset1() {
		System.out.println("Preset 1");
		minSize = 20;
		smoXY = 0.5f;
		thrARScl = 3;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 0.2f;
		maxStp = 11;
		zThr = 2;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = true;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 0.5f;
		spatialRes = 0.5f;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset2() {
		System.out.println("Preset 2");
		minSize = 15;
		smoXY = 0.5f;
		thrARScl = 1.75f;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 1;
		maxStp = 11;
		zThr = 2;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = true;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset3() {
		System.out.println("Preset 3");
		minSize = 8;
		smoXY = 0.5f;
		thrARScl = 2;
		thrTWScl = 2;
		thrExtZ = 1;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 1;
		maxStp = 11;
		zThr = 0;
		ignoreMerge = 1;
		mergeEventDiscon = 0;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 200;
		movAvgWin = 25;
		extendSV = 1;
		legacyModeActRun = 1;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 4;
		superEventdensityFirst = 0;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0.2f;
		minShow1 = 0.2f;
		minShowEvtGUI = 0.5f;
		ignoreTau = true;
		correctTrend = 0;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
	
	public void preset4() {
		System.out.println("Preset 4");
		minSize = 8;
		smoXY = 1;
		thrARScl = 2;
		thrTWScl = 2;
		thrExtZ = 2;
		cDelay = 2;
		cRise = 2;
		gtwSmo = 0.3f;
		maxStp = 11;
		zThr = 7;
		ignoreMerge = 0;
		mergeEventDiscon = 10;
		mergeEventCorr = 0;
		mergeEventMaxTimeDif = 2;
		
		regMaskGap = 5;
		usePG = true;
		cut = 40;
		movAvgWin = 20;
		extendSV = 0;
		legacyModeActRun = 0;
		getTimeWindowExt = 50;
		seedNeib = 1;
		seedRemoveNeib = 2;
		thrSvSig = 1;
		superEventdensityFirst = 1;
		gtwGapSeedRatio = 4;
		gtwGapSeedMin = 5;
		cOver = 0;
		minShow1 = 0.2f;
		minShowEvtGUI = 0;
		ignoreTau = true;
		correctTrend = 1;
		extendEvtRe = 0;
		
		frameRate = 1;
		spatialRes = 1;
		varEst = 0.02f;
		fgFluo = 0;
		bgFluo = 0;
		northx = 0;
		northy = 1;
	}
}
