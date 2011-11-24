package bnb.tsp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import bnb.Problem;

public class TspProblem implements Problem {
	private static final Logger LOG = Logger.getLogger(TspProblem.class);
	//cities arranged where cities[i].id = i
	private City[] cities;
	
	private Collection<Edge> edges;
	
	/**
	 * Should only be called right before initFromBytes
	 */
	public TspProblem() {
		
	}
	
	public TspProblem(City[] cities) {
		this.cities = cities;
	}
	
	private void makeEdges() {
		edges = new ArrayList<Edge>((cities.length - 1) * cities.length / 2);
		for (int i = 0; i < cities.length; i++) {
			for (int j = i+1; j < cities.length; j++) {
				Edge e = new Edge(cities[i], cities[j]);
				edges.add(e);
			}
		}
	}
	
	public Collection<Edge> getEdges() {
		return edges;
	}
	
	public City[] getCities() {
		return cities;
	}
	
	public int getNumCities() {
		return cities.length;
	}
	
	public byte[] toBytes() {
		//TODO: tell it how big a buffer to use
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			//numcities
			dos.writeInt(cities.length);
			
			//write out all the city attrs
			for (City city : cities) {
				dos.writeInt(city.x);
				dos.writeInt(city.y);
				dos.writeInt(city.id);
			}
			return baos.toByteArray();
		} catch (IOException ex) {
			LOG.error("shouldn't get exception writing to byte array");
			return null;
		}
	}
	
	public void initFromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bais);
			int numCities = dis.readInt();
			
			cities = new City[numCities];
			for (int i = 0; i < numCities; i++) {
				int x = dis.readInt();
				int y = dis.readInt();
				int id = dis.readInt();
				cities[i] = new City(x, y, id);
			}
			
			makeEdges();
		} catch (IOException ex) {
			LOG.error("IOException reading from byte array, this should never happen", ex);
		}
	}
}
