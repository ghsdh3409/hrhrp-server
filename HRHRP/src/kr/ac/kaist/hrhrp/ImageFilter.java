package kr.ac.kaist.hrhrp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.util.PhotoFilter;

public class ImageFilter {
	
	private DBHandler dbTemplate;
	
	private static String srcPath = "";
	private final static String destPath = "/home/daehoon/public_html/hrhrp/photos/";
	private final static String domain = "http://dmserver4.kaist.ac.kr/~daehoon/hrhrp/photos/";

	public ImageFilter() {
		dbTemplate = new DBHandler();
	}
	
	private void close() {
		dbTemplate.close();
	}
	
	private ArrayList<String> getUserPaths() {
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

	private void validateFolder(String filePath) {
		File file = new File( filePath );

		if( file.exists() == false ) {
			file.mkdirs();
		}
	}
	
	private String getImgURL(String user, String filePath) {
		File file = new File(filePath);
		String filename = file.getName();
		String parentDir = file.getParentFile().getName();
		
		return domain + user + "/" + parentDir + "/" + filename;
	}
	
	private void uploadDatabase(String imageUrl, String imagePath, String ownerId, Date imageTime, double lat, double lng) {
		dbTemplate.insertImageInfo(imageUrl, imagePath, imageTime, ownerId, lat, lng);
	}
	
	public void imageFilter() {
		// TODO Auto-generated method stub
		
		srcPath = "/home/daehoon/HRHRP/images/source/";
		
		ImageFilter filter = new ImageFilter();
		
		for (String user : filter.getUserPaths()) {
			PhotoFilter pf = new PhotoFilter();
			
			ArrayList<String> imagePaths = new ArrayList<String>();

			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowDate =  sdf.format(d);
			
			String userSrcPath = srcPath + user + "/";
			String userDestPath = destPath + user + "/" + nowDate + "/";
			
			filter.validateFolder(userDestPath);

			imagePaths = pf.selectPhotos(userSrcPath, userDestPath);
			
			for (String imagePath : imagePaths) {
				String imgUrl = filter.getImgURL(user, imagePath);
				System.out.println(imgUrl);
				
				double lat = 36.370300;
				double lng = 127.361573;
				
				Date takenAt = null;
				
				File file = new File(imagePath);
				long lastModifiedInMSec = file.lastModified();
				takenAt = new Date(lastModifiedInMSec);		
				
				/* Unsupported
				try {
					takenAt = JPEGExifExtraction.getEXIFDateInfo(imagePath);
					if (takenAt == null) {
						File file = new File(imagePath);
						long lastModifiedInMSec = file.lastModified();
						takenAt = new Date(lastModifiedInMSec);					
					}
				} catch (Exception e) {	
					e.printStackTrace();
					takenAt = null;
				}
				*/
				
				filter.uploadDatabase(imgUrl, imagePath, user, takenAt, lat, lng);
			}
			
		}
	
		filter.close();
	}
}
