/**
 * Copyright 2012 Sandy Ryza
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pls.vrp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

public class LnsExtraData implements Writable {
	
	private static final Logger LOG = Logger.getLogger(LnsExtraData.class);
	
	private int maxNeighborhoods = 50;
	private List<List<Integer>> neighborhoods;
	
	private int numHelperSuccessful;
	private int numHelperTried;
	private int numRegularSuccessful;
	private int numRegularTried;
	private double regularImprovement;
	private double helperImprovement;
	
	private int helperTime;
	private int regularTime;
	
	public LnsExtraData() {
		neighborhoods = new LinkedList<List<Integer>>();
	}
	
	public LnsExtraData(List<List<Integer>> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}
	
	public void setMaxNeighborhoods(int maxNeighbs) {
		maxNeighborhoods = maxNeighbs;
	}
	
	public void addNeighborhood(VrpSolution partialSolution) {
		addNeighborhood(partialSolution.getUninsertedNodes());
	}
	
	public void addNeighborhood(List<Integer> neighborhood) {
		if (maxNeighborhoods > 0 && (neighborhoods.size() < maxNeighborhoods)) {
			neighborhoods.add(neighborhood);
//			if (neighborhoods.size() > maxNeighborhoods) {
//				neighborhoods.remove(0);
//			}
		}
	}
	
	public int getHelperTime() {
		return helperTime;
	}
	
	public int getRegularTime() {
		return regularTime;
	}
	
	public void setHelperStats(int numHelperSuccessful, int numHelperTried, double improvement, int time) {
		this.numHelperSuccessful = numHelperSuccessful;
		this.numHelperTried = numHelperTried;
		this.helperImprovement = improvement;
		this.helperTime = time;
	}
	
	public void setRegularStats(int numRegularSuccessful, int numRegularTried, double improvement, int time) {
		this.numRegularSuccessful = numRegularSuccessful;
		this.numRegularTried = numRegularTried;
		this.regularImprovement = improvement;
		this.regularTime = time;
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
		helperTime = input.readInt();
		regularTime = input.readInt();
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
		output.writeInt(helperTime);
		output.writeInt(regularTime);
	}
	
	@Override
	public boolean equals(Object o) {
		LnsExtraData other = (LnsExtraData)o;
		return other.neighborhoods.equals(neighborhoods);
	}
}
