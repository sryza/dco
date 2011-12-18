package bnb.lord;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.Problem;
import bnb.stats.LordJobStats;

public class LordJobManager {
	private static final Logger LOG = Logger.getLogger(LordJobManager.class);
	
	private static final int MAX_FAILED_STEAL_ATTEMPTS = 3;
	
	private double minCost = Double.MAX_VALUE;
	private final int jobid;
	private final Problem problem;
	private final List<VassalProxy> vassalProxies;
	private final List<BnbNode> unevaluated;
	
	private final LordJobStats stats;
	
	//for who to steal work from policy
	private final ConcurrentHashMap<Integer, Boolean> hasWorkMap;
	private final LinkedBlockingQueue<VassalProxy> nextVassalQueue;
	
	private boolean failed;
	
	public LordJobManager(int jobid, List<BnbNode> unevaluated, Problem problem, List<VassalProxy> vassalProxies, LordJobStats stats) {
		this.jobid = jobid;
		this.unevaluated = unevaluated;
		this.problem = problem;
		this.vassalProxies = vassalProxies;
		this.stats = stats;
		
		hasWorkMap = new ConcurrentHashMap<Integer, Boolean>();
		nextVassalQueue = new LinkedBlockingQueue<VassalProxy>();
		for (VassalProxy proxy : vassalProxies) {
			hasWorkMap.put(proxy.getVassalIdCache(), true);
			nextVassalQueue.add(proxy);
		}
	}
	
	public List<BnbNode> askForWork(int vassalId) {
		hasWorkMap.remove(vassalId);
		
		if (failed) {
			return new LinkedList<BnbNode>();
		}
		
//		LOG.info("lord about to deal with request for work");
		//if we have nodes here, return one of them
		synchronized(this) {
			if (unevaluated.size() > 0) {
				BnbNode node = unevaluated.remove(0);
				hasWorkMap.put(vassalId, true);
				LOG.info("Sending back work from unevaluated list to vassal " + vassalId + "; unevaluated.size()=" + unevaluated.size());
				return Arrays.asList(node);
			}
		}
		
		//store the number of times we fail to reach a particular proxy so we don't
		//continue doing this forever if one is down
		Map<VassalProxy, Integer> failedAttempts = new HashMap<VassalProxy, Integer>();
		
		while (true) {
			//check every time because it could've filled up since we started
			if (hasWorkMap.size() == 0) {
				done();
				return new LinkedList<BnbNode>();
			}
			
			//TODO: what if we get back to the beginning?
			//TODO: is remove blocking? we want it to be
			VassalProxy proxy = nextVassalQueue.remove();
			Integer numFailedAttempts = failedAttempts.get(proxy);
			if (numFailedAttempts != null && numFailedAttempts >= MAX_FAILED_STEAL_ATTEMPTS) {
				LOG.info("Failed to contact vassal " + proxy.getVassalIdCache() + " " + MAX_FAILED_STEAL_ATTEMPTS + " times, aborting job");
				failed = true;
				return new LinkedList<BnbNode>();
			}
			
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
					if (failedAttempts.containsKey(proxy)) {
						failedAttempts.put(proxy, failedAttempts.get(proxy)+1);
					} else {
						failedAttempts.put(proxy, 1);
					}
				}
			} else {
				LOG.info("skipping vassal " + proxy.getVassalIdCache() + " because it does not have work");
			}
		}
	}
	
	//WARNING: I've noticed this called more than once
	private void done() {
		stats.finished();
		LOG.info("Computation completed!");
		LOG.info("Best cost: " + minCost);
		LOG.info("Stats: \n" + stats.makeReport());
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
			LOG.info("lord received better min cost from vassal " + source.getVassalIdCache() + ": " + cost);
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
