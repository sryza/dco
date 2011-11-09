package bnb.rpc;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import bnb.BnbNode;
import bnb.Problem;

public class RpcUtil {
	//TODO do we need a separate method for ProblemSpec data?
	
	public static Problem problemFromThriftData(ThriftData problemData) throws ClassNotFoundException, 
		InstantiationException, IllegalAccessException {
		Object o = Class.forName(problemData.className).newInstance();
		Problem problem = (Problem)o;
		problem.initFromBytes(problemData.bytes.array());
		return problem;
	}

	public static BnbNode nodeFromThriftData(ThriftData nodeData, Problem problem)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
		InvocationTargetException, NoSuchMethodException, SecurityException {
		//have to pass in Problem to newInstance
		BnbNode node = (BnbNode)Class.forName(nodeData.className).newInstance();
		node.initFromBytes(nodeData.bytes.array(), problem);
		return node;
	}

	
	public static ThriftData toThriftData(Byteable byteable) {
		return new ThriftData(byteable.getClass().getName(), 
				ByteBuffer.wrap(byteable.toBytes()));
	}
}