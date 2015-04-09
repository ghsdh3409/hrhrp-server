package kr.ac.kaist.hrhrp.quiz;


import java.util.ArrayList;
import java.util.HashMap;

public class PersonalizationScoreCalculator {
	private JDBC jdbc;
	private String user_id;
	private HashMap<Integer,ArrayList<HashMap<String,String>>> freqItemsets;
	private HashMap<String,String> input_photo;
	private final HashMap<Integer, Float> level_weight;
	
	public PersonalizationScoreCalculator(){
		jdbc=new JDBC();
		jdbc.setConnection();
		level_weight=new HashMap<Integer, Float>();
		level_weight.put(3, (float) 0.5);
		level_weight.put(2, (float) 0.3);
		level_weight.put(1, (float) 0.2);
	}
	
	/// same codes exist in QuizAnalyzer///////////////////////////////////////////////////////////////////
	// 주어진 사진에 대해, 개인화 점수를 계산
	private String getTimeSlot(String date){
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
	
	// Color H
	String getColorH(int h){
		if ((h>=0 && h<=19) || (h>=330 && h<=360)) return "Red";
		else if (h>=20 && h<=49) return "Orange";
		else if (h>=50 && h<=69) return "Yellow";
		else if (h>=70 && h<=84) return "Lime";
		else if (h>=85 && h<=170) return "Green";
		else if (h>=171 && h<=191) return "Aqua";
		else if (h>=192 && h<=264) return "Blue";
		else if (h>=265 && h<=289) return "Violet";
		else if (h>=290 && h<=329) return "Purple";
		else return "null";
	}
	
	// Color S
	String getColorS(int s){
		if (s>=0 && s<=12) return "Drab";
		else if (s>=13 && s<=24) return "Semi-drab";
		else if (s>=25 && s<=37) return "Semi-faded";
		else if (s>=38 && s<=49) return "Faded";
		else if (s>=50 && s<=62) return "Rich";
		else if (s>=63 && s<=74) return "Semi-rich";
		else if (s>=75 && s<=87) return "Semi-pure";
		else if (s>=88 && s<=100) return "Pure";
		else return "null";
	}
	
	// Color V
	String getColorV(int v){
		if (v>=0 && v<=12) return "Dark4";
		else if (v>=13 && v<=24) return "Dark3";
		else if (v>=25 && v<=37) return "Dark2";
		else if (v>=38 && v<=49) return "Dark1";
		else if (v>=50 && v<=62) return "Bright1";
		else if (v>=63 && v<=74) return "Bright2";
		else if (v>=75 && v<=87) return "Bright3";
		else if (v>=88 && v<=100) return "Bright4";
		else return "null";
	}
	///////////////////////////////////////////////////
	
	public float calculateScore(String user_id, HashMap<String,String> input_photo) throws Exception{
		
		// To Discretize features (time, colors) of Photo table
		input_photo.put("time", getTimeSlot(input_photo.get("time")));
		input_photo.put("color_h", getColorH(Integer.parseInt(input_photo.get("color_h"))));
		input_photo.put("color_s", getColorS(Integer.parseInt(input_photo.get("color_s"))));
		input_photo.put("color_v", getColorV(Integer.parseInt(input_photo.get("color_v"))));
		
		setUserID(user_id);
		setPhotoFeature(input_photo);

		int matchIdx=-1, support, cnt_total, cnt_wrong;
		float score=0, temp_score=0, prob_wrong;
		HashMap<String,String> photoFeature, matchItemset;
		
		System.out.println("\n\n# Calculate score~!");
		
		for(int level=3;level>0;level--){
			System.out.println("- in level "+level);
			photoFeature=getPhotoFeatureForLevel(level);

			matchIdx=membershipTest(photoFeature,freqItemsets.get(level));
			if(matchIdx!=-1){
				matchItemset=freqItemsets.get(level).get(matchIdx);
				cnt_total=jdbc.getNumberOfPattern(user_id, photoFeature);
				cnt_wrong=jdbc.getWrongNumberOfPattern(user_id, photoFeature);
				prob_wrong=(float)cnt_wrong/cnt_total;
				support=Integer.parseInt(matchItemset.get("support"));
				temp_score=prob_wrong*support*level_weight.get(level);
				System.out.println("* prob_wrong: "+prob_wrong+" ("+cnt_wrong+"/"+cnt_total+")");
				System.out.println("* support: "+support);
				System.out.println("* weight: "+level_weight.get(level));
				System.out.println("=> + "+temp_score);
				score+=temp_score;
			}
			else{
				continue;
			}
		}
		
		return score;
	}
	
	// 레벨에 따라 사진의 feature를 구함
	public HashMap<String,String> getPhotoFeatureForLevel(int level) throws Exception{
		String person=input_photo.get("person");
		String weather=input_photo.get("weather");
		String time=input_photo.get("time");
		String location=input_photo.get("location");
		String color_h=input_photo.get("color_h");
		String color_s=input_photo.get("color_s");
		String color_v=input_photo.get("color_v");
		String object=input_photo.get("object");
		HashMap<String,String> outputMap=new HashMap<String,String>();
		
		if(level==3){
			// input 그대로!
			outputMap.put("person",person);
			outputMap.put("weather", weather);
			outputMap.put("time", time);
			outputMap.put("location", location);
			outputMap.put("color_h", color_h);
			outputMap.put("color_s", color_s);
			outputMap.put("color_v", color_v);
			outputMap.put("object", object);
		}
		else if(level==2){
			// 사람
			if(person.equals("사람없음")) outputMap.put("person", "사람없음");
			else{
				outputMap.put("person", jdbc.getRelationshipBtwn(user_id, person));
			}
			// 날씨
			outputMap.put("weather", jdbc.getParentFeature(weather,3,"날씨"));
			// 시간
			outputMap.put("time", jdbc.getParentFeature(time,3,"시간"));
			// 장소
			outputMap.put("location", jdbc.getParentFeature(location,3,"장소"));
			// color_h
			outputMap.put("color_h",jdbc.getParentFeature(color_h, 3, "H"));
			// color_s
			outputMap.put("color_s",jdbc.getParentFeature(color_s, 3, "S"));
			// color_v
			outputMap.put("color_v",jdbc.getParentFeature(color_v, 3, "V"));
			// object
			outputMap.put("object",jdbc.getParentFeature(object, 3, "Object"));
		}
		else{
			// 사람
			if(person.equals("사람없음")) outputMap.put("person", "사람없음");
			else outputMap.put("person","사람있음");
			// 날씨
			outputMap.put("weather", jdbc.getParentFeature(jdbc.getParentFeature(weather,3,"날씨"),2,"날씨"));
			// 시간
			outputMap.put("time", jdbc.getParentFeature(jdbc.getParentFeature(time,3,"시간"),2,"시간"));
			// 장소
			outputMap.put("location", jdbc.getParentFeature(jdbc.getParentFeature(location,3,"장소"),2,"장소"));
			// color_h
			outputMap.put("color_h", jdbc.getParentFeature(jdbc.getParentFeature(color_h,3,"H"),2,"H"));
			// color_h
			outputMap.put("color_s", jdbc.getParentFeature(jdbc.getParentFeature(color_s,3,"S"),2,"S"));
			// color_h
			outputMap.put("color_v", jdbc.getParentFeature(jdbc.getParentFeature(color_v,3,"V"),2,"V"));
			// object
			outputMap.put("object", jdbc.getParentFeature(jdbc.getParentFeature(object,3,"Object"),2,"Object"));
		}
		return outputMap;
	}
	
	public int membershipTest(HashMap<String,String> photoFeature, ArrayList<HashMap<String,String>> itemsets){
		String f_person, f_weather, f_time, f_location, f_color_h, f_color_s, f_color_v, f_object;
		String p_person, p_weather, p_time, p_location, p_color_h, p_color_s, p_color_v, p_object;
		
		p_person=photoFeature.get("person");
		p_weather=photoFeature.get("weather");
		p_time=photoFeature.get("time");
		p_location=photoFeature.get("location");
		p_color_h=photoFeature.get("color_h");
		p_color_s=photoFeature.get("color_s");
		p_color_v=photoFeature.get("color_v");
		p_object=photoFeature.get("object");
		
		for(HashMap<String,String> itemset : itemsets){
			f_person=itemset.get("person");
			f_weather=itemset.get("weather");
			f_time=itemset.get("time");
			f_location=itemset.get("location");
			f_color_h=itemset.get("color_h");
			f_color_s=itemset.get("color_s");
			f_color_v=itemset.get("color_v");
			f_object=itemset.get("object");
			
			if(p_person.equals(f_person) && p_weather.equals(f_weather) && p_time.equals(f_time) && p_location.equals(f_location) && 
					p_color_h.equals(f_color_h) && p_color_s.equals(f_color_s) && p_color_v.equals(f_color_v) && p_object.equals(f_object)){
				return itemsets.indexOf(itemset);
			}
		}
		return -1;
	}
	
	public void setUserID(String user_id){
		this.user_id=user_id;
	}
	
	public void setPhotoFeature(HashMap<String,String> photoFeature){
		this.input_photo=photoFeature;
	}
	
	public void setFreqItemsets(HashMap<Integer,ArrayList<HashMap<String,String>>> itemsets){
		this.freqItemsets=itemsets;
	}
}
