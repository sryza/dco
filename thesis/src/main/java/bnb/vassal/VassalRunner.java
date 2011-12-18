package bnb.vassal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
import bnb.stats.VassalJobStats;

public class VassalRunner implements VassalPublic {
	
	private static final Logger LOG = Logger.getLogger(VassalRunner.class);
	
	private int numSlots;
	private final Map<Integer, VassalJobManager> jobMap;
	private final LordProxy lordProxy;
	private final int vassalId;
	private TServer server;
	private final int port;
	
	private final OutputStream statsOs;
	
	public VassalRunner(LordProxy lordProxy, int numSlots, int vassalId, int port, OutputStream statsOs) {
		this.numSlots = numSlots;
		jobMap = new HashMap<Integer, VassalJobManager>();
		this.lordProxy = lordProxy;
		this.vassalId = vassalId;
		this.port = port;
		this.statsOs = statsOs;
	}
	
	public void start() {
		startServer(port);
		
		//let lord know we're here
		try {
			InetAddress addr = InetAddress.getLocalHost();
		    String hostname = addr.getHostName();
		    LOG.info("My hostname is " + hostname);
		    lordProxy.registerVassal(hostname, port, vassalId);
		} catch (UnknownHostException ex) {
			LOG.error("Failed to retrieve hostname, not going to register with lord", ex);
		} catch (IOException ex) {
			LOG.error("Failed to register with lord", ex);
		}
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
		VassalJobManager jobManager = new VassalJobManager(bestCost, nodePool, spec, lordProxy, vassalId, jobid);
		Thread jobManagerThread = new Thread(jobManager, "jobmanager" + jobid);
		jobManagerThread.start();
		
		VassalJobStats stats = new VassalJobStats();

		jobMap.put(jobid, jobManager);
		List<Thread> taskThreads = new ArrayList<Thread>();
		for (int i = 0; i < numThreads; i++) {
			Thread t = startTaskRunner(lordProxy, nodePool, jobManager, stats);
			taskThreads.add(t);
		}
		
		new TermThread(taskThreads, stats).start();
	}
	
	/**
	 * Returns the thread that the task is running on.
	 */
	public Thread startTaskRunner(LordProxy lordInfo, VassalNodePool nodePool,
			VassalJobManager jobManager, VassalJobStats stats) {
		TaskRunner runner = new TaskRunner(jobManager, stats);
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
		private final VassalJobStats stats;
		
		public TermThread(List<Thread> threads, VassalJobStats stats) {
			this.threads = threads;
			this.stats = stats;
		}
		
		public void run() {
			for (Thread thread : threads) {
				try {
					thread.join();	
				} catch (InterruptedException ex) {
					LOG.error("Interrupted while waiting to terminate", ex);
				}
			}
			
			if (statsOs != null) {
				try {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(statsOs));
					bw.write(stats.makeReport());
				} catch (IOException ex) {
					LOG.error("Error writing stats to output stream", ex);
				}
			}
			
			LOG.info("job completed, stopping Thrift server");
			VassalRunner.this.stop();
			LOG.info("Thrift server successfully stopped");
			
			System.exit(0);
		}
	}
}
