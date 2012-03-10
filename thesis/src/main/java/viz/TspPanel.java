package viz;

import javax.swing.*;

import pls.TspLsCityReader;
import pls.VizSaRunner;
import pls.tsp.TspLsCity;
import pls.tsp.TspLsUtils;

import bnb.tsp.TspUtils;

import tsp.TspCity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TspPanel extends JPanel{
	
	private static final Color CITY_COLOR = Color.black;
	private static final Color PATH_COLOR = Color.red;
	private static final Color BEST_PATH_COLOR = Color.cyan;
	
	private static final int MAX_WIDTH = 800;
	private static final int MAX_HEIGHT = 600;
	private static final int DOT_RADIUS = 3;
	
	private static final int PADDING = 20;
	
	private TspCity[] allCities;
	private TspCity[] cities;
	private TspCity[] bestCities;
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
		allCities = new TspCity[cities.length];
		System.arraycopy(cities, 0, allCities, 0, cities.length);
		
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
	
	public void setBest(TspCity[] cities) {
		this.bestCities = cities;
		repaint();
	}
	
	public void clearCurTour() {
		cities = null;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (bestCities != null) {
			g.setColor(BEST_PATH_COLOR);
			drawPath(g, bestCities, true, 3);
		}
		
		//draw lines
		if (cities != null) {
			g.setColor(PATH_COLOR);
			drawPath(g, cities, circuit, 1);
		}
		
		//draw dots
		g.setColor(CITY_COLOR);
		for (TspCity city : allCities) {
			int x = cityXToPixX(city.x);
			int y = cityYToPixY(city.y);
			g.fillOval(x-DOT_RADIUS, y-DOT_RADIUS, DOT_RADIUS*2, DOT_RADIUS*2);
		}
	}
	
	private void drawPath(Graphics g, TspCity[] path, boolean circuit, int thickness) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(thickness));
		
        
        
		for (int i = 1; i < path.length; i++) {
			int x1 = cityXToPixX(path[i].x);
			int y1 = cityYToPixY(path[i].y);
			int x2 = cityXToPixX(path[i-1].x);
			int y2 = cityYToPixY(path[i-1].y);
			g.setColor(GraphPanelUtils.randomColor());
			g.drawLine(x1, y1, x2, y2);
		}
		
		if (circuit) {
			int x1 = cityXToPixX(path[0].x);
			int y1 = cityYToPixY(path[0].y);
			int x2 = cityXToPixX(path[path.length-1].x);
			int y2 = cityYToPixY(path[path.length-1].y);
//			g.setColor(randomColor());
			g.drawLine(x1, y1, x2, y2);
		}
	}
		
	private int cityXToPixX(int x) {
		return (int)((x - xOffset)*scale) + PADDING;
	}
	
	private int cityYToPixY(int y) {
		return (int)((y - yOffset)*scale) + PADDING;
	}
}
