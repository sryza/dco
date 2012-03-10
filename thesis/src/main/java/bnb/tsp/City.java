package bnb.tsp;

import tsp.TspCity;

public class City extends TspCity {

	public ThreadLocal<Integer> threadLocalMark = new ThreadLocal<Integer>();
	
	public City(int x, int y, int id) {
		super(id, x, y);
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
