package bnb.lord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.rpc.Ports;
import bnb.tsp.City;
import bnb.tsp.ProblemGen;
import bnb.tsp.TspNode;
import bnb.tsp.TspProblem;

public class LordMain {
	
	private static final Logger LOG = Logger.getLogger(LordMain.class);
	
	private static final int DEFAULT_LORD_PORT = Ports.DEFAULT_LORD_PORT;
	
	public static void main(String[] args) throws IOException {
		int lordPort = DEFAULT_LORD_PORT;
		File vassalFile = new File(args[0]);
		File citiesFile = new File(args[1]);
		int numCities = Integer.parseInt(args[2]);
		List<String> vassalHosts = readLines(vassalFile);
		
		final LordRunner lord = new LordRunner(lordPort);
		
		VassalProxy[] vassalProxies = new VassalProxy[vassalHosts.size()];
		for (int i = 0; i < vassalHosts.size(); i++) {
			vassalProxies[i] = new VassalProxy(vassalHosts.get(i), Ports.DEFAULT_VASSAL_PORT);
			lord.registerVassal(vassalProxies[i]);
		}
		
		lord.start();
		
		City[] cities = ProblemGen.read(citiesFile, numCities);
		
		TspProblem problem = new TspProblem(cities);

		LinkedList<City> remainingCities = new LinkedList<City>();
		remainingCities.addAll(Arrays.asList(cities).subList(1, cities.length));
		TspNode root = new TspNode(cities[0], cities[0], 1, null, remainingCities, null, -1, problem);

		lord.runJob(root, problem, Double.MAX_VALUE, vassalHosts.size(), 0);
	}
	
	private static List<String> readLines(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
}
