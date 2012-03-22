package pls.vrp;

import java.util.Random;

import pls.PlsRunner;
import pls.PlsSolution;
import pls.vrp.hm.VrpCpStats;
import pls.vrp.hm.VrpSearcher;

public class VrpLnsRunner implements PlsRunner {
	@Override
	public PlsSolution[] run(PlsSolution plsSol, long timeMs, Random rand) {
		VrpPlsSolution solAndStuff = (VrpPlsSolution)plsSol;
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeMs;
		
		VrpSolution sol = solAndStuff.getSolution();
		VrpProblem problem = sol.getProblem();
		LnsRelaxer relaxer = new LnsRelaxer(solAndStuff.getRelaxationRandomness(), problem.getMaxDistance(), rand);
		
		outer:
		while (true) {
			for (int n = solAndStuff.getCurEscalation(); n <= solAndStuff.getMaxEscalation(); n++) { 
				for (int i = solAndStuff.getCurIteration(); i < solAndStuff.getMaxIterations(); i++) {
					if (System.currentTimeMillis() >= endTime) {
						break outer;
					}
					
					VrpSearcher solver = new VrpSearcher(problem);
					VrpCpStats stats = new VrpCpStats();
					
					VrpSolution partialSol = relaxer.relaxShaw(sol, n, -1);
					
					VrpSolution newSol = solver.solve(partialSol, sol.getToursCost(), solAndStuff.getMaxDiscrepancies(), stats);
					if (newSol != null) {
						sol = newSol;
						solAndStuff.setSolution(sol);
					}
					solAndStuff.setCurEscalation(n);
					solAndStuff.setCurIteration(i);
				}
			}	
			solAndStuff.setCurEscalation(1);
			solAndStuff.setCurIteration(0);
		}

		return new PlsSolution[] {solAndStuff};
	}
}
