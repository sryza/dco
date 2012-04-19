package pls.vrp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import pls.SolutionData;

public class VrpExtraDataHandler {
	private static final Logger LOG = Logger.getLogger(VrpExtraDataHandler.class);
	
	private Random rand;
	private int maxRoutes;
	
	public VrpExtraDataHandler(Random rand, int maxRoutes) {
		this.rand = rand;
		this.maxRoutes = maxRoutes;
	}
	
	public List<Writable> makeNextRoundHelperDataFromExtraData(List<Writable> extraDatas, int numToMake) {
		//make a big ol' list of all the routes
		List<List<Integer>> routes = new ArrayList<List<Integer>>();
		for (Writable extraData : extraDatas) {
			LnsExtraData vrpExtraData = (LnsExtraData)extraData;
			routes.addAll(vrpExtraData.getNeighborhoods());
		}
		LOG.info("Received " + routes.size() + " routes for helper data");
		
		int numRoutesPerHelperData = Math.min(routes.size(), 100);
		
		List<Writable> results = new ArrayList<Writable>(numToMake);
		int routesIndex = 0;
		for (int i = 0; i < numToMake; i++) {
			List<List<Integer>> helperDataRoutes = new ArrayList<List<Integer>>(numRoutesPerHelperData);
			for (int j = 0; j < numRoutesPerHelperData; j++) {
				helperDataRoutes.add(routes.get(routesIndex));
				routesIndex = (routesIndex + 1) % routes.size();
			}
			LnsExtraData helperData = new LnsExtraData(helperDataRoutes);
			results.add(helperData);
		}
		
		return results;
	}
	
	public List<LnsExtraData> makeNextRoundHelperDataFromExtraData2(List<LnsExtraData> extraDatas, int numToMake) {
		List<LnsExtraData> results = new ArrayList<LnsExtraData>(numToMake);
		for (int i = 0; i < numToMake; i++) {
			ArrayList<Integer> indexes = new ArrayList<Integer>(extraDatas.size());
			ArrayList<LnsExtraData> remDatas = new ArrayList<LnsExtraData>(extraDatas.size());
			
			Iterator<LnsExtraData> iter = extraDatas.iterator();
			for (int j = 0; j < extraDatas.size(); j++) {
				LnsExtraData extraData = iter.next();
				if (extraData.getNeighborhoods().size() > 0) {
					remDatas.add(extraData);
					indexes.add(0);
				}
			}
			
			List<List<Integer>> neighbsList = new ArrayList<List<Integer>>(maxRoutes);
			while (neighbsList.size() <= maxRoutes && remDatas.size() > 0) {
				int dataIndex = rand.nextInt(remDatas.size());
				int neighbIndex = indexes.get(dataIndex);
				List<Integer> neighb = remDatas.get(dataIndex).getNeighborhoods().get(neighbIndex);
				neighbsList.add(neighb);
				
				indexes.set(dataIndex, neighbIndex+1);
				if (indexes.get(dataIndex) >= remDatas.get(dataIndex).getNeighborhoods().size()) {
					indexes.remove(dataIndex);
					remDatas.remove(dataIndex);
				}
			}
			results.add(new LnsExtraData(neighbsList));
		}
		return results;
	}
	
	public Map<SolutionData, LnsExtraData> makeNextRoundHelperDatas3(List<SolutionData> solDatas, List<SolutionData> toMakeFor) 
		throws IOException {
		Map<SolutionData, LnsExtraData> results = new HashMap<SolutionData, LnsExtraData>();
		Map<SolutionData, VrpSolution> solDataSols = new HashMap<SolutionData, VrpSolution>();
		for (SolutionData solData : solDatas) {
			BytesWritable bytes = solData.getBestSolutionBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes.getBytes(), 0, bytes.getLength());
			DataInputStream dis = new DataInputStream(bais);
			VrpPlsSolution plsSol = new VrpPlsSolution(false);
			plsSol.readFields(dis);
			solDataSols.put(solData, plsSol.getSolution());
		}
		
		for (SolutionData solToMakeFor : toMakeFor) {
			List<List<Integer>> routes = new ArrayList<List<Integer>>();
			for (SolutionData solData : solDataSols.keySet()) {
				if (solData != solToMakeFor) {
					VrpSolution sol = solDataSols.get(solData);
					routes.addAll(sol.getRoutes());
				}
			}
			Collections.shuffle(routes);
			LnsExtraData helperData = new LnsExtraData(routes);
			results.put(solToMakeFor, helperData);
		}
		
		return results;
	}
}
