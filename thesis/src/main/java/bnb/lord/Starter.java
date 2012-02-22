package bnb.lord;

import java.util.LinkedList;
import java.util.List;

import bnb.Problem;
import bnb.BnbNode;

public class Starter {
	/**
	 * Breadth-first searches through the tree to create an initial partitioning
	 * of the problem.  Returns a list of nodes with size >= count.
	 * 
	 * @param spec
	 * @param bestCost
	 * @param root
	 * @param count
	 * @return
	 * 		two lists - the first is a set of evaluated nodes to distribute to processes,
	 * 		the second of which is the remaining set of unevaluated nodes
	 */
	public List<BnbNode> startEvaluation(Problem spec, double bestCost, BnbNode root, int count) {
		//TODO: what if we exhaust all the nodes during this part
		LinkedList<BnbNode> nodes = new LinkedList<BnbNode>();
		nodes.add(root);
		
		while (nodes.size() < count) {
			BnbNode node = nodes.removeFirst();
			node.evaluate(bestCost);
			//TODO: shouldn't care about recreating remaining children for tsp?
			while (node.hasNextChild()) {
				BnbNode child = node.nextChild(false);
				nodes.addLast(child);
			}
		}
		
		return nodes;
	}
}
