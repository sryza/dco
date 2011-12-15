package bnb.stats;

import java.util.LinkedList;
import java.util.List;

public class ThreadLocalCount {
	private List<Count> allCounts = new LinkedList<Count>(); 
	private ThreadLocal<Count> localCount = new ThreadLocal<Count>();
	
	public void increment() {
		if (localCount.get() == null) {
			localCount.set(new Count());
			synchronized(allCounts) {
				allCounts.add(localCount.get());
			}
		}
		localCount.get().inc();
	}
	
	public int getCount() {
		int sum = 0;
		for (Count count : allCounts) {
			sum += count.get();
		}
		return sum;
	}
	
	private class Count {
		private int count = 0;
		
		public void inc() {
			count++;
		}
		
		public int get() {
			return count;
		}
	}
}
