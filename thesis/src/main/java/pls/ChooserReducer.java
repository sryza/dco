package pls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

import pls.tsp.TspSaSolution;

public class ChooserReducer extends MapReduceBase implements Reducer<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {
	
	private static final Logger LOG = Logger.getLogger(ChooserReducer.class);
	
	private int k = -1;
	private int bestCostAlways = -1;
	
	/**
	 * The reduce inputs can take two forms:
	 * "best"->two BytesWritables
	 * 		this is the global best solution being passed through
	 * 		the first BytesWritable contains metadata like k and the global best cost
	 * 		the second BytesWritable is the serialized global best solution
	 * "rest"->many BytesWritables
	 * 		these are the solutions found in by each of the jobs
	 * 		each BytesWritable contains
	 * 			any metadata (cost of best solution of run, cost of ending solution of run)
	 * 			the ending solution of the run
	 * 				containing T
	 * 			the best solution found in the run
	 * 				containing T
	 */
	@Override
	public void reduce(BytesWritable key, Iterator<BytesWritable> values,
			OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter)
			throws IOException {
		
		if (key.equals(PlsUtil.METADATA_KEY)) {
			//first value is info regarding the problem and best solution
			BytesWritable auxInfo = values.next();
			ByteArrayInputStream bais = new ByteArrayInputStream(auxInfo.getBytes());
			DataInputStream dis = new DataInputStream(bais);
			k = dis.readInt();
			bestCostAlways = dis.readInt();
			LOG.info("Received metadata: k=" + k + ", bestCostAlways=" + bestCostAlways);
			return;
		} else if (k == -1) {
			LOG.info("Received solutions before metadata, aborting");
			return;
			//log something here
			//output some sort of error code
		}
		
		//find the best solution
		int bestCostThisRound = Integer.MAX_VALUE;
		SolutionData bestSolThisRound = null;
		ArrayList<SolutionData> solsThisRound = new ArrayList<SolutionData>();
		while (values.hasNext()) {
			BytesWritable bytes = values.next();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes.getBytes());
			DataInputStream dis = new DataInputStream(bais);
			int runsBestCost = dis.readInt();
			int bestSolOffset = dis.readInt();
			int bestSolLen = dis.readInt();
			int endSolOffset = dis.readInt();
			int endSolLen = dis.readInt();
			SolutionData solData = new SolutionData(runsBestCost, bytes, bestSolOffset, bestSolLen, endSolOffset, endSolLen);
			
			if (runsBestCost < bestCostThisRound) {
				bestCostThisRound = runsBestCost;
				bestSolThisRound = solData;
				solsThisRound.add(solData);
			}
		}
		
		//prepare inputs to next round
		
		//TODO: do the temperatures
		if (bestCostThisRound < bestCostAlways) {
			bestCostAlways = bestCostThisRound; //for passing on
			int nMappers = solsThisRound.size();
			//choose best k solutions
			solsThisRound = chooseKBest(solsThisRound, k);
			BytesWritable val;
			//first write out the k best
			for (SolutionData solution : solsThisRound) {
				val = new BytesWritable();
				val.set(solution.solutionBytes.getBytes(), solution.endSolOffset, solution.endSolLen);
				output.collect(PlsUtil.SOLS_KEY, val);
			}
			
			//then write out the rest of the bestSolutionAlways as many times as the difference
			int nBest = nMappers - k;
			val = new BytesWritable();
			val.set(bestSolThisRound.solutionBytes.getBytes(), bestSolThisRound.bestSolOffset, bestSolThisRound.bestSolLen);
			for (int i = 0; i < nBest; i++) {
				output.collect(PlsUtil.SOLS_KEY, val);
			}
		} else { //just continue with what we've got
			for (SolutionData solution : solsThisRound) {
				//TODO: should we need to copy here?
				BytesWritable val = new BytesWritable();
				val.set(solution.solutionBytes.getBytes(), solution.endSolOffset, solution.endSolLen);
				output.collect(PlsUtil.SOLS_KEY, val);
			}
		}
		
		//write out the metadata key
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(k);
		dos.writeInt(bestCostAlways);
		BytesWritable metadata = new BytesWritable(baos.toByteArray());
		output.collect(PlsUtil.METADATA_KEY, metadata);
	}
	
	/**
	 * Returns in order of highest cost first.
	 */
	protected ArrayList<SolutionData> chooseKBest(ArrayList<SolutionData> solDatas, int k) {
		PriorityQueue<SolutionData> maxHeap = new PriorityQueue<SolutionData>();
		for (SolutionData solData : solDatas) {
			if (maxHeap.size() < k || solData.cost < maxHeap.peek().cost) {
				if (maxHeap.size() >= k) {
					maxHeap.remove();
				}
				maxHeap.add(solData);
			}
		}
		
		ArrayList<SolutionData> kbest = new ArrayList<SolutionData>();
		while (!maxHeap.isEmpty()) {
			kbest.add(maxHeap.remove());
		}
		
		return kbest;
	}
	
	protected static class SolutionData implements Comparable<SolutionData> {
		public final int cost;
		public final int endSolOffset;
		public final int endSolLen;
		public final int bestSolOffset;
		public final int bestSolLen;
		public final BytesWritable solutionBytes;
		
		public SolutionData(int cost, BytesWritable solutionBytes, int bestSolOffset, int bestSolLen, 
				int endSolOffset, int endSolLen) {
			this.cost = cost;
			this.solutionBytes = solutionBytes;
			this.bestSolOffset = bestSolOffset;
			this.bestSolLen = bestSolLen;
			this.endSolOffset = endSolOffset;
			this.endSolLen = endSolLen;
		}
		
		@Override
		public int compareTo(SolutionData other) {
			//right because this returns positive if other is smaller,
			//which ensures the max heap that we want
			return other.cost - this.cost;
		}
	}
}
