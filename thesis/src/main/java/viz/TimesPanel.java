package viz;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;
import java.util.List;

@SuppressWarnings("serial")
public class TimesPanel extends JPanel {
	
	//each deep element is the length of a time segment
	private List<List<Segment>> segments;

	public void setTimes(List<List<Long>> times) {
		long minStart = Long.MAX_VALUE;
		long maxEnd = Long.MIN_VALUE;
		
		for (List<Long> nodeTimes : times) {
			
		}
	}
	
	public void paintComponent(Graphics g) {
		
	}
	
	private class Segment {
		private long start;
		private long length;
		private Color color;
	}
}
