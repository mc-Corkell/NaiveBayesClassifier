
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

	public WordCount(String w) {
		word = w; 
	}

}
