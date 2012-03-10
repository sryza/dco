package pls.vrp;

import JaCoP.core.Domain;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.search.Indomain;

public class IndomainShortest<T extends IntVar> implements Indomain<T> {
	
	private IntVar[] nodePositions;
	private IntVar[] successors;
	private IntVar[] nodesInOrder;
	
	@Override
	public int indomain(T var) {
		IntDomain dom  = var.dom();
		//TODO: can make this more optimal later
		for (int val : dom.toIntArray()) {
			//val is the index of a node which code be this node's successor
			//we want to assign it one
			
		}
		// TODO Auto-generated method stub
		return 0;
	}

}
