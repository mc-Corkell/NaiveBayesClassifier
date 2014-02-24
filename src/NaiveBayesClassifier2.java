import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner; 
import java.util.Set;
import java.util.TreeSet;


public class NaiveBayesClassifier2 {
	
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
			if(Double.isNaN(pSpam) || Double.isNaN(pHam)) {
				System.out.println("NAN POCALYPSE"); 
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
		Set<String> spamSet = new TreeSet<String>(); 
		Set<String> hamSet = new TreeSet<String>(); 
		Scanner fileScanner = new Scanner(new File(TRAIN_DATA_FILE));
		// For each training email 
		while(fileScanner.hasNextLine()) {
			String email = fileScanner.nextLine(); 
			Scanner lineScanner = new Scanner(email);
			lineScanner.next(); // throw away the email id
			boolean spam = lineScanner.next().equals("spam");
			if(spam) {
				spamSet.add(email); 
			} else {
				hamSet.add(email); 
			}
		} 
		
		totalCount.spamCount = spamSet.size();
		totalCount.hamCount = spamSet.size(); 
		totalCount.computeProbability(); 
		// System.out.println(totalCount);
		
		// For the ham Set 
		int nHamWords =  0; 
		for(String email : hamSet){
			Scanner lineScanner = new Scanner(email); 
			lineScanner.next(); lineScanner.next(); // throw away the email id & spam descriptor
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next(); 
				int count = lineScanner.nextInt(); 
				WordCount wordCount = wordMap.get(w);
				if (wordCount == null) {
					wordCount = new WordCount(w); 
				}
				nHamWords += count; 
				wordCount.hamTally += count;
				wordCount.originalHamTally++; 
				wordCount.total++; 
				wordMap.put(w, wordCount); 
			}
		}
		
		// For the spam Set 
		int nSpamWords =  0; 
		for(String email : spamSet){
			Scanner lineScanner = new Scanner(email); 
			lineScanner.next(); lineScanner.next(); // throw away the email id & spam descriptor
			// For each word in the email  
			while (lineScanner.hasNext()){
				String w = lineScanner.next(); 
				int count = lineScanner.nextInt(); 
				WordCount wordCount = wordMap.get(w);
				if (wordCount == null) {
					wordCount = new WordCount(w); 
				}
				nSpamWords += count; 
				wordCount.spamTally += count;
				wordCount.originalSpamTally++; 
				wordCount.total++; 
				wordMap.put(w, wordCount); 
			}
		}
		int vocabularySize = wordMap.size(); 
		for (Map.Entry<String, WordCount> entry : wordMap.entrySet()){
			entry.getValue().computeProbabilitiesLaPlace(nSpamWords, nHamWords, vocabularySize);
		}
		
		return totalCount; 
	}
}
