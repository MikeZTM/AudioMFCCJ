/*
 * Created on 22.12.2004
 */
package comirva.io.web;

import comirva.config.PageCountsRetrieverConfig;
import comirva.data.DataMatrix;

import javax.swing.*;
import java.util.*;


/**
 * This class implements functions for retrieving page counts with
 * any search engine with Google-like interface.
 * 
 * @author Markus Schedl
 */
public class PageCountsRetriever extends Thread {
	// a Vector for the list of search words
	private Vector searchWords;
	// a PageCountsRetrieverConfig-instance containing the settings for the web crawls
	private PageCountsRetrieverConfig pcrCfg;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// page count matrix
	private DataMatrix pageCountMatrix;
	// listMatrices is needed to add the name of the data matrix to the matrix list
	private DefaultListModel listMatrices;
	// Vector containing the loaded matrices
	private Vector matrixList;
		
	/**
	 * Creates a PageCountsRetriever for accessing Google-like search engines
	 * and calculating a page count matrix for the co-occurence of the terms
	 * in the <code>searchList</code> Vector.
	 * 
	 * @param pcrCfg		a PageCountsRetrieverConfig-instance containing the configuration for the web crawls
	 * @param searchWords	a Vector containing the search words for which the (joint) appearance on web pages should be determined				
 	 * @param ml			the Vector to which the name of the DataMatrix should be added after it has been determined by web crawl
	 * @param lm			the DefaultListModel to add the name of the matrix to the UI
	 * @param statusBar		the JLabel represetning the status bar (for writing current loading progress)
	 */
	public PageCountsRetriever(PageCountsRetrieverConfig pcrCfg, Vector searchWords, Vector ml, DefaultListModel lm, JLabel statusBar) {
		this.pcrCfg = pcrCfg;
		this.searchWords = searchWords;
		this.statusBar = statusBar;
		this.matrixList = ml;
		this.listMatrices = lm;
	}
	
	/**
	 * This method is called when the thread is started.
	 * It creates AnySearch-instances for each query, raises the query
	 * and stores the retrieved page counts into a DataMatrix.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// query for all pairs of elements in the meta-data vector?
		if (pcrCfg.getQueryForAllPairs())
			queryForAllPairs();
		else		// use meta-data vector as list and query only for single items in this list
			queryForSingleItems();
  	    // create new data matrix instance
	    if (listMatrices != null)
	    	listMatrices.addElement(pageCountMatrix.getName());
	    if (matrixList != null)
	    	matrixList.addElement(pageCountMatrix);
	    // inform user, that search finished
	    if (statusBar != null)
    		statusBar.setText("Web Crawl finished. Page-Count-Matrix has been added to matrix list.");
	}

	/**
	 * Returns the page count matrix for the co-occurence of the search terms on web pages.
	 * 
	 * @return a DataMatrix with the page count for the co-occurence of the search terms
	 */
	public DataMatrix getPageCountMatrix() {
		return this.pageCountMatrix;
	}

	/**
	 * Raises queries for all pairs of items in the meta-data vector to be processed.
	 */
	private void queryForAllPairs() {
		// define variables for pcrCfg-settings which are use often
		String addKeywords = pcrCfg.getAdditionalKeywords();
		boolean addKeywordsAfterSearchString = pcrCfg.getAdditionalKeywordsAfterSearchString();
		// initialize page count matrix
		this.pageCountMatrix = new DataMatrix();
		// loop through all words in meta-data vector
	    for (int i=0; i<searchWords.size(); i++) {
	    	for (int j=0; j<searchWords.size(); j++) {
			    // set search string
	    		String queryString = new String();
	    		// if additional keywords were specified and should be addded before query, add them now
	    		if ((addKeywords != null) && (addKeywords != "") && (!addKeywordsAfterSearchString)) 
	    			queryString = queryString + addKeywords + " ";
	    		if (i!=j)	// if search terms differ, use both in query
	    			queryString = queryString + "\""  + (String)searchWords.elementAt(i) + "\"+\"" + (String)searchWords.elementAt(j) + "\"";
	    		else		// if search terms equal, use only the first one in query
	    			queryString = queryString + "\""  + (String)searchWords.elementAt(i) + "\"";
	    		// if additional keywords were specified and should be addded after query, add them now
	    		if ((addKeywords != null) && (addKeywords != "") && (addKeywordsAfterSearchString)) 
	    			queryString = queryString + addKeywords;
	    		try {
	    			// create an AnySearch-instance and raise the query
	    			AnySearch as = new AnySearch(pcrCfg, pcrCfg.getSearchEngineURL(), queryString);
	    			// get and store page count
	    	    	pageCountMatrix.addValue(new Double(as.getPageCount()));
	    	    	if (statusBar != null)
	    	    		statusBar.setText("<html>Query <b>" + queryString + "</b> yielded a page count of " + pageCountMatrix.getValueAtPos(i,j).intValue() + "</html>");
	    	    } catch (Exception e) {
	    	    	// error occured -> store -1 in page count matrix
	    	    	pageCountMatrix.addValue(new Double(-1));
	    	    	if (statusBar != null)
	    	    		statusBar.setText("<html>Call for query <b>" + queryString + "</b> failed! - Storing -1 in Page-Count-Matrix</html>");
	    	    }
	    	}
	    	pageCountMatrix.startNewRow();
//	    	pageCountMatrix.printMatrix();
	    }
	    // remove last row since "startNewRow" was called once too often
	    pageCountMatrix.removeLastAddedElement();
//    	pageCountMatrix.printMatrix();
    	// set name of data matrix
	    pageCountMatrix.setName("page-counts (" +  searchWords.size() + "x" + searchWords.size() + ")");		
	}

	/**
	 * Raises queries for all single items in the meta-data vector to be processed.
	 */ 
	private void queryForSingleItems() {
		// define variables for pcrCfg-settings which are use often
		String addKeywords = pcrCfg.getAdditionalKeywords();
		boolean addKeywordsAfterSearchString = pcrCfg.getAdditionalKeywordsAfterSearchString();
		// initialize page count matrix
		this.pageCountMatrix = new DataMatrix();
		// loop through all words in meta-data vector
	    for (int i=0; i<searchWords.size(); i++) {
	    	// set search string
	    	String queryString = new String();
	    	// if additional keywords were specified and should be addded before query, add them now
	    	if ((addKeywords != null) && (addKeywords != "") && (!addKeywordsAfterSearchString)) 
	    		queryString = queryString + addKeywords + " ";
   			//queryString = queryString + "\""  + (String)searchWords.elementAt(i) + "\"";
	    	queryString = queryString + (String)searchWords.elementAt(i);
	    	// if additional keywords were specified and should be addded after query, add them now
	    	if ((addKeywords != null) && (addKeywords != "") && (addKeywordsAfterSearchString)) 
	    		queryString = queryString + addKeywords;
	    	try {
	    		// create an AnySearch-instance and raise the query
	    		AnySearch as = new AnySearch(pcrCfg, pcrCfg.getSearchEngineURL(), queryString);
	    		// get and store page count
	    	   	pageCountMatrix.addValue(new Double(as.getPageCount()));
	    	   	if (statusBar != null)
	    	   		statusBar.setText("<html>Query <b>" + queryString + "</b> yielded a page count of " + pageCountMatrix.getValueAtPos(i,0).intValue() + "</html>");
	    	} catch (Exception e) {
	    	  	// error occured -> store -1 in page count matrix
	    	   	pageCountMatrix.addValue(new Double(-1));
	    	   	if (statusBar != null)
	    	   		statusBar.setText("<html>Call for query <b>" + queryString + "</b> failed! - Storing -1 in Page-Count-Vector</html>");
	    	}
	    	pageCountMatrix.startNewRow();
	    }
	    // remove last row since "startNewRow" was called once too often
	    pageCountMatrix.removeLastAddedElement();
    	// set name of data matrix
	    pageCountMatrix.setName("page-counts (" +  searchWords.size() + "x1)");		
	}

}
