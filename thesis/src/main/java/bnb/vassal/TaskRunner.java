package bnb.vassal;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.BnbNode;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private final LordProxy lordInfo;
	private final VassalJobManager jobManager;
	
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
	
	/**
	 * @return
	 * 		true if the operation succeeded
	 */
	private boolean stealWork() {
		List<BnbNode> work;
		try {
			work = lordInfo.askForWork(jobManager);
		} catch (IOException ex) {
			LOG.error("Couldn't steal work", ex);
			return false;
		}
		if (work.isEmpty()) {
			LOG.info("Out of work at " + new Date());
			return false;
		} else {
			for (BnbNode node : work) {
				jobManager.getNodePool().postEvaluated(node);
			}
			return true;
		}
	}
}
