package gc;

import java.io.File;
import java.util.Random;

import org.apache.log4j.Logger;

public class TestGcLocalSearch {
	private static final Logger LOG = Logger.getLogger(TestGcLocalSearch.class);
	
	public static void main(String[] args) throws Exception {
		File inputFile = new File("../gctests/dsjc250.5.col");
		int k = 28;
		
		Random rand = new Random();
		GcProblem problem = GcReader.read(inputFile);
		GcInitializer initializer = new GcInitializer(rand);
		GcSolution initSol = initializer.makeInitialColoring(problem, k);
		
		LOG.info("Initial sol cost: " + initSol.getCost());
		LOG.info("Calculated cost: " + initSol.calcCost(problem));
		
		GcTabuSearchRunner lsRunner = new GcTabuSearchRunner(40, .6, rand);
		GcSolution improved = lsRunner.run(initSol.getNodeColors(), problem.getNodeNeighbors(), k, initSol.getCost(), 
				System.currentTimeMillis() + 3000);
		LOG.info("Final cost: " + improved.getCost());
		LOG.info("Calculated cost: " + improved.calcCost(problem));
	}
}
