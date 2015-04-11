package FeatureExtraction;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


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
	
	private void fileDelete(String filePath) {
		try {
			File file = new File(filePath);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getMostFrequentHSV() {
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
		for (int i=0; i<row; i=i+3) {
			for (int j=0; j<col; j=j+3) {
				double h = (hsv.get(i, j)[0] / 180) * 360;
				double s = (hsv.get(i, j)[1] / 255) * 100;
				double v = (hsv.get(i, j)[2] / 255) * 100;
				
				hsvKey = (int)h+"/"+(int)s+"/"+(int)v;
				
				if (!hsvValueFreq.containsKey(hsvKey)) {
					hsvValueFreq.put(hsvKey, 0); 
				}
				hsvValueFreq.put(hsvKey, hsvValueFreq.get(hsvKey) + 1);
				
				//h_sum += h;
				//s_sum += s;
				//v_sum += v;
				
				if (h_max < h) {
					h_max = h;
				}
				
				if (s_max < s) {
					s_max = s;
				}
				
				if (v_max < v) {
					v_max = v;
				}
				
			}
		}
		
		int maxHSV = 0;
		String maxHSVValue = null;
		for (String key : hsvValueFreq.keySet()) {
			int hsvFreq = hsvValueFreq.get(key);
			if (maxHSV < hsvFreq) {
				maxHSV = hsvFreq;
				maxHSVValue = key;
			}
		}
		
		System.out.println(maxHSV + " " + maxHSVValue);
		
		String[] hsvSplit = maxHSVValue.split("/");
		String[] resultHSV = new String[3];
		resultHSV[0] = "h;" + hsvSplit[0];
		resultHSV[1] = "s;" + hsvSplit[1];
		resultHSV[2] = "v;" + hsvSplit[2];
		
		//double h_avg = h_sum / (row*col);
		//double s_avg = s_sum / (row*col);
		//double v_avg = v_sum / (row*col);
		
		//System.out.println(col + "\t" + row + "\t" + m.get(0, 0)[0] + "\t" + m.get(0, 0)[1] + "\t" + m.get(0, 0)[2]);
		//System.out.println(h_avg + "\t" + s_avg + "\t" + v_avg);
		//System.out.println(h_max + "\t" + s_max + "\t" + v_max);
		return resultHSV;
	}
	
	//get svalue
	public String sStart() throws IOException{
		
		String filename = mFilename+"_s.dat";
		
		//For Test
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		StringBuffer sResult = new StringBuffer("");
		
		Mat hsv_image = new Mat();
			
		if(m.channels() == numberOfChannel){
			org.opencv.imgproc.Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_RGB2HSV);
			java.util.List<Mat> hsv_planes = new LinkedList<Mat>();
			MatOfFloat vHistRange = new MatOfFloat(0, vHistSize);
			Mat v_hist = new Mat();
			boolean accumulate = false;
			org.opencv.core.Core.split(hsv_image, hsv_planes);
			java.util.List<Mat> hsv_planes1 = new LinkedList<Mat>();
			
			//To get 's' part in picture
			hsv_planes1.add(hsv_planes.get(1));

			//Calculate histogram
			Imgproc.calcHist(hsv_planes1, 
					new MatOfInt(0), new Mat(), v_hist , 
					new MatOfInt(vHistSize), 
					vHistRange,
					accumulate);
			
			Mat vHistImage = new Mat(vHist_w, vHist_h, org.opencv.core.CvType.CV_8UC3, new Scalar(0, 0, 0));
			
			//Normalization
			Core.normalize(v_hist, v_hist, 0, vHistImage.rows(), Core.NORM_MINMAX, -1, new Mat());
			
			//Approximation
			approximateSV(v_hist, vMax, out, sResult, 's');
			
		}
		else{
			System.out.println("There are some problems in image channel");
		}

		out.close();
		fileDelete(filename);
		return sResult.toString();
	}
	
	//getv_value
	public String vStart() throws IOException{
		
		String filename = mFilename+"_v.dat";
		
		StringBuffer vResult = new StringBuffer("");
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		Mat hsv_image = new Mat();
			
		if(m.channels() == numberOfChannel){
			org.opencv.imgproc.Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_RGB2HSV);
			java.util.List<Mat> hsv_planes = new LinkedList<Mat>();
			MatOfFloat vHistRange = new MatOfFloat(0, vHistSize);
			Mat v_hist = new Mat();
			boolean accumulate = false;
			org.opencv.core.Core.split(hsv_image, hsv_planes);
			java.util.List<Mat> hsv_planes1 = new LinkedList<Mat>();
			
			//To get 'v' part in the picture.
			hsv_planes1.add(hsv_planes.get(2));

			Imgproc.calcHist(hsv_planes1, 
					new MatOfInt(0), new Mat(), v_hist , 
					new MatOfInt(vHistSize), 
					vHistRange,
					accumulate);
			
			Mat vHistImage = new Mat(vHist_w, vHist_h, org.opencv.core.CvType.CV_8UC3, new Scalar(0, 0, 0));
			Core.normalize(v_hist, v_hist, 0, vHistImage.rows(), Core.NORM_MINMAX, -1, new Mat());
			approximateSV(v_hist, vMax, out, vResult, 'v');
			
		}
		else{
			System.out.println("There are some problems in image channel");
		}
		out.close();
		
		fileDelete(filename);
		return vResult.toString();
	}

	//get hvalue
	public String hStart() throws IOException{
		
		String filename = mFilename+"_h.dat";
		
		StringBuffer hResult = new StringBuffer("");
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		
		Mat hsv_image = new Mat();
			
		if(m.channels() == numberOfChannel){
		
			org.opencv.imgproc.Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_RGB2HSV);
			
			java.util.List<Mat> hsv_planes = new LinkedList<Mat>();
			
			MatOfFloat hHistRange = new MatOfFloat(0, hHistSize);
			
			Mat h_hist = new Mat();
			boolean accumulate = false;
			
			org.opencv.core.Core.split(hsv_image, hsv_planes);
			
			java.util.List<Mat> hsv_planes1 = new LinkedList<Mat>();
			
			//To get 'h' elements
			hsv_planes1.add(hsv_planes.get(0));
			  
			Imgproc.calcHist(hsv_planes1, 
					new MatOfInt(0), new Mat(), h_hist , 
					new MatOfInt(hHistSize), 
					hHistRange,
					accumulate);
			
			Mat hHistImage = new Mat(hHist_w, hHist_h, org.opencv.core.CvType.CV_8UC3, new Scalar(0, 0, 0));
			
			//Normalization
			Core.normalize(h_hist, h_hist, 0, hHistImage.rows(), Core.NORM_MINMAX, -1, new Mat());
			
			//Approximation
			approximateH(h_hist, hMax, out, hResult);

		
		}
		else{
			System.out.println("There are some problems in image channel");
		}

		out.close();
		
		fileDelete(filename);
		return hResult.toString();
	}
	
	
	private int getMostFrequentNumber(ArrayList<Double> list) {
		HashMap<Integer, Integer> freq = new HashMap<Integer, Integer> ();
		for (int i=0; i<list.size(); i++) {
			int number = list.get(i).intValue();
			if (!freq.containsKey(number))
				freq.put(number, 0);
			freq.put(number, freq.get(number)+1);
		}
		
		int max = -1;
		int maxIdx = -1;
		for (int key : freq.keySet()) {
			int freqNum = freq.get(key);
			
			if (max < freqNum) {
				maxIdx = key;
				max = freqNum;
			}
		}
		
		return maxIdx;
	}
	
	private void approximateSV(Mat v_hist, int hmax2, BufferedWriter out, StringBuffer stb, char sv) throws IOException {
		int i, x, tmp, color_max = -1, idx = 0, w_bin = Math.round( 256 / vHistSize);
		int[] sv_table = new int[numberOfSV];
		
		for(i = 0; i < numberOfSV; i++){
			sv_table[i] = 0;
		}
	
		ArrayList<Double> valueList = new ArrayList<Double>();
		
		for(i = 0; i < hmax2; i++){
			x = i * w_bin;
			double[] t = v_hist.get(i,0);
			tmp = Integer.valueOf((int) Math.round(t[0]));
			
			for (int j=0; j<tmp; j++) {
				valueList.add((i / 255.0)*100);		
			}	
		}

		int mostFrequentValue = getMostFrequentNumber(valueList);
		
		//int mostFrequentValue = (int) average(valueList);
		
		stb.append(sv + ";" + mostFrequentValue);

	}
	
	public static double average(ArrayList<Double> list) {
		System.out.println(list.size());
	    // 'average' is undefined if there are no elements in the list.
	    if (list == null || list.isEmpty())
	        return 0.0;
	    // Calculate the summation of the elements in the list
	    long sum = 0;
	    int n = list.size();
	    // Iterating manually is faster than using an enhanced for loop.
	    for (int i = 0; i < n; i++)
	        sum += list.get(i);
	    // We don't want to perform an integer division, so the cast is mandatory.
	    return ((double) sum) / n;
	}
	
	private void approximateH(Mat hist, int histSize, BufferedWriter out, StringBuffer stb ) throws IOException{
		int i, x, tmp, color_max = -1, idx = 0, w_bin = Math.round( hMax / histSize);
		int[] color_table = new int[numberOfColor];
		
		//initialize
		for(i = 0; i < numberOfColor; i++){
			color_table[i] = 0;
		}
		
		ArrayList<Double> valueList = new ArrayList<Double>();
		
		for(i = 0; i < histSize; i++){
			x = i * w_bin;
			double[] t = hist.get(i,0);
			
			tmp = Integer.valueOf((int) Math.round(t[0]));
			
			for (int j=0; j<tmp; j++) {
				valueList.add(i * 2.0);		
			}	
		}  	
		int mostFrequentValue = getMostFrequentNumber(valueList);
		//int mostFrequentValue = (int) average(valueList);
		stb.append("h" + ";" + mostFrequentValue);
	}
}