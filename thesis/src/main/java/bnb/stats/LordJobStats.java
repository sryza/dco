package bnb.stats;

public class LordJobStats {
	
	private long startTime;
	private long finishedInitialTime;
	private long finishTime;
	private ThreadLocalCount askedForWorkCount = new ThreadLocalCount();
	
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
		return sb.toString();
	}
}
