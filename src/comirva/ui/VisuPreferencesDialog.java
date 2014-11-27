/*
 * Created on 05.07.2005
 */
package comirva.ui;

import comirva.config.VisuPreferences;
import comirva.config.defaults.VisuDefaultPreferences;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * This class implements a dialog for specifying some
 * preferences of the visualization pane.
 * 
 * @author Markus Schedl
 */
/**
 * @author Markus
 *
 */
public class VisuPreferencesDialog extends JDialog implements ActionListener {
	// UI-elements
	private JButton btnOK = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnColor = new JButton();
	private JCheckBox cbEnableEPS = new JCheckBox();
	private JPanel panel = new JPanel();
	private SpinnerNumberModel spinModelBorderSize = new SpinnerNumberModel(50,0,200,1);
	private JSpinner spinBorderSize = new JSpinner(spinModelBorderSize);
	private SpinnerNumberModel spinModelLabelFontSize = new SpinnerNumberModel(10,4,72,1);
	private JSpinner spinLabelFontSize = new JSpinner(spinModelLabelFontSize);
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Create SDH"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the visualization preferences dialog and initializes it.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     */
    public VisuPreferencesDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initVisuPreferencesDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initVisuPreferencesDialog() {
    	this.setTitle("Visualization Preferences");
    	cbEnableEPS.setHorizontalAlignment(JCheckBox.CENTER);
    	// assign text to buttons, set name and assign action listener
    	btnOK.setText("OK");
    	btnOK.setMnemonic(KeyEvent.VK_O);
    	// set "OK"-button as default
    	this.getRootPane().setDefaultButton(btnOK);
    	btnOK.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
       	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// assign default visu background color as background color of the color chooser button
    	btnColor.setBackground(new Color(250,250,255));
    	btnColor.addActionListener(this);
    	// init grid layout
    	gridLayout.setRows(6);
    	gridLayout.setVgap(5);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("Background Color"));
    	panel.add(new JLabel());
    	panel.add(btnColor);
    	panel.add(new JLabel("Border Size"));
    	panel.add(new JLabel());
    	panel.add(spinBorderSize);    	
    	panel.add(new JLabel("Font Size for Labels"));
    	panel.add(new JLabel());
    	panel.add(spinLabelFontSize);    	
    	panel.add(new JLabel("Enable EPS-Output"));
    	panel.add(new JLabel());
    	panel.add(cbEnableEPS);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnOK);
    	panel.add(btnDefault);
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
  
   	/**
   	 * Returns the background color of the visualization pane. 
   	 * 
   	 * @return the Color that defines the background color the user has selected.
   	 */
   	public Color getBackgroundColor() {
   		return btnColor.getBackground();
   	}
   	
   	/** 
   	 * Returns the border size of the visualization pane.
   	 * 
   	 * @return the border size of the visualization pane the user has selected
   	 */
   	public int getBorderSize() {
   		return ((Integer)this.spinModelBorderSize.getValue()).intValue();
   	}
   	
   	/**
   	 * Returns the font size for drawing labels within the visualization pane.
   	 * 
   	 * @return the font size used for drawing labels and other text the used has selected
   	 */
   	public int getLabelFontSize() {
   		return ((Integer)this.spinModelLabelFontSize.getValue()).intValue();
   	}
   	
   	/**
	 * Returns whether the EPS output option is enabled. 
	 * 
	 * @return	true, if EPS-output is enabled; false if disabled (according to the user's selection)
	 */
   	public boolean isEnableEPS() {
   		return this.cbEnableEPS.isSelected();
   	}
   	
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given VisuPreferences-instance.
   	 * 
   	 * @param visuPrefs the VisuPreferences-instance containing the values for the dialog
   	 */
   	public void setConfig(VisuPreferences visuPrefs) {
   		if (visuPrefs != null) {
   	    	btnColor.setBackground(visuPrefs.getBackgroundColor());
   	    	spinModelBorderSize.setValue(new Integer(visuPrefs.getBorderSize()));
   	    	spinLabelFontSize.setValue(new Integer(visuPrefs.getLabelFontSize()));
   	    	cbEnableEPS.setSelected(visuPrefs.isEnableEPS());
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
    		this.setConfig(new VisuDefaultPreferences());
    	}
    	if (actionEvent.getSource() == btnOK) {
    		confirmOperation = true;
    		dispose();
    	}
    	// if button for color selection was pressed, open a JColorChooser
    	if (actionEvent.getSource() == btnColor) {
    		// show a color chooser
    		Color selectedColor = JColorChooser.showDialog(this, "Background Color", btnColor.getBackground());
    		// if user has selected a color, apply it to button
    		if (selectedColor != null) {
    			this.btnColor.setBackground(selectedColor);
    		}
    	}
    }

}