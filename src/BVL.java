
public class BVL {
	public double bias; 
	public double var; //variance 
	public double loss; 
	public double accuracy; 
	
	public String toString() {
		double b = round2(bias); 
		double v = round2(var); 
		double l = round2(loss); 
		double a = round2(accuracy);
		return ("bias: " + b + "\t\tvar: " + v + "\t\tloss: " + l + "\t\t" ); // accuracy: " + accuracy); 
	}
	
	private double round2(double in) {
		return Math.round(in * 10000.0) / 10000.0;
	}
	
	// adds the values of other to this BVL 
	public void addBVL(BVL other) {
		this.bias += other.bias; 
		this.var += other.var;
		this.loss += other.loss; 
		this.accuracy += other.accuracy; 
	}
	
	// divides the values of this BVL 
	public void average(int runs) {
		bias = bias/runs;
		var = var/runs;
		loss = loss/runs; 
		accuracy = accuracy/runs; 
	}
}
