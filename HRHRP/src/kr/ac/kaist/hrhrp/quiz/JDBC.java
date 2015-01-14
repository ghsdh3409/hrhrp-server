package kr.ac.kaist.hrhrp.quiz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
	Connection conn;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	
	String getTemplateSQL="SELECT template FROM Template WHERE template_id=?";
	String addQuizToDBSQL="INSERT INTO Quiz (template_id, solver_id, quiz_text, quiz_image, selection_type, selection1, selection2, selection3, selection4, answer, solved, quiz_face, selection1_face, selection2_face, selection3_face, selection4_face) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	String getRelationSQL="SELECT relationship FROM PersonPerson WHERE owner_id=? AND person_id=?";
	
	public JDBC(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String getTemplate(int templateID) throws Exception{
		String template="";
		pstmt=conn.prepareStatement(getTemplateSQL);
		pstmt.setInt(1, templateID);
		rs=pstmt.executeQuery();
		while(rs.next()){
			template=rs.getString("template");
		}
		return template;
	}
	
	String getRelationshipBtwn(String user_id, String person_id) throws Exception{
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
	
	void insertQuiz(int template_id, String solver_id, String quiz_text, String quiz_image, String selection_type, String[] selections, int answer_number, String quiz_face, String[] selections_faces) throws SQLException{
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
	
	public void setConnection(){
		try{
			String jdbcUrl="jdbc:mysql://143.248.91.79:3306/hrhrp?characterEncoding=euckr";
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
