package pls;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;


public class SaSolutionData extends SolutionData {
	public int cost;
	public int endSolOffset;
	public int endSolLen;
	public int bestSolOffset;
	public int bestSolLen;
	public BytesWritable solutionBytes;
	
	public SaSolutionData() {
	}
	
	public SaSolutionData(int cost, BytesWritable solutionBytes, int bestSolOffset, int bestSolLen, 
			int endSolOffset, int endSolLen) {
		this.cost = cost;
		this.solutionBytes = solutionBytes;
		this.bestSolOffset = bestSolOffset;
		this.bestSolLen = bestSolLen;
		this.endSolOffset = endSolOffset;
		this.endSolLen = endSolLen;
	}
	
	@Override
	public void init(BytesWritable bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes.getBytes());
		DataInputStream dis = new DataInputStream(bais);
		cost = dis.readInt();
		bestSolOffset = dis.readInt();
		bestSolLen = dis.readInt();
		endSolOffset = dis.readInt();
		endSolLen = dis.readInt();
		solutionBytes = bytes;
	}

	@Override
	public int getBestCost() {
		return cost;
	}

	@Override
	public BytesWritable getBestSolutionBytes() {
		BytesWritable val = new BytesWritable();
		val.set(solutionBytes.getBytes(), bestSolOffset, bestSolLen);
		return val;
	}

	@Override
	public BytesWritable getEndSolutionBytes() {
		BytesWritable val = new BytesWritable();
		val.set(solutionBytes.getBytes(), endSolOffset, endSolLen);
		return val;
	}
}