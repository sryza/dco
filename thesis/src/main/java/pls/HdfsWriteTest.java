package pls;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsWriteTest {
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("/users/sryza/");
		FSDataOutputStream os = fs.create(path);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		bw.write("lalalalala\n");
		bw.write("fjkfdjkfd");
		bw.close();
	}
}
