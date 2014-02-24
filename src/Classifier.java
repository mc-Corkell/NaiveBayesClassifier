
public interface Classifier {
	
	// Returns 1 if example is classified as part of + class 
	public int vote(String example); 
}
