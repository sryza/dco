package bnb;

import java.io.InputStream;
import java.io.OutputStream;

public interface Byteable {
	public void writeToStream(OutputStream os);
	public void readFromStream(InputStream is);
	public void writeToBytes();
	public void writeToStream();
}
