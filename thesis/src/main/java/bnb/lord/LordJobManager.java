package bnb.lord;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.BnbNode;
import bnb.Problem;
import bnb.rpc.VassalPublic;

public class LordJobManager {
	private static final Logger LOG = Logger.getLogger(LordJobManager.class);
	
	private double minCost = Double.MAX_VALUE;
	private final int jobid;
	private final Problem problem;
	private final List<VassalProxy> vassalProxies;
	private final List<BnbNode> unevaluated;
	private int numCompletedVassals;
	
	public LordJobManager(int jobid, List<BnbNode> unevaluated, Problem problem, List<VassalProxy> vassalProxies) {
		this.jobid = jobid;
		this.unevaluated = unevaluated;
		this.problem = problem;
		this.vassalProxies = vassalProxies;
	}
	
	//TODO: this shouldn't be synchronized
	public synchronized List<BnbNode> askForWork(int vassalId) {
//		LOG.info("lord about to deal with request for work");
		if (unevaluated.size() > 0) {
			BnbNode node = unevaluated.remove(0);
			LOG.info("Sending back work to vassal " + vassalId + "; unevaluated.size()=" + unevaluated.size());
			return Arrays.asList(node);
		} else { // no work left
			numCompletedVassals++;
			LOG.info("No work to give to vassal " + vassalId + "; numCompletedVassals=" + numCompletedVassals);
			if (numCompletedVassals == vassalProxies.size()) { // we're done
				done();
			}
			return new LinkedList<BnbNode>();
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
	
	public void updateMinCost(double cost, VassalProxy source) {
		if (cost < minCost) {
			System.out.println("received better min cost: " + cost);
			this.minCost = Math.min(cost, minCost);
			for (VassalProxy vassalProxy : vassalProxies) {
				if (vassalProxy != source) {
					try {
						vassalProxy.updateBestSolCost(minCost, jobid);
					} catch (IOException ex) {
						LOG.info("Failed to send cost " + minCost + " to vassalProxy", ex);
					}
				}
			}
		}
	}
}
