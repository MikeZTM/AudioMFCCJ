/*
 * Created on 11.03.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the Circled-Bars-Advanced-visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class CircledBarsAdvancedDefaultConfig extends CircledBarsAdvancedConfig {
	// default values
	private static int showNearestN = 15;
	private static boolean sortByDistance = true;
	
	/**
	 * <p>Creates a default configuration for a Circled-Bars-Advanced-visualization.</p>
	 * The default values are:
	 * 		<li><code>showNearestN = 15</code></li>
	 * 		<li><code>sortByDistance = true</code></li>
	 */
	public CircledBarsAdvancedDefaultConfig() {
		super(showNearestN, sortByDistance);
	}
}