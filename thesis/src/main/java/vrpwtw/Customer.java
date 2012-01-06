package vrpwtw;

public class Customer {
	private int routeId = -1;
	private final int x;
	private final int y;
	private final int windowStart;
	private final int windowEnd;
	private final int demand;
	private final int serviceTime;
	private final int id;
	
	private Customer next;
	private Customer prev;
	
	public Customer(int x, int y, int id, int windowStart, int windowEnd, int serviceTime, int demand, int routeId) {
		this.x = x;
		this.y = y;
		this.routeId = routeId;
		this.windowStart = windowStart;
		this.windowEnd = windowEnd;
		this.serviceTime = serviceTime;
		this.demand = demand;
		this.id = id;
	}
	
	// TODO: make sure this gets used, it will need to.
	public Customer copy() {
		return null;
	}
	
	public Customer getPrev() {
		return prev;
	}
	
	public void setPrev(Customer prev) {
		this.prev = prev;
	}
	
	public Customer getNext() {
		return next;
	}
	
	public void setNext(Customer next) {
		this.next = next;
	}
	
	public int getDemand() {
		return demand;
	}
	
	public int getServiceTime() {
		return serviceTime;
	}
	
	public int getWindowStart() {
		return windowStart;
	}
	
	public int getWindowEnd() {
		return windowEnd;
	}
	
	public int getId() {
		return id;
	}
	
	public int dist(Customer other) {
		return (int)Math.sqrt(x*x + y*y);
	}
	
	public boolean equals(Object other) {
		Customer cust = (Customer)other;
		return id == cust.id;
	}
}
