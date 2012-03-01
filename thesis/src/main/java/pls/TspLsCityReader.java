package pls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pls.tsp.TspLsCity;

public class TspLsCityReader {
	public static ArrayList<TspLsCity> read(File f, int maxCities) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine().trim();
		int numVertices = Integer.parseInt(line);
		ArrayList<TspLsCity> nodes = new ArrayList<TspLsCity>(Math.min(maxCities, numVertices));
		for (int i = 0; i < Math.min(maxCities, numVertices); i++) {
			String[] tokens = br.readLine().trim().replaceAll("[)(]", "").split("[, ]");
			nodes.add(new TspLsCity(i, (int)Double.parseDouble(tokens[0]), (int)Double.parseDouble(tokens[1])));
		}
		
		br.close();
		return nodes;
	}
}
