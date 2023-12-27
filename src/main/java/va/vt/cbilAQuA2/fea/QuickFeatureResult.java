package va.vt.cbilAQuA2.fea;

public class QuickFeatureResult {
	public FtsLst ftsLst= null;
	public float[][] dffMatExt = null;
	public int[][][] evtMap = null;
	
	public QuickFeatureResult(FtsLst ftsLst, float[][] dffMatExt, int[][][] evtMap) {
		this.ftsLst = ftsLst;
		this.dffMatExt = dffMatExt;
		this.evtMap = evtMap;
	}
}