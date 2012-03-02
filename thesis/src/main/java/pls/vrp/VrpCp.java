package pls.vrp;

import JaCoP.constraints.Element;
import JaCoP.constraints.XgteqC;
import JaCoP.constraints.XltC;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYlteqZ;
import JaCoP.core.IntVar;
import JaCoP.core.Store;

public class VrpCp {
	public static void main(String[] args) {
		final int numCities = 0;
		Store store = new Store();
		
		int[][] distances = new int[numCities][numCities];
		int[] serviceTimes = new int[numCities];
		int[] windowStartTimes = new int[numCities];
		int[] windowEndTimes = new int[numCities];
		
		//the model
		IntVar[] successors = new IntVar[numCities];
		IntVar[] predecessors = new IntVar[numCities];
		IntVar[] vehicles = new IntVar[numCities];
		IntVar[] visitTimes = new IntVar[numCities];
		IntVar[] departTimes = new IntVar[numCities];
		IntVar[] successorVisitTimes = new IntVar[numCities];
		IntVar[] successorDistances = new IntVar[numCities];
		
		//each time we do an assignment, we manually impose this constraint
//		cp.post(vehicle[succ[n]] == vehicle[n]);
		
		//impose that the vehicle servicing the successor of each node services that node
		// (i.e. the same vehicle services a tour)
//		cp.post(vehicle[succ[n]] == vehicle[n]);
		
		for (int i = 0; i < numCities; i++) {
			store.impose(new Element(successors[i], vehicles, vehicles[i]));
			store.impose(new XplusCeqZ(visitTimes[i], serviceTimes[i], departTimes[i]));
			store.impose(new XltC(visitTimes[i], windowEndTimes[i])); //eq?
			store.impose(new XgteqC(visitTimes[i], windowStartTimes[i])); //eq?
			//arrival time
			//visitTime[succ[n]] >= departTime[n] + distance[n, succ[n]]
			store.impose(new Element(successors[i], visitTimes, successorVisitTimes[i]));
			store.impose(new Element(successors[i], distances[i], successorDistances[i]));
			store.impose(new XplusYlteqZ(departTimes[i], successorDistances[i], successorVisitTimes[i]));
//			store.impose(new XeqY(successorVisitTimes[i], new Max)
		}
	}
}
