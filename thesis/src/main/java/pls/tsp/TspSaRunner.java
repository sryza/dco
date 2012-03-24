package pls.tsp;

import static pls.tsp.TspUtils.wrap;

import java.util.Random;

import org.apache.log4j.Logger;

import pls.PlsRunner;
import pls.PlsSolution;
import pls.SaStats;

public class TspSaRunner implements PlsRunner {

	private static final Logger LOG = Logger.getLogger(TspSaRunner.class);
	
	private Random rand;
	
	/**
	 * Returns an array containing the best solution and the ending solution.
	 */
	@Override
	public TspSaSolution[] run(PlsSolution start, long timeMs, Random rand) {
		this.rand = rand;
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeMs;
		
		TspSaSolution best = (TspSaSolution)start;
		TspLsCity[] nodes1 = new TspLsCity[best.numCities()];
		System.arraycopy(best.getTour(), 0, nodes1, 0, best.numCities());
		TspLsCity[] nodes2 = new TspLsCity[best.numCities()];
		TspUtils.WRAP_NUM_NODES = nodes1.length;
		
		int i = 0;
		int totalCost = (int)best.getCost();
		
		double temperature = best.getTemperature();
		double scaler = best.getScaler();
		while (System.currentTimeMillis() < endTime) {

			int delta = runStep(nodes1, nodes2, temperature, endTime);
			TspSaSolution curSol = new TspSaSolution(new TspLsCity[best.numCities()], totalCost+delta, temperature, scaler);
			System.arraycopy(nodes2, 0, curSol.getTour(), 0, nodes1.length);
			temperature = temperature * scaler;
			
			if (delta == Integer.MAX_VALUE) {
				//no solution found in time
				LOG.info("No solution found in time");
				break;
			}
			
			totalCost += delta;
			if (totalCost < best.getCost()) {
				best = new TspSaSolution(new TspLsCity[best.numCities()], totalCost, temperature, scaler);
				System.arraycopy(nodes2, 0, best.getTour(), 0, nodes1.length);
				LOG.info("Found new best solution with cost " + totalCost);
//				System.out.println(best.verifyCost());
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
		//TODO: if we use a counter we could call System.currentTimeMillis() less
		while (System.currentTimeMillis() < end) {
			
			if (rand.nextDouble() < .5) { //try a random 2 opt move
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
