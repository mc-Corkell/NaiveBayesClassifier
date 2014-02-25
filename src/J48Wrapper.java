
public class J48Wrapper implements Classifier {
	J48 tree; 
	
	@Override
	public double vote(Instance instance) {
		return tree.classifyInstance(instance);
	}
	
	// returns a J48 tree trained on the Instances 
	public J48Wrapper(Instances instances){ 
		tree = new J48(); 
		String[] options = new String[] {"-U"};
		tree.setOptions(options);
		tree.buildClassifier(instances); 
		return tree; 
	}

}
