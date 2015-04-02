package kr.ac.kaist.hrhrp.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

public class PhotoFilter {
	
	public PhotoFilter(){
		
	}
	
	/*
	// folderName 폴더에 있는 사진들 중에서 골라내기!
	public ArrayList<File> selectPhotos(String folderName){
		// 선별된 사진들을 저장하는 리스트
		ArrayList<File> selectedPhotoList = new ArrayList<File>();
		
		File folder=new File(folderName);
		File[] files=folder.listFiles();
		int numOfFiles=files.length;
		System.out.println(numOfFiles+" images!");
		File file1, file2;
		double sim;
		
		// 첫 사진은 우선 저장하다.
		selectedPhotoList.add(files[0]);
		
		// 각 사진들에 대해...
		for (int i=0;i<numOfFiles-1;i++){
			//System.out.println((i+1)+ "번째 photo!");
			file1=new File(folderName+"/"+files[i].getName());
			file2=new File(folderName+"/"+files[i+1].getName());
			
			sim=this.getCosSimilarity(file1, file2);
			if(sim>0.8){
				// Do Nothing!
			}
			else{
				selectedPhotoList.add(file2);
			}
		}
		
		return selectedPhotoList;
	}
	*/
	
	// folderName 폴더에 있는 사진들 중에서 골라내기!
	public ArrayList<String> selectPhotos(String srcFolderName, String destFolderName){
		ArrayList<File> selectedPhotoList = new ArrayList<File>();
		ArrayList<File> processedFileList = new ArrayList<File>();
		ArrayList<String> selectedPhotoPathList = new ArrayList<String>(); //return value
		
		File folder=new File(srcFolderName);
		File[] files=folder.listFiles();
		if (files == null) {
			System.out.println("IMAGE_EXTRACTOR :: There is no images taken at today.");
			return selectedPhotoPathList;
		}
		int numOfFiles=files.length;
		System.out.println("IMAGE_EXTRACTOR :: Extract " + numOfFiles+" images.");
		File file1=null, file2=null;
		double sim;
		boolean survived;
		int current, other;
		
		//System.out.println(this.getCosSimilarity(new File("C:\\img\\b.jpg"), new File("C:\\img\\c.jpg")));
		//System.out.println(this.getCosSimilarity(new File("C:\\img\\f.jpg"), new File("C:\\img\\g.jpg")));

		for(int i=0;i<files.length;i++){
			//System.out.println((i+1)+ "번째 photo!");
			current=i;
			file1=new File(srcFolderName+"/"+files[current].getName());
			survived=true;
			for(int j=1;j<=1;j++){
				other=current-j;
				if(other>-1){
					file2=new File(srcFolderName+"/"+files[other].getName());
					sim=this.getCosSimilarity(file1, file2);
					if (sim>0.8){
						survived=false;
						break;
					}
				}
			}
			if(survived) selectedPhotoList.add(file1);
			processedFileList.add(file1);
		}
		selectedPhotoPathList = copySelectedPhotos(selectedPhotoList, destFolderName);
		deletePhotos(processedFileList);
		 
		/*
		File destFolder=new File(destFolderName);
		for(File f: destFolder.listFiles()){
			AbsolutePathList.add(f.getAbsolutePath());
		}
		*/
		
		return selectedPhotoPathList;
	}
	
	public void deletePhotos(ArrayList<File> deleteFileList){
		for (File f : deleteFileList){
			try {
				f.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> copySelectedPhotos(ArrayList<File> selectedPhotoList, String destFolderName){
		ArrayList<String> copiedFilePath = new ArrayList<String>();
		for (File f : selectedPhotoList){
			String copyFilePath = destFolderName+"/"+f.getName();
			File dest = new File(copyFilePath);
			try {
				FileUtils.copyFile(f,dest);
				copiedFilePath.add(copyFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return copiedFilePath;
	}
	
	public double[] getHistogram(File imageFile){
		// 각 이미지들의 색상정보를 담는 벡터 (512가지 색의 조합)
		double [] vector=new double[512];
		
		BufferedImage image=null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
        //System.out.println(image.getWidth());
        //System.out.println(image.getHeight());
		double de=image.getWidth()*image.getHeight();
        
		// 실제 값을 저장 (0~255)
        int color,red,green,blue;
        
        // quantized 되는 값을 저장 (0~8) 
        int red_idx,green_idx,blue_idx;
        
        for (int x=0;x<image.getWidth();x++){
        	for (int y=0;y<image.getHeight();y++){
        		color=image.getRGB(x, y);
        		red=(color & 0x00ff0000) >> 16;
        		green=(color & 0x0000ff00) >> 8;
        		blue=(color & 0x000000ff);
        		
        		// quantize
        		red_idx=red/32;
        		green_idx=green/32;
        		blue_idx=blue/32;
        		
        		vector[64*red_idx+8*green_idx+blue_idx]+=1/de;
        	}
        }

		return vector;
	}
	
	public double getCosSimilarity(File file1, File file2){
		double[] vector1=getHistogram(file1);
		double[] vector2=getHistogram(file2);
		
		double innerProduct=0;		
		double sqSum1=0;
		double sqSum2=0;
		
		for (int i=0;i<vector1.length;i++){
			innerProduct+=vector1[i]*vector2[i];
			sqSum1+=vector1[i]*vector1[i];
			sqSum2+=vector2[i]*vector2[i];
		}
		double norm1=Math.sqrt(sqSum1);
		double norm2=Math.sqrt(sqSum2);
		
		//System.out.println("norm1 : "+norm1);
		double cosSim=innerProduct/(norm1*norm2);
		
		return cosSim;
	}
	
	public double getHistInterSimilarity(File file1, File file2){
		double[] vector1=getHistogram(file1);
		double[] vector2=getHistogram(file2);
		
		double sum1=0, sum2=0, minSum=0;
		for(int i=0;i<vector1.length;i++){
			sum1+=vector1[i];
			sum2+=vector2[i];
			if(vector1[i]<=vector2[i]){
				minSum+=vector1[i];
			}
			else{
				minSum+=vector2[i];
			}
		}
		
		if(sum1<=sum2){
			return minSum/sum1;
		}
		else{
			return minSum/sum2;
		}
	}
}
