/*
 * Created on 10.03.2005
 */
package comirva.ui;

import comirva.config.ProbabilisticNetworkConfig;
import comirva.config.defaults.ProbabilisticNetworkDefaultConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class implements a dialog for specifying the parameters of a
 * Probabilistic-Network-Visualization. 
 * 
 * @author Markus Schedl
 */
public class ProbabilisticNetworkCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateNetwork = new JButton();
	private JButton btnDefault = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderMaxEdgeThickness;
	private JSlider sliderMaxDistReduction;
	private JSlider sliderMaxVertexDiameter;
	private JSlider sliderMinVertexDiameter;
	private JSlider sliderProbCorrection;
	private JSlider sliderAdaptationRunsEpochs;
	private JSlider sliderAdaptationThreshold;
	private JSlider sliderGridSize;
	private JLabel currentMaxEdgeThickness;
	private JLabel currentMaxDistReduction;
	private JLabel currentMaxVertexDiameter;
	private JLabel currentMinVertexDiameter;
	private JLabel currentProbCorrection;
	private JLabel currentAdaptationRunsEpochs;
	private JLabel currentAdaptationThreshold;
	private JLabel currentGridSize;
    private GridLayout gridLayout = new GridLayout();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Create Network"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the PN-parameter dialog and initializes it.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     */
    public ProbabilisticNetworkCreationDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initPNCreationDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.  
     */
   private void initPNCreationDialog() {
    	this.setTitle("Probabilistic Network - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnCreateNetwork.setText("Create Network");
    	btnCreateNetwork.setMnemonic(KeyEvent.VK_N);
    	// set "Create SDH"-button as default
    	this.getRootPane().setDefaultButton(btnCreateNetwork);
    	btnCreateNetwork.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	btnDefault.setText("Default Values");
    	btnDefault.setMnemonic(KeyEvent.VK_D);
    	btnDefault.addActionListener(this);
    	// create and initialize sliders
    	sliderMaxEdgeThickness = new JSlider(1, 100, 6);
    	sliderMaxDistReduction = new JSlider(0, 500, 100);
    	sliderMaxVertexDiameter = new JSlider(1, 100, 18);
    	sliderMinVertexDiameter = new JSlider(1, 20, 4);
    	sliderProbCorrection = new JSlider(0, 5000, 500);
    	sliderAdaptationRunsEpochs = new JSlider(0, 100, 10);
		sliderAdaptationThreshold = new JSlider(0, 100, 25);
		sliderGridSize = new JSlider(1, 100, 1);
    	sliderMaxEdgeThickness.setMinorTickSpacing(1);
    	sliderMaxDistReduction.setMinorTickSpacing(10);
    	sliderMaxDistReduction.setSnapToTicks(true);
    	sliderMaxVertexDiameter.setMinorTickSpacing(1);
    	sliderMinVertexDiameter.setMinorTickSpacing(1);
    	sliderProbCorrection.setMinorTickSpacing(1);
    	sliderAdaptationRunsEpochs.setMinorTickSpacing(1);
    	sliderAdaptationThreshold.setMinorTickSpacing(1);
    	sliderGridSize.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentMaxEdgeThickness = new JLabel(Integer.toString(sliderMaxEdgeThickness.getValue()), JLabel.CENTER);
    	currentMaxDistReduction = new JLabel(Integer.toString(sliderMaxDistReduction.getValue()), JLabel.CENTER);
    	currentMaxVertexDiameter = new JLabel(Integer.toString(sliderMaxVertexDiameter.getValue()), JLabel.CENTER);
    	currentMinVertexDiameter = new JLabel(Integer.toString(sliderMinVertexDiameter.getValue()), JLabel.CENTER);
    	currentProbCorrection = new JLabel((Double.toString((double)sliderProbCorrection.getValue()/100)), JLabel.CENTER);
    	currentAdaptationRunsEpochs = new JLabel(Integer.toString(sliderAdaptationRunsEpochs.getValue()), JLabel.CENTER);
    	currentAdaptationThreshold = new JLabel((Double.toString((double)sliderAdaptationThreshold.getValue()/100)), JLabel.CENTER);
    	currentGridSize = new JLabel(Integer.toString(sliderGridSize.getValue()), JLabel.CENTER);
    	// assign change listeners
    	sliderMaxEdgeThickness.addChangeListener(this);
    	sliderMaxDistReduction.addChangeListener(this);
    	sliderMaxVertexDiameter.addChangeListener(this);
    	sliderMinVertexDiameter.addChangeListener(this);
    	sliderProbCorrection.addChangeListener(this);
    	sliderAdaptationRunsEpochs.addChangeListener(this);
    	sliderAdaptationThreshold.addChangeListener(this);
    	sliderGridSize.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(14);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("Maximum Vertex Diameter"));
    	panel.add(sliderMaxVertexDiameter);
    	panel.add(currentMaxVertexDiameter);
    	panel.add(new JLabel("Minimum Vertex Diameter"));
    	panel.add(sliderMinVertexDiameter);
    	panel.add(currentMinVertexDiameter);
    	panel.add(new JLabel("Maximum Edge Thickness"));
    	panel.add(sliderMaxEdgeThickness);
    	panel.add(currentMaxEdgeThickness);
    	panel.add(new JLabel("Grid Size in Pixels (1...no grid)"));
    	panel.add(sliderGridSize);
    	panel.add(currentGridSize);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel("<html>Adaptation Iterations in Epochs<br>(1 Epoch = Number of Data Items ^ 2 Runs)</html>"));
    	panel.add(sliderAdaptationRunsEpochs);
    	panel.add(currentAdaptationRunsEpochs);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel("<html>Adaptation Threshold (AT)<br>Distance(i,j) is adapted only if Similarity(i,j) > AT</html>"));
    	panel.add(sliderAdaptationThreshold);
    	panel.add(currentAdaptationThreshold);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel("<html>Maximum Distance Reduction between Pair of<br>Vertices in a Single Adaptation Iteration</html>"));
    	panel.add(sliderMaxDistReduction);
    	panel.add(currentMaxDistReduction);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel("<html>Probability Correction (PC) for Drawing Edges<br>Edge(i,j) is Drawn if Similarity(i,j) > RandomValue[0,1]*PC</html>"));
    	panel.add(sliderProbCorrection);
    	panel.add(currentProbCorrection);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnCreateNetwork);
    	panel.add(btnDefault);
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
  
   	/**
   	 * Returns the maximum thickness for an edge the user has selected with the slider.
   	 * 
   	 * @return the maximum thickness for an edge selected with the slider 
   	 */
   	public int getMaxEdgeThickness() {
   		return sliderMaxEdgeThickness.getValue();
   	}
   	
   	/**
   	 * Returns the maximum distance reduction between two data points in the adaptation process 
   	 * the user has selected with the slider.
   	 * 
   	 * @return the maximum distance reduction
   	 */
   	public int getMaxDistReduction() {
   		return sliderMaxDistReduction.getValue();
   	}
   	
   	/**
   	 * Returns the maximum vertex diameter for a data point
   	 * the user has selected with the slider.
   	 * 
   	 * @return the maximum diameter for a vertex
   	 */
   	public int getMaxVertexDiameter() {
   		return sliderMaxVertexDiameter.getValue();
   	}
   	
   	/**
   	 * Returns the minimum vertex diameter for a data point
   	 * the user has selected with the slider.
   	 * 
   	 * @return the minimum diameter for a vertex
   	 */
   	public int getMinVertexDiameter() {
   		return sliderMinVertexDiameter.getValue();
   	}
   	
   	/**
   	 * Returns the probability correction for drawing edges the user has selected with the slider.
   	 * An edge between data point (vertex) <code>i</code> and <code>j</code> is drawn
   	 * with a probability that equals the similarity between <code>i</code> and <code>j</code> multiplied
   	 * with the probability correction.
   	 * 
   	 * @return the probability correction
   	 */
   	public double getProbCorrection() {
   		return (double)sliderProbCorrection.getValue()/100;
   	}
   	
   	/**
   	 * Returns the number of iterations in epochs the adaptation process is performed. The value which the user has selected with the slider is returned.
   	 * One epoch means that, on average, each pair of data items is selected for adaptation once.
   	 * Thus, one epoch means that the adaptation is iterated <code>numberOfDataItems^2</code> times.
   	 * 
   	 * @return the number of epochs the adaptation process is performed
   	 */
   	public int getAdaptationRunsEpochs() {
   		return sliderAdaptationRunsEpochs.getValue();
   	}
   	
    /**
   	 * Returns the adaptation threshold the user has selected with the slider.
   	 * The output distance is adapted only for those data items <code>i, j</code> that have a similarity 
   	 * greater than the adaptation threshold.
   	 * 
   	 * @return the adaptation threshold
   	 */
   	public double getAdaptationThreshold() {
   		return (double)sliderAdaptationThreshold.getValue()/100;
   	}
 
   	/**
   	 * Returns the grid size used for vertex placement which the user has selected with the slider.
   	 * 
   	 * @return the grid size in pixels
   	 */
   	public int getGridSize() {
   		return sliderGridSize.getValue();
   	}  	

   	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given ProbabilisticNetworkConfig-instance.
   	 * 
   	 * @param pnCfg the ProbabilisticNetworkConfig-instance containing the values for the dialog
   	 */
   	public void setConfig(ProbabilisticNetworkConfig pnCfg) {
   		if (pnCfg != null) {
   			sliderMaxEdgeThickness.setValue(pnCfg.getMaxEdgeThickness());
   			sliderMaxDistReduction.setValue(pnCfg.getMaxDistReduction());
   			sliderMaxVertexDiameter.setValue(pnCfg.getMaxVertexDiameter());
   			sliderMinVertexDiameter.setValue(pnCfg.getMinVertexDiameter());
   			sliderProbCorrection.setValue((int)Math.round(pnCfg.getProbCorrection()*100));
   			sliderAdaptationRunsEpochs.setValue(pnCfg.getAdaptationRunsEpochs());
   			sliderAdaptationThreshold.setValue((int)Math.round(pnCfg.getAdaptationThreshold()*100));
   			sliderGridSize.setValue(pnCfg.getGridSize());
   		}
   	}
   	
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderMaxEdgeThickness)
	        this.currentMaxEdgeThickness.setText(Integer.toString(source.getValue()));
	    if (source == sliderMaxDistReduction)
	    	this.currentMaxDistReduction.setText(Integer.toString(source.getValue()));
	    if (source == sliderMaxVertexDiameter) {
	    	this.currentMaxVertexDiameter.setText(Integer.toString(source.getValue()));
	    	// make sure that minimum vertex diameter is less then or equal to maximum vertex diameter
	    	if (sliderMinVertexDiameter.getValue() > sliderMaxVertexDiameter.getValue())
	    		sliderMinVertexDiameter.setValue(sliderMaxVertexDiameter.getValue());
	    }
	    if (source == sliderMinVertexDiameter) {
	    	this.currentMinVertexDiameter.setText(Integer.toString(source.getValue()));
	    	// make sure that maximum vertex diameter is greater than or equal to minimum vertex diameter
	    	if (sliderMinVertexDiameter.getValue() > sliderMaxVertexDiameter.getValue())
	    		sliderMaxVertexDiameter.setValue(sliderMinVertexDiameter.getValue());
	    }
	    if (source == sliderProbCorrection)
	    	this.currentProbCorrection.setText((Double.toString((double)source.getValue()/100)));
	    if (source == sliderAdaptationRunsEpochs)
	    	this.currentAdaptationRunsEpochs.setText(Integer.toString(source.getValue()));
	    if (source == sliderAdaptationThreshold)
	    	this.currentAdaptationThreshold.setText((Double.toString((double)source.getValue()/100)));
	    if (source == sliderGridSize)
	    	this.currentGridSize.setText(Integer.toString(source.getValue()));
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
    		this.setConfig(new ProbabilisticNetworkDefaultConfig());
    	}
    	if (actionEvent.getSource() == btnCreateNetwork) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
