
public class WordCount {
	public int spamTally;
	public int hamTally;
	public int total;
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
	
	public void computeProbabilities(){
		pGivenSpam = (double) spamTally/total;
		pGivenHam = (double) hamTally/total;
	}
	
	public WordCount(String w) {
		word = w; 
	}

}
