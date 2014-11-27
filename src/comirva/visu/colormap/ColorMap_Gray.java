/*
 * Created on 26.11.2004
 */
package comirva.visu.colormap;

import java.awt.Color;

/**
 * This class defines a "Gray" colormap.
 * This colormap goes from back to white.
 * 
 * @author Markus Schedl
 */
public class ColorMap_Gray extends ColorMap {
	public ColorMap_Gray() {
		// create a color map
		super(Color.BLACK, Color.WHITE, 128);
	}
}
