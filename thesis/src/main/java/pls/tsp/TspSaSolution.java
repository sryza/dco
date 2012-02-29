package pls.tsp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import pls.SaSolution;

public class TspSaSolution implements SaSolution {
	
	private static final Logger LOG = Logger.getLogger(TspSaSolution.class);
	
	private TspLsCity[] tour;
	private int cost;
	private double temp;
	
	public TspSaSolution(TspLsCity[] tour, int cost, double temp) {
		this.cost = cost;
		this.tour = tour;
		this.temp = temp;
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
	
	public double getTemperature() {
		return temp;
	}
	
	/**
	 * Verifies that the cost is accurate to the solution.
	 */
	public int verifyCost() {
		return TspUtils.tourDist(tour) - cost;
	}
	
	public static TspSaSolution fromStream(DataInputStream dis) throws IOException {
		int cost = dis.readInt();
		double temp = dis.readDouble();
		int numCities = dis.readInt();
		TspLsCity[] cities = new TspLsCity[numCities];
		for (int i = 0; i < numCities; i++) {
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			cities[i] = new TspLsCity(id, x, y);
		}
		
		return new TspSaSolution(cities, cost, temp);
	}
	
	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(cost);
		dos.writeDouble(temp);
		dos.writeInt(tour.length);
		for (TspLsCity city : tour) {
			dos.writeInt(city.id);
			dos.writeInt(city.x);
			dos.writeInt(city.y);
		}
	}
	
	public byte[] toBytes() {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bais);
		try {
			toStream(dos);
		} catch (IOException ex) {
			LOG.info("Failure to serialize solution to bytes, this shouldn't happen");
		}
		return bais.toByteArray();
	}
}
