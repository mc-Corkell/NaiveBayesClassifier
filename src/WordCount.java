
public class WordCount {
	public int spamTally;
	public int hamTally;
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
	public double[] spamCount; 
	public double[] hamCount; 
	
	public void computeProbabilities(int totalSpam, int totalHam, int vocabSize){
		for(int i=0; i<spamCount.length; i++) {			
		    spamCount[i] = (spamCount[i])/(spamTally);
			hamCount[i] = (hamCount[i])/(hamTally); 
		}
	}

	public WordCount(String w) {
		word = w; 
		spamCount = new double[4];
		hamCount = new double[4];
	}

}
