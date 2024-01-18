package va.vt.cbilAQuA2;

public class Test {
	public static void main(String[] orgs) {
//		String os = System.getProperty("os.name").toLowerCase();
//		
//		if (os.contains("win")) {
//	        System.out.println("Windows");
//	    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
//	        System.out.println("Linux or Mac");
//	    } else {
//	        System.out.println("Other");
//	    }

		float[][] distMatrix = new float[3][3];
		float[] res = Helper.DTW_Edge_input(distMatrix);
		System.out.println(res[0]);
		System.out.println(res[1]);
		
	}
}
