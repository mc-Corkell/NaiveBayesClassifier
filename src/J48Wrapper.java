import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;


public class J48Wrapper implements KatieClassifier {
	J48 tree; 
	
	@Override
	public double vote(Instance instance) {
		try {
			return tree.classifyInstance(instance);
		} catch (Exception e) {
			System.out.println("an error occurred when classifying this instance"); 
			return Double.MIN_VALUE; 
		}
	}
	
	// returns a J48 tree trained on the Instances 
	public J48Wrapper(Instances instances) throws Exception{ 
		tree = new J48(); 
		String[] options = new String[] {"-U"};
		tree.setOptions(options);
		tree.buildClassifier(instances); 
	}

}
