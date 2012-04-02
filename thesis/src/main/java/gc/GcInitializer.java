package gc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class GcInitializer {
	
	private Random rand;
	
	public GcInitializer(Random rand) {
		this.rand = rand;
	}
	
	public GcSolution makeInitialColoring(GcProblem problem, int k) {
		int[][] nodeNeighbors = problem.getNodeNeighbors();
		Set<Integer>[] nodeAllowedColors = new Set[problem.getNumNodes()];
		for (int i = 0; i < problem.getNumNodes(); i++) {
			nodeAllowedColors[i] = new TreeSet<Integer>();
			for (int c = 0; c < k; c++) {
				nodeAllowedColors[i].add(c);
			}
		}
		
		//color optimally
		int[] nodeColors = new int[problem.getNumNodes()];
		Arrays.fill(nodeColors, -1);
		while (true) {
			//choose most constrained vertex
			int bestNode = -1;
			int numColors = Integer.MAX_VALUE;
			for (int i = 0; i < problem.getNumNodes(); i++) {
				if (nodeColors[i] != -1 || nodeAllowedColors[i].size() == 0) {
					continue;
				}
				if (nodeAllowedColors[i].size() < numColors) {
					bestNode = i;
					numColors = nodeAllowedColors[i].size();
				}
			}
			if (bestNode == -1) {
				break;
			}
			
			int color = nodeAllowedColors[bestNode].iterator().next();
			nodeColors[bestNode] = color;
			for (int neighbor : nodeNeighbors[bestNode]) {
				nodeAllowedColors[neighbor].remove(color);
			}
		}
		
		int cost = 0;
		//color remaining randomly
		for (int i = 0; i < nodeColors.length; i++) {
			if (nodeColors[i] == -1) {
				nodeColors[i] = rand.nextInt(k);
				for (int neighbor : nodeNeighbors[i]) {
					if (nodeColors[neighbor] == nodeColors[i]) {
						cost++;
					}
				}
			}
		}
		
		return new GcSolution(nodeColors, cost);
	}
}
