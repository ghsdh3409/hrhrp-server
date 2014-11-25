package kr.ac.kaist.hrhrp;
import java.util.ArrayList;

import kr.ac.kaist.hrhrp.FaceRecognition;
import kr.ac.kaist.hrhrp.db.DBWriter;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Init;
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
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersons(onwerId);
		for (String newPersonId : newPersonIds) {
			System.out.println(newPersonId);
			//fr.personUpdate(newPersonId, "");
		}
	}
	
	//TODO UPDATE PERSON NAME, RELATION
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
	
	private void getExternalInfo(Image image) {
		// TO DO : get external information
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Extractor ex = new Extractor();
		String imageUrl = "http://img2.sbs.co.kr/img/sbs_cms/VD/2013/11/08/VD33114235_w656.jpg";
		String groupName = "HRHRP_Test";
		String imageOwnerId = "daehoonkim@kaist.ac.kr";
		
		//ex.getInformation(imageUrl, groupName, imageOwnerId);	
		ex.updateNewPersons(imageOwnerId, groupName);
		
		System.out.println("END");
	}
}
