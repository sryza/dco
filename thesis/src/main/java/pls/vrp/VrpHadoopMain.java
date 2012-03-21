package pls.vrp;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pls.PlsMaster;
import pls.PlsSolution;

public class VrpHadoopMain {
	private static final Logger LOG = Logger.getLogger(VrpHadoopMain.class);
	
	public static void main(String[] args) throws IOException {
		int numTasks = Integer.parseInt(args[0]);
		int numRuns = Integer.parseInt(args[1]);
		File inputFile = new File("../vrptests/rc110_1.txt");
		final int maxDiscrepancies = 5;
		final int relaxationRandomness = 25;
		final int maxIter = 30;
		final int maxEscalation = 25;
		
		VrpProblem problem = VrpReader.readSolomon(inputFile, Integer.MAX_VALUE); 
		
		List<PlsSolution> initSols = new ArrayList<PlsSolution>();
		int bestStartCost = Integer.MAX_VALUE;
		//create different initializations by varying weights on initializer
		for (int i = 0; i < numTasks; i++) {
			double timeDiffWeight = Math.random() * .3;
			double distanceWeight = Math.random() * .5;
			double urgencyWeight = Math.random() * .2;
			VrpGreedyInitializer initializer = new VrpGreedyInitializer(timeDiffWeight, distanceWeight, urgencyWeight);
			VrpSolution sol = initializer.nearestNeighborHeuristic(problem);
//			NumberFormat nf = NumberFormat.getPercentInstance();
//			System.out.println(nf.format(timeDiffWeight) + ", " + nf.format(distanceWeight) + ", " + nf.format(urgencyWeight)
//					+ ": " + sol.getToursCost());
			if (sol.getToursCost() < bestStartCost) {
				bestStartCost = sol.getToursCost();
			}
			initSols.add(new VrpPlsSolution(sol, maxIter, maxEscalation, relaxationRandomness, maxDiscrepancies));
		}
		
		PlsMaster master = new PlsMaster();
		String dir = "/users/sryza/testdir/" + System.currentTimeMillis() + "/";
		LOG.info("results going to " + dir);
		master.run(numRuns, initSols, bestStartCost, dir, VrpMapper.class, VrpReducer.class);
	}
}
