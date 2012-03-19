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
import org.apache.log4j.Logger;

import pls.tsp.TspSaRunner;
import pls.tsp.TspSaSolution;

public class SaMapper extends MapReduceBase implements Mapper<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {

	private static final Logger LOG = Logger.getLogger(SaMapper.class);
	
	private static final int TIME = 60000;
	
	@Override
	public void map(BytesWritable key, BytesWritable value, OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter)
		throws IOException {
		
		LOG.info("Received map input with key \"" + new String(key.getBytes()) + "\"");
		
		//pass metadata on through
		if (key.equals(PlsUtil.METADATA_KEY)) {
			output.collect(key, value);
			LOG.info("Passing on metadata");
			return;
		}
		
		byte[] input = value.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		DataInputStream dis = new DataInputStream(bais);
		TspSaSolution sol = null;
		try {
			sol = TspSaSolution.fromStream(dis);
		} catch (IOException ex) {
			LOG.error("Failed to read initial solution, aborting", ex);
			return;
		}
		
		LOG.info("Initial solution cost: " + sol.getCost());
		
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
		output.collect(PlsUtil.SOLS_KEY, new BytesWritable(baos.toByteArray()));
	}
}
