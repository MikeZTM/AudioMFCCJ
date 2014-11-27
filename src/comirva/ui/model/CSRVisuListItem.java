package comirva.ui.model;

import java.util.Vector;

import comirva.config.CSRConfig;
import comirva.data.DataMatrix;

/** 
 * This class implements a CSR Visualisation list item
 * @author Florian Marchl
 */
public class CSRVisuListItem implements VisuListItem {
	private DataMatrix distMatrix;
	private Vector labels;
	private CSRConfig config;
	
	/**
	 *  Constructs a new CSR list item
	 * @param distMatrix data matrix 
	 * @param labels configuration labels
	 * @param config visualisation configuration
	 */
	public CSRVisuListItem(DataMatrix distMatrix, Vector labels, CSRConfig config) {
		this.distMatrix = distMatrix;
		this.labels = labels;
		this.config = config;
	}

	/**
	 * @return the config
	 */
	public CSRConfig getConfig() {
		return config;
	}

	/**
	 * @return the distMatrix
	 */
	public DataMatrix getDistMatrix() {
		return distMatrix;
	}

	/**
	 * @return the labels
	 */
	public Vector getLabels() {
		return labels;
	}
}
