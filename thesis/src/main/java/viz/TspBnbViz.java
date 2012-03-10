package viz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import bnb.BnbNode;
import bnb.tsp.City;
import bnb.tsp.ParentCityIterator;
import bnb.tsp.TspNode;
import bnb.tsp.TspProblem;
import bnb.tsp.TspSolution;
import bnb.tsp.run.ProblemGen;
import bnb.vassal.SimpleVassalNodePool;

public class TspBnbViz {
	public static void main(String[] args) throws IOException {
		final int bestCost = 400;
		
		//initialize tsp
		City[] cities = ProblemGen.genCities(40);
		
		//shuffle
		List<City> cityList = Arrays.asList(cities);
		Collections.shuffle(cityList);
		cities = cityList.toArray(new City[0]);
		
		TspProblem problem = new TspProblem(cities);
		problem.makeEdges();
		LinkedList<City> remainingCities = new LinkedList<City>();
		remainingCities.addAll(Arrays.asList(cities).subList(1, cities.length));
		TspNode rootNode = new TspNode(cities[0], cities[0], 1, null, remainingCities, null, -1, problem);
		
		//initalize display
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TspPanel panel = new TspPanel();
		panel.setScale(cities);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		SimpleVassalNodePool nodePool = new SimpleVassalNodePool();
//		Starter starter = new Starter();
		//used to be using totalSlots for the last arg, but not for now
//		List<BnbNode> startNodes = starter.startEvaluation(problem, bestCost, rootNode, 1);
//		for (BnbNode startNode : startNodes) {
//			if (!startNode.isEvaluated()) {
//				startNode.evaluate(bestCost);
//			}
//			nodePool.post(startNode);
//		}
		nodePool.post(rootNode);
		run(nodePool, bestCost, panel);
		System.out.println("done");
		panel.clearCurTour();
	}
	
	private static void run(SimpleVassalNodePool nodePool, int minCost, TspPanel panel) {
		int numIters = -1;
		while (true) {
			numIters++;
			BnbNode node = nodePool.nextNode();
			if (node == null) {
				break;
			} else {
				node.evaluate(minCost);
				if (numIters % 10 == 0) {
					display((TspNode)node, panel, false);
				}

//				if (numEvaluated % EVALUATED_LOG_INTERVAL == 0) {
//					LOG.info("evaluated " + numEvaluated + " nodes");
//				}
				if (node.isSolution()) {
					System.out.println("found solution with cost " + node.getCost());
					if (node.getCost() < minCost) {
						System.out.println("new best cost: " + node.getCost());
						System.out.println("new best solution: " + node.getSolution());
						TspSolution solution = (TspSolution)node.getSolution();
						panel.setBest(solution.getCities());
						minCost = (int)node.getCost();
//						jobManager.betterLocalSolution(node.getSolution(), node.getCost());
						//TODO: mark as done
						
						node.whenAllChildrenDone();
						node.getParent().childDone();
					}
				} else {
					if (!node.isLeaf()) {
						nodePool.post(node);
					} else {
						//if we're not posting the node to do work with, let its parent
						//know that we're done doing computation on it
						node.whenAllChildrenDone();
						if (node.getParent() != null) {
							node.getParent().childDone();
						}
					}
				}
			}
		}
	}
	
	
	private static void display(TspNode node, TspPanel panel, boolean best) {
		ParentCityIterator iter = new ParentCityIterator(node);
		ArrayList<City> cities = new ArrayList<City>(node.getDepth()+1);
		while (iter.hasNext()) {
			City city = iter.next();
			cities.add(city);
		}
		City[] citiesArr = cities.toArray(new City[cities.size()]);
		if (!best) {
			panel.setTour(citiesArr, false);
		} else {
			panel.setBest(citiesArr);
		}
	}
}
