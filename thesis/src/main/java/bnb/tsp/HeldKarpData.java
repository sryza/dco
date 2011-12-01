package bnb.tsp;

/**
 * Held & Karp data that can be passed from node to node
 */
public class HeldKarpData {
	private int[] nodeWeights;
	
	public HeldKarpData() {
		
	}
	
	public int[] getNodeWeights() {
		return nodeWeights;
	}
	
	public void setNodeWeights(int[] nodeWeights) {
		this.nodeWeights = nodeWeights;
	}
	
//	public City getOneTreeCity() {
//		
//	}
//	
//	public Edge getOneTreeBestEdge() {
//		
//	}
//	
//	public Edge getOneTreeSecondBestEdge() {
//		
//	}
}
