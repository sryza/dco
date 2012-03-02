package pls;

import java.util.Random;

import pls.tsp.TspLsCity;
import pls.tsp.TspSaRunner;
import viz.TspPanel;

public class VizSaRunner {
	private TspPanel panel;
	private TspSaRunner lsRunner;
	private TspLsCity[] cities;
	
	public VizSaRunner(TspLsCity[] cities, TspPanel panel) {
		this.cities = cities;
		this.panel = panel;
		lsRunner = new TspSaRunner(new Random(), new SaStats());
	}
	
	public void runStepAndDisplay() {
		TspLsCity[] newNodes = new TspLsCity[cities.length];

		int cost = lsRunner.runStep(cities, newNodes, 0.0, System.currentTimeMillis()+1000);
		System.out.println("Cost of step: " + cost);
		cities = newNodes;
		panel.setTour(newNodes, true);
	}
}
