package FeatureExtraction;

public class main {
	public static void main( String[] args ) throws Exception{
		
		//image file name
		String filename = "D:/img/2.jpg";
		StartFeature sf = new StartFeature();
		
		double[] representativeHSV = sf.startFromFile(filename);
		
		
		
		
		// [0] : H
		// [1] : S
		// [2] : V
		
		System.out.println("Representative H = "+representativeHSV[0]);
		System.out.println("Representative S = "+representativeHSV[1]);
		System.out.println("Representative V = "+representativeHSV[2]);
		
		
	//	System.out.println("finished!");
	}
}