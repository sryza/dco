package bnb.lord;

import java.util.LinkedList;
import java.util.List;

import bnb.Problem;
import bnb.BnbNode;

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
	public List<BnbNode>[] startEvaluation(Problem spec, double bestCost, BnbNode root, int count) {
		//BFS
		//TODO: what if we exhaust all the nodes during this part
		LinkedList<BnbNode> nodes = new LinkedList<BnbNode>();
		nodes.add(root);
		LinkedList<BnbNode> unevaluated = new LinkedList<BnbNode>();
		
		while (nodes.size() < count) {
			if (!unevaluated.isEmpty()) {
				BnbNode node = unevaluated.removeFirst();
				node.evaluate(bestCost);
				if (node.hasNextChild()) {
					nodes.addLast(node);
				}
			} else {
				BnbNode node = nodes.getFirst();
				unevaluated.add(node.nextChild());
			}
		}
		
		return new List[] {nodes, unevaluated};
	}
}
