package bnb.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.thrift.TException;

import bnb.BnbNode;
import bnb.lord.LordRunner;

/**
 * Wrapper between interface that thrift expects to see and LordRunner functionality
 */
public class LordThriftWrapper implements ThriftLord.Iface {
	
	private final LordRunner lord;
	
	public LordThriftWrapper(LordRunner lord) {
		this.lord = lord;
	}

	@Override
	public void sendBestSolCost(double bestCost, int jobid, int vassalid)
			throws TException {
		lord.sendBestSolCost(bestCost, jobid, vassalid);
	}

	@Override
	public List<ThriftNodeData> askForWork(int jobid) throws TException {
		List<BnbNode> nodes = lord.askForWork(jobid);
		List<ThriftNodeData> nodesData = new ArrayList<ThriftNodeData>(nodes.size());

		for (BnbNode node : nodes) {
			nodesData.add(RpcUtil.toThriftNode(node));
		}
		
		return nodesData;
	}
}
