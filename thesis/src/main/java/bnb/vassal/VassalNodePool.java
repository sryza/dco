package bnb.vassal;

import java.util.List;

import bnb.BnbNode;

public interface VassalNodePool {
	
	/**
	 * Removes nodes from the pool to be given to another process.
	 */
	public List<BnbNode> stealNodes();
	
	/**
	 * Returns the next node for the vassal to work on and removes
	 * it from the pool.
	 */
	public BnbNode nextNode();
	
	/**
	 * Posts an evaluated node to the pool.
	 */
	public void postEvaluated(BnbNode node);
}
