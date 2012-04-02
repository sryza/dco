package gc;

import java.util.Arrays;
import java.util.Random;

public class GcBreeder {
	private Random rand;
	
	public GcBreeder(Random rand) {
		this.rand = rand;
	}
	
	public GcSolution cross(GcSolution sol1, GcSolution sol2, int[][] nodeNeighbors, int k) {
		int[] nodeColors1 = (int[])sol1.getNodeColors().clone();
		int[] nodeColors2 = (int[])sol2.getNodeColors().clone();
		int[] colorFreqs1 = new int[k];
		int[] colorFreqs2 = new int[k];
		int[] newNodeColors = new int[nodeColors1.length];
		Arrays.fill(newNodeColors, -1);
		
		//build colorFreqs
		for (int i = 0; i < nodeColors1.length; i++) {
			colorFreqs1[nodeColors1[i]]++;
			colorFreqs2[nodeColors2[i]]++;
		}
		
		for (int i = 0; i < k; i++) {
			int[] colorFreqs = (i % 2 == 0) ? colorFreqs1 : colorFreqs2;
			int[] colorFreqsOther = (i % 2 == 0) ? colorFreqs2 : colorFreqs1;
			//find color class with max cardinality
			int color = -1;
			int maxCard = Integer.MIN_VALUE;
			for (int c = 0; c < colorFreqs.length; c++) {
				if (colorFreqs[c] > maxCard) {
					maxCard = colorFreqs[c];
					color = c;
				}
			}
			
			colorFreqs[color] = Integer.MIN_VALUE;
			
			int[] nodeColors = (i % 2 == 0) ? nodeColors1 : nodeColors2;
			int[] nodeColorsOther = (i % 2 == 0) ? nodeColors2 : nodeColors1;
			for (int j = 0; j < nodeColors.length; j++) {
				if (nodeColors[j] == color) {
					newNodeColors[j] = color;
					colorFreqsOther[nodeColorsOther[j]]--;
					nodeColorsOther[j] = -1;
				}
			}
		}
		
		//fill in remaining randomly
		for (int i = 0; i < newNodeColors.length; i++) {
			if (newNodeColors[i] == -1) {
				newNodeColors[i] = rand.nextInt(k);
			}
		}
		
		return new GcSolution(newNodeColors, nodeNeighbors);
	}
}
