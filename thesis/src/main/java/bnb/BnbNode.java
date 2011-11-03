package bnb;

import bnb.rpc.Byteable;

public abstract class BnbNode implements Byteable {
	
	/**
	 * Runs bounding a solving on this node.  Returns a list of the child nodes
	 * produced in this way, in order of most preferred.
	 */
	public abstract void evaluate(double bound);
	
	public abstract boolean isEvaluated();
	
	public abstract BnbNode nextChild();
	
	public abstract boolean hasNextChild();
	
	public abstract boolean isSolution();
	
	public abstract double getCost();
	
	public abstract Solution getSolution();
}
