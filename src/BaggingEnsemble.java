import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource; 
import java.io.BufferedReader;
import java.io.FileReader;


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
		Instances trainInstances = wekaParseData(TRAIN_DATA); 
		Set<Classifier> classifierSet = new HashSet<Classifier>(); 
		for(int i=0; i<numSamplings; i++) {
			classifierSet.add(new J48Wrapper(trainInstances)); 
		}
		
		// Test
		Accuracy accuracy = new Accuracy(); 
		Instances testInstances = wekaParseData(TEST_DATA); 
		int trueCount = 0; 
		for(Instance instance: testInstances) {
			boolean realYes = (instance.charAt(0) == '+'); 
			if(realYes) { trueCount++;}
			accuracy.total++;
			int yesVotes = 0; 
			for(Classifier model: classifierSet) {
				yesVotes += model.vote(instance);
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

	
	public static Instances wekaParseData(String file) {
		 DataSource source = new Datasource(file); 
		 Instances data = source.getDataSet(); 
		 // setting class attribute
		 if(data.classIndex() == -1) {
			 int classAttributeIndex = 0; // data.numAttributes() - 1
			 data.setClassIndex(classAttributeIndex);
		 }
		 return data; 
	}
	
}
