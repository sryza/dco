package tsp;

import java.util.Arrays;
import java.util.Collection;

public class UnionFind
{
	private Node[] parents;
	private int[] ranks;

	public UnionFind(Collection<Node> nodes, Node startNode, Node endNode, int size)
	{
		parents = new Node[size];
		ranks = new int[size];
		for (Node node : nodes)
			parents[node.id] = node;
		parents[startNode.id] = startNode;
		parents[endNode.id] = endNode;
	}
	
	/**
	 * This constructor allows us to reuse an old UnionFind that will never be used again in
	 * order to save on allocating memory.
	 */
	public UnionFind(Node[] nodes, Node[] parents, int[] ranks)
	{
		this.parents = parents;
		this.ranks = ranks;
		for (int i = 0; i < nodes.length; i++)
		{
			parents[nodes[i].id] = nodes[i];
			ranks[i] = 0;
		}
	}

	/**
	 * Returns the root of the tree that this node is a part of.
	 */
	public Node find(Node n)
	{
		if (parents[n.id] == n)
			return n;
		else
		{
			parents[n.id] = find(parents[n.id]);
			return parents[n.id];
		}
	}

	/**
	 * Merges the trees with the given roots;
	 */
	public void union(Node root1, Node root2)
	{
		if (ranks[root1.id] < ranks[root2.id])
			parents[root1.id] = root2;
		else if  (ranks[root1.id] > ranks[root2.id])
			parents[root2.id] = root1;
		else
		{
			parents[root2.id] = root1;
			ranks[root1.id]++;
		}
	}

	public class UFNode
	{
		public Node node;
		public UFNode parent;

		public UFNode(Node node, UFNode parent)
		{
			this.node = node;
			this.parent = parent;
		}
	}
}
