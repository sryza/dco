package bnb.rpc;

import java.io.IOException;

import bnb.BnbNode;

public interface LordPublic {
	public void sendBestSolCost(double cost, int jobid, int vassalId) throws IOException;
	
	public BnbNode askForWork();
}
