/*
 * Created on 28.12.2005
 */
package comirva.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import comirva.config.ETPLoaderConfig;

/**
 * This class implements a dialog for specifying which
 * data from an Entity Term Profile that is to
 * be loaded from an XML-file are to be extracted.
 * 
 * @author Markus Schedl
 */
public class ETPLoaderDialog extends JDialog implements ActionListener {
	// UI-elements
	private JButton btnOK = new JButton();
	private JPanel panel = new JPanel();
	private JCheckBox cbTerms = new JCheckBox("Term List", null, true);
	private JCheckBox cbDocPaths = new JCheckBox("Paths to Documents", null, true);
	private JCheckBox cbTO = new JCheckBox("Term Occurrences", null, true);
	private JCheckBox cbTF = new JCheckBox("Term Frequencies", null, true);
	private JCheckBox cbDF = new JCheckBox("Document Frequencies", null, true);
	private JCheckBox cbTFxIDF = new JCheckBox("TFxIDF", null, true);
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Create SDH"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the ETPLoader-dialog and initializes it.
     * 
     * @param parent					the Frame of the parent window where the dialog should be displayed
     */
    public ETPLoaderDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            // init UI
            initETPLoaderDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initETPLoaderDialog() {
    	this.setTitle("ETP Loader - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnOK.setText("OK");
    	btnOK.setMnemonic(KeyEvent.VK_O);
    	// set "OK"-button as default
    	this.getRootPane().setDefaultButton(btnOK);
    	btnOK.addActionListener(this);
    	// init grid layout
    	gridLayout.setRows(11);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// assign key shortcuts to checkboxes
    	cbTerms.setMnemonic(KeyEvent.VK_T);
    	cbDocPaths.setMnemonic(KeyEvent.VK_P);
    	cbTO.setMnemonic(KeyEvent.VK_C);
    	cbTF.setMnemonic(KeyEvent.VK_F);
    	cbDF.setMnemonic(KeyEvent.VK_D);
    	cbTFxIDF.setMnemonic(KeyEvent.VK_I);
    	// add UI-elements
    	getContentPane().add(panel);
      	panel.add(new JLabel("Please specify which data should be loaded."));
      	panel.add(new JLabel());
    	panel.add(cbTerms);
    	panel.add(cbDocPaths);
    	panel.add(new JLabel());
    	panel.add(cbTO);   	
    	panel.add(cbTF);
    	panel.add(cbDF);
    	panel.add(cbTFxIDF);
    	panel.add(new JLabel());
    	panel.add(btnOK);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
   
 	/**
  	 * Sets the values of the configuration dialog to the ones specified by the
  	 * given ETPLoaderConfig-instance.
  	 * 
  	 * @param etplCfg the ETPLoaderConfig-instance containing the values for the dialog
  	 */
  	public void setConfig(ETPLoaderConfig etplCfg) {
  		if (etplCfg != null) {
  			cbTerms.setSelected(etplCfg.isLoadTerms());
 			cbDocPaths.setSelected(etplCfg.isLoadDocPaths());
 			cbTO.setSelected(etplCfg.isLoadTO());
 			cbTF.setSelected(etplCfg.isLoadTF());
 			cbDF.setSelected(etplCfg.isLoadDF());
 			cbTFxIDF.setSelected(etplCfg.isLoadTFxIDF());
  		}
  	}

	/**
	 * @return Returns the loadDF.
	 */
	public boolean isLoadDF() {
		return this.cbDF.isSelected();
	}

	/**
	 * @return Returns the loadDocPaths.
	 */
	public boolean isLoadDocPaths() {
		return this.cbDocPaths.isSelected();
	}

	/**
	 * @return Returns the loadTerms.
	 */
	public boolean isLoadTerms() {
		return this.cbTerms.isSelected();
	}

	/**
	 * @return Returns the loadTF.
	 */
	public boolean isLoadTF() {
		return this.cbTF.isSelected();
	}

	/**
	 * @return Returns the loadTFxIDF.
	 */
	public boolean isLoadTFxIDF() {
		return this.cbTFxIDF.isSelected();
	}

	/**
	 * @return Returns the loadTO.
	 */
	public boolean isLoadTO() {
		return this.cbTO.isSelected();
	}
	
   	/**
   	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   	 */
   	public void actionPerformed(ActionEvent actionEvent) {
   		if (actionEvent.getSource() == btnOK) {
   			confirmOperation = true;
   			dispose();
   		}
   	}
   	
}
