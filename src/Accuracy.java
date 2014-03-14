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
		return ("Accuracy: " + round2(a)); 
	}
	
	private double round2(double in) {
		return Math.round(in * 10000.0) / 10000.0;
	}
}

