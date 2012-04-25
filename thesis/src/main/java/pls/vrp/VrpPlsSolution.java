package pls.vrp;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pls.PlsSolution;

public class VrpPlsSolution implements PlsSolution {
	private int maxIter;
	private int maxEscalation;
	private int relaxationRandomness;
	private int maxDiscrepancies;
	
	private int curIter;
	private int curEscalation;
	
	private int solId;
	private int parentSolId;
	
	private VrpSolution sol;
	
	private boolean readProblem = true;
	
	public VrpPlsSolution() {
	}
	
	public VrpPlsSolution(boolean readProblem) {
		this.readProblem = readProblem;
	}
	
	public VrpPlsSolution(VrpSolution sol, int maxIter, int maxEscalation, int relaxationRandomness, int maxDiscrepancies, 
			int solId, int parentSolId) {
		this.sol = sol;
		this.maxIter = maxIter;
		this.maxEscalation = maxEscalation;
		this.maxDiscrepancies = maxDiscrepancies;
		this.relaxationRandomness = relaxationRandomness;

		this.solId = solId;
		this.parentSolId = parentSolId;
	}
	
	public int getCurEscalation() {
		return curEscalation;
	}
	
	public void setCurEscalation(int curEscalation) {
		this.curEscalation = curEscalation;
	}
	
	public int getMaxEscalation() {
		return maxEscalation;
	}
	
	public int getMaxDiscrepancies() {
		return maxDiscrepancies;
	}
	
	public int getCurIteration() {
		return curIter;
	}
	
	public void setCurIteration(int curIter) {
		this.curIter = curIter;
	}
	
	public int getMaxIterations() {
		return maxIter;
	}
	
	public int getRelaxationRandomness() {
		return relaxationRandomness;
	}
	
	public VrpSolution getSolution() {
		return sol;
	}
	
	public void setSolution(VrpSolution sol) {
		this.sol = sol;
	}
	
	@Override
	public int getSolutionId() {
		return solId;
	}
	
	@Override
	public void setSolutionId(int id) {
		this.solId = id;
	}
	
	@Override
	public int getParentSolutionId() {
		return parentSolId;
	}
	
	@Override
	public void setParentSolutionId(int id) {
		this.parentSolId = id;
	}
	
	@Override
	public void write(DataOutput dos) throws IOException {
		dos.writeDouble(sol.getToursCost());
		dos.writeInt(solId);
		dos.writeInt(parentSolId);
		dos.writeShort(sol.getNumVehicles());
		for (List<Integer> route : sol.getRoutes()) {
			dos.writeShort(route.size());
			for (int custId : route) {
				dos.writeShort(custId);
			}
		}
		
		writeProblemToStream(sol.getProblem(), dos);
		
		dos.writeInt(maxIter);
		dos.writeInt(curIter);
		dos.writeInt(maxEscalation);
		dos.writeInt(curEscalation);
		dos.writeInt(relaxationRandomness);
		dos.writeInt(maxDiscrepancies);
	}
	
	@Override
	public void readFields(DataInput dis) throws IOException {
		double toursCost = dis.readDouble();
		solId = dis.readInt();
		parentSolId = dis.readInt();
		int numVehicles = dis.readShort();
		List<List<Integer>> routes = new ArrayList<List<Integer>>(numVehicles);
		for (int i = 0; i < numVehicles; i++) {
			int numCusts = dis.readShort();
			List<Integer> route = new ArrayList<Integer>(numCusts);
			routes.add(route);
			for (int j = 0; j < numCusts; j++) {
				route.add((int)dis.readShort());
			}
		}
		VrpProblem problem = null;
		if (readProblem) {
			problem = buildProblemFromStream(dis);
		}
		sol = new VrpSolution(routes, problem, toursCost);
		maxIter = dis.readInt();
		curIter = dis.readInt();
		maxEscalation = dis.readInt();
		curEscalation = dis.readInt();
		relaxationRandomness = dis.readInt();
		maxDiscrepancies = dis.readInt();
	}

	@Override
	public int serializedSize() {
		//TODO: this calculation is incorrect!
		return 4 //cost
			+ 4 //numVehicles
			+ sol.getNumVehicles() * 4 //route sizes
			+ sol.getProblem().getNumCities() * 4 //cust ids
			+ sol.getProblem().getNumCities() * 4 * 6 //attrs
			+ 4 * 4 //other problem attrs
			+ 4 * 8; //curIter, maxIter, etc.
	}

	@Override
	public double getCost() {
		return sol.getToursCost();
	}
	
	private VrpProblem buildProblemFromStream(DataInput dis) throws IOException {
		int numCities = dis.readShort();
		int depotX = dis.readShort();
		int depotY = dis.readShort();
		int vehicleCapacity = dis.readShort();
		int[] serviceTimes = new int[numCities];
		int[] demands = new int[numCities];
		int[] windowStartTimes = new int[numCities];
		int[] windowEndTimes = new int[numCities];
		int[] xCoors = new int[numCities];
		int[] yCoors = new int[numCities];
		for (int i = 0; i < numCities; i++) {
			demands[i] = dis.readShort();
			serviceTimes[i] = dis.readShort();
			windowStartTimes[i] = dis.readShort();
			windowEndTimes[i] = dis.readShort();
			xCoors[i] = dis.readShort();
			yCoors[i] = dis.readShort();
		}
		
		return new VrpProblem(demands, xCoors, yCoors, serviceTimes, windowStartTimes, windowEndTimes,
				depotX, depotY, vehicleCapacity);
	}
	
	public void writeProblemToStream(VrpProblem problem, DataOutput dos) throws IOException {
		dos.writeShort(problem.getDemands().length);
		dos.writeShort(problem.getDepotX());
		dos.writeShort(problem.getDepotY());
		dos.writeShort(problem.getVehicleCapacity());
		for (int i = 0; i < problem.getNumCities(); i++) {
			dos.writeShort(problem.getDemands()[i]);
			dos.writeShort(problem.getServiceTimes()[i]);
			dos.writeShort(problem.getWindowStartTimes()[i]);
			dos.writeShort(problem.getWindowEndTimes()[i]);
			dos.writeShort(problem.getXCoors()[i]);
			dos.writeShort(problem.getYCoors()[i]);
		}
	}
	
	
	public boolean equals(Object o) {
		VrpPlsSolution other = (VrpPlsSolution)o;
		VrpSolution otherSol = other.getSolution();
		if (!otherSol.getRoutes().equals(sol.getRoutes())) {
			return false;
		}
		return true;
	}
}
