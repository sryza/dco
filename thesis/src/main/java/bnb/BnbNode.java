package bnb;

public abstract class BnbNode {
	
	/**
	 * Should have an empty constructor after which this can be called.
	 */
	public void initFromBytes(byte[] bytes);
	
	public byte[] toBytes();
	
	/**
	 * Runs bounding a solving on this node.  Returns a list of the child nodes
	 * produced in this way, in order of most preferred.
	 */
	public abstract void evaluate(ProblemSpec spec, double bound);
	
	public abstract boolean isEvaluated();
	
	public abstract BnbNode nextChild();
	
	public abstract boolean hasNextChild();
	
	public abstract boolean isSolution();
	
	public abstract double getCost();
	
	public abstract Solution getSolution();
}
