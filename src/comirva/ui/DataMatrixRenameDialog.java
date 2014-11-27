/*
 * Created on 09.03.2005
 */
package comirva.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * This class implements a dialog for renaming a (meta-)data matrix.
 * 
 * @author Markus Schedl
 */
public class DataMatrixRenameDialog extends JDialog implements ActionListener {
	// UI-elements
	private JButton btnRename = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
	private JTextField tfName= new JTextField();
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Rename"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the Rename-dialog and initializes it.
     * Furthermore, the original name of the matrix is passed to be shown as default.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     * @param origName		a String representing the original name of the (meta-)data matrix
     * @param isDataMatrix	a flag indicating whether the user intends to rename a data matrix (if <code>true</code>),
     * or a meta-data vector (if <code>false</code>)
     */
    public DataMatrixRenameDialog(Frame parent, String origName, boolean isDataMatrix) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initRenameDialog(origName, isDataMatrix);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and text fields, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initRenameDialog(String origName, boolean isDataMatrix) {
    	if (isDataMatrix)
    		this.setTitle("Rename Data Matrix");
    	else
    		this.setTitle("Rename Meta-Data");
    	// assign text to buttons, set name and assign action listener
    	btnRename.setText("Rename");
    	btnRename.setMnemonic(KeyEvent.VK_R);
    	// set "Rename"-button as default
    	this.getRootPane().setDefaultButton(btnRename);
    	btnRename.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	// set original name as default new name
    	tfName.setText(origName);
    	// select the complete name by default
    	tfName.setSelectionStart(0);
    	tfName.setSelectionEnd(origName.length());
    	tfName.setPreferredSize(new Dimension(200,15));
    	// init grid layout
    	gridLayout.setRows(3);
    	gridLayout.setVgap(0);
    	gridLayout.setHgap(10);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("New Name"));
    	panel.add(tfName);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnRename);
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }

   	/**
   	 * Returns the new name for the (meta-)data matrix.
   	 * 
   	 * @return	a String containing the new name
   	 */
   	public String getNewName() {
   		return tfName.getText();
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
    	if (actionEvent.getSource() == btnRename) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
