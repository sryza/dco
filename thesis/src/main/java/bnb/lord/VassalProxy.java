package bnb.lord;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import bnb.BnbNode;
import bnb.Problem;
import bnb.rpc.RpcUtil;
import bnb.rpc.ThriftData;
import bnb.rpc.ThriftVassal;

public class VassalProxy {

	private ThriftVassal.Client vassalClient;
	private TSocket socket;
	private int numSlotsCache = -1;
	
	public VassalProxy(String host, int port) {
		socket = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(socket);
		vassalClient = new ThriftVassal.Client(protocol);
	}
	
	public int getNumSlots() throws IOException {
		if (numSlotsCache == -1) {
			try {
				if (!socket.isOpen()) {
					socket.open();
				}
				numSlotsCache = vassalClient.getNumSlots();
			} catch (TException ex) {
				throw new IOException("send exception", ex);
			}
		}
		return numSlotsCache;
	}
	
	public void updateBestSolCost(double bestCost, int jobid)
		throws IOException {
		try {
			if (!socket.isOpen()) {
				socket.open();
			}
			vassalClient.updateBestSolCost(bestCost, jobid);
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		}
	}

	public void startJobTasks(List<BnbNode> nodes, Problem spec, double bestCost, int jobid, int nthreads)
		throws IOException {
		try {
			if (!socket.isOpen()) {
				socket.open();
			}
			List<ThriftData> nodesData = new ArrayList<ThriftData>(nodes.size());
			for (BnbNode node : nodes) {
				nodesData.add(RpcUtil.toThriftData(node));
			}
			ThriftData problemData = RpcUtil.toThriftData(spec);
			vassalClient.startJobTasks(nodesData, problemData, bestCost, jobid, nthreads);
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		}
	}

	public List<BnbNode> stealWork(LordJobManager jobManager) throws IOException {
		try {
			if (!socket.isOpen()) {
				socket.open();
			}
			List<ThriftData> nodesData = vassalClient.stealWork(jobManager.getJobID());
			List<BnbNode> nodes = new ArrayList<BnbNode>();
			for (ThriftData nodeData : nodesData) {
				nodes.add((BnbNode)RpcUtil.nodeFromThriftData(nodeData, jobManager.getProblem()));
			}
			return nodes;
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		} catch (ClassNotFoundException ex) {
			throw new IOException("class not found", ex);
		} catch (InstantiationException e) {
			throw new IOException("trouble instantiating", e);
		} catch (IllegalAccessException e) {
			throw new IOException("illegal access what?", e);
		} catch (IllegalArgumentException e) {
			throw new IOException("", e);
		} catch (InvocationTargetException e) {
			throw new IOException("", e);
		} catch (NoSuchMethodException e) {
			throw new IOException("", e);
		} catch (SecurityException e) {
			throw new IOException("", e);
		}
	}
}
