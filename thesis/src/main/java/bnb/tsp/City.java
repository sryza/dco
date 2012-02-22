package bnb.tsp;

public class City {

	public int x;
	public int y;
	public int id;
	public ThreadLocal<Integer> threadLocalMark = new ThreadLocal<Integer>();
	
	public City(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public int dist(City other) {
		int xDiff = x-other.x;
		int yDiff = y-other.y;
		return (int)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	@Override
	public String toString() {
		return "City[x=" + x + ",y=" + y + ",id=" + id + "]";
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
