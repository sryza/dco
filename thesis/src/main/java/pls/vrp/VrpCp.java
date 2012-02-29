package pls.vrp;

import JaCoP.constraints.XeqY;
import JaCoP.core.IntVar;
import JaCoP.core.Store;

public class VrpCp {
	public static void main(String[] args) {
		final int numCities = 0;
		Store store = new Store();
		
		int[] serviceTimes = new int[numCities];
		
		//the model
		IntVar[] successors = new IntVar[numCities];
		IntVar[] successorVehicles = new IntVar[numCities];
		IntVar[] predecessors = new IntVar[numCities];
		IntVar[] vehicles = new IntVar[numCities];
		
		//impose that the vehicle servicing the successor of each node services that node
		// (i.e. the same vehicle services a tour)
		cp.post(vehicle[succ[n]] == vehicle[n]);
		
		for (int i = 0; i < numCities; i++) {
			store.impose(new Element(vehicles[i], successorVehicles[i]));
		}
	}
}
