package kr.ac.kaist.hrhrp.quiz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import weka.associations.Apriori;
import weka.core.Instances;

public class MultilevelAssociationMiner {
	private JDBC jdbc;
	private final int treedepth=3;
	private final int numOfFeatures=4;
	private String user_id;
	private HashMap<Integer,ArrayList<HashMap<String,String>>> freqItemsets;
	
	// Apriori Parameter
	private final double deltaValue = 1;
	private final double lowerBoundMinSupportValue = 0.1;
	private final double upperBoundMinSupportValue = 1.0;
	private final double minMetricValue = 0.5;
	private final int numRulesValue = 10;

	private String mARFFPath;

	public MultilevelAssociationMiner(String arffPath){
		jdbc=new JDBC();
		jdbc.setConnection();
		freqItemsets=new HashMap<Integer,ArrayList<HashMap<String,String>>>();
		user_id="";
		mARFFPath = arffPath;
	}
	

	public HashMap<Integer, ArrayList<HashMap<String,String>>> startMining(String user_id) throws Exception{
		setUserID(user_id);
		for(int level=treedepth;level>0;level--){
			System.out.println("\n\n## START Apriori at level "+level+"!");
			getMiningResult(level);
		}
		return freqItemsets;
	}
	

	public void getMiningResult(int level){
		Instances data=null;
		String arffFilename= mARFFPath + "input_lev"+level+"_"+user_id+".arff";
		

		try{
			BufferedReader reader=new BufferedReader(new FileReader(arffFilename));
			data=new Instances(reader);
			reader.close();
			data.setClassIndex(data.numAttributes()-1);
		}
		catch(IOException e){
			System.out.println("Error during File Reading!");
			e.printStackTrace();
		}
		

		String aprioriResult;
		
		Apriori apriori = new Apriori();
		apriori.setDelta(deltaValue);
		apriori.setLowerBoundMinSupport(lowerBoundMinSupportValue);
		apriori.setNumRules(numRulesValue);
		apriori.setUpperBoundMinSupport(upperBoundMinSupportValue);
		apriori.setMinMetric(minMetricValue); 
		apriori.setOutputItemSets(true);
		

		try{
			apriori.buildAssociations(data);
		}
		catch(Exception e){
			System.out.println("Error during Apriori!");
			e.printStackTrace();
		}
		aprioriResult=apriori.toString();
		 

		ArrayList<String> itemsets=getItemsets(aprioriResult,numOfFeatures);
		
		for(String itemset : itemsets){
			System.out.println(itemset);
		}
		
		freqItemsets.put(level, analyzeItemsets(itemsets));
	}
	

	public ArrayList<String> getItemsets(String result, int size){
		ArrayList<String> itemsets=new ArrayList<String>();

		System.out.println("** RETRIEVE itemsets of size "+size+"!");
		String[] tokens=result.split("\n");
		int startIdx=0, endIdx=0;
		for(;startIdx<tokens.length;startIdx++){
			if(tokens[startIdx].contains("Itemsets L("+size+")")){
				break;
			}
		}

		endIdx=startIdx;
		for(;endIdx<tokens.length;endIdx++){
			if(tokens[endIdx].equals("")){
				break;
			}
		}
		
		startIdx++; 
		endIdx--;   
		
		for(int i=startIdx;i<=endIdx;i++){
			itemsets.add(tokens[i]);
		}
		
		return itemsets;
	}
	
	public ArrayList<HashMap<String,String>> analyzeItemsets(ArrayList<String> itemsets){
		ArrayList<HashMap<String,String>> analItemsets=new ArrayList<HashMap<String,String>>();
		String[] tokens;
		for(String itemset : itemsets){
			HashMap<String,String> tempMap=new HashMap<String,String>();
			tokens=itemset.split(" ");
			tempMap.put("person", tokens[0].split("=")[1]);
			tempMap.put("weather", tokens[1].split("=")[1]);
			tempMap.put("time", tokens[2].split("=")[1]);
			tempMap.put("location", tokens[3].split("=")[1]);
			tempMap.put("support", tokens[4]);
			analItemsets.add(tempMap);
		}
		return analItemsets;
	}
	
	public void setUserID(String user_id){
		this.user_id=user_id;
	}
}
