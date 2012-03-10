package viz;

import java.awt.Color;

public class GraphPanelUtils {
	public static Color randomColor() {
		return new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));
	}
	
	public static int cityToPix(int x, int offset, double scale, int padding) {
		return (int)((x - offset)*scale) + padding;
	}
}
