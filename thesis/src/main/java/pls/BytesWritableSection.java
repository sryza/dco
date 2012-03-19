package pls;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;

public class BytesWritableSection extends BytesWritable implements Writable {
	private final BytesWritable wrapped;
	private final int offset;
	private final int len;

	public BytesWritableSection(BytesWritable wrapped, int offset, int len) {
		this.wrapped = wrapped;
		this.offset = offset;
		this.len = len;
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		throw new IllegalStateException("this method should never be called");
	}

	@Override
	public void write(DataOutput output) throws IOException {
		byte[] bytes = wrapped.getBytes();
		output.write(bytes, offset, len);
	}
}