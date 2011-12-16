package vrpwtw;

import java.util.LinkedList;
import java.util.List;
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
 */
public class VrpBookkeeping {
	
	private static final Logger LOG = Logger.getLogger(VrpBookkeeping.class);
	
	// array index is customer id
	// should order by cost of insertion
	// could be a TreeMap<Integer (cost), List<RouteNode>>
	private Set<RouteNode>[] insertionPoints;
	private VrpProblem problem;
	
	private Stack<List<RouteNode>> removedCustsStack;
	
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
					
					//TODO: add it to list of possible insertions
					
				} while ((routeNode = routeNode.next) != routeStart);
			}

		}
	}
	
	
	
	/**
	 * Returns the RouteNode containing the inserted customer.
	 * @param cust
	 * 		the customer to insert
	 * @param node
	 * 		the node to insert the customer after
	 */
	public void insert(Customer cust, RouteNode node) {
		//perform insertion
		RouteNode newNode = new RouteNode(cust, node.next, node);
		node.next.prev = newNode;
		node.next = newNode;
		
		List<RouteNode> prunedInsertions = new LinkedList<RouteNode>();
		
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
			int minDepartTime = Math.max(minArriveTime, curNode.customer.getWindowStart()) + 
				curNode.customer.getServiceTime();
			//if minDepartTime isn't changed, no need to keep going
			if (curNode != newNode && minDepartTime == curNode.minDepartTime) {
				break;
			}
			
			curNode.minDepartTime = minDepartTime;
			//TODO: remove insertions here
		} while ((curNode = curNode.next) != routeStart); //TODO: any edge conditions here?
		
		//TODO: are we handling insertion at the end?
		
		//update maxDepartTimes
		curNode = newNode;
		do {
			int maxDepartTime = Math.min(curNode.customer.getWindowEnd() + 
					curNode.customer.getServiceTime(),
					curNode.next.maxDepartTime - curNode.next.customer.getServiceTime() -
					curNode.customer.dist(curNode.next.customer));
			//if maxDepartTime isn't changed, no need to keep going
			if (curNode != newNode && maxDepartTime == curNode.maxDepartTime) {
				break;
			}
			curNode.maxDepartTime = maxDepartTime;
			
			//TODO: prune 
		} while ((curNode = curNode.prev) != );
		
		//if any customers have no insertion points, return false
	}
	
	/**
	 * We can represent an insertion with a (disconnected RouteNode).
	 * insertedNode was inserted into a tour. what possible insertions does it remove?
	 * return them
	 */
	private List<RouteNode> pruneInsertions(RouteNode insertedNode) {
		
	}
	
	/**
	 * Undoes an insertion, i.e. removes the node from the route
	 * .
	 * @param node
	 */
	public void uninsert(RouteNode node) {
		
	}
}
