package pls;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface PlsSolution {
	/**
	 * Returns this
	 */
	public PlsSolution buildFromStream(DataInputStream dis) throws IOException;
	
	public void writeToStream(DataOutputStream dos) throws IOException;
	
	public int serializedSize();
	
	public double getCost();
	
	public int getSolutionId();
	
	public int getParentSolutionId();
	
	public void setParentSolutionId(int id);
}
