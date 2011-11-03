package bnb.rpc;

public interface Byteable {
	/**
	 * Should have an empty constructor after which this can be called.
	 */
	public void initFromBytes(byte[] bytes);
	
	public byte[] toBytes();
}
