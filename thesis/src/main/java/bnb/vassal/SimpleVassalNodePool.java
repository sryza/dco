package bnb.vassal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import bnb.BnbNode;

public class SimpleVassalNodePool implements VassalNodePool {
	private static final Logger LOG = Logger.getLogger(VassalNodePool.class);
	
	private final LinkedList<BnbNode> nodeList;
	
	public SimpleVassalNodePool() {
		nodeList = new LinkedList<BnbNode>();
	}
	
	@Override
	public synchronized List<BnbNode> stealNodes() {
		if (nodeList.isEmpty() || nodeList.getFirst().isSolution() || 
				(nodeList.getFirst().isEvaluated() && !nodeList.getFirst().hasNextChild()) ||
				nodeList.getFirst().dontSteal()) {
			return new LinkedList<BnbNode>();
		}
		return Arrays.asList(nodeList.removeFirst());
	}

	@Override
	public synchronized BnbNode nextNode() {
		//TODO: make sure it's LIFO
		while (nodeList.size() > 0) {
			BnbNode lastNode = nodeList.getLast();
			if (!lastNode.isEvaluated()) {
				return lastNode;
			}
			if (lastNode.hasNextChild())
				return lastNode.nextChild();
			else
				nodeList.removeLast();
		}
			
		return null;
	}

	@Override
	public synchronized void post(BnbNode node) {
		nodeList.addLast(node);
	}	
}
