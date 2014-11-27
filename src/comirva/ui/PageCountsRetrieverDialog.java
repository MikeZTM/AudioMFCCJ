/*
 * Created on 01.03.2005
 */
package comirva.ui;

import comirva.config.PageCountsRetrieverConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;


/**
 * This class implements a dialog for specifying the parameters for the PageCounts-Retriever.
 * It is shown when the user wished to do a co-occurrence analysis based on page counts using
 * the Google Web-API. 
 * 
 * @author Markus Schedl
 */
public class PageCountsRetrieverDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnRetrievePageCounts = new JButton();
	private JButton btnCancel = new JButton();
	private JPanel panel = new JPanel();
    private GridLayout gridLayout = new GridLayout();
    private JTextField tfSearchEngineURL = new JTextField();
    private JSlider sliderNumberOfRetries = new JSlider(0,10,3);
    private JSlider sliderIntervalBetweenRetries = new JSlider(1,60,10);
    private JLabel currentNumberOfRetries;
    private JLabel currentIntervalBetweenRetries;
    private JTextField tfAdditionalKeywords = new JTextField();
    private JPanel panelAdditionalKeywordsPlacement = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel panelMetaDataUsage = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JRadioButton rbBeforeSearchString = new JRadioButton("Before Search String", false);
    private JRadioButton rbAfterSearchString = new JRadioButton("After Search String", true);
    private ButtonGroup bgAdditionalKeywordsPlacement = new ButtonGroup();
    private JRadioButton rbQueryForAllPairs = new JRadioButton("Query for All Pairs", true);
    private JRadioButton rbQueryForSingleItems = new JRadioButton("Query for Single Items", false);
    private ButtonGroup bgMetaDataUsage = new ButtonGroup();
    // flag indicating for the creating instance, if the dialog was closed by clicking of "Retrieve"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the PageCountsRetriever-parameter dialog and initializes it.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     */
    public PageCountsRetrieverDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initPageCountsRetrieverDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.   
     */
   private void initPageCountsRetrieverDialog() {
    	this.setTitle("Retrieve Page Counts - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnRetrievePageCounts.setMnemonic(KeyEvent.VK_R);
    	// set "Retrieve"-button as default
    	this.getRootPane().setDefaultButton(btnRetrievePageCounts);
    	btnRetrievePageCounts.setText("Retrieve");
    	btnRetrievePageCounts.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	// set default values for text fields
    	tfSearchEngineURL.setText("http://www.google.com");
    	tfAdditionalKeywords.setText("+music+review");
    	// create and initialize sliders
    	sliderNumberOfRetries.setMinorTickSpacing(1);
    	sliderIntervalBetweenRetries.setMinorTickSpacing(1);
    	// initialize labels for slider values
    	currentNumberOfRetries = new JLabel(Integer.toString(sliderNumberOfRetries.getValue()), JLabel.CENTER);
    	currentIntervalBetweenRetries = new JLabel(Integer.toString(sliderIntervalBetweenRetries.getValue()), JLabel.CENTER);
    	// initialize button group for placement of additional keywords in search string
    	panelAdditionalKeywordsPlacement.add(rbBeforeSearchString);
    	panelAdditionalKeywordsPlacement.add(rbAfterSearchString);
    	bgAdditionalKeywordsPlacement.add(rbBeforeSearchString);
    	bgAdditionalKeywordsPlacement.add(rbAfterSearchString);
    	rbBeforeSearchString.setMnemonic(KeyEvent.VK_B);
    	rbAfterSearchString.setMnemonic(KeyEvent.VK_A);
    	// initialize button group for metadata-usage
    	panelMetaDataUsage.add(rbQueryForAllPairs);
    	panelMetaDataUsage.add(rbQueryForSingleItems);
    	bgMetaDataUsage.add(rbQueryForAllPairs);
    	bgMetaDataUsage.add(rbQueryForSingleItems);
    	rbQueryForAllPairs.setMnemonic(KeyEvent.VK_P);
    	rbQueryForSingleItems.setMnemonic(KeyEvent.VK_S);
    	// assign change listeners
    	sliderNumberOfRetries.addChangeListener(this);
    	sliderIntervalBetweenRetries.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(7);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(new JLabel("URL of Search Engine"));
    	panel.add(tfSearchEngineURL);
    	panel.add(new JLabel());
    	panel.add(new JLabel("Number of Retries"));
    	panel.add(sliderNumberOfRetries);
    	panel.add(currentNumberOfRetries);
    	panel.add(new JLabel("Interval between Retries (sec)"));
    	panel.add(sliderIntervalBetweenRetries);
    	panel.add(currentIntervalBetweenRetries);
    	panel.add(new JLabel("Additional Keywords"));
    	panel.add(tfAdditionalKeywords);
    	panel.add(panelAdditionalKeywordsPlacement);
    	panel.add(new JLabel("Usage of Meta-Data"));
    	panel.add(panelMetaDataUsage);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnRetrievePageCounts);
    	panel.add(new JLabel());
    	panel.add(btnCancel);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
   
    /**
     * Returns the URL of the search engine to be used
     * for the web crawl. 
     * 
     * @return a String containing the URL of the search engine
     */
    public String getSearchEngineURL() {
    	return tfSearchEngineURL.getText();
    }
    
    /**
     * Returns the number of retries in case of a failure in
     * accessing the Google Web API.
     * 
     * @return the number of retries
     */
    public int getNumberOfRetries() {
    	return sliderNumberOfRetries.getValue();
    }
    
    /**
     * Returns the interval between two retries of accessing the
     * Google Web API. It is measures in seconds.
     * 
     * @return the interval between two retries
     */
    public int getIntervalBetweenRetries() {
    	return sliderIntervalBetweenRetries.getValue();
    }
    
    /**
     * Returns the additional keywords the user has entered.
     * 
     * @return a String containing the additional keywords the user may have specified
     */
    public String getAdditionalKeywords() {
    	return tfAdditionalKeywords.getText();
    }
    
    /**
     * Returns whether additional keywords are to be placed after the search string or before.
     *  
     * @return <code>true</code> if additional keywords should be placed after the search string, 
     * <code>false</code> if they are placed before the search string
     */
    public boolean getAdditionalKeywordsAfterSearchString() {
    	return bgAdditionalKeywordsPlacement.isSelected(rbAfterSearchString.getModel());
    }
    
    /**
     * Returns whether queries should be raised for all pairs of the strings 
     * in the meta-data vector or the selected meta-data vector is processed 
     * sequentially as a list that is queried.
     * 
     * @return	<code>true</code> if all pairwise combinations of the elements in the
     * selected meta-data vector should be queried, <code>false</code> if each item in the
     * meta-data vector is queried independently of the others
     */
    public boolean getQueryForAllPairs() {
    	return bgMetaDataUsage.isSelected(rbQueryForAllPairs.getModel());
    }
    
    /**
     * Selects and locks the radio button "Query for all Pairs".
     * This is needed by the "Requery Invalid Entries in Page-Count-Matrix" method.
     */
    public void lockQueryForAllPairs() {
    	this.rbQueryForAllPairs.setSelected(true);
    	this.rbQueryForAllPairs.setEnabled(false);
    	this.rbQueryForSingleItems.setEnabled(false);
    }

    /**
     * Selects and locks the radio button "Query for all Pairs".
     * This is needed by the "Requery Invalid Entries in Page-Count-Matrix" method.
     */
    public void lockQueryForSingleItems() {
    	this.rbQueryForSingleItems.setSelected(true);
    	this.rbQueryForAllPairs.setEnabled(false);
    	this.rbQueryForSingleItems.setEnabled(false);
    }

  	/**
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given PageCountsRetriever-instance.
   	 * 
   	 * @param pcrCfg the PageCountsRetriever-instance containing the values for the dialog
   	 */
   	public void setConfig(PageCountsRetrieverConfig pcrCfg) {
   		if (pcrCfg != null) {
   			tfSearchEngineURL.setText(pcrCfg.getSearchEngineURL());
   			sliderNumberOfRetries.setValue(pcrCfg.getNumberOfRetries());
   			sliderIntervalBetweenRetries.setValue(pcrCfg.getIntervalBetweenRetries());
   			tfAdditionalKeywords.setText(pcrCfg.getAdditionalKeywords());
   			rbAfterSearchString.setSelected(pcrCfg.getAdditionalKeywordsAfterSearchString());
   			rbBeforeSearchString.setSelected(!pcrCfg.getAdditionalKeywordsAfterSearchString());
   			rbQueryForAllPairs.setSelected(pcrCfg.getQueryForAllPairs());
   			rbQueryForSingleItems.setSelected(!pcrCfg.getQueryForAllPairs());
   		}
   	}

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
	    // set label according to slider which has been changed
    	JSlider source = (JSlider)e.getSource();
    	if (source == sliderNumberOfRetries)
	        this.currentNumberOfRetries.setText(Integer.toString(source.getValue()));
	    if (source == sliderIntervalBetweenRetries)
	    	this.currentIntervalBetweenRetries.setText(Integer.toString(source.getValue()));
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
    	if (actionEvent.getSource() == btnRetrievePageCounts) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
