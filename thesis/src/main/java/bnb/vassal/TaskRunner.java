package bnb.vassal;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.Problem;
import bnb.BnbNode;
import bnb.rpc.LordPublic;

public class TaskRunner implements Runnable {
	private static final Logger LOG = Logger.getLogger(TaskRunner.class);
	
	private final LordPublic lordInfo;
	private final VassalJobManager jobManager;
	
	public TaskRunner(LordPublic lordInfo, VassalJobManager jobManager) {
		this.lordInfo = lordInfo;
		this.jobManager = jobManager;
	}
	
	public void run() {
		while (true) {
			BnbNode node = jobManager.getNodePool().nextNode();
			if (node == null) {
				if (!stealWork()) {
					break;
				}
			} else {
				node.evaluate(jobManager.getMinCost());
				if (node.isSolution() && node.getCost() < jobManager.getMinCost()) {
					System.out.println(node.getCost());
					System.out.println(node.getSolution());
					jobManager.betterLocalSolution(node.getSolution(), node.getCost());
				}
				jobManager.getNodePool().postEvaluated(node);
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
			work = lordInfo.askForWork(jobManager.getJobID());
		} catch (IOException ex) {
			LOG.error("Couldn't steal work", ex);
			return false;
		}
		if (work == null) {
			System.out.println("Out of work at " + System.currentTimeMillis());
			return false;
		} else {
			for (BnbNode node : work) {
				jobManager.getNodePool().postEvaluated(node);
			}
			return true;
		}
	}
}
