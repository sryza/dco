package bnb.rpc;

import java.util.ArrayList;
import java.util.List;

import bnb.BnbNode;

public class RpcUtil {
	public static BnbNode fromThriftNode(ThriftNodeData nodeData) throws ClassNotFoundException {
		Object o = Class.forName(nodeData.className);
		BnbNode node = (BnbNode)o;
		//TODO: this is really not a good use of time/space
		//should not have to copy into another buffer
		byte[] bytes = new byte[nodeData.bytes.size()];
		for (int i = 0; i < nodeData.bytes.size(); i++) {
			bytes[i] = nodeData.bytes.get(i);
		}
		node.initFromBytes(bytes);
		return node;
	}
	
	public static ThriftNodeData toThriftNode(BnbNode node) {
		byte[] nodeBytes = node.toBytes();
		List<Byte> bytesList = new ArrayList<Byte>(nodeBytes.length);
		for (Byte b : nodeBytes) {
			bytesList.add(b);
		}
		return new ThriftNodeData(node.getClass().getName(), bytesList);
	}
}
