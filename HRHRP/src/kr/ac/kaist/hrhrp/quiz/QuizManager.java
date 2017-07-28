package kr.ac.kaist.hrhrp.quiz;

import java.util.ArrayList;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Quiz;

public class QuizManager {
	private static DBHandler dbTemplate;
	
	public QuizManager() {
		dbTemplate = new DBHandler();
	}
	
	public void close() {
		dbTemplate.close();
	}
	
	public ArrayList<Quiz> getQuizes(String ownerId) {
		return dbTemplate.selectQuiz(ownerId);
	}
	
	public void updateQuizSolved(int quizId, int solved) {
		dbTemplate.updateQuizSolved(quizId, solved);
	}
	
	public ArrayList<Quiz> getQuizResult(String ownerId) {
		return dbTemplate.selectQuizResult(ownerId);
	}
}
