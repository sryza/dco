package bnb.tsp;

public class City {

	public int x;
	public int y;
	public int id;
	public volatile int index; //into the city array
	
	public int cost; //for held & karp bounds
	
	public City(int x, int y, int index, int id) {
		this.x = x;
		this.y = y;
		this.index = index;
		this.id = id;
	}
	
	public int dist(City other) {
		int xDiff = x-other.x;
		int yDiff = y-other.y;
		return (int)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	public String toString() {
		return "City[x=" + x + ",y=" + y + ",index=" + index + "]";
	}
	
	public City copy() {
		return new City(x, y, index, id);
	}
}
