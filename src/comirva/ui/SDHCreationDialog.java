/*
 * Created on 11.11.2004
 */
package comirva.ui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

import comirva.config.SDHConfig;
import comirva.config.defaults.SDHDefaultConfig;
import comirva.mlearn.SOM;

/**
 * This class implements a dialog for specifying the parameters of an SDH.
 * It is shown when the user wished to create an SDH. 
 * 
 * @author Markus Schedl
 */
public class SDHCreationDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnCreateSDH = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDefault = new JButton();
	private JPanel panel = new JPanel();
	private JSlider sliderSpread;
	private JSlider sliderIterations;
	private JSlider sliderFractalComponent;
	private JLabel currentSpread;
	private JLabel currentIterations;
	private JLabel currentFractalComponent;
	private JComboBox underlyingSOM;
	private GridBagLayout gridBagLayout = new GridBagLayout();
	private JPanel buttonPanel;
	private GridLayout buttonPanelLayout;

	// flag indicating for the creating instance, if the dialog was closed by clicking of "Create SDH"
	public boolean confirmOperation;

	// properties
	private SOM[] som;

	/**
	 * Creates a new instance of the SDH-parameter dialog and initializes it.
	 * Furthermore, the maximum values for the displayed sliders are set.
	 * 
	 * @param parent			the Frame of the parent window where the dialog should be displayed
	 * @param maxSpread			the maximum spread the user can select with the slider
	 * @param maxIterations		the maximum iterations the user can select with the slider
	 * @param underlyingSOMs	a list of available SOM that can be used for the new SDH
	 * @param preselection		the index specifying which of the underlyingSOMs should be selected by default
	 */
	public SDHCreationDialog(Frame parent, int maxSpread, int maxIterations, String[] underlyingSOMs, int preselection) {
		super(parent);
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			initSDHCreationDialog(maxSpread, maxIterations, underlyingSOMs, preselection);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Creates a new instance of the SDH-parameter dialog and initializes it.
	 * Furthermore, the maximum avlues for the displayed sliders are set according to the preselection for the som list
	 * @param parent			the frame of the parent window where the dialog should be displayed
	 * @param som				a list of SOMs that can be used as bese for the new SDH
	 * @param somNames			the names of the soms given bei the som parameter
	 * @param preselection		the index that should be preselected.
	 * @param maxIterations		the maximum iterations the user can select with the slider
	 */
	public SDHCreationDialog(Frame parent, SOM[] som, String[] somNames, int preselection, int maxIterations) {
		this(parent, som[preselection].getNumberOfColumns() * som[preselection].getNumberOfRows(), maxIterations, somNames, preselection);
		this.som = som;
	}

	/**
	 * Creates a new instance of the SDH-parameter dialog and initializes it.
	 * Furthermore, the maximum values for the displayed sliders are set.
	 * 
	 * @param parent		the Frame of the parent window where the dialog should be displayed
	 * @param maxSpread		the maximum spread the user can select with the slider
	 * @param maxIterations	the maximum iterations the user can select with the slider
	 */    
	public SDHCreationDialog(Frame parent, int maxSpread, int maxIterations) {
		this(parent, maxSpread, maxIterations, null, -1);
	}

	/**
	 * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
	 * and assigning an ActionListener.</p>
	 * To arrange the elements of the dialog box, a GridLayout is used.  
	 */
	private void initSDHCreationDialog(int maxSpread, int maxIterations, String[] underlyingSOMs, int preselection) {
		Insets insets = new Insets(10, 0, 10, 0); 	// top, left, bottom, right
		// define gridbag constraints
		GridBagConstraints c1 = new GridBagConstraints();		// 1 column
		c1.weightx = 1.0;
		c1.insets = insets;
		c1.fill = GridBagConstraints.BOTH;	   
		GridBagConstraints c1n = new GridBagConstraints();		// 1 column + '\n'
		c1n.weightx = 1.0;
		c1n.insets = insets;
		c1n.gridwidth = GridBagConstraints.REMAINDER;
		c1n.fill = GridBagConstraints.BOTH;	   
		GridBagConstraints c2n = new GridBagConstraints();		// 2 column + '\n'
		c2n.weightx = 2.0;
		c2n.insets = insets;
		c2n.gridwidth = GridBagConstraints.REMAINDER;
		c2n.fill = GridBagConstraints.HORIZONTAL;	   
		GridBagConstraints cn = new GridBagConstraints();		//	'\n'
		cn.gridwidth = GridBagConstraints.REMAINDER;
		cn.fill = GridBagConstraints.BOTH;
		// set dialog title
		this.setTitle("SDH - Configuration");
		// assign text to buttons, set name and assign action listener
		btnCreateSDH.setText("Create SDH");
		btnCreateSDH.setMnemonic(KeyEvent.VK_S);
		// set "Create SDH"-button as default
		this.getRootPane().setDefaultButton(btnCreateSDH);
		btnCreateSDH.addActionListener(this);
		btnCancel.setText("Cancel");
		btnCancel.setMnemonic(KeyEvent.VK_C);
		btnCancel.addActionListener(this);
		btnDefault.setText("Default Values");
		btnDefault.setMnemonic(KeyEvent.VK_D);
		btnDefault.addActionListener(this);
		// create and initalize combo box
		underlyingSOM = new JComboBox(underlyingSOMs);
		underlyingSOM.setSelectedIndex(preselection);
		underlyingSOM.addItemListener(new UnderlyingSomSelectedItemListener());
		// create and initialize sliders
		sliderSpread = new JSlider(1, maxSpread, 3);
		sliderIterations = new JSlider(1, maxIterations, 5);
		sliderFractalComponent = new JSlider(0, 100, 10);
		sliderSpread.setMinorTickSpacing(1);
		sliderIterations.setMinorTickSpacing(1);
		sliderFractalComponent.setMinorTickSpacing(1);
		// initialize labels for slider values
		currentSpread = new JLabel(Integer.toString(sliderSpread.getValue()), JLabel.CENTER);
		currentIterations = new JLabel(Integer.toString(sliderIterations.getValue()), JLabel.CENTER);
		currentFractalComponent = new JLabel(Integer.toString(sliderFractalComponent.getValue()), JLabel.CENTER);
		// assign change listeners
		sliderSpread.addChangeListener(this);
		sliderIterations.addChangeListener(this);
		sliderFractalComponent.addChangeListener(this);
		// init grid layout
		//gridBagLayout.setRows(8);
		//gridBagLayout.setVgap(0);		
		// assign layout
		panel.setLayout(gridBagLayout);
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		// add UI-elements
		getContentPane().add(panel);
		addCaptionLabel("<html>Underlying SOM<br>(Choose the SOM to use for this SDH)</html>", c1);
		gridBagLayout.setConstraints(underlyingSOM, c2n);
		panel.add(underlyingSOM);
		addCaptionLabel(null, cn);
		addCaptionLabel("<html>Spread<br>(Number of Votes Each Data Item Has)</html>", c1);
		gridBagLayout.setConstraints(sliderSpread,  c1 );    	panel.add(sliderSpread);
		gridBagLayout.setConstraints(currentSpread, c1n);    	panel.add(currentSpread);
		addCaptionLabel("<html>Iterations of <br>Voting Matrix Interpolations</html>", c1);
		gridBagLayout.setConstraints(sliderIterations,  c1 );	panel.add(sliderIterations);
		gridBagLayout.setConstraints(currentIterations, c1n);	panel.add(currentIterations);
		addCaptionLabel("<html>Strength of Fractal Component<br>(Unsharpens the SDH)</html>", c1);
		gridBagLayout.setConstraints(sliderFractalComponent,  c1 );	panel.add(sliderFractalComponent);
		gridBagLayout.setConstraints(currentFractalComponent, c1n);	panel.add(currentFractalComponent);
		
		buttonPanelLayout = new GridLayout(1, 3);
		buttonPanel = new JPanel(buttonPanelLayout);
		buttonPanel.add(btnCreateSDH);
		buttonPanel.add(btnDefault);
		buttonPanel.add(btnCancel);
		//gridBagLayout.setConstraints(btnCreateSDH, c1);	panel.add(btnCreateSDH);
		//gridBagLayout.setConstraints(btnDefault,   c1);	panel.add(btnDefault);
		//gridBagLayout.setConstraints(btnCancel,   c1n);	panel.add(btnCancel);
		gridBagLayout.setConstraints(buttonPanel, cn);		panel.add(buttonPanel);
		// set default look and feel
		this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		this.setResizable(false);
	}

	private void addCaptionLabel(String text, GridBagConstraints constraints) {
		JLabel somCaption = new JLabel(text);
		gridBagLayout.setConstraints(somCaption, constraints);
		panel.add(somCaption);
	}
	/**
	 * Returns the spread the user has selected with the slider.
	 * 
	 * @return the spread selected with the slider 
	 */
	public int getSpread() {
		return sliderSpread.getValue();
	}

	/**
	 * Returns the number of iterations for the interpolation of 
	 * the voting matrix the user has selected with the slider.
	 * 
	 * @return the iterations selected with the slider 
	 */
	public int getIterations() {
		return sliderIterations.getValue();
	}

	/**
	 * Returns the strength of the fractal component used to 
	 * unsharpen the SDH in order to give it a more natural appearance.
	 * 
	 * @return the strength of the fractal component  
	 */
	public int getFractalComponent() {
		return sliderFractalComponent.getValue();
	}

	/**
	 * Returns the index of the selected underlying SOM.
	 *
	 * @return the selected index
	 */
	public int getIndexOfSelectedSOM() {
		return underlyingSOM.getSelectedIndex();
	}

	/**
	 * Sets the values of the configuration dialog to the ones specified by the
	 * given SDHConfig-instance.
	 * 
	 * @param sdhCfg the SDHConfig-instance containing the values for the dialog
	 */
	public void setConfig(SDHConfig sdhCfg) {
		if (sdhCfg != null) {
			sliderSpread.setValue(sdhCfg.getSpread());
			sliderIterations.setValue(sdhCfg.getIterations());
			sliderFractalComponent.setValue(sdhCfg.getFractalComponent());
		}
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		// set label according to slider which has been changed
		JSlider source = (JSlider)e.getSource();
		if (source == sliderSpread)
			this.currentSpread.setText(Integer.toString(source.getValue()));
		if (source == sliderIterations)
			this.currentIterations.setText(Integer.toString(source.getValue()));
		if (source == sliderFractalComponent)
			this.currentFractalComponent.setText(Integer.toString(source.getValue()));
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
			this.setConfig(new SDHDefaultConfig());
		}
		if (actionEvent.getSource() == btnCreateSDH) {
			confirmOperation = true;
			dispose();
		}
	}

	/** update the dialog controls if the selected SOM has changed
	 * 
	 * @param e the item event
	 */
	private void underlyingSomSelectedItemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			// update slider values
			SOM selection = som[underlyingSOM.getSelectedIndex()];
			int maxSpread = selection.getNumberOfColumns() * selection.getNumberOfRows();
			sliderSpread.setMaximum(maxSpread);
		}
	}

	/**
	 * A listener that listens to changes of the selected SOM
	 * @author Florian Marchl
	 *
	 */
	private class UnderlyingSomSelectedItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			underlyingSomSelectedItemStateChanged(e);
		}    	
	}
}
