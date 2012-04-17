package pls.stats;

import java.util.ArrayList;
import java.util.List;

import pls.PlsMetadata;

public class PlsJobStats {
	private List<Integer> roundTimes;
	private String problemName;
	private PlsMetadata metadata;
	private int numMappers;
	private int numRounds;
	
	public PlsJobStats(PlsMetadata metadata, String problemName, int numMappers, int numRounds) {
		roundTimes = new ArrayList<Integer>();
		this.metadata = metadata;
		this.numMappers = numMappers;
		this.numRounds = numRounds;
	}
	
	public void reportRoundTime(int time) {
		roundTimes.add(time);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append(",\n");
		sb.append("\"populationK\":" + metadata.getK());
		sb.append(",\n");
		sb.append("\"lsRunTime\":" + metadata.getRoundTime());
		sb.append(",\n");
		sb.append("\"addFirstNeighborhoods\":" + metadata.getAddFirstNeighborhoods());
		sb.append(",\n");
		sb.append("\"numHelperNeighborhoods\":" + metadata.getHelperDataNumNeighbors());
		sb.append(",\n");
		sb.append("\"numRounds\":" + numRounds);
		sb.append(",\n");
		sb.append("\"numMappers\":" + numMappers);
		sb.append(",\n");
		sb.append("\"problemName\":\"" + problemName + "\"");
		sb.append("}");
		return sb.toString();
	}
}
