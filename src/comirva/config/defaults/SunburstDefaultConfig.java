/*
 * Created on 16.12.2005
 */
package comirva.config.defaults;

import java.util.Vector;

import comirva.config.*;

/**
 * <p>This class defines default values for the Sunburst-Calculation/Visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class SunburstDefaultConfig extends SunburstConfig {
	// default values
    private static int maxItemsPerNode = 20;								// maximum children per node
    private static int maxDepth = 8;										// maximum depth of hierarchy
    private static double minImportance = 1.0/360.0;						// threshold for importance (nodes with importance below it will be excluded from the sunburst), set to 1° angular extent	
    private static Vector<String> rootTerms = new Vector<String>();		// terms that must be contained in the root node
    private static int minFontSize = 8;									// minimum font size for labels
    private static int maxFontSize = 20;									// maximum font size for labels
	
	/**
	 * <p>Creates a default configuration for a Sunburst.</p>
	 * The default values are:
	 * 		<li><code>maxItemsPerNode = 20</code></li>
	 * 		<li><code>maxDepth = 8</code></li>
	 * 		<li><code>minImportance = 1.0/360.0 (equals 1° angular extent)</code></li>
	 * 		<li><code>rootTerms = new Vector<String>()</li></code>
	 * 		<li><code>minFontSize = 8</li></code>
	 * 		<li><code>maxFontSize = 20</li></code>
	 */
	public SunburstDefaultConfig() {
		super(maxItemsPerNode, maxDepth, minImportance, rootTerms, minFontSize, maxFontSize);
	}
}