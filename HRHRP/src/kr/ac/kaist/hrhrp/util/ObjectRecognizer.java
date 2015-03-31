package kr.ac.kaist.hrhrp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import kr.ac.kaist.hrhrp.type.Image;

public class ObjectRecognizer {

	public static int objectReconizer(Image image) {
		String filePath = image.getPath();
		return getObjectIndex(filePath);
	}
	
	public static int getObjectIndex(String imageFile){
		String filename = imageFile;

		String s = null;
		try{
			Process q = Runtime.getRuntime().exec("/bin/sh convert -resize 256x256! " +imageFile+" "+imageFile);
			String input = "python /home/hrhrpobj/caffe-master/python/predict3_path.py "+filename;
			System.out.println(input);
			Process p = Runtime.getRuntime().exec(input);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((s = stdInput.readLine()) != null) {
				StringTokenizer st1 = new StringTokenizer(s);
				while(st1.hasMoreTokens()){
					String temp = st1.nextToken();	
					if(isInteger(temp) == true){
						System.out.println(temp);	
						return Integer.parseInt(temp);
					}

				}
			}
		}

		catch (Throwable t){
			t.printStackTrace();	
		}
		return -1;
	}


	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}
}
