import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Benchmarks {
	
	private static final Random RAND = new Random();
	private static final int SIZE = 10000000;
	
	
	public static void main(String[] args) {
		new HashSet<Integer>(Arrays.asList(new Integer[] {null}));
	}
	
	public static void bench() {
		long start = System.currentTimeMillis();
		List<Integer> intList = new LinkedList<Integer>();
		for (int i = 0; i < SIZE; i++)  {
			intList.add(i);
		}
		long end = System.currentTimeMillis();
		System.out.println("list: " + (end - start));
		
		start = System.currentTimeMillis();
		Set<Integer> intSet = new HashSet<Integer>();
		for (int i = 0; i < SIZE; i++)  {
			intSet.add(i);
		}
		end = System.currentTimeMillis();
		System.out.println("set: " + (end - start));
	}
}
