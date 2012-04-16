package pls;

public interface PlsSolution extends WritableSolution {
	public int serializedSize();
	
	public double getCost();
	
	public int getSolutionId();
	
	public void setSolutionId(int id);
	
	public int getParentSolutionId();
	
	public void setParentSolutionId(int id);
}
