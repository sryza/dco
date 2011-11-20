package bnb.vassal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.Problem;
import bnb.Solution;

public class VassalJobManager implements Runnable {
	private static final Logger LOG = Logger.getLogger(VassalJobManager.class);
	
	private static final int UPDATE_INTERVAL = 5000;
	
	private volatile double minCost;
	private volatile Solution bestSolution;
	
	private volatile boolean update;
	private final LordProxy lordProxy;
	private final int jobid;
	private final int vassalid;
	private final Problem problem;
	
	private final VassalNodePool nodePool;
	
	private final List<TaskRunner> taskRunners;
	
	private volatile boolean isCompleted;
	
	//lock for sending messages to the lord
	private final Object sendLock = new Object();
	//lock for updating minCost and bestSolution
	private final Object bestLock = new Object();
	
	public VassalJobManager(double initCost, VassalNodePool nodePool, 
			Problem problem, LordProxy lordProxy, int vassalid, int jobid) {
		minCost = initCost;
		this.lordProxy = lordProxy;
		this.jobid = jobid;
		this.vassalid = vassalid;
		this.nodePool = nodePool;
		this.problem = problem;
		this.taskRunners = new ArrayList<TaskRunner>();
	}
	
	public void registerTaskRunner(TaskRunner taskRunner) {
		taskRunners.add(taskRunner);
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
	
	public int getVassalID() {
		return vassalid;
	}
	
	/**
	 * Returns true if no more computation will be done on the job on
	 * this computer.
	 */
	public boolean isCompleted() {
		return isCompleted;
	}
	
	/**
	 * Returns true if the operation succeeded.
	 * The caller should check isCompleted afterward to see whether
	 * there was any more work to be done.
	 */
	public boolean askForWork(TaskRunner taskRunner) {
		synchronized(sendLock) {
			if (isCompleted) {
				return true;
			}
			for (TaskRunner runner : taskRunners) {
				if (runner.working()) {
					//work must've been fetched in between when this was called
					//and when it started executing
					//or let the next thread handle this
					return true;
				}
			}
			
			LOG.info("about to ask lord for work");
			List<BnbNode> work;
			try {
				work = lordProxy.askForWork(this, minCost);
			} catch (IOException ex) {
				LOG.error("Couldn't steal work", ex);
				return false;
			}
			if (work.isEmpty()) {
				LOG.info("Out of work at " + new Date());
				isCompleted = true;
				done();
				return true;
			} else {
				LOG.debug("received work");
				for (BnbNode node : work) {
					nodePool.postEvaluated(node);
				}
				taskRunner.setWorking();
				return true;
			}
		}
	}
	
	/**
	 * Returns true on success.
	 */
	private boolean sendMinCost() {
		synchronized(sendLock) {
			try {
				LOG.info("Reporting new minCost " + minCost + " to lord");
				lordProxy.sendBestSolCost(minCost, jobid, vassalid);
				LOG.info("Completed reporting new minCost");
				return true;
			} catch (IOException ex) {
				LOG.error("Couldn't reach lord to report cost");
				return false;
			}
		}
	}
	
	public synchronized void betterLocalSolution(Solution sol, double cost) {
		synchronized(bestLock) {
			if (cost < minCost) {
				bestSolution = sol;
				minCost = cost;
				update = true;
			}
		}
	}
	
	/**
	 * Called when we receive a remote communication telling us
	 * of a new best solution.
	 */
	public void updateGlobalMinCost(double cost) {
		synchronized(bestLock) {
			if (cost < minCost) {
				minCost = cost;
				bestSolution = null;
				update = false;
			}
		}
	}
	
	private void done() {
		int numEvaluated = 0;
		for (TaskRunner runner : taskRunners) {
			numEvaluated += runner.getNumEvaluated();
		}
		LOG.info("Vassal " + vassalid + " evaluated " + numEvaluated + " nodes");
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
