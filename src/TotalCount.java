
public class TotalCount {
	public int hamCount; 
	public int spamCount;
	public int total; 
	public double pSpam;
	public double pHam; 
	
	public void computeProbabilities() {
		pSpam = (double) spamCount/total;
		pHam = (double) hamCount/total; 
	}
	
	public String toString(){
		return ("total count: " + total + ", spamCount: " + spamCount + ", hamCount: " + hamCount);
	}
}
