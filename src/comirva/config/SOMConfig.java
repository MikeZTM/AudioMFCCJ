/*
 * Created on 15.02.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a SOM-Calculation.
 * It is used to pass a configuration to the SOM-instance.
 * 
 * @author Markus Schedl
 */
public class SOMConfig {
	private int mapUnitsInRow;
	private int mapUnitsInColumn;
	private int trainingMethod;
	private int trainingLength;
	private int initMethod;
	private boolean circular;
	
	/**
	 * Creates a new instance of a SOM-Configuration.
	 *  
	 * @param mapUnitsInRow 	the number of map units in each row
	 * @param mapUnitsInColumn	the number of map units in each column
	 * @param initMethod		the initialization method {@link comirva.mlearn.SOM}
	 * @param trainingMethod	the training method {@link comirva.mlearn.SOM}
	 * @param trainingLength	the training length in epochs (1 epoch = number of iterations equals number of data items)
	 * @see comirva.mlearn.SOM
	 */
	public SOMConfig(int mapUnitsInRow, int mapUnitsInColumn, int initMethod, int trainingMethod, int trainingLength, boolean circular){
		this.mapUnitsInRow = mapUnitsInRow;
		this.mapUnitsInColumn = mapUnitsInColumn;
		this.initMethod = initMethod;
		this.trainingMethod = trainingMethod;
		this.trainingLength = trainingLength;
		this.circular = circular;
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
	 * Returns the method used for training the SOM.
	 * 
	 * @return	an int indicating the training method (for a list, see @link comirva.mlearn.SOM)
	 * @see comirva.mlearn.SOM
	 */
	public int getTrainingMethod() {
		return this.trainingMethod;
	}
	
	/**
	 * Returns the training length in epochs.
	 * 
	 * @return	the number of epochs the training is performed
	 */
	public int getTrainingLength() {
		return this.trainingLength;
	}

	public boolean isCircular() {
		return circular;
	}

	public void setCircular(boolean circular) {
		this.circular = circular;
	}

}
