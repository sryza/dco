package pls.tsp;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import pls.SaStats;
import pls.TspLsCityReader;

public class TestTspSa {
	
	private static final Logger LOG = Logger.getLogger(TestTspSa.class);
	
	public static void main(String[] args) throws IOException {
		File f = new File("../tsptests/eil51.258");
		ArrayList<TspLsCity> citiesList = TspLsCityReader.read(f, Integer.MAX_VALUE);
		Greedy greedy = new Greedy();
		TspLsCity[] cities = greedy.computeGreedy(citiesList);
		double temp = 5.0;
		double scaler = 2.0/3.0;
		TspSaSolution solution = new TspSaSolution(cities, TspUtils.tourDist(cities), temp, scaler);
		
		Random rand = new Random();
		SaStats stats = new SaStats();
		
		TspSaRunner runner = new TspSaRunner(rand, stats);

		long time = 15 * 1000;
		
		for (int i = 0; i < 20; i++) {
			LOG.info("temp is " + temp);
			TspSaSolution[] solutions = runner.run(solution, time);
			LOG.info("best solution cost: " + solutions[0].getCost());
			temp = temp * 2 / 3;
		}
	}
}
