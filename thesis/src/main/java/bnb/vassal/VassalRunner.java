package bnb.vassal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import bnb.Problem;
import bnb.BnbNode;
import bnb.rpc.ThriftVassal;
import bnb.rpc.VassalPublic;
import bnb.rpc.VassalThriftWrapper;

public class VassalRunner implements VassalPublic {
	
	private static final Logger LOG = Logger.getLogger(VassalRunner.class);
	
	private int numSlots;
	private final Map<Integer, VassalJobManager> jobMap;
	private final LordProxy lordInfo;
	private final int vassalId;
	private TServer server;
	private final int port;
	
	public VassalRunner(LordProxy lordProxy, int numSlots, int vassalId, int port) {
		this.numSlots = numSlots;
		jobMap = new HashMap<Integer, VassalJobManager>();
		this.lordInfo = lordProxy;
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
			VassalThriftWrapper vassalThriftWrapper = new VassalThriftWrapper(this);
			TProcessor processor = new ThriftVassal.Processor<VassalThriftWrapper>(vassalThriftWrapper);
			args.processor(processor);
			server = new TThreadPoolServer(args);
			Thread serverThread = new Thread("Vassal Thrift Server") {
				public void run() {
					server.serve();
				}
			};
			serverThread.start();
		} catch (TTransportException ex) {
			LOG.error("Trouble making server socket", ex);
		}
	}
		
	public int numSlots() {
		return numSlots;
	}
	
	public int getId() {
		return vassalId;
	}
	
	@Override
	public void startJobTasks(List<BnbNode> nodes, Problem spec, double bestCost, int jobid, int numThreads) 
		throws IOException {
		VassalNodePool nodePool = new SimpleVassalNodePool();
		for (BnbNode node : nodes) {
			//TODO: should this be happening here?
			if (!node.isEvaluated()) {
				node.evaluate(bestCost);
			}
			nodePool.postEvaluated(node);
		}
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, spec, lordInfo, vassalId, jobid);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();

		jobMap.put(jobid, jobManager);
		for (int i = 0; i < numThreads; i++) {
			startTaskRunner(lordInfo, nodePool, jobManager);
		}
	}
	
	public void startTaskRunner(LordProxy lordInfo, VassalNodePool nodePool,
			VassalJobManager jobManager) {
		TaskRunner runner = new TaskRunner(jobManager);
		Thread taskThread = new Thread(runner);
		taskThread.setName("Vassal " + vassalId + " TaskRunner");
		taskThread.start();
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
