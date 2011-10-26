package bnb.lord;

import java.util.LinkedList;
import java.util.List;

import bnb.ProblemSpec;
import bnb.TreeNode;

public class Starter {
	/**
	 * @param spec
	 * @param bestCost
	 * @param root
	 * @param count
	 * @return
	 * 		two lists - the first is a set of evaluated nodes to distribute to processes,
	 * 		the second of which is the remaining set of unevaluated nodes
	 */
	public List<TreeNode>[] startEvaluation(ProblemSpec spec, double bestCost, TreeNode root, int count) {
		//BFS
		//TODO: what if we exhaust all the nodes during this part
		LinkedList<TreeNode> nodes = new LinkedList<TreeNode>();
		nodes.add(root);
		LinkedList<TreeNode> unevaluated = new LinkedList<TreeNode>();
		
		while (nodes.size() < count) {
			if (!unevaluated.isEmpty()) {
				TreeNode node = unevaluated.removeFirst();
				node.evaluate(spec, bestCost);
				if (node.hasNextChild()) {
					nodes.addLast(node);
				}
			} else {
				TreeNode node = nodes.getFirst();
				unevaluated.add(node.nextChild());
			}
		}
		
		return new List[] {nodes, unevaluated};
	}
}
