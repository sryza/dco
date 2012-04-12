package pls.vrp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import pls.PlsSolution;
import pls.map.PlsRunner;
import pls.vrp.hm.VrpCpStats;
import pls.vrp.hm.VrpSearcher;

public class VrpLnsRunner implements PlsRunner {
	
	private static final Logger LOG = Logger.getLogger(VrpLnsRunner.class);
	
	private VrpSolvingExtraData extraData = new VrpSolvingExtraData();
	private VrpSolvingExtraData helperData;
	
	@Override
	public PlsSolution[] run(PlsSolution plsSol, long timeToFinish, Random rand) {
		VrpPlsSolution solAndStuff = (VrpPlsSolution)plsSol;
		
		long startTime = System.currentTimeMillis();
		
		VrpSolution sol = solAndStuff.getSolution();
		VrpProblem problem = sol.getProblem();
		LnsRelaxer relaxer = new LnsRelaxer(solAndStuff.getRelaxationRandomness(), problem.getMaxDistance(), rand);
		VrpSearcher solver = new VrpSearcher(problem);

		//if we've been sent neighborhoods to check, check them first
		if (helperData != null) {
			LOG.info("Has helper neighborhoods!");
			List<List<Integer>> neighborhoods = helperData.getNeighborhoods();
			int numSuccessful = 0;
			long helperStartTime = System.currentTimeMillis();
			for (List<Integer> neighborhood : neighborhoods) {
				VrpCpStats stats = new VrpCpStats();
				VrpSolution partialSol = new VrpSolution(relaxer.buildRoutesWithoutCusts(sol.getRoutes(), neighborhood), neighborhood, problem);
				VrpSolution newSol = solver.solve(partialSol, sol.getToursCost(), solAndStuff.getMaxDiscrepancies(), stats, true);
				if (newSol != null && Math.abs(newSol.getToursCost() - sol.getToursCost()) > .001) {
					extraData.addNeighborhood(partialSol);
					sol = newSol;
					solAndStuff.setSolution(sol);
					numSuccessful++;
				}
			}
			long helperFinishTime = System.currentTimeMillis();
			LOG.info("Helper neighborhoods: " + numSuccessful + " successful / " + neighborhoods.size() + 
					". Took " + (helperFinishTime - helperStartTime) + " ms");
		}
		
		outer:
		while (true) {
			for (int n = solAndStuff.getCurEscalation(); n <= solAndStuff.getMaxEscalation(); n++) { 
				for (int i = solAndStuff.getCurIteration(); i < solAndStuff.getMaxIterations(); i++) {
					if (System.currentTimeMillis() >= timeToFinish) {
						break outer;
					}
					
					VrpCpStats stats = new VrpCpStats();
					VrpSolution partialSol = relaxer.relaxShaw(sol, n, -1);
					
					VrpSolution newSol = solver.solve(partialSol, sol.getToursCost(), solAndStuff.getMaxDiscrepancies(), stats, true);
					if (newSol != null && Math.abs(newSol.getToursCost() - sol.getToursCost()) > .001) {
						extraData.addNeighborhood(partialSol);
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
		
		long endTime = System.currentTimeMillis();
		LOG.info("VrpLnsRunner took " + (endTime - startTime) + " ms");
		
		return new PlsSolution[] {solAndStuff};
	}

	@Override
	public Writable getExtraData() {
		return extraData;
	}

	@Override
	public void setHelperData(Writable helperData) {
		this.helperData = (VrpSolvingExtraData)helperData;
	}
}
