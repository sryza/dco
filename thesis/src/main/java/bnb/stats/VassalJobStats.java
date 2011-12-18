package bnb.stats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class VassalJobStats {
	
	private static final Logger LOG = Logger.getLogger(VassalJobStats.class);
	
	private ThreadLocal<Long> askForWorkStart;
	private ThreadLocal<Long> nextNodeStart;
	
	//TODO: probably can just be a regular list
	private ThreadLocalList<Long> askForWorkLats;
	private ThreadLocalList<Long> nextNodeLats;
	
	public VassalJobStats() {
		askForWorkLats = new ThreadLocalList<Long>();
		askForWorkStart = new ThreadLocal<Long>();
		nextNodeLats = new ThreadLocalList<Long>();
		nextNodeStart = new ThreadLocal<Long>();
	}
	
	public void reportNextNodeStart() {
		nextNodeStart.set(System.currentTimeMillis());
	}
	
	public void reportNextNodeEnd() {
		nextNodeLats.add(System.currentTimeMillis()-nextNodeStart.get());
	}
	
	public void reportAskForWorkStart() {
		askForWorkStart.set(System.currentTimeMillis());
	}
	
	/**
	 * TODO: maybe report depth of node stolen
	 */
	public void reportAskForWorkEnd() {
		askForWorkLats.add(System.currentTimeMillis()-askForWorkStart.get());
	}
	
	/**
	 * If this method is called multiple times (from different threads) it
	 * only executes once.
	 */
	public void report() {
		LOG.info(makeReport());
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ask for work latencies: \n");
		makeReportOnList(askForWorkLats.getAll(), sb);
		sb.append("Next node latencies: \n");
		makeReportOnList(nextNodeLats.getAll(), sb);

		return sb.toString();
	}
	
	/**
	 * Calculates statistics on a list of numbers and appends them in
	 * human readable form to the given StringBuilder.
	 */
	private void makeReportOnList(List<Long> list, StringBuilder sb) {
		long max = Integer.MIN_VALUE;
		long min = Integer.MAX_VALUE;
		long sum = 0;
		for (Long val : list) {
			sum += val;
			if (val > max) {
				max = val;
			}
			if (val < min) {
				min = val;
			}
		}
		double avg = sum / list.size();
		double sumSquareDist = 0.0;
		for (Long val : list) {
			sumSquareDist += (val - avg) * (val - avg);
		}
		double var = sumSquareDist / list.size();
		
		sb.append("max: " + max + "\n");
		sb.append("min: " + min + "\n");
		sb.append("sum: " + sum + "\n");
		sb.append("avg: " + avg + "\n");
		sb.append("var: " + var + "\n");
	}
}
