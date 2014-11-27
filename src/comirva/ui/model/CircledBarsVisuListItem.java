package comirva.ui.model;

import java.util.Vector;

import comirva.config.CircledBarsAdvancedConfig;
import comirva.data.DataMatrix;

/**
 * The list item class of circled bars visualisation
 * (both basic and advanced). For the basic visualisation the
 * advanced configuration property remains empty (null).
 * 
 * @author Florian Marchl
 */
public class CircledBarsVisuListItem implements VisuListItem {
	private DataMatrix distance;
	private Vector labels;	
	private CircledBarsAdvancedConfig cbConfig;
	
	/** 
	 * Creates a list item for an advanced circled bars visualisation
	 * given by its visu thread and its configuration
	 * @param cbVisu	the visualisation thread
	 * @param cbConfig	the advanced visualisation configuration
	 */
	public CircledBarsVisuListItem(DataMatrix distance, Vector labels, CircledBarsAdvancedConfig cbConfig) {
		this.distance = distance;
		this.labels = labels;
		this.cbConfig = cbConfig;
	}
	
	public CircledBarsVisuListItem(DataMatrix distance, Vector labels) {
		this(distance, labels, null);
	}
	
	public CircledBarsVisuListItem(DataMatrix distance) {
		this(distance, null, null);
	}
	
	public DataMatrix getDistanceVector() {
		return distance;
	}
	
	public Vector getLabels() {
		return labels;
	}
	
	public CircledBarsAdvancedConfig getCircledBarsAdvancedConfig() {
		return cbConfig;
	}
}
