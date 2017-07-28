package kr.ac.kaist.hrhrp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kr.ac.kaist.hrhrp.type.Face;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Init;
import kr.ac.kaist.hrhrp.type.Person;
import kr.ac.kaist.hrhrp.type.Quiz;
import kr.ac.kaist.hrhrp.type.Selection;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class DBHandler extends Init {

	private final String url = "jdbc:mysql://kse-dm2.kaist.ac.kr/hrhrp?user=mschoy&password=minsoo&autoReconnect=true";
	private final String className = "com.mysql.jdbc.Driver";

	private Connection conn = null;

	private final String SELECT_IMAGE_SQL = "SELECT * FROM Photo WHERE state = 0 LIMIT ?";
	private final String INSERT_IMAGE_INFO_SQL = "INSERT INTO Photo (url, path, taken_at, owner_id, lat, lng) VALUES (?, ?, ? ,?, ?, ?)";
	private final String UPDATE_IMAGE_WEATHER_INFO_SQL = "UPDATE Photo SET weather = ? WHERE url = ?";
	private final String UPDATE_IMAGE_ADDRESS_INFO_SQL = "UPDATE Photo SET city = ?, district = ?, street = ? WHERE url = ?";
	private final String UPDATE_IMAGE_COLOR_INFO_SQL = "UPDATE Photo SET color_H = ?, color_S = ?, color_V = ? WHERE url = ?";
	private final String UPDATE_IMAGE_OBJECT_INFO_SQL = "UPDATE Photo SET object_id = ? WHERE url = ?";
	private final String UPDATE_IMAGE_STATE_SQL = "UPDATE Photo SET state = ? WHERE url = ?";

	private final String DELETE_PERSON_INFO_SQL = "DELETE FROM Person WHERE person_id = ?";
	private final String INSERT_PERSON_INFO_SQL = "INSERT INTO Person (person_id, name) VALUES (?, ?)";
	private final String INSERT_PERSON_RELATION_SQL = "INSERT INTO PersonPerson (owner_id, person_id, relationship) VALUES (?, ?, ?)";
	private final String UPDATE_PERSON_NAME_SQL = "UPDATE Person SET name = ? WHERE person_id = ?";
	private final String SELECT_NEW_PERSON_SQL = "SELECT Person.person_id, Person.name, PersonPerson.relationship from PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id where owner_id = ? and (Person.name is NULL or PersonPerson.relationship is NULL)";
	private final String SELECT_NEW_PERSON_RELATION_SQL = "SELECT Person.person_id FROM PersonPerson INNER JOIN Person ON PersonPerson.person_id = Person.person_id WHERE owner_id = ? and PersonPerson.relationship is NULL";
	private final String UPDATE_PERSON_RELATION_SQL = "UPDATE PersonPerson SET relationship = ? WHERE owner_id = ? and person_id = ?";

	private final String DELETE_PHOTO_PERSON_INFO_SQL = "DELETE FROM PhotoPerson WHERE photo_id = ? and person_id = ?";
	private final String UPDATE_PHOTO_PERSON_BY_PHOTO_SQL = "UPDATE PhotoPerson SET person_id = ? WHERE person_id = ? and photo_id = ?";
	private final String UPDATE_PHOTO_PERSON_SQL = "UPDATE PhotoPerson SET person_id = ? WHERE person_id = ?";
	private final String UPDATE_PERSON_PERSON_BY_ONWER_PERSON_SQL = "UPDATE PersonPerson SET person_id = ? WHERE owner_id =? and person_id = ?";
	private final String SELECT_PERSON_PERSON_BY_PERSON_SQL = "SELECT * FROM PersonPerson WHERE person_id = ?";
	private final String SELECT_PERSON_PERSON_BY_OWNER_PERSON_SQL = "SELECT * FROM PersonPerson WHERE owner_id=? and person_id = ?";
	private final String DELETE_PERSON_PERSON_BY_OWNER_PERSON_SQL = "DELETE FROM PersonPerson WHERE owner_id = ? and person_id = ?";
	private final String INSERT_IMAGE_PERSON_SQL = "INSERT INTO PhotoPerson (photo_id, person_id, face_id, width, height, center_x, center_y, is_person_auto_detected) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private final String UPDATE_EXTERNAL_INFO_SQL = "UPDATE Photo SET weather = ?, address = ?, venue = ? WHERE url = ?";

	private final String SELECT_PHOTO_PERSON_BY_OWNER_ID_SQL = "SELECT PhotoPerson.* from PhotoPerson INNER JOIN Photo ON PhotoPerson.photo_id = Photo.url where Photo.owner_id = ? and PhotoPerson.person_id = ?  ORDER BY created_at DESC";
	private final String SELECT_PHOTO_PERSON_BY_PHOTO_PERSON_SQL = "SELECT * FROM PhotoPerson WHERE photo_id =? and person_id = ?";
	
	private final String INSERT_DIARY_SQL = "INSERT INTO Diary (owner_id, diary) VALUES(?, ?)";
	
	private final String SELECT_SOLVED_QUIZ_SQL = "SELECT Quiz.*," +
			" P5.person_id as 'quiz_person'," +
			" P1.person_id as 'selection1_person'," +
			" P2.person_id as 'selection2_person'," +
			" P3.person_id as 'selection3_person'," +
			" P4.person_id as 'selection4_person'," +
			" PE5.name as 'quiz_name'," +
			" PE1.name as 'selection1_name'," +
			" PE2.name as 'selection2_name'," +
			" PE3.name as 'selection3_name'," +
			" PE4.name as 'selection4_name'," +
			" Ph1.color_H as 'quiz_person_color_h', Ph1.color_S as 'quiz_person_color_s', Ph1.color_V as 'quiz_person_color_v'," +
			" Ph2.color_H as 'selection1_color_h', Ph2.color_S as 'selection1_color_s', Ph2.color_V as 'selection1_color_v'," +
			" Ph3.color_H as 'selection2_color_h', Ph3.color_S as 'selection2_color_s', Ph3.color_V as 'selection2_color_v'," +
			" Ph4.color_H as 'selection3_color_h', Ph4.color_S as 'selection3_color_s', Ph4.color_V as 'selection3_color_v'," +
			" Ph5.color_H as 'selection4_color_h', Ph5.color_S as 'selection4_color_s', Ph5.color_V as 'selection4_color_v'" +
			" FROM Quiz" +
			" LEFT OUTER JOIN PhotoPerson P1 ON selection1_face = P1.face_id" +
			" LEFT OUTER JOIN PhotoPerson P2 ON selection2_face = P2.face_id" +
			" LEFT OUTER JOIN PhotoPerson P3 ON selection3_face = P3.face_id" +
			" LEFT OUTER JOIN PhotoPerson P4 ON selection4_face = P4.face_id" +
			" LEFT OUTER JOIN PhotoPerson P5 ON quiz_face = P5.face_id" +
			" LEFT OUTER JOIN Person PE1 ON P1.person_id = PE1.person_id" +
			" LEFT OUTER JOIN Person PE2 ON P2.person_id = PE2.person_id" +
			" LEFT OUTER JOIN Person PE3 ON P3.person_id = PE3.person_id" +
			" LEFT OUTER JOIN Person PE4 ON P4.person_id = PE4.person_id" +
			" LEFT OUTER JOIN Person PE5 ON P5.person_id = PE5.person_id" +
			" LEFT OUTER JOIN Photo Ph1 ON quiz_image = Ph1.url" +
			" LEFT OUTER JOIN Photo Ph2 ON selection1 = Ph2.url" +
			" LEFT OUTER JOIN Photo Ph3 ON selection2 = Ph3.url" +
			" LEFT OUTER JOIN Photo Ph4 ON selection3 = Ph4.url" +
			" LEFT OUTER JOIN Photo Ph5 ON selection4 = Ph5.url" +
			" WHERE solved != 0 and solver_id = ?";
	private final String SELECT_SOLVED_QUIZ_CNT_SQL = "SELECT count(*) FROM Quiz Where solver_id = ? AND solved != 0";
	private final String SELECT_QUIZ_SQL = "SELECT Quiz.* FROM Quiz Where solver_id = ? AND solved = 0";
	private final String SELECT_QUIZ_RESULT_SQL = "SELECT * FROM Quiz WHERE solver_id=? AND DATE(createAt) = CURDATE()";
	private final String SELECT_FACE_SQL = "SELECT PhotoPerson.* FROM PhotoPerson Where face_id = ?";
	private final String UPDATE_QUIZ_SOLVED_SQL = "UPDATE Quiz SET solved = ? WHERE quiz_id = ?";

	private final String SELECT_PHOTO_BY_T1 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and Person.name is not null";

	private final String SELECT_PHOTO_BY_T2 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and PersonPerson.relationship is not null";

	private final String SELECT_PHOTO_BY_T3 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and Person.name is not null";

	private final String SELECT_PHOTO_BY_T4 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and PersonPerson.relationship is not null";

	private final String SELECT_PHOTO_BY_T5 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and DATE(Photo.taken_at) >= DATE_ADD(CURDATE(), INTERVAL -1 DAY)";

	private final String SELECT_PHOTO_BY_T5_NOT = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? and DATE(Photo.taken_at) < DATE_ADD(CURDATE(), INTERVAL -1 DAY)";
	
	private final String SELECT_PHOTO_BY_T10 =  "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? "
			+ "and DATE(Photo.taken_at) >= curdate() - INTERVAL DAYOFWEEK(curdate())+6 DAY AND DATE(Photo.taken_at) < curdate() - INTERVAL DAYOFWEEK(curdate())-1 DAY";
	
	private final String SELECT_PHOTO_BY_T10_NOT = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Photo.owner_id = ? "
			+ "and DATE(Photo.taken_at) < curdate() - INTERVAL DAYOFWEEK(curdate())+6 DAY";
	
	private final String SELECT_ALL_PHOTO_BY_T1 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Person.name is not null LIMIT 1000";

	private final String SELECT_ALL_PHOTO_BY_T2 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE PersonPerson.relationship is not null LIMIT 1000";

	private final String SELECT_ALL_PHOTO_BY_T3 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE Person.name is not null LIMIT 1000";

	private final String SELECT_ALL_PHOTO_BY_T4 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE PersonPerson.relationship is not null LIMIT 1000";

	private final String SELECT_ALL_PHOTO_BY_T5 = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE DATE(Photo.taken_at) >= DATE_ADD(CURDATE(), INTERVAL -1 DAY) LIMIT 1000";

	private final String SELECT_ALL_PHOTO_BY_T5_NOT = "SELECT Photo.owner_id, Photo.url as 'photo_id', Photo.weather, Photo.city, Photo.district, Photo.street, Photo.taken_at, PhotoPerson.person_id, PhotoPerson.face_id, PhotoPerson.width, PhotoPerson.height, PhotoPerson.center_x, PhotoPerson.center_y, Person.name, PersonPerson.relationship, Photo.color_H, Photo.color_S, Photo.color_V, Photo.object_id FROM Photo "
			+ "JOIN PhotoPerson ON PhotoPerson.photo_id = Photo.url "
			+ "JOIN Person ON Person.person_id = PhotoPerson.person_id "
			+ "JOIN PersonPerson ON Person.person_id = PersonPerson.person_id and Photo.owner_id = PersonPerson.owner_id "
			+ "WHERE DATE(Photo.taken_at) < DATE_ADD(CURDATE(), INTERVAL -1 DAY) LIMIT 1000";

	
	private final String getClassCodeSQL="SELECT class_code FROM Object WHERE code=?";

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

	public ArrayList<HashMap<String, Object>> getSolvedQuizByUsername(String ownerId) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_SOLVED_QUIZ_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			while(rs.next()) {
				HashMap<String, Object> result = new HashMap<String, Object>();
				for(int i=1; i<=columns; ++i){   
					result.put(md.getColumnLabel(i),rs.getObject(i));
				}
				list.add(result);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int getSolvedQuizCntByUsername(String ownerId) {
		int solvedQuizCnt = 0;
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_SOLVED_QUIZ_CNT_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				solvedQuizCnt = rs.getInt(1);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return solvedQuizCnt;
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
				Date takenAt = rs.getTimestamp("taken_at");

				Image image = new Image(imageUrl, ownerId, groupName);
				image.setPath(path);

				if (lat != null && lng != null) {
					image.setGPS(lat, lng);
				}
				if (weather != null) {
					image.setWeather(weather);
				}
				if (takenAt != null) {
					image.setImageTime(takenAt);
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

	public void updateAddressInfo(String city, String district, String street, String imageId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_IMAGE_ADDRESS_INFO_SQL);
			ps.setString(1, city);
			ps.setString(2, district);
			ps.setString(3, street);
			ps.setString(4, imageId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateColorInfo(int[] colorInfo, String imageId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_IMAGE_COLOR_INFO_SQL);
			ps.setInt(1, colorInfo[0]);
			ps.setInt(2, colorInfo[1]);
			ps.setInt(3, colorInfo[2]);
			ps.setString(4, imageId);
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

	public ArrayList<Face> selectFacesByPerson(String ownerId, Person aPerson) {
		ArrayList<Face> faces = new ArrayList<Face>();
		PreparedStatement ps;
		try {			
			ps = conn.prepareStatement(SELECT_PHOTO_PERSON_BY_OWNER_ID_SQL); //TODO ADD OWNER_ID TO WEHRE CLAUSE
			ps.setString(1, ownerId);
			ps.setString(2, aPerson.getPersonId());
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				String imageUrl = rs.getString("photo_id");
				String faceId = rs.getString("face_id");
				double width = rs.getDouble("width");
				double height = rs.getDouble("height");
				double center_x = rs.getDouble("center_x");
				double center_y = rs.getDouble("center_y");

				Face face = new Face();
				face.setFaceId(faceId);
				face.setImgUrl(imageUrl);
				face.setPosition(width, height, center_x, center_y);

				faces.add(face);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return faces;
	}

	public String selectFaceIdFromPhotoPerson(String photoId, String personId) {
		String faceId = null;
		PreparedStatement ps;
		try {			
			ps = conn.prepareStatement(SELECT_PHOTO_PERSON_BY_PHOTO_PERSON_SQL);
			ps.setString(1, photoId);
			ps.setString(2, personId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				faceId = rs.getString("face_id");
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return faceId;
	}

	public boolean selectIsPersonAutoDectected(String photoId, String personId) {
		boolean isPersonAutoDectected = true; //Safely update
		PreparedStatement ps;
		try {			
			ps = conn.prepareStatement(SELECT_PHOTO_PERSON_BY_PHOTO_PERSON_SQL);
			ps.setString(1, photoId);
			ps.setString(2, personId);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				if (rs.getInt("is_person_auto_detected") == 1) 
					isPersonAutoDectected = true;			
				else
					isPersonAutoDectected = false;	
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isPersonAutoDectected;
	}

	public ArrayList<Person> selectNewPersons(String ownerId) {
		ArrayList<Person> newPersons = new ArrayList<Person>();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(SELECT_NEW_PERSON_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String personId = rs.getString("person_id");
				String personName = rs.getString("name");
				String personRelation = rs.getString("relationship");

				Person person = new Person();
				person.setPersonId(personId);
				person.setPersonName(personName);
				person.setPersonRelation(personRelation);

				newPersons.add(person);
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

	public void insertPersonInfoWithUpdate(String correctPersonId, String correctPersonName)  {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_PERSON_INFO_SQL);
			ps.setString(1, correctPersonId);
			ps.setString(2, correctPersonName);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			try {
				ps = conn.prepareStatement(UPDATE_PERSON_NAME_SQL);
				ps.setString(1, correctPersonName);
				ps.setString(2, correctPersonId);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertPersonRelationWithUpdate(String ownerId, String correctPersonId, String relation)  {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_PERSON_RELATION_SQL);
			ps.setString(1, ownerId);
			ps.setString(2, correctPersonId);
			ps.setString(3, relation);
			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {
			try {
				ps = conn.prepareStatement(UPDATE_PERSON_RELATION_SQL);
				ps.setString(1, relation);
				ps.setString(2, ownerId);
				ps.setString(3, correctPersonId);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void updatePhotoPersonByPhoto(String photoId, String newId, String existedId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_PHOTO_PERSON_BY_PHOTO_SQL);
			ps.setString(1, newId);
			ps.setString(2, existedId);
			ps.setString(3, photoId);
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//Verify that there is a same key USER + PERSON ID

			ps = conn.prepareStatement(SELECT_PERSON_PERSON_BY_PERSON_SQL);
			ps.setString(1, deletedId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String ownerId = rs.getString("owner_id");
				String relationship = rs.getString("relationship");
				PreparedStatement ps1 = conn.prepareStatement(SELECT_PERSON_PERSON_BY_OWNER_PERSON_SQL);
				ps1.setString(1, ownerId);
				ps1.setString(2, existedId);
				ResultSet rs1 = ps1.executeQuery();
				if (rs1.first()) { // if existed, DELETE existed records
					String existedOwnerId = rs1.getString("owner_id");
					String existedPersonId = rs1.getString("person_id");
					if (relationship == null) {
						PreparedStatement ps2 = conn.prepareStatement(DELETE_PERSON_PERSON_BY_OWNER_PERSON_SQL);
						ps2.setString(1, existedOwnerId);
						ps2.setString(2, deletedId);
						ps2.executeUpdate();
						ps2.close();
					} else {
						PreparedStatement ps2 = conn.prepareStatement(DELETE_PERSON_PERSON_BY_OWNER_PERSON_SQL);
						ps2.setString(1, existedOwnerId);
						ps2.setString(2, existedPersonId);
						ps2.executeUpdate();
						ps2.close();

						PreparedStatement ps3 = conn.prepareStatement(UPDATE_PERSON_PERSON_BY_ONWER_PERSON_SQL); //then, UPDATE_PERSON_PERSON_SQL
						ps3.setString(1, existedId);
						ps3.setString(2, ownerId);
						ps3.setString(3, deletedId);
						ps3.executeUpdate();
						ps3.close();

					}			
				}
				ps1.close();
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ps = conn.prepareStatement(DELETE_PERSON_INFO_SQL);
			ps.setString(1, deletedId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deletePhotoPerson(String photoId, String personId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(DELETE_PHOTO_PERSON_INFO_SQL);
			ps.setString(1, photoId);
			ps.setString(2, personId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deletePerson(String personId) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(DELETE_PERSON_INFO_SQL);
			ps.setString(1, personId);
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

	public void insertImagePerson(String imageId, String personId, String faceId, double width, double height, double center_x, double center_y, int isPersonAutoDetected) {
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
			ps.setInt(8, isPersonAutoDetected);

			ps.executeUpdate();
			ps.close();
		} catch(MySQLIntegrityConstraintViolationException e) {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertImageInfo(String imageUrl, String imagePath, Date imageTime, String ownerId, double lat, double lng) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_IMAGE_INFO_SQL);
			ps.setString(1, imageUrl);
			ps.setString(2, imagePath);
			ps.setTimestamp(3, new java.sql.Timestamp(imageTime.getTime()));
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

	public String getObjectCode(int code) throws Exception{
		PreparedStatement ps = null;
		String classCode="";
		ps = conn.prepareStatement(this.getClassCodeSQL);
		ps.setInt(1, code);
		ResultSet rs=ps.executeQuery();
		while(rs.next()){
			classCode=rs.getString("class_code");
		}
		return classCode;

	}

	public ArrayList<Image> selectAllImagesByTemplateId(int templateType) {
		PreparedStatement ps = null;
		ArrayList<Image> images = new ArrayList<Image>();
		try {
			if (templateType == 1) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T1);
			} else if (templateType == 2) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T2);
			} else if (templateType == 3) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T3);
			} else if (templateType == 4) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T4);
			} else if (templateType == 5) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T5);
			} else if (templateType == -5) {
				ps = conn.prepareStatement(SELECT_ALL_PHOTO_BY_T5_NOT);
			}
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String url = rs.getString("photo_id");
				String ownerId = rs.getString("owner_id");
				String city = rs.getString("city");
				String district = rs.getString("district");
				String street = rs.getString("street");
				Date takenAt = rs.getTimestamp("taken_at");
				String weather = rs.getString("weather");
				int colorH = rs.getInt("color_H");
				int colorS = rs.getInt("color_S");
				int colorV = rs.getInt("color_V");
				int objectId = rs.getInt("object_id");
				String objectClassCode=null; 

				Image image = new Image(url, ownerId, GROUP_NAME);
				image.setAddress(city, district, street);
				image.setImageTime(takenAt);
				image.setWeather(weather);
				image.setColorH(colorH);
				image.setColorS(colorS);
				image.setColorV(colorV);

				if (objectId == 0) {
					objectClassCode="Null";
				} else {
					objectClassCode=getObjectCode(objectId);
				}
				image.setObjectId(objectClassCode);

				String personId = rs.getString("person_id");
				String name = rs.getString("name");
				String relationship = rs.getString("relationship");

				Person person = new Person();
				person.setPersonId(personId);
				person.setPersonName(name);
				person.setPersonRelation(relationship);

				String faceId = rs.getString("face_id");
				double width = rs.getDouble("width");
				double height = rs.getDouble("height");
				double center_x = rs.getDouble("center_x");
				double center_y = rs.getDouble("center_y");

				Face face = new Face();
				face.setFaceId(faceId);
				face.setPosition(width, height, center_x, center_y);

				person.addFace(face);
				image.addPerson(person);

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

	public ArrayList<Image> selectImagesByTemplateIdOwner(int templateType, String mOwnerId) {
		PreparedStatement ps = null;
		ArrayList<Image> images = new ArrayList<Image>();
		try {
			if (templateType == 1) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T1);
				ps.setString(1, mOwnerId);
			} else if (templateType == 2) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T2);
				ps.setString(1, mOwnerId);
			} else if (templateType == 3) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T3);
				ps.setString(1, mOwnerId);
			} else if (templateType == 4) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T4);
				ps.setString(1, mOwnerId);
			} else if (templateType == 5) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T5);
				ps.setString(1, mOwnerId);
			} else if (templateType == -5) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T5_NOT);
				ps.setString(1, mOwnerId);
			} else if (templateType == 10) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T10);
				ps.setString(1, mOwnerId);
			} else if (templateType == -10) {
				ps = conn.prepareStatement(SELECT_PHOTO_BY_T10_NOT);
				ps.setString(1, mOwnerId);
			}
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String url = rs.getString("photo_id");
				String ownerId = rs.getString("owner_id");
				String city = rs.getString("city");
				String district = rs.getString("district");
				String street = rs.getString("street");
				Date takenAt = rs.getTimestamp("taken_at");
				String weather = rs.getString("weather");
				int colorH = rs.getInt("color_H");
				int colorS = rs.getInt("color_S");
				int colorV = rs.getInt("color_V");
				int objectId = rs.getInt("object_id");
				String objectClassCode=null; 

				Image image = new Image(url, ownerId, GROUP_NAME);
				image.setAddress(city, district, street);
				image.setImageTime(takenAt);
				image.setWeather(weather);
				image.setColorH(colorH);
				image.setColorS(colorS);
				image.setColorV(colorV);

				if (objectId == 0) {
					objectClassCode="Null";
				} else {
					objectClassCode=getObjectCode(objectId);
				}
				image.setObjectId(objectClassCode);

				String personId = rs.getString("person_id");
				String name = rs.getString("name");
				String relationship = rs.getString("relationship");

				Person person = new Person();
				person.setPersonId(personId);
				person.setPersonName(name);
				person.setPersonRelation(relationship);

				String faceId = rs.getString("face_id");
				double width = rs.getDouble("width");
				double height = rs.getDouble("height");
				double center_x = rs.getDouble("center_x");
				double center_y = rs.getDouble("center_y");

				Face face = new Face();
				face.setFaceId(faceId);
				face.setPosition(width, height, center_x, center_y);

				person.addFace(face);
				image.addPerson(person);

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

	public Face selectFace(String aFaceId) {
		PreparedStatement ps;
		Face face = null;
		try {
			ps = conn.prepareStatement(SELECT_FACE_SQL);
			ps.setString(1, aFaceId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String faceId = rs.getString("face_id");
				String photoId = rs.getString("photo_id");

				double width = rs.getDouble("width");
				double height = rs.getDouble("height");
				double centerX = rs.getDouble("center_x");
				double centerY = rs.getDouble("center_y");

				face = new Face();		
				face.setFaceId(faceId);
				face.setImgUrl(photoId);
				face.setPosition(width, height, centerX, centerY);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return face;
	}	

	public ArrayList<Quiz> selectQuizResult(String ownerId) {
		PreparedStatement ps;
		ArrayList<Quiz> quizes = new ArrayList<Quiz>();
		try {
			ps = conn.prepareStatement(SELECT_QUIZ_RESULT_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				int quizId = rs.getInt("quiz_id");
				int templateId = rs.getInt("template_id");
				String solverId = rs.getString("solver_id");

				String quizText = rs.getString("quiz_text");
				String quizImage = rs.getString("quiz_image");
				String quizImageFace = rs.getString("quiz_face");
				Face quizFace = selectFace(quizImageFace);

				String selectionType = rs.getString("selection_type");		

				String selection1 = rs.getString("selection1");
				String selectionFace1 = rs.getString("selection1_face");
				Face selection1Face = selectFace(selectionFace1); 

				Selection sel1 = new Selection();
				sel1.setSelectionType(selectionType);
				sel1.setSelection(selection1);
				sel1.setSelectionFace(selection1Face);

				String selection2 = rs.getString("selection2");
				String selectionFace2 = rs.getString("selection2_face");
				Face selection2Face = selectFace(selectionFace2); 

				Selection sel2 = new Selection();
				sel2.setSelectionType(selectionType);
				sel2.setSelection(selection2);
				sel2.setSelectionFace(selection2Face);

				String selection3 = rs.getString("selection3");
				String selectionFace3 = rs.getString("selection3_face");
				Face selection3Face = selectFace(selectionFace3); 

				Selection sel3 = new Selection();
				sel3.setSelectionType(selectionType);
				sel3.setSelection(selection3);
				sel3.setSelectionFace(selection3Face);

				String selection4 = rs.getString("selection4");
				String selectionFace4 = rs.getString("selection4_face");
				Face selection4Face = selectFace(selectionFace4); 

				Selection sel4 = new Selection();
				sel4.setSelectionType(selectionType);
				sel4.setSelection(selection4);
				sel4.setSelectionFace(selection4Face);

				int answer = rs.getInt("answer");
				int solved = rs.getInt("solved");

				Quiz quiz = new Quiz();
				quiz.setQuizId(quizId);
				quiz.setTemplateId(templateId);
				quiz.setSolverId(solverId);
				quiz.setQuizText(quizText);
				quiz.setQuizImageUrl(quizImage);
				quiz.setQuizFace(quizFace);
				quiz.addSelection(sel1);
				quiz.addSelection(sel2);
				quiz.addSelection(sel3);
				quiz.addSelection(sel4);
				quiz.setAnswer(answer);
				quiz.setSolved(solved);
				quizes.add(quiz);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return quizes;
	}

	public ArrayList<Quiz> selectQuiz(String ownerId) {
		PreparedStatement ps;
		ArrayList<Quiz> quizes = new ArrayList<Quiz>();
		try {
			ps = conn.prepareStatement(SELECT_QUIZ_SQL);
			ps.setString(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				int quizId = rs.getInt("quiz_id");
				int templateId = rs.getInt("template_id");
				String solverId = rs.getString("solver_id");

				String quizText = rs.getString("quiz_text");
				String quizImage = rs.getString("quiz_image");
				String quizImageFace = rs.getString("quiz_face");
				Face quizFace = selectFace(quizImageFace);

				String selectionType = rs.getString("selection_type");		

				String selection1 = rs.getString("selection1");
				String selectionFace1 = rs.getString("selection1_face");
				Face selection1Face = selectFace(selectionFace1); 

				Selection sel1 = new Selection();
				sel1.setSelectionType(selectionType);
				sel1.setSelection(selection1);
				sel1.setSelectionFace(selection1Face);

				String selection2 = rs.getString("selection2");
				String selectionFace2 = rs.getString("selection2_face");
				Face selection2Face = selectFace(selectionFace2); 

				Selection sel2 = new Selection();
				sel2.setSelectionType(selectionType);
				sel2.setSelection(selection2);
				sel2.setSelectionFace(selection2Face);

				String selection3 = rs.getString("selection3");
				String selectionFace3 = rs.getString("selection3_face");
				Face selection3Face = selectFace(selectionFace3); 

				Selection sel3 = new Selection();
				sel3.setSelectionType(selectionType);
				sel3.setSelection(selection3);
				sel3.setSelectionFace(selection3Face);

				String selection4 = rs.getString("selection4");
				String selectionFace4 = rs.getString("selection4_face");
				Face selection4Face = selectFace(selectionFace4); 

				Selection sel4 = new Selection();
				sel4.setSelectionType(selectionType);
				sel4.setSelection(selection4);
				sel4.setSelectionFace(selection4Face);

				int answer = rs.getInt("answer");

				Quiz quiz = new Quiz();
				quiz.setQuizId(quizId);
				quiz.setTemplateId(templateId);
				quiz.setSolverId(solverId);
				quiz.setQuizText(quizText);
				quiz.setQuizImageUrl(quizImage);
				quiz.setQuizFace(quizFace);
				quiz.addSelection(sel1);
				quiz.addSelection(sel2);
				quiz.addSelection(sel3);
				quiz.addSelection(sel4);
				quiz.setAnswer(answer);
				quizes.add(quiz);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return quizes;
	}	

	public void updateQuizSolved(int quizId, int solved) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_QUIZ_SOLVED_SQL);
			ps.setInt(1, solved);
			ps.setInt(2, quizId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateObjectInfo(int objectIdx, String imageId) {
		// TODO Auto-generated method stub
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(UPDATE_IMAGE_OBJECT_INFO_SQL);
			ps.setInt(1, objectIdx);
			ps.setString(2, imageId);
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean uploadDiary(String user, String diary) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(INSERT_DIARY_SQL);
			ps.setString(1, user);
			ps.setString(2, diary);
			ps.executeUpdate();
			ps.close();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
