package comirva.config.defaults;

import comirva.config.SOMConfig;
import comirva.mlearn.SOM;

/**
 * Default configuration for SOM
 * @author Florian Marchl
 *
 */
public class SOMDefaultConfig extends SOMConfig {

	// map units in row/column defaults are read from som
	/** the default value for 'circular' */
	private static final boolean DEFAULT_CIRCULAR = false;
	/** the default value for training length */
	private static final int DEFAULT_TRAINING_LENGTH = 5;	
	/** the default value for init */
	private static final int DEFAULT_INIT = SOM.INIT_RANDOM;
	/** the default value for train */
	private static final int DEFAULT_TRAIN = SOM.TRAIN_BATCH;
	
	/** 
	 * Create a new instance of a SOM-Configuration using the default values
	 * @param mapUnitsInRow
	 * @param mapUnitsInColumn
	 */
	public SOMDefaultConfig(int mapUnitsInRow, int mapUnitsInColumn) {
		super(mapUnitsInRow, mapUnitsInColumn, DEFAULT_INIT, DEFAULT_TRAIN, DEFAULT_TRAINING_LENGTH, DEFAULT_CIRCULAR);
	}
	
	/**
	 * Create a new instance of a SOM-Configuration using default values
	 * for map units in row/column too.
	 */
	public SOMDefaultConfig() {
		this(-1,-1);
	}	
}
