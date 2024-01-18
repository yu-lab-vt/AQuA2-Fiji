package va.vt.cbilAQuA2;

import com.sun.jna.*;

public interface MyDll2 extends Library{
	String ext = Helper.getOsExt();
	MyDll2 mydll = (MyDll2) Native.load("BILCO" + ext, MyDll2.class);
//	MyDll2 mydll = (MyDll2) Native.load("Dll2", MyDll2.class);
	void runBILCO(int N, int T1, int T2, int nPair, float smo, Pointer[] iniCut, Pointer[] ref, Pointer[] tst, Pointer[] GijTemp, Pointer[] minCut);
}
