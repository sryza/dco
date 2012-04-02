package gc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GcReader {
	public static GcProblem read(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()).startsWith("c"));
		
		String[] tokens = line.split("\\s");
		int nNodes = Integer.parseInt(tokens[2]);
		List<Integer>[] nodeNeighborLists = new List[nNodes];
		for (int i = 0; i < nNodes; i++) {
			nodeNeighborLists[i] = new ArrayList<Integer>();
		}
		while ((line = br.readLine()) != null) {
			tokens = line.split("\\s");
			int node1 = Integer.parseInt(tokens[1])-1;
			int node2 = Integer.parseInt(tokens[2])-1;
			nodeNeighborLists[node1].add(node2);
			nodeNeighborLists[node2].add(node1);
		}
		int[][] nodeNeighbors = new int[nNodes][];
		for (int i = 0; i < nNodes; i++) {
			nodeNeighbors[i] = new int[nodeNeighborLists[i].size()];
			for (int j = 0; j < nodeNeighborLists[i].size(); j++) {
				nodeNeighbors[i][j] = nodeNeighborLists[i].get(j);
			}
		}
		
		return new GcProblem(nodeNeighbors);
	}
}
