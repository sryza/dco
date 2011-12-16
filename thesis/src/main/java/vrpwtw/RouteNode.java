package vrpwtw;

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
}
