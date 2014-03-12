
public class WordCount {
	public double pGivenSpam;
	public double pGivenHam;
	public String word; 
	public double spamCount; 
	public double hamCount; 
	
	public void computeProbabilities(double m, int totalSpam, int totalHam, int vocabSize){
			double p = 1.0/vocabSize;
			pGivenSpam = (spamCount + (m*p))/ (totalSpam + m);
			pGivenHam = (hamCount + (m*p))/ (totalHam + m);
		//  System.out.println("word " + word + " pGivenSpam: " + pGivenSpam + " pGivenHam " + pGivenHam); 
	}

	public WordCount(String w) {
		word = w; 
	}

}
