/*
 * Created on 20.10.2004
 */
package comirva;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import comirva.audio.AudioPlaylistPlayer;
import comirva.audio.extraction.FluctuationPatternExtractionThread;
import comirva.audio.extraction.MandelEllisExtractionThread;
import comirva.audio.extraction.TimbreDistributionExtractionThread;
import comirva.config.CSRConfig;
import comirva.config.CircledBarsAdvancedConfig;
import comirva.config.CircledFansConfig;
import comirva.config.DataMatrixNormalizeConfig;
import comirva.config.ETPLoaderConfig;
import comirva.config.GHSOMConfig;
import comirva.config.PCAConfig;
import comirva.config.PageCountsRetrieverConfig;
import comirva.config.ProbabilisticNetworkConfig;
import comirva.config.SDHConfig;
import comirva.config.SOMConfig;
import comirva.config.SunburstConfig;
import comirva.config.VisuPreferences;
import comirva.config.WebCrawlingConfig;
import comirva.config.defaults.CSRDefaultConfig;
import comirva.config.defaults.CircledFansDefaultConfig;
import comirva.config.defaults.GHSOMDefaultConfig;
import comirva.config.defaults.ProbabilisticNetworkDefaultConfig;
import comirva.config.defaults.SDHDefaultConfig;
import comirva.config.defaults.SOMDefaultConfig;
import comirva.config.defaults.SunburstDefaultConfig;
import comirva.config.defaults.VisuDefaultPreferences;
import comirva.data.DataMatrix;
import comirva.data.SunburstNode;
import comirva.exception.SizeMismatchException;
import comirva.io.DocumentTermExtractorThread;
import comirva.io.ETPCreatorThread;
import comirva.io.ETPXMLExtractorThread;
import comirva.io.ETPXMLPathUpdaterThread;
import comirva.io.MatrixDataFileLoaderThread;
import comirva.io.MetaDataFileLoaderThread;
import comirva.io.SOM2HTMLExporter;
import comirva.io.filefilter.AudioFileFilter;
import comirva.io.filefilter.EPSFileFilter;
import comirva.io.filefilter.GHSOMFileFilter;
import comirva.io.filefilter.GraphicFileFilter;
import comirva.io.filefilter.HTMLFileFilter;
import comirva.io.filefilter.MP3FileFilter;
import comirva.io.filefilter.MatlabASCIIFileFilter;
import comirva.io.filefilter.SDHFileFilter;
import comirva.io.filefilter.SOMFileFilter;
import comirva.io.filefilter.TextFileFilter;
import comirva.io.filefilter.WorkspaceFileFilter;
import comirva.io.filefilter.XMLFileFilter;
import comirva.io.web.InvalidPageCountsRetriever;
import comirva.io.web.PageCountsRetriever;
import comirva.io.web.WebCrawling;
import comirva.mlearn.GHSOM;
import comirva.mlearn.GHSOMTrainingThread;
import comirva.mlearn.MDM;
import comirva.mlearn.SDH;
import comirva.mlearn.SOM;
import comirva.mlearn.SOMTrainingThread;
import comirva.mlearn.ghsom.GhSomPrototypeFinder;
import comirva.mlearn.ghsom.MeanPrototypeFinder;
import comirva.mlearn.ghsom.WebCoocGroupPrototypeFinder;
import comirva.mlearn.ghsom.WebCoocIndividualPrototypeFinder;
import comirva.ui.AboutBox;
import comirva.ui.CSRCreationDialog;
import comirva.ui.CircledBarsAdvancedCreationDialog;
import comirva.ui.CircledFansCreationDialog;
import comirva.ui.DataManagementPreferencesDialog;
import comirva.ui.VisuPreferencesDialog;
import comirva.ui.DataMatrixNormalizeDialog;
import comirva.ui.DataMatrixRenameDialog;
import comirva.ui.ETPLoaderDialog;
import comirva.ui.GHSOMCreationDialog;
import comirva.ui.PCACalculationDialog;
import comirva.ui.PageCountsRetrieverDialog;
import comirva.ui.ProbabilisticNetworkCreationDialog;
import comirva.ui.SDHCreationDialog;
import comirva.ui.SOMCreationDialog;
import comirva.ui.SunburstCreationDialog;
import comirva.ui.VisuPreferencesDialog;
import comirva.ui.WebCrawlingDialog;
import comirva.ui.component.ButtonTabComponent;
import comirva.ui.component.DataManagementToolbar;
import comirva.ui.model.CSRVisuListItem;
import comirva.ui.model.CircledBarsVisuListItem;
import comirva.ui.model.CircledFansVisuListItem;
import comirva.ui.model.ProbabilisticNetworkVisuListItem;
import comirva.ui.model.SunBurstVisuListItem;
import comirva.ui.model.VisuListItem;
import comirva.util.FileUtils;
import comirva.util.PCACalculationThread;
import comirva.util.TermProfileUtils;
import comirva.util.VectorSort;
import comirva.util.external.ID3Reader;
import comirva.visu.VisuPane;
import comirva.visu.colormap.ColorMap_Colorful;
import comirva.visu.colormap.ColorMap_Fire;
import comirva.visu.colormap.ColorMap_Gray;
import comirva.visu.colormap.ColorMap_Islands;
import comirva.visu.colormap.ColorMap_Ocean;
import comirva.visu.colormap.ColorMap_Sun;

import cp.util.ThreadListener;

/**
 * This class implements the main user interface of CoMIRVA.
 *
 * @author Markus Schedl
 */
public class MainUI { 
	AudioPlaylistPlayer pl;

	// constants
	public static String VERSION = "0.2.N3 (Nocturnal Ninja Nerd)";		// current version
	public static String DATE	 = "July 2008";							// date of last modification

	// standard background color
	private Color colBackground = new Color(220, 220, 220);
	// JFrame for main window
	private JFrame comirvaUI = new JFrame("CoMIRVA " + VERSION);
	// JLabel for status bar
	private JLabel statusBar = new JLabel();
	// JProgressBar for indicating work
	private JProgressBar progressBar = new JProgressBar();
	// right pane for data matrix, meta-data lists, and audio player
	private JPanel paneRight = new JPanel(new BorderLayout());
	// check box menu item for Audio Player
	private JCheckBoxMenuItem menuAudioShowAudioPlayer;		// is global because it must be accessible by the method that shows/hides the audio player
	// check box menu item for ColorMap-inversion
	private JCheckBoxMenuItem menuVisuCMInverted;			// is global because selecting a new colormap must change it
	// check box menu item for ColorMap-inversion (popup menu)
	private JCheckBoxMenuItem popupVisuCMInverted;			// is global because selecting a new colormap must change it
	// check box menu for EPS-Output
	private JMenuItem menuVisuExportEPS;					// is global because must be changable if user deselects "Enable EPS-Output" 

	// by Florian Marchl:
	// colormap main menu and popup menu entries are global, because they must be synchronised
	// (if user clicks main menu the popup menu state must be updated and vice versa)
	// The synchronisation is done by "setSelected(boolean)" calls, because they manage
	// deselection of the other elements of the group but do not fire an action event.
	JRadioButtonMenuItem menuVisuCMIslands = new JRadioButtonMenuItem("Islands", true);
	JRadioButtonMenuItem menuVisuCMFire = new JRadioButtonMenuItem("Fire", false);
	JRadioButtonMenuItem menuVisuCMColorful = new JRadioButtonMenuItem("Colorful", false);
	JRadioButtonMenuItem menuVisuCMSun = new JRadioButtonMenuItem("Sun", false);
	JRadioButtonMenuItem menuVisuCMOcean = new JRadioButtonMenuItem("Ocean", false);
	JRadioButtonMenuItem menuVisuCMGray = new JRadioButtonMenuItem("Gray", false);
	JRadioButtonMenuItem popupColormapIslands = new JRadioButtonMenuItem("Islands", true);
	JRadioButtonMenuItem popupColormapFire = new JRadioButtonMenuItem("Fire", false);
	JRadioButtonMenuItem popupColormapColorful = new JRadioButtonMenuItem("Colorful", false);
	JRadioButtonMenuItem popupColormapSun = new JRadioButtonMenuItem("Sun", false);
	JRadioButtonMenuItem popupColormapOcean = new JRadioButtonMenuItem("Ocean", false);
	JRadioButtonMenuItem popupColormapGray = new JRadioButtonMenuItem("Gray", false);

	private JTabbedPane tbp_matrices = new JTabbedPane();				// declared here for use in loading/open events that need to produce a new tab	
	JToolBar toolbarDataManagement = new DataManagementToolbar(this);	// declared here for property manipulation
	Vector<JList> dataMatrixListVector = new Vector<JList>();			// declared here for retrieving selected list item

	// workspace containing all data matrices and meta-data instances
	Workspace ws = new Workspace();

	// JList for list of visualizations
	private JList visuList = new JList(ws.listVisu);
	// JPanel to hold visu list
	private JPanel panelVisuList = new JPanel(new BorderLayout());
	// JList for matrix names
	private JList dataMatrixList = new JList(ws.listMatrices);
	// JPanel to hold matrix list
	private JPanel panelMatrixList = new JPanel(new BorderLayout());
	// JList for meta-data names
	private JList dataMetaDataList = new JList(ws.listMetaData);
	// JPanel to hold meta-data list
	private JPanel panelMetaDataList = new JPanel(new BorderLayout());

	// JPanel to hold audio player
	private JPanel paneAudioPlayer = new JPanel(new GridLayout(1, 5, 5, 5));
	// buttons for audio player
	private JButton btnPlayPause;
	private JButton btnNext;
	private JButton btnPrevious;
	private JButton btnStop;

	// Canvas for Visualizations
	private VisuPane paneVisu = new VisuPane(progressBar); //SimpleUniverse.getPreferredConfiguration());

	// Self-Organizing Map
	// private SOM som;
	// Growing Hierarchical Self-Organizing Map
	// private GHSOM ghsom;
	// Smoothed Data Histogram
	// private SDH sdh;
	// configuration settings for some functions
	private WebCrawlingConfig wcCfg;						// configuration for web crawling
	private PageCountsRetrieverConfig pcrCfg;				// configuration for page counts retriever
	private DataMatrixNormalizeConfig dmnCfg;				// configuration for data matrix normalizer
	private ETPLoaderConfig etplCfg;						// configuration for ETP loader
	private PCAConfig pcaCfg;								// configuration for PCA calculation

	//preferences
	private VisuPreferences dmPreferences = new VisuPreferences();		// data management preferences

	/**
	 * Creates the menus of the user interface.
	 *
	 * @return the complete JMenuBarmenu
	 */
	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.X_AXIS));
		// disable "lightweight"-property for menu (to make sure, that menu is always visible and not hidded by visu pane)
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// build menu "File"
		JMenu menuFile = new JMenu("File");
		// create menu entries
		JMenuItem menuFileLoadDataFile = new JMenuItem("Load Matrix Data File...");
		JMenuItem menuFileLoadMetaDataFile = new JMenuItem("Load Meta-Data File...");
		JMenuItem menuFileSaveDataFile = new JMenuItem("Save Matrix Data File...");
		JMenuItem menuFileSaveMetaDataFile = new JMenuItem("Save Meta-Data File...");
		JMenuItem menuFileLoadWorkspace = new JMenuItem("Load Workspace...");
		JMenuItem menuFileSaveWorkspace = new JMenuItem("Save Workspace...");
		JMenuItem menuFileEmptyDataFileList = new JMenuItem("Empty Matrix Data File List");
		JMenuItem menuFileEmptyMetaDataFileList = new JMenuItem("Empty Meta-Data File List");
		JMenuItem menuFileExit = new JMenuItem("Exit");
		// assign action listeners
		menuFileLoadDataFile.addActionListener(new MenuFileLoadDataFile_Action(this));
		menuFileLoadMetaDataFile.addActionListener(new MenuFileLoadMetaDataFile_Action(this));
		menuFileSaveDataFile.addActionListener(new MenuFileSaveDataFile_Action(this));
		menuFileSaveMetaDataFile.addActionListener(new MenuFileSaveMetaDataFile_Action(this));
		menuFileEmptyDataFileList.addActionListener(new MenuFileEmptyDataFileList_Action(this));
		menuFileEmptyMetaDataFileList.addActionListener(new MenuFileEmptyMetaDataFileList_Action(this));
		menuFileLoadWorkspace.addActionListener(new MenuFileLoadWorkspace_Action(this));
		menuFileSaveWorkspace.addActionListener(new MenuFileSaveWorkspace_Action(this));
		menuFileExit.addActionListener(new MenuFileExit_Action(this));
		// add menu entries
		menuFile.add(menuFileLoadDataFile);
		menuFile.add(menuFileSaveDataFile);
		menuFile.addSeparator();
		menuFile.add(menuFileLoadMetaDataFile);
		menuFile.add(menuFileSaveMetaDataFile);
		menuFile.addSeparator();
		menuFile.add(menuFileEmptyDataFileList);
		menuFile.add(menuFileEmptyMetaDataFileList);
		menuFile.addSeparator();
		menuFile.add(menuFileLoadWorkspace);
		menuFile.add(menuFileSaveWorkspace);
		menuFile.addSeparator();
		menuFile.add(menuFileExit);
		// add menu to menu bar
		menuBar.add(menuFile);
		// assign key-shortcuts (mnemonics and accelerators)
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFileLoadDataFile.setMnemonic(KeyEvent.VK_L);
		menuFileLoadDataFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		menuFileSaveDataFile.setMnemonic(KeyEvent.VK_S);
		menuFileLoadMetaDataFile.setMnemonic(KeyEvent.VK_M);
		menuFileLoadMetaDataFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		menuFileSaveMetaDataFile.setMnemonic(KeyEvent.VK_T);
		menuFileLoadWorkspace.setMnemonic(KeyEvent.VK_O);
		menuFileLoadWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		menuFileSaveWorkspace.setMnemonic(KeyEvent.VK_A);
		menuFileEmptyDataFileList.setMnemonic(KeyEvent.VK_E);
		menuFileEmptyMetaDataFileList.setMnemonic(KeyEvent.VK_P);
		menuFileExit.setMnemonic(KeyEvent.VK_X);
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		// build menu "Data"
		JMenu menuData = new JMenu("Data");
		// create submenu "Data Matrix"
		JMenu menuDataDataMatrix = new JMenu("Data Matrix");
		// create menu entries
		JMenuItem menuDataDataMatrixRename = new JMenuItem("Rename...");
		JMenuItem menuDataDataMatrixSort = new JMenuItem("Sort List");
		JMenuItem menuDataDataMatrixNormalize = new JMenuItem("Normalize...");
		JMenuItem menuDataDataMatrixPCA = new JMenuItem("Principal Components Analysis...");
		// assign action listeners
		menuDataDataMatrixRename.addActionListener(new MenuDataDataMatrixRename_Action(this));
		menuDataDataMatrixSort.addActionListener(new MenuDataDataMatrixSort_Action(this));
		menuDataDataMatrixNormalize.addActionListener(new MenuDataDataMatrixNormalize_Action(this));
		menuDataDataMatrixPCA.addActionListener(new MenuDataDataMatrixPCA_Action(this));
		// add menu entries
		menuDataDataMatrix.add(menuDataDataMatrixRename);
		menuDataDataMatrix.add(menuDataDataMatrixSort);
		menuDataDataMatrix.addSeparator();
		menuDataDataMatrix.add(menuDataDataMatrixNormalize);
		// create submenu "Vectorize"
		JMenu menuDataDataMatrixVectorize = new JMenu("Vectorize");
		// create menu entries
		JMenuItem menuDataDataMatrixVectorizeByRows = new JMenuItem("by Rows");
		JMenuItem menuDataDataMatrixVectorizeByColumns = new JMenuItem("by Columns");
		// assign action listeners
		menuDataDataMatrixVectorizeByRows.addActionListener(new MenuDataDataMatrixVectorizeByRows_Action(this));
		menuDataDataMatrixVectorizeByColumns.addActionListener(new MenuDataDataMatrixVectorizeByColumns_Action(this));
		// add menu entries
		menuDataDataMatrixVectorize.add(menuDataDataMatrixVectorizeByRows);
		menuDataDataMatrixVectorize.add(menuDataDataMatrixVectorizeByColumns);
		// add "Vectorize"-submenu to "DataMatrix"-menu
		menuDataDataMatrix.add(menuDataDataMatrixVectorize);
		menuDataDataMatrix.addSeparator();
		// add "PCA"-entry to "Data"-menu
		menuDataDataMatrix.add(menuDataDataMatrixPCA);
		// add "Data Matrix"-submenu to "Data"-menu
		menuData.add(menuDataDataMatrix);

		// create submenu "Meta-Data"
		JMenu menuDataMetaData = new JMenu("Meta-Data");
		// create menu entries
		JMenuItem menuDataMetaDataRename = new JMenuItem("Rename...");
		JMenuItem menuDataMetaDataSort = new JMenuItem("Sort List");
		JMenuItem menuDataMetaDataExtract = new JMenuItem("Extract ID3-Tags from File List");
		// assign action listeners
		menuDataMetaDataRename.addActionListener(new MenuDataMetaDataRename_Action(this));
		menuDataMetaDataSort.addActionListener(new MenuDataMetaDataSort_Action(this));
		menuDataMetaDataExtract.addActionListener(new MenuDataMetaDataExtract_Action(this));
		// add menu entries
		menuDataMetaData.add(menuDataMetaDataRename);
		menuDataMetaData.add(menuDataMetaDataSort);
		menuDataMetaData.addSeparator();
		menuDataMetaData.add(menuDataMetaDataExtract);
		// add "Meta-Data" to "Data"-menu
		menuData.add(menuDataMetaData);

		// create submenu "Web Mining"
		JMenu menuDataWebMining = new JMenu("Web Mining");
		// create submenu "Co-Occurence Analysis"
		JMenu menuDataWebMiningCoOcAnalysis = new JMenu("Co-Occurrence Analysis");
		// create menu entries
		JMenuItem menuDataWebMiningPageCountMatrix = new JMenuItem("Retrieve Page Counts");
		JMenuItem menuDataWebMiningRequeryPageCountMatrix = new JMenuItem("Requery Invalid Entries in Page-Count-Matrix");
		JMenuItem menuDataWebMiningEstimateConditionalProbabilities = new JMenuItem("Estimate Conditional Probabilities");
		// assign action listeners
		menuDataWebMiningPageCountMatrix.addActionListener(new MenuDataWebMiningPageCountMatrix_Action(this));
		menuDataWebMiningRequeryPageCountMatrix.addActionListener(new MenuDataWebMiningRequeryPageCountMatrix_Action(this));
		menuDataWebMiningEstimateConditionalProbabilities.addActionListener(new MenuDataWebMiningEstimateConditionalProbabilities_Action(this));
		// add menu entries
		menuDataWebMiningCoOcAnalysis.add(menuDataWebMiningPageCountMatrix);
		menuDataWebMiningCoOcAnalysis.add(menuDataWebMiningRequeryPageCountMatrix);
		menuDataWebMiningCoOcAnalysis.addSeparator();
		menuDataWebMiningCoOcAnalysis.add(menuDataWebMiningEstimateConditionalProbabilities);
		// add "Co-Occurrence Analysis"-submenu to "Web Mining"
		menuDataWebMining.add(menuDataWebMiningCoOcAnalysis);

		// create submenu "Term Profile"
		JMenu menuDataWebMiningTermProfile= new JMenu("Term Profile Creation");
		// create menu entries
		JMenuItem menuDataWebMiningTermProfileRetrieveRelatedPages = new JMenuItem("1 - Retrieve Meta-Data-Related Web Pages...");
		JMenuItem menuDataWebMiningTermProfileCreateEntityTermProfile = new JMenuItem("2 - Create ETP: Entity Term Profile(s) from Retrieved Documents...");
		JMenuItem menuDataWebMiningTermProfileLoadETP = new JMenuItem("3 - Load ETP from XML-File(s)...");
		JMenuItem menuDataWebMiningTermProfileExtractTerms = new JMenuItem("Extract Terms from Retrieved Documents...");
		JMenuItem menuDataWebMiningTermProfileUpdatePathsETP = new JMenuItem("Update Paths in ETP-XML-File(s)...");
		// assign action listeners
		menuDataWebMiningTermProfileLoadETP.addActionListener(new MenuDataWebMiningTermProfileLoadETP_Action(this));
		menuDataWebMiningTermProfileRetrieveRelatedPages.addActionListener(new MenuDataWebMiningTermProfileRetrieveRelatedPages_Action(this));
		menuDataWebMiningTermProfileCreateEntityTermProfile.addActionListener(new MenuDataWebMiningTermProfileCreateEntityTermProfile_Action(this));
		menuDataWebMiningTermProfileExtractTerms.addActionListener(new MenuDataWebMiningTermProfileExtractTerms_Action(this));
		menuDataWebMiningTermProfileUpdatePathsETP.addActionListener(new MenuDataWebMiningTermProfileUpdatePathsETP_Action(this));
		// add menu entries
		menuDataWebMiningTermProfile.add(menuDataWebMiningTermProfileRetrieveRelatedPages);
		menuDataWebMiningTermProfile.add(menuDataWebMiningTermProfileCreateEntityTermProfile);
		menuDataWebMiningTermProfile.add(menuDataWebMiningTermProfileLoadETP);
		menuDataWebMiningTermProfile.addSeparator();
		menuDataWebMiningTermProfile.add(menuDataWebMiningTermProfileExtractTerms);
		menuDataWebMiningTermProfile.addSeparator();
		menuDataWebMiningTermProfile.add(menuDataWebMiningTermProfileUpdatePathsETP);
		// add "Term Profile"-submenu to "Web Mining"
		menuDataWebMining.add(menuDataWebMiningTermProfile);
		// add "Web Mining"-submenu to "Data"-menu
		menuData.addSeparator();
		menuData.add(menuDataWebMining);

		// add menu to menu bar
		menuBar.add(menuData);
		// assign key-shortcuts (mnemonics and accelerators)
		menuData.setMnemonic(KeyEvent.VK_D);
		menuDataDataMatrix.setMnemonic(KeyEvent.VK_D);
		menuDataDataMatrixRename.setMnemonic(KeyEvent.VK_R);
		menuDataDataMatrixRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		menuDataDataMatrixSort.setMnemonic(KeyEvent.VK_S);
		menuDataDataMatrixSort.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuDataDataMatrixNormalize.setMnemonic(KeyEvent.VK_N);
		menuDataDataMatrixNormalize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuDataDataMatrixVectorize.setMnemonic(KeyEvent.VK_V);
		menuDataDataMatrixVectorizeByRows.setMnemonic(KeyEvent.VK_R);
		menuDataDataMatrixVectorizeByRows.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuDataDataMatrixVectorizeByColumns.setMnemonic(KeyEvent.VK_C);
		menuDataDataMatrixVectorizeByColumns.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuDataMetaData.setMnemonic(KeyEvent.VK_M);
		menuDataMetaDataRename.setMnemonic(KeyEvent.VK_R);
		menuDataMetaDataRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_MASK));
		menuDataMetaDataSort.setMnemonic(KeyEvent.VK_S);
		menuDataMetaDataSort.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK));
		menuDataDataMatrixPCA.setMnemonic(KeyEvent.VK_P);
		menuDataWebMining.setMnemonic(KeyEvent.VK_W);
		menuDataWebMiningCoOcAnalysis.setMnemonic(KeyEvent.VK_O);
		menuDataWebMiningPageCountMatrix.setMnemonic(KeyEvent.VK_P);
		menuDataWebMiningRequeryPageCountMatrix.setMnemonic(KeyEvent.VK_R);
		menuDataWebMiningEstimateConditionalProbabilities.setMnemonic(KeyEvent.VK_E);
		menuDataWebMiningTermProfile.setMnemonic(KeyEvent.VK_T);
		menuDataWebMiningTermProfileCreateEntityTermProfile.setMnemonic(KeyEvent.VK_C);
		menuDataWebMiningTermProfileRetrieveRelatedPages.setMnemonic(KeyEvent.VK_R);
		menuDataWebMiningTermProfileLoadETP.setMnemonic(KeyEvent.VK_L);
		menuDataWebMiningTermProfileExtractTerms.setMnemonic(KeyEvent.VK_E);
		menuDataWebMiningTermProfileUpdatePathsETP.setMnemonic(KeyEvent.VK_U);

		// build menu "Audio"
		JMenu menuAudio = new JMenu("Audio");
		// create menu entries
		JMenuItem menuAudioLoadAudioFile = new JMenuItem("Load Audio File...");
		// create submenu "Extract Features"
		JMenu menuAudioExtractFeatures = new JMenu("Extract Features");
		// create menu entries
		JMenuItem menuAudioExtractFeatureFP = new JMenuItem("FP: Fluctuation Patterns...");
		JMenuItem menuAudioExtractFeatureMFCC = new JMenuItem("MFCC: Aucouturier and Pachet...");
		JMenuItem menuAudioExtractFeatureGMMME = new JMenuItem("GMM-ME: Mandel and Ellis...");
		// assign action listeners
		menuAudioExtractFeatureFP.addActionListener(new MenuAudioExtractFeatureFP_Action(this));
		menuAudioExtractFeatureMFCC.addActionListener(new MenuAudioExtractFeatureMFCC_Action(this));
		menuAudioExtractFeatureGMMME.addActionListener(new MenuAudioExtractFeatureGMMME_Action(this));
		// add menu entries
		menuAudioExtractFeatures.add(menuAudioExtractFeatureFP);
		menuAudioExtractFeatures.add(menuAudioExtractFeatureMFCC);
		menuAudioExtractFeatures.add(menuAudioExtractFeatureGMMME);
		menuAudioShowAudioPlayer = new JCheckBoxMenuItem("Show Audio Player", false);
		// assign action listeners
		menuAudioLoadAudioFile.addActionListener(new MenuAudioLoadAudioFile_Action(this));
		menuAudioShowAudioPlayer.addActionListener(new MenuAudioShowAudioPlayer_Action(this));
		// add menu entries
		menuAudio.add(menuAudioLoadAudioFile);
		menuAudio.add(menuAudioExtractFeatures);
		menuAudio.addSeparator();
		menuAudio.add(menuAudioShowAudioPlayer);
		// add menu to menu bar
		menuBar.add(menuAudio);
		// assign key-shortcuts (mnemonics and accelerators)
		menuAudio.setMnemonic(KeyEvent.VK_A);
		menuAudioLoadAudioFile.setMnemonic(KeyEvent.VK_L);
		menuAudioLoadAudioFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		menuAudioExtractFeatures.setMnemonic(KeyEvent.VK_F);
		menuAudioExtractFeatureFP.setMnemonic(KeyEvent.VK_F);
		menuAudioExtractFeatureMFCC.setMnemonic(KeyEvent.VK_M);
		menuAudioExtractFeatureGMMME.setMnemonic(KeyEvent.VK_G);
		menuAudioShowAudioPlayer.setMnemonic(KeyEvent.VK_P);

		// build menu "Visualization"
		JMenu menuVisu = new JMenu("Visualization");
		// create submenu "SOM"
		JMenu menuVisuSOM = new JMenu("SOM");
		// create menu entries
		JMenuItem menuVisuCreateSOM = new JMenuItem("Create SOM");
		JMenuItem menuVisuAssignLabels = new JMenuItem("Assign Labels");
		JMenuItem menuVisuClearLabels = new JMenuItem("Clear Labels");
		JMenuItem menuVisuCalcMDMLabels = new JMenuItem("Create Music Description Map (MDM) Labels");
		JMenuItem menuVisuShowSOMGrid = new JMenuItem("Show SOM-Grid");
		JMenuItem menuVisuShowMDMGrid = new JMenuItem("Show MDM-Grid");
//		?			JMenuItem menuVisuAddSOM = new JMenuItem("Add Data to SOM");
		JMenuItem menuVisuLoadSOM = new JMenuItem("Load SOM...");
		JMenuItem menuVisuSaveSOM = new JMenuItem("Save SOM...");
		JMenuItem menuVisuExportHTMLSOM = new JMenuItem("Export SOM to HTML...");
		JMenuItem menuVisuExportHTMLMDM = new JMenuItem("Export MDM to HTML...");
		// assign action listeners
		menuVisuCreateSOM.addActionListener(new MenuVisuCreateSOM_Action(this));
		menuVisuAssignLabels.addActionListener(new MenuVisuAssignLabels_Action(this));
		menuVisuClearLabels.addActionListener(new MenuVisuClearLabels_Action(this));
		menuVisuCalcMDMLabels.addActionListener(new MenuVisuCalcMDMLabels_Action(this));
		menuVisuShowSOMGrid.addActionListener(new MenuVisuShowSOMGrid_Action(this));
		menuVisuShowMDMGrid.addActionListener(new MenuVisuShowMDMGrid_Action(this));
		menuVisuLoadSOM.addActionListener(new MenuVisuLoadSOM_Action(this));
		menuVisuSaveSOM.addActionListener(new MenuVisuSaveSOM_Action(this));
		menuVisuExportHTMLSOM.addActionListener(new MenuVisuExportHTMLSOM_Action(this));
		menuVisuExportHTMLMDM.addActionListener(new MenuVisuExportHTMLMDM_Action(this));
		// add menu entries
		menuVisuSOM.add(menuVisuCreateSOM);
		menuVisuSOM.addSeparator();
		menuVisuSOM.add(menuVisuAssignLabels);
		menuVisuSOM.add(menuVisuClearLabels);
		menuVisuSOM.addSeparator();
		menuVisuSOM.add(menuVisuCalcMDMLabels);
		menuVisuSOM.addSeparator();
		menuVisuSOM.add(menuVisuShowSOMGrid);
		menuVisuSOM.add(menuVisuShowMDMGrid);
//		?			menuVisuSOM.add(menuVisuAddSOM);
		menuVisuSOM.addSeparator();
		menuVisuSOM.add(menuVisuLoadSOM);
		menuVisuSOM.add(menuVisuSaveSOM);
		menuVisuSOM.addSeparator();
		menuVisuSOM.add(menuVisuExportHTMLSOM);
		menuVisuSOM.add(menuVisuExportHTMLMDM);
		// add "SOM"-submenu to "Visualization"-menu
		menuVisu.add(menuVisuSOM);

		JMenu menuVisuGHSOM = new JMenu("GHSOM");
		JMenuItem menuVisuCreateGHSOM = new JMenuItem("Create GHSOM");
		JMenuItem menuVisuAssignGHSOMLabels = new JMenuItem("Assign Labels");
		JMenuItem menuVisuClearGHSOMLabels = new JMenuItem("Clear Labels");
		JMenuItem menuVisuShowGHSOMGrid = new JMenuItem("Show Grid");
		JMenuItem menuVisuShowGHSOMGridOnlyMean = new JMenuItem("Show Grid (only mean label)");
		JMenuItem menuVisuAssignGHSOMCoocMatrix = new JMenuItem("Load CoOcc Matrix");
		JMenuItem menuVisuAssignGHSOMCoocMatrixLabels = new JMenuItem("Load CoOcc Matrix Labels");
		JMenuItem menuVisuAssignGHSOMCoocLabels = new JMenuItem("Assign CoOcc Labels");
		JMenuItem menuVisuShowGHSOMGridCoocGroupProto = new JMenuItem("Show Grid (CoOcc Group Prototype)");
		JMenuItem menuVisuShowGHSOMGridCoocGroupProto2 = new JMenuItem("Show Grid (CoOcc Group Prototype, #Occs accounted)");
		JMenuItem menuVisuShowGHSOMGridCoocIndividualProto = new JMenuItem("Show Grid (CoOcc Individual Prototype)");
		JMenuItem menuVisuLoadGHSOM = new JMenuItem("Load GHSOM...");
		JMenuItem menuVisuSaveGHSOM = new JMenuItem("Save GHSOM...");
		menuVisuCreateGHSOM.addActionListener(new MenuVisuCreateGHSOM_Action(this));
		menuVisuShowGHSOMGrid.addActionListener(new MenuVisuShowGHSOMGrid_Action(this));
		menuVisuAssignGHSOMLabels.addActionListener(new MenuVisuAssignGHSOMLabels_Action(this));
		menuVisuClearGHSOMLabels.addActionListener(new MenuVisuClearGHSOMLabels_Action(this));
		menuVisuShowGHSOMGridOnlyMean.addActionListener(new MenuVisuShowGHSOMGrid_Action(this, new MeanPrototypeFinder()));
		menuVisuAssignGHSOMCoocMatrix.addActionListener(new MenuVisuAssignGHSOMCoocMatrix_Action(this));
		menuVisuAssignGHSOMCoocMatrixLabels.addActionListener(new MenuVisuAssignGHSOMCoocMatrixLabels_Action(this));
		menuVisuAssignGHSOMCoocLabels.addActionListener(new MenuVisuAssignGHSOMCoocLabels_Action(this));
		menuVisuShowGHSOMGridCoocGroupProto.addActionListener(new MenuVisuShowGHSOMGrid_Action(this, new WebCoocGroupPrototypeFinder(WebCoocGroupPrototypeFinder.CALC_TYPE_WEBCOOC)));
		menuVisuShowGHSOMGridCoocGroupProto2.addActionListener(new MenuVisuShowGHSOMGrid_Action(this, new WebCoocGroupPrototypeFinder(WebCoocGroupPrototypeFinder.CALC_TYPE_WEBCOOC_NUMBER_OF_OCCS)));
		menuVisuShowGHSOMGridCoocIndividualProto.addActionListener(new MenuVisuShowGHSOMGrid_Action(this, new WebCoocIndividualPrototypeFinder()));
		menuVisuLoadGHSOM.addActionListener(new MenuVisuLoadGHSOM_Action(this));
		menuVisuSaveGHSOM.addActionListener(new MenuVisuSaveGHSOM_Action(this));
		menuVisuGHSOM.add(menuVisuCreateGHSOM);
		menuVisuGHSOM.addSeparator();
		menuVisuGHSOM.add(menuVisuAssignGHSOMLabels);
		menuVisuGHSOM.add(menuVisuClearGHSOMLabels);
		menuVisuGHSOM.addSeparator();
		menuVisuGHSOM.add(menuVisuShowGHSOMGrid);
		menuVisuGHSOM.add(menuVisuShowGHSOMGridOnlyMean);
		menuVisuGHSOM.addSeparator();
		menuVisuGHSOM.add(menuVisuAssignGHSOMCoocMatrix);
		menuVisuGHSOM.add(menuVisuAssignGHSOMCoocMatrixLabels);
		menuVisuGHSOM.add(menuVisuAssignGHSOMCoocLabels);
		menuVisuGHSOM.add(menuVisuShowGHSOMGridCoocGroupProto);
		menuVisuGHSOM.add(menuVisuShowGHSOMGridCoocGroupProto2);
		menuVisuGHSOM.add(menuVisuShowGHSOMGridCoocIndividualProto);
		menuVisuGHSOM.addSeparator();
		menuVisuGHSOM.add(menuVisuLoadGHSOM);
		menuVisuGHSOM.add(menuVisuSaveGHSOM);
		menuVisu.add(menuVisuGHSOM);

		// create submenu "SDH"
		JMenu menuVisuSDH = new JMenu("SDH");
		// create menu entries
		JMenuItem menuVisuCreateSDH = new JMenuItem("Create SDH");
		JMenuItem menuVisuShowSDH = new JMenuItem("Show SDH");
		JMenuItem menuVisuLoadSDH = new JMenuItem("Load SDH (and underlying SOM)...");
		JMenuItem menuVisuSaveSDH = new JMenuItem("Save SDH (and underlying SOM)...");
		// assign action listeners
		menuVisuCreateSDH.addActionListener(new MenuVisuCreateSDH_Action(this));
		menuVisuShowSDH.addActionListener(new MenuVisuShowSDH_Action(this));
		menuVisuLoadSDH.addActionListener(new MenuVisuLoadSDH_Action(this));
		menuVisuSaveSDH.addActionListener(new MenuVisuSaveSDH_Action(this));
		// add menu entries
		menuVisuSDH.add(menuVisuCreateSDH);
		menuVisuSDH.addSeparator();
		menuVisuSDH.add(menuVisuShowSDH);
		menuVisuSDH.addSeparator();
		menuVisuSDH.add(menuVisuLoadSDH);
		menuVisuSDH.add(menuVisuSaveSDH);
		// add "SDH"-submenu to "Visualization"-menu
		menuVisu.add(menuVisuSDH);

		// create submenu "Similarity Vector"
		JMenu menuVisuSimVec = new JMenu("Similarity Vector");
		// create menu entries
		JMenuItem menuVisuCircledBarsBasic = new JMenuItem("Circled Bars - Basic");
		JMenuItem menuVisuCircledBarsAdvanced = new JMenuItem("Circled Bars - Advanced");
		// assign action listeners
		menuVisuCircledBarsBasic.addActionListener(new MenuVisuCircledBarsBasic_Action(this));
		menuVisuCircledBarsAdvanced.addActionListener(new MenuVisuCircledBarsAdvanced_Action(this));
		// add menu entries
		menuVisuSimVec.add(menuVisuCircledBarsBasic);
		menuVisuSimVec.add(menuVisuCircledBarsAdvanced);
		// add "Similarity Matrix"-submenu to "Visualization"-menu
		menuVisu.add(menuVisuSimVec);

		// create submenu "Similarity Matrix"
		JMenu menuVisuSimMat = new JMenu("Similarity Matrix");
		// create menu entries
		JMenuItem menuVisuCircledFans = new JMenuItem("Circled Fans");
		JMenuItem menuVisuProbabilisticNetwork = new JMenuItem("Probabilistic Network");
		JMenuItem menuVisuContinuousSimilarityRing = new JMenuItem("Continuous Similarity Ring");
		// assign action listeners
		menuVisuCircledFans.addActionListener(new MenuVisuCircledFans_Action(this));
		menuVisuProbabilisticNetwork.addActionListener(new MenuVisuProbabilisticNetwork_Action(this));
		menuVisuContinuousSimilarityRing.addActionListener(new MenuVisuContinuousSimilarityRing_Action(this));
		// add menu entries
		menuVisuSimMat.add(menuVisuCircledFans);
		menuVisuSimMat.add(menuVisuProbabilisticNetwork);
		menuVisuSimMat.add(menuVisuContinuousSimilarityRing);
		// add "Similarity Matrix"-submenu to "Visualization"-menu
		menuVisu.add(menuVisuSimMat);

		// create submenu "Term Occurrence Matrix"
		JMenu menuVisuTermOccurrenceMatrix = new JMenu("Term Occurrence Matrix");
		// create menu entries
		JMenuItem menuVisuTermOccurrenceMatrixSunburst = new JMenuItem("Sunburst");
		// assign action listeners
		menuVisuTermOccurrenceMatrixSunburst.addActionListener(new MenuVisuTermOccurrenceMatrixSunburst_Action(this));
		// add menu entries
		menuVisuTermOccurrenceMatrix.add(menuVisuTermOccurrenceMatrixSunburst);
		// add "Term Occurrence Matrix"-submenu to "Visualization"-menu
		menuVisu.add(menuVisuTermOccurrenceMatrix);

		// by Florian Marchl: 
		// method of menu creation extracted for reuse with popup menu
		JMenu menuVisuColormap = new JMenu("Colormap"); //createMenuVisuColormap();

//		JRadioButtonMenuItem menuVisuCMIslands = new JRadioButtonMenuItem("Islands", true);
//		JRadioButtonMenuItem menuVisuCMFire = new JRadioButtonMenuItem("Fire", false);
//		JRadioButtonMenuItem menuVisuCMColorful = new JRadioButtonMenuItem("Colorful", false);
//		JRadioButtonMenuItem menuVisuCMSun = new JRadioButtonMenuItem("Sun", false);
//		JRadioButtonMenuItem menuVisuCMOcean = new JRadioButtonMenuItem("Ocean", false);
//		JRadioButtonMenuItem menuVisuCMGray = new JRadioButtonMenuItem("Gray", false);

		menuVisuCMInverted = new JCheckBoxMenuItem("Inverted", false);
		// group radio buttons
		ButtonGroup rbGroup = new ButtonGroup();
		rbGroup.add(menuVisuCMIslands);
		rbGroup.add(menuVisuCMFire);
		rbGroup.add(menuVisuCMColorful);
		rbGroup.add(menuVisuCMSun);
		rbGroup.add(menuVisuCMOcean);
		rbGroup.add(menuVisuCMGray);

		// assign action listeners
		menuVisuCMIslands.addActionListener(new MenuVisuCMIslands_Action(this));
		menuVisuCMFire.addActionListener(new MenuVisuCMFire_Action(this));
		menuVisuCMColorful.addActionListener(new MenuVisuCMColorful_Action(this));
		menuVisuCMSun.addActionListener(new MenuVisuCMSun_Action(this));
		menuVisuCMOcean.addActionListener(new MenuVisuCMOcean_Action(this));
		menuVisuCMGray.addActionListener(new MenuVisuCMGray_Action(this));
		menuVisuCMInverted.addActionListener(new MenuVisuCMInverted_Action(this));

		menuVisuCMIslands.setMnemonic(KeyEvent.VK_I);
		menuVisuCMFire.setMnemonic(KeyEvent.VK_F);
		menuVisuCMColorful.setMnemonic(KeyEvent.VK_C);
		menuVisuCMSun.setMnemonic(KeyEvent.VK_S);
		menuVisuCMOcean.setMnemonic(KeyEvent.VK_O);
		menuVisuCMGray.setMnemonic(KeyEvent.VK_G);

		menuVisuColormap.setMnemonic(KeyEvent.VK_C);

		// add menu entries			
		menuVisuColormap.add(menuVisuCMIslands);
		menuVisuColormap.add(menuVisuCMFire);
		menuVisuColormap.add(menuVisuCMColorful);
		menuVisuColormap.add(menuVisuCMSun);
		menuVisuColormap.add(menuVisuCMOcean);
		menuVisuColormap.add(menuVisuCMGray);
		menuVisuColormap.addSeparator();
		menuVisuColormap.add(menuVisuCMInverted);

		// add "Colormap"-submenu to "Visualization"-menu
		menuVisu.addSeparator();
		menuVisu.add(menuVisuColormap);

		// create menu entry "Save Visualization..."
		JMenuItem menuVisuSaveVisu = new JMenuItem("Save Visualization...");
		menuVisuExportEPS = new JMenuItem("Export to EPS...");
		// assign action listeners
		menuVisuSaveVisu.addActionListener(new MenuVisuSaveVisu_Action(this));
		menuVisuExportEPS.addActionListener(new MenuVisuExportEPS_Action(this));
		// add menu entries
		menuVisu.addSeparator();
		menuVisu.add(menuVisuSaveVisu);
		menuVisu.add(menuVisuExportEPS);

		// create menu entry "Preferences"
		JMenuItem menuVisuPreferences = new JMenuItem("Preferences...");
		// assign action listeners
		menuVisuPreferences.addActionListener(new MenuVisuPreferences_Action(this));
		// add menu entries
		menuVisu.addSeparator();
		menuVisu.add(menuVisuPreferences);

		// add menu to menu bar
		menuBar.add(menuVisu);
		// assign key-shortcuts (mnemonics and accelerators)
		menuVisu.setMnemonic(KeyEvent.VK_V);
		menuVisuSOM.setMnemonic(KeyEvent.VK_S);
		menuVisuCreateSOM.setMnemonic(KeyEvent.VK_C);
		menuVisuCreateSOM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuAssignLabels.setMnemonic(KeyEvent.VK_A);
		menuVisuClearLabels.setMnemonic(KeyEvent.VK_E);
		menuVisuShowSOMGrid.setMnemonic(KeyEvent.VK_G);
		menuVisuShowSOMGrid.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuShowMDMGrid.setMnemonic(KeyEvent.VK_M);
		menuVisuShowMDMGrid.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuLoadSOM.setMnemonic(KeyEvent.VK_L);
		menuVisuSaveSOM.setMnemonic(KeyEvent.VK_S);
		menuVisuGHSOM.setMnemonic(KeyEvent.VK_G);
		menuVisuCreateGHSOM.setMnemonic(KeyEvent.VK_C);
		menuVisuCreateGHSOM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuAssignGHSOMLabels.setMnemonic(KeyEvent.VK_A);
		menuVisuClearGHSOMLabels.setMnemonic(KeyEvent.VK_E);
		menuVisuShowGHSOMGrid.setMnemonic(KeyEvent.VK_S);
		menuVisuShowGHSOMGrid.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuShowGHSOMGridOnlyMean.setMnemonic(KeyEvent.VK_O);
		menuVisuLoadGHSOM.setMnemonic(KeyEvent.VK_L);
		menuVisuSaveGHSOM.setMnemonic(KeyEvent.VK_S);
		menuVisuSDH.setMnemonic(KeyEvent.VK_D);
		menuVisuCreateSDH.setMnemonic(KeyEvent.VK_C);
		menuVisuCreateSDH.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		menuVisuShowSDH.setMnemonic(KeyEvent.VK_H);
		menuVisuLoadSDH.setMnemonic(KeyEvent.VK_L);
		menuVisuSaveSDH.setMnemonic(KeyEvent.VK_S);
		menuVisuSimVec.setMnemonic(KeyEvent.VK_V);
		menuVisuCircledBarsBasic.setMnemonic(KeyEvent.VK_B);
		menuVisuCircledBarsBasic.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVisuCircledBarsAdvanced.setMnemonic(KeyEvent.VK_A);
		menuVisuCircledBarsAdvanced.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVisuSimMat.setMnemonic(KeyEvent.VK_M);
		menuVisuCircledFans.setMnemonic(KeyEvent.VK_F);
		menuVisuCircledFans.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVisuProbabilisticNetwork.setMnemonic(KeyEvent.VK_N);
		menuVisuProbabilisticNetwork.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVisuContinuousSimilarityRing.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVisuContinuousSimilarityRing.setMnemonic(KeyEvent.VK_C);
		menuVisuTermOccurrenceMatrix.setMnemonic(KeyEvent.VK_T);
		menuVisuTermOccurrenceMatrixSunburst.setMnemonic(KeyEvent.VK_B);
		menuVisuTermOccurrenceMatrixSunburst.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));

		// by Florian Marchl: key mnemonic for colormap entries moved to colormap creation method
		menuVisuCMInverted.setMnemonic(KeyEvent.VK_N);
		menuVisuSaveVisu.setMnemonic(KeyEvent.VK_A);
		menuVisuExportEPS.setMnemonic(KeyEvent.VK_E);
		menuVisuPreferences.setMnemonic(KeyEvent.VK_P);

		// build menu "Help"
		JMenu menuHelp = new JMenu("Help");
		// create menu entries
		JMenuItem menuHelpAbout = new JMenuItem("About");
		// assign action listeners
		menuHelpAbout.addActionListener(new MenuHelpAbout_Action(this));
		// add menu entries
		menuHelp.add(menuHelpAbout);
		// add menu to menu bar
		menuBar.add(menuHelp);
		// assign key-shortcuts (mnemonics and accelerators)
		menuHelp.setMnemonic(KeyEvent.VK_H);
		menuHelpAbout.setMnemonic(KeyEvent.VK_A);

		menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		return menuBar;
	}

	/**
	 * Initializes the User Interface (Menus, StatusBar, etc)
	 */
	private void initUI() {
		// set the Java look and feel for frames and dialogs
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		// set default look and feel for main frame
		comirvaUI.setUndecorated(true);
		comirvaUI.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		// set exit on close
		comirvaUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set a nice font for menus and title border
		Font menuFont = new Font("Arial", Font.PLAIN, 13);
		Font statusBarFont = new Font("Arial", Font.PLAIN, 11);
		Font listFont = new Font("Arial", Font.PLAIN, 12);
		Font labelFont = new Font("Arial", Font.PLAIN, 12);
		// menu
		UIManager.put("Menu.font", menuFont);
		UIManager.put("MenuBar.font", menuFont);
		UIManager.put("MenuItem.font", menuFont);
		UIManager.put("CheckBoxMenuItem.font", menuFont);
		// lists
		UIManager.put("List.font", listFont);
		UIManager.put("ScrollPane.font", listFont);
		UIManager.put("ComboBox.font", listFont);
		// labels and texts and check/radio button labels
		UIManager.put("TextArea.font", labelFont);
		UIManager.put("TextField.font", labelFont);
		UIManager.put("Label.font", labelFont);
		UIManager.put("RadioButton.font", labelFont);
		UIManager.put("CheckBox.font", labelFont);

		// update UI for matrix list and meta-data list
		this.dataMatrixList.updateUI();
		this.dataMetaDataList.updateUI();

		// get and setup ContentPane
		Container contentPane = comirvaUI.getContentPane();
		contentPane.setBackground(colBackground);
		contentPane.setLayout(new BorderLayout());

		// setup status & prgress bar
		statusBar.setFont(statusBarFont);
		statusBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(165, 163, 151)),
				BorderFactory.createEmptyBorder(2,5,2,5)));

		// build status panel layout
		GridBagLayout statusGridLayout = new GridBagLayout();
		GridBagConstraints sbc = new GridBagConstraints();
		GridBagConstraints pbc = new GridBagConstraints();

		sbc.fill = GridBagConstraints.BOTH;
		pbc.fill = GridBagConstraints.BOTH;
		sbc.weightx =  9.0;
		pbc.weightx =  1.0;
		pbc.gridwidth = GridBagConstraints.REMAINDER;

		statusGridLayout.setConstraints(statusBar, sbc);
		statusGridLayout.setConstraints(progressBar, pbc);

		JPanel statusPane = new JPanel(statusGridLayout);
		statusPane.add(statusBar);
		statusPane.add(progressBar);

		// add menus
		contentPane.add(this.createMenu(), BorderLayout.NORTH);

		// add status panel		
		//contentPane.add(statusBar, BorderLayout.SOUTH);
		contentPane.add(statusPane, BorderLayout.SOUTH);

		// initialize panel to take matrix list
		panelMatrixList.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Data Management", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Arial", Font.PLAIN, 12)),
				BorderFactory.createEmptyBorder(0,1,0,1)));
		panelMatrixList.setBackground(colBackground);				

		// add and design matrix list		
		dataMatrixList.setBackground(colBackground);
		dataMatrixList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add and design visu list
		VisuListEventListener vleListener = new VisuListEventListener(this);
		visuList.setBackground(colBackground);
		visuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// visuList.addListSelectionListener(vleListener);	// automatic update of visu pane after selection changes
		visuList.addMouseListener(vleListener);				// update of visu pane after mouse click

		// provide the matrix list with a scroll pane
		JScrollPane jscp_matrix = new JScrollPane(dataMatrixList);
		JScrollPane jscp_visu = new JScrollPane(visuList);

		// design data management toolbar
		toolbarDataManagement.setFloatable(false);

		// JTabbedPane tbp_matrices = new JTabbedPane();
		tbp_matrices.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tbp_matrices.addTab("Visualizations", jscp_visu);	// static visualization list
		tbp_matrices.addTab("Data Matrix", jscp_matrix);	// data matrix list

		// add the scroll pane component to the panel for the data matrix list
		// panelMatrixList.add(jscp_matrix);
		panelMatrixList.add(toolbarDataManagement, BorderLayout.NORTH);
		panelMatrixList.add(tbp_matrices, BorderLayout.CENTER);

		// initialize panel to take meta-data list
		panelMetaDataList.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Meta-Data", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Arial", Font.PLAIN, 12)),
				BorderFactory.createEmptyBorder(0,1,0,1)));
		panelMetaDataList.setBackground(colBackground);
		// add and design meta-data list
		dataMetaDataList.setBackground(colBackground);
		dataMetaDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// provide the meta-data list with a scroll pane
		JScrollPane jscp_metadata = new JScrollPane(dataMetaDataList);
		// add the scroll pane component to the panel for the meta-data list
		panelMetaDataList.add(jscp_metadata);

		// initialize panel to take audio player
		paneAudioPlayer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Audio Player", TitledBorder.CENTER, TitledBorder.BELOW_TOP, new Font("Arial", Font.PLAIN, 12)),
				BorderFactory.createEmptyBorder()));
		paneAudioPlayer.setBackground(colBackground);
		// initialize buttons used by the audio player
		btnPlayPause = new JButton(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Play.gif")));
		btnStop = new JButton(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Stop.gif")));
		btnNext = new JButton(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Next.gif")));
		btnNext.setEnabled(false);
		btnPrevious = new JButton(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Previous.gif")));
		btnPrevious.setEnabled(false);
		// set margins to 0 (fill complete button with icon)
		btnPlayPause.setMargin(new Insets(0,0,0,0));
		btnStop.setMargin(new Insets(0,0,0,0));
		btnNext.setMargin(new Insets(0,0,0,0));
		btnPrevious.setMargin(new Insets(0,0,0,0));
		// add buttons to audio player
		paneAudioPlayer.add(btnPlayPause);
		paneAudioPlayer.add(btnStop);
		paneAudioPlayer.add(btnPrevious);
		paneAudioPlayer.add(btnNext);
		// add action listeners
		btnPlayPause.addActionListener(new ButtonAudioPlayerPlay_Action(this));
		btnStop.addActionListener(new ButtonAudioPlayerStop_Action(this));
		btnNext.addActionListener(new ButtonAudioPlayerNext_Action(this));
		btnPrevious.addActionListener(new ButtonAudioPlayerPrevious_Action(this));

		// create visualization area
		paneVisu.setPreferredSize(new Dimension(800, 600));
		paneVisu.setMinimumSize(new Dimension(200, 150));
		// set default ColorMap to "Islands"
		paneVisu.setColorMap(new ColorMap_Islands());
		paneVisu.setVisible(true);
		// insert split panes
		// split pane: data matrix list / meta-data list
		JSplitPane jspMatrixLists = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelMatrixList, panelMetaDataList);
		jspMatrixLists.setBorder(BorderFactory.createEmptyBorder());
		// split pane: matrix lists / audio player
//		JSplitPane jspRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jspMatrixLists, panelAudioPlayer);
//		jspRight.setBorder(BorderFactory.createEmptyBorder());
		// split pane: visu area / data&player area
		JSplitPane jspMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paneVisu, paneRight);
		paneRight.add(jspMatrixLists, BorderLayout.CENTER);
		paneRight.add(paneAudioPlayer, BorderLayout.SOUTH);
		paneRight.remove(paneAudioPlayer);
		jspMain.setBorder(BorderFactory.createEmptyBorder());
		// set some properties of the split panes
		jspMain.setOneTouchExpandable(true);
		jspMain.setContinuousLayout(true);
//		jspRight.setOneTouchExpandable(false);
//		jspRight.setDividerSize(5);
//		jspRight.setEnabled(false);
//		jspRight.setContinuousLayout(true);
		jspMatrixLists.setOneTouchExpandable(false);
		jspMatrixLists.setDividerSize(4);
		jspMatrixLists.setContinuousLayout(true);
		// add split pane to content pane
		contentPane.add(jspMain, BorderLayout.CENTER);

		// display and center main window
		comirvaUI.setSize(1024,768);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension comirvaUISize = comirvaUI.getSize();
		comirvaUI.setLocation((screenSize.width - comirvaUISize.width) / 2, (screenSize.height - comirvaUISize.height) / 2);
		comirvaUI.setVisible(true);

		// force right pane (matrix and meta-data list and audio player) to have a width of 20% of left pane (visu pane)
		jspMain.setDividerLocation(0.8);
		// force matrix and metadata-lists to be sized equally
		jspMatrixLists.setDividerLocation(0.5);
		// force right pane (matrix and meta-data list and audio player) to remain constant in size when the window is resized
		jspMain.setResizeWeight(1.0);
		// force pane splitting matrix and meta-data lists to remain constant in size when the window is resized
		jspMatrixLists.setResizeWeight(0.5);
//		// force data lists/audio player split pane to give 80% of the size to the lists and 20% to the audio player
//		jspRight.setDividerLocation(0.95);
//		// force audio player to remain constant in size when the pane is resized
//		jspRight.setResizeWeight(1.0);

		// add mouse listeners for the popup menus
		JPopupMenu visupopup = this.createVisuPopupMenu();
		paneVisu.registerPopupMenuListener(visupopup);		
		dataMatrixList.addMouseListener(new PopupMenuMouseAdapter(createDataMatrixPopupMenu()));
		dataMetaDataList.addMouseListener(new PopupMenuMouseAdapter(createMetaDataPopupMenu()));
		paneVisu.addMouseListener(new PopupMenuMouseAdapter(visupopup));
		visuList.addMouseListener(new PopupMenuMouseAdapter(this.createVisuListPopupMenu()));

		this.tbp_matrices.setSelectedIndex(1);	// preselect data matrix tab
		// display welcome-message
		setStatusBar("Welcome to CoMIRVA! - System is ready");
	}

	/**
	 * Tries to load the user preferences for the visualization pane.
	 */
	private void loadVisuPreferences() {
		String userHomeDir = System.getProperty("user.home");			// get user's home directory
		File prefs = new File(userHomeDir+"/CoMIRVA.prefs");
		// if a visu preferences file exists, load it
		if (prefs.exists()) {
			setStatusBar("Loading visualization preferences");
			try {
				// open and load
				FileInputStream in = new FileInputStream(prefs);
				ObjectInputStream s = new ObjectInputStream(in);
				VisuPreferences data = (VisuPreferences) s.readObject();
				// update preferences object and update gui elements according to prefences
				this.paneVisu.setVisuPreferences(data);
				this.dmPreferences = data;
				toolbarDataManagement.setFloatable(data.isToolbarFloatable());
				tbp_matrices.setTabLayoutPolicy(data.getTabLayout());
				// close
				s.close();
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			setStatusBar("Visualization preferences successfully loaded from file: " + prefs.getAbsolutePath());
		} else {		// if not, use default values
			this.paneVisu.setVisuPreferences(new VisuDefaultPreferences());
		}
		// if EPS-output is not allowed, disable respective menu entry
		this.menuVisuExportEPS.setEnabled(this.paneVisu.getVisuPreferences().isEnableEPS());
	}

	/**
	 * tries to find the current selected list entry in the jungle of data matrix tabs.
	 * The selected entry of the current selected tab is considered to be the selected.
	 * If nothing is selected in the current tab or the "Visualization" tab
	 * is currently selected then <code>null</code> or  is returned
	 * @return the current selected data matrix entry or <code>null</code> if nothing is selected
	 */
	private DataMatrix getSelectedDataMatrix() {
		int index = tbp_matrices.getSelectedIndex();
		int selected = -1;
		Vector source = null;

		if (index < 1) {
			return null;	// visualization tab or no tab is selected, but not a data matrix
		} else if (index == 1) {	
			// data matrix tab is selected (original)
			selected = dataMatrixList.getSelectedIndex();
			source = ws.matrixList;
		} else {	// index > 1
			// a newly created tab is selected
			selected = this.dataMatrixListVector.get(index - 2).getSelectedIndex();
			source = ws.additionalMatrixList.get(index - 2);
		}			
		if (selected < 0 || source == null) {
			return null;		// nothing selected
		}
		// retrieve data matrix from specified source vector
		return (DataMatrix)source.elementAt(selected); 
	}

	/**
	 * Determinds which visualization list item (SOM, GHSOM etc.) is selected. If no element is selected or the selected element
	 * is not the same type as given by the parameter then <code>null</code> is returned.
	 * @param items an array (may be empty but not <code>null</code>) which defines which type of VisuListItems should be searched for.
	 * @return the list item that is selected (dynamic type is the same as the array given as parameter).
	 */
	private VisuListItem getSelectedvisualizationItem(VisuListItem[] items, String className) {
		// String className = items[0].getClass().getName();
		// get only items of desired type from visu list
		items = ws.getVisuListItems(className).toArray(items);  //ws.visuList.toArray(items);		
		int selected = this.visuList.getSelectedIndex();		// get selection
		//boolean ghsomSelected = false;
		VisuListItem itemSelected = null;
		if (items.length == 0) {			// no ghsom loaded: 	// error handling
			return null;
		} else if (items.length == 1) {	// if only one ghsom is loaded, 
			this.visuList.setSelectedIndex(ws.visuList.indexOf(items[0]));	// be user-friendly and select it
			itemSelected = items[0];		// finally a GHSOM is selected
		} else {							// multiply choices - ask user to select the one he wants to use
			if (selected < 0) {				// no GHSOM selected
				return null;
			}
			for (int i=0; i<items.length; i++) {
				if (items[i].equals(ws.visuList.get(selected))) {
					itemSelected = items[i];
				}
			}
		}
		return itemSelected;
	}
	
	/**
	 * Tries to load the user preferences for the data management pane.
	 */
/*	private void loadVisuPreferences() {
		String userHomeDir = System.getProperty("user.home");			// get user's home directory
		File prefs = new File(userHomeDir+"/CoMIRVA_dm.prefs");
		// if a data management preferences file exists, load it
		if (prefs.exists()) {
			setStatusBar("Loading data management preferences");
			try {
				FileInputStream in = new FileInputStream(prefs);
				ObjectInputStream s = new ObjectInputStream(in);
				// this.paneVisu.setVisuPreferences((VisuPreferences)s.readObject());
				VisuPreferences dmPref = (VisuPreferences)s.readObject();
				// apply preferences
				dmPreferences = dmPref;
				// update gui elements according to new preferences
				toolbarDataManagement.setFloatable(dmPref.isToolbarFloatable());
				tbp_matrices.setTabLayoutPolicy(dmPref.getTabLayout());
				// close streams
				s.close();
				in.close();
			} catch (InvalidClassException e) {
				JOptionPane.showMessageDialog(comirvaUI, 
						"Cannot read data management Preferences because the file seems to be an older format", "Error", JOptionPane.ERROR_MESSAGE);
				this.dmPreferences = new VisuPreferences();		// set to default
			} catch (FileNotFoundException e) {
				this.dmPreferences = new VisuPreferences();
				System.err.println("Data management preferences file not found - using default values");
			} catch (IOException e) {
				this.dmPreferences = new VisuPreferences();
				System.err.println("Some I/O Problems occured - using default values");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			setStatusBar("Data management preferences successfully loaded from file: " + prefs.getAbsolutePath());
		} else {		// if not, use default values
			this.dmPreferences = new VisuPreferences();
		}
		// if EPS-output is not allowed, disable respective menu entry
		// this.menuVisuExportEPS.setEnabled(this.paneVisu.getVisuPreferences().isEnableEPS());
	}
*/
	// actions for audio player
	// "play"/"pause"
	private void btnAudioPlayerPlay_actionPerformed(ActionEvent actionEvent) {
		if (this.pl == null)						// play list not initialized?
			this.pl = new AudioPlaylistPlayer();	// create new instance
		if (!this.pl.isAlive()) {	// audio playlist player thread has not been started -> start it (and playing)
//			this.pl.addTrack(new File("/Research/Music Collections/Private/Rock/In Extremo/Kein Blick Zurück (Disc I)/In Extremo - Kein Blick Zurück (Disc I) - 02 - Ai Vis Lo Lop.mp3"));
//			this.pl.addTrack(new File("/Research/Music Collections/Private/Rock/In Extremo/Kein Blick Zurück (Disc I)/In Extremo - Kein Blick Zurück (Disc I) - 08 - Omnia Sol Temperat.mp3"));
//			this.pl.addTrack(new File("F:/Music/WAV/smash.wav"));
//			this.pl.addTrack(new File("F:/Music/WAV/clapton.wav"));
//			this.pl.addTrack(new File("F:/Music/WAV/openyoureyes.wav"));
//			this.pl.addTrack(new File("F:/Music/WAV/freude2.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Ash - 1977 - 03 - Girl From Mars.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Bad Religion - No Substance - 06 - Raise Your Voice.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Black Sabbath - Paranoid - 02 - Paranoid.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Blink 182 - Enema Of The State - 08 - All the Small Things.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Bloodhound Gang - Fire Water Burn.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Bloodhoung Gang - Hooray For Boobies - 10 - The Bad Touch.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Bomfunk MC's - In Stereo - 04 - Freestyler.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Bots - Sieben Tage lang.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Cranberries - Zombie.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Deep Purple - Purplexed - 02 - The Battle Rages On.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Desireless - Voyage Voyage.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Dire Straits - On The Night - 10 - Brothers In Arms.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Guano Apes - Proud Like A God - 01 - Open Your Eyes.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Helloween - Metal Jukebox - 07 - Hocus Pocus (Focus).wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Helloween - Metal Jukebox - 10 - White Room (Cream).wav"));
//			this.pl.addTrack(new File("c:/temp/wav/In Extremo - Mein Rasend Herz - 01 - Raue See.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/In Extremo - Weckt die Toten! - 01 - Ai vis lo lop.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/JBO - Gimme Dope Joanna.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/JBO - Saufen Saufen Saufen.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Juli - Perfekte Welle.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Kansas - Point Of Know Return - 07 - Dust In The Wind.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Knack - My Sherona.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Led Zeppelin - Remasters - 15 - Stairway To Heaven.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Majesty - Metal Law (Disc 1) - 07 - Into The Stadiums.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Marillion - Fugazi - 07 - Fugazi.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Metallica - Garage Inc. (Disc 1) - 05 - Die, Die My Darling.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Metallica - Garage Inc. (Disc 1) - 09 - Whiskey in the Jar.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Metallica - Ride The Lightning - 07 - Creeping Death.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Midnight Oil - Beds Are Burning.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Nick Kershaw - The Riddle.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Nightwish - Crimson Tide Deep Blue Sea.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Nightwish - Over the Hills And Far Away.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Norah Jones - Don't Know Why.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Norah Jones - Seven Years.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Norah Jones - Sunrise.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Norah Jones - Turn Me On.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Opus - Live Is Life.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Papermoon - The World in Lucy's Eyes - 04 - Where The Wind Blows Forever.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Patti Smith - Easter - 03 - Because The Night.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Peter Schilling - Major Tom - 01 - Major Tom (Völlig Losgelöst).wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Ronan Keating feat. Yusuf - Father and Son.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Santana - Samba Pa Ti.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/The Clash - London Calling - 01 - London Calling.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Tom Petty - Free Falling.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Type O Negative - The Least Worst Of - 02 - Everyone I Love Is Dead.wav"));
//			this.pl.addTrack(new File("c:/temp/wav/Van Halen - 1984 - 02 - Jump.wav"));
			// start playing
			this.pl.start();
			// set icon of "play"/"pause"-button to "pause"
			this.btnPlayPause.setIcon(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Pause.gif")));
			// enable "next"/"previous" buttons in state "playing"
			this.btnNext.setEnabled(true);
			this.btnPrevious.setEnabled(true);
		} else {				// audio playlist player thread has already been started -> toggle state between "play" and "pause"
			if (this.pl.isPaused()) {		// state is "paused"
				// continue playing
				this.pl.continuePlaying();
				// set icon of "play"/"pause"-button to "pause"
				this.btnPlayPause.setIcon(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Pause.gif")));
				// enable "next"/"previous" buttons in state "playing"
				this.btnNext.setEnabled(true);
				this.btnPrevious.setEnabled(true);
			} else {					// state is "playing" (not "paused")
				// pause playing
				this.pl.pausePlaying();
				// set icon of "play"/"pause"-button to "play"
				this.btnPlayPause.setIcon(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Play.gif")));
				// disable "next"/"previous" buttons in state "paused"
				this.btnNext.setEnabled(false);
				this.btnPrevious.setEnabled(false);
			}
		}
	}
	// "stop"
	private void btnAudioPlayerStop_actionPerformed(ActionEvent actionEvent) {
		if (this.pl != null)	{				// not already stopped
			this.pl.stopPlaying();
			this.pl = null;
		}
		// set icon of "play"/"pause"-button to "play"
		this.btnPlayPause.setIcon(new ImageIcon(MainUI.class.getResource("MainUI_AudioPlayer_Play.gif")));
		// disable "next"/"previous" buttons in state "paused"
		this.btnNext.setEnabled(false);
		this.btnPrevious.setEnabled(false);
	}
	// "next"
	private void btnAudioPlayerNext_actionPerformed(ActionEvent actionEvent) {
		pl.playNext();
	}
	// "previous"
	private void btnAudioPlayerPrevious_actionPerformed(ActionEvent actionEvent) {
		pl.playPrevious();
	}


	// actions for menus
	// Menu "File"
	// File -> Load Matrix Data File...
	private void menuFileLoadDataFile_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog with filter for Matlab ASCII files
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		MatlabASCIIFileFilter filter = new MatlabASCIIFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// load file
			File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(fileData.getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// actually load
			setStatusBar("Loading data matrix from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
			// create a MatrixDataFileLoaderThread and try to load the file
			MatrixDataFileLoaderThread dfl = new MatrixDataFileLoaderThread(fileData, ws.matrixList, this.statusBar, this.ws.listMatrices);
			dfl.start();
		}
	}
	// File -> Save Matrix Data File...
	private void menuFileSaveDataFile_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no meta-data available or selected
		DataMatrix writeMatrix = getSelectedDataMatrix();
		if (writeMatrix == null)		// no matrix selected
			// if ((ws.matrixList.isEmpty()) || (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1)))
			JOptionPane.showMessageDialog(comirvaUI, "Please select the data matrix that you wish to save.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix available and selected
		else {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			MatlabASCIIFileFilter filter = new MatlabASCIIFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving data matrix to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						// create BufferedWriter
						Writer ow = new BufferedWriter(new FileWriter(fileData));
						// get selected DataMatrix (already done above)
						// DataMatrix writeMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
						// for all rows
						for (int i=0; i<writeMatrix.getNumberOfRows(); i++) {
							// get row
							Vector row = writeMatrix.getRow(i);
							// get every element in row
							for (int j=0; j<row.size(); j++) {
								ow.write(row.elementAt(j).toString()+" ");
							}
							// start new line after each row
							ow.write("\n");
						}
						ow.flush();
						setStatusBar("Data matrix stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
						ow.close();
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving SOM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	// File -> Load Meta-Data File...
	private void menuFileLoadMetaDataFile_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog with filter for Matlab ASCII files
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// load file
			File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(fileData.getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// actually start loading
			setStatusBar("Loading meta-data from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
			// create a MatrixDataFileLoaderThread and try to load the file
			MetaDataFileLoaderThread mdfl = new MetaDataFileLoaderThread(fileData, this.ws.metaDataList, this.statusBar, this.ws.listMetaData);
			mdfl.start();
		}
	}
	// File -> Save Meta-Data File...
	private void menuFileSaveMetaDataFile_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix available or selected
		if ((ws.metaDataList.isEmpty()) || (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)))
			JOptionPane.showMessageDialog(comirvaUI, "Please select the Meta-Data instance that you wish to save.", "Error", JOptionPane.ERROR_MESSAGE);
		// meta-data available and selected
		else {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			TextFileFilter filter = new TextFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving Meta-Data instance to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						// create BufferedWriter
						Writer ow = new BufferedWriter(new FileWriter(fileData));
						// get selected Vector
						Vector writeMetaData= (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
						// for all strings in meta-data instance
						for (int i=0; i<writeMetaData.size(); i++) {
							ow.write(writeMetaData.elementAt(i).toString()+"\n");
						}
						ow.flush();
						setStatusBar("Meta-Data instance stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
						ow.close();
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving SOM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	// File -> Delete Selected Item
	public void menuFileDeleteSelectedItem_actionPerformed(ActionEvent actionEvent) {
		int index = tbp_matrices.getSelectedIndex();	// get index of active tab
		int selected = 									// get selectd index in list
			index < 2 ? this.dataMatrixList.getSelectedIndex()
					: this.dataMatrixListVector.elementAt(index-2).getSelectedIndex();

			if (selected < 0) {
				// nothing selected
				String missing = index < 1 ? "visualization" : "data matrix";
				JOptionPane.showMessageDialog(comirvaUI, "Sorry, cannot found a selected " + missing + "!", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				switch (index) {
				case 0:
					// if (JOptionPane.showConfirmDialog(comirvaUI, "Are you sure to delete the selected visualization\n'" + this.visuList.getSelectedValue() + "'?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					ws.listVisu.remove(index);
					ws.visuList.remove(index);
					//}
					break;
				case 1:
					ws.listMatrices.remove(selected);
					ws.matrixList.remove(selected);
					break;
				default:
					ws.additionalListMatrices.get(index-2).remove(selected);
				ws.additionalMatrixList.get(index-2).remove(selected);
				break;
				}
			}
	}
	// File -> Empty Matrix Data File List
	private void menuFileEmptyDataFileList_actionPerformed(ActionEvent actionEvent) {
		int selected = tbp_matrices.getSelectedIndex();
		if (selected < 0) {
			JOptionPane.showMessageDialog(comirvaUI, "No tabulator selected. Please select the tab containing the list that should be emptied!", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (selected < 1) {
			JOptionPane.showMessageDialog(comirvaUI, "The visualization list cannot be emptied at once. Please delete the entries seperatly!", "Problem", JOptionPane.ERROR_MESSAGE);
		} else if (selected < 2) {
			// clear entries in data matrix list of UI
			this.ws.listMatrices.clear();
			// empty Vector ws.matrixList containing the DataMatrix-instances from loaded data matrix files
			this.ws.matrixList.clear();
		} else {  // selected >= 2 
			// clear entries in data matrix list of UI
			this.ws.additionalListMatrices.get(selected-2).clear();
			// empty Vector at the corresponding index in ws.additionalMatrixList
			this.ws.additionalMatrixList.get(selected-2).clear();
		}
	}
	// File -> Empty Meta-Data File List
	private void menuFileEmptyMetaDataFileList_actionPerformed(ActionEvent actionEvent) {
		// clear entries in meta-data list of UI
		this.ws.listMetaData.clear();
		// empty Vector ws.metaDataList containing the metadata from loaded data files
		this.ws.metaDataList.clear();
	}
	// File -> Load Workspace...
	private void menuFileLoadWorkspace_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		WorkspaceFileFilter filter = new WorkspaceFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				// load file
				File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
				// remember directory where selected file is located (for future file chooser access)
				dmPreferences.setLastDir(fileData.getPath());
				// save new last directory
				saveDataManagementPrefs(dmPreferences);
				// actually start loading
				setStatusBar("Loading workspace from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
				FileInputStream in = new FileInputStream(fileData);
				ObjectInputStream s = new ObjectInputStream(in);
				ws = (Workspace)s.readObject();
				s.close();
				in.close();
				// update list models
				dataMatrixList.setModel(ws.listMatrices);
				dataMetaDataList.setModel(ws.listMetaData);
				visuList.setModel(ws.listVisu);
				// recreate addtional tabs
				for (int i=0; i<ws.additionalListMatrices.size(); i++) {
					addDataMatrixTab(ws.additionalListMatrices.elementAt(i), ws.additionalMatrixNames.elementAt(i));					
				}
				// done ... ready
				setStatusBar("Workspace successfully loaded from file: " + fileChooser.getSelectedFile().getAbsolutePath());
			} catch (ClassNotFoundException fnfe) {
				JOptionPane.showMessageDialog(comirvaUI, "Internal error (ClassNotFoundException) occurred while loading workspace from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while loading workspace from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	// File -> Save Workspace...
	private void menuFileSaveWorkspace_actionPerformed(ActionEvent actionEvent) {
		// if no Workspace-instance has been created, show error message
		if (ws == null)
			JOptionPane.showMessageDialog(comirvaUI, "No workspace has been created.\nThis should not happen under normal circumstances.\nPlease restart CoMIRVA", "Fatal Error", JOptionPane.ERROR_MESSAGE);
		else {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			WorkspaceFileFilter filter = new WorkspaceFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving workspace to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						// serialize workspace
						FileOutputStream out = new FileOutputStream(fileData);
						ObjectOutputStream ous = new ObjectOutputStream(out);
						ous.writeObject(ws);
						ous.flush();
						ous.close();
						out.close();
						setStatusBar("Workspace successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
					}
				} catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(comirvaUI, "Workspace could not be restored because the workspace file was not found:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving workspace to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	// File -> Exit
	private void menuFileExit_actionPerformed(ActionEvent actionEvent) {
		System.exit(0);
	}

	// Menu "Audio"
	// Audio -> Load Audio File
	private void menuAudioLoadAudioFile_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog with filter for Audio files
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		AudioFileFilter filter = new AudioFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// load audio file
			File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(fileData.getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// add selected file to playlist and show player
			if (this.pl == null)
				this.pl = new AudioPlaylistPlayer();
			this.pl.addTrack(fileData);
			// show audio player and update UI
			if (!this.menuAudioShowAudioPlayer.isSelected()) {
				this.menuAudioShowAudioPlayer.setSelected(true);
				this.paneRight.add(this.paneAudioPlayer, BorderLayout.SOUTH);
				this.paneRight.updateUI();
			}
//			setStatusBar("Loading audio data from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
			// create a MatrixDataFileLoaderThread and try to load the file
//			AudioFileLoaderThread afl = new AudioFileLoaderThread(fileData, this.ws.matrixList, this.statusBar, this.ws.listMatrices);
//			afl.start();
//			ap = new AudioPlayer(fileData);
//			ap.start();
		}
	}
	// Audio -> Extract Feature -> Fluctuation Patterns
	private void menuAudioExtractFeatureFP_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog so that user can choose directory where audio files reside
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// let the user select directories and files
		// if he/she selects 1 or more audio file(s), only this/these file is processed
		// if he/she selects a directory, the entire directory is searched for audio files
		MP3FileFilter filter = new MP3FileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// calc FPs
			// create a list of all files which reside in this directory and in subdirs
			File[] audioFiles = FileUtils.getAllFilesRecursively(fileChooser.getSelectedFiles(), new MP3FileFilter());			
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(audioFiles[0].getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
//			for (int i=0; i<audioFiles.length; i++)
//			System.out.println(audioFiles[i].getAbsolutePath().toString());
			// start thread to extract FPs
			FluctuationPatternExtractionThread fpet = new FluctuationPatternExtractionThread(audioFiles, this.ws, this.statusBar);
			fpet.start();
		}
	}
	// Audio -> Extract Feature -> MFCC
	private void menuAudioExtractFeatureMFCC_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog so that user can choose directory where audio files reside
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// let the user select directories and files
		// if he/she selects 1 or more xml-file(s), only this/these file is processed
		// if he/she selects a directory, the entire directory is searched for xml-files
		MP3FileFilter filter = new MP3FileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// calc MFCCs
			// calc GMM: Mandel Ellis
			// create a list of all files which reside in this directory and in subdirs
			File[] audioFiles = FileUtils.getAllFilesRecursively(fileChooser.getSelectedFiles(), new MP3FileFilter());

			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(audioFiles[0].getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
//			for (int i=0; i<audioFiles.length; i++)
//			System.out.println(audioFiles[i].getAbsolutePath().toString());
			// start thread to extract MFCCs
			TimbreDistributionExtractionThread tdet = new TimbreDistributionExtractionThread(audioFiles, this.ws, this.statusBar);
			tdet.start();
		}
	}
	// Audio -> Extract Feature -> GMM-ME: Mandel and Ellis
	private void menuAudioExtractFeatureGMMME_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog so that user can choose directory where audio files reside
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// let the user select directories and files
		// if he/she selects 1 or more xml-file(s), only this/these file is processed
		// if he/she selects a directory, the entire directory is searched for xml-files
		MP3FileFilter filter = new MP3FileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// create a list of all files which reside in this directory and in subdirs
			File[] audioFiles = FileUtils.getAllFilesRecursively(fileChooser.getSelectedFiles(), new MP3FileFilter());
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(audioFiles[0].getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
//			for (int i=0; i<audioFiles.length; i++)
//			System.out.println(audioFiles[i].getAbsolutePath().toString());
			// start thread to extract MFCCs
			MandelEllisExtractionThread meet = new MandelEllisExtractionThread(audioFiles, this.ws, this.statusBar);
			meet.start();
		}
	}

	// Audio -> Show Audio Player
	private void menuAudioShowAudioPlayer_actionPerformed(ActionEvent actionEvent) {
		// toggle state of audio player
		if (this.menuAudioShowAudioPlayer.isSelected())
			this.paneRight.add(this.paneAudioPlayer, BorderLayout.SOUTH);
		else
			this.paneRight.remove(this.paneAudioPlayer);
		// update UI
		this.paneRight.updateUI();
	}

	// Menu "Data"
	// Submenu "Data Matrix"
	// Data -> Data Matrix -> Rename...
	private void menuDataDataMatrixRename_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a matrix?
		DataMatrix selectedDM = this.getSelectedDataMatrix();
		if (selectedDM == null) 
			// if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1) || getSelectedDataMatrix()==null)
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get selected DataMatrix
			//DataMatrix selectedDM = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// show dialog for setting a new name
			DataMatrixRenameDialog dmrDialog = new DataMatrixRenameDialog(comirvaUI, selectedDM.getName(), true);
			// position and initialize the dialog
			Dimension dlgSize = dmrDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			dmrDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			dmrDialog.setModal(true);
			dmrDialog.pack();
			dmrDialog.setVisible(true);
			// if dialog was closed by clicking on "Rename", do rename
			if (dmrDialog.confirmOperation){
				// set new name for selected data matrix
				selectedDM.setName(dmrDialog.getNewName());
				// set new name in data matrix list (for UI)
				int index = tbp_matrices.getSelectedIndex();
				int selected = -1;
				DefaultListModel source = null;
				if (index == 1) {	
					// data matrix tab is selected (original)
					source = ws.listMatrices;
					selected = dataMatrixList.getSelectedIndex();
				} else {	// index > 1
					// a newly created tab is selected
					source = ws.additionalListMatrices.get(index - 2);
					selected = this.dataMatrixListVector.get(index - 2).getSelectedIndex();
				}	
				source.setElementAt(selectedDM.getName(), selected);
				// ws.listMatrices.setElementAt(selectedDM.getName(), dataMatrixList.getSelectedIndex());
			}
		}
	}
	// Data -> Data Matrix -> Sort List
	private void menuDataDataMatrixSort_actionPerformed(ActionEvent actionEvent) {
		// the data matrix that should be sorted is the one shown in the current tab
		int tab = this.tbp_matrices.getSelectedIndex();
		if (tab < 1) {
			JOptionPane.showMessageDialog(comirvaUI, "Please select a tab that contains data matrices.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			DefaultListModel dlm;		// List model of the list in the current selected tab
			Vector ml;					// Data vector fo the list in the current selected tab
			if (tab == 1) {		// data matrix tab
				dlm = ws.listMatrices;
				ml = ws.matrixList;
			} else {			// additional data matrix tab
				dlm = ws.additionalListMatrices.get(tab-2);
				ml = ws.additionalMatrixList.get(tab-2);
			}
			// sort JList
			int numItems = dlm.getSize();
			String[] description = new String[numItems];
			for (int i=0;i<numItems;i++)
				description[i] = (String)dlm.getElementAt(i);
			// sort descriptions
			Arrays.sort(description);
			// generate Vector for sorted DataMatrices
			Vector sortedList = new Vector();
			for (int i=0;i<numItems;i++){
				int idx = -1;
				for (int j=0;j<numItems;j++) {
					if ( ((DataMatrix) ml.elementAt(j)).getName().equals(description[i]) ) {
						idx = j;
					}
				}
				sortedList.addElement(ml.elementAt(idx));	//sortedList.addElement(this.ws.matrixList.elementAt(idx));
				dlm.setElementAt(description[i], i);
			}
			if (tab == 1) {
				this.ws.matrixList = sortedList;
			} else {
				this.ws.additionalMatrixList.set(tab-2, sortedList);
			}
			// debug
//			for (int i=0;i<numItems;i++)
//			System.out.println("DM-name: "+((DataMatrix)this.ws.matrixList.elementAt(i)).getName()+"\tJList-name: "+this.ws.listMatrices.elementAt(i));
		}
	}
	// Data -> Data Matrix -> Normalize...
	private void menuDataDataMatrixNormalize_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a matrix?
		DataMatrix selectedDM = this.getSelectedDataMatrix();
		if (selectedDM == null) 	
			// if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get selected DataMatrix
			//DataMatrix selectedDM = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// show dialog for normalizing the data matrix
			DataMatrixNormalizeDialog dmnDialog = new DataMatrixNormalizeDialog(comirvaUI);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (this.dmnCfg != null) {
				dmnDialog.setConfig(dmnCfg);
			}
			// position and initialize the dialog
			Dimension dlgSize = dmnDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			dmnDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			dmnDialog.setModal(true);
			dmnDialog.pack();
			dmnDialog.setVisible(true);
			// if dialog was closed by clicking on "Normalize", perform normalization
			if (dmnDialog.confirmOperation){
				// save configuration
				this.dmnCfg = new DataMatrixNormalizeConfig(	
						dmnDialog.getLowerBound(),
						dmnDialog.getUpperBound(),
						dmnDialog.isLinear(),
						dmnDialog.getScope());
				// normalize
				selectedDM.normalize(dmnDialog.getLowerBound(), dmnDialog.getUpperBound(), dmnDialog.isLinear(), dmnDialog.getScope());
				// rename data matrix
				System.out.println("data matrix name: " + selectedDM.getName());
				selectedDM.setName("normalized " + selectedDM.getName());
				// set new name in data matrix list (for UI)
				int index = tbp_matrices.getSelectedIndex();
				int selected = -1;
				DefaultListModel source = null;
				if (index == 1) {	
					// data matrix tab is selected (original)
					source = ws.listMatrices;
					selected = dataMatrixList.getSelectedIndex();
				} else {	// index > 1
					// a newly created tab is selected
					source = ws.additionalListMatrices.get(index - 2);
					selected = this.dataMatrixListVector.get(index - 2).getSelectedIndex();
				}	
				source.setElementAt(selectedDM.getName(), selected);
				// ws.listMatrices.setElementAt(selectedDM.getName(), dataMatrixList.getSelectedIndex());
				// inform user
				setStatusBar("Normalization finished.");
			}
		}
	}
	// Data -> Data Matrix -> Vectorize -> By Rows
	private void menuDataDataMatrixVectorizeByRows_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a matrix?
		DataMatrix selectedDM = this.getSelectedDataMatrix();
		if (selectedDM == null) 	
			// if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get selected DataMatrix
			// DataMatrix selectedDM = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// create model and data vector for new tab
			DefaultListModel model = new DefaultListModel();
			String title = selectedDM.getName();
			Vector data = new Vector();
			// add them to workspace
			ws.additionalListMatrices.add(model);
			ws.additionalMatrixList.add(data);
			ws.additionalMatrixNames.add(title);
			// add new tab 
			addDataMatrixTab(model, title);			
			// extract every row of the data matrix
			for (int i=0; i<selectedDM.getNumberOfRows(); i++) {
				// create a new DataMatrix-instance representing row i of original data matrix
				DataMatrix vectorizedByRows = new DataMatrix();
				// insert values
				Vector vecTemp = selectedDM.getRow(i);
				Enumeration e = vecTemp.elements();
				while (e.hasMoreElements()) {
					vectorizedByRows.addValue((Double)e.nextElement());
				}
				// set name of the matrix
				// check if user has selected a meta-data instance with the same number of items...
				String nameRow = new String();		// name that specifies the new data vector
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1) && (((Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex())).size() == selectedDM.getNumberOfRows()))
					// yes: use it for naming the new data vector
					nameRow = (String)((Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex())).elementAt(i);
				else
					// no: use index of row in original data matrix as name
					nameRow = "Row "+i;
				// set name
				vectorizedByRows.setName(/* selectedDM.getName()+" - "+ */ nameRow+" (1x"+selectedDM.getNumberOfColumns()+")");
				// insert name of the newly created vector into list
				model.addElement(/* selectedDM.getName()+" - "+ */ nameRow+" (1x"+selectedDM.getNumberOfColumns()+")");
				// ws.listMatrices.addElement(/* selectedDM.getName()+" - "+ */ nameRow+" (1x"+selectedDM.getNumberOfColumns()+")");
				// insert new Vector created from row of data matrix
				data.add(vectorizedByRows);
				//ws.matrixList.add(vectorizedByRows);
			}
		}
	}
	// Data -> Data Matrix -> Vectorize -> By Columns
	private void menuDataDataMatrixVectorizeByColumns_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a matrix?
		DataMatrix selectedDM = this.getSelectedDataMatrix();
		if (selectedDM == null) 
			// if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select a data matrix first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get selected DataMatrix
			// DataMatrix selectedDM = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// create model and data vector for new tab
			DefaultListModel model = new DefaultListModel();
			Vector data = new Vector();
			String title = dataMatrixList.getSelectedValue().toString();
			// add them to workspace
			ws.additionalListMatrices.add(model);
			ws.additionalMatrixList.add(data);
			ws.additionalMatrixNames.add(title);
			// add new tab
			addDataMatrixTab(model, title);
			// extract every column of the data matrix
			for (int i=0; i<selectedDM.getNumberOfColumns(); i++) {
				// create a new DataMatrix-instance representing column i of original data matrix
				DataMatrix vectorizedByColumns = new DataMatrix();
				// insert values
				for (int j=0; j<selectedDM.getNumberOfRows(); j++) {
					vectorizedByColumns.addValue(selectedDM.getValueAtPos(j,i));
				}
				// set name of the matrix
				// check if user has selected a meta-data instance with the same number of items...
				String nameColumn = new String();		// name that specifies the new data vector
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1) && (((Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex())).size() == selectedDM.getNumberOfColumns()))
					// yes: use it for naming the new data vector
					nameColumn = (String)((Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex())).elementAt(i);
				else
					// no: use index of row in original data matrix as name
					nameColumn = "Column "+i;
				// set name				
				vectorizedByColumns.setName(/* selectedDM.getName() + " - " + */ nameColumn + " (1x" + selectedDM.getNumberOfRows() + ")");
				// insert name of the newly created vector into list
				model.addElement(/* selectedDM.getName()+" - "+*/ nameColumn + " (1x" + selectedDM.getNumberOfRows() + ")");
				// ws.listMatrices.addElement(selectedDM.getName()+" - "+nameColumn+" (1x"+selectedDM.getNumberOfRows()+")");
				// insert new Vector created from row of data matrix
				data.add(vectorizedByColumns);
				// ws.matrixList.add(vectorizedByColumns);
			}
		}
	}
	// Data -> Data Matrix -> PCA
	private void menuDataDataMatrixPCA_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a matrix? - try to get it
		DataMatrix selectedDM = this.getSelectedDataMatrix();
		if (selectedDM == null)		// if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get selected DataMatrix
			// DataMatrix selectedDM = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// show dialog for normalizing the data matrix
			PCACalculationDialog pcaDialog = new PCACalculationDialog(comirvaUI, selectedDM.getNumberOfColumns());
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (this.pcaCfg != null) {
				pcaDialog.setConfig(pcaCfg);
			}
			// position and initialize the dialog
			Dimension dlgSize = pcaDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			pcaDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			pcaDialog.setModal(true);
			pcaDialog.pack();
			pcaDialog.setVisible(true);
			// if dialog was closed by clicking on "Calculate", perform normalization
			if (pcaDialog.confirmOperation){
				// save configuration
				this.pcaCfg = new PCAConfig(pcaDialog.getUsedEigenvectors());				// perform PCA
				PCACalculationThread pcact = new PCACalculationThread(selectedDM, this.pcaCfg, this.statusBar, this.ws);
				pcact.start();
			}
		}
	}

	// Submenu "Meta-Data"	
	// Data -> Meta-Data -> Delete Selected Item
	private void menuDataMetaDataDeleteItem_actionPerformed(ActionEvent actionEvent) {
		int index = dataMetaDataList.getSelectedIndex();
		if (index < 0) {
			JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data instance that should be romoved first.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {	// remove it
			ws.metaDataList.remove(index);
			ws.listMetaData.remove(index);
		}
	}
	// Data -> Meta-Data -> Rename...
	private void menuDataMetaDataRename_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a meta-data instance?
		if (ws.metaDataList.isEmpty() || (dataMetaDataList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a meta-data instance first.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// meta-data instance selected
			// show dialog for setting a new name
			DataMatrixRenameDialog dmrDialog = new DataMatrixRenameDialog(comirvaUI, (String)ws.listMetaData.getElementAt(dataMetaDataList.getSelectedIndex()) , false);
			// position and initialize the dialog
			Dimension dlgSize = dmrDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			dmrDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			dmrDialog.setModal(true);
			dmrDialog.pack();
			dmrDialog.setVisible(true);
			// if dialog was closed by clicking on "Rename", do rename
			if (dmrDialog.confirmOperation){
				// set new name of meta-data instance in meta-data list (for UI)
				ws.listMetaData.setElementAt(dmrDialog.getNewName(), dataMetaDataList.getSelectedIndex());
			}
		}
	}
	// Data -> Meta Data -> Sort List
	private void menuDataMetaDataSort_actionPerformed(ActionEvent actionEvent) {
		// sort JList
		DefaultListModel dlm = this.ws.listMetaData;
		int numItems = dlm.getSize();
		String[] description = new String[numItems];
		// create (sorted) TreeMap with descriptions as keys and indices in JList as values
		TreeMap tm = new TreeMap();
		for (int i=0;i<numItems;i++) {
			// insert descriptions in lower case (otherwise they would be sorted in order A-Za-z
			tm.put(((String)dlm.getElementAt(i)).toLowerCase(), new Integer(i));
			// and store original descriptions (for reconstruction)
			description[i] = (String)dlm.getElementAt(i);
		}
		// generate Vector for sorted MetaDataVectors
		Vector sortedList = new Vector();
		// add elements in a sorted fashion to sortedList and also sort DefaultListModel of JList
		if (!tm.isEmpty()) {
			int i=0;		// loop iterator
			Iterator it = tm.keySet().iterator();
			while (it.hasNext()) {
				// get description and index
				String key = (String)it.next();
				int idx = ((Integer)tm.get(key)).intValue();
				sortedList.addElement(this.ws.metaDataList.elementAt(idx));
				dlm.setElementAt(description[idx], i);
//				System.out.println(key + "\t" + tm.get(key));
				i++;			// increase loop-iterator
			}
		}
		this.ws.metaDataList = sortedList;
		// debug
//		for (int i=0; i<numItems; i++)
//		System.out.println(i+", "+((Vector)this.ws.metaDataList.elementAt(i)).size());
	}
	// Data -> Data Matrix -> Extraxt Data from File List
	private void menuDataMetaDataExtract_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a meta-data instance?
		if (ws.metaDataList.isEmpty() || (dataMetaDataList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a meta-data instance containing pathnames.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// meta-data instance selected
			Vector<String> list = (Vector<String>)(ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex()));
			Vector<String> newlist = new Vector<String>();
			Vector<String> othernewlist = new Vector<String>();

			Iterator<String> i = list.iterator();
			while (i.hasNext()) {
				String entry = i.next();
				try {
					File f = new File(entry);
					if (!f.isFile()) {
						newlist.addElement(entry);
						othernewlist.addElement(entry);
						continue;
					}
					ID3Reader id3 = new ID3Reader(f);
					// TODO: extract desired information
					// right now, read artist name
					newlist.addElement(id3.getArtist());
					othernewlist.addElement(id3.getArtist()+"-"+id3.getTitle());
				}
				catch (Exception e) {
					if (statusBar != null)
						statusBar.setText(e.getMessage());
					newlist.addElement(entry);
					othernewlist.addElement(entry);
					continue;
				}
			}
			if (statusBar != null)
				statusBar.setText("Meta-Data extraction completed.");
			this.ws.metaDataList.addElement(newlist);
			this.ws.listMetaData.addElement("artist from "+(String)(dataMetaDataList.getSelectedValue()));
			this.ws.metaDataList.addElement(othernewlist);
			this.ws.listMetaData.addElement("artist-title from "+(String)(dataMetaDataList.getSelectedValue()));
		}
	}

	// Submenu "Web Mining"
	// Data -> Web Mining -> Co-Occurrence Analysis -> Retrieve Page Counts
	private void menuDataWebMiningPageCountMatrix_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a meta-data instance?
		if (ws.metaDataList.isEmpty() || (dataMetaDataList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the meta-data instance\nthat you intend to use for the web crawl.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// meta-data instance selected
			// open a dialog where the user can specify some parameters of the web crawl
			PageCountsRetrieverDialog pcrDialog = new PageCountsRetrieverDialog(comirvaUI);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (this.pcrCfg != null) {
				pcrDialog.setConfig(pcrCfg);
			}
			// position and initialize the dialog
			Dimension dlgSize = pcrDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			pcrDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			pcrDialog.setModal(true);
			pcrDialog.pack();
			pcrDialog.setVisible(true);
			// if dialog was closed by clicking on "Retrieve", start retrieval
			if (pcrDialog.confirmOperation) {
				// get meta-data containing the search strings
				Vector searchWords = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
				// save configuration
				this.pcrCfg = new PageCountsRetrieverConfig(	pcrDialog.getSearchEngineURL(),
						pcrDialog.getNumberOfRetries(),
						pcrDialog.getIntervalBetweenRetries(),
						pcrDialog.getAdditionalKeywords(),
						pcrDialog.getAdditionalKeywordsAfterSearchString(),
						pcrDialog.getQueryForAllPairs());
				// create and initialize a PageCountsRetriever-object according to dialog settings and selected meta-data vector
				PageCountsRetriever pcRet = new PageCountsRetriever(pcrCfg,
						searchWords,
						this.ws.matrixList, this.ws.listMatrices,
						this.statusBar);
				// start thread
				pcRet.start();
			}
		}
	}
	// Data -> Web Mining -> Co-Occurrence Analysis -> Requery Invalid Page Counts Matrix Entries
	private void menuDataWebMiningRequeryPageCountMatrix_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix pageCountsMatrix = this.getSelectedDataMatrix();
		if (pageCountsMatrix == null)  // if (ws.matrixList.isEmpty() || dataMatrixList.getSelectedIndex() == -1)
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the data matrix\ncontaining the incomplete page counts.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else if (ws.metaDataList.isEmpty() || (dataMetaDataList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the meta-data\nthat was originally used for the web crawl.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// everything seems to be ok
			// get page counts matrix
			// DataMatrix pageCountsMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// get meta-data containing the search strings
			Vector searchWords = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
			// test, if length of meta-data and number of rows in page-counts-matrix match
			if (pageCountsMatrix.getNumberOfRows() != searchWords.size())
				JOptionPane.showMessageDialog(comirvaUI, "The number of rows in the selected page count matrix does not equal \nthe number of rows in the selected meta-data vector.", "Error", JOptionPane.ERROR_MESSAGE);
			else {
				// open a dialog where the user can specify some parameters of the web crawl
				PageCountsRetrieverDialog pcrDialog = new PageCountsRetrieverDialog(comirvaUI);
				// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
				if (this.pcrCfg != null) {
					pcrDialog.setConfig(pcrCfg);
				}
				// select and lock the radio buttion "Query for All Pairs" if page-count-matrix is quadratic
				if (pageCountsMatrix.getNumberOfRows() == pageCountsMatrix.getNumberOfColumns())
					pcrDialog.lockQueryForAllPairs();
				else
					pcrDialog.lockQueryForSingleItems();
				// position and initialize the dialog
				Dimension dlgSize = pcrDialog.getPreferredSize();
				Dimension frmSize = comirvaUI.getSize();
				Point loc = comirvaUI.getLocation();
				pcrDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
				pcrDialog.setModal(true);
				pcrDialog.pack();
				pcrDialog.setVisible(true);
				// if dialog was closed by clicking on "Retrieve", start retrieval
				if (pcrDialog.confirmOperation) {
					// save configuration
					this.pcrCfg = new PageCountsRetrieverConfig(	pcrDialog.getSearchEngineURL(),
							pcrDialog.getNumberOfRetries(),
							pcrDialog.getIntervalBetweenRetries(),
							pcrDialog.getAdditionalKeywords(),
							pcrDialog.getAdditionalKeywordsAfterSearchString(),
							pcrDialog.getQueryForAllPairs());
					// create and initialize a PageCountsRetriever-object according to dialog settings and selected meta-data vector
					InvalidPageCountsRetriever pcRet = new InvalidPageCountsRetriever(		pcrCfg,
							searchWords,
							pageCountsMatrix,
							this.statusBar);
					// start thread
					pcRet.start();
				}
			}
		}
	}
	// Data -> Web Mining -> Co-Occurrence Analysis -> Estimate Conditional Probabilities
	private void menuDataWebMiningEstimateConditionalProbabilities_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// try to get selected data matrix
		DataMatrix pageCountsMatrix = this.getSelectedDataMatrix();
		if (pageCountsMatrix == null) //if (ws.matrixList.isEmpty() || (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the data matrix containing the page counts.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {	// everything seems to be ok
			// get page counts matrix
			// DataMatrix pageCountsMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// test, if page-counts-matrix is quadratic
			if (pageCountsMatrix.getNumberOfRows() != pageCountsMatrix.getNumberOfColumns())
				JOptionPane.showMessageDialog(comirvaUI, "The selected data matrix is not quadratic.", "Error", JOptionPane.ERROR_MESSAGE);
			else {
				// estimate conditional probabilities
				DataMatrix condProbMatrix = new DataMatrix("cond-probs for " + pageCountsMatrix.getName());
				for (int i=0; i<pageCountsMatrix.getNumberOfRows(); i++) {
					for (int j=0; j<pageCountsMatrix.getNumberOfRows(); j++) {
						condProbMatrix.addValue(new Double(pageCountsMatrix.getValueAtPos(i,j).doubleValue()/pageCountsMatrix.getValueAtPos(i,i).doubleValue()));
					}
					condProbMatrix.startNewRow();
				}
				condProbMatrix.removeLastAddedElement();
				// add matrix containing conditional probabilities to UI
				if (ws.listMatrices != null)
					ws.listMatrices.addElement(condProbMatrix.getName());		// add name of matrix to matrix list in UI, if possible
				if (ws.matrixList != null)
					ws.matrixList.addElement(condProbMatrix);					// add DataMatrix-instance (condProbMatrix) to ws.matrixList-Vector, if possible
			}
		}
	}
	// Data -> Web Mining -> Term Profile -> Retrieve Meta-Data-Related Pages
	private void menuDataWebMiningTermProfileRetrieveRelatedPages_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// user selected a meta-data instance?
		if (ws.metaDataList.isEmpty() || (dataMetaDataList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the meta-data\nthat you intend to use for the web crawl.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// meta-data instance selected
			// open a dialog where the user can specify some parameters of the web crawl
			WebCrawlingDialog wcDialog = new WebCrawlingDialog(comirvaUI);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (this.wcCfg != null) {
				wcDialog.setConfig(wcCfg);
			}
			// position and initialize the dialog
			Dimension dlgSize = wcDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			wcDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			wcDialog.setModal(true);
			wcDialog.pack();
			wcDialog.setVisible(true);
			// if dialog was closed by clicking on "Start Crawl", start retrieval
			if (wcDialog.confirmOperation) {
				// get meta-data containing the search strings
				Vector searchWords = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
				// save configuration
				this.wcCfg = new WebCrawlingConfig(			wcDialog.getSearchEngineURL(),
						wcDialog.getNumberOfRetries(),
						wcDialog.getIntervalBetweenRetries(),
						0,
						wcDialog.getAdditionalKeywords(),
						wcDialog.getAdditionalKeywordsAfterSearchString(),
						wcDialog.getNumberOfRequestedPages(),
						wcDialog.getPathStoreRetrievedPages(),
						wcDialog.getPathExternalCrawler(),
						wcDialog.isStoreURLList()
				);
				// create and initialize a WebCrawling-object according to dialog settings and selected meta-data vector
				WebCrawling wcRet = new WebCrawling(				wcCfg,
						searchWords,
						this.statusBar);
				// start thread
				wcRet.start();
			}
		}
	}
	// Data -> Web Mining -> Term Profile -> Create Entity Term Profile(s) from Extracted Documents
	private void menuDataWebMiningTermProfileCreateEntityTermProfile_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no meta-data list (terms) loaded
		if (ws.metaDataList.isEmpty())
			JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the terms\n" +
					"for which the documents should be scanned.\n\n" +
					"In the appearing file chooser, select the root directory\n" +
					"of the retrieved documents. Each entity must have its own\n" +
					"sub directory (that contains its documents)\n" +
					"under the root directory.", "Error", JOptionPane.ERROR_MESSAGE);
		// meta-data (for terms) loaded
		else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) 		// at least 1 meta-data vector loaded but none selected
			JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that contains the terms\n" +
					"for which the documents should be scanned.\n\n" +
					"In the appearing file chooser, select the root directory\n" +
					"of the retrieved documents. Each entity must have its own\n" +
					"sub directory (that contains its documents)\n" +
					"under the root directory.", "Error", JOptionPane.ERROR_MESSAGE);
		// meta-data (for terms) loaded and selected
		else {
			// create file open dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			// let the user only select directories
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(comirvaUI);
			// valid directory selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// remember directory where selected file is located (for future file chooser access)
				// remember directory where selected file is located (for future file chooser access)
				dmPreferences.setLastDir(fileChooser.getSelectedFile().getPath());
				// save new last directory
				saveDataManagementPrefs(dmPreferences);
				// start creation of ETPs
				ETPCreatorThread etpct = new ETPCreatorThread(fileChooser.getSelectedFile(), (Vector<String>)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex()), this.statusBar);
				etpct.start();
			}
		}
	}
	// Data -> Web Mining -> Term Profile -> Extract Terms from Retrieved Documents
	private void menuDataWebMiningTermProfileExtractTerms_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		// let the user only select directories
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid directory selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// get directory name
			File dir = fileChooser.getSelectedFile();	// directory where documents reside
			FileFilter filter = new HTMLFileFilter();	// a filter for HTML-documents
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(dir.getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// actually start loading
			// start extraction of terms
			DocumentTermExtractorThread dtet = new DocumentTermExtractorThread(dir, filter, this.statusBar, this.ws);
			dtet.start();
//			Vector<String> terms = TermProfileUtils.extractTermsFromHTMLDocuments(fileChooser.getSelectedFile(), new HTMLFileFilter());
//			this.ws.addMetaData(terms, "terms extracted from " + fileChooser.getSelectedFile().getName() + " (" + terms.size() + ")");
		}
	}
	// Data -> Web Mining -> Term Profile -> Load ETP from ETP-XML-File
	private void menuDataWebMiningTermProfileLoadETP_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog with filter for XML-files
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		// let the user select directories and files
		// if he/she selects 1 or more xml-file(s), only this/these file is processed.
		// if he/she selects a directory, the entire directory is searched for xml-files
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		XMLFileFilter filter = new XMLFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(fileChooser.getSelectedFile().getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// open a dialog where the user can specify which data should be loaded
			ETPLoaderDialog etplDialog = new ETPLoaderDialog(comirvaUI);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (this.etplCfg != null) {
				etplDialog.setConfig(etplCfg);
			}
			// position and initialize the dialog
			Dimension dlgSize = etplDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			etplDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			etplDialog.setModal(true);
			etplDialog.pack();
			etplDialog.setVisible(true);
			// if dialog was closed by clicking on "OK", start loading the ETP from XML-file(s)
			if (etplDialog.confirmOperation) {
				// save configuration
				this.etplCfg = new ETPLoaderConfig(	
						etplDialog.isLoadTerms(),
						etplDialog.isLoadDocPaths(),
						etplDialog.isLoadTO(),
						etplDialog.isLoadTF(),
						etplDialog.isLoadDF(),
						etplDialog.isLoadTFxIDF());
				// get files/directory of multiple selection
				File[] filesXML = fileChooser.getSelectedFiles();
				for (int i=0; i<filesXML.length; i++) {
					// get current XML-file
					File fileXML = filesXML[i];
					// determine if source is directory or file
					if (fileXML.isFile()) {				// xml-file
						// extract term occurrences of selected file and add them to UI
						ETPXMLExtractorThread toXMLe = new ETPXMLExtractorThread(fileXML, ws.matrixList, ws.listMatrices, ws.metaDataList, ws.listMetaData, this.statusBar, this.etplCfg);
						toXMLe.start();
					} else if (fileXML.isDirectory()) {		// directory of xml-files
						// get list of XML-files in selected directory
						File[] xmlFiles = fileXML.listFiles(new XMLFileFilter());
						// extract term occurrences of all XML-files in selected directory and add them to UI
						for (int j=0; j<xmlFiles.length; j++) {
							// extract term occurrences of current file and add them to UI
							if (xmlFiles[j].isFile()) {
								ETPXMLExtractorThread toXMLe = new ETPXMLExtractorThread(xmlFiles[j], ws.matrixList, ws.listMatrices, ws.metaDataList, ws.listMetaData, this.statusBar, this.etplCfg);
								toXMLe.start();
							}
						}
					}
				}
			}

		}
	}
	// Data -> Web Mining -> Term Profile -> Update Paths in ETP-XML-File(s)
	private void menuDataWebMiningTermProfileUpdatePathsETP_actionPerformed(ActionEvent actionEvent) {
		// create file open dialog with filter for XML-files
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		// let the user select directories and files
		// if he/she selects 1 or more xml-file(s), only this/these file is processed.
		// if he/she selects a directory, the entire directory is searched for xml-files
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		XMLFileFilter filter = new XMLFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// remember directory where selected file is located (for future file chooser access)
			dmPreferences.setLastDir(fileChooser.getSelectedFile().getPath());
			// save new last directory
			saveDataManagementPrefs(dmPreferences);
			// get files/directory of multiple selection
			File[] filesXML= fileChooser.getSelectedFiles();
			// create and run thread for updating the paths
			ETPXMLPathUpdaterThread etpXMLput = new ETPXMLPathUpdaterThread(filesXML, this.statusBar);
			etpXMLput.start();
		}
	}


	// Menu "Visualization"
	// Submenu "SOM"
	// Visualization -> SOM -> Create SOM
	// To perform this operation, a matrix must be selected from the matrix list
	// Additionally, if a meta-data Vector is selected from the meta-data list
	// (and it has the same number of items than the data set), it is assumed
	// to contain the labels for the SOM
	private void menuVisuCreateSOM_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix selectedDM = getSelectedDataMatrix();
		if (selectedDM == null) 
			//if (ws.matrixList.isEmpty())
			//	JOptionPane.showMessageDialog(comirvaUI, "Please load a data matrix for training the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
			//else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix for training the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// create a SOM based on the selected data matrix (initially for estimating the size (number of map units)
			//som = new SOM((DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex()));
			SOM som = new SOM(selectedDM);
			// open a dialog where the user can specify some parameters of the SOM
			// in addition, the default values as calculated automatically by constructing a new
			// SOM-object are passed to be used as default values in the dialog
			SOMCreationDialog somDialog = new SOMCreationDialog(comirvaUI, som.getNumberOfRows(), som.getNumberOfColumns());
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (paneVisu.getSOMConfig() != null) {
				somDialog.setConfig(paneVisu.getSOMConfig());
			}
			// position and initialize the dialog
			Dimension dlgSize = somDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			somDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			somDialog.setModal(true);
			somDialog.pack();
			somDialog.setVisible(true);
			// if dialog was closed by clicking on "Create SOM", calculate the SOM
			if (somDialog.confirmOperation) {
				som.statusBar = this.statusBar;		// pass statusBar-instance to som-instance in order to update the status bar
				this.progressBar.setIndeterminate(true);
				// initialize the SOM
				setStatusBar("Initializing the SOM");
				som.setSOMSize(somDialog.getMapUnitsInRow(), somDialog.getMapUnitsInColumn());
				// save configuration
				SOMConfig somc = new SOMConfig(somDialog.getMapUnitsInRow(), somDialog.getMapUnitsInColumn(), somDialog.getInitMethod(), somDialog.getTrainingMethod(), somDialog.getTrainingLength(), somDialog.isCircular());
				SOMConfig somd = new SOMDefaultConfig(somDialog.getMapUnitsInRow(), somDialog.getMapUnitsInColumn());
				paneVisu.resetVisuThreads();
				paneVisu.setSOMConfig(somc);
				// train the SOM
				SOMTrainingThread somThread = new SOMTrainingThread(som, somc, this.statusBar, this.progressBar);
				somThread.start();
				// setting SOM-labels, if meta-data is selected
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
					try {
						// get label vector
						Vector labels = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
						// set SOM-labels
						som.setLabels(labels);
//						System.out.println("SOM-labels set");
					} catch (SizeMismatchException sme) {
//						System.out.println("SOM-labels not set");
					}
				}
				int count = ws.countVisuTypes(som.getClass().getName()) + 1;
				// construct name of visualization according to users' preferences
				String caption = new String("SOM ");	// the visu type and
				caption += count + ": ";				// its number in the list is fixed
				// the rest is optional; if desired it is displayed as 'key=value' pairs
				boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
				boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
				if (allval || nonstd && !somDialog.isMapUnitsInRowDefault(somc.getMapUnitsInRow())) {
					caption += "map unit per row=" + somc.getMapUnitsInRow() + "; ";
				}
				if (allval || nonstd && !somDialog.isMapUnitsInColumnDefault(somc.getMapUnitsInColumn())) {
					caption += "map unit per col=" + somc.getMapUnitsInColumn() + "; ";
				}
				if (allval || nonstd && somc.getInitMethod() != somd.getInitMethod()) {
					caption += "init method=" + somDialog.getInitMethodsString().get(somc.getInitMethod()) + "; ";
				}
				if (allval || nonstd && somc.getTrainingMethod() != somd.getTrainingMethod()) {
					caption += "train method=" + somDialog.getInitMethodsString().get(somc.getTrainingMethod()) + "; ";
				}
				if (allval || nonstd && somc.getTrainingLength() != somd.getTrainingLength()) {
					caption += "train length = " + somc.getTrainingLength() + "; ";
				}
				if (allval || nonstd && somc.isCircular() != somd.isCircular()) {
					caption += "circular=" + somc.isCircular() + "; ";
				}
				if (dmPreferences.useDataMatrixName()) {
					caption += selectedDM.getName();
				}
				caption = caption.trim();
				// remove ':' if no text was added and so it is still at the end
				if (caption.charAt(caption.length()-1) == ':') {
					caption.replace(":", "");
				}
				ws.addVisu(som, caption);
				this.visuList.setSelectedValue(caption, true);		// select new visu
			}
		}
	}
	// Visualization -> SOM -> Assign Labels
	private void menuVisuAssignLabels_actionPerformed(ActionEvent actionEvent) {
		// perform some feasibility-tests
		int somcount = ws.countVisuTypes(SOM.class.getName());		// count soms
		if (somcount == 0) {							// does a SOM exist?
			JOptionPane.showMessageDialog(comirvaUI, "No SOM has been created or loaded.\nThe requested operation does not make sense.", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (ws.metaDataList.isEmpty()) {		// is meta-data loaded?
			JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the labels.", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) {		// at least 1 meta-data vector loaded but none selected
			JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that represents the labels.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {									// meta-data loaded and selected
			// assigning SOM-labels
			try {
				SOM som = null;
				Vector<VisuListItem> somList = ws.getVisuListItems(SOM.class.getName());
				if (somcount == 1) {
					som = (SOM) somList.firstElement();
				} else {
					som = (SOM) askUserForVisuListItem(somList, SOM.class.getName());
				}
				if (som != null) {
				// get label vector
				Vector labelsOrig = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
				// the following code extracts the last part of the label (after the last "-" delimiter)
				// extract last part of label (after a tokenize string)
				Vector labels = new Vector();
				Iterator iter = labelsOrig.iterator();
				while (iter.hasNext()) {
					String currentLabel = (String)iter.next();
					// create a StringTokenizer
					StringTokenizer st = new StringTokenizer(currentLabel, "-");
					// get last token
					if (st.countTokens() == 1)	// only 1 token -> use original label
						labels.add(currentLabel);
					else {						// > 1 token -> get last token
						String lastElement = new String();
						while (st.hasMoreElements()) {
							lastElement = st.nextToken();
						}
						// add last token to label-vector
						labels.add(lastElement);
					}
				}
				// set labels
				som.setLabels(labelsOrig);
				setStatusBar("SOM-labels set");
				// force visualization to be repainted
				paneVisu.resetVisuThreads();
				paneVisu.setLoadBufferedImage(false);
				paneVisu.repaint();
				}
			} catch (SizeMismatchException sme) {
				JOptionPane.showMessageDialog(comirvaUI, "The labels cannot be assigned to the SOM because\nthe number of the data items and the labels are not the same.", "Error", JOptionPane.ERROR_MESSAGE);
				setStatusBar("SOM-labels not set");
			}
		}
	}
	// Visualization -> SOM -> Clear Labels
	private void menuVisuClearLabels_actionPerformed(ActionEvent actionEvent) {
		int somcount = ws.countVisuTypes(SOM.class.getName());
		if (somcount < 1)
			JOptionPane.showMessageDialog(comirvaUI, "No SOM has been created or loaded.\nThe requested operation does not make sense.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			Vector<VisuListItem> soms = ws.getVisuListItems(SOM.class.getName());
			SOM som = null;
			if (somcount == 1) {	// only one
				som = (SOM) soms.firstElement();
			} else {				// more soms
				som = (SOM) askUserForVisuListItem(soms, SOM.class.getName());
			}
			if (som != null) {
				som.clearLabels();
				setStatusBar("SOM-Labels discarded");
				// force visualization to be repainted
				paneVisu.resetVisuThreads();
				paneVisu.setLoadBufferedImage(false);
				paneVisu.repaint();
			}
		}
	}
	// Visualization -> SOM -> Calc MDM Labels
	private void menuVisuCalcMDMLabels_actionPerformed(ActionEvent actionEvent) {
		int somcount = ws.countVisuTypes(SOM.class.getName());
		if (somcount < 1)
			JOptionPane.showMessageDialog(comirvaUI, "No SOM has been created or loaded.\nThe requested operation does not make sense.", "Error", JOptionPane.ERROR_MESSAGE);
		/* else if (som.getLabels() == null)
			JOptionPane.showMessageDialog(comirvaUI, "No Labels have been assigned to the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
		 */
		else {
			Vector<VisuListItem> soms = ws.getVisuListItems(SOM.class.getName());
			SOM som = null;
			if (somcount == 1) {
				som = (SOM) soms.firstElement();
				if (som.getLabels() == null) {
					JOptionPane.showMessageDialog(comirvaUI, "No Labels have been assigned to the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				String[] options = ws.getVisuListNamesArray(SOM.class.getName(), somcount);
				do {
					Object input = JOptionPane.showInputDialog(comirvaUI, "Pleas select the SOM " +
							"which the MDM should be based on:", "Question", JOptionPane.QUESTION_MESSAGE, 
							null, options, options[0]);
					if (input != null) {	// OK
						som = (SOM) soms.get(ws.listVisu.indexOf(input));
					} else {				// Cancel
						setStatusBar("MDM calculation cancelled.");
						return;
					}
					if (som.getLabels() == null) {
						JOptionPane.showMessageDialog(comirvaUI, "No Labels have been assigned to the selected SOM.\nPlease select another one!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} while (som.getLabels() == null);
			}
			if (som != null) {		// safety check
				synchronized (som) {
					MDM mdm = new MDM(som, true);
					mdm.addThreadListener(new CalcMDMThreadListener());
					setStatusBar("Calculation of MDM started.");
					// force visualization to be repainted
					paneVisu.resetVisuThreads();
					paneVisu.setLoadBufferedImage(false);
					paneVisu.repaint();
					mdm.start();
				}
			}
		}
	}
	private class CalcMDMThreadListener implements ThreadListener {
		public void threadEnded() {
			setStatusBar("Calculation of MDM finished.");
			// force visualization to be repainted
			paneVisu.resetVisuThreads();
			paneVisu.setLoadBufferedImage(false);
			paneVisu.repaint();
		}

	}
	// Visualization -> SOM -> Show SOM-Grid
	private void menuVisuShowSOMGrid_actionPerformed(ActionEvent actionEvent) {
		// create and show grid for SOM
		setStatusBar("Preparing SOM-Grid-Visualization");
		// Because of the visu list it is now possible to have more than one SOM.
		// Now this action asks the user which SOM to choose if there are more than one
		int count = ws.countVisuTypes(SOM.class.getName());		// count soms
		if (count == 0) {			// no som created, display error message
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load a SOM first.\nOtherwise it is a bit difficult to display it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else
			if (count > 0) {
				Vector <VisuListItem> items = ws.getVisuListItems(SOM.class.getName());
				SOM request = null;
				if (count == 1) {	// if only one choice take it
					request = (SOM) items.firstElement();
				} else {			// if more than one choices ask user
					request = (SOM) askUserForVisuListItem(items, SOM.class.getName());
				}
				if (request != null)
					displayvisualization(request);	
			}
	}
	// Visualization -> SOM -> Show MDM-Grid
	private void menuVisuShowMDMGrid_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// if no SOM-instance has been created, show error message
		Vector<VisuListItem> items = ws.getVisuListItems(SOM.class.getName());
		for (int i=0; i<items.size(); i++) {
			SOM tmpSOM = (SOM) items.get(i);
			// remove SOMs that does not contain a MDM or no labels exist
			if (tmpSOM.getMDM() == null || tmpSOM.getLabels() == null) {
				items.remove(tmpSOM);
			}
		}
		if (items.size() < 1) // no items found or left
		// if (som == null || som.getMDM() == null || som.getLabels() == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create a SOM and an MDM first and assign labels.", "Error", JOptionPane.ERROR_MESSAGE);
		else {	// at least one item has survived
			SOM som = null;
			if (items.size() == 1) {
				som = (SOM) items.firstElement();
			} else {
				som = (SOM) askUserForVisuListItem(items, SOM.class.getName());
			}
			if (som != null) {
				// create and show grid for MDM
				setStatusBar("Preparing MDM-Grid-Visualization");
				paneVisu.setSOM(som);
				paneVisu.resetVisuThreads();
				paneVisu.setLoadBufferedImage(false);
				paneVisu.setVisuType(VisuPane.TYPE_MDMGRID);
			}
		}
	}
	// Visualization -> SOM -> Load SOM
	private void menuVisuLoadSOM_actionPerformed(ActionEvent actionEvent) {
		/* Now: more than one som can be created or loaded. It does not matter whether visualizations already exist when
		 * 		the user want to load another. Therefore the SOM existance check is not needed anymore.
		 */		
		// create file open dialog
		JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
		SOMFileFilter filter = new SOMFileFilter();
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(comirvaUI);
		// valid file selected?
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				// load file
				File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
				// remember directory where selected file is located (for future file chooser access)
				dmPreferences.setLastDir(fileData.getPath());
				// save new last directory
				saveDataManagementPrefs(dmPreferences);
				// load data from file
				setStatusBar("Loading SOM from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
				FileInputStream in = new FileInputStream(fileData);
				ObjectInputStream s = new ObjectInputStream(in);
				SOM som = (SOM)s.readObject();
				s.close();
				in.close();
				// add to list
				int count = ws.countVisuTypes(som.getClass().getName()) + 1;
				String caption = "SOM " + count + ", file: " + fileData.getName();
				ws.addVisu(som, caption);
				setStatusBar("SOM successfully loaded from file: " + fileChooser.getSelectedFile().getAbsolutePath());
				this.visuList.setSelectedValue(caption, true);		// select new visu
				// som.printVoronoiSet();
			} catch (ClassNotFoundException fnfe) {
				JOptionPane.showMessageDialog(comirvaUI, "Internal error (ClassNotFoundException) occurred while loading SOM from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while loading SOM from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	// Visualization -> SOM -> Save SOM
	private void menuVisuSaveSOM_actionPerformed(ActionEvent actionEvent) {
		// if no SOM-instance has been created, show error message
		int count = ws.countVisuTypes(SOM.class.getName());
		if (count == 0) {
		// if (som == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create a SOM first.\nOtherwise it is a bit difficult to save it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			Vector<VisuListItem> items = ws.getVisuListItems(SOM.class.getName());
			SOM som = null;
			if (count == 1) {
				// only one element - take it
				som = (SOM) items.get(0);
			} else {
				// more than one - ask user which one to take
				som = (SOM) askUserForVisuListItem(items, SOM.class.getName());
			}
			if (som != null) {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			SOMFileFilter filter = new SOMFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving SOM to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						FileOutputStream out = new FileOutputStream(fileData);
						ObjectOutputStream s = new ObjectOutputStream(out);
						s.writeObject(som);
						s.flush();
						setStatusBar("SOM successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
						s.close();
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving SOM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			}
		}
	}

	private void menuVisuExportHTMLSOM_actionPerformed(ActionEvent actionEvent) {
		// if no SOM-instance has been created, show error message
		int count = ws.countVisuTypes(SOM.class.getName());
		if (count == 0)
		// if (som == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create a SOM first.\nOtherwise it is a bit difficult to save it.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			Vector<VisuListItem> items = ws.getVisuListItems(SOM.class.getName());
			SOM som = null;
			if (count == 1) {
				som = (SOM) items.firstElement();
			} else {
				som = (SOM) askUserForVisuListItem(items, SOM.class.getName());
			}
			if (som != null) {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			HTMLFileFilter filter = new HTMLFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Exporting SOM to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						SOM2HTMLExporter.exportSOM(som, fileData);
						setStatusBar("SOM successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
					}
				} catch (Exception ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "Error occurred while saving SOM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			}
		}
	}

	private void menuVisuExportHTMLMDM_actionPerformed(ActionEvent actionEvent) {
		// if no SOM-instance has been created, show error message
		Vector<VisuListItem> items = ws.getVisuListItems(SOM.class.getName());
		for (int i=0; i<items.size(); i++) {
			SOM tmpSOM = (SOM) items.get(i);
			// remove SOMs that does not contain a MDM or no labels exist
			if (tmpSOM.getMDM() == null || tmpSOM.getLabels() == null) {
				items.remove(tmpSOM);
			}
		}
		if (items.size() < 1) {
		// if (som == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create a SOM and assign MDM labels to it.\nOtherwise it is a bit difficult to export it.", "Error", JOptionPane.ERROR_MESSAGE);
		//else if (som.getMDM().getLabels() == null)
		//	JOptionPane.showMessageDialog(comirvaUI, "Please create an MDM first.\nOtherwise it is a bit difficult to export it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			// create file save dialog
			SOM som = null;
			if (items.size() == 1) {
				som = (SOM) items.firstElement();
			} else {
				som = (SOM) askUserForVisuListItem(items, SOM.class.getName());
			}
			if (som != null) {
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			HTMLFileFilter filter = new HTMLFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Exporting MDM to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						SOM2HTMLExporter.exportMDM(som, fileData);
						setStatusBar("MDM successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
					}
				} catch (Exception ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "Error occurred while saving MDM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			}
		}
	}
	// Menu "Visualization"
	// Submenu "GHSOM"
	// Visualization -> GHSOM -> Create GHSOM
	// To perform this operation, a matrix must be selected from the matrix list
	// Additionally, if a meta-data Vector is selected from the meta-data list
	// (and it has the same number of items than the data set), it is assumed
	// to contain the labels for the GHSOM
	private void menuVisuCreateGHSOM_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix selectedDM = getSelectedDataMatrix();
		if (selectedDM == null) 
//			if (ws.matrixList.isEmpty())
//			JOptionPane.showMessageDialog(comirvaUI, "Please load a data matrix for training the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
//			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a data matrix for training the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// create a GHSOM based on the selected data matrix (initially for estimating the size (number of map units)
			GHSOM ghsom = new GHSOM(selectedDM);
			// open a dialog where the user can specify some parameters of the SOM
			// in addition, the default values as calculated automatically by constructing a new
			// SOM-object are passed to be used as default values in the dialog
			GHSOMCreationDialog ghsomDialog = new GHSOMCreationDialog(comirvaUI);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (paneVisu.getGHSOMConfig() != null) {
				ghsomDialog.setConfig(paneVisu.getGHSOMConfig());
			}
			// position and initialize the dialog
			Dimension dlgSize = ghsomDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			ghsomDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			ghsomDialog.setModal(true);
			ghsomDialog.pack();
			ghsomDialog.setVisible(true);
			// if dialog was closed by clicking on "Create GHSOM", calculate the GHSOM
			if (ghsomDialog.confirmOperation) {
				ghsom.statusBar = this.statusBar;		// pass statusBar-instance to som-instance in order to update the status bar
				this.progressBar.setIndeterminate(true);
				System.out.println("Start!");
				// initialize theGH SOM
				setStatusBar("Initializing the GHSOM");
				// save configuration
				GHSOMConfig ghsomc = new GHSOMConfig(ghsomDialog.getMapUnitsInRow(), ghsomDialog.getMapUnitsInColumn(), 
						ghsomDialog.getInitMethod(), ghsomDialog.getTrainingLength(), ghsomDialog.getGrowThreshold(), 
						ghsomDialog.getExpandThreshold(), ghsomDialog.getMaxSize(), ghsomDialog.getMaxDepth(), 
						ghsomDialog.isCircular(), ghsomDialog.isFirstCircular(), ghsomDialog.isOrientated());
				paneVisu.setGHSOMConfig(ghsomc);
				GHSOMConfig ghsomd = new GHSOMDefaultConfig();	// default values
				// create ghsom name according to options
				int count = ws.countVisuTypes(ghsom.getClass().getName()) + 1;
				String caption = new String("GHSOM ");	// the visu type and
				caption += count + ": ";				// its number in the list is fixed
				// the rest is optional; if desired it is displayed as 'key=value' pairs
				boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
				boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
				if (allval || nonstd && ghsomc.getMapUnitsInRow() != ghsomd.getMapUnitsInRow()) {
					caption += "map units in row=" + ghsomc.getMapUnitsInRow() + "; ";
				}
				if (allval || nonstd && ghsomc.getMapUnitsInColumn() != ghsomd.getMapUnitsInColumn()) {
					caption += "map units in col=" + ghsomc.getMapUnitsInColumn() + "; ";
				}
				if (allval || nonstd && ghsomc.getTrainingLength() != ghsomd.getTrainingLength()) {
					caption += "training length=" + ghsomc.getTrainingLength() + "; ";
				}
				if (allval || nonstd && ghsomc.getInitMethod() != ghsomd.getInitMethod()) {
					caption += "init method=" + ghsomc.getInitMethod() + "; ";
				}
				if (allval || nonstd && ghsomc.getGrowThreshold() != ghsomd.getGrowThreshold()) {
					caption += "grow threshold=" + ghsomc.getGrowThreshold() + "; ";
				}
				if (allval || nonstd && ghsomc.getExpandThreshold() != ghsomd.getExpandThreshold()) {
					caption += "expand threshold=" + ghsomc.getExpandThreshold() + "; ";
				}
				if (allval || nonstd && ghsomc.getMaxSize() != ghsomd.getMaxSize()) {
					caption += "max size=" + ghsomc.getMaxSize() + "; ";
				}
				if (allval || nonstd && ghsomc.getMaxDepth() != ghsomd.getMaxDepth()) {
					caption += "max depth=" + ghsomc.getMaxDepth() + "; ";
				}
				if (allval || nonstd && ghsomc.isCircular() != ghsomd.isCircular()) {
					caption += "circular=" + ghsomc.isCircular() + "; ";
				}
				if (allval || nonstd && ghsomc.isOnlyFirstCircular() != ghsomd.isOnlyFirstCircular()) {
					caption += "only first circular" + ghsomc.isOnlyFirstCircular() + "; ";
				}
				if (allval || nonstd && ghsomc.isOrientated() != ghsomd.isOrientated()) {
					caption += "orientated=" + ghsomc.isOrientated();
				}
				if (dmPreferences.useDataMatrixName()) {
					caption += selectedDM.getName();
				}
				caption = caption.trim();
				// remove ':' if no text was added and therefore it is still at the end
				if (caption.charAt(caption.length()-1) == ':') {
					caption.replace(":", "");
				}
				// train the GHSOM
				GHSOMTrainingThread somThread = new GHSOMTrainingThread(ghsom, ghsomc, this.statusBar, this.progressBar);
				somThread.start();
				// setting GHSOM-labels, if meta-data is selected
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
					try {
						// get label vector
						Vector labels = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
						// set GHSOM-labels
						ghsom.setLabels(labels);
//						System.out.println("SOM-labels set");
					} catch (SizeMismatchException sme) {
//						System.out.println("SOM-labels not set");
					}
				}
				// add ghsom visu to list
				ws.addVisu(ghsom, caption);
				this.visuList.setSelectedValue(caption, true);		// select new visu
				//this.progressBar.setIndeterminate(false);
				// don't reset progress bar here, this is done when
				// training thread is finished!
			}
		}
	}
	// Visualization -> GHSOM -> Assign Labels
	private void menuVisuAssignGHSOMLabels_actionPerformed(ActionEvent actionEvent) {
		// perform some feasibility-tests
		GHSOM ghsom = (GHSOM) getSelectedvisualizationItem(new GHSOM[0], GHSOM.class.getName());
		if (ghsom == null) {							// does a GHSOM exist?
			JOptionPane.showMessageDialog(comirvaUI, "No GHSOM has been created or loaded.\nThe requested operation does not make sense.", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (ws.metaDataList.isEmpty()) {		// is meta-data loaded?
			JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the labels.", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) {		// at least 1 meta-data vector loaded but none selected
			JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that represents the labels.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {									// meta-data loaded and selected
			// assigning GHSOM-labels
			try {
				// get label vector
				Vector labelsOrig = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
				// the following code extracts the last part of the label (after the last "-" delimiter)
				// extract last part of label (after a tokenize string)
				Vector labels = new Vector();
				Iterator iter = labelsOrig.iterator();
				while (iter.hasNext()) {
					String currentLabel = (String)iter.next();
					// create a StringTokenizer
					StringTokenizer st = new StringTokenizer(currentLabel, "-");
					// get last token
					if (st.countTokens() == 1)	// only 1 token -> use original label
						labels.add(currentLabel);
					else {						// > 1 token -> get last token
						String lastElement = new String();
						while (st.hasMoreElements()) {
							lastElement = st.nextToken();
						}
						// add last token to label-vector
						labels.add(lastElement);
					}
				}
				// set labels
				ghsom.setLabels(labelsOrig);
				setStatusBar("GHSOM-labels set");
				// force visualization to be repainted
				paneVisu.setLoadBufferedImage(false);
				paneVisu.repaint();
			} catch (SizeMismatchException sme) {
				JOptionPane.showMessageDialog(comirvaUI, "The labels cannot be assigned to the GHSOM because\nthe number of the data items and the labels are not the same.", "Error", JOptionPane.ERROR_MESSAGE);
				setStatusBar("GHSOM-labels not set");
			}
		}
	}
	// Visualization -> GHSOM -> Clear Labels
	private void menuVisuClearGHSOMLabels_actionPerformed(ActionEvent actionEvent) {
		GHSOM ghsom = (GHSOM) getSelectedvisualizationItem(new GHSOM[0], GHSOM.class.getName());
		if (ghsom == null) {
				JOptionPane.showMessageDialog(comirvaUI, "No GHSOM has been created or loaded nor selected.\nThe requested operation does not make sense.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			ghsom.clearLabels();
			setStatusBar("GHSOM-Labels discarded");
			// force visualization to be repainted
			paneVisu.setLoadBufferedImage(false);
			paneVisu.repaint();
		}
	}

	// Visualization -> GHSOM -> Show GHSOM-Grid (different ones)
	private void menuVisuShowGHSOMGrid_actionPerformed(ActionEvent actionEvent, GhSomPrototypeFinder prototypor) {
		// error handling
		// if no SOM-instance has been created, show error message
		int count = ws.countVisuTypes(GHSOM.class.getName());		// count soms
		//if (ghsom == null)
		if (count == 0)
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load a GHSOM first.\nOtherwise it is a bit difficult to display it.", "Error", JOptionPane.ERROR_MESSAGE);
		else if (count > 0) {
			GHSOM request;
			Vector<VisuListItem> items = ws.getVisuListItems(GHSOM.class.getName());
			if (count == 1) {	// only one
				request = (GHSOM) items.firstElement();
			} else {			// more than one
				request = (GHSOM) askUserForVisuListItem(items, GHSOM.class.getName());
			}
			// display
			if (request != null)
				this.displayvisualization(request, prototypor);
		}
	}
	// Visualization -> GHSOM -> Load CoOcc Matrix (GHSOM)
	private void menuVisuAssignGHSOMCoocMatrix_actionPerformed(ActionEvent actionEvent) {
		GHSOM ghsom = (GHSOM) getSelectedvisualizationItem(new GHSOM[0], GHSOM.class.getName());
		if (ghsom == null) {
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load and select a GHSOM first.\nOtherwise it is a bit difficult to display it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			if (ws.matrixList.isEmpty())
				JOptionPane.showMessageDialog(comirvaUI, "Please load a cooccurence matrix for the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
				JOptionPane.showMessageDialog(comirvaUI, "Please select a cooccurence matrix for the SOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else 
				ghsom.setCoOccMatrix((DataMatrix) ws.matrixList.elementAt(dataMatrixList.getSelectedIndex()));
		}
	}

	// Visualization -> GHSOM -> Load CoOcc Matrix Labels (GHSOM)
	private void menuVisuAssignGHSOMCoocMatrixLabels_actionPerformed(ActionEvent actionEvent) {
		GHSOM ghsom = (GHSOM) getSelectedvisualizationItem(new GHSOM[0], GHSOM.class.getName());
		if (ghsom != null) {
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load and select a GHSOM first.\nOtherwise it is a bit difficult to display it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			if (ws.metaDataList.isEmpty()) 		
				JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the CoOcc-Matrix labels.", "Error", JOptionPane.ERROR_MESSAGE);
			else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) 
				JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that represents the  CoOcc-Matrix labels.", "Error", JOptionPane.ERROR_MESSAGE);
			else 
				ghsom.setCoOccMatrixLabels((Vector<String>) ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex()));
		}
	}

	// Visualization -> GHSOM -> Assign CoOcc Labels (GHSOM)
	private void menuVisuAssignGHSOMCoocLabels_actionPerformed(ActionEvent actionEvent) {
		GHSOM ghsom = (GHSOM) getSelectedvisualizationItem(new GHSOM[0], GHSOM.class.getName());
		if (ghsom == null) {
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load and select a GHSOM first. Otherwise it is impossible to perform an action on it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			// check for meta data
			if (ws.metaDataList.isEmpty()) 		
				JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the CoOcc labels for the GHSOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) 
				JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that represents the CoOcc labels for the GHSOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else
				try {
					ghsom.setAltLabels((Vector<String>) ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex()));
				} catch (SizeMismatchException e) {
					e.printStackTrace();
				}
		}
	}

	// Visualization -> GHSOM -> Load GHSOM
	private void menuVisuLoadGHSOM_actionPerformed(ActionEvent actionEvent) {
		// if GHSOM-instance has already been created, ask user if he/she wants to discard current GHSOM
		// Remark: if (ghsom == null) is true, the second condition of the if-clause is not evaluated
		/*if ((ghsom == null) || (JOptionPane.showConfirmDialog(comirvaUI,
				"A GHSOM has already been created or loaded.\nDo you really want to replace it by loading another?",
				"Question",
				JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
			// create file open dialog*/
		// loading does not replace existing ghsom anymore
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			GHSOMFileFilter filter = new GHSOMFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showOpenDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// load file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					setStatusBar("Loading GHSOM from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
					FileInputStream in = new FileInputStream(fileData);
					ObjectInputStream s = new ObjectInputStream(in);
					GHSOM ghsom = (GHSOM)s.readObject();
					s.close();
					in.close();
					// add ghsom
					int count = ws.countVisuTypes(GHSOM.class.getName());
					String caption = "GHSOM " + count + ", file: " + fileData.getName();
					ws.addVisu(ghsom, caption);
					setStatusBar("GHSOM successfully loaded from file: " + fileChooser.getSelectedFile().getAbsolutePath());
					displayvisualization(ghsom);
				} catch (ClassNotFoundException fnfe) {
					JOptionPane.showMessageDialog(comirvaUI, "Internal error (ClassNotFoundException) occurred while loading GHSOM from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while loading GHSOM from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace();
				}
			}
		//}
	}
	// Visualization -> GHSOM -> Save GHSOM
	private void menuVisuSaveGHSOM_actionPerformed(ActionEvent actionEvent) {
		// if no SOM-instance has been created, show error message
		int count = ws.countVisuTypes(GHSOM.class.getName());
		if (count < 1)
		// if (ghsom == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create a GHSOM first.\nOtherwise it is a bit difficult to save it.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			GHSOM ghsom = null;
			Vector<VisuListItem> items = ws.getVisuListItems(GHSOM.class.getName());
			if (count == 1) {
				ghsom = (GHSOM) items.firstElement();
			} else {
				ghsom = (GHSOM) askUserForVisuListItem(items, GHSOM.class.getName());
			}
			if (ghsom != null) {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			GHSOMFileFilter filter = new GHSOMFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving GHSOM to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						FileOutputStream out = new FileOutputStream(fileData);
						ObjectOutputStream s = new ObjectOutputStream(out);
						s.writeObject(ghsom);
						s.flush();
						setStatusBar("GHSOM successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
						s.close();
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving GHSOM to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace();
				}
			}
			}
		}
	}

	// Submenu "SDH"
	// Visualization -> SDH -> Create SDH
	// To perform this operation, a SOM must already be created or loaded.
	private void menuVisuCreateSDH_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// if no SOM-instance has been created, show error message
		int somcount = ws.countVisuTypes(SOM.class.getName());		// count soms
		if (somcount <= 0)
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load a SOM first.\nOtherwise no SDH can be created.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// get a list of available SOM for the combox in the dialog
			SOM[] som = new SOM[somcount];			// the soms itself for updating gui elements
			String[] soms = new String[somcount];	// their names for displaying a choice
			int s = 0, sel = 0;
			// add the soms to the list of choices
			for (int i=0; i<ws.visuList.size(); i++) {
				if (ws.visuList.get(i).getClass().getName().equals(SOM.class.getName())) {
					// add som and its name
					som[s] = (SOM) ws.visuList.get(i);
					soms[s] = ws.listVisu.get(i).toString();
					if (this.visuList.isSelectedIndex(i)) {
						sel = s;	// save selected index in list for preselection
					}
					s++;
				}
			}
			// open a dialog where the user can specify some parameters of the SDH
			SDHCreationDialog sdhDialog = new SDHCreationDialog(comirvaUI, som, soms, sel, 8);
			// SDHCreationDialog sdhDialog = new SDHCreationDialog(comirvaUI, som.getNumberOfColumns()*som.getNumberOfRows(), 8, soms, sel);
			// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
			if (paneVisu.getSDHConfig() != null) {
				sdhDialog.setConfig(paneVisu.getSDHConfig());
			}
			// position and initialize the dialog
			Dimension dlgSize = sdhDialog.getPreferredSize();
			Dimension frmSize = comirvaUI.getSize();
			Point loc = comirvaUI.getLocation();
			sdhDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
			sdhDialog.setModal(true);
			sdhDialog.pack();
			sdhDialog.setVisible(true);
			// if dialog was closed by clicking on "Create SDH", calculate the SDH
			if (sdhDialog.confirmOperation) {
				int spread = sdhDialog.getSpread();
				int iterations = sdhDialog.getIterations();
				// create a new SDH-instance
				SDH sdh = new SDH( som[sdhDialog.getIndexOfSelectedSOM()] );
				// save configuration
				SDHConfig sdhc = new SDHConfig(spread, iterations, sdhDialog.getFractalComponent());
				SDHConfig sdhd = new SDHDefaultConfig();		// default config for comparing
				paneVisu.setSDHConfig(sdhc);
				setStatusBar("Calculating an SDH with spread " + spread);
				// calculate the SDH with the defined spread
				sdh.calcSDH(sdhc.getSpread());
				// normalize the voting matrix
				sdh.normalizeVotingMatrix();
				// interpolate voting matrix
				sdh.interpolateVotingMatrix(sdhc.getIterations());
				// add fractal component
				sdh.addFractalComponent(sdhDialog.getFractalComponent());
				// create name according to options
				int count = ws.countVisuTypes(sdh.getClass().getName()) + 1;
				String caption = new String("SDH ");	// the visu type and
				caption += count + ": ";				// its number in the list is fixed
				// the rest is optional; if desired it is displayed as 'key=value' pairs
				boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
				boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
				if (allval || nonstd && sdhc.getSpread() != sdhd.getSpread()) {
					caption += "spread=" + sdhc.getSpread() + "; ";
				}
				if (allval || nonstd && sdhc.getIterations() != sdhd.getIterations()) {
					caption += "iterations=" + sdhc.getSpread() + "; ";
				}
				if (allval || nonstd && sdhc.getFractalComponent() != sdhd.getFractalComponent()) {
					caption += "fractal component=" + sdhc.getFractalComponent() + "; ";
				}
				if (dmPreferences.useDataMatrixName()) {
					caption += sdh.getSOM().getDataset().getName(); //som.getDataset().getName();
				}
				caption = caption.trim();
				// remove ':' if no text was added and so it is still at the end
				if (caption.charAt(caption.length()-1) == ':') {
					caption.replace(":", "");
				}
				// add visu to list				
				ws.addVisu(sdh, caption);
				this.visuList.setSelectedValue(caption, true);		// select new visu
				setStatusBar("SDH-Calculation with spread " + spread + " finished.");
			}
		}
	}
	// Visualization -> SDH -> Show SDH
	private void menuVisuShowSDH_actionPerformed(ActionEvent actionEvent) {
		// error handling
		int count = ws.countVisuTypes(SDH.class.getName());		// count sdhs
		if (count == 0) {			// no som created, display error message
			// if no SDH-instance has been created, show error message
			JOptionPane.showMessageDialog(comirvaUI, "Please create or load an SDH first.\nOtherwise it is a bit difficult to display it.", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (count > 0) {
			Vector<VisuListItem> items = ws.getVisuListItems(SDH.class.getName());
			SDH request;
			if (count == 1) {	// if only one choice take it
				request = (SDH) items.firstElement();
			} else {			// if more than one choices ask user
				request = (SDH) askUserForVisuListItem(items, SDH.class.getName());
			}
			if (request != null)
				this.displayvisualization(request);
		}
	}
	// Visualization -> SDH -> Load SDH
	private void menuVisuLoadSDH_actionPerformed(ActionEvent actionEvent) {
		// if SDH-instance has already been created, ask user if he/she wants to discard current SDH
		// Remark: if ((sdh == null) && (som == null)) is true, the second condition of the if-clause is not evaluated
		/*if (((sdh == null) && (som == null)) || (JOptionPane.showConfirmDialog(comirvaUI,
				"Loading an SDH will replace existing SDHs and SOMs.\nAn SDH or SOM has already been created or loaded.\nDo you really want to replace them by loading another SDH?",
				"Question",
				JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
		*/	
		// opening an visualization does not replace another visu anymore
			// create file open dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			SDHFileFilter filter = new SDHFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showOpenDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// load file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					setStatusBar("Loading SDH from file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
					FileInputStream in = new FileInputStream(fileData);
					ObjectInputStream s = new ObjectInputStream(in);
					SDH sdh = (SDH)s.readObject();
					s.close();
					in.close();
					// also set SOM
					SOM som = sdh.getSOM();
					// add som and sdh
					int somcount = ws.countVisuTypes(SOM.class.getName()) + 1;
					int sdhcount = ws.countVisuTypes(SDH.class.getName()) + 1;
					String somstr = "SOM " + somcount + ", file: " + fileData.getName();
					String sdhstr = "SDH " + sdhcount + ", file: " + fileData.getName(); 
					ws.addVisu(som, somstr);
					ws.addVisu(sdh, sdhstr);
					// display new visu
					displayvisualization(sdh);
					setStatusBar("SDH and underlying SOM successfully loaded from file: " + fileChooser.getSelectedFile().getAbsolutePath());
				} catch (ClassNotFoundException fnfe) {
					JOptionPane.showMessageDialog(comirvaUI, "Internal error (ClassNotFoundException) occurred while loading SDH from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while loading SDH from file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		//}
	}
	// Visualization -> SDH -> Save SDH
	private void menuVisuSaveSDH_actionPerformed(ActionEvent actionEvent) {
		// if no SDH-instance has been created, show error message
		int count = ws.countVisuTypes(SDH.class.getName());
		if (count < 1)
		//if (sdh == null)
			JOptionPane.showMessageDialog(comirvaUI, "Please create an SDH first.\nOtherwise it is a bit difficult to save it.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// sdh selection
			Vector<VisuListItem> items = ws.getVisuListItems(SDH.class.getName());
			SDH sdh = null;
			if (count == 1) {	// only one
				sdh = (SDH) items.firstElement();
			} else {			// more than one
				sdh = (SDH) askUserForVisuListItem(items, SDH.class.getName());
			}
			if (sdh != null) {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			SDHFileFilter filter = new SDHFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving SDH to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						FileOutputStream out = new FileOutputStream(fileData);
						ObjectOutputStream s = new ObjectOutputStream(out);
						s.writeObject(sdh);
						s.flush();
						setStatusBar("SDH successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
						s.close();
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving SDH to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			}
		}
	}

	// Submenu "Similarity Vector"
	// Visualization -> Similarity Vector -> Circled Bars (Basic)
	private void menuVisuCircledBarsBasic_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix distMatrix = this.getSelectedDataMatrix();
		if (distMatrix == null) 
//			if (ws.matrixList.isEmpty())
//			JOptionPane.showMessageDialog(comirvaUI, "Please load the data matrix (distance vector) that should be visualized.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
//			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a distance vector from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// DataMatrix distMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// test, if matrix is a distance vector
			if (distMatrix.getNumberOfRows() != 1) {
				JOptionPane.showMessageDialog(comirvaUI, "The selected data matrix is not a distance vector\nbecause it contains more than one row.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// create visu list item
				Vector lab = null;
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
					lab = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
					// if meta-data vector with same number of data items as in distance vector selected, take it as labels
					if (lab.size() != distMatrix.getNumberOfColumns()) {		// ... otherwise not.
						lab = null;
					}
				}
				CircledBarsVisuListItem item = new CircledBarsVisuListItem(distMatrix, lab);
				// display visualization
				displayvisualization(item);
				// add visu to visu list
				int count = 1 + ws.countVisuTypes(item.getClass().getName());
				String caption = "Circled Bars " + count + ": ";
				if (dmPreferences.useDataMatrixName()) {
					caption += distMatrix.getName();
				}
				caption = caption.trim();
				if (caption.endsWith(":")) {
					caption = caption.replace(":", "");
				}
				ws.addVisu(item, caption);
				// finished
				setStatusBar("Circled-Bars-Visualization finished.");
			}
		}
	}
	// Visualization -> Similarity Vector -> Circled Bars (Advanced)
	private void menuVisuCircledBarsAdvanced_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix distMatrix = this.getSelectedDataMatrix();
		if (distMatrix == null) 
//			if (ws.matrixList.isEmpty())
//			JOptionPane.showMessageDialog(comirvaUI, "Please load the data matrix (distance vector) that should be visualized.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
//			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please select a distance vector from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// DataMatrix distMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			// test, if matrix is a distance vector
			if (distMatrix.getNumberOfRows() != 1) {
				JOptionPane.showMessageDialog(comirvaUI, "The selected data matrix is not a distance vector\nbecause it contains more than one row.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// open a dialog where the user can specify some parameters of the visualization
				CircledBarsAdvancedCreationDialog cbDialog = new CircledBarsAdvancedCreationDialog(comirvaUI, distMatrix.getNumberOfColumns() - 1);
				// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
				if (paneVisu.getCircledBarsAdvancedConfig() != null) {
					cbDialog.setConfig(paneVisu.getCircledBarsAdvancedConfig());
				}
				// position and initialize the dialog
				Dimension dlgSize = cbDialog.getPreferredSize();
				Dimension frmSize = comirvaUI.getSize();
				Point loc = comirvaUI.getLocation();
				cbDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
				cbDialog.setModal(true);
				cbDialog.pack();
				cbDialog.setVisible(true);
				// if dialog was closed by clicking on "Create CB", calculate the circled bars visu
				if (cbDialog.confirmOperation) {
					// save configuration
					CircledBarsAdvancedConfig cbaCfg = new CircledBarsAdvancedConfig(	cbDialog.getShowNearestN(),
							cbDialog.isSortByDistance());
					setStatusBar("Preparing Circled-Bars-Visualization");
					Vector labels = new Vector();			// vector containing labels
					// if meta-data vector with same number of data items as in distance vector selected, take it as labels
					if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
						Vector lab = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
						if (lab.size() == distMatrix.getNumberOfColumns())
							labels = (Vector)lab.clone();
					}
					// if no meta-data vector with same number of data items is selected, create Vector with increasing ints and take it as labels
					if ((labels == null) || (labels.size() == 0)) {
						for (int i=0; i<distMatrix.getNumberOfColumns(); i++)
							labels.addElement(Integer.toString(i));
					}
					// extract the number of data items the user wishes to see
					Vector distVector = (Vector)distMatrix.getRow(0).clone();					// clone distance vector
					VectorSort.sortWithMetaData(distVector, labels);							// sort the distance vector and update the labels vector accordingly
					DataMatrix distMatrixModified = new DataMatrix();							// new DataMatrix for sorted and pruned distance vector
					Vector labelsModified = new Vector();										// new Vector for labels of pruned distance vector
					for (int i=0; i<cbDialog.getShowNearestN(); i++) {
						distMatrixModified.addValue((Double)distVector.elementAt(i));
						labelsModified.addElement(labels.elementAt(i));
					}
					// special handling if user wishes to see data items sorted by meta-data names (alphabetically)
					if (!cbDialog.isSortByDistance()) {
						// create a TreeMap
						TreeMap tm = new TreeMap();
						// insert meta-data as key and distances as value into TreeMap
						// the TreeMap automatically sorts by key
						// ATTENTION: each key must be unique because inserting a new key with an existing name will overwrite the existing value!!!
						for (int i=0; i<cbDialog.getShowNearestN(); i++) {
							tm.put((String)labels.elementAt(i), distMatrixModified.getValueAtPos(0,i));
						}
						// erase original label and distance vectors
						labelsModified.clear();
						distMatrixModified = new DataMatrix();
						// copy alphabetically sorted TreeMap to label vector and distance vector
						Iterator it;
						it = tm.keySet().iterator();			// iterator over keys (meta-data names)
						// copy back
						while (it.hasNext())
							labelsModified.addElement((String)it.next());
						it = tm.values().iterator();			// iterator over values
						while (it.hasNext())
							distMatrixModified.addValue((Double)it.next());
					}
					CircledBarsVisuListItem item = new CircledBarsVisuListItem(distMatrixModified, labelsModified, cbaCfg);
					// show visualization
					displayvisualization(item);
					// add visu to visu list					 
					int count = 1 + ws.countVisuTypes(item.getClass().getName());
					String caption = "Circled Bars " + count + " (advanced): ";
					if (dmPreferences.useDataMatrixName()) {
						caption += distMatrix.getName();
					}
					caption = caption.trim();
					if (caption.endsWith(":")) {
						caption = caption.replace(":", "");
					}
					ws.addVisu(item, caption);
					// finished
					setStatusBar("Circled-Bars-Visualization finished.");
				}
			}
		}
	}

	// Submenu "Similarity Matrix"
	// Visualization -> Similarity Matrix -> Circled Fans
	private void menuVisuCircledFans_actionPerformed(ActionEvent actionEvent) {
		// get selected data matrix
		DataMatrix distMatrix = this.getSelectedDataMatrix();
		if (distMatrix == null) 
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select a quadratic data matrix (distance matrix) from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		else { 	// data matrix loaded and selected
			setStatusBar("Preparing Circled-Fans-Visualization");
			// test, whether matrix is quadratic or not
			if (distMatrix.getNumberOfColumns() != distMatrix.getNumberOfRows())
				JOptionPane.showMessageDialog(comirvaUI, "The selected matrix is not quadratic and, therefore, \ncannot be visualized as distance matrix.", "Error", JOptionPane.ERROR_MESSAGE);
			else {
				// labels for configuration dialog
				Vector labels_cfg = new Vector();
				// if meta-data vector with same number of data items as in distance matrix selected, take it as labels
				// paneVisu.setLabels(null);			// set labels in visualization pane to null
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
					Vector lab = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
					if (lab.size() == distMatrix.getNumberOfColumns()) {	// specified
						// paneVisu.setLabels(lab);
						labels_cfg = lab;									// set labels Vector for configuration dialog
					}
				}
				// if label Vector is still empty, insert Integers [0, #dataItems] as labels
				if (labels_cfg.isEmpty()) {
					// create label vector containing Integers for configuration dialog
					for (int i=0; i<distMatrix.getNumberOfColumns(); i++) {
						labels_cfg.add(new String(Integer.toString(i)));
					}
				}
				// open a dialog where the user can specify some parameters of the visualization
				CircledFansCreationDialog cfDialog = new CircledFansCreationDialog(comirvaUI, labels_cfg);
				// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
				boolean found = false;
				for (int i=ws.visuList.size()-1; i>=0 && !found; i--) {		// search f�r last created circled fans visu
					VisuListItem item = ws.visuList.get(i);
					if (item instanceof CircledFansVisuListItem) {
						cfDialog.setConfig( ((CircledFansVisuListItem)item).getCfg() );
						found = true;		// quit for loop because we've found the last configuration
					}
				}
				// position and initialize the dialog
				Dimension dlgSize = cfDialog.getPreferredSize();
				Dimension frmSize = comirvaUI.getSize();
				Point loc = comirvaUI.getLocation();
				cfDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
				cfDialog.setModal(true);
				cfDialog.pack();
				cfDialog.setVisible(true);
				// if dialog was closed by clicking on "Create CF", calculate the circled fans visu
				if (cfDialog.confirmOperation) {
					// save configuration
					CircledFansConfig cfCfg = new CircledFansConfig(cfDialog.getMaxBarThickness(),
							cfDialog.getMaxDataItemsL0(),
							cfDialog.getMaxDataItemsL1(),
							cfDialog.getAngleFanL1(),
							cfDialog.isRandomCenter(),
							cfDialog.getIndexCenter(),
							cfDialog.isNormalizeData());
					setStatusBar("Preparing Circled-Fans-Visualization");
					CircledFansVisuListItem item = new CircledFansVisuListItem(distMatrix, labels_cfg, cfCfg);
					// show visualization
					displayvisualization(item);
					// add visu to list
					int count = 1 + ws.countVisuTypes(item.getClass().getName());
					String caption = "Circled Fans " + count + ": ";
					// the rest is optional; if desired it is displayed as 'key=value' pairs
					boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
					boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
					CircledFansConfig stdCfg = new CircledFansDefaultConfig();
					if (allval || nonstd && (cfCfg.isRandomCenter() != stdCfg.isRandomCenter())) {
						if (cfCfg.isRandomCenter())
							caption += "random center; ";
						else
							caption += "center index=" + cfCfg.getIndexCenter() + "; ";
					}
					if (allval || nonstd && (cfCfg.getMaxBarThickness() != stdCfg.getMaxBarThickness()))  {
						caption += "max bar thickness=" + cfCfg.getMaxBarThickness() + "; ";
					}
					if (allval || nonstd && (cfCfg.getMaxDataItemsL0() != stdCfg.getMaxDataItemsL0())) {
						caption += "max data items L0=" + cfCfg.getMaxDataItemsL0() + "; ";
					}
					if (allval || nonstd && (cfCfg.getMaxDataItemsL1() != stdCfg.getMaxDataItemsL1())) {
						caption += "max data items L1=" + cfCfg.getMaxDataItemsL1() + "; ";
					}
					if (allval || nonstd && (cfCfg.getAngleFanL1() != stdCfg.getAngleFanL1())) {
						caption += "angle fan L1=" + cfCfg.getAngleFanL1() + "; "; 
					}
					if (allval || nonstd && (cfCfg.isNormalizeData() != stdCfg.isNormalizeData())) {
						caption += "normalize data=" + cfCfg.isNormalizeData() + "; ";
					}
					if (dmPreferences.useDataMatrixName()) {
						caption += distMatrix.getName();
					}
					caption = caption.trim();
					if (caption.endsWith(":")) {
						caption = caption.replace(":", "");
					}
					ws.addVisu(item, caption);
					this.visuList.setSelectedValue(caption, true);		// select new visu
					// finished					
					setStatusBar("Circled-Fans-Visualization finished.");
				}
			}
		}
	}
	// Visualization -> Similarity Matrix -> Probabilistic Network
	private void menuVisuProbabilisticNetwork_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix distMatrix = this.getSelectedDataMatrix();
		if (distMatrix == null) 
//			if (ws.matrixList.isEmpty())
//			JOptionPane.showMessageDialog(comirvaUI, "Please load the data matrix (distance vector) that should be visualized.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
//			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select a quadratic data matrix (distance matrix) from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// DataMatrix distMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			if (distMatrix.getNumberOfColumns() != distMatrix.getNumberOfRows())
				JOptionPane.showMessageDialog(comirvaUI, "The selected matrix is not quadratic and, therefore, \ncannot be visualized as distance matrix.", "Error", JOptionPane.ERROR_MESSAGE);
			else {	// a quadratic distance matrix is selected
				// open a dialog where the user can specify some parameters of the visualization
				ProbabilisticNetworkCreationDialog pnDialog = new ProbabilisticNetworkCreationDialog(comirvaUI);
				// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
				if (paneVisu.getProbabilisticNetworkConfig() != null) {
					pnDialog.setConfig(paneVisu.getProbabilisticNetworkConfig());
				}
				// position and initialize the dialog
				Dimension dlgSize = pnDialog.getPreferredSize();
				Dimension frmSize = comirvaUI.getSize();
				Point loc = comirvaUI.getLocation();
				pnDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
				pnDialog.setModal(true);
				pnDialog.pack();
				pnDialog.setVisible(true);
				// if dialog was closed by clicking on "Create Network", calculate the network visu
				if (pnDialog.confirmOperation) {
					// save configuration
					ProbabilisticNetworkConfig pnCfg = new ProbabilisticNetworkConfig(	pnDialog.getMaxEdgeThickness(),
							pnDialog.getMaxDistReduction(),
							pnDialog.getMaxVertexDiameter(),
							pnDialog.getMinVertexDiameter(),
							pnDialog.getProbCorrection(),
							pnDialog.getAdaptationRunsEpochs(),
							pnDialog.getAdaptationThreshold(),
							pnDialog.getGridSize());
					setStatusBar("Preparing Probabilistic-Network-Visualization");
					// show visualization
					Vector lab;
					if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
						lab = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
						// if meta-data vector with same number of data items as in distance matrix selected, take it as labels
						if (lab.size() != distMatrix.getNumberOfColumns())
							lab = new Vector();	// don't use meta data if size does not correspond to number of columns
						//paneVisu.setLabels(lab);
					} else {
						lab = new Vector();
					}
					ProbabilisticNetworkVisuListItem item = new ProbabilisticNetworkVisuListItem(distMatrix, lab, pnCfg);



					// add to visu list
					int count = 1 + ws.countVisuTypes(item.getClass().getName());
					String caption = "Probabilistic Network " + count + ": ";
					// the rest is optional; if desired it is displayed as 'key=value' pairs
					boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
					boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
					ProbabilisticNetworkConfig stdCfg = new ProbabilisticNetworkDefaultConfig();

					if (allval || nonstd && pnCfg.getMaxEdgeThickness()!=stdCfg.getMaxEdgeThickness()) {
						caption += "max edge thickness=" + pnCfg.getMaxEdgeThickness() + "; ";
					}
					if (allval || nonstd && pnCfg.getMaxDistReduction()!=stdCfg.getMaxDistReduction()) {
						caption += "max dist reduction=" + pnCfg.getMaxDistReduction() + "; ";
					}
					if (allval || nonstd && pnCfg.getMaxVertexDiameter()!=stdCfg.getMaxVertexDiameter()) {
						caption += "max vertex diameter=" + pnCfg.getMaxVertexDiameter() + "; ";
					}
					if (allval || nonstd && pnCfg.getMinVertexDiameter()!=stdCfg.getMinVertexDiameter()) {
						caption += "min vertex diameter=" + pnCfg.getMinVertexDiameter() + "; ";
					}
					if (allval || nonstd && pnCfg.getProbCorrection()!=stdCfg.getProbCorrection()) {
						caption += "prob correction=" + pnCfg.getProbCorrection() + ";";
					}
					if (allval || nonstd && pnCfg.getAdaptationRunsEpochs()!=stdCfg.getAdaptationRunsEpochs()) {
						caption += "adaption runs epochs=" + pnCfg.getAdaptationRunsEpochs() + "; ";
					}
					if (allval || nonstd && pnCfg.getAdaptationThreshold()!=stdCfg.getAdaptationThreshold()) {
						caption += "adpation threshold="  + pnCfg.getAdaptationThreshold() + "; ";
					}
					if (allval || nonstd && pnCfg.getGridSize()!=stdCfg.getGridSize()) {
						caption += "grid size=" + pnCfg.getGridSize() +  "; ";
					}
					if (dmPreferences.useDataMatrixName()) {
						caption += distMatrix.getName() + "; ";
					}
					caption = caption.trim();
					if (caption.endsWith(":")) {
						caption = caption.replace(":", "");
					}
					ws.addVisu(item, caption);
					this.visuList.setSelectedValue(caption, true);		// select new visu
					// finsished
					setStatusBar("Probabilistic-Network-Visualization finished.");
				}
			}
		}
	}
	// Visualization -> Similarity Matrix -> Continuous Similarity Ring
	private void menuVisuContinuousSimilarityRing_actionPerformed(ActionEvent actionEvent) {
		// error handling
		// no data matrix loaded
		DataMatrix distMatrix = this.getSelectedDataMatrix();
		if (distMatrix == null) 
//			if (ws.matrixList.isEmpty())
//			JOptionPane.showMessageDialog(comirvaUI, "Please load the data matrix (distance vector) that should be visualized.", "Error", JOptionPane.ERROR_MESSAGE);
			// at least 1 data matrix loaded but none selected
//			else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select a quadratic data matrix (distance matrix) from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else {
			// DataMatrix distMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());
			if (distMatrix.getNumberOfColumns() != distMatrix.getNumberOfRows())
				JOptionPane.showMessageDialog(comirvaUI, "The selected matrix is not quadratic and, therefore, \ncannot be visualized as distance matrix.", "Error", JOptionPane.ERROR_MESSAGE);
			else {	// a quadratic distance matrix is selected
				// labels for configuration dialog
				Vector labels_cfg = new Vector();
				// if meta-data vector with same number of data items as in distance matrix selected, take it as labels
				paneVisu.setLabels(null);			// set labels in visualization pane to null
				if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() != -1)) {
					Vector lab = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
					if (lab.size() == distMatrix.getNumberOfColumns()) {	// specified
						paneVisu.setLabels(lab);
						labels_cfg = lab;									// set labels Vector for configuration dialog
					}
				}
				// if label Vector is still empty, insert Integers [0, #dataItems] as labels
				if (labels_cfg.isEmpty()) {
					// create label vector containing Integers for configuration dialog
					for (int i=0; i<distMatrix.getNumberOfColumns(); i++) {
						labels_cfg.add(new String(Integer.toString(i)));
					}
					paneVisu.setLabels(labels_cfg);
				}
				// open a dialog where the user can specify some parameters of the visualization
				CSRCreationDialog csrDialog = new CSRCreationDialog(comirvaUI, labels_cfg);
				// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
				if (paneVisu.getCSRConfig() != null) {
					csrDialog.setConfig(paneVisu.getCSRConfig());
				}
				// position and initialize the dialog
				Dimension dlgSize = csrDialog.getPreferredSize();
				Dimension frmSize = comirvaUI.getSize();
				Point loc = comirvaUI.getLocation();
				csrDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
				csrDialog.setModal(true);
				csrDialog.pack();
				csrDialog.setVisible(true);
				// if dialog was closed by clicking on "Create CSR", calculate the CSR visu
				if (csrDialog.confirmOperation) {
					// save configuration
					CSRConfig csrCfg = new CSRConfig(	csrDialog.getNumberOfNeighborsPerPrototype(),
							csrDialog.getPrototypeIndices(),
							csrDialog.getMaxEdgeThickness(),
							csrDialog.getPrototypesVertexDiameter(),
							csrDialog.getNeighborsVertexDiameter(),
							csrDialog.getIterationsNeighborsPlacement());
					setStatusBar("Preparing CSR-Visualization");
					CSRVisuListItem item = new CSRVisuListItem(distMatrix, labels_cfg, csrCfg);
					// show visualization
					displayvisualization(item);
					// add visu to list
					int count = 1 + ws.countVisuTypes(item.getClass().getName());
					String caption = "Continuous Similarity Ring " + count + ": ";
					// the rest is optional; if desired it is displayed as 'key=value' pairs
					boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
					boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
					CSRDefaultConfig stdCfg = new CSRDefaultConfig();
					int[] sel = csrCfg.getPrototypeIndices();
					int[] std = stdCfg.getPrototypeIndices();
					String items = "";

					for (int i=0; i<sel.length; i++) {
						// search for occurrence of this index in the standard selection vector
						boolean occured = false;
						for (int j=0; j<std.length; j++) {
							if (std[j]==sel[i]) occured = true;	// found
						}
						// add field name only if:
						//   b. we should display all values OR
						//   c. we should display only nonstandard values AND
						//	 d. it does NOT occur in the list of standard values
						if ( (allval || nonstd && !occured) ) {
							items += labels_cfg.get(sel[i]) + ", ";		// add name (from labels vector)
						}
					}
					// trim item string and remove ',' at the end
					items = items.trim();
					if (items.endsWith(",")) {
						items = items.substring(0, items.length()-1);
					}					
					// integrate item string into caption
					if (items.length() > 0) {
						if (nonstd) {
							caption += "non-standard ";
						}
						caption += "prototype items: " + items + "; ";
					}
					if (allval || nonstd && csrCfg.getNumberOfNeighborsPerPrototype() != stdCfg.getNumberOfNeighborsPerPrototype()) {
						caption += "Neighbours/Prototype=" + csrCfg.getNumberOfNeighborsPerPrototype() + "; ";
					}
					if (allval || nonstd && csrCfg.getMaxEdgeThickness() != stdCfg.getMaxEdgeThickness()) {
						caption += "max. Edge Thickness=" + csrCfg.getMaxEdgeThickness() + "; ";
					}
					if (allval || nonstd && csrCfg.getPrototypesVertexDiameter() != stdCfg.getPrototypesVertexDiameter()) {
						caption += "Prototype Vertex Diameter=" + csrCfg.getPrototypesVertexDiameter() + "; ";
					}
					if (allval || nonstd && csrCfg.getNeighborsVertexDiameter() != stdCfg.getNeighborsVertexDiameter()) {
						caption += "Neighbour Vertex Diameter=" + csrCfg.getNeighborsVertexDiameter() + "; ";
					}
					if (dmPreferences.useDataMatrixName()) {
						caption += distMatrix.getName();
					}
					caption = caption.trim();
					if (caption.endsWith(":")) {
						caption = caption.replace(":", "");
					}
					ws.addVisu(item, caption);
					this.visuList.setSelectedValue(caption, true);		// select new visu
					// finsished
					setStatusBar("CSR-Visualization finished.");
				}
			}
		}
	}

	// Submenu "Term Occurrence Matrix"
	// Visualization -> Term Occurrence Matrix -> Sunburst
	private void menuVisuShowSunburst_actionPerformed(ActionEvent actionEvent) {
		// error handling		
		// no data matrix loaded
		DataMatrix toMatrix = this.getSelectedDataMatrix();
		if (toMatrix == null) 			
			//if (ws.matrixList.isEmpty())
			JOptionPane.showMessageDialog(comirvaUI, "Please load and select the term occurrence matrix \nthat should be used for the sunburst.", "Error", JOptionPane.ERROR_MESSAGE);
		// at least 1 data matrix loaded but none selected
		//else if (!ws.matrixList.isEmpty() && (dataMatrixList.getSelectedIndex() == -1))
		//	JOptionPane.showMessageDialog(comirvaUI, "Please select a term occurrence matrix from the data matrix list.", "Error", JOptionPane.ERROR_MESSAGE);
		// data matrix loaded and selected
		else if (ws.metaDataList.isEmpty())
			JOptionPane.showMessageDialog(comirvaUI, "Please load meta-data containing the terms for the term occurrence matrix.", "Error", JOptionPane.ERROR_MESSAGE);
		// meta-data (for terms) loaded
		else if (!ws.metaDataList.isEmpty() && (dataMetaDataList.getSelectedIndex() == -1)) 		// at least 1 meta-data vector loaded but none selected
			JOptionPane.showMessageDialog(comirvaUI, "Please select the meta-data item that contains the terms \nfor the selected term occurrence matrix.", "Error", JOptionPane.ERROR_MESSAGE);
		// meta-data (for terms) loaded and selected
		else {
			// get selected term occurrence matrix and meta-data term list
			//DataMatrix toMatrix = (DataMatrix)ws.matrixList.elementAt(dataMatrixList.getSelectedIndex());	// already assign to
			Vector termVector = (Vector)ws.metaDataList.elementAt(dataMetaDataList.getSelectedIndex());
			// test, if matrix is a term occurrence matrix (boolean!)
			if (!toMatrix.isBooleanMatrix())
				JOptionPane.showMessageDialog(comirvaUI, "The selected data matrix is not a term occurrence matrix\n" +
						"because it contains values other than 0 and 1.\n\n" +
						"The rows of a term occurrence matrix refer to the terms,\n" +
						"the columns to the documents in which the terms may occur.\n" +
						"A value of 1 at position (i,j) indicates that term i occurs in\n" +
						"document j, a value of 0 indicates that this is not the case.", "Error", JOptionPane.ERROR_MESSAGE);
			else {	// term occurrence matrix is boolean matrix
				// test if number of rows of selected term occurrence matrix and selected term list equal
				if (toMatrix.getNumberOfRows() != termVector.size())
					JOptionPane.showMessageDialog(comirvaUI, "The number of rows of the selected term occurrence matrix \n" +
							"does not equal the number of rows of the selected meta-data vector.\n" +
							"Please select the meta-data item that contains the terms \n" +
							"for the selected term occurrence matrix.", "Error", JOptionPane.ERROR_MESSAGE);
				else {
					// open a dialog where the user can specify some parameters of the visualization
					SunburstCreationDialog sbDialog = new SunburstCreationDialog(comirvaUI, termVector);
					// set the values for the config dialog to the values specified the last time the dialog was shown (if it was already shown before)
					if (paneVisu.getSunburstConfig() != null) {
						sbDialog.setConfig(paneVisu.getSunburstConfig());
					}
					// position and initialize the dialog
					Dimension dlgSize = sbDialog.getPreferredSize();
					Dimension frmSize = comirvaUI.getSize();
					Point loc = comirvaUI.getLocation();
					sbDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
					sbDialog.setModal(true);
					sbDialog.pack();
					sbDialog.setVisible(true);
					// if dialog was closed by clicking on "Create Sunburst", calculate the sunburst visu
					if (sbDialog.confirmOperation) {
						// save configuration
						SunburstConfig sbCfg = new SunburstConfig(	sbDialog.getMaxItemsPerNode(),
								sbDialog.getMaxDepth(),
								sbDialog.getMinImportance(),
								sbDialog.getRootTerms(),
								sbDialog.getMinFontSize(),
								sbDialog.getMaxFontSize());
						// inform user
						setStatusBar("Preparing Sunburst-Visualization");
						// generate sunburst data
						double importanceRoot = 1.0;		// importance of root node
						double angularStartPosition = 0.0;	// angular start position of root node
						// make sure that term occurrence matrix complies with co-oc term Vector
						// by eliminating all documents from toMatrix that do not
						// contain all words that must be contained in root node
						Vector<Integer> idxDocPaths = new Vector<Integer>();	// to store indices of documents that contain the filter terms
						toMatrix = TermProfileUtils.getSubsetOfTermOccurrenceMatrix(toMatrix, termVector, sbCfg.getRootTerms(), idxDocPaths);
						// generate root node of sunburst
						SunburstNode rootNode = new SunburstNode(	toMatrix,
								termVector,
								sbCfg.getRootTerms(),
								importanceRoot,
								importanceRoot,
								angularStartPosition,
								sbCfg.getMaxItemsPerNode(),
								sbCfg.getMaxDepth(),
								sbCfg.getMinImportance(),
								null);
						// try to find a "path to documents" meta-data instance for
						// the selected term occurrence matrix
						// note that this does only work, if the user did not
						// rename the imported ETP data!
						rootNode.setDocuments(null);		// by default, set doc-paths Vector to null (no document-paths found)
						// first, get name of selected term occurrence matrix
						String nameTOM = toMatrix.getName();	// (String)ws.listMatrices.getElementAt(dataMatrixList.getSelectedIndex());
						// get prefix for term occurrence matrices
						String prefixTOM = ETPXMLExtractorThread.prefixTermOccurrences;
						// test if TOM has correct prefix
						if (nameTOM.startsWith(prefixTOM)) {
							// search for meta-data instance with the prefix for a documents path vector
							// extract filename from selected TOM
							int startIdx = prefixTOM.length();
							int endIdx = nameTOM.indexOf(".xml");
							if (endIdx == -1)			// avoid exception because of not found ".xml"
								endIdx = startIdx;
							nameTOM = nameTOM.substring(startIdx, endIdx);	// nameTOM contains the filename
							// get prefix for document paths meta-data
							String prefixDocPaths = ETPXMLExtractorThread.prefixDocumentPaths;
							// get prefix for TFxIDF data
							String prefixTFxIDF = ETPXMLExtractorThread.prefixTFxIDFs;
							// search in meta-data instances for a Vector
							// with the correct doc-path prefix and the
							// same filename as the selected TOM
							for (int i=0; i<ws.listMetaData.size(); i++) {
								String nameCurrentMetaData = (String)ws.listMetaData.getElementAt(i);	// the name of the current meta-data instance
								// test for matching string
								if (nameCurrentMetaData.startsWith(prefixDocPaths)) {	// meta-data instance is a doc-path-vector
									// test, if it is the correct filename
									// extract filename from current doc-path-vector
									startIdx = prefixDocPaths.length();
									endIdx = nameCurrentMetaData.indexOf(".xml");
									if (endIdx == -1)			// avoid exception because of not found ".xml"
										endIdx = startIdx;
									nameCurrentMetaData = nameCurrentMetaData.substring(startIdx, endIdx);	// nameCurrentMetaData contains the filename
									// iff filenames equal, then use meta-data vector as doc-paths vector
									if (nameCurrentMetaData.compareTo(nameTOM) == 0) {
										Vector<String> documents = TermProfileUtils.getMaskedDocumentPaths((Vector<String>)ws.metaDataList.elementAt(i), idxDocPaths);
										rootNode.setDocuments(documents);
									}
								}
							}
							// search data instances for a Vector containing
							// the TFxIDF values of the terms
							//	ATTENTION: searches only in main data matrix list!!!
							for (int i=0; i<ws.listMatrices.size(); i++) {
								String nameCurrentMatrix = (String)ws.listMatrices.getElementAt(i);	// the name of the current data instance
								// test for matching string
								if (nameCurrentMatrix.startsWith(prefixTFxIDF)) {	// TFxIDF data found
									// test, if it is the correct filename
									// extract filename from current doc-path-vector
									startIdx = prefixTFxIDF.length();
									endIdx = nameCurrentMatrix.indexOf(".xml");
									if (endIdx == -1)			// avoid exception because of not found ".xml"
										endIdx = startIdx;
									nameCurrentMatrix = nameCurrentMatrix.substring(startIdx, endIdx);	// nameCurrentMetaData contains the filename
									// iff filenames equal, then use data vector as TFxIDF vector
									if (nameCurrentMatrix.compareTo(nameTOM) == 0) {
										Vector<Double> tfxidf = new Vector<Double>();
										DataMatrix dmTmp = (DataMatrix)ws.matrixList.elementAt(i);	
										// convert TFxIDF values from DataMatrix to Vector<Double>
										for (int j=0; j<dmTmp.getNumberOfRows(); j++) {
											tfxidf.addElement(dmTmp.getValueAtPos(j, 0));
										}
										rootNode.setTFxIDF(tfxidf);
									}
								}
							}
						}
						// calculate all sunburst nodes
						rootNode.calculateSunburst();
						// create sunburst item for visualization list
						SunBurstVisuListItem item = new SunBurstVisuListItem(toMatrix, termVector, sbCfg, rootNode);
						// display visu
						displayvisualization(item);
						// add visu to list
						int count = 1 + ws.countVisuTypes(item.getClass().getName());
						String caption = "Sunburst " + count + ": ";
						// the rest is optional; if desired it is displayed as 'key=value' pairs
						boolean allval = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_ALL);
						boolean nonstd = (dmPreferences.getVisuName() == VisuPreferences.VISU_NAME_NONSTANDARD);
						SunburstDefaultConfig stdCfg = new SunburstDefaultConfig();

						if (allval || nonstd && sbCfg.getMaxItemsPerNode()!=stdCfg.getMaxItemsPerNode()) {
							caption += "max items/node=" + sbCfg.getMaxItemsPerNode() + "; ";
						}
						if (allval || nonstd && sbCfg.getMaxDepth() != stdCfg.getMaxDepth()) {
							caption += "max depth=" + sbCfg.getMaxDepth() + "; ";
						}
						if (allval || nonstd && sbCfg.getMinImportance() != stdCfg.getMinImportance()) {
							caption += "min importance=" + sbCfg.getMinImportance() + "; ";
						}
						if (allval || nonstd && sbCfg.getMinFontSize() != stdCfg.getMinFontSize()) {
							caption += "min font size=" + sbCfg.getMinFontSize() + "; ";
						}
						if (allval || nonstd && sbCfg.getMaxFontSize() != stdCfg.getMaxFontSize()) {
							caption += "max font size=" + sbCfg.getMaxFontSize() + "; ";
						}
						Vector<String> rt = sbCfg.getRootTerms();	// get selected root terms
						if (allval || nonstd && !rt.isEmpty())	{	// empty root terms vector is the standard value
							for (int i = 0; i < rt.size(); i++) {
								caption += rt.get(i) + ", ";
							}
							caption = caption.substring(0, caption.length() - 2);	// remove last ", "
							caption += "; ";										// and add "; " instead
						}
						if (dmPreferences.useDataMatrixName()) {
							caption += toMatrix.getName() + "; ";
						}
						caption.trim();
						if (caption.endsWith(": ")) caption += " (standard)";
						ws.addVisu(item, caption);
						this.visuList.setSelectedValue(caption, true);		// select new visu
						// finished
						setStatusBar("Sunburst-Visualization finished.");
					}
				}
			}
		}
	}

	// Submenu "Colormap"
	// Visualization -> Colormap -> Islands
	private void menuVisuCMIslands_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Islands\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);					// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMIslands.setSelected(true);
		popupColormapIslands.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Islands());
		this.setStatusBar("Colormap set to \"Islands\"");

	}
	// Visualization -> Colormap -> Fire
	private void menuVisuCMFire_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Fire\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);					// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMFire.setSelected(true);
		popupColormapFire.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Fire());
		this.setStatusBar("Colormap set to \"Fire\"");
	}
	// Visualization -> Colormap -> Colorful
	private void menuVisuCMColorful_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Colorful\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);					// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMColorful.setSelected(true);
		popupColormapColorful.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Colorful());
		this.setStatusBar("Colormap set to \"Colorful\"");
	}
	// Visualization -> Colormap -> Sun
	private void menuVisuCMSun_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Sun\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);					// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMSun.setSelected(true);
		popupColormapSun.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Sun());
		this.setStatusBar("Colormap set to \"Sun\"");
	}
	// Visualization -> Colormap -> Ocean
	private void menuVisuCMOcean_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Ocean\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);					// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMOcean.setSelected(true);
		popupColormapOcean.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Ocean());
		this.setStatusBar("Colormap set to \"Ocean\"");
	}
	// Visualization -> Colormap -> Gray
	private void menuVisuCMGray_actionPerformed(ActionEvent actionEvent) {
		// change colormap
		this.setStatusBar("Assigning colormap \"Gray\" and updating visualization area...");
		menuVisuCMInverted.setSelected(false);			// switch to new Colormap resets the inverted-CheckBox
		popupVisuCMInverted.setSelected(false);
		menuVisuCMGray.setSelected(true);
		popupColormapGray.setSelected(true);
		paneVisu.setColorMap(new ColorMap_Gray());
		this.setStatusBar("Colormap set to \"Gray\"");
	}
	// Visualization -> Colormap -> Inverted
	private void menuVisuCMInverted_actionPerformed(ActionEvent actionEvent) {
		menuVisuCMInverted.setSelected(true);
		popupVisuCMInverted.setSelected(true);
		// invert current colormap
		paneVisu.getColorMap().invert();
		// force visualization to be repainted
		paneVisu.setLoadBufferedImage(false);
		paneVisu.repaint();
		this.setStatusBar("Current colormap is inverted");
	}

	// Visualization -> Save Visualization...
	private void menuVisuSaveVisu_actionPerformed(ActionEvent actionEvent) {
		// if no VisuPane-instance has been created, don't do anything
		if (paneVisu == null)
			this.setStatusBar("Fatal Error: No VisuPane-instance has been created!");
		else if (paneVisu.getImage() == null)	// if content of visu pane is empty, inform user
			JOptionPane.showMessageDialog(comirvaUI, "Visualization area is empty. No graphics to store.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			GraphicFileFilter filter = new GraphicFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving visualization pane to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						// get content of visu pane as BufferedImage
						BufferedImage img = paneVisu.getImage();
						ImageIO.write(img, filter.getExtension(fileData), fileData);
						setStatusBar("Visualization pane successfully stored in file: " + fileChooser.getSelectedFile().getAbsolutePath());
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving the visualization pane to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	// Visualization -> Export to EPS...
	private void menuVisuExportEPS_actionPerformed(ActionEvent actionEvent) {
		// if no VisuPane-instance has been created, don't do anything
		if (paneVisu == null)
			this.setStatusBar("Fatal Error: No VisuPane-instance has been created!");
		else if (paneVisu.getImage() == null)	// if content of visu pane is empty, inform user
			JOptionPane.showMessageDialog(comirvaUI, "Visualization area is empty. No graphics to store.", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			// create file save dialog
			JFileChooser fileChooser = new JFileChooser(dmPreferences.getLastDir());		// go directly to the directory where the last file was chosen
			EPSFileFilter filter = new EPSFileFilter();
			fileChooser.setFileFilter(filter);
			int returnVal = fileChooser.showSaveDialog(comirvaUI);
			// valid file selected?
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					// save file
					File fileData = new File(fileChooser.getSelectedFile().getAbsolutePath());
					// remember directory where selected file is located (for future file chooser access)
					dmPreferences.setLastDir(fileData.getPath());
					// save new last directory
					saveDataManagementPrefs(dmPreferences);
					// test, if file already exists
					if (!fileData.exists() || (JOptionPane.showConfirmDialog(comirvaUI, "A file named "+fileData.getAbsolutePath()+" already exists.\nDo you want to overwrite it?",
							"Question",
							JOptionPane.YES_NO_OPTION) ==  JOptionPane.YES_OPTION)) {
						setStatusBar("Saving visualization pane as EPS to file: "  +  fileChooser.getSelectedFile().getAbsoluteFile());
						// get content of visu pane as EPS
						BufferedWriter bw = new BufferedWriter(new FileWriter(fileData));
						//bw.write(paneVisu.getEpsGraphics().toEPS(paneVisu.getBounds()));
						paneVisu.getEpsGraphics().toEPS(paneVisu.getBounds(), bw);
						bw.flush();
						bw.close();
						setStatusBar("Visualization pane successfully exported to EPS-file: " + fileChooser.getSelectedFile().getAbsolutePath());
					}
				} catch (FileNotFoundException fnfe) {
					// not possible, because file is to be stored
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(comirvaUI, "I/O-Error occurred while saving the visualization pane to file:\n"  +  fileChooser.getSelectedFile().getAbsoluteFile(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	// Visualization -> Preferences...
	private void menuVisuPreferences_actionPerformed(ActionEvent actionEvent) {
		// open a dialog where the user can specify some preferences of the visualization pane
		VisuPreferencesDialog visuPrefDialog = new VisuPreferencesDialog(comirvaUI);
		// set the values for the preferences dialog to the values specified the
		// last time the dialog was shown (if it was already shown before)
		// or, if it was never shown, to the values specified by the user preferences file in the
		// user's home directory
		if (this.paneVisu.getVisuPreferences() != null) {
			visuPrefDialog.setConfig(this.paneVisu.getVisuPreferences());
		}
		// position and initialize the dialog
		Dimension dlgSize = visuPrefDialog.getPreferredSize();
		Dimension frmSize = comirvaUI.getSize();
		Point loc = comirvaUI.getLocation();
		visuPrefDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		visuPrefDialog.setModal(true);
		visuPrefDialog.pack();
		visuPrefDialog.setVisible(true);
		// if dialog was closed by clicking on "OK", update the configuration
		if (visuPrefDialog.confirmOperation) {
			// save preferences
			/*VisuPreferences visuPrefs = new VisuPreferences(	
					visuPrefDialog.getBackgroundColor(),
					visuPrefDialog.getBorderSize(),
					visuPrefDialog.getLabelFontSize(),
					visuPrefDialog.isEnableEPS());*/
			this.dmPreferences.setVisuPreferences(
					visuPrefDialog.getBackgroundColor(), 
					visuPrefDialog.getBorderSize(), 
					visuPrefDialog.getLabelFontSize(),
					visuPrefDialog.isEnableEPS()
			);			
			// write preferences to object stream
			setStatusBar("Saving visualization preferences");
			String userHomeDir = System.getProperty("user.home");			// get user's home directory
			File prefs = new File(userHomeDir+"/CoMIRVA.prefs");
			try {
				FileOutputStream out = new FileOutputStream(prefs);
				ObjectOutputStream s = new ObjectOutputStream(out);
				s.writeObject(dmPreferences);
				s.flush();
				s.close();
				out.close();
				setStatusBar("Visualization preferences successfully stored in file: " + prefs.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// apply new preferences
			this.paneVisu.setVisuPreferences(dmPreferences);
			// force visualization to be repainted
			paneVisu.setLoadBufferedImage(false);
			paneVisu.repaint();
			// if EPS-output is not allowed, disable respective menu entry
			this.menuVisuExportEPS.setEnabled(this.paneVisu.getVisuPreferences().isEnableEPS());
		}
	}

	// Menu "Help"
	// Help -> About
	private void menuHelpAbout_actionPerformed(ActionEvent actionEvent) {
		// display about box
		AboutBox aboutBox = new AboutBox(comirvaUI);
		Dimension dlgSize = aboutBox.getPreferredSize();
		Dimension frmSize = comirvaUI.getSize();
		Point loc = comirvaUI.getLocation();
		aboutBox.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		aboutBox.setModal(true);
		aboutBox.pack();
		aboutBox.setVisible(true);
	}

	//toolbar actions
	// rename tab
	public void toolbarTabRename_actionPerformed(ActionEvent e) {
		int index = tbp_matrices.getSelectedIndex(); 
		String oldTitle = tbp_matrices.getTitleAt(index);
		if (index>1) {
			String newTitle = JOptionPane.showInputDialog(comirvaUI, "Enter new name for current selected Tab:", oldTitle);
			if (newTitle == null || (newTitle.length() == 0)) {		// prevent deleting the old title if dialog is cancelled
				newTitle = oldTitle;
			}
			tbp_matrices.setTitleAt(index, newTitle);
		} else if (index <= 1) {	// one of the static tabs is selected
			JOptionPane.showMessageDialog(comirvaUI, "Current selected Tab '" + tbp_matrices.getTitleAt(index) + "' cannot be renamed", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	//remove tab
	public void toolbarTabRemove_actionPerformed(ActionEvent e) {
		int index = tbp_matrices.getSelectedIndex();
		if (index > 1) {
			ws.additionalListMatrices.remove(index - 2);	// remove list model of this tab
			ws.additionalMatrixList.remove(index - 2);		// remove data matrix list of this tab
			dataMatrixListVector.remove(index - 2);			// remove reference to JList of this tab
			tbp_matrices.removeTabAt(index);				// remove the tab itself
		} else if (index <= 1) {
			// non-removable tab is currently selected
			JOptionPane.showMessageDialog(comirvaUI, "Current selected Tab '" + tbp_matrices.getTitleAt(index) + "' cannot be removed", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	//show preferences dialog
	public void toolbarOptions_actionPerformed(ActionEvent e) {
		// open a dialog where the user can specify some preferences of the visualization pane
		DataManagementPreferencesDialog dmPrefDialog = new DataManagementPreferencesDialog(comirvaUI);
		// set the values for the preferences dialog to the values specified the
		// last time the dialog was shown (if it was already shown before)
		// or, if it was never shown, to the values specified by the user preferences file in the
		// user's home directory
		/*if (this.paneVisu.getVisuPreferences() != null) { */
		dmPrefDialog.setConfig(dmPreferences);
		/* }*/
		// position and initialize the dialog
		Dimension dlgSize = dmPrefDialog.getPreferredSize();
		Dimension frmSize = comirvaUI.getSize();
		Point loc = comirvaUI.getLocation();
		dmPrefDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		dmPrefDialog.setModal(true);
		dmPrefDialog.pack();
		dmPrefDialog.setVisible(true);
		// if dialog was closed by clicking on "OK", update the configuration
		if (dmPrefDialog.confirmOperation) {
			// apply settings
			this.dmPreferences.setDataManagementPreferences(
					dmPrefDialog.isToolbarFloatable(), 
					dmPrefDialog.getTabLayout(), 
					dmPrefDialog.getVisuName(), 
					dmPrefDialog.useDataMatrixName()
			);
			// save settings
			saveDataManagementPrefs(dmPreferences);
			// apply new preferences
			//this.dmPreferences = dmPrefs;
			this.toolbarDataManagement.setFloatable(dmPreferences.isToolbarFloatable());
			this.tbp_matrices.setTabLayoutPolicy(dmPreferences.getTabLayout());
			// force visualization to be repainted
			paneVisu.setLoadBufferedImage(false);
			paneVisu.repaint();
			// if EPS-output is not allowed, disable respective menu entry
			// this.menuVisuExportEPS.setEnabled(this.paneVisu.getVisuPreferences().isEnableEPS());
		}
	}

	/**
	 * @param dmPrefDialog
	 * @return
	 */
	private void saveDataManagementPrefs(VisuPreferences dmPrefs) {
		// write preferences to object stream
		setStatusBar("Saving data matrix preferences");
		String userHomeDir = System.getProperty("user.home");			// get user's home directory
		File prefs = new File(userHomeDir+"/CoMIRVA_dm.prefs");
		try {
			FileOutputStream out = new FileOutputStream(prefs);
			ObjectOutputStream s = new ObjectOutputStream(out);
			s.writeObject(dmPrefs);
			s.flush();
			s.close();
			out.close();
			setStatusBar("Data matrix preferences successfully stored in file: " + prefs.getAbsolutePath());
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	// visu list selection listener
	private void visuList_valueChanged(ListSelectionEvent e) {
		int index = visuList.getSelectedIndex();		// get selected visu
		if (index >= 0) {
			VisuListItem item = ws.visuList.get(index);		// get corresponding visu item
			displayvisualization(item);
		}
	}
	// visu list actions
	// rename visu list item
	private void visuListRename_actionPerformed(ActionEvent e) {
		if (this.visuList.getSelectedIndex() >= 0) {
			String result = (String) JOptionPane.showInputDialog(comirvaUI, "Enter a new name for the visualization", "visualization Rename", JOptionPane.QUESTION_MESSAGE, null, null, this.visuList.getSelectedValue());
			if (result != null)	{	// OK clicked
				ws.listVisu.set(this.visuList.getSelectedIndex(), result);
			}
		}
	}	
	// delete visu list item
	private void visuListDeleteItem_actionPerformed(ActionEvent e) {
		int index = this.visuList.getSelectedIndex(); 
		if (index >= 0) {
			if (JOptionPane.showConfirmDialog(comirvaUI, "Are you sure to delete the selected visualization\n'" + this.visuList.getSelectedValue() + "'?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				ws.listVisu.remove(index);
				ws.visuList.remove(index);
			}
		}
	}	

	// action listener
	// Menu "File"
	// action listener for menu File -> Load Matrix Data File...
	private class MenuFileLoadDataFile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileLoadDataFile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileLoadDataFile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Save Matrix Data File...
	private class MenuFileSaveDataFile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileSaveDataFile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileSaveDataFile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Load Meta-Data File...
	private class MenuFileLoadMetaDataFile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileLoadMetaDataFile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileLoadMetaDataFile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Save Meta-Data File...
	private class MenuFileSaveMetaDataFile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileSaveMetaDataFile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileSaveMetaDataFile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Delete Selected Item
	private class MenuFileDeleteSelectedItem_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		public MenuFileDeleteSelectedItem_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method with a newly created action event,
			// containing the clicked list as event source
			// ActionEvent my = new ActionEvent(list, actionEvent.getID(), actionEvent.getActionCommand(), actionEvent.getWhen(), actionEvent.getModifiers());
			adaptee.menuFileDeleteSelectedItem_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Empty Matrix Data List
	private class MenuFileEmptyDataFileList_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileEmptyDataFileList_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileEmptyDataFileList_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Empty Meta-Data List
	private class MenuFileEmptyMetaDataFileList_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileEmptyMetaDataFileList_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileEmptyMetaDataFileList_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Load Workspace...
	private class MenuFileLoadWorkspace_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileLoadWorkspace_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileLoadWorkspace_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Save Workspace...
	private class MenuFileSaveWorkspace_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileSaveWorkspace_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileSaveWorkspace_actionPerformed(actionEvent);
		}
	}
	// action listener for menu File -> Exit
	private class MenuFileExit_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuFileExit_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileExit_actionPerformed(actionEvent);
		}
	}

	// Menu "Audio"
	// action listener for menu Audio -> Load Audio File
	private class MenuAudioLoadAudioFile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuAudioLoadAudioFile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuAudioLoadAudioFile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Audio -> Extract Feature -> Fluctuation Patterns
	private class MenuAudioExtractFeatureFP_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuAudioExtractFeatureFP_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuAudioExtractFeatureFP_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Audio -> Extract Feature -> MFCC
	private class MenuAudioExtractFeatureMFCC_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuAudioExtractFeatureMFCC_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuAudioExtractFeatureMFCC_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Audio -> Extract Feature -> GMM-ME
	private class MenuAudioExtractFeatureGMMME_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuAudioExtractFeatureGMMME_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuAudioExtractFeatureGMMME_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Audio -> Show Audio Player
	private class MenuAudioShowAudioPlayer_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuAudioShowAudioPlayer_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuAudioShowAudioPlayer_actionPerformed(actionEvent);
		}
	}

	// Menu "Data"
	// Submenu "Data Matrix"
	// action listener for menu Data -> Data Matrix -> Rename...
	private class MenuDataDataMatrixRename_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixRename_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixRename_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Data Matrix -> Sort List
	private class MenuDataDataMatrixSort_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixSort_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixSort_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Data Matrix -> Normalize...
	private class MenuDataDataMatrixNormalize_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixNormalize_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixNormalize_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Data Matrix -> Vectorize -> by Rows
	private class MenuDataDataMatrixVectorizeByRows_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixVectorizeByRows_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixVectorizeByRows_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Data Matrix -> Vectorize -> by Columns
	private class MenuDataDataMatrixVectorizeByColumns_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixVectorizeByColumns_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixVectorizeByColumns_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Data Matrix -> PCA
	private class MenuDataDataMatrixPCA_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataDataMatrixPCA_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataDataMatrixPCA_actionPerformed(actionEvent);
		}
	}

	// Submenu "Meta-Data"
	// action listener for popup menu Meta-Data -> Delete Item
	private class MenuDataMetaDataDeleteItem_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataMetaDataDeleteItem_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			adaptee.menuDataMetaDataDeleteItem_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Meta-Data -> Rename...
	private class MenuDataMetaDataRename_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataMetaDataRename_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataMetaDataRename_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Meta-Data -> Sort
	private class MenuDataMetaDataSort_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataMetaDataSort_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataMetaDataSort_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Meta-Data -> Extract Data from File List
	private class MenuDataMetaDataExtract_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataMetaDataExtract_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataMetaDataExtract_actionPerformed(actionEvent);
		}
	}

	// Submenu "Web Mining"
	// action listener for menu Data -> Web Mining -> Co-Occurrence Analysis -> Retrieve Page Counts
	private class MenuDataWebMiningPageCountMatrix_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningPageCountMatrix_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningPageCountMatrix_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Co-Occurrence Analysis -> Requery Invalid Page Counts Matrix Entries
	private class MenuDataWebMiningRequeryPageCountMatrix_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningRequeryPageCountMatrix_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningRequeryPageCountMatrix_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Co-Occurrence Analysis -> Estimate Conditional Probabilities
	private class MenuDataWebMiningEstimateConditionalProbabilities_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningEstimateConditionalProbabilities_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningEstimateConditionalProbabilities_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Term Profile -> Retrieve Meta-Data-Related Pages
	private class MenuDataWebMiningTermProfileRetrieveRelatedPages_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningTermProfileRetrieveRelatedPages_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningTermProfileRetrieveRelatedPages_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Term Profile -> Create Entity Term Profile(s) from Extracted Documents
	private class MenuDataWebMiningTermProfileCreateEntityTermProfile_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningTermProfileCreateEntityTermProfile_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningTermProfileCreateEntityTermProfile_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Term Profile -> Load ETP from XML-File
	private class MenuDataWebMiningTermProfileLoadETP_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningTermProfileLoadETP_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningTermProfileLoadETP_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Term Profile -> Extract terms from Retrieved Documents
	private class MenuDataWebMiningTermProfileExtractTerms_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningTermProfileExtractTerms_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningTermProfileExtractTerms_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Data -> Web Mining -> Term Profile -> Update Paths in ETP-XML-File(s)
	private class MenuDataWebMiningTermProfileUpdatePathsETP_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuDataWebMiningTermProfileUpdatePathsETP_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuDataWebMiningTermProfileUpdatePathsETP_actionPerformed(actionEvent);
		}
	}

	// Menu "Visualization"
	// Submenu "SOM"
	// action listener for menu Visualization -> Create SOM
	private class MenuVisuCreateSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCreateSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCreateSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Assign Labels (SOM)
	private class MenuVisuAssignLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuAssignLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuAssignLabels_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Clear Labels (SOM)
	private class MenuVisuClearLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuClearLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuClearLabels_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> MDM Labels
	private class MenuVisuCalcMDMLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCalcMDMLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCalcMDMLabels_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Show SOM-Grid
	private class MenuVisuShowSOMGrid_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuShowSOMGrid_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowSOMGrid_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Show MDM-Grid
	private class MenuVisuShowMDMGrid_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuShowMDMGrid_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowMDMGrid_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Load SOM
	private class MenuVisuLoadSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuLoadSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuLoadSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Save SOM
	private class MenuVisuSaveSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuSaveSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuSaveSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Export SOM to HTML
	private class MenuVisuExportHTMLSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuExportHTMLSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuExportHTMLSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Export MDM to HTML
	private class MenuVisuExportHTMLMDM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuExportHTMLMDM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuExportHTMLMDM_actionPerformed(actionEvent);
		}
	}
	// Menu "Visualization"
	// Submenu "GHSOM"
	// action listener for menu Visualization -> Create GHSOM
	private class MenuVisuCreateGHSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCreateGHSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCreateGHSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Assign Labels (GHSOM)
	private class MenuVisuAssignGHSOMLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuAssignGHSOMLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuAssignGHSOMLabels_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Clear Labels (GHSOM)
	private class MenuVisuClearGHSOMLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuClearGHSOMLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuClearGHSOMLabels_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Show GHSOM-Grid
	private class MenuVisuShowGHSOMGrid_Action implements ActionListener {
		MainUI adaptee;
		private GhSomPrototypeFinder prototypor;

		// constructor
		MenuVisuShowGHSOMGrid_Action(MainUI adaptee) {
			this(adaptee, null);
		}

		// constructor
		MenuVisuShowGHSOMGrid_Action(MainUI adaptee, GhSomPrototypeFinder prototypor) {
			this.adaptee = adaptee;
			this.prototypor = prototypor;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowGHSOMGrid_actionPerformed(actionEvent, prototypor);
		}
	}

	// action listener for menu Visualization -> Load CoOcc Matrix (GHSOM)
	private class MenuVisuAssignGHSOMCoocMatrix_Action implements ActionListener {
		MainUI adaptee;
		GhSomPrototypeFinder prototypor;

		// constructor
		MenuVisuAssignGHSOMCoocMatrix_Action(MainUI adaptee) {
			this(adaptee, null);
		}

		// constructor
		MenuVisuAssignGHSOMCoocMatrix_Action(MainUI adaptee, GhSomPrototypeFinder prototypor) {
			this.adaptee = adaptee;
			this.prototypor = prototypor;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuAssignGHSOMCoocMatrix_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Load CoOcc Matrix Labels (GHSOM)
	private class MenuVisuAssignGHSOMCoocMatrixLabels_Action implements ActionListener {
		MainUI adaptee;
		GhSomPrototypeFinder prototypor;

		public MenuVisuAssignGHSOMCoocMatrixLabels_Action(MainUI mainUI, GhSomPrototypeFinder prototypor) {
			this.adaptee = mainUI;
			this.prototypor = prototypor;
		}

		public MenuVisuAssignGHSOMCoocMatrixLabels_Action(MainUI mainUI) {
			this(mainUI,null);
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowGHSOMGrid_actionPerformed(actionEvent, prototypor);
		}
	}

//	// action listener for menu Visualization -> Load CoOcc Matrix Labels (GHSOM)
//	private class MenuVisuAssignGHSOMCoocMatrixLabels_Action implements ActionListener {
//	MainUI adaptee;

//	// constructor
//	MenuVisuAssignGHSOMCoocMatrixLabels_Action(MainUI adaptee) {
//	this.adaptee = adaptee;
//	}

//	// action
//	public void actionPerformed(ActionEvent actionEvent) {
//	// call adaptee's method
//	adaptee.menuVisuAssignGHSOMCoocMatrixLabels_actionPerformed(actionEvent);
//	}
//	}
	// action listener for menu Visualization -> Assign CoOcc Labels (GHSOM)
	private class MenuVisuAssignGHSOMCoocLabels_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuAssignGHSOMCoocLabels_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuAssignGHSOMCoocLabels_actionPerformed(actionEvent);
		}
	}

	// action listener for menu Visualization -> Load GHSOM
	private class MenuVisuLoadGHSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuLoadGHSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuLoadGHSOM_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Save GHSOM
	private class MenuVisuSaveGHSOM_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuSaveGHSOM_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuSaveGHSOM_actionPerformed(actionEvent);
		}
	}

	// Submenu "SDH"
	// action listener for menu Visualization -> Create SDH
	private class MenuVisuCreateSDH_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCreateSDH_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCreateSDH_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Show SDH
	private class MenuVisuShowSDH_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuShowSDH_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowSDH_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Load SDH
	private class MenuVisuLoadSDH_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuLoadSDH_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuLoadSDH_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Save SDH
	private class MenuVisuSaveSDH_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuSaveSDH_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuSaveSDH_actionPerformed(actionEvent);
		}
	}

	// Submenu "Similarity Vector"
	// action listener for menu Visualization -> Circled-Bars (Basic)
	private class MenuVisuCircledBarsBasic_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCircledBarsBasic_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCircledBarsBasic_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Circled-Bars (Advanced)
	private class MenuVisuCircledBarsAdvanced_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCircledBarsAdvanced_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCircledBarsAdvanced_actionPerformed(actionEvent);
		}
	}

	// Submenu "Similarity Matrix"
	// action listener for menu Visualization -> Circled Fans
	private class MenuVisuCircledFans_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCircledFans_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCircledFans_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Probabilistic Network
	private class MenuVisuProbabilisticNetwork_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuProbabilisticNetwork_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuProbabilisticNetwork_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Continuous Similarity Ring
	private class MenuVisuContinuousSimilarityRing_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuContinuousSimilarityRing_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuContinuousSimilarityRing_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Term Occurrence Matrix -> Sunburst
	private class MenuVisuTermOccurrenceMatrixSunburst_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuTermOccurrenceMatrixSunburst_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuShowSunburst_actionPerformed(actionEvent);
		}
	}

	// Submenu "Colormap"
	// action listener for menu Visualization -> Colormap -> Islands
	private class MenuVisuCMIslands_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMIslands_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMIslands_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Fire
	private class MenuVisuCMFire_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMFire_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMFire_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Colorful
	private class MenuVisuCMColorful_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMColorful_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMColorful_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Sun
	private class MenuVisuCMSun_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMSun_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMSun_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Ocean
	private class MenuVisuCMOcean_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMOcean_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMOcean_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Gray
	private class MenuVisuCMGray_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMGray_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMGray_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Colormap -> Inverted
	private class MenuVisuCMInverted_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuCMInverted_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuCMInverted_actionPerformed(actionEvent);
		}
	}

	// action listener for menu Visualization -> Save Visualization...
	private class MenuVisuSaveVisu_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuSaveVisu_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuSaveVisu_actionPerformed(actionEvent);
		}
	}
	// action listener for menu Visualization -> Export to EPS...
	private class MenuVisuExportEPS_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuExportEPS_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuExportEPS_actionPerformed(actionEvent);
		}
	}

	// action listener for menu Visualization -> Preferences...
	private class MenuVisuPreferences_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuVisuPreferences_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuVisuPreferences_actionPerformed(actionEvent);
		}
	}

	// Menu "Help"
	// action listener for menu Help -> About
	private class MenuHelpAbout_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		MenuHelpAbout_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuHelpAbout_actionPerformed(actionEvent);
		}
	}


	// action listeners for audio player
	// action listener for button "Play"/"Pause" of audio player
	private class ButtonAudioPlayerPlay_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		ButtonAudioPlayerPlay_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.btnAudioPlayerPlay_actionPerformed(actionEvent);
		}
	}
	// action listener for button "Stop" of audio player
	private class ButtonAudioPlayerStop_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		ButtonAudioPlayerStop_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.btnAudioPlayerStop_actionPerformed(actionEvent);
		}
	}
	// action listener for button "Next" of audio player
	private class ButtonAudioPlayerNext_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		ButtonAudioPlayerNext_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.btnAudioPlayerNext_actionPerformed(actionEvent);
		}
	}
	// action listener for button "Previous" of audio player
	private class ButtonAudioPlayerPrevious_Action implements ActionListener {
		MainUI adaptee;

		// constructor
		ButtonAudioPlayerPrevious_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.btnAudioPlayerPrevious_actionPerformed(actionEvent);
		}
	}

	//by Florian Marchl
	/**
	 * This class provides an event listener for the visualization list box.
	 * It implements a {@link MouseAdapter} for the mouse clicks, a {@link ListSelectionListener} for
	 * list selection changes (e.g. automatic update on adding elements to the list) and an 
	 * {@link ActionListener}. The handling is delegated to 
	 * {@link MainUI#visuList_valueChanged(ListSelectionEvent)} which triggers drawing of the new selection
	 * 
	 * @author Florian Marchl
	 */
	private class VisuListEventListener extends MouseAdapter implements ListSelectionListener, ActionListener {
		/** link to main UI */
		MainUI adaptee;

		// constructor
		/** constructs a new listener object with a link to the main UI */
		VisuListEventListener(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// Events defined by ListSelectionListener interface
		public void valueChanged(ListSelectionEvent e) {
			// call adaptee's method
			adaptee.visuList_valueChanged(e);
		}

		// Events defined by MouseAdapter
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int index = adaptee.visuList.locationToIndex(e.getPoint());
				ListSelectionEvent event = new ListSelectionEvent(e.getSource(), index, index, false);
				adaptee.visuList_valueChanged(event);
			}
		}

		// Events defined by ActionListener
		public void actionPerformed(ActionEvent e) {
			int index = adaptee.visuList.getSelectedIndex();
			adaptee.visuList_valueChanged(
					new ListSelectionEvent(e.getSource(), index, index, false)
			);
		}		
	}

	/** 
	 * This class implements an {@link ActionListener} for the renaming a list item
	 * @author Florian Marchl
	 */
	private class VisuListRename_Action implements ActionListener {
		/** link to main UI */
		MainUI adaptee;

		/** constructs a new rename action event with given main ui */
		public VisuListRename_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}
		
		// action method
		public void actionPerformed(ActionEvent e) {
			adaptee.visuListRename_actionPerformed(e);
		}		
	}

	/**
	 * This class implements the action for deleting a visualization list item.
	 * @author Florian Marchl
	 */
	private class VisuListDeleteItem_Action implements ActionListener {
		/** link to main ui */
		MainUI adaptee;

		/** constructs a action object with the given main ui */
		public VisuListDeleteItem_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}

		// action method
		public void actionPerformed(ActionEvent e) {
			adaptee.visuListDeleteItem_actionPerformed(e);			
		}	
	}

	//------------------------------------------------------------	
	/**
	 * Changes the text that is displayed in the status bar.
	 *
	 * @param	text	a String containing the text that is displayed in the status bar
	 */
	protected void setStatusBar(String text) {
		statusBar.setText(text);
		statusBar.validate();
		// force status bar to be repainted immediately
		statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		comirvaUI.paint(comirvaUI.getGraphics());
	}

	/**
	 * Changes the text that is displayed in the statusbar
	 * and sets current progress indication to the given value
	 * @param text		a String containing the text that is displeyed
	 * @param progress	a value between the model's minimum and maximum value 
	 * 						indication the progress of the current work 
	 */
	protected void setStatusBar(String text, int progress) {
		setStatusBar(text);
		setStatusBar(progress);
	}

	/**
	 * sets current progress indication to the given value
	 * @param progress a value between the model's minimum and maximum value 
	 * 						indication the progress of the current work 
	 */
	protected void setStatusBar(int progress) {
		// statusBar.getModel().setValue(progress);
		// statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
	}

	/**
	 * Adds a text to the content of the status bar.
	 *
	 * @param	text	a String containing the text that is added at the end of the current status bar text
	 */
	protected void addStatusBar(String text) {
		statusBar.setText(statusBar.getText()+text);
		statusBar.validate();
		// force status bar to be repainted immediately
		statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		comirvaUI.paint(comirvaUI.getGraphics());
	}

	/**
	 * Erases the text in the status bar
	 * and reset progress to minimum
	 */
	protected void clearStatusBar() {
		statusBar.setText("");
		//statusBar.getModel().setValue(statusBar.getModel().getMinimum());
		statusBar.validate();
		// force status bar to be repainted immediately
		statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
	}

	/**
	 * Creates a new instance of the user interface and initializes it.
	 *
	 * @param args	the command line arguments passed to the program
	 */
	public static void main(String[] args) {
		MainUI comirva = new MainUI();								// create instance
		comirva.initUI();											// setup user interface
		comirva.loadVisuPreferences();								// load user preferences for visualization pane
		comirva.loadVisuPreferences();					// load user preferences for data management pane
	}

	/** 
	 * A popup menu mouse adapter
	 * @author Florian Marchl
	 */
	public class PopupMenuMouseAdapter extends MouseAdapter {
		private JPopupMenu popup;

		/** 
		 * create a popup menu mouse adapter displaying an empty popup menu.
		 * The popup menu can be set later using {@link #setPopupMenu(JPopupMenu)}
		 */
		public PopupMenuMouseAdapter() {
			this(null);
		}

		/**
		 * create a popup menu mouse adapter displaying the given popup
		 * menu if a mouse event is received that is a popup trigger.<br>
		 * The displayed popu menu will be empty if the parameter 
		 * is <code>null</code>.
		 * @param popup the popup menu to be displayed
		 */
		public PopupMenuMouseAdapter(JPopupMenu popup) {
			this.popup = popup != null ? popup : new JPopupMenu();
		}

		/** 
		 * replace the current set popup menu by the given popup menu.
		 * Note: The popup menu is only replaced if the new popup menu is not
		 * 	<code>null</code>!
		 * @param popup the new popup menu to be displayed by this mouse adapter.
		 */
		public void setPopupMenu(JPopupMenu popup) {
			if (popup != null) {
				this.popup = popup;
			}
		}
		// respond to both mousePressed and mouseReleased events because
		// it depends on the platform which one is seen as popup trigger
		// the popup trigger test is done in the display method.
		@Override
		public void mousePressed(MouseEvent e) {
			display(e);
		}		
		@Override
		public void mouseReleased(MouseEvent e) {
			display(e);
		}

		/**
		 * displays the popup if the given mouse event is considered
		 * as popup trigger. If the menu belongs to a list, the item
		 * nearest to the click position is automatically selectd.
		 * @param e the mouse event
		 */
		private void display(MouseEvent e) {
			if (popup.isPopupTrigger(e)) {
				Point location = e.getPoint();
				Object source = e.getSource();
				// select (nearest) entry under mouse cursor if a list was clicked
				if (source instanceof JList) {
					JList list = (JList) e.getSource();
					list.setSelectedIndex(list.locationToIndex(location));
				}
				// show popup menu
				popup.show((Component) source, e.getX(), e.getY());
			}
		}
	}

	/** 
	 * creates and returns the popup menu for the data matrix list
	 * @return popup menu for data matrix list
	 */
	private JPopupMenu createDataMatrixPopupMenu() {
		JPopupMenu menuDataMatrix = new JPopupMenu();
		// create MenuEntries and their submenus
		JMenuItem menuLoad = new JMenuItem("Load...");
		JMenuItem menuSave = new JMenuItem("Save...");
		JMenuItem menuEmptyList = new JMenuItem("Empty List");
		JMenuItem menuDelete = new JMenuItem("Delete selected item");
		JMenuItem menuRename = new JMenuItem("Rename...");
		JMenuItem menuSortList = new JMenuItem("Sort List...");
		JMenuItem menuNormalize = new JMenuItem("Normalize");
		JMenu 	  menuVectorize = new JMenu("Vectorize");
		JMenuItem menuVectorizeByRows = new JMenuItem("by Rows");
		JMenuItem menuVectorizeByColumns = new JMenuItem("by Columns");
		JMenuItem menuPCA = new JMenuItem("Principal Component Analysis");

		// add the actions
		menuLoad.addActionListener(new MenuFileLoadDataFile_Action(this));
		menuSave.addActionListener(new MenuFileSaveDataFile_Action(this));
		menuEmptyList.addActionListener(new MenuFileEmptyDataFileList_Action(this));
		menuDelete.addActionListener(new MenuFileDeleteSelectedItem_Action(this));
		menuRename.addActionListener(new MenuDataDataMatrixRename_Action(this));
		menuSortList.addActionListener(new MenuDataDataMatrixSort_Action(this));
		menuNormalize.addActionListener(new MenuDataDataMatrixNormalize_Action(this));
		menuVectorizeByRows.addActionListener(new MenuDataDataMatrixVectorizeByRows_Action(this));
		menuVectorizeByColumns.addActionListener(new MenuDataDataMatrixVectorizeByColumns_Action(this));
		menuPCA.addActionListener(new MenuDataDataMatrixPCA_Action(this));

		// add mnemonic (the underscored letters)
		menuLoad.setMnemonic(KeyEvent.VK_L);
		menuSave.setMnemonic(KeyEvent.VK_S);
		menuEmptyList.setMnemonic(KeyEvent.VK_E);
		menuDelete.setMnemonic(KeyEvent.VK_D);
		menuRename.setMnemonic(KeyEvent.VK_R);
		menuSortList.setMnemonic(KeyEvent.VK_O);
		menuNormalize.setMnemonic(KeyEvent.VK_N);
		menuVectorize.setMnemonic(KeyEvent.VK_V);
		menuVectorizeByRows.setMnemonic(KeyEvent.VK_R);
		menuVectorizeByColumns.setMnemonic(KeyEvent.VK_C);
		menuPCA.setMnemonic(KeyEvent.VK_P);

		// add shortcuts
		menuLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		menuRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		menuSortList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		menuNormalize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		menuVectorizeByRows.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		menuVectorizeByColumns.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));

		// put menu hierachy together
		menuVectorize.add(menuVectorizeByRows);
		menuVectorize.add(menuVectorizeByColumns);		
		menuDataMatrix.add(menuLoad);
		menuDataMatrix.add(menuSave);
		menuDataMatrix.add(menuEmptyList);
		menuDataMatrix.addSeparator();
		menuDataMatrix.add(menuDelete);
		menuDataMatrix.add(menuRename);
		menuDataMatrix.add(menuSortList);
		menuDataMatrix.add(menuNormalize);
		menuDataMatrix.add(menuVectorize);
		menuDataMatrix.add(menuPCA);

		return menuDataMatrix;
	}

	private JPopupMenu createVisuPopupMenu() {
		JPopupMenu menuVisu = new JPopupMenu();

		// create menu entries and their submenues		
		JMenuItem menuCreateSom = new JMenuItem("Create SOM");
		JMenuItem menuCreateGhsom = new JMenuItem("Create GHSOM");
		JMenuItem menuCreateSdh = new JMenuItem("Create SDH");
		JMenuItem menuShowSom = new JMenuItem("Show SOM-Grid");
		JMenuItem menuShowGhsom = new JMenuItem("Show GHSOM-Grid");
		JMenuItem menuShowSdh = new JMenuItem("Show SDH-Grid");		
		JMenuItem menuLoadSom = new JMenuItem("Load SOM");
		JMenuItem menuLoadGhsom = new JMenuItem("Load GHSOM");
		JMenuItem menuLoadSdh = new JMenuItem("Load SHD (and underlying SOM)");
//		JMenu	  menuEditVisu = new JMenu("Edit visualization");
		JMenu	  menuColormap = new JMenu("Colormap");
		popupVisuCMInverted = new JCheckBoxMenuItem("Inverted", false);
		JMenuItem menuSaveVisu = new JMenuItem("Save visualization");
		JMenuItem menuExportEps = new JMenuItem("Export to EPS");
		JMenuItem menuPreferences = new JMenuItem("Preferences");

		// add the actions
		menuCreateSom.addActionListener(new MenuVisuCreateSOM_Action(this));
		menuCreateGhsom.addActionListener(new MenuVisuCreateGHSOM_Action(this));
		menuCreateSdh.addActionListener(new MenuVisuCreateSDH_Action(this));
		menuShowSom.addActionListener(new MenuVisuShowSOMGrid_Action(this));
		menuShowGhsom.addActionListener(new MenuVisuShowGHSOMGrid_Action(this));
		menuLoadSom.addActionListener(new MenuVisuLoadSOM_Action(this));
		menuLoadGhsom.addActionListener(new MenuVisuLoadGHSOM_Action(this));
		menuLoadSdh.addActionListener(new MenuVisuLoadSDH_Action(this));
		popupColormapIslands.addActionListener(new MenuVisuCMIslands_Action(this));
		popupColormapFire.addActionListener(new MenuVisuCMFire_Action(this));
		popupColormapColorful.addActionListener(new MenuVisuCMColorful_Action(this));
		popupColormapSun.addActionListener(new MenuVisuCMSun_Action(this));
		popupColormapOcean.addActionListener(new MenuVisuCMOcean_Action(this));
		popupColormapGray.addActionListener(new MenuVisuCMGray_Action(this));
		popupVisuCMInverted.addActionListener(new MenuVisuCMInverted_Action(this));
		menuSaveVisu.addActionListener(new MenuVisuSaveVisu_Action(this));
		menuExportEps.addActionListener(new MenuVisuExportEPS_Action(this));
		menuPreferences.addActionListener(new MenuVisuPreferences_Action(this));

		// group radio buttons
		ButtonGroup rbGroup = new ButtonGroup();
		rbGroup.add(popupColormapIslands);
		rbGroup.add(popupColormapFire);
		rbGroup.add(popupColormapColorful);
		rbGroup.add(popupColormapSun);
		rbGroup.add(popupColormapOcean);
		rbGroup.add(popupColormapGray);
		rbGroup.setSelected(popupColormapIslands.getModel(), true);

		// construct submenues
		menuColormap.add(popupColormapIslands);
		menuColormap.add(popupColormapFire);
		menuColormap.add(popupColormapColorful);
		menuColormap.add(popupColormapSun);
		menuColormap.add(popupColormapOcean);
		menuColormap.add(popupColormapGray);
		menuColormap.addSeparator();
		menuColormap.add(popupVisuCMInverted);

		// deactivate menues
		// menuShowSom.setEnabled(false);
		// menuShowGhsom.setEnabled(false);
		// menuShowSdh.setEnabled(false);
		// menuEditVisu.setEnabled(false);

		// put hierarchy together
		menuVisu.add(menuCreateSom);
		menuVisu.add(menuCreateGhsom);
		menuVisu.add(menuCreateSdh);
		menuVisu.addSeparator();
		menuVisu.add(menuShowSom);
		menuVisu.add(menuShowGhsom);
		menuVisu.add(menuShowSdh);
		menuVisu.addSeparator();
		menuVisu.add(menuLoadSom);
		menuVisu.add(menuLoadGhsom);
		menuVisu.add(menuLoadSdh);
		menuVisu.addSeparator();
//		menuVisu.add(menuEditVisu);
//		menuVisu.addSeparator();
		menuVisu.add(menuColormap); //menuVisu.add(createMenuVisuColormap()); //
		menuVisu.addSeparator();
		menuVisu.add(menuSaveVisu);
		menuVisu.add(menuExportEps);
		menuVisu.addSeparator();
		menuVisu.add(menuPreferences);

		return menuVisu;
	}

	private JPopupMenu createMetaDataPopupMenu() {
		JPopupMenu popupMenuMetaData = new JPopupMenu();

		// create menu entries
		JMenuItem menuLoad = new JMenuItem("Load...");
		JMenuItem menuSave = new JMenuItem("Save...");
		JMenuItem menuEmpty = new JMenuItem("Empty List...");
		JMenuItem menuDeleteItem = new JMenuItem("Delete");
		JMenuItem menuRenameItem = new JMenuItem("Rename");
		JMenuItem menuSortList = new JMenuItem("Sort List");
		JMenuItem menuExtractID3 = new JMenuItem("Extract ID3-Tags");

		// add actions
		menuLoad.addActionListener(new MenuFileLoadMetaDataFile_Action(this));
		menuSave.addActionListener(new MenuFileSaveMetaDataFile_Action(this));
		menuEmpty.addActionListener(new MenuFileEmptyMetaDataFileList_Action(this));
		menuDeleteItem.addActionListener(new MenuDataMetaDataDeleteItem_Action(this));
		menuRenameItem.addActionListener(new MenuDataMetaDataRename_Action(this));
		menuSortList.addActionListener(new MenuDataMetaDataSort_Action(this));
		menuExtractID3.addActionListener(new MenuDataMetaDataExtract_Action(this));

		// add mnemonic
		menuLoad.setMnemonic(KeyEvent.VK_L);
		menuSave.setMnemonic(KeyEvent.VK_S);
		menuEmpty.setMnemonic(KeyEvent.VK_E);
		menuDeleteItem.setMnemonic(KeyEvent.VK_D);
		menuRenameItem.setMnemonic(KeyEvent.VK_R);
		menuSortList.setMnemonic(KeyEvent.VK_O);
		menuExtractID3.setMnemonic(KeyEvent.VK_X);

		// add keystrokes
		menuLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		menuRenameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		menuSortList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		// put hierarchie together
		popupMenuMetaData.add(menuLoad);
		popupMenuMetaData.add(menuSave);
		popupMenuMetaData.add(menuSave);
		popupMenuMetaData.add(menuEmpty);
		popupMenuMetaData.addSeparator();
		popupMenuMetaData.add(menuDeleteItem);
		popupMenuMetaData.add(menuRenameItem);
		popupMenuMetaData.add(menuSortList);
		popupMenuMetaData.add(menuExtractID3);

		return popupMenuMetaData;
	}

	private JPopupMenu createVisuListPopupMenu() {
		JPopupMenu popupMenuVisuList = new JPopupMenu();

		JMenuItem menuShow = new JMenuItem("Show");
		JMenuItem menuRename = new JMenuItem("Rename");
		JMenuItem menuDelete = new JMenuItem("Delete");

		menuShow.addActionListener(new VisuListEventListener(this));
		menuRename.addActionListener(new VisuListRename_Action(this));
		menuDelete.addActionListener(new VisuListDeleteItem_Action(this));

		popupMenuVisuList.add(menuShow);
		popupMenuVisuList.add(menuRename);
		popupMenuVisuList.add(menuDelete);

		return popupMenuVisuList;
	}

	/** 
	 * add a new tab for a data matrix list
	 * @param model the list model for the new list
	 */
	private void addDataMatrixTab(DefaultListModel model, String title) {
		JList list = new JList(model);
		// add data matrix popup menu
		list.addMouseListener(new PopupMenuMouseAdapter(createDataMatrixPopupMenu()));
		// add list to the list of list components
		this.dataMatrixListVector.add(list);
		// add the tab to the tab pane
		this.tbp_matrices.addTab(title, new JScrollPane(list));
		// make tab editable and closeable
		this.tbp_matrices.setTabComponentAt(tbp_matrices.getTabCount()-1, new ButtonTabComponent(tbp_matrices, this, ws.additionalMatrixNames));
	}

	/**
	 * Displays a dialog box where the user can choose the SOM he/she wants to use
	 * @param items The list of the items that should be chooseable
	 * @return the SOM the user wants to use
	 */
	private VisuListItem askUserForVisuListItem(Vector<VisuListItem> items, String className) {
		SOM som = null;
		String[] soms = ws.getVisuListNamesArray(className, items.size());
		int sel = 0;
		// get selection in GUI for preselection in choose dialog
		String selection = this.visuList.getSelectedValue().toString();
		for (int i=0; i<soms.length; i++) {
			if (soms[i].equals(selection)) {
				sel = i;
			}
		}
		// display input dialog
		String result = (String) JOptionPane.showInputDialog(
				comirvaUI, 											// parent
				"Please select a " + className + " from the list", 	// text
				"Show SOM grid", 									// title
				JOptionPane.QUESTION_MESSAGE, 						// question
				null, 												// no custom icon
				soms,												// possible choices 
				soms[sel]											// preselect the value select in visu list
		);
		if (result != null) {
			som = (SOM) ws.visuList.get(ws.listVisu.indexOf(result));
		}
		return som;
	}
	
	// visualizaTION DISPLAY METHODS
	// The following methods (re)display a visualization
	// They are called either from the Visu menu actions (inital display)
	// or from visu list events (redisplay)
	/** Displays the visualization defined by the visualization list item.
	 *  If the parameter is a GHSOM, the prototypor for it is set to <code>null</code>.
	 *  Use <code>{@link #displayvisualization(VisuListItem, GhSomPrototypeFinder)}</code> instead,
	 *  if you want to specify a certain prototypor.
	 *  
	 * @param item The visualization that should be displayed.
	 */
	private void displayvisualization(VisuListItem item) {
		displayvisualization(item, null);
	}

	/** Displays the visualization defined by the visualization list item.
	 * The second parameter is only used for GHSOM visualizations, but it may also be <code>null</code>.
	 * See the {@link GHSOM} and the {@link GhSomPrototypeFinder} classes documentation for further help
	 * on usage of the prototypor for a GHSOM.
	 * 
	 * @param item			The viusalisation that should be displayed.
	 * @param prototypor	The GhSomPrototypeFinder for a GHSOM.
	 */
	private void displayvisualization(VisuListItem item, GhSomPrototypeFinder prototypor) {
		// remove existing mouse (motion) listeners
		// the visualizations will add their listeners if they need some.
		for (MouseListener l: paneVisu.getMouseListeners()) {
			if (!(l instanceof PopupMenuMouseAdapter)) {	// keep the popup menu listener
				paneVisu.removeMouseListener(l);	
			}
		}
		for (MouseMotionListener l: paneVisu.getMouseMotionListeners()) {
			paneVisu.removeMouseMotionListener(l);
		}
		// display visualization
		setStatusBar("Displaying visualization...");
		Class clazz = item.getClass();
		if (clazz.equals(GHSOM.class)) {		// item instanceof GHSOM // GHSOM (extends SOM!)
			GHSOM ghsom = (GHSOM) item;
			if(!ghsom.isCalculationReady())		// display message if not ready yet
				JOptionPane.showMessageDialog(comirvaUI, "Can't visualize the current GHSOM. It is still under construction.", "Error", JOptionPane.ERROR_MESSAGE);
			else if(prototypor instanceof WebCoocGroupPrototypeFinder && ghsom.getAltLabels() == null)
				JOptionPane.showMessageDialog(comirvaUI, "Please load the CoOcc labels for the GHSOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else if(prototypor instanceof WebCoocGroupPrototypeFinder && ghsom.getCoOccMatrix() == null)
				JOptionPane.showMessageDialog(comirvaUI, "Please load the CoOcc matrix for the GHSOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else if(prototypor instanceof WebCoocGroupPrototypeFinder && ghsom.getCoOccMatrixLabels() == null)
				JOptionPane.showMessageDialog(comirvaUI, "Please load the CoOcc matrix labels for the GHSOM.", "Error", JOptionPane.ERROR_MESSAGE);
			else if(ghsom == paneVisu.getGHSOM() && paneVisu.getVisuType() == VisuPane.TYPE_GHSOMGRID) {
				ghsom.setPrototypor(prototypor);
				paneVisu.setLoadBufferedImage(false);
				paneVisu.repaint();
			} else {
				paneVisu.resetVisuThreads();
				// create and show grid for SOM
				setStatusBar("Preparing GHSOM-Grid-Visualization");
				paneVisu.setGHSOM(ghsom);
				ghsom.setPrototypor(prototypor);
				paneVisu.setLoadBufferedImage(false);
				paneVisu.setVisuType(VisuPane.TYPE_GHSOMGRID);
				paneVisu.repaint();
			}
		} else if (clazz.equals(SOM.class)) {		// item instanceof SOM // SOM
			paneVisu.setSOM((SOM)item);
			paneVisu.setVisuType(VisuPane.TYPE_SOMGRID);
			paneVisu.setLoadBufferedImage(false);
			paneVisu.resetVisuThreads();
			paneVisu.repaint();
		} else if (clazz.equals(SDH.class)) {		// item instanceof SDH // SDH
			setStatusBar("Preparing SDH-Visualization");
			paneVisu.setSDH((SDH) item);
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_SDH);
			paneVisu.resetVisuThreads();
			paneVisu.repaint();
		} else if (clazz.equals(CircledBarsVisuListItem.class)) {	// item instanceof CircledBarsVisuListItem
			CircledBarsVisuListItem cb = (CircledBarsVisuListItem) item;
			DataMatrix distMatrix = cb.getDistanceVector();
			Vector lab = cb.getLabels();
			setStatusBar("Preparing Circled-Bars-Visualization");
			// show visualization
			paneVisu.setDistanceMatrix(distMatrix);
			paneVisu.setLabels(lab);					// set labels for visu pane
			paneVisu.setCircledBarsAdvancedConfig(cb.getCircledBarsAdvancedConfig());	// config is set to null if not advanced
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_DISTANCE_VECTOR_CIRCLED_BARS);
			// repaint visualization (necessary when key-shortcut was used to display graphicss
			paneVisu.resetVisuThreads();
			paneVisu.repaint();
		} else if (clazz.equals(CircledFansVisuListItem.class)) {	// item instanceof CircledFansVisuListItem
			CircledFansVisuListItem cf = (CircledFansVisuListItem) item;
			setStatusBar("Preparing Circled-Fans-Visualization");
			// show visualization
			paneVisu.setDistanceMatrix(cf.getDistMatrix());				// set the distance matrix to the sorted Vector
			paneVisu.setCircledFansConfig(cf.getCfg());					// save configuration in VisuPane for later use
			paneVisu.setLabels(cf.getLabels());
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_DISTANCE_MATRIX_CIRCLED_FANS);
			// repaint visualization (necessary when key-shortcut was used to display graphicss
			paneVisu.resetVisuThreads();
			paneVisu.repaint();
		} else if (clazz.equals(CSRVisuListItem.class)) {	// item instanceof CSRVisuListItem
			CSRVisuListItem csr = (CSRVisuListItem) item;
			paneVisu.setDistanceMatrix(csr.getDistMatrix());
			paneVisu.setCSRConfig(csr.getConfig());
			// if meta-data vector with same number of data items as in distance matrix selected, take it as labels
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_DISTANCE_MATRIX_CONTINUOUS_SIMILARITY_RING);
			// repaint visualization (necessary when key-shortcut was used to display graphicss
			paneVisu.resetVisuThreads();
			paneVisu.repaint();
		} else if (clazz.equals(ProbabilisticNetworkVisuListItem.class)) {	// item instanceof ProbabilisticNetworkVisuListItem
			ProbabilisticNetworkVisuListItem pn = (ProbabilisticNetworkVisuListItem) item;
			paneVisu.setDistanceMatrix(pn.getDistMatrix());
			paneVisu.setProbabilisticNetworkConfig(pn.getConfig());
			paneVisu.setLabels(pn.getLabels());
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_DISTANCE_MATRIX_PROBABILISTIC_NETWORK);
			// repaint visualization (necessary when key-shortcut was used to display graphicss
			paneVisu.resetVisuThreads();
			paneVisu.repaint();			
		} else if (clazz.equals(SunBurstVisuListItem.class)) {
			SunBurstVisuListItem sb = (SunBurstVisuListItem) item;
			// set term occurrence matrix in visu pane
			paneVisu.setTermOccurrenceMatrix(sb.getToMatrix());
			// set labels to meta-data vector containing the terms
			paneVisu.setLabels(sb.getTermVector());
			// set sunburst-config in visu pane
			paneVisu.setSunburstConfig(sb.getConfig());
			// set root node
			paneVisu.setSunburstRootNode(sb.getRootNode());
			// prepare visu
			paneVisu.setLoadBufferedImage(false);
			paneVisu.setVisuType(VisuPane.TYPE_TERM_OCCURRENCE_MATRIX_SUNBURST);
//			// set colormap to "Sun"
//			paneVisu.setColorMap(new ColorMap_Sun());
			// repaint visualization (necessary when key-shortcut was used to display graphicss
			paneVisu.resetVisuThreads();
//			paneVisu.repaint();		
		}
		paneVisu.initMouseListener();
		setStatusBar("Ready");
	}
}
