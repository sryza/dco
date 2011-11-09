package bnb.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
			double bestCost, int jobid, int nthreads)
			throws TException {
		try {
			Problem problem = (Problem)RpcUtil.problemFromThriftData(problemData);
			List<BnbNode> nodes = new ArrayList<BnbNode>(nodesData.size());
			for (ThriftData nodeData : nodesData) {
				nodes.add((BnbNode)RpcUtil.nodeFromThriftData(nodeData, problem));
			}
			vassal.startJobTasks(nodes, problem, bestCost, jobid, nthreads);
		} catch (IOException ex) {
			LOG.error("IOException where we shouldn't really have one", ex);
			throw new TException(ex);
		} catch (ClassNotFoundException ex) {
			LOG.error("Node class not found");
			throw new TException(ex);
		} catch (InstantiationException e) {
			throw new TException("trouble instantiating", e);
		} catch (IllegalAccessException e) {
			throw new TException("illegal access what?", e);
		} catch (IllegalArgumentException e) {
			throw new TException("", e);
		} catch (InvocationTargetException e) {
			throw new TException("", e);
		} catch (NoSuchMethodException e) {
			throw new TException("", e);
		} catch (SecurityException e) {
			throw new TException("", e);
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
