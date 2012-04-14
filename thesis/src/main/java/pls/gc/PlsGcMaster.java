package pls.gc;

import gc.GcInitializer;
import gc.GcProblem;
import gc.GcReader;
import gc.GcSolution;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.log4j.Logger;

import pls.PlsSolution;
import pls.PlsUtil;
import pls.stats.PlsJobStats;

public class PlsGcMaster {
	private static final Logger LOG = Logger.getLogger(PlsGcMaster.class);
	
	public static void main(String[] args) throws IOException {
		int popSize = 10;
		int roundTime = 60000;
		int lsTime = 2000;
		File inputFile = new File("../gctests/dsjc250.5.col");
		int k = 28;
		int numMappers = Integer.parseInt(args[0]);
		int numRounds = Integer.parseInt(args[1]);
		
		Random rand = new Random();
		GcProblem problem = GcReader.read(inputFile);
		GcInitializer initializer = new GcInitializer(rand);
		GcSolution[] population = new GcSolution[popSize];
		for (int i = 0; i < popSize; i++) {
			population[i] = initializer.makeInitialColoring(problem, k);
		}
		
		String dir = "/users/sryza/gcdir/" + System.currentTimeMillis() + "/";
		
		GcPlsParams params = new GcPlsParams(popSize, roundTime, lsTime, 40, .6, System.currentTimeMillis() + roundTime, true);
		
		GcJobStats stats = new GcJobStats(numMappers, popSize, k, inputFile.getName(), params);
		PlsGcMaster master = new PlsGcMaster();
		master.run(numRounds, numMappers, population, dir, params, problem, stats);
	}
	
	public void run(int numRounds, int numMappers, GcSolution[] population, String dir, GcPlsParams params, GcProblem problem,
			GcJobStats stats) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path dirPath = new Path(dir);
		fs.delete(dirPath, true);
		
		//write out initial input file
		Path initDirPath = new Path(dirPath, "0/");
		if (!fs.mkdirs(initDirPath)) {
			LOG.info("Failed to create directory: " + initDirPath);
		}
		
		Path initFilePath = new Path(initDirPath, "part-00000");
		
		//write out solutions
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, initFilePath, BytesWritable.class, BytesWritable.class);
		int numSolsPerMapper = population.length / numMappers;
		int solIndex = 0;
		for (int i = 0; i < numMappers; i++) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			new GcPlsProblem(problem).write(dos);
			params.write(dos);
			for (int j = 0; j < numSolsPerMapper; j++) {
				GcPlsSolution plsSol = new GcPlsSolution(population[solIndex]);
				plsSol.write(dos);
				solIndex++;
			}
			writer.append(PlsGcUtils.KEY, new BytesWritable(baos.toByteArray()));
		}
		
		for (int i = 0; i < numRounds; i++) {
			Path inputPath = new Path(dirPath, i + "/");
			Path outputPath = new Path(dirPath, (i+1) + "/");
			long start = System.currentTimeMillis();
			LOG.info("About to run job " + i);
			runHadoopJob(inputPath, outputPath, numMappers, false);
			long end = System.currentTimeMillis();
			LOG.info("Took " + (end-start) + " ms");
			stats.reportRoundTime((int)(end-start));
		}
		
		writeStatsFile(dirPath, stats, fs);
	}
	
	
	private void runHadoopJob(Path inputPath, Path outputPath, int numMaps, boolean runLocal)
		throws IOException {
		JobConf conf = new JobConf();

		if (runLocal) {
			conf.set("mapred.job.tracker", "local");
		}

		conf.setOutputKeyClass(BytesWritable.class);
		conf.setOutputValueClass(BytesWritable.class);
		conf.setMapOutputKeyClass(BytesWritable.class);
		conf.setMapOutputValueClass(BytesWritable.class);

		conf.setJar("tspls.jar");

		conf.setMapperClass(GcMapper.class);
		conf.setReducerClass(GcReducer.class);

		conf.setSpeculativeExecution(false);

		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);

		conf.setNumMapTasks(numMaps);
		conf.setNumReduceTasks(1);

		FileInputFormat.addInputPath(conf, inputPath);
		FileOutputFormat.setOutputPath(conf, outputPath);

		JobClient.runJob(conf);
	}
	
	private void writeStatsFile(Path dir, GcJobStats stats, FileSystem fs) throws IOException {
		Path statsPath = new Path(dir, "jobstats.stats");
		FSDataOutputStream os = fs.create(statsPath);
		String report = stats.makeReport();
		os.writeUTF(report);
		os.close();
	}

}
