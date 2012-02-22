package bnb;

import java.util.concurrent.atomic.AtomicInteger;

import bnb.rpc.Byteable;

public abstract class BnbNode implements Byteable {
	private BnbNode parent;
	protected AtomicInteger activeChildCount = new AtomicInteger(0);
	
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
			whenAllChildrenDone();
			if (getParent() != null) {
				getParent().childDone();
			}
		}
		
		//TODO: it's really here that we want to add city back to remainingCities
		//city should be added back to whatever list it was taken from when
		//no further children will be processed
		
	}
	
	public BnbNode getParent() {
		return parent;
	}
	
	/**
	 * Code to be executed when the last of the children is done being computed on
	 */
	public abstract void whenAllChildrenDone();
	
	/**
	 * Runs bounding a solving on this node.  Returns a list of the child nodes
	 * produced in this way, in order of most preferred.
	 */
	public abstract void evaluate(double bound);
	
	public abstract boolean isEvaluated();
	
	/**
	 * Will only be called from within a synchronized block.
	 */
	public abstract BnbNode nextChild(boolean alwaysCopy);
	
	public abstract boolean hasNextChild();
	
	public abstract boolean isLeaf();
	
	public abstract boolean isSolution();
	
	public abstract double getCost();
	
	public abstract Solution getSolution();
	
	public abstract void initFromBytes(byte[] bytes, Problem problem);
	
	/**
	 * Returns false when the node shouldn't be stolen (probably because it's too small
	 * to be worth it).
	 */
	public abstract boolean dontSteal();
	
	/**
	 * For logging purposes.
	 */
	public int getDepth() {
		return -1;
	}
}
