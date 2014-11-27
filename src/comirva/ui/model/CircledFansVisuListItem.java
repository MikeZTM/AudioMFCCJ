/**
 * 
 */
package comirva.ui.model;

import java.util.Vector;

import comirva.config.CircledFansConfig;
import comirva.data.DataMatrix;

/**
 * This class is for list items of circled lists in the visualisation list.
 * 
 * @author Florian Marchl
 */
public class CircledFansVisuListItem implements VisuListItem {
	private DataMatrix distMatrix;
	private Vector labels;
	private CircledFansConfig cfg;
	
	/**
	 * Constructs a new visualisation list item for a circled fans visu
	 * @param distMatrix the distance matrix
	 * @param labels the labels
	 * @param cfg the configuration
	 */
	public CircledFansVisuListItem(DataMatrix distMatrix, Vector labels, CircledFansConfig cfg) {
		this.distMatrix = distMatrix;
		this.labels = labels;
		this.cfg = cfg;
	}
	
	/**
	 * @return the distance data matrix
	 */
	public DataMatrix getDistMatrix() {
		return this.distMatrix;
	}
	
	/**
	 * @return the the label vector
	 */
	public Vector getLabels() {
		return this.labels;
	}
	
	/**
	 * @return the the configuration
	 */
	public CircledFansConfig getCfg() {
		return this.cfg;
	}
}
