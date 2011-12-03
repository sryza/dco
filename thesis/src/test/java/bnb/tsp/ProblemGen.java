package bnb.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProblemGen {
	
	private static final File PROB_FILE = new File("../tsptests/eil51.258");
	private static final int MAX_CITIES = 51;
	
	public static City[] genCities(int numCities) throws IOException {
		if (numCities > MAX_CITIES) {
			throw new IllegalArgumentException("too many cities requested");
		}
		
		City[] cities = read(PROB_FILE, numCities);
		return cities;
	}
	
	public static City[] read(File f, int maxCities) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine().trim();
		int numVertices = Integer.parseInt(line);
		City[] nodes = new City[Math.min(maxCities, numVertices)];
		for (int i = 0; i < Math.min(maxCities, numVertices); i++)
		{
			String[] tokens = br.readLine().trim().replaceAll("[)(]", "").split("[, ]");
			nodes[i] = new City((int)Double.parseDouble(tokens[0]), (int)Double.parseDouble(tokens[1]), i);
		}
		
		br.close();
		return nodes;
	}

}
