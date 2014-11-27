/*
 * Created on 19.04.2005
 */
package comirva.io.web;


import comirva.config.WebCrawlingConfig;
import comirva.util.external.*;
//import cp.net.*;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JLabel;

/**
 * This class implements a simple web crawler
 * that uses the external tool wget (or the internal W3C-implementation).
 * It accesses a search engine to get a list of
 * web pages and crawls them.
 * The results are stored in a local path.
 * 
 * @author Markus Schedl
 */
public class WebCrawling extends Thread {
	private static int MAX_RETURNED_PAGES = 100;	// the maximum number of results on one page returned by the search engine (e.g. 100 for Google) 
//	private static String STORAGE_PATH = "/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MB";		// local path to store the crawled web pages
	
	private String storagePath;					// (root) path to store the retrieved pages
	private int lastIndex[];						// to store the index of the last accessible web page for each search query
	private Vector searchQueryUrls[];				// to store a list of already retrieved urls for every search query
	private JLabel statusBar;						// to access CoMIRVA's status bar
	private WebCrawlingConfig wcCfg;				// the web crawling configuration to be used
	private Vector searchWords;						// a list containing the search queries
	
	/**
	 * Creates a new WebCrawling-instance.
	 * 
	 * @param wcCfg			a WebCrawlingConfig-instance containing the settings for the web crawl
	 * @param searchWords	a Vector containing all search queries
	 * @param statusBar		the JLabel of CoMIRVA's status bar to inform the user 
	 */
	public WebCrawling(WebCrawlingConfig wcCfg, Vector searchWords, JLabel statusBar) {
		// set parameters
		this.storagePath = wcCfg.getPathStoreRetrievedPages();
		this.statusBar = statusBar;
		this.wcCfg = wcCfg;
		this.searchWords = searchWords;
	}
	
	/**
	 * Initializes and starts the web crawl.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// create storage path directory and init data structures
		try {
			// make sure that storage path ends with a slash
			if (this.storagePath.charAt(this.storagePath.length()-1) != '/' && this.storagePath.charAt(this.storagePath.length()-1) != '\\')
				this.storagePath+="/";
			// create output directory if it does not already exist
			File fileOutputPath = new File(this.storagePath);
			if (!fileOutputPath.isDirectory())
				fileOutputPath.mkdir();
//				Runtime.getRuntime().exec("mkdir " + this.storagePath);
			// init last index
			this.lastIndex = new int[searchWords.size()];
			// init vector of artists' urls
			this.searchQueryUrls = new Vector[searchWords.size()];
			for (int i=0; i<searchWords.size(); i++)
				this.searchQueryUrls[i] = new Vector();
		} catch (Exception e) {
		}	
		// multiple crawls if number of requested pages exceeds the
		// maximum number of returned pages from the search engine
		WebCrawlingConfig wc2Cfg;
		for (int i=0; i<(int)Math.ceil((double)wcCfg.getNumberOfRequestedPages()/MAX_RETURNED_PAGES); i++) {
			// create configuration for web crawl
			wc2Cfg = new WebCrawlingConfig(wcCfg.getSearchEngineURL(),
					wcCfg.getNumberOfRetries(),
					wcCfg.getIntervalBetweenRetries(),
					i*MAX_RETURNED_PAGES,
					wcCfg.getAdditionalKeywords(),
					wcCfg.getAdditionalKeywordsAfterSearchString(),
					wcCfg.getNumberOfRequestedPages(),
					this.storagePath,
					wcCfg.getPathExternalCrawler(),
					wcCfg.isStoreURLList());
			// start web crawl with index 0
			startCrawl(this.searchWords, wc2Cfg, wcCfg.getFirstRequestedPageNumber());
		}
		// write list of retrieved URLs to a file for every search query
		// if this was requested by the user
		if (wcCfg.isStoreURLList()) {
			// for every search query
			for (int i=0; i<this.searchQueryUrls.length; i++) {
				// get vector of all URLs
				Vector v = this.searchQueryUrls[i];
				Enumeration e = v.elements();
				// create file in directory where retrieved pages of current search term has been stored
				try {
					File fileURLList = new File(	this.storagePath + 
													TextFormatTool.removeUnwantedChars((String)searchWords.elementAt(i)) + 
													"/urls.dat");
					BufferedWriter bw = new BufferedWriter(new FileWriter(fileURLList));
					// for every URL of current search query
					while (e.hasMoreElements()) {
						// get URL
						URL url = (URL)e.nextElement();
						// write URL to file
						bw.write(url.toString()+"\n");
					}
					bw.flush();
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
		}
		// inform user
	    if (statusBar != null)
    		statusBar.setText("Web Crawl finished.");
	}

	/**
	 * Performs the web crawl.
	 * 
	 * @param searchWords		a Vector containing all search queries
	 * @param wcCfg				a WebCrawlingConfig-instance containing the settings for the web crawl			
	 * @param startIdx			the position in the Vector <code>searchWords</code> at which the crawl starts
	 */
	private void startCrawl(Vector searchWords, WebCrawlingConfig wcCfg, int startIdx) {
		int curIdx = 0;		// to remember actual position in case of error
		try {
			for (int i=startIdx; i<searchWords.size(); i++) {
				curIdx = i;	// remember current position
			    // create search string
	    		String addKeywords = wcCfg.getAdditionalKeywords();
				boolean addKeywordsAfterSearchString = wcCfg.getAdditionalKeywordsAfterSearchString();
				String query = new String();
				// if additional keywords were specified and should be addded before query, add them now
	    		if ((addKeywords != null) && (addKeywords != "") && (!addKeywordsAfterSearchString)) 
	    			query = addKeywords + " ";
	    		// add actual query word
//	    		query = query + "\"" + (String)searchWords.elementAt(i) + "\"";		// use exact (phrase) search
	    		query = query + (String)searchWords.elementAt(i);					// use conjuctive search
	    		// if additional keywords were specified and should be addded after query, add them now
	    		if ((addKeywords != null) && (addKeywords != "") && (addKeywordsAfterSearchString)) 
	    			query = query + addKeywords;
	    		// create a new AnySearch-instance
				AnySearch as = new AnySearch(wcCfg, wcCfg.getSearchEngineURL(), query);
//				System.out.println("processing artist "+(String)searchWords.elementAt(i)+", retrieving pages "+(wcCfg.getFirstRequestedPageNumber()+1)+" to "+(wcCfg.getFirstRequestedPageNumber()+MAX_RETURNED_PAGES));
			    // inform user of progress
			    if (statusBar != null)
		    		statusBar.setText("<html>Processing query <b>"+query+"</b>, pages "+(wcCfg.getFirstRequestedPageNumber()+1)+" to "+(wcCfg.getFirstRequestedPageNumber()+MAX_RETURNED_PAGES)+"</html>");
				URL[] resultURLs = as.getResultURLs(MAX_RETURNED_PAGES);
//				System.out.println("accessible web pages: "+resultURLs.length);
			    // inform user of progress"+
			    if (statusBar != null)
		    		statusBar.setText("<html>Retrieving approximately "+resultURLs.length+" pages for query <b>"+query+"</b></html>");
				if (resultURLs != null && resultURLs.length != 0) {
					// create directory for current search term
//					Runtime.getRuntime().exec("mkdir " + this.storagePath + TextFormatTool.removeUnwantedChars((String)searchWords.elementAt(i)));
					File dirTerm = new File(this.storagePath + TextFormatTool.removeUnwantedChars((String)searchWords.elementAt(i)));
					dirTerm.mkdir();
				}	
				// every url returned
				for (int j=0; j<resultURLs.length; j++) {
					// store current url in searchWords' url vector if it is not already contained
					if (this.searchQueryUrls[i].indexOf(resultURLs[j]) == -1) {
						// store new url in searchWords' url vector
						this.searchQueryUrls[i].addElement(resultURLs[j]);
						// retrieve			

						// code for using external wget
						Runtime.getRuntime().exec(wcCfg.getPathExternalCrawler()
								+ " -t " + wcCfg.getNumberOfRetries()
								+ " -T " + wcCfg.getIntervalBetweenRetries()
								+ " -O " + this.storagePath 
								+ TextFormatTool.removeUnwantedChars((String)searchWords.elementAt(i)) + "/" 
								+ TextFormatTool.leadingDoubleZero(Integer.toString(this.lastIndex[i])) + ".html" + " " 
								+ resultURLs[j].toString());
						
// code for using internal wget from cp.net
//						WGet wget = new WGet();
//						URL url = new URL(resultURLs[j].toString());
//						File store = new File(this.storagePath + TextFormatTool.removeUnwantedChars((String)searchWords.elementAt(i)) + "/" + TextFormatTool.leadingDoubleZero(Integer.toString(this.lastIndex[i])) + ".html");
//						// retrieve content
//						String content = wget.get(url);
//						// write content to file
//						FileWriter fw = new FileWriter(store);
//						BufferedWriter bw = new BufferedWriter(fw);
//						bw.write(content);
//						bw.flush();
//						fw.flush();
//						bw.close();
//						fw.close();
// 						System.out.println(j+"\t"+resultURLs[j].toString());						
						// update index of last accessible web page
						this.lastIndex[i]++;
					} else {		// current url is already contained in searchWords' url vector
//						System.out.println(resultURLs[i].toString() + " already in vector");
					}
				}
//				System.out.println(this.lastIndex[i]+" pages retrieved");	
			    // inform user of progress
			    if (statusBar != null)
		    		statusBar.setText("<html>" + this.lastIndex[i] + " pages for query <b>"+query+"</b> retrieved</html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// restart crawl after position where error occurred
			startCrawl(searchWords, wcCfg, curIdx+1);
		}
	
	}
}
