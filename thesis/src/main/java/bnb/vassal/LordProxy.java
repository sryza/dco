package bnb.vassal;

import java.io.IOException;
import java.net.Socket;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.ThriftLord;
import bnb.rpc.VassalPublic;

public class LordProxy implements LordPublic {

	private ThriftLord.Client lordClient;
	
	public LordProxy(String host, int port) {
		TSocket socket = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(socket);
		lordClient = new ThriftLord.Client(protocol);
	}
	
	@Override
	public void sendBestSolCost(double cost, int jobid, int vassalid) throws IOException {
		try {
			lordClient.sendBestSolCost(cost, jobid);
		} catch (TException ex) {
			throw new IOException("send exception", ex);
		}
	}

	@Override
	public BnbNode askForWork() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
