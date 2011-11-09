package bnb;

import bnb.rpc.Byteable;

/**
 * Information that's sent at the beginning of a solver execution
 * and stored at each VassalRunner
 */
public interface Problem extends Byteable {
	public void initFromBytes(byte[] bytes);
}
