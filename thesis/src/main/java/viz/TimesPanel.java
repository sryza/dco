package viz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class TimesPanel extends JPanel {
	
	private static final Logger LOG = Logger.getLogger(TimesPanel.class);
	
	private static final int DEFAULT_HEIGHT = 700;
	private static final int DEFAULT_WIDTH = 700;
	
	private static final Color BACKGROUND_COLOR = Color.white;
	
	private static final int TOP_PADDING = 15;
	private static final int BAR_HEIGHT = 15;
	private static final int BETWEEN_THREADS_PADDING = 10;
	private static final int BETWEEN_NODES_PADDING = 10;
	private static final int SIDE_PADDING = 10; //for each side
	
	private static final Color[] WORKING_COLORS = {Color.green, Color.blue, Color.red, Color.yellow};
	private static final Color[] NOT_WORKING_COLORS = {Color.black, Color.black, Color.black, Color.black};
//	private static final Color[] NOT_WORKING_COLORS = {lighter(WORKING_COLORS[0]), lighter(WORKING_COLORS[1]), lighter(WORKING_COLORS[2]), lighter(WORKING_COLORS[3])};
	
	private Point mouseClickLocation;
	
	//each deep element is the length of a time segment
	private List<List<List<Long>>> nodes;
	private long minStart;
	private long maxEnd;

	/**
	 * 
	 * @param nodes
	 * @param finishTime
	 * 		when the lord says that it finished
	 */
	public TimesPanel(List<List<List<Long>>> nodes, long finishTime) {
		minStart = Long.MAX_VALUE;
		maxEnd = Long.MIN_VALUE;
		
		setBackground(BACKGROUND_COLOR);
		
		//find min and max
		for (List<List<Long>> threads : nodes) {
			for (List<Long> times : threads) {
				minStart = Math.min(minStart, times.get(0));
				maxEnd = Math.max(maxEnd, times.get(times.size()-1));
			}
		}
		
		if (maxEnd > finishTime) {
			System.out.println("maxEnd > finishTime");
		}
		maxEnd = Math.max(maxEnd, finishTime);
		
		this.nodes = nodes;
		
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		addMouseListener(new ClickListener());
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = getWidth();
		int height = getHeight();
		
		int barStartX = SIDE_PADDING;
		int barLength = width - SIDE_PADDING * 2;
		double pxPerMs = (double)barLength / (maxEnd - minStart);
		
		int barStartY = TOP_PADDING;

		for (int i = 0; i < nodes.size(); i++) {
			List<List<Long>> threads = nodes.get(i);
			Color workingColor = WORKING_COLORS[i % WORKING_COLORS.length];
			Color notWorkingColor = NOT_WORKING_COLORS[i % WORKING_COLORS.length];
			
			for (int j = 0; j < threads.size(); j++) {
				List<Long> times = threads.get(j);
				boolean working = true;
				for (int k = 1; k < times.size(); k++) {
					long prevTime = times.get(k-1);
					long time = times.get(k);

					int segStartX = (int)(barStartX + (prevTime - minStart) * pxPerMs);
					int segLen = (int)((time - prevTime) * pxPerMs);
					
					g.setColor(working? workingColor : notWorkingColor);
					g.fillRect(segStartX, barStartY, segLen, BAR_HEIGHT);
					
					working = !working;
				}
				barStartY += BAR_HEIGHT;
				
				if (j != threads.size()-1) {
					barStartY += BETWEEN_THREADS_PADDING;
				}
			}
			
			barStartY += BETWEEN_NODES_PADDING;
		}
		
		//draw limits
		g.setColor(Color.black);
		g.drawLine(SIDE_PADDING, TOP_PADDING, SIDE_PADDING, barStartY);
		g.drawLine(width-SIDE_PADDING, TOP_PADDING, width-SIDE_PADDING, barStartY);
		
		//draw time at bottom
		if (mouseClickLocation != null) {
			int ms = (int)((mouseClickLocation.x-barStartX) / pxPerMs);
			int strStartX = SIDE_PADDING;
			int strStartY = height - 12;
			g.drawString("" + ms, strStartX, strStartY);
		}
	}
	
	private static Color lighter(Color col) {
		float hsbVals[] = Color.RGBtoHSB( col.getRed(), col.getGreen(), col.getBlue(), null);
		Color highlight = Color.getHSBColor( hsbVals[0], hsbVals[1], 0.5f * ( 1f + hsbVals[2] ));
		return highlight;
	}
	
	private class ClickListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			mouseClickLocation = event.getPoint();
			repaint();
		}
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		File dir = new File("/home/sryza/logs/stats/");
		int testId = 14;
		long finishTime = 1328561548728l;
		
		ObjectMapper mapper = new ObjectMapper();
		
		//each node has a list of threads.  each thread is a list of longs.  then we have a list for each node.
		
		List<List<List<Long>>> nodes = new ArrayList<List<List<Long>>>();
		
		File f;
		for (int i = 0; i < 100; i++) {
			if ((f = new File(dir, i + "_" + testId + ".stats")).exists()) {
				LOG.info("about to parse: " + f.getAbsolutePath());
				
				Map<Object, Object> map = (Map<Object, Object>)mapper.readValue(f, Map.class);
				List<List<Long>> threads = (List<List<Long>>)map.get("toggleWorkingLists");
				nodes.add(threads);
			}
		}
		
		System.out.println("data:");
		System.out.println(nodes);
		
		JPanel timesPanel = new TimesPanel(nodes, finishTime);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(timesPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
