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
	private final String addQuizFeatureToDBSQL="INSERT INTO QuizFeature (quiz_id, template_id, solver_id, level, person, weather, time, location, color_h, color_s, color_v, object, correct) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String getRelationSQL="SELECT relationship FROM PersonPerson WHERE owner_id=? AND person_id=?";
	private final String getParentFeatureSQL="SELECT parent FROM Tree WHERE feature=? and level=? and type=?";
	private final String getQuizOfUser="SELECT * FROM Quiz WHERE solver_id=? and solved != 0";
	private final String getPersonInPhotoSQL="SELECT person_id FROM PhotoPerson WHERE photo_id=? and face_id=?";
	private final String getPhotoInfoSQL="SELECT * FROM Photo WHERE url=?";
	
	private final String getDistinctPersonFeatureSQL="SELECT distinct(person) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctWeatherFeatureSQL="SELECT distinct(weather) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctTimeFeatureSQL="SELECT distinct(time) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctLocationFeatureSQL="SELECT distinct(location) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctColorHFeatureSQL="SELECT distinct(color_h) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctColorSFeatureSQL="SELECT distinct(color_s) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctColorVFeatureSQL="SELECT distinct(color_v) FROM QuizFeature WHERE solver_id=? and level=?";
	private final String getDistinctObjectFeatureSQL="SELECT distinct(object) FROM QuizFeature WHERE solver_id=? and level=?";
	
	private final String getFeaturesForLevelSQL="SELECT * FROM QuizFeature WHERE solver_id=? and level=?";
	
	private final String getNumberOfPatternSQL="SELECT count(*) as cnt FROM QuizFeature WHERE solver_id=? and person=? and weather=? and time=? and location=? and color_h=? and color_s=? and color_v=? and object=?";
	private final String getWrongNumberOfPatternSQL="SELECT count(*) as cnt FROM QuizFeature WHERE solver_id=? and person=? and weather=? and time=? and location=? and color_h=? and color_s=? and color_v=? and object=? and correct=0";
	
	private final String getCorrectInfoSQL="SELECT correct FROM QuizFeature WHERE solver_id=? and template_id=?";
	private final String getClassCodeSQL="SELECT class_code FROM Object WHERE code=?";
	
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
		String color_h=quiz.get("color_h");
		String color_s=quiz.get("color_s");
		String color_v=quiz.get("color_v");
		String object=quiz.get("object");
		
		pstmt=conn.prepareStatement(getNumberOfPatternSQL);
		pstmt.setString(1, solver_id);
		pstmt.setString(2, person);
		pstmt.setString(3, weather);
		pstmt.setString(4, time);
		pstmt.setString(5, location);
		pstmt.setString(6, color_h);
		pstmt.setString(7, color_s);
		pstmt.setString(8, color_v);
		pstmt.setString(9, object);
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
		String color_h=quiz.get("color_h");
		String color_s=quiz.get("color_s");
		String color_v=quiz.get("color_v");
		String object=quiz.get("object");
		
		pstmt=conn.prepareStatement(getWrongNumberOfPatternSQL);
		pstmt.setString(1, solver_id);
		pstmt.setString(2, person);
		pstmt.setString(3, weather);
		pstmt.setString(4, time);
		pstmt.setString(5, location);
		pstmt.setString(6, color_h);
		pstmt.setString(7, color_s);
		pstmt.setString(8, color_v);
		pstmt.setString(9, object);
		
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
		int object;
		while(rs.next()){
			info.put("date",rs.getString("taken_at"));
			info.put("weather", rs.getString("weather"));
			info.put("street", rs.getString("street"));
			info.put("color_h", ""+rs.getInt("color_H"));
			info.put("color_s", ""+rs.getInt("color_S"));
			info.put("color_v", ""+rs.getInt("color_V"));
			object=rs.getInt("object_id");
			if(object==0){
				info.put("object", "Null");
			} else{
				String classCode=getObjectCode(object);
				if(classCode!=null){
					info.put("object", classCode);
				} else{
					info.put("object", "Null");
				}
			}
		}
		return info;
	}
	
	public String getObjectCode(int code) throws Exception{
		String classCode="";
		pstmt=conn.prepareStatement(this.getClassCodeSQL);
		pstmt.setInt(1, code);
		rs=pstmt.executeQuery();
		while(rs.next()){
			classCode=rs.getString("class_code");
		}
		return classCode;
		
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
	
	// 특정 level에 distinct한 color h들
	public ArrayList<String> getDistinctColorHFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctColorHFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("color_h"));
		}
		return features;
	}
		
	// 특정 level에 distinct한 color s들
	public ArrayList<String> getDistinctColorSFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctColorSFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("color_s"));
		}
		return features;
	}
	
	// 특정 level에 distinct한 color v들
	public ArrayList<String> getDistinctColorVFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctColorVFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("color_v"));
		}
		return features;
	}
	
	// 특정 level에 distinct한 object들
	public ArrayList<String> getDistinctObjectFeatures(String solver_id, int level) throws Exception{
		ArrayList<String> features=new ArrayList<String>();
		pstmt=conn.prepareStatement(this.getDistinctObjectFeatureSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, level);
		rs=pstmt.executeQuery();
		while(rs.next()){
			features.add(rs.getString("object"));
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
			featureMap.put("color_h", rs.getString("color_h"));
			featureMap.put("color_s", rs.getString("color_s"));
			featureMap.put("color_v", rs.getString("color_v"));
			featureMap.put("object", rs.getString("object"));
			featureList.add(featureMap);
		}
		return featureList;
	}
	
	public String getPersonInPhoto(String photo_id, String face_id) throws Exception{
		String person_id=null;
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
	
	public void insertQuizFeature(int quiz_id, int template_id, String solver_id, int level, String person, String weather, String time, String location, String h, String s, String v, String object, int correct) throws SQLException{
		pstmt=conn.prepareStatement(addQuizFeatureToDBSQL);
		pstmt.setInt(1, quiz_id);
		pstmt.setInt(2, template_id);
		pstmt.setString(3, solver_id);
		pstmt.setInt(4, level);
		pstmt.setString(5, person);
		pstmt.setString(6, weather);
		pstmt.setString(7, time);
		pstmt.setString(8, location);
		pstmt.setString(9, h);
		pstmt.setString(10, s);
		pstmt.setString(11, v);
		pstmt.setString(12, object);
		pstmt.setInt(13, correct);
		pstmt.executeUpdate();
	}
	
	public ArrayList<Integer> getCorrectInfoOfTemplateID(String solver_id, int template_id) throws Exception{
		ArrayList<Integer> correctInfoList=new ArrayList<Integer>();
		pstmt=conn.prepareStatement(getCorrectInfoSQL);
		pstmt.setString(1, solver_id);
		pstmt.setInt(2, template_id);
		rs=pstmt.executeQuery();
		while(rs.next()){
			correctInfoList.add(rs.getInt("correct"));
		}
		return correctInfoList;
	}
	
	public void setConnection(){
		try{
			String jdbcUrl="jdbc:mysql://143.248.91.79:3306/hrhrp?characterEncoding=euckr&autoReconnect=true";
			String userId="mschoy";
			String userPass="minsoo";
			conn=DriverManager.getConnection(jdbcUrl,userId,userPass);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void endConnection(){
		try{
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
