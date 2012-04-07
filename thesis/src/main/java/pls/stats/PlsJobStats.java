package pls.stats;

import java.util.ArrayList;
import java.util.List;

public class PlsJobStats {
	private List<Integer> roundTimes;
	private int k;
	private int lsRunTime;
	
	public PlsJobStats() {
		roundTimes = new ArrayList<Integer>();
	}
	
	public void setK(int k) {
		this.k = k;
	}
	
	public void setLsRunTime(int time) {
		this.lsRunTime = time;
	}
	
	public void reportRoundTime(int time) {
		roundTimes.add(time);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append(",\n");
		sb.append("\"populationK\":" + k);
		sb.append(",\n");
		sb.append("\"lsRunTime\":" + lsRunTime);
		sb.append("}");
		return sb.toString();
	}
}
