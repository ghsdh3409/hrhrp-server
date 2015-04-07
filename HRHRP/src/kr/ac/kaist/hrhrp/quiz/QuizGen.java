package kr.ac.kaist.hrhrp.quiz;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import kr.ac.kaist.hrhrp.type.Image;

public class QuizGen {
	private JDBC jdbc;
	private ArrayList<String> default_name=new ArrayList<String>();
	private ArrayList<String> default_relation=new ArrayList<String>();

	private final int SUPPORTED_TEMPLATE_NUM = 5; 
	//private final String ARFF_PATH = "D:/arff/";
	private final String ARFF_PATH = "/home/daehoon/HRHRP/personalized/arff/";
	// 생성자
	public QuizGen(){
		jdbc=new JDBC();
		jdbc.setConnection();

		default_name.add("이재길");
		default_name.add("이유리");
		default_name.add("박신혜");
		default_name.add("이민호");

		default_relation.add("아버지");
		default_relation.add("어머니");
		default_relation.add("친구");
		default_relation.add("지도교수");
	}

	// 어떤 템플릿의 퀴즈를 출제할 지 결정
	private int selectTemplateID(String userId, int numOfQuiz, ArrayList<Integer> templateDistributionList, boolean isPersonalized) {
		if (isPersonalized && templateDistributionList.size() > 0) {
			Collections.shuffle(templateDistributionList);
			return templateDistributionList.get(0);
		} else {
			// 랜덤 숫자 생성하도록!
			return genRandNumber(1,SUPPORTED_TEMPLATE_NUM);
		}
	}

	private ArrayList<Integer> getNormalizedRatioCntofTemplateId (String userId) {
		HashMap<Integer, Float> ratioListofTemplate = generateListWrongRatioOfTemplateId(userId);
		System.out.println("WrongRationList " + ratioListofTemplate.values());
		
		ArrayList<Integer> templateDistributionList = new ArrayList<Integer>();

		for (int templateId : ratioListofTemplate.keySet()) {
			float ratio = ratioListofTemplate.get(templateId);
			float normalizedRatio = ratio * 10;
			for (int i=0; i<normalizedRatio; i++) {
				templateDistributionList.add(templateId);
			}
		}

		return templateDistributionList;
	}

	private HashMap<Integer, Float> generateListWrongRatioOfTemplateId(String userId) {
		HashMap<Integer, Float> ratioListofTemplate = new HashMap<Integer, Float>();
		for (int templateId = 1; templateId <= SUPPORTED_TEMPLATE_NUM; templateId++) {
			float wrongRatio = getWrongRatioOfTemplateID(userId, templateId);
			ratioListofTemplate.put(templateId, wrongRatio);
		}
		return ratioListofTemplate;
	}

	private float getWrongRatioOfTemplateID(String user_id, int template_id){
		float wrongRatio=0;
		try {
			ArrayList<Integer> correctInfoList=jdbc.getCorrectInfoOfTemplateID(user_id, template_id);
			if (correctInfoList.size()>0){
				int totalCnt=correctInfoList.size();
				int wrongCnt=0;
				for(int c : correctInfoList){
					if (c==0){
						wrongCnt++;
					}
				}
				wrongRatio=(float)wrongCnt/totalCnt;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wrongRatio;
	}

	// 퀴즈 출제 날짜 (오늘) 생성
	private String getTodayDate(){
		java.util.Date d = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		return df.format(d); 
	}

	// 1번부터 4번 중, 정답 선택지 번호 생성 (랜덤)
	private int genRandNumber(int min, int max){
		int r=min + (int)(Math.random() * ((max - min) + 1));
		return r;
	}

	// 퀴즈DB에 퀴즈 추가
	private void quizToDB(int template_id, String solver_id, String quiz_text, String quiz_image, String selection_type, String[] selections, int answer_number, String quiz_face, String[] selections_face){
		try {
			jdbc.insertQuiz(template_id, solver_id, quiz_text, quiz_image, selection_type, selections, answer_number, quiz_face, selections_face);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Quiz Insertion Error");
			e.printStackTrace();
		}
	}

	// 시간으로부터 시간대 구하기
	private String getTimeslot(int hour){
		if (hour>=6 && hour<=11) return "아침 (6시~11시)";
		else if (hour>11 && hour<=14) return "점심 (12시~14시)";
		else if (hour>14 && hour<=18) return "오후 (15시~18시)";
		else if (hour>18 && hour<=20) return "저녁 (19시~20시)";
		else if (hour>20 && hour<=23) return "밤 (21시~23시)";
		else return "새벽 (24시~5시)";
	}

	// 배열 멤버십 테스트
	private boolean membership(String a, String[] array){
		for(int i=0;i<array.length;i++){
			if(array[i]!=null){
				if(array[i].equals(a)){
					return true;
				}
			}
		}
		return false;
	}

	// 퀴즈 세트 생성 함수
	public int generateQuizset(int numOfQuiz, String solver_id, float personalizedRatio){
		Set<Integer> failedTemplateSet = new HashSet<Integer>();
		int template_id;
		int curNum=0;
		// numOfQuiz 개수만큼 퀴즈를 생성하고, DB에 저장한다.

		try {

			int personalizedQuizNum = (int) (numOfQuiz * personalizedRatio); //Personalization + Random
			boolean isPersonalized = true;

			ArrayList<Image> existedImages = new ArrayList<Image>(); // This list contains selected image before. DAEHOONKIM
		
			QuizAnalyzer qa = new QuizAnalyzer(ARFF_PATH);
			if (personalizedRatio > 0.0f) {
				qa.analyzeQuiz(solver_id);
			} else {
				System.out.print("UPDATE_QUIZ_FEATURE");
				qa.updateQuizFeature(solver_id);
			}

			ArrayList<Integer> templateDistributionList = getNormalizedRatioCntofTemplateId(solver_id);
			Set<Integer> templateDistributionSet = new HashSet<Integer>(templateDistributionList);
			
			while(curNum < numOfQuiz && failedTemplateSet.size() < SUPPORTED_TEMPLATE_NUM){		
				
				if (templateDistributionSet.size() > failedTemplateSet.size() && curNum < personalizedQuizNum)
					isPersonalized = true;
				else
					isPersonalized = false;

				// 퀴즈 템플릿을 선택한다.
				template_id=selectTemplateID(solver_id, numOfQuiz, templateDistributionList, isPersonalized);
				System.out.println("TEMPLATE " + template_id);

				// 퀴즈를 출제하고 DB에 저장한다. 출제 성공하면 퀴즈 수 하나 증가!
				if (generateQuiz(template_id,solver_id, existedImages, isPersonalized)){ // This list contains selected image before. DAEHOONKIM
					curNum++;
				} else {
					failedTemplateSet.add(template_id);					
				}
			}

			System.out.print("FAILED TEMPLATE :: " + failedTemplateSet.toString());

			jdbc.endConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curNum;
	}


	// 퀴즈 생성 함수
	private boolean generateQuiz(int template_id, String solver_id, ArrayList<Image> existedImages, boolean isPersonalized){

		String quiz_template="";
		String quiz_text="";
		Image quiz_image=null;
		String quiz_image_url=null;
		String quiz_face_id = null;
		String selection_type="";
		String[] selections=new String[4];
		String[] selections_faces = new String[4];
		int answer_number=0;
		//String quiz_date="";
		//PhotoSelector ps=new PhotoSelector();

		// 템플릿 ID로부터 템플릿 구함
		try {
			quiz_template=jdbc.getTemplate(template_id);
			System.out.println(quiz_template);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ImageSelector로 부터 퀴즈 생성 관련된 사진들을 얻어옴
		//public Image(String aUrl, String aImageownerId, String aGroupName)
		GetQuizImages getQuizImages = new GetQuizImages(ARFF_PATH);
		HashMap<String, ArrayList<Image>> selectedImages = getQuizImages.getQuizImages(template_id, solver_id, existedImages, isPersonalized); // This list contains selected image before. DAEHOONKIM
		getQuizImages.close();

		if (selectedImages.get("right").size()==0 && selectedImages.get("wrong").size()==0){
			System.out.println(template_id+" 번 템플릿에 해당하는 사진 없음!");
			return false;
		}
		//////////////////

		// 퀴즈 출제 날짜
		//quiz_date=getTodayDate();

		// 사진 속 사람의 이름은?  OK!
		if(template_id==1){
			quiz_text=quiz_template;
			quiz_image=selectedImages.get("right").get(0);  	// 정답이름에 해당하는 사진
			quiz_image_url=quiz_image.getUrl();						// 정답 사진의 URL
			if (quiz_image.getPersons().size() > 0) {
				quiz_face_id = quiz_image.getPersons().get(0).getFaces().get(0).getFaceId();
			}
			answer_number=this.genRandNumber(1, 4);			// 정답 번호 만들기
			selection_type="text";											// 텍스트 보기
			String person_name;
			int wrongIdx=0;

			selections[answer_number-1]=selectedImages.get("right").get(0).getPersons().get(0).getPersonName();		// 정답 답지 만들기
			// 오답답지 만들기
			for(int i=0;i<4;i++){
				if (i != answer_number-1){    // 오답
					if(wrongIdx<selectedImages.get("wrong").size()){		// 사진이 있으면,
						person_name=selectedImages.get("wrong").get(wrongIdx++).getPersons().get(0).getPersonName();	// 오답 이름들
						selections[i]=person_name;
					}
					else{	// 사진이 없으면, 디폴트 이름보기로 구성
						do{
							person_name=default_name.get(genRandNumber(0,default_name.size()-1));   // default 이름 답지에서 받아옴 (기존과 중복 안되게!)
						} while(membership(person_name,selections));
						selections[i]=person_name;
					}
				}
			}
		}
		// 사진 속 사람의 관계는? 
		else if (template_id==2){
			//getRelationshipBtwn
			quiz_text=quiz_template;
			quiz_image=selectedImages.get("right").get(0);
			quiz_image_url=quiz_image.getUrl();
			if (quiz_image.getPersons().size() > 0) {
				quiz_face_id = quiz_image.getPersons().get(0).getFaces().get(0).getFaceId();
			}
			answer_number=this.genRandNumber(1, 4);
			selection_type="text";
			String person_id, relation;
			int wrongIdx=0;

			// 정답 답지 만들기
			person_id=selectedImages.get("right").get(0).getPersons().get(0).getPersonId();
			try {
				selections[answer_number-1]=jdbc.getRelationshipBtwn(solver_id, person_id);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("템플릿2에서 관계구하다가 에러발생!");
				e1.printStackTrace();
			}
			// 오답 답지 만들기
			for(int i=0;i<4;i++){
				if(i != answer_number-1){
					if(wrongIdx<selectedImages.get("wrong").size()){		// 사진이 있으면,
						person_id=selectedImages.get("wrong").get(wrongIdx++).getPersons().get(0).getPersonId();
						try {
							selections[i]=jdbc.getRelationshipBtwn(solver_id, person_id);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{		// 사진이 없으면, 디폴트 관계보기로 구성
						do{
							relation=default_relation.get(genRandNumber(0,default_relation.size()-1));   // default 이름 답지에서 받아옴 (기존과 중복 안되게!)
						} while(membership(relation,selections));
						selections[i]=relation;
					}
				}
			}
		}
		// [이름]에 해당하는 사진은? 
		else if (template_id==3){
			quiz_text=quiz_template;
			quiz_image=selectedImages.get("right").get(0);
			quiz_text=quiz_text.replace("[이름]", quiz_image.getPersons().get(0).getPersonName());  // [이름] 대치
			quiz_image_url=null;											// 사진 제시없는 문제
			answer_number=this.genRandNumber(1, 4);			// 정답 번호 만들기
			selection_type="image";										// 사진 보기
			int wrongIdx=0;
			// 4지선다 선택지 (사진) 만들기
			for(int i=0;i<4;i++){
				if(i==answer_number-1){
					selections[i]=selectedImages.get("right").get(0).getUrl();						// 정답
					if (selectedImages.get("right").get(0).getPersons().size() > 0) {
						selections_faces[i] = selectedImages.get("right").get(0).getPersons().get(0).getFaces().get(0).getFaceId();
					}
				}
				else{
					Image image = selectedImages.get("wrong").get(wrongIdx++);
					selections[i]=image.getUrl();		// 오답
					if (image.getPersons().size() > 0) {
						selections_faces[i] = image.getPersons().get(0).getFaces().get(0).getFaceId();
					}
				}
			}
		}
		// [관계]에 해당하는 사진은?  
		else if (template_id==4){
			quiz_text=quiz_template;
			quiz_image=selectedImages.get("right").get(0);
			// [관계]를 문제에서 요구하는 관계로 대치시킴!
			try {
				quiz_text=quiz_text.replace("[관계]", jdbc.getRelationshipBtwn(solver_id,quiz_image.getPersons().get(0).getPersonId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("템플릿4에서 관계구하다가 에러!");
				e.printStackTrace();
			}
			quiz_image_url=null;									  // 사진 제시없는 문제
			answer_number=this.genRandNumber(1, 4);	  // 정답 번호 만들기
			selection_type="image";								  // 사진 보기
			int wrongIdx=0;
			// 4지선다 선택지 (사진) 만들기
			for(int i=0;i<4;i++){
				if(i==answer_number-1){
					selections[i]=selectedImages.get("right").get(0).getUrl();						// 정답
					if (selectedImages.get("right").get(0).getPersons().size() > 0) {
						selections_faces[i] = selectedImages.get("right").get(0).getPersons().get(0).getFaces().get(0).getFaceId();
					}
				}
				else{
					Image image = selectedImages.get("wrong").get(wrongIdx++);
					selections[i]=image.getUrl();		// 오답
					if (image.getPersons().size() > 0) {
						selections_faces[i] = image.getPersons().get(0).getFaces().get(0).getFaceId();
					}
				}
			}
		}
		// 사진 속 사람을 오늘 만났나? 
		else if (template_id==5){
			quiz_text=quiz_template;
			if(selectedImages.get("right").size()>0){  // 만남
				quiz_image=selectedImages.get("right").get(0);     // 정답이름에 해당하는 사진
				quiz_image_url=quiz_image.getUrl();				        // 정답 사진의 URL
				if (quiz_image.getPersons().size() > 0) {
					quiz_face_id = quiz_image.getPersons().get(0).getFaces().get(0).getFaceId();
				}

				answer_number=1;
			}
			else{	// 안만남
				quiz_image=selectedImages.get("wrong").get(0);  // 정답이름에 해당하는 사진
				quiz_image_url=quiz_image.getUrl();				        // 정답 사진의 URL
				if (quiz_image.getPersons().size() > 0) {
					quiz_face_id = quiz_image.getPersons().get(0).getFaces().get(0).getFaceId();
				}

				answer_number=2;
			}
			selection_type="text";
			selections[0]="만났음";
			selections[1]="만나지 않았음";
			selections[2]=selections[3]=null;
		}

		// 사진들 중 오늘과 관련있는 사진은?  
		else if (template_id==6){
			quiz_text=quiz_template;
			quiz_image_url=null;									// 사진 제시없는 문제
			answer_number=this.genRandNumber(1, 4);	// 정답 번호 만들기
			selection_type="image";								// 사진 보기
			int wrongIdx=0;
			// 4지선다 선택지 (사진) 만들기
			for(int i=0;i<4;i++){
				if(i==answer_number-1){
					selections[i]=selectedImages.get("right").get(0).getUrl();	// 정답
					answer_number=1;
				}
				else{
					selections[i]=selectedImages.get("wrong").get(wrongIdx++).getUrl();		// 오답
					answer_number=2;
				}
			}
		}
		// 사진들 중 오늘 [시간대]와 관련있는 것은? 
		else if (template_id==7){
			quiz_text=quiz_template;
			quiz_image=selectedImages.get("right").get(0);
			quiz_text=quiz_text.replace("[시간대]", getTimeslot(Integer.parseInt(quiz_image.getImageHour())/100));  // [시간대] 대치
			quiz_image_url=null;									// 사진 제시없는 문제
			answer_number=this.genRandNumber(1, 4);	// 정답 번호 만들기
			selection_type="image";								// 사진 보기
			int wrongIdx=0;
			// 4지선다 선택지 (사진) 만들기
			for(int i=0;i<4;i++){
				if(i==answer_number-1){
					selections[i]=selectedImages.get("right").get(0).getUrl();						// 정답
				}
				else{
					selections[i]=selectedImages.get("wrong").get(wrongIdx++).getUrl();		// 오답
				}
			}
		}
		// 사진들 중, 오늘 가장 먼저 촬영된 사진은? 
		else if (template_id==8){
			quiz_text=quiz_template;
			quiz_image_url=null;										// 사진 제시없는 문제
			answer_number=this.genRandNumber(1, 4);		// 정답 번호 만들기
			selection_type="image";									// 사진 보기
			int wrongIdx=0;
			// 4지선다 선택지 (사진) 만들기
			for(int i=0;i<4;i++){
				if(i==answer_number-1){
					selections[i]=selectedImages.get("right").get(0).getUrl();	// 정답
				}
				else{
					selections[i]=selectedImages.get("wrong").get(wrongIdx++).getUrl();		// 오답
				}
			}
		}
		this.quizToDB(template_id, solver_id, quiz_text, quiz_image_url,selection_type, selections, answer_number, quiz_face_id, selections_faces);
		return true;
	}
}
