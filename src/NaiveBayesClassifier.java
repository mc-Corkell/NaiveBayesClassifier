import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner; 


public class NaiveBayesClassifier {
	
	public static final String TRAIN_DATA_FILE = "data/train";
	public static final String TEST_DATA_FILE = "data/test";

	
	public static void main(String[] args) throws FileNotFoundException {
		Map<String, WordCount> wordMap = new HashMap<String, WordCount>(); 
		TotalCount totalCount = processTrainingData(wordMap);
		testData(wordMap, totalCount); 
	}
	
	public static void testData(Map<String, WordCount> wordMap, TotalCount totalCount) throws FileNotFoundException {
		int testCorrect = 0; 
		int testTotal = 0; 
		Scanner fileScanner = new Scanner(new File(TEST_DATA_FILE)); 
		// For Each Test Email 
		while(fileScanner.hasNextLine()){
			double pSpam = totalCount.pSpam; 
			double pHam = totalCount.pHam; 
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.next(); // throw away email id
			boolean targetIsSpam = lineScanner.next().equals("spam"); 
	
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
					wordPSpam = wordCount.pGivenSpam; 
					wordPHam = wordCount.pGivenHam; 
				}
				wordPSpam = Math.pow(wordPSpam, count);
				wordPHam = Math.pow(wordPHam, count);
				pSpam = pSpam * wordPSpam;
				pHam = pHam * wordPHam; 
			}
			if(Double.isNaN(pHam) || Double.isNaN(pSpam)) { 
				System.out.println("testTotal " + testTotal);
				System.out.println("pHam: " + pHam  + " pSpam: " + pSpam); 
			} 
			boolean guessItsSpam = (pSpam > pHam); 
			if (targetIsSpam == guessItsSpam) {
				testCorrect++; 
			}
			testTotal++;
		}
		double accuracy = (double) testCorrect / testTotal; 
		System.out.println("accuracy " + accuracy); 
	}
	
	
	public static TotalCount processTrainingData(Map<String, WordCount> wordMap) throws FileNotFoundException {
		TotalCount totalCount = new TotalCount(); 
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
				wordCount.total++;
				if (spam) {
					wordCount.spamTally++;
				} else {
					wordCount.hamTally++; 
				}
				wordMap.put(w, wordCount); 
			}
		}
		// Compute probabilities for each word 
		for (Map.Entry<String, WordCount> entry : wordMap.entrySet()){
			entry.getValue().computeProbabilities();
		}
		// System.out.println("number of words: " + wordMap.size());
		// System.out.println(totalCount);
		totalCount.computeProbabilities(); 
		return totalCount; 
	}
}
