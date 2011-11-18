package bnb.vassal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
					//TODO: send min cost
//					update = !sendMinCost();
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
	public synchronized boolean askForWork(TaskRunner taskRunner) {
		for (TaskRunner runner : taskRunners) {
			if (runner.working()) {
				//work must've been fetched in between when this was called
				//and when it started executing
				//or let the next thread handle this
				taskRunner.setWorking();
				return true;
			}
		}
		
		LOG.info("about to ask lord for work");
		List<BnbNode> work;
		try {
			work = lordProxy.askForWork(this);
		} catch (IOException ex) {
			LOG.error("Couldn't steal work", ex);
			return false;
		}
		if (work.isEmpty()) {
			LOG.info("Out of work at " + new Date());
			isCompleted = true;
			return true;
		} else {
			for (BnbNode node : work) {
				nodePool.postEvaluated(node);
			}
			taskRunner.setWorking();
			return true;
		}
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
