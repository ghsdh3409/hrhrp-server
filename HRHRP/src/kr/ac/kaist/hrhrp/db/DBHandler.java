package kr.ac.kaist.hrhrp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import kr.ac.kaist.hrhrp.type.Face;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Person;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DBHandler {
		
	private final String url = "jdbc:mysql://kse-dm2.kaist.ac.kr/hrhrp?user=mschoy&password=minsoo";
	private final String className = "com.mysql.jdbc.Driver";
	
	private Connection conn = null;
	
	private final String SELECT_IMAGE_SQL = "SELECT * FROM Photo WHERE state = 0 LIMIT ?";
	private final String INSERT_IMAGE_INFO_SQL = "INSERT INTO Photo (url, path, taken_at, owner_id, lat, lng) VALUES (?, ?, ? ,?, ?, ?)";
	private final String UPDATE_IMAGE_WEATHER_INFO_SQL = "UPDATE Photo SET weather = ? WHERE url = ?";
	private final String UPDATE_IMAGE_STATE_SQL = "UPDATE Photo SET state = ? WHERE url = ?";
	
	private final String DELETE_PERSON_INFO_SQL = "DELETE FROM Person WHERE person_id = ?";
	private final String INSERT_PERSON_INFO_SQL = "INSERT INTO Person (person_id, name) VALUES (?, ?)";
	private final String INSERT_PERSON_RELATION_SQL = "INSERT INTO PersonPerson (owner_id, person_id, relationship) VALUES (?, ?, ?)";
	private final String UPDATE_PERSON_NAME_SQL = "UPDATE Person SET name = ? WHERE person_id = ?";
	private final String SELECT_NEW_PERSON_SQL = "SELECT Person.person_id from PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id where owner_id = ? and Person.name is NULL";
	private final String SELECT_NEW_PERSON_RELATION_SQL = "SELECT Person.person_id FROM PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id WHERE owner_id = ? and PersonPerson.relationship is NULL";
	private final String UPDATE_PERSON_RELATION_SQL = "UPDATE PersonPerson SET relationship = ? WHERE owner_id = ? and person_id = ?";
	
	private final String UPDATE_PHOTO_PERSON_SQL = "UPDATE PhotoPerson SET person_id = ? WHERE person_id = ?";
	private final String INSERT_IMAGE_PERSON_SQL = "INSERT INTO PhotoPerson (photo_id, person_id, face_id, width, height, center_x, center_y) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private final String UPDATE_EXTERNAL_INFO_SQL = "UPDATE Photo SET weather = ?, address = ?, venue = ? WHERE url = ?";
	
	private final String SELECT_PHOTO_PERSON_SQL = "SELECT * FROM PhotoPerson WHERE person_id = ?";
	//TODO : Select face id from photo person
	
	
	public DBHandler() {
		init();
	}
	
	private void init() {
		try {
			Class.forName(className);
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		conn = null;
	}
	
	public ArrayList<Image> selectNewImages(int num, String groupName) {
		ArrayList<Image> images = new ArrayList<Image>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_IMAGE_SQL);
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				
				String imageUrl = rs.getString("url");
				String path = rs.getString("path");
				String ownerId = rs.getString("owner_id");
				Double lat = rs.getDouble("lat");
				Double lng = rs.getDouble("lng");
				String weather = rs.getString("weather");
				
				Image image = new Image(imageUrl, ownerId, groupName);
				image.setPath(path);
				if (lat != null && lng != null) {
					image.setGPS(lat, lng);
				}
				if (weather != null) {
					image.setWeather(weather);
				}
				
				images.add(image);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return images;
	}
	
	public void updateImageState(String imageId, int state) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_IMAGE_STATE_SQL);
			ps.setInt(1, state);
			ps.setString(2, imageId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePersonName(String personName, String personId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_PERSON_NAME_SQL);
			ps.setString(1, personName);
			ps.setString(2, personId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateWeatherInfo(String weather, String imageId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_IMAGE_WEATHER_INFO_SQL);
			ps.setString(1, weather);
			ps.setString(2, imageId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertPersonInfo(String personId, String personName) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_PERSON_INFO_SQL);
			ps.setString(1, personId);
			ps.setString(2, personName);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Person selectFacesPerson(String aPersonId) {
		Person person = new Person();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_PHOTO_PERSON_SQL);
			ps.setString(1, aPersonId);
			ResultSet rs = ps.executeQuery();
				
			while(rs.next()) {
				String imageUrl = rs.getString("photo_id");
				String personId = rs.getString("person_id");
				String faceId = rs.getString("face_id");
				double width = rs.getDouble("width");
				double height = rs.getDouble("height");
				double center_x = rs.getDouble("center_x");
				double center_y = rs.getDouble("center_y");
				
				person.setPersonId(personId);
				
				Face face = new Face();
				face.setFaceId(faceId);
				face.setImgUrl(imageUrl);
				face.setPosition(width, height, center_x, center_y);
				
				person.addFace(face);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return person;
	}
	
	public ArrayList<String> selectNewPersons(String ownerId) {
		ArrayList<String> newPersons = new ArrayList<String>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_NEW_PERSON_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String imageUrl = rs.getString(1);
				newPersons.add(imageUrl);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPersons;
	}
	
	public ArrayList<String> selectNewPersonRelations(String ownerId) {
		ArrayList<String> newPersons = new ArrayList<String>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_NEW_PERSON_RELATION_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String imageUrl = rs.getString(1);
				newPersons.add(imageUrl);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPersons;
	}
	
	public void updatePersonRelation(String ownerId, String personId, String relation) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_PERSON_RELATION_SQL);
			ps.setString(1, relation);
			ps.setString(2, ownerId);
			ps.setString(3, personId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePersonId(String existedId, String deletedId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_PHOTO_PERSON_SQL);
			ps.setString(1, existedId);
			ps.setString(2, deletedId);
			ps.executeUpdate();
			ps.close();
			
			ps = conn.prepareStatement(DELETE_PERSON_INFO_SQL);
			ps.setString(1, deletedId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertPersonRelation(String ownerId, String personId, String relation) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_PERSON_RELATION_SQL);
			ps.setString(1, ownerId);
			ps.setString(2, personId);
			ps.setString(3, relation);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertImagePerson(String imageId, String personId, String faceId, double width, double height, double center_x, double center_y) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_IMAGE_PERSON_SQL);
			
			ps.setString(1, imageId);
			ps.setString(2, personId);
			ps.setString(3, faceId);
			ps.setDouble(4, width);
			ps.setDouble(5, height);
			ps.setDouble(6, center_x);
			ps.setDouble(7, center_y);
			
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertImageInfo(String imageUrl, String imagePath, long imageTime, String ownerId, double lat, double lng) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_IMAGE_INFO_SQL);
			ps.setString(1, imageUrl);
			ps.setString(2, imagePath);
			ps.setLong(3, imageTime);
			ps.setString(4, ownerId);
			ps.setDouble(5, lat);
			ps.setDouble(6, lng);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateExternalInfo(String imageId, String weather, String address, String venue) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_EXTERNAL_INFO_SQL);
			ps.setString(1, weather);
			ps.setString(2, address);
			ps.setString(3, venue);
			ps.setString(4, imageId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
