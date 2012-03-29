package pls;

import java.util.Random;

public class SolutionIdGenerator {
	private static final Random RAND = new Random();
	
	public static int generateId() {
		return Math.abs(RAND.nextInt(Integer.MAX_VALUE));
	}
}
