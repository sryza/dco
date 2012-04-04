package bnb.stats;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LordJobStats {
	
	private long startTime;
	private long finishedInitialTime;
	private long finishTime;
	private ThreadLocalCount askedForWorkCount = new ThreadLocalCount();
	private ThreadLocalList<WorkTheft> workThefts = new ThreadLocalList<WorkTheft>();
	
//	private AtomicInteger totalWorkStolenTime = new AtomicInteger();
//	private AtomicInteger numWorkSteals = new AtomicInteger();
	
	/**
	 * Called after a successful work stealing when we're about to send
	 * the work back to the requesting client.
	 * @param timeTaken
	 * 		time taken in milliseconds for the whole operation
	 * @param numFailedAttempts
	 * 		the number of clients who were reached and had no work to donate
	 */
	public void reportWorkStolen(int timeTaken, int numFailedAttempts) {
		long time = System.currentTimeMillis();
		workThefts.add(new WorkTheft(timeTaken, System.currentTimeMillis(), numFailedAttempts));
	}
	
	public void aboutToStart() {
		startTime = System.currentTimeMillis();
	}
	
	public void finishedSendingInitialWork() {
		finishedInitialTime = System.currentTimeMillis();
	}
	
	public void askedForWork() {
		askedForWorkCount.increment();
	}
	
	public void finished() {
		finishTime = System.currentTimeMillis();
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total time: " + (finishTime - startTime) + "\n");
		sb.append("Initialization time: " + (finishedInitialTime - startTime) + "\n");
		sb.append("Finish time: " + finishTime + "\n");
		sb.append("# Work Stealing Threads: " + workThefts.getLists().size() + "\n");
		sb.append("Total work stolen time: " + sumWorkStolenTime() + "\n");
		sb.append("Times work stolen: " + sumTimesWorkStolen() + "\n");
		return sb.toString();
	}
	
	private int sumWorkStolenTime() {
		int sum = 0;
		List<List<WorkTheft>> lists = workThefts.getLists();
		for (List<WorkTheft> list : lists) {
			for (WorkTheft theft : list) {
				sum += theft.timeTaken;
			}
		}
		return sum;
	}
	
	private int sumTimesWorkStolen() {
		int sum = 0;
		List<List<WorkTheft>> lists = workThefts.getLists();
		for (List<WorkTheft> list : lists) {
			sum += list.size();
		}
		return sum;
	}
	
	private class WorkTheft {
		public int timeTaken;
		public long endTime;
		public int numFailedAttempts;
		
		public WorkTheft(int duration, long endTime, int numFailedAttempts) {
			this.timeTaken = duration;
			this.endTime = endTime;
			this.numFailedAttempts = numFailedAttempts;
		}
	}
}
