package kr.ac.kaist.hrhrp.type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Image {
	private String url;
	private ArrayList<Person> persons = new ArrayList<Person>();
	private double[] gps = {36.370300, 127.361573};
	private Date imageTime;	
	private String address;
	private String buildingName;
	private String weather;
	private String imageOwnerId;
	private String groupName;
	private String path;

	public Image(String aUrl, String aImageownerId, String aGroupName) {
		url = aUrl;
		imageOwnerId = aImageownerId;
		groupName = aGroupName;
	}
	
	public static Image getDefaultImage() {
		String defaulUrl = "http://default.image.url";
		Image image = new Image(defaulUrl, null, null);
		return image;
	}

	public void setPath(String aPath) {
		path = aPath;
	}
	
	public void addPerson(Person aPerson) {
		persons.add(aPerson);
	}
	
	public void setPersons(ArrayList<Person> aPersons) {
		persons = aPersons;
	}

	public void setGPS(double lat, double lng) {
		gps[0] = lat;
		gps[1] = lng;
	}
	
	public void setImageTime(Date aImageTime) {
		imageTime = aImageTime;
	}
	
	public void setAddress(String mAddress) {
		address = mAddress;
	}
	
	public void setBuildingName(String aBuildingName) {
		buildingName = aBuildingName;
	}
	
	public void setWeather(String aWeather) {
		weather = aWeather;
	}
	
	public void setGroupName(String aGroupName) {
		groupName = aGroupName;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public String getImageOwnerId() {
		return imageOwnerId;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getBuildingName() {
		return buildingName;
	}
	
	public String getWeather() {
		return weather;
	}
	
	public ArrayList<Person> getPersons() {
		return persons;
	}
	
	public String getUrl() {
		return url;
	}
	
	public double[] getGPS() {
		return gps;
	}
	
	public Date getImageTime() {
		return imageTime;
	}
	
	public String getImageHour() {
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(imageTime);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		return String.valueOf(hours) + String.valueOf(minutes);
		*/
		
		String imageHour = new SimpleDateFormat("HHmm").format(imageTime);
		return imageHour;
		
	}
	
	public String getImageDate() {
		String imageDate = new SimpleDateFormat("yyyyMMdd").format(imageTime);
		return imageDate;
	}

}
