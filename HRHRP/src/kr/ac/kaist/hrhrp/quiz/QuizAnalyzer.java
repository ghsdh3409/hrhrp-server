package kr.ac.kaist.hrhrp.quiz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class QuizAnalyzer {
	private JDBC jdbc;
	private String user_id;
	
	private String mARFFPath;
	
	public QuizAnalyzer(){
		jdbc=new JDBC();
		jdbc.setConnection();
	}
	
	public QuizAnalyzer(String arffPath){
		jdbc=new JDBC();
		jdbc.setConnection();
		
		mARFFPath = arffPath;
	}
	
	public void close() {
		if (jdbc != null) {
			jdbc.endConnection();
		}
	}
	
	public void updateQuizFeature(String userId) throws Exception {
		int template_id, quiz_id, isCorrect;
		String photo_id="", face_id="", person_id="", weather="", timeslot="", location="";
		String parent_person="", parent_weather="", parent_time="", parent_location="";
		HashMap<String,String> quiz, photoInfo;
		
		setUserID(userId);
		ArrayList<HashMap<String,String>> quizList=jdbc.getQuizOfUser(user_id);
		
		// 각 퀴즈마다
		for(int i=0;i<quizList.size();i++){
			quiz=quizList.get(i);
			quiz_id=Integer.parseInt(quiz.get("quiz_id"));
			template_id=Integer.parseInt(quiz.get("template_id"));
			
			// template1~template5: 사람과 관련된 질문!
			if(template_id>=1 && template_id<=5){
				if(template_id==1 || template_id==2 || template_id==5){
					photo_id=quiz.get("quiz_image");
					face_id=quiz.get("quiz_face");
				}
				else if(template_id==3 || template_id==4){
					photo_id=quiz.get("selection");
					face_id=quiz.get("selection_face");
				}
				person_id=jdbc.getPersonInPhoto(photo_id,face_id);
			}
			else{
				person_id="사람없음";
			}
			
			photoInfo=jdbc.getPhotoInfo(photo_id);
			// 날씨, 시간대, 장소
			weather=getWeather(photoInfo.get("weather"));
			timeslot=getTimeSlot(photoInfo.get("date"));
			location=photoInfo.get("street");
			
			// 정답인지 오답인지.
			if(quiz.get("answer").equals(quiz.get("solved"))) isCorrect=1;
			else isCorrect=0;
			
			// DB에 넣기!
			try{
				// level 3
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 3, person_id, weather, timeslot, location, isCorrect);
				
				// level 2
				if(template_id>=1 && template_id<=5){
					parent_person=jdbc.getRelationshipBtwn(user_id, person_id);
				}
				else{
					parent_person="사람없음";
				}
				parent_weather=jdbc.getParentFeature(weather,3,"날씨");
				parent_time=jdbc.getParentFeature(timeslot,3,"시간");
				parent_location=jdbc.getParentFeature(location,3,"장소");
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 2, parent_person, parent_weather, parent_time, parent_location, isCorrect);
				
				// level 1
				if(template_id>=1 && template_id<=5){
					parent_person="사람있음";
				}
				else{
					parent_person="사람없음";
				}
				parent_weather=jdbc.getParentFeature(parent_weather,2,"날씨");
				parent_time=jdbc.getParentFeature(parent_time,2,"시간");
				parent_location=jdbc.getParentFeature(parent_location,2,"장소");
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 1, parent_person, parent_weather, parent_time, parent_location, isCorrect);
			}
			catch(Exception e){
				// Insertion시 Primary Key 에러 날 수 있으므로.
				// Do Nothing!
			}
		}
	}
	
	// Quiz분석 및 QuizFeature 테이블 채우기
	public void analyzeQuiz(String solver_id) throws Exception{
		int template_id, quiz_id, isCorrect;
		String photo_id="", face_id="", person_id="", weather="", timeslot="", location="";
		String parent_person="", parent_weather="", parent_time="", parent_location="";
		HashMap<String,String> quiz, photoInfo;
		
		setUserID(solver_id);
		ArrayList<HashMap<String,String>> quizList=jdbc.getQuizOfUser(user_id);
		
		// 각 퀴즈마다
		for(int i=0;i<quizList.size();i++){
			quiz=quizList.get(i);
			quiz_id=Integer.parseInt(quiz.get("quiz_id"));
			template_id=Integer.parseInt(quiz.get("template_id"));
			
			// template1~template5: 사람과 관련된 질문!
			if(template_id>=1 && template_id<=5){
				if(template_id==1 || template_id==2 || template_id==5){
					photo_id=quiz.get("quiz_image");
					face_id=quiz.get("quiz_face");
				}
				else if(template_id==3 || template_id==4){
					photo_id=quiz.get("selection");
					face_id=quiz.get("selection_face");
				}
				person_id=jdbc.getPersonInPhoto(photo_id,face_id);
			}
			else{
				person_id="사람없음";
			}
			
			photoInfo=jdbc.getPhotoInfo(photo_id);
			// 날씨, 시간대, 장소
			weather=getWeather(photoInfo.get("weather"));
			timeslot=getTimeSlot(photoInfo.get("date"));
			location=photoInfo.get("street");
			
			// 정답인지 오답인지.
			if(quiz.get("answer").equals(quiz.get("solved"))) isCorrect=1;
			else isCorrect=0;
			
			// DB에 넣기!
			try{
				// level 3
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 3, person_id, weather, timeslot, location, isCorrect);
				
				// level 2
				if(template_id>=1 && template_id<=5){
					parent_person=jdbc.getRelationshipBtwn(user_id, person_id);
				}
				else{
					parent_person="사람없음";
				}
				parent_weather=jdbc.getParentFeature(weather,3,"날씨");
				parent_time=jdbc.getParentFeature(timeslot,3,"시간");
				parent_location=jdbc.getParentFeature(location,3,"장소");
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 2, parent_person, parent_weather, parent_time, parent_location, isCorrect);
				
				// level 1
				if(template_id>=1 && template_id<=5){
					parent_person="사람있음";
				}
				else{
					parent_person="사람없음";
				}
				parent_weather=jdbc.getParentFeature(parent_weather,2,"날씨");
				parent_time=jdbc.getParentFeature(parent_time,2,"시간");
				parent_location=jdbc.getParentFeature(parent_location,2,"장소");
				jdbc.insertQuizFeature(quiz_id, template_id, user_id, 1, parent_person, parent_weather, parent_time, parent_location, isCorrect);
			}
			catch(Exception e){
				// Insertion시 Primary Key 에러 날 수 있으므로.
				// Do Nothing!
			}
		}
		
		// 레벨별로 ARFF 파일 만들기
		prepareArffs();
	}
	
	// 날씨 feature
	String getWeather(String weather){
		String cloud=weather.split("/")[1];
		String rain=weather.split("/")[2];
		return cloud+"+"+rain;
	}
	
	// 시간대 feature
	String getTimeSlot(String date){
		int hour=Integer.parseInt(date.split(" ")[1].split(":")[0]);
		if(hour>=0 && hour<3) return "이른새벽";
		else if(hour>=3 && hour<6) return "늦은새벽";
		else if(hour>=6 && hour<9) return "이른아침";
		else if(hour>=9 && hour<12) return "아침";
		else if(hour>=12 && hour<15) return "점심";
		else if(hour>=15 && hour<18) return "낮";
		else if(hour>=18 && hour<21) return "저녁";
		else return "늦은밤";  // (hour>=21 && hour <24)
	}
	
	
	// arff파일들 각 level에 따라 다 만들기!
	public void prepareArffs() throws Exception{
		for(int level=1;level<=3;level++){
			createArffFile(level);
		}
	}
		
	// level에 해당하는 arff 파일 만들기!
	public void createArffFile(int level) throws Exception{
		File file=new File(mARFFPath + "/input_lev"+level+"_"+user_id+".arff");
		if(!file.exists()){
			file.createNewFile();
		}
		
		String content="";
		content+="@relation input-level"+level+"\n\n";
		content+="@attribute person {";
		content+=createArffAttributes(level, "사람");
		content+="@attribute weather {";
		content+=createArffAttributes(level, "날씨");
		content+="@attribute time {";
		content+=createArffAttributes(level, "시간");
		content+="@attribute location {";
		content+=createArffAttributes(level, "장소");
		
		content+="\n@data\n";
		content+=createArffData(level);
		
		// File에 쓰기
		BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		bw.write(content);
		bw.close();
		System.out.println("Level "+level+" - Input File Write Done! :D");
	}
		
	public String createArffAttributes(int level, String type) throws Exception{
		String content="";
		ArrayList<String> features = null;

		if(type.equals("사람")) features=jdbc.getDistinctPersonFeatures(user_id, level);
		if(type.equals("날씨")) features=jdbc.getDistinctWeatherFeatures(user_id, level);
		if(type.equals("시간")) features=jdbc.getDistinctTimeFeatures(user_id, level);
		if(type.equals("장소")) features=jdbc.getDistinctLocationFeatures(user_id, level);
		
		for(int i=0;i<features.size();i++){
			content+=features.get(i);
			if(i<features.size()-1) content+=", ";
		}
		content+="}\n";
		return content;
	}
	
	public String createArffData(int level) throws Exception{
		String content="";
		ArrayList<HashMap<String, String>> featureList=jdbc.getFeaturesForLevel(user_id, level);
		for(HashMap<String,String> features : featureList){
			content+=features.get("person")+","+features.get("weather")+","+features.get("time")+","+features.get("location")+"\n";
		}
		return content;
	}
	
	public void setUserID(String user_id){
		this.user_id=user_id;
	}
}
