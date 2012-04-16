package pls.vrp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

public class VrpSolvingExtraData implements Writable {
	
	private static final Logger LOG = Logger.getLogger(VrpSolvingExtraData.class);
	
	private int maxNeighborhoods = 50;
	private List<List<Integer>> neighborhoods;
	private boolean addFirstNeighborhoods;
	
	private int numHelperSuccessful;
	private int numHelperTried;
	private int numRegularSuccessful;
	private int numRegularTried;
	private double regularImprovement;
	private double helperImprovement;
	
	public VrpSolvingExtraData() {
		neighborhoods = new LinkedList<List<Integer>>();
	}
	
	public VrpSolvingExtraData(List<List<Integer>> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}
	
	public void setAddFirstNeighborhoods(boolean addFirstNeighborhoods) {
		this.addFirstNeighborhoods = addFirstNeighborhoods;
	}
	
	public void setMaxNeighborhoods(int maxNeighbs) {
		maxNeighborhoods = maxNeighbs;
	}
	
	public void addNeighborhood(VrpSolution partialSolution) {
		if (maxNeighborhoods > 0 && (!addFirstNeighborhoods || neighborhoods.size() < maxNeighborhoods)) {
			neighborhoods.add(partialSolution.getUninsertedNodes());
			if (neighborhoods.size() > maxNeighborhoods) {
				neighborhoods.remove(0);
			}
		}
	}
	
	public void setHelperStats(int numHelperSuccessful, int numHelperTried, double improvement) {
		this.numHelperSuccessful = numHelperSuccessful;
		this.numHelperTried = numHelperTried;
		this.helperImprovement = improvement;
	}
	
	public void setRegularStats(int numRegularSuccessful, int numRegularTried, double improvement) {
		this.numRegularSuccessful = numRegularSuccessful;
		this.numRegularTried = numRegularTried;
		this.regularImprovement = improvement;
	}
	
	public int getNumRegularSuccessful() {
		return numRegularSuccessful;
	}
	
	public int getNumRegularTried() {
		return numRegularTried;
	}
	
	public double getRegularImprovement() {
		return regularImprovement;
	}
	
	
	public double getHelperImprovement() {
		return helperImprovement;
	}
	
	public int getNumHelperTried() {
		return numHelperTried;
	}
	
	public int getNumHelperSuccessful() {
		return numHelperSuccessful;
	}
	
	public List<List<Integer>> getNeighborhoods() {
		return neighborhoods;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		int numNeighborhoods = input.readInt();
		LOG.info("About to read " + numNeighborhoods + " of helper data.");
		neighborhoods = new ArrayList<List<Integer>>(numNeighborhoods);
		for (int i = 0; i < numNeighborhoods; i++) {
			int numCusts = input.readInt();
			ArrayList<Integer> neighborhood = new ArrayList<Integer>(numCusts);
			neighborhoods.add(neighborhood);
			for (int j = 0; j < numCusts; j++) {
				neighborhood.add(input.readInt());
			}
		}
		numHelperSuccessful = input.readInt();
		numHelperTried = input.readInt();
		numRegularSuccessful = input.readInt();
		numRegularTried = input.readInt();
		regularImprovement = input.readDouble();
		helperImprovement = input.readDouble();

	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(neighborhoods.size());
		for (List<Integer> custs : neighborhoods) {
			output.writeInt(custs.size());
			for (Integer cust : custs) {
				output.writeInt(cust);
			}
		}
		
		output.writeInt(numHelperSuccessful);
		output.writeInt(numHelperTried);
		output.writeInt(numRegularSuccessful);
		output.writeInt(numRegularTried);
		output.writeDouble(regularImprovement);
		output.writeDouble(helperImprovement);
	}
	
	@Override
	public boolean equals(Object o) {
		VrpSolvingExtraData other = (VrpSolvingExtraData)o;
		return other.neighborhoods.equals(neighborhoods);
	}
}
