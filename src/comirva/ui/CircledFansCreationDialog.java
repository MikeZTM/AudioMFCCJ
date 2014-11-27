/*
 * Created on 01.12.2004
 */
package comirva.ui;

import comirva.config.CircledFansConfig;
import comirva.config.defaults.CircledFansDefaultConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a Circled-Fans-Visualization.
 * It is shown when the user wished to create a Circled-Fans-Visualization. 
 * 
 * @author Markus Schedl
 */
public class CircledFansCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateCF = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
    private JPanel panelMain = new JPanel();
    private JPanel panelSliders = new JPanel();
    private JPanel panelCenterItem = new JPanel();
    private JPanel panelRadioButtons = new JPanel();
	private JSlider sliderMaxBarThickness;
	private JSlider sliderMaxDataItemsL0;
	private JSlider sliderMaxDataItemsL1;
	private JSlider sliderAngleFanL1;
	private JCheckBox cbNormalizeData;
	private JLabel currentMaxBarThickness;
	private JLabel currentMaxDataItemsL0;
	private JLabel currentMaxDataItemsL1;
	private JLabel currentAngleFanL1;
    private GridLayout gridLayoutSliders = new GridLayout();
    private BorderLayout borderLayoutMain = new BorderLayout();
    private BorderLayout borderLayoutCenterItem = new BorderLayout();
    private BorderLayout borderLayoutRadioButtons = new BorderLayout();
    private ButtonGroup bg = new ButtonGroup();
    private JRadioButton rbRandomCenter = new JRadioButton("Random Center", true);
    private JRadioButton rbSpecifiedCenter = new JRadioButton("Specified Center", false);
    // JList for list of data items
    private JList jlDataItems = new JList();

	// flag indicating for the creating instance, if the dialog was closed by clicking of "Create Circled Fans"
    public boolean confirmOperation;

    
    /**
     * Creates a new instance of the Circled-Fans-parameter dialog and initializes it.
     * 
     * @param parent				the Frame of the parent window where the dialog should be displayed
     * @param labelsDataItems		a Vector containing the labels for all data items (taken from meta-data or just Integers) 
     */
    public CircledFansCreationDialog(Frame parent, Vector labelsDataItems) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initCFCreationDialog(labelsDataItems);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
    private void initCFCreationDialog(Vector labelsDataItems) {
    	this.setTitle("Circled Fans Visualization - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateCF.setText("Create CF");
    	btnCreateCF.addActionListener(this);
    	btnCreateCF.setMnemonic(KeyEvent.VK_F);
    	// set "Create CF"-button as default
    	this.getRootPane().setDefaultButton(btnCreateCF);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderMaxBarThickness = new JSlider(1, 50, 15);
    	sliderMaxBarThickness.setMinorTickSpacing(1);
    	sliderMaxDataItemsL0 = new JSlider(4, 30, 10);
    	sliderMaxDataItemsL0.setMinorTickSpacing(1);
    	sliderMaxDataItemsL1 = new JSlider(2, 15, 6);
    	sliderMaxDataItemsL1.setMinorTickSpacing(1);
    	sliderAngleFanL1 = new JSlider(10, 360, 60);
    	sliderAngleFanL1.setMinorTickSpacing(1);
    	cbNormalizeData = new JCheckBox("Normalize Data for Every Fan", false);
    	// initialize labels for slider values
    	currentMaxBarThickness = new JLabel(Integer.toString(sliderMaxBarThickness.getValue()), JLabel.CENTER);
    	currentMaxDataItemsL0 = new JLabel(Integer.toString(sliderMaxDataItemsL0.getValue()), JLabel.CENTER);
    	currentMaxDataItemsL1 = new JLabel(Integer.toString(sliderMaxDataItemsL1.getValue()), JLabel.CENTER);
    	currentAngleFanL1 = new JLabel(Integer.toString(sliderAngleFanL1.getValue()), JLabel.CENTER);
    	// initialize labels list
		jlDataItems.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(165, 163, 151)),
				BorderFactory.createEmptyBorder(0,1,0,1)));
		jlDataItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	// add data
    	jlDataItems.setListData(labelsDataItems);
    	// select first element
    	if (labelsDataItems.size() > 0)
    		jlDataItems.setSelectedIndex(0);
		// provide the labels list with a scroll pane
		JScrollPane jscp_labels = new JScrollPane(jlDataItems);
//		jscp_labels.setPreferredSize(new Dimension(200, 250));
		// group radio buttons
		bg.add(rbRandomCenter);
		bg.add(rbSpecifiedCenter);
    	// assign change listeners
    	sliderMaxBarThickness.addChangeListener(this);
    	sliderMaxDataItemsL0.addChangeListener(this);
    	sliderMaxDataItemsL1.addChangeListener(this);
    	sliderAngleFanL1.addChangeListener(this);
    	// init grid layout
    	gridLayoutSliders.setRows(6);
    	gridLayoutSliders.setVgap(0);
    	// assign layout
    	panelMain.setLayout(borderLayoutMain);
    	panelCenterItem.setLayout(borderLayoutCenterItem);
    	panelCenterItem.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	panelRadioButtons.setLayout(borderLayoutRadioButtons);
    	panelRadioButtons.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    	panelSliders.setLayout(gridLayoutSliders);
    	panelSliders.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
    	// add UI-elements
    	getContentPane().add(panelMain);
    	panelMain.add(panelSliders, BorderLayout.EAST);
    	panelMain.add(panelCenterItem, BorderLayout.WEST);
    	panelCenterItem.add(jscp_labels, BorderLayout.SOUTH);
    	panelCenterItem.add(panelRadioButtons, BorderLayout.NORTH);
		panelRadioButtons.add(rbRandomCenter, BorderLayout.NORTH);
		panelRadioButtons.add(rbSpecifiedCenter, BorderLayout.SOUTH);
    	panelSliders.add(new JLabel("<html>Maximum Bar Thickness</html>"));
    	panelSliders.add(sliderMaxBarThickness);
    	panelSliders.add(currentMaxBarThickness);
    	panelSliders.add(new JLabel("<html>Maximum Number of Data Items<br>on Level 0 (Inner Circle)</html>"));
    	panelSliders.add(sliderMaxDataItemsL0);
    	panelSliders.add(currentMaxDataItemsL0);
    	panelSliders.add(new JLabel("<html>Maximum Number of Data Items<br>on Level 1 (Outer Fans)</html>"));
    	panelSliders.add(sliderMaxDataItemsL1);
    	panelSliders.add(currentMaxDataItemsL1);
    	panelSliders.add(new JLabel("<html>Angular Extent for Fans<br>on Level 1</html>"));
    	panelSliders.add(sliderAngleFanL1);
    	panelSliders.add(currentAngleFanL1);
    	panelSliders.add(new JLabel());
    	panelSliders.add(cbNormalizeData);
    	panelSliders.add(new JLabel());
    	panelSliders.add(btnCreateCF);
    	panelSliders.add(btnDefault);
    	panelSliders.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
  
   	/**
   	 * Returns the maximum bar thickness the user has selected with the slider.
   	 * 
   	 * @return the maximum bar thickness selected with the slider 
   	 */
   	public int getMaxBarThickness() {
   		return sliderMaxBarThickness.getValue();
   	}
   	
   	/**
   	 * Returns the maximum number of data items on level 0 the user has selected with the slider.
   	 * 
   	 * @return the maximum number of data items on level 0 selected with the slider 
   	 */
   	public int getMaxDataItemsL0() {
   		return sliderMaxDataItemsL0.getValue();
   	}
   	
   	/**
   	 * Returns the maximum number of data items on level 1 the user has selected with the slider.
   	 * 
   	 * @return the maximum number of data items on level 1 selected with the slider 
   	 */
   	public int getMaxDataItemsL1() {
   		return sliderMaxDataItemsL1.getValue();
   	}
   	
   	/**
   	 * Returns the angular extent for fans on level 1 the user has selected with the slider.
   	 * 
   	 * @return the angular extent for fans on level 1 selected with the slider 
   	 */
   	public int getAngleFanL1() {
   		return sliderAngleFanL1.getValue();
   	}
	/**
	 * Returns whether a random center should be chosen or the center is selected by the user.
	 * 
	 * @return a boolean indicating whether a random element should be picked as center 
	 */
   	public boolean isRandomCenter() {
   		return rbRandomCenter.isSelected();
   	}
   	
   	/**
   	 * Returns the index of the data item which resides in the center of the visualization.
   	 * This data item is either chosen randomly or taken from the user's selection.
   	 * 
   	 * @return	the index of the center data item
   	 */
   	public int getIndexCenter() {
   		// if the center data item is to be chosen randomly, do this
   		if (rbRandomCenter.isSelected()) {
   	        // create a new Random-instance for random values
   			Random rand = new Random();
   	        return rand.nextInt(jlDataItems.getModel().getSize());
   		} else 
   		// is center data item is specified by user, return index of selected label
   		if (rbSpecifiedCenter.isSelected())
   			return jlDataItems.getSelectedIndex();
   		else				// an error occured because neither of the radio buttons is selected -> return index of first element
   			return 0;
   		
   	}
   	
   	/**
   	 * Returns whether the normalized data checkbox is checked or not.
   	 * 
   	 * @return a boolean indicating whether the normalized data checkbox is checked or not
   	 */
   	public boolean isNormalizeData() {
   		return cbNormalizeData.isSelected();
   	}
   	
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given CircledFansConfig-instance.
   	 * 
   	 * @param cfCfg the CircledFansConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(CircledFansConfig cfCfg) {
   		if (cfCfg != null) {
   			sliderMaxBarThickness.setValue(cfCfg.getMaxBarThickness());
   			sliderMaxDataItemsL0.setValue(cfCfg.getMaxDataItemsL0());
   			sliderMaxDataItemsL1.setValue(cfCfg.getMaxDataItemsL1());
   			sliderAngleFanL1.setValue(cfCfg.getAngleFanL1());
   			rbRandomCenter.setSelected(cfCfg.isRandomCenter());
   			rbSpecifiedCenter.setSelected(!cfCfg.isRandomCenter());
   			if (cfCfg.getIndexCenter() >= 0)
   				jlDataItems.setSelectedIndex(cfCfg.getIndexCenter());
   			cbNormalizeData.setSelected(cfCfg.isNormalizeData());
   		}
   	}
   	
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderMaxBarThickness)
	        this.currentMaxBarThickness.setText(Integer.toString(source.getValue()));
    	if (source == sliderMaxDataItemsL0)
	        this.currentMaxDataItemsL0.setText(Integer.toString(source.getValue()));
    	if (source == sliderMaxDataItemsL1)
	        this.currentMaxDataItemsL1.setText(Integer.toString(source.getValue()));
    	if (source == sliderAngleFanL1)
	        this.currentAngleFanL1.setText(Integer.toString(source.getValue()));
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
    		this.setConfig(new CircledFansDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnCreateCF) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
