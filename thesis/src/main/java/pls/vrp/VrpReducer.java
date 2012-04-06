package pls.vrp;

import pls.LnsSolutionData;
import pls.reduce.ChooserReducer;

public class VrpReducer extends ChooserReducer {

	public Class getSolutionDataClass() {
		return LnsSolutionData.class;
	}
}
