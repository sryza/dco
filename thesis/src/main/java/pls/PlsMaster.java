package pls;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.log4j.Logger;

import pls.tsp.TspSaSolution;

public class PlsMaster {

	private static final Logger LOG = Logger.getLogger(PlsMaster.class);
	
	public void run(int numRuns, List<TspSaSolution> startSolutions, String dir) throws IOException {
		//write out start solutions to HDFS
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path dirPath = new Path(dir);
		if (!fs.mkdirs(dirPath)) {
			LOG.info("Failed to create directory: " + dir);
		}
		
		//write out initial input file
		Path initFilePath = new Path(dirPath, "0");
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, initFilePath, BytesWritable.class, BytesWritable.class);
		BytesWritable key = new BytesWritable("rest".getBytes());
		for (TspSaSolution sol : startSolutions) {
			byte[] solBytes = sol.toBytes();
			writer.append(key, new BytesWritable(solBytes));
		}
		writer.close();
		
		//run the waves
		for (int i = 0; i < numRuns; i++) {
			Path inputPath = new Path(dirPath, i + "");
			Path outputPath = new Path(dirPath, (i+1) + "");
			long start = System.currentTimeMillis();
			LOG.info("About to run job " + i);
			runHadoopJob(inputPath, outputPath);
			long end = System.currentTimeMillis();
			LOG.info("Took " + (end-start) + " ms");
		}
	}

	/**
	 * Involves sending a solution (or location of a solution) to each node.
	 */
	private void runHadoopJob(Path inputPath, Path outputPath) throws IOException {
		JobConf conf = new JobConf();

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		conf.setMapperClass(SaMapper.class);
		conf.setReducerClass(ChooserReducer.class);

		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);
		
		FileInputFormat.addInputPath(conf, inputPath);
		FileOutputFormat.setOutputPath(conf, outputPath);
		
		JobClient.runJob(conf);
	}
}
