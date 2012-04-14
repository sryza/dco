package pls.gc;

import gc.GcHybridRunner;
import gc.GcProblem;
import gc.GcSolution;
import gc.GcTabuSearchRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

public class GcMapper extends MapReduceBase implements Mapper<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {

	private static final Logger LOG = Logger.getLogger(GcMapper.class);

	@Override
	public void map(BytesWritable key, BytesWritable value,
			OutputCollector<BytesWritable, BytesWritable> output, Reporter reporter)
			throws IOException {
		
		ByteArrayInputStream bais = new ByteArrayInputStream(value.getBytes());
		DataInputStream dis = new DataInputStream(bais);
		GcPlsProblem plsProblem = new GcPlsProblem();
		plsProblem.readFields(dis);
		GcProblem problem = plsProblem.getProblem();
		GcPlsParams params = new GcPlsParams();
		params.readFields(dis);
		
		GcSolution[] sols = new GcSolution[params.getPopulationSize()];
		Random rand = new Random();
		GcTabuSearchRunner lsRunner = new GcTabuSearchRunner(params.getA(), params.getAlpha(), rand);
		
		for (int i = 0; i < params.getPopulationSize(); i++) {
			GcPlsSolution sol = new GcPlsSolution(problem);
			sol.readFields(dis);
			sols[i] = sol.getSolution();
			if (params.getStartWithLs()) {
				sols[i] = lsRunner.run(problem, sols[i], plsProblem.getK(), params.getLsTime());
			}
		}
		
		GcHybridRunner runner = new GcHybridRunner();
		runner.run(sols, lsRunner, rand, problem, plsProblem.getK(), params.getLsTime(), params.getFinishByTime());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		plsProblem.write(dos);
		params.write(dos);
		for (GcSolution sol : sols) {
			GcPlsSolution gcPlsSol = new GcPlsSolution(sol);
			gcPlsSol.write(dos);
		}
		output.collect(PlsGcUtils.KEY, new BytesWritable(baos.toByteArray()));
	}
}
