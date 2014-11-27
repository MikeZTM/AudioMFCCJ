/*
 * Created on 11.03.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the CSR-visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class CSRDefaultConfig extends CSRConfig {
	// default values
	private static int numberOfNeighborsPerPrototype = 5;
	private static int[] idxPrototypes = {19, 197, 295, 360, 1165, 1244, 1521, 1714, 1989}; //{0, 16, 32, 53, 76, 91, 108, 121, 128, 155, 160, 180, 199, 210}; //{1, 6, 10, 12, 17, 24, 28, 32, 34, 39, 47, 49, 58, 63, 68, 71, 75, 81, 88, 91, 97, 99};
	// idxPrototypes = {1, 6, 10, 12, 17, 24, 28, 32, 34, 39, 47, 49, 58, 63, 68, 71, 75, 81, 88, 91, 97, 99};	// prototypes for C103a_MR
	// idxPrototypes = {0, 16, 32, 53, 76, 91, 108, 121, 128, 155, 160, 180, 199, 210};	// prototypes for C224a_MR
	// idxPrototypes = {19, 197, 295, 360, 1165, 1244, 1521, 1714, 1989};	// prototypes	 for C2000a_MR
	private static int maxEdgeThickness = 5;
	private static int prototypesVertexDiameter = 15;
	private static int neighborsVertexDiameter = 10;
	private static int iterationsNeighborsPlacement = 5000;
	
	/**
	 * <p>Creates a default configuration for a Continuous Similarity Ring (CSR).</p>
	 * The default values are:
	 * 		<li><code>numberOfNeighborsPerPrototype = 5</code></li>
	 * 		<li><code>idxPrototypes = {0}</code></li>
	 * 		<li><code>maxEdgeThickness = 5</code></li>
	 * 		<li><code>prototypesVertexDiameter = 15</code></li>
	 * 		<li><code>neighborsVertexDiameter = 10</code></li>
	 * 		<li><code>iterationsNeighborsPlacement = 5000</code></li>
	 */
	public CSRDefaultConfig() {
		super(numberOfNeighborsPerPrototype, idxPrototypes, maxEdgeThickness, prototypesVertexDiameter, neighborsVertexDiameter, iterationsNeighborsPlacement);
	}
}