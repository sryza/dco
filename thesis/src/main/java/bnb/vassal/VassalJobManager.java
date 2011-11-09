package bnb.vassal;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.Problem;
import bnb.Solution;
import bnb.rpc.LordPublic;

public class VassalJobManager implements Runnable {
	private static final Logger LOG = Logger.getLogger(VassalJobManager.class);
	
	private static final int UPDATE_INTERVAL = 500;
	
	private volatile double minCost;
	private volatile Solution bestSolution;
	
	private volatile boolean update;
	private final LordProxy lordProxy;
	private final int jobid;
	private final int vassalid;
	private final Problem problem;
	
	private final VassalNodePool nodePool;
	
	public VassalJobManager(double initCost, VassalNodePool nodePool, 
			Problem problem, LordProxy lordProxy, int vassalid, int jobid) {
		minCost = initCost;
		this.lordProxy = lordProxy;
		this.jobid = jobid;
		this.vassalid = vassalid;
		this.nodePool = nodePool;
		this.problem = problem;
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(UPDATE_INTERVAL);
				if (update) {
					update = !sendMinCost();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace(); //TODO: log4j
			}
		}
	}
	
	public VassalNodePool getNodePool() {
		return nodePool;
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	public int getJobID() {
		return jobid;
	}
	
	private boolean sendMinCost() {
		try {
			lordProxy.sendBestSolCost(minCost, jobid, vassalid);
			return true;
		} catch (IOException ex) {
			LOG.error("Couldn't reach lord to report cost");
			return false;
		}
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
	
	public List<BnbNode> stealWork() {
		return nodePool.stealNodes();
	}
}
