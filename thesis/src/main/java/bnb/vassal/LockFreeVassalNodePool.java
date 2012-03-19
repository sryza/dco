//package bnb.vassal;
//
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import bnb.BnbNode;
//
//public class LockFreeVassalNodePool implements VassalNodePool {
//	private static final Logger LOG = Logger.getLogger(VassalNodePool.class);
//	
//	private final ConcurrentLinkedDeque<BnbNode> nodeList;
//	
//	public LockFreeVassalNodePool() {
//		nodeList = new ConcurrentLinkedDeque<BnbNode>();
//	}
//	
//	@Override
//	public synchronized List<BnbNode> stealNodes() {
//		if (nodeList.isEmpty() || nodeList.getFirst().isSolution() || 
//				(nodeList.getFirst().isEvaluated() && !nodeList.getFirst().hasNextChild()) ||
//				nodeList.getFirst().dontSteal()) {
//			return new LinkedList<BnbNode>();
//		}
//		return Collections.singletonList(nodeList.removeFirst());
//	}
//	
////	@Override
////	public synchronized List<BnbNode> stealNodes() {
////		if (nodeList.isEmpty() || nodeList.getFirst().isSolution() || 
////				(nodeList.getFirst().isEvaluated() && !nodeList.getFirst().hasNextChild()) ||
////				nodeList.getFirst().dontSteal()) {
////			return new LinkedList<BnbNode>();
////		}
////		BnbNode first = nodeList.getFirst();
////		BnbNode child = first.nextChild(true);
////		first.childDone();
////		
////		return Collections.singletonList(child);
////	}
//
//
//	@Override
//	public synchronized BnbNode nextNode() {
//		//TODO: make sure it's LIFO
//		while (nodeList.size() > 0) {
//			BnbNode lastNode = nodeList.getLast();
//			if (!lastNode.isEvaluated()) {
//				return lastNode;
//			}
//			if (lastNode.hasNextChild()) {
//				BnbNode child = lastNode.nextChild(false);
//				if (!lastNode.hasNextChild()) {
//					nodeList.removeLast();
//				}
//				return child;
//			} else {
//				nodeList.removeLast();
//			}
//		}
//			
//		return null;
//	}
//	
//	@Override
//	public synchronized boolean hasNextNode() {
//		return nodeList.size() > 0;
//	}
//
//	@Override
//	public synchronized void post(BnbNode node) {
//		nodeList.addLast(node);
//	}	
//}
