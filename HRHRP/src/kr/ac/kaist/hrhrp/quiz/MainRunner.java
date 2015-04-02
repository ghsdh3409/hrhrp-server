package kr.ac.kaist.hrhrp.quiz;


import java.util.ArrayList;
import java.util.HashMap;

public class MainRunner {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		//PhotoFilter pf = new PhotoFilter();
		//QuizGen qg = new QuizGen();
		String arffPath = "/home/daehoon/HRHRP/personalized/arff/";
		QuizAnalyzer qa = new QuizAnalyzer(arffPath);
		MultilevelAssociationMiner ruleMiner=new MultilevelAssociationMiner(arffPath);
		PersonalizationScoreCalculator psc=new PersonalizationScoreCalculator();
		/*
		//찍힌 사진들은 img폴더에 저장된다 가정
		ArrayList<String> selectedFileNameList=pf.selectPhotos("C:\\img","C:\\dest");
		System.out.println(selectedFileNameList.size()+" photos survived! :) ");
		for(String fileName:selectedFileNameList){
			System.out.println(fileName);
		}
		*/
		/*
		qg.generateQuizset(10, "ghsdh3409@gmail.com");
		*/
		
		//////////////// 사진 예시
		HashMap<String,String> photo=new HashMap<String,String>();
		//photo.put("person", "5140373f90001a7ffefbd946a1fad5ad");
		//photo.put("person", "0aee3c7eb3f3504bcb9efdfc84cd1f91");
		//photo.put("person", "17ee226580e350dd946b1840de3882aa");
		photo.put("person", "b7e73c29d9c61ccb341456906cee8c2b");
		photo.put("weather","구름많음+없음");
		photo.put("time","2015-01-15 13:45:45");
		photo.put("location","구성동");
		////////////////
		
		
		qa.analyzeQuiz("ghsdh3409@gmail.com");
		HashMap<Integer, ArrayList<HashMap<String,String>>> itemsets=ruleMiner.startMining("ghsdh3409@gmail.com");
		psc.setFreqItemsets(itemsets);
		float score=psc.calculateScore("ghsdh3409@gmail.com", photo);
		System.out.println(score);
	}
}
