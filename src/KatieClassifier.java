import weka.core.Instance;

public interface KatieClassifier {
	
	// Returns 1 if example is classified as part of + class 
	public double vote(Instance instance); 
	
}
