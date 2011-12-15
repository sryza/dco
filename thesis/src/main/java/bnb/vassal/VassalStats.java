package bnb.vassal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VassalStats {
	private ThreadLocal<Long> askForWorkStart;
	private ThreadLocal<Long> nextNodeStart;
	
	//TODO: probably can just be a regular list
	private ThreadLocalList<Long> askForWorkLats;
	private ThreadLocalList<Long> nextNodeLats;
	
	public VassalStats() {
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
		System.out.println(makeReport());
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
		sb.append("avg: " + avg + "\n");
		sb.append("var: " + var + "\n");
	}
	
	/**
	 * Aggregates separate statistics for each thread with the option to combine them
	 * later.
	 */
	private class ThreadLocalList<T> {
		private List<List<T>> listOfLists = new LinkedList<List<T>>();
		private ThreadLocal<List<T>> list = new ThreadLocal<List<T>>();
		
		public void add(T t) {
			if (list.get() == null) {
				list.set(new LinkedList<T>());
				synchronized(listOfLists) {
					listOfLists.add(list.get());
				}
			}
			list.get().add(t);
		}
		
		public List<T> getAll() {
			List<T> all = new ArrayList<T>();
			for (List<T> list : listOfLists) {
				all.addAll(list);
			}
			return all;
		}
	}
}
