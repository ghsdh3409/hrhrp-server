package kr.ac.kaist.hrhrp.quiz;

import java.util.ArrayList;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Quiz;

public class QuizGetter {
	private static DBHandler dbTemplate = new DBHandler();
	
	public ArrayList<Quiz> getQuizes(String ownerId) {
		return dbTemplate.selectQuiz(ownerId);
	}
}
