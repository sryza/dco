package bnb.vassal;

import java.util.LinkedList;
import java.util.List;

import bnb.BnbNode;

public class LDSNodePool implements VassalNodePool {

	private LinkedList<BnbNode> nodes;
	private LinkedList<Integer> nodeDiscrepancies;
	private int discrepancies;
	
	public LDSNodePool(int discrepancies) {
		this.discrepancies = discrepancies;
	}
	
	@Override
	public BnbNode nextNode() {
		
		return null;
	}

	@Override
	public void postEvaluated(BnbNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BnbNode> stealNodes() {
		// TODO Auto-generated method stub
		return null;
	}
}
