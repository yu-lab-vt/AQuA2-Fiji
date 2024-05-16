/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package va.vt.cbilAQuA2;

import javax.swing.JOptionPane;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import inra.ijpb.watershed.MarkerControlledWatershedTransform3D;
import net.sourceforge.jdistlib.NonCentralT;
import va.vt.cbilAQuA2.ui.AQuAWelcome;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link run} method implementation with your own logic.
 * </p>
 */
public class AQuA2 implements PlugIn {

    @Override
    public void run(String arg) {
    	AQuAWelcome begin = new AQuAWelcome();
    	try {
    		check();
			begin.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void check() {
    	String os = System.getProperty("os.name").toLowerCase();
		System.out.println(os);
    	
    	try {
    		NonCentralT nctcdf = new NonCentralT(2, 0);
    	}catch (Exception e){
    		System.out.println("Need to download jdistlib.jar");
    		JOptionPane.showMessageDialog(null, "Need to download jdistlib.jar", "Message", JOptionPane.INFORMATION_MESSAGE);
    	}
    
    	try {
	    	ImagePlus input = Helper.convertToImgPlus(new float[1][1][1]);
			ImagePlus marker = Helper.convertToImgPlus(new int[1][1][1]);
			ImagePlus mask = Helper.convertToImgPlus(new int[2][2][2]);
			MarkerControlledWatershedTransform3D watershed = new MarkerControlledWatershedTransform3D (input, marker, mask, 26);
    	}catch (Exception e){
    		JOptionPane.showMessageDialog(null, "Need to download MorphoLib.jar", "Message", JOptionPane.INFORMATION_MESSAGE);
    	}
    	
    	
//    	System.setProperty("jna.debug_load", "true");
    	
    	try {
		    Pointer[] pIniCut = Helper.jna2DArray(new float[1][1]);
		    float[][] distMatrix = new float[3][3];
			float[] res = Helper.DTW_Edge_input(distMatrix);
//			System.out.println(res[0]);
			
//			Helper.BILCO(float[][] ref, float[][] tst, int[][] Gij, float smo, float[][] initialCut)
    	}catch (Exception e){
    		JOptionPane.showMessageDialog(null, "Need to update Jna lib", "Message", JOptionPane.INFORMATION_MESSAGE);
    	}
    	System.out.println("Check done!");
    }
    
    public static void main(String[] args) {
    	AQuAWelcome begin = new AQuAWelcome();
    	try {
    		// To Check
    		check();
    		
			begin.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
