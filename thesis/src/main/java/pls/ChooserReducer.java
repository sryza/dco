package pls;

import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class ChooserReducer extends Reducer<BytesWritable, IntWritable> {
	
	//this is wrong
	public void reduce(BytesWritable key, Iterable<IntWritable> values, Context context) throws IOException {
		
	}
}
