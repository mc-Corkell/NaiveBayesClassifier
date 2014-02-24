import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


public class BaggingEnsemble {
	
	public static final String TRAIN_DATA = "molecular-biology_promoters_train.arff.arff";
	public static final String TEST_DATA = "molecular-biology_promoters_test.arff.arff"; 
	
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 1) {
			System.out.println("\tPlease give the number of samplings as an integer argument!!!");
			return; 
		}
		int numSamplings; 
		try {
			numSamplings = Integer.parseInt(args[0]);
	    }
	    catch( Exception e ) {
			System.out.println("\t The argument you gave was not an integer. Try again :)");
	        return;
	    }

		// Train
		Set<Classifier> classifierSet = new HashSet<Classifier>(); 
		Set<String> trainData = parseData(TRAIN_DATA); 
		for(int i=0; i<numSamplings; i++) {
			classifierSet.add(makeClassifier(trainData)); 
		}
		
		// Test
		Accuracy accuracy = new Accuracy(); 
		Set<String> testData = parseData(TEST_DATA); 
		int trueCount = 0; 
		for(String example: testData) {
			boolean realYes = (example.charAt(0) == '+'); 
			if(realYes) { trueCount++;}
			accuracy.total++;
			int yesVotes = 0; 
			for(Classifier model: classifierSet) {
				yesVotes += model.vote(example);
			}
			double majority = (double) yesVotes / classifierSet.size(); 
			boolean majorityVoteYes = (majority > .5);
			if(majorityVoteYes == realYes) {
				accuracy.correct++; 
			}
		}		
		accuracy.calculatePercent(); 
		System.out.println(accuracy); 
	}

	private static Set<String> parseData(String fileName) throws FileNotFoundException {
		Set<String> data = new TreeSet<String>(); 
		Scanner fileScanner = new Scanner(new File(fileName)); 
		while(fileScanner.hasNextLine()) { 
			String line = fileScanner.nextLine();
			if(line.length() > 1) {
				char startChar = line.charAt(0);
				if(startChar == '+' || startChar == '-') {
					data.add(line);
				}
			}
		}
		return data;
	}

	private static Classifier makeClassifier(Set<String> input) {
		ID3 tree = new ID3(); 
		return tree;
	}

}
