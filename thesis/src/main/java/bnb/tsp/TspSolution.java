package bnb.tsp;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.Solution;

public class TspSolution implements Solution {
	private static final Logger LOG = Logger.getLogger(TspSolution.class);

	private City[] cities;	
	
	public TspSolution(Iterator<City> citiesIter, int numCities) {
		cities = new City[numCities];
		for (int i = cities.length-1; i >= 0; i--) {
			cities[i] = citiesIter.next();
		}
	}
	
	public TspSolution(Iterator<City> citiesIter, List<City> extra, int numCities) {
		cities = new City[numCities];
		
		int i = cities.length-extra.size();
		for (City city : extra) {
			cities[i] = city;
			i++;
		}
		i = cities.length-extra.size()-1;
		while (citiesIter.hasNext()) {
			cities[i] = citiesIter.next();
			i--;
		}
		if (i != -1) {
			LOG.error("something's wrong");
		}
	}
	
	public String toString() {
		StringBuilder sb =  new StringBuilder();
		for (City city : cities) {
			sb.append("(" + city.x + ", " + city.y + ", " + city.id + "), ");
		}
		return sb.toString();
	}
}
