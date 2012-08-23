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
	
	private List<Integer> bestSolCosts;
	
	public GcJobStats(int numMappers, int popSize, int k, String problemName, GcPlsParams params) {
		roundTimes = new ArrayList<Integer>();
		bestSolCosts = new ArrayList<Integer>();
		this.numMappers = numMappers;
		this.k = k;
		this.popSize = popSize;
		this.params = params;
		this.problemName = problemName;
	}
	
	public void reportRoundTime(int roundTime) {
		roundTimes.add(roundTime);
	}
	
	public void reportBestSolCost(int bestCost) {
		bestSolCosts.add(bestCost);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append(",\n");
		sb.append("\"k\":" + k);
		sb.append(",\n");
		sb.append("\"popSize\":" + popSize);
		sb.append(",\n");
		sb.append("\"lsTime\":" + params.getLsTime());
		sb.append(",\n");
		sb.append("\"A\":" + params.getA());
		sb.append(",\n");
		sb.append("\"alpha\":" + params.getAlpha());
		sb.append(",\n");
		sb.append("\"reduceShuffle\":" + params.getReduceShuffle());
		sb.append(",\n");
		sb.append("\"numRounds\":" + roundTimes.size());
		sb.append(",\n");
		sb.append("\"numMappers\":" + numMappers);
		sb.append(",\n");
		sb.append("\"problemName\":\"" + problemName + "\"");
		sb.append(",\n");
		sb.append("\"roundTimes\":\"" + roundTimes + "\"");
		sb.append(",\n");
		sb.append("\"bestSolCosts\":\"" + bestSolCosts + "\"");
		sb.append("}");
		return sb.toString();
	}
}
