package bnb.vassal;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.stats.VassalJobStats;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private final VassalJobManager jobManager;
	private final VassalJobStats stats;
	
	private int numEvaluated;
	
	private volatile boolean working = true;
	
	public TaskRunner(VassalJobManager jobManager, VassalJobStats stats) {
		this.jobManager = jobManager;
		this.stats = stats;
	}
	
	public void run() {
		LOG.info("running task");
		stats.reportWorking();
		while (true) {
			stats.reportNextNodeStart();
			BnbNode node = jobManager.getNodePool().nextNode();
			stats.reportNextNodeEnd();
			if (node == null) {
				stats.reportNotWorking();
				stats.reportAskForWorkStart();
				boolean workStolen = stealWork();
				stats.reportAskForWorkEnd();
				if (!workStolen) {
					break;
				}
			} else {
				node.evaluate(jobManager.getMinCost());
				numEvaluated++;
				if (node.isSolution()) {
					if (node.getCost() < jobManager.getMinCost()) {
						LOG.info("new best cost: " + node.getCost());
						LOG.info("new best solution: " + node.getSolution());
						jobManager.betterLocalSolution(node.getSolution(), node.getCost());
						//TODO: mark as done
						
						node.whenAllChildrenDone();
						node.getParent().childDone();
					}
				} else {
					if (!node.isLeaf()) {
						jobManager.getNodePool().post(node);
					} else {
						//if we're not posting the node to do work with, let its parent
						//know that we're done doing computation on it
						node.whenAllChildrenDone();
						if (node.getParent() != null) {
							node.getParent().childDone();
						}
					}
				}
			}
		}
		stats.reportDone();
//		LOG.info("numEvaluated: " + numEvaluated);
	}
	
	public boolean working() {
		return working;
	}
	
	public int getNumEvaluated() {
		return numEvaluated;
	}
	
	public void setWorking() {
		stats.reportWorking();
		working  = true;
	}
	
	/**
	 * @return
	 * 		true if computation should continue
	 */
	private boolean stealWork() {
		working = false;
		boolean succ = jobManager.askForWork(this);
		return !jobManager.isCompleted();
	}
}
