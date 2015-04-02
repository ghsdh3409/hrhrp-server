package kr.ac.kaist.hrhrp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import FeatureExtraction.StartFeature;
import weather.WeatherAPI;
import weather.WeatherInfo;
import kr.ac.kaist.hrhrp.FaceRecognition;
import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Face;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Init;
import kr.ac.kaist.hrhrp.type.Person;
import kr.ac.kaist.hrhrp.util.ObjectRecognizer;

public class Extractor extends Init {

	private DBHandler dbTemplate;

	private final int COMPLETE_STATE = 1;
	//private final int NEW_STATE = 0;
	//private final int ERROR_STATE = -1;

	public Extractor() {
		dbTemplate = new DBHandler();
	}

	private void close() {
		dbTemplate.close();
	}

	private void getInformation(Image image, String groupName) {

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

		updateAddress(image);

		try {
			WeatherInfo info = getExternalInfo(image);
			updateWeather(info, image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			int objectIdx = ObjectRecognizer.objectReconizer(image);
			updateObject(objectIdx, image);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			//TODO EXTRACT COLOR
			//int[] colorInfo = getColorInfo(image);
			//updateColor(colorInfo, image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		dbTemplate.updateImageState(image.getUrl(), COMPLETE_STATE);

	}

	private void updateObject(int objectIdx, Image image) {
		// TODO Auto-generated method stub
		dbTemplate.updateObjectInfo(objectIdx, image.getUrl());
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

	private ArrayList<Person> getNewRelations(String ownerId) {
		ArrayList<String> newPersonIds = new ArrayList<String>();
		newPersonIds = dbTemplate.selectNewPersonRelations(ownerId);

		ArrayList<Person> newPersons = new ArrayList<Person>();

		for (String newPersonId : newPersonIds) {
			Person newPerson = new Person(newPersonId, KEY_PERSON_ID);
			newPersons.add(newPerson);
		}

		return newPersons;
	}

	public void updateNewPersons(String ownerId, String personId, String personName) {	
		FaceRecognition fr = new FaceRecognition();
		Person person = fr.personUpdate(personId, personName);
		if (person != null) {
			if (personId.equals(person.getPersonId())){ // New Person
				System.out.println("NEW");
				dbTemplate.updatePersonName(person.getPersonName(), person.getPersonId());
			} else { // Existed User
				System.out.println("EXISTED");
				dbTemplate.updatePersonId(person.getPersonId(), personId);
			}
		}

	}

	public void updateNewRalations(String ownerId, String personId, String newRelation) {
		dbTemplate.updatePersonRelation(ownerId, personId, newRelation);
	}

	private ArrayList<Image> getNewImages(int num, String groupName) {
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

	private WeatherInfo getExternalInfo(Image image) {
		WeatherAPI wAPI = new WeatherAPI();

		WeatherInfo info = wAPI.getWeatherInfo(image.getImageDate(), image.getImageHour(), image.getGPS()[0], image.getGPS()[1]);
		return info;
	}

	private void updateWeather(WeatherInfo info, Image image) {
		String weather = info.HUMIDITY + "/" + info.SKY + "/" + info.RAINFALL + "/" + info.TEMPERATURE;
		dbTemplate.updateWeatherInfo(weather, image.getUrl());
	}

	private int[] getColorInfo(Image image) throws Exception {
			String filePath = image.getPath();
			StartFeature sf = new StartFeature();

			String[] tmpHSV = sf.startFromFile(filePath);

			int hsv[] = new int[3];
			hsv[0] = Integer.valueOf(tmpHSV[0].split("h;")[1]);
			hsv[1] = Integer.valueOf(tmpHSV[1].split("s;")[1]);
			hsv[2] = Integer.valueOf(tmpHSV[2].split("v;")[1]);

			return hsv;
		
	}

	private void updateColor(int[] colorInfo, Image image) {
		if (colorInfo != null) {
			dbTemplate.updateColorInfo(colorInfo, image.getUrl());
		}		
	}

	private void updateAddress(Image image) {

		double[] gps = image.getGPS();

		double lat = gps[0];
		double lng = gps[1];

		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=false&language=ko";
		String queryResult;

		queryResult = sendGet(url);

		if (queryResult != null) { 
			JSONObject jsonResult = new JSONObject(queryResult);

			if (jsonResult.getString("status").equals("OK")) {
				JSONArray results = jsonResult.getJSONArray("results");
				JSONObject result = results.getJSONObject(0);
				String formattedAddress = result.getString("formatted_address");
				String[] address = formattedAddress.split(" ");
				String city = address[1];
				String district = address[2];
				String street = address[3];

				dbTemplate.updateAddressInfo(city, district, street, image.getUrl());
			}

		}
	}

	private static String sendGet(String url) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void extractor() {
		// TODO Auto-generated method stub

		Extractor ex = new Extractor();
		String groupName = "HRHRP_Test";

		ArrayList<Image> newImages = ex.getNewImages(10, groupName);

		while (newImages.size() > 0) {
			for (Image image : newImages) {
				System.out.println(image.getUrl());
				ex.getInformation(image, groupName);	
			}
			newImages = ex.getNewImages(10, groupName);
		}
		//ex.updateNewPersons(imageOwnerId, groupName);
		//ex.updateNewRalations(imageOwnerId);

		ex.close();
		System.out.println("END");

	}
}
