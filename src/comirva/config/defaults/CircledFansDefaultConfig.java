/*
 * Created on 12.03.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the Circled-Fans-visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class CircledFansDefaultConfig extends CircledFansConfig {
	// default values
	private static int maxBarThickness = 15;
	private static int maxDataItemsL0 = 10;
	private static int maxDataItemsL1 = 6;
	private static int angleFanL1 = 60;
	private static boolean normalizeData = false;
	private static boolean randomCenter = true;
	private static int idxCenter = -1;
	
	/**
	 * <p>Creates a default configuration for an Circled-Fans-visualization.</p>
	 * The default values are:
     * 		<li><code>maxBarThickness = 15</code></li>
     * 		<li><code>maxDataItemsL0 = 10</code></li>
     * 		<li><code>maxDataItemsL1 = 6</code></li>
     * 		<li><code>angleFanL1 = 60</code></li>
     * 		<li><code>randomCenter = true	</code></li>
     * 		<li><code>idxCenter = -1</code></li>
     *		<li><code>normalizeData = false</code></li>
	 */
	public CircledFansDefaultConfig() {
		super(maxBarThickness, maxDataItemsL0, maxDataItemsL1, angleFanL1, randomCenter, idxCenter, normalizeData);
	}
}