package pls.gc;

import gc.GcInitializer;
import gc.GcProblem;
import gc.GcReader;
import gc.GcSolution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

public class PlsGcMaster {
	private static final Logger LOG = Logger.getLogger(PlsGcMaster.class);
	
	//for collecting stats
	private BlockingQueue<Path> completedJobPathsQueue = new LinkedBlockingQueue<Path>();
	//for telling the stats thread we're done
	private static final Path DONE_PATH = new Path("/");
	
	public static void main(String[] args) throws IOException {
		int popSize = 10;
		int roundTime = 60000;
		int lsTime = 2000;
		boolean runLocal = false;
		File inputFile = new File("../gctests/dsjc250.5.col");
		int k = 28;
		int numMappers = Integer.parseInt(args[0]);
		int numRounds = Integer.parseInt(args[1]);
		boolean reduceShuffle = true;
		if (args.length > 2) {
			if (args[2].matches("(true|false)")) {
				runLocal = Boolean.parseBoolean(args[2]);
			} else {
				inputFile = new File(args[2]);
			}
		}
		if (args.length > 3) {
			k = Integer.parseInt(args[3]);
		}
		if (args.length > 4) {
			reduceShuffle = Boolean.parseBoolean(args[4]);
		}
		if (args.length > 5) {
			roundTime = Integer.parseInt(args[5]);
		}
		if (args.length > 6) {
			lsTime = Integer.parseInt(args[6]);
		}
		if (args.length > 7) {
			popSize = Integer.parseInt(args[7]);
		}
		
		Random rand = new Random();
		GcProblem problem = GcReader.read(inputFile);
		GcInitializer initializer = new GcInitializer(rand);
		GcSolution[] population = new GcSolution[popSize * numMappers];
		for (int i = 0; i < population.length; i++) {
			population[i] = initializer.makeInitialColoring(problem, k);
		}
		
		String dir = "/users/sryza/gcdir/" + System.currentTimeMillis() + "/";
		
		GcPlsParams params = new GcPlsParams(popSize, roundTime, lsTime, 40, .6, System.currentTimeMillis() + roundTime, true, reduceShuffle);
		
		GcJobStats stats = new GcJobStats(numMappers, popSize, k, inputFile.getName(), params);
		PlsGcMaster master = new PlsGcMaster();
		master.run(numRounds, numMappers, population, dir, params, problem, k, stats, runLocal);
	}
	
	public void run(int numRounds, int numMappers, GcSolution[] population, String dir, GcPlsParams params, GcProblem problem, int k,
			GcJobStats stats, boolean runLocal) throws IOException {
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
			new GcPlsProblem(problem, k).write(dos);
			params.write(dos);
			for (int j = 0; j < numSolsPerMapper; j++) {
				GcPlsSolution plsSol = new GcPlsSolution(population[solIndex]);
				plsSol.write(dos);
				solIndex++;
			}
			writer.append(PlsGcUtils.KEY, new BytesWritable(baos.toByteArray()));
		}
		writer.close();
		
		//start stats collection
		StatsThread statsThread = new StatsThread(stats, fs, conf);
		statsThread.start();
		
		completedJobPathsQueue.offer(initDirPath);
		stats.reportRoundTime(0);
		//run the rounds
		for (int i = 0; i < numRounds; i++) {
			Path inputPath = new Path(dirPath, i + "/");
			Path outputPath = new Path(dirPath, (i+1) + "/");
			long start = System.currentTimeMillis();
			LOG.info("About to run job " + i);
			runHadoopJob(inputPath, outputPath, numMappers, runLocal);
			completedJobPathsQueue.offer(outputPath);
			long end = System.currentTimeMillis();
			LOG.info("Took " + (end-start) + " ms");
			stats.reportRoundTime((int)(end-start));
		}
		
		
		completedJobPathsQueue.offer(DONE_PATH);
		try {
			statsThread.join();
		} catch (InterruptedException ex) {
			LOG.error("Error waiting for stats thread to join", ex);
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

		conf.setJar("gc.jar");

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
	
	private class StatsThread extends Thread {
		private final GcJobStats stats;
		private final FileSystem fs;
		private final Configuration conf;
		
		public StatsThread(GcJobStats stats, FileSystem fs, Configuration conf) {
			this.stats = stats;
			this.fs = fs;
			this.conf = conf;
		}
		
		public void run() {
			Path path;
			try {
				while ((path = completedJobPathsQueue.take()) != DONE_PATH) {
					Path outFilePath = new Path(path, "part-00000");
					SequenceFile.Reader reader = new SequenceFile.Reader(fs, outFilePath, conf);
					processRoundOutput(reader);
					reader.close();
				}
			} catch (InterruptedException ex) {
				LOG.error("Interrupted", ex);
			} catch (IOException ex) {
				LOG.error("Failed to read output file", ex);
			}
		}
		
		private void processRoundOutput(SequenceFile.Reader reader) throws IOException {
			BytesWritable key = new BytesWritable();
			BytesWritable value = new BytesWritable();
			GcSolution bestSol = null;
			while (reader.next(key, value)) {
				ByteArrayInputStream bais = new ByteArrayInputStream(value.getBytes(), 0, value.getLength());
				DataInputStream dis = new DataInputStream(bais);
				
				List<GcPlsSolution> sols = new ArrayList<GcPlsSolution>();
				GcPlsProblem problem = new GcPlsProblem();
				GcPlsParams params = new GcPlsParams();
				problem.readFields(dis);
				params.readFields(dis);
				
				for (int i = 0; i < params.getPopulationSize(); i++) {
					GcPlsSolution plsSol = new GcPlsSolution();
					plsSol.readFields(dis);
					sols.add(plsSol);
					
					GcSolution sol = plsSol.getSolution();
					if (bestSol == null || sol.getCost() < bestSol.getCost()) {
						bestSol = sol;
					}
				}
				
			}
			
			stats.reportBestSolCost(bestSol.getCost());
		}
	}
}
