/*
 * Created on 01.12.2004
 */
package comirva.config;

/**
 * This class represents a configuration for a Circled-Fans-Visualization.
 * It is used to pass a configuration to the visualization pane.
 * 
 * @author Markus Schedl
 */
public class CircledFansConfig {
	private int maxBarThickness;
	private int maxDataItemsL0;
	private int maxDataItemsL1;
	private int angleFanL1;
	private boolean normalizeData;
	private boolean randomCenter;
	private int idxCenter;
	
	/**
	 * Creates a new instance of a CircledFans-Configuration.
	 *  
     * @param maxBarThickness		the maximum thickness of a bar
     * @param maxDataItemsL0		the maximum number of data items on level 0 (inner circle)
     * @param maxDataItemsL1		the maximum number of data items on level 1 (outer fans)
     * @param angleFanL1			the angular extent for fans on level 1
     * @param randomCenter			a boolean indicating whether a random element should be picked as center	
     * @param idxCenter				the index of the data item in the center of the visualization (ignored if randomCenter is <code>true</code>)
     * @param normalizeData			a boolean indicating whether the data should be normalized for every fan or not
	 */
	public CircledFansConfig(int maxBarThickness, int maxDataItemsL0, int maxDataItemsL1, int angleFanL1, boolean randomCenter, int idxCenter, boolean normalizeData){
		this.maxBarThickness = maxBarThickness;
		this.maxDataItemsL0 = maxDataItemsL0;
		this.maxDataItemsL1 = maxDataItemsL1;
		this.angleFanL1 = angleFanL1;
		this.randomCenter = randomCenter;
		this.idxCenter = idxCenter;
		this.normalizeData = normalizeData;
	}
	
	/**
	 * Returns the maximum bar thickness.
	 * 
	 * @return	the maximum bar thickness
	 */
	public int getMaxBarThickness() {
		return this.maxBarThickness;
	}
	
	/**
	 * Returns the maximum number of data items on level 0.
	 * 
	 * @return	the maximum number of data items on level 0
	 */
	public int getMaxDataItemsL0() {
		return this.maxDataItemsL0;
	}
	
	/**
	 * Returns the maximum number of data items on level 1.
	 * 
	 * @return	the maximum number of data items on level 1
	 */
	public int getMaxDataItemsL1() {
		return this.maxDataItemsL1;
	}
	
  	/**
   	 * Returns the angular extent for fans on level 1.
   	 * 
   	 * @return the angular extent for fans on level 1
   	 */
	public int getAngleFanL1() {
		return this.angleFanL1;
	}
	
	/**
	 * Sets the index of the data item in the center of the visualization.  
	 * 
	 * @param idxCenter		the index of the centermost data item
	 */
	public void setIndexCenter(int idxCenter) {
		this.idxCenter = idxCenter;
	}
	
	/**
	 * Returns the index of the data item that resides in the center of the visualization.
	 * 
	 * @return	the index of the center data item
	 */
	public int getIndexCenter() {
		return this.idxCenter;
	}
	
   	/**
   	 * Returns whether the data should be normalized for every fan.
   	 * 
   	 * @return a boolean indicating whether the data is normalized for every fan
   	 */	
	public boolean isNormalizeData() {
		return this.normalizeData;
	}

	/**
	 * Returns whether a random center should be chosen or the center is selected by the user.
	 * 
	 * @return a boolean indicating whether a random element should be picked as center 
	 */
	public boolean isRandomCenter() {
		return this.randomCenter;
	}

}
