/*
 * Created on 07.04.2005
 */
package comirva.ui;

import comirva.config.CSRConfig;
import comirva.config.defaults.CSRDefaultConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a CSR-Visualization.
 * It is shown when the user wished to create a CSR-Visualization. 
 * 
 * @author Markus Schedl
 */
public class CSRCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateCSR = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
    private JPanel panelMain = new JPanel();
    private JPanel panelSliders = new JPanel();
    private JPanel panelPrototypeItems = new JPanel();
	private JSlider sliderNumberOfNeighborsPerPrototype;
	private JSlider sliderMaxEdgeThickness;
	private JSlider sliderPrototypesVertexDiameter;
	private JSlider sliderNeighborsVertexDiameter;
	private JSlider sliderIterationsNeighborsPlacement;
	private JLabel currentNumberOfNeighborsPerPrototype;
	private JLabel currentMaxEdgeThickness;
	private JLabel currentPrototypesVertexDiameter;
	private JLabel currentNeighborsVertexDiameter;
	private JLabel currentIterationsNeighborsPlacement;
    private GridLayout gridLayoutSliders = new GridLayout();
    private BorderLayout borderLayoutMain = new BorderLayout();
    private BorderLayout borderLayoutPrototypeItems = new BorderLayout();
    // JList for list of data items
    private JList jlDataItems = new JList();

	// flag indicating for the creating instance, if the dialog was closed by clicking of "Create Circled Fans"
    public boolean confirmOperation;

    
    /**
     * Creates a new instance of the CSR-parameter dialog and initializes it.
     * 
     * @param parent				the Frame of the parent window where the dialog should be displayed
     * @param labelsDataItems		a Vector containing the labels for all data items (taken from meta-data or just Integers) 
     */
    public CSRCreationDialog(Frame parent, Vector labelsDataItems) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initCSRCreationDialog(labelsDataItems);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
    private void initCSRCreationDialog(Vector labelsDataItems) {
    	this.setTitle("Continuous Similarity Ring (CSR) - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateCSR.setText("Create CSR");
    	btnCreateCSR.addActionListener(this);
    	btnCreateCSR.setMnemonic(KeyEvent.VK_R);
    	// set "Create CF"-button as default
    	this.getRootPane().setDefaultButton(btnCreateCSR);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderNumberOfNeighborsPerPrototype = new JSlider(0, 30, 5);
    	sliderMaxEdgeThickness = new JSlider (1, 20, 5);
    	sliderMaxEdgeThickness.setMinorTickSpacing(1);
    	sliderPrototypesVertexDiameter = new JSlider(1, 30, 15);
    	sliderNeighborsVertexDiameter = new JSlider(1, 30, 10);
    	sliderPrototypesVertexDiameter.setMinorTickSpacing(1);
    	sliderNeighborsVertexDiameter.setMinorTickSpacing(1);
    	sliderIterationsNeighborsPlacement = new JSlider(1, 1000, 50);
    	sliderIterationsNeighborsPlacement.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentNumberOfNeighborsPerPrototype = new JLabel(Integer.toString(sliderNumberOfNeighborsPerPrototype.getValue()), JLabel.CENTER);
    	currentMaxEdgeThickness = new JLabel(Integer.toString(sliderMaxEdgeThickness.getValue()), JLabel.CENTER);
    	currentPrototypesVertexDiameter = new JLabel(Integer.toString(sliderPrototypesVertexDiameter.getValue()), JLabel.CENTER);
    	currentNeighborsVertexDiameter = new JLabel(Integer.toString(sliderNeighborsVertexDiameter.getValue()), JLabel.CENTER);
    	currentIterationsNeighborsPlacement = new JLabel(Integer.toString(sliderIterationsNeighborsPlacement.getValue()*100), JLabel.CENTER);
    	// initialize labels list
		jlDataItems.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.WHITE, new Color(165, 163, 151)),
				BorderFactory.createEmptyBorder(0,1,0,1)));
		jlDataItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	// add data
    	jlDataItems.setListData(labelsDataItems);
    	// select first element
    	if (labelsDataItems.size() > 0)
    		jlDataItems.setSelectedIndex(0);
		// provide the labels list with a scroll pane
		JScrollPane jscp_labels = new JScrollPane(jlDataItems);
    	// assign change listeners
    	sliderNumberOfNeighborsPerPrototype.addChangeListener(this);
    	sliderMaxEdgeThickness.addChangeListener(this);
    	sliderPrototypesVertexDiameter.addChangeListener(this);
    	sliderNeighborsVertexDiameter.addChangeListener(this);
    	sliderIterationsNeighborsPlacement.addChangeListener(this);
    	// init grid layout
    	gridLayoutSliders.setRows(7);
    	gridLayoutSliders.setVgap(0);
    	// assign layout
    	panelMain.setLayout(borderLayoutMain);
    	panelPrototypeItems.setLayout(borderLayoutPrototypeItems);
    	panelPrototypeItems.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	panelSliders.setLayout(gridLayoutSliders);
    	panelSliders.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
    	// add UI-elements
    	getContentPane().add(panelMain);
    	panelMain.add(panelSliders, BorderLayout.EAST);
    	panelMain.add(panelPrototypeItems, BorderLayout.WEST);
    	panelPrototypeItems.add(new JLabel("<html>Please select the Prototype Items<br><small>(multiple selections possible)</small></html>"), BorderLayout.NORTH);
    	panelPrototypeItems.add(jscp_labels, BorderLayout.SOUTH);
    	panelSliders.add(new JLabel("<html>Number of Neighbors per Prototype</html>"));
    	panelSliders.add(sliderNumberOfNeighborsPerPrototype);
    	panelSliders.add(currentNumberOfNeighborsPerPrototype);
    	panelSliders.add(new JLabel("<html>Maximum Edge Thickness</html>"));
    	panelSliders.add(sliderMaxEdgeThickness);
    	panelSliders.add(currentMaxEdgeThickness);
    	panelSliders.add(new JLabel("<html>Prototypes Vertex Diameter</html>"));
    	panelSliders.add(sliderPrototypesVertexDiameter);
    	panelSliders.add(currentPrototypesVertexDiameter);
    	panelSliders.add(new JLabel("<html>Neighbors Vertex Diameter</html>"));
    	panelSliders.add(sliderNeighborsVertexDiameter);
    	panelSliders.add(currentNeighborsVertexDiameter);
    	panelSliders.add(new JLabel("<html>Iterations for Neighbor-Positioning-Heuristic</html>"));
    	panelSliders.add(sliderIterationsNeighborsPlacement);
    	panelSliders.add(currentIterationsNeighborsPlacement);
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(new JLabel());
    	panelSliders.add(btnCreateCSR);
    	panelSliders.add(btnDefault);
    	panelSliders.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
    
   	
	/**
	 * Returns the number of neighboring data items per prototype 
	 * the user has selected with the slider.
	 * 
	 * @return	the number of neighboring data items for each prototype selected with the slider
	 */
    public int getNumberOfNeighborsPerPrototype() {
    	return sliderNumberOfNeighborsPerPrototype.getValue();
    }
    
    /**
     * Returns the indices of the data items the user has selected as prototypes.
     * 
     * @return	an int[] containing the indices of the selected prototypes
     */
    public int[] getPrototypeIndices() {
    	return jlDataItems.getSelectedIndices();
    }
    
	/**
	 * Returns the maximum thickness for the edges connecting the
	 * prototypes with their neighbors the user has selected with the slider.
	 * 
	 * @return the maximum thickness for edges
	 */
    public int getMaxEdgeThickness() {
    	return sliderMaxEdgeThickness.getValue();
    }
    
   	/**
   	 * Returns the vertex diameter for the prototype vertices the user has selected with the slider.
   	 * 
   	 * @return the vertex diameter for the prototype vertices selected with the slider
   	 */
   	public int getPrototypesVertexDiameter() {
   		return sliderPrototypesVertexDiameter.getValue();
   	}
   	
   	/**
   	 * Returns the vertex diameter for the neighbor vertices the user has selected with the slider.
   	 * 
   	 * @return the vertex diameter for the neighbor vertices selected with the slider
   	 */
   	public int getNeighborsVertexDiameter() {
   		return sliderNeighborsVertexDiameter.getValue();
   	}
   	
   	/**
   	 * Returns the number of iterations for the heuristic that positions the vertices of the neighbors
   	 * 
   	 * @return the number of iterations for the heuristic that positions the vertices of the neighbors
   	 */
   	public int getIterationsNeighborsPlacement() {
   		return sliderIterationsNeighborsPlacement.getValue()*100;
   	}
    
   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given CSRConfig-instance.
   	 * 
   	 * @param csrCfg the CSRConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(CSRConfig csrCfg) {
   		if (csrCfg != null) {
   			sliderNumberOfNeighborsPerPrototype.setValue(csrCfg.getNumberOfNeighborsPerPrototype()); 
   			jlDataItems.setSelectedIndices(csrCfg.getPrototypeIndices());
   			sliderMaxEdgeThickness.setValue(csrCfg.getMaxEdgeThickness());
   			sliderPrototypesVertexDiameter.setValue(csrCfg.getPrototypesVertexDiameter());
   			sliderNeighborsVertexDiameter.setValue(csrCfg.getNeighborsVertexDiameter());
   			sliderIterationsNeighborsPlacement.setValue((int)(csrCfg.getIterationsNeighborsPlacement()/100));
   		}
   	}
   	
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderNumberOfNeighborsPerPrototype)
	        this.currentNumberOfNeighborsPerPrototype.setText(Integer.toString(source.getValue()));
    	if (source == sliderMaxEdgeThickness)
	        this.currentMaxEdgeThickness.setText(Integer.toString(source.getValue()));
    	if (source == sliderPrototypesVertexDiameter)
	        this.currentPrototypesVertexDiameter.setText(Integer.toString(source.getValue()));
    	if (source == sliderNeighborsVertexDiameter)
	        this.currentNeighborsVertexDiameter.setText(Integer.toString(source.getValue()));
    	if (source == sliderIterationsNeighborsPlacement)
    		this.currentIterationsNeighborsPlacement.setText(Integer.toString(source.getValue()*100));
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
    		this.setConfig(new CSRDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnCreateCSR) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
