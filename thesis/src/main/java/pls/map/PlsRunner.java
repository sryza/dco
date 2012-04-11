package pls.map;

import java.util.Random;

import org.apache.hadoop.io.Writable;

import pls.PlsSolution;

public interface PlsRunner {
	/**
	 * Returns the final state and the best state.
	 */
	public PlsSolution[] run(PlsSolution start, long timeMs, Random rand);
	
	public Writable getExtraData();
	
	public void setHelperData(Writable helpData);
}
