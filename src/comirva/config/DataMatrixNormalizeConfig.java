/*
 * Created on 20.05.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a DataMatrix-Normalization.
 * 
 * @author Markus Schedl
 */
public class DataMatrixNormalizeConfig {
	/**
	 * <code>SCOPE_MATRIX</code> is used to set the normalization to be performed on the complete matrix.
	 */
	public static final int SCOPE_MATRIX = 0;
	/**
	 * <code>SCOPE_PER_ROW</code> is used to set the normalization to be performed for each row of the matrix.
	 */
	public static final int SCOPE_PER_ROW = 1;
	/**
	 * Comment for <code>SCOPE_PER_COLUMN</code> is used to set the normalization to be performed for each column of the matrix.
	 */
	public static final int SCOPE_PER_COLUMN = 2;
	
	private double lowerBound;
	private double upperBound;
	private boolean isLinear;
	private int scope;
	
	/**
	 * Creates a new instance of a DataMatrixNormalize-Configuration.
	 *  
	 * @param lowerBound		the lower bound of the projection range
	 * @param upperBound		the upper bound of the projection range
	 * @param isLinear			indicates whether normalization should be conducted linearly or logarithmically
	 * @param scope				the scope of the normalization
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_MATRIX
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_ROW
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_COLUMN
	 */
	public DataMatrixNormalizeConfig(double lowerBound, double upperBound, boolean isLinear, int scope){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isLinear = isLinear;
		this.scope = scope;
	}
	
	/**
	 * Returns the lower bound of the projection range.
	 * 
	 * @return	the lower bound of the projection range
	 */
	public double getLowerBound() {
		return this.lowerBound;
	}
	/**
	 * Returns the upper bound of the projection range.
	 * 
	 * @return	the upper bound of the projection range
	 */
	public double getUpperBound() {
		return this.upperBound;
	}
	/**
	 * Returns whether the normalization should be performed linearly or logarithmically.
	 * 
	 * @return	a boolean indicating whether the normalization should be performed linearly or logarithmically
	 */
	public boolean isLinear() {
		return this.isLinear;
	}
	/**
	 * Returns the normalization scope (complete matrix, normalization for each row, normalization for each column).
	 * 
	 * @return the scope of the normalization 
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_MATRIX
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_ROW
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_COLUMN
	 */
	public int getScope() {
		return this.scope;
	}
}
