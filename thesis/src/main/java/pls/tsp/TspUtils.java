package pls.tsp;

public class TspUtils 
{
	public static int WRAP_NUM_NODES = 0; //0 ensures we'll get an exception if it's not set
	
	public static int dist(TspLsCity n1, TspLsCity n2)
	{
		double tempd = Math.sqrt((n1.x - n2.x) * (n1.x - n2.x) + (n1.y - n2.y) * (n1.y - n2.y));
		int tempi = (int)tempd;
		if(tempd-tempi<.5)
			return tempi;
		else
			return tempi+1;
	
		
		//speed this up using this: http://ww1.microchip.com/downloads/en/AppNotes/91040a.pdf?
//		return (int)Math.round(Math.sqrt((n1.x - n2.x) * (n1.x - n2.x) + (n1.y - n2.y) * (n1.y - n2.y)));
	}
	
	public static double dist(int n1, int n2)
	{
		//speed this up using this: http://ww1.microchip.com/downloads/en/AppNotes/91040a.pdf?
		return (int)Math.round(Math.sqrt((n1) * (n1) + (n2) * (n2)));
	}
	
	public static double dist2(int n1, int n2)
	{
		double tempd = (Math.sqrt((n1) * (n1) + (n2) * (n2)));
		int tempi = (int)tempd;
		if(tempd-tempi<.5)
			return tempi;
		else
			return tempi+1;
		
		//speed this up using this: http://ww1.microchip.com/downloads/en/AppNotes/91040a.pdf?
		
	}
	
	public static int tourDist(TspLsCity[] nodes)
	{
		int total = dist(nodes[0], nodes[nodes.length-1]);
		for (int i = 1; i < nodes.length; i++)
			total += dist(nodes[i-1], nodes[i]);
		return total;		
	}
	
	public static int wrap(int index)
	{
		return (index + WRAP_NUM_NODES) % WRAP_NUM_NODES;
	}
	
	/**
	 * Range is inclusive on the left and non-inclusive on the right.
	 */
	public static void reverse(TspLsCity[] nodes, int fromIndex, int toIndex)
	{
		fromIndex--;
		while (fromIndex < toIndex)
		{
			TspLsCity tmp = nodes[fromIndex];
			nodes[fromIndex] = nodes[toIndex];
			nodes[toIndex] = tmp;
			fromIndex++;
			toIndex++;
		}
	}
	
	public static boolean edgeCrossesPath(TspLsCity[] path, int numChosen, TspLsCity n1, TspLsCity n2)
	{
		for (int i = 0; i < numChosen-1; i++)
			if (path[i] != n1 && path[i+1] != n1 && path[i] != n2 && path[i+1] != n2 && crosses(path[i], path[i+1], n1, n2))
				return true;
		return false;
	}
	
	public static boolean crosses(TspLsCity n1, TspLsCity n2, TspLsCity n3, TspLsCity n4)
	{
		if (n1.x == n2.x || n3.x == n4.x)
			return false;
		double m1 = (double)(n1.y - n2.y) / (n1.x - n2.x), m2 = (double)(n3.y - n4.y) / (n3.x - n4.x);
		double b1 = (n1.y) - m1*n1.x, b2 = n3.y - m2*n3.x;
		if (m1 == m2)
			return false;
		double xIntersect = (b2 - b1) / (m1 - m2);
		int minX1 = Math.min(n1.x, n2.x), maxX1 = Math.max(n1.x, n2.x);
		int minX2 = Math.min(n3.x, n4.x), maxX2 = Math.max(n3.x, n4.x);
		return xIntersect > minX1 && xIntersect < maxX1 && xIntersect > minX2 && xIntersect < maxX2;
	}
}
