package FeatureExtraction;

import java.io.IOException;


public class StartFeature {
	public double[] startFromFile(String filename) throws IOException {
		
		//HSV_Extractor help to extract h,s,v element in picture.
		HSV_Extractor hsvExtractor = new HSV_Extractor(filename);


		
		double[] representativeHSV = hsvExtractor.getRepresentativeHSV();
		
		return representativeHSV;

	}


}
