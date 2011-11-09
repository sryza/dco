package bnb;

import java.util.concurrent.atomic.AtomicInteger;

import bnb.rpc.Byteable;

public abstract class BnbNode implements Byteable {
	private BnbNode parent;
	protected AtomicInteger activeChildCount;
	
	/**
	 * A BnbNode should have an empty constructor after which initFromBytes can be called
	 */
	
	public BnbNode(BnbNode parent) {
		this.parent = parent;
	}
	
	/**
	 * when we send out a child, increment a counter
	 * when a child is done, decrement the counter
	 * if childDone() is called, and hasNextNode() is false, call childDone on our parent
	 */
	
	public void childDone() {
		int newCount = activeChildCount.decrementAndGet();
		if (newCount == 0 && !hasNextChild()) {
			//let our parent node know we're done
			getParent().childDone();
		}
	}
	
	public BnbNode getParent() {
		return parent;
	}
	
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
	
	public abstract void initFromBytes(byte[] bytes, Problem problem);
}
