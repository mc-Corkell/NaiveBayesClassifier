
public class WordCount {
	public int spamTally;
	public int hamTally;
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
	
	int total;
	int originalSpamTally; 
	int originalHamTally; 
	public static final double alpha = 1.0; 
	
	public void computeProbabilitiesLaPlace(int totalSpam, int totalHam, int vocabSize){
		pGivenSpam = (spamTally + alpha)/(totalSpam + (alpha*vocabSize));
		pGivenHam = (hamTally + alpha)/(totalHam + (alpha*vocabSize));
//		System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	}
	
	public void computeProbabilities(int totalSpam, int totalHam, int vocabSize){
		pGivenSpam = (double) spamTally/totalSpam;
		pGivenHam = (double) hamTally/totalHam;
//		System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	}
	
	public void computeProbabilitiesOriginal(int w, int h, int y){
		pGivenSpam = (double) (originalSpamTally+1)/total;
		pGivenHam = (double) (originalHamTally+1)/total;
	}
	
	public WordCount(String w) {
		word = w; 
	}

}
