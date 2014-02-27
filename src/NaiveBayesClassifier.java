import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NaiveBayesClassifier {
	
	public static final String TRAIN_DATA_FILE = "data/train";
	public static final String TEST_DATA_FILE = "data/test";

	public static double M; 

	public static void main(String[] args) throws FileNotFoundException {
		/*if(args.length != 1) {
			System.out.println("\tPlease pass in the soothing parameter!!!");
			return; 
		}
		ALPHA = Double.parseDouble(args[0]); */
		M = .00001; 
		System.out.println("Naive Bayes Classifier");
		System.out.println("M \t\t accuracy");
		for(int i=0; i<20; i++){
			M = M *10;
			Map<String, WordCount> wordMap = new HashMap<String, WordCount>(); 
			TotalCount totalCount = processTrainingData(wordMap);
			double accuracy = testData(wordMap, totalCount); 
			System.out.println(M  + "\t\t" + accuracy); 
		}
	}
	
	// Classifies each test email based on learner and calculates and prints overall accuracy 
	public static double testData(Map<String, WordCount> wordMap, TotalCount totalCount) throws FileNotFoundException {
		int testCorrect = 0; 
		int testTotal = 0; 
		Scanner fileScanner = new Scanner(new File(TEST_DATA_FILE)); 
		// For Each Test Email 
		int c = 0; 
		while(fileScanner.hasNextLine()){
			double pSpam = Math.log(totalCount.pSpam); 
			double pHam = Math.log(totalCount.pHam); 
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.next(); // throw away email id
			boolean targetIsSpam = lineScanner.next().equals("spam"); 
			c++;
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next(); 
				int count = lineScanner.nextInt(); 
				WordCount wordCount = wordMap.get(w);
				double wordPSpam; 
				double wordPHam; 
				if (wordCount == null) {
					wordPSpam = 0; 
					wordPHam = 0;
				} else { 
					wordPSpam = Math.log(wordCount.pGivenSpam); 
					wordPHam = Math.log(wordCount.pGivenHam); 
				}
				wordPSpam = wordPSpam * count;
				wordPHam = wordPHam * count;
				pSpam = pSpam + wordPSpam;
				pHam = pHam + wordPHam; 
			}
			// System.out.println("pHam : " + pHam + " pSpam " + pSpam);
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
	public static TotalCount processTrainingData(Map<String, WordCount> wordMap) throws FileNotFoundException {
		TotalCount totalCount = new TotalCount(); 
		int totalSpamWords =  0; 
		int totalHamWords =  0; 
		Scanner fileScanner = new Scanner(new File(TRAIN_DATA_FILE));
		// For each training email 
		while(fileScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.next(); // throw away the email id
			
			// Update overall counts 
			totalCount.total++;
			boolean spam = lineScanner.next().equals("spam");
			if(spam) {
				totalCount.spamCount++;
			} else {
				totalCount.hamCount++; 
			}
			
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next(); 
				int count = lineScanner.nextInt(); 
				WordCount wordCount = wordMap.get(w);
				if (wordCount == null) {
					wordCount = new WordCount(w); 
				 }
				if (spam) {
					wordCount.spamTally += count;
					totalSpamWords += count;
				} else {
					wordCount.hamTally += count; 
					totalHamWords += count; 
				}
				wordMap.put(w, wordCount); 
			}
		}
		int vocabularySize = wordMap.size();

		
		// Compute probabilities for each word 
		for (Map.Entry<String, WordCount> entry : wordMap.entrySet()){
			entry.getValue().computeProbabilities(M, totalSpamWords, totalHamWords, vocabularySize);

		}
		totalCount.computeProbability(); 
		// System.out.println(totalCount);
		return totalCount; 
	}

}
