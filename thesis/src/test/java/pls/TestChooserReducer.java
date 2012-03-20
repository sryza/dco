package pls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pls.vrp.VrpReducer;

public class TestChooserReducer {
	@Test
	public void testChooseKBest() {
		final int k = 3;
		SolutionData[] solDatas = {
				new SaSolutionData(5, null, -1, -1, -1, -1),
				new SaSolutionData(10, null, -1, -1, -1, -1),
				new SaSolutionData(4, null, -1, -1, -1, -1),
				new SaSolutionData(3, null, -1, -1, -1, -1),
				new SaSolutionData(27, null, -1, -1, -1, -1),
				new SaSolutionData(1, null, -1, -1, -1, -1),
				new SaSolutionData(4, null, -1, -1, -1, -1)
		};
		
		ChooserReducer cr = new VrpReducer();
		List<SolutionData> kbest = cr.chooseKBest(new ArrayList<SolutionData>(Arrays.asList(solDatas)), k);
		Assert.assertEquals(solDatas[2], kbest.get(0));
		Assert.assertEquals(solDatas[3], kbest.get(1));
		Assert.assertEquals(solDatas[5], kbest.get(2));
	}
}
