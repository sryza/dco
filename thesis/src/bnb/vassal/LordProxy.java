package bnb.vassal;

import bnb.TreeNode;
import bnb.lord.VassalProxy;

public interface LordProxy {
	public void sendBestSolCost(double cost, int jobid, VassalProxy source);
	
	public TreeNode askForWork();
}
