package FeatureExtraction;

import java.io.IOException;

public class main {
	public static void main( String[] args ) throws IOException{
		
		String filename = "/home/daehoon/image/024804.jpg";
		StartFeature sf = new StartFeature();
		
		String[] hsv = sf.startFromFile(filename);
		
		System.out.println(hsv[0]);
		System.out.println(hsv[1]);
		System.out.println(hsv[2]);
	}
}