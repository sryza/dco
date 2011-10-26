package bnb.lord;

import java.util.LinkedList;
import java.util.List;

import bnb.TreeNode;

public class LordJobManager {
	private double minCost = Double.MAX_VALUE;
	private final int jobid;
	private final List<VassalProxy> vassalProxies;
	private final List<TreeNode> unevaluated;
	
	public LordJobManager(int jobid, List<TreeNode> unevaluated) {
		this.jobid = jobid;
		this.unevaluated = unevaluated;
		System.out.println("unevaluated size: " + unevaluated.size());
		vassalProxies = new LinkedList<VassalProxy>();
	}
	
	public void updateMinCost(double cost, VassalProxy source) {
		if (cost < minCost) {
			System.out.println("received better min cost: " + cost);
			this.minCost = Math.min(cost, minCost);
			for (VassalProxy vassalProxy : vassalProxies) {
				if (vassalProxy != source) {
					vassalProxy.updateBestSolCost(minCost, jobid);
				}
			}
		}
	}
}
