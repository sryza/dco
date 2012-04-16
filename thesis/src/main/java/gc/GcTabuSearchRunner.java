package gc;

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;

public class GcTabuSearchRunner {
	
	private static final Logger LOG = Logger.getLogger(GcTabuSearchRunner.class);
	
	private int A;
	private double alpha;
	private Random rand;
	
	public GcTabuSearchRunner(int A, double alpha, Random rand) {
		this.A = A;
		this.alpha = alpha;
		this.rand = rand;
	}
	
	public GcSolution run(GcProblem problem, GcSolution sol, int k, long duration) {
		return run(sol.getNodeColors(), problem.getNodeNeighbors(), k, sol.getCost(), System.currentTimeMillis() + duration);
	}
	
	public GcSolution run(int[] nodeColors, int[][] nodeNeighbors, int k, int cost, long endTime) {
		
		//each entry of the tabu table contains the iteration at which the tabu move is
		//set to expire
		int[][] tabuTable = new int[nodeColors.length][k];
		for (int[] tabuRow : tabuTable) {
			Arrays.fill(tabuRow, -1); //so nothing is tabu at beginning
		}
		int[][] numNeighborsOfColor = buildNumNeighborsOfColor(nodeColors, nodeNeighbors, k);
		
		int[] bestSol = new int[nodeColors.length];
		System.arraycopy(nodeColors, 0, bestSol, 0, nodeColors.length);
		int bestSolCost = cost;
		
		//run the search
		int iter = 0;
		while (System.currentTimeMillis() < endTime) {
			if (cost == 0) {
				return new GcSolution(nodeColors, cost);
			}
			int deltaCost = move(nodeColors, nodeNeighbors, numNeighborsOfColor, tabuTable, iter, cost, bestSolCost, k);
			cost += deltaCost;
			if (cost < bestSolCost) {
//				LOG.info("new best cost=" + bestSolCost + ", iter=" + iter);
			}
			if (cost <= bestSolCost) {
				System.arraycopy(nodeColors, 0, bestSol, 0, nodeColors.length);
				bestSolCost = cost;
			}
			
			iter++;
		}
		return new GcSolution(bestSol, bestSolCost);
	}
	
	private int[][] buildNumNeighborsOfColor(int[] nodeColors, int[][] nodeNeighbors, int k) {
		int[][] numNeighborsOfColor = new int[nodeColors.length][k];
		for (int i = 0; i < nodeColors.length; i++) {
			for (int neighbor : nodeNeighbors[i]) {
				numNeighborsOfColor[neighbor][nodeColors[i]]++;
			}
		}
		return numNeighborsOfColor;
	}
	
	/**
	 * @return
	 * 		delta cost
	 */
	private int move(int[] nodeColors, int[][] nodeNeighbors, int[][] numNeighborsOfColor, int[][] tabuTable, int iter, int curCost, 
			int bestCostEver, int k) {
		//for each point, we keep track of the number of neighbors with each color
		//for even more efficiency, we could sort the list of colors for each node
		//that way determining the cost of recoloring a node is O(1)
		
		//find best move
		int bestNode = -1;
		int bestColor = -1;
		int bestCost = Integer.MAX_VALUE;
		for (int i = 0; i < nodeColors.length; i++) {
			int oldColorCost = numNeighborsOfColor[i][nodeColors[i]];
			for (int j = 0; j < k; j++) {
				if (j == nodeColors[i]) {
					continue;
				}
				int cost = numNeighborsOfColor[i][j] - oldColorCost;
				
				//let it ride if we have the absolute best cost
				if (isTabu(i, j, tabuTable, iter) && curCost + cost >= bestCostEver) {
					continue;
				}
				
				if (cost < bestCost) {
					bestNode = i;
					bestColor = j;
					bestCost = cost;
				}
			}
		}
		
		//carry out move
		int[] neighbors = nodeNeighbors[bestNode];
		for (int neighbor : neighbors) {
			numNeighborsOfColor[neighbor][nodeColors[bestNode]]--;
			numNeighborsOfColor[neighbor][bestColor]++;
		}
		makeTabu(bestNode, nodeColors[bestNode], tabuTable, iter, curCost);
		nodeColors[bestNode] = bestColor;
		
		return bestCost;
	}
	
	private boolean isTabu(int node, int color, int[][] tabuTable, int iter) {
		return iter <= tabuTable[node][color];
	}
	
	private void makeTabu(int node, int color, int[][] tabuTable, int iter, int numConflicting) {
		int tenure = (int)(rand.nextInt(A) + alpha * numConflicting);
		tabuTable[node][color] = iter + tenure;
	}
}
