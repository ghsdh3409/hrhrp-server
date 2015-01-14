package kr.ac.kaist.hrhrp.quiz;

import java.util.ArrayList;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Quiz;

public class QuizManager {
	private static DBHandler dbTemplate = new DBHandler();
	
	public ArrayList<Quiz> getQuizes(String ownerId) {
		return dbTemplate.selectQuiz(ownerId);
	}
	
	public void updateQuizSolved(int quizId, int solved) {
		dbTemplate.updateQuizSolved(quizId, solved);
	}
}
