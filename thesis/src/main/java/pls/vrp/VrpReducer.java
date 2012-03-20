package pls.vrp;

import pls.ChooserReducer;
import pls.LnsSolutionData;

public class VrpReducer extends ChooserReducer {

	public Class getSolutionDataClass() {
		return LnsSolutionData.class;
	}
}
