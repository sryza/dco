package pls.vrp.hm;

import java.util.List;
import java.util.Set;

/**
 * A node in a vehicle routing problem CP search tree.
 */
public class VrpCpSearchNode {
	public Set<Integer> unrouted;
	public CustInsertionPoints[] custsInsertionPoints;
	public BoundRemaining boundRemaining;
	public double curCost;
	public List<RouteNode> routeStarts;
	
	public VrpCpSearchNode(Set<Integer> unrouted, CustInsertionPoints[] custsInsertionPoints, 
			BoundRemaining boundRemaining, double curCost, List<RouteNode> routeStarts) {
		this.unrouted = unrouted;
		this.custsInsertionPoints = custsInsertionPoints;
		this.curCost = curCost;
		this.boundRemaining = boundRemaining;
		this.routeStarts = routeStarts;
	}
}
