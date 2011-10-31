package bnb.tsp;

import bnb.Solution;

public class TspSolution implements Solution {
	private City[] cities;
	
	public TspSolution(City[] cities) {
		this.cities = new City[cities.length];
		System.arraycopy(cities, 0, this.cities, 0, cities.length);
	}
	
	public String toString() {
		StringBuilder sb =  new StringBuilder();
		for (City city : cities) {
			sb.append("(" + city.x + ", " + city.y + "), ");
		}
		return sb.toString();
	}
}
