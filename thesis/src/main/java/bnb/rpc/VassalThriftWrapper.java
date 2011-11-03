package bnb.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import bnb.BnbNode;
import bnb.Problem;

public class VassalThriftWrapper implements ThriftVassal.Iface {
	private static final Logger LOG = Logger.getLogger(VassalThriftWrapper.class);
	
	private VassalPublic vassal;
	
	public VassalThriftWrapper(VassalPublic vassal) {
		this.vassal = vassal;
	}

	@Override
	public void updateBestSolCost(double bestCost, int jobid) throws TException {
		try {
			vassal.updateBestSolCost(bestCost, jobid);
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		}
	}

	@Override
	public void startJobTasks(List<ThriftData> nodesData, ThriftData problemData,
			double bestCost, int jobid)
			throws TException {
		try {
			List<BnbNode> nodes = new ArrayList<BnbNode>(nodesData.size());
			for (ThriftData nodeData : nodesData) {
				nodes.add((BnbNode)RpcUtil.fromThriftData(nodeData));
			}
			Problem problem = (Problem)RpcUtil.fromThriftData(problemData);
			vassal.startJobTasks(nodes, problem, bestCost, jobid);
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		} catch (ClassNotFoundException ex) {
			LOG.error("Node class not found");
			throw new TException(ex);
		}
	}

	@Override
	public List<ThriftData> stealWork(int jobid) throws TException {
		try {
			List<BnbNode> nodes = vassal.stealWork(jobid);
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

	@Override
	public int getNumSlots() throws TException {
		try {
			return vassal.getNumSlots();
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		}
	}
}
