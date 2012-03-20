package pls;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;

public class LnsSolutionData extends SolutionData {
	private int cost;
	private BytesWritable bytes;
	
	public LnsSolutionData(){
	}
	
	public LnsSolutionData(int cost, BytesWritable bytes) {
		this.cost = cost;
		this.bytes = bytes;
	}
	
	@Override
	public int getBestCost() {
		return cost;
	}

	@Override
	public void init(BytesWritable bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes.getBytes());
		DataInputStream dis = new DataInputStream(bais);
		cost = dis.readInt();
		this.bytes = bytes;
	}
	
	@Override
	public BytesWritable getBestSolutionBytes() {
		return bytes;
	}
	
	@Override
	public BytesWritable getEndSolutionBytes() {
		return bytes;
	}
}
