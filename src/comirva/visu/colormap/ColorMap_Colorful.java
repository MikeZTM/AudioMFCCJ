/*
 * Created on 26.11.2004
 */
package comirva.visu.colormap;

import java.awt.Color;

/**
 * This class defines a "Colorful" colormap.
 * It roughly equals the Matlab-colormap "jet".
 * 
 * @author Markus Schedl
 */
public class ColorMap_Colorful extends ColorMap {
	public ColorMap_Colorful() {
		// create a color map
		super(new Color(0, 0, 143), new Color(128, 0, 0), 128);
		// set the color fix points
		this.addColor(new Color(0, 0, 255), 16);
		this.addColor(new Color(0, 255, 255), 48);
		this.addColor(new Color(255, 255, 0), 80);
		this.addColor(new Color(255, 0, 0), 112);
	}
}
