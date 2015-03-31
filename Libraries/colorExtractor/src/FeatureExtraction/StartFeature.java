package FeatureExtraction;

import java.io.IOException;


public class StartFeature {
	public String[] startFromFile(String filename) throws IOException {
		
		//HSV_Extractor help to extract h,s,v element in picture.
		HSV_Extractor hsvExtractor = new HSV_Extractor(filename);

		//Append each elements
		String h = (hsvExtractor.hStart());
		String s = (hsvExtractor.sStart());
		String v = (hsvExtractor.vStart());

		String[] hsv = new String[3];

		hsv[0] = h;
		hsv[1] = s;
		hsv[2] = v;

		return hsv;

	}


}
