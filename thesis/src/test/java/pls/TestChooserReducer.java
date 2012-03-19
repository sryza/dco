package pls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class TestChooserReducer {
	@Test
	public void testChooseKBest() {
		final int k = 3;
		ChooserReducer.SolutionData[] solDatas = {
				new ChooserReducer.SolutionData(5, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(10, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(4, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(3, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(27, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(1, null, -1, -1, -1, -1),
				new ChooserReducer.SolutionData(4, null, -1, -1, -1, -1)
		};
		
		ChooserReducer cr = new ChooserReducer();
		List<ChooserReducer.SolutionData> kbest = cr.chooseKBest(new ArrayList<ChooserReducer.SolutionData>(Arrays.asList(solDatas)), k);
		Assert.assertEquals(solDatas[2], kbest.get(0));
		Assert.assertEquals(solDatas[3], kbest.get(1));
		Assert.assertEquals(solDatas[5], kbest.get(2));
	}
}
