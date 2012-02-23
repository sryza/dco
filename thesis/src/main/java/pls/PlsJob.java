package pls;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

public class PlsJob {

	private int numRuns;

	public void run() {
		//reducer chooses solutions to keep, writes them out to HDFS
		//master process decides then resends solutions that it's chosen to new MR job
	}

	/**
	 * Involves sending a solution (or location of a solution) to each node.
	 */
	private void runHadoopJob() {
		Configuration conf = new Configuration();
		
		Job job = new Job(conf, "wordcount");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.waitForCompletion(true);
	}
}
