package kr.ac.kaist.hrhrp.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import kr.ac.kaist.hrhrp.db.DBHandler;

public class QuizResultAnalyzer {

	private static DBHandler mDBHandler = null;

	private ArrayList<HashMap<String, Object>> getSolvedQuizList(ArrayList<String> users) {
		mDBHandler = new DBHandler();
		ArrayList<HashMap<String, Object>> fullList = new ArrayList<HashMap<String, Object>>();

		for (String ownerId : users) {
			ArrayList<HashMap<String, Object>> list = mDBHandler.getSolvedQuizByUsername(ownerId);
			fullList.addAll(list);
		}

		if (mDBHandler != null) {
			mDBHandler.close();
		}
		
		return fullList;
	}
	
	private static ArrayList<HashMap<String, Object>> getSolvedQuizList(String users) {
		mDBHandler = new DBHandler();

		ArrayList<HashMap<String, Object>> list = mDBHandler.getSolvedQuizByUsername(users);

		if (mDBHandler != null) {
			mDBHandler.close();
		}
		
		return list;
	}
	
	private static void analyze() {
		ArrayList<String> userPatternUser = new ArrayList<String>();
		
		userPatternUser.add("je_kim@kaist.ac.kr");
		userPatternUser.add("minseo@kaist.ac.kr");
		userPatternUser.add("kaka7537@kaist.ac.kr");
		userPatternUser.add("arirang@kaist.ac.kr");
		
		userPatternUser.add("kimhyunyoung@kaist.ac.kr");
		userPatternUser.add("songhwanjun@kaist.ac.kr");
		userPatternUser.add("jinwoo.lee@kaist.ac.kr");
		userPatternUser.add("daehoonkim@kaist.ac.kr");
		
		userPatternUser.add("junghoon.kim@kaist.ac.kr");
		userPatternUser.add("hkang106@kaist.ac.kr");
		userPatternUser.add("mschoy0372@naver.com");
		userPatternUser.add("ssungssu@kaist.ac.kr");
		
		for (String user : userPatternUser) {
			ArrayList<HashMap<String, Object>> quizList = getSolvedQuizList(user);
			
			HashMap<Object, Integer> personQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorHQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorSQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorVQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> templateQuiz = new HashMap<Object, Integer>();
			
			HashMap<Object, Integer> personWrongQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorHWrongQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorSWrongQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> colorVWrongQuiz = new HashMap<Object, Integer>();
			HashMap<Object, Integer> templateWrongQuiz = new HashMap<Object, Integer>();
			
			for (HashMap<String, Object> quizInfo : quizList) {
				int answer = (Integer) quizInfo.get("answer");
				int solved = (Integer) quizInfo.get("solved");
				int templateId = (Integer) quizInfo.get("template_id");
				
				String quizName = (String) quizInfo.get("quiz_name");
				String selection1Name = (String) quizInfo.get("selection1_name");
				String selection2Name = (String) quizInfo.get("selection2_name");
				String selection3Name = (String) quizInfo.get("selection3_name");
				String selection4Name = (String) quizInfo.get("selection4_name");
				
				Integer quizPersonColorH = (Integer) quizInfo.get("quiz_person_color_h");
				Integer quizPersonColorS = (Integer) quizInfo.get("quiz_person_color_s");
				Integer quizPersonColorV = (Integer) quizInfo.get("quiz_person_color_v");
				Integer selection1ColorH = (Integer) quizInfo.get("selection1_color_h");
				Integer selection1ColorS = (Integer) quizInfo.get("selection1_color_s");
				Integer selection1ColorV = (Integer) quizInfo.get("selection1_color_v");
				Integer selection2ColorH = (Integer) quizInfo.get("selection2_color_h");
				Integer selection2ColorS = (Integer) quizInfo.get("selection2_color_s");
				Integer selection2ColorV = (Integer) quizInfo.get("selection2_color_v");
				Integer selection3ColorH = (Integer) quizInfo.get("selection3_color_h");
				Integer selection3ColorS = (Integer) quizInfo.get("selection3_color_s");
				Integer selection3ColorV = (Integer) quizInfo.get("selection3_color_v");
				Integer selection4ColorH = (Integer) quizInfo.get("selection4_color_h");
				Integer selection4ColorS = (Integer) quizInfo.get("selection4_color_s");
				Integer selection4ColorV = (Integer) quizInfo.get("selection4_color_v");
						
				String name = null;
				Integer colorH = null;
				Integer colorS = null;
				Integer colorV = null;
				
				if (quizName != null) {
					name = quizName;
					colorH = quizPersonColorH;
					colorS = quizPersonColorS;
					colorV = quizPersonColorV;
				} else {
					if (answer == 1) {
						name = selection1Name;
						colorH = selection1ColorH;
						colorS = selection1ColorS;
						colorV = selection1ColorV;
					} else if (answer == 2) {
						name = selection2Name;
						colorH = selection2ColorH;
						colorS = selection2ColorS;
						colorV = selection2ColorV;
					} else if (answer == 3) {
						name = selection3Name;
						colorH = selection3ColorH;
						colorS = selection3ColorS;
						colorV = selection3ColorV;
					} else if (answer == 4) {
						name = selection4Name;
						colorH = selection4ColorH;
						colorS = selection4ColorS;
						colorV = selection4ColorV;
					}
				}
				
				hashMapIncrement(personQuiz, name, 1);
				hashMapIncrement(colorHQuiz, colorH, 1);
				hashMapIncrement(colorSQuiz, colorS, 1);
				hashMapIncrement(colorVQuiz, colorV, 1);
				hashMapIncrement(templateQuiz, templateId, 1);
				
				
				if (answer != solved) {
					hashMapIncrement(personWrongQuiz, name, 1);
					hashMapIncrement(colorHWrongQuiz, colorH, 1);
					hashMapIncrement(colorSWrongQuiz, colorS, 1);
					hashMapIncrement(colorVWrongQuiz, colorV, 1);
					hashMapIncrement(templateWrongQuiz, templateId, 1);
				} else {
					hashMapIncrement(personWrongQuiz, name, 0);
					hashMapIncrement(colorHWrongQuiz, colorH, 0);
					hashMapIncrement(colorSWrongQuiz, colorS, 0);
					hashMapIncrement(colorVWrongQuiz, colorV, 0);
					hashMapIncrement(templateWrongQuiz, templateId, 0);
				}
			}
			
			String resultText = "";
			
			resultText += (user + "\t");
			resultText += (pearsonsCorrelation(personQuiz, personWrongQuiz) + "\t");
			resultText += (pearsonsCorrelation(colorHQuiz, colorHWrongQuiz) + "\t");
			resultText += (pearsonsCorrelation(colorSQuiz, colorSWrongQuiz) + "\t");
			resultText += (pearsonsCorrelation(colorVQuiz, colorVWrongQuiz) + "\t");
			resultText += (pearsonsCorrelation(templateQuiz, templateWrongQuiz) + "\t");
			resultText += (personQuiz + "\t" + personWrongQuiz + "\t");
			resultText += (colorHQuiz + "\t" + colorHWrongQuiz + "\t");
			resultText += (colorSQuiz + "\t" + colorSWrongQuiz + "\t");
			resultText += (colorVQuiz + "\t" + colorVWrongQuiz + "\t");
			resultText += (templateQuiz + "\t" + templateWrongQuiz + "\n");
			
			System.out.print(resultText);

		}
		
	}
	
	private static double pearsonsCorrelation(HashMap<Object, Integer> map1, HashMap<Object, Integer> map2) {		
		double correlation = 0.0;
			
		double[] xArray = new double[map1.size()];
		double[] yArray = new double[map2.size()];
		
		int idx = 0;
		for (Object key : map1.keySet()) {
			xArray[idx] = map1.get(key);
			yArray[idx] = map2.get(key);
			idx++;
		}

		if (xArray.length > 1) {		
			PearsonsCorrelation pc = new PearsonsCorrelation();
			correlation = pc.correlation(xArray, yArray);
		}
		return correlation;
	}

	private static void hashMapIncrement(HashMap<Object, Integer> hashMap, Object key, int addNum) {
		if (!hashMap.containsKey(key)) {
			hashMap.put(key, 0);
		}
		hashMap.put(key, hashMap.get(key) + addNum);
	}
	
	public static void main(String[] args) {
		
		analyze();
	}
}
