package bnb.vassal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bnb.ProblemSpec;
import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.VassalPublic;

public class VassalRunner implements Runnable, VassalPublic {

	private int numSlots;
	private final Map<Integer, VassalJobManager> jobMap;
	private final LordPublic lordInfo;
	private final int vassalId;
	
	public VassalRunner(LordPublic lordInfo, int numSlots) {
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
	public void startJobTasks(List<BnbNode> nodes, ProblemSpec spec, double bestCost, int jobid) {
		VassalNodePool nodePool = new SimpleVassalNodePool();
		for (BnbNode node : nodes) {
			nodePool.postEvaluated(node);
		}
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, lordInfo, vassalId, jobid);
		jobMap.put(jobid, jobManager);
		startVassalRunner(lordInfo, nodePool, spec, jobManager);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();
	}
	
	public void startVassalRunner(LordPublic lordInfo, VassalNodePool nodePool, ProblemSpec spec,
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
