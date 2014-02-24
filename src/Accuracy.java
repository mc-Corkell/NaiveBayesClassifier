
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
		return ("Accuracy is " + a); 
	}
}
