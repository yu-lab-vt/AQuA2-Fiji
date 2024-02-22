package va.vt.cbilAQuA2;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.process.BinaryProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.util.ThreadUtil;
import inra.ijpb.morphology.Reconstruction3D;
import inra.ijpb.watershed.WatershedTransform3D;
import va.vt.cbilAQuA2.run.Step3MajorityResult;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import com.sun.jna.*;


public class Helper {
	
	/**
	 * Writes an object to a file using Java serialization.
	 *
	 * @param proPath   The path of the directory where the file will be created.
	 * @param fileName  The name of the file to be created.
	 * @param toWrite   The object to be written.
	 * @throws IOException if an I/O error occurs during the writing process.
	 */
	public static void writeObjectToFile(String proPath, String fileName, Object toWrite) throws IOException {
	    FileOutputStream fileOutputStream = null;
	    ObjectOutputStream objectOutputStream = null;
	    
	    try {
	        // Create a file output stream
	        fileOutputStream = new FileOutputStream(new File(proPath + fileName));	        
	        // Create an object output stream
	        objectOutputStream = new ObjectOutputStream(fileOutputStream);	        
	        // Write the object to the file
	        objectOutputStream.writeObject(toWrite);
	    } finally {
	        // Close the object output stream and file output stream
	        if (objectOutputStream != null) {
	            objectOutputStream.close();
	        }
	        if (fileOutputStream != null) {
	            fileOutputStream.close();
	        }
	    }
	}
	
	public static void writeObjectToFile(String proPath, Object toWrite) throws IOException {
	    FileOutputStream fileOutputStream = null;
	    ObjectOutputStream objectOutputStream = null;
	    
	    try {
	        // Create a file output stream
	        fileOutputStream = new FileOutputStream(new File(proPath));	        
	        // Create an object output stream
	        objectOutputStream = new ObjectOutputStream(fileOutputStream);	        
	        // Write the object to the file
	        objectOutputStream.writeObject(toWrite);
	    } finally {
	        // Close the object output stream and file output stream
	        if (objectOutputStream != null) {
	            objectOutputStream.close();
	        }
	        if (fileOutputStream != null) {
	            fileOutputStream.close();
	        }
	    }
	}
	
	
	/**
	 * Reads an object from a file using Java deserialization.
	 *
	 * @param proPath          The path of the directory where the file is located.
	 * @param fileName         The name of the file to be read.
	 * @param castingDataType  The data type to which the object should be casted.
	 * @return                 The deserialized and casted object.
	 * @throws IOException            if an I/O error occurs during the reading process.
	 * @throws ClassNotFoundException if the class of the deserialized object cannot be found.
	 */
	public static <T> T readObjectFromFile(String proPath, String fileName, Class<T> castingDataType)
	        throws IOException, ClassNotFoundException {
	    FileInputStream fileInputStream = null;
	    ObjectInputStream objectInputStream = null;

	    try {
	        // Create a file input stream
	        fileInputStream = new FileInputStream(new File(proPath + fileName));

	        // Create an object input stream
	        objectInputStream = new ObjectInputStream(fileInputStream);

	        // Read the object from the file
	        Object deserializedObject = objectInputStream.readObject();

	        // Cast the object to the specified data type
	        return castingDataType.cast(deserializedObject);
	    } finally {
	        // Close the object input stream and file input stream
	        if (objectInputStream != null) {
	            objectInputStream.close();
	        }
	        if (fileInputStream != null) {
	            fileInputStream.close();
	        }
	    }
	}
	
	public static <T> T readObjectFromFile(String proPath, Class<T> castingDataType)
	        throws IOException, ClassNotFoundException {
	    FileInputStream fileInputStream = null;
	    ObjectInputStream objectInputStream = null;

	    try {
	        // Create a file input stream
	        fileInputStream = new FileInputStream(new File(proPath));

	        // Create an object input stream
	        objectInputStream = new ObjectInputStream(fileInputStream);

	        // Read the object from the file
	        Object deserializedObject = objectInputStream.readObject();

	        // Cast the object to the specified data type
	        return castingDataType.cast(deserializedObject);
	    } finally {
	        // Close the object input stream and file input stream
	        if (objectInputStream != null) {
	            objectInputStream.close();
	        }
	        if (fileInputStream != null) {
	            fileInputStream.close();
	        }
	    }
	}
	
	public static <T> T readObjectFromFile(InputStream is, Class<T> castingDataType)
	        throws IOException, ClassNotFoundException {
	    ObjectInputStream objectInputStream = null;

	    try {
	        // Create an object input stream
	        objectInputStream = new ObjectInputStream(is);

	        // Read the object from the file
	        Object deserializedObject = objectInputStream.readObject();

	        // Cast the object to the specified data type
	        return castingDataType.cast(deserializedObject);
	    } finally {
	        // Close the object input stream and file input stream
	        if (objectInputStream != null) {
	            objectInputStream.close();
	        }
	        
	    }
	}
	
	/**
	   * Compute truncated kept variances from quantiles.
	   * 
	   * @param quantiles a 2D array of quantiles
	   * @return a 2D array of truncated kept variances
	   */
	  public static float truncatedKeptVar(float quantiles) {
		if (quantiles == 0) {
			return 2.0f;
		}
		if (quantiles == 1) {
			return 0.000001f;
		}
	    float a, phi_a, mu, second_order, pars;
	    a = phi_a = mu = second_order = pars = 0;
	    
	    // a = norminv(quantiles);    
	    NormalDistribution dist = new NormalDistribution(0, 1); // Initialize a NormalDistribution object with mean=0 and std=1    
		a = (float) dist.inverseCumulativeProbability(quantiles);
		phi_a = (float) dist.density(a);
		mu = a * quantiles + phi_a;
		second_order = a * a * quantiles + 1 - quantiles + a * phi_a;
		pars = 2 * (second_order - mu * mu);
	    return pars;
	  }  
	
	  public static ArrayList<Integer> array2list(int[] data){
		  ArrayList<Integer> res = new ArrayList<Integer>();
		  if (data!=null) {
			  int T = data.length;
			  
			  for (int t = 0; t < T; t++) {
				  res.add(data[t]);
			  }
		  }
		  
		  return res;
	  }
	  
	public static ArrayList<Float> flatten(ArrayList<Float>[] list){
		ArrayList<Float> res = new ArrayList<Float>();
		float v;
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].size(); j++) {
				v = list[i].get(j);
				if (!Float.isNaN(v)) {
					res.add(v);
				}
			}
		}
		return res;
	}
	
	
	
  public static void viewMatrix(int H, int W, String name, float[][] m) {    
	System.out.println("check " + name);
	    
	int testP, testW, testH;
	testW = W;
	testH = H;
		for(int i=0;i<testH;i++){
		    for(int j=0;j<testW;j++){
		        System.out.printf("%f ", m[i][j]);
		    }
		    System.out.printf("\n");
		}
		System.out.printf("\n\n");     
	 }
  

  
  public static void viewMatrix(int H, int W, String name, int[][] m) {    
		System.out.println("check " + name);
		    
		int testP, testW, testH;
		testW = W;
		testH = H;
			for(int i=0;i<testH;i++){
			    for(int j=0;j<testW;j++){
			        System.out.printf("%d ", m[i][j]);
			    }
			    System.out.printf("\n");
			}
			System.out.printf("\n\n");     
		 }
  
  public static void viewMatrix(int H, int W, String name, boolean[][] m) {    
		System.out.println("check " + name);
		    
		int testP, testW, testH;
		testW = W;
		testH = H;
			for(int i=0;i<testH;i++){
			    for(int j=0;j<testW;j++){
			    	if (m[i][j]) {
			    		System.out.printf("%f ", 1f);
			    	}else {
			    		System.out.printf("%f ", 0f);
			    	}
			    }
			    System.out.printf("\n");
			}
			System.out.printf("\n\n");     
 }	
  
  
  public static float[] copy1Darray(float[] m) {    
	  float[] newArray = new float[m.length];
	  for(int i=0;i<m.length;i++){
	    	newArray[i] =  m[i];
	  }
	  return newArray;
  }	
  
  public static float[][] copy2Darray(float[][] m) {    
	  float[][] newArray = new float[m.length][m[0].length];
	  for(int i=0;i<m.length;i++){
		    for(int j=0;j<m[0].length;j++){
		    	newArray[i][j] =  m[i][j];
		    }
	  }
	  return newArray;
  }	
  
  public static int[][] copy2Darray(int[][] m) {    
	  int[][] newArray = new int[m.length][m[0].length];
	  for(int i=0;i<m.length;i++){
		    for(int j=0;j<m[0].length;j++){
		    	newArray[i][j] =  m[i][j];
		    }
	  }
	  return newArray;
  }	
  
  public static float[][][] copy3Darray(float[][][] m) {    
	  float[][][] newArray = new float[m.length][m[0].length][m[0][0].length];
	  for(int i=0;i<m.length;i++){
		    for(int j=0;j<m[0].length;j++){
		    	for(int k=0;k<m[0][0].length;k++){
		    		newArray[i][j][k] =  m[i][j][k];
		    	}
		    }
	  }
	  return newArray;
  }	
  
  public static int[][][] copy3Darray(int[][][] m) {    
	  int[][][] newArray = new int[m.length][m[0].length][m[0][0].length];
	  for(int i=0;i<m.length;i++){
		    for(int j=0;j<m[0].length;j++){
		    	for(int k=0;k<m[0][0].length;k++){
		    		newArray[i][j][k] =  m[i][j][k];
		    	}
		    }
	  }
	  return newArray;
  }	
	
  public static void viewMatrix(int H, int W, int P, String name, float[][][] m) {    
    System.out.println("check " + name);
    
    int testP, testW, testH;
    testP = P;
    testW = W;
    testH = H;
    for(int k=0;k<testP;k++) {
        for(int i=0;i<testH;i++){
            for(int j=0;j<testW;j++){
                System.out.printf("%f ", m[i][j][k]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n\n");
    }       
 }
  
  public static void viewMatrix(int H, int W, int P, String name, boolean[][][] m) {    
	    System.out.println("check " + name);
	    
	    int testP, testW, testH;
	    testP = P;
	    testW = W;
	    testH = H;
	    for(int k=0;k<testP;k++) {
	        for(int i=0;i<testH;i++){
	            for(int j=0;j<testW;j++){
	            	if (m[i][j][k])
	            		System.out.printf("1 ");
	            	else
	            		System.out.printf("0 ");
	            }
	            System.out.printf("\n");
	        }
	        System.out.printf("\n\n");
	    }       
	 }
  
  public static void boolean3DArrayFill(boolean[][][] BW) {
	  int H = BW.length;
	  int W = BW[0].length;
	  int T = BW[0][0].length;
	  
	  for (int x = 0; x < H; x++) {
		  for(int y = 0; y < W; y++) {
			  for (int t = 0; t < T; t++) {
				  BW[x][y][t] = true;
			  }
		  }
	  }
	  
	  return;
  }

  public static void saveMatrix(String proPath, String name, float[][][] dat) {
      String full = proPath + name;
      System.out.println(full);
      try {
        FileOutputStream f = new FileOutputStream(new File(full));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(dat);
        o.close();
        f.close();
        o.close();
        f.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }   
  }

  // Helper method to extract a 2D slice from the 3D matrix at the specified index
  public static float[][] getSlice(float[][][] matrix, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      float[][] slice = new float[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              slice[i][j] = matrix[i][j][index];
          }
      }
      return slice;
  }
  
  public static boolean[][] getSlice(boolean[][][] matrix, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      boolean[][] slice = new boolean[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              slice[i][j] = matrix[i][j][index];
          }
      }
      return slice;
  }
  
  public static int[][] getSlice(int[][][] matrix, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      int[][] slice = new int[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              slice[i][j] = matrix[i][j][index];
          }
      }
      return slice;
  }
  
  public static float[][] getSlice(float[][][] matrix, int index, int x0, int x1, int y0, int y1) {
      int rows = x1 - x0 + 1;
      int cols = y1 - y0 + 1;
      float[][] slice = new float[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              slice[i][j] = matrix[i + x0][j + y0][index];
          }
      }
      return slice;
  }

  // Helper method to set a 2D slice in the 3D matrix at the specified index
  public static void setSlice(float[][][] matrix, float[][] slice, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              matrix[i][j][index] = slice[i][j];
          }
      }
  }
  
  public static void setSlice(int[][][] matrix, float[][] slice, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              matrix[i][j][index] = (int) slice[i][j];
          }
      }
  }
  
  public static void setSliceRev(float[][][] matrix, float[][] slice, int index) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              matrix[i][j][index] = - slice[i][j];
          }
      }
  }
  
  public static ImagePlus convertToImgPlus(float[][][] dat) {
	  int H = dat.length;
      int W = dat[0].length;
      int T = dat[0][0].length;

      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      for (int z = 0; z < T; z++) {
          // Fill pixels array with your data here
          float[][] array = getSlice(dat,z);
          ImageProcessor ip = new FloatProcessor(array);
          stack.addSlice(ip);
      }

      // Create an ImagePlus from the ImageStack
      ImagePlus imp = new ImagePlus("3D Image", stack);
      return imp;
  }
  
  public static ImageStack convertToImgStk(float[][][] dat) {
	  int H = dat.length;
      int W = dat[0].length;
      int T = dat[0][0].length;

      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      for (int z = 0; z < T; z++) {
          // Fill pixels array with your data here
          float[][] array = getSlice(dat,z);
          ImageProcessor ip = new FloatProcessor(array);
          stack.addSlice(ip);
      }

      // Create an ImagePlus from the ImageStack
      return stack;
  }
  
  public static ImagePlus convertToImgPlus(float[][] dat) {
	  int H = dat.length;
      int W = dat[0].length;
      
      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      float[][] array = Helper.copy2Darray(dat);
      ImageProcessor ip = new FloatProcessor(array);
      stack.addSlice(ip);

      // Create an ImagePlus from the ImageStack
      ImagePlus imp = new ImagePlus("2D Image", stack);
      return imp;
  }
  
  public static ImagePlus convertToImgPlus(int[][][] dat) {
	  int H = dat.length;
      int W = dat[0].length;
      int T = dat[0][0].length;

      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      for (int z = 0; z < T; z++) {
          // Fill pixels array with your data here
          int[][] array = getSlice(dat,z);
          ImageProcessor ip = new FloatProcessor(array);
          stack.addSlice(ip);
      }

      // Create an ImagePlus from the ImageStack
      ImagePlus imp = new ImagePlus("3D Image", stack);
      return imp;
  }
  
  public static ImagePlus convertToImgPlus(int[][] dat) {
	  int H = dat.length;
      int W = dat[0].length;

      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      int[][] array = Helper.copy2Darray(dat);
      ImageProcessor ip = new FloatProcessor(array);
      stack.addSlice(ip);
  

      // Create an ImagePlus from the ImageStack
      ImagePlus imp = new ImagePlus("2D Image", stack);
      return imp;
  }
  
  
  public static ImagePlus convertToImgPlus(float[][][] dat, int x0, int x1, int y0, int y1, int t0, int t1) {
	  int H = x1 - x0 + 1;
      int W = y1 - y0 + 1;
//      int T = dat[0][0].length;

      // Create an empty ImageStack
      ImageStack stack = new ImageStack(H, W);

      // Fill the ImageStack with slices
      for (int z = t0; z <= t1; z++) {
          // Fill pixels array with your data here
          float[][] array = getSlice(dat,z, x0, x1, y0, y1);
          ImageProcessor ip = new FloatProcessor(array);
          stack.addSlice(ip);
      }

      // Create an ImagePlus from the ImageStack
      ImagePlus imp = new ImagePlus("3D Image", stack);
      return imp;
  }
  
  
  // baselineLinearEstimate 
  /**
   * Calculates the moving mean of a 3D matrix along the third dimension.
   *
   * @param dat Input 3D matrix
   * @param movAvgWin   Number of elements used for the moving mean calculation
   * @return    Moving mean of the input matrix
   */
  public static float[][][] movingMean(float[][][] dat, int movAvgWin) {
      int m = dat.length;
      int n = dat[0].length;
      int p = dat[0][0].length;

      // Create the output array
      float[][][] datMA = new float[m][n][p];
      
	  int rWin = (movAvgWin - 1) / 2;
	  int lWin = movAvgWin - 1 - rWin;

      // Calculate the moving mean
      for (int i = 0; i < m; i++) {
          for (int j = 0; j < n; j++) {
        	  float curSum = 0;
			  int cnt = 0;
			  for (int k = 0; k < rWin; k ++) {
				  curSum += dat[i][j][k];
				  cnt ++;
			  }
			  for (int k = 0; k < p; k++) {
				  if (k + rWin < p) {
					  curSum += dat[i][j][k + rWin];
					  cnt ++;
				  }
				  if (k - lWin - 1 >= 0) {
					  curSum -= dat[i][j][k - lWin - 1];
					  cnt --;
				  }
				  datMA[i][j][k] = curSum / cnt;
			  }
          }
      }

      return datMA;
  }
  
  public static float[] movingMean(float[] dat, int movAvgWin) {
      int p = dat.length;

      // Create the output array
      float[] datMA = new float[p];
      
	  int rWin = (movAvgWin - 1) / 2;
	  int lWin = movAvgWin - 1 - rWin;

      // Calculate the moving mean
	  float curSum = 0;
	  int cnt = 0;
	  for (int k = 0; k < rWin; k ++) {
		  curSum += dat[k];
		  cnt ++;
	  }
	  for (int k = 0; k < p; k++) {
		  if (k + rWin < p) {
			  curSum += dat[k + rWin];
			  cnt ++;
		  }
		  if (k - lWin - 1 >= 0) {
			  curSum -= dat[k - lWin - 1];
			  cnt --;
		  }
		  datMA[k] = curSum / cnt;
	  }

      return datMA;
  }
  
  public static float[] smooth(float[] dat, int windowSize) {
      float[] smoothed = new float[dat.length];
      for (int i = 0; i < dat.length; i++) {
          int start = Math.max(0, i - windowSize + 1);
          int end = Math.min(dat.length - 1, i + windowSize - 1);
          int count = end - start + 1;
          float sum = 0;
          for (int j = start; j <= end; j++) {
              if (!Float.isNaN(dat[j])) {
                  sum += dat[j];
              } else {
                  count--;
              }
          }
          if (count > 0) {
              smoothed[i] = sum / count;
          } else {
              smoothed[i] = Float.NaN;
          }
      }  
      return smoothed;
  }  

  /**
   * Fills any NaN values in a 3D matrix with NaN.
   * 
   * @param dat The input 3D matrix.
   * @param datMA The output 3D matrix with NaN values filled.
   * @return maxValue.
   */
  public static float fillNaN(float[][][] dat, float[][][] datMA) {
	  float maxV = Float.MIN_VALUE; // initialize maxV to the smallest possible float value
      for (int i = 0; i < dat.length; i++) {
          for (int j = 0; j < dat[i].length; j++) {
              for (int k = 0; k < dat[i][j].length; k++) {
                  if (Float.isNaN(dat[i][j][k])) {
                      datMA[i][j][k] = Float.NaN;
                  } else {
                	  maxV = Math.max(maxV, dat[i][j][k]);
                  }
              }
          }
      }
	return maxV;
  }
  
  /**
   * This method reshapes a 3D float array into a new 2D float array with the specified number of columns.
   *
   * @param dat3D The input 3D float array to be reshaped.
   * @param P The number of columns in the output 2D float array.
   * @return A new 2D float array with the specified number of columns, containing the reshaped data from the input array.
   */
  public static float[][] reshape(float[][][] dat3D, int P) {
      int numRows = dat3D.length * dat3D[0].length;
      int numCols = P;
      float[][] dat2D = new float[numRows][numCols];
      System.out.println("Size of dat3D: " + dat3D.length + " x " + dat3D[0].length + " x " + dat3D[0][0].length);
      System.out.println("Size of newdat: " + numRows + " x " + numCols);
      for (int row = 0; row < numRows; row++) {
          int z = row / dat3D[0].length;
          int y = row % dat3D[0].length;
          for (int col = 0; col < numCols; col++) {
              int x = col;
              dat2D[row][col] = dat3D[z][y][x];
          }
      }
      return dat2D;
  }

  /**
   * Reshapes a 2-dimensional matrix into a 3-dimensional matrix with specified dimensions.
   *
   * @param dat  The original 2-dimensional matrix to be reshaped.
   * @param W    The desired width of the reshaped matrix.
   * @param H    The desired height of the reshaped matrix.
   * @param P    The desired depth of the reshaped matrix.
   * @return     The reshaped 3-dimensional matrix.
   * @throws IllegalArgumentException if the reshape dimensions mismatch.
   */
  public static float[][][] reshape(float[][] dat, int W, int H, int P) {
      int numRows = dat.length;
      int numCols = dat[0].length;
      
      // Check if the reshape is possible
      if (numRows * numCols != W * H * P) {
          throw new IllegalArgumentException("Reshape dimensions mismatch");
      }

      float[][][] dat3D = new float[W][H][P];
      int index = 0;

      // Reshape the matrix
      for (int p = 0; p < P; p++) {
          for (int h = 0; h < H; h++) {
              for (int w = 0; w < W; w++) {
                  int originalRow = index / numCols;
                  int originalCol = index % numCols;
                  dat3D[w][h][p] = dat[originalRow][originalCol];
                  index++;
              }
          }
      }
      return dat3D;
  }
  
  public static float findMax(float[][][] dat) {
    float maxV = Float.MIN_VALUE; // initialize maxV to the smallest possible float value
      // iterate over all elements of the array to find the maximum value
      for (int i = 0; i < dat.length; i++) {
          for (int j = 0; j < dat[i].length; j++) {
              for (int k = 0; k < dat[i][j].length; k++) {
            	  maxV = Math.max(maxV, dat[i][j][k]);
              }
          }
      }
    return maxV;
  }
  
  /**
   * Returns an array of unique elements from the i-th row of the given
   * two-dimensional integer array.
   *
   * @param minPosition the input matrix
   * @param i the index of the row to extract
   * @return an array of unique elements from the i-th row of the input matrix
   */
  public static int[] getUniqueRow(int[][] minPosition, int i) {
      int[] row = minPosition[i];
      Set<Integer> set = new HashSet<Integer>();
      for (int val : row) {
          set.add(val);
      }
      int[] curP = new int[set.size()];
      int j = 0;
      for (int val : set) {
          curP[j++] = val;
      }
      return curP;
  }
  
  /**
   * Finds the unique elements in a list of integers and returns them in sorted order.
   * This function removes any duplicate values and only retains the distinct values.
   *
   * @param arr The list of integers.
   * @return A sorted list of unique integers.
   */
  public static List<Integer> unique(List<Integer> arr) {
      Set<Integer> uniqueSet = new HashSet<>(arr); // Use a HashSet to efficiently store unique elements
      List<Integer> uniqueList = new ArrayList<>(uniqueSet); // Convert the set to a list
      Collections.sort(uniqueList); // Sort the list in ascending order
      return uniqueList;
  }
  
  /**
   * Filter out NaN values from a 1D array based on a corresponding 1D array of data values.
   *
   * @param dataValues The corresponding data values to check for NaN values.
   * @param vector The vector to filter NaN values from.
   * @return A new vector with NaN values removed.
   */
  public static int[] removeNanIndices(float[] value, int[] curP) {
    List<Integer> indicesToKeep = new ArrayList<>();
    for (int i = 0; i < value.length; i++) {
        if (!Float.isNaN(value[i])) {
            indicesToKeep.add(curP[i]);
        }
    }
    int[] newCurP = new int[indicesToKeep.size()];
    for (int i = 0; i < indicesToKeep.size(); i++) {
        newCurP[i] = indicesToKeep.get(i);
    }
    return newCurP;
  }
  
  /**
   * Removes NaN values from a 1D float array
   * 
   * @param value the input 1D float array
   * @return a new 1D float array with NaN values removed
   */
  public static float[] removeNaN(float[] value) {
      int count = 0;
      for (float f : value) {
          if (!Float.isNaN(f)) {
              count++;
          }
      }
      float[] result = new float[count];
      int index = 0;
      for (float f : value) {
          if (!Float.isNaN(f)) {
              result[index] = f;
              index++;
          }
      }
      return result;
  }
  
  // noiseEstimation
  /**
   * implement 
   * datOrg(:,:,1:end-1)
   * Extracts a sub-matrix from a 3D float matrix by excluding the last dimension.
   * @param dat The original 3D matrix.
   * @return The sub-matrix without the last dimension.
   */
  public static float[][][] extractSubExcludingLast(float[][][] dat) {
      int width = dat.length;
      int height = dat[0].length;
      int depth = dat[0][0].length;
      int newDepth = depth - 1;

      float[][][] subMatrix = new float[width][height][newDepth];
      for (int i = 0; i < width; i++) {
          for (int j = 0; j < height; j++) {
              for (int k = 0; k < newDepth; k++) {
                  subMatrix[i][j][k] = dat[i][j][k];
              }
          }
      }
      return subMatrix;
  }
  
  /**
   * implement 
   * datOrg(:,:,2:end)
   * Extracts a sub-matrix from a 3D float matrix by excluding the first slice along the third dimension.
   * @param dat The original 3D matrix.
   * @return The sub-matrix excluding the first slice along the third dimension.
   */
  public static float[][][] extractSubExcludingFirst(float[][][] dat) {
      int width = dat.length;
      int height = dat[0].length;
      int depth = dat[0][0].length;
      int newDepth = depth - 1;

      float[][][] subMatrix = new float[width][height][newDepth];
      for (int i = 0; i < width; i++) {
          for (int j = 0; j < height; j++) {
              for (int k = 1; k < depth; k++) {
                  subMatrix[i][j][k - 1] = dat[i][j][k];
              }
          }
      }
      return subMatrix;
  }
  
  /**
   * implement
   * dat / n
   * Divides each element of a 2D matrix by a scalar value.
   * @param dat The original 2D matrix.
   * @param n   The scalar value to divide by.
   * @return The resulting matrix after division by the scalar.
   */
  public static float[][] divideByScalar(float[][] dat, float n) {
      int rows = dat.length;
      int columns = dat[0].length;

      float[][] result = new float[rows][columns];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < columns; j++) {
              result[i][j] = dat[i][j] / n;
          }
      }
      return result;
  }
  
  /**
   * implement 
   * (datSmo(:,:,1:end-1) - datSmo(:,:,2:end)).^2
   * Performs element-wise subtraction and squaring of two 3D float matrices.
   * @param a The first 3D matrix.
   * @param b The second 3D matrix.
   * @return The result of element-wise subtraction and squaring.
   */
  public static float[][][] elementWiseSubtractionAndSquare(float[][][] a, float[][][] b) {
      int width = a.length;
      int height = a[0].length;
      int depth = a[0][0].length;

      float[][][] result = new float[width][height][depth];
      for (int i = 0; i < width; i++) {
          for (int j = 0; j < height; j++) {
              for (int k = 0; k < depth; k++) {
                  float diff = a[i][j][k] - b[i][j][k];
                  result[i][j][k] = diff * diff;
              }
          }
      }
      return result;
  }
  
  /**
   * implement 
   * mean(x,3,'omitnan')
   * Calculates the mean along the third dimension of a 3D float matrix, omitting NaN values.
   * @param dat The original 3D matrix.
   * @return The mean along the third dimension.
   */
  public static float[][] reduceMean(float[][][] dat) {
      int width = dat.length;
      int height = dat[0].length;
      int depth = dat[0][0].length;

      float[][] result = new float[width][height];
      for (int i = 0; i < width; i++) {
          for (int j = 0; j < height; j++) {
              float sum = 0;
              int count = 0;

              for (int k = 0; k < depth; k++) {
                  float value = dat[i][j][k];

                  if (!Float.isNaN(value)) {
                      sum += value;
                      count++;
                  }
              }
              
              result[i][j] = sum / count;
          }
      }
      return result;
  }
  
  /**
   * Calculates the mean of squared differences between two 3D matrices along the third dimension, omitting NaN values.
   *
   * @param subMatrix1 The 1st 3D matrix.
   * @param subMatrix2 The 2nd 3D matrix.
   * @return The mean of squared differences along the third dimension.
   */
  public static float[][] meanSquaredDifferences(float[][][] dat) {
	  int H = dat.length;
	  int W = dat[0].length;
	  int T = dat[0][0].length;
	  float[][] result = new float[H][W];
	  int cnt;
	  float curSum;
	  float dif;
	  
	  for (int x = 0; x < H; x++) {
		  for (int y = 0; y < W; y++) {
			  curSum = 0;
			  cnt = 0;
			  for (int t = 0; t < T - 1; t++) {
				  dif = dat[x][y][t + 1] - dat[x][y][t];
				  if (!Float.isNaN(dif)) {
					  cnt++;
					  curSum += dif * dif;
				  }
			  }
			  result[x][y] = curSum / cnt;
		  }
	  }
      return result;
  }
  
  public static float[][] computeDiff(int dim1, int dim2, int dim3, float[][][] dat) {
    float[][] res = new float[dim1][dim2];
    float[][] datSlice1, datSlice2, diff, squaredDiff;
    for (int k = 0; k < dim3 - 1; k++) {
        datSlice1 = datSlice2 = new float[dim1][dim2];
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                datSlice1[i][j] = dat[i][j][k];
                datSlice2[i][j] = dat[i][j][k+1];
            }
        }
        diff = matrixSubtract(datSlice1, datSlice2);
        squaredDiff = matrixElementwiseMultiply(diff, diff);
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                if (Float.isNaN(squaredDiff[i][j])) {
                  res[i][j] = Float.NaN;
                } else if (k == 0) {
                  res[i][j] = squaredDiff[i][j];
                } else {
                    res[i][j] = (res[i][j] * k + squaredDiff[i][j]) / (k + 1);
                }
            }
        }
    }
    return res;
  }  
  
  public static float[][] matrixSubtract(float[][] A, float[][] B) {
      int rows = A.length;
      int cols = A[0].length;
      float[][] C = new float[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              C[i][j] = A[i][j] - B[i][j];
          }
      }
      return C;
  }

  public static float[][] matrixElementwiseMultiply(float[][] A, float[][] B) {
      int rows = A.length;
      int cols = A[0].length;
      float[][] C = new float[rows][cols];
      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              C[i][j] = A[i][j] * B[i][j];
          }
      }
      return C;
  }

  public static float[][] imfilter(float[][] A, float[][] h) {
    int rows = A.length;
    int cols = A[0].length;
    int filterSize = h.length;
    int paddingSize = filterSize / 2; // assuming odd filter size    
    float[][] result = new float[rows][cols];
    
    // add padding to input matrix A
    float[][] paddedA = new float[rows + 2 * paddingSize][cols + 2 * paddingSize];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            paddedA[i + paddingSize][j + paddingSize] = A[i][j];
        }
    }
    
    // perform 2D convolution
    for (int i = paddingSize; i < rows + paddingSize; i++) {
        for (int j = paddingSize; j < cols + paddingSize; j++) {
            float sum = 0;
            for (int k = -paddingSize; k <= paddingSize; k++) {
                for (int l = -paddingSize; l <= paddingSize; l++) {
                    sum += paddedA[i + k][j + l] * h[k + paddingSize][l + paddingSize];
                }
            }
            result[i - paddingSize][j - paddingSize] = sum;
        }
    }    
    return result;
  }
  
  // obtainBias
  public static float[][] loadMatrix(InputStream is) {
  		float[][] data = null;
		try {
			InputStreamReader reader0 = new InputStreamReader(is);
		    BufferedReader reader = new BufferedReader(reader0);
		    String line;
		    // since the table is known.
		    int numRows = 215;
		    int numCols = 55;
//		    reader.close();
		    data = new float[numRows][numCols];
		    int row = 0;
		    while ((line = reader.readLine()) != null) {
		        String[] values = line.split(",");
		        for (int col = 0; col < numCols; col++) {
		            data[row][col] = Float.parseFloat(values[col]);
		        }
		        row++;
		    }
		    reader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return data;
  }
  
  public static float[][] loadMatrix(String filePath) {
		float[][] data = null;
		try {
		    BufferedReader reader = new BufferedReader(new FileReader(filePath));
		    String line = reader.readLine();
		    int numRows = 0;
		    int numCols = line.split(",").length;
		    while (line != null) {
		        numRows++;
		        line = reader.readLine();
		    }
		    reader.close();
		    data = new float[numRows][numCols];
		    reader = new BufferedReader(new FileReader(filePath));
		    int row = 0;
		    while ((line = reader.readLine()) != null) {
		        String[] values = line.split(",");
		        for (int col = 0; col < numCols; col++) {
		            data[row][col] = Float.parseFloat(values[col]);
		        }
		        row++;
		    }
		    reader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return data;
}

  public static float[] loadArray(InputStream is) {
      float[] array = null;
//      try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	  try {
		  InputStreamReader reader0 = new InputStreamReader(is);
		  BufferedReader br = new BufferedReader(reader0);
          String line;
          int count = 0;
          while ((line = br.readLine()) != null) {
              if (count == 0) {
                  array = new float[line.split(",").length];
              }
              String[] elements = line.split(",");
              for (int i = 0; i < elements.length; i++) {
                  array[i] = Float.parseFloat(elements[i]);
              }
              count++;
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return array;
  }
  
  public static float[] loadArray(String filePath) {
      float[] array = null;
      try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
          String line;
          int count = 0;
          while ((line = br.readLine()) != null) {
              if (count == 0) {
                  array = new float[line.split(",").length];
              }
              String[] elements = line.split(",");
              for (int i = 0; i < elements.length; i++) {
                  array[i] = Float.parseFloat(elements[i]);
              }
              count++;
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      return array;
  }
  
  /**
   * Replicates a 2D matrix along the third dimension to create a 3D matrix.
   *
   * @param dat2D The original 2D matrix to be replicated.
   * @param n     The number of times to replicate the matrix along the third dimension.
   * @return      The replicated 3D matrix.
   */
  public static float[][][] repmat(float[][] dat2D, int n) {
      int rows = dat2D.length;
      int cols = dat2D[0].length;

      // Create a new 3D matrix to store the replicated matrix
      float[][][] dat3D = new float[rows][cols][n];

      // Replicate the 2D matrix along the third dimension
      for (int i = 0; i < n; i++) {
          for (int j = 0; j < rows; j++) {
              for (int k = 0; k < cols; k++) {
                  dat3D[j][k][i] = dat2D[j][k];
              }
          }
      }

      // Return the replicated 3D matrix
      return dat3D;
  }
  
  /**
   * Generates a matrix with each row containing repetitions of numbers from 1 to n.
   *
   * @param n The number of elements and repetitions.
   * @return The generated matrix.
   */
  public static List<List<Integer>> repmat(int n) {
      // Initialize the matrix
      List<List<Integer>> matrix = new ArrayList<>();

      // Generate matrix with repeated elements
      for (int i = 0; i < n; i++) {
          List<Integer> row = new ArrayList<>();
          for (int j = 0; j < n; j++) {
              row.add(i);
          }
          matrix.add(row);
      }
      return matrix;
  }
  
  /**
   * Creates a matrix by replicating the elements of the 1-dimensional array 'arr' in a tiled manner.
   *
   * @param arr     The input 1-dimensional array.
   * @param scalar1 The number of times to replicate the array in the rows.
   * @param scalar2 The number of times to replicate the array in the columns.
   * @return A 2-dimensional array with dimensions scalar1 * arr.length x scalar2 * arr.length,
   *         where the elements are replicated from the input array 'arr'.
   */
  public static int[][] repmat(int[] arr, int scalar1, int scalar2) {
      int[][] result = new int[scalar1 * arr.length][scalar2 * arr.length];

      for (int i = 0; i < scalar1 * arr.length; i++) {
          for (int j = 0; j < scalar2 * arr.length; j++) {
              result[i][j] = arr[j % arr.length];
          }
      }

      return result;
  }
  
  public static float getMean(float[][] matrix, int[] rows, int col) {
    float sum = 0;
    int count = 0;
    for (int i : rows) {
        float value = matrix[i][col];
        if (!Float.isNaN(value)) {
            sum += value;
            count++;
        }
    }
    return count > 0 ? sum / count : Float.NaN;
  }
  
  public static float getMean(ArrayList<Float> data) {
	    float res = 0;
	    for (int i = 0; i < data.size(); i++) {
	    	res += data.get(i);
	    }
	    res /= data.size();
	    return res;
	  }
  public static float getSumFloat(ArrayList<Float> data) {
	    float res = 0;
	    for (int i = 0; i < data.size(); i++) {
	    	res += data.get(i);
	    }
	    return res;
  }
  public static int getSumInteger(ArrayList<Integer> data) {
	    int res = 0;
	    for (int i = 0; i < data.size(); i++) {
	    	res += data.get(i);
	    }
	    return res;
}
  
  /**
   * Converts linear indices to subscripts.
   *

   */
  public static int[] ind2sub(int H, int W, int index) {
	  int[] p = new int[2];
	  p[0] = index / W;
	  p[1] = index - p[0] * W;
	  return p;
  }
  
  public static int[] ind2sub(int H, int W, int T, int index) {
	  int[] p = new int[3];
	  p[0] = index / W / T;
	  index -= p[0] * W * T;
	  p[1] = index / T;
	  index -= p[1] * T;
	  p[2] = index;
	  return p;
  }

  /**
   * Calculates the linear indices based on the given subscripts and matrix dimensions.
   *
   * @param dimensions The dimensions of the matrix as a list [W, H, P].
   * @param indices    The subscripts as separate lists [iw], [ih], [ip].
   * @return The list of linear indices.
   */
  public static int sub2ind(int H, int W, int x, int y) {
	  return x * W + y;
  }
  
  public static int sub2ind(int H, int W, int T, int x, int y, int z) {
	  return x * W * T + y * T + z;
  }

  /**
   * Generates a disk-shaped structuring element of radius r.
   *
   * @param r the radius of the disk
   * @return a 2D boolean array representing the disk-shaped structuring element
   */
  public static boolean[][] getDiskStrel(int r) {
      // calculate the size of the structuring element
      int size = 2 * r + 1;
      // create a 2D boolean array to represent the structuring element
      boolean[][] strel = new boolean[size][size];
      // calculate the square of the radius for distance comparison
      int radiusSquared = r * r;
      // loop over all the elements of the structuring element
      for (int i = -r; i <= r; i++) {
          for (int j = -r; j <= r; j++) {
              // calculate the squared distance from the center of the structuring element
              int distanceSquared = i * i + j * j;
              // if the squared distance is less than or equal to the squared radius, set the element to true
              if (distanceSquared <= radiusSquared) {
                  strel[i + r][j + r] = true;
              }
          }
      }
      // return the generated structuring element
      return strel;
  }  
  
  	public static void bfsConn3DHelper(boolean[][][] dActVoxDi, int[][][] label, int H, int W, int T, int i, int j, int k, int curLabel, HashMap<Integer, ArrayList<int[]>> map) {
  		Stack<int[]> stk = new Stack<int[]>();
  		ArrayList<int[]> l = new ArrayList<>();
  		stk.push(new int[] {i,j,k});
  		int x, y, z;
  		while (!stk.empty()) {
  			int[] p = stk.pop();
  			if (label[p[0]][p[1]][p[2]] == 0) {
  				label[p[0]][p[1]][p[2]] = curLabel;
  				l.add(p);
  				for (int dk = -1; dk <= 1; dk ++) {
  	  				for (int dj = -1; dj <= 1; dj ++) {
  	  					for (int di = -1; di <= 1; di ++) {
  	  						x = p[0] + di;
  	  						y = p[1] + dj;
  	  						z = p[2] + dk;
	  	  					if (x >= 0 && y >= 0 && z >= 0 && x < H && y < W && z < T && dActVoxDi[x][y][z] && label[x][y][z] == 0) {
	  	  						stk.push(new int[] {x,y,z});
	  	  					}
  	  					}
  	  					
  	  				}	
  	  			}
  			}
  		}
  		map.put(curLabel, l);
		
	}
  

  	public static void bfsConn3D(boolean[][][] dActVoxDi, HashMap<Integer, ArrayList<int[]>> map) {
		int H = dActVoxDi.length;
		int W = dActVoxDi[0].length;
		int T = dActVoxDi[0][0].length;
		
		
		int[][][] label = new int[H][W][T];
		int curLabel = 1;
		for(int k=0;k<T;k++) {
			for(int j=0;j<W;j++) {
				for(int i=0;i<H;i++) {
					if (dActVoxDi[i][j][k] && label[i][j][k] == 0) {
						
						bfsConn3DHelper(dActVoxDi, label, H, W, T, i, j, k, curLabel, map);
						curLabel += 1;
					}
				}
			}
		}
	}
  	
  	public static void bfsConn2DHelper(boolean[][] dActVoxDi, int[][] label, int H, int W, int i, int j, int curLabel, HashMap<Integer, ArrayList<int[]>> map) {
  		Stack<int[]> stk = new Stack<int[]>();
  		ArrayList<int[]> l = new ArrayList<>();
  		stk.push(new int[] {i,j});
  		int x, y;
  		while (!stk.empty()) {
  			int[] p = stk.pop();
  			if (label[p[0]][p[1]] == 0) {
  				label[p[0]][p[1]] = curLabel;
  				l.add(p);
  				for (int dj = -1; dj <= 1; dj ++) {
  					for (int di = -1; di <= 1; di ++) {
  						x = p[0] + di;
  						y = p[1] + dj;
  	  					if (x >= 0 && y >= 0 && x < H && y < W && dActVoxDi[x][y] && label[x][y] == 0) {
  	  						stk.push(new int[] {x,y});
  	  					}
  					}
  					
  				}	

  			}
  		}
  		map.put(curLabel, l);
		
	}
  	
  	public static void bfsConn2D(boolean[][] dActVoxDi, HashMap<Integer, ArrayList<int[]>> map) {
		int H = dActVoxDi.length;
		int W = dActVoxDi[0].length;
		
		
		int[][] label = new int[H][W];
		int curLabel = 1;
		for(int j=0;j<W;j++) {
			for(int i=0;i<H;i++) {
				if (dActVoxDi[i][j] && label[i][j] == 0) {
					bfsConn2DHelper(dActVoxDi, label, H, W, i, j, curLabel, map);
					curLabel += 1;
				}
			}
		}

	}
  	
  	public static float[] crop1D (float[] data, int start, int end) {
  		float[] res = new float[end - start + 1];
  		for (int i = start; i <= end; i++) {
  			res[i - start] = data[i];
  		}
  		return res;
  	}
  	
  	public static float[][] crop2D (float[][] data, int x0, int x1, int y0, int y1) {
  		float[][] res = new float[x1 - x0 + 1][y1 - y0 + 1];
  		for (int x = x0; x <= x1; x++) {
  			for (int y = y0; y <= y1; y++) {
  					res[x - x0][y - y0] = data[x][y];
  			}
  		}
  		return res;
  	}
  	
  	public static float[][][] crop3D (float[][][] data, int x0, int x1, int y0, int y1, int t0, int t1) {
  		float[][][] res = new float[x1 - x0 + 1][y1 - y0 + 1][t1 - t0 + 1];
  		for (int x = x0; x <= x1; x++) {
  			for (int y = y0; y <= y1; y++) {
  				for (int t = t0; t <= t1; t++) {
  					res[x - x0][y - y0][t - t0] = data[x][y][t];
  				}
  			}
  		}
  		return res;
  	}
  	
  	public static int[][][] crop3D (int[][][] data, int x0, int x1, int y0, int y1, int t0, int t1) {
  		int[][][] res = new int[x1 - x0 + 1][y1 - y0 + 1][t1 - t0 + 1];
  		for (int x = x0; x <= x1; x++) {
  			for (int y = y0; y <= y1; y++) {
  				for (int t = t0; t <= t1; t++) {
  					res[x - x0][y - y0][t - t0] = data[x][y][t];
  				}
  			}
  		}
  		return res;
  	}
	
	/**
	* Dilates a 3D binary image with a disk-shaped structuring element of radius r.
 	* @param BW the input binary image to be dilated
	* @param r the radius of the disk-shaped structuring element
 	* @return the dilated binary image
	*/
  	public static boolean[][][] imdilate(boolean[][][] BW, int r) {
		System.out.println("imdilate");
	    // Create the disk-shaped structuring element
	    boolean[][] se = Helper.getDiskStrel(r);  // strel('disk', r)
	    // Create the output binary image with the same size as the input
	    boolean[][][] BW2 = new boolean[BW.length][BW[0].length][BW[0][0].length];
	    // Apply dilation to each slice of the input binary image using the structuring element
	    for (int k = 0; k < BW[0][0].length; k++) {
	        for (int i = 0; i < BW.length; i++) {
	            for (int j = 0; j < BW[0].length; j++) {
	                // Find the maximum value in the neighborhood defined by the structuring element
	                boolean maxVal = false;
	                for (int ii = -r; ii <= r; ii++) {
	                    for (int jj = -r; jj <= r; jj++) {
	                        int ni = i + ii;
	                        int nj = j + jj;
	                        if (ni >= 0 && ni < BW.length && nj >= 0 && nj < BW[0].length) {
	                            if (se[ii + r][jj + r] && BW[ni][nj][k]) {
	                                maxVal = true;
	                                break;
	                            }
	                        }
	                    }
	                    if (maxVal) {
	                        break;
	                    }
	                }
	                // Set the output value to the maximum value found in the neighborhood
	                BW2[i][j][k] = maxVal;
	            }
	        }
	    }
	    return BW2;
	}
	
	/**
	* This method implements the bwlabeln algorithm which labels the connected components in a 3D binary array.
	* @param BW 3D boolean array representing the binary image.
	* @param conn Integer specifying the connectivity of the algorithm (6, 18 or 26).
	* @return A 3D integer array with the same dimensions as BW, where each element represents the label of its connected component.
	*/
  	public static int[][][] bwlabeln(boolean[][][] BW, int conn) {
		System.out.println("bwlabeln");
	    int[] dim = new int[]{BW.length, BW[0].length, BW[0][0].length};
	    int[][][] L = new int[dim[0]][dim[1]][dim[2]];
	    int label = 0;
	    ArrayList<Integer> queue = new ArrayList<Integer>();
	    int[] nbrhdOffsets = new int[conn];
	    if (conn == 6) {
	        nbrhdOffsets = new int[]{-1, 0, 0, 1, 0, 0, 0, -1, 0, 0, 1, 0};
	    } else if (conn == 18) {
	        nbrhdOffsets = new int[]{-1, 0, 0, 1, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, -1, 0, 0, 1};
	    } else if (conn == 26) {
	        nbrhdOffsets = new int[]{-1, 0, 0, 1, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, -1, 0, 0, 1, -1, 0, 0, 1, 0, 0, -1, 0, 0, 1};
	    }
	    for (int k = 0; k < dim[2]; k++) {
	        for (int j = 0; j < dim[1]; j++) {
	            for (int i = 0; i < dim[0]; i++) {
	                if (BW[i][j][k] && L[i][j][k] == 0) {
	                    label++;
	                    L[i][j][k] = label;
	                    queue.add(i);
	                    queue.add(j);
	                    queue.add(k);
	                    while (!queue.isEmpty()) {
	                        int x = queue.remove(0);
	                        int y = queue.remove(0);
	                        int z = queue.remove(0);
	                        for (int n = 0; n < conn; n++) {
	                            int xn = x + nbrhdOffsets[n];
	                            int yn = y + nbrhdOffsets[n + 1];
	                            int zn = z + nbrhdOffsets[n + 2];
	                            if (xn >= 0 && xn < dim[0] && yn >= 0 && yn < dim[1] && zn >= 0 && zn < dim[2] && BW[xn][yn][zn] && L[xn][yn][zn] == 0) {
	                                L[xn][yn][zn] = label;
	                                queue.add(xn);
	                                queue.add(yn);
	                                queue.add(zn);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	    return L;	    
	}
	
	/**
	 * Performs erosion on a binary image using a structuring element.
	 *
	 * @param curMap2D The input binary image.
	 * @param se       The structuring element.
	 * @return The eroded image.
	 */
	public static boolean[][] erodeImage(boolean[][] curMap2D) {
	    int height = curMap2D.length;
	    int width = curMap2D[0].length;

	    boolean[][] erodeMap = new boolean[height][width];

	    // Iterate over the pixels of the input image
	    for (int x = 0; x < height; x ++ ) {
	    	for (int y = 0; y < width; y ++) {
	    		if (!curMap2D[x][y]) {
	    			erodeMap[x][y] = true;
	    			if (x + 1 < height) erodeMap[x + 1][y] = true;
	    			if (x - 1 >= 0) erodeMap[x - 1][y] = true;
	    			if (y + 1 < width) erodeMap[x][y + 1] = true;
	    			if (y - 1 >= 0) erodeMap[x][y - 1] = true;
	    		}
	    	}
	    }
	    for (int x = 0; x < height; x ++ ) {
	    	for (int y = 0; y < width; y ++) {
	    		erodeMap[x][y] = !erodeMap[x][y];
	    	}
	    }
	    return erodeMap;
	}
	
	/**
	 * Resizes a 3D float matrix by a given scale.
	 *
	 * @param dat   The original matrix to be resized.
	 * @param scale The scaling factor for resizing the matrix.
	 * @return The resized matrix.
	 */
	public static float[][][] imResize(float[][][] dat, int scale) {
	    // Determine the new dimensions of the resized matrix
	    int H = dat.length;
	    int W = dat[0].length;
	    int T = dat[0][0].length;
	    int H0 = (int) Math.ceil(H / (float)scale);
	    int W0 = (int) Math.ceil(W / (float)scale);
	    
	    // Create a new 3D float matrix to store the resized matrix
	    float[][][] resizedMatrix = new float[H0][W0][T];
	    int[][] cntMap = new int[H0][W0];
	    
	    int x0, y0, x1, y1;
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		x0 = Math.floorDiv(x, scale);
	    		y0 = Math.floorDiv(y, scale);
	    		cntMap[x0][y0]++; 
	    	}
	    }

	    // downsample
	    for (int t = 0; t < T; t++) {
	    	for (int x = 0; x < H0; x++) {
	    		x0 = x * scale; 
	    		x1 = Math.min((x + 1) * scale, H);
	    		for (int y = 0; y < W0; y++) {
	    			y0 = y * scale;
	    			y1 = Math.min((y + 1) * scale, W);
	    			
	    			float sumUp = 0;
	    			for (int i = x0; i < x1; i++) {
	    				for (int j = y0; j < y1; j++) {
	    					sumUp += dat[i][j][t];
	    				}
	    			}
	    			resizedMatrix[x][y][t] = sumUp / cntMap[x][y]; 
	    		}
	    	}
	    }

	    // Return the resized matrix
	    return resizedMatrix;
	}
	
	/**
	 * Resizes a 3D float matrix by a given scale.
	 *
	 * @param dat   The original matrix to be resized.
	 * @param scale The scaling factor for resizing the matrix.
	 * @return The resized matrix.
	 */
	public static boolean[][][] imResizeAcitveMap(int[][][] dat, int scale) {
	    // Determine the new dimensions of the resized matrix
	    int H = dat.length;
	    int W = dat[0].length;
	    int T = dat[0][0].length;
	    int H0 = (int) Math.ceil(H / (float)scale);
	    int W0 = (int) Math.ceil(W / (float)scale);
	    
	    // Create a new 3D float matrix to store the resized matrix
	    boolean[][][] resizedMatrix = new boolean[H0][W0][T];
	    
	    int x0, y0, x1, y1;
//	    int cnt = 0;
	    // downsample
	    for (int t = 0; t < T; t++) {
	    	for (int x = 0; x < H0; x++) {
	    		x0 = x * scale; 
	    		x1 = Math.min((x + 1) * scale, H);
	    		for (int y = 0; y < W0; y++) {
	    			y0 = y * scale;
	    			y1 = Math.min((y + 1) * scale, W);
	    			
	    			boolean valid = false;
	    			for (int i = x0; i < x1; i++) {
	    				for (int j = y0; j < y1; j++) {
	    					if (dat[i][j][t] > 0) {
	    						valid = true;
	    					}
	    				}
	    			}
	    			resizedMatrix[x][y][t] = valid; 
//	    			if (valid) cnt++;
	    		}
	    	}
	    }

//	    System.out.printf("valid pixels %d\n", cnt);
	    // Return the resized matrix
	    return resizedMatrix;
	}
	
	
	/**
	 * Resizes a 3D float matrix by a given scale.
	 *
	 * @param dat   The original matrix to be resized.
	 * @param scale The scaling factor for resizing the matrix.
	 * @return The resized matrix.
	 */
	public static float[][] imResize(float[][] dat, int scale) {
	    // Determine the new dimensions of the resized matrix
	    int H = dat.length;
	    int W = dat[0].length;
	    int H0 = (int) Math.ceil(H / (float)scale);
	    int W0 = (int) Math.ceil(W / (float)scale);
	    
	    // Create a new 3D float matrix to store the resized matrix
	    float[][] resizedMatrix = new float[H0][W0];
	    int[][] cntMap = new int[H0][W0];
	    
	    int x0, y0, x1, y1;
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		x0 = Math.floorDiv(x, scale);
	    		y0 = Math.floorDiv(y, scale);
	    		cntMap[x0][y0]++; 
	    	}
	    }

	    // downsample
    	for (int x = 0; x < H0; x++) {
    		x0 = x * scale; 
    		x1 = Math.min((x + 1) * scale, H);
    		for (int y = 0; y < W0; y++) {
    			y0 = y * scale;
    			y1 = Math.min((y + 1) * scale, W);
    			
    			float sumUp = 0;
    			for (int i = x0; i < x1; i++) {
    				for (int j = y0; j < y1; j++) {
    					sumUp += dat[i][j];
    				}
    			}
    			resizedMatrix[x][y] = sumUp / cntMap[x][y]; 
    		}
    	}
	    

	    // Return the resized matrix
	    return resizedMatrix;
	}
	
	public static int[][][] refineWaterShed(int[][][] map){
		int H = map.length;
		int W = map[0].length;
		int T = map[0][0].length;
		int[][] dirs = dirGenerate(26);
		int x0, y0, z0;
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int z = 0; z < T; z++) {
					if (map[x][y][z] == 0) {
						int nLabel = 0;
						for (int k = 0; k < 26; k++) {
							x0 = x + dirs[k][0];
							y0 = y + dirs[k][1];
							z0 = z + dirs[k][2];
							if (x0 >= 0 && x0 < H && y0 >= 0 && y0 < W && z0 >= 0 && z0 < T && map[x0][y0][z0] > 0) {
								 if (nLabel == 0)
									 nLabel = map[x0][y0][z0];
								 else if(nLabel != map[x0][y0][z0]){
									 nLabel = -1;
									 break;
								 }	
							}
						}
						if (nLabel > 0) {
							map[x][y][z] = nLabel;
//							System.out.println("Used");
						}	
					}
				}
			}
		}
		
		return map;
	}
	
	 public static int[][] convertMaskToShifts(boolean[][][] mask) {
        // retrieve mask size
        int sizeZ = mask.length;
        int sizeY = mask[0].length;
        int sizeX = mask[0][0].length;
        
        // compute offsets, using automated rounding of division
        int offsetX = (sizeX - 1) / 2;
        int offsetY = (sizeY - 1) / 2;
        int offsetZ = (sizeZ - 1) / 2;

        // count the number of positive elements
        int n = 0;
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (mask[z][y][x])
                        n++;
                }
            }
        }

        // allocate result
        int[][] offsets = new int[n][3];
        
        // fill up result array with positive elements
        int i = 0;
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (mask[z][y][x])
                    {
                        offsets[i][0] = x - offsetX;
                        offsets[i][1] = y - offsetY;
                        offsets[i][2] = z - offsetZ;
                        i++;
                    }
                }
            }
        }
        return offsets;
    }
	 
	 public static int[][] convertMaskToShifts(boolean[][] mask) {
	        // retrieve mask size
	        int sizeZ = mask.length;
	        int sizeY = mask[0].length;
	        
	        // compute offsets, using automated rounding of division
	        int offsetY = (sizeY - 1) / 2;
	        int offsetZ = (sizeZ - 1) / 2;

	        // count the number of positive elements
	        int n = 0;
	        for (int z = 0; z < sizeZ; z++)
	        {
	            for (int y = 0; y < sizeY; y++)
	            {
                    if (mask[z][y])
                        n++;
	            }
	        }

	        // allocate result
	        int[][] offsets = new int[n][2];
	        
	        // fill up result array with positive elements
	        int i = 0;
	        for (int z = 0; z < sizeZ; z++)
	        {
	            for (int y = 0; y < sizeY; y++)
	            {
                    if (mask[z][y])
                    {
                        offsets[i][0] = y - offsetY;
                        offsets[i][1] = z - offsetZ;
                        i++;
                    }
	            }
	        }
	        return offsets;
	    }
	 
	public static boolean[][][] erodeSlow(boolean[][][] mask, int[][] offsets){
		int H = mask.length;
		int W = mask[0].length;
		int T = mask[0][0].length;
		boolean[][][] res = new boolean[H][W][T];
		
		int n_cpus = Prefs.getThreads();
		System.out.println("nCFU: " + n_cpus);
		int cnt = 0;
		int cntPos = 0;
		int x0, y0, z0;
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int z = 0; z < T; z++) {
					if (mask[x][y][z]) {
						cnt += 1;
						boolean setFalse = false;
//						System.out.println(x + " " + y + " " + z);
						for (int k = 0; k < offsets.length; k++) {
							x0 = x + offsets[k][0];
							y0 = y + offsets[k][1];
							z0 = z + offsets[k][2];
							if (x0 < 0 || x0 >= H || y0 < 0 || y0 >= W || z0 < 0 || z0 >= T)
								continue;
							if (mask[x0][y0][z0] == false) {
								setFalse = true;
								break;
							}
						}
						if (setFalse)
							res[x][y][z] = false;
						else {
							res[x][y][z] = true;
							cntPos += 1;
						}
					}
				}
			}
		}
		System.out.println(" cnt: " + cnt + " cntPos: " + cntPos);
		return res;
	}
	
	public static boolean[][][] erode(boolean[][][] mask, int[][] offsets){
		int H = mask.length;
		int W = mask[0].length;
		int T = mask[0][0].length;
		boolean[][][] res = new boolean[H][W][T];
		
		final AtomicInteger ai = new AtomicInteger(0);
		final int n_cpus = Prefs.getThreads();
		ArrayList<int[]> pointsLst = new ArrayList<>();
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int z = 0; z < T; z++) {
					if (mask[x][y][z])
						pointsLst.add(new int[] {x, y, z});
				}
			}
		}
//		System.out.println("Erosion start");
		
		final int nPix = pointsLst.size();
//		System.out.println("nCFU: " + n_cpus + " nPix " + nPix);
		final int dec = (int) Math.ceil((double) nPix / (double) n_cpus);
		boolean[] setFalses = new boolean[nPix];
		
		Thread[] threads = ThreadUtil.createThreadArray( n_cpus );
		for (int iThread = 0; iThread < threads.length; iThread++) {
			threads[iThread] = new Thread() {
				public void run() {
					for (int iTh = ai.getAndIncrement(); iTh < n_cpus; iTh = ai.getAndIncrement()) {
						int idMin = dec * iTh;
						int idMax = idMin + dec;
						if (idMax > nPix)
							idMax = nPix;
						for (int i = idMin; i < idMax; i++) {
							final int x = pointsLst.get(i)[0];
							final int y = pointsLst.get(i)[1];
							final int z = pointsLst.get(i)[2];
							boolean setFalse = false;
							for (int k = 0; k < offsets.length; k++) {
								final int x0 = x + offsets[k][0];
								final int y0 = y + offsets[k][1];
								final int z0 = z + offsets[k][2];
								if (x0 < 0 || x0 >= H || y0 < 0 || y0 >= W || z0 < 0 || z0 >= T)
									continue;
								if (mask[x0][y0][z0] == false) {
									setFalse = true;
									break;
								}
							}
							setFalses[i] = setFalse;
						}
					}
				}
			};
		}
		ThreadUtil.startAndJoin(threads);
		int[] p;
		int cntPos = 0;
		for (int i = 0; i < nPix; i++) {
			p =  pointsLst.get(i);
			res[p[0]][p[1]][p[2]] = !setFalses[i];
			if (res[p[0]][p[1]][p[2]])
				cntPos += 1;
		}
		
//		System.out.println("Erosion done cntPos " + cntPos);
		return res;
	}
	
	
	public static boolean[][] imResize(boolean[][] dat, int scale) {
	    // Determine the new dimensions of the resized matrix
	    int H = dat.length;
	    int W = dat[0].length;
	    int H0 = (int) Math.ceil(H / (float)scale);
	    int W0 = (int) Math.ceil(W / (float)scale);
	    
	    // Create a new 3D float matrix to store the resized matrix
	    boolean[][] resizedMatrix = new boolean[H0][W0];
	    int[][] cntMap = new int[H0][W0];
	    
	    int x0, y0, x1, y1;
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		x0 = Math.floorDiv(x, scale);
	    		y0 = Math.floorDiv(y, scale);
	    		cntMap[x0][y0]++; 
	    	}
	    }

	    // downsample
    	for (int x = 0; x < H0; x++) {
    		x0 = x * scale; 
    		x1 = Math.min((x + 1) * scale, H);
    		for (int y = 0; y < W0; y++) {
    			y0 = y * scale;
    			y1 = Math.min((y + 1) * scale, W);
    			
    			boolean sumUp = false;
    			for (int i = x0; i < x1; i++) {
    				for (int j = y0; j < y1; j++) {
    					sumUp |= dat[i][j];
    				}
    			}
    			resizedMatrix[x][y] = sumUp; 
    		}
    	}
	    

	    // Return the resized matrix
	    return resizedMatrix;
	}
	
	
    
    /**
     * Performs element-wise multiplication of matrix a with a scalar value n, and then element-wise division by matrix b.
     *
     * @param a The first input matrix.
     * @param n The scalar value.
     * @param b The second input matrix.
     * @return The result of element-wise multiplication and division.
     */
    public static float[][] elementWiseMultiplyAndDivide(float[][] a, float n, float[][] b) {
        int numRows = a.length;
        int numCols = a[0].length;

        // Create a new matrix to store the result
        float[][] result = new float[numRows][numCols];

        // Perform element-wise multiplication and division
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = (a[i][j] * n) / b[i][j];
            }
        }
        return result;
    }
    
    /**
     * Performs element-wise division between two 3D matrices and updates the first matrix.
     *
     * @param datDS The first matrix (to be updated).
     * @param fs The second matrix.
     */
    public static void elementWiseDivisionInPlace(float[][][] datDS, float[][][] fs) {
        int width = datDS.length;
        int height = datDS[0].length;
        int depth = datDS[0][0].length;

        // Perform element-wise division and update the first matrix
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < depth; k++) {
                    datDS[i][j][k] /= fs[i][j][k];
                }
            }
        }
    }

    /**
     * Performs element-wise multiplication of matrices a and b, and then divides the result by matrix c.
     *
     * @param a The first input matrix.
     * @param b The second input matrix.
     * @param c The third input matrix.
     * @return The result of element-wise multiplication and division.
     */
    public static float[][] elementWiseMultiplyAndDivide(float[][] a, float[][] b, float[][] c) {
        int numRows = a.length;
        int numCols = a[0].length;

        // Create a new matrix to store the result
        float[][] result = new float[numRows][numCols];

        // Perform element-wise multiplication and division
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = (a[i][j] * b[i][j]) / c[i][j];
            }
        }
        return result;
    }
    
    /**
     * Performs element-wise subtraction of 1 from each element in the given list 'list',
     * and multiplies each element by the specified 'multiplier'.
     *
     * @param list      The input list.
     * @param multiplier The value to multiply the subtracted element by.
     * @return A new list with each element decreased by 1 and multiplied by the 'multiplier'.
     */
    public static List<Integer> subtractOneAndMultiply(List<Integer> lst, int multiplier) {
        List<Integer> result = new ArrayList<>(); // Create a new list to store the result

        for (Integer element : lst) {
            result.add((element - 1) * multiplier); // Subtract 1 from each element, multiply by 'multiplier', and add to the result list
        }

        return result; // Return the new list
    }
    
    /**
     * Converts a List<Integer> into an int[] with the same length and identical elements.
     *
     * @param list The input List<Integer>.
     * @return An int[] with the same length and identical elements as the input list.
     */
    public static int[] convertListToArray1D(List<Integer> list) {
        int[] result = new int[list.size()]; // Create a new array with the same length as the list
        
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i); // Assign each element from the list to the corresponding position in the array
        }
        
        return result; // Return the new array
    }
    
    /**
     * Converts a List<List<Integer>> into an int[][].
     *
     * @param list The input list.
     * @return The converted 2-dimensional array.
     */
    public static int[][] convertListToArray2D(List<List<Integer>> list) {
        int numRows = list.size();
        int numCols = list.get(0).size();

        int[][] array = new int[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            List<Integer> innerList = list.get(i);
            for (int j = 0; j < numCols; j++) {
                array[i][j] = innerList.get(j);
            }
        }

        return array;
    }
    
    /**
     * Calculates the square root of each element in the matrix.
     *
     * @param x The input matrix.
     * @return The matrix with square root of each element.
     */
    public static float[][] calculateSquareRoot(float[][] x) {
        int numRows = x.length;
        int numCols = x[0].length;

        // Create a new matrix to store the result
        float[][] result = new float[numRows][numCols];

        // Calculate square root of each element
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = (float) Math.sqrt(x[i][j]);
            }
        }
        return result;
    }
    
    /**
     * Flatten a 2D list and convert it to a 1D List.
     *
     * @param matrix The 2D list to be flattened.
     * @return The resulting 1D List.
     */
    public static List<Integer> flatten2DList(List<List<Integer>> matrix) {
        // Flatten the 2D list and convert it to an array
        List<Integer> flattened = new ArrayList<>();
        for (List<Integer> row : matrix) {
            flattened.addAll(row);
        }
        
        return flattened;
    }
    
    /**
     * Compute the sizes of each list in curRegions.
     *
     * @param curRegions The array of lists to compute sizes for.
     * @return An array containing the sizes of each list.
     */
    public static int[] computeSizes(List<Integer>[] curRegions) {
        int[] sizes = new int[curRegions.length];

        // Compute the size of each list
        for (int i = 0; i < curRegions.length; i++) {
            sizes[i] = curRegions[i].size();
        }

        return sizes;
    }
    
    /**
     * Resize an array to the specified size.
     *
     * @param array The array to be resized.
     * @param size  The new size of the array.
     * @return The resized array.
     */
    public static <T> T[] resizeArray(T[] array, int size) {
        T[] resizedArray = (T[]) new Object[size];
        System.arraycopy(array, 0, resizedArray, 0, size);
        return resizedArray;
    }
    
    /**
     * Get the maximum value in a list of integers.
     *
     * @param list The list of integers.
     * @return The maximum value in the list.
     */
    public static int getMax(List<Integer> list) {
        int max = Integer.MIN_VALUE;
        for (int num : list) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    /**
     * Get the minimum value in a list of integers.
     *
     * @param list The list of integers.
     * @return The minimum value in the list.
     */
    public static int getMin(List<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (int num : list) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }
    
    /**
     * Generates a sequence of consecutive numbers from scalar1 to scalar2 (inclusive).
     *
     * @param scalar1 The starting value of the sequence.
     * @param scalar2 The ending value of the sequence.
     * @return A List containing the generated sequence.
     */
    public static List<Integer> generateSequence(int scalar1, int scalar2) {
        List<Integer> sequence = new ArrayList<>();

        // Generate the sequence from scalar1 to scalar2 (inclusive)
        for (int i = scalar1; i <= scalar2; i++) {
            sequence.add(i);
        }

        return sequence;
    }
    
    /**
     * Finds the set difference between the elements of the input matrix and the scalar value 0.
     * It returns an array containing the elements from the matrix that are not equal to 0.
     *
     * @param inputMatrix The input matrix.
     * @return An array containing the non-zero elements of the input matrix.
     */
    public static int[] findNonZeroElements(int[][] mat) {
        List<Integer> result = new ArrayList<>(); // Create a list to store the non-zero elements
        
        for (int[] row : mat) {
            for (int element : row) {
                if (element != 0) {
                    result.add(element); // Add non-zero element to the list
                }
            }
        }
        
        int[] arr = new int[result.size()]; // Create an array to store the result
        
        for (int i = 0; i < result.size(); i++) {
            arr[i] = result.get(i); // Convert list to array
        }
        
        return arr; // Return the array containing non-zero elements
    }
    
    /**
     * Checks if an array contains a specific element.
     *
     * @param array   The input array.
     * @param element The element to be checked.
     * @return True if the array contains the element, false otherwise.
     */
    private static boolean contains(int[] array, int element) {
        for (int value : array) {
            if (value == element) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Performs element-wise addition of two 2-dimensional matrices.
     *
     * @param mat1 The first input matrix.
     * @param mat2 The second input matrix.
     * @return A new matrix obtained by adding the corresponding elements of mat1 and mat2.
     */
    public static int[][] matrixAddition(int[][] mat1, int[][] mat2) {
        int rows = mat1.length;
        int cols = mat1[0].length;
        int[][] result = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = mat1[i][j] + mat2[i][j];
            }
        }

        return result;
    }
    
    /**
     * Selects elements from a 2D matrix based on the given selection matrix.
     *
     * @param mat    The input matrix.
     * @param select The selection matrix.
     * @return A list of selected elements.
     */
    public static List<Integer> selectElements(int[][] mat, int[][] select) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (select[i][j] != 0) {
                    result.add(mat[i][j]);
                }
            }
        }

        return result;
    }    
    
    /**
     * Checks if each element in the given 3D float matrix is greater than 0.
     *
     * @param mat The input 3D float matrix.
     * @return A 3D boolean matrix where each element represents if the corresponding element in 'mat' is greater than 0.
     */
    public static boolean[][][] checkPositiveElements(float[][][] mat) {
        int dim1 = mat.length;
        int dim2 = mat[0].length;
        int dim3 = mat[0][0].length;

        boolean[][][] result = new boolean[dim1][dim2][dim3];

        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                for (int k = 0; k < dim3; k++) {
                    result[i][j][k] = mat[i][j][k] > 0;
                }
            }
        }

        return result;
    }
// end

    /**
     * Get average curve from data
     */
	public static float[] getAvgCurve(float[][][] dat, HashSet<Integer> ihw) {
		int H = dat.length;
		int W = dat[0].length;
		int T = dat[0][0].length;
		float[] avgCurve = new float[T];
		int n = ihw.size();
		int[] p;
		for (int index : ihw) {
			p = ind2sub(H, W, index);
			for (int t = 0; t < T; t++) {
				if (!Float.isNaN(dat[p[0]][p[1]][t]))
					avgCurve[t] += dat[p[0]][p[1]][t] / n;
			}
		}
		
		return avgCurve;
	}
	
	public static float[] getAvgCurve(float[][][] dat, ArrayList<int[]> ihw) {
		int T = dat[0][0].length;
		float[] avgCurve = new float[T];
		int n = ihw.size();
		int[] p;
		for (int i = 0; i < ihw.size(); i ++) {
			p = ihw.get(i);
			for (int t = 0; t < T; t++) {
				avgCurve[t] += dat[p[0]][p[1]][t] / n;
			}
		}
		
		return avgCurve;
	}

	public static float[] tempDownSample(float[] curve, int t_scl) {
		int T = curve.length;
		int T0 = (int) Math.ceil((float) T / t_scl);
		float[] curveDS = new float[T0];
		int t0, t1, count;
		for (int i = 0; i < T0; i++) {
			t0 = i * t_scl;
			t1 = Math.min(T, (i + 1) * t_scl);
			count = t1 - t0;
			for (int t = t0; t < t1; t++) {
				curveDS[i] += curve[t] / count;
			}
		}
		return curveDS;
	}

	public static HashMap<Integer, ArrayList<int[]>> label2idx(int[][][] map) {
		HashMap<Integer, ArrayList<int[]>> lst = new HashMap<Integer, ArrayList<int[]>>();
		int H = map.length;
		int W = map[0].length;
		int T = map[0][0].length;
		int label;
		for (int t = 0; t < T; t++) {
			for (int y = 0; y < W; y++) {
				for (int x = 0; x < H; x++) {
					label = map[x][y][t];
					if (label > 0) {
						ArrayList<int[]> l = lst.get(label);
						if(l == null)
							l = new ArrayList<>();
							lst.put(label, l);
						l.add(new int[] {x, y, t});
					}
				}
			}
		}
		return lst;
	}
	
	public static HashMap<Integer, ArrayList<int[]>> label2idx(int[][] map) {
		HashMap<Integer, ArrayList<int[]>> lst = new HashMap<Integer, ArrayList<int[]>>();
		int H = map.length;
		int W = map[0].length;
		int label;
		for (int y = 0; y < W; y++) {
			for (int x = 0; x < H; x++) {
				label = map[x][y];
				if (label > 0) {
					ArrayList<int[]> l = lst.get(label);
					if(l == null)
						l = new ArrayList<>();
						lst.put(label, l);
					l.add(new int[] {x, y});
				}
			}
		}
		return lst;
	}

	public static float estimateNoiseByMedian(float[] curve) {
		int T = curve.length;
		float[] tmp = new float[T - 1];
		float dif;
		for (int i = 0; i < T - 1; i++) {
			dif = curve[i + 1] - curve[i];
			tmp[i] = dif * dif;
		}
		Arrays.sort(tmp);
		
		float median;
		if ((T - 1) % 2 == 0) {
			median = (tmp[(T - 1) / 2 - 1] + tmp[(T - 1) / 2]) / 2;
		}else {
			median = tmp[(T - 2) / 2];
		}
				
		return (float) Math.sqrt(median / 0.9099);
	}
	
	public static float getMedian(ArrayList<Double> tmp) {
		int T = tmp.size();
		Collections.sort(tmp);
		float median;
		if (T % 2 == 0) {
			median = (float) ((tmp.get(T/2 - 1) + tmp.get(T/2)) / 2);
		}else {
			median = (float) (tmp.get((T - 1) / 2) + 0);
		}
				
		return median;
	}
	
	public static float estimateNoiseByMean(float[] curve) {
		int T = curve.length;
		float dif;
		double sum = 0;
		for (int i = 0; i < T - 1; i++) {
			dif = curve[i + 1] - curve[i];
			sum += dif * dif;
		}
		sum /= (T - 1);
		return (float) Math.sqrt(sum / 2);

	}

	public static float[][][] convertImgPlusToArray(ImagePlus img) {
		int dim1 = img.getWidth();
		int dim2 = img.getHeight();
		int dim3 = img.getImageStackSize();
		float[][][] res = new float[dim1][dim2][dim3];
		for (int z = 0; z < dim3; z++) {
			img.setPosition(z + 1);
			float[][] tmp = img.getProcessor().getFloatArray();
			setSlice(res, tmp, z);
		}
		return res;
	}
	
	public static int[][][] convertImgPlusToIntArray(ImagePlus img) {
		int dim1 = img.getWidth();
		int dim2 = img.getHeight();
		int dim3 = img.getImageStackSize();
		int[][][] res = new int[dim1][dim2][dim3];
		for (int z = 0; z < dim3; z++) {
			img.setPosition(z + 1);
			float[][] tmp = img.getProcessor().getFloatArray();
			setSlice(res, tmp, z);
		}
		return res;
	}
	
	public static float[][][] convertImgPlusToArray(ImageStack img) {
		int dim1 = img.getWidth();
		int dim2 = img.getHeight();
		int dim3 = img.getSize();
		float[][][] res = new float[dim1][dim2][dim3];
		for (int z = 0; z < dim3; z++) {
			for (int x = 0; x < dim1; x++) {
				for (int y = 0; y < dim2; y++) {
					res[x][y][z] = (float) img.getVoxel(x, y, z);
				}
			}
		}
		return res;
	}
	
	public static float[][][] imimposemin(float[][][] I, boolean[][][] BW){
		int dim1 = I.length;
		int dim2 = I[0].length;
		int dim3 = I[0][0].length;
		
		float[][][] fm = new float[dim1][dim2][dim3];
		float maxV = Float.NEGATIVE_INFINITY;
		float minV = Float.POSITIVE_INFINITY;
		for (int x = 0; x < dim1; x++) {
			for (int y = 0; y < dim2; y++) {
				for (int z = 0; z < dim3; z++) {
					if (BW[x][y][z])
						fm[x][y][z] = Float.NEGATIVE_INFINITY;
					else
						fm[x][y][z] = Float.POSITIVE_INFINITY;
					maxV = Math.max(maxV, I[x][y][z]);
					minV = Math.min(minV, I[x][y][z]);
				}
			}
		}
		
		float h = 1;
		if (maxV == minV)
			h = 0.1f;
		else
			h = 0.001f * (maxV - minV);
		
		float[][][] mask = new float[dim1][dim2][dim3];
		for (int x = 0; x < dim1; x++) {
			for (int y = 0; y < dim2; y++) {
				for (int z = 0; z < dim3; z++) {
					mask[x][y][z] = 1 - Math.min(I[x][y][z] + h, fm[x][y][z]);
					fm[x][y][z] = - fm[x][y][z];
				}
			}
		}
		
		float[][][] res = Helper.convertImgPlusToArray(Reconstruction3D.reconstructByDilation(Helper.convertToImgStk(fm),Helper. convertToImgStk(mask), 26));
		for (int x = 0; x < dim1; x++) {
			for (int y = 0; y < dim2; y++) {
				for (int z = 0; z < dim3; z++) {
					res[x][y][z] = 1 - res[x][y][z];
				}
			}
		}
		return res;
		
	}
	
	public static int[][] dirGenerate(int conn) {
		int [][] dirs = null;
		int cnt = 0;
		if (conn == 26) {
			dirs = new int[26][3];
			
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if (x == 0 && y == 0 && z == 0) {
							
						}else {
							dirs[cnt][0] = x;
							dirs[cnt][1] = y;
							dirs[cnt][2] = z;
							cnt ++;
						}
					}
				}
			}
		}else if(conn == 8) {
			dirs = new int[8][2];
			
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					if (x == 0 && y == 0) {
						
					}else {
						dirs[cnt][0] = x;
						dirs[cnt][1] = y;
						cnt ++;
					}
				}
			}
		}
		
		
		return dirs;		
	}

	public static HashMap<Integer, ArrayList<int[]>> filterWithMask(HashMap<Integer, ArrayList<int[]>> sdLst,
			boolean[] nonEmpty) {
		HashMap<Integer, ArrayList<int[]>> res = new HashMap<Integer, ArrayList<int[]>>();
		int cnt = 1;
		for (int i = 1; i <= sdLst.size(); i++) {
			if (nonEmpty[i - 1]) {
				res.put(cnt, sdLst.get(i));
				cnt ++;
			}
			
		}
		return res;
	}
	
	public static HashMap<Integer, Step3MajorityResult> filterWithMaskMajor(HashMap<Integer, Step3MajorityResult> sdLst,
			boolean[] nonEmpty) {
		HashMap<Integer, Step3MajorityResult> res = new HashMap<Integer, Step3MajorityResult>();
		int cnt = 1;
		for (int i = 1; i <= sdLst.size(); i++) {
			if (nonEmpty[i - 1]) {
				res.put(cnt, sdLst.get(i));
				cnt ++;
			}
			
		}
		return res;
	}
	
	
	public static void normalizeCurve(float[] curve) {
		float minV = Float.POSITIVE_INFINITY;
		float maxV = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < curve.length; i ++) {
			minV = Math.min(minV, curve[i]);
			maxV = Math.max(maxV, curve[i]);
		}
		for (int i = 0; i < curve.length; i++) {
			curve[i] = (curve[i] - minV) / (maxV - minV);
		}
		return;
	}
	
	public static void normalizeCurveByNoise(float[] curve, float sigma) {
		for (int i = 0; i < curve.length; i++) {
			curve[i] /= sigma;
		}
		return;
	}
	
	public static HashSet<Integer> getUniqueSpa(ArrayList<int[]> pix, int H, int W) {
		HashSet<Integer> ihw = new HashSet<Integer>();
		int[] p;
		for (int pId = 0; pId < pix.size(); pId++) {
			p = pix.get(pId);
			ihw.add(Helper.sub2ind(H, W, p[0], p[1]));
		}
		return ihw;
	}

	public static float corr(float[] xs, float[] ys) {
		double mux = 0;
		double muy = 0;
		double sxx = 0;
		double syy = 0;
		double sxy = 0;
		int n = xs.length;
		
		double x, y;
		for (int i = 0; i < n; i++) {
			x = xs[i];
			y = ys[i];
			mux += x;
			muy += y;
			sxx += x * x;
			syy += y * y;
			sxy += x * y;
		}
		mux /= n;
		muy /= n;
		sxx /= n;
		syy /= n;
		sxy /= n;
		double res = (sxy - mux * muy) / Math.sqrt((sxx - mux * mux) * (syy - muy * muy));
		
		return (float) res;
	}

	public static void setValue(int[][][] Map, ArrayList<int[]> pix, int curLabel) {
		int[] p;
		for (int pId = 0; pId < pix.size(); pId++) {
			p = pix.get(pId);
			Map[p[0]][p[1]][p[2]] = curLabel;
		}
	}
	
	public static void setValue(boolean[][][] Map, ArrayList<int[]> pix) {
		int[] p;
		for (int pId = 0; pId < pix.size(); pId++) {
			p = pix.get(pId);
			Map[p[0]][p[1]][p[2]] = true;
		}
	}
	
	public static void setValue(boolean[][] Map, ArrayList<int[]> pix) {
		int[] p;
		for (int pId = 0; pId < pix.size(); pId++) {
			p = pix.get(pId);
			Map[p[0]][p[1]] = true;
		}
	}
	
	public static void setValue(int[][] Map, ArrayList<int[]> pix, int curLabel) {
		int[] p;
		for (int pId = 0; pId < pix.size(); pId++) {
			p = pix.get(pId);
			Map[p[0]][p[1]] = curLabel;
		}
	}

	public static float[] fill0(float[] curve, int l, int r) {
		float[] res = new float[l + r + curve.length];
		
		for (int t = 0; t < curve.length; t++) {
			res[t + l] = curve[t];
		}
		
		
		return res;
	}
	
	public static int[][][] loadMatlabStep2Result() {
		ImagePlus imgPlus = new ImagePlus("D:\\Test\\Map.tiff");
		int W = imgPlus.getWidth();
		int H = imgPlus.getHeight();
		System.out.println("H: " + H + " W: " + W);
		int T = imgPlus.getImageStackSize();
		int[][][] Map = new int[H][W][T];
		ImageStack stk = imgPlus.getStack().convertToFloat();
		imgPlus.setStack(stk);
		ImageProcessor imgProcessor = imgPlus.getProcessor();
		for(int k = 1;k <= T;k++) {
			imgPlus.setPosition(k);
			float[][] f = imgProcessor.getFloatArray();
			for(int i = 0;i < W;i++) {
				for(int j=0;j < H;j++) {
					Map[j][i][k - 1] = (int) f[i][j];
				}
			}
		}		
		
		return Map;
	}

	public static ArrayList<int[]> dtw(float[] s, float[] t) {
		ArrayList<int[]> xy = new ArrayList<int[]>();
		int m = s.length;
		int n = t.length;
		float[][] cost = new float[m][n];
		int[][] dirs = new int[m][n];
		float dist;
		cost[0][0] = Math.abs(s[0] - t[0]);
		dirs[0][0] = -1;
		for (int x = 1; x < m; x++) {
			cost[x][0] = cost[x - 1][0] + Math.abs(s[x] - t[0]);
			dirs[x][0] = 1;
		}
		for (int y = 1; y < n; y++) {
			cost[0][y] = cost[0][y - 1] + Math.abs(s[0] - t[y]);
			dirs[0][y] = 2;
		}
		for (int x = 1; x < m; x++) {
			for (int y = 1; y < n; y++) {
				dist = Math.abs(s[x] - t[y]);
				if (cost[x - 1][y - 1] <= Math.min(cost[x - 1][y], cost[x][y - 1])) {
					dirs[x][y] = 3;
					cost[x][y] = cost[x - 1][y - 1] + dist;
				}else if (cost[x - 1][y] <= Math.min(cost[x - 1][y - 1], cost[x][y - 1])) {
					dirs[x][y] = 1;
					cost[x][y] = cost[x - 1][y] + dist;
				}else { // if (cost[x][y - 1] < Math.min(cost[x - 1][y - 1], cost[x - 1][y])) {
					dirs[x][y] = 2;
					cost[x][y] = cost[x][y - 1] + dist;
				}
			}
		}
		int x = m - 1;
		int y = n - 1;
		xy.add(new int[] {x, y});
//		System.out.printf("x: %d, y: %d\n", x+1, y+1);
		while (dirs[x][y] > 0) {
			if (dirs[x][y] == 1){
				x -= 1;
			}else if (dirs[x][y] == 2) {
				y -= 1;
			}else {
				x -= 1; y -= 1;
			}
			xy.add(new int[] {x, y});
//			System.out.printf("x: %d, y: %d\n", x+1, y+1);
		}
//		System.out.println(cost[m - 1][n - 1]);
		return xy;
	}

	public static boolean[][][] AndOperation(boolean[][][] mask, boolean[][][] mask2) {
		int H = mask.length;
		int W = mask[0].length;
		int T = mask[0][0].length;
		boolean[][][] res = new boolean[H][W][T];
		for (int x = 0; x < H; x ++) {
			for (int y = 0; y < W; y ++) {
				for (int z = 0; z < T; z ++) {
					res[x][y][z] = mask[x][y][z] & mask2[x][y][z];
				}
			}
		}
		
		return res;
	}

	public static void scaleCurve(float[] curve, double scale) {
		for (int t = 0; t < curve.length; t++) {
			curve[t] *= scale;
		}
		
	}

	public static ArrayList<int[]> convertSet2Array(HashSet<Integer> ihw, int H0, int W0) {
		// TODO Auto-generated method stub
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (int index : ihw) {
			res.add(ind2sub(H0, W0, index));
		}
		return res;
	}

	public static Pointer[] jna2DArray(float[][] data) {
		int m = data.length;
		int n = data[0].length;
		
		Pointer[] p = new Pointer[m];
		for (int i = 0; i < m; i ++) {
			p[i] = new Memory(n * Native.getNativeSize(Float.TYPE));
			p[i].write(0, data[i], 0, n);
		}
		return p;
	}
	
	public static Pointer[] jna2DArray(int[][] data) {
		int m = data.length;
		int n = data[0].length;
		
		Pointer[] p = new Pointer[m];
		for (int i = 0; i < m; i ++) {
			p[i] = new Memory(n * Native.getNativeSize(Integer.TYPE));
			p[i].write(0, data[i], 0, n);
		}
		return p;
	}
	
	public static float[] DTW_Edge_input(float[][] data0) {
		int T = data0.length;
		
		Pointer[] p = jna2DArray(data0);
		Pointer p2 = MyDll.mydll.DTW_Edge_input(p, T, T);
		float[] tempCut = p2.getFloatArray(0, T - 1);
		return tempCut;
	}
	
	
	public static float[][] BILCO(float[][] ref, float[][] tst, int[][] Gij, float smo, float[][] initialCut) {
		int N = ref.length;
		int T = ref[0].length;
		int nPair = Gij.length;	
		
		Pointer[] pIniCut = Helper.jna2DArray(initialCut);
		Pointer[] pRef = Helper.jna2DArray(ref);
		Pointer[] pTst = Helper.jna2DArray(tst);
		Pointer[] pGijTemp = Helper.jna2DArray(Gij);
		float[][] minCut = new float[N][T - 1];
		Pointer[] pMinCut = Helper.jna2DArray(minCut);
		MyDll2.mydll.runBILCO(N, T, T, nPair, smo, pIniCut, pRef, pTst, pGijTemp, pMinCut);
//		MyDll2.mydll.runBILCO(N, T, T, nPair, smo, pIniCut, pRef, pTst, pGijTemp);
//		float[][] minCut = new float[N][T - 1];
		for (int i = 0; i < N; i++) {
			minCut[i] = pMinCut[i].getFloatArray(0, T - 1);
		}
		
		return minCut;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> twoPassConnect2D_ForBuilder(boolean[][] input) {		
		int width = input.length;
		int height = input[0].length;
		int changeParameter = Math.max(width, height);
		
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=0;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<Integer> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(i*changeParameter + j);
				}
			}
		}
		
		return map;
	}
	
	public static HashMap<Integer, ArrayList<Integer>> twoPassConnect2D_ForBuilder(boolean[][] input, int minSize) {		
		int width = input.length;
		int height = input[0].length;
		int changeParameter = Math.max(width, height);
		
		
		int[][] label = new int[width][height];
		ArrayList<Integer> list = new ArrayList<>();
		list.add(0);
		int curLabel = 1;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				if(input[i][j]) {
					int[] labels = new int[4];
					labels[0] = i>0 && j>0? label[i-1][j-1]:0;
					labels[1] = i>0? label[i-1][j]:0;
					labels[2] = i>0 && j<height-1? label[i-1][j+1]:0;
					labels[3] = j>0? label[i][j-1]:0;
					
					ArrayList<Integer> labelList = new ArrayList<>();
					int min = Integer.MAX_VALUE;
					for(int ii=0;ii<4;ii++) {
						if(labels[ii]!=0) {
							min = Math.min(min, labels[ii]);
							labelList.add(labels[ii]);
						}
					}
					if(labelList.size()==0) {
						label[i][j] = curLabel;
						list.add(0);
						curLabel++;
					}else {
						label[i][j] = min;
						for(int ii=0;ii<labelList.size();ii++) {
							union_connect(min,labelList.get(ii),list);
						}
					}
				}
			}
		}
		
		HashMap<Integer,Integer> rootMap = new HashMap<>();
		int cnt = 1;
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
		for(int j=0;j<height;j++) {
			for(int i=0;i<width;i++) {
			
				if(label[i][j]!=0) {
					int root = union_find(label[i][j], list);
					int value;
					if(rootMap.get(root)!=null) {
						value = rootMap.get(root);
					}else {
						value = cnt;
						rootMap.put(root, value);
						cnt++;
					}
					
					label[i][j] = value;
					ArrayList<Integer> l = map.get(value);
					if(l==null) {
						l= new ArrayList<>();
						map.put(value, l);
					}
					l.add(i*changeParameter + j);
				}
			}
		}
		
		cnt = 1;
		HashMap<Integer, ArrayList<Integer>> newMap = new HashMap<>();
		for(Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
			ArrayList<Integer> points = entry.getValue();
			if(points.size()>minSize) {
				newMap.put(cnt, points);
				cnt++;
			}else {
				for(int xy:points) {
					int x = xy/changeParameter;
					int y = xy%changeParameter;
					input[x][y] = false;
				}
			}
		}
		
		return newMap;
	}
	
	public static int union_find(int label, ArrayList<Integer> list){
		int i = label;
		
		while(list.get(i)!=0) {
			i = list.get(i);
		}
		if(i!=label)
			list.set(label, i);
		return i;
	}
	
	public static void union_connect(int label1, int label2, ArrayList<Integer> list) {
		if(label1==label2)
			return;
		int i = union_find(label1,list);
		int j = union_find(label2,list);
		if(i!=j)
			list.set(j, i);
	}

	public static String getOsExt() {
		// TODO Auto-generated method stub
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
	        return ".dll";
	    } else if (os.contains("nix") || os.contains("nux")) {
	    	return ".so";
	    } else if (os.contains("mac")){
	        return "";
	    } else {
	    	return "";
	    }
	}

	public static int[][][] watershed(float[][][] scoreMap) {
		int H = scoreMap.length;
		int W = scoreMap[0].length;
		int T = scoreMap[0][0].length;
		
		int[][][] mask0 = new int[H][W][T];
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < W; y++) {
				for (int z = 0; z < T; z++) {
					mask0[x][y][z] = 1;
				}
			}
		}
		ImagePlus input = Helper.convertToImgPlus(scoreMap);
		ImagePlus mask = Helper.convertToImgPlus(mask0);
		WatershedTransform3D watershed = new WatershedTransform3D (input, mask, 26);
		ImagePlus result = watershed.apply();
		int[][][] MapOut = Helper.convertImgPlusToIntArray(result);
		MapOut = Helper.refineWaterShed(MapOut);
		
		// TODO Auto-generated method stub
		return MapOut;
	}
	
	
}