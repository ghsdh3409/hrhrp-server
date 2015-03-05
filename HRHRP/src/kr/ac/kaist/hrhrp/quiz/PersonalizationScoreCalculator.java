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
		
	public float calculateScore(String user_id, HashMap<String,String> input_photo) throws Exception{
		
		input_photo.put("time", getTimeSlot(input_photo.get("time")));
		
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
		HashMap<String,String> outputMap=new HashMap<String,String>();
		
		if(level==3){
			// input 그대로!
			outputMap.put("person",person);
			outputMap.put("weather", weather);
			outputMap.put("time", time);
			outputMap.put("location", location);
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
		}
		return outputMap;
	}
	
	public int membershipTest(HashMap<String,String> photoFeature, ArrayList<HashMap<String,String>> itemsets){
		String f_person, f_weather, f_time, f_location;
		String p_person, p_weather, p_time, p_location;
		
		p_person=photoFeature.get("person");
		p_weather=photoFeature.get("weather");
		p_time=photoFeature.get("time");
		p_location=photoFeature.get("location");
		
		for(HashMap<String,String> itemset : itemsets){
			f_person=itemset.get("person");
			f_weather=itemset.get("weather");
			f_time=itemset.get("time");
			f_location=itemset.get("location");
			
			if(p_person.endsWith(f_person) && p_weather.endsWith(f_weather) && 
					p_time.endsWith(f_time) && p_location.endsWith(f_location)){
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
