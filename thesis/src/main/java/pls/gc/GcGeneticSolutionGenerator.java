package pls.gc;

import java.util.List;

import org.apache.hadoop.io.BytesWritable;

import pls.map.NextRoundGenerator;

public class GcGeneticSolutionGenerator implements NextRoundGenerator {

	@Override
	public List<BytesWritable> generateNextRoundInputs(List<BytesWritable> solBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsProblem() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setProblem(BytesWritable problemBytes) {
		// TODO Auto-generated method stub
		
	}

}
