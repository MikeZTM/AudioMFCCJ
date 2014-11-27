package comirva.ui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import comirva.config.GHSOMConfig;
import comirva.config.defaults.GHSOMDefaultConfig;
import comirva.mlearn.GHSOM;
import comirva.mlearn.SOM;

/**
 * This class implements a dialog for specifying the parameters of a GHSOM. It
 * is shown when the user wished to create a GHSOM.
 * 
 * @author Markus Dopler
 */
public class GHSOMCreationDialog extends JDialog implements ActionListener,
		ChangeListener {

	public static final int TRAIN_LENGTH = 5;

	private static final long serialVersionUID = 2015078837606664911L;

	private JButton btnCreateSOM = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDefault = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderMapUnitsInRow;
	private JSlider sliderMapUnitsInColumn;
	private JSlider sliderGrowThreshold;
	private JSlider sliderExpandThreshold;
	private JSlider sliderTrainingLength;
	private JLabel currentMapUnitsInRow;
	private JLabel currentMapUnitsInColumn;
	private JLabel currentGrowThreshold;
	private JLabel currentExpandThreshold;

	private JComboBox cbInitMethod;
	private JLabel currentTrainingLength;

	private ButtonGroup circularGroup;
	private JRadioButton notCircular;
	private JRadioButton firstCircular;
	private JRadioButton allCircular;

	private JCheckBox orientated;

	private static final String CIRCULAR_NOT = "Non-circular";
	private static final String CIRCULAR_FIRST = "First Map Circular";
	private static final String CIRCULAR_ALL = "All Maps Circular";

	private JSlider sliderMaxSize;
	private JSlider sliderMaxDepth;
	private JLabel currentMaxSize;
	private JLabel currentMaxDepth;

	private GridLayout gridLayout = new GridLayout();
	// default values for the number of map units in each row and column based
	// on the
	// heuristic function setSOMSizeEstimation in the SOM-class
	
	//NOTE: Default values moved to GHSOMConfig class!!!!
	//	private double growThresholdDefault = 0.6;
	//	private double expandThresholdDefault = 0.1;
	//	private int mapUnitsInRowDefault = 2;
	//	private int mapUnitsInColumnDefault = 2;
	//	private int maxSizeDefault = -1;
	//	private int maxDepthDefault = -1;
	
	/** a Vector and an int array storing the possible initialisation methods
	 	the Vector stores the String-representation, the int[] the numbers i.e.
		SOM.RANDOM */
	int[] initMethods;
	/** String represenations of the initialisation methods */
	Vector<String> initMethodsString;

	/** flag indicating for the creating instance, if the dialog was closed by
	 	clicking of "Create SOM" */
	public boolean confirmOperation;

	/**
	 * Creates a new instance of the SOM-parameter dialog and initializes it.
	 * Furthermore, the default values for the displayed sliders are passed as
	 * calculated by the heuristic function in the SOM-class.
	 * 
	 * @param parent
	 *            the Frame of the parent window where the dialog should be
	 *            displayed
	 * @param mapUnitsInRowDefault
	 *            the default number of map units per row (used for the "Default
	 *            Values"-button)
	 * @param mapUnitsInColumnDefault
	 *            the default number of map units per column (used for the
	 *            "Default Values"-button)
	 * 
	 * @see comirva.mlearn.SOM#setSOMSizeEstimation()
	 */
	public GHSOMCreationDialog(Frame parent) {
		super(parent);
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			// different training methods
			// fill possible init-methods as defined in SOM into the array
			// the pairs have to match for every index. for example:
			// if initMethods[0] = SOM.GRADIENT, then initMethodsString[1] must
			// be "gradient init"
			this.initMethods = new int[] { SOM.INIT_RANDOM, SOM.INIT_GRADIENT,
					SOM.INIT_LINEAR, SOM.INIT_SLC };
			this.initMethodsString = new Vector<String>();
			this.initMethodsString.add("Random");
			this.initMethodsString.add("Gradient");
			this.initMethodsString.add("Linear (Kohonen)");
			this.initMethodsString.add("SLC");
			// init UI
			initSOMCreationDialog();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Initializes the dialog by displaying all labels and sliders, setting
	 * title, creating buttons and assigning an ActionListener.
	 * </p>
	 * To arrange the elements of the dialog box, a GridLayout is used.
	 */
	private void initSOMCreationDialog() {
		GHSOMConfig ghsomDefault = new GHSOMDefaultConfig();
		this.setTitle("GHSOM - Configuration");
		// assign text to buttons, set name and assign action listener
		btnCreateSOM.setText("Create GHSOM");
		btnCreateSOM.setMnemonic(KeyEvent.VK_S);
		// set "Create SDH"-button as default
		this.getRootPane().setDefaultButton(btnCreateSOM);
		btnCreateSOM.addActionListener(this);
		btnCancel.setText("Cancel");
		btnCancel.setMnemonic(KeyEvent.VK_C);
		btnCancel.addActionListener(this);
		btnDefault.setText("Default Values");
		btnDefault.setMnemonic(KeyEvent.VK_D);
		btnDefault.addActionListener(this);
		// create and initialize sliders
		sliderMapUnitsInRow = new JSlider(1, 10, ghsomDefault.getMapUnitsInRow());
		sliderMapUnitsInColumn = new JSlider(1, 10, ghsomDefault.getMapUnitsInColumn());
		sliderGrowThreshold = new JSlider(1, 100,
				(int) (ghsomDefault.getGrowThreshold() * 100));
		sliderExpandThreshold = new JSlider(1, 100,
				(int) (ghsomDefault.getExpandThreshold() * 100));

		sliderMaxSize = new JSlider(GHSOM.NA_MAX_SIZE, 400, GHSOM.NA_MAX_SIZE);
		sliderMaxDepth = new JSlider(GHSOM.NA_MAX_DEPTH, 100,
				GHSOM.NA_MAX_DEPTH);

		sliderTrainingLength = new JSlider(1, 20, 5);
		sliderMapUnitsInRow.setMinorTickSpacing(1);
		sliderMapUnitsInColumn.setMinorTickSpacing(1);
		sliderGrowThreshold.setMinorTickSpacing(1);
		sliderExpandThreshold.setMinorTickSpacing(1);
		sliderTrainingLength.setMinorTickSpacing(1);
		sliderMaxSize.setMinorTickSpacing(1);
		sliderMaxDepth.setMinorTickSpacing(1);
		// initialize labels for slider values
		currentMapUnitsInRow = new JLabel(Integer.toString(sliderMapUnitsInRow
				.getValue()), JLabel.CENTER);
		currentMapUnitsInColumn = new JLabel(Integer
				.toString(sliderMapUnitsInColumn.getValue()), JLabel.CENTER);
		currentGrowThreshold = new JLabel(Double.toString(sliderGrowThreshold
				.getValue()
				/ (double) 100), JLabel.CENTER);
		currentExpandThreshold = new JLabel(Double
				.toString(sliderExpandThreshold.getValue() / (double) 100),
				JLabel.CENTER);
		currentTrainingLength = new JLabel(Integer
				.toString(sliderTrainingLength.getValue()), JLabel.CENTER);
		Integer intSize = sliderMaxSize.getValue();
		Integer intDepth = sliderMaxDepth.getValue();
		currentMaxSize = new JLabel(
				intSize.intValue() == GHSOM.NA_MAX_SIZE ? "N/A" : Integer
						.toString(intSize), JLabel.CENTER);
		currentMaxDepth = new JLabel(
				intDepth.intValue() == GHSOM.NA_MAX_DEPTH ? "N/A" : Integer
						.toString(intDepth), JLabel.CENTER);

		// init combo boxes
		cbInitMethod = new JComboBox(initMethodsString);
		cbInitMethod.setSelectedIndex(SOM.INIT_RANDOM);
		// assign change listeners
		sliderMapUnitsInRow.addChangeListener(this);
		sliderMapUnitsInColumn.addChangeListener(this);
		sliderGrowThreshold.addChangeListener(this);
		sliderExpandThreshold.addChangeListener(this);
		sliderTrainingLength.addChangeListener(this);
		sliderMaxDepth.addChangeListener(this);
		sliderMaxSize.addChangeListener(this);

		notCircular = new JRadioButton(CIRCULAR_NOT);
		notCircular.setActionCommand(CIRCULAR_NOT);
		allCircular = new JRadioButton(CIRCULAR_ALL);
		allCircular.setActionCommand(CIRCULAR_ALL);
		firstCircular = new JRadioButton(CIRCULAR_FIRST);
		firstCircular.setActionCommand(CIRCULAR_FIRST);

		circularGroup = new ButtonGroup();
		circularGroup.add(notCircular);
		circularGroup.add(allCircular);
		circularGroup.add(firstCircular);
		circularGroup.setSelected(notCircular.getModel(), true);

		notCircular.addActionListener(this);
		allCircular.addActionListener(this);
		firstCircular.addActionListener(this);

		orientated = new JCheckBox("SubSOM-Orientierung");
		orientated.setSelected(true);
		orientated.addActionListener(this);

		// init grid layout
		gridLayout.setRows(12);
		gridLayout.setVgap(0);
		// assign layout
		panel.setLayout(gridLayout);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// add UI-elements
		getContentPane().add(panel);
		panel.add(new JLabel("Map Units per Row at Initialization"));
		panel.add(sliderMapUnitsInRow);
		panel.add(currentMapUnitsInRow);
		panel.add(new JLabel("Map Units per Column at Initialization"));
		panel.add(sliderMapUnitsInColumn);
		panel.add(currentMapUnitsInColumn);
		panel.add(new JLabel("Growing Threshold"));
		panel.add(sliderGrowThreshold);
		panel.add(currentGrowThreshold);
		panel.add(new JLabel("Expansion Threshold"));
		panel.add(sliderExpandThreshold);
		panel.add(currentExpandThreshold);
		panel.add(new JLabel("Maximum Size of Single Map"));
		panel.add(sliderMaxSize);
		panel.add(currentMaxSize);
		panel.add(new JLabel("Maximum Depth"));
		panel.add(sliderMaxDepth);
		panel.add(currentMaxDepth);
		panel.add(new JLabel("Initialization Method"));
		panel.add(cbInitMethod);
		panel.add(new JLabel());
		panel.add(notCircular);
		panel.add(firstCircular);
		panel.add(allCircular);
		panel.add(new JLabel());
		panel.add(orientated);
		panel.add(new JLabel());
		panel.add(new JLabel("Training Length in Epochs"));
		panel.add(sliderTrainingLength);
		panel.add(currentTrainingLength);

		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(btnCreateSOM);
		panel.add(btnDefault);
		panel.add(btnCancel);
		// set default look and feel
		this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		this.setResizable(false);
	}

	/**
	 * Returns the number of map units per row the user has selected with the
	 * slider.
	 * 
	 * @return the number of map units per row selected with the slider
	 */
	public int getMapUnitsInRow() {
		return sliderMapUnitsInRow.getValue();
	}

	/**
	 * Returns the number of map units per column the user has selected with the
	 * slider.
	 * 
	 * @return the number of map units per column selected with the slider
	 */
	public int getMapUnitsInColumn() {
		return sliderMapUnitsInColumn.getValue();
	}

	/**
	 * Returns the chosen initialisation method as an int as specified in the
	 * class SOM.
	 * 
	 * @return the chosen initialisation method as an int as specified in the
	 *         class SOM (i.e. SOM.GRADIENT)
	 * 
	 * created by MSt
	 */
	public int getInitMethod() {
		int index = cbInitMethod.getSelectedIndex();
		return initMethods[index];
	}

	/**
	 * Returns the growThreshold the user has selected with the slider.
	 * 
	 * @return growThreshold
	 */
	public double getGrowThreshold() {
		return this.sliderGrowThreshold.getValue() / (double) 100;
	}

	/**
	 * Returns the growThreshold the user has selected with the slider.
	 * 
	 * @return growThreshold
	 */
	public double getExpandThreshold() {
		return this.sliderExpandThreshold.getValue() / (double) 100;
	}

	/**
	 * Returns the training length in epochs the user has selected with the
	 * slider.
	 * 
	 * @return the number of epochs the training is performed
	 */
	public int getTrainingLength() {
		return this.sliderTrainingLength.getValue();
	}

	/**
	 * Returns the maximum size of a map of the GHSOM the user has selected with
	 * the slider.
	 * 
	 * @return the maximum size of a map of the GHSOM
	 */
	public int getMaxSize() {
		return this.sliderMaxSize.getValue();
	}

	/**
	 * Returns the maximum depth of the GHSOM the user has selected with the
	 * slider.
	 * 
	 * @return the maximum depth of the GHSOM
	 */
	public int getMaxDepth() {
		return this.sliderMaxDepth.getValue();
	}

	public boolean isCircular() {
		return !this.notCircular.isSelected();
	}

	public boolean isFirstCircular() {
		return this.firstCircular.isSelected();
	}

	public boolean isOrientated() {
		return this.orientated.isSelected();
	}

	/**
	 * Sets the values of the configuration dialog to the ones specified by the
	 * given GHSOMConfig-instance.
	 * 
	 * @param ghsomCfg
	 *            the GHSOMConfig-instance containing the values for the dialog
	 */
	public void setConfig(GHSOMConfig ghsomCfg) {
		if (ghsomCfg != null) {
			sliderMapUnitsInRow.setValue(ghsomCfg.getMapUnitsInRow());
			sliderMapUnitsInColumn.setValue(ghsomCfg.getMapUnitsInColumn());
			sliderGrowThreshold
					.setValue((int) (ghsomCfg.getGrowThreshold() * 100));
			sliderExpandThreshold
					.setValue((int) (ghsomCfg.getExpandThreshold() * 100));
			cbInitMethod.setSelectedIndex(ghsomCfg.getInitMethod());
			sliderTrainingLength.setValue(ghsomCfg.getTrainingLength());
			sliderMaxSize.setValue(ghsomCfg.getMaxSize());
			sliderMaxDepth.setValue(ghsomCfg.getMaxDepth());
			orientated.setSelected(ghsomCfg.isOrientated());
			if (ghsomCfg.isCircular()) {
				if (ghsomCfg.isOnlyFirstCircular())
					circularGroup.setSelected(firstCircular.getModel(), true);
				else
					circularGroup.setSelected(allCircular.getModel(), true);
			} else
				circularGroup.setSelected(notCircular.getModel(), true);

		}
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		// set label according to slider which has been changed
		JSlider source = (JSlider) e.getSource();
		if (source == sliderMapUnitsInRow)
			this.currentMapUnitsInRow.setText(Integer.toString(source
					.getValue()));
		if (source == sliderMapUnitsInColumn)
			this.currentMapUnitsInColumn.setText(Integer.toString(source
					.getValue()));
		if (source == sliderGrowThreshold)
			this.currentGrowThreshold.setText(Double.toString(source.getValue()
					/ (double) 100));
		if (source == sliderExpandThreshold)
			this.currentExpandThreshold.setText(Double.toString(source
					.getValue()
					/ (double) 100));
		if (source == sliderTrainingLength)
			this.currentTrainingLength.setText(Integer.toString(source
					.getValue()));
		if (source == sliderMaxSize) {
			if (source.getValue() == GHSOM.NA_MAX_SIZE)
				this.currentMaxSize.setText("N/A");
			else
				this.currentMaxSize
						.setText(Integer.toString(source.getValue()));
		}
		if (source == sliderMaxDepth) {
			if (source.getValue() == GHSOM.NA_MAX_DEPTH)
				this.currentMaxDepth.setText("N/A");
			else
				this.currentMaxDepth.setText(Integer
						.toString(source.getValue()));
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		// if button "Cancel" was pressed, close dialog
		if (actionEvent.getSource() == btnCancel) {
			confirmOperation = false;
			dispose();
		}
		// if button "Default Values" was pressed, load default values
		if (actionEvent.getSource() == btnDefault) {
			// set default values based on the heuristic function
			// setSOMSizeEstimation in the SOM-class
			this.setConfig(new GHSOMDefaultConfig());
//			this.setConfig(new GHSOMConfig(this.mapUnitsInRowDefault,
//					this.mapUnitsInColumnDefault, SOM.INIT_RANDOM, TRAIN_LENGTH,
//					this.growThresholdDefault, this.expandThresholdDefault,
//					this.maxSizeDefault, this.maxDepthDefault, false, false,
//					true));
		}
		if (actionEvent.getSource() == btnCreateSOM) {
			confirmOperation = true;
			dispose();
		}
	}
	
	// test for standard values
//	
//	public boolean isMapUnitsInRowDefault(int mapUnitsInRow) {
//		return (mapUnitsInRow == this.mapUnitsInRowDefault);
//	}
//	
//	public boolean isMapUnitsInColumnDefault(int mapUnitsInColumn) {
//		return (mapUnitsInColumn == this.mapUnitsInColumnDefault);
//	}
//	
//	public boolean isInitMethodDefault(int initMethod) {
//		return (initMethod == SOM.INIT_RANDOM);
//	}
//	
//	public boolean isTrainLengthDefault(int trainLength) {
//		return (trainLength == TRAIN_LENGTH);
//	}
//	
//	public boolean isGrowThresholdDefault(double growThreshold ) {
//		return (growThreshold == this.growThresholdDefault);
//	}
//	
//	public boolean isExpandThresholdDefault(double expandThreshold) {
//		return (expandThreshold == this.expandThresholdDefault);
//	}
//
//	public boolean isMaxSizeDefault(int maxSize) {
//		return (maxSize == this.maxSizeDefault);
//	}
//	
//	public boolean isMaxDepthDefault(int maxDepth) {
//		return (maxDepth == this.maxDepthDefault);
//	}
	
}
