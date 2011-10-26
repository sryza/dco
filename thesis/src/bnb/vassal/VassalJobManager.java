package bnb.vassal;

import bnb.Solution;
import bnb.lord.VassalProxy;

public class VassalJobManager implements Runnable {
	private static final int UPDATE_INTERVAL = 500;
	
	private volatile double minCost;
	private volatile Solution bestSolution;
	
	private volatile boolean update;
	private final LordProxy lordProxy;
	private final VassalProxy vassalProxy;
	private final int jobid;
	
	private final VassalNodePool nodePool;
	
	public VassalJobManager(double initCost, VassalNodePool nodePool, LordProxy lordProxy, VassalProxy vassalProxy, int jobid) {
		minCost = initCost;
		this.lordProxy = lordProxy;
		this.vassalProxy = vassalProxy;
		this.jobid = jobid;
		this.nodePool = nodePool;
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(UPDATE_INTERVAL);
				if (update) {
					update = false;
					sendMinCost();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace(); //TODO: log4j
			}
		}
	}
	
	public VassalNodePool getNodePool() {
		return nodePool;
	}
	
	private void sendMinCost() {
		lordProxy.sendBestSolCost(minCost, jobid, vassalProxy);
	}
	
	public synchronized void betterLocalSolution(Solution sol, double cost) {
		if (cost < minCost) {
			bestSolution = sol;
			minCost = cost;
			update = true;
		}
	}
	
	/**
	 * Called when we receive a remote communication telling us
	 * of a new best solution.
	 */
	public synchronized void updateGlobalMinCost(double cost) {
		if (cost < minCost) {
			minCost = cost;
			bestSolution = null;
			update = false;
		}
	}
	
	public double getMinCost() {
		return minCost;
	}
	
	public Solution getBestSolution() {
		return bestSolution;
	}
}
