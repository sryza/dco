package bnb.lord;

import java.io.IOException;
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
	private final List<VassalPublic> vassalProxies;
	private final List<BnbNode> unevaluated;
	
	public LordJobManager(int jobid, List<BnbNode> unevaluated, Problem problem) {
		this.jobid = jobid;
		this.unevaluated = unevaluated;
		this.problem = problem;
		System.out.println("unevaluated size: " + unevaluated.size());
		vassalProxies = new LinkedList<VassalPublic>();
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
			for (VassalPublic vassalProxy : vassalProxies) {
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
