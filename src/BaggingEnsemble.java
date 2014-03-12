import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
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
import weka.classifiers.trees.REPTree;


public class BaggingEnsemble {
	
	public static final String TRAIN_DATA = "molecular-biology_promoters_train.arff.arff";
	public static final String TEST_DATA = "molecular-biology_promoters_test.arff.arff"; 
	public static String MAX_DEPTH; 
	
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length != 1) {
			System.out.println("\tPlease give the maxDepth as an integer argument!!!");
			return; 
		}
		try {
			MAX_DEPTH = args[0];
	    }
	    catch( Exception e ) {
			System.out.println("\t There was an error w/ your argument. Try again.");
	        return;
	    }
		String[] maxDepths = new String[]{"1", "2", "3", "4", "5", "10", "20"};
		int bagSize = 30;  
		boolean bagging = true; //set to false if u just want to check out the decision tree by itself 
		int runs = 200; 
		System.out.println("Bagging Ensembles with " + runs + " runs");
		for(int i=0; i<maxDepths.length; i++){
			MAX_DEPTH = maxDepths[i]; 
			BVL overall = new BVL(); 
			for(int j=0; j<runs; j++){
				Instances trainInstances = wekaParseData(TRAIN_DATA); 
				ArrayList<Classifier> classifierSet = buildBag(trainInstances, bagSize, bagging); 
				Instances testInstances = wekaParseData(TEST_DATA); 
				BVL thisRun = testData(testInstances, classifierSet); 
				overall.addBVL(thisRun); 
			}
			overall.average(runs); 
			System.out.print("MAX_DEPTH: " + MAX_DEPTH + " ");
			System.out.println(overall); 
			System.out.println();
	   }
	}

		
	// Builds and trains bag of n classifiers based on Instances file 
	public static ArrayList<Classifier> buildBag(Instances trainInstances, int n, boolean bagging) {
		Random random = new Random(); 
		ArrayList<Classifier> classifierSet = new ArrayList<Classifier>(); 
		for(int i=0; i<n; i++) {
			Instances newSample; 
			if(!bagging) {
				newSample = trainInstances;
			} else { 
				newSample = trainInstances.resample(random);
			}
			try {
				Classifier c = new REPTree(); 
				String[] options = new String[]{"-P", "-L", MAX_DEPTH}; // no pruning , sets max depth to MAX_DEPTH 
				c.setOptions(options);
				c.buildClassifier(newSample);
				classifierSet.add(c);
			} catch (Exception e) {
				System.out.println("There was a problem building tree " + i + " in the bag. Uh oh."); 
			} 
		}
		return classifierSet; 
	}

	// Test each example in TEST_DATA file against the classifierSet
	// Vote by majority 
	// Prints overall ensemble accuracy 
	public static BVL testData(Instances testInstances, ArrayList<Classifier> classifierSet) {
		int[] testClasses = new int[testInstances.numInstances()];
		int[][] preds = new int[testInstances.numInstances()][classifierSet.size()];
		int[] prediction = new int[testInstances.numInstances()]; 
		Accuracy accuracy = new Accuracy(); 
		for(int i=0; i<testInstances.numInstances(); i++) {
			Instance instance = testInstances.instance(i); 
			double thisInstanceValue = instance.classValue();
			boolean thisInstanceTrue = (thisInstanceValue == 1.0);
			if(thisInstanceTrue) {
				testClasses[i] = 1;  // else 0, by default 
			}
			accuracy.total++;
			double yesVotes = 0.0; 
	//.	for(Classifier c: classifierSet) {
			for(int j=0; j<classifierSet.size(); j++){
				Classifier c = classifierSet.get(j); 
				double vote = 0.0;
				try {
					vote = c.classifyInstance(instance);
				} catch (Exception e) {
					System.out.println("There was a problem testing an instance w/ a tree");
				}
				if(Double.isNaN(vote)) {
					vote = 0.0; 
				}
				if(vote == 1.0) {
					preds[i][j] = 1; 
				} else{
					preds[i][j] = 0; 
				}
				yesVotes += vote; 
			}
			double majority = yesVotes / classifierSet.size(); 
			boolean majorityVoteYes = (majority > .5);
			if(majorityVoteYes == thisInstanceTrue) {
				accuracy.correct++; 
			}
			prediction[i] = majorityVoteYes? 1 : 0; 

		}	
		//System.out.println("Test Truth\t" + Arrays.toString(testClasses)); 
		//System.out.println("What we voted\t" + Arrays.toString(prediction)); 
		BVL biasVar = new BiasVarianceCalculator().biasVar(testClasses, preds, classifierSet.size(), 2); 
		accuracy.calculatePercent();
		biasVar.accuracy = accuracy.a; 

		return biasVar; 
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
