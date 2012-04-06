package pls.stats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class JobSolsFilesGatherer {
	private FileSystem fileSystem;
	
	public JobSolsFilesGatherer(FileSystem fs) {
		this.fileSystem = fs;
	}
	
	public List<Path> gather(Path jobBaseDir) throws FileNotFoundException, IOException {
		FileStatus[] statuses = fileSystem.listStatus(jobBaseDir);
		Arrays.sort(statuses, new NumberFolderComparator());
		List<Path> paths = new ArrayList<Path>();
		for (FileStatus fileStatus : statuses) {
			if (fileStatus.isDir()) {
				Path subpath = fileStatus.getPath();
				subpath = new Path(subpath, "part-00000");
				paths.add(subpath);
			}
		}
		return paths;
	}
	
	public Path getLatestRunFolderPath(Path testDir, int howManyBack) throws IOException {
		FileStatus[] statuses = fileSystem.listStatus(testDir);
		Arrays.sort(statuses, new NumberFolderComparator());
		return statuses[statuses.length-1-howManyBack].getPath();
	}
}
