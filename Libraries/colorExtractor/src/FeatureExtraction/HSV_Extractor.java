package FeatureExtraction;

import org.opencv.core.*;
import org.opencv.highgui.*;

import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
	
	

	private void approximateSV(Mat v_hist, int hmax2, BufferedWriter out, StringBuffer stb, char sv) throws IOException {
		int i, x, tmp, color_max = -1, idx = 0, w_bin = Math.round( 256 / vHistSize);
		int[] sv_table = new int[numberOfSV];
		
		for(i = 0; i < numberOfSV; i++){
			sv_table[i] = 0;
		}
	
		for(i = 0; i < hmax2; i++){
			x = i * w_bin;
			double[] t = v_hist.get(i,0);
			tmp = Integer.valueOf((int) Math.round(t[0]));
			
			
			
			if(tmp < slotSize){
				sv_table[0] = sv_table[0] + tmp;
			}
			else if(tmp >= slotSize && tmp < slotSize*2){
				sv_table[1] = sv_table[1] + tmp;
			}
			else if(tmp >= slotSize*2 && tmp < slotSize*3){
				sv_table[2] = sv_table[2] + tmp;
			}
			else if(tmp >= slotSize*3 && tmp < slotSize*4){
				sv_table[3] = sv_table[3] + tmp;
			}
			else if(tmp >= slotSize*4 && tmp < slotSize*5){
				sv_table[4] = sv_table[4] + tmp;
			}
			else if(tmp >= slotSize*5 && tmp < slotSize*6){
				sv_table[5] = sv_table[5] + tmp;
			}
			else if(tmp >= slotSize*6 && tmp < slotSize*7){
				sv_table[6] = sv_table[6] + tmp;
			}
			else if(tmp >= slotSize*7 && tmp <= slotSize*8){
				sv_table[7] = sv_table[7] + tmp;
			}

		}
		
		for(i = 0; i < numberOfSV; i++){
			sv_table[i] = sv_table[i] / slotSize;
		}
		
		
		stb.append(sv + ";");
		
		for(i = 0; i < numberOfSV; i++){
			if(i == numberOfSV-1)
				stb.append(sv_table[i]);
			else
				stb.append(sv_table[i]+",");
			out.write((i+1) + "\t" + sv_table[i] + "\n");
		}  
	}
	

	static void approximateH(Mat hist, int histSize, BufferedWriter out, StringBuffer stb ) throws IOException{
		int i, x, tmp, color_max = -1, idx = 0, w_bin = Math.round( hMax / histSize);
		int[] color_table = new int[numberOfColor];
		
		//initialize
		for(i = 0; i < numberOfColor; i++){
			color_table[i] = 0;
		}
	
		for(i = 0; i < histSize; i++){
			x = i * w_bin;
			double[] t = hist.get(i,0);
			
			tmp = Integer.valueOf((int) Math.round(t[0]));
			//out.write((i+1) + "\t" + tmp + "\n");
			//System.out.println("color = "+x+"power = "+tmp);
			
			// RED
			if( (x >= 0.0 && x < 7.5) || (x >= 172.5 && x < 180) )
				color_table[0] = color_table[0] + tmp;
			// ORANGE
			else if( x >= 7.5 && x < 22.5 )
				color_table[1] = color_table[1] + tmp;
			// YELLOW
			else if( x >= 22.5 && x < 37.5 )
				color_table[2] = color_table[2] + tmp;
			// YELLOW GREEN
			else if( x >= 37.5 && x < 52.5 )
				color_table[3] = color_table[3] + tmp;
			// GREEN
			else if( x >= 52.5 && x < 67.5 )
				color_table[4] = color_table[4] + tmp;
			// BLUISH GREEN
			else if( x >= 67.5 && x < 82.5 )
				color_table[5] = color_table[5] + tmp;
			// CYAN
			else if( x >= 82.5 && x < 97.5 )
				color_table[6] = color_table[6] + tmp;
			// NAVY BLUE
			else if( x >= 97.5 && x < 112.5 )
				color_table[7] = color_table[7] + tmp;
			// BLUE
			else if( x >= 112.5 && x < 127.5 )
				color_table[8] = color_table[8] + tmp;
			// PURPLE
			else if( x >= 127.5 && x < 142.5 )
				color_table[9] = color_table[9] + tmp;
			// MAGENTA
			else if( x >= 142.5 && x < 157.5 )
				color_table[10] = color_table[10] + tmp;
			// PINK
			else if( x >= 157.5 && x < 172.5 )
				color_table[11] = color_table[11] + tmp;
		}

		for( i=0; i<numberOfColor; i++){
			color_table[i] = color_table[i] / numberOfColor;
		}  
		
		stb.append("h;");
		
		// get the maximum among color_table value, which is the most used color in the image
		for( i=0; i<numberOfColor; i++){
			if(i == numberOfColor-1)
				stb.append(color_table[i]);
			else
				stb.append(color_table[i]+",");
			out.write((i+1) + "\t" + color_table[i] + "\n");
		}  
	}
}