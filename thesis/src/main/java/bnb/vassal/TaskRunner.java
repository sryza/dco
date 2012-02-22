package bnb.vassal;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.stats.VassalJobStats;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private static final int EVALUATED_LOG_INTERVAL= 1000;
	
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
		try {
			stats.reportWorking();
			while (true) {
				BnbNode node = jobManager.getNodePool().nextNode();
				if (node == null) {
					if (working) {
						stats.reportNotWorking();
						working = false;
					}
					
					stats.reportAskForWorkStart();
					boolean succeeded = stealWork();
					stats.reportAskForWorkEnd();
					if (jobManager.isCompleted()) {
						break;
					}
				} else {
					if (!working) {
						stats.reportWorking();
						working = true;
					}
					
					node.evaluate(jobManager.getMinCost());
					numEvaluated++;
//					if (numEvaluated % EVALUATED_LOG_INTERVAL == 0) {
//						LOG.info("evaluated " + numEvaluated + " nodes");
//					}
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
		} catch (Exception ex) {
			LOG.error("Error inside task running function", ex);
		}

//		LOG.info("numEvaluated: " + numEvaluated);
	}
	
	public boolean working() {
		return working;
	}
	
	public int getNumEvaluated() {
		return numEvaluated;
	}
	
	/**
	 * @return
	 * 		true if work was stolen
	 */
	private boolean stealWork() {
		boolean succ = jobManager.askForWork(this);
		return succ;
	}
}
