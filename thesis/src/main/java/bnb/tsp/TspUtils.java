package bnb.tsp;

import java.util.Iterator;

public class TspUtils {
	public static int cost3opt(City[] nodes, int index1, int index2, int index3)
	{
		int delta = 0;
		if((index3-index2+nodes.length)%nodes.length < (index1-index2+nodes.length)%nodes.length)
		{
			delta -= nodes[index1].dist(nodes[wrap(nodes, index1+1)]);
			delta += nodes[wrap(nodes, index1+1)].dist(nodes[index2]);
			delta -= nodes[index2].dist(nodes[wrap(nodes, index2-1)]);
			delta += nodes[wrap(nodes, index2-1)].dist(nodes[index3]);
			delta -= nodes[index3].dist(nodes[wrap(nodes, index3-1)]);
			delta += nodes[index1].dist(nodes[wrap(nodes, index3-1)]);
		}
		else
		{
			delta -= nodes[index1].dist(nodes[wrap(nodes, index1+1)]);
			delta += nodes[index3].dist(nodes[wrap(nodes, index2-1)]);
			delta -= nodes[index2].dist(nodes[wrap(nodes, index2-1)]);
			delta += nodes[index2].dist(nodes[wrap(nodes, index1+1)]);
			delta -= nodes[index3].dist(nodes[wrap(nodes, index3+1)]);
			delta += nodes[index1].dist(nodes[wrap(nodes, index3+1)]);
		}
		return delta;
	}
	
//	/**
//	 * Assumes that cityX is between cityX and cityX
//	 */
//	public static int cost3opt(City city1, City city1Next, City city2, City city2Prev, City city3)
//	{
//		int delta = 0;
//		if((index3-index2+nodes.length)%nodes.length < (index1-index2+nodes.length)%nodes.length)
//		{
//			delta -= nodes[index1].dist(nodes[wrap(nodes, index1+1)]);
//			delta += nodes[wrap(nodes, index1+1)].dist(nodes[index2]);
//			delta -= nodes[index2].dist(nodes[wrap(nodes, index2-1)]);
//			delta += nodes[wrap(nodes, index2-1)].dist(nodes[index3]);
//			delta -= nodes[index3].dist(nodes[wrap(nodes, index3-1)]);
//			delta += nodes[index1].dist(nodes[wrap(nodes, index3-1)]);
//		}
//		else
//		{
//			delta -= nodes[index1].dist(nodes[wrap(nodes, index1+1)]);
//			delta += nodes[index3].dist(nodes[wrap(nodes, index2-1)]);
//			delta -= nodes[index2].dist(nodes[wrap(nodes, index2-1)]);
//			delta += nodes[index2].dist(nodes[wrap(nodes, index1+1)]);
//			delta -= nodes[index3].dist(nodes[wrap(nodes, index3+1)]);
//			delta += nodes[index1].dist(nodes[wrap(nodes, index3+1)]);
//		}
//		return delta;
//	}
	
	public static int cost2opt(City[] nodes, int index1, int index2)
	{
		//add edge from index1 to predecessor of index2
		//add edge from successor of index1 to index2
		//remove edge from index1 to it's successor
		//remove edge from index2 to it's predecessor
		return nodes[index1].dist(nodes[index2-1]) + nodes[index1+1].dist(nodes[index2]) 
			- nodes[index1].dist(nodes[index1+1]) - nodes[index2].dist(nodes[index2-1]);
	}
	
	/**
	 * Computes the cost of removing the edges between city1 and its successor and
	 * city 2 and its predecessor, and adding the edges from city1 to the 
	 * predecessor of city2 and from the successor of city1 to city2
	 */
	public static int cost2opt(City city1, City city1Next, City city2, City city2Prev) {
		//add edge from index1 to predecessor of index2
		//add edge from successor of index1 to index2
		//remove edge from index1 to it's successor
		//remove edge from index2 to it's predecessor
		return city1.dist(city2Prev) + city1Next.dist(city2) 
			- city1.dist(city1Next) - city2.dist(city2Prev);
	}
	
	public static int wrap(City[] nodes, int index) {
		return (index + nodes.length) % nodes.length;
	}
	
	public static boolean edgeCrossesPath(Iterator<City> path, Edge edge) {
		City prev = path.next();
		while (path.hasNext()) {
			City cur = path.next();
			if (intersects(edge, cur, prev)) {
				return true;
			}
			prev = cur;
		}
		return false;
	}
	
	public static boolean intersects(Edge edge, City city1, City city2) {
		int x1 = edge.node1.x;
		int x2 = edge.node2.x;
		int x3 = city1.x;
		int x4 = city2.x;
		int y1 = edge.node1.y;
		int y2 = edge.node2.y;
		int y3 = city1.y;
		int y4 = city2.y;

		int denom = (y4-y3)*(x2-x1)-(x4-x3)*(y2-y1);
		double ua = (double)((x4-x3)*(y1-y3)-(y4-y3)*(x1-x3)) / denom;
		double ub = (double)((x2-x1)*(y1-y3)-(y2-y1)*(x1-x3)) / denom;
		return ua > 0 && ub > 0 && ua < 1 && ua < 1;
	}
	
	public static void main(String[] args) {
		City c1 = new City(0, 5, -1);
		City c2 = new City(5, 5, -1);
		City c3 = new City(0, 5, -1);
		City c4 = new City(5, 0, -1);
		Edge edge = new Edge(c3, c4);
		System.out.println(intersects(edge, c1, c2));
	}
}
