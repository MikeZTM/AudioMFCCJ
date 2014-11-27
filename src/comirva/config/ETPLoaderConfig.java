/*
 * Created on 28.12.2005
 */
package comirva.config;

import java.io.File;

/**
 * This class represents a configuration for an ETP-Loader.
 * It is used to pass a configuration to the loader.
 * 
 * @author Markus Schedl
 */
public class ETPLoaderConfig {
	private boolean loadTerms;
	private boolean loadDocPaths;
	private boolean loadTO;
	private boolean loadTF;
	private boolean loadDF;
	private boolean loadTFxIDF;
	
	/**
	 * Creates a new instance of an ETPLoader-configuartion.
	 * 
	 * @param loadTerms			flag indicating whether the terms are to be loaded
	 * @param loadDocPaths		flag indicating whether the paths to the documents the ETP is based on are to be loaded
	 * @param loadTO			flag indicating whether the term occurrences are to be loaded
	 * @param loadTF			flag indicating whether the term frequencies are to be loaded
	 * @param loadDF			flag indicating whether the document frequencies are to be loaded
	 * @param loadTFxIDF		flag indicating whether the TFxIDF values are to be loaded
	 */
	public ETPLoaderConfig(boolean loadTerms, boolean loadDocPaths, boolean loadTO, boolean loadTF, boolean loadDF, boolean loadTFxIDF) {
		this.loadTerms = loadTerms;
		this.loadDocPaths = loadDocPaths;
		this.loadTO = loadTO;
		this.loadTF = loadTF;
		this.loadDF = loadDF;
		this.loadTFxIDF = loadTFxIDF;
	}

	/**
	 * @return Returns the loadDF.
	 */
	public boolean isLoadDF() {
		return loadDF;
	}

	/**
	 * @return Returns the loadDocPaths.
	 */
	public boolean isLoadDocPaths() {
		return loadDocPaths;
	}

	/**
	 * @return Returns the loadTerms.
	 */
	public boolean isLoadTerms() {
		return loadTerms;
	}

	/**
	 * @return Returns the loadTF.
	 */
	public boolean isLoadTF() {
		return loadTF;
	}

	/**
	 * @return Returns the loadTFxIDF.
	 */
	public boolean isLoadTFxIDF() {
		return loadTFxIDF;
	}

	/**
	 * @return Returns the loadTO.
	 */
	public boolean isLoadTO() {
		return loadTO;
	}

}
