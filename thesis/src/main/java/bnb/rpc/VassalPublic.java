package bnb.rpc;

import java.io.IOException;
import java.util.List;

import bnb.BnbNode;
import bnb.Problem;

public interface VassalPublic {
	public void updateBestSolCost(double bestCost, int jobid) throws IOException;
	
	public void startJobTasks(List<BnbNode> nodes, Problem spec, double bestCost, int jobid, int nThreads) throws IOException;
	
	public int getNumSlots() throws IOException;
	
    public List<BnbNode> stealWork(int jobid) throws IOException;
    
    public int getId() throws IOException;
}
