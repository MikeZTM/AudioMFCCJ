/*
 * Created on 09.03.2005
 */
package comirva.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

import comirva.config.DataMatrixNormalizeConfig;
import comirva.config.defaults.DataMatrixNormalizeDefaultConfig;


/**
 * This class implements a dialog for normalizing a data matrix.
 * 
 * @author Markus Schedl
 */
public class DataMatrixNormalizeDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnNormalize = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderLowerBound = new JSlider(-100,100,0);
    private JSlider sliderUpperBound = new JSlider(-100,100,10);
    private JLabel currentLowerBound;
    private JLabel currentUpperBound;
    private JPanel panelMethod = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JRadioButton rbLinear = new JRadioButton("Linear", true);
    private JRadioButton rbLogarithmic = new JRadioButton("Logarithmic", false);
    private ButtonGroup bgMethod = new ButtonGroup();
    private JPanel panelScope = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JRadioButton rbMatrix = new JRadioButton("Complete Matrix", true);
    private JRadioButton rbRow = new JRadioButton("Per Row", false);
    private JRadioButton rbColumn = new JRadioButton("Per Column", false);
    private ButtonGroup bgScope = new ButtonGroup();
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Rename"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the Normalization-dialog and initializes it.
     * Furthermore, the original name of the matrix is passed to be shown as default.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     */
    public DataMatrixNormalizeDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initRenameDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and text fields, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initRenameDialog() {
   		this.setTitle("Normalize Data Matrix");
    	// assign text to buttons, set name and assign action listener
    	btnNormalize.setText("Normalize");
    	btnNormalize.setMnemonic(KeyEvent.VK_N);
    	// set "Normalize"-button as default
    	this.getRootPane().setDefaultButton(btnNormalize);
    	btnNormalize.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderLowerBound.setMinorTickSpacing(1);
    	sliderUpperBound.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentLowerBound = new JLabel(Double.toString((double)sliderLowerBound.getValue()/10), JLabel.CENTER);
    	currentUpperBound = new JLabel(Double.toString((double)sliderUpperBound.getValue()/10), JLabel.CENTER);
    	// initialize button group for normalization method
    	panelMethod.add(rbLinear);
    	panelMethod.add(rbLogarithmic);
    	bgMethod.add(rbLinear);
    	bgMethod.add(rbLogarithmic);
    	rbLinear.setMnemonic(KeyEvent.VK_L);
    	rbLogarithmic.setMnemonic(KeyEvent.VK_O);
    	// initialize button group for normalization scope
    	panelScope.add(rbMatrix);
    	panelScope.add(rbRow);
    	panelScope.add(rbColumn);
    	bgScope.add(rbMatrix);
    	bgScope.add(rbRow);
    	bgScope.add(rbColumn);
    	rbMatrix.setMnemonic(KeyEvent.VK_M);
    	rbRow.setMnemonic(KeyEvent.VK_R);
    	rbColumn.setMnemonic(KeyEvent.VK_U);
    	// assign change listeners
    	sliderLowerBound.addChangeListener(this);
    	sliderUpperBound.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(6);
    	gridLayout.setVgap(0);
    	gridLayout.setHgap(10);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("Lower Bound"));
    	panel.add(sliderLowerBound);
    	panel.add(currentLowerBound);
    	panel.add(new JLabel("Upper Bound"));
    	panel.add(sliderUpperBound);
    	panel.add(currentUpperBound);
    	panel.add(new JLabel("Normalization Scope"));
    	panel.add(panelScope);
    	panel.add(new JLabel());
    	panel.add(new JLabel("Normalization Method"));
    	panel.add(panelMethod);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnNormalize);
    	panel.add(btnDefault);
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }

	/**
	 * Returns the lower bound of the projection range the used has selected with the slider.
	 * 
	 * @return	the lower bound of the projection range
	 */
	public double getLowerBound() {
		return sliderLowerBound.getValue()/10;
	}
	/**
	 * Returns the upper bound of the projection range the used has selected with the slider.
	 * 
	 * @return	the upper bound of the projection range
	 */
	public double getUpperBound() {
		return sliderUpperBound.getValue()/10;
	}
	/**
	 * Returns whether the normalization should be performed linearly or logarithmically according to the user's selection.
	 * 
	 * @return	a boolean indicating whether the normalization should be performed linearly or logarithmically
	 */
	public boolean isLinear() {
		return rbLinear.isSelected();
	}
	/**
	 * Returns the normalization scope the user has selected (complete matrix, normalization for each row, normalization for each column).
	 * 
	 * @return the scope of the normalization 
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_MATRIX
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_ROW
	 * @see comirva.config.DataMatrixNormalizeConfig#SCOPE_PER_COLUMN
	 */
	public int getScope() {
		if (rbRow.isSelected())
			return DataMatrixNormalizeConfig.SCOPE_PER_ROW;
		else if (rbColumn.isSelected())
			return DataMatrixNormalizeConfig.SCOPE_PER_COLUMN;
		else
			return DataMatrixNormalizeConfig.SCOPE_MATRIX;
	}

  	/**
  	 * Sets the values of the configuration dialog to the ones specified by the
  	 * given DataMatrixNormalizeConfig-instance.
  	 * 
  	 * @param dmnCfg the DataMatrixNormalizeConfig-instance containing the values for the dialog
  	 */
  	public void setConfig(DataMatrixNormalizeConfig dmnCfg) {
  		if (dmnCfg != null) {
  			sliderLowerBound.setValue((int)(dmnCfg.getLowerBound()*10));
  			sliderUpperBound.setValue((int)(dmnCfg.getUpperBound()*10));
  			rbLinear.setSelected(dmnCfg.isLinear());
  			rbLogarithmic.setSelected(!dmnCfg.isLinear());
  			if (dmnCfg.getScope() == DataMatrixNormalizeConfig.SCOPE_MATRIX)
  				rbMatrix.setSelected(true);
  			if (dmnCfg.getScope() == DataMatrixNormalizeConfig.SCOPE_PER_ROW)
  				rbRow.setSelected(true);
  			if (dmnCfg.getScope() == DataMatrixNormalizeConfig.SCOPE_PER_COLUMN)
  				rbColumn.setSelected(true);
  		}
  	}
  	
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderLowerBound) {
	        this.currentLowerBound.setText(Double.toString((double)source.getValue()/10));
	        // make sure that lower bound is below upper bound
	        if (sliderLowerBound.getValue() > sliderUpperBound.getValue())
	        	sliderUpperBound.setValue(sliderLowerBound.getValue());
    	}
	    if (source == sliderUpperBound) {
	    	this.currentUpperBound.setText(Double.toString((double)source.getValue()/10));
	        // make sure that upper bound is above lower bound
	        if (sliderUpperBound.getValue() < sliderLowerBound.getValue())
	        	sliderLowerBound.setValue(sliderUpperBound.getValue());	    	
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
    		// set default values
    		this.setConfig(new DataMatrixNormalizeDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnNormalize) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
