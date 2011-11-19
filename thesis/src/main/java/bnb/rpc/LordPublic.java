package bnb.rpc;

import java.io.IOException;
import java.util.List;

import bnb.BnbNode;

public interface LordPublic {
	public void sendBestSolCost(double cost, int jobid, int vassalId) throws IOException;
	
	public List<BnbNode> askForWork(int jobid, int vassalid) throws IOException;
}
