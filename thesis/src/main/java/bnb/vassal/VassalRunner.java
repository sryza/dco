package bnb.vassal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

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
import bnb.stats.VassalJobStats;

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
		
		//let lord know we're here
//		java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
//	    String hostname = addr.getHostName();
//	    System.out.println("Hostname of system = " + hostname);
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
			LOG.info("starting vassal server");
			serverThread.start();
		} catch (TTransportException ex) {
			LOG.error("Trouble making server socket", ex);
		}
	}
	
	public void stop() {
		server.stop();
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
		if (numThreads < 1) {
				LOG.error("Illegal number of threads: " + numThreads);
		}
		
		VassalNodePool nodePool = new SimpleVassalNodePool();
		for (BnbNode node : nodes) {
			//TODO: should this be happening here?
			if (!node.isEvaluated()) {
				node.evaluate(bestCost);
			}
			nodePool.post(node);
		}
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, spec, lordInfo, vassalId, jobid);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();
		
		VassalJobStats stats = new VassalJobStats();
		CyclicBarrier completeBarrier = new CyclicBarrier(numThreads);

		jobMap.put(jobid, jobManager);
		List<Thread> taskThreads = new ArrayList<Thread>();
		for (int i = 0; i < numThreads; i++) {
			Thread t = startTaskRunner(lordInfo, nodePool, jobManager, stats, completeBarrier);
			taskThreads.add(t);
		}
		
		new TermThread(taskThreads).start();
	}
	
	/**
	 * Returns the thread that the task is running on.
	 */
	public Thread startTaskRunner(LordProxy lordInfo, VassalNodePool nodePool,
			VassalJobManager jobManager, VassalJobStats stats, CyclicBarrier completeBarrier) {
		TaskRunner runner = new TaskRunner(jobManager, stats, completeBarrier);
		jobManager.registerTaskRunner(runner);
		Thread taskThread = new Thread(runner);
		taskThread.setName("Vassal " + vassalId + " TaskRunner");
		taskThread.start();
		return taskThread;
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
	
	/**
	 * For terminating this vassal when a job is done.
	 */
	private class TermThread extends Thread {
		
		private final List<Thread> threads;
		
		public TermThread(List<Thread> threads) {
			this.threads = threads;
		}
		
		public void run() {
			for (Thread thread : threads) {
				try {
					thread.join();	
				} catch (InterruptedException ex) {
					LOG.error("Interrupted while waiting to terminate", ex);
				}
			}
			LOG.info("job completed, stopping Thrift server");
			VassalRunner.this.stop();
			LOG.info("Thrift server successfully stopped");
		}
	}
}
