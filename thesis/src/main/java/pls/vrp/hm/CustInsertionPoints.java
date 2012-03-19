package pls.vrp.hm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class CustInsertionPoints {
	private static final Logger LOG = Logger.getLogger(CustInsertionPoints.class);
	
	private BoundRemaining boundRemaining;
	
	//cost of inserting at node with id index. last is depot.
	private HashMap<RouteNode, Integer> costs;
	private int minCost;
	private TreeSet<InsertionPointCost> insertionPointCosts;
	private int custId;
	private int numCusts;
	
	public CustInsertionPoints(BoundRemaining boundRemaining, int custId, int numCusts) {
		insertionPointCosts = new TreeSet<InsertionPointCost>();
		this.boundRemaining = boundRemaining;
		this.custId = custId;
		this.costs = new HashMap<RouteNode, Integer>();
		this.numCusts = numCusts;
	}
		
	public Iterator<RouteNode> inCostOrderIterator() {
		return new InsertionPointIterator(insertionPointCosts.iterator());
	}
	
	public void add(RouteNode node, int cost) {
		if (insertionPointCosts.contains(new InsertionPointCost(node, cost))) {
			LOG.error("Already contains " + node.custId);
		}
		insertionPointCosts.add(new InsertionPointCost(node, cost));
		setCost(node, cost);
		propagateBound();
	}
	
	/**
	 * Returns false if there are none left
	 */
	public boolean remove(RouteNode node) {
		if (!insertionPointCosts.remove(new InsertionPointCost(node, getCost(node)))) {
			LOG.error("Removing an insertion point that is not there");
		}
		if (insertionPointCosts.isEmpty()) {
			return false;
		} else {
			propagateBound();
			return true;
		}
	}
	
	public void update(RouteNode node, int cost) {
		if (!insertionPointCosts.remove(new InsertionPointCost(node, getCost(node)))) {
			LOG.error("Update to element not already in insertion points");
		}
		insertionPointCosts.add(new InsertionPointCost(node, cost));
		setCost(node, cost);
		propagateBound();
	}
	
	private void setCost(RouteNode node, int cost) {
		costs.put(node,	cost);
	}
	
	private int getCost(RouteNode node) {
		return costs.get(node);
	}
	
	private void propagateBound() {
		int newMinCost = insertionPointCosts.first().cost;
		if (newMinCost != minCost) {
			boundRemaining.updateMinInsertionCost(custId, newMinCost);
			minCost = newMinCost;
		}
	}
	
	public boolean isEmpty() {
		return insertionPointCosts.isEmpty();
	}
	
	public int getMinCost() {
		return minCost;
	}
	
	protected TreeSet<InsertionPointCost> getInsertionPointCosts() {
		return insertionPointCosts;
	}
	
	@Override
	public String toString() {
		return insertionPointCosts.toString();
	}
	
	protected static class InsertionPointCost implements Comparable<InsertionPointCost> {
		public RouteNode insertAfter;
		public int cost;
		
		public InsertionPointCost(RouteNode node, int cost) {
			this.insertAfter = node;
			this.cost = cost;
//			TODO: if (cost < 0) {
//				throw new IllegalStateException("something's up, cost of insertion is " + cost);
//			}
		}
		
		@Override
		public int compareTo(InsertionPointCost other) {
			int costDiff = this.cost - other.cost;
//			return costDiff != 0 ? costDiff : this.insertAfter.custId - other.insertAfter.custId;
			return costDiff != 0 ? costDiff : this.insertAfter.hashCode() - other.insertAfter.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			InsertionPointCost other = (InsertionPointCost)o;
			return this.cost == other.cost && other.insertAfter.hashCode() == this.insertAfter.hashCode();
//			return this.cost == other.cost && other.insertAfter.custId == this.insertAfter.custId;
		}
		
		@Override
		public String toString() {
			return "[custId=" + insertAfter.custId + ", cost=" + cost + "]";
		}
	}
	
	private class InsertionPointIterator implements Iterator<RouteNode> {
		
		private Iterator<InsertionPointCost> wrapped;
		
		public InsertionPointIterator(Iterator<InsertionPointCost> wrapped) {
			this.wrapped = wrapped;
		}
		
		@Override
		public boolean hasNext() {
			return wrapped.hasNext();
		}

		@Override
		public RouteNode next() {
			return wrapped.next().insertAfter;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
