package pls.stats;

import java.util.ArrayList;
import java.util.List;

import pls.PlsMetadata;
import pls.vrp.LnsExtraData;

public class PlsJobStats {
	private String problemName;
	private PlsMetadata metadata;
	private int numMappers;
	private int numRounds;
	private List<Double> bestSolCosts;
	private List<LnsExtraData> bestExtraDatas;
	private List<PlsMetadata> plsMetadatas;
	private List<Integer> roundTimes;
	
	public PlsJobStats(PlsMetadata metadata, String problemName, int numMappers, int numRounds) {
		roundTimes = new ArrayList<Integer>();
		bestSolCosts = new ArrayList<Double>();
		plsMetadatas = new ArrayList<PlsMetadata>();
		bestExtraDatas = new ArrayList<LnsExtraData>();
		this.metadata = metadata;
		this.numMappers = numMappers;
		this.numRounds = numRounds;
	}
	
	public void reportRoundTime(int time) {
		roundTimes.add(time);
	}
	
	public void reportBestSolCost(double bestSolCost) {
		bestSolCosts.add(bestSolCost);
	}
	
	public void reportMetadata(PlsMetadata metadata) {
		plsMetadatas.add(metadata);
	}
	
	public void reportBestExtraData(LnsExtraData extraData) {
		bestExtraDatas.add(extraData);
	}
	
	public String makeReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"problemName\":\"" + problemName + "\"");
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
		sb.append("\"roundLengths\":" + roundTimes);
		sb.append(",\n");
		sb.append("\"bestSolCosts\":" + bestSolCosts);
		sb.append(",\n");
		sb.append("\"helperTimes\":" + compileHelperTimes());
		sb.append(",\n");
		sb.append("\"regularTimes\":" + compileRegularTimes());
		sb.append(",\n");
		sb.append("\"helperSuccessfuls\":" + compileHelperNumSuccessfuls());
		sb.append(",\n");
		sb.append("\"helperTrieds\":" + compileHelperNumTrieds());
		sb.append(",\n");
		sb.append("\"regularSuccessfuls\":" + compileRegularNumSuccessfuls());
		sb.append(",\n");
		sb.append("\"regularTrieds\":" + compileRegularNumTrieds());
		sb.append(",\n");
		sb.append("\"helperImprovements\":" + compileHelperImprovements());
		sb.append(",\n");
		sb.append("\"regularImprovements\":" + compileRegularImprovements());
		sb.append("}");
		return sb.toString();
	}
	
	private List<Integer> compileHelperTimes() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getHelperTime());
		}
		return list;
	}
	
	private List<Integer> compileRegularTimes() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getRegularTime());
		}
		return list;
	}
	
	private List<Integer> compileHelperNumSuccessfuls() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getNumHelperSuccessful());
		}
		return list;
	}
	
	private List<Integer> compileRegularNumSuccessfuls() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getNumRegularSuccessful());
		}
		return list;
	}
	
	private List<Integer> compileHelperNumTrieds() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getNumHelperTries());
		}
		return list;
	}
	
	private List<Integer> compileRegularNumTrieds() {
		List<Integer> list = new ArrayList<Integer>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getNumRegularTries());
		}
		return list;
	}
	
	private List<Double> compileHelperImprovements() {
		List<Double> list = new ArrayList<Double>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getHelperImprovement());
		}
		return list;
	}
	
	private List<Double> compileRegularImprovements() {
		List<Double> list = new ArrayList<Double>();
		for (PlsMetadata data : plsMetadatas) {
			list.add(data.getRegularImprovement());
		}
		return list;
	}
	
	private int sumInts(List<Integer> intList) {
		int total = 0;
		for (int num : intList) {
			total += num;
		}
		return total;
	}
	
	private double sumDoubles(List<Double> doubleList) {
		double total = 0;
		for (double num : doubleList) {
			total += num;
		}
		return total;
	}
}
