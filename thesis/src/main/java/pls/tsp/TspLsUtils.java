package pls.tsp;

public class TspLsUtils {
	package tsp;

	import static tsp.Utils.*;

	import java.util.Random;

	public class LocalSearch
	{
		
		
//		public Node[] runLocalSearch(Node[] nodes)
//		{
//			int i = 0;
//			Node[] prev = nodes;
//			while ((nodes = iteration(nodes)) != null)
//			{
//				System.out.println ("iter: " + i);
//				System.out.println ("tourDist: " + Utils.tourDist(nodes));
//				i++;
//				prev = nodes;
//			}
//			return prev;
//		}
	//	
//		public Node[] iteration(Node[] nodes)
//		{
//			int besti = -1, bestj = -1, bestk = -1;
//			int minDelta = Integer.MAX_VALUE;
//			outer:
//			for (int i = 0; i < nodes.length; i++)
//			{
//				for (int j = 0; j < nodes.length; j++)
//				{
//					if (j == i || wrap(j+1) == i || wrap(j-1) == i)
//						continue;
//					
//					for (int k = 0; k < nodes.length; k++)
//					{
//						if (j == k || wrap(j+1) == k || wrap(j-1) == k || i == k)
//							continue;
//						
//						int delta = cost3opt(nodes, i, j, k);
//						if (delta < minDelta)
//						{
//							besti = i;
//							bestj = j;
//							bestk = k;
//							minDelta = delta;
//							if(delta<0)
//								break outer;
//						}
//					}
//				}
//			}
//			System.out.println ("minDelta: " + minDelta);
//			if (minDelta < 0)
//				return swap3opt(nodes,besti, bestj, bestk);
//			else
//				return null;
//		}
		
		public static Node[] swap3opt(Node[] nodes, Node[] newNodes, int index1, int index2, int index3)
		{
			newNodes[0] = nodes[index1];
			int newNodesIndex = 1;
			
			/* going from 3 to 1, you hit 5 */
			if((index3-index2+nodes.length)%nodes.length < (index1-index2+nodes.length)%nodes.length)
			{
				int index = wrap(index3-1);
				while (index != index2)
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index-1);
					newNodesIndex++;
				}
				
				newNodes[newNodesIndex] = nodes[index];
				newNodesIndex++;
				
				index = wrap(index1+1);
				while (index != wrap(index2-1))
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index+1);
					newNodesIndex++;
				}
				
				newNodes[newNodesIndex] = nodes[index];
				newNodesIndex++;
				
				index = index3;
				while (index != index1)
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index+1);
					newNodesIndex++;
				}
			}
			else
			{
				//go forwards from succ of index3 until the pred of index2
				int index = wrap(index3+1);
				while (index != wrap(index2-1))
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index+1);
					newNodesIndex++;
				}
				//include pred of index2
				newNodes[newNodesIndex] = nodes[index];
				newNodesIndex++;
				//go backwards from index3 to succ of index1
				index = index3;
				while (index != wrap(index1+1))
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index-1);
					newNodesIndex++;
				}
				//include succ of index1
				newNodes[newNodesIndex] = nodes[index];
				newNodesIndex++;
				//go forwards from index2 until index1
				index = wrap(index2);
				while (index != wrap(index1))
				{
					newNodes[newNodesIndex] = nodes[index];
					index = wrap(index+1);
					newNodesIndex++;
				}
			}
			
			return newNodes;
		}
		
		public static int cost3opt(Node[] nodes, int index1, int index2, int index3)
		{
			int delta = 0;
			if((index3-index2+nodes.length)%nodes.length < (index1-index2+nodes.length)%nodes.length)
			{
				delta -= Utils.dist(nodes[index1], nodes[wrap(index1+1)]);
				delta += Utils.dist(nodes[wrap(index1+1)], nodes[index2]);
				delta -= Utils.dist(nodes[index2], nodes[wrap(index2-1)]);
				delta += Utils.dist(nodes[wrap(index2-1)], nodes[index3]);
				delta -= Utils.dist(nodes[index3], nodes[wrap(index3-1)]);
				delta += Utils.dist(nodes[index1], nodes[wrap(index3-1)]);
			}
			else
			{
				delta -= Utils.dist(nodes[index1], nodes[wrap(index1+1)]);
				delta += Utils.dist(nodes[index3], nodes[wrap(index2-1)]);
				delta -= Utils.dist(nodes[index2], nodes[wrap(index2-1)]);
				delta += Utils.dist(nodes[index2], nodes[wrap(index1+1)]);
				delta -= Utils.dist(nodes[index3], nodes[wrap(index3+1)]);
				delta += Utils.dist(nodes[index1], nodes[wrap(index3+1)]);
			}
			return delta;
		}
		
//		public static void main(String[] args)
//		{
//			Random rand = new Random(1000);
//			Node[] nodes = new Node[12];
//			Utils.nodes = nodes;
//			for (int i = 0; i < nodes.length; i++)
//				nodes[i] = new Node(i, rand.nextInt(10), rand.nextInt(10));
//			print(nodes);
//			
//			System.out.println (Utils.tourDist(nodes));
//			System.out.println(cost3opt(nodes, 0, 7, 1));
//			print(nodes = swap3opt(nodes, 0, 7, 1));
//			System.out.println (Utils.tourDist(nodes));
//		}
		
		private static void print(Node[] nodes)
		{
			for (Node node : nodes)
				System.out.print(node.id + " ");
			System.out.println();
		}
		
		public static int cost2opt(Node[] nodes, int index1, int index2)
		{
			//add edge from index1 to predecessor of index2
			//add edge from successor of index1 to index2
			//remove edge from index1 to it's successor
			//remove edge from index2 to it's predecessor
			return Utils.dist(nodes[index1], nodes[index2-1]) + Utils.dist(nodes[index1+1], nodes[index2]) 
				- Utils.dist(nodes[index1], nodes[index1+1]) - Utils.dist(nodes[index2], nodes[index2-1]);
		}
		
		public static Node[] swap2opt(Node[] nodes, int index1, int index2)
		{
			Node[] newNodes = new Node[nodes.length];
			newNodes[0] = nodes[index1];

			int newNodesIndex = 1;
			int index = wrap(index2-1);
			while (index != wrap(index1+1))
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(index-1);
				newNodesIndex++;
			}
			newNodes[newNodesIndex] = nodes[wrap(index1+1)];
			newNodesIndex++;
			
			index = index2;
			while (index != index1)
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(index+1);
				newNodesIndex++;
			}
			return newNodes;
		}
		
		public static void main(String[] args)
		{
			Random rand = new Random(5489);
			Node[] nodes = new Node[20];
			for (int i = 0; i < nodes.length; i++)
				nodes[i] = new Node(i, rand.nextInt(100), rand.nextInt(100));
			int tourDist = Utils.tourDist(nodes);
			Utils.nodes = nodes;
			
			for (int i = 1; i < nodes.length-2; i++)
				for (int j = 1; j < nodes.length-2; j++)
				{
					if (Math.abs(j - i) > 2)
					{
						int cost = cost2opt(nodes, i, j);
						Node[] newNodes = swap2opt(nodes, i, j);
						int newTourDist = Utils.tourDist(newNodes);
						System.out.println (cost - (newTourDist - tourDist));
					}
				}
		}
	}

}
