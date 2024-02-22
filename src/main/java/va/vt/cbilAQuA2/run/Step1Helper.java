package va.vt.cbilAQuA2.run;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;

import va.vt.cbilAQuA2.Helper;
import va.vt.cbilAQuA2.ImageDealer;
import va.vt.cbilAQuA2.Opts;
import va.vt.cbilAQuA2.tools.GaussFilter;

public class Step1Helper {
	public static float[][][] baselineRemoveAndNoiseEstimation(float[][][] datOrg, boolean[][] evtSpatialMask, int ch, 
			String proPath, Opts opts) {
      float bias, maxdF;
      long startTime, stopTime, elapsedTime;
      float[][] stdMapOrg, stdMapSmo, tempVarOrg, correctPars;
      float[][][] datSmo, F0, dF;
      
      int H = datOrg.length;
      int W = datOrg[0].length;
      int T = datOrg[0][0].length;
      System.out.println("H: " + H + " W " + W + " T: " + T);
          
      // publish(1);       
      // save the original data
//      Helper.saveMatrix(proPath, "datOrg.ser", datOrg);
      
      // Calculate standard variance: Noise for smoothed data
      // publish(2);
      System.out.println("\npublish 2");  
      
      // smooth the data -- checked
      System.out.println("smooth the data"); 
      datSmo = new float[H][W][T];
     
      for (int tt = 0; tt < T; tt++) {
		    float[][] slice = Helper.getSlice(datOrg, tt);
		    float[][] filteredSlice;
		    if (opts.smoXY > 0) {
		    	filteredSlice = GaussFilter.gaussFilter(slice, opts.smoXY, opts.smoXY);
		    }else {
		    	filteredSlice = slice;
		    }
		    Helper.setSlice(datSmo, filteredSlice, tt); 
      }      
      // linear estimation of F0 - checked
      System.out.println("baselineLinearEstimate"); 
      opts.cut = Math.min(opts.cut, T);
      startTime = System.currentTimeMillis();
      F0 = baselineLinearEstimate(datSmo, opts.cut, opts.movAvgWin);
      stopTime = System.currentTimeMillis();
      elapsedTime = stopTime - startTime;
      System.out.println("Elapsed time: " + elapsedTime + "ms");      
      
      // get projection
      float[][] F0Pro = new float[H][W];
      for (int i = 0; i < H; i++) {
          for (int j = 0; j < W; j++) {
              float sum = 0;
              for (int k = 0; k < T; k++) {
                  sum += F0[i][j][k];
              }
              F0Pro[i][j] = sum / T;
          }
      }
//      Helper.viewMatrix(5, 5, "F0Pro", F0Pro);
      
      // noise estimation - piece wise linear function to model
      System.out.println("\npublish 3");
      startTime = System.currentTimeMillis();
      stdMapOrg = new float[H][W];
      stdMapSmo = new float[H][W];
      tempVarOrg = new float[H][W];
      correctPars = new float[H][W];
      noiseEstimation(F0Pro, datOrg, datSmo, opts.smoXY, evtSpatialMask, stdMapOrg, stdMapSmo, tempVarOrg, correctPars);
//      Helper.viewMatrix(10, 10, "stdMapOrg", stdMapOrg);
//      Helper.viewMatrix(10, 10, "stdMapSmo", stdMapSmo);
//      Helper.viewMatrix(10, 10, "tempVarOrg", tempVarOrg);
//      Helper.viewMatrix(10, 10, "correctPars", correctPars); 
      stopTime = System.currentTimeMillis();
      elapsedTime = stopTime - startTime;
      System.out.println("Elapsed time: " + elapsedTime + "ms");
      
      // correct bias during noise estimation. Bias does not impact noise
      // publish(4);
      System.out.println("\npublish 4");
      // Done !!! Now in jar, the configurations in resources can be read.
      bias = obtainBias(opts.movAvgWin, opts.cut);
      System.out.printf("bias % f", bias);
      
      dF = new float[H][W][T];
      maxdF = Float.NEGATIVE_INFINITY;
      for (int i = 0; i < H; i++) {
    	  for (int j = 0; j < W; j++) {
    		  for (int k = 0; k < T; k++) {
    			  F0[i][j][k] = F0[i][j][k] - bias * stdMapSmo[i][j];
    			  dF[i][j][k] = (datSmo[i][j][k] - F0[i][j][k]) / stdMapSmo[i][j];
    			  maxdF = Math.max(dF[i][j][k], maxdF);
    		  }
    	  }
      }

      opts.tempVarOrg1 = tempVarOrg;
      opts.correctPars1 = correctPars;

      if (ch==1) {
        opts.stdMap1 = stdMapSmo;
        opts.stdMapOrg1 = stdMapOrg;
        opts.tempVarOrg1 = tempVarOrg;
        opts.correctPars1 = correctPars;
        opts.maxdF1 = maxdF;
      } 
      
//      Helper.viewMatrix(10, 10, 3, "dF", dF);
      
      // publish(5)
      System.out.println("\npublish 5");
      // save the computed data
      Helper.saveMatrix(proPath, "dF.ser", dF);
      
      return dF;
	}
		
	public static float[][][] baselineLinearEstimate(float[][][] dat, int cut, int movAvgWin) {  
      int step, nSegment, t0, t1, preP, curP;
      float maxV, preV, curV, deltaV;
      
      float[][][] F0;
      int[][][] minPosition;
      int H = dat.length; // number of rows
      int W = dat[0].length; // number of columns
      int T = dat[0][0].length; // number of slices
      float[] curve = new float[T];
      // moving average - checked
      F0 = Helper.movingMean(dat, movAvgWin);
      maxV = Helper.fillNaN(dat, F0);
	  step = (int) Math.round(0.5 * cut);	  
	  nSegment = Math.max(1, (int) Math.ceil((float) T / step) - 1);
	  minPosition = new int[H][W][nSegment]; 
	  
	  for (int k = 0; k < nSegment; k++) {
        t0 = k * step;
        t1 = Math.min(T, t0 + cut + 1);
        
        for (int x = 0; x < H; x++) {
        	for (int y = 0; y < W; y++) {
        		float minV = Float.MAX_VALUE;
                int minP = -1;
                for (int t = t0; t < t1; t++) {
                	if (F0[x][y][t] < minV) {
                		minV = F0[x][y][t];
                		minP = t;
                	}
                }
                minPosition[x][y][k] = minP;
        	}
        }
      }
	  
	  for (int x = 0; x < H; x++) {
      	for (int y = 0; y < W; y++) {
      		int k = 0;
      		preP = 0;
      		curP = -1;
      		
//      		piecewise linear fill
      		while (k < nSegment) {
      			curP = minPosition[x][y][k];
      			if (curP >= 0) {
      				curV = F0[x][y][curP];
//      				first segment
      				if (k == 0) {
      					for (int t = 0; t < curP; t++) {
      						curve[t] = curV;
      					}
      				} else {
      					preV = F0[x][y][preP];
      					deltaV = (curV - preV) / (curP - preP);
      					for (int t = preP; t < curP; t++) {
      						curve[t] = preV + (t - preP) * deltaV;
      					}
      				}
      				preP = curP;
      			}
      			k++;
      		}
      		if (curP >= 0) {
      			preV = F0[x][y][preP];
      		}else {
      			preV = maxV;
      		}
//      		last segment
      		for (int t = preP; t < T; t++) {
				curve[t] = F0[x][y][preP];
			}
//      		update
      		for (int t = 0; t < T; t++) {
      			F0[x][y][t] = curve[t];
      		}
      		
      	}
	  }
	  return F0;
	}
	
	/**
     * Set stdMapOrg, stdMapSmo, tempVarOrg, correctPars
     */
	public static void noiseEstimation(float[][]F0Pro, float[][][]datOrg, float[][][]datSmo, float smoXY, boolean[][]evtSpatialMask, 
	  float[][]stdMapOrg, float[][]stdMapSmo, float[][]tempVarOrg, float[][]correctPars) {
	  int H = datOrg.length;
      int W = datOrg[0].length;
      int T = datOrg[0][0].length; 
      float[][] tempMap;
      float[][] varMapOrg = new float[H][W];
      float[][] varMapSmo = new float[H][W];
      boolean[][] validMap = new boolean[H][W];
      
      // variance - checked
   	  tempMap = Helper.meanSquaredDifferences(datOrg);
   	  for (int i = 0; i < H; i++) {
   		  for (int j = 0; j < W; j++) {
   			  tempVarOrg[i][j] = tempMap[i][j] / 2;
   			  int count0 = 0;
   			  int countNotNan = 0;
   			  for (int k = 0; k < T; k++) {
   				  if (datOrg[i][j][k] == 0) {
   					count0++;
   				  }
   				  if (!Float.isNaN(datOrg[i][j][k])) {
   					countNotNan++;
   				  }
   			  }			  
   			  float ratio = ((float) count0) / countNotNan;
   			  // truncatedKeptVar - checked
   			  correctPars[i][j] = Helper.truncatedKeptVar(ratio);
   			  varMapOrg[i][j] = tempMap[i][j] / correctPars[i][j];
   			  if (!evtSpatialMask[i][j]) {
   				  varMapOrg[i][j] = Float.NaN;
   			  }
   			  validMap[i][j] = true;
   		  }
   	  }
   	  
	  // find the relationship between F0 and variance, update stdMapOrg - checked
	  fitF0Var(stdMapOrg, F0Pro, varMapOrg, validMap);
//	  Helper.viewMatrix(10, 10, "stdMapOrg", stdMapOrg);
	  if (smoXY == 0) {
		  for (int i = 0; i < H; i++) {
			  for (int j = 0; j < W; j++) {
				  stdMapSmo[i][j] = stdMapOrg[i][j];
			  }
		  }
		  return;
	  }
	  
	  // Smoothed part
      int dist = (int) Math.ceil(2*smoXY);
      float[][] filter0 = new float[dist * 2 + 1][dist * 2 + 1];
      float[][] filter = new float[dist * 2 + 1][dist * 2 + 1];
  
	  // Gaussian filter	  
      filter0[dist][dist] = 1;
      filter0 = GaussFilter.gaussFilter(filter0, smoXY, smoXY);
	  for (int i = 0; i < filter.length; i++) {
  	    for (int j = 0; j < filter[0].length; j++) {
  	        filter[i][j] = filter0[i][j] * filter0[i][j];
  	    }
  	  }
	  
	  // varMapSmo = mean((datSmo(:,:,1:end-1) - datSmo(:,:,2:end)).^2,3,'omitnan')/2;
	  varMapSmo = Helper.meanSquaredDifferences(datSmo);
	  for (int i = 0; i < H; i++) {
   		  for (int j = 0; j < W; j++) {
   			varMapSmo[i][j] = varMapSmo[i][j] / 2;
   		  }  
	  }
//	  Helper.viewMatrix(10, 10, "varMapSmo", varMapSmo);
	  
	  float[][] tempMapCorrected, filteredTempMapCorrected, filteredVarMapOrg;
	  tempMapCorrected = new float[H][W];
	  // element-wise division of tempMap and correctPars
	  for (int i = 0; i < H; i++) {
		  for (int j = 0; j < W; j++) {
			  tempMapCorrected[i][j] = tempMap[i][j] / correctPars[i][j];
		  }
	  }
	  // apply filter to tempMapCorrected and varMapOrg
	  filteredTempMapCorrected = Helper.imfilter(tempMapCorrected, filter);
	  filteredVarMapOrg = Helper.imfilter(tempVarOrg, filter);
    
	  // element-wise multiplication of varMapSmo and divisionResult
	  for (int i = 0; i < H; i++) {
		  for (int j = 0; j < W; j++) {
			  varMapSmo[i][j] *= filteredTempMapCorrected[i][j] / filteredVarMapOrg[i][j];
			  if (!evtSpatialMask[i][j]) {
				  varMapSmo[i][j] = Float.NaN;
			  }
		  }
	  }
          
	  // correct the variance in the boundary (caused by smoothing operation) - checked
	  float[][] correctMap2 = correctBoundaryStd(filter0, filter, dist, stdMapSmo.length, stdMapSmo[0].length);
//	  Helper.viewMatrix(10, 10, "correctMap2", correctMap2);
	  for (int i = 0; i < H; i++) {
		  for (int j = 0; j < W; j++) {
			  if (correctMap2[i][j] >= 1.00001f) {
				  validMap[i][j] = false;
			  }
		  }
	  }
	  
	  fitF0Var(stdMapSmo, F0Pro, varMapSmo, validMap);
//	  Helper.viewMatrix(10, 10, "stdMapSmo", stdMapSmo);
	  for (int i = 0; i < H; i++) {
		  for (int j = 0; j < W; j++) {
			  stdMapSmo[i][j] *= correctMap2[i][j];
		  }
	  }
//	  Helper.viewMatrix(10, 10, "stdMapSmo", stdMapSmo);
	  return;
	}

	  
	  
	  public static void fitF0Var(float[][] stdMapOut, float[][] F0ProOrg, float[][] varMapOrg, boolean[][] validMap) { // dist may be passed or not
	    int H = F0ProOrg.length;
	    int W = F0ProOrg[0].length;
	    ArrayList<Float> F0ProList = new ArrayList<Float>(); // create a list to store the extracted elements
	    ArrayList<Float> varMapList = new ArrayList<Float>(); // create a list to store the extracted elements
	    float eps = 0.00000001f;
	    float minX = Float.MAX_VALUE;
	    float maxX = Float.MIN_VALUE;
	    
	    for (int i = 0; i < H; i++) {
	        for (int j = 0; j < W; j++) {
	            if (validMap[i][j] && varMapOrg[i][j] > eps) {
	            	F0ProList.add(F0ProOrg[i][j]);
	            	varMapList.add(varMapOrg[i][j]);
	            	minX = Math.min(minX, F0ProOrg[i][j]);
	            	maxX = Math.max(maxX, F0ProOrg[i][j]);
	            }
	        }
	    }
	    
	    if(F0ProList.size() == 0) {
	    	for (int i = 0; i < H; i++) {
		        for (int j = 0; j < W; j++) {
		        	stdMapOut[i][j] = (float) Math.sqrt(eps);
		        }
	    	}
	    	return;
	    }
	    
	    int size = 2000;
	    float delta = Math.max(0.00001f, (maxX - minX) / size);
	    int[] countMap = new int[size];
	    float[] sumX = new float[size];
	    float[] sumY = new float[size];
	    float curX;
	    int p;
	    for (int i = 0; i < F0ProList.size(); i++) {
	    	curX = F0ProList.get(i);
	    	p = Math.max(0, (int) Math.ceil((curX - minX) / delta) - 1);
	    	countMap[p]++;
	    	sumX[p] += curX;
	    	sumY[p] += varMapList.get(i);
	    }
	    int cnt = 0;
	    for (int i = 0; i < size; i++) {
	    	if (countMap[i] > 0) {
	    		cnt++;
	    	}
	    }
	    float[] x = new float[cnt]; 
	    float[] y = new float[cnt];
	    cnt = 0;
	    for (int i = 0; i < size; i++) {
	    	if (countMap[i] > 0) {
	    		x[cnt] = sumX[i] / countMap[i];
	    		y[cnt] = sumY[i] / countMap[i];
	    		cnt++;
	    	}
	    }
	    
	    if(x.length == 1) {
	    	for (int i = 0; i < H; i++) {
		        for (int j = 0; j < W; j++) {
		        	stdMapOut[i][j] = y[0];
		        }
	    	}
	    	return;
	    }
	    
	    // graph construction
	    int source = (int) Math.ceil(y.length * 0.05);
	    int sink = Math.max(1, (int) Math.floor(y.length * 0.99));
	    float[] dist1 = new float[y.length];
	    float[] dist2 = new float[y.length];
	    float[] dist3 = new float[y.length];
	    int[] preMap1 = new int[y.length];
	    int[] preMap2 = new int[y.length];
	    int[] preMap3 = new int[y.length];
	    float minCost;
	    int preNode;
	    float cost;
	    float a, b;
	    
	    // first layer
	    for (int j = 0; j < y.length; j++) {
	      minCost = Float.POSITIVE_INFINITY;
	      preNode = j;
	      for (int i = 0; i < Math.min(j, source); i++) {
	          a = (y[j] - y[i]) / (x[j] - x[i]);
	          b = y[i] - a * x[i];
	          cost = 0;
	          for (int k = i; k <= j; k++) {
	              cost += Math.abs(y[k] - (a * x[k] + b));
	          }
	          for (int k = 0; k < i; k++) {
	              cost += Math.abs(y[k] - y[i]);
	          }
	          if (cost < minCost) {
	              minCost = cost;
	              preNode = i;
	          }
	      }
	      dist1[j] = minCost;
	      preMap1[j] = preNode;
	    }
//	    System.out.println("Done");
	    
//	    // second layer
	    for (int j = 0; j < y.length; j++) {
	      minCost = Float.POSITIVE_INFINITY;
	      preNode = j;
	      for (int i = 0; i < j; i++) {
	          a = (y[j] - y[i]) / (x[j] - x[i]);
	          b = y[i] - a * x[i];
	          cost = 0;
	          for (int k = i; k <= j; k++) {
	              cost += Math.abs(y[k] - (a * x[k] + b));
	          }
	          if (dist1[i] + cost < minCost) {
	              minCost = dist1[i] + cost;
	              preNode = i;
	          }
	      }
	      dist2[j] = minCost;
	      preMap2[j] = preNode;
	    }    
	    
//	    // third layer
	    for (int j = sink - 1; j < y.length; j++) {
	      minCost = Float.POSITIVE_INFINITY;
	      preNode = j;
	      for (int i = 0; i < j; i++) {
	          a = (y[j] - y[i]) / (x[j] - x[i]);
	          b = y[i] - a * x[i];
	          cost = 0;
	          for (int k = i; k <= j; k++) {
	              cost += Math.abs(y[k] - (a * x[k] + b));
	          }
	          for (int k = j; k < y.length; k++) {
	              cost += Math.abs(y[k] - y[j]);
	          }
	          if (dist2[i] + cost < minCost) {
	              minCost = dist2[i] + cost;
	              preNode = i;
	          }
	      }
	      dist3[j] = minCost;
	      preMap3[j] = preNode;
	    }

	    int node3 = 0;
	    float minDist = Float.MAX_VALUE;
	    for (int i = sink - 1; i < dist3.length; i++) {
	        if (dist3[i] < minDist) {
	            minDist = dist3[i];
	            node3 = i;
	        }
	    }
	    float x3 = x[node3];
	    float y3 = y[node3];
	    
	    int node2 = preMap3[node3];
	    float x2 = x[node2];
	    float y2 = y[node2];
	    
	    int node1 = preMap2[node2];
	    float x1 = x[node1];
	    float y1 = y[node1];

	    int node0 = preMap1[node1];
	    float x0 = x[node0];
	    float y0 = y[node0];
	    
	    float a1 = (y0 - y1) / (x0 - x1);
	    float b1 = y1 - a1 * x1;

	    float a2 = (y1 - y2) / (x1 - x2);
	    float b2 = y2 - a2 * x2;

	    float a3 = (y3 - y2) / (x3 - x2);
	    float b3 = y2 - a3 * x2;
	    
	    for (int i = 0; i < F0ProOrg.length; i++) {
	      for (int j = 0; j < F0ProOrg[0].length; j++) {
	          if (F0ProOrg[i][j] <= x0) {
	              stdMapOut[i][j] = y0;
	          } else if (F0ProOrg[i][j] >= x0 && F0ProOrg[i][j] < x1) {
	              stdMapOut[i][j] = a1 * F0ProOrg[i][j] + b1;
	          } else if (F0ProOrg[i][j] >= x1 && F0ProOrg[i][j] < x2) {
	              stdMapOut[i][j] = a2 * F0ProOrg[i][j] + b2;
	          } else if (F0ProOrg[i][j] >= x2 && F0ProOrg[i][j] <= x3) {
	              stdMapOut[i][j] = a3 * F0ProOrg[i][j] + b3;
	          } else if (F0ProOrg[i][j] >= x3) {
	              stdMapOut[i][j] = y3;
	          }
	      }
	    }
//	    Helper.viewMatrix(10, 10, "stdMapOut", stdMapOut);
	    for (int i = 0; i < stdMapOut.length; i++) {
	      for (int j = 0; j < stdMapOut[i].length; j++) {
	        if (Float.isNaN(stdMapOut[i][j])) {
	        	stdMapOut[i][j] = y0;
	        }
	        stdMapOut[i][j] = (float) Math.sqrt(stdMapOut[i][j]);
	      }
	    }
//	    Helper.viewMatrix(10, 10, "stdMapOut", stdMapOut);
	  }
		  
	  public static float[][] correctBoundaryStd(float[][] filter0, float[][] filter, int dist, int H, int W) {
	    float[][] correctMap, correctMap2, filter1, slicedCorrectMap; 
	    float sumSquare, sumFilter, minV;
	    float[] columnSums, rowSums;
	    
	    sumFilter = 0;
    	for (int i = 0; i < filter.length; i++) {
    	    for (int j = 0; j < filter[0].length; j++) {
    	        sumFilter += filter[i][j];
    	    }
    	}
	    
	    correctMap = new float[filter0.length][filter0[0].length];
	    for (int x = dist; x < 2*dist + 1; x++) {
	        for (int y = dist; y < 2*dist + 1; y++) {
	        	filter1 = Helper.copy2Darray(filter0);           	
	        	columnSums = new float[filter1[0].length];
	        	for (int i = x; i < filter1.length; i++) {
	        	    for (int j = 0; j < filter1[i].length; j++) {
	        	        columnSums[j] += filter1[i][j];
	        	    }
	        	}
	        	filter1[x] = columnSums;

	        	rowSums = new float[filter1.length];
	        	for (int i = 0; i < filter1.length; i++) {
	        	    for (int j = y; j < filter1[i].length; j++) {
	        	        rowSums[i] += filter1[i][j];
	        	    }
	        	}
	        	for (int i = 0; i < filter1.length; i++) {
	        		filter1[i][y] = rowSums[i];
	        	}
	        	
	        	sumSquare = 0;
	        	for (int i = 0; i <= x; i++) {
	        	    for (int j = 0; j <= y; j++) {
	        	        sumSquare += filter1[i][j] * filter1[i][j];
	        	    }
	        	}
	        	correctMap[x][y] = (float) Math.sqrt(sumSquare / sumFilter);
	        }
	    }
	    
	    
	    
	    // correctMap = correctMap(1+dist:end,1+dist:end);
	    slicedCorrectMap = new float[correctMap.length - dist][correctMap[0].length - dist];
	    for (int i = 0; i < slicedCorrectMap.length; i++) {
	        for (int j = 0; j < slicedCorrectMap[0].length; j++) {
	            slicedCorrectMap[i][j] = correctMap[i + dist][j + dist];
	        }
	    }
	    
	    // correctMap2 = ones(sz);
	    int x0, y0;
	    minV = Float.MAX_VALUE;
	    correctMap2 = new float[H][W];
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		x0 = Math.min(Math.min(x, H - x - 1), dist);
	    		y0 = Math.min(Math.min(y, W - y - 1), dist);
	    		correctMap2[x][y] = slicedCorrectMap[x0][y0];
	    		minV = Math.min(minV, correctMap2[x][y]);
	    	}
	    }
	    for (int x = 0; x < H; x++) {
	    	for (int y = 0; y < W; y++) {
	    		correctMap2[x][y] /= minV;	    		
	    	}
	    }
//	    Helper.viewMatrix(10, 10, "correctMap2", correctMap2);
	    return correctMap2;    
	  }		
	  
	public static float obtainBias(int winSize, int cut) {  
		
      if (winSize>cut) return 0;      
      
      // load('F0_biasMatrix.mat');
      float bias0, bias, cut0, cut1, bias1;
      int idx0, idx1, idy0, idy1;
//      InputStream is = ImageDealer.class.getClassLoader().getResourceAsStream("cuts.csv");
//      float[][] biasMatrix = Helper.loadMatrix(opts.resourcePath + "biasMatrix.csv"); // src/main/resources/biasMatrix.csv
//      float[] cuts = Helper.loadArray(opts.resourcePath + "cuts.csv");
//      float[] windowSizes = Helper.loadArray(opts.resourcePath + "windowSizes.csv");
      
      float[][] biasMatrix = Helper.loadMatrix(ImageDealer.class.getClassLoader().getResourceAsStream("biasMatrix.csv")); // src/main/resources/biasMatrix.csv
      float[] cuts = Helper.loadArray(ImageDealer.class.getClassLoader().getResourceAsStream("cuts.csv"));
      float[] windowSizes = Helper.loadArray(ImageDealer.class.getClassLoader().getResourceAsStream("windowSizes.csv"));
      
      // linear interpolate value if cannot find it
      idx0 = -1;
      for (int i = windowSizes.length - 1; i >= 0; i--) {
          if (winSize >= windowSizes[i]) {
              idx0 = i;
              break;
          }
      }
      idy0 = -1;
      for (int i = cuts.length - 1; i >= 0; i--) {
          if (cut >= cuts[i]) {
              idy0 = i;
              break;
          }
      }
      if (idx0 == windowSizes.length || windowSizes[idx0] == winSize) {
          idx1 = idx0;
      } else {
          idx1 = idx0 + 1;
      }
      if (idy0 == cuts.length || cuts[idy0] == cut) {
          idy1 = idy0;
      } else {
          idy1 = idy0 + 1;
      }
      
      // bias0 = nanmean(biasMatrix([idx0,idx1],idy0));
      bias0 = Helper.getMean(biasMatrix, new int[]{idx0, idx1}, idy0);
      bias1 = Helper.getMean(biasMatrix, new int[]{idx0, idx1}, idy1);
      cut0 = cuts[idy0];
      cut1 = cuts[idy0 + 1];
      
      // if isnan(bias0)
      if (Float.isNaN(bias0)) {
          bias = bias1;
      } else {
          bias = bias0 + (bias1 - bias0) / (cut1 - cut0) * (cut - cut0);
      }
      return bias;
    }	
	
}