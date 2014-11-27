/*
 * Created on 05.07.2005
 */
package comirva.config;

import java.awt.*;
import java.io.Serializable;

import javax.swing.JTabbedPane;

/**
 * This class represents the global preferences for the visualization pane
 * and the data management area.
 * 
 * @author Markus Schedl
 */
public class VisuPreferences implements Serializable {
	
	// DEFAULT VALUES
	// visualisation pane
	// default values
	/** background of the visualisation pane. Standard value is <code>Color(250,250,255)</code> */
	public static final Color VISU_BACKGROUND = new Color(250,250,255);
	/** size of the area between visualisation and the eadge of the painting area. Standard value is <code>50</code> */
	public static final int VISU_BORDER_SIZE = 50;
	/** size of the font used for labels etc. Standard value is 10. */
	public static final int VISU_FONT_SIZE = 10;
	/** eps output is disabled by default */
	public static final boolean VISU_ENABLE_EPS = false;

	// data management preferences
	/** path where open/save dialogs start. Standard is the user's home directory */
	public static final String VISU_PATH = System.getProperty("user.home");
	/** the toolbar is fixed (not floatable) by default */
	public static final boolean VISU_TOOLBAR_FLOATABLE = false;
	/** the toolbar layout is {@link JTabbedPane#SCROLL_TAB_LAYOUT} by default 
	 *	which means that the tabs in one line and may be scrolled */
	public static final int VISU_TOOLBAR_LAYOUT = JTabbedPane.SCROLL_TAB_LAYOUT;
	/** the data matrix name is used for generating a visualisation name by default */
	public static final boolean VISU_USE_DATA_MATRIX_NAME = true;
	/** constant for index of setting: use all visualisation parameters */ 
	public static final int VISU_NAME_ALL = 0;
	/** constant for index of setting: use only non-standard configuration values */
	public static final int VISU_NAME_NONSTANDARD = 1;
	/** constant for index of setting: use no configuration values */
	public static final int VISU_NAME_NOTHING = 2;	
	/** the titles for the options concerning the visualisation name generation */
	public static final String[] VISU_NAME_NAMES = {
		"All", "Non-Standard only", "Nothing"
	};
	
	// VARIABLES
	// visualization pane
	/** the color of the vizualisation background */
	private Color backgroundColor;
	/** the size of the border */
	private int borderSize;
	/** the size of the label font */
	private int labelFontSize;
	/** switch for enabling/disabling EPS */
	private boolean enableEPS;
	
	// data management preferences
	/** String for last directory */
	private String lastDir;	
	/** flag for floatable toolbar */
	private boolean toolbarFloatable;
	/** flag for tab layout (scroll/wrap) */
	private int tabLayout;
	/** flag for using cofiguration information for naming new visualistions */
	private int visuName;
	/** flag for using data matrix name in visualisation name */
	private boolean useDataMatrixName;	
	
	/**
	 * Creates a new instance of the preferences object.
	 *  
	 * @param backgroundColor	the Color that defines the background color of the visualization pane
	 * @param borderSize		the border size of the visualization pane
	 * @param labelFontSize		the font size of the labels and other text of the visualization pane
	 * @param enableEPS			enable EPS-Output (slower graphics display!)
	 */
	public VisuPreferences(Color backgroundColor, int borderSize, int labelFontSize, boolean enableEPS){
		this();
		this.setVisuPreferences(backgroundColor, borderSize, labelFontSize, enableEPS);
	}
	
	/**
	 * Creates a new instance of the preferences object
	 * 
	 * @param lastDir			the last directory that was selected in a open/save dialog
	 * @param toolbarFloatable	the toolbar is floatable or not (floatable means that the user can move the toolbar around)
	 * @param tabLayout			the layout of the tabulators (see {@link JTabbedPane#SCROLL_TAB_LAYOUT} or {@link JTabbedPane#SCROLL_TAB_LAYOUT})
	 * @param visuName			defines how visualisation names are constructed (one of {@link #VISU_NAME_ALL}, {@link #VISU_NAME_NONSTANDARD} or {@value #VISU_NAME_NOTHING})
	 * @param useDataMatrixName append the data matrix name to visualisation name
	 */
	public VisuPreferences(String lastDir, boolean toolbarFloatable, int tabLayout, int visuName, boolean useDataMatrixName) {
		this();
		this.setDataManagementPreferences(lastDir, toolbarFloatable, tabLayout, visuName, useDataMatrixName);
	}
	
	/**
	 * Constructs a new instance of a preferences object with the standard values of the fields.
	 * The standard values are defined in constants. See the "See also" list below.
	 * @see #VISU_BACKGROUND
	 * @see #VISU_BORDER_SIZE
	 * @see #VISU_ENABLE_EPS
	 * @see #VISU_FONT_SIZE
	 * @see #VISU_PATH
	 * @see #VISU_TOOLBAR_FLOATABLE
	 * @see #VISU_TOOLBAR_LAYOUT
	 * @see #VISU_USE_DATA_MATRIX_NAME
	 */
	public VisuPreferences() {
		this.setVisuPreferences(VISU_BACKGROUND, VISU_BORDER_SIZE, VISU_FONT_SIZE, VISU_ENABLE_EPS);
		this.setDataManagementPreferences(VISU_PATH, VISU_TOOLBAR_FLOATABLE, VISU_TOOLBAR_LAYOUT, VISU_NAME_ALL, VISU_USE_DATA_MATRIX_NAME);
	}
	
	/**
	 * Set visualisation preferences. This method is also called by the constructor with the parameters passed to it.
	 * If no parameters are passed, the default values defined by the constants are used.
	 * 
	 * @param backgroundColor	the Color that defines the background color of the visualisation pane
	 * @param borderSize		the border size of the visualisation pane
	 * @param labelFontSize		the font size of the labels and other text of the visualisation pane
	 * @param enableEPS			enable EPS-Output (slower graphics display!)
	 */
	public void setVisuPreferences(Color backgroundColor, int borderSize, int labelFontSize, boolean enableEPS) {
		this.backgroundColor = backgroundColor;
		this.borderSize = borderSize;
		this.labelFontSize = labelFontSize;
		this.enableEPS = enableEPS;		
	}
	
	/** 
	 * Set data  management preferences. This method is also called by the constructor with the parameters passed to it.
	 * If no parameters are passed to the constructor this method is only called with the default values, 
	 * so the default values defined by the constants are used.
	 * 
	 * @param lastDir			the last directory that was selected in a open/save dialog
	 * @param toolbarFloatable	the toolbar is floatable or not (floatable means that the user can move the toolbar around)
	 * @param tabLayout			the layout of the tabulators (see {@link JTabbedPane#SCROLL_TAB_LAYOUT} or {@link JTabbedPane#SCROLL_TAB_LAYOUT})
	 * @param visuName			defines how visualisation names are constructed (one of {@link #VISU_NAME_ALL}, {@link #VISU_NAME_NONSTANDARD} or {@value #VISU_NAME_NOTHING})
	 * @param useDataMatrixName append the data matrix name to visualisation name
	 */
	public void setDataManagementPreferences(String lastDir, boolean toolbarFloatable, int tabLayout, int visuName, boolean useDataMatrixName) {
		this.lastDir = lastDir;
		this.toolbarFloatable = toolbarFloatable;
		if (tabLayout != JTabbedPane.SCROLL_TAB_LAYOUT && 
			tabLayout != JTabbedPane.WRAP_TAB_LAYOUT) 
			tabLayout  = JTabbedPane.SCROLL_TAB_LAYOUT;
		this.tabLayout = tabLayout;
		if (visuName != VISU_NAME_ALL &&
			visuName != VISU_NAME_NONSTANDARD &&
			visuName != VISU_NAME_NOTHING)
			visuName = VISU_NAME_NONSTANDARD;
		this.visuName = visuName;
		this.useDataMatrixName = useDataMatrixName;
	}
	
	/**
	 * Set data management preferences without changing the last directory setting. In fact it only calls
	 * {@link #setDataManagementPreferences(String, boolean, int, int, boolean)} with the current directory setting.
	 * 
	 * @param toolbarFloatable	the toolbar is floatable or not (floatable means that the user can move the toolbar around)
	 * @param tabLayout			the layout of the tabulators (see {@link JTabbedPane#SCROLL_TAB_LAYOUT} or {@link JTabbedPane#SCROLL_TAB_LAYOUT})
	 * @param visuName			defines how visualisation names are constructed (one of {@link #VISU_NAME_ALL}, {@link #VISU_NAME_NONSTANDARD} or {@value #VISU_NAME_NOTHING})
	 * @param useDataMatrixName append the data matrix name to visualisation name
	 * 
	 * @see #setDataManagementPreferences(String, boolean, int, int, boolean)
	 */
	public void setDataManagementPreferences(boolean toolbarFloatable, int tabLayout, int visuName, boolean useDataMatrixName) {
		this.setDataManagementPreferences(lastDir, toolbarFloatable, tabLayout, visuName, useDataMatrixName);
	}
	
	// GETTERS AND SETTERS
	/**
	 * Returns the background color.
	 * 
	 * @return	the background color of the visualization pane
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}
	/**
	 * Returns the border size.
	 * 
	 * @return	the border size (in pixels) of the visualization pane
	 */
	public int getBorderSize() {
		return this.borderSize;
	}
	/**
	 * Returns the standard font used for drawing labels. 
	 * 
	 * @return the size of the font which is used for drawing labels or other text within a visualization
	 */
	public int getLabelFontSize() {
		return this.labelFontSize;
	}
	/**
	 * Returns whether the EPS output option is enabled. 
	 * 
	 * @return	true, if EPS-output is enabled; false if disabled
	 */
	public boolean isEnableEPS() {
		return this.enableEPS;
	}

	/**
	 * the last directory the user has selected
	 * @return the last directory
	 */
	public String getLastDir() {
		return lastDir;
	}
	
	/**
	 * set last directory. This property is not set from the data management
	 * option dialog but after closing any file open/save dialog boxes. 
	 * @param lastDirectory the new last directory
	 */
	public void setLastDir(String lastDirectory) {
		if (lastDirectory != null && lastDirectory.length()>0) {
			this.lastDir = lastDirectory;
		}
	}
	
	/** 
	 * when a toolbar is floatable it means that it can be moved to
	 * another edge inside its container or own window. This property
	 * corresponds to the floatable property of a {@link JToolBar}
	 * @return false if toolbar cannot be moved
	 * @see JToolBar#isFloatable()
	 * @see JToolBar#setFloatable(boolean)
	 */
	public boolean isToolbarFloatable() {
		return toolbarFloatable;
	}

	/**
	 * this property determines whether the tabs are arranged in multiple
	 * lines or in one single line with buttons for scrolling. It
	 * corresponds to the layout policy of a {@link JTabbdedPane}
	 * @return tab layout policy
	 * @see JTabbedPane#getTabLayoutPolicy()
	 * @see JTabbedPane#setTabLayoutPolicy(int)
	 */
	public int getTabLayout() {
		return tabLayout;
	}

	/**
	 * this property determines how much of the visualisation configuration
	 * parameters should be used for generating a appropiate name for the new
	 * visualistion. The return value is one of these constants
	 * <ul>
	 * 	<li><b>{@link #VISU_NAME_ALL}:</b> use every configuration parameter
	 * 		(from the appropriate creation box) for generating the name.</li>
	 *  <li><b>{@link #VISU_NAME_NONSTANDARD}:</b> use only the configuration
	 *  	parameters (From the appropiate creation box) which are different
	 *  	from their standard values. If nothing is changed in the dialog by
	 * 		the user the effect will be the same as <code>VISU_NAME_NOTHING</code>,
	 * 		if all values are changed the result will be equal to option 
	 * 		<code>VIUS_NAME_ALL</code></li>
	 * 	<li><b>{@link #VISU_NAME_NOTHING}:</b> do not use any configuration
	 * 		parameter information for the name of the visualisation
	 * </ul>
	 * @return the visualisation name option
	 */
	public int getVisuName() {
		return visuName;
	}

	/**
	 * this property determines if the name of the data matrix should be appended
	 * to the name of the visualisation. It may be to turned off if the data matrix
	 * name is not needed or not suitable to identify a visualisation or the resulting
	 * name would be unreadable long. 
	 * @return use data matrix name
	 */
	public boolean useDataMatrixName() {
		return useDataMatrixName;
	}
   	 
}
