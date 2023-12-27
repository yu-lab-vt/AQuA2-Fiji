package va.vt.cbilAQuA2.run;

import java.io.Serializable;

import va.vt.cbilAQuA2.fea.FtsLst;

public class Step6Result implements Serializable{
	private static final long serialVersionUID = 1L;
	FtsLst fts = null;
	float[][][] dffMat = null;
	public Step6Result(FtsLst fts, float[][][]dffMat){
		this.fts = fts;
		this.dffMat = dffMat;
	}
}
