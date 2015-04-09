package kr.ac.kaist.hrhrp.type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Image {
	private String url;
	private ArrayList<Person> persons = new ArrayList<Person>();
	private double[] gps = {36.370300, 127.361573};
	private Date imageTime;	
	private String city;
	private String district;
	private String street;
	private String buildingName;
	private String weather;
	private String imageOwnerId;
	private String groupName;
	private String path;
	
	private int colorH;
	private int colorS;
	private int colorV;
	
	private String objectId;

	public Image(String aUrl, String aImageownerId, String aGroupName) {
		url = aUrl;
		imageOwnerId = aImageownerId;
		groupName = aGroupName;
	}
	
	public static Image getDefaultImage() {
		Random r = new Random();
		int randomPicNum = r.nextInt(7 - 1) + 1;
		String defaulUrl = "http://dmserver4.kaist.ac.kr/~daehoon/hrhrp/photos/default/" + randomPicNum + ".jpg";
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
	
	public void setAddress(String city, String district, String street) {
		this.city = city;
		this.district = district;
		this.street = street;
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
	
	public void setColorH(int aColorH) {
		colorH = aColorH;
	}
	
	public void setColorS(int aColorS) {
		colorS = aColorS;
	}
	
	public void setColorV(int aColorV) {
		colorV = aColorV;
	}
	
	public void setObjectId(String aObjectId) {
		objectId = aObjectId;
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
	
	public String getCity() {
		return city;
	}
	
	public String getDistrict() {
		return district;
	}
	
	public String getStreet() {
		return street;
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
	
	public int getColorH() {
		return colorH;
	}
	
	public int getColorS() {
		return colorS;
	}
	
	public int getColorV() {
		return colorV;
	}
	
	public String getObjectId() {
		return objectId;
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
