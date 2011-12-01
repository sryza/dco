package bnb.tsp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import bnb.Problem;
import bnb.Solution;
import bnb.BnbNode;

public class TspNode extends BnbNode {
	private static final Logger LOG = Logger.getLogger(TspNode.class);
	
	private boolean isEvaluated;
	
	private boolean bounded;
	private int parentTourCost = -1;
	private int tourCost = -1;
	
	private int numChosen;
	private City city;
	
	private TspProblem problem;
	
	//children that are returned with nextChild are added to this list
	//TODO: take this into account when constructing a node
	private List<City> exploredChildren;
	
	private LinkedList<City> remainingCities;
	//true if the city remains
	private boolean[] remainingVector;
	
	private City startCity;
	
	//if parent is null, and this isn't the root, this shouldn't be null
	private ArrayList<City> prevCities;
	
	public TspNode() {
		super(null);
	}
	
	public TspNode(City startCity, City city, int numChosen, TspNode parent, LinkedList<City> remCities, 
			boolean[] remVector, TspProblem problem) {
		super(parent);
		this.startCity = startCity;
		this.city = city;
		this.remainingCities = remCities;
		this.remainingVector = remVector;
		
		this.numChosen = numChosen;
		if (parent != null) {
			parentTourCost = parent.getTourCost();
		}
		this.problem = problem;
	}
	
	/**
	 * Builds remainingVector for you
	 */
	public TspNode(City startCity, City city, int numChosen, TspNode parent, LinkedList<City> remCities, 
			TspProblem problem) {
		this(startCity, city, numChosen, parent, remCities, null, problem);
		if (remCities != null) {
			remainingVector = new boolean[problem.getNumCities()];
			for (City remCity : remCities) {
				remainingVector[remCity.id] = true;
			}
		}
	}
	
	@Override
	public boolean isEvaluated() {
		return isEvaluated;
	}
	
	@Override
	public boolean isLeaf() {
		return numChosen == problem.getNumCities();
	}
	
	@Override
	public boolean isSolution() {
		if (!isEvaluated) {
			throw new IllegalStateException("Tsp node not yet evaluated.");
		}
		return !bounded && numChosen == problem.getNumCities();
	}
	
	public int getTourCost() {
		return tourCost;
	}
	
	
//	private List<City> buildRemainingCities(Iterator<City> iter) {
//		//build a new remainingCities
//		for (City city : problem.getCities()) {
//			city.threadLocalMark.set(0);
//		}
//		while (iter.hasNext()) {
//			City usedCity = iter.next();
//			usedCity.threadLocalMark.set(1);
//		}
//		if (exploredChildren != null) {
//			for (City city : exploredChildren) {
//				city.threadLocalMark.set(2);
//			}
//		}
//		LinkedList<City> remCities = new LinkedList<City>();
//		for (City city : problem.getCities()) {
//			if (city.threadLocalMark.get() == 0) {
//				remCities.addFirst(city);
//			} else if (city.threadLocalMark.get() == 2) {
//				//explored children should get added to the back of the list
//				remCities.addLast(city);
//			}
//		}
//		
//		return remCities;
//	}
	
	public City getCity() {
		return city;
	}
	
	public ArrayList<City> getPrevCities() {
		return prevCities;
	}
	
	@Override
	public void evaluate(double bound) {

		if (!doEvaluate(bound)) {
			bounded = true;
//			System.out.println("bounded");
		}
		isEvaluated = true;
		exploredChildren = new LinkedList<City>();
	}
		
	/**
	 * 
	 * @return
	 * 		true if not bounded, false is yes bounded
	 */
	private boolean doEvaluate(double minCost) {
		if (isEvaluated) {
			LOG.warn("node about to be reevaluated");
		}
//		LOG.debug("about to evaluate " + this);
		
		if (numChosen == 2) {
			tourCost = 2 * startCity.dist(city);
		} else if (numChosen > 2) {
			Iterator<City> iter = new ParentCityIterator(this);
			iter.next();
			City prevCity = iter.next();
			tourCost = parentTourCost - prevCity.dist(startCity) + 
				prevCity.dist(city) + city.dist(startCity);
		}
		
		if (tourCost >= minCost) {
			return false;
		}
		
		if (numChosen <= 3)
			return true; // otherwise do opt stuff
			
		
		//if 2 or 3 opt give us better solutions, discard this one
		ParentCityIterator iter = new ParentCityIterator(this);
		iter.next();
		City cityPred = iter.next();
		City curSucc = iter.next(); // successor to current city
		while(iter.hasNext())
		{
			City temp = iter.next();
			if (TspUtils.cost2opt(temp, curSucc, city, cityPred) < 0)
			{
				//cost2opt gives something better, so discard
				return false;
			}
			curSucc = temp;
		}
		
//		for (int j = 1; j < numChosen-3; j++)
//		{
//			for (int k = 1; k < numChosen-3; k++)
//			{
//				if (j == k || (j+1) == k || (j-1) == k)
//					continue;
//				
//				if (TspUtils.cost3opt(cities, j, k, numChosen-2) < 0 || 
//					TspUtils.cost3opt(cities, k, j, numChosen-2) < 0 ||
//					TspUtils.cost3opt(cities, j, numChosen-2, k) < 0 ||
//					TspUtils.cost3opt(cities, numChosen-2, k, j) < 0 ||
//					TspUtils.cost3opt(cities, k, numChosen-2, j) < 0 ||
//					TspUtils.cost3opt(cities, numChosen-2, j, k) < 0)
//				{
//					return false;
//				}
//			}
//		}
		
		if (numChosen > 1 && numChosen < problem.getNumCities()) {
			int heldKarpBound = HeldAndKarp.bound(startCity, city, remainingVector, problem.getEdges(), minCost, 
					remainingCities, problem.getNumCities(), tourCost-city.dist(startCity));
			if (heldKarpBound >= minCost) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean hasNextChild() {
		//TODO: make sure we're incrementing numChildrenReleased whenever we call nextChild
		return !bounded && !isSolution() && numChosen + exploredChildren.size() < problem.getNumCities();
	}
	
	@Override
	public BnbNode nextChild() {
		if (!hasNextChild()) {
			throw new NoSuchElementException("Node has no next child.");
		}
		LinkedList<City> remCities = remainingCities;
		boolean[] remVec = remainingVector;
		int count = activeChildCount.getAndIncrement();
		if (count > 0) {
//			LOG.info("Splitting, activeChildCount before increment=" + count);
			remCities = new LinkedList<City>();
			remVec = new boolean[problem.getNumCities()];
			buildSinglePathStructures(remCities, remVec, new ParentCityIterator(this));
		}
		City city = remCities.remove(0);
		exploredChildren.add(city);
		remVec[city.id] = false;
		TspNode child = new TspNode(startCity, city, numChosen+1, this, remCities, remVec, problem);
		return child;
	}
	
	private void buildSinglePathStructures(LinkedList<City> remCities, boolean[] remVec, Iterator<City> iter) {
		//build a new remainingCities
		for (City city : problem.getCities()) {
			city.threadLocalMark.set(0);
		}
		while (iter.hasNext()) {
			City usedCity = iter.next();
			usedCity.threadLocalMark.set(1);
		}
		if (exploredChildren != null) {
			for (City city : exploredChildren) {
				city.threadLocalMark.set(2);
			}
		}
		for (City city : problem.getCities()) {
			if (city.threadLocalMark.get() == 0) {
				remCities.addFirst(city);
				remVec[city.id] = true;
			} else if (city.threadLocalMark.get() == 2) {
				//explored children should get added to the back of the list
				remCities.addLast(city);
				remVec[city.id] = true; //explore children are remaining too
			}
		}
	}
	
	@Override
	public void whenAllChildrenDone() {
		remainingCities.add(city);
		remainingVector[city.id] = true;
	}
	
	@Override
	public void initFromBytes(byte[] bytes, Problem prob) {
		if (!(prob instanceof TspProblem)) {
			throw new IllegalArgumentException("problem must be TspProblem");
		}
		problem = (TspProblem)prob;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bais);
			
			isEvaluated = dis.readBoolean();
			int numCities = dis.readInt();
			//TODO: how do we reconstruct this in the most efficient way?
			prevCities = new ArrayList<City>(numCities);
			for (int i = 0; i < numCities; i++) {
				prevCities.add(null);
			}
			City[] problemCities = problem.getCities();
			for (int i = numCities-1; i >= 0; i--) {
				int id = dis.readInt();
				prevCities.set(i, problemCities[id]);
				numChosen++;
			}
			
			//TODO: explored children
			//TODO: this can be an arraylist of the size we expect
			int numExploredChildren = dis.readInt();
			if (numExploredChildren != -1) {
				exploredChildren = new LinkedList<City>();
				for (int i = 0; i < numExploredChildren; i++) {
					int id = dis.readInt();
					exploredChildren.add(problemCities[id]);
				}
			}
			
			startCity = problemCities[0];
			remainingCities = new LinkedList<City>();
			remainingVector = new boolean[problem.getNumCities()];
			buildSinglePathStructures(remainingCities, remainingVector, prevCities.iterator());
			
			//remove what's set to city. we only kept it in as a shortcut to add it
			//to the list for buildSinglePathStructures
			city = prevCities.remove(prevCities.size()-1);
			
			
		} catch (IOException ex) {
			LOG.error("IOException reading from byte array, this should never happen", ex);
		}
	}
	
	@Override
	public byte[] toBytes() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			//TODO: check for off by ones
			
			//isEvaluated
			dos.writeBoolean(isEvaluated);
			
			//num nodes in path
			dos.writeInt(numChosen);

			//nodes in path
			Stack<City> stack = new Stack<City>();
			ParentCityIterator iter = new ParentCityIterator(this);
			while (iter.hasNext()) {
				stack.add(iter.next());
			}
			for (City city : stack) {
				dos.writeInt(city.id);
			}
			//num explored children
			if (exploredChildren != null) {
				dos.writeInt(exploredChildren.size());
				//explored children
				for (City child : exploredChildren) {
					dos.writeInt(child.id);
				}
			} else {
				dos.writeInt(-1);
			}
			
			return baos.toByteArray();
		} catch (IOException ex) {
			LOG.error("IOException writing to byte array, this should never happen", ex);
			return null;
		}
	}
	
	@Override
	public double getCost() {
		return tourCost;
	}
	
	@Override
	public Solution getSolution() {
		return new TspSolution(new ParentCityIterator(this), problem.getNumCities());
	}
	
	@Override
	public String toString() {
		Stack<Integer> stack = new Stack<Integer>();
		ParentCityIterator iter = new ParentCityIterator(this);
		while (iter.hasNext()) {
			stack.add(iter.next().id);
		}
		StringBuilder sb = new StringBuilder();
		while (!stack.isEmpty()) {
			sb.append(stack.pop() + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}
}