
public class WordCount {
	public int spamTally;
	public int hamTally;
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
	
	public static final double alpha = 1.0; 
	
	public void computeProbabilitiesLaPlace(int totalSpam, int totalHam, int vocabSize){
		pGivenSpam = (spamTally + alpha)/(totalSpam + (alpha*vocabSize));
		pGivenHam = (hamTally + alpha)/(totalHam + (alpha*vocabSize));
//		System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	}

	public WordCount(String w) {
		word = w; 
	}

}
