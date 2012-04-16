package pls;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class PlsMetadata implements Writable {
	private int k;
	private double bestCostAlways;
	private int roundTime;
	private boolean useBestForAll;
	private int extraDataNumNeighbs;
	private int helperDataNumNeighbs;
	private boolean addFirstNeighbs;
	
	private int bestSolRegularNumSuccessful;
	private int bestSolRegularNumTries;
	private int bestSolHelperNumSuccessful;
	private int bestSolHelperNumTries;
	private double regularImprovement;
	private double helperImprovement;
	
	public PlsMetadata() {
	}
	
	public PlsMetadata(int k, double bestCostAlways, int roundTime, boolean useBestForAll, int extraDataNumNeighbs,
			int helperDataNumNeighbs, boolean addFirstNeighbs) {
		this.k = k;
		this.bestCostAlways = bestCostAlways;
		this.roundTime = roundTime;
		this.useBestForAll = useBestForAll;
		this.extraDataNumNeighbs = extraDataNumNeighbs;
		this.helperDataNumNeighbs = helperDataNumNeighbs;
		this.addFirstNeighbs = addFirstNeighbs;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		k = input.readInt();
		bestCostAlways = input.readDouble();
		roundTime = input.readInt();
		useBestForAll = input.readBoolean();
		extraDataNumNeighbs = input.readInt();
		helperDataNumNeighbs = input.readInt();
		addFirstNeighbs = input.readBoolean();
		bestSolRegularNumSuccessful = input.readInt();
		bestSolRegularNumTries = input.readInt();
		bestSolHelperNumSuccessful = input.readInt();
		bestSolHelperNumTries = input.readInt();
		regularImprovement = input.readDouble();
		helperImprovement = input.readDouble();
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(k); 
		output.writeDouble(bestCostAlways);
		output.writeInt(roundTime);
		output.writeBoolean(useBestForAll);
		output.writeInt(extraDataNumNeighbs);
		output.writeInt(helperDataNumNeighbs);
		output.writeBoolean(addFirstNeighbs);
		output.writeInt(bestSolRegularNumSuccessful);
		output.writeInt(bestSolRegularNumTries);
		output.writeInt(bestSolHelperNumSuccessful);
		output.writeInt(bestSolHelperNumTries);
		output.writeDouble(regularImprovement);
		output.writeDouble(helperImprovement);
	}
	
	public int getK() {
		return k;
	}
	
	public double getBestCostAlways() {
		return bestCostAlways;
	}
	
	public void setBestCostAlways(double bestCostAlways) {
		this.bestCostAlways = bestCostAlways;
	}
	
	public int getRoundTime() {
		return roundTime;
	}
	
	public boolean getUseBestForAll() {
		return useBestForAll;
	}
	
	public boolean getAddFirstNeighborhoods() {
		return addFirstNeighbs;
	}
	
	public int getExtraDataNumNeighbors() {
		return extraDataNumNeighbs;
	}
	
	public int getHelperDataNumNeighbors() {
		return helperDataNumNeighbs;
	}
	
	public int getRegularNumSuccessful() {
		return bestSolRegularNumSuccessful;
	}
	
	public int getRegularNumTries() {
		return bestSolRegularNumTries;
	}
	
	public int getHelperNumSuccessful() {
		return bestSolHelperNumSuccessful;
	}
	
	public int getHelperNumTries() {
		return bestSolHelperNumTries;
	}
	
	public double getImprovementRegular() {
		return regularImprovement;
	}
	
	public double getImprovementHelper() {
		return helperImprovement;
	}
	
	public void setImprovementStats(int regSuccessful, int regNumTries, int helpSuccessful, int helpNumTries, 
			double regImprovement, double helpImprovement) {
		this.bestSolRegularNumSuccessful = regSuccessful;
		this.bestSolRegularNumTries = regNumTries;
		this.bestSolHelperNumSuccessful = helpSuccessful;
		this.bestSolHelperNumTries = helpNumTries;
		this.regularImprovement = regImprovement;
		this.helperImprovement = helpImprovement;
	}
}