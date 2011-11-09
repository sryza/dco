package bnb.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.lord.LordRunner;
import bnb.lord.VassalProxy;
import bnb.rpc.Ports;
import bnb.vassal.LordProxy;
import bnb.vassal.VassalRunner;

public class TspMain {
	private static final Logger LOG = Logger.getLogger(TspMain.class);
	
	public static void main(String[] args) throws IOException {
		File f = new File("../eil4.txt");
		int numCores = 2;//Runtime.getRuntime().availableProcessors() * 4;
		LOG.info("numCores: " + numCores);

		LordRunner lord = new LordRunner(Ports.DEFAULT_LORD_PORT);
		lord.start();
		LOG.info("started lord");
		
		LordProxy lordProxy = new LordProxy("localhost", Ports.DEFAULT_LORD_PORT); 
		LOG.info("instantiated lord proxy");

		VassalRunner vassal = new VassalRunner(lordProxy, numCores, 1, Ports.DEFAULT_VASSAL_PORT);
		vassal.start();
		LOG.info("started vassal");
		
		VassalProxy vassalProxy = new VassalProxy("localhost", Ports.DEFAULT_VASSAL_PORT);
		LOG.info("started vassal proxy");
		
		final int numCities = 4;
		
		City[] cities = read(f, numCities);
		TspProblem problem = new TspProblem(cities);
		List<City> remainingCities = Arrays.asList(cities).subList(1, cities.length);
		TspNode root = new TspNode(cities[0], cities[0], 1, null, remainingCities, problem);

		lord.runJob(root, problem, /*Double.MAX_VALUE*/300, Arrays.asList(vassalProxy));
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
