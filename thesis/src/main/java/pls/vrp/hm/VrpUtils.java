package pls.vrp.hm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pls.vrp.VrpProblem;

public class VrpUtils {
	
	/**
	 * @param newNode
	 * 		should have minDepartTime set correctly
	 * @return
	 * 		a list of affected nodes
	 */
	public static List<RouteNode> propagateMinDepartTime(RouteNode newNode, VrpProblem problem) {
		int[] serviceTimes = problem.getServiceTimes();
		int[] windowStartTimes = problem.getWindowStartTimes();
		
		List<RouteNode> affectedList = new ArrayList<RouteNode>();
		
		RouteNode curNode = newNode.next;
		while (curNode.custId != -1) { //while we haven't reached depot
			int newCurNodeMinDepartTime = Math.max(curNode.prev.minDepartTime + 
					problem.getDistance(curNode.prev.custId, curNode.custId), windowStartTimes[curNode.custId]) +
					serviceTimes[curNode.custId];
			if (newCurNodeMinDepartTime == curNode.minDepartTime) {
				//if nothing's changed here, nothing's gonna change in the future
				break;
			}
			curNode.minDepartTime = newCurNodeMinDepartTime;
			affectedList.add(curNode);
			curNode = curNode.next;
		}
		
		return affectedList;
	}
	
	/**
	 * 
	 * @param newNode
	 * 		Should have its maxVisitTime set correctly
	 * @param problem
	 * @return
	 */
	public static List<RouteNode> propagateMaxVisitTime(RouteNode newNode, VrpProblem problem) {
		int[] serviceTimes = problem.getServiceTimes();
		int[] windowEndTimes = problem.getWindowEndTimes();
		
		List<RouteNode> affectedList = new ArrayList<RouteNode>();
		
		RouteNode curNode = newNode.prev;
		while (curNode.custId != -1) {
			//not sure this is right
			int newCurNodeMaxDepartTime = curNode.next.maxArriveTime - problem.getDistance(curNode.next.custId, curNode.custId);
			int newCurNodeMaxArriveTime = Math.min(windowEndTimes[curNode.custId], 
					newCurNodeMaxDepartTime - serviceTimes[curNode.custId]);
			if (newCurNodeMaxArriveTime == curNode.maxArriveTime) {
				break;
			}
			curNode.maxArriveTime = newCurNodeMaxArriveTime;
			affectedList.add(curNode);
			curNode = curNode.prev;
		}
		return affectedList;
	}
	
	public static int costOfInsertion(int custIdBefore, int custIdAfter, int custIdToInsert, VrpProblem problem) {
		return problem.getDistance(custIdBefore, custIdToInsert) + 
			problem.getDistance(custIdToInsert, custIdAfter) - 
			problem.getDistance(custIdBefore, custIdAfter);
	}
	
	/**
	 * Validates the customers that are insertable at a given point.
	 * 
	 * @param custsIter
	 * @param custBefore
	 * 		The predecessor to a would be inserted node. -1 if it's the depot.
	 * @param custAfter
	 * 		The successor to a would be inserted node. -1 if it's the depot.
	 * @param minDepartTime
	 * 		The new minimum depart time for predecessor.
	 * @param minVisitTime
	 * 		The new maximum visit time for successor.
	 * @param remove
	 * 		If true, then pruned custs will be removed from the original list, and the removed list will be returned.
	 * 		Otherwise, the returned list will be those that remain.
	 * @return
	 */
	public static Set<Integer> validateInsertableCusts(Iterator<Integer> custsIter, int custBefore, int custAfter, int minDepartTime, 
			int maxVisitTime, VrpProblem problem, boolean remove) {
		
		int[] serviceTimes = problem.getServiceTimes();
		int[] windowStartTimes = problem.getWindowStartTimes();
		int[] windowEndTimes = problem.getWindowEndTimes();
		int[][] distances = problem.getDistances();
		int[] distsFromBefore = (custBefore == -1) ? problem.getDistancesFromDepot() : distances[custBefore];
		int[] distsFromAfter = (custAfter == -1) ? problem.getDistancesFromDepot() : distances[custAfter];
		Set<Integer> list = new HashSet<Integer>();
		
		while (custsIter.hasNext()) {
			int custId = custsIter.next();
			boolean insertable = true;
			int custMinArriveTime = minDepartTime + distsFromBefore[custId];
			if (custMinArriveTime > windowEndTimes[custId]) {
				insertable = false;
			} else {
				int custMinDepartTime = Math.max(windowStartTimes[custId], custMinArriveTime) + serviceTimes[custId];
				if (custMinDepartTime + distsFromAfter[custId] > maxVisitTime) {
					insertable = false;
				}
			}
			if (!insertable && remove) {
				custsIter.remove();
				list.add(custId);
			} else if (insertable && !remove) {
				list.add(custId);
			}
		}
		
		return list;
	}
	
	public static int calcMinDepartTime(int prevMinDepartTime, int dist, int windowStartTime, int serviceTime) {
		return Math.max(prevMinDepartTime + dist, windowStartTime) + serviceTime;
	}
	
	public static int calcMaxArriveTime(int nextMaxArriveTime, int dist, int windowEndTime, int serviceTime) {
		return Math.min(nextMaxArriveTime - dist - serviceTime, windowEndTime);
	}
}
