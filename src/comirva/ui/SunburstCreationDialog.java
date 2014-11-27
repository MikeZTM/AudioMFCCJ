/*
 * Created on 16.12.2005
 */
package comirva.ui;

import comirva.config.SunburstConfig;
import comirva.config.defaults.SunburstDefaultConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a Sunburst-Visualization.
 * It is shown when the user wished to create a Sunburst-Visualization. 
 * 
 * @author Markus Schedl
 */
public class SunburstCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateSunburst = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
    private JPanel panelMain = new JPanel();
    private JPanel panelSliders = new JPanel();
    private JPanel panelCenterItem = new JPanel();
    private JPanel panelRadioButtons = new JPanel();
	private JSlider sliderMaxItemsPerNode;
	private JSlider sliderMaxDepth;
	private JSlider sliderMinImportance;
	private JSlider sliderMinFontSize;
	private JSlider sliderMaxFontSize;
	private JLabel currentMaxItemsPerNode;
	private JLabel currentMaxDepth;
	private JLabel currentMinImportance;
	private JLabel currentMinFontSize;
	private JLabel currentMaxFontSize;
    private GridLayout gridLayoutSliders = new GridLayout();
    private BorderLayout borderLayoutMain = new BorderLayout();
    private BorderLayout borderLayoutCenterItem = new BorderLayout();
    private BorderLayout borderLayoutRadioButtons = new BorderLayout();
    private ButtonGroup bg = new ButtonGroup();
    private JRadioButton rbNoRootNodeTerm = new JRadioButton("No Root Node Terms", true);
    private JRadioButton rbSpecifiedRootNodeTerm = new JRadioButton("Specified Root Node Terms", false);
    // JList for list of data items
    private JList jlTerms = new JList();

	// flag indicating for the creating instance, if the dialog was closed by clicking on "Create Sunburst"
    public boolean confirmOperation;

    
    /**
     * Creates a new instance of the Sunburst-parameter dialog and initializes it.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     * @param terms			a Vector<String> containing the terms available as root node term (taken from meta-data) 
     */
    public SunburstCreationDialog(Frame parent, Vector terms) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            // sort term list
            Vector sortedTerms = (Vector)terms.clone();
            Collections.sort(sortedTerms);
            initSunburstCreationDialog(sortedTerms);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
    private void initSunburstCreationDialog(Vector labelsDataItems) {
    	this.setTitle("Sunburst Visualization - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateSunburst.setText("Create Sunburst");
    	btnCreateSunburst.addActionListener(this);
    	btnCreateSunburst.setMnemonic(KeyEvent.VK_S);
    	// set "Create CF"-button as default
    	this.getRootPane().setDefaultButton(btnCreateSunburst);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderMaxItemsPerNode = new JSlider(1, 100, 20);
    	sliderMaxItemsPerNode.setMinorTickSpacing(1);
    	sliderMaxDepth = new JSlider(2, 15, 8);
    	sliderMaxDepth.setMinorTickSpacing(1);
    	sliderMinImportance = new JSlider(1, 450, 10);
    	sliderMinImportance.setMinorTickSpacing(1);
    	sliderMinFontSize = new JSlider(1, 15, 8);
    	sliderMinFontSize.setMinorTickSpacing(1);
    	sliderMaxFontSize = new JSlider(8, 100, 20);
    	sliderMaxFontSize.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentMaxItemsPerNode = new JLabel(Integer.toString(sliderMaxItemsPerNode.getValue()), JLabel.CENTER);
    	currentMaxDepth = new JLabel(Integer.toString(sliderMaxDepth.getValue()), JLabel.CENTER);
    	currentMinImportance = new JLabel((Double.toString((double)sliderMinImportance.getValue()/10)), JLabel.CENTER);
    	currentMinFontSize = new JLabel(Integer.toString(sliderMinFontSize.getValue()), JLabel.CENTER);
    	currentMaxFontSize = new JLabel(Integer.toString(sliderMaxFontSize.getValue()), JLabel.CENTER);
    	// initialize labels list
		jlTerms.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(165, 163, 151)),
				BorderFactory.createEmptyBorder(0,1,0,1)));
		jlTerms.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	// add data
    	jlTerms.setListData(labelsDataItems);
    	// select first element
    	if (labelsDataItems.size() > 0)
    		jlTerms.setSelectedIndex(0);
		// provide the labels list with a scroll pane
		JScrollPane jscp_labels = new JScrollPane(jlTerms);
//		jscp_labels.setPreferredSize(new Dimension(200, 250));
		// group radio buttons
		bg.add(rbNoRootNodeTerm);
		bg.add(rbSpecifiedRootNodeTerm);
		rbNoRootNodeTerm.setMnemonic(KeyEvent.VK_N);
		rbSpecifiedRootNodeTerm.setMnemonic(KeyEvent.VK_R);
    	// assign change listeners
		sliderMaxItemsPerNode.addChangeListener(this);
		sliderMaxDepth.addChangeListener(this);
		sliderMinImportance.addChangeListener(this);
		sliderMinFontSize.addChangeListener(this);
		sliderMaxFontSize.addChangeListener(this);
    	// init grid layout
    	gridLayoutSliders.setRows(8);
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
    	panelMain.add(panelSliders, BorderLayout.CENTER);
    	panelMain.add(panelCenterItem, BorderLayout.WEST);
    	panelCenterItem.add(jscp_labels, BorderLayout.SOUTH);
    	panelCenterItem.add(panelRadioButtons, BorderLayout.NORTH);
		panelRadioButtons.add(rbNoRootNodeTerm, BorderLayout.NORTH);
		panelRadioButtons.add(rbSpecifiedRootNodeTerm, BorderLayout.SOUTH);
    	panelSliders.add(new JLabel("<html>Maximum Sub-Nodes per Node</html>"));
    	panelSliders.add(sliderMaxItemsPerNode);
    	panelSliders.add(currentMaxItemsPerNode);
    	panelSliders.add(new JLabel("<html>Maximum Depth (Hierarchy Levels)</html>"));
    	panelSliders.add(sliderMaxDepth);
    	panelSliders.add(currentMaxDepth);
    	panelSliders.add(new JLabel("<html>Minimum Angular Extent of Arc (Degrees)</html>"));
    	panelSliders.add(sliderMinImportance);
    	panelSliders.add(currentMinImportance);
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel("<html>Minimum Font Size for Labels</html>"));
    	panelSliders.add(sliderMinFontSize);
    	panelSliders.add(currentMinFontSize);
    	panelSliders.add(new JLabel("<html>Maximum Font Size for Labels</html>"));
    	panelSliders.add(sliderMaxFontSize);
    	panelSliders.add(currentMaxFontSize);
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(btnCreateSunburst);
    	panelSliders.add(btnDefault);
    	panelSliders.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
   
	/**
	 * Returns the maximum depth of the hierarchy to be included in the sunburst the user has selected with the slider.
	 * 
	 * @return returns the maximum hierarchy depth the user has selected with the slider
	 */
	public int getMaxDepth() {
		return this.sliderMaxDepth.getValue();
	}

	/**
	 * Returns the maximum number of subnodes per sunburst-node the user has selected with the slider.
	 * 
	 * @return returns the maximum number of subnodes for every node of the sunburst the user has selected with the slider
	 */
	public int getMaxItemsPerNode() {
		return this.sliderMaxItemsPerNode.getValue();
	}

	/**
	 * Returns the threshold for the importance of a node the user has selected with the slider. 
	 * 
	 * @return returns the minimum importance of a node (so that it will still be included in the sunburst) the user has selected with the slider 
	 */
	public double getMinImportance() {
		return (double)this.sliderMinImportance.getValue()/(double)(10*360);
	}

	/**
	 * Returns a list of terms that are included in the root node.
	 * Based on these terms, the sunburst will be constructed.
	 * 
	 * @return returns the terms of the root node the user has selected.
	 */
	public Vector<String> getRootTerms() {
		Vector<String> rt = new Vector<String>();
		// if root node term(s) is/are specified -> construct list of root node terms
		if (this.rbSpecifiedRootNodeTerm.isSelected()) {
			int[] selectedIndices = jlTerms.getSelectedIndices();
			ListModel lm = jlTerms.getModel();
			for (int i=0; i<selectedIndices.length; i++)
				rt.addElement((String)lm.getElementAt(selectedIndices[i]));			
		}
		return rt;		
	}

	/**
	 * Returns the maximum font size for labels the user has selected.
	 * 
	 * @return returns the maximum font size in pt the user has selected
	 */
	public int getMaxFontSize() {
		return this.sliderMaxFontSize.getValue();
	}

	/**
	 * Returns the minimun font size for labels the user has selected.
	 * 
	 * @return returns the minimum font size in pt the user has selected
	 */
	public int getMinFontSize() {
		return this.sliderMinFontSize.getValue();
	}
	
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given SunburstConfig-instance.
   	 * 
   	 * @param sbCfg the SunburstConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(SunburstConfig sbCfg) {
   		if (sbCfg != null) {
   			sliderMaxItemsPerNode.setValue(sbCfg.getMaxItemsPerNode());
   			sliderMaxDepth.setValue(sbCfg.getMaxDepth());
   			sliderMinImportance.setValue((int)Math.round(sbCfg.getMinImportance()*10*360));
   			sliderMinFontSize.setValue(sbCfg.getMinFontSize());
   			sliderMaxFontSize.setValue(sbCfg.getMaxFontSize());
   			Vector<String> rootNodeTerms = sbCfg.getRootTerms();
   			// select terms that should be included in root node
   			if (rootNodeTerms == null || rootNodeTerms.size() == 0) {		// no terms
   				rbNoRootNodeTerm.setSelected(true);
   			} else {		// at least one term should be included in root node
   				rbSpecifiedRootNodeTerm.setSelected(true);
   				// select all terms that are to be included
   				ListModel lm = jlTerms.getModel();
   				int[] selectedIndices = new int[rootNodeTerms.size()];
   				int idxCount = 0;
   				for (int i=0; i<lm.getSize(); i++) {
   					if (rootNodeTerms.contains((String)lm.getElementAt(i))) {
   						selectedIndices[idxCount] = i;
   						idxCount++;
   					}
   				}
   				jlTerms.setSelectedIndices(selectedIndices);
   			}
   		}
   	}
   	
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderMaxItemsPerNode)
	        this.currentMaxItemsPerNode.setText(Integer.toString(source.getValue()));
    	if (source == sliderMaxDepth)
	        this.currentMaxDepth.setText(Integer.toString(source.getValue()));
    	if (source == sliderMinImportance)
	        this.currentMinImportance.setText(Double.toString((double)source.getValue()/10));
    	if (source == sliderMaxFontSize) {
	        this.currentMaxFontSize.setText(Integer.toString(source.getValue()));	
	    	// make sure that minimum font size is less then or equal to maximum font size
	    	if (sliderMinFontSize.getValue() > sliderMaxFontSize.getValue())
	    		sliderMinFontSize.setValue(sliderMaxFontSize.getValue());
    	}
    	if (source == sliderMinFontSize) {
	        this.currentMinFontSize.setText(Integer.toString(source.getValue()));	
	    	// make sure that maximum font size is greater then or equal to minimum font size
	    	if (sliderMinFontSize.getValue() > sliderMaxFontSize.getValue())
	    		sliderMaxFontSize.setValue(sliderMinFontSize.getValue());
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
    		this.setConfig(new SunburstDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnCreateSunburst) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
