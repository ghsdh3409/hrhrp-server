package kr.ac.kaist.hrhrp;
import java.util.ArrayList;
import java.util.Scanner;

import weather.WeatherAPI;
import weather.WeatherInfo;
import kr.ac.kaist.hrhrp.FaceRecognition;
import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Face;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Init;
import kr.ac.kaist.hrhrp.type.Person;

public class Extractor extends Init {

	private DBHandler dbTemplate;
	
	private final int COMPLETE_STATE = 1;
	//private final int NEW_STATE = 0;
	//private final int ERROR_STATE = -1;
	
	public Extractor() {
		dbTemplate = new DBHandler();
	}
	
	public void close() {
		dbTemplate.close();
	}
	
	public void getInformation(Image image, String groupName) {
	
		ArrayList<Person> recogPersons = faceRecogition(image);
		image.setPersons(recogPersons);
		
		System.out.println("Reconized Person List");
		
		for(Person person : recogPersons) {
			ArrayList<Face> faces = person.getFaces();
			Face face = faces.get(faces.size()-1);
			System.out.println(person.getPersonName() + "\t" + person.getPersonId());
			dbTemplate.insertPersonInfo(person.getPersonId(), person.getPersonName());
			dbTemplate.insertPersonRelation(image.getImageOwnerId(), person.getPersonId(), null);
			dbTemplate.insertImagePerson(image.getUrl(), person.getPersonId(), face.getFaceId(), face.getPosition().getWidth(),
					face.getPosition().getHeight(), face.getPosition().getCenterX(), face.getPosition().getCenterY());
		}
	
		WeatherInfo info = getExternalInfo(image);
		updateWeather(info, image);

		dbTemplate.updateImageState(image.getUrl(), COMPLETE_STATE);
	}
	
	public ArrayList<Person> getNewPersons(String ownerId) {
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersons(ownerId);
		
		ArrayList<Person> newPersons = new ArrayList<Person>();
		
		for (String newPersonId : newPersonIds) {
			Person newPerson = dbTemplate.selectFacesPerson(newPersonId);
			newPersons.add(newPerson);
		}
		
		return newPersons;
	}
	
	public ArrayList<Person> getNewRelations(String ownerId) {
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersonRelations(ownerId);
		
		ArrayList<Person> newPersons = new ArrayList<Person>();
		
		for (String newPersonId : newPersonIds) {
			Person newPerson = new Person(newPersonId, KEY_PERSON_ID);
			newPersons.add(newPerson);
		}
		
		return newPersons;
	}
	
	public void updateNewPersons(String ownerId, String groupName) {	
		FaceRecognition fr = new FaceRecognition(groupName);
		Scanner scan = new Scanner(System.in);
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersons(ownerId);
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
	
	public ArrayList<Image> getNewImages(int num, String groupName) {
		ArrayList<Image> images = new ArrayList<Image>();
		images = dbTemplate.selectNewImages(num, groupName);
		return images;
	}
		
	private ArrayList<Person> faceRecogition(Image image) {
		ArrayList<Person> persons = new ArrayList<Person>();
		FaceRecognition fr = new FaceRecognition(image.getGroupName());
		persons = fr.recognition(image.getUrl());
		return persons;
	}
	
	public WeatherInfo getExternalInfo(Image image) {
		WeatherAPI wAPI = new WeatherAPI();
		WeatherInfo info = wAPI.getWeatherInfo(image.getImageDate(), image.getImageHour(), image.getGPS()[0], image.getGPS()[1]);
		return info;
	}
	
	private void updateWeather(WeatherInfo info, Image image) {
		String weather = info.HUMIDITY + "/" + info.SKY + "/" + info.RAINFALL + "/" + info.TEMPERATURE;
		dbTemplate.updateWeatherInfo(weather, image.getUrl());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Extractor ex = new Extractor();
		String groupName = "HRHRP_Test";
	
		ArrayList<Image> newImages = ex.getNewImages(10, groupName);
		
		for (Image image : newImages) {
			System.out.println(image.getUrl());
			ex.getInformation(image, groupName);	
		}
		
		//ex.updateNewPersons(imageOwnerId, groupName);
		//ex.updateNewRalations(imageOwnerId);
		
		ex.close();
		System.out.println("END");

	}
}
