package bnb.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import bnb.BnbNode;

/**
 * Wrapper between interface that thrift expects to see and LordRunner functionality
 */
public class LordThriftWrapper implements ThriftLord.Iface {
	
	private static final Logger LOG = Logger.getLogger(LordThriftWrapper.class);
	
	private final LordPublic lord;
	
	public LordThriftWrapper(LordPublic lord) {
		this.lord = lord;
	}

	@Override
	public void sendBestSolCost(double bestCost, int jobid, int vassalid)
			throws TException {
		try {
			lord.sendBestSolCost(bestCost, jobid, vassalid);
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		}
	}

	@Override
	public List<ThriftData> askForWork(int jobid) throws TException {
		try {
			List<BnbNode> nodes = lord.askForWork(jobid);
			List<ThriftData> nodesData = new ArrayList<ThriftData>(nodes.size());
			for (BnbNode node : nodes) {
				nodesData.add(RpcUtil.toThriftData(node));
			}
			return nodesData;
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		}
	}
}
