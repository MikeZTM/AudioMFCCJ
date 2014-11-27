/*
 * Created on 05.07.2005
 */
package comirva.config.defaults;

import comirva.config.*;
import java.awt.*;

/**
 * <p>This class defines default values for the visualization preferences.</p>
 * The values speficied here are loaded when the user activates the 
 * "Default Values" button of the "Visualization Preferences" dialog.
 * 
 * @author Markus Schedl
 */
public class VisuDefaultPreferences extends VisuPreferences {
	// default values
	private static Color backgroundColor = new Color(250,250,255);
	private static int borderSize = 50;
	private static int labelFontSize = 10;
	private static boolean enableEPS = false;
	
	/**
	 * <p>Creates a default configuration for visualization preferences.</p>
	 * The default values are:
	 * 		<li><code>backgroundColor = new Color(240,240,245)</code></li>
	 * 		<li><code>borderSize = 50</code></li>
	 * 		<li><code>labelFont = 10</code></li>
	 */
	public VisuDefaultPreferences() {
		super();	// standard constructor initialises to default values!
	}
}