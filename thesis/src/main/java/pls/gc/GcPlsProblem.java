package pls.gc;

import gc.GcProblem;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class GcPlsProblem implements Writable {

	private GcProblem problem;
	private int k;
	
	public GcPlsProblem() {
	}
	
	public GcPlsProblem(GcProblem problem) {
		this.problem = problem;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		k = input.readInt();
		int numNodes = input.readInt();
		int[][] nodeNeighbors = new int[numNodes][];
		for (int i = 0; i < numNodes; i++) {
			int numNeighbors = input.readInt();
			nodeNeighbors[i] = new int[numNeighbors];
			for (int j = 0; j < numNeighbors; j++) {
				nodeNeighbors[i][j] = input.readInt();
			}
		}
		problem = new GcProblem(nodeNeighbors);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(k);
		int[][] nodeNeighbors = problem.getNodeNeighbors();
		output.writeInt(nodeNeighbors.length);
		for (int[] neighbors : nodeNeighbors) {
			output.writeInt(neighbors.length);
			for (int neighbor : neighbors) {
				output.writeInt(neighbor);
			}
		}
	}
	
	public GcProblem getProblem() {
		return problem;
	}
	
	public int getK() {
		return k;
	}
}
