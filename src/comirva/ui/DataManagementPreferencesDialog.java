package comirva.ui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
import javax.swing.JTabbedPane;

import comirva.config.VisuPreferences;

/** 
 * This class implements a dialog for specifying some
 * preferences of the data management panel
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class DataManagementPreferencesDialog extends JDialog implements
		ActionListener {
	// UI-elements
	private JButton btnOK = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
    private GridLayout gridLayout = new GridLayout();
    
    private JCheckBox toolbarFloatable = new JCheckBox("Floatable Toolbar");
    private GridLayout glTabLayout = new GridLayout();
    private JPanel panelTabLayout = new JPanel(glTabLayout);
    private JRadioButton tabLayoutWrap = new JRadioButton("Wrap Tabs");
    private JRadioButton tabLayoutScroll = new JRadioButton("Scroll Tabs");
    private JComboBox comboVisuName = new JComboBox(VisuPreferences.VISU_NAME_NAMES);
    private JCheckBox useDataMatrixName = new JCheckBox("append data matrix name");
    
    // flag indicating for the creating instance, if the dialog was closed by clicking "OK" or "Cancel"
    public boolean confirmOperation;
	
    /** 
     * Constructs a data management preferences dialog
     * @param owner an owner frame
     * @see JDialog#JDialog(Frame)
     */
	public DataManagementPreferencesDialog(Frame owner) {
		super(owner);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initDataManagementPreferencesDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
	}

	/**
	 * initialises the components
	 */
	private void initDataManagementPreferencesDialog() {
		int rows = 5;		// number of rows needed in dialog
		
		this.setTitle("Data Management Preferences");
    	// assign text to buttons, set name and assign action listener
    	btnOK.setText("OK");
    	btnOK.setMnemonic(KeyEvent.VK_O);
    	// set "OK"-button as default
    	this.getRootPane().setDefaultButton(btnOK);
    	btnOK.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	
    	// init ui elements
    	glTabLayout.setRows(1);
    	glTabLayout.setColumns(2);    	
    	ButtonGroup groupTabLayout = new ButtonGroup();
    	groupTabLayout.add(tabLayoutWrap);
    	groupTabLayout.add(tabLayoutScroll);
    	panelTabLayout.add(tabLayoutWrap);
    	panelTabLayout.add(tabLayoutScroll);
    	tabLayoutScroll.setSelected(true);
    	
    	// init grid layout
    	gridLayout.setRows(rows);
    	gridLayout.setColumns(2);
    	gridLayout.setVgap(5);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	// set default look and feel
    	panel.add(new JLabel("Toolbar options:"));	// Make toolbar floatable
    	panel.add(toolbarFloatable);
    	panel.add(new JLabel("Tab Layout:"));
    	panel.add(panelTabLayout);
    	panel.add(new JLabel("<html>Use configuration information <br>for visualization names</html>"));
    	panel.add(comboVisuName);
    	panel.add(new JLabel("Names of new visualization"));
    	panel.add(useDataMatrixName);
    	
    	panel.add(btnOK);
    	panel.add(btnCancel);
    	
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
	}
		
	/**
	 * Set the configuration to a given configuration. This methods allows to
	 * preset the components according to the current settings (e.g. if the 
	 * toolbar is floatable the "is floatable" checkbox should be checked)
	 * @param dmPrefs	a visualization preferences object
	 */
	public void setConfig(VisuPreferences dmPrefs) {
		if (dmPrefs != null) {
			this.toolbarFloatable.setSelected(dmPrefs.isToolbarFloatable());
			if (dmPrefs.getTabLayout()==JTabbedPane.SCROLL_TAB_LAYOUT) {
				this.tabLayoutScroll.setSelected(true);
			} else if (dmPrefs.getTabLayout()==JTabbedPane.WRAP_TAB_LAYOUT) {
				this.tabLayoutWrap.setSelected(true);
			}
			this.comboVisuName.setSelectedIndex(dmPrefs.getVisuName());
			this.useDataMatrixName.setSelected(dmPrefs.useDataMatrixName());
		}
	}
	
	/**
	 * @return whether the floatable toolbar checkbox is checked or not
	 */
	public boolean isToolbarFloatable() {
		return toolbarFloatable.isSelected();
	}
	
	/**
	 * @return the tab layout. It is one of the constants {@link JTabbedPane#SCROLL_TAB_LAYOUT}
	 * 	or {@link JTabbedPane#WRAP_TAB_LAYOUT}.
	 * @see JTabbedPane#SCROLL_TAB_LAYOUT
	 * @see JTabbedPane#WRAP_TAB_LAYOUT
	 */
	public int getTabLayout() {
		int layout = JTabbedPane.SCROLL_TAB_LAYOUT;
		if (tabLayoutWrap.isSelected()) {
			layout = JTabbedPane.WRAP_TAB_LAYOUT;
		}
		return layout;
	}
	
	/**
	 * returns how the visualization names should be composed. The return value is one of
	 * {@link VisuPreferences#VISU_NAME_ALL}, {@link VisuPreferences#VISU_NAME_NONSTANDARD} and
	 * {@link VisuPreferences#VISU_NAME_NOTHING}
	 * @return the visualization names configuration
	 */
	public int getVisuName() {
		return this.comboVisuName.getSelectedIndex();
	}
	
	/** 
	 * @return if the data matrix matrix name should be used (determined by selection state of
	 * the checkbox)
	 */
	public boolean useDataMatrixName() {
		return this.useDataMatrixName.isSelected();
	}
	
	// implements
	public void actionPerformed(ActionEvent actionEvent) {
        // if button "Cancel" was pressed, close dialog
    	if (actionEvent.getSource() == btnCancel) {
    		confirmOperation = false;
    		dispose();
    	}
    	// if button "Ok" was pressed, set flag and close dialog
    	if (actionEvent.getSource() == btnOK) {
    		confirmOperation = true;
    		dispose();
    	}
	}
}
