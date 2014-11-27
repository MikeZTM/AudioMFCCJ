/*
 * Created on 03.11.2004
 */
package comirva.visu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import comirva.MainUI.PopupMenuMouseAdapter;
import comirva.config.CSRConfig;
import comirva.config.CircledBarsAdvancedConfig;
import comirva.config.CircledFansConfig;
import comirva.config.GHSOMConfig;
import comirva.config.ProbabilisticNetworkConfig;
import comirva.config.SDHConfig;
import comirva.config.SOMConfig;
import comirva.config.SunburstConfig;
import comirva.config.VisuPreferences;
import comirva.data.DataMatrix;
import comirva.data.SunburstNode;
import comirva.mlearn.GHSOM;
import comirva.mlearn.SDH;
import comirva.mlearn.SOM;
import comirva.visu.colormap.ColorMap;
import comirva.visu.epsgraphics.EpsCanvas;
import comirva.visu.epsgraphics.EpsGraphics2D;


/**
 * This class implements a visualization pane for various kinds of visualizations.
 * It provides a collection of data structures for different visualizations.
 * The drawing itself is done by specific classes that run as threads to
 * avoid blocking the user interface.
 *
 * @author Markus Schedl
 */
public class VisuPane extends EpsCanvas {
	// constants for the the different types of visualizations
	/**
	 * <code>TYPE_SOMGRID</code> defines the visualization of a SOM-Grid.
	 */
	public static final int TYPE_SOMGRID = 0;
	/**
	 * <code>TYPE_SDH</code> defines the visualization of a Smoothed Data Histogram (SDH).
	 */
	public static final int TYPE_SDH = 1;
	/**
	 * <code>TYPE_DISTANCE_VECTOR_CIRCLED_BARS</code> defines the visualization of a distance vector with circled bars.
	 */
	public static final int TYPE_DISTANCE_VECTOR_CIRCLED_BARS = 3;
	/**
	 * <code>TYPE_DISTANCE_MATRIX_CIRCLED_FANS</code> defines the visualization of a distance matrix with circled fans.
	 */
	public static final int TYPE_DISTANCE_MATRIX_CIRCLED_FANS = 4;
	/**
	 * <code>TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK</code> defines the visualization of a distance matrix with a probabilistic network.
	 */
	public static final int TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK = 6;
	/**
	 * <code>TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING</code> defines the visualization of a distance matrix with a continuous similarity ring.
	 */
	public static final int TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING = 7;
	/**
	 * <code>TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST</code> defines the visualization of a term occurrence matrix with a sunburst.
	 */
	public static final int TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST = 8;
	/**
	 * <code>TYPE_MDMGRID</code> defines the visualization of a MDM-Grid.
	 */
	public static final int TYPE_MDMGRID = 9;
	/**
	 * <code>TYPE_SOMGRID</code> defines the visualization of a SOM-Grid.
	 */
	public static final int TYPE_GHSOMGRID = 10;
	/**
	 * <code>TYPE_EXPERIMENTAL</code> defines a visualization for experimental use.
	 */
	
	public static final int TYPE_EXPERIMENTAL = 100;

	private int visuType = -1;					// the type of the current visualization (-1 by default means that no type has been specified)
	protected VisuPreferences visuPreferences;	// user preferences for the visualization pane
	protected ColorMap cm;						// the colormap for all visualizations
	protected Vector moElements = new Vector();	// Vector to store position (and additional information) of elements for mouse over detection

	// the different visualizations provided
	private SOMGridVisu somgv;
	private GHSOMGridVisu ghsomgv;
	private MDMGridVisu mdmgv;
	private SDHVisu sdhv;
	private CircledBarsVisu cbv;
	private CircledFansVisu cfv;
	private ProbabilisticNetworkVisu pnv;
	private ContinuousSimilarityRingVisu csrv;
	private SunburstVisu sbv;
	private ExperimentalVisu ev;
	// data structures for the visualizations
	protected SOM som;							// ...for SOM-visualizations
	protected GHSOM ghsom;						// ...for GHSOM-visualizations
	protected SDH sdh;							// ...for SDH-visualizations
	protected DataMatrix distMat;				// ...for visualizations of distance matrix
	protected DataMatrix toMat;					// ...for visualizations of term occurrence matrix
	protected SunburstNode sbRoot;				// ...for visualizing a sunburst
	protected Vector labels;					// ...for labels for distance matrix
	// configurations
	protected SOMConfig somCfg;					// configuration for SOMs
	protected GHSOMConfig ghsomCfg;				// configuration for SOMs
	protected SDHConfig sdhCfg;					// configuration for SDH-Visualizations
	protected CircledBarsAdvancedConfig cbaCfg;	// configuration for Circled-Bars-Advanced-Visualizations
	protected CircledFansConfig cfCfg;			// configuration for Circled-Fans-Visualizations
	protected ProbabilisticNetworkConfig pnCfg;	// configuration for Probabilistic-Network-Visualizations
	protected CSRConfig csrCfg;					// configuration for Continuous-Similarity-Ring-Visualizations 
	protected SunburstConfig sbCfg;				// configuration for Sunburst-Visualization

	// buffering the content of the Canvas (for more drawing performance)
	protected BufferedImage bi;   					// Image for buffering  complex visualizations
	protected Graphics2D big;                 		// Graphics2D-object for buffering the Canvas-content
	protected boolean loadBufferedImage = false;	// flag for using a buffered image

//	protected GraphicsOutputWrapper gow;		// wrapper for Graphics2D objects
	protected EpsGraphics2D eps;

	private JProgressBar progressBar;
	private VisuPopupMenuListener listener;
	/**
	 * Creates a new VisuPane-instance with an undefined visualization type.
	 */
	public VisuPane() {
		super();
		// initialize MouseListener
		//VisuPane_MouseInputAdapter mia = new VisuPane_MouseInputAdapter(this);
		//this.addMouseListener((MouseListener)mia);
		//this.addMouseMotionListener((MouseMotionListener)mia);
	}
	/**
	 * Creates a new VisuPane-instance and sets its visualization type
	 * to the argument <code>type</code>.
	 *
	 * @param type 	the type of the visualization
	 * @see comirva.visu.VisuPane#TYPE_SOMGRID
	 * @see comirva.visu.VisuPane#TYPE_SDH
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_VECTOR_CIRCLED_BARS
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_CIRCLED_FANS
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING
	 * @see comirva.visu.VisuPane#TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST
	 * @see comirva.visu.VisuPane#TYPE_EXPERIMENTAL
	 */
	public VisuPane(int type) {
		new VisuPane();			// to init mouse listener
		this.visuType = type;	// set visualization type
	}

	public VisuPane(JProgressBar progressBar) {
		this();
		this.progressBar = progressBar;
	}
	
	/**
	 * Sets the visualization type of the VisuPane.
	 *
	 * @param type	the type of the visualization
	 * @see comirva.visu.VisuPane#TYPE_SOMGRID
	 * @see comirva.visu.VisuPane#TYPE_SDH
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_VECTOR_CIRCLED_BARS
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_CIRCLED_FANS
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK
	 * @see comirva.visu.VisuPane#TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING
	 * @see comirva.visu.VisuPane#TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST
	 * @see comirva.visu.VisuPane#TYPE_EXPERIMENTAL
	 */
	public void setVisuType(int type) {
		this.visuType = type;
	}

	/**
	 * Sets the user preferences for the visualization pane.
	 *
	 * @param visuPrefs	a VisuPreferences-instance containing the preferences
	 */
	public void setVisuPreferences(VisuPreferences visuPrefs) {
		this.visuPreferences = visuPrefs;
		// disable EPS-output, if this is desired
		if (!this.visuPreferences.isEnableEPS())
			this.eps = null;
		else
			this.eps = super.getEpsGraphics();			
	}

	/**
	 * Returns the user preferences for the visualization pane.
	 *
	 * @return	a VisuPreferences-instance containing the preferences
	 */
	public VisuPreferences getVisuPreferences() {
		return this.visuPreferences;
	}

	/**
	 * Sets the colormap for the visualizations.
	 *
	 * @param cm	the ColorMap that should be used
	 */
	public void setColorMap(ColorMap cm) {
		// if a new colormap was selected, change it and repaint visualization area
		if (this.cm == null)	// no colormap assigned before
			this.cm = cm;
		else {					// colormap already assigned
			if (!(this.cm.getClass() == cm.getClass())) {		// a new colormap is assigned
				this.cm = cm;
				// force repaint to apply the new colormap
				this.setLoadBufferedImage(false);
			}
		}
	}

	/**
	 * Returns the ColorMap which is currently used.
	 *
	 * @return	the ColorMap which is currently used
	 */
	public ColorMap getColorMap() {
		return this.cm;
	}

	/**
	 * Sets, whether the visualization should be loaded from a
	 * (previously saved) buffered image or be redrawn.
	 * If the argument <code>loadBufferedImage</code> is set to <false>,
	 * complete drawing is forced, the next time the
	 * Canvas needs to be repainted (no loading from buffered image).
	 *
	 * @param loadBufferedImage	<code>true</code> if visualization is to be loaded from buffered image,
	 * 							<code>false</code> if visualization should be redrawn
	 */
	public void setLoadBufferedImage(boolean loadBufferedImage) {
		this.loadBufferedImage = loadBufferedImage;
	}

	/**
	 * Sets the internal SOM-parameter to the argument <code>som</code>.
	 *
	 * @param som	the SOM-instance for which the visualization is to be created
	 */
	public void setSOM(SOM som) {
		this.som = som;
	}

	/**
	 * Sets the internal SDH-parameter to the argument <code>sdh</code>.
	 *
	 * @param sdh	the SDH-instance for which the visualization is to be created
	 */
	public void setSDH(SDH sdh) {
		this.sdh = sdh;
	}

	/**
	 * Sets the internal distance matrix to the passed DataMatrix.
	 * A distance matrix must be quadratic.
	 *
	 * @param distMatrix	the DataMatrix-instance that represents the distance matrix
	 */
	public void setDistanceMatrix(DataMatrix distMatrix) {
		this.distMat = distMatrix;
	}

	/**
	 * Sets the internal term occurrence matrix to the passed DataMatrix. 
	 * 
	 * @param toMatrix	the DataMatrix-instance that represents the term occurrence matrix
	 */
	public void setTermOccurrenceMatrix(DataMatrix toMatrix) {
		this.toMat = toMatrix;
	}

	/**
	 * Sets the labels for the data items to visualize.
	 * (for experimental use)
	 *
	 * @param l	a Vector containing the labels
	 */
	public void setLabels(Vector l) {
		this.labels = l;
	}

	/**
	 * Set the SunburstNode representing the
	 * root node of a sunburst visualization.
	 * 
	 * @param sbRoot The sbRoot to set.
	 */
	public void setSunburstRootNode(SunburstNode sbRoot) {
		this.sbRoot = sbRoot;
	}

	/**
	 * Sets the configuration for a SOM-Calculation.
	 *
	 * @param somCfg	a SOMConfig-instance containing the configuration
	 */
	public void setSOMConfig(SOMConfig somCfg) {
		this.somCfg = somCfg;
	}
	
	/**
	 * Sets the configuration for a GHSOM-Calculation.
	 *
	 * @param ghsomCfg	a GHSOMConfig-instance containing the configuration
	 */
	public void setGHSOMConfig(GHSOMConfig ghsomCfg) {
		this.ghsomCfg = ghsomCfg;
	}
	/**
	 * Sets the configuration for an SDH-Visualization.
	 *
	 * @param sdhCfg an SDHConfig-instance containing the configuration
	 */
	public void setSDHConfig(SDHConfig sdhCfg) {
		this.sdhCfg = sdhCfg;
	}
	/**
	 * Sets the configuration for a Circled-Bars-Visualization.
	 *
	 * @param cbaCfg	a CircledBarsAdvancedConfig-instance containing the configuration
	 */
	public void setCircledBarsAdvancedConfig(CircledBarsAdvancedConfig cbaCfg) {
		this.cbaCfg = cbaCfg;
	}
	/**
	 * Sets the configuration for a Circled-Fans-Visualization.
	 *
	 * @param cfCfg	a CircledFansConfig-instance containing the configuration
	 */
	public void setCircledFansConfig(CircledFansConfig cfCfg) {
		this.cfCfg = cfCfg;
	}
	/**
	 * Sets the configuration for a Probabilistic-Network-Visualization.
	 *
	 * @param pnCfg a ProbabilisticNetworkConfig-instance containing the configuration
	 */
	public void setProbabilisticNetworkConfig(ProbabilisticNetworkConfig pnCfg) {
		this.pnCfg = pnCfg;
	}
	/**
	 * Sets the configuration for a Continuous-Similarity-Ring-Visualization.
	 *
	 * @param csrCfg a CSRConfig-instance containing the configuration
	 */
	public void setCSRConfig(CSRConfig csrCfg) {
		this.csrCfg = csrCfg;
	}
	/**
	 * Sets the configuration for a Sunburst-Visualization.
	 * 
	 * @param sbCfg a SunburstConfig-instance containing the configuration
	 */
	public void setSunburstConfig(SunburstConfig sbCfg) {	
		this.sbCfg = sbCfg;
	}

	/**
	 * Returns the configuration for a SOM-Calculation.
	 *
	 * @return a SOMConfig-instance containing the configuration
	 */
	public SOMConfig getSOMConfig() {
		return this.somCfg;
	}
	
	/**
	 * Returns the configuration for a GHSOM-Calculation.
	 *
	 * @return a GHSOMConfig-instance containing the configuration
	 */
	public GHSOMConfig getGHSOMConfig() {
		return this.ghsomCfg;
	}
	
	/**
	 * Returns the configuration for an SDH-Visualization.
	 *
	 * @return an SDHConfig-instance containing the configuration
	 */
	public SDHConfig getSDHConfig() {
		return this.sdhCfg;
	}
	/**
	 * Returns the configuration for a Circled-Bars-Advanced-Visualization.
	 *
	 * @return a CircledBarsAdvancedConfig-instance containing the configuration
	 */
	public CircledBarsAdvancedConfig getCircledBarsAdvancedConfig() {
		return this.cbaCfg;
	}
	/**
	 * Returns the configuration for a Circled-Fans-Visualization.
	 *
	 * @return a CircledFansConfig-instance containing the configuration
	 */
	public CircledFansConfig getCircledFansConfig() {
		return this.cfCfg;
	}
	/**
	 * Returns the configuration for the Probabilistic-Network-Visualization.
	 *
	 * @return a ProbabilisticNetworkConfig-instance containing the configuration
	 */
	public ProbabilisticNetworkConfig getProbabilisticNetworkConfig() {
		return this.pnCfg;
	}
	/**
	 * Returns the configuration for a Continuous-Similarity-Ring-Visualization.
	 *
	 * @return a CSRConfig-instance containing the configuration
	 */
	public CSRConfig getCSRConfig() {
		return this.csrCfg;
	}
	/**
	 * Returns the configuration for a Sunburst-Visualization.
	 * 
	 * @return a SunburstConfig-instance containing the configuration
	 */
	public SunburstConfig getSunburstConfig() {	
		return this.sbCfg;
	}

	/**
	 * Returns a Vector containing the labels that should be visualized.
	 *
	 * @return 	a Vector containing the labels
	 */
	public Vector getLabels() {
		return this.labels;
	}

//	public void setBounds(Rectangle r) {
//	// create bi
//	}

//	public Dimension getPreferredSize() { 
//	return getParent().getPreferredSize();
//	} 

	/**
	 * Paints a visualization according to the type set with {@link comirva.visu.VisuPane#setVisuType}.
	 *
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		// proceed only if visualization type is set
		if (visuType != -1) {
			// set background color
			this.setBackground(visuPreferences.getBackgroundColor());
			// if a buffered image exists (and the size of the drawing area has not changed), load it
			if ((this.loadBufferedImage) && (this.bi != null) && (bi.getWidth() == this.getWidth()) && (bi.getHeight() == this.getHeight())) {
				g.drawImage(bi, 0, 0, this);
				// no buffered image exists or the size of the drawing area has changed (e.g. user has resized the window) -> (re)draw
			} else {
				// print a SOM-Grid
				if ((visuType == VisuPane.TYPE_SOMGRID) && (som != null)) {
					if (this.somgv == null)		// thread not yet created 
						this.somgv = new SOMGridVisu(this);
					if (!this.somgv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.somgv);
						//this.somgv.start();
					} else
						try {
							this.somgv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print a GHSOM-Grid
				if ((visuType == VisuPane.TYPE_GHSOMGRID) && (ghsom != null)) {
					if (this.ghsomgv == null)		// thread not yet created 
						this.ghsomgv = new GHSOMGridVisu(this);
					if (!this.ghsomgv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.ghsomgv);
						//this.ghsomgv.start();
					} else
						try {
							this.ghsomgv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print a MDM-Grid
				if ((visuType == VisuPane.TYPE_MDMGRID) && (som != null)) {
					if (this.mdmgv == null)		// thread not yet created 
						this.mdmgv = new MDMGridVisu(this);
					if (!this.mdmgv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.mdmgv);
						this.mdmgv.start();
					} else
						try {
							this.mdmgv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print an SDH
				if ((visuType == VisuPane.TYPE_SDH) && (sdh != null)) {
					if (this.sdhv == null)		// thread not yet created 
						this.sdhv = new SDHVisu(this);
					if (!this.sdhv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.sdhv);
						this.sdhv.start();
					} else
						try {
							this.sdhv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print a circled bars visualization
				if ((visuType == VisuPane.TYPE_DISTANCE_VECTOR_CIRCLED_BARS) && (this.distMat != null)) {
					if (this.cbv == null)			// thread not yet created 
						this.cbv = new CircledBarsVisu(this);
					if (!this.cbv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.cbv);
						this.cbv.start();
					} else
						try {
							this.cbv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print a circled fans visualization
				if ((visuType == VisuPane.TYPE_DISTANCE_MATRIX_CIRCLED_FANS) && (this.distMat != null) && (this.cfCfg != null)) {
					if (this.cfv == null)			// thread not yet created 
						this.cfv = new CircledFansVisu(this);
					if (!this.cfv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.cfv);
						this.cfv.start();
					} else
						try {
							this.cfv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}					
				}
				// print a probabilistic network visualization
				if ((visuType == VisuPane.TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK) && (this.distMat != null) && (this.pnCfg != null)) {
					if (this.pnv == null)			// thread not yet created 
						this.pnv = new ProbabilisticNetworkVisu(this);
					if (!this.pnv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.pnv);
						this.pnv.start();
					} else
						try {
							this.pnv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				// print a continuous similarity ring visualization
				if ((visuType == VisuPane.TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING) && (this.distMat != null) && (this.csrCfg != null)) {
					if (this.csrv == null)			// thread not yet created 
						this.csrv = new ContinuousSimilarityRingVisu(this);
					if (!this.csrv.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.csrv);
						this.csrv.start();
					} else
						try {
							this.csrv.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}					
				}
				// print a sunburst visualization based on a term occurrence matrix
				if ((visuType == VisuPane.TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST) && (this.toMat != null)) {
					this.sbv = new SunburstVisu(this);
					SwingUtilities.invokeLater(this.sbv);
					this.sbv.showSunburst();
					// when running as thread, there are some problems with graphics output
					// that's why no thread is used for Sunburst visualization
//					this.sbv.start();
				}
				// print experimental visualization
				if (visuType == VisuPane.TYPE_EXPERIMENTAL) {
					if (this.ev == null)			// thread not yet created 
						this.ev = new ExperimentalVisu(this);
					if (!this.ev.isAlive()) {	// thread not running - run it	
						SwingUtilities.invokeLater(this.ev);
						this.ev.start();
					} else
						try {
							this.ev.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}
		}
	}


	/**
	 *	Kills all threads currenty performing visualization tasks  
	 */
	public void resetVisuThreads() {
		this.somgv = null;
		this.ghsomgv = null;
		this.mdmgv = null;
		this.sdhv = null;
		this.cbv = null;
		this.cfv = null;
		this.pnv = null;
		this.csrv = null;
		this.sbv = null;
		this.ev = null;
	}

	/**
	 * Returns the content of the canvas (for saving in a file).
	 *
	 * @return a BufferedImage containing the content of the visualization pane.
	 */
	public BufferedImage getImage() {
		return this.bi;
	}
	public GHSOM getGHSOM() {
		return ghsom;
	}
	public void setGHSOM(GHSOM ghsom) {
		this.ghsom = ghsom;
	}
	public GHSOMGridVisu getGHSOMVisalizer() {
		return ghsomgv;
	}
	public int getVisuType() {
		return visuType;
	}
	
	/**
	 * @return the circled bars visu
	 */
	public CircledBarsVisu getCircledBarsVisu() {
		return cbv;
	}		
	
	/**
	 * First all existing visualisation dependent mouse listeners are removed
	 * (That means that the popup menu is not removed).
	 * Then the mouse listeners that are needed for the current visualisation 
	 * are added. Depending on the current visualisation 
	 * (given by {@link #visuType} which is (set by {@link #setVisuType(int)}
	 * and read by {@link #getVisuType()}) the <code>initMouseListener()</code> 
	 * method of the appropiate visu object is called or if a sepearate
	 * Listener object exists (like in case of the GHSOM Visu) an instance of
	 * this listener is created and added directly.
	 */
	public void initMouseListener() {
		// remove existing mouse (motion) listeners, 
		// because they will be readded if necessary		
		for (MouseListener l: getMouseListeners()) {
			if (!(l instanceof PopupMenuMouseAdapter)) {	// don't remove listener for popup menu
				removeMouseListener(l);
			}
		}
		for (MouseMotionListener l: this.getMouseMotionListeners()) {
			this.removeMouseMotionListener(l);
		}
		// add listeners for certain visualisations
		if ((visuType == VisuPane.TYPE_GHSOMGRID)) {
			this.addMouseListener(new GHSOM_MIA(this));
		} else
		if ((visuType == VisuPane.TYPE_DISTANCE_MATRIX_CIRCLED_FANS) && (this.cfv != null)) {
			this.cfv.initMouseListener();
		} else 
		if ((visuType == VisuPane.TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK) && (this.pnv != null)) {
			this.pnv.initMouseListener();
		} else 
		if ((visuType == VisuPane.TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST) && (this.sbv != null)) {
			this.sbv.initMouseListener();
		}
	}
		
	////////////////////////////////
	/** add this object as a popup menu listener to the given popup menu
	 *  @param menu the menu this object is added to as popup menu listener
	 */
	public void registerPopupMenuListener(JPopupMenu menu) {
		if (this.listener == null) {
			this.listener = new VisuPopupMenuListener(this);
		}
		menu.addPopupMenuListener(listener);
	}
	
	/** this class is a popup menu listener for a visualisation popup
	 *  that adds special menu commands for some viusalisations. These
	 *  special menus must be provided by the visualisation classes
	 *  themselves. This class just adds the resulting menu as first
	 *  entry of the general context menu just before the popup becomes
	 *  visible.
	 * @author Florian Marchl
	 *
	 */
	private class VisuPopupMenuListener implements PopupMenuListener {
		private VisuPane adaptee;
		private JMenu menu;
		
		/** Constructor @param adaptee the containing visupane */
		public VisuPopupMenuListener(VisuPane adaptee) {
			this.adaptee = adaptee;
			this.menu = new JMenu();
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
			// no operation			
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// no operation
		}
		
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			Object o = e.getSource();
			if (o instanceof JPopupMenu) {
				JPopupMenu source = (JPopupMenu)o;
				// If the special menu has not been added so far
				// and it contains at least one sub element...
				if ((source.getComponentIndex(menu) < 0) && (menu.getItemCount() > 0)) {
					// add it on top and a sepereator
					source.insert(menu, 0);
					source.insert(new JPopupMenu.Separator(), 1);
				}
				// remove existing sub menu entries
				menu.removeAll();
				// create special menus according to the current
				// visualization displayed ( = the context menus
				// that have already existed in previous versions)
				if (adaptee.visuType == VisuPane.TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST) {
					menu.setText("Sunburst visualisation");		// set special menu title					
					JMenuItem[] items = adaptee.sbv.getSunburstContextMenu(source.getLocation());
					for (JMenuItem i: items) {			// add items to list
						menu.add(i);
					}
				} else if (adaptee.visuType == VisuPane.TYPE_GHSOMGRID) {
					menu.setText("GHSOM Grid visualisation");
					MouseListener[] listeners = adaptee.getMouseListeners();
					GHSOM_MIA mia = null;
					for (MouseListener listener : listeners) {
						if (listener instanceof GHSOM_MIA) {
							mia = (GHSOM_MIA)listener;
						}
					}
					Point location = new Point(0,0);
					if (mia != null) {
						location = mia.getLastMouseInput();
					}
					JMenuItem[] items = adaptee.ghsomgv.getGHSOMGridContextMenu(location);
					for (JMenuItem i: items) {			// add items to list
						menu.add(i);
					}
				} else {
					// remove special menu if not needed for current visu
					if (source.getComponentIndex(menu) >= 0) {
						source.remove(menu);
						source.remove(0);
					}
				}
			}
		}		
	}
}