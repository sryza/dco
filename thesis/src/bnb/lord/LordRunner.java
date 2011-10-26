package bnb.lord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bnb.ProblemSpec;
import bnb.TreeNode;
import bnb.tsp.TspTreeNode;
import bnb.vassal.LordProxy;
import bnb.vassal.SimpleVassalNodePool;
import bnb.vassal.VassalNodePool;
import bnb.vassal.VassalServer;

public class LordRunner implements Runnable, LordProxy {
	
	private int nextJobid;
	private final Map<Integer, LordJobManager> jobMap;
	
	public LordRunner() {
		jobMap = new HashMap<Integer, LordJobManager>();
	}
	
	public void run() {
		
	}
	
	public void runJob(TreeNode root, ProblemSpec spec, double bestCost, List<VassalServer> vassalServers) {
		int jobid = nextJobid++;
		
		int totalSlots = 0;
		for (VassalServer runner : vassalServers) {
			totalSlots += runner.numSlots();
		}
		//TODO: what happens if slots free up during this?
		Starter starter = new Starter();
		List<TreeNode>[] startNodes = starter.startEvaluation(spec, bestCost, root, totalSlots);
		LordJobManager jobManager = new LordJobManager(jobid, startNodes[1]);
		jobMap.put(jobid, jobManager);
		Iterator<TreeNode> startNodesIter = startNodes[0].iterator();
		for (VassalServer server : vassalServers) {
			List<TreeNode> nodePool = new LinkedList<TreeNode>();
			TreeNode node = startNodesIter.next();
			((TspTreeNode)node).copyCities();
			nodePool.add(node);
			server.startJobTasks(nodePool, spec, bestCost, jobid);
		}
	}

	@Override
	public void sendBestSolCost(double cost, int jobid, VassalProxy source) {
		jobMap.get(jobid).updateMinCost(cost, source);
	}

	@Override
	public TreeNode askForWork() {
		// TODO Auto-generated method stub
		return null;
	}
}
