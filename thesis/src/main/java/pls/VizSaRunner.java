package pls;

import java.util.Random;

import pls.tsp.TspLsCity;
import pls.tsp.TspSaRunner;
import pls.tsp.TspUtils;
import viz.TspPanel;

public class VizSaRunner {
	private TspPanel panel;
	private TspSaRunner lsRunner;
	private TspLsCity[] cities;
	private int tourCost;
	
	public VizSaRunner(TspLsCity[] cities, TspPanel panel) {
		this.cities = cities;
		tourCost = TspUtils.tourDist(cities);
		this.panel = panel;
		lsRunner = new TspSaRunner(new Random(), new SaStats());
	}
	
	public void runStepAndDisplay(double temp) {
		TspLsCity[] newNodes = new TspLsCity[cities.length];
		
		int cost = lsRunner.runStep(cities, newNodes, temp, System.currentTimeMillis()+1000);
		if (cost != Integer.MAX_VALUE) {
			cities = newNodes;
			tourCost += cost;
		}
		System.out.println("Cost: " + tourCost + " (" + cost + ") | Temp: " + temp);

		panel.setTour(newNodes, true);
	}
}
