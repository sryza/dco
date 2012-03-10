package pls.vrp;

import java.io.DataOutputStream;

public class VrpProblem {
	private int vehicleCapacity;
	private int[] demands;
	private int[] serviceTimes;
	private int[] windowStartTimes;
	private int[] windowEndTimes;
	private int[][] cityDists;
	private int[] distsFromDepot;
	
	private int depotX;
	private int depotY;
	private int[] xCoors;
	private int[] yCoors;
	
	private int maxDist;
	
	public VrpProblem(int[] demands, int[] xCoors, int[] yCoors, int[] serviceTimes, int[] windowStartTimes, int[] windowEndTimes,
			int depotX, int depotY, int capacity) {
		this.demands = demands;
		this.serviceTimes = serviceTimes;
		this.windowStartTimes = windowStartTimes;
		this.windowEndTimes = windowEndTimes;
		this.vehicleCapacity = capacity;
		this.xCoors = xCoors;
		this.yCoors = yCoors;
		this.depotX = depotX;
		this.depotY = depotY;
		
		cityDists = new int[demands.length][demands.length];
		distsFromDepot = new int[demands.length];
		for (int i = 0; i < demands.length; i++) {
			int xDiffFromDepot = xCoors[i] - depotX;
			int yDiffFromDepot = yCoors[i] - depotY;
			distsFromDepot[i] = (int)Math.sqrt(xDiffFromDepot * xDiffFromDepot + yDiffFromDepot * yDiffFromDepot);
			
			for (int j = 0; j < demands.length; j++) {
				int xDiff = xCoors[i] - xCoors[j];
				int yDiff = yCoors[i] - yCoors[j];
				cityDists[i][j] = (int)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
				if (cityDists[i][j] > maxDist) {
					maxDist = cityDists[i][j];
				}
			}
		}
	}
	
	public int getDepotX() {
		return depotX;
	}
	
	public int getDepotY() {
		return depotY;
	}
	
	public int[] getXCoors() {
		return xCoors;
	}
	
	public int[] getYCoors() {
		return yCoors;
	}
	
	public int getMaxDistance() {
		return maxDist;
	}
	
	public int[] getDistancesFromDepot() {
		return distsFromDepot;
	}
	
	public int[] getServiceTimes() {
		return serviceTimes;
	}
	
	public int[][] getDistances() {
		return cityDists;
	}
	
	public int[] getWindowStartTimes() {
		return windowStartTimes;
	}
	
	public int[] getWindowEndTimes() {
		return windowEndTimes;
	}
	
	public int[] getDemands() {
		return demands;
	}
	
	public int getVehicleCapacity() {
		return vehicleCapacity;
	}
	
	public int getNumCities() {
		return demands.length;
	}
	
	public void toStream(DataOutputStream dos) {
		
	}
}
