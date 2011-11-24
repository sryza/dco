package bnb.tsp;

/**
 * Tests a solution to make sure it's the cost that the solver reports.
 */
public class TestSolCost {
	public static void main(String[] args) {
		String solStr = 
			"(145, 215), (161, 242), (163, 247), (159, 261), (151, 264), (130, 254), (128, 252), (146, 246)";
		
		String[] cityStrs = solStr.replaceAll("[(]", "").split("[)],?\\s*");
		System.out.println("numCities: " + cityStrs.length);
		
		int cost = 0;
		City[] cities = new City[cityStrs.length];
		for (int i = 0; i < cities.length; i++) {
			String[] xy = cityStrs[i].split(",\\s*");
			cities[i] = new City(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), i);

			if (i > 0) {
				cost += cities[i-1].dist(cities[i]);
			}
		}
		cost += cities[0].dist(cities[cities.length-1]);
		System.out.println("cost: " + cost);
	}
}
