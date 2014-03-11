
public class BiasVarianceCalculator {

	// As adopted from code by Pedro Domingos: http://homes.cs.washington.edu/~pedrod/bvd.c
	/* 
	 * 
classes:  a vector containing the actual classes of the test examples,
          represented as integers; classes[i] is the class of test example i

preds:    an array where preds[i][j] is the class predicted for example i
          by the classifier learned on training set j; classes are represented
          as integers, consistent with those used in the classes vector

nTrainSets: the number of training examples used 

numClasses : the number of classes being considered

returns the Bias, Variance and Loss in a BVL object 

	 */
	public static BVL biasVar(int[] testClasses, int[][] preds, int nTrainSets, int numClasses) { 
		int MaxClasses = numClasses; 
		BVL overall = new BVL(); 
		
		for(int e=0; e<testClasses.length; e++) {
			BVL bvlX = biasVarX(testClasses[e], preds[e], nTrainSets, MaxClasses); 
			overall.loss += bvlX.loss; 
			overall.bias += bvlX.bias;
		}
		overall.loss = overall.loss/testClasses.length;
		overall.bias = overall.bias/testClasses.length;
		overall.var = overall.loss - overall.bias; 
		return overall; 
	} 
	
	public static BVL biasVarX(int classx, int[] predsx, int ntrsets, int MaxClasses) {
		int[] nclass = new int[MaxClasses]; 
		int majclass = -1; 
		int nmax = 0; 
		for(int t=0; t<ntrsets; t++){
			nclass[predsx[t]]++;
		}
		for(int c=0; c<MaxClasses; c++) {
			if(nclass[c] > nmax){ 
				majclass = c;
				nmax = nclass[c];
			}
		}
		BVL x = new BVL();
		x.loss = 1.0 - (double)nclass[classx] / ntrsets; 
		boolean b = (majclass!= classx);
		x.bias = b? 1 : 0;
		x.var = 1.0 - nclass[majclass] /ntrsets;
		return x; 
	}
}
