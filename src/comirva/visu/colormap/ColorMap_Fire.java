/*
 * Created on 26.11.2004
 */
package comirva.visu.colormap;

import java.awt.Color;

/**
 * This class defines a "Fire" colormap.
 * It roughly equals the Matlab-colormap "hot".
 * 
 * @author Markus Schedl
 */
public class ColorMap_Fire extends ColorMap {
	public ColorMap_Fire() {
		// create a color map
		super(Color.BLACK, Color.WHITE, 128);
		// set the color fix points
		this.addColor(Color.RED, 48);
		this.addColor(Color.YELLOW, 96);
	}
}
