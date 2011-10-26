package bnb.vassal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import bnb.TreeNode;

public class SimpleVassalNodePool implements VassalNodePool {

	private final LinkedList<TreeNode> nodeList;
	
	public SimpleVassalNodePool() {
		nodeList = new LinkedList<TreeNode>();
	}
	
	@Override
	public synchronized List<TreeNode> stealNodes() {
		if (nodeList.isEmpty()) {
			return null;
		}
		return Arrays.asList(nodeList.removeFirst());
	}

	@Override
	public synchronized TreeNode nextNode() {
		//TODO: make sure it's LIFO
		while (nodeList.size() > 0) {
			TreeNode lastNode = nodeList.getLast();
			if (lastNode.hasNextChild())
				return lastNode.nextChild();
			else
				nodeList.removeLast();
		}
			
		return null;
	}

	@Override
	public synchronized void postEvaluated(TreeNode node) {
		nodeList.add(node);
	}	
}
