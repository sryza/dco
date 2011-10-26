package bnb.vassal;

import bnb.ProblemSpec;
import bnb.TreeNode;

public class TaskRunner implements Runnable {
	private final LordProxy lordInfo;
	private final ProblemSpec problemSpec;
	private final VassalJobManager jobManager;
	
	public TaskRunner(LordProxy lordInfo, ProblemSpec spec, VassalJobManager jobManager) {
		this.lordInfo = lordInfo;
		this.problemSpec = spec;
		this.jobManager = jobManager;
	}
	
	public void run() {
		while (true) {
			TreeNode node = jobManager.getNodePool().nextNode();
			if (node == null) {
				if (!stealWork()) {
					break;
				}
			} else {
				node.evaluate(problemSpec, jobManager.getMinCost());
				if (node.isSolution() && node.getCost() < jobManager.getMinCost()) {
					System.out.println(node.getCost());
					System.out.println(node.getSolution());
					jobManager.betterLocalSolution(node.getSolution(), node.getCost());
				}
				jobManager.getNodePool().postEvaluated(node);
			}
		}
	}
	
	private boolean stealWork() {
		TreeNode work = lordInfo.askForWork();
		if (work == null) {
			System.out.println("Out of work at " + System.currentTimeMillis());
			return false;
		} else {
			jobManager.getNodePool().postEvaluated(work);
			return true;
		}
	}
}
