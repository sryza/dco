package pls.map;

import java.util.Random;

import pls.PlsSolution;

public interface PlsRunner {
	/**
	 * Returns the final state and the best state.
	 */
	public PlsSolution[] run(PlsSolution start, long timeMs, Random rand);
}
