package pls.tsp;

public class TspLsCity {
	public int id;
	public int x;
	public int y;
	
	public TspLsCity(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public TspLsCity copy() {
		return new TspLsCity(id, x, y);
	}
}
