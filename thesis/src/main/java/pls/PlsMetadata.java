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
}
