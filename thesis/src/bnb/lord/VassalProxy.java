package bnb.lord;

import java.util.List;

import bnb.ProblemSpec;
import bnb.TreeNode;

public interface VassalProxy {
	public void updateBestSolCost(double bestCost, int jobid);
	
	public void startJobTasks(List<TreeNode> nodes, ProblemSpec spec, double bestCost, int jobid);
}
