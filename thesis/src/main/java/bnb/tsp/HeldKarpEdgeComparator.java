package bnb.tsp;

import java.util.Comparator;

public class HeldKarpEdgeComparator implements Comparator<Edge> {
	
	private final int[] nodeCosts;
	
	public HeldKarpEdgeComparator(int[] nodeCosts) {
		this.nodeCosts = nodeCosts;
	}

	@Override
	public int compare(Edge e1, Edge e2) {
		return (e1.cost() - nodeCosts[e1.node1.id] - nodeCosts[e1.node2.id]) - 
			(e2.cost() - nodeCosts[e2.node1.id] - nodeCosts[e2.node2.id]);
	}
}
