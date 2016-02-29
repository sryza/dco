/**
 * Copyright 2012 Sandy Ryza
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pls.vrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import pls.vrp.hm.VrpCpStats;
import pls.vrp.hm.VrpSearcher;

public class RoutePoolSolutionGenerator {
	
	private static final Logger LOG = Logger.getLogger(RoutePoolSolutionGenerator.class);
	
	//we need a list of routes labeled by their source's objective function's value
	//we need to be able to remove routes from this list based on the nodes contained in them
	
	//we can use a linked list of routes that we have pointers into from a hashmap of nodes
	//using the pointers we can remove the routes from the list
	//initially, the linked list should be sorted by objective function value
	
	private ListNode routeListStart;
	private int routeListSize;
	private Map<Integer, List<ListNode>> custsMap;
	private Random rand;
	private VrpProblem problem;
	
	public RoutePoolSolutionGenerator(List<VrpSolution> sols, VrpProblem problem, Random rand) {
		this.rand = rand;
		this.problem = problem;
		
		//sort sols ascending by solution cost
		Collections.sort(sols, new SolCostComparator());
		
		custsMap = new HashMap<Integer, List<ListNode>>();
		
		routeListStart = new ListNode(null, null, null);
		ListNode prev = routeListStart;
		for (VrpSolution sol : sols) {
			for (List<Integer> route : sol.getRoutes()) {
				ListNode routeNode = new ListNode(route, null, prev);
				routeListSize++;
				prev.next = routeNode;
				prev = routeNode;
				
				for (Integer custId : route) {
					List<ListNode> routeNodesWithCust = custsMap.get(custId);
					if (routeNodesWithCust == null) {
						routeNodesWithCust = new ArrayList<ListNode>();
						custsMap.put(custId, routeNodesWithCust);
					}
					routeNodesWithCust.add(routeNode);
				}
			}
		}
	}
	
	public List<VrpSolution> generateSolutions(int num) {
		List<VrpSolution> solutions = new ArrayList<VrpSolution>();
		List<ListNode> allListNodes = new ArrayList<ListNode>();
		ListNode cur = routeListStart.next;
		while (cur != null) {
			allListNodes.add(cur);
			cur = cur.next;
		}
		
		for (int i = 0; i < num; i++) {
			solutions.add(constructSolution());
			
			if (i < num-1) {
				//repair list
				ListNode prev = routeListStart;
				for (ListNode routeListNode : allListNodes) {
					prev.next = routeListNode;
					routeListNode.prev = prev;
					prev = routeListNode;
				}
				routeListSize = allListNodes.size();//exclude start node
			}
		}
		return solutions;
	}
	
	private VrpSolution constructSolution() {
		List<List<Integer>> routes = new ArrayList<List<Integer>>();
		int[] usedCities = new int[problem.getNumCities()]; //1 means used
		int numUsedCities = 0;
		//while routes remain
		while (routeListStart.next != null) {
			//choose a route to insert
			ListNode cur = routeListStart.next;
			double val = rand.nextDouble();
			double cumulative = 0.0;
			double denom = routeListSize * (routeListSize + 1);
			int i = routeListSize;
			while (cur.next != null) {
				cumulative += 2 * i / denom;
				if (val < cumulative) {
					break;
				}
				cur = cur.next;
				i--;
			}
			
			//cur is the chosen route
			routes.add(cur.route);
			for (Integer custId : cur.route) {
				List<ListNode> routeNodesWithCust = custsMap.get(custId);
				numUsedCities++;
				if (usedCities[custId] == 1) {
					throw new IllegalStateException(custId + " already used");
				}
				usedCities[custId] = 1;
				for (ListNode routeNode : routeNodesWithCust) {
					if (routeNode.prev.next == routeNode) { //still in list
						routeListSize--;
						routeNode.prev.next = routeNode.next;
						if (routeNode.next != null) {
							routeNode.next.prev = routeNode.prev;
						}
					}
				}
			}
		}
		
		//fill in remaining
		List<Integer> remaining = new ArrayList<Integer>();
		for (int i = 0; i < usedCities.length; i++) {
			if (usedCities[i] == 0) {
				remaining.add(i);
			}
		}
//		LOG.debug(remaining.size() + " cities remaining to insert");
//		int numExtraRoutes = remaining.size();
//		for (int i = 0; i < numExtraRoutes; i++) {
//			routes.add(new ArrayList<Integer>());
//		}
//		VrpSearcher searcher = new VrpSearcher(problem);
//		VrpSolution partialSol = new VrpSolution(routes, remaining, problem);
//		return searcher.solve(partialSol, Integer.MAX_VALUE, 5, new VrpCpStats(), false);
		VrpGreedyInitializer greedy = new VrpGreedyInitializer(1.0, 1.0, 0.0);
		return greedy.nearestNeighborHeuristic(problem, routes);
	}
	
	private class ListNode {
		public ListNode next;
		public ListNode prev;
		public List<Integer> route;
		
		public ListNode(List<Integer> route, ListNode next, ListNode prev) {
			this.next = next;
			this.route = route;
			this.prev = prev;
		}
		
		public String toString() {
			List<List<Integer>> routes = new ArrayList<List<Integer>>();
			ListNode cur = this;
			while (cur != null) {
				routes.add(cur.route);
				cur = cur.next;
			}
			return routes.size()+":"+routes.toString();
		}
	}
	
	private class SolCostComparator implements Comparator<VrpSolution> {

		@Override
		public int compare(VrpSolution sol1, VrpSolution sol2) {
			return (int)(Math.signum(sol1.getToursCost() - sol2.getToursCost()));
		}
	}
}
