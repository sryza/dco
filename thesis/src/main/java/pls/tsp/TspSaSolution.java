package pls.tsp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pls.SaSolution;

public class TspSaSolution implements SaSolution {
	private TspLsCity[] tour;
	private int cost;
	
	public TspSaSolution(TspLsCity[] tour, int cost) {
		this.cost = cost;
		this.tour = tour;
	}
	
	@Override
	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public TspLsCity[] getTour() {
		return tour;
	}
	
	public int numCities() {
		return tour.length;
	}
	
	/**
	 * Verifies that the cost is accurate to the solution.
	 */
	public int verifyCost() {
		return TspUtils.tourDist(tour) - cost;
	}
	
	public static TspSaSolution fromStream(DataInputStream dis) throws IOException {
		int numCities = dis.readInt();
		TspLsCity[] cities = new TspLsCity[numCities];
		for (int i = 0; i < numCities; i++) {
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			cities[i] = new TspLsCity(id, x, y);
		}
		
		return new TspSaSolution(cities, TspUtils.tourDist(cities));
	}
	
	public void toStream(DataOutputStream dis) throws IOException {
		dis.writeInt(tour.length);
		for (TspLsCity city : tour) {
			dis.writeInt(city.id);
			dis.writeInt(city.x);
			dis.writeInt(city.y);
		}
	}
}
