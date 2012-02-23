package pls.tsp;

import static pls.tsp.TspUtils.wrap;

import java.util.Random;

public class TspSimulatedAnnealing
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

	public TspSimulatedAnnealing(int duration, int maxtemp)
	{
		_duration = duration;
		max_temp = maxtemp;
	}

	public TspLsCity[] runSimulatedAnnealing(TspLsCity[] nodes)
	{
		start_time = System.currentTimeMillis();

		TspLsCity[] best = new TspLsCity[nodes.length];
		System.arraycopy(nodes, 0, best, 0, nodes.length);
		TspLsCity[] nodes1 = new TspLsCity[nodes.length];
		System.arraycopy(nodes, 0, nodes1, 0, nodes.length);
		TspLsCity[] nodes2 = new TspLsCity[nodes.length];

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

			TspLsCity[] temp = nodes2;
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


	public int runStep(TspLsCity[] nodes, TspLsCity[] newNodes, long end) {
		int maxIter = 1;
		int newMaxIter = 1;
		for (int i = 0; i < 3; i++) {
			newMaxIter = maxIter*nodes.length;
			if(newMaxIter > maxIter) {
				maxIter = newMaxIter;
			} else {
				maxIter = Integer.MAX_VALUE;
				break;
			}
		}

		for (int iter = 0; iter < maxIter; iter++) {
			if (iter % 1000 == 0 && System.currentTimeMillis() >= end) {
				return Integer.MAX_VALUE;
			}

			int i = RAND.nextInt(nodes.length), j = RAND.nextInt(nodes.length), k = RAND.nextInt(nodes.length);

			if (j == i || wrap(j+1) == i || wrap(j-1) == i || j == k || wrap(j+1) == k || wrap(j-1) == k || i == k) {
				continue;
			}

			int delta = TspLsUtils.cost3opt(nodes, i, j, k);
			if (delta < 0) {
				totalImproving++;
				TspLsUtils.swap3opt(nodes, newNodes, i, j, k);
				return delta;
			}

			if (_temp != 0 && RAND.nextDouble() < Math.exp(-(delta+1)/_temp))
			{
				totalTempChosen++;
				TspLsUtils.swap3opt(nodes, newNodes, i, j, k);
				return delta;
			}
		}

		//		System.out.println ("hit max iterations");
		return Integer.MAX_VALUE;
	}
	//	private void updateTemp(int iter)
	//	{
	//		_temp = MAX_TEMP - (MAX_TEMP * iter) / MAX_ITER;
	//	//	_temp = 0;
	//	}


	//	public Node[] runStep(Node[] nodes)
	//	{
	//		int maxIter = nodes.length * nodes.length * nodes.length;
	//		
	//		for (int iter = 0; iter < maxIter; iter++)
	//		{
	//			int i = RAND.nextInt(nodes.length), j = RAND.nextInt(nodes.length), k = RAND.nextInt(nodes.length);
	//			if (j == i || wrap(j+1) == i || wrap(j-1) == i || j == k || wrap(j+1) == k || wrap(j-1) == k || i == k)
	//				continue;
	//			
	//			int delta = LocalSearch.cost3opt(nodes, i, j, k);
	//			if (delta < 0)
	//			{
	//				totalImproving++;
	//				return LocalSearch.swap3opt(nodes, i, j, k);
	//			}
	//			
	//			if (_temp != 0 && RAND.nextDouble() < Math.exp(-(delta+1)/_temp))
	//			{
	//				totalTempChosen++;
	//				return LocalSearch.swap3opt(nodes, i, j, k);
	//			}
	//		}
	//
	//		System.out.println ("hit max iterations");
	//		return null;
	//	}

}
