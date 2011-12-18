package vrpwtw;

/**
 * Because we don't reduce the number of routes in our CP, we don't worry about, number of routes
 * can be fixed in the problem.
 */
public class VrpProblem {
	//by route id
	private Route[] routes;
	private int capacity;
	
	public int getNumRoutes() {
		return routes.length;
	}
	
	public Route getRouteById(int routeId) {
		return routes[routeId];
	}
	
	public int getVehicleCapacity() {
		return capacity;
	}
}
