package pls.gc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class GcReducer extends MapReduceBase implements Reducer<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {

	@Override
	public void reduce(BytesWritable key, Iterator<BytesWritable> values,
			OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter)
			throws IOException {
		
		List<GcPlsSolution> sols = new ArrayList<GcPlsSolution>();
		GcPlsParams params = new GcPlsParams();
		GcPlsProblem problem = new GcPlsProblem();
		
		int numVals = 0;
		while (values.hasNext()) {
			BytesWritable val = values.next();
			ByteArrayInputStream bais = new ByteArrayInputStream(val.getBytes());
			DataInputStream dis = new DataInputStream(bais);
			problem.readFields(dis);
			params.readFields(dis);
			for (int i = 0; i < params.getPopulationSize(); i++) {
				GcPlsSolution plsSol = new GcPlsSolution();
				plsSol.readFields(dis);
				sols.add(plsSol);
			}
			numVals++;
		}
		
		if (params.getReduceShuffle()) {
			Collections.shuffle(sols);
		}
		
		params.setFinishByTime(System.currentTimeMillis() + params.getRoundTime());
		params.setStartWithLs(false);
		
		Iterator<GcPlsSolution> iter = sols.iterator();
		int numSols = sols.size() / numVals;
		for (int i = 0; i < numVals; i++) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			problem.write(dos);
			params.write(dos);
			for (int j = 0; j < numSols ; j++) {
				GcPlsSolution sol = iter.next();
				sol.write(dos);
			}
			output.collect(PlsGcUtils.KEY, new BytesWritable(baos.toByteArray()));
		}
	}
}
