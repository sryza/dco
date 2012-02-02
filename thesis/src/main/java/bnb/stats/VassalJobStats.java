package bnb.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class VassalJobStats {
	
	private static final Logger LOG = Logger.getLogger(VassalJobStats.class);
	
	private ThreadLocal<Long> askForWorkStart;
	private ThreadLocal<Long> nextNodeStart;
	
	//TODO: probably can just be a regular list
	private ThreadLocalList<Long> askForWorkLats;
	private ThreadLocalList<Long> nextNodeLats;
	
	private ThreadLocalList<Long> stopWorkTimes;
	private ThreadLocalList<Long> startWorkTimes;
	
	private ThreadLocal<Long> doneTime;
	
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
	
	public void reportWorking() {
		startWorkTimes.add(System.currentTimeMillis());
	}
	
	public void reportNotWorking() {
		stopWorkTimes.add(System.currentTimeMillis());
	}
	
	public void reportDone() {
		long time = System.currentTimeMillis();
		stopWorkTimes.add(time);
		doneTime.set(time);
	}
	
	/**
	 * TODO: maybe report depth of node stolen
	 */
	public void reportAskForWorkEnd() {
		askForWorkLats.add(System.currentTimeMillis()-askForWorkStart.get());
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

		sb.append("\"nextNode_latencies_stats\": ");
		makeReportOnList(nextNodeLats.getAll(), sb);
		
		sb.append("\"time_spent\": ");
		List<List<Long>> workStartLists = startWorkTimes.getLists();
		List<List<Long>> workStopLists = stopWorkTimes.getLists();
		if (workStartLists.size() != workStopLists.size()) {
			sb.append("-1");
		} else {
			List<Long> totalWorkingTimes = new ArrayList<Long>();
			List<Long> totalTimes = new ArrayList<Long>();
			loop:
			for (int i = 0; i < workStartLists.size(); i++) {
				long totalWorkingTime = 0;
				
				List<Long> startTimesList = workStartLists.get(i);
				List<Long> stopTimesList = workStopLists.get(i);
				Iterator<Long> startTimesIter = startTimesList.iterator();
				Iterator<Long> stopTimesIter = stopTimesList.iterator();
				while (startTimesIter.hasNext()) {
					if (!stopTimesIter.hasNext()) {
						sb.append("-1");
						break loop;
					}
					long startTime = startTimesIter.next();
					long stopTime = stopTimesIter.next();
					if (stopTime < startTime) {
						sb.append("-1");
						break loop;
					}
					
					totalWorkingTime += startTime - stopTime;
				}
				totalWorkingTimes.add(totalWorkingTime);
				totalTimes.add(stopTimesList.get(stopTimesList.size()-1) - startTimesList.get(0));
			}
			sb.append("[" + totalWorkingTimes + ", " + totalTimes + "]");
		}
		
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
