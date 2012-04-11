package pls;

import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;

public abstract class SolutionData implements Comparable<SolutionData> {
	
	public abstract void init(BytesWritable bytes) throws IOException;
	
	public abstract double getBestCost();
	
	public abstract BytesWritable getBestSolutionBytes();
	
	public abstract BytesWritable getEndSolutionBytes();

	public abstract Writable getExtraData();
	
	public int compareTo(SolutionData other) {
		//right because this returns positive if other is smaller,
		//which ensures the max heap that we want
		return (int)Math.signum(other.getBestCost() - this.getBestCost());
	}
}
