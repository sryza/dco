package pls;

import java.util.Random;

public interface PlsRunner {
	/**
	 * Returns the final state and the best state.
	 */
	public PlsSolution[] run(PlsSolution start, long timeMs, Random rand);
}
