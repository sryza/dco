package pls.tsp;

import java.util.*;

public class Greedy {
	
	public TspLsCity[] computeGreedy(TspLsCity[] nodes) {
		//find farthest nodes and add them to the linked list
		TspLsCity node1 = null, node2 = null;
		int maxDist = Integer.MIN_VALUE;
		for (int i = 0; i < nodes.length; i++) {
			for (int j = i + 1; j < nodes.length; j++) {
				int dist = TspUtils.dist(nodes[i], nodes[j]);
				if (dist > maxDist) {
					maxDist = dist;
					node1 = nodes[i];
					node2 = nodes[j];
				}
			}
		}
		
		ArrayList<TspLsCity> sol = new ArrayList<TspLsCity>(nodes.length);
		ArrayList<TspLsCity> remaining = new ArrayList<TspLsCity>(nodes.length-2);
		int[] minDists = new int[nodes.length];
		TspLsCity[] minDistNodes = new TspLsCity[nodes.length];
		for (TspLsCity node : nodes) {
			if (node != node1 && node != node2) {
				remaining.add(node);
				int dist1 = TspUtils.dist(node, node1), dist2 = TspUtils.dist(node, node2);
				minDists[node.id] = Math.min(dist1, dist2);
				minDistNodes[node.id] = (dist1 < dist2) ? node1 : node2;
			}
		}
		sol.add(node1);
		sol.add(node2);
		
		//while there are still nodes not used
		while (!remaining.isEmpty())
		{
//			if (remaining.size() % 100 == 0)
//				System.out.println (remaining.size());
			
			//find the one with the shortest distance to any of the tour nodes
			int minDist = Integer.MAX_VALUE, bestRemIndex = -1;
			TspLsCity bestSolNode = null;
			for (int i = 0; i < remaining.size(); i++)
			{
				TspLsCity remNode = remaining.get(i);
				if (minDists[remNode.id] < minDist)
				{
					minDist = minDists[remNode.id];
					bestRemIndex = i;
					bestSolNode = minDistNodes[remNode.id];
				}
			}
			
			//recompute minDists
			TspLsCity bestRemNode = remaining.get(bestRemIndex);
			for (int i = 0; i < remaining.size(); i++)
			{
				TspLsCity remNode = remaining.get(i);
				if (remNode == bestRemNode)
					continue;
				int dist = TspUtils.dist(remNode, bestRemNode);
				if (dist < minDists[remNode.id])
				{
					minDists[remNode.id] = dist;
					minDistNodes[remNode.id] = bestRemNode;
				}
			}
			
			int bestSolIndex = -1;
			for (int j = 0; j < sol.size(); j++)
			{
				TspLsCity solNode = sol.get(j);
				if (solNode == bestSolNode)
					bestSolIndex = j;
			}
			
			//check both ways of inserting that one
			int costInsertForward = TspUtils.dist(remaining.get(bestRemIndex), sol.get((bestSolIndex+1)%sol.size())) - 
									TspUtils.dist(sol.get((bestSolIndex)), sol.get((bestSolIndex+1)%sol.size()));
			int costInsertBackward = TspUtils.dist(remaining.get(bestRemIndex), sol.get((bestSolIndex-1+sol.size())%sol.size())) - 
									TspUtils.dist(sol.get((bestSolIndex)), sol.get((bestSolIndex-1+sol.size())%sol.size()));
			//insert using the best one
			if (costInsertForward < costInsertBackward)
				sol.add(bestSolIndex+1, remaining.get(bestRemIndex));
			else
				sol.add(bestSolIndex, remaining.get(bestRemIndex));
			
			remaining.remove(bestRemIndex);
		}
		
		return sol.toArray(new TspLsCity[sol.size()]);
	}
}
