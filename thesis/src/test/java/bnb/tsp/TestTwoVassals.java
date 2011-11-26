package bnb.tsp;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import bnb.lord.LordRunner;
import bnb.lord.VassalProxy;
import bnb.rpc.Ports;
import bnb.vassal.LordProxy;
import bnb.vassal.VassalRunner;

public class TestTwoVassals {
	private static final Logger LOG = Logger.getLogger(TestTwoVassals.class);
	
	public static void main(String[] args) throws IOException {
		final int numCores = 4;//Runtime.getRuntime().availableProcessors() * 4;
		final int numVassals = 4;
		LOG.info("numCores: " + numCores);

		LordRunner lord = new LordRunner(Ports.DEFAULT_LORD_PORT);
		lord.start();
		LOG.info("started lord");
		
		VassalRunner[] vassals = new VassalRunner[numVassals];
		for (int i = 0; i < vassals.length; i++) {
			LordProxy lordProxy = new LordProxy("localhost", Ports.DEFAULT_LORD_PORT);
			LOG.info("instantiated lord proxy");
			vassals[i] = new VassalRunner(lordProxy, numCores, i, Ports.DEFAULT_VASSAL_PORT+i);
			vassals[i].start();
			LOG.info("started vassal " + i);
		}
		
		VassalProxy[] vassalProxies = new VassalProxy[vassals.length];
		for (int i = 0; i < vassals.length; i++) {
			vassalProxies[i] = new VassalProxy("localhost", Ports.DEFAULT_VASSAL_PORT + i);
			LOG.info("instantiated vassal proxy " + i);
			lord.registerVassal(vassalProxies[i], i);
		}
		
		final int numCities = 22;
		
		City[] cities = ProblemGen.genCities(numCities);
		TspProblem problem = new TspProblem(cities);
		LinkedList<City> remainingCities = new LinkedList<City>();
		remainingCities.addAll(Arrays.asList(cities).subList(1, cities.length));

		TspNode root = new TspNode(cities[0], cities[0], 1, null, remainingCities, problem);

		lord.runJob(root, problem, /*Double.MAX_VALUE*/300, Arrays.asList(vassalProxies), 0);
	}
}
