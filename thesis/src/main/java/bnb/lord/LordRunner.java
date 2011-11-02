package bnb.lord;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import bnb.ProblemSpec;
import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.VassalPublic;
import bnb.tsp.TspNode;
import bnb.vassal.VassalRunner;

public class LordRunner implements LordPublic {
	
	private static final Logger LOG = Logger.getLogger(LordRunner.class);
	
	private int nextJobid;
	private final Map<Integer, LordJobManager> jobMap;
	private final Map<Integer, VassalPublic> vassalMap;
	private final int port;
	
	private TServer server;
	
	public LordRunner(int port) {
		jobMap = new HashMap<Integer, LordJobManager>();
		vassalMap = new HashMap<Integer, VassalPublic>();
		this.port = port;
	}
	
	public void start() {
		startServer(port);
	}
	
	public void registerVassal(String host, int port, int id) {
		
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
	
	public void runJob(BnbNode root, ProblemSpec spec, double bestCost, List<VassalRunner> vassalServers) {
		int jobid = nextJobid++;
		
		int totalSlots = 0;
		for (VassalRunner runner : vassalServers) {
			totalSlots += runner.numSlots();
		}
		//TODO: what happens if slots free up during this?
		Starter starter = new Starter();
		List<BnbNode>[] startNodes = starter.startEvaluation(spec, bestCost, root, totalSlots);
		LordJobManager jobManager = new LordJobManager(jobid, startNodes[1]);
		jobMap.put(jobid, jobManager);
		Iterator<BnbNode> startNodesIter = startNodes[0].iterator();
		for (VassalRunner server : vassalServers) {
			List<BnbNode> nodePool = new LinkedList<BnbNode>();
			BnbNode node = startNodesIter.next();
			((TspNode)node).copyCities();
			nodePool.add(node);
			server.startJobTasks(nodePool, spec, bestCost, jobid);
		}
	}

	@Override
	public void sendBestSolCost(double cost, int jobid, int vassalid) {
		VassalPublic vassal = vassalMap.get(vassalid);
		if (vassal == null) {
			LOG.error("Lord couldn't locate vassal with id " + vassalid);
			return;
		}
		LordJobManager jobManager = jobMap.get(jobid);
		if (jobManager == null) {
			LOG.error("Lord couldn't local job with id " + jobid);
			return;
		}
		jobManager.updateMinCost(cost, vassal);
	}

	@Override
	public BnbNode askForWork(int jobid) {
		return null;
	}
}
