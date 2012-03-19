package pls.vrp.hm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import pls.vrp.LnsRelaxer;
import pls.vrp.VrpCp;
import pls.vrp.VrpGreedyInitializer;
import pls.vrp.VrpProblem;
import pls.vrp.VrpReader;
import pls.vrp.VrpSolution;
import viz.VrpPanel;

public class TestHomemadeVrpCp {
	public static void main(String[] args) throws IOException {
		final int numCities = 100;
		
//		File f = new File("../vrptests/rc110_1.txt");
		File f = new File("../vrptests/r1.txt");
		VrpProblem problem = VrpReader.readSolomon(f, numCities);
		//seems like more for the first two and less for the last works
		VrpGreedyInitializer init = new VrpGreedyInitializer(1.0, 1.0, 0);
		VrpSolution sol = init.nearestNeighborHeuristic(problem);
		System.out.println(sol.getRoutes());
		System.out.println("Initial tour cost: " + sol.getToursCost());
		System.out.println("Tour num vehicles: " + sol.getNumVehicles());
		System.out.println("verified: " + sol.verify(problem));

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		VrpPanel panel = new VrpPanel();
		panel.setScale(problem);
		panel.setSolution(sol);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		
		final int maxIter = 30;
		final int maxSearches = 60;
		final int maxEscalation = 25;
//		int numToRelax = 7;
//		int numFailures = 0;
//		int failuresBeforeEscalate = 25;
		
		long start = System.currentTimeMillis();
		
		LnsRelaxer relaxer = new LnsRelaxer(25, problem.getMaxDistance(), new Random());
		
		outer:
		for (int j = 0; j < maxSearches; j++) {
			for (int n = 1; n <= maxEscalation; n++) { 
				for (int i = 0; i < maxIter; i++) {
					int dist =  sol.getToursCost(); //to bound cp
					VrpSolution partialSol = relaxer.relaxShaw(sol, n, -1);
					System.out.println("partial sol: " + partialSol.getRoutes());
					System.out.println("relaxed: " + partialSol.getUninsertedNodes());
					System.out.println("partialSol cost: " + partialSol.getToursCost());
					panel.setSolution(partialSol);

					VrpSearcher solver = new VrpSearcher(problem);
					VrpCpStats stats = new VrpCpStats();
					long solveStartTime = System.currentTimeMillis();
					VrpSolution newSol = solver.solve(partialSol, dist, 5, stats);
					long solveEndTime = System.currentTimeMillis();
					System.out.println("# nodes evaluated: " + stats.getNumNodesEvaluated());
					System.out.println("max insert time: " + stats.getMaxInsertTime());
					//			VrpCp jacopSolver = new VrpCp();
					//			VrpSolution newSol2 = jacopSolver.solve(problem, partialSol, dist-1);
					//			if (newSol2 != null) {
					//				System.out.println("jacop sol: " + newSol2.getRoutes());
					//			}
					
					if (newSol == null) {
						System.out.println("failed to find better");
						//				numFailures++;
						//				if (numFailures > failuresBeforeEscalate) {
						//					numToRelax++;
						//					numFailures = 0;
						//					System.out.println("INCREASING NUM TO RELAX TO " + numToRelax);
						//				}

					} else {
						System.out.println("verified: " + newSol.verify(problem));
						//				numFailures = 0;
						sol = newSol;
						panel.setSolution(sol);
						i = 0;
					}
					System.out.println("best cost so far: " + sol.getToursCost() + "\n");
					
					if (solveEndTime - solveStartTime > 10000) {
						break outer;
					}

				}
			}
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("done, bestCost=" + sol.getToursCost() + ", numVehicles="+sol.getNumVehicles());
		System.out.println("took " + (end - start) + " millis");
		panel.setSolution(sol);
	}
}
