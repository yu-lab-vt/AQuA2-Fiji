package va.vt.cbilAQuA2.fea;

public class FeatureTopResult{
	public FtsLst ftsLst= null;
	public float[][][] dffMat = null;
	public float[][][] dMat = null;
	public float[][] dffAlignedMat = null;
	public float minArea = Float.MAX_VALUE;
	public float maxArea = -Float.MAX_VALUE;
	public float minPvalue = Float.MAX_VALUE;
	public float maxPvalue = -Float.MAX_VALUE;
	public float minDecayTau = Float.MAX_VALUE;
	public float maxDecayTau = -Float.MAX_VALUE;
	public float mindffMax = Float.MAX_VALUE;
	public float maxdffMax = -Float.MAX_VALUE;
	public float minDuration = Float.MAX_VALUE;
	public float maxDuration = -Float.MAX_VALUE;	
	
	public FeatureTopResult(FtsLst ftsLst, float[][][] dffMat, float[][][] dMat, float[][] dffAlignedMat, float minArea, 
			float maxArea, float minPvalue, float maxPvalue, float minDuration, float maxDuration, 
			float mindffMax, float maxdffMax, float minDecayTau, float maxDecayTau) {
		this.ftsLst = ftsLst;
		this.dffMat = dffMat;
		this.dMat = dMat;
		this.dffAlignedMat = dffAlignedMat;
		this.minArea = minArea;
		this.maxArea = maxArea;
		this.minPvalue = minPvalue;
		this.maxPvalue = maxPvalue;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.mindffMax = mindffMax;
		this.maxdffMax = maxdffMax;
		this.minDecayTau = minDecayTau;
		this.maxDecayTau = maxDecayTau;
	}
	
}