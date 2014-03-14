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
	
	public static void main(String[] args) throws Exception {
		Random r = new Random(); 
		String[] maxDepths = new String[]{"1", "2", "3", "5", "10", "20"};
		int bagSize = 30;  
		boolean bagging = true; //set to false if u just want to check out the decision tree by itself 
		int groupOfLearnersSize = 30; 
		Instances trainInstancesD = wekaParseData(TRAIN_DATA); 
		Instances testInstances = wekaParseData(TEST_DATA); 
		int numTest = testInstances.numInstances(); 
		for(int k=0; k < 2; k++) { // this loop determines if base learner is bagged tree or single tree
			int bagNoBag = k; 
			if(bagNoBag == 0) { 
				System.out.println("Using " + groupOfLearnersSize + " decision trees as base learner L ");
			}else if (bagNoBag == 1){ 
				System.out.println("Using " + groupOfLearnersSize + " bagged decision tree as base learner L "); 
			}
			System.out.println(); 
			for(int i=0; i<maxDepths.length; i++){
				MAX_DEPTH = maxDepths[i]; 
				ArrayList<ArrayList<Classifier>> groupOfLearners = new ArrayList<ArrayList<Classifier>>(); 
				for(int j=0; j<groupOfLearnersSize; j++){
					Instances trainInstancesDPrime = trainInstancesD.resample(r); 
					ArrayList<Classifier> classifierSet;
					if (bagNoBag == 0) {
						classifierSet = buildOneTree(trainInstancesDPrime);  
					} else {
						classifierSet = buildBag(trainInstancesDPrime, bagSize); 
					}
					groupOfLearners.add(classifierSet); 
				}
				int[] testClasses = new int[numTest];
				int[][] preds = new int[numTest][groupOfLearnersSize];
				Accuracy overallAccuracy = new Accuracy(); 
				for(int x = 0; x<numTest; x++) {
					Instance instanceX = testInstances.instance(x); 
					boolean thisInstanceTrue = (instanceX.classValue() == 1.0);
					if(thisInstanceTrue) {
						testClasses[x] = 1;  // else 0, by default 
					}
					for(int j=0; j<groupOfLearnersSize; j++) {
						ArrayList<Classifier> classifierSet = groupOfLearners.get(j); 
						int vote = testData(instanceX, classifierSet); 
						preds[x][j] = vote; 
						if(vote == testClasses[x]) {
							overallAccuracy.correct++;
						}
						overallAccuracy.total++; 
					}
				}
				BVL biasVar = new BiasVarianceCalculator().biasVar(testClasses, preds, groupOfLearners.size(), 2); 
				overallAccuracy.calculatePercent();

				System.out.print("MAX_DEPTH: " + MAX_DEPTH + " ");
				System.out.print(biasVar); 
				System.out.print(overallAccuracy); 
				System.out.println(); 
			}
			System.out.println(); 
		}
	}
	
	public static ArrayList<Classifier> buildOneTree(Instances trainInstancesDPrime) throws Exception {
		ArrayList<Classifier> classifierSet = new ArrayList<Classifier>(); 
		Classifier c = new REPTree(); 
		String[] options = new String[]{"-P", "-L", MAX_DEPTH}; // no pruning , sets max depth to MAX_DEPTH 
		c.setOptions(options);
		c.buildClassifier(trainInstancesDPrime);
		classifierSet.add(c);
		return classifierSet; 
	}

		
	// Builds and trains bag of n classifiers based on Instances file 
	public static ArrayList<Classifier> buildBag(Instances trainInstances, int n) {
		Random random = new Random(); 
		ArrayList<Classifier> classifierSet = new ArrayList<Classifier>(); 
		for(int i=0; i<n; i++) {
			Instances newSample; 
			newSample = trainInstances.resample(random);
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
	public static int testData(Instance instance, ArrayList<Classifier> classifierSet) {
		double yesVotes = 0.0; 
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
			yesVotes += vote; 
		}
		double majority = yesVotes / classifierSet.size(); 
		boolean majorityVoteYes = (majority > .5);
		int intVote = majorityVoteYes? 1 : 0; 
		return intVote; 
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
