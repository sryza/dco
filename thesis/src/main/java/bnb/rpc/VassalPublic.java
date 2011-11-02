package bnb.rpc;

import java.io.IOException;
import java.util.List;

import bnb.ProblemSpec;
import bnb.BnbNode;

public interface VassalPublic {
	public void updateBestSolCost(double bestCost, int jobid) throws IOException;
	
	public void startJobTasks(List<BnbNode> nodes, ProblemSpec spec, double bestCost, int jobid) throws IOException;
	
	public int getNumSlots() throws IOException;
}
