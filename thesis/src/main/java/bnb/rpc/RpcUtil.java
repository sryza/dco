package bnb.rpc;

import java.util.ArrayList;
import java.util.List;

public class RpcUtil {
	public static Byteable fromThriftData(ThriftData nodeData) throws ClassNotFoundException, 
		InstantiationException, IllegalAccessException {
		Object o = Class.forName(nodeData.className).newInstance();
		Byteable node = (Byteable)o;
		//TODO: this is really not a good use of time/space
		//should not have to copy into another buffer
		byte[] bytes = new byte[nodeData.bytes.size()];
		for (int i = 0; i < nodeData.bytes.size(); i++) {
			bytes[i] = nodeData.bytes.get(i);
		}
		node.initFromBytes(bytes);
		return node;
	}
	
	public static ThriftData toThriftData(Byteable byteable) {
		byte[] nodeBytes = byteable.toBytes();
		List<Byte> bytesList = new ArrayList<Byte>(nodeBytes.length);
		for (Byte b : nodeBytes) {
			bytesList.add(b);
		}
		return new ThriftData(byteable.getClass().getName(), bytesList);
	}
}