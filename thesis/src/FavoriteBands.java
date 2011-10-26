import java.io.*;

import java.util.*;

public class FavoriteBands {
	public static void main(String[] args) throws Exception {
		File f = new File("C:/Users/Sandy/Documents/BCA/survey results 2011.csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		Map<String, Integer> bandHist = new HashMap<String, Integer>();
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split(",\\s*");

			Set<String> bands = new HashSet<String>();
			for (int i = 0; i < Math.min(3, tokens.length); i++) {
				if (!tokens[i].matches("\\s*")) {
					bands.add(tokens[i].toLowerCase().trim());
				}
			}
			for (String band : bands) {
				Integer count = bandHist.get(band);
				if (count == null) {
					count = new Integer(0);
				}
				bandHist.put(band, count+1);
			}
		}
		
		for (String str : bandHist.keySet()) {
			System.out.println(str + ", " + bandHist.get(str));
		}
	}
}
