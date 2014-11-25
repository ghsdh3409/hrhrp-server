package kr.ac.kaist.hrhrp.type;

import java.util.ArrayList;

public class Image {
	private String url;
	private ArrayList<Person> persons = new ArrayList<Person>();
	private float[] gps;
	private long imageTime;	
	private String address;
	private String buildingName;
	private String weather;
	private String imageOwnerId;
	private String groupName;
	
	public Image(String aUrl, String aImageownerId, String aGroupName) {
		url = aUrl;
		imageOwnerId = aImageownerId;
		groupName = aGroupName;
	}

	public void setPersons(ArrayList<Person> aPersons) {
		persons = aPersons;
	}

	public void setGPS(float lat, float lng) {
		gps[0] = lat;
		gps[1] = lng;
	}
	
	public void setImageTime(long aImageTime) {
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
	
	public float[] getGPS() {
		return gps;
	}
	
	public long getImageTime() {
		return imageTime;
	}

}
