package pls.vrp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a solution or partial solution to the vehicle routing problem.
 */
public class VrpSolution {
	//values of -1 point to the depot
	private List<List<Integer>> routes;
	private List<Integer> unrouted;
	private int numVehicles;
	private VrpProblem problem;
	private int toursCost = -1;
	
	public VrpSolution(List<List<Integer>> routes, VrpProblem problem) {
		this.routes = routes;
		this.problem = problem;
		this.numVehicles = routes.size();
	}
	
	public VrpSolution(List<List<Integer>> routes, List<Integer> unroutedNodes, VrpProblem problem) {
		this(routes, problem);
		this.unrouted = unroutedNodes;
	}
	
	private int calcToursCost(List<List<Integer>> routes, VrpProblem problem) {
		int[][] distances = problem.getDistances();
		int[] distancesFromDepot = problem.getDistancesFromDepot();
		
		int toursCost = 0;
		for (List<Integer> route : routes) {
			Iterator<Integer> iter = route.iterator();
			if (!iter.hasNext()) {
				continue;
			}
			int prev = iter.next();
			toursCost += distancesFromDepot[prev];
			while (iter.hasNext()) {
				int cur = iter.next();
				toursCost += distances[prev][cur];
				prev = cur;
			}
			toursCost += distancesFromDepot[prev];
		}
		return toursCost;
	}
	
	public VrpProblem getProblem() {
		return problem;
	}
	
	public List<List<Integer>> getRoutes() {
		return routes;
	}
	
	public List<Integer> getUninsertedNodes() {
		return unrouted;
	}
	
	public int getNumVehicles() {
		return numVehicles;
	}
	
	/**
	 * Verify that the solution satisfied the constraints for the given problem,
	 * and that the reported values for the objective function are correct.
	 */
	public boolean verify(VrpProblem problem) {
		int[][] distances = problem.getDistances();
		int[] distancesFromDepot = problem.getDistancesFromDepot();
		
//		int toursCost = calcToursCost(routes, problem);
		
		int[] windowStartTimes = problem.getWindowStartTimes();
		int[] windowEndTimes = problem.getWindowEndTimes();
		int[] serviceTimes = problem.getServiceTimes();
		int[] demands = problem.getDemands();
		boolean[] visited = new boolean[problem.getNumCities()];
		
		//follow the paths and make sure that the time constraints hold
		for (List<Integer> route : routes) {
			Iterator<Integer> iter = route.iterator();
			if (route.isEmpty()) {
				System.out.println("EMPTY ROUTE!!!");
				continue;
			}
			int prev = iter.next();
			if (windowEndTimes[prev] < distancesFromDepot[prev]) {
				System.out.println("first node violated time constraint: endTime=" + 
						windowEndTimes[prev] + ", dist=" + distancesFromDepot[prev]);
				return false;
			}
			visited[prev] = true;
			int remCapacity = problem.getVehicleCapacity() - demands[prev];
			int minVisitTime = Math.max(windowStartTimes[prev], distancesFromDepot[prev]);
			
			while (iter.hasNext()) {
				int cur = iter.next();
				visited[cur] = true;
				int nextMinVisitTime = Math.max(minVisitTime + serviceTimes[prev] + distances[prev][cur], windowStartTimes[cur]);
				if (nextMinVisitTime > windowEndTimes[cur]) {
					System.out.println(minVisitTime + "\t" + serviceTimes[prev] + "\t" + distances[prev][cur]);
					System.out.println("violated time constraint for " + prev + "->" + cur + 
							": endTime=" + windowEndTimes[cur] + ", visitTime=" + nextMinVisitTime);
					return false;
				}
				minVisitTime = nextMinVisitTime;
				
				remCapacity -= demands[cur];
				if (remCapacity < 0) {
					System.out.println("violated capacity constraint");
					return false;
				}
				prev = cur;
			}
		}	
		
		for (boolean b : visited) {
			if (!b) {
				System.out.println("one of the nodes not visited");
				return false;
			}
		}
		
//		if (toursCost != this.toursCost) {
//			System.out.println("tour costs do not match: " + toursCost + " != " + this.toursCost);
//			return false;
//		}
		
		return true;
	}
	
	public int getToursCost() {
		if (toursCost != -1) {
			return toursCost;
		} else {
			return toursCost = calcToursCost(routes, problem);
		}
	}
	
	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(routes.size());
		for (List<Integer> route : routes) {
			dos.writeInt(route.size());
			for (int custId : route) {
				dos.writeInt(custId);
			}
		}
	}
	
	public static VrpSolution fromStream(DataInputStream dis, VrpProblem problem) throws IOException {
		int numRoutes = dis.readInt();
		List<List<Integer>> routes = new ArrayList<List<Integer>>(numRoutes);
		for (int i = 0; i < numRoutes; i++) {
			int numCusts = dis.readInt();
			List<Integer> route = new ArrayList<Integer>(numCusts);
			routes.add(route);
			for (int j = 0; j < numCusts; j++) {
				route.add(dis.readInt());
			}
		}
		return new VrpSolution(routes, problem);
	}
}
