package pls.vrp;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import viz.VrpPanel;

public class TestVrpLns {
	public static void main(String[] args) throws IOException {
		final int numRoutes = 8;
		final int numVehicles = 100;
		
		File f = new File("../vrptests/r1.txt");
		VrpProblem problem = VrpReader.readSolomon(f, numVehicles);
		//seems like more for the first two and less for the last works
		VrpGreedyInitializer init = new VrpGreedyInitializer(1.0, 1.0, 0);
		VrpSolution sol = init.nearestNeighborHeuristic(problem);
		System.out.println("Initial tour cost: " + sol.getToursCost());
		System.out.println("Tour num vehicles: " + sol.getNumVehicles());

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		VrpPanel panel = new VrpPanel();
		panel.setScale(problem);
		panel.setSolution(sol);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		LnsRelaxer relaxer = new LnsRelaxer(25, problem.getMaxDistance(), new Random());
		
		int numToRelax = 5;
		int numFailures = 0;
		for (int i = 0; i < 1; i++) {
			int dist =  sol.getToursCost(); //to bound cp
			VrpSolution partialSol = relaxer.relaxShaw(sol, numToRelax, -1);
			System.out.println("partialSol cost: " + partialSol.getToursCost());
			panel.setSolution(partialSol);
		
			VrpCp solver = new VrpCp();
			VrpSolution newSol = solver.solve(problem, partialSol, dist-1);
			if (newSol == null) {
				System.out.println("failed to find better");
				numFailures++;
				if (numFailures > 3) {
					numToRelax++;
					numFailures = 0;
					System.out.println("INCREASING NUM TO RELAX TO " + numToRelax);
				}
				
			} else {
				System.out.println("verified: " + newSol.verify(problem));
				numFailures = 0;
				sol = newSol;
				panel.setSolution(sol);
			}
		}
	}
}
