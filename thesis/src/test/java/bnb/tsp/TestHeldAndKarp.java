package bnb.tsp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

import junit.framework.Assert;

import org.junit.Test;

public class TestHeldAndKarp {
	@Test
	public void testMinimumSpanningTree() {
		int[][] coors = {
				{0,1},
				{1,0},
				{0,0},
				{-1, 0}
		};
		City[] cities = new City[coors.length];
		for (int i = 0; i < coors.length; i++) {
			cities[i] = new City(coors[i][0], coors[i][1], i);
		}
		
		PriorityQueue<Edge> edgesQueue = new PriorityQueue<Edge>();
		for (int i = 0; i < cities.length; i++) {
			for (int j = i+1; j < cities.length; j++) {
				Edge e = new Edge(cities[i], cities[j]);
				edgesQueue.add(e);
			}
		}
		
		LinkedList<City> remainingNodes = new LinkedList<City>();
		remainingNodes.addAll(Arrays.asList(cities).subList(2, cities.length));
		int[] nodeEdges = new int[cities.length];
		
//		int cost = HeldAndKarp.mstCost(cities[0], cities[1], remainingNodes, edgesQueue, nodeEdges);
//		Assert.assertEquals(3, cost);
	}
}
