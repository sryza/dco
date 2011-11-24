package bnb.tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class HeldAndKarp {
	
	private static final int MAX_ITERATIONS = 50;
	
	/**
	 * 
	 * @param startNode
	 * 		the first node in the current path
	 * @param endNode
	 * 		the last node in the current path
	 * @param remainingVector
	 * @param edges
	 * @param minCost
	 * @param remainingNodesList
	 * @param numCities
	 * 		the total number of nodes we're trying to solve for
	 * @param curTourCost
	 * 		the cost of the current tour (not including edge from start to finish)
	 * @return
	 */
	public static int bound(City startNode, City endNode, boolean[] remainingVector, Collection<Edge> edges, 
			double minCost, List<City> remainingNodesList, int numCities, int curTourCost)
	{
		List<Edge> usableEdges = new ArrayList<Edge>(edges.size());
		
		//compute edges we can still use
		for (Edge edge : edges)
		{
			if (edge.node1 == startNode || edge.node2 == startNode || edge.node1 == endNode || edge.node2 == endNode)
				if ((edge.node1 == startNode && edge.node2 == endNode) || (edge.node1 == endNode && edge.node2 == startNode))
					continue;
			//ignore all edges that touch interior nodes in the path-so-far
			if (edge.node1 != startNode && edge.node1 != endNode && !remainingVector[edge.node1.id])
				continue;
			if (edge.node2 != startNode && edge.node2 != endNode && !remainingVector[edge.node2.id])
				continue;
//			if (Utils.edgeCrossesPath(nodes, numChosen, edge.node1, edge.node2))
//				continue;
			
			usableEdges.add(edge);
		}
		
		int lowerBound = Integer.MIN_VALUE;
		
		for (int i = 0; i < remainingNodesList.size(); i++)
		{
			City oneTreeNode = remainingNodesList.remove(0); //so it won't be considered in the spanning tree
			LinkedList<Edge> removedEdges = new LinkedList<Edge>();
			Iterator<Edge> iter = usableEdges.iterator();
			
			//find the two edges of least distance coming out of the one-tree node
			//remove all edges coming out of the one-tree node
			//TODO: this could be more efficient because we only need to consider edges
			//that touch the one-tree node
			Edge bestEdge = null, secondBestEdge = null;
			while (iter.hasNext()) {
				Edge e = iter.next();
				if (e.node1 == oneTreeNode || e.node2 == oneTreeNode) {
					iter.remove();
					removedEdges.add(e);
					if (bestEdge == null || e.dist < bestEdge.dist) {
						secondBestEdge = bestEdge;
						bestEdge = e;
					} else if (secondBestEdge == null || e.dist < secondBestEdge.dist) {
						secondBestEdge = e;
					}
				}
			}
			
			int[] nodeEdges = new int[numCities];
			PriorityQueue<Edge> edgesHeap = new PriorityQueue<Edge>(usableEdges);
			int mstCost = mstCost(startNode, endNode, remainingNodesList, edgesHeap, nodeEdges);
			int cost = curTourCost + bestEdge.dist + secondBestEdge.dist + mstCost;
			
			if (cost > lowerBound) {
				lowerBound = cost;
				if (lowerBound > minCost) {
					remainingNodesList.add(oneTreeNode);
					return lowerBound;
				}
			}
			
//			//held and karp!
//			int[] nodeCosts = new int[numCities];
//			int[] nodeEdges = new int[numCities];
//			final double limit = .5;
//			double stepSize = Double.MAX_VALUE, stepScale = 2.0, stepChange = .75;
//			
//			for (int j = 0; j < MAX_ITERATIONS; j++)
//			{
//				for (Edge edge : usableEdges)
//					edge.extra = -nodeCosts[edge.node1.id] - nodeCosts[edge.node2.id];
//				Arrays.fill(nodeEdges, 0);
//
//				PriorityQueue<Edge> edgesHeap = new PriorityQueue<Edge>(usableEdges);
//				int cost = curTourCost + mstCost(startNode, endNode, remainingNodesList, edgesHeap, nodeEdges)
//					+ bestEdge.dist + secondBestEdge.dist;
////				System.out.println ("before cost: "+ cost);
//				for (City node : remainingNodesList)
//					cost += 2 * nodeCosts[node.id];
//				cost += nodeCosts[startNode.id];
//				cost += nodeCosts[endNode.id];
////				System.out.println ("after cost: " + cost);
//				if (cost > minCost)
//				{
//					remainingNodesList.add(oneTreeNode);
//					return cost;
//				}
//
//				if (cost > lowerBound) {
//					lowerBound = cost;
//				}
//				
//				//calculate step size
//				int sumSquareDiffs = 0;
//				for (City node : remainingNodesList) {
////					if (nodeEdges[node.id] < 1)
////						System.out.println ("this is wrong");
//					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == secondBestEdge.node1 || node == secondBestEdge.node2) ? 1 : 2;
//					sumSquareDiffs += (optimalNumEdges-nodeEdges[node.id]) * (optimalNumEdges-nodeEdges[node.id]);
//				}
//				sumSquareDiffs += (1-nodeEdges[startNode.id]) * (1-nodeEdges[startNode.id]);
//				sumSquareDiffs += (1-nodeEdges[endNode.id]) * (1-nodeEdges[endNode.id]);
//					
//				stepSize = (stepScale * (minCost - cost)) / sumSquareDiffs;
//				if (stepSize < limit) {
////					System.out.println ("j: " + j);
//					break;
//				}
//				
//				for (City node : remainingNodesList)
//				{
//					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == bestEdge.node1 || node == bestEdge.node2) ? 1 : 2;
//					nodeCosts[node.id] += (int)(stepSize * (optimalNumEdges - nodeEdges[node.id]));
//				}
//				startNode.cost += (int)(stepSize * (1 - nodeEdges[startNode.id]));
//				endNode.cost += (int)(stepSize * (1 - nodeEdges[endNode.id]));
//				
//				stepScale *= stepChange;
//			}
			
			//add stuff back in
			remainingNodesList.add(oneTreeNode);
			usableEdges.addAll(removedEdges);
		}
		return lowerBound;
	}
	
	/**
	 * Finds the cost of the minimum spanning tree through startNode, endNode,
	 * and all the nodes in remainingNodes.
	 */
	public static int mstCost(City startNode, City endNode, Collection<City> remainingNodes, 
			PriorityQueue<Edge> edgesQueue, int[] nodeEdges)
	{
		int totalCost = 0;
		int numEdges = 0;
		
		UnionFind unionFind = new UnionFind(remainingNodes, startNode, endNode, nodeEdges.length);
		//startNode and endNode are already connected by the rest of the path,
		//so put them together
		unionFind.union(startNode, endNode);
		/*for (Edge edge : requiredEdges)
		{
			Node root1 = unionFind.find(edge.node1), root2 = unionFind.find(edge.node2);
			if (root1 == root2)
				System.err.println ("We are not creating a tree here! minSpanningTreeCost");
			unionFind.union(root1, root2);
			totalCost += edge.dist;
		}*/
		//second -1 accounts for union of startNode and endNode
		while (numEdges < remainingNodes.size()+2-1-1 && edgesQueue.size() > 0)
		{
//			System.out.println ("in while loop");
			Edge e = edgesQueue.remove();
			City root1 = unionFind.find(e.node1);
			City root2 = unionFind.find(e.node2);
//			System.out.println ("finished finds");
			if (root1 != root2)
			{
				totalCost += e.cost();
				nodeEdges[e.node1.id]++;
				nodeEdges[e.node2.id]++;
				unionFind.union(root1, root2);
				numEdges++;
			}
		}
		
		return totalCost;
	}
}