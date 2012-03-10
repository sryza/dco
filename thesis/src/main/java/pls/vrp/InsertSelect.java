package pls.vrp;

import java.util.IdentityHashMap;

import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.core.Var;
import JaCoP.search.SelectChoicePoint;

public class InsertSelect implements SelectChoicePoint<IntVar> {

	private IntVar[] successors;
	private int[] windowStartTimes;
	private int[] windowEndTimes;
	
	@Override
	public IntVar getChoiceVariable(int index) {
		return null;
	}

	@Override
	public int getChoiceValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PrimitiveConstraint getChoiceConstraint(int index) {
		
		int nodeId = -1;
		//check all insertion points
		IntDomain successorsDom = successors[nodeId].dom();
		//can only consider things that have already been inserted
		for (int succId : successorsDom.toIntArray()) { //TODO: don't need to make whole array
			succId--; //convert back to our indexing
			//check to see whether we can insert here
			//how do we find the predecessor?
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private int getPredecessor() {
		return -1;
	}
	
	@Override
	public IdentityHashMap getVariablesMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

}
