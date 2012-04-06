package pls.vrp.hm;

import org.apache.log4j.Logger;

public class VrpCpStats {
	
	private static final Logger LOG = Logger.getLogger(VrpCpStats.class);
	
	private int nEvaluated;
	private long insertTimeStart;
	private long maxInsertTime;
	private boolean quiet;
	
	public VrpCpStats(boolean quiet) {
		this.quiet = quiet;
	}
	
	public VrpCpStats() {
		quiet = true;
	}
	
	public void reportNodeEvaluated() {
		nEvaluated++;
	}
	
	public int getNumNodesEvaluated() {
		return nEvaluated;
	}
	
	public void reportAboutToInsert() {
		insertTimeStart = System.currentTimeMillis();
	}
	
	public void reportFinishedInsertion() {
		long time = System.currentTimeMillis() - insertTimeStart;
		if (time > maxInsertTime) {
			maxInsertTime = time;
		}
	}
	
	public void reportNewBestSolution(double cost) {
		if (!quiet) {
			LOG.info("found solution with cost " + cost);
		}
	}
	
	public long getMaxInsertTime() {
		return maxInsertTime;
	}
}
