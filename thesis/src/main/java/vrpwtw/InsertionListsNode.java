package vrpwtw;

public class InsertionListsNode {
	public final Customer customer;
	public final RouteNode insertionPoint;
	
	public InsertionListsNode nextInCustList;
	public InsertionListsNode prevInCustList;
	public InsertionListsNode nextInPointList;
	public InsertionListsNode prevInPointList;
	
	public InsertionListsNode(Customer customer, RouteNode insertionPoint) {
		this.customer = customer;
		this.insertionPoint = insertionPoint;
	}
	
	public void addToLists(InsertionList custList, InsertionList pointList) {
		throw new IllegalStateException("not yet implemented");
	}
}
