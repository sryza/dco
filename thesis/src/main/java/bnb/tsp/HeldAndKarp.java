package bnb.tsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class HeldAndKarp {
	
	private static final int MAX_ITERATIONS = 5;
	private static final int MAX_CHANGES = 5;
	private static final double LIMIT = .5;
	
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
	 * 		must have at least 2 entries
	 * @param numCities
	 * 		the total number of nodes we're trying to solve for
	 * @param curTourCost
	 * 		the cost of the current tour (not including edge from start to finish)
	 * @return
	 * 		a lower bound on the current solution
	 */
	public static int bound(City startNode, City endNode, boolean[] remainingVector, Collection<Edge> edges, 
			double minCost, List<City> remainingNodesList, int numCities, int curTourCost)
	{
		List<Edge> usableEdges = new ArrayList<Edge>(edges.size());
		//compute edges we can still use
		for (Edge edge : edges)
		{
			if (edge.node1 == startNode || edge.node2 == startNode || edge.node1 == endNode || edge.node2 == endNode) {
				if ((edge.node1 == startNode && edge.node2 == endNode) || (edge.node1 == endNode && edge.node2 == startNode)) {
					continue;
				}
			}
			//ignore all edges that touch interior nodes in the path-so-far
			if (edge.node1 != startNode && edge.node1 != endNode && !remainingVector[edge.node1.id])
				continue;
			if (edge.node2 != startNode && edge.node2 != endNode && !remainingVector[edge.node2.id])
				continue;
//			if (Utils.edgeCrossesPath(nodes, numChosen, edge.node1, edge.node2))
//				continue;
			
			usableEdges.add(edge);
		}
		
		//choose one tree node
		//min two edges for every node
		//edges can't go both to startNode and endNode, because that would create a cycle in the MST
		Edge[][] bestEdges = new Edge[numCities][2]; //TODO: shouldn't allocate this every time
		Iterator<Edge> iter = usableEdges.iterator();
		while (iter.hasNext()) {
			Edge e = iter.next();
			boolean touchesStartOrEnd = e.node1 == startNode || e.node1 == endNode || e.node2 == startNode ||
				e.node2 == endNode;
			//node 1
			Edge bestEdge = bestEdges[e.node1.id][0];
			Edge secondBestEdge = bestEdges[e.node1.id][1];
			boolean bestEdgeTouchesStartOrEnd = bestEdge != null && (bestEdge.node1 == startNode || 
					bestEdge.node1 == endNode || bestEdge.node2 == startNode ||
					bestEdge.node2 == endNode);
			if (bestEdge == null || e.dist < bestEdge.dist) {
				if (!touchesStartOrEnd || !bestEdgeTouchesStartOrEnd) {
					bestEdges[e.node1.id][1] = bestEdges[e.node1.id][0];
				}
				bestEdges[e.node1.id][0] = e;
			} else if (secondBestEdge == null || e.dist < secondBestEdge.dist) {
				if (!touchesStartOrEnd || !bestEdgeTouchesStartOrEnd) {
					bestEdges[e.node1.id][1] = e;
				}
			}
			
			//node 2
			bestEdge = bestEdges[e.node2.id][0];
			secondBestEdge = bestEdges[e.node2.id][1];
			bestEdgeTouchesStartOrEnd = bestEdge != null && (bestEdge.node1 == startNode || 
					bestEdge.node1 == endNode || bestEdge.node2 == startNode ||
					bestEdge.node2 == endNode);
			if (bestEdge == null || e.dist < bestEdge.dist) {
				if (!touchesStartOrEnd || !bestEdgeTouchesStartOrEnd) {
					bestEdges[e.node2.id][1] = bestEdges[e.node2.id][0];
				}
				bestEdges[e.node2.id][0] = e;
			} else if (secondBestEdge == null || e.dist < secondBestEdge.dist) {
				if (!touchesStartOrEnd || !bestEdgeTouchesStartOrEnd) {
					bestEdges[e.node2.id][1] = e;
				}
			}
		}
		
		City oneTreeNode = null;
		City oneTreeNodeTarget1 = null;
		City oneTreeNodeTarget2 = null;
		int maxSummedMinCost = Integer.MIN_VALUE;
		for (City city : remainingNodesList) {
			if (bestEdges[city.id][1] != null) { //TODO: is this ever allowed to happen
				int summedMinCost = bestEdges[city.id][0].dist + bestEdges[city.id][1].dist;
				if (summedMinCost > maxSummedMinCost) {
					oneTreeNode = city;
					oneTreeNodeTarget1 = (bestEdges[city.id][0].node1 == oneTreeNode) ? bestEdges[city.id][0].node2
							: bestEdges[city.id][0].node1;
					oneTreeNodeTarget2 = (bestEdges[city.id][1].node1 == oneTreeNode) ? bestEdges[city.id][1].node2
							: bestEdges[city.id][1].node1;
					maxSummedMinCost = summedMinCost;
				}
			}
		}
		
		//new held & karp starts here
		int bestBound = Integer.MIN_VALUE;
		int[] nodeWeights = new int[numCities]; // TODO: get this from somewhere else
		int[] nodeEdges = new int[numCities]; // TODO: don't need to initialize this every time
		int weightsSum = 0;
		HeldKarpEdgeComparator edgeComparator = new HeldKarpEdgeComparator(nodeWeights);
		double stepScale = 2.0;
		double stepChange = .5;
		for (int c = 0; c < MAX_CHANGES; c++) {
			for (int j = 0; j < MAX_ITERATIONS; j++) {
				//calculate one-tree bound
				PriorityQueue<Edge> edgesHeap = new PriorityQueue<Edge>(usableEdges.size(), edgeComparator);
				edgesHeap.addAll(usableEdges);
				int mstCost = mstCost(startNode, endNode, oneTreeNode, oneTreeNodeTarget1, oneTreeNodeTarget2,
						remainingNodesList, edgesHeap, nodeWeights, nodeEdges);
				int cost = 2 * weightsSum + curTourCost + mstCost + maxSummedMinCost;
				//include weights from one-tree edges
				cost -= nodeWeights[oneTreeNodeTarget1.id];
				cost -= nodeWeights[oneTreeNodeTarget2.id];
				
				if (cost > bestBound) {
					bestBound = cost;
					if (bestBound >= minCost) {
						return bestBound;
					}
				}

				//TODO: if we've reached a tour, stop
				//if the tour improves our objective function, we should return it
				//and not explore the rest of the subtree
				//for now, just print out when this happens
				
				//compute step size
				int sumSquareDiffs = 0;
				int numOptimalNumEdges = 0;
				for (City node : remainingNodesList) {
					if (node == oneTreeNode) {
						continue;
					}
					int optimalNumEdges = (node == bestEdges[oneTreeNode.id][0].node1 || 
							node == bestEdges[oneTreeNode.id][0].node2 || 
							node == bestEdges[oneTreeNode.id][1].node1 || 
							node == bestEdges[oneTreeNode.id][1].node2) ? 1 : 2;
					if (nodeEdges[node.id] == optimalNumEdges) {
						numOptimalNumEdges++;
					}
					sumSquareDiffs += (optimalNumEdges-nodeEdges[node.id]) * (optimalNumEdges-nodeEdges[node.id]);
				}
				sumSquareDiffs += (1 - nodeEdges[startNode.id]) * (1 - nodeEdges[startNode.id]);
				if (nodeEdges[startNode.id] == 1) {
					numOptimalNumEdges++;
				}
				sumSquareDiffs += (1 - nodeEdges[endNode.id]) * (1 - nodeEdges[endNode.id]);
				if (nodeEdges[endNode.id] == 1) {
					numOptimalNumEdges++;
				}
				double stepSize = stepScale * (minCost - cost) / (sumSquareDiffs);
				
				if (numOptimalNumEdges == remainingNodesList.size()+2-1) { //-1 for oneTreeNode, +2 for start/end
					System.out.println("found tour at remainingNodesList.size()=" + remainingNodesList.size());
					System.out.println(cost);
				}
				
				if (stepSize < LIMIT) {
					return bestBound;
				}
				
				//update weights
				weightsSum = 0;
				for (City city : remainingNodesList) {
					if (city != oneTreeNode) {
						nodeWeights[city.id] += stepSize * (2 - nodeEdges[city.id]);
						weightsSum += nodeWeights[city.id];
					}
				}
//				nodeWeights[startNode.id] += stepSize * (1 - nodeEdges[startNode.id]);
//				weightsSum += nodeWeights[startNode.id];
//				nodeWeights[endNode.id] += stepSize * (1 - nodeEdges[endNode.id]);
//				weightsSum += nodeWeights[endNode.id];
			}
			stepScale = stepScale * stepChange;
		}

		return bestBound;
	}
	
	/**
	 * Finds the cost of the minimum spanning tree through startNode, endNode,
	 * and all the nodes in remainingNodes.
	 * 
	 * @param oneTreeNode
	 * 		all edges touching this node will be ignored
	 * @param nodeEdges
	 * 		the number of incoming edges for each node
	 */
	public static int mstCost(City startNode, City endNode, City oneTreeNode, City oneTreeTarget1, 
			City oneTreeTarget2, Collection<City> remainingNodes, PriorityQueue<Edge> edgesQueue, 
			int[] nodeWeights, int[] nodeEdges)
	{
		int totalCost = 0;
		int numEdges = 0;
		
		UnionFind unionFind = new UnionFind(remainingNodes, startNode, endNode, nodeEdges.length);
		//startNode and endNode are already connected by the rest of the path,
		//so put them together
		//TODO: connect whichever edges the oneTreeNode connects
//		unionFind.union(oneTreeTarget1, oneTreeTarget2);
		unionFind.union(startNode, endNode);
		/*for (Edge edge : requiredEdges)
		{
			Node root1 = unionFind.find(edge.node1), root2 = unionFind.find(edge.node2);
			if (root1 == root2)
				System.err.println ("We are not creating a tree here! minSpanningTreeCost");
			unionFind.union(root1, root2);
			totalCost += edge.dist;
		}*/
		//have to connect all remainingNodes, which should take remainingNodes.size()-1
		//we subtract one because we're ignoring the oneTreeNode
		//we add two to this for startNode and endNode
		//we subtract one from this for the virtual edge we have connecting startNode and endNode
		//which represents the path so far
		while (numEdges < remainingNodes.size()-1-1+2-1 && edgesQueue.size() > 0)
		{
//			System.out.println ("in while loop");
			Edge e = edgesQueue.remove();
			if (e.node1 == oneTreeNode || e.node2 == oneTreeNode) {
				continue;
			}
			City root1 = unionFind.find(e.node1);
			City root2 = unionFind.find(e.node2);
//			System.out.println ("finished finds");
			if (root1 != root2)
			{
				totalCost += e.cost() - nodeWeights[e.node1.id] - nodeWeights[e.node2.id];
				nodeEdges[e.node1.id]++;
				nodeEdges[e.node2.id]++;
				unionFind.union(root1, root2);
				numEdges++;
			}
		}
		
		return totalCost;
	}
}