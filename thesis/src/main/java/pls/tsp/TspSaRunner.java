package pls.tsp;

import static pls.tsp.TspUtils.wrap;

import java.util.Random;

import org.apache.log4j.Logger;

import pls.SaRunner;
import pls.SaStats;

public class TspSaRunner implements SaRunner<TspSaSolution> {

	private static final Logger LOG = Logger.getLogger(SaRunner.class);
	
	private final SaStats stats;
	private final Random rand;
	
	public TspSaRunner(Random rand, SaStats stats) {
		this.rand = rand;
		this.stats = stats;
	}
	
	@Override
	public TspSaSolution[] run(TspSaSolution start, long timeMs) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeMs;
		
		TspSaSolution best = start;
		TspLsCity[] nodes1 = new TspLsCity[best.numCities()];
		System.arraycopy(best.getTour(), 0, nodes1, 0, best.numCities());
		TspLsCity[] nodes2 = new TspLsCity[best.numCities()];
		
		int i = 0;
		int totalCost = best.getCost();
		double temperature = start.getTemperature();
		double scaler = start.getScaler();
		while (System.currentTimeMillis() < endTime) {

			int delta = runStep(nodes1, nodes2, temperature, endTime);
			temperature = temperature * scaler;
			
			if (delta == Integer.MAX_VALUE) {
				//no solution found in time
				stats.reportNoSolutionFoundInTime();
				break;
			}
			
			totalCost += delta;
			if (totalCost < best.getCost()) {
				best = new TspSaSolution(new TspLsCity[best.numCities()], totalCost, temperature, scaler);
				System.arraycopy(nodes2, 0, best.getTour(), 0, nodes1.length);
				stats.reportNewBestSolution(totalCost);
				LOG.info("Solution verified " + best.verifyCost());
			}
			
			TspLsCity[] temp = nodes2;
			nodes2 = nodes1;
			nodes1 = temp;
			
//			System.out.println ("temp: " + _temp);
//			System.out.println ("iter: " + i + "\t" + totalImproving + "\t" + totalTempChosen);
//			System.out.println ("tourDist: " + Utils.tourDist(nodes1));
			i++;
		}
		
		TspSaSolution endSol = new TspSaSolution(nodes1, totalCost, temperature, scaler);
		return new TspSaSolution[] {best, endSol};
	}
	
	public int runStep(TspLsCity[] nodes, TspLsCity[] newNodes, double temp, long end) {
		//TODO: if we use a counter for every second we could 
		while (System.currentTimeMillis() < end) {
			
			if (Math.random() < .5) { //try a random 2 opt move
				int i = rand.nextInt(nodes.length);
				int j = rand.nextInt(nodes.length);
				
				if (j == i || wrap(j+1) == i || wrap(j-1) == i) {
					continue;
				}
				
				int delta = TspLsUtils.cost2opt(nodes, i, j);
				if (delta < 0) {
					TspLsUtils.swap2opt(nodes, newNodes, i, j);
					return delta;
				}
				
				if (temp != 0 && rand.nextDouble() < Math.exp(-(delta+1)/temp)) {
					TspLsUtils.swap2opt(nodes, newNodes, i, j);
					return delta;
				}
			} else { //try a random 3 opt move
				int i = rand.nextInt(nodes.length);
				int j = rand.nextInt(nodes.length);
				int k = rand.nextInt(nodes.length);
				
				if (j == i || wrap(j+1) == i || wrap(j-1) == i || j == k || wrap(j+1) == k || wrap(j-1) == k || i == k) {
					continue;
				}
				
				int delta = TspLsUtils.cost3opt(nodes, i, j, k);
				if (delta < 0) {
					TspLsUtils.swap3opt(nodes, newNodes, i, j, k);
					return delta;
				}
				
				if (temp != 0 && rand.nextDouble() < Math.exp(-(delta+1)/temp)) {
					TspLsUtils.swap3opt(nodes, newNodes, i, j, k);
					return delta;
				}
			}
		}

//		System.out.println ("hit max iterations");
		return Integer.MAX_VALUE;
	}

}
