import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource; 
import weka.core.Attribute;
import weka.classifiers.Classifier;
import weka.classifiers.trees.Id3;


public class BaggingEnsemble {
	
	public static final String TRAIN_DATA = "molecular-biology_promoters_train.arff.arff";
	public static final String TEST_DATA = "molecular-biology_promoters_test.arff.arff"; 
	
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 1) {
			System.out.println("\tPlease give the number of samplings as an integer argument!!!");
			return; 
		}
		int bagSize; 
		try {
			bagSize = Integer.parseInt(args[0]);
	    }
	    catch( Exception e ) {
			System.out.println("\t The argument you gave was not an integer. Try again.");
	        return;
	    }
//		int[] bagSizes = new int[]{1, 3, 5, 10, 20};
		int runs = 100; 
		System.out.println("Bagging Ensembles");
//		for(int i=0; i<bagSizes.length; i++){
			bagSize = bagSizes[i];
			double averageAccuracy = 0; 
			for(int j=0; j<runs; j++){
				Instances trainInstances = wekaParseData(TRAIN_DATA); 
				Set<Classifier> classifierSet = buildBag(trainInstances, bagSize); 
				Instances testInstances = wekaParseData(TEST_DATA); 
				double accuracy = testData(testInstances, classifierSet); 
				averageAccuracy += accuracy;
			}
			averageAccuracy = averageAccuracy/runs;
			System.out.println(runs + "\t\t" + bagSize + "\t\t" + averageAccuracy);
	//    }
		}
	}

	// Creates a new Instances set by random sampling with replacement
		private static Instances bootstrap(Instances input) {
			int size = input.numInstances();
			Random random = new Random(); 
			Instances newSet = new Instances(input, size); // creates empty set with same header as old set
			for(int i=0; i<size; i++) {
				int r = random.nextInt(size);
				try {
				    Thread.sleep(0, 1);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				newSet.add(input.instance(r));
			}
			return newSet;
		}
		
	// Builds and trains bag of n classifiers based on Instances file 
	public static Set<Classifier> buildBag(Instances trainInstances, int n) {
		Set<Classifier> classifierSet = new HashSet<Classifier>(); 
		for(int i=0; i<n; i++) {
			Instances trainInstanceSample = bootstrap(trainInstances);
			try {
				Classifier c = new Id3(); 
				String[] options = new String[]{"-U"}; // no pruning 
				c.setOptions(options);
				c.buildClassifier(trainInstanceSample);
				classifierSet.add(c);
			} catch (Exception e) {
				System.out.println("There was a problem building tree " + i + " in the bag. Uh oh."); 
			} 
		}
		return classifierSet; 
	}
		
	// Creates a new Instances set by random sampling with replacement
	private static Instances bootstrap(Instances input) {
		int size = input.numInstances();
		Random random = new Random(); 
		Instances newSet = new Instances(input, size); // creates empty set with same header as old set
		for(int i=0; i<size; i++) {
			int r = random.nextInt(size);
			try {
			    Thread.sleep(1);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			newSet.add(input.instance(r));
		}
		return newSet;
	}

	// Test each example in TEST_DATA file against the classifierSet
	// Vote by majority 
	// Prints overall ensemble accuracy 
	public static double testData(Instances testInstances, Set<Classifier> classifierSet) {
		Accuracy accuracy = new Accuracy(); 
		Attribute yesClass = new Attribute("+"); 
		double trueCount = 0; 
		for(int i=0; i<testInstances.numInstances(); i++) {
			Instance instance = testInstances.instance(i); 
			double thisInstanceValue = instance.classValue(); 
			trueCount += thisInstanceValue;
			boolean thisInstanceTrue = (thisInstanceValue == 1.0);
			accuracy.total++;
			double yesVotes = 0; 
			for(Classifier c: classifierSet) {
				double vote = 0.0;
				try {
					vote = c.classifyInstance(instance);
				} catch (Exception e) {
					System.out.println("There was a problem testing an instance w/ a tree");
				}
				yesVotes += vote; 
			}
			double majority = yesVotes / classifierSet.size(); 
			boolean majorityVoteYes = (majority > .5);
			if(majorityVoteYes == thisInstanceTrue) {
				accuracy.correct++; 
			}
		}		
		accuracy.calculatePercent(); 
		return accuracy.a; 
	}

	// Returns Instances class for given arff file 
	public static Instances wekaParseData(String file) {
		DataSource source = null;
		try {
			source = new DataSource(file);
		} catch (Exception e) {
			System.out.println("There was a problem with importing your file " + file); 
		} 
		 Instances data = null;
		try {
			data = source.getDataSet();
		} catch (Exception e) {
			System.out.println("There was a problem turning your file into a weka Instances. This file was the problem: " + file); 
		} 
		 // setting class attribute
		 if(data.classIndex() == -1) {
			 int classAttributeIndex = 0; // data.numAttributes() - 1
			 data.setClassIndex(classAttributeIndex);
		 }
		 return data; 
	}
	
}
