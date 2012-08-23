package pls.gc;

import gc.GcSolution;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import pls.WritableSolution;

public class GcPlsSolution implements WritableSolution {

	private GcSolution sol;
	
	public GcPlsSolution(GcSolution sol) {
		this.sol = sol;
	}
	
	public GcPlsSolution() {
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		int numNodes = input.readInt();
		int[] nodeColors = new int[numNodes];
		for (int i = 0; i < numNodes; i++) {
			nodeColors[i] = input.readByte();
		}
//		if (problem != null) {
//			sol = new GcSolution(nodeColors, problem.getNodeNeighbors());
//		} else {
//			sol = new GcSolution(nodeColors);
//		}
		int cost = input.readInt();
		sol = new GcSolution(nodeColors, cost);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		int[] colors = sol.getNodeColors();
		output.writeInt(colors.length);
		for (int color : colors) {
			output.writeByte((byte)color);
		}
		output.writeInt(sol.getCost());
	}
	
	@Override
	public double getCost() {
		return sol.getCost();
	}
	
	public GcSolution getSolution() {
		return sol;
	}
}
