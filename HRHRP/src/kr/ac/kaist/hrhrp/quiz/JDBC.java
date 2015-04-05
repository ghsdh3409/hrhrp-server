package kr.ac.kaist.hrhrp.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class JDBC {
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private final String getPhotoCntByUsernameSQL = "SELECT count(*) FROM Photo WHERE owner_id=?"; //ADDED BY DAEHOONKIM for verifying if existing photo
	private final String getTemplateSQL="SELECT template FROM Template WHERE template_id=?";
	private final String addQuizToDBSQL="INSERT INTO Quiz (template_id, solver_id, quiz_text, quiz_image, selection_type, selection1, selection2, selection3, selection4, answer, solved, quiz_face, selection1_face, selection2_face, selection3_face, selection4_face) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String addQuizFeatureToDBSQL="INSERT INTO QuizFeature (quiz_id, solver_id, level, person, weather, time, location, correct) VALUES (?,?,?,?,?,?,?,?)";
	private final String getRelationSQL="SELECT relationship FROM PersonPerson WHERE owner_id=? AND person_id=?";
	private final String getParentFeatureSQL="SELECT parent FROM Tree WHERE feature=? and level=? and type=?";
	private final String getQuizOfUser="SELECT * FROM Quiz WHERE solver_id=? and solved != 0";
	private final String getPersonInPhotoSQL="SELECT person_id FROM PhotoPerson WHERE photo_id=? and face_id=?";
	private final String getPhotoInfoSQL="SELECT taken_at,weather,city,district,street FROM Photo WHERE url=?";
	
	private final String getDistinctPersonFeatureSQL="SELECT distinct(person) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctWeatherFeatureSQL="SELECT distinct(weather) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctTimeFeatureSQL="SELECT distinct(time) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctLocationFeatureSQL="SELECT distinct(location) FROM QuizFeature WHERE solver_id=? and level=?";
	
	private final String getFeaturesForLevelSQL="SELECT person, weather, time, location FROM QuizFeature WHERE solver_id=? and level=?";
	
	private final String getNumberOfPatternSQL="SELECT count(*) as cnt FROM QuizFeature WHERE solver_id=? and person=? and weather=? and time=? and location=?";
	private final String getWrongNumberOfPatternSQL="SELECT count(*) as cnt FROM QuizFeature WHERE solver_id=? and person=? and weather=? and time=? and location=? and correct=0";
	
	public JDBC(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getPhotoCntByUsername(String solver_id) throws SQLException {
		int cnt = 0;
		
		pstmt=conn.prepareStatement(getPhotoCntByUsernameSQL);
		pstmt.setString(1, solver_id);
		rs=pstmt.executeQuery();
		while(rs.next()){
			cnt=rs.getInt(1);
		}
		return cnt;
		
	}
	
	public int getNumberOfPattern(String solver_id, HashMap<String,String> quiz) throws Exception{
		int cnt=0;
		String person=quiz.get("person");
		String weather=quiz.get("weather");
		String time=quiz.get("time");
		String location=quiz.get("location");
		
		pstmt=conn.prepareStatement(getNumberOfPatternSQL);
		pstmt.setString(1, solver_id);
		pstmt.setString(2, person);
		pstmt.setString(3, weather);
		pstmt.setString(4, time);
		pstmt.setString(5, location);
		rs=pstmt.executeQuery();
		while(rs.next()){
			cnt=rs.getInt("cnt");
		}
		return cnt;
	}
	
	public int getWrongNumberOfPattern(String solver_id, HashMap<String,String> quiz) throws Exception{
		int cnt=0;
		String person=quiz.get("person");
		String weather=quiz.get("weather");
		String time=quiz.get("time");
		String location=quiz.get("location");
		
		pstmt=conn.prepareStatement(getWrongNumberOfPatternSQL);
		pstmt.setString(1, solver_id);
		pstmt.setString(2, person);
		pstmt.setString(3, weather);
		pstmt.setString(4, time);
		pstmt.setString(5, location);
		rs=pstmt.executeQuery();
		while(rs.next()){
			cnt=rs.getInt("cnt");
		}
		return cnt;
	}
	
	// ���� ����
	public HashMap<String,String> getPhotoInfo(String photo_id) throws Exception{
		HashMap<String,String> info=new HashMap<String,String>();
		pstmt=conn.prepareStatement(getPhotoInfoSQL);
		pstmt.setString(1, photo_id);
		rs=pstmt.executeQuery();
		while(rs.next()){
			info.put("date",rs.getString("taken_at"));
			info.put("weather", rs.getString("weather"));
			info.put("street", rs.getString("street"));
		}
		return info;
	}
		
	// Ư�� level�� distinct�� �����
	public ArrayList<String> getDistinctPersonFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctPersonFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("person"));
		}
		return features;
	}
	
	// Ư�� level�� distinct�� ������
	public ArrayList<String> getDistinctWeatherFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctWeatherFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("weather"));
		}
		return features;
	}
	
	// Ư�� level�� distinct�� �ð���
	public ArrayList<String> getDistinctTimeFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctTimeFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("time"));
		}
		return features;
	}
	
	// Ư�� level�� distinct�� ��ҵ�
	public ArrayList<String> getDistinctLocationFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctLocationFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("location"));
		}
		return features;
	}
		
	// Ư�� level�� arff ������ ä�� �����͵�
	public ArrayList<HashMap<String,String>> getFeaturesForLevel(String solver_id, int level) throws Exception{
		ArrayList<HashMap<String,String>> featureList=new ArrayList<HashMap<String,String>>();
		pstmt=conn.prepareStatement(this.getFeaturesForLevelSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			HashMap<String,String> featureMap=new HashMap<String,String>();
			featureMap.put("person", rs.getString("person"));
			featureMap.put("weather", rs.getString("weather"));
			featureMap.put("time", rs.getString("time"));
			featureMap.put("location", rs.getString("location"));
			featureList.add(featureMap);
		}
		return featureList;
	}
	
	public String getPersonInPhoto(String photo_id, String face_id) throws Exception{
		String person_id="";
		pstmt=conn.prepareStatement(getPersonInPhotoSQL);
		pstmt.setString(1, photo_id);
		pstmt.setString(2, face_id);
		rs=pstmt.executeQuery();
		while(rs.next()){
			person_id=rs.getString("person_id");
		}
		return person_id;
	}
	
	// user_id�� Ǯ���� �����
	public ArrayList<HashMap<String,String>> getQuizOfUser(String userID) throws Exception{
		ArrayList<HashMap<String,String>> quizList=new ArrayList<HashMap<String,String>>();
		String answer;
		pstmt=conn.prepareStatement(getQuizOfUser);
		pstmt.setString(1, userID);
		rs=pstmt.executeQuery();
		while(rs.next()){
			HashMap<String,String> quiz=new HashMap<String,String>();
			quiz.put("quiz_id", rs.getString("quiz_id"));
			quiz.put("template_id", rs.getString("template_id"));
			quiz.put("quiz_face", rs.getString("quiz_face"));
			quiz.put("quiz_image", rs.getString("quiz_image"));
			answer= rs.getString("answer");
			quiz.put("selection", rs.getString("selection"+answer));
			quiz.put("selection_face", rs.getString("selection"+answer+"_face"));
			quiz.put("answer", rs.getString("answer"));
			quiz.put("solved", rs.getString("solved"));
			quizList.add(quiz);
		}
		return quizList;
	}
	
	public String getParentFeature(String feature, int level, String type) throws Exception{
		String parent="";
		pstmt=conn.prepareStatement(getParentFeatureSQL);
		pstmt.setString(1, feature);
		pstmt.setInt(2, level);
		pstmt.setString(3, type);
		rs=pstmt.executeQuery();
		while(rs.next()){
			parent=rs.getString("parent");
		}
		return parent;
	}
	
	public String getTemplate(int templateID) throws Exception{
		String template="";
		pstmt=conn.prepareStatement(getTemplateSQL);
		pstmt.setInt(1, templateID);
		rs=pstmt.executeQuery();
		while(rs.next()){
			template=rs.getString("template");
		}
		return template;
	}
	
	public String getRelationshipBtwn(String user_id, String person_id) throws Exception{
		String relationship="";
		pstmt=conn.prepareStatement(getRelationSQL);
		pstmt.setString(1, user_id);
		pstmt.setString(2, person_id);
		rs=pstmt.executeQuery();
		while(rs.next()){
			relationship=rs.getString("relationship");
		}
		return relationship;
	}
	
	public void insertQuiz(int template_id, String solver_id, String quiz_text, String quiz_image, String selection_type, String[] selections, int answer_number, String quiz_face, String[] selections_faces) throws SQLException{
		pstmt=conn.prepareStatement(addQuizToDBSQL);
		pstmt.setInt(1, template_id);
		pstmt.setString(2, solver_id);
		pstmt.setString(3, quiz_text);
		pstmt.setString(4, quiz_image);
		pstmt.setString(5, selection_type);
		pstmt.setString(6, selections[0]);
		pstmt.setString(7, selections[1]);
		pstmt.setString(8, selections[2]);
		pstmt.setString(9, selections[3]);
		pstmt.setInt(10, answer_number);
		pstmt.setInt(11, 0);
		pstmt.setString(12, quiz_face);
		pstmt.setString(13, selections_faces[0]);
		pstmt.setString(14, selections_faces[1]);
		pstmt.setString(15, selections_faces[2]);
		pstmt.setString(16, selections_faces[3]);
		pstmt.executeUpdate();
	}
	
	public void insertQuizFeature(int quiz_id, String solver_id, int level, String person, String weather, String time, String location, int correct) throws SQLException{
		pstmt=conn.prepareStatement(addQuizFeatureToDBSQL);
		pstmt.setInt(1, quiz_id);
		pstmt.setString(2, solver_id);
		pstmt.setInt(3, level);
		pstmt.setString(4, person);
		pstmt.setString(5, weather);
		pstmt.setString(6, time);
		pstmt.setString(7, location);
		pstmt.setInt(8, correct);
		pstmt.executeUpdate();
	}
	
	public void setConnection(){
		try{
			String jdbcUrl="jdbc:mysql://143.248.91.79:3306/hrhrp?characterEncoding=euckr&autoReconnection=true";
			String userId="mschoy";
			String userPass="minsoo";
			conn=DriverManager.getConnection(jdbcUrl,userId,userPass);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void endConnection(){
		try{
			rs.close();
			stmt.close();
			pstmt.close();
			conn.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
