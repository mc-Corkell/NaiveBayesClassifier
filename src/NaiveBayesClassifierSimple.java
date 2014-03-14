import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class NaiveBayesClassifierSimple {
	
	public static final String TRAIN_DATA_FILE = "promoters_svm_corrected/training.new";
	public static final String TEST_DATA_FILE = "promoters_svm_corrected/validation.new";
	public static double M;


	public static void main(String[] args) throws FileNotFoundException {
		/*if(args.length != 1) {
			System.out.println("\tPlease pass in the soothing parameter!!!");
			return; 
		}
		M = Double.parseDouble(args[0]); */
		M = .00001; 
		System.out.println("Naive Bayes Classifier");
			Map<String, WordCountSimple> wordMap = new HashMap<String, WordCountSimple>(); 
			TotalCount totalCount = processTrainingData(wordMap);
			double accuracy = testData(wordMap, totalCount); 
			System.out.println("Accuracy:\t\t" + accuracy); 
	}
	
	// Classifies each test email based on learner and calculates and prints overall accuracy 
	public static double testData(Map<String, WordCountSimple> wordMap, TotalCount totalCount) throws FileNotFoundException {

		int testCorrect = 0; 
		int testTotal = 0; 
		Scanner fileScanner = new Scanner(new File(TEST_DATA_FILE)); 
		// For Each Test Email 
		int c = 0; 	
		while(fileScanner.hasNextLine()){
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			double pSpam = Math.log(totalCount.pSpam); 
			double pHam = Math.log(totalCount.pHam); 
			boolean targetIsSpam = lineScanner.next().equals("1"); 
			c++;
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next();
				String[] parts = w.split(":");
				w = parts[1];
				WordCountSimple wordCount = wordMap.get(w);
				double wordPSpam; 
				double wordPHam; 
				if (wordCount == null) {
					wordPSpam = 0; 
					wordPHam = 0;
				} else { 
					wordPSpam = Math.log(wordCount.pGivenSpam); 
					wordPHam = Math.log(wordCount.pGivenHam); 
				}
				pSpam = pSpam + wordPSpam;
				pHam = pHam + wordPHam; 
			}
			boolean guessItsSpam = (pSpam > pHam); 
			if (targetIsSpam == guessItsSpam) {
				testCorrect++; 
			}
			testTotal++;
		}
		double accuracy = (double) testCorrect / testTotal; 
		return accuracy; 
	}
	

	// Reads testData to build wordMap of word frequencies and computer overall spam frequency 
	public static TotalCount processTrainingData(Map<String, WordCountSimple> wordMap) throws FileNotFoundException {
		TotalCount totalCount = new TotalCount(); 
		int totalSpamWords =  0; 
		int totalHamWords =  0; 
		int eduSpam = 0; 
		Scanner fileScanner = new Scanner(new File(TRAIN_DATA_FILE));
		// For each training email 
		while(fileScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			
			// Update overall counts 
			totalCount.total++;
			boolean spam = lineScanner.next().equals("1");
			if(spam) {
				totalCount.spamCount++;
			} else {
				totalCount.hamCount++; 
			}
			
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next();
				String[] parts = w.split(":");
				w = parts[1];
				WordCountSimple wordCount = wordMap.get(w);
				if (wordCount == null) {
					wordCount = new WordCountSimple(w); 
				 }
				if (spam) {
					wordCount.spamCount++;
					totalSpamWords++;
				} else {
					wordCount.hamCount++;
					totalHamWords++; 
				}
				wordMap.put(w, wordCount); 
			}
		}
		int vocabularySize = wordMap.size();
		
		// Compute probabilities for each word 
		for (Map.Entry<String, WordCountSimple> entry : wordMap.entrySet()){
			entry.getValue().computeProbabilities(M, totalSpamWords, totalHamWords, vocabularySize);

		}
		totalCount.computeProbability(); 
		// System.out.println(totalCount);
		return totalCount; 
	}

}
