package pls.gc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class GcPlsParams implements Writable {
	private int popSize;
	private int roundTime;
	private int A;
	private double alpha;
	private long finishByTime;
	private boolean startWithLs;
	private int lsTime;
	
	public GcPlsParams() {
	}
	
	public GcPlsParams(int popSize, int roundTime, int lsTime, int A, double alpha, long finishByTime, boolean startWithLs) {
		this.popSize = popSize;
		this.roundTime = roundTime;
		this.lsTime = lsTime;
		this.A = A;
		this.alpha = alpha;
		this.finishByTime = finishByTime;
		this.startWithLs = startWithLs;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		popSize = input.readInt();
		roundTime = input.readInt();
		A = input.readInt();
		alpha = input.readDouble();
		finishByTime = input.readLong();
		startWithLs = input.readBoolean();
		lsTime = input.readInt();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(popSize);
		output.writeInt(roundTime);
		output.writeInt(A);
		output.writeDouble(alpha);
		output.writeLong(finishByTime);
		output.writeBoolean(startWithLs);
		output.writeInt(lsTime);
	}
	
	public int getPopulationSize() {
		return popSize;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public int getA() {
		return A;
	}
	
	public int getRoundTime() {
		return roundTime;
	}
	
	public long getFinishByTime() {
		return finishByTime;
	}
	
	public void setFinishByTime(long finishByTime) {
		this.finishByTime = finishByTime;
	}
	
	public boolean getStartWithLs() {
		return startWithLs;
	}
	
	public void setStartWithLs(boolean startWithLs) {
		this.startWithLs = startWithLs;
	}
	
	public int getLsTime() {
		return lsTime;
	}
}
