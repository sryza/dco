package pls.stats;

import java.util.Comparator;

import org.apache.hadoop.fs.FileStatus;

public class NumberFolderComparator implements Comparator<FileStatus> {
	@Override
	public int compare(FileStatus fs1, FileStatus fs2) {
		return (int)(getRunNumber(fs1) - getRunNumber(fs2));
	}
	
	private long getRunNumber(FileStatus fs) {
		String name = fs.getPath().getName();
		return Long.parseLong(name);
	}
}
