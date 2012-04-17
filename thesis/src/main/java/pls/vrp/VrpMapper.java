package pls.vrp;

import pls.map.PlsMapper;

public class VrpMapper extends PlsMapper {

	@Override
	public Class getSolutionClass() {
		return VrpPlsSolution.class;
	}

	@Override
	public Class getRunnerClass() {
		return VrpLnsRunner.class;
	}

	@Override
	public Class getHelperDataClass() {
		return LnsExtraData.class;
	}
}
