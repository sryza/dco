package pls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import pls.tsp.TspSaRunner;
import pls.tsp.TspSaSolution;

public class SaMapper extends MapReduceBase implements Mapper<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {

	private static final int TIME = 60000;
	private static final BytesWritable THEKEY = new BytesWritable("rest".getBytes());
	
	@Override
	public void map(BytesWritable key, BytesWritable value, OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter)
		throws IOException {
		
		//pass metadata on through
		if (new String(key.getBytes()).equals("metadata")) {
			output.collect(key, value);
			return;
		}
		
		byte[] input = value.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		DataInputStream dis = new DataInputStream(bais);
		TspSaSolution sol = null;
		try {
			sol = TspSaSolution.fromStream(dis);
		} catch (IOException ex) {
		}
		
		SaStats stats = new SaStats();
		//use host name to add some randomness in case multiple mappers are started at the same time
		Random rand = new Random(System.currentTimeMillis() + InetAddress.getLocalHost().getHostName().hashCode());
		TspSaRunner runner = new TspSaRunner(rand, stats);
		
		TspSaSolution[] solutions = runner.run(sol, TIME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		//write out metadata before solutions
		int bestSolLen = solutions[0].serializedSize();
		int endSolLen = solutions[1].serializedSize();
		int bestSolOffset = 4 * 5;
		int endSolOffset = bestSolOffset + bestSolLen;
		dos.writeInt(solutions[0].getCost());
		dos.writeInt(bestSolOffset);
		dos.writeInt(bestSolLen);
		dos.writeInt(endSolOffset);
		dos.writeInt(endSolLen);

		solutions[0].toStream(dos);
		solutions[1].toStream(dos);
		output.collect(THEKEY, new BytesWritable(baos.toByteArray()));
	}
}
