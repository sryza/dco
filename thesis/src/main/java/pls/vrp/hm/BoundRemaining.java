package pls.vrp.hm;

/**
 * Computes a bound on the cost of inserting the remaining uninserted nodes.
 */
public class BoundRemaining {
	
	private double[] minInsertionCosts;
	private double bound;
	
	public BoundRemaining(int numCusts) {
		minInsertionCosts = new double[numCusts];
	}
	
	public void updateMinInsertionCost(int custId, double minCost) {
		bound = bound - minInsertionCosts[custId] + minCost;
		minInsertionCosts[custId] = minCost;
	}
	
	public void notifyCustInserted(int custId) {
		bound -= minInsertionCosts[custId];
	}
	
	public void notifyCustReverted(int custId) {
		bound += minInsertionCosts[custId];
	}
	
	public double getBound() {
		return bound;
	}
}
