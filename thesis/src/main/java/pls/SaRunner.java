package pls;

public interface SaRunner<T extends SaSolution> {
	/**
	 * Returns the final state and the best state.
	 */
	public SaSolution[] run(T start, long timeMs);
}
