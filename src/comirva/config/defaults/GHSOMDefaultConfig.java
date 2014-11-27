package comirva.config.defaults;

import comirva.config.GHSOMConfig;
import comirva.mlearn.SOM;

/**
 * Default GHSOM Configuration
 * @author Florian Marchl
 *
 */
public class GHSOMDefaultConfig extends GHSOMConfig {
	
	// default configuration values
	private static final int 		DEFAULT_MAP_UNITS_IN_ROW 	= 2;
	private static final int 		DEFAULT_MAP_UNITS_IN_COLUMN	= 2;
	private static final int 		DEFAULT_TRAINING_LENGTH 	= 5;
	private static final int 		DEFAULT_INIT_METHOD 		= SOM.INIT_RANDOM;
	private static final double		DEFAULT_GROW_THRESHOLD 		= 0.6;
	private static final double		DEFAULT_EXPAND_THRESHOLD 	= 0.1;
	private static final int		DEFAULT_MAX_SIZE 			= -1;
	private static final int		DEFAULT_MAX_DEPTH 			= -1;
	private static final boolean	DEFAULT_CIRCULAR 			= false;
	private static final boolean	DEFAULT_ONLY_FIRST_CIRCULAR	= false;
	private static final boolean	DEFAULT_ORIENTATED 			= true;
	
	/** 
	 * Create a default GHSOM Configuration instance
	 */
	public GHSOMDefaultConfig() {
		super(DEFAULT_MAP_UNITS_IN_ROW, DEFAULT_MAP_UNITS_IN_COLUMN, DEFAULT_INIT_METHOD,
				DEFAULT_TRAINING_LENGTH, DEFAULT_GROW_THRESHOLD, DEFAULT_EXPAND_THRESHOLD,
				DEFAULT_MAX_SIZE, DEFAULT_MAX_DEPTH, DEFAULT_CIRCULAR, 
				DEFAULT_ONLY_FIRST_CIRCULAR, DEFAULT_ORIENTATED);
	}
}
