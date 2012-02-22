package bnb.stats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Aggregates separate statistics for each thread with the option to combine them
 * later.
 */

public class ThreadLocalList<T> {
	private List<List<T>> listOfLists = new ArrayList<List<T>>();
	private ThreadLocal<List<T>> list = new ThreadLocal<List<T>>();
	
	public void add(T t) {
		if (list.get() == null) {
			list.set(new LinkedList<T>());
			synchronized(listOfLists) {
				listOfLists.add(list.get());
			}
		}
		list.get().add(t);
	}
	
	public List<T> getAll() {
		List<T> all = new ArrayList<T>();
		for (List<T> list : listOfLists) {
			all.addAll(list);
		}
		return all;
	}
	
	public List<List<T>> getLists() {
		return listOfLists;
	}
}
