package bnb.stats;

import java.util.concurrent.atomic.AtomicInteger;

public class LordJobStats {
	
	private long startTime;
	private long finishedInitialTime;
	private long finishTime;
	private ThreadLocalCount askedForWorkCount = new ThreadLocalCount();
	
	private AtomicInteger totalWorkStolenTime = new AtomicInteger();
	private AtomicInteger numWorkSteals = new AtomicInteger();
	
	/**
	 * Called after a successful work stealing when we're about to send
	 * the work back to the requesting client.
	 */
	public void workStolen(int timeTaken) {
		totalWorkStolenTime.addAndGet(timeTaken);
		numWorkSteals.incrementAndGet();
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
		sb.append("Total work stolen time: " + totalWorkStolenTime.get() + "\n");
		sb.append("Times work stolen: " + numWorkSteals.get() + "\n");
		return sb.toString();
	}
}
