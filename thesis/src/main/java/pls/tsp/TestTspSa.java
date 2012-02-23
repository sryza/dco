package pls.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.apache.log4j.Logger;

import pls.SaStats;

public class TestTspSa {
	
	private static final Logger LOG = Logger.getLogger(TestTspSa.class);
	
	public static void main(String[] args) throws IOException {
		File f = new File("../tsptests/eil51.258");
		TspLsCity[] cities = read(f, Integer.MAX_VALUE);
		Greedy greedy = new Greedy();
		cities = greedy.computeGreedy(cities);
		TspSaSolution solution = new TspSaSolution(cities, TspUtils.tourDist(cities));
		
		Random rand = new Random();
		SaStats stats = new SaStats();
		
		TspSaRunner runner = new TspSaRunner(solution, rand, stats);

		double temp = 5.0;
		long time = 15 * 1000;
		
		for (int i = 0; i < 20; i++) {
			LOG.info("temp is " + temp);
			TspSaSolution bestSolution = (TspSaSolution)runner.run(time, temp);
			LOG.info("best solution cost: " + bestSolution.getCost());
			temp = temp * 2 / 3;
		}
	}
	
	public static TspLsCity[] read(File f, int maxCities) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine().trim();
		int numVertices = Integer.parseInt(line);
		TspLsCity[] nodes = new TspLsCity[Math.min(maxCities, numVertices)];
		for (int i = 0; i < Math.min(maxCities, numVertices); i++) {
			String[] tokens = br.readLine().trim().replaceAll("[)(]", "").split("[, ]");
			nodes[i] = new TspLsCity(i, (int)Double.parseDouble(tokens[0]), (int)Double.parseDouble(tokens[1]));
		}
		
		br.close();
		return nodes;
	}
}
