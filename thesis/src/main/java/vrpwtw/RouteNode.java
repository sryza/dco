package vrpwtw;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class RouteNode {
	public final Customer customer;
	public RouteNode next;
	public RouteNode prev;
	//the earliest that the current path allows us to arrive at customer
	public int minDepartTime;
	//the latest that the current path allows us to depart and not violate
	//time windows in the future
	public int maxDepartTime;
	
	public RouteNode(Customer customer, RouteNode next, RouteNode prev) {
		this.customer = customer;
		this.next = next;
		this.prev = prev;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof RouteNode)) {
			return false;
		}
		RouteNode otherNode = (RouteNode)other;
		if (minDepartTime != otherNode.minDepartTime) {
			return false;
		}
		if (maxDepartTime != otherNode.maxDepartTime) {
			return false;
		}
		if (customer.getId() != otherNode.customer.getId()) {
			return false;
		}
		if ((next == null) != (otherNode.next == null )|| (prev == null) != (otherNode.prev == null)) {
			return false;
		}
		if (next != null) {
			if (otherNode.next.customer.getId() != next.customer.getId()) {
				return false;
			}
		}
		if (prev != null) {
			if (otherNode.prev.customer.getId() != prev.customer.getId()) {
				return false;
			}
		}
		return true;
	}
	
	private class InsertCostComparator implements Comparator<Customer> {
		private Customer prev;
		private Customer next;
		
		public InsertCostComparator(Customer cust1, Customer cust2) {
			this.prev = cust1;
			this.next = cust2;
		}
		
		@Override
		public int compare(Customer cust1, Customer cust2) {
			int cost1 = cust1.dist(prev) + cust1.dist(next);
			int cost2 = cust2.dist(prev) + cust2.dist(next);
			return cost1 - cost2;
		}
	}
}
