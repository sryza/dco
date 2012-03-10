package pls.vrp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VrpGreedyInitializer {
	private static final double TIME_DIFF_WEIGHT = .4;
	private static final double DISTANCE_WEIGHT = .4;
	private static final double URGENCY_WEIGHT = .2;
	
	private double timeDiffWeight = TIME_DIFF_WEIGHT;
	private double distanceWeight = DISTANCE_WEIGHT;
	private double urgencyWeight = URGENCY_WEIGHT;
	
	public VrpGreedyInitializer(double timeDiffWeight, double distanceWeight, double urgencyWeight) {
		this.timeDiffWeight = timeDiffWeight;
		this.distanceWeight = distanceWeight;
		this.urgencyWeight = urgencyWeight;
	}
	
	/**
	 * Nearest neighbor heuristic from Solomon paper.
	 */
	public VrpSolution nearestNeighborHeuristic(VrpProblem problem) {
		List<List<Integer>> routes = new ArrayList<List<Integer>>();
		Set<Integer> remainingNodes = new HashSet<Integer>();
		for (int i = 0; i < problem.getNumCities(); i++) {
			remainingNodes.add(i);
		}
		
		List<Integer> curRoute = new ArrayList<Integer>();
		routes.add(curRoute);
		int curNodeId = -1;
		int curVisitTime = 0;
		int remCapacity = problem.getVehicleCapacity();
		while (remainingNodes.size() > 0) {
			int[] ret = findClosest(curNodeId, curVisitTime, remCapacity, remainingNodes, problem);
			int nextNodeId = ret[0];
			if (nextNodeId != -1) {
				remainingNodes.remove(nextNodeId);
				curRoute.add(nextNodeId);
				curNodeId = nextNodeId;
				curVisitTime = ret[1];
				remCapacity -= problem.getDemands()[nextNodeId];
			} else {
				curRoute = new ArrayList<Integer>();
				routes.add(curRoute);
				curVisitTime = 0;
				curNodeId = -1;
				remCapacity = problem.getVehicleCapacity();
			}
		}
		
		return new VrpSolution(routes, problem);
	}
	
	/**
	 * @param curLastId
	 * 		-1 if it's the depot
	 * @param curLastVisitTime
	 * @param curLastServiceTime
	 * @param remainingNodes
	 * @param problem
	 * @return
	 * 		array containing best node id and visit time
	 */
	private int[] findClosest(int curLastId, int curLastVisitTime, int remCapacity,
			Set<Integer> remainingNodes, VrpProblem problem) {
		
		int[] demands = problem.getDemands();
		int[] windowStartTimes = problem.getWindowStartTimes();
		int[] windowEndTimes = problem.getWindowEndTimes();
		int[][] distances = problem.getDistances();
		int[] distancesFromDepot = problem.getDistancesFromDepot();
		int[] serviceTimes = problem.getServiceTimes();
		int curLastServiceTime = (curLastId == -1) ? 0 : serviceTimes[curLastId];
		
		double bestVal = Integer.MAX_VALUE;
		int bestNodeId = -1;
		int bestNodeVisitTime = -1;
		
		//bj = time when service begins, for depot its 0
		Iterator<Integer> iter = remainingNodes.iterator();
		while (iter.hasNext()) {
			int nodeId = iter.next();
			if (demands[nodeId] > remCapacity) {
				continue;
			}
			
			int distance = (curLastId == -1) ? distancesFromDepot[nodeId] : distances[curLastId][nodeId];
			int minVisitTime = Math.max(windowStartTimes[nodeId], curLastVisitTime + curLastServiceTime + distance);
			if (minVisitTime > windowEndTimes[nodeId]) {
				continue;
			}
			int timeDiff = minVisitTime - (curLastVisitTime + curLastVisitTime);
			int urgency = windowEndTimes[nodeId] - (curLastVisitTime + curLastServiceTime + distance);
			double val = timeDiff * timeDiffWeight + distance * distanceWeight + urgency * urgencyWeight;
			if (val < bestVal) {
				bestVal = val;
				bestNodeId = nodeId;
				bestNodeVisitTime = minVisitTime;
			}
		}
		
		return new int[] {bestNodeId, bestNodeVisitTime};
	}
}
