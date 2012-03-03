package viz;

import javax.swing.*;

import pls.TspLsCityReader;
import pls.VizSaRunner;
import pls.tsp.TspLsCity;
import pls.tsp.TspLsUtils;

import bnb.tsp.TspUtils;

import tsp.TspCity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TspPanel extends JPanel{
	
	private static final Color CITY_COLOR = Color.black;
	private static final Color PATH_COLOR = Color.red;
	
	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 600;
	private static final int DOT_RADIUS = 3;
	
	private static final int PADDING = 20;
	
	private TspCity[] cities;
	private boolean circuit;
	
	private double scale;
	private int xOffset;
	private int yOffset;
	
	public TspPanel() {
		setBackground(Color.white);
		setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
	}
	
	/**
	 * Infers scale from set of cities;
	 * @param cities
	 */
	public void setScale(TspCity[] cities) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for (TspCity city : cities) {
			minX = Math.min(minX, city.x);
			minY = Math.min(minY, city.y);
			maxX = Math.max(maxX, city.x);
			maxY = Math.max(maxY, city.y);
		}
		
		xOffset = minX;
		yOffset = minY;
		double xScale = (MAX_WIDTH-PADDING*2) / (double)(maxX - minX);
		double yScale = (MAX_HEIGHT-PADDING*2) / (double)(maxY - minY);
		scale = Math.min(xScale, yScale);
	}
	
	public void setTour(TspCity[] cities, boolean circuit) {
		this.cities = cities;
		this.circuit = circuit;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//draw lines
		g.setColor(PATH_COLOR);
		for (int i = 1; i < cities.length; i++) {
			int x1 = cityXToPixX(cities[i].x);
			int y1 = cityYToPixY(cities[i].y);
			int x2 = cityXToPixX(cities[i-1].x);
			int y2 = cityYToPixY(cities[i-1].y);
			g.drawLine(x1, y1, x2, y2);
		}
		
		if (circuit) {
			int x1 = cityXToPixX(cities[0].x);
			int y1 = cityYToPixY(cities[0].y);
			int x2 = cityXToPixX(cities[cities.length-1].x);
			int y2 = cityYToPixY(cities[cities.length-1].y);
			g.drawLine(x1, y1, x2, y2);
		}

		//draw dots
		g.setColor(CITY_COLOR);
		for (TspCity city : cities) {
			int x = cityXToPixX(city.x);
			int y = cityYToPixY(city.y);
			g.fillOval(x-DOT_RADIUS, y-DOT_RADIUS, DOT_RADIUS*2, DOT_RADIUS*2);
		}
	}
	
	private int cityXToPixX(int x) {
		return (int)((x - xOffset)*scale) + PADDING;
	}
	
	private int cityYToPixY(int y) {
		return (int)((y - yOffset)*scale) + PADDING;
	}
	
	//for testing
	public static void main(String[] args) throws IOException {
//		TspCity[] cities = {
//				new TspCity(0, 5, 8),
//				new TspCity(1, 6, 10),
//				new TspCity(2, 8, 3)
//		};
		
		File file = new File("../tsptests/eil51.258");
		TspLsCity[] cities = TspLsCityReader.read(file, Integer.MAX_VALUE).toArray(new TspLsCity[0]);		
		
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
			double temp = 5.0;
			@Override
			public void mouseClicked(MouseEvent arg0) {
				runner.runStepAndDisplay(temp);
				temp = temp * .9;
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
	}
}
