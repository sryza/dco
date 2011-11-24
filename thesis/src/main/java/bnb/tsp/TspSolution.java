package bnb.tsp;

import java.util.Iterator;

import bnb.Solution;

public class TspSolution implements Solution {
	private City[] cities;
	
	public TspSolution(Iterator<City> citiesIter, int numCities) {
		cities = new City[numCities];
		for (int i = cities.length-1; i >= 0; i--) {
			cities[i] = citiesIter.next();
		}
//		this.cities = new City[cities.length];
//		System.arraycopy(cities, 0, this.cities, 0, cities.length);
	}
	
	public String toString() {
		StringBuilder sb =  new StringBuilder();
		for (City city : cities) {
			sb.append("(" + city.x + ", " + city.y + ", " + city.id + "), ");
		}
		return sb.toString();
	}
}
