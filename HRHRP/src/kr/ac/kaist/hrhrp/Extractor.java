package kr.ac.kaist.hrhrp;
import java.util.ArrayList;
import java.util.Scanner;

import weather.WeatherAPI;
import weather.WeatherAPI.WeatherInfo;
import kr.ac.kaist.hrhrp.FaceRecognition;
import kr.ac.kaist.hrhrp.db.DBWriter;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Person;

public class Extractor {

	private DBWriter dbTemplate;
	
	private final int COMPLETE_STATE = 1;
	private final int NEW_STATE = 0;
	private final int ERROR_STATE = -1;
	
	public Extractor() {
		dbTemplate = new DBWriter();
	}
	
	public void close() {
		dbTemplate.close();
	}
	
	public void getInformation(String imageUrl, String groupName, String imageOwnerId) {
		Image image = new Image(imageUrl, imageOwnerId, groupName);
		
		ArrayList<Person> recogPersons = faceRecogition(image);
		image.setPersons(recogPersons);
		
		//TODO getExternalInfo(image);
		
		System.out.println("Reconized Person List");
		
		for(Person person : recogPersons) {
			System.out.println(person.getPersonName() + "\t" + person.getPersonId());
			dbTemplate.insertPersonInfo(person.getPersonId(), person.getPersonName());
			dbTemplate.insertPersonRelation(image.getImageOwnerId(), person.getPersonId(), null);
			dbTemplate.insertImagePerson(image.getUrl(), person.getPersonId());
		}
	
		//TODO dbTemplate.updateExternalInfo(image.getUrl(), image.getWeather(), image.getAddress(), image.getBuildingName());
		//dbTemplate.updateImageState(image.getUrl(), COMPLETE_STATE);
	}
	
	public void updateNewPersons(String onwerId, String groupName) {	
		FaceRecognition fr = new FaceRecognition(groupName);
		Scanner scan = new Scanner(System.in);
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersons(onwerId);
		for (String newPersonId : newPersonIds) {
			
			System.out.print("Enter the name of Person " + newPersonId + " : ");
			String newPersonName;
			newPersonName = scan.nextLine();
							
			if (newPersonName.length() > 0) {
				Person person = fr.personUpdate(newPersonId, newPersonName);
				System.out.println(person.getPersonId());
				System.out.println(newPersonId);
				if (person != null) {
					if (newPersonId.equals(person.getPersonId())){ // New Person
						System.out.println("NEW");
						dbTemplate.updatePersonName(person.getPersonName(), person.getPersonId());
					} else { // Existed User
						System.out.println("EXISTED");
						dbTemplate.updatePersonId(person.getPersonId(), newPersonId);
					}
				}
			}
		}
		scan.close();
	}
	
	public void updateNewRalations(String ownerId) {
		Scanner scan = new Scanner(System.in);
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersonRelations(ownerId);
		for (String newPersonId : newPersonIds) {
			System.out.print("Enter the relation with Person " + newPersonId + " : ");
			String newRelation;
			newRelation = scan.nextLine();
			
			if (newRelation.length() > 0) {
				dbTemplate.updatePersonRelation(ownerId, newPersonId, newRelation);
			}
		}
		scan.close();
	}
	
	public ArrayList<String> getNewImage(int num) {
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls = dbTemplate.selectNewImage(num);
		return imageUrls;
	}
		
	private ArrayList<Person> faceRecogition(Image image) {
		ArrayList<Person> persons = new ArrayList<Person>();
		FaceRecognition fr = new FaceRecognition(image.getGroupName());
		persons = fr.recognition(image.getUrl());
		return persons;
	}
	
	public void getExternalInfo(Image image) {
		// TO DO : get external information
		
		WeatherAPI wAPI = new WeatherAPI();
		double lat = 36.370300;
		double lng = 127.361573;
		WeatherInfo testinfo = wAPI.getWeatherInfo("20141127", "20:02", lat, lng);
	}
	
	private void updateWeather(WeatherInfo info, Image image) {
		// TO DO:
		String weather = "";
		dbTemplate.updateWeatherInfo(weather, image.getUrl());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Extractor ex = new Extractor();
		String groupName = "HRHRP_Test";
		String imageOwnerId = "daehoonkim@kaist.ac.kr";
		
		ArrayList<String> newImageUrls = ex.getNewImage(10);
		
		for (String newImageUrl : newImageUrls) {
			System.out.println(newImageUrl);
			ex.getInformation(newImageUrl, groupName, imageOwnerId);	
		}
		
		//ex.updateNewPersons(imageOwnerId, groupName);
		//ex.updateNewRalations(imageOwnerId);
		
		ex.close();
		System.out.println("END");
	}
}
