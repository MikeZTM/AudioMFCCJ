/*
 * Created on 26.11.2004
 */
package comirva.visu.colormap;

import java.awt.Color;

/**
 * This class defines a "Sun" colormap.
 * This colormap goes from yellow to orange.
 * 
 * @author Markus Schedl
 */
public class ColorMap_Sun extends ColorMap {
	public ColorMap_Sun() {
		// create a color map
		super(Color.YELLOW, new Color(230,0,0), 128);
	}
}
