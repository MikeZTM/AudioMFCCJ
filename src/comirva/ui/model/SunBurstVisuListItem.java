package comirva.ui.model;

import java.util.Vector;

import comirva.config.SunburstConfig;
import comirva.data.DataMatrix;
import comirva.data.SunburstNode;

public class SunBurstVisuListItem implements VisuListItem {
	/** the term occurence matrix */
	private DataMatrix toMatrix;
	/** the term vector */
	private Vector termVector;
	/** the sunburst configuration */
	private SunburstConfig config;
	/** the sunburst root node */
	private SunburstNode rootNode;
	
	/** Constructs a new sun burst list item for the visualisation list
	 * @param toMatrix
	 * @param termVector
	 * @param config
	 */
	public SunBurstVisuListItem(DataMatrix toMatrix, Vector termVector, SunburstConfig config, SunburstNode rootNode) {
		this.toMatrix = toMatrix;
		this.termVector = termVector;
		this.config = config;
		this.rootNode = rootNode;
	}
	
	/** Returns the configuration
	 * @return the config
	 */
	public SunburstConfig getConfig() {
		return config;
	}
	
	/** Returns the term vector
	 * @return the termVector
	 */
	public Vector getTermVector() {
		return termVector;
	}
	
	/** Returns the Term Occurence Matrix
	 * @return the toMatrix
	 */
	public DataMatrix getToMatrix() {
		return toMatrix;
	}
	
	/** Returns the Sunburst Root Node
	 * @return the sunburst root node
	 */
	public SunburstNode getRootNode() {
		return rootNode;
	}
}
