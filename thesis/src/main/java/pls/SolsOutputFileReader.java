package pls;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.log4j.Logger;

import pls.tsp.TspSaSolution;

public class SolsOutputFileReader {
	
	private static final Logger LOG = Logger.getLogger(SolsOutputFileReader.class);
	
	public static void main(String[] args) throws IOException {
//		Path path = new Path(args[0]);
		Path path = new Path("/users/sryza/testdir/1331512645039/");
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		FileStatus[] statuses = fs.listStatus(path);
		System.out.println("number of subfiles: " + statuses.length);
		for (FileStatus fileStatus : statuses) {
			Path subpath = fileStatus.getPath();
			subpath = new Path(subpath, "part-00000");
			System.out.println("Sols for " + subpath.getName());
			List<TspSaSolution> solutions = getFileSolutions(subpath, fs, conf);
			for (TspSaSolution sol : solutions) {
				LOG.info("sol cost=" + sol.getCost() + ", temp=" + sol.getTemperature());
			}
			System.out.println();

		}
	}
	
	public static List<TspSaSolution> getFileSolutions(Path path, FileSystem fs, Configuration conf) throws IOException {
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
		BytesWritable key = new BytesWritable();
		BytesWritable value = new BytesWritable();
		List<TspSaSolution> sols = new ArrayList<TspSaSolution>();
		while (reader.next(key, value)) {
			if (key.equals(PlsUtil.METADATA_KEY)) {
				//TODO: read best solution so that we can note that it's not among reported if it's not
				continue;
			} else if (!key.equals(PlsUtil.SOLS_KEY)) {
				LOG.warn("Found unexpected key " + new String(key.getBytes()));
			}
			
			TspSaSolution sol = TspSaSolution.fromStream(new DataInputStream(new ByteArrayInputStream(value.getBytes())));
			sols.add(sol);
		}
		return sols;
	}
}
