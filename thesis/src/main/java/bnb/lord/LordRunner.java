package bnb.lord;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bnb.ProblemSpec;
import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.VassalPublic;
import bnb.tsp.TspTreeNode;
import bnb.vassal.VassalRunner;

public class LordRunner implements Runnable, LordPublic {
	
	private static final Logger LOG = Logger.getLogger(LordRunner.class);
	
	private int nextJobid;
	private final Map<Integer, LordJobManager> jobMap;
	private final Map<Integer, VassalPublic> vassalMap;
	
	public LordRunner() {
		jobMap = new HashMap<Integer, LordJobManager>();
		vassalMap = new HashMap<Integer, VassalPublic>();
	}
	
	public void run() {
		
	}
	
	public void runJob(BnbNode root, ProblemSpec spec, double bestCost, List<VassalRunner> vassalServers) {
		int jobid = nextJobid++;
		
		int totalSlots = 0;
		for (VassalRunner runner : vassalServers) {
			totalSlots += runner.numSlots();
		}
		//TODO: what happens if slots free up during this?
		Starter starter = new Starter();
		List<BnbNode>[] startNodes = starter.startEvaluation(spec, bestCost, root, totalSlots);
		LordJobManager jobManager = new LordJobManager(jobid, startNodes[1]);
		jobMap.put(jobid, jobManager);
		Iterator<BnbNode> startNodesIter = startNodes[0].iterator();
		for (VassalRunner server : vassalServers) {
			List<BnbNode> nodePool = new LinkedList<BnbNode>();
			BnbNode node = startNodesIter.next();
			((TspTreeNode)node).copyCities();
			nodePool.add(node);
			server.startJobTasks(nodePool, spec, bestCost, jobid);
		}
	}

	@Override
	public void sendBestSolCost(double cost, int jobid, int vassalid) {
		VassalPublic vassal = vassalMap.get(vassalid);
		if (vassal == null) {
			LOG.error("Lord couldn't locate vassal with id " + vassalid);
			return;
		}
		LordJobManager jobManager = jobMap.get(jobid);
		if (jobManager == null) {
			LOG.error("Lord couldn't local job with id " + jobid);
			return;
		}
		jobManager.updateMinCost(cost, vassal);
	}

	@Override
	public BnbNode askForWork() {
		// TODO Auto-generated method stub
		return null;
	}
}
