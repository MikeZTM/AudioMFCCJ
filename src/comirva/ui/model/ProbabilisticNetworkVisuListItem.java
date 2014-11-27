package comirva.ui.model;

import java.util.Vector;

import comirva.config.ProbabilisticNetworkConfig;
import comirva.data.DataMatrix;

/**
 * This class is for list items of probabilistic network visualisations in the visualisation list
 * @author Florian Marchl
 *
 */
public class ProbabilisticNetworkVisuListItem implements VisuListItem {
	private DataMatrix distMatrix;
	private Vector labels;
	private ProbabilisticNetworkConfig config;
	
	/**
	 * construct new list item
	 * @param distMatrix the distance matrix
	 * @param labels the labels
	 * @param config the configuration
	 */
	public ProbabilisticNetworkVisuListItem(DataMatrix distMatrix, Vector labels, ProbabilisticNetworkConfig config) {
		super();
		this.distMatrix = distMatrix;
		this.labels = labels;
		this.config = config;
	}

	/**
	 * @return the config
	 */
	public ProbabilisticNetworkConfig getConfig() {
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
