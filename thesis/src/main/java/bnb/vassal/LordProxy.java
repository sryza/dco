package bnb.vassal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import bnb.BnbNode;
import bnb.rpc.RpcUtil;
import bnb.rpc.ThriftData;
import bnb.rpc.ThriftLord;

public class LordProxy {

	private ThriftLord.Client lordClient;
	private TSocket socket;

	public LordProxy(String host, int port) {
		socket = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(socket);
		lordClient = new ThriftLord.Client(protocol);
	}
	
	public void sendBestSolCost(double cost, int jobid, int vassalid) throws IOException {
		try {
			if (!socket.isOpen()) {
				socket.open();
			}
			lordClient.sendBestSolCost(cost, jobid, vassalid);
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		}
	}

	public List<BnbNode> askForWork(VassalJobManager jobManager) throws IOException {
		try {
			if (!socket.isOpen()) {
				socket.open();
			}

			int jobid = jobManager.getJobID();
			int vassalid = jobManager.getVassalID();
			List<ThriftData> nodesData = lordClient.askForWork(jobid, vassalid);
			List<BnbNode> nodes = new LinkedList<BnbNode>();
			for (ThriftData nodeData : nodesData) {
				nodes.add((BnbNode)RpcUtil.nodeFromThriftData(nodeData, jobManager.getProblem()));
			}
			return nodes;
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		} catch (ClassCastException ex) {
			throw new IOException("given class doesn't extend bnbnode", ex);
		} catch (ClassNotFoundException ex) {
			throw new IOException("invalid class", ex);
		} catch (InstantiationException e) {
			throw new IOException("invalid class", e);
		} catch (IllegalAccessException e) {
			throw new IOException("invalid class", e);
		} catch (IllegalArgumentException e) {
			throw new IOException("invalid class", e);
		} catch (InvocationTargetException e) {
			throw new IOException("invalid class", e);
		} catch (NoSuchMethodException e) {
			throw new IOException("invalid class", e);
		} catch (SecurityException e) {
			throw new IOException("invalid class", e);
		}
	}
}
