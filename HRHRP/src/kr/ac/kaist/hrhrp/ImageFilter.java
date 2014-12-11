package kr.ac.kaist.hrhrp;

import hrhrp.PhotoFilter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.kaist.hrhrp.db.DBHandler;

public class ImageFilter {
	
	private DBHandler dbTemplate;
	
	private final static String srcPath = "D:/HRHRP_TEST/source/";
	private final static String destPath = "D:/HRHRP_TEST/destination/";
	private final static String domain = "http://dmserver1.kaist.ac.kr/hrhrp/images/photos/";

	public ImageFilter() {
		dbTemplate = new DBHandler();
	}
	
	public void close() {
		dbTemplate.close();
	}
	
	public ArrayList<String> getUserPaths() {
		ArrayList<String> userPaths = new ArrayList<String>();		

		File path = new File(srcPath);
		File[] fileList = path.listFiles();

		for (int i=0; i<fileList.length; i++) {
			File file = fileList[i];
			if (file.isDirectory()) {
				System.out.println(file.getPath());
				userPaths.add(file.getName());
			}
		}

		return userPaths;

	}

	public void validateFolder(String filePath) {
		File file = new File( filePath );

		if( file.exists() == false ) {
			file.mkdirs();
		}
	}
	
	public String getImgURL(String user, String filePath) {
		File file = new File(filePath);
		String filename = file.getName();
		
		return domain + user + "/" + filename;
	}
	
	public void uploadDatabase(String imageUrl, String imagePath, String ownerId, long imageTime, double lat, double lng) {
		dbTemplate.insertImageInfo(imageUrl, imagePath, imageTime, ownerId, lat, lng);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ImageFilter filter = new ImageFilter();
		
		for (String user : filter.getUserPaths()) {
			PhotoFilter pf = new PhotoFilter();
			
			ArrayList<String> imagePaths = new ArrayList<String>();

			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowDate =  sdf.format(d);
			
			String userSrcPath = srcPath + user + "/" + nowDate + "/";
			String userDestPath = destPath + user + "/" + nowDate + "/";
			
			filter.validateFolder(userDestPath);

			imagePaths = pf.selectPhotos(userSrcPath, userDestPath);
			
			for (String imagePath : imagePaths) {
				String imgUrl = filter.getImgURL(user, imagePath);
				System.out.println(imgUrl);
				double lat = 36.370300;
				double lng = 127.361573;
				filter.uploadDatabase(imgUrl, imagePath, user, 00000000, lat, lng);
			}
			
		}
	
		filter.close();
	}
}
