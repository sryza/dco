package bnb.stats;

import java.util.List;

import org.apache.log4j.Logger;

public class VassalJobStats {
	
	private static final Logger LOG = Logger.getLogger(VassalJobStats.class);
	
	private ThreadLocal<Long> askForWorkStart;
	private ThreadLocal<Long> nextNodeStart;
	
	//TODO: probably can just be a regular list
	private ThreadLocalList<Long> askForWorkLats;
	private ThreadLocalList<Long> nextNodeLats;
	
	private ThreadLocalList<Long> toggleWorkingTimes;
	
	private ThreadLocalCount totalStealTime;
	private ThreadLocalList<Long> workStolenTimes;//logged after work has been stolen
	private ThreadLocalList<Integer> workStealLats;
	
	private int numEvaluated;
	
	private long doneTime;
	
	public VassalJobStats() {
		askForWorkLats = new ThreadLocalList<Long>();
		askForWorkStart = new ThreadLocal<Long>();
		nextNodeLats = new ThreadLocalList<Long>();
		nextNodeStart = new ThreadLocal<Long>();
		toggleWorkingTimes = new ThreadLocalList<Long>();
		totalStealTime = new ThreadLocalCount();
		workStealLats = new ThreadLocalList<Integer>();
		workStolenTimes = new ThreadLocalList<Long>();
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

	public void reportWorkStolen(int timeTaken) {
		totalStealTime.add(timeTaken);
		workStealLats.add(timeTaken);
		workStolenTimes.add(System.currentTimeMillis());
	}
	
	public void reportWorking() {
		toggleWorkingTimes.add(System.currentTimeMillis());
	}
	
	public void reportNotWorking() {
		toggleWorkingTimes.add(System.currentTimeMillis());
	}
	
	public void reportDone() {
		doneTime = System.currentTimeMillis();
	}
	
	public void reportNumEvaluated(int numEvaluated) {
		this.numEvaluated = numEvaluated;
	}
		
	/**
	 * In a JSON format.
	 */
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"askForWork_latencies\":");
		sb.append(askForWorkLats.getAll());
		sb.append(",\n");
		sb.append("\"askForWork_latencies_stats\":");
		makeReportOnList(askForWorkLats.getAll(), sb);
		sb.append(",\n");
		sb.append("\"numEvaluated\": " + numEvaluated);
		sb.append(",\n");
		List<List<Long>> toggleWorkingLists = toggleWorkingTimes.getLists();
		sb.append("\"toggleWorkingLists\": " + toggleWorkingLists);
		sb.append(",\n");
		sb.append("\"workStealLats\": " + workStealLats.getAll());
		sb.append(",\n");
		sb.append("\"workStolenTimes\": " + workStolenTimes.getAll());
		sb.append(",\n");
		sb.append("\"totalStealTime\": " + totalStealTime.getCount());
		sb.append(",\n");
		sb.append("\"doneTime\": " + doneTime);
		
//		
//		sb.append("\"time_spent\": ");
//		if (workStartLists.size() != workStopLists.size()) {
//			sb.append("\"workStartLists.size() != workStopLists.size()\"");
//		} else {
//			List<Long> totalWorkingTimes = new ArrayList<Long>();
//			List<Long> totalTimes = new ArrayList<Long>();
//			boolean failed = false;
//			loop:
//			for (int i = 0; i < workStartLists.size(); i++) {
//				long totalWorkingTime = 0;
//				
//				List<Long> startTimesList = workStartLists.get(i);
//				List<Long> stopTimesList = workStopLists.get(i);
//				Iterator<Long> startTimesIter = startTimesList.iterator();
//				Iterator<Long> stopTimesIter = stopTimesList.iterator();
//				while (startTimesIter.hasNext()) {
//					if (!stopTimesIter.hasNext()) {
//						sb.append("\"no accompanying stop time for start time\"");
//						failed = true;
//						break loop;
//					}
//					long startTime = startTimesIter.next();
//					long stopTime = stopTimesIter.next();
//					if (stopTime < startTime) {
//						sb.append("\"stopTime < startTime\"");
//						failed = true;
//						break loop;
//					}
//					
//					totalWorkingTime += startTime - stopTime;
//				}
//				totalWorkingTimes.add(totalWorkingTime);
//				totalTimes.add(stopTimesList.get(stopTimesList.size()-1) - startTimesList.get(0));
//			}
//			if (!failed) {
//				sb.append("[" + totalWorkingTimes + ", " + totalTimes + "]");
//			}
//		}
		
		sb.append("}");
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
		
		sb.append("{");
		sb.append("\"count\": " + list.size());
		sb.append(", ");
		sb.append("\"max\": " + max);
		sb.append(", ");
		sb.append("\"min\": " + min + "\n");
		sb.append(", ");
		sb.append("\"sum\": " + sum + "\n");
		sb.append(", ");
		sb.append("\"avg\": " + avg + "\n");
		sb.append(", ");
		sb.append("\"var\": " + var + "\n");
		sb.append("}");
	}
}
