/*
 * Created on 15.02.2005
 */
package comirva.ui;

import comirva.config.SOMConfig;
import comirva.config.defaults.SOMDefaultConfig;
import comirva.mlearn.SOM;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a SOM.
 * It is shown when the user wished to create a SOM.
 * 
 * @author Markus Schedl
 */
public class SOMCreationDialog extends JDialog implements ActionListener, ChangeListener {

	// UI-elements
	private JButton btnCreateSOM = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDefault = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderMapUnitsInRow;
	private JSlider sliderMapUnitsInColumn;
	private JSlider sliderTrainingLength;
	private JLabel currentMapUnitsInRow;
	private JLabel currentMapUnitsInColumn;
	private JComboBox cbInitMethod;
	private JLabel currentTrainingLength;
	private JComboBox cbTrainingMethod;
	private JCheckBox circular;
    private GridLayout gridLayout = new GridLayout();
    // default values for the number of map units in each row and column based on the
    // heuristic function setSOMSizeEstimation in the SOM-class
    private int mapUnitsInRowDefault;
    private int mapUnitsInColumnDefault;
    // a Vector and an int array storing the possible initialisation methods
    // the Vector stores the String-representation, the int[] the numbers i.e. SOM.RANDOM
    int[] initMethods;
    Vector initMethodsString;

    // flag indicating for the creating instance, if the dialog was closed by clicking of "Create SOM"
    public boolean confirmOperation;
    // Vector containing possible training methods
	Vector trainingMethods = new Vector();
    
    /**
     * Creates a new instance of the SOM-parameter dialog and initializes it.
     * Furthermore, the default values for the displayed sliders are passed as 
     * calculated by the heuristic function in the SOM-class.
     * 
     * @param parent					the Frame of the parent window where the dialog should be displayed
     * @param mapUnitsInRowDefault		the default number of map units per row (used for the "Default Values"-button)
     * @param mapUnitsInColumnDefault	the default number of map units per column (used for the "Default Values"-button)
     * 
     * @see comirva.mlearn.SOM#setSOMSizeEstimation()
     */
    public SOMCreationDialog(Frame parent, int mapUnitsInRowDefault, int mapUnitsInColumnDefault) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            // store default values
            this.mapUnitsInRowDefault = mapUnitsInRowDefault;
            this.mapUnitsInColumnDefault = mapUnitsInColumnDefault;
            // different training methods
            this.trainingMethods.add("Sequential");
            this.trainingMethods.add("Batch");
            // fill possible init-methods as defined in SOM into the array
            // the pairs have to match for every index. for example:
            // if initMethods[0] = SOM.GRADIENT, then initMethodsString[1] must be "gradient init"
            this.initMethods = new int[] {SOM.INIT_RANDOM, SOM.INIT_GRADIENT, SOM.INIT_LINEAR, SOM.INIT_SLC};
            this.initMethodsString = new Vector();
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
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initSOMCreationDialog() {
	   	SOMDefaultConfig somDefault = new SOMDefaultConfig(mapUnitsInRowDefault, mapUnitsInColumnDefault);
    	this.setTitle("SOM - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateSOM.setText("Create SOM");
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
    	sliderMapUnitsInRow = new JSlider(1, 100, mapUnitsInRowDefault);
    	sliderMapUnitsInColumn = new JSlider(1, 100, mapUnitsInColumnDefault);
    	sliderTrainingLength = new JSlider(1, 20, somDefault.getTrainingLength());
    	sliderMapUnitsInRow.setMinorTickSpacing(1);
    	sliderMapUnitsInColumn.setMinorTickSpacing(1);
    	sliderTrainingLength.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentMapUnitsInRow = new JLabel(Integer.toString(sliderMapUnitsInRow.getValue()), JLabel.CENTER);
    	currentMapUnitsInColumn = new JLabel(Integer.toString(sliderMapUnitsInColumn.getValue()), JLabel.CENTER);
    	currentTrainingLength = new JLabel(Integer.toString(sliderTrainingLength.getValue()), JLabel.CENTER);
    	// init combo boxes
    	cbTrainingMethod = new JComboBox(trainingMethods);  
    	cbTrainingMethod.setSelectedIndex(SOM.TRAIN_BATCH);
    	cbInitMethod = new JComboBox(initMethodsString);
    	cbInitMethod.setSelectedIndex(SOM.INIT_RANDOM);
    	// assign change listeners
    	sliderMapUnitsInRow.addChangeListener(this);
    	sliderMapUnitsInColumn.addChangeListener(this);
    	sliderTrainingLength.addChangeListener(this);
    	// create and init checkBox 
    	circular = new JCheckBox("Circular");
    	circular.setMnemonic(KeyEvent.VK_I);
    	circular.setSelected(somDefault.isCircular());
    	circular.addActionListener(this);
    	// init grid layout
    	gridLayout.setRows(9);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("Map Units per Row"));
    	panel.add(sliderMapUnitsInRow);
    	panel.add(currentMapUnitsInRow);   	
    	panel.add(new JLabel("Map Units per Column"));
    	panel.add(sliderMapUnitsInColumn);
    	panel.add(currentMapUnitsInColumn);
    	panel.add(new JLabel("Initialization Method"));
    	panel.add(cbInitMethod);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel("Training Method"));
    	panel.add(cbTrainingMethod);
    	panel.add(new JLabel());
    	panel.add(new JLabel("Training Length in Epochs"));
    	panel.add(sliderTrainingLength);
    	panel.add(currentTrainingLength);
    	panel.add(new JLabel());
    	panel.add(circular);
    	panel.add(new JLabel());
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
   	 * Returns the number of map units per row the user has selected with the slider.
   	 * 
   	 * @return the number of map units per row selected with the slider 
   	 */
   	public int getMapUnitsInRow() {
   		return sliderMapUnitsInRow.getValue();
   	}
   	
   	/**
   	 * Returns the number of map units per column the user has selected with the slider.
   	 * 
   	 * @return the number of map units per column selected with the slider 
   	 */
   	public int getMapUnitsInColumn() {
   		return sliderMapUnitsInColumn.getValue();
   	}
   	
   	/**
	 * Returns the chosen initialisation method as an int as specified in the class SOM.
	 * 
	 * @return the chosen initialisation method as an int as specified in the class SOM (i.e. SOM.GRADIENT)
	 *
	 * created by MSt */
	public int getInitMethod(){
	   	int index = cbInitMethod.getSelectedIndex();
	   	return initMethods[index];
	}
	
	/**
	 * Returns the method used for training the user has selected in the combo box.
	 * 
	 * @return	an int indicating the training method (for a list, see @link comirva.mlearn.SOM)
	 * @see comirva.mlearn.SOM
	 */
	public int getTrainingMethod() {
		return this.cbTrainingMethod.getSelectedIndex();
	}
	
	/**
	 * Returns the training length in epochs the user has selected with the slider.
	 * 
	 * @return	the number of epochs the training is performed
	 */
	public int getTrainingLength() {
		return this.sliderTrainingLength.getValue();
	}
	
	public boolean isCircular() {
		return this.circular.isSelected();
	}
   	
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given SOMConfig-instance.
   	 * 
   	 * @param somCfg the SOMConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(SOMConfig somCfg) {
   		if (somCfg != null) {
   			sliderMapUnitsInRow.setValue(somCfg.getMapUnitsInRow());
   			sliderMapUnitsInColumn.setValue(somCfg.getMapUnitsInColumn());
   			cbTrainingMethod.setSelectedIndex(somCfg.getTrainingMethod());
   			cbInitMethod.setSelectedIndex(somCfg.getInitMethod());
   			sliderTrainingLength.setValue(somCfg.getTrainingLength());
   			circular.setSelected(somCfg.isCircular());
   		}
   	}

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderMapUnitsInRow)
	        this.currentMapUnitsInRow.setText(Integer.toString(source.getValue()));
	    if (source == sliderMapUnitsInColumn)
	    	this.currentMapUnitsInColumn.setText(Integer.toString(source.getValue()));
	    if (source == sliderTrainingLength)
	    	this.currentTrainingLength.setText(Integer.toString(source.getValue()));
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
    		// set default values based on the heuristic function setSOMSizeEstimation in the SOM-class
    		//this.setConfig(new SOMConfig(this.mapUnitsInRowDefault, this.mapUnitsInColumnDefault, SOM.INIT_RANDOM, SOM.TRAIN_BATCH, SOMConfig.DEFAULT_TRAINING_LENGTH, SOMConfig.DEFAULT_CIRCULAR));
    		this.setConfig(new SOMDefaultConfig(this.mapUnitsInRowDefault, this.mapUnitsInColumnDefault));
    	}
    	if (actionEvent.getSource() == btnCreateSOM) {
    		confirmOperation = true;
    		dispose();
    	}
    }

	/**
	 * @return the initMethodsString
	 */
	public Vector getInitMethodsString() {
		return initMethodsString;
	}
	
	// tests for standard values
	
	/** returns <code>true</code> if parameter is equal to its standard value
	 *  @param mapUnitsInRow map units in row
	 *  @return if the parameter is the standard value
	 */
	public boolean isMapUnitsInRowDefault(int mapUnitsInRow) {
		return mapUnitsInRow == mapUnitsInRowDefault;
	}
	
	/** returns <code>true>/code> if parameter is equal to its standard value
	 * @param mapUnitsInColumn map units in column
	 * @return if the parameter is the standard value
	 */
	public boolean isMapUnitsInColumnDefault(int mapUnitsInColumn) {
		return mapUnitsInColumn == mapUnitsInColumnDefault;
	}
	
//	/** returns <code>true>/code> if parameter is equal to its standard value
//	 * @param initMethod som init method
//	 * @return if the parameter is the standard value
//	 */
//	public boolean isInitMethodDefault(int initMethod) {
//		return initMethod == SOM.INIT_RANDOM;
//	}
//	
//	/** returns <code>true>/code> if parameter is equal to its standard value
//	 * @param trainingMethod som training method
//	 * @return if the parameter is the standard value
//	 */
//	public boolean isTrainingMethodDefault(int trainingMethod) {
//		return trainingMethod == SOM.TRAIN_BATCH;
//	}
//	
//	/** returns <code>true>/code> if parameter is equal to its standard value
//	 * @param trainingLength training length
//	 * @return if the parameter is the standard value
//	 */
//	public boolean isTrainingLengthDefault(int trainingLength) {
//		return trainingLength == SOMConfig.DEFAULT_TRAINING_LENGTH;
//	}
//	
//	/** returns <code>true>/code> if parameter is equal to its standard value
//	 * @param isCircular is circular
//	 * @return if the parameter is the standard value
//	 */
//	public boolean isCircularDefault(boolean isCircular) {
//		return (isCircular == SOMConfig.DEFAULT_CIRCULAR);
//	}
}
