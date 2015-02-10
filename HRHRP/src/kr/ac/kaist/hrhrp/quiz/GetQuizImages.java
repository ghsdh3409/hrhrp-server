package kr.ac.kaist.hrhrp.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Image;

public class GetQuizImages {

	private static DBHandler dbTemplate = new DBHandler();
	private static String KEY_CORRECT = "right";
	private static String KEY_INCORRECT = "wrong";

	public static HashMap<String, ArrayList<Image>> getQuizImages(int templateType, String user) {
		if (templateType == 1) {
			return getImagesTemplate1(user);
		} else if (templateType == 2) {
			return getImagesTemplate2(user);
		} else if (templateType == 3) {
			return getImagesTemplate3(user);
		} else if (templateType == 4) {
			return getImagesTemplate4(user);
		} else if (templateType == 5) {
			return getImagesTemplate5(user);
		} else {
			return null;
		}
	}

	private static HashMap<String, ArrayList<Image>> getImagesTemplate1(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(1, ownerId);
		personalizedImages = personalization(images);

		Image correctImage = personalizedImages.get(0);
		correctImages.add(correctImage);

		ArrayList<String> optionSet = new ArrayList<String>();
		optionSet.add(correctImage.getPersons().get(0).getPersonName());

		for (Image incorrectImage : personalizedImages) {
			String personName = incorrectImage.getPersons().get(0).getPersonName();
			if (!optionSet.contains(personName)) {
				incorrectImages.add(incorrectImage);
				optionSet.add(personName);
			}
			if (incorrectImages.size() > 2) {
				break;
			}
		}

		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private static HashMap<String, ArrayList<Image>> getImagesTemplate3(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(1, ownerId);
		personalizedImages = personalization(images);

		Image correctImage = personalizedImages.get(0);
		correctImages.add(correctImage);

		ArrayList<String> optionSet = new ArrayList<String>();
		optionSet.add(correctImage.getPersons().get(0).getPersonName());

		for (Image incorrectImage : personalizedImages) {
			String personName = incorrectImage.getPersons().get(0).getPersonName();
			if (!optionSet.contains(personName)) {
				incorrectImages.add(incorrectImage);
				optionSet.add(personName);
			}
			if (incorrectImages.size() > 2) {
				break;
			}
		}

		while (incorrectImages.size() < 3) {
			incorrectImages.add(Image.getDefaultImage());
		}

		System.out.println(incorrectImages.size());
		
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private static HashMap<String, ArrayList<Image>> getImagesTemplate2(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(2, ownerId);
		personalizedImages = personalization(images);

		Image correctImage = personalizedImages.get(0);
		correctImages.add(correctImage);

		ArrayList<String> optionSet = new ArrayList<String>();
		optionSet.add(correctImage.getPersons().get(0).getPersonRelation());

		for (Image incorrectImage : personalizedImages) {
			String personRelation = incorrectImage.getPersons().get(0).getPersonRelation();
			if (!optionSet.contains(personRelation)) {
				incorrectImages.add(incorrectImage);
				optionSet.add(personRelation);
			}
			if (incorrectImages.size() > 2) {
				break;
			}
		}

		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private static HashMap<String, ArrayList<Image>> getImagesTemplate4(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(2, ownerId);
		personalizedImages = personalization(images);

		Image correctImage = personalizedImages.get(0);
		correctImages.add(correctImage);

		ArrayList<String> optionSet = new ArrayList<String>();
		optionSet.add(correctImage.getPersons().get(0).getPersonRelation());

		for (Image incorrectImage : personalizedImages) {
			String personRelation = incorrectImage.getPersons().get(0).getPersonRelation();
			if (!optionSet.contains(personRelation)) {
				incorrectImages.add(incorrectImage);
				optionSet.add(personRelation);
			}
			if (incorrectImages.size() > 2) {
				break;
			}
		}

		while (incorrectImages.size() < 3) {
			incorrectImages.add(Image.getDefaultImage());
		}

		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private static HashMap<String, ArrayList<Image>> getImagesTemplate5(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(5, ownerId);

		if (images.size() > 0) {

			personalizedImages = personalization(images);
			Image correctImage = personalizedImages.get(0);
			correctImages.add(correctImage);

			incorrectImages = dbTemplate.selectImagesByTemplate(-5, ownerId);			
		}
		
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);
		
		return quizImages;
	}

	private static ArrayList<Image> personalization(ArrayList<Image> images) {
		ArrayList<Image> personalizedImages = new ArrayList<Image>();

		personalizedImages = images;

		Collections.shuffle(personalizedImages);
		
		return personalizedImages;
	}

	public static void main(String[] args) {
		QuizGen guizGen = new QuizGen();
		guizGen.generateQuizset(30, "ghsdh3409@gmail.com");

		/*
		HashMap<String, ArrayList<Image>> quizSet = getQuizImages(5, "ghsdh3409@gmail.com");
		for (Image image : quizSet.get(KEY_CORRECT)) {
			System.out.println(image.getPersons().get(0).getPersonRelation());
		}

		System.out.println("--");

		for (Image image : quizSet.get(KEY_INCORRECT)) {
			System.out.println(image.getPersons().get(0).getPersonRelation());
		}
		 */
	}

}
