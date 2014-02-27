
public class WordCount {
	public int spamTally;
	public int hamTally;
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
		
	public void computeProbabilitiesLaPlace(double alpha, int totalSpam, int totalHam, int vocabSize){
		pGivenSpam = (spamTally + alpha)/ (totalSpam + (alpha*vocabSize));
		pGivenHam = (hamTally + alpha)/ (totalHam + (alpha*vocabSize));
	//  System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	
	}
	
	public void computeProbabilities(double m, int totalSpam, int totalHam, int vocabSize){
		double p = 1.0/vocabSize;
		pGivenSpam = (spamTally + (m*p))/ (totalSpam + m);
		pGivenHam = (hamTally + (m*p))/ (totalHam + m);
	//  System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	
	}

	public WordCount(String w) {
		word = w; 
	}

}
