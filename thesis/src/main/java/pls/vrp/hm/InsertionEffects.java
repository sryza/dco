package pls.vrp.hm;

import java.util.List;

public class InsertionEffects {
	public List<RemovedCustomers> removedCustsList;
	public RouteNode insertedNode;
	public boolean consistent;
	
	public InsertionEffects(RouteNode insertedNode, List<RemovedCustomers> removedCustsList, boolean consistent) {
		this.removedCustsList = removedCustsList;
		this.insertedNode = insertedNode;
		this.consistent = consistent;
	}
}
