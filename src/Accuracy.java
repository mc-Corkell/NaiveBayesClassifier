// This is a simple class for tallying accuracy 
public class Accuracy {
	public int correct;
	public int total; 
	public double a; 
	
	public double calculatePercent() {
		a = (double) correct / total; 
		return a; 
	}
	
	@Override
	public String toString() {
		return ("Accuracy: " + a); 
	}
}
