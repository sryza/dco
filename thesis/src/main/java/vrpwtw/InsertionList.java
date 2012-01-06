package vrpwtw;

import java.util.Iterator;

public class InsertionList {
	
	public static final boolean CUST = true;
	public static final boolean POINT = false;
	
	private InsertionListsNode head;
	private InsertionListsNode tail;
	private final boolean custOrPoint; //true -> cust
	
	public InsertionList(boolean custOrPoint) {
		this.custOrPoint = custOrPoint;
	}
	
	public void add(InsertionListsNode node) {
		//TODO: assert that all pointers are null
		if (head == null) {
			head = tail = node;
		} else {
			if (custOrPoint) {
				node.prevInCustList = tail;
				tail.nextInCustList = node;
			} else {
				node.prevInPointList = tail;
				tail.nextInPointList = node;
			}
			
			tail = node;
		}
	}
	
	public void addBack(InsertionListsNode node) {
		
	}
	
	public void remove(InsertionListsNode node) {
		if (custOrPoint) {
			if (node.nextInCustList != null) {
				node.nextInCustList.prevInCustList = node.prevInCustList;
			}
			if (node.prevInCustList != null) {
				node.prevInCustList.nextInCustList = node.nextInCustList;
			}
			if (node == head) {
				head = node.nextInCustList;
			}
			if (node == tail) {
				tail = node.prevInCustList;
			}
		} else {
			if (node.nextInPointList != null) {
				node.nextInPointList.prevInPointList = node.prevInPointList;
			}
			if (node.prevInPointList != null) {
				node.prevInPointList.nextInPointList = node.nextInPointList;
			}
		}
	}
	
	public InsertionListsNode getHead() {
		return head;
	}
	
	public boolean equals(InsertionList other) {
		if (this.custOrPoint != other.custOrPoint) {
			return false;
		}
		Iterator<InsertionListsNode> myIter = iterator();
		Iterator<InsertionListsNode> otherIter = ((InsertionList)other).iterator();
		while (myIter.hasNext()) {
			if (!otherIter.hasNext()) { //different sizes
				return false;
			}
			
			InsertionListsNode myNode = myIter.next();
			InsertionListsNode otherNode = otherIter.next();
			if (!myNode.customer.equals(otherNode.customer)) {
				return false;
			}
			if (!myNode.insertionPoint.equals(otherNode.insertionPoint)) {
				return false;
			}
		}
		if (otherIter.hasNext()) { //different sizes
			return false;
		}
		return true;
	}
	
	public Iterator<InsertionListsNode> iterator() {
		return new InsertionListIter();
	}
	
	private class InsertionListIter implements Iterator<InsertionListsNode> {
		
		private InsertionListsNode cur = head;
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public InsertionListsNode next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
	}
}
