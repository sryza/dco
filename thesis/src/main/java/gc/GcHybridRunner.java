package gc;

import java.util.Arrays;
import java.util.Random;

public class GcHybridRunner {
	
	public void run(GcSolution[] population, GcTabuSearchRunner lsRunner, Random rand, GcProblem problem, int k, long lsTime,
			long finishTime) {
		GcBreeder breeder = new GcBreeder(rand);
		Arrays.sort(population);
		while (System.currentTimeMillis() < finishTime) {
			int parent1Index = rand.nextInt(population.length);
//			int parent1Index = chooseRandIndex(population.length, rand);

			int parent2Index = parent1Index;
			while (parent2Index == parent1Index) {
				parent2Index = rand.nextInt(population.length);
//				parent2Index = chooseRandIndex(population.length, rand);
			}
			
			GcSolution child = breeder.cross(population[parent1Index], population[parent2Index], problem.getNodeNeighbors(), k);
			child = lsRunner.run(problem, child, k, lsTime);
			if (child.getCost() == 0) {
				break;
			}
			
			//evict the worst solution
//			int worstSolCost = Integer.MIN_VALUE;
//			int worstSolIndex = -1;
//			for (int i = 0; i < popSize; i++) {
//				if (population[i].getCost() > worstSolCost) {
//					worstSolCost = population[i].getCost();
//					worstSolIndex = i;
//				}
//			}
			
//			if (child.getCost() <= population[population.length-1].getCost()) {
				population[population.length-1] = child;
				Arrays.sort(population);
//			}
			
//			population[worstSolIndex] = child;
		}
	}
	
	private static int chooseRandIndex(int size, Random rand) {
		double c = rand.nextDouble();
		double i = (1 + Math.sqrt(1 + 4 * (1-c)*size*(size+1))) / 2;
		return Math.min((int)i, size-1); //the min is a precaution against weird floating point stuff I don't know about
	}
}
