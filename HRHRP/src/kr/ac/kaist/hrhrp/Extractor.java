package kr.ac.kaist.hrhrp;
import java.util.ArrayList;

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
		
		ArrayList<ArrayList<Person>> persons = faceRecogition(image);
		image.setPersons(persons);
		getExternalInfo(image);
		
		ArrayList<Person> recogPersons = image.getPersons().get(0);
		ArrayList<Person> newPersons = image.getPersons().get(1);
		
		for(Person person : recogPersons) {
			dbTemplate.insertImagePerson(image.getUrl(), person.getPersonId());
			dbTemplate.insertPersonRelation(image.getImageOwnerId(), person.getPersonId(), null);
		}
		
		for(Person person : newPersons) {
			dbTemplate.insertImagePerson(image.getUrl(), person.getPersonId());
			dbTemplate.insertPersonInfo(person.getPersonId(), null);
		}
				
		dbTemplate.updateExternalInfo(image.getUrl(), image.getWeather(), image.getAddress(), image.getBuildingName());
		dbTemplate.updateImageState(image.getUrl(), COMPLETE_STATE);
	}
	
	public ArrayList<Person> getNewPerson(String onwerId) {	
		ArrayList<Person> newPersons = new ArrayList<Person>();
		//TODO Get new person in pictures of ownerId
		
		return newPersons;
	}
	
	//TODO UPDATE PERSON NAME, RELATION
	public ArrayList<String> getNewImage(int num) {
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls = dbTemplate.selectNewImage(num);
		return imageUrls;
	}
	private void updatePerson(String groupName, String newPersonName) {
		FaceRecognition fr = new FaceRecognition(groupName);
		for (Person person : fr.getNewPerson()) {
			fr.personUpdate(person, newPersonName);
		}
	}
	
	private ArrayList<ArrayList<Person>> faceRecogition(Image image) {
		ArrayList<ArrayList<Person>> persons = new ArrayList<ArrayList<Person>>();
		FaceRecognition fr = new FaceRecognition(image.getGroupName());
		persons = fr.recognition(image.getUrl());
		return persons;
	}
	
	private void getExternalInfo(Image image) {
		// TO DO : get external information
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Extractor ex = new Extractor();
		String imageUrl = "http://img.tvreport.co.kr/images/20130807/20130807_1375853314_11589400_1.jpg";
		String groupName = "DaehoonKim_Test";
		String imageOwnerId = "DaehoonKim";
		
		ex.getInformation(imageUrl, groupName, imageOwnerId);			
	}
}
