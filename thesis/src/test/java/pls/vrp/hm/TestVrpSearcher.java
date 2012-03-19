package pls.vrp.hm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pls.vrp.VrpProblem;
import pls.vrp.VrpReader;
import pls.vrp.VrpSolution;

public class TestVrpSearcher {
	@Test
	public void testInitialize() throws IOException {
		final int numCities = 5;
		VrpProblem problem = VrpReader.readSolomon(new File("../vrptests/r1.txt"), numCities);
		List[] routesArr = {
			Arrays.asList(1, 0), Arrays.asList(4)
		};
		List<List<Integer>> partialRoutes = Arrays.asList((List<Integer>[])routesArr);
		List<Integer> unrouted = Arrays.asList(2, 3);
		VrpSolution partialSol = new VrpSolution(partialRoutes, unrouted, problem);
		
		VrpSearcher searcher = new VrpSearcher(problem);
		VrpCpSearchNode root = searcher.initialize(partialSol);

//		final int actualCurCost = (int)Math.sqrt((49-17)*(49-17) + );
//		Assert.assertEquals(actualCurCost, root.curCost);
		CustInsertionPoints points2 = root.custsInsertionPoints[2];
		CustInsertionPoints points3 = root.custsInsertionPoints[3];
		System.out.println(points2.getInsertionPointCosts());
		System.out.println(points3.getInsertionPointCosts());
		System.out.println(points2.getMinCost());
		System.out.println(points3.getMinCost());
		System.out.println(root.boundRemaining.getBound());
		System.out.println(root.curCost);
	}
}
