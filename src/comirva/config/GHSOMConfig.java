/*
 * Created on 15.05.2006
 */
package comirva.config;

/**
 * This class represents a configuration for a GHSOM-Calculation.
 * It is used to pass a configuration to the GHSOM-instance.
 * 
 * @author Markus Dopler
 */
public class GHSOMConfig {
	private int mapUnitsInRow;
	private int mapUnitsInColumn;
	private int trainingLength;
	private int initMethod;
	private double growThreshold;
	private double expandThreshold;
	private int maxSize;
	private int maxDepth;
	private boolean orientated;
	
	private boolean circular;
	private boolean onlyFirstCircular;
	
	/**
	 * Creates a new instance of a GHSOM-Configuration.
	 *  
	 * @param mapUnitsInRow 	the number of map units in each row
	 * @param mapUnitsInColumn	the number of map units in each column
	 * @param initMethod		the initialization method {@link comirva.mlearn.SOM}
	 * @param trainingLength	the training length in epochs (1 epoch = number of iterations equals number of data items)
	 * @see comirva.mlearn.GHSOM
	 */
	public GHSOMConfig(int mapUnitsInRow, int mapUnitsInColumn, int initMethod, int trainingLength, double growThreshold, double expandThreshold, int maxSize, int maxDepth, boolean circular, boolean onlyFirstCircular, boolean orientated){
		this.mapUnitsInRow = mapUnitsInRow;
		this.mapUnitsInColumn = mapUnitsInColumn;
		this.initMethod = initMethod;
		this.trainingLength = trainingLength;
		this.growThreshold = growThreshold;
		this.expandThreshold = expandThreshold;
		this.circular = circular;
		this.onlyFirstCircular = onlyFirstCircular;
		this.maxSize = maxSize;
		this.maxDepth = maxDepth;
		this.orientated = orientated;
	}
	
	/**
	 * Returns the number of map units in each row.
	 * 
	 * @return	the number of map units in each row
	 */
	public int getMapUnitsInRow() {
		return this.mapUnitsInRow;
	}

	/**
	 * Returns the number of map units in each column.
	 * 
	 * @return	the number of map units in each column
	 */
	public int getMapUnitsInColumn() {
		return this.mapUnitsInColumn;
	}
	
	/**
	 * Returns the method used for initializing the codebook of the SOM.
	 * 
	 * @return	an int indicating the initialization method (for a list, see @link comirva.mlearn.SOM)
	 * @see comirva.mlearn.SOM
	 */
	public int getInitMethod() {
		return this.initMethod;
	}
		
	/**
	 * Returns the training length in epochs.
	 * 
	 * @return	the number of epochs the training is performed
	 */
	public int getTrainingLength() {
		return this.trainingLength;
	}

	/**
	 * Returns the threshold which decides when to expand a map unit.
	 * 
	 * @return	the threshold which decides when to expand a map unit
	 */
	public double getExpandThreshold() {
		return expandThreshold;
	}

	/**
	 * Returns the threshold which decides when a map has to grow.
	 * 
	 * @return	the threshold which decides when a map has to grow
	 */
	public double getGrowThreshold() {
		return growThreshold;
	}
	
	public boolean isCircular() {
		return circular;
	}

	public void setCircular(boolean circular) {
		this.circular = circular;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isOnlyFirstCircular() {
		return onlyFirstCircular;
	}

	public void setOnlyFirstCircular(boolean onlyFirstCircular) {
		this.onlyFirstCircular = onlyFirstCircular;
	}

	public boolean isOrientated() {
		return orientated;
	}

	public void setOrientated(boolean orientated) {
		this.orientated = orientated;
	}

}

