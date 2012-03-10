package pls.vrp;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestVrpCp {
	public static void main(String[] args) throws IOException {
		final int numRoutes = 8;
		final int numVehicles = 25;
		
		File f = new File("../vrptests/r1.txt");
		VrpProblem problem = VrpReader.readSolomon(f, numVehicles);
		List<Integer> unroutedNodes = new LinkedList<Integer>();
		for (int i = 0; i < problem.getNumCities(); i++) {
			unroutedNodes.add(i);
		}
		List<List<Integer>> routes = new LinkedList<List<Integer>>();
		for (int i = 0; i < numRoutes; i++) {
			routes.add(new LinkedList<Integer>());
		}
		
		VrpSolution sol = new VrpSolution(routes, unroutedNodes, problem);//best found is 8
		VrpCp solver = new VrpCp();
		VrpSolution newSol = solver.solve(problem, sol, Integer.MAX_VALUE);
		System.out.println("verified: " + newSol.verify(problem));
	}
}
