package bnb.tsp;

import java.util.Random;
import static bnb.tsp.TspUtils.wrap;


public class SimulatedAnnealing
{
	private final Random RAND = new Random((int)(Math.random() * 40000));
//	private static final double MAX_TEMP = 5.0;
	private static final int MAX_ITER = 1000;
	private static final int RANGE = 20;
	
	private long start_time;
	private int _duration;
	private double _temp = 0, max_temp;

	private double alpha;
	
	private int totalImproving = 0, totalTempChosen = 0;
	
	public SimulatedAnnealing(int duration, int maxtemp)
	{
		_duration = duration;
		max_temp = maxtemp;
	}
	
	public City[] runSimulatedAnnealing(City[] nodes)
	{
		start_time = System.currentTimeMillis();
		
		City[] best = new City[nodes.length];
		System.arraycopy(nodes, 0, best, 0, nodes.length);
		City[] nodes1 = new City[nodes.length];
		System.arraycopy(nodes, 0, nodes1, 0, nodes.length);
		City[] nodes2 = new City[nodes.length];
		
//		for (int i = 1; i <= MAX_ITER; i++)
		long end = start_time + _duration * 1000;
		alpha = max_temp / (_duration * _duration);
		int i = 0, totalCost = 0, bestCost = 0;
		while (System.currentTimeMillis() < end)
		{
			updateTemp();
			
			int delta = runStep(nodes1, nodes2, end);
			
			if (delta == Integer.MAX_VALUE)
				break;
			
			totalCost += delta;
			if (totalCost < bestCost)
			{
				System.arraycopy(nodes2, 0, best, 0, nodes1.length);
				bestCost = totalCost;
			}
			
			City[] temp = nodes2;
			nodes2 = nodes1;
			nodes1 = temp;
			
//			System.out.println ("temp: " + _temp);
//			System.out.println ("iter: " + i + "\t" + totalImproving + "\t" + totalTempChosen);
//			System.out.println ("tourDist: " + Utils.tourDist(nodes1));
			i++;
		}
		return best;
	}
	
	private void updateTemp()
	{
	//	_temp = MAX_TEMP - (MAX_TEMP * (System.currentTimeMillis() - start_time) / 1000) / _duration;
		double inside = (System.currentTimeMillis() - start_time) / 1000 - _duration;
		_temp = alpha * inside * inside;
//		_temp = 0;
	}
	

	public int runStep(City[] nodes, City[] newNodes, long end)
	{
		int maxIter = 1;
		int newMaxIter = 1;
		for(int i=0; i<3; i++)
		{
			newMaxIter = maxIter*nodes.length;
			if(newMaxIter > maxIter)
				maxIter = newMaxIter;
			else
			{
				maxIter = Integer.MAX_VALUE;
				break;
			}
		}

		for (int iter = 0; iter < maxIter; iter++)
		{
			if (iter % 1000 == 0 && System.currentTimeMillis() >= end)
				return Integer.MAX_VALUE;
			
			int i = RAND.nextInt(nodes.length), j = RAND.nextInt(nodes.length), k = RAND.nextInt(nodes.length);
			
			if (j == i || wrap(nodes, j+1) == i || 
					wrap(nodes, j-1) == i || j == k || 
					wrap(nodes, j+1) == k || 
					wrap(nodes, j-1) == k || i == k)
				continue;
			
			int delta = TspUtils.cost3opt(nodes, i, j, k);
			if (delta < 0)
			{
				totalImproving++;
				swap3opt(nodes, newNodes, i, j, k);
				return delta;
			}
			
			if (_temp != 0 && RAND.nextDouble() < Math.exp(-(delta+1)/_temp))
			{
				totalTempChosen++;
				swap3opt(nodes, newNodes, i, j, k);
				return delta;
			}
		}

//		System.out.println ("hit max iterations");
		return Integer.MAX_VALUE;
	}
	
	public static City[] swap3opt(City[] nodes, City[] newNodes, int index1, int index2, int index3)
	{
		newNodes[0] = nodes[index1];
		int newNodesIndex = 1;
		
		/* going from 3 to 1, you hit 5 */
		if((index3-index2+nodes.length)%nodes.length < (index1-index2+nodes.length)%nodes.length)
		{
			int index = wrap(nodes, index3-1);
			while (index != index2)
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index-1);
				newNodesIndex++;
			}
			
			newNodes[newNodesIndex] = nodes[index];
			newNodesIndex++;
			
			index = wrap(nodes, index1+1);
			while (index != wrap(nodes, index2-1))
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index+1);
				newNodesIndex++;
			}
			
			newNodes[newNodesIndex] = nodes[index];
			newNodesIndex++;
			
			index = index3;
			while (index != index1)
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index+1);
				newNodesIndex++;
			}
		}
		else
		{
			//go forwards from succ of index3 until the pred of index2
			int index = wrap(nodes, index3+1);
			while (index != wrap(nodes, index2-1))
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index+1);
				newNodesIndex++;
			}
			//include pred of index2
			newNodes[newNodesIndex] = nodes[index];
			newNodesIndex++;
			//go backwards from index3 to succ of index1
			index = index3;
			while (index != wrap(nodes, index1+1))
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index-1);
				newNodesIndex++;
			}
			//include succ of index1
			newNodes[newNodesIndex] = nodes[index];
			newNodesIndex++;
			//go forwards from index2 until index1
			index = wrap(nodes, index2);
			while (index != wrap(nodes, index1))
			{
				newNodes[newNodesIndex] = nodes[index];
				index = wrap(nodes, index+1);
				newNodesIndex++;
			}
		}
		
		return newNodes;
	}

}