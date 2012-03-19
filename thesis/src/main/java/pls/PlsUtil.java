package pls;

import org.apache.hadoop.io.BytesWritable;

public class PlsUtil {
	public static final BytesWritable METADATA_KEY = new BytesWritable("metadata".getBytes());
	public static final BytesWritable SOLS_KEY = new BytesWritable("sols".getBytes());
}
