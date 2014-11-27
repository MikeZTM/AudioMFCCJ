/*
 * Created on 07.04.2005
 */
package comirva.config.defaults;

import comirva.config.*;

/**
 * <p>This class defines default values for the DataMatrix-normalization.</p>
 * 
 * @author Markus Schedl
 */
public class DataMatrixNormalizeDefaultConfig extends DataMatrixNormalizeConfig {
	// default values
	private static double lowerBound = 0;
	private static double upperBound = 1;
	private static boolean isLinear = true;
	private static int scope = DataMatrixNormalizeConfig.SCOPE_MATRIX;
	
	/**
	 * <p>Creates a default configuration for a DataMatrix-normalization.</p>
	 * The default values are:
	 * 		<li><code>lowerBound = 0.0</code></li>
	 * 		<li><code>upperBound = 1.0</code></li>
	 * 		<li><code>isLinear = true</code></li>
	 *		<li><code>scope = DataMatrixNormalizeConfig.SCOPE_MATRIX</code></li>
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_MATRIX
	 */
	public DataMatrixNormalizeDefaultConfig() {
		super(lowerBound, upperBound, isLinear, scope);
	}
}