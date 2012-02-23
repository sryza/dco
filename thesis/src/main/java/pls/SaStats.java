package pls;

import org.apache.log4j.Logger;

public class SaStats {
	private static final Logger LOG = Logger.getLogger(SaStats.class);
	
	public void reportImproving() {
		
	}
	
	public void reportNewBestSolution(int cost) {
		LOG.info("Found new best solution with cost " + cost);
	}
	
	public void reportNoSolutionFoundInTime() {
		LOG.info("No solution found in time");
	}
}
