package pls.vrp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Writable;

public class VrpSolvingExtraData implements Writable {
	
	private static final int MAX_NEIGHBORHOODS = 50;
	
	private List<List<Integer>> neighborhoods;

	public VrpSolvingExtraData() {
		neighborhoods = new LinkedList<List<Integer>>();
	}
	
	public VrpSolvingExtraData(List<List<Integer>> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}
	
	public void addNeighborhood(VrpSolution partialSolution) {
		neighborhoods.add(partialSolution.getUninsertedNodes());
		if (neighborhoods.size() > MAX_NEIGHBORHOODS) {
			neighborhoods.remove(0);
		}
	}
	
	public List<List<Integer>> getNeighborhoods() {
		return neighborhoods;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		int numNeighborhoods = input.readInt();
		neighborhoods = new ArrayList<List<Integer>>(numNeighborhoods);
		for (int i = 0; i < numNeighborhoods; i++) {
			int numCusts = input.readInt();
			ArrayList<Integer> neighborhood = new ArrayList<Integer>(numCusts);
			neighborhoods.add(neighborhood);
			for (int j = 0; j < numCusts; j++) {
				neighborhood.add(input.readInt());
			}
		}
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
	}
}
