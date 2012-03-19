package pls.vrp.hm;

/**
 * Computes a bound on the cost of inserting the remaining uninserted nodes.
 */
public class BoundRemaining {
	
	private int[] minInsertionCosts;
	private int bound;
	
	public BoundRemaining(int numCusts) {
		minInsertionCosts = new int[numCusts];
	}
	
	public void updateMinInsertionCost(int custId, int minCost) {
		bound = bound - minInsertionCosts[custId] + minCost;
		minInsertionCosts[custId] = minCost;
	}
	
	public void notifyCustInserted(int custId) {
		bound -= minInsertionCosts[custId];
	}
	
	public void notifyCustReverted(int custId) {
		bound += minInsertionCosts[custId];
	}
	
	public int getBound() {
		return bound;
	}
}
