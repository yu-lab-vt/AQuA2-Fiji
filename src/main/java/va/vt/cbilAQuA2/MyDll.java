package va.vt.cbilAQuA2;

import com.sun.jna.*;

public interface MyDll extends Library{
	String ext = Helper.getOsExt();
	
	MyDll mydll = (MyDll) Native.load("DTW_Edge_input" + ext, MyDll.class);
	
	Pointer print(Pointer a, int len);
	
	Pointer[] print2D(Pointer[] a, int N, int T);
	
	Pointer DTW_Edge_input(Pointer[] a, int N, int T);
}
