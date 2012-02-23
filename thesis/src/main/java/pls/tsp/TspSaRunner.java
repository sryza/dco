package pls.tsp;

import static pls.tsp.TspUtils.wrap;

import java.util.Random;

import org.apache.log4j.Logger;

import pls.SaRunner;
import pls.SaSolution;
import pls.SaStats;

public class TspSaRunner implements SaRunner {

	private static final Logger LOG = Logger.getLogger(SaRunner.class);
	
	private final SaStats stats;
	private TspSaSolution initialSolution;
	private final Random rand;
	
	public TspSaRunner(TspSaSolution initialSolution, Random rand, SaStats stats) {
		this.initialSolution = initialSolution;
		this.rand = rand;
		this.stats = stats;
	}
	
	@Override
	public SaSolution run(long timeMs, double temperature) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeMs;
		
		TspSaSolution best = initialSolution;
		TspLsCity[] nodes1 = new TspLsCity[best.numCities()];
		System.arraycopy(best.getTour(), 0, nodes1, 0, best.numCities());
		TspLsCity[] nodes2 = new TspLsCity[best.numCities()];
		
		int i = 0;
		int totalCost = best.getCost();
		while (System.currentTimeMillis() < endTime) {

			int delta = runStep(nodes1, nodes2, temperature, endTime);
			
			if (delta == Integer.MAX_VALUE) {
				//no solution found in time
				stats.reportNoSolutionFoundInTime();
				break;
			}
			
			totalCost += delta;
			if (totalCost < best.getCost()) {
				System.arraycopy(nodes2, 0, best.getTour(), 0, nodes1.length);
				best.setCost(totalCost);
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
		return best;
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
