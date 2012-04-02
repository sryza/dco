package gc;

public class GcProblem {
	private int[][] nodeNeighbors;
	
	public GcProblem(int[][] nodeNeighbors) {
		this.nodeNeighbors = nodeNeighbors;
	}
	
	public int getNumNodes() {
		return nodeNeighbors.length;
	}
	
	public int[][] getNodeNeighbors() {
		return nodeNeighbors;
	}
}
