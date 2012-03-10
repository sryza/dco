package pls.vrp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VrpReader {
	public static VrpProblem readSolomon(File f, int numCities) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] xCoors = new int[numCities];
		int[] yCoors = new int[numCities];
		int[] demands = new int[numCities];
		int[] windowStarts = new int[numCities];
		int[] windowEnds = new int[numCities];
		int[] serviceTimes = new int[numCities];
		
		String line = br.readLine();
		int capacity = Integer.parseInt(line.trim());
		line = br.readLine();
		String[] tokens = line.trim().split("\\s+");
		int depotX = (int)(Double.parseDouble(tokens[1]));
		int depotY = (int)(Double.parseDouble(tokens[2]));
		//get coordinates of depot
//		while ((line = br.readLine()) != null) {
		for (int i = 0; i < numCities; i++) {
			line = br.readLine();
			tokens = line.trim().split("\\s+");
			//CUST NO.   XCOORD.   YCOORD.    DEMAND   READY TIME   DUE DATE   SERVICE TIME
			int x = (int)(Double.parseDouble(tokens[1]));
			xCoors[i] = (x);
			int y = (int)(Double.parseDouble(tokens[2]));
			yCoors[i] = (y);
			int demand = (int)(Double.parseDouble(tokens[3]));
			demands[i] = (demand);
			int windowStart = (int)(Double.parseDouble(tokens[4]));
			windowStarts[i] = (windowStart);
			int windowEnd = (int)(Double.parseDouble(tokens[5]));
			windowEnds[i] = (windowEnd);
			int serviceTime = (int)(Double.parseDouble(tokens[6]));
			serviceTimes[i] = (serviceTime);
		}
		
		VrpProblem problem = new VrpProblem(demands, xCoors, yCoors, serviceTimes, 
				windowStarts, windowEnds, depotX, depotY, capacity);
		return problem;
	}
}
