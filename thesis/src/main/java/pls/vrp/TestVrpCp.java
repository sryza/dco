/**
 * Copyright 2012 Sandy Ryza
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
