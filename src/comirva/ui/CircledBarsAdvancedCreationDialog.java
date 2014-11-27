/*
 * Created on 07.03.2005
 */
package comirva.ui;

import comirva.config.CircledBarsAdvancedConfig;
import comirva.config.defaults.CircledBarsAdvancedDefaultConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a Circled-Bars-Advanced-Visualization.
 * It is shown when the user wished to create a Circled-Bars-Advanced-Visualization. 
 * 
 * @author Markus Schedl
 */
public class CircledBarsAdvancedCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateCBA = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderShowNearestN;
	private JLabel currentShowNearestN;
	private JPanel panelSort = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JRadioButton rbSortByDistance = new JRadioButton("by distance", true);
	private JRadioButton rbSortByMetaData = new JRadioButton ("by meta-data (alphabetically)", false);
	private ButtonGroup bgSort = new ButtonGroup();
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Create CBA"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the Circled-Bars-Advanced-parameter dialog and initializes it.
     * Furthermore, the maximum values for the displayed sliders are set.
     * 
     * @param parent				the Frame of the parent window where the dialog should be displayed
     * @param maxShowNearestN		the maximum number of nearest data items that can be shown (equals total number of item - 1) 
     */
    public CircledBarsAdvancedCreationDialog(Frame parent, int maxShowNearestN) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initCBACreationDialog(maxShowNearestN);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     *  
     */
   private void initCBACreationDialog(int maxShowNearestN) {
    	this.setTitle("Circled Bars Advanced Visualization - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateCBA.setText("Create CBA");
    	btnCreateCBA.setMnemonic(KeyEvent.VK_B);
    	// set "Create SDH"-button as default
    	this.getRootPane().setDefaultButton(btnCreateCBA);
    	btnCreateCBA.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
       	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderShowNearestN = new JSlider(3, maxShowNearestN, 15);
    	sliderShowNearestN.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentShowNearestN = new JLabel(Integer.toString(sliderShowNearestN.getValue()), JLabel.CENTER);
    	// initialize button group for sorting criteria
    	panelSort.add(rbSortByDistance);
    	panelSort.add(rbSortByMetaData);
    	bgSort.add(rbSortByDistance);
    	bgSort.add(rbSortByMetaData);
    	rbSortByDistance.setMnemonic(KeyEvent.VK_D);
    	rbSortByMetaData.setMnemonic(KeyEvent.VK_M);
    	// assign change listeners
    	sliderShowNearestN.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(4);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("<html>Number of Data Items to Display<br>(Only the Nearest N Items are Shown)</html>"));
    	panel.add(sliderShowNearestN);
    	panel.add(currentShowNearestN);
    	panel.add(new JLabel("Sort Data Items"));
    	panel.add(panelSort);
    	panel.add(new JLabel());    	
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnCreateCBA);
    	panel.add(btnDefault);
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
  
   	/**
   	 * Returns the number of nearest data items the user wishes to display.
   	 * 
   	 * @return the number of data items selected with the slider 
   	 */
   	public int getShowNearestN() {
   		return sliderShowNearestN.getValue();
   	}
   	
   	/**
   	 * Returns whether the data items are to be sorted by their distance 
   	 * to the selected data item (or alphabetically by their meta-data name).
   	 * 
   	 * @return 	<code>true</code> if the data items should be sorted by distance,
   	 * <code>false</code> if they are to be sorted by meta-data names
   	 */
   	public boolean isSortByDistance() {
   		return bgSort.isSelected(rbSortByDistance.getModel());
   	}
   	
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given CircledBarsAdvancedConfig-intance.
   	 * 
   	 * @param cbaCfg the CircledBarsAdvancedConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(CircledBarsAdvancedConfig cbaCfg) {
   		if (cbaCfg != null) {
   			sliderShowNearestN.setValue(cbaCfg.getShowNearestN());
   			rbSortByDistance.setSelected(cbaCfg.isSortByDistance());
   			rbSortByMetaData.setSelected(!cbaCfg.isSortByDistance());
   		}
   	}

   	/**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderShowNearestN)
	        this.currentShowNearestN.setText(Integer.toString(source.getValue()));
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
    		this.setConfig(new CircledBarsAdvancedDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnCreateCBA) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
