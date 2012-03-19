package pls.vrp.hm;

public class VrpCpStats {
	
	private int nEvaluated;
	private long insertTimeStart;
	private long maxInsertTime;
	
	public void reportNodeEvaluated() {
		nEvaluated++;
	}
	
	public int getNumNodesEvaluated() {
		return nEvaluated;
	}
	
	public void reportAboutToInsert() {
		insertTimeStart = System.currentTimeMillis();
	}
	
	public void reportFinishedInsertion() {
		long time = System.currentTimeMillis() - insertTimeStart;
		if (time > maxInsertTime) {
			maxInsertTime = time;
		}
	}
	
	public long getMaxInsertTime() {
		return maxInsertTime;
	}
}
