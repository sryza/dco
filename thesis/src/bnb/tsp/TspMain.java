package bnb.tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import bnb.lord.LordRunner;
import bnb.vassal.VassalServer;

public class TspMain {
	public static void main(String[] args) throws IOException {
		File f = new File("../eil22.txt");
		int numCores = Runtime.getRuntime().availableProcessors() * 4;
		System.out.println("numCores: " + numCores);

		LordRunner lordRunner = new LordRunner();
		VassalServer vassalServer = new VassalServer(lordRunner, numCores);
		City[] cities = read(f);
		TspTreeNode root = new TspTreeNode(cities, cities[0], 1, null);
		lordRunner.runJob(root, new TspProblemSpec(), /*Double.MAX_VALUE*/300, Arrays.asList(vassalServer));
	}
	
	public static City[] read(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine().trim();
		int numVertices = Integer.parseInt(line);
		City[] nodes = new City[numVertices];
		for (int i = 0; i < numVertices; i++)
		{
			String[] tokens = br.readLine().trim().replaceAll("[)(]", "").split("[, ]");
			nodes[i] = new City((int)Double.parseDouble(tokens[0]), (int)Double.parseDouble(tokens[1]), i);
		}
		
		br.close();
		return nodes;
	}
}
