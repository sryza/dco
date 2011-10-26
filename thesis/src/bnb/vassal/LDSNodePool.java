package bnb.vassal;

import java.util.LinkedList;
import java.util.List;

import bnb.TreeNode;

public class LDSNodePool implements VassalNodePool {

	private LinkedList<TreeNode> nodes;
	private LinkedList<Integer> nodeDiscrepancies;
	private int discrepancies;
	
	public LDSNodePool(int discrepancies) {
		this.discrepancies = discrepancies;
	}
	
	@Override
	public TreeNode nextNode() {
		
		return null;
	}

	@Override
	public void postEvaluated(TreeNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TreeNode> stealNodes() {
		// TODO Auto-generated method stub
		return null;
	}
}
