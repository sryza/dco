package viz;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import pls.TspLsCityReader;
import pls.VizSaRunner;
import pls.tsp.TspLsCity;
import pls.tsp.TspUtils;

public class TestSaViz {
	public static void main(String[] args) throws IOException, InterruptedException {
//		TspCity[] cities = {
//				new TspCity(0, 5, 8),
//				new TspCity(1, 6, 10),
//				new TspCity(2, 8, 3)
//		};
		
		final double START_TEMP = 5.0;
//		final double START_TEMP = 0.0;
		final double ALPHA = .9995;
		
		File file = new File("../tsptests/eil101.258");
		TspLsCity[] cities = TspLsCityReader.read(file, Integer.MAX_VALUE).toArray(new TspLsCity[0]);		
		TspUtils.WRAP_NUM_NODES = cities.length;
		
		TspPanel panel = new TspPanel();
		panel.setScale(cities);
		panel.setTour(cities, true);
		final VizSaRunner runner = new VizSaRunner(cities, panel);
		
		JFrame frame = new JFrame("Traveling Salesmen Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		MouseListener clickListener = new MouseListener() {
			double temp = START_TEMP;
			@Override
			public void mouseClicked(MouseEvent arg0) {
				runner.runStepAndDisplay(temp);
				temp = temp * ALPHA;
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		};
		panel.addMouseListener(clickListener);
		
		double temp = START_TEMP;
		while (true) {
			runner.runStepAndDisplay(temp);
			temp = temp * ALPHA;
			Thread.sleep(30);
		}
	}
}
