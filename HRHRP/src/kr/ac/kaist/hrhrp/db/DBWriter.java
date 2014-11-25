package kr.ac.kaist.hrhrp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DBWriter {
		
	private final String url = "jdbc:mysql://kse-dm2.kaist.ac.kr/hrhrp?user=mschoy&password=minsoo";
	private final String className = "com.mysql.jdbc.Driver";
	
	private Connection conn = null;
	
	private final String SELECT_IMAGE_SQL = "SELECT path FROM Photo WHERE state = 0 LIMIT ?";
	private final String INSERT_IMAGE_INFO_SQL = "INSERT INTO Photo (path, taken_at, latitude, longitude, owner_id) VALUES (?, ?, ? ,?, ?)";
	private final String UPDATE_IMAGE_STATE_SQL = "UPDATE Photo SET state = ? WHERE path = ?";
	
	private final String INSERT_PERSON_INFO_SQL = "INSERT INTO Person (person_id, name) VALUES (?, ?)";
	private final String INSERT_PERSON_RELATION_SQL = "INSERT INTO PersonPerson (owner_id, person_id, relationship) VALUES (?, ?, ?)";
	private final String UPDATE_PERSON_NAME_SQL = "UPDATE Person SET name = ? WHERE person_id = ?";
	private final String SELECT_NEW_PERSON_SQL = "SELECT Person.person_id from PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id where owner_id = ? and Person.name is NULL";
	private final String SELECT_NEW_PERSON_RELATION_SQL = "SELECT Person.person_id FROM PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id WHERE owner_id = ? and PersonPerson.relationship is NULL";
	private final String UPDATE_PERSON_RELATION_SQL = "UPDATE PersonPerson SET relationship = ? WHERE owner_id = ? and person_id = ?";
	
	private final String INSERT_IMAGE_PERSON_SQL = "INSERT INTO PhotoPerson (photo_id, person_id) VALUES (?, ?)";
	private final String UPDATE_EXTERNAL_INFO_SQL = "UPDATE Photo SET weather = ?, address = ?, venue = ? WHERE path = ?";
	
	
	public DBWriter() {
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
	
	public ArrayList<String> selectNewImage(int num) {
		ArrayList<String> imageUrls = new ArrayList<String>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_IMAGE_SQL);
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String imageUrl = rs.getString(1);
				imageUrls.add(imageUrl);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageUrls;
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
		// TODO update Person ID (Delete personId in Person, Update PhotoPerson)
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
	
	public void insertImagePerson(String imageId, String personId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_IMAGE_PERSON_SQL);
			ps.setString(1, imageId);
			ps.setString(2, personId);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void insertImageInfo(String imageId, long imageTime, float lat, float lng, String ownerId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_IMAGE_INFO_SQL);
			ps.setString(1, imageId);
			ps.setLong(2, imageTime);
			ps.setFloat(3, lat);
			ps.setFloat(4, lng);
			ps.setString(5, ownerId);
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
