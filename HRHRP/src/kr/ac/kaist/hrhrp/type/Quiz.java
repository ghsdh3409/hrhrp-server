package kr.ac.kaist.hrhrp.type;

import java.util.ArrayList;

public class Quiz {
	private int quizId;
	private int templateId;
	
	private String solverId;
	
	private String quizText;
	private String quizImageUrl;
	private Face quizFace;
	
	private ArrayList<Selection> selections = new ArrayList<Selection>();
	
	public int getQuizId() {
		return quizId;
	}
	
	public int getTemplateId() {
		return templateId;
	}
	
	public String getSolverId() {
		return solverId;
	}
	
	public String getQuizText() {
		return quizText;
	}
	
	public String getQuizImageUrl() {
		return quizImageUrl;
	}
	
	public Face getQuizFace() {
		return quizFace;
	}
	
	public ArrayList<Selection> getSelections() {
		return selections;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}
	
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	
	public void setSolverId(String solverId) {
		this.solverId = solverId;
	}
	
	public void setQuizText(String quizText) {
		this.quizText = quizText;
	}

	public void setQuizImageUrl(String quizImageUrl) {
		this.quizImageUrl = quizImageUrl;
	}
	
	public void setQuizFace(Face quizFace) {
		this.quizFace = quizFace;
	}
	
	public void addSelection(Selection selection) {
		selections.add(selection);
	}
}
