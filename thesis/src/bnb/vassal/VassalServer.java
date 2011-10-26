package bnb.vassal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bnb.ProblemSpec;
import bnb.TreeNode;
import bnb.lord.VassalProxy;

public class VassalServer implements Runnable, VassalProxy {

	private int numSlots;
	private final Map<Integer, VassalJobManager> jobMap;
	private final LordProxy lordInfo;
	
	public VassalServer(LordProxy lordInfo, int numSlots) {
		this.numSlots = numSlots;
		jobMap = new HashMap<Integer, VassalJobManager>();
		this.lordInfo = lordInfo;
	}
	
	public void run() {
		
	}
	
	public int numSlots() {
		return numSlots;
	}
	
	@Override
	public void startJobTasks(List<TreeNode> nodes, ProblemSpec spec, double bestCost, int jobid) {
		VassalNodePool nodePool = new SimpleVassalNodePool();
		for (TreeNode node : nodes) {
			nodePool.postEvaluated(node);
		}
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, lordInfo, this, jobid);
		jobMap.put(jobid, jobManager);
		startVassalRunner(lordInfo, nodePool, spec, jobManager);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();
	}
	
	public void startVassalRunner(LordProxy lordInfo, VassalNodePool nodePool, ProblemSpec spec,
			VassalJobManager jobManager) {
		TaskRunner runner = new TaskRunner(lordInfo, spec, jobManager);
		Thread vassalThread = new Thread(runner);
		vassalThread.start();
		numSlots--;
	}

	@Override
	public void updateBestSolCost(double bestCost, int jobid) {
		jobMap.get(jobid).updateGlobalMinCost(bestCost);
	}
}
