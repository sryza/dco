package bnb.tsp;

public class City {

	public int x;
	public int y;
	public volatile int index; //into the city array
	
	public int cost; //for held & karp bounds
	
	public City(int x, int y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
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
		return new City(x, y, index);
	}
}
