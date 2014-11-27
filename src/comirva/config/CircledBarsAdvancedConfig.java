/*
 * Created on 12.03.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a Circled-Bars-Advanced-Visualization.
 * It is used to pass a configuration to the visualization pane.
 * 
 * @author Markus Schedl
 */
public class CircledBarsAdvancedConfig {
	private int showNearestN;
	private boolean sortByDistance;
	
	/**
	 * Creates a new instance of an Circled-Bars-Advanced-Configuration.
	 *  
	 * @param showNearestN		the number of nearest data items the user wishes to display	
	 * @param sortByDistance	<code>true</code> if the data items should be sorted by distance,
   	 * <code>false</code> if they are to be sorted by meta-data names
	 */
	public CircledBarsAdvancedConfig(int showNearestN, boolean sortByDistance){
		this.showNearestN = showNearestN;
		this.sortByDistance = sortByDistance;
	}
	
   	/**
   	 * Returns the number of nearest data items the user wishes to display.
   	 * 
   	 * @return the number of data items that should be displayed 
   	 */
	public int getShowNearestN() {
		return this.showNearestN;
	}
	
  	/**
   	 * Returns whether the data items are to be sorted by their distance 
   	 * to the selected data item (or alphabetically by their meta-data name).
   	 * 
   	 * @return 	<code>true</code> if the data items should be sorted by distance,
   	 * <code>false</code> if they are to be sorted by meta-data names
   	 */
	public boolean isSortByDistance() {
		return this.sortByDistance;
	}
   	 
}
