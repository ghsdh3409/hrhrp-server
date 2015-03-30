package FeatureExtraction;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class HSV_Extractor {


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
	private String filename;

	
	
	
	//Initialization
	public HSV_Extractor(String _filename){
		System.loadLibrary("opencv_java249");
		m=Highgui.imread(_filename,Highgui.CV_LOAD_IMAGE_COLOR);
		filename = _filename;
	}
	
	
	//get svalue
	public String sStart() throws IOException{
		
		//For Test
		BufferedWriter out = new BufferedWriter(new FileWriter(filename+"_s.dat"));
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
		return sResult.toString();
	}
	
	//getv_value
	public String vStart() throws IOException{
		
		StringBuffer vResult = new StringBuffer("");
		BufferedWriter out = new BufferedWriter(new FileWriter(filename+"_v.dat"));

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
		return vResult.toString();
	}

	//get hvalue
	public String hStart() throws IOException{
		
		StringBuffer hResult = new StringBuffer("");
		BufferedWriter out = new BufferedWriter(new FileWriter(filename+"_h.dat"));
		
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
		return hResult.toString();
	}
	
	
	private int getMostFrequentNumber(ArrayList<Integer> list) {
		HashMap<Integer, Integer> freq = new HashMap<Integer, Integer> ();
		for (int i=0; i<list.size(); i++) {
			int number = list.get(i);
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
	
		ArrayList<Integer> valueList = new ArrayList<Integer>();
		
		for(i = 0; i < hmax2; i++){
			x = i * w_bin;
			double[] t = v_hist.get(i,0);
			tmp = Integer.valueOf((int) Math.round(t[0]));
			
			valueList.add(tmp);
		}

		int mostFrequentValue = getMostFrequentNumber(valueList);
		
		stb.append(sv + ";" + mostFrequentValue);

	}
	
	private void approximateH(Mat hist, int histSize, BufferedWriter out, StringBuffer stb ) throws IOException{
		int i, x, tmp, color_max = -1, idx = 0, w_bin = Math.round( hMax / histSize);
		int[] color_table = new int[numberOfColor];
		
		//initialize
		for(i = 0; i < numberOfColor; i++){
			color_table[i] = 0;
		}
		
		ArrayList<Integer> valueList = new ArrayList<Integer>();
		
		for(i = 0; i < histSize; i++){
			x = i * w_bin;
			double[] t = hist.get(i,0);
			
			tmp = Integer.valueOf((int) Math.round(t[0]));
			
			valueList.add(tmp);		
		}  	
		int mostFrequentValue = getMostFrequentNumber(valueList);
		
		stb.append("h" + ";" + mostFrequentValue);
	}
}