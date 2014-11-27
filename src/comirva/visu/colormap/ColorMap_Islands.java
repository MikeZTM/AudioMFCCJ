/*
 * Created on 26.11.2004
 */
package comirva.visu.colormap;

import java.awt.Color;

/**
 * This class defines an "Islands" colormap.
 * 
 * @author Markus Schedl
 */
public class ColorMap_Islands extends ColorMap {
	private static Color DARK_BLUE = new Color(0,0,150);
	private static Color LIGHT_BLUE = new Color(0,100,255);
	private static Color YELLOW_GREEN = new Color(200,255,0);
	private static Color DARK_GREEN = new Color(40,180,0);
	
	public ColorMap_Islands() {
        // create a color map
		super(DARK_BLUE, Color.WHITE, 512);
		// set the color fix points
		this.addColor(LIGHT_BLUE, 50);
		this.addColor(Color.YELLOW, 57);
		this.addColor(YELLOW_GREEN, 62);
		this.addColor(Color.GREEN, 120);
		this.addColor(DARK_GREEN, 260);
		this.addColor(Color.ORANGE, 320);
		this.addColor(Color.GRAY, 400);
	}
}
