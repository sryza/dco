package bnb.vassal;

import java.util.concurrent.CyclicBarrier;

import org.apache.log4j.Logger;

import bnb.BnbNode;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private final VassalJobManager jobManager;
	private final VassalStats stats;
	private final CyclicBarrier completeBarrier;
	
	private int numEvaluated;
	
	private volatile boolean working = true;
	
	public TaskRunner(VassalJobManager jobManager, VassalStats stats, CyclicBarrier barrier) {
		this.jobManager = jobManager;
		this.stats = stats;
		this.completeBarrier = barrier;
	}
	
	public void run() {
		LOG.info("running task");
		while (true) {
			stats.reportNextNodeStart();
			BnbNode node = jobManager.getNodePool().nextNode();
			stats.reportNextNodeEnd();
			if (node == null) {
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
		try {
			if (completeBarrier.await() == 0) {
				stats.report();
			}
		} catch (Exception ex) {
			LOG.warn("complete barrier problem, could not report stats", ex);
		}
		
//		LOG.info("numEvaluated: " + numEvaluated);
	}
	
	public boolean working() {
		return working;
	}
	
	public int getNumEvaluated() {
		return numEvaluated;
	}
	
	public void setWorking() {
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
