package bnb.lord;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.Problem;

public class LordJobManager {
	private static final Logger LOG = Logger.getLogger(LordJobManager.class);
	
	private double minCost = Double.MAX_VALUE;
	private final int jobid;
	private final Problem problem;
	private final List<VassalProxy> vassalProxies;
	private final List<BnbNode> unevaluated;
	
	//for who to steal work from policy
	private ConcurrentHashMap<Integer, Boolean> hasWorkMap;
	private LinkedBlockingQueue<VassalProxy> nextVassalQueue;
	
	public LordJobManager(int jobid, List<BnbNode> unevaluated, Problem problem, List<VassalProxy> vassalProxies) {
		this.jobid = jobid;
		this.unevaluated = unevaluated;
		this.problem = problem;
		this.vassalProxies = vassalProxies;
		
		hasWorkMap = new ConcurrentHashMap<Integer, Boolean>();
		nextVassalQueue = new LinkedBlockingQueue<VassalProxy>();
		for (VassalProxy proxy : vassalProxies) {
			hasWorkMap.put(proxy.getVassalIdCache(), true);
			nextVassalQueue.add(proxy);
		}
	}
	
	public List<BnbNode> askForWork(int vassalId) {
//		System
		hasWorkMap.remove(vassalId);
//		System.out.println(b);
//		System.out.println("hasWorkMap size after removal of " + vassalId + ": " + hasWorMap.size());
		
//		LOG.info("lord about to deal with request for work");
		//if we have nodes here, return one of them
		synchronized(this) {
			if (unevaluated.size() > 0) {
				BnbNode node = unevaluated.remove(0);
				hasWorkMap.put(vassalId, true);
				LOG.info("Sending back work to vassal " + vassalId + "; unevaluated.size()=" + unevaluated.size());
				return Arrays.asList(node);
			}
		}
		
		while (true) {
			//check every time because it could've filled up since we started
			if (hasWorkMap.size() == 0) {
				done();
				return new LinkedList<BnbNode>();
			}
			
			//TODO: what if we get back to the beginning?
			//TODO: is remove blocking? we want it to be
			VassalProxy proxy = nextVassalQueue.remove();
			LOG.info("contacting proxy " + proxy.getVassalIdCache() + " for work");
			nextVassalQueue.add(proxy);
			if (hasWorkMap.containsKey(proxy.getVassalIdCache())) {
				try {
					List<BnbNode> stolenWork = proxy.stealWork(this);
					if (stolenWork.size() > 0) {
						//TODO: synchronize this?
						hasWorkMap.put(vassalId, true); //TODO: defer this until after we've sent our response?
						return stolenWork;
					} else {
						LOG.warn("vassal " + proxy.getVassalIdCache() + " that allegedly had work actually doesn't");
						//there's a specific race condition in which this can happen:
						//two threads going to the same one?
					}
				} catch (IOException ex) {
					LOG.error("problem stealing work from vassal " + proxy.getVassalIdCache(), ex);
				}
			} else {
				LOG.info("skipping vassal " + proxy.getVassalIdCache() + " because it does not have work");
			}
		}
	}
	
	private void done() {
		LOG.info("Computation completed!");
		LOG.info("Best cost: " + minCost);
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	public int getJobID() {
		return jobid;
	}
	
	//TODO: need this synchronization?
	public synchronized void updateMinCost(double cost, VassalProxy source) {
		if (cost < minCost) {
			LOG.info("lord received better min cost: " + cost);
			this.minCost = Math.min(cost, minCost);
			for (VassalProxy vassalProxy : vassalProxies) {
				if (vassalProxy != source) {
					try {
						vassalProxy.updateBestSolCost(minCost, jobid);
						LOG.debug("Successfully sent best cost " + minCost + " to " + vassalProxy.getVassalId());
					} catch (IOException ex) {
						LOG.warn("Failed to send cost " + minCost + " to vassalProxy", ex);
					}
				}
			}
		}
	}
}
