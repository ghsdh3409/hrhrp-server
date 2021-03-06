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

	private ArrayList<Image> mExistedImages;
	private boolean mIsPersonalized = true;

	private String mArffPath;

	public GetQuizImages(String arffPath) {
		dbTemplate = new DBHandler();
		mArffPath = arffPath;
	}

	public void close() {
		dbTemplate.close();
	}

	public HashMap<String, ArrayList<Image>> getQuizImages(int templateType, String user, ArrayList<Image> existedImage, boolean isPersonalized) {
		mExistedImages = existedImage;
		mIsPersonalized = isPersonalized;

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
		} else if (templateType == 10) {
			return getImagesTemplate10(user);
		} else {
			return null;
		}
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate1(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(1, ownerId);

		if (images.size() > 0) {

			personalizedImages = personalization(images, ownerId);

			if (personalizedImages.size() > 0) {

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

		ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(3, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

			if (personalizedImages.size() > 0) {

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

				if (incorrectImages.size() < 4)
					addIncorrectImageforTemplate(3, ownerId, 4, incorrectImages, correctImage);
			}
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

		ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(2, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

			if (personalizedImages.size() > 0) {

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

		ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(4, ownerId);
		if (images.size() > 0) {
			personalizedImages = personalization(images, ownerId);

			if (personalizedImages.size() > 0) {

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

				if (incorrectImages.size() < 4)
					addIncorrectImageforTemplate(4, ownerId, 4, incorrectImages, correctImage);
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

		int answerType = 1 + (int)(Math.random() * ((2 - 1) + 1));
		
		if (answerType == 1) {
			ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(5, ownerId);

			if (images.size() > 0) {

				personalizedImages = personalization(images, ownerId);

				if (personalizedImages.size() > 0) {
					Image correctImage = personalizedImages.get(0);
					correctImages.add(correctImage);				
				}
			}
		}
		
		if (answerType == 2 || correctImages.size() == 0){
			incorrectImages = dbTemplate.selectImagesByTemplateIdOwner(-5, ownerId);
			incorrectImages = getUniquePersonaliedImages(incorrectImages);
		}
		
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}

	private HashMap<String, ArrayList<Image>> getImagesTemplate10(String ownerId) {
		HashMap<String, ArrayList<Image>> quizImages = new HashMap<String, ArrayList<Image>>();

		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		ArrayList<Image> correctImages = new ArrayList<Image>();
		ArrayList<Image> incorrectImages = new ArrayList<Image>();

		int answerType = 1 + (int)(Math.random() * ((2 - 1) + 1));
		
		if (answerType == 1) {
			ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(10, ownerId);

			if (images.size() > 0) {

				personalizedImages = personalization(images, ownerId);

				if (personalizedImages.size() > 0) {
					Image correctImage = personalizedImages.get(0);
					correctImages.add(correctImage);				
				}
			}
		}
		
		if (answerType == 2 || correctImages.size() == 0){
			incorrectImages = dbTemplate.selectImagesByTemplateIdOwner(-10, ownerId);
			incorrectImages = getUniquePersonaliedImages(incorrectImages);
		}
		
		quizImages.put(KEY_CORRECT, correctImages);
		quizImages.put(KEY_INCORRECT, incorrectImages);

		return quizImages;
	}
	
	private boolean isEqualImage(Image existedImage, Image image) {
		String imagePersonId = image.getPersons().get(0).getPersonId();
		String imageRelation = image.getPersons().get(0).getPersonRelation();
		String existedPersonId = existedImage.getPersons().get(0).getPersonId();
		String existedRelation = existedImage.getPersons().get(0).getPersonRelation();	
		if (existedPersonId != null && existedPersonId.equals(imagePersonId)) {
			return true;
		}
		if (existedRelation != null && existedRelation.equals(imageRelation)) {
			return true;
		}
		return false;
	}
	
	private boolean isImageContain(ArrayList<Image> imageList, Image image) {
		String imagePersonId = image.getPersons().get(0).getPersonId();
		String imageRelation = image.getPersons().get(0).getPersonRelation();
		for (Image existedImage : imageList) {
			String existedPersonId = existedImage.getPersons().get(0).getPersonId();
			String existedRelation = existedImage.getPersons().get(0).getPersonRelation();	
			if (existedPersonId != null && existedPersonId.equals(imagePersonId)) {
				return true;
			}
			if (existedRelation != null && existedRelation.equals(imageRelation)) {
				return true;
			}
		}
		return false;		
	}
	
	private void addIncorrectImageforTemplate(int templateId, String ownerId, int incorrectImageSize, ArrayList<Image> incorrectImages, Image correctImage) {
		ArrayList<Image> images = dbTemplate.selectImagesByTemplateIdOwner(templateId, ownerId);		
		Collections.shuffle(images);

		for (int i=0; i<images.size(); i++) {
			if (incorrectImages.size() < incorrectImageSize) {
				Image candidateImage = images.get(i);
				if (!isEqualImage(correctImage, candidateImage)) {
					if (!isImageContain(incorrectImages, candidateImage)) {
						incorrectImages.add(candidateImage);
					}
				}
			} else {
				break;
			}
		}
		
		if (incorrectImages.size() < incorrectImageSize) {
			images = dbTemplate.selectAllImagesByTemplateId(templateId);
			Collections.shuffle(images);
			
			for (int i=0; i<images.size(); i++) {
				if (incorrectImages.size() < incorrectImageSize) {
					Image candidateImage = images.get(i);
					if (!isEqualImage(correctImage, candidateImage)) {
						if (!isImageContain(incorrectImages, candidateImage)) {
							incorrectImages.add(candidateImage);
						}
					}
				} else {
					break;
				}
			}
		}
	}

	private ArrayList<Image> personalization(ArrayList<Image> images, String ownerId) {
		ArrayList<Image> personalizedImages = new ArrayList<Image>();
		HashMap<Image, Float> scoredImages = new HashMap<Image, Float>(); 

		int solvedQuizCnt = dbTemplate.getSolvedQuizCntByUsername(ownerId);

		if (mIsPersonalized && solvedQuizCnt > 0) {

			MultilevelAssociationMiner ruleMiner=new MultilevelAssociationMiner(mArffPath);
			PersonalizationScoreCalculator psc=new PersonalizationScoreCalculator();

			try {
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
				int colorH = image.getColorH();
				int colorS = image.getColorS();
				int colorV = image.getColorV();
								
				String objectClassCode = image.getObjectId();

				HashMap<String,String> photo=new HashMap<String,String>();

				photo.put("person", personId);
				photo.put("weather", weather);
				photo.put("time", time);
				photo.put("location", location);
				photo.put("color_h", ""+colorH);
				photo.put("color_s", ""+colorS);
				photo.put("color_v", ""+colorV);
				photo.put("object",objectClassCode);
				

				System.out.print(photo);

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

		ArrayList<Image> uniquePersonalizedImages = (ArrayList<Image>) personalizedImages.clone();
		System.out.println("PREV UPI SIZE " + uniquePersonalizedImages.size());
		for (Image personalizedImage : personalizedImages) { //Remove images selected before for quiz.
			for (Image existedImage : mExistedImages) {
				if (existedImage.getPersons().size() > 0) {
					if (existedImage.getPersons().get(0).getPersonId().equals(personalizedImage.getPersons().get(0).getPersonId())) {
						uniquePersonalizedImages.remove(personalizedImage);
					}
				} else {
					if (existedImage.getUrl().equals(personalizedImage.getUrl())) {
						uniquePersonalizedImages.remove(personalizedImage);
					}
				}
			}
		}
		System.out.println("AFTER UPI SIZE " + uniquePersonalizedImages.size());

		if (uniquePersonalizedImages.size() > 0)
			mExistedImages.add(uniquePersonalizedImages.get(0)); //Add correct image to existedImages.

		return uniquePersonalizedImages;
	}

	private ArrayList<Image> getUniquePersonaliedImages(ArrayList<Image> images) {
		ArrayList<Image> uniquePersonalizedImages = (ArrayList<Image>) images.clone();
		System.out.println("PREV UPI SIZE " + uniquePersonalizedImages.size());
		for (Image personalizedImage : images) { //Remove images selected before for quiz.
			for (Image existedImage : mExistedImages) {
				if (existedImage.getPersons().size() > 0) {
					if (existedImage.getPersons().get(0).getPersonId().equals(personalizedImage.getPersons().get(0).getPersonId())) {
						uniquePersonalizedImages.remove(personalizedImage);
					}
				} else {
					if (existedImage.getUrl().equals(personalizedImage.getUrl())) {
						uniquePersonalizedImages.remove(personalizedImage);
					}
				}
			}
		}
		System.out.println("AFTER UPI SIZE " + uniquePersonalizedImages.size());

		if (uniquePersonalizedImages.size() > 0)
			mExistedImages.add(uniquePersonalizedImages.get(0)); //Add correct image to existedImages.

		return uniquePersonalizedImages;
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