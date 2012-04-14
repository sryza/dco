package pls.gc;

import java.util.ArrayList;
import java.util.List;

public class GcJobStats {
	private GcPlsParams params;
	
	private List<Integer> roundTimes;
	private int popSize;
	private int k;
	private String problemName;
	private int numMappers;
	
	public GcJobStats(int numMappers, int popSize, int k, String problemName, GcPlsParams params) {
		roundTimes = new ArrayList<Integer>();
		this.numMappers = numMappers;
		this.k = k;
		this.popSize = popSize;
		this.params = params;
		this.problemName = problemName;
	}
	
	public void setNumMappers(int numMappers) {
		this.numMappers = numMappers;
	}
	
	public void setK(int k) {
		this.k = k;
	}
	
	public void setPopulationSize(int popSize) {
		this.popSize = popSize;
	}
	
	public void setParams(GcPlsParams params) {
		this.params = params;
	}
	
	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}
	
	public void reportRoundTime(int roundTime) {
		roundTimes.add(roundTime);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append(",\n");
		sb.append("\"k\":" + k);
		sb.append(",\n");
		sb.append("\"lsTime\":" + params.getLsTime());
		sb.append(",\n");
		sb.append("\"A\":" + params.getA());
		sb.append(",\n");
		sb.append("\"alpha\":" + params.getAlpha());
		sb.append(",\n");
		sb.append("\"numRounds\":" + roundTimes.size());
		sb.append(",\n");
		sb.append("\"numMappers\":" + numMappers);
		sb.append(",\n");
		sb.append("\"problemName\":\"" + problemName + "\"");
		sb.append("}");
		return sb.toString();
	}
}
