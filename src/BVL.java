
public class BVL {
	public double bias; 
	public double var; //variance 
	public double loss; 
	public double accuracy; 
	
	public String toString() {
		return ("bias: " + bias + "\t\tvar: " + var + "\t\tloss: " + loss + "\t\t" ); // accuracy: " + accuracy); 
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
