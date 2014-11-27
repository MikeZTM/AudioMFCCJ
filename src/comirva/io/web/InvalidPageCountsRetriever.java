/*
 * Created on 27.04.2005
 */
package comirva.io.web;

import comirva.config.PageCountsRetrieverConfig;
import comirva.data.DataMatrix;

import javax.swing.*;
import java.util.*;


/**
 * This class implements functions for retrieving page counts with
 * any search engine with Google-like interface.
 * It is used to requery invalid entries (-1) in an existing
 * page-count-matrix.
 * 
 * @author Markus Schedl
 */
public class InvalidPageCountsRetriever extends Thread {
	// a Vector for the list of search words
	private Vector searchWords;
	// a PageCountsRetrieverConfig-instance containing the settings for the web crawls
	private PageCountsRetrieverConfig pcrCfg;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// page count matrix
	private DataMatrix pageCountMatrix;

		
	/**
	 * Creates an InvalidPageCountsRetriever for accessing Google-like search engines
	 * and calculating a page count matrix for the co-occurence of the terms
	 * in the <code>searchList</code> Vector.
	 * 
	 * @param pcrCfg			a PageCountsRetrieverConfig-instance containing the configuration for the web crawls
	 * @param searchWords		a Vector containing the search words for which the (joint) appearance on web pages should be determined				
 	 * @param pageCountMatrix	a DataMatrix containing the page-count-matrix with invalid entries
	 * @param statusBar			the JLabel represetning the status bar (for writing current loading progress)
	 */
	public InvalidPageCountsRetriever(PageCountsRetrieverConfig pcrCfg, Vector searchWords, DataMatrix pageCountMatrix, JLabel statusBar) {
		this.pcrCfg = pcrCfg;
		this.searchWords = searchWords;
		this.statusBar = statusBar;
		this.pageCountMatrix = pageCountMatrix;
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
	    // inform user, that search finished
	    if (statusBar != null)
    		statusBar.setText("Web Crawl finished. Page-Count-Matrix has been modified.");
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
	 * But performs the search only for those data items for which the page-count-matrix
	 * contains the value -1. 
	 */
	private void queryForAllPairs() {
		// define variables for pcrCfg-settings which are use often
		String addKeywords = pcrCfg.getAdditionalKeywords();
		boolean addKeywordsAfterSearchString = pcrCfg.getAdditionalKeywordsAfterSearchString();
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
	    			// only query if page count matrix contains the value -1 at current position
	    			if (this.pageCountMatrix.getValueAtPos(i,j).doubleValue() == -1) {
	    				// create an AnySearch-instance and raise the query
	    				AnySearch as = new AnySearch(pcrCfg, pcrCfg.getSearchEngineURL(), queryString);
	    				// get and store page count
	    				pageCountMatrix.getRow(i).setElementAt(new Double(as.getPageCount()), j);
	    				if (statusBar != null)
	    					statusBar.setText("<html>Query <b>" + queryString + "</b> yielded a page count of " + pageCountMatrix.getValueAtPos(i,j).intValue() + "</html>");
	    			}
	    		} catch (Exception e) {
	    			// error occured -> store -1 in page count matrix
	    			pageCountMatrix.getRow(i).setElementAt(new Double(-1), j);
	    			if (statusBar != null)
	    				statusBar.setText("Call for query <b>" + queryString + "</b> failed! - Storing -1 in Page-Count-Matrix");
	    		}
	    		
	    	}
	    }
	}

	/**
	 * Raises queries for all single items in the meta-data vector to be processed.
	 * But performs the search only for those data items for which the page-count-matrix
	 * contains the value -1. 
	 */
	private void queryForSingleItems() {
		// define variables for pcrCfg-settings which are use often
		String addKeywords = pcrCfg.getAdditionalKeywords();
		boolean addKeywordsAfterSearchString = pcrCfg.getAdditionalKeywordsAfterSearchString();
		// loop through all words in meta-data vector
	    for (int i=0; i<searchWords.size(); i++) {
	    	// set search string
	    	String queryString = new String();
	    	// if additional keywords were specified and should be addded before query, add them now
	    	if ((addKeywords != null) && (addKeywords != "") && (!addKeywordsAfterSearchString)) 
	    		queryString = queryString + addKeywords + " ";
   			queryString = queryString + "\""  + (String)searchWords.elementAt(i) + "\"";
	    	// if additional keywords were specified and should be addded after query, add them now
	    	if ((addKeywords != null) && (addKeywords != "") && (addKeywordsAfterSearchString)) 
	    		queryString = queryString + addKeywords;
	    	try {
	    		// only query if page count matrix contains the value -1 at current position
    			if (this.pageCountMatrix.getValueAtPos(i,0).doubleValue() == -1) {
    				// create an AnySearch-instance and raise the query
    				AnySearch as = new AnySearch(pcrCfg, pcrCfg.getSearchEngineURL(), queryString);
    				// get and store page count
    				pageCountMatrix.getRow(i).setElementAt(new Double(as.getPageCount()), 0);
    				if (statusBar != null)
    					statusBar.setText("<html>Query <b>" + queryString + "</b> yielded a page count of " + pageCountMatrix.getValueAtPos(i,0).intValue() + "</html>");
    			}
	    	} catch (Exception e) {
	    	  	// error occured -> store -1 in page count matrix
	    		pageCountMatrix.getRow(i).setElementAt(new Double(-1), 0);
	    	   	if (statusBar != null)
	    	   		statusBar.setText("Call for query <b>" + queryString + "</b> failed! - Storing -1 in Page-Count-Vector");
	    	}
	    }
	}

}
