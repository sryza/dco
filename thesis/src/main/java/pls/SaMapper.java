package pls;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;

import pls.tsp.TspSaRunner;
import pls.tsp.TspSaSolution;

public class SaMapper extends Mapper<BytesWritable, IntWritable, BytesWritable, IntWritable> {

	@Override
	public void map(BytesWritable key, IntWritable value, Context context) {
		byte[] input = key.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		DataInputStream dis = new DataInputStream(bais);
		int time = -1;
		double temp = -1.0;
		TspSaSolution sol = null;
		try {
			time = dis.readInt();
			temp = dis.readDouble();
			sol = TspSaSolution.fromStream(dis);
		} catch (IOException ex) {
		}
		
		SaStats stats = new SaStats();
		Random rand = new Random();
		TspSaRunner runner = new TspSaRunner(sol, rand, stats);
		runner.run(time, temp);
		
		
	}
}
