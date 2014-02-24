
public class TotalCount {
	public int hamCount; 
	public int spamCount;
	public int total; 
	public double pSpam;
	public double pHam; 
	
	public void computeProbability() {
		total = hamCount + spamCount; 
		pSpam = (double) spamCount/total;
		pHam = (double) hamCount/total; 
	}
	
	@Override
	public String toString(){
		return ("total count: " + total + ", spamCount: " + spamCount + ", hamCount: " + hamCount + 
				"\n pSpam: " + pSpam + ", pHam: " + pHam);
	}
}
