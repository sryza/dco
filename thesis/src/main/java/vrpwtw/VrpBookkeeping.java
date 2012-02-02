package vrpwtw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * We want to calculate the feasibility/cost of insertion for each customer at
 * each insertion point.
 * We also want to calculate the zs (latest time a vehicle can depart each
 * customer such that feasibility is preserved).
 * We want to be able to find the customer with the worst best cost of insertion.
 * 
 * When we carry out an insertion, we want to be able to recalculate the 
 * feasibilities, costs of insertion, and zs for all the affected customers.
 * Costs of insertion should only change for insertions at the same point.
 * To carry out the cost of insertion update, we need to be able to efficiently 
 * look up all customers that would be inserted at a particular point.  We
 * then check for feasibility/cost both before and after the inserted city.
 *
 * TODO: HOW DO WE BEST CONSIDER VEHICLE CAPACITIES
 * 
 * TODO: make sure we're consistent with all the bounds
 */
public class VrpBookkeeping {
	
	/**
	 * TODO: First node in route should probably always be depot
	 */
	
	private static final Logger LOG = Logger.getLogger(VrpBookkeeping.class);
	
	//RouteNode is the node after which the node would be inserted
	//lists in this structure are ordered by cost of insertion
//	private Map<RouteNode, List<Customer>> insertionsByNode;
	private Map<RouteNode, InsertionList> insertionsByPoint;
	private Map<Customer, InsertionList> insertionsByCust;
	private VrpProblem problem;
	
	//for every customer we need to keep a list of insertion points, sorted by cost
	//when we insert something, we need to modify the insertion points in the customer lists
	//when we 
	
	
	/**
	 * 
	 * @param startNodes
	 * 		starting nodes (all at the depot)
	 * @param custs
	 * 		customers to insert
	 * @param toursCost
	 * 		the combined cost of all the tours before any insertions
	 * @param bestCost
	 * 		the objective function that we're trying to beat, used to prune
	 */
	public void precomputeInsertionPoints(List<RouteNode> startNodes, int[] routeFullnesses, 
			List<Customer> custs, int toursCost, int bestCost) {
		
		//compute minDepartTime for each customer
		for (RouteNode routeStart : startNodes) {
			routeStart.minDepartTime = routeStart.customer.getWindowStart() + routeStart.customer.getServiceTime();
			RouteNode routeNode = routeStart.next;
			while (routeNode != routeStart) {
				int minArriveTime = routeNode.prev.minDepartTime + routeNode.prev.customer.dist(routeNode.customer);
				routeNode.minDepartTime = minDepartTime(minArriveTime, routeNode.customer);
				routeNode = routeNode.next;
			}
		}
		
		//compute maxDepartTime for each customer
		for (RouteNode routeStart : startNodes) {
			RouteNode routeNode = routeStart.prev;
			routeNode.maxDepartTime = routeNode.customer.getWindowEnd() + routeNode.customer.getServiceTime();
			while (routeNode != routeStart) {
				routeNode = routeNode.prev;
				int maxDepartTime = Math.min(routeNode.customer.getWindowEnd() + 
						routeNode.customer.getServiceTime(),
						routeNode.next.maxDepartTime - routeNode.next.customer.getServiceTime() -
						routeNode.customer.dist(routeNode.next.customer));
				routeNode.maxDepartTime = maxDepartTime;
			}
		}
			
		for (Customer cust : custs) {
			//for each route
			int routeId = -1;
			for (RouteNode routeStart : startNodes) {
				routeId++;
				//if customer can't fit in route, skip route
				if (routeFullnesses[routeId] + cust.getDemand() > problem.getVehicleCapacity()) {
					continue;
				}
				
				//for each node in the route
				RouteNode routeNode = routeStart;
				do {
					//here consider inserting cust after routeNode
					
					//calculate dists
					int distToCust = routeNode.customer.dist(cust);
					int distFromCust = cust.dist(routeNode.next.customer);
					
					//check time constraints
					int minArriveTime = routeNode.minDepartTime + distToCust;
					if (minArriveTime >= cust.getWindowEnd()) {
						continue;
					}
					//calculate cust's minDepartTime
					int minDepartTime = Math.max(minArriveTime, cust.getWindowStart()) + cust.getServiceTime();
					
					//TODO: what if routeNode.next is the last node, is maxDepartTime correct
					if (minDepartTime + distFromCust + routeNode.next.customer.getServiceTime()
							>= routeNode.next.maxDepartTime) {
						continue;
					}
					
					//calculate insertion cost
					int insertionCost = distToCust + distFromCust -
						routeNode.customer.dist(routeNode.next.customer);
					
					//skip if insertion cost makes the objective function too high
					if (toursCost + insertionCost >= bestCost) {
						continue;
					}
					
					//add it to list of possible insertions
					InsertionList byCustList = insertionsByCust.get(cust);
					InsertionList byPointList = insertionsByPoint.get(routeNode);
					InsertionListsNode insertion = new InsertionListsNode(cust, routeNode);
					byCustList.add(insertion);
					byPointList.add(insertion);
				} while ((routeNode = routeNode.next) != routeStart);
			}
		}
		
		//TODO: now sort the lists in the way they should be sorted
		//TODO: this can be done in parallel if it helps
	}
	
	public InsertionList getMaxCheapestInsertionCustomer() {
		return null;
	}
	
	/**
	 * Returns the RouteNode containing the inserted customer.
	 * @param cust
	 * 		the customer to insert
	 * @param node
	 * 		the node to insert the customer after
	 */
	public void insert(InsertionListsNode insertion, RouteNode routeStart) {
		RouteNode node = insertion.insertionPoint;
		Customer cust = insertion.customer;
		
		//update possible insertions data structure
		InsertionList custList = insertionsByCust.remove(cust);
		InsertionList pointList = insertionsByPoint.remove(node);
		//TODO: gotta save custList
		
		//perform insertion
		RouteNode newNode = new RouteNode(cust, node.next, node);
		node.next.prev = newNode;
		node.next = newNode;
		//calc minDepartTime and maxDepartTime for new node
		int newNodeMinArriveTime = node.minDepartTime + node.customer.dist(cust);
		newNode.minDepartTime = minDepartTime(newNodeMinArriveTime, cust);
		newNode.maxDepartTime = maxDepartTime(cust, newNode.next.customer, newNode.next.maxDepartTime);
		List<InsertionListsNode> prunedInsertions = new LinkedList<InsertionListsNode>();
		
		//remove insertions at the point and place them into new lists if feasible
		InsertionList before = new InsertionList(InsertionList.POINT);
		//are we sure we want to use node and not a new one?  could conflict with updating minDepartTimes
		//TODO: use a new node
		insertionsByPoint.put(node, before);
		InsertionList after = new InsertionList(InsertionList.POINT);
		insertionsByPoint.put(newNode, after);
		Iterator<InsertionListsNode> pointListIter = pointList.iterator();
		while (pointListIter.hasNext()) {
			InsertionListsNode curInsertion = pointListIter.next();
			//remember to insert into both point list and cust list
			boolean canInsertAfter = insertionFeasibleForward(curInsertion.customer, newNode, newNode.next);
			if (canInsertAfter) {
				InsertionListsNode afterInsertion = new InsertionListsNode(curInsertion.customer, newNode);
				after.add(afterInsertion);
			}
			boolean canInsertBefore = insertionFeasibleBackward(curInsertion.customer, node, newNode);
			if (canInsertBefore) {
				before.add(curInsertion);
			}
			if (!canInsertBefore && !canInsertAfter) {
				prunedInsertions.add(curInsertion);
			}
		}
		
		//go forwards to update minDepartTimes
		//at each insertion point, see whether this eliminates any feasible insertions
		RouteNode curNode = newNode;
		do {
			//check time constraints
			int minArriveTime = curNode.prev.minDepartTime + curNode.prev.customer.dist(curNode.customer);
			if (minArriveTime >= cust.getWindowEnd()) {
				LOG.error("invalid insertion when we expected it to be valid, something's wrong");
				continue;
			}
			//calculate cust's new minDepartTime
			int minDepartTime = minDepartTime(minArriveTime, curNode.customer);
			//if minDepartTime isn't changed, no need to keep going
			if (curNode != newNode && minDepartTime == curNode.minDepartTime) {
				break;
			}
			
			curNode.minDepartTime = minDepartTime;
			//prune/modify insertable data structure
			InsertionList previouslyInsertable = insertionsByPoint.get(curNode); // TODO: what if curNode is newNode?  it won't have an entry
			Iterator<InsertionListsNode> iter = previouslyInsertable.iterator();
			while (iter.hasNext()) {
				InsertionListsNode curInsertion = iter.next();
				Customer insertCust = curInsertion.customer;
				//if no longer insertable, prune it
				if (!insertionFeasibleForward(insertCust, curNode, curNode.next)) {
					iter.remove();
					prunedInsertions.add(curInsertion);
				}
			}
		} while ((curNode = curNode.next) != routeStart); //TODO: any edge conditions here?
		
		//TODO: are we handling insertion at the end?
		
		//update maxDepartTimes
		curNode = newNode;
		while ((curNode = curNode.prev) != null){//TODO: this is wrong, just did this to get it to compile {
			int maxDepartTime = maxDepartTime(curNode.customer, curNode.next.customer, curNode.next.maxDepartTime);
			//if maxDepartTime isn't changed, no need to keep going
			if (curNode != newNode && maxDepartTime == curNode.maxDepartTime) {
				break;
			}
			curNode.maxDepartTime = maxDepartTime;
			
			//TODO: prune 
			//prune/modify insertable data structure
			InsertionList previouslyInsertable = insertionsByPoint.get(curNode); // TODO: what if curNode is newNode?  it won't have an entry
			Iterator<InsertionListsNode> iter = previouslyInsertable.iterator();
			while (iter.hasNext()) {
				InsertionListsNode curInsertion = iter.next();
				Customer insertCust = curInsertion.customer;
				//if no longer insertable
				if (!insertionFeasibleBackward(insertCust, curNode, curNode.next)) {
					iter.remove();
					prunedInsertions.add(curInsertion);
				}
			}
		}
		
		//if any customers have no insertion points, return false
	}
	
	/**
	 * Given an insertion point into a tour and a customer to insert into it (both specified by
	 * a route node), looks at what was previously insertable there and figures out what
	 * is still feasible to insert on either side of the new node.
	 * 
	 * We need to split the insertion list at that point into two different insertion lists.
	 * @param insertions
	 * 		customers that are able to be inserted at the insertion point before 
	 * 		insertedNode.customer is inserted
	 * @param cost
	 * 		the new cost of the tour after inserting insertedNode
	 * @param bestCost
	 * 		the current bestCost which we must not exceed
	 * 
	 *TODO: consider the edge cases
	 */
	private void pruneInsertions(RouteNode insertedNode, int cost, int bestCost, List<Customer> insertions,
			List<Customer> pruned, List<Customer> afterOk, List<Customer> beforeOk) {
		//for each customer at the given insertion point
		
		//TODO: consider capacity
		
		for (Customer cust : insertions) {
			boolean beforeFeasible = insertionFeasible(insertedNode.prev, insertedNode, cust, bestCost-cost);
			boolean afterFeasible = insertionFeasible(insertedNode, insertedNode.next, cust, bestCost-cost);
			if (beforeFeasible) {
				beforeOk.add(cust);
			}
			if (afterFeasible) {
				afterOk.add(cust);
			}
			if (!beforeFeasible && !afterFeasible) {
				pruned.add(cust);
			}
		}
	}
	
	/**
	 * Tests whether it's feasible to insert Customer insertCust between node1 and node2
	 * 
	 * The thinking behind this method is that when a node is inserted, feasibility for other nodes is only needed to
	 * be tested in the direction of the other nodes from the node.
	 * 
	 * @param node1
	 * @return
	 */
	private boolean insertionFeasibleForward(Customer insertCust, RouteNode node1, RouteNode node2) {
		int insertCustMinArriveTime = node1.minDepartTime + node1.customer.dist(insertCust);
		if (insertCustMinArriveTime >= insertCust.getWindowEnd()) {
			return false;
		}
		int insertCustMinDepartTime = minDepartTime(insertCustMinArriveTime, node2.customer);
		int nextMinArriveTime = insertCustMinDepartTime + insertCust.dist(node2.customer);
		if (nextMinArriveTime >= node2.customer.getWindowEnd()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Determines the feasibility of inserting cust between prev and next.
	 * @param
	 * 		if inserting would increase the objective function by costSlack or more, return false
	 */
	private boolean insertionFeasible(RouteNode prev, RouteNode next, Customer cust, int costSlack) {
		int costOfInsertion = prev.customer.dist(cust) + cust.dist(next.customer) - 
			prev.customer.dist(next.customer);
		if (costOfInsertion >= costSlack) {
			return false;
		}
		
		int minArriveTime = prev.minDepartTime + prev.customer.dist(cust); //for cust
		if (minArriveTime >= cust.getWindowEnd()) {
			return false;
		}
		int minDepartTime = minDepartTime(minArriveTime, cust);
		if (minDepartTime + cust.dist(next.customer) >= next.customer.getWindowEnd()) {
			return false;
		}
		
		int maxDepartTime = maxDepartTime(cust, next.customer, next.maxDepartTime); //for cust
		if (maxDepartTime - cust.getServiceTime() < cust.getWindowStart()) {
			return false;
		}
		int prevMaxDepartTime = maxDepartTime(prev.customer, cust, maxDepartTime);
		if (prevMaxDepartTime - prev.customer.getServiceTime() < prev.customer.getWindowStart()) {
			return false;
		}
		
		return true;
	}
	
	private boolean insertionFeasibleBackward(Customer insertCust, RouteNode node1, RouteNode node2) {
		return false;
	}

	
	private int minDepartTime(int minArriveTime, Customer cust) {
		return Math.max(cust.getWindowStart(), minArriveTime) + cust.getServiceTime();
	}
	
	private int maxDepartTime(Customer cust, Customer next, int nextMaxDepartTime) {
		return Math.min(cust.getWindowEnd() + 
				cust.getServiceTime(),
				nextMaxDepartTime - next.getServiceTime() -
				cust.dist(next));
	}
	
	/**
	 * Undoes an insertion, i.e. removes the node from the route
	 * @param custList
	 * 		list of points that insertion's customer could be inserted at
	 * @param node
	 */
	public void uninsert(InsertionListsNode insertion, InsertionList custList, InsertionList pointList, 
			List<InsertionListsNode> prunedInsertions) {
		//update insertion data structures
		Customer cust = insertion.customer;
		insertionsByCust.put(cust, custList);
		pointList.addBack(insertion);
		
		
	}
	
	public boolean equals(VrpBookkeeping other) {
		return false;
	}
	
	public VrpBookkeeping copy() {
		return null;
	}
}
