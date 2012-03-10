package pls.vrp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class LnsRelaxer {
	
	private int randomnessMeasure;
	private int maxDist; //used for normalizing distances for relatedness measure
	
	public LnsRelaxer(int randomnessMeasure, int maxDist) {
		this.randomnessMeasure = randomnessMeasure;
		this.maxDist = maxDist;
	}
	
	/**
	 * The input solution is not modified.
	 */
	public VrpSolution relaxShaw(VrpSolution sol, int numToRelax) {
		VrpProblem problem = sol.getProblem();
		ArrayList<Integer> removedCities = new ArrayList<Integer>(numToRelax);
		HashSet<Integer> remainingCities = new HashSet<Integer>();
		
		//choose first to remove
		int firstToRemove = (int)(Math.random() * problem.getNumCities());
		for (int i = 0; i < problem.getNumCities(); i++) {
			if (i == firstToRemove) {
				removedCities.add(i);
			} else {
				remainingCities.add(i);
			}
		}
		//remove the rest
		for (int i = 1; i < numToRelax; i++) {
			//take a random removed node
			int removedCityId = removedCities.get((int)(Math.random() * removedCities.size()));
			int rank = (randomnessMeasure == Integer.MAX_VALUE) ? 0 :
					(int)(Math.pow(Math.random(), randomnessMeasure) * remainingCities.size());
			int cityId = chooseByRankAndRelatedness(remainingCities, sol, rank, removedCityId);
			remainingCities.remove(cityId);
			removedCities.add(cityId);
		}
		//build the new solution
		HashSet<Integer> removedCitiesSet = new HashSet<Integer>(removedCities);
		List<List<Integer>> oldRoutes = sol.getRoutes();
		List<List<Integer>> newRoutes = new ArrayList<List<Integer>>(oldRoutes.size());
		for (List<Integer> oldRoute : oldRoutes) {
			List<Integer> newRoute = new ArrayList<Integer>();
			newRoutes.add(newRoute);
			for (Integer cityId : oldRoute) {
				if (!removedCitiesSet.contains(cityId)) {
					newRoute.add(cityId);
				}
			}
		}
		System.out.println(removedCities);
		System.out.println(newRoutes);

		
		return new VrpSolution(newRoutes, removedCities, problem);
	}
	
	/**
	 * @param cityId
	 * 		the id of the city that we're determining relatedness in relation to
	 */
	private int chooseByRankAndRelatedness(HashSet<Integer> remaining, VrpSolution sol, int rank, int cityId) {
		//the head is the least element, i.e. the one whose compareTo(any other element) is less than 0
		//we want the head of the queue to be the least related customer
		//thus, we want compareTo to return negative if the argument is more related than us
		PriorityQueue<CityRelatedness> heap = new PriorityQueue<CityRelatedness>(rank+1);
		for (int remainingCityId : remaining) {
			double relatedness = relatedness(cityId, remainingCityId, sol);
			if (heap.size() < rank + 1 || relatedness > heap.peek().relatedness) {
				if (heap.size() == rank + 1) {
					heap.remove();
				}
				heap.add(new CityRelatedness(remainingCityId, relatedness));
			}
		}
		return heap.peek().cityId;
	}
	
	private double relatedness(int nodeId1, int nodeId2, VrpSolution sol) {
		int dist = sol.getProblem().getDistances()[nodeId1][nodeId2];
		double denom = (double)dist / maxDist;
		if (sol.getCityVehicles()[nodeId1] == sol.getCityVehicles()[nodeId2]) {
			denom += 1.0;
		}
		return 1/denom;
	}
	
	private class CityRelatedness implements Comparable<CityRelatedness> {
		public int cityId;
		public double relatedness;

		public CityRelatedness(int cityId, double relatedness) {
			this.cityId = cityId;
			this.relatedness = relatedness;
		}
		
		@Override
		public int compareTo(CityRelatedness other) {
			return (int)Math.signum(this.relatedness - other.relatedness);
		}
	}
}
