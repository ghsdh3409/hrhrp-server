package kr.ac.kaist.hrhrp.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class JPEGExifExtraction {
	
	public static Date getEXIFDateInfo(String imgPath) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(new File(imgPath));
		 // obtain the Exif directory
        ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);

        // query the tag's value
        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        
        return date;
	}
}
