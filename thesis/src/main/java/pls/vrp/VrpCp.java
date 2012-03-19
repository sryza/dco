package pls.vrp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import JaCoP.constraints.Circuit;
import JaCoP.constraints.Element;
import JaCoP.constraints.Sum;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XgteqC;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XlteqC;
import JaCoP.constraints.XlteqY;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusYlteqZ;
import JaCoP.core.IntVar;
import JaCoP.core.Store;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.Indomain;
import JaCoP.search.IndomainList;
import JaCoP.search.IndomainMax;
import JaCoP.search.IndomainMin;
import JaCoP.search.IndomainRandom;
import JaCoP.search.PrintOutListener;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;

public class VrpCp {
	
	public VrpSolution solve(VrpProblem problem, VrpSolution partialSolution, int bound) {
		//gather info from problem
		int numVehicles = partialSolution.getNumVehicles();
		int numCities = problem.getNumCities();
		int vehicleCapacity = problem.getVehicleCapacity();
		int numDepots = numVehicles;
		int numNodes = numDepots + numCities;
		int[] demands = problem.getDemands();
		int[] windowStartTimes = problem.getWindowStartTimes();
		int[] windowEndTimes = problem.getWindowEndTimes();
		
		//build distance 2d arrays TODO: this can be its own function
		int[][] distances = new int[numNodes][numNodes];
		int[][] timeDists = new int[numNodes][numNodes];
		buildDistArrays(distances, timeDists, problem, numCities, numNodes);
		
		Store store = new Store();
		
		//the model
		IntVar[] nodePositions = new IntVar[numNodes]; //TODO: is alldifferent helpful here?
		IntVar[] nodesInOrder = new IntVar[numNodes];
		IntVar[] successors = new IntVar[numNodes];
		IntVar[] vehicles = new IntVar[numNodes];
		IntVar[] successorDistances = new IntVar[numNodes];
		
		//initialize IntVars
		for (int i = 0; i < numNodes; i++) {
			successors[i] = new IntVar(store, "successors[" + (i+1) + "]", 1, successors.length);
			nodePositions[i] = new IntVar(store, "positions[" + (i+1) + "]", 1, successors.length);
			nodesInOrder[i] = new IntVar(store, "order[" + (i+1) + "]", 1, successors.length);
			//TODO: might need to do something to make sure that different routes have different vehicles
			vehicles[i] = new IntVar(store, "vehicle of " + (i+1), 1, numVehicles);
			successorDistances[i] = new IntVar(store, "successor distance of " + (i+1), 0, Integer.MAX_VALUE);
		}
		IntVar toursCost = new IntVar(store, "toursCost", 0, bound);
		
		//impose constraints
		store.impose(new Circuit(successors));
		
		IntVar[] visitTimes = imposeTimeWindowConstraints(store, successors, numCities, numNodes, windowStartTimes, windowEndTimes, timeDists);
		imposeVehicleCapacityConstraints(store, vehicles, numCities, demands, numVehicles, vehicleCapacity);
		
		//define stuff for both depots and vehicles
		for (int i = 0; i < numNodes; i++) {
			//for calculating the objective function
			store.impose(new Element(successors[i], distances[i], successorDistances[i]));
			
			//tie nodesInOrder to successors
			//successors[nodesInOrder[i-1]] = nodesInOrder[i]
			//wrap around
			store.impose(new Element(nodesInOrder[(i-1+successors.length) % successors.length], successors, nodesInOrder[i]));
			
			//tie nodePositions to nodesInOrder
			//nodePositions[nodesInOrder[i]] = i
			store.impose(new Element(nodesInOrder[i], nodePositions, new IntVar(store, i+1, i+1))); //redundant
			store.impose(new Element(nodePositions[i], nodesInOrder, new IntVar(store, i+1, i+1))); //redundant
		}
		//first node is first depot
		store.impose(new XeqC(nodePositions[numCities], 1)); //redundant
		store.impose(new XeqC(nodesInOrder[0], numCities+1)); //redundant
		//order the depots to break symmetries
		for (int i = numCities; i < numNodes; i++) {
			//nodePositions[i] < nodePositions[i+1]]
			if (i+1 < numNodes) {
				store.impose(new XltY(nodePositions[i], nodePositions[i+1]));
			}
		}
		//break vehicle symmetries by ordering vehicles at depots
		//TODO: the approach below seems to be making things slower? and probably isn't necessary with lns
//		for (int i = numCities; i < numNodes; i++) {
//			store.impose(new XeqC(vehicles[i], i-numCities+1));
//		}
		
		for (int i = 0; i < numCities; i++) {
			//a node's successor must use the same vehicle as it - vehicles[succ[i]] = vehicles[i]
			//(this means that the last depot in a tour will have the vehicle, and the first vehicle won't)
			store.impose(new Element(successors[i], vehicles, vehicles[i]));
		}
		//successors of depot should have different vehicles
		for (int i = numCities; i < numNodes; i++) {
			IntVar depotSuccessorVehicle = new IntVar(store, 1, numVehicles);
			store.impose(new Element(successors[i], vehicles, depotSuccessorVehicle));
			store.impose(new XneqY(depotSuccessorVehicle, vehicles[i]));
		}
		//assign first node's vehicle as 1
		store.impose(new XeqC(vehicles[0], 1));
		
		//for calculating the objective function
		store.impose(new Sum(successorDistances, toursCost));
		
		//TODO: can use the values constraint to count the number of vehicles
		
		//TODO: do we need to do something to ensure that the visit times take their minimum values?
		//will need the min and max constraints
		
//		System.out.println("toursCost min: " + toursCost.dom().min());
		store.consistency();
//		System.out.println("toursCost min after consistency: " + toursCost.dom().min());
		imposePartialSolutionConstraints(partialSolution, store, nodePositions, vehicles, numCities);
//		System.out.println("toursCost after imposing partial solution constraints: " + toursCost.dom().min());
		store.consistency();
//		System.out.println("toursCost min after consistency: " + toursCost.dom().min());
		
		
		//carry out the search
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		ComparatorVariable<IntVar> varSelector = new SmallestDomain<IntVar>();
//		ComparatorVariable<IntVar> varTieBreaker = new MostConstrainedStatic<IntVar>();
//		Indomain<IntVar> valueSelector = new IndomainMax<IntVar>();//doesn't really matter
		//TODO: might be good to use IndomainRandom or IndomainSimpleRandom
		//test to find out difference??
		Indomain<IntVar> valueSelector = new IndomainRandom<IntVar>();//doesn't really matter
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(successors, varSelector, valueSelector);
//		store.setLevel(1);
//		System.out.println(store.level);
		//carry out the search!
		search.getSolutionListener().searchAll(true); 
		// record solutions; if not set false 
		search.getSolutionListener().recordSolutions(true); 
		search.setSolutionListener(new PrintOutListener<IntVar>());
		if (!search.labeling(store, select, toursCost)) {
			return null;
		}
//		search.labeling(store, select);
		
		System.out.println(toursCost.dom());
//		System.out.println("vehicles: " + Arrays.asList(vehicles));
		List<Integer> demandsList = new ArrayList<Integer>();
		for (int demand : demands) {
			demandsList.add(demand);
		}
//		System.out.println("demands: " + demandsList);
//		System.out.println("visitTimes: " + Arrays.asList(visitTimes));
//		System.out.println("capacity: " + vehicleCapacity);
		System.out.println("nodesInOrder: " + Arrays.asList(nodesInOrder));
		System.out.println("successors: " + Arrays.asList(successors));
		System.out.println("nodePositions: " + Arrays.asList(nodePositions));
//		for (IntVar nodePos : nodePositions) {
//			System.out.println(nodePos.dom());
//		}
		
		return resultsToSolution(nodesInOrder, numCities, problem);
	}
	
	private VrpSolution resultsToSolution(IntVar[] nodesInOrder, int numCities, VrpProblem problem) {
		List<List<Integer>> routes = new ArrayList<List<Integer>>();
		
		List<Integer> route = null;
		for (IntVar node : nodesInOrder) {
			int nodeId = node.dom().min()-1;
			if (nodeId >= numCities) {
				route = new LinkedList<Integer>();
				routes.add(route);
			} else {
				route.add(nodeId);
			}
		}
		return new VrpSolution(routes, problem);
	}
	
	/**
	 * @return
	 * 		visitTimes
	 */
	private IntVar[] imposeTimeWindowConstraints(Store store, IntVar[] successors, int numCities, int numNodes, 
			int[] windowStartTimes, int[] windowEndTimes, int[][] timeDists) {
		IntVar[] visitTimes = new IntVar[numNodes];
		IntVar[] successorVisitTimes = new IntVar[numNodes];
		IntVar[] successorTimeDistances = new IntVar[numNodes];
		
		for (int i = 0; i < numNodes; i++) {
			visitTimes[i] = new IntVar(store, "visit time of " + (i+1), 0, Integer.MAX_VALUE);
			successorVisitTimes[i] = new IntVar(store, "successor visit time of " + (i+1), 0, Integer.MAX_VALUE);
			successorTimeDistances[i] = new IntVar(store, "successor time distance of " + (i+1), 0, Integer.MAX_VALUE);
		}
		
		for (int i = 0; i < numNodes; i++) {
			store.impose(new Element(successors[i], visitTimes, successorVisitTimes[i]));
			store.impose(new Element(successors[i], timeDists[i], successorTimeDistances[i]));
		}
		
		for (int i = 0; i < numCities; i++) {
			//windowStartTime <= visitTime < windowEndTime
			store.impose(new XgteqC(visitTimes[i], windowStartTimes[i])); //eq?
			store.impose(new XlteqC(visitTimes[i], windowEndTimes[i])); //eq?

			//timeDists[n, succ[n]] + visitTime[n] <= visitTime[succ[n]]
			//(this means depots will have visit times after the last city in the tour,
			// but they won't be constrained to come before anything)
			store.impose(new XplusYlteqZ(successorTimeDistances[i], visitTimes[i], successorVisitTimes[i]));
		}

		//don't take the depots' visit times into account when determining their successors' visit times
		for (int i = numCities; i < numNodes; i++) {
			store.impose(new XlteqY(successorTimeDistances[i], successorVisitTimes[i]));
		}
		
		return visitTimes;
	}
	
	/**
	 * Sets precedence constraints on fixed nodes in a partial solution.
	 * @param nodePositions
	 * 		the variables representing the node positions in the tour, the last of which
	 * 		are the depots
	 * @param vehicles
	 * 		so we can set the first vehicle on each route. necessary? TODO
	 */
	private void imposePartialSolutionConstraints(VrpSolution solution, Store store, IntVar[] nodePositions, IntVar[] vehicles, 
			int numCities) {
		List<List<Integer>> routes = solution.getRoutes();
		int depotIndex = numCities;
		for (List<Integer> route : routes) {
			if (!route.isEmpty()) {
				Iterator<Integer> iter = route.iterator();
				int prev = iter.next();
				//put first node after depot
//				System.out.println (depotIndex + " pos <= " + prev + " pos");
				store.impose(new XltY(nodePositions[depotIndex], nodePositions[prev]));
				while (iter.hasNext()) {
					int cur = iter.next();
					store.impose(new XltY(nodePositions[prev], nodePositions[cur]));
					prev = cur;
				}
				//put depot after last node
				if (depotIndex+1 < nodePositions.length) {
					store.impose(new XltY(nodePositions[prev], nodePositions[depotIndex+1]));
				}
			}
			depotIndex++;
		}
	}
	
	private void imposeVehicleCapacityConstraints(Store store, IntVar[] vehicles, int numCities, int[] demands, int numVehicles, 
			int vehicleCapacity) {
		//TODO: can we do this without cityVehicles?
		IntVar[][] cityVehicles = new IntVar[numCities][numVehicles];
//		IntVar capacity = new IntVar(store, "capacity", vehicleCapacity, vehicleCapacity);
		IntVar one = new IntVar(store, "one", 1, 1);
		
		for (int i = 0; i < numCities; i++) {
			for (int j = 0; j < numVehicles; j++) {
				cityVehicles[i][j] = new IntVar(store, 0, 1);
			}
		}
		
		//columns of cityVehicles (each col corresponds to a city) must sum to 1
		for (int i = 0; i < numCities; i++) {
			store.impose(new Sum(cityVehicles[i], one));
		}
		//the sum of the demands for a vehicle cannot be greater than the capacity
		//we have to make new arrays for these because cityVehicles is going the opposite way
		for (int i = 0; i < numVehicles; i++) {
			IntVar[] citiesForVehicle = new IntVar[numCities];
			for (int j = 0; j < numCities; j++) {
				citiesForVehicle[j] = cityVehicles[j][i];
			}
			IntVar vehicleSum = new IntVar(store, 0, vehicleCapacity);
			store.impose(new SumWeight(citiesForVehicle, demands, vehicleSum));
		}
		
		for (int i = 0; i < numCities; i++) {
			//link vehicles to cityVehicles i.e. cityVehicles[city][vehicles[city]] = 1;
			store.impose(new Element(vehicles[i], cityVehicles[i], one));
		}
	}
	
	private void buildDistArrays(int[][] distances, int[][] timeDists, VrpProblem problem, int numCities, int numNodes) {
		int[][] cityDistances = problem.getDistances();
		int[] distsFromOrigin = problem.getDistancesFromDepot();
		//copy over cityDistances
		for (int i = 0; i < numCities; i++) {
			System.arraycopy(cityDistances[i], 0, distances[i], 0, cityDistances.length);
			Arrays.fill(distances[i], numCities, numNodes, distsFromOrigin[i]);
		}
		//because all the depots are at the same point, we can speed things up a little by calculating
		//distances once
		int[] depotDists = new int[numNodes];
		System.arraycopy(distsFromOrigin, 0, depotDists, 0, distsFromOrigin.length);
		for (int i = numCities; i < numNodes; i++) {
			distances[i] = depotDists;
		}
		//fill in timeDists
		for (int r = 0; r < numNodes; r++) {
			for (int c = 0; c < numNodes; c++) {
				timeDists[r][c] = distances[r][c];
				if (r < numCities) {
					timeDists[r][c] += problem.getServiceTimes()[r];
				}
			}
		}
	}
}
