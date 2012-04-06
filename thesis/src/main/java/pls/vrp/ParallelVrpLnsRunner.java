package pls.vrp;

import java.util.Random;

import org.apache.log4j.Logger;

import pls.PlsSolution;
import pls.map.PlsRunner;
import pls.vrp.hm.VrpCpStats;
import pls.vrp.hm.VrpSearcher;

public class ParallelVrpLnsRunner implements PlsRunner {
	
	private static final Logger LOG = Logger.getLogger(VrpLnsRunner.class);
	
	private VrpSolution sol;
	
	@Override
	public PlsSolution[] run(PlsSolution plsSol, long timeToFinish, Random rand) {
		VrpPlsSolution solAndStuff = (VrpPlsSolution)plsSol;
		
		sol = solAndStuff.getSolution();
		VrpProblem problem = sol.getProblem();
		LnsRelaxer relaxer = new LnsRelaxer(solAndStuff.getRelaxationRandomness(), problem.getMaxDistance(), rand);
		
		outer:
		while (true) {
			for (int n = solAndStuff.getCurEscalation(); n <= solAndStuff.getMaxEscalation(); n++) { 
				for (int i = solAndStuff.getCurIteration(); i < solAndStuff.getMaxIterations(); i++) {
					if (System.currentTimeMillis() >= timeToFinish) {
						break outer;
					}
					
					VrpSearcher solver = new VrpSearcher(problem);
					VrpCpStats stats = new VrpCpStats();
					
					VrpSolution partialSol = relaxer.relaxShaw(sol, n, -1);
					
					VrpSolution newSol = solver.solve(partialSol, sol.getToursCost(), solAndStuff.getMaxDiscrepancies(), stats, true);
					if (newSol != null && Math.abs(newSol.getToursCost() - sol.getToursCost()) > .001) {
						sol = newSol;
						solAndStuff.setSolution(sol);
						i = 0;
					}
					solAndStuff.setCurEscalation(n);
					solAndStuff.setCurIteration(i);
				}
			}
			//LOG.info("Starting new search");
			solAndStuff.setCurEscalation(1);
			solAndStuff.setCurIteration(0);
		}
		
		return new PlsSolution[] {solAndStuff};
	}
}
