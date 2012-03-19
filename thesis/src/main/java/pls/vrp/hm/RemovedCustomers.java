package pls.vrp.hm;

import java.util.List;
import java.util.Set;

/**
 * An insertion point and a set of customers removed at it.
 */
public class RemovedCustomers {
	public RouteNode insertAfter;
	public Set<Integer> custIds;
	
	public RemovedCustomers(RouteNode insertAfter, Set<Integer> custIds) {
		this.insertAfter = insertAfter;
		this.custIds = custIds;
	}
}
