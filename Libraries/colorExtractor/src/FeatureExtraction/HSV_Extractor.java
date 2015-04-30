package FeatureExtraction;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


class hsv{
	double h;
	double s;
	double v;
	int clusterId;
	
	public hsv(){
		h = 0.0;
		s = 0.0;
		v = 0.0;
		clusterId = -1;
	}	
	
	public hsv(double _h, double _s, double _v, int _clusterId){
		h = _h;
		s = _s;
		v = _v;
		clusterId = _clusterId;
	}
	
	int getClusterId() {return clusterId;}
	void setClusterId(int _clusterId) { clusterId = _clusterId; }
	
	double getH() {return h;}
	double getS() {return s;}
	double getV() {return v;}
	
	void setH(double _h) { h = _h;}
	void setS(double _s) { s = _s;}
	void setV(double _v) { v = _v;}
	
}

public class HSV_Extractor {

	static {
		//System.loadLibrary("opencv_java249"); //opencv_java249
        System.load("/usr/local/share/OpenCV/java/libopencv_java249.so"); //opencv_java249

	}

	//Constants
	public static final String[] colorMatch = { "blue", "navy blue" ,"cyan" ,
		"bluish green", "green", "yellow green", "yellow", "orange", "red", "pink",
		"magenta", "purple"};
	
	public static final int numberOfChannel = 3;
	public static final int numberOfSV = 8;
	public static final int slotSize = 32;
	public static final int numberOfColor = colorMatch.length;
	
	public static final int hHistSize = 180, hMax = 180, 
			hHist_w = 360, hHist_h = 400;
	
	public static final int vHistSize = 256, vMax = 256, 
			vHist_w = 300, vHist_h = 400;
	
	private Mat m;
	private String mFilename;

	//Initialization
	public HSV_Extractor(String _filename){
		m=Highgui.imread(_filename,Highgui.CV_LOAD_IMAGE_COLOR);
		mFilename = _filename;
	}
	
	
	
	
	public double[] getRepresentativeHSV() {
		String hsvKey = null;
		Mat hsv = new Mat();
		hsv = m;
		Imgproc.cvtColor(m, hsv, Imgproc.COLOR_BGR2HSV);
		int col = (int) hsv.size().width;
		int row = (int) hsv.size().height;
		int colSampleRate = (int) (col * 0.01);
		int rowSampleRate = (int) (row * 0.01);
		//double h_sum = 0.0;
		//double s_sum = 0.0;
		//double v_sum = 0.0;
		
		double h_max = 0.0;
		double s_max = 0.0;
		double v_max = 0.0;
		
		//System.out.println(rowSampleRate + "\t" + colSampleRate);
		
		HashMap<String, Integer> hsvValueFreq = new HashMap<String, Integer>();
		
		Vector<hsv> hsvSet = new Vector<hsv>();
		for (int i=0; i<row; i+=3) {
			for (int j=0; j<col; j+=3) {
				double h = (hsv.get(i, j)[0] / 180) * 360;
				double s = (hsv.get(i, j)[1] / 255) * 100;
				double v = (hsv.get(i, j)[2] / 255) * 100;
				
				//System.out.println(h+"\t"+s+"\t"+v);
				
				hsvKey = (int)h+"/"+(int)s+"/"+(int)v;
				
				hsvSet.add(new hsv(h, s, v, 0));
				
			}
		}
		
		
		
		//Start K-means algorithm
		//Parameter : hsvValueContainer, value k, threshold
		double[] representativeValue = startKmeans(hsvSet, 3, 0.05);	
		
		System.out.println("Representative HSV value is following");
		System.out.println("h = "+representativeValue[0]);
		System.out.println("s = "+representativeValue[1]);
		System.out.println("v = "+representativeValue[2]);
		
		double[] returnValue = new double[3];
		returnValue[0] = representativeValue[0];
		returnValue[1] = representativeValue[1];
		returnValue[2] = representativeValue[2];
		
		
		return returnValue;
	}	

	
	
	
	
	
	private double[] startKmeans( Vector<hsv> hsvSet, int k, double threshold) {
		
		//int numberOfFixel = hsvSet.size();
		double[][] clusterPoint = new double[k][3];
		//double threshold = 0.5;
		
		Random oRandom = new Random();
		
		//Initialize cluster points
		for(int i = 0; i < k; i++){
			clusterPoint[i][0] = oRandom.nextDouble();
			clusterPoint[i][1] = oRandom.nextDouble();
			clusterPoint[i][2] = oRandom.nextDouble();		
		}
		
		hsv sumCluster[] = new hsv[k];
		int clusterSize[] = new int[k];
		
		for(int i = 0; i < k; i++){
			sumCluster[i] = new hsv();
			sumCluster[i].setH(0.0);
			sumCluster[i].setS(0.0);
			sumCluster[i].setV(0.0);
			clusterSize[i] = 0;
			
		}
		
		
		
		//Calculate Kmeans Algorithm
		double delta = 10000;
		int cnt = 0;
		while(delta > threshold && cnt < 50 ){
			cnt++;
			System.out.println("cnt = "+cnt);
			
			for(int i = 0; i < k; i++){
				clusterSize[i] = 0;
				sumCluster[i].setH(0.0);
				sumCluster[i].setS(0.0);
				sumCluster[i].setV(0.0);
			}
			
			for (hsv temp : hsvSet){
				int newCid = getCluster(temp, clusterPoint, k, sumCluster, clusterSize);
				temp.setClusterId(newCid);
			}
			//Adjust Cluster Points
			double[][] clusterPointOriginal = new double[k][3];
			
			for(int i = 0; i < k; i++){
				for(int l = 0; l < 3; l++){
					clusterPointOriginal[i][l] = clusterPoint[i][l];
				}
			}
			
			for (int j = 0; j < k; j++){
				clusterPoint[j][0] = sumCluster[j].getH() / clusterSize[j];
				clusterPoint[j][1] = sumCluster[j].getS() / clusterSize[j];
				clusterPoint[j][2] = sumCluster[j].getV() / clusterSize[j];
			}
			
			for(int p = 0; p < k; p++){
				delta = 0;
				delta += Math.abs(clusterPointOriginal[p][0] - clusterPoint[p][0]);
				delta += Math.abs(clusterPointOriginal[p][1] - clusterPoint[p][1]);
				delta += Math.abs(clusterPointOriginal[p][2] - clusterPoint[p][2]);
			}
			System.out.println("delta = "+delta);
			
			for(int i = 0; i < k; i++){
				System.out.println("Cluster "+i+" Count = "+clusterSize[i]);
			}
		}
		
		System.out.println("Cnt = "+cnt);
		int max = 0;
		int maxIdx = -1;
		for(int i = 0; i < k; i++){
			if(clusterSize[i] > max){
				max = clusterSize[i];
				maxIdx = i;
			}
		}
		System.out.println("Largest cluster ID = "+maxIdx);
		return clusterPoint[maxIdx];
	}




	private int getCluster(hsv temp, double[][] clusterPoint, int k, hsv[] sumCluster, int[] clusterSize) {
		
		int newCid = -1;
		double maxDist = 100000000;
		
		double temp_h = temp.getH();
		double temp_s = temp.getS();
		double temp_v = temp.getV();
		
		for(int i = 0; i < k; i++){
			double dist = Math.sqrt(
					Math.pow((temp_h - clusterPoint[i][0]),2) +  
					Math.pow((temp_s - clusterPoint[i][1]),2) +  
					Math.pow((temp_v - clusterPoint[i][2]),2)
					);
			if(dist < maxDist){
				maxDist = dist;
				newCid = i;
			}
		}
		
		sumCluster[newCid].setH(sumCluster[newCid].getH() + temp_h);
		sumCluster[newCid].setS(sumCluster[newCid].getS() + temp_s);
		sumCluster[newCid].setV(sumCluster[newCid].getV() + temp_v);

		clusterSize[newCid]++;
		
		
		return newCid;
	}
}