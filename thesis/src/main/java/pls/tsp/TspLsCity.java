package pls.tsp;

import tsp.TspCity;

public class TspLsCity extends TspCity {
	public TspLsCity(int id, int x, int y) {
		super(id, x, y);
	}
	
	public TspLsCity copy() {
		return new TspLsCity(id, x, y);
	}
}
