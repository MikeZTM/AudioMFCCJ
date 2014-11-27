/*
 * Created on 20.05.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the SDH-visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class SDHDefaultConfig extends SDHConfig {
	// default values
	private static int spread = 3;
	private static int iterations = 5;
	private static int fractalComponent = 10;
	
	/**
	 * <p>Creates a default configuration for an SDH.</p>
	 * The default values are:
	 * 		<li><code>spread = 3</code></li>
	 * 		<li><code>iterations = 5</code></li>
	 * 		<li><code>fractalComponent = 10</code></li>
	 */
	public SDHDefaultConfig() {
		super(spread, iterations, fractalComponent);
	}
}