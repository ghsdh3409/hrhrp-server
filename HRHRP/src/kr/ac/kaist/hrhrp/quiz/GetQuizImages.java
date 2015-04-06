package kr.ac.kaist.hrhrp.quiz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import kr.ac.kaist.hrhrp.db.DBHandler;
import kr.ac.kaist.hrhrp.type.Image;
import kr.ac.kaist.hrhrp.type.Person;

public class GetQuizImages {

	private static DBHandler dbTemplate;
	private static String KEY_CORRECT = "right";
	private static String KEY_INCORRECT = "wrong";

	public GetQuizImages() {
		dbTemplate = new DBHandler();
	}
	
	public void close() {
		dbTemplate.close();
	}
	
	public HashMap<String, ArrayList<Image>> getQuizImages(int templateType, String user) {
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

	private HashMap<String, ArrayList<Image>> getImagesTemplate1(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(1, ownerId);

		if (images.size() > 0) {

			personalizedImages = personalization(images, ownerId);

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
		}
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate3(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(1, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

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
		}
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate2(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(2, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

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
		}
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate4(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(2, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

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
		}
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate5(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplate(5, ownerId);

		if (images.size() > 0) {

			personalizedImages = personalization(images, ownerId);
			Image correctImage = personalizedImages.get(0);
			correctImages.add(correctImage);

			incorrectImages = dbTemplate.selectImagesByTemplate(-5, ownerId);			
		}

		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private ArrayList<Image> personalization(ArrayList<Image> images, String ownerId) {
		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		HashMap<Image, Float> scoredImages = new HashMap<Image, Float>(); 

		int solvedQuizCnt = dbTemplate.getSolvedQuizCntByUsername(ownerId);

		if (solvedQuizCnt > 0) {

			String arffPath = "/home/daehoon/HRHRP/personalized/arff/";
			QuizAnalyzer qa = new QuizAnalyzer(arffPath);
			MultilevelAssociationMiner ruleMiner=new MultilevelAssociationMiner(arffPath);
			PersonalizationScoreCalculator psc=new PersonalizationScoreCalculator();

			try {
				qa.analyzeQuiz(ownerId);
				HashMap<Integer, ArrayList<HashMap<String,String>>> itemsets=ruleMiner.startMining(ownerId);
				psc.setFreqItemsets(itemsets);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (Image image : images) {

				Person person = image.getPersons().get(0);
				String personId = person.getPersonId();
				String weather = image.getWeather();
				weather = weather.split("/")[1] + "+" + weather.split("/")[2];
				String location = image.getStreet();
				Date takenAt = image.getImageTime();
				SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = transFormat.format(takenAt);

				HashMap<String,String> photo=new HashMap<String,String>();

				photo.put("person", personId);
				photo.put("weather", weather);
				photo.put("time", time);
				photo.put("location", location);

				System.out.print(personId + '\t' + weather + '\t' + time + '\t' + location);

				float score = 0.0f;
				try {
					score = psc.calculateScore(ownerId, photo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(score);
				scoredImages.put(image, score);
			}

			Map sortedMap = sortByValue(scoredImages);
			for (Object imageObj : sortedMap.keySet().toArray()) {
				Image image = (Image) imageObj;
				personalizedImages.add(image);
			}
		} else {
			personalizedImages = (ArrayList<Image>) images.clone();
			Collections.shuffle(personalizedImages);
		}

		return personalizedImages;
	}

	public Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}

class ValueComparator implements Comparator {

	Map map;

	public ValueComparator(Map map) {
		this.map = map;
	}

	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}