package pls.vrp.hm;

import java.util.Set;

public class RouteNode {
	public final int custId; //-1 if it's a depot node
	public RouteNode next;
	public RouteNode prev;
	//the earliest that the current path allows us to arrive at customer
	public int minDepartTime;
	//the latest that the current path allows us to depart and not violate
	//time windows in the future, - our service time
	public int maxArriveTime;
	public Route route;
	
	//ids of customers that can be inserted after this node
	public Set<Integer> insertableAfter;
	
	public RouteNode(int custId, RouteNode next, RouteNode prev, Route route) {
		this.custId = custId;
		this.next = next;
		this.prev = prev;
		this.route = route;
	}
	
	public int hashCode() {
		if (custId != -1) {
			return custId;
		} else {
			return super.hashCode();
		}
	}
}