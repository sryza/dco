package bnb.tsp;

public class Edge {
	public City node1;
	public City node2;
	public int dist;
	public int extra;

	public Edge(City node1, City node2) {
		this.node1 = node1;
		this.node2 = node2;
		this.dist = node1.dist(node2);
	}	
}
