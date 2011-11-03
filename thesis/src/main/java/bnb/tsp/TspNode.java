package bnb.tsp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.Problem;
import bnb.Solution;
import bnb.BnbNode;

public class TspNode extends BnbNode {
	private static final Logger LOG = Logger.getLogger(TspNode.class);
	
	private boolean isEvaluated;
	
	private boolean bounded;
	private double parentTourCost = -1.0;
	private double tourCost = -1.0;
	
	private City[] cities;
	private int numChosen;
	private City city;
	
	private final TspProblem problem;
	
	private int nextChildIndex;
	
	private List<City> exploredChildren;
	
	public TspNode(TspProblem problem) {
		this.problem = problem;
	}
	
	public TspNode(City[] cities, City city, int numChosen, TspNode parent) {
		this.cities = cities;
//		System.out.println(numUniqueCities());

		this.city = city;
		this.numChosen = numChosen;
		nextChildIndex = numChosen;
		if (parent != null) {
			parentTourCost = parent.getTourCost();
		}
	}
	
	// for while we don't have serialization
	public void copyCities() {
		City[] temp = new City[cities.length];
		for (int i = 0; i < cities.length; i++) {
			temp[i] = cities[i].copy();
		}
		cities = temp;
	}
	
	@Override
	public boolean isEvaluated() {
		return isEvaluated;
	}
	
	@Override
	public boolean isSolution() {
		if (!isEvaluated) {
			throw new IllegalStateException("Tsp node not yet evaluated.");
		}
		return !bounded && numChosen == cities.length;
	}
	
	public double getTourCost() {
		return tourCost;
	}

	@Override
	public BnbNode nextChild() {
		if (!hasNextChild()) {
			throw new NoSuchElementException("Node has no next child.");
		}
		TspNode child = new TspNode(cities, cities[nextChildIndex], numChosen+1, this);
		//TODO: take this out
		System.out.println("we're checking for explored children");
		if (exploredChildren.contains(cities[nextChildIndex])) {
			LOG.error("about to explore node that's already been explored.  your understanding's wrong, sandy.");
		}
		exploredChildren.add(cities[nextChildIndex]);
		nextChildIndex++;
		return child;
	}

	@Override
	public boolean hasNextChild() {
		return !bounded && nextChildIndex < cities.length;
	}
	
	@Override
	public void evaluate(double bound) {

		if (!doEvaluate(bound)) {
			bounded = true;
//			System.out.println("bounded");
		}
		isEvaluated = true;
	}
	
	private int numUniqueCities() {
		Set<City> citySet = new HashSet<City>();
		for (City city : cities) {
			citySet.add(city);
		}
		return citySet.size();
	}
	
	private boolean indexesRight() {
		for (int i = 0; i < cities.length; i++) {
			if (cities[i].index != i) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 * 		true if not bounded, false is yes bounded
	 */
	private boolean doEvaluate(Problem spec, double minCost) {
		if (isEvaluated) {
			System.out.println("already evaluated");
		}
		//evaluates whether the placement of the last node makes sense
		int before = numUniqueCities();
		TspUtils.placeCity(cities, city, numChosen-1);
		int after = numUniqueCities();
		if (after != before) {
			System.out.println(before + " != " + after);
			System.exit(0);
		}
		if (!indexesRight()) {
			before += 0;
			after += 0;
			System.out.println("indexes wrong " + numChosen);
			System.exit(0);
		}
		
		if (numChosen == 2) {
			tourCost = 2 * cities[0].dist(cities[1]);
		} else if (numChosen > 2) {
			tourCost = parentTourCost - cities[numChosen-2].dist(cities[0]) + 
				cities[numChosen-2].dist(cities[numChosen-1]) + cities[numChosen-1].dist(cities[0]);
		}
		
		if (tourCost >= minCost) {
			return false;
		}
		
		//if 2 or 3 opt give us better solutions, continue
		for (int j = 1; j < numChosen-3; j++)
		{
			if (TspUtils.cost2opt(cities, numChosen-2, j) < 0 || TspUtils.cost2opt(cities, j, numChosen-2) < 0)
			{
				//cost2opt gives something better, so discard
				return false;
			}
		}
		
		for (int j = 1; j < numChosen-3; j++)
		{
			for (int k = 1; k < numChosen-3; k++)
			{
				if (j == k || (j+1) == k || (j-1) == k)
					continue;
				
				if (TspUtils.cost3opt(cities, j, k, numChosen-2) < 0 || 
					TspUtils.cost3opt(cities, k, j, numChosen-2) < 0 ||
					TspUtils.cost3opt(cities, j, numChosen-2, k) < 0 ||
					TspUtils.cost3opt(cities, numChosen-2, k, j) < 0 ||
					TspUtils.cost3opt(cities, k, numChosen-2, j) < 0 ||
					TspUtils.cost3opt(cities, numChosen-2, j, k) < 0)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void initFromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bais);
			int numCities = dis.readInt();
			
			//TODO: how do we reconstruct this in the best way?
			cities = new City[numCities];
			City[] problemCities = problem.getCities();
			for (int i = 0; i < numCities; i++) {
				int id = dis.readInt();
				cities[i] = problemCities[id].copy();
			}
			

		} catch (IOException ex) {
			LOG.error("IOException reading from byte array, this should never happen", ex);
		}
	}
	
	@Override
	public byte[] toBytes() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			//TODO: check for off by ones
			
			//num nodes in path
			dos.writeInt(numChosen);
			//nodes in path
			for (int i = 0; i < numChosen; i++) {
				dos.writeInt(cities[i].id);
			}
			//num explored children
			dos.writeInt(exploredChildren.size());
			//explored children
			for (City child : exploredChildren) {
				dos.writeInt(child.id);
			}
			
			return baos.toByteArray();
		} catch (IOException ex) {
			LOG.error("IOException writing to byte array, this should never happen", ex);
			return null;
		}
	}
	
	@Override
	public double getCost() {
		return tourCost;
	}
	
	@Override
	public Solution getSolution() {
		return new TspSolution(cities);
	}
	
/*	
	private int bound(City[] nodes, boolean[] remainingVector, int numChosen, Collection<Edge> edges)
	{
		List<Edge> usableEdges = new ArrayList<Edge>(edges.size());
		City startNode = nodes[0], endNode = nodes[numChosen-1];
		
		//compute edges we can still use
		for (Edge edge : edges)
		{
			if (edge.node1 == startNode || edge.node2 == startNode || edge.node1 == endNode || edge.node2 == endNode)
				if ((edge.node1 == startNode && edge.node2 == endNode) || (edge.node1 == endNode && edge.node2 == startNode))
					continue;
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
			City oneTreeNode = remainingNodesList.remove(0);
			LinkedList<Edge> removedEdges = new LinkedList<Edge>();
			Iterator<Edge> iter = usableEdges.iterator();
			Edge bestEdge = null, secondBestEdge = null;
			
//			boolean[] nodesTouched = new boolean[nodes.length];
//			int numNodesTouched = 0;
//			for (Node node : remainingNodesList)
//				nodesTouched[node.id] = true;
			
			while (iter.hasNext())
			{
				Edge e = iter.next();
				if (e.node1 == oneTreeNode || e.node2 == oneTreeNode)
				{
					iter.remove();
					removedEdges.add(e);
					if (bestEdge == null || e.dist < bestEdge.dist)
					{
						secondBestEdge = bestEdge;
						bestEdge = e;
					}
					else if (secondBestEdge == null || e.dist < secondBestEdge.dist)
					{
						secondBestEdge = e;
					}
				}
//				else
//				{
//					if (nodesTouched[e.node1.id] == true)
//					{
//						nodesTouched[e.node1.id] = false;
//						numNodesTouched++;
//					}
//					if (nodesTouched[e.node2.id] == true)
//					{
//						nodesTouched[e.node2.id] = false;
//						numNodesTouched++;
//					}
//				}
			}
//			if (numNodesTouched != remainingNodesList.size())
//			{
//				remainingNodesList.add(oneTreeNode);
//				return Integer.MAX_VALUE / 2;
//			}
			
			//held and karp!
			int[] nodeCosts = new int[nodes.length], nodeEdges = new int[nodes.length];
			final double limit = .5;
			double stepSize = Double.MAX_VALUE, stepScale = 2.0, stepChange = .75;
			
			for (int j = 0; j < MAX_ITERATIONS; j++)
			{
				for (Edge edge : usableEdges)
					edge.extra = -nodeCosts[edge.node1.id] - nodeCosts[edge.node2.id];
				Arrays.fill(nodeEdges, 0);

				PriorityQueue<Edge> edgesHeap = new PriorityQueue<Edge>(usableEdges);
				int cost = mstCost(startNode, endNode, remainingNodesList, edgesHeap, nodeEdges) + bestEdge.dist + secondBestEdge.dist;
//				System.out.println ("before cost: "+ cost);
				for (Node node : remainingNodesList)
					cost += 2 * nodeCosts[node.id];
				cost += nodeCosts[startNode.id];
				cost += nodeCosts[endNode.id];
//				System.out.println ("after cost: " + cost);
				if (cost > upper_bound)
				{
					remainingNodesList.add(oneTreeNode);
					return cost;
				}
				if (cost > lowerBound)
					lowerBound = cost;
				
				//calculate step size
				int sumSquareDiffs = 0;
				for (Node node : remainingNodesList)
				{
//					if (nodeEdges[node.id] < 1)
//						System.out.println ("this is wrong");
					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == secondBestEdge.node1 || node == secondBestEdge.node2) ? 1 : 2;
					sumSquareDiffs += (optimalNumEdges-nodeEdges[node.id]) * (optimalNumEdges-nodeEdges[node.id]);
				}
				sumSquareDiffs += (1-nodeEdges[startNode.id]) * (1-nodeEdges[startNode.id]);
				sumSquareDiffs += (1-nodeEdges[endNode.id]) * (1-nodeEdges[endNode.id]);
					
				stepSize = (stepScale * (upper_bound - cost)) / sumSquareDiffs;
				if (stepSize < limit)
				{
//					System.out.println ("j: " + j);
					break;
				}
				
				for (City node : remainingNodesList)
				{
					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == bestEdge.node1 || node == bestEdge.node2) ? 1 : 2;
					nodeCosts[node.id] += (int)(stepSize * (optimalNumEdges - nodeEdges[node.id]));
				}
				startNode.cost += (int)(stepSize * (1 - nodeEdges[startNode.id]));
				endNode.cost += (int)(stepSize * (1 - nodeEdges[endNode.id]));
				
				stepScale *= stepChange;
			}
			
			//add stuff back in
			remainingNodesList.add(oneTreeNode);
			usableEdges.addAll(removedEdges);
		}
		return lowerBound;
	}*/
}


//public class BranchAndBound
//{
//	private static final int MAX_ITERATIONS = 50;
//	
//	private int upper_bound = 0;
//	private Node[] best_sol = null;
//	
//	public void runBranchAndBound(Node[] nodes, int upperBound)
//	{
//		Node[] sol = new Node[nodes.length];
//		sol[0] = nodes[0];
//		upper_bound = upperBound;
//		best_sol = new Node[nodes.length];
//		System.arraycopy(nodes, 0, best_sol, 0, nodes.length);
//		
//		List<Node> remainingNodes = new LinkedList<Node>();
//		boolean[] remainingVector = new boolean[nodes.length];
//		for (Node node : nodes)
//			if (node != sol[0])
//			{
//				remainingNodes.add(node);
//				remainingVector[node.id] = true;
//			}
//		
//		Collection<Edge> edges = new ArrayList<Edge>((nodes.length - 1) * nodes.length / 2);
//		for (int i = 0; i < nodes.length; i++)
//			for (int j = i+1; j < nodes.length; j++)
//			{
//				Edge e = new Edge(nodes[i], nodes[j]);
//				edges.add(e);
//			}
//		
//		recurseBranchAndBound(sol, remainingNodes, remainingVector, 1, edges, 0, 0);
//	}
//	
//	public int getCost()
//	{
//		return upper_bound;
//	}
//	
//	public Node[] getSolution()
//	{
//		return best_sol;
//	}
//	
//	/**
//	 * @return the min tour dist under this branch.
//	 */
//	private void recurseBranchAndBound(Node[] nodes, List<Node> remainingNodes, boolean[] remainingVector, int numChosen, Collection<Edge> edges, int sumDist, int level)
//	{
//		//base case
//		if (numChosen == nodes.length-1)
//		{
//			nodes[numChosen] = remainingNodes.get(0);
//			sumDist += Utils.dist(nodes[numChosen-1], nodes[numChosen]) + Utils.dist(nodes[numChosen], nodes[0]);
//			if (sumDist < upper_bound)
//			{
//				upper_bound = sumDist;
//				System.arraycopy(nodes, 0, best_sol, 0, nodes.length);
//			}
//			return;
//		}
//		//else
//		outerloop:
//		for (int i = 0; i < remainingNodes.size(); i++)
//		{
////			if(level <= 7)
////				System.out.println("at loop: " + level + "\t" + i);
//			Node node = remainingNodes.remove(0);
//			// or the added edge crosses an edge, continue
//			if (Utils.edgeCrossesPath(nodes, numChosen, nodes[numChosen-1], node))
//			{
//				remainingNodes.add(node);
////				System.out.println ("pruned w/crossing edge");
//				continue;
//			}
//			
//			nodes[numChosen] = node;
//			remainingVector[node.id] = false;
//			//if 2 or 3 opt give us better solutions, continue
//			for (int j = 1; j < numChosen-2; j++)
//			{
//				if (LocalSearch.cost2opt(nodes, numChosen-1, j) < 0 || LocalSearch.cost2opt(nodes, j, numChosen-1) < 0)
//				{
//					remainingNodes.add(node);
//					nodes[numChosen] = null;
//					remainingVector[node.id] = true;
////					System.out.println ("found better with cost2opt");
//					continue outerloop;
//				}
//			}
//			
//			for (int j = 1; j < numChosen-2; j++)
//			{
//				for (int k = 1; k < numChosen-2; k++)
//				{
//					if (j == k || (j+1) == k || (j-1) == k)
//						continue;
//					
//					if (cost3opt(nodes, j, k, numChosen-1) < 0 || cost3opt(nodes, k, j, numChosen-1) < 0 || cost3opt(nodes, j, numChosen-1, k) < 0
//						|| cost3opt(nodes, numChosen-1, k, j) < 0 || cost3opt(nodes, k, numChosen-1, j) < 0 || cost3opt(nodes, numChosen-1, j, k) < 0)
//					{
//						remainingNodes.add(node);
//						nodes[numChosen] = null;
//						remainingVector[node.id] = true;
////						System.out.println ("found better with cost3opt");
//						continue outerloop;
//					}
//				}
//			}
//			
//			//obtain lower bound using held & karp
//			int lowerBound = (numChosen < nodes.length-3) ? sumDist + bound(nodes, remainingNodes, remainingVector, numChosen+1, edges) : -1;
//			if (lowerBound > upper_bound)
//			{
////				System.out.println("pruned using held & karp " + lowerBound + "\t" + numChosen);
//			}
//			else if (lowerBound == upper_bound)
//			{
////				System.out.println ("held & karp lower bound equals upper bound");
//			}
//			else
//			{
//				recurseBranchAndBound(nodes, remainingNodes, remainingVector, numChosen+1, edges, sumDist + Utils.dist(nodes[numChosen-1], node), level+1);
//			}
//			remainingNodes.add(node);
//			
//			remainingVector[node.id] = true;
//			
////			remainingNodes.(node);
//		}
//	}
//	
//	//banned edges:
//	//edges on the path
//	//edges incident on nodes on the path that aren't the start and finish
//	//edge from start to finish
//	//crossing edges
//	
//	private int bound(Node[] nodes, List<Node> remainingNodesList, boolean[] remainingVector, int numChosen, Collection<Edge> edges)
//	{
//		List<Edge> usableEdges = new ArrayList<Edge>(edges.size());
//		Node startNode = nodes[0], endNode = nodes[numChosen-1];
//		
//		//compute edges we can still use
//		for (Edge edge : edges)
//		{
//			if (edge.node1 == startNode || edge.node2 == startNode || edge.node1 == endNode || edge.node2 == endNode)
//				if ((edge.node1 == startNode && edge.node2 == endNode) || (edge.node1 == endNode && edge.node2 == startNode))
//					continue;
//			if (edge.node1 != startNode && edge.node1 != endNode && !remainingVector[edge.node1.id])
//				continue;
//			if (edge.node2 != startNode && edge.node2 != endNode && !remainingVector[edge.node2.id])
//				continue;
////			if (Utils.edgeCrossesPath(nodes, numChosen, edge.node1, edge.node2))
////				continue;
//			
//			usableEdges.add(edge);
//		}
//		
//		int lowerBound = Integer.MIN_VALUE;
//		
//		for (int i = 0; i < remainingNodesList.size(); i++)
//		{
//			Node oneTreeNode = remainingNodesList.remove(0);
//			LinkedList<Edge> removedEdges = new LinkedList<Edge>();
//			Iterator<Edge> iter = usableEdges.iterator();
//			Edge bestEdge = null, secondBestEdge = null;
//			
////			boolean[] nodesTouched = new boolean[nodes.length];
////			int numNodesTouched = 0;
////			for (Node node : remainingNodesList)
////				nodesTouched[node.id] = true;
//			
//			while (iter.hasNext())
//			{
//				Edge e = iter.next();
//				if (e.node1 == oneTreeNode || e.node2 == oneTreeNode)
//				{
//					iter.remove();
//					removedEdges.add(e);
//					if (bestEdge == null || e.dist < bestEdge.dist)
//					{
//						secondBestEdge = bestEdge;
//						bestEdge = e;
//					}
//					else if (secondBestEdge == null || e.dist < secondBestEdge.dist)
//					{
//						secondBestEdge = e;
//					}
//				}
////				else
////				{
////					if (nodesTouched[e.node1.id] == true)
////					{
////						nodesTouched[e.node1.id] = false;
////						numNodesTouched++;
////					}
////					if (nodesTouched[e.node2.id] == true)
////					{
////						nodesTouched[e.node2.id] = false;
////						numNodesTouched++;
////					}
////				}
//			}
////			if (numNodesTouched != remainingNodesList.size())
////			{
////				remainingNodesList.add(oneTreeNode);
////				return Integer.MAX_VALUE / 2;
////			}
//			
//			//held and karp!
//			int[] nodeCosts = new int[nodes.length], nodeEdges = new int[nodes.length];
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
//				int cost = mstCost(startNode, endNode, remainingNodesList, edgesHeap, nodeEdges) + bestEdge.dist + secondBestEdge.dist;
////				System.out.println ("before cost: "+ cost);
//				for (Node node : remainingNodesList)
//					cost += 2 * nodeCosts[node.id];
//				cost += nodeCosts[startNode.id];
//				cost += nodeCosts[endNode.id];
////				System.out.println ("after cost: " + cost);
//				if (cost > upper_bound)
//				{
//					remainingNodesList.add(oneTreeNode);
//					return cost;
//				}
//				if (cost > lowerBound)
//					lowerBound = cost;
//				
//				//calculate step size
//				int sumSquareDiffs = 0;
//				for (Node node : remainingNodesList)
//				{
////					if (nodeEdges[node.id] < 1)
////						System.out.println ("this is wrong");
//					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == secondBestEdge.node1 || node == secondBestEdge.node2) ? 1 : 2;
//					sumSquareDiffs += (optimalNumEdges-nodeEdges[node.id]) * (optimalNumEdges-nodeEdges[node.id]);
//				}
//				sumSquareDiffs += (1-nodeEdges[startNode.id]) * (1-nodeEdges[startNode.id]);
//				sumSquareDiffs += (1-nodeEdges[endNode.id]) * (1-nodeEdges[endNode.id]);
//					
//				stepSize = (stepScale * (upper_bound - cost)) / sumSquareDiffs;
//				if (stepSize < limit)
//				{
////					System.out.println ("j: " + j);
//					break;
//				}
//				
//				for (Node node : remainingNodesList)
//				{
//					int optimalNumEdges = (node == bestEdge.node1 || node == bestEdge.node2 || node == bestEdge.node1 || node == bestEdge.node2) ? 1 : 2;
//					nodeCosts[node.id] += (int)(stepSize * (optimalNumEdges - nodeEdges[node.id]));
//				}
//				nodeCosts[startNode.id] += (int)(stepSize * (1 - nodeEdges[startNode.id]));
//				nodeCosts[endNode.id] += (int)(stepSize * (1 - nodeEdges[endNode.id]));
//				
//				stepScale *= stepChange;
//			}
//			
//			//add stuff back in
//			remainingNodesList.add(oneTreeNode);
//			usableEdges.addAll(removedEdges);
//		}
//		return lowerBound;
//	}
//	
//	private int mstCost(Node startNode, Node endNode, Collection<Node> remainingNodes, PriorityQueue<Edge> edgesQueue, int[] nodeEdges)
//	{
//		int totalCost = 0;
//		int numEdges = 0;
//		
//		UnionFind unionFind = new UnionFind(remainingNodes, startNode, endNode, nodeEdges.length);
//		/*for (Edge edge : requiredEdges)
//		{
//			Node root1 = unionFind.find(edge.node1), root2 = unionFind.find(edge.node2);
//			if (root1 == root2)
//				System.err.println ("We are not creating a tree here! minSpanningTreeCost");
//			unionFind.union(root1, root2);
//			totalCost += edge.dist;
//		}*/
//		while (numEdges < remainingNodes.size()+2-1 && edgesQueue.size() > 0)
//		{
////			System.out.println ("in while loop");
//			Edge e = edgesQueue.remove();
//			Node root1 = unionFind.find(e.node1);
//			Node root2 = unionFind.find(e.node2);
////			System.out.println ("finished finds");
//			if (root1 != root2)
//			{
//				totalCost += e.cost();
//				nodeEdges[e.node1.id]++;
//				nodeEdges[e.node2.id]++;
//				unionFind.union(root1, root2);
//				numEdges++;
//			}
//		}
//		
//		return totalCost;
//	}
//}
