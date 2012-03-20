package pls.vrp;

import pls.PlsMapper;

public class VrpMapper extends PlsMapper {

	@Override
	public Class getSolutionClass() {
		return VrpSolution.class;
	}

	@Override
	public Class getRunnerClass() {
		return VrpLnsRunner.class;
	}

}
