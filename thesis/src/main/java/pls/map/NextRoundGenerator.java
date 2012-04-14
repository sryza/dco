package pls.map;

import java.util.List;

import org.apache.hadoop.io.BytesWritable;

public interface NextRoundGenerator {
	public List<BytesWritable> generateNextRoundInputs(List<BytesWritable> solBytes);
	
	/**
	 * True if we need to read in the problem.
	 */
	public boolean needsProblem();
	
	public void setProblem(BytesWritable problemBytes);
}
