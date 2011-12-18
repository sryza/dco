package bnb.rpc;

import org.junit.Test;

import bnb.BnbNode;
import bnb.tsp.City;
import bnb.tsp.TspNode;
import bnb.tsp.TspProblem;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//TODO: would it be better to build the list of remaining cities inside doEvaluate?
public class TestRpcUtil {
	@Test
	public void testWriteAndReadTspNode() throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException {
		City[] cities = {new City(5, 10, 0), new City(5, 65, 1), new City(4, 5, 2)};
		TspProblem problem  = new TspProblem(cities);
//		LinkedList<City> remCities = Arrays.asList(cities).subList(1, cities.length);
		//TODO: try this with cities[1] also
//		TspNode node = new TspNode(cities[0], cities[0], 1, null, remCities, problem);
		
		//write
//		ThriftData nodeData = RpcUtil.toThriftData(node);
		
		//read
//		BnbNode readNode = RpcUtil.nodeFromThriftData(nodeData, problem);
	}
}
