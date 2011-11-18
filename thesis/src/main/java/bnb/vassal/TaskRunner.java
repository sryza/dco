package bnb.vassal;

import org.apache.log4j.Logger;

import bnb.BnbNode;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private final LordProxy lordInfo;
	private final VassalJobManager jobManager;
	
	private int nEvaluated;
	
	private volatile boolean working = true;
	
	public TaskRunner(LordProxy lordInfo, VassalJobManager jobManager) {
		this.lordInfo = lordInfo;
		this.jobManager = jobManager;
	}
	
	public void run() {
		LOG.info("running task");
		while (true) {
			BnbNode node = jobManager.getNodePool().nextNode();
			if (node == null) {
				if (!stealWork()) {
					break;
				}
			} else {
				node.evaluate(jobManager.getMinCost());
				nEvaluated++;
				if (node.isSolution()) {
					if (node.getCost() < jobManager.getMinCost()) {
						LOG.info("new best cost: " + node.getCost());
						LOG.info("new best solution: " + node.getSolution());
						jobManager.betterLocalSolution(node.getSolution(), node.getCost());
					}
				}
				if (!node.isLeaf()) {
					jobManager.getNodePool().postEvaluated(node);
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
	
	public boolean working() {
		return working;
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
