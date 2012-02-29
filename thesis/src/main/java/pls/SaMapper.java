package pls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

public class SaMapper extends MapReduceBase implements Mapper<Text, BytesWritable, Text, BytesWritable> {

	private static final int TIME = 60000;
	private final static Text THEKEY = new Text("rest");
	
	@Override
	public void map(Text key, BytesWritable value, OutputCollector<Text, BytesWritable> output, Reporter reporter)
		throws IOException {
		
		//pass metadata on through
		if (key.equals("metadata")) {
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
		Random rand = new Random();
		TspSaRunner runner = new TspSaRunner(sol, rand, stats);
		
		TspSaSolution bestSol = (TspSaSolution)runner.run(TIME, sol.getTemperature());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		bestSol.toStream(dos);
		output.collect(THEKEY, new BytesWritable(baos.toByteArray()));
	}
}
