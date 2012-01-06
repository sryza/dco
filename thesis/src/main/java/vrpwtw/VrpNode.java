package vrpwtw;

import java.util.List;
import java.util.ListIterator;

import bnb.Problem;
import bnb.Solution;
import bnb.BnbNode;

/**
 * We're exploring different permutatons with a bunch of the original cities set stable.
 */
public class VrpNode extends BnbNode {
	
	private VrpProblem problem;
	
	private int[] routeFilledAmounts; //organized by route id
	
	private VrpBookkeeping bookkeeping;
	
	private InsertionListsNode insertion; 
	
	private InsertionListsNode nextChildInsertion;
	
	public VrpNode(BnbNode parent, Customer custToInsert, RouteNode insertionPoint) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This is where we want to do the bulk of the checking against the constraints.
	 * We can also later move it to nextChild, which might make more sense because we'd be able to
	 * create far fewer nodes.  They don't actually stack up, but the cost of creating them
	 * and putting them into the data structures could be worse, and there's also the risk of
	 * stealing nodes that are impotent.
	 */
	@Override
	public void evaluate(double bound) {
		// TODO Auto-generated method stub
		bookkeeping.insert(insertion, routeStart);
		
	}

	@Override
	public boolean isEvaluated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BnbNode nextChild() {
		//here we don't actually make any changes to the data structures, we leave
		//that to the evaluate method. that way, we don't have to undo anything before forking.
			//not exactly true, because stuff will be modified down the line
		
		//TODO: make it so that propagation can make us insert (and be able to uninsert) multiple children
		// for a single node
		
		//find customer whose cheapest insertion point is maximum
		if (nextChildInsertion == null) {
			InsertionList custList = bookkeeping.getMaxCheapestInsertionCustomer();
			nextChildInsertion = custList.getHead();
		} else {
			nextChildInsertion = nextChildInsertion.nextInCustList;
		}
		
		
		//TODO: what if there are none?
		
		new VrpNode(nextChildInsertion);
		
		
		// TODO: select depot whose best insertion degrades the objective function the most
		// does this mean that we have to try inserting every node in every place?
		
		// look through every insertion point, test it for capacity
		// we should be able to rule out entire routes at once, so if we're able to store them
		// together it would be better
		
		// each route can be a linked list, and we have a pointer into the route
		// if we consider removing the beginning of that route, we would use the next pointer
		// doing it as a linked list, we should also be able to see whether our time is up as well
		// we should be able to do something clever with knowing whether we're pushing depots that come in the
		// path after an inserted node ahead of the times we're allowed to visit them at

		//this should be set by the heuristic somehow, now we apply our CP to it
	}
	
	@Override
	public void whenAllChildrenDone() {
		//TODO: uninsert the inserted customer(s?)
		bookkeeping.uninsert(insertion);
		
		//TODO: insert the insertion back into the customer's insertion list
		insertion.addToLists(custList, pointList);
	}

	@Override
	public boolean hasNextChild() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSolution() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Solution getSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initFromBytes(byte[] bytes, Problem problem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dontSteal() {
		// TODO Auto-generated method stub
		return false;
	}

}
