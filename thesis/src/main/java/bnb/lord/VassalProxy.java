package bnb.lord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import bnb.BnbNode;
import bnb.ProblemSpec;
import bnb.rpc.RpcUtil;
import bnb.rpc.ThriftLord;
import bnb.rpc.ThriftNodeData;
import bnb.rpc.ThriftVassal;
import bnb.rpc.VassalPublic;

public class VassalProxy implements VassalPublic {

	private ThriftVassal.Client vassalClient;
	private int numSlotsCache = -1;
	
	public VassalProxy(String host, int port) {
		TSocket socket = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(socket);
		vassalClient = new ThriftVassal.Client(protocol);
	}
	
	public int getNumSlots() throws IOException {
		if (numSlotsCache == -1) {
			try {
				vassalClient.getNumSlots();
			} catch (TException ex) {
				throw new IOException("send exception", ex);
			}
		}
		return numSlotsCache;
	}
	
	@Override
	public void updateBestSolCost(double bestCost, int jobid)
			throws IOException {
		if (numSlotsCache == -1) {
			try {
				vassalClient.updateBestSolCost(bestCost, jobid);
			} catch (TException ex) {
				throw new IOException("send exception", ex);
			}
		}
	}

	@Override
	public void startJobTasks(List<BnbNode> nodes, ProblemSpec spec,
			double bestCost, int jobid) {
		if (numSlotsCache == -1) {
			try {
				vassalClient.startJobTasks(nodes, spec, bestCost, jobid);
			} catch (TException ex) {
				throw new IOException("send exception", ex);
			}
		}
	}

	@Override
	public List<BnbNode> stealWork(int jobid) throws IOException {
		try {
			List<ThriftNodeData> nodesData = vassalClient.stealWork(jobid);
			List<BnbNode> nodes = new ArrayList<BnbNode>();
			for (ThriftNodeData nodeData : nodesData) {
				nodes.add(RpcUtil.fromThriftNode(nodeData));
			}
			return nodes;
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		} catch (ClassNotFoundException ex) {
			throw new IOException("class not found", ex);
		}
	}
}
