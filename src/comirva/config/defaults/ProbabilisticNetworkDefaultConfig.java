/*
 * Created on 11.03.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the Probabilistic-Network-visualization.</p>
 * The values speficied here are loaded when the user wishes to show such
 * a visualization the first time or when he/she intends to load the default values.
 * 
 * @author Markus Schedl
 */
public class ProbabilisticNetworkDefaultConfig extends ProbabilisticNetworkConfig {
	// default values
	private static int maxEdgeThickness = 6;
	private static int maxDistReduction = 100;
	private static int maxVertexDiameter = 18;
	private static int minVertexDiameter = 4;
	private static double probCorrection = 5;
	private static int adaptationRunsEpochs = 10;
	private static double adaptationThreshold = 0.25;
	private static int gridSize = 1;

	/**
	 * <p>Creates a default configuration for a Probabilistic-Network-visualization.</p>
	 * The default values are:
	 * 		<li><code>maxEdgeThickness = 6</code></li>
	 * 		<li><code>maxDistReduction = 100</code></li>
	 *		<li><code>maxVertexDiameter = 18</code></li>
	 *		<li><code>minVertexDiameter = 4</code></li>
	 *		<li><code>probCorrection = 50</code></li>
	 *		<li><code>adaptationRunsEpochs = 10</code></li>
	 *		<li><code>adaptationThreshold = 0.25</code></li>
	 *		<li><code>gridSize = 1</code></li>
	 */
	public ProbabilisticNetworkDefaultConfig() {
		super(maxEdgeThickness, maxDistReduction, maxVertexDiameter, minVertexDiameter, probCorrection, adaptationRunsEpochs, adaptationThreshold, gridSize);
	}
}