package bnb.vassal;

import java.util.LinkedList;
import java.util.List;

public class VassalStats {
	private long askForWorkStart;
	
	private List<Long> askForWorkLats;
	
	public VassalStats() {
		askForWorkLats = new LinkedList<Long>();
	}
	
	public void reportAskForWorkStartTime(long time) {
		askForWorkStart = time;
	}
	
	public void reportAskForWorkEndTime(long time) {
		askForWorkLats.add(time-askForWorkStart);
	}
}
