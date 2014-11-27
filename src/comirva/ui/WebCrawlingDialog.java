/*
 * Created on 01.03.2005
 */
package comirva.ui;

import comirva.config.WebCrawlingConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;


/**
 * This class implements a dialog for specifying the parameters for a web crawl.
 * It is shown when the user wished to perform a meta-data-related web crawl.  
 * 
 * @author Markus Schedl
 */
public class WebCrawlingDialog extends JDialog implements ActionListener, ChangeListener {
	// UI-elements
	private JButton btnStartWebCrawl = new JButton();
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
    private JRadioButton rbBeforeSearchString = new JRadioButton("Before Search String", false);
    private JRadioButton rbAfterSearchString = new JRadioButton("After Search String", true);
    private ButtonGroup bgAdditionalKeywordsPlacement = new ButtonGroup();
    private JCheckBox cbStoreURLList = new JCheckBox("Store List of Retrieved URLs", true);
    private JTextField tfPathStoreRetrievedPages = new JTextField();
    private JTextField tfPathExternalCrawler = new JTextField();
    private JSpinner jsNumberOfPages = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 10));
    
    // flag indicatingPageCountsRetriever for the creating instance, if the dialog was closed by clicking of "Retrieve"
    public boolean confirmOperation;

    /**
     * Creates a new instance of the WebCrawling-parameter dialog and initializes it.
     * 
     * @param parent		the Frame of the parent window where the dialog should be displayed
     */
    public WebCrawlingDialog(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initWebCrawlingDialog();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * <p> Initializes the dialog by displaying all labels and sliders, setting title, creating buttons
     * and assigning an ActionListener.</p>
     * To arrange the elements of the dialog box, a GridLayout is used.   
     */
   private void initWebCrawlingDialog() {
    	this.setTitle("Meta-Data-Related Web Crawling - Configuration");
    	// assign text to buttons, set name and assign action listener
    	btnStartWebCrawl.setMnemonic(KeyEvent.VK_S);
    	// set "Crawl"-button as default
    	this.getRootPane().setDefaultButton(btnStartWebCrawl);
    	btnStartWebCrawl.setText("Start Crawling");
    	btnStartWebCrawl.addActionListener(this);
    	btnCancel.setText("Cancel");
    	btnCancel.setMnemonic(KeyEvent.VK_C);
    	btnCancel.addActionListener(this);
    	// set default values for text fields
    	tfSearchEngineURL.setText("http://www.google.com");
    	tfAdditionalKeywords.setText("+music+review");
    	tfPathExternalCrawler.setText("wget");
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
    	// assign change listeners
    	sliderNumberOfRetries.addChangeListener(this);
    	sliderIntervalBetweenRetries.addChangeListener(this);
    	// init grid layout
    	gridLayout.setRows(10);
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
    	panel.add(new JLabel("Maximum Number of Retrieved Pages per Query"));
    	panel.add(jsNumberOfPages);
    	panel.add(new JLabel());
    	panel.add(new JLabel("Storage Path for Retrieved Pages"));
    	panel.add(tfPathStoreRetrievedPages);
    	panel.add(new JLabel());
    	panel.add(new JLabel("Command for External Crawler"));
    	panel.add(tfPathExternalCrawler);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(cbStoreURLList);
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(new JLabel());
    	panel.add(btnStartWebCrawl);
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
     * Returns the number of pages that should be returned by the search engine and
     * subsequently crawled.
     * 
     * @return 	the number of web pages
     */
    public int getNumberOfRequestedPages() {
    	return ((Integer)this.jsNumberOfPages.getValue()).intValue();
    }
    
    /**
     * Returns the root directory where all retrieved web pages are to be stored.
     * 
     * @return	a String containing the path where to retrieved pages should be stored. 
     */
   public String getPathStoreRetrievedPages() {
    	return this.tfPathStoreRetrievedPages.getText();
    }
    
    /**
     * Returns the command needed to start the external crawler.
     * 
     * @return	a String containing the path to an external crawler.
     */
    public String getPathExternalCrawler() {
    	return this.tfPathExternalCrawler.getText();
    }
    
    /**
     * Returns whether a list of all crawled URLs should be stored for every query term. 
     * 
 	 * @return <code>true</code> if a text file containing all crawled URLs is to be stored for every query term 
     * <code>false</code> if information of crawled URLs is to be discarded 
  	 */
    public boolean isStoreURLList() {
    	return this.cbStoreURLList.isSelected(); 
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
   	 * Sets the values of the configuration dialog to the ones specified by the
   	 * given WebCrawling-instance.
   	 * 
   	 * @param wcCfg the WebCrawling-instance containing the values for the dialog
   	 */
   	public void setConfig(WebCrawlingConfig wcCfg) {
   		if (wcCfg != null) {
   			tfSearchEngineURL.setText(wcCfg.getSearchEngineURL());
   			sliderNumberOfRetries.setValue(wcCfg.getNumberOfRetries());
   			sliderIntervalBetweenRetries.setValue(wcCfg.getIntervalBetweenRetries());
   			tfAdditionalKeywords.setText(wcCfg.getAdditionalKeywords());
   			rbAfterSearchString.setSelected(wcCfg.getAdditionalKeywordsAfterSearchString());
   			rbBeforeSearchString.setSelected(!wcCfg.getAdditionalKeywordsAfterSearchString());
   			jsNumberOfPages.setValue(new Integer(wcCfg.getNumberOfRequestedPages()));
   			tfPathStoreRetrievedPages.setText(wcCfg.getPathStoreRetrievedPages());
   	   	    tfPathExternalCrawler.setText(wcCfg.getPathExternalCrawler());
   			cbStoreURLList.setSelected(wcCfg.isStoreURLList());   	    
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
    	if (actionEvent.getSource() == btnStartWebCrawl) {
    		confirmOperation = true;
    		dispose();
    	}
    }

}
