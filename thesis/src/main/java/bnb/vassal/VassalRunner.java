package bnb.vassal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import bnb.Problem;
import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.VassalPublic;

public class VassalRunner implements VassalPublic {
	
	private static final Logger LOG = Logger.getLogger(VassalRunner.class);
	
	private int numSlots;
	private final Map<Integer, VassalJobManager> jobMap;
	private final LordPublic lordInfo;
	private final int vassalId;
	private TServer server;
	private final int port;
	
	public VassalRunner(LordPublic lordInfo, int numSlots, int vassalId, int port) {
		this.numSlots = numSlots;
		jobMap = new HashMap<Integer, VassalJobManager>();
		this.lordInfo = lordInfo;
		this.vassalId = vassalId;
		this.port = port;
	}
	
	public void start() {
		startServer(port);
	}
	
	private void startServer(int port) {
		TServerSocket serverSocket;
		try {
			serverSocket = new TServerSocket(port);
			TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);
			server = new TThreadPoolServer(args);
			server.serve();
		} catch (TTransportException ex) {
			LOG.error("Trouble making server socket", ex);
		}
	}
		
	public int numSlots() {
		return numSlots;
	}
	
	@Override
	public void startJobTasks(List<BnbNode> nodes, Problem spec, double bestCost, int jobid) 
		throws IOException {
		VassalNodePool nodePool = new SimpleVassalNodePool();
		for (BnbNode node : nodes) {
			nodePool.postEvaluated(node);
		}
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, lordInfo, vassalId, jobid);
		jobMap.put(jobid, jobManager);
		startVassalRunner(lordInfo, nodePool, jobManager);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();
	}
	
	public void startVassalRunner(LordPublic lordInfo, VassalNodePool nodePool,
			VassalJobManager jobManager) {
		TaskRunner runner = new TaskRunner(lordInfo, jobManager);
		Thread vassalThread = new Thread(runner);
		vassalThread.start();
		numSlots--;
	}

	@Override
	public void updateBestSolCost(double bestCost, int jobid) throws IOException {
		jobMap.get(jobid).updateGlobalMinCost(bestCost);
	}

	@Override
	public int getNumSlots() throws IOException {
		return numSlots;
	}

	@Override
	public List<BnbNode> stealWork(int jobid) throws IOException {
		LOG.info("Work being stolen from job " + jobid);
		VassalJobManager jobManager = jobMap.get(jobid);
		return jobManager.stealWork();
	}
}
