package pls.vrp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
			distsFromDepot[i] = (int)Math.round(Math.sqrt(xDiffFromDepot * xDiffFromDepot + yDiffFromDepot * yDiffFromDepot));
			
			for (int j = 0; j < demands.length; j++) {
				int xDiff = xCoors[i] - xCoors[j];
				int yDiff = yCoors[i] - yCoors[j];
				cityDists[i][j] = (int)Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));
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
	
	//if id's are negative, they refer to the depot
	public int getDistance(int custId1, int custId2) {
		if (custId1 >= 0 && custId2 >= 0) {
			return cityDists[custId1][custId2];
		} else if (custId1 >= 0) {
			return distsFromDepot[custId1];
		} else if (custId2 >= 0){
			return distsFromDepot[custId2];
		} else {
			return 0; //both depot
		}
	}
	
	public int getVehicleCapacity() {
		return vehicleCapacity;
	}
	
	public int getNumCities() {
		return demands.length;
	}
	
	public void toStream(DataOutputStream dos) throws IOException {
		dos.writeInt(demands.length);
		dos.writeInt(depotX);
		dos.writeInt(depotY);
		dos.writeInt(vehicleCapacity);
		for (int i = 0; i < demands.length; i++) {
			dos.writeInt(demands[i]);
			dos.writeInt(serviceTimes[i]);
			dos.writeInt(windowStartTimes[i]);
			dos.writeInt(windowEndTimes[i]);
			dos.writeInt(xCoors[i]);
			dos.writeInt(yCoors[i]);
		}
	}
	
	public static VrpProblem fromStream(DataInputStream dis) throws IOException {
		int numCities = dis.readInt();
		int depotX = dis.readInt();
		int depotY = dis.readInt();
		int vehicleCapacity = dis.readInt();
		int[] serviceTimes = new int[numCities];
		int[] demands = new int[numCities];
		int[] windowStartTimes = new int[numCities];
		int[] windowEndTimes = new int[numCities];
		int[] xCoors = new int[numCities];
		int[] yCoors = new int[numCities];
		for (int i = 0; i < numCities; i++) {
			demands[i] = dis.readInt();
			serviceTimes[i] = dis.readInt();
			windowStartTimes[i] = dis.readInt();
			windowEndTimes[i] = dis.readInt();
			xCoors[i] = dis.readInt();
			yCoors[i] = dis.readInt();
		}
		return new VrpProblem(demands, xCoors, yCoors, serviceTimes, windowStartTimes, windowEndTimes, depotX, depotY, 
				vehicleCapacity);
	}
}
