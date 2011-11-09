package bnb.lord;

import java.io.IOException;
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

import bnb.Problem;
import bnb.BnbNode;
import bnb.rpc.LordPublic;
import bnb.rpc.ThriftLord;
import bnb.rpc.LordThriftWrapper;
import bnb.rpc.VassalPublic;

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
		VassalProxy proxy = new VassalProxy(host, port);
		vassalMap.put(id, proxy);
	}
	
	private void startServer(int port) {
		TServerSocket serverSocket;
		try {
			serverSocket = new TServerSocket(port);
			TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);
			LordThriftWrapper lordThriftWrapper = new LordThriftWrapper(this);
			args.processor(new ThriftLord.Processor<LordThriftWrapper>(lordThriftWrapper));
			server = new TThreadPoolServer(args);
			server.serve();
		} catch (TTransportException ex) {
			LOG.error("Trouble making server socket", ex);
		}
	}
	
	public void runJob(BnbNode root, Problem spec, double bestCost, List<VassalPublic> vassalServers) {
		int jobid = nextJobid++;
		
		int totalSlots = 0;
		for (VassalPublic runner : vassalServers) {
			try {
				totalSlots += runner.getNumSlots();
			} catch (IOException ex) {
				//TODO: don't use this runner
				LOG.error("Couldn't reach runner to find number of slots", ex);
			}
		}
		//TODO: what happens if slots free up during this?
		Starter starter = new Starter();
		List<BnbNode>[] startNodes = starter.startEvaluation(spec, bestCost, root, totalSlots);
		LordJobManager jobManager = new LordJobManager(jobid, startNodes[1]);
		jobMap.put(jobid, jobManager);
		Iterator<BnbNode> startNodesIter = startNodes[0].iterator();
		for (VassalPublic vassal : vassalServers) {
			List<BnbNode> nodePool = new LinkedList<BnbNode>();
			BnbNode node = startNodesIter.next();
			nodePool.add(node);
			
			try {
				vassal.startJobTasks(nodePool, spec, bestCost, jobid);
			} catch (IOException ex) {
				LOG.error("Failed to start job tasks on vassal", ex);
			}
		}
	}

	@Override
	public void sendBestSolCost(double cost, int jobid, int vassalid) throws IOException {
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
	public List<BnbNode> askForWork(int jobid) {
		return null;
	}
}
