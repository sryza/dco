package pls.tsp;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import pls.PlsSolution;

public class TspSaSolution implements PlsSolution {
	
	private static final Logger LOG = Logger.getLogger(TspSaSolution.class);
	
	private TspLsCity[] tour;
	private int cost;
	private double temp;
	private double scaler;
	
	public TspSaSolution() {
	}
	
	public TspSaSolution(TspLsCity[] tour, int cost, double temp, double scaler) {
		this.cost = cost;
		this.tour = tour;
		this.temp = temp;
		this.scaler = scaler;
	}
	
	public double getCost() {
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
	
	public double getScaler() {
		return scaler;
	}
	
	/**
	 * Verifies that the cost is accurate to the solution.
	 */
	public int verifyCost() {
		return TspUtils.tourDist(tour) - cost;
	}
	
	@Override
	public void readFields(DataInput dis) throws IOException {
		cost = dis.readInt();
		temp = dis.readDouble();
		scaler = dis.readDouble();
		int numCities = dis.readInt();
		tour = new TspLsCity[numCities];
		for (int i = 0; i < numCities; i++) {
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			tour[i] = new TspLsCity(id, x, y);
		}
	}
	
	@Override
	public void write(DataOutput dos) throws IOException {
		dos.writeInt(cost);
		dos.writeDouble(temp);
		dos.writeDouble(scaler);
		dos.writeInt(tour.length);
		for (TspLsCity city : tour) {
			dos.writeInt(city.id);
			dos.writeInt(city.x);
			dos.writeInt(city.y);
		}
	}
	
	public int serializedSize() {
		return 4 + 8 + 8 + 4 + tour.length * 4 * 3;
	}
	
	public byte[] toBytes() {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bais);
		try {
			write(dos);
		} catch (IOException ex) {
			LOG.info("Failure to serialize solution to bytes, this shouldn't happen");
		}
		return bais.toByteArray();
	}

	@Override
	public int getSolutionId() {
		return -1;
	}
	
	@Override
	public int getParentSolutionId() {
		return -1;
	}
	
	@Override
	public void setParentSolutionId(int id) {
	}
	
	@Override
	public void setSolutionId(int id) {
	}
}
