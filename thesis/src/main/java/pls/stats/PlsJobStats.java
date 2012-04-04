package pls.stats;

import java.util.ArrayList;
import java.util.List;

public class PlsJobStats {
	private List<Integer> roundTimes;
	
	public PlsJobStats() {
		roundTimes = new ArrayList<Integer>();
	}
	
	public void reportRoundTime(int time) {
		roundTimes.add(time);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append("}");
		return sb.toString();
	}
}
