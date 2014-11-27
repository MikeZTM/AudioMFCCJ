/*
 * Created on 16.12.2005
 */
package comirva.config;

import java.util.Vector;

/**
 * This class represents a configuration for a Sunburst-Calculation.
 * 
 * @author Markus Schedl
 */
public class SunburstConfig {
    private int maxItemsPerNode;								// maximum children per node
    private int maxDepth;										// maximum depth of hierarchy
    private double minImportance;								// threshold for importance (nodes with importance below it will be excluded from the sunburst), set to 1° angular extent	
    private Vector<String> rootTerms = new Vector<String>();	// terms that must be contained in the root node
	private int minFontSize;									// minimum font size
	private int maxFontSize;									// maximum font size
	
	/**
	 * Creates a new instance of a Sunburst-Configuration.
	 *  
	 * @param maxItemsPerNode	maximum children per node
	 * @param maxDepth			maximum depth of hierarchy
	 * @param minImportance		threshold for importance (nodes with importance below it will be excluded from the sunburst)
	 * @param rootTerms			a Vector<String> containing the terms that must be included in the root node
	 * @see comirva.data.SunburstNode
	 */
	public SunburstConfig(int maxItemsPerNode, int maxDepth, double minImportance, Vector<String> rootTerms, int minFontSize, int maxFontSize) {
		this.maxItemsPerNode = maxItemsPerNode;
		this.maxDepth = maxDepth;
		this.minImportance = minImportance;
		this.rootTerms = rootTerms;
		this.minFontSize = minFontSize;
		this.maxFontSize = maxFontSize;
	}

	/**
	 * Returns the maximum depth of the hierarchy to be included in the sunburst.
	 * 
	 * @return returns the maximum hierarchy depth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Returns the maximum number of subnodes per sunburst-node.
	 * 
	 * @return returns the maximum number of subnodes for every node of the sunburst.
	 */
	public int getMaxItemsPerNode() {
		return maxItemsPerNode;
	}

	/**
	 * Returns the threshold for the importance of a node in degrees.
	 * 
	 * @return returns the minimum importance of a node (so that it will still be included in the sunburst) 
	 */
	public double getMinImportance() {
		return minImportance;
	}

	/**
	 * Returns a list of terms that are included in the root node.
	 * Based on these terms, the sunburst will be constructed.
	 * 
	 * @return returns the terms of the root node
	 */
	public Vector<String> getRootTerms() {
		return rootTerms;
	}

	/**
	 * Returns the maximum font size for labels.
	 * 
	 * @return returns the maximum font size in pt
	 */
	public int getMaxFontSize() {
		return maxFontSize;
	}

	/**
	 * Returns the minimun font size for labels.
	 * 
	 * @return returns the minimum font size in pt
	 */
	public int getMinFontSize() {
		return minFontSize;
	}

}
