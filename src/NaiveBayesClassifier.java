import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class NaiveBayesClassifier {
	
	public static final String TRAIN_DATA_FILE = "data/train";
	public static final String TEST_DATA_FILE = "data/test";
	public static final String EDU_TEST_EMAILS = "testEmailEduIds";
	public static final String EDU_TRAIN_EMAILS = "trainEmailEduIds";
	public static double pEduSpam; 

	public static double M; 

	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 1) {
			System.out.println("\tPlease pass in the soothing parameter!!!");
			return; 
		}
		M = Double.parseDouble(args[0]); 
		// M = .00001; 
		System.out.println("Naive Bayes Classifier");
		//for(int i=0; i<20; i++){
		//	M = M *10;
			Map<String, WordCount> wordMap = new HashMap<String, WordCount>(); 
			TotalCount totalCount = processTrainingData(wordMap);
			double accuracy = testData(wordMap, totalCount); 
			System.out.println("M \t\t accuracy");
			System.out.println(M  + "\t\t" + accuracy); 
		//}
	}
	
	// Classifies each test email based on learner and calculates and prints overall accuracy 
	public static double testData(Map<String, WordCount> wordMap, TotalCount totalCount) throws FileNotFoundException {
		int markedSpamisHam = 0; 
		int markedHamisSpam = 0;
		int testCorrect = 0; 
		int testTotal = 0; 
		Scanner fileScanner = new Scanner(new File(TEST_DATA_FILE)); 
		// For Each Test Email 
		int c = 0; 	
		Set<String> emails = getEduEmails(EDU_TEST_EMAILS); 
		while(fileScanner.hasNextLine()){
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			String emailId = lineScanner.next(); // throw away email id
			double pSpam = Math.log(totalCount.pSpam); 
			double pHam = Math.log(totalCount.pHam); 
			boolean eduWatch = emails.contains(emailId);
			if(eduWatch) {
				 pSpam = Math.log(pEduSpam);
				 pHam = Math.log(1- pSpam); 
			}
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
			boolean guessItsSpam = (pSpam > pHam); 
			if(eduWatch) {
			//	System.out.println("guessItsSpam: " + guessItsSpam + " targetIsSpam: " + targetIsSpam);
			//	System.out.println("pHam : " + pHam + " pSpam: " + pSpam);
				double diff = Math.abs( Math.abs(pHam-pSpam)/pSpam);
			//	System.out.println("(pHam-pSpam) / pSpam " + diff );
				//if(diff < .05) {
				guessItsSpam = false; 
			//	}
			}
			if(!targetIsSpam && guessItsSpam) {
				markedSpamisHam++;
			}
			if(targetIsSpam && !guessItsSpam) {
				markedHamisSpam++;
			}
			if (targetIsSpam == guessItsSpam) {
				testCorrect++; 
			}
			testTotal++;
		}
	//	System.out.println("Marked Spam but is Ham " + markedSpamisHam); 
	//	System.out.println("Marked Ham but is Spam " + markedHamisSpam); 
		double accuracy = (double) testCorrect / testTotal; 
		return accuracy; 
	}
	

	// Reads testData to build wordMap of word frequencies and computer overall spam frequency 
	public static TotalCount processTrainingData(Map<String, WordCount> wordMap) throws FileNotFoundException {
		Set<String> eduEmails = getEduEmails(EDU_TRAIN_EMAILS); 
	//	System.out.println("eduEmails.size() " + eduEmails.size()); 
		TotalCount totalCount = new TotalCount(); 
		int totalSpamWords =  0; 
		int totalHamWords =  0; 
		int eduSpam = 0; 
		Scanner fileScanner = new Scanner(new File(TRAIN_DATA_FILE));
		// For each training email 
		while(fileScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			String emailID = lineScanner.next(); 

			// Update overall counts 
			totalCount.total++;
			boolean spam = lineScanner.next().equals("spam");
			if(spam) {
				totalCount.spamCount++;
			} else {
				totalCount.hamCount++; 
			}
			if(eduEmails.contains(emailID)){
				if(spam) {
					eduSpam++; 
				}
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
		pEduSpam = ((double) eduSpam)/eduEmails.size(); 
	//	System.out.println(eduSpam + " spam emails from " + eduEmails.size() + " Edu addresses"); 
		
		// Compute probabilities for each word 
		for (Map.Entry<String, WordCount> entry : wordMap.entrySet()){
			entry.getValue().computeProbabilities(M, totalSpamWords, totalHamWords, vocabularySize);

		}
		totalCount.computeProbability(); 
		// System.out.println(totalCount);
		return totalCount; 
	}

	public static Set<String> getEduEmails(String filename) throws FileNotFoundException{
		Set<String> emails = new TreeSet<String>(); 
		Scanner fileScanner = new Scanner(new File(filename));
		while(fileScanner.hasNextLine()){
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			emails.add(lineScanner.next());
		}
		return emails; 
	}
}
