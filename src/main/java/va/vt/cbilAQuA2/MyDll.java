package va.vt.cbilAQuA2;

import javax.swing.JOptionPane;

import com.sun.jna.*;

public interface MyDll extends Library{
	String ext = Helper.getOsExt();
	
	
//	String path = Helper.abosoluteDLLpath("DTW_Edge_input");
//	String path = "1";1
//	try {
//		path = JNADLLLoader.loadLibraryFromJar("DTW_Edge_input" + ext);
//	} catch(Exception e) {
//		JOptionPane.showMessageDialog(null, "1111", "Message", JOptionPane.INFORMATION_MESSAGE);
//	}
//	
//	MyDll mydll = (MyDll) Native.load("DTW_Edge_input" + ext, MyDll.class);
	
//	String tempDir = System.getProperty("java.io.tmpdir");
//	String home = System.getProperty("user.home") + "\\Downloads\\";
	
	MyDll mydll = (MyDll) Native.load("DTW_Edge_input" + ext, MyDll.class);
//	MyDll mydll = (MyDll) Native.load("DTW", MyDll.class);
	
	Pointer print(Pointer a, int len);
	
	Pointer[] print2D(Pointer[] a, int N, int T);
	
	Pointer DTW_Edge_input(Pointer[] a, int N, int T);
}
