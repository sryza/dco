package bnb.tsp;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E> {
	private final E[] arr;
	private int index = 0;
	
	public ArrayIterator(E[] arr) {
		this.arr = arr;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return index < arr.length;
	}

	@Override
	public E next() {
		return arr[index++];
	}

	@Override
	public void remove() {
		throw new IllegalStateException("operation not supported");
	}
}
