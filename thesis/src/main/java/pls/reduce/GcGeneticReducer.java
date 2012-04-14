package pls.reduce;

import gc.GcSolution;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

import pls.gc.GcPlsSolution;

public abstract class GcGeneticReducer extends MapReduceBase implements 
	Reducer<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {

	private static final Logger LOG = Logger.getLogger(GcGeneticReducer.class);
	
	@Override
	public void reduce(BytesWritable key, Iterator<BytesWritable> values,
			OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter) throws IOException {
		
		List<GcPlsSolution> sols = new ArrayList<GcPlsSolution>();
		while (values.hasNext()) {
			BytesWritable value = values.next();
			GcPlsSolution gcPlsSol = new GcPlsSolution();
			gcPlsSol.readFields(new DataInputStream(new ByteArrayInputStream(value.getBytes())));
			sols.add(gcPlsSol);
		}
		
		
	}
}