package bnb;

public interface BnbNode {
	
	/**
	 * Should have an empty constructor after which this can be called.
	 */
	public void initFromBytes(byte[] bytes);
	
	public byte[] toBytes();
	
	/**
	 * Runs bounding a solving on this node.  Returns a list of the child nodes
	 * produced in this way, in order of most preferred.
	 */
	public void evaluate(ProblemSpec spec, double bound);
	
	public boolean isEvaluated();
	
	public BnbNode nextChild();
	
	public boolean hasNextChild();
	
	public boolean isSolution();
	
	public double getCost();
	
	public Solution getSolution();
}
