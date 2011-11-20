package bnb.tsp;

import java.util.ArrayList;
import java.util.Iterator;

public class ParentCityIterator implements Iterator<City> {
	
	private TspNode node;
	private int prevCitiesIndex = -1;
	private ArrayList<City> prevCities;
	
	public ParentCityIterator(TspNode node) {
		this.node = node;
	}
	
	@Override
	public boolean hasNext() {
		return node != null || prevCitiesIndex > 0;
	}

	@Override
	public City next() {
		if (node != null) {
			City city = node.getCity();
			TspNode parent = (TspNode)node.getParent();
			if (parent == null) {
				prevCities = node.getPrevCities();
				prevCitiesIndex = prevCities == null ? 0 : prevCities.size();
			}
			node = parent;
			return city;
		} else {
			prevCitiesIndex--;
			return prevCities.get(prevCitiesIndex);
		}
	}
	
	@Override
	public void remove() {
		throw new IllegalStateException("Operation not supported");
	}
}
