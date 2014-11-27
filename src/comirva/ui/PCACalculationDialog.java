/*
 * Created on 15.12.2006
 */
package comirva.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

import comirva.config.PCAConfig;

/**
 * This class implements a dialog for specifying the parameters of a PCA-projection.
 * 
 * @author Markus Schedl
 */
public class PCACalculationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCalculatePCA = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderUsedEigenvectors;
	private JLabel currentUsedEigenvectors;
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Calculate"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the PCA-parameter dialog and initializes it.
     * Furthermore, the maximum values for the displayed sliders are set.
     * 
     * @param parent					the Frame of the parent window where the dialog should be displayed
     * @param maxUsedEigenvectors		the maximum number of used Eigenvectors (equals the dimensionality of the input data)
     */
    public PCACalculationDialog(Frame parent, int maxUsedEigenvectors) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initPCADialog(maxUsedEigenvectors);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initPCADialog(int maxUsedEigenvectors) {
    	this.setTitle("PCA - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCalculatePCA.setText("Calculate");
    	btnCalculatePCA.setMnemonic(KeyEvent.VK_A);
    	// set "Calculate"-button as default
    	this.getRootPane().setDefaultButton(btnCalculatePCA);
    	btnCalculatePCA.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	// create and initialize sliders
    	sliderUsedEigenvectors = new JSlider(1, maxUsedEigenvectors, maxUsedEigenvectors);
    	sliderUsedEigenvectors.setMinorTickSpacing(1);
    	sliderUsedEigenvectors.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentUsedEigenvectors = new JLabel(Integer.toString(sliderUsedEigenvectors.getValue()), JLabel.CENTER);
    	// assign change listeners
    	sliderUsedEigenvectors.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(3);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("<html>Number of Used Eigenvectors for<br>PCA-projection</html>"));
    	panel.add(sliderUsedEigenvectors);
    	panel.add(currentUsedEigenvectors);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());    	
    	panel.add(btnCalculatePCA);
    	panel.add(new JLabel());    	
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
  
   	/**
   	 * Returns the number of used Eigenvectors.
   	 * 
   	 * @return the number of used Eigenvectors selected with the slider 
   	 */
   	public int getUsedEigenvectors() {
   		return sliderUsedEigenvectors.getValue();
   	}

   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given PCAConfig-instance.
   	 * 
   	 * @param pcaCfg the PCAConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(PCAConfig pcaCfg) {
   		if (pcaCfg != null) {
   			sliderUsedEigenvectors.setValue(pcaCfg.getUsedEigenvectors());
   		}
   	}

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderUsedEigenvectors)
	        this.currentUsedEigenvectors.setText(Integer.toString(source.getValue()));
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
    	if (actionEvent.getSource() == btnCalculatePCA) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
