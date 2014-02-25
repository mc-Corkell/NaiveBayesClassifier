
public interface Classifier {
	
	// Returns 1 if example is classified as part of + class 
	public double vote(String example); 
	
	public Classifier(Instances instances); 
}
