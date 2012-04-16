package pls.stats;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.log4j.Logger;

import pls.PlsSolution;
import pls.PlsUtil;
import pls.WritableSolution;

public class SolsOutputFileReader {
	
	private static final Logger LOG = Logger.getLogger(SolsOutputFileReader.class);
	
	private FileSystem fileSystem;
	
	public static void main(String[] args) throws IOException, InstantiationException, 
		IllegalAccessException, ClassNotFoundException {
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		String solutionClass = args[0];
		Path path;
		if (args.length > 1) {
			if (args[1].startsWith("-")) {
				path = getLatestRunFolderPath(new Path("/users/sryza/testdir/"), fs, -Integer.parseInt(args[1]));
			} else {
				path = new Path(args[1]);
			}
		} else {
			path = getLatestRunFolderPath(new Path("/users/sryza/testdir/"), fs, 0);
		}
		
		JobSolsFilesGatherer gatherer = new JobSolsFilesGatherer(fs);
		
		SolsOutputFileReader reader = new SolsOutputFileReader(fs);
		List<Path> files = gatherer.gather(path);
		for (Path subpath : files) {
			subpath = new Path(subpath, "part-00000");
			System.out.println("Sols for " + subpath.toString());
			List<WritableSolution> solutions = reader.getFileSolutions(subpath, conf, solutionClass);
			for (WritableSolution sol : solutions) {
				LOG.info("sol cost=" + sol.getCost());
			}
			System.out.println();
		}
	}
	
	public SolsOutputFileReader(FileSystem fs) {
		this.fileSystem = fs;
	}
	
	public List<WritableSolution> getFileSolutions(Path path, Configuration conf, String solutionClass)
		throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		SequenceFile.Reader reader = new SequenceFile.Reader(fileSystem, path, conf);
		BytesWritable key = new BytesWritable();
		BytesWritable value = new BytesWritable();
		List<WritableSolution> sols = new ArrayList<WritableSolution>();
		while (reader.next(key, value)) {
			if (key.equals(PlsUtil.METADATA_KEY)) {
				//TODO: read best solution so that we can note that it's not among reported if it's not
				continue;
			}
			
			WritableSolution sol = (WritableSolution)Class.forName(solutionClass).newInstance();
			sol.readFields(new DataInputStream(new ByteArrayInputStream(value.getBytes())));
			sols.add(sol);
		}
		return sols;
	}
	
	public List<BytesWritable> getSolutionBytes(Path path, Configuration conf) throws IOException {
		SequenceFile.Reader reader = new SequenceFile.Reader(fileSystem, path, conf);
		BytesWritable key = new BytesWritable();
		BytesWritable value = new BytesWritable();
		List<BytesWritable> datas = new ArrayList<BytesWritable>();
		while (reader.next(key, value)) {
			datas.add(value);
			
			key = new BytesWritable();
			value = new BytesWritable();
		}
		return datas;
	}
	
	private static Path getLatestRunFolderPath(Path testDir, FileSystem fs, int howManyBack) throws IOException {
		FileStatus[] statuses = fs.listStatus(testDir);
		Arrays.sort(statuses, new NumberFolderComparator());
		return statuses[statuses.length-1-howManyBack].getPath();
	}
}
