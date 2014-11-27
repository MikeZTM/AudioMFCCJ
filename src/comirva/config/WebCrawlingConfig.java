/*
 * Created on 23.11.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a simple WebCrawler.
 * The WebCrawler uses an arbitrary search engine to get a bunch 
 * of urls and then crawls them.
 * It is used to pass a configuration to the WebCrawler instance.
 * 
 * @author Markus Schedl
 */
public class WebCrawlingConfig implements AnySearchConfig {
	private String searchEngineURL;
	private int numberOfRetries;
	private int intervalBetweenRetries;
	private int firstRequestedPageNumber;
	private String additionalKeywords;
	private boolean additionalKeywordsAfterSearchString;
	private int numberOfPages;
	private String pathStoreRetrievedPages;
	private String pathExternalCrawler;
	private boolean isStoreURLList;
	
	/**
	 * Creates a new instance of a WebCrawling-Configuration.
	 *  
	 * @param searchEngineURL							a String containing the URL of the search engine
	 * @param numberOfRetries							the number of retries in case of failure
	 * @param intervalBetweenRetries					the interval between two retries (in seconds)
	 * @param firstRequestedPageNumber					the number (index) of the first requested page 
	 * @param additionalKeywords						additional keywords in the query
	 * @param additionalKeywordsAfterSearchString		whether the additional keywords are to be placed after (or before) the search string
	 * @param numberOfPages								number of pages to retrieve
	 * @param pathStoreRetrievedPages					local path where the retrieved html documents should be stored
	 * @param pathExternalCrawler						command to run wget
	 * @param isStoreURLList							flag to determine whether a list of all retrieved URLs should be stored for every query term
	 */
	public WebCrawlingConfig(String searchEngineURL, int numberOfRetries, int intervalBetweenRetries, int firstRequestedPageNumber, String additionalKeywords, boolean additionalKeywordsAfterSearchString, int numberOfPages, String pathStoreRetrievedPages, String pathExternalCrawler, boolean isStoreURLList){
		this.searchEngineURL = searchEngineURL;
		this.numberOfRetries = numberOfRetries;
		this.intervalBetweenRetries = intervalBetweenRetries;
		this.firstRequestedPageNumber = firstRequestedPageNumber;
		this.additionalKeywords = additionalKeywords;
		this.additionalKeywordsAfterSearchString = additionalKeywordsAfterSearchString;
		this.numberOfPages = numberOfPages;
		this.pathStoreRetrievedPages = pathStoreRetrievedPages;
		this.pathExternalCrawler = pathExternalCrawler;
		this.isStoreURLList = isStoreURLList;
	}
	
	/**
	 * Returns the URL of the search engine to be used.
	 * 
	 * @return 	a String containing the URL of the search engine.
	 */
	public String getSearchEngineURL() {
		return this.searchEngineURL;
	}
	/**
	 * Returns the number of retries in case of failure to raise a search query.
	 * 
	 * @return	the number of retries
	 */
	public int getNumberOfRetries() {
		return this.numberOfRetries;
	}
   	/**
   	 * Returns the interval between two retries in case of failure (in seconds). 
   	 * 
   	 * @return		the interval between two retries
   	 */
   	public int getIntervalBetweenRetries() {
   		return this.intervalBetweenRetries;
   	}
  	/**
   	 * Returns the number of the first requested page (usually 0).
   	 * Setting the value of this parameter is necessary if the search
   	 * engine used does not provide more than a fixed number of results.
   	 * Google, for example, limits this number to 100.
   	 * 
   	 * @return the number of the first requested page
   	 */
   	public int getFirstRequestedPageNumber() {
   		return this.firstRequestedPageNumber;
   	}
    /**
     * Returns the additional keywords to be added to the search string.
     * 
     * @return a String containing the additional keywords
     */
    public String getAdditionalKeywords() {
    	return this.additionalKeywords;
    }
    
    /**
     * Returns whether additional keywords are to be placed after the search string or before.
     *  
     * @return <code>true</code> if additional keywords should be placed after the search string, 
     * <code>false</code> if they are placed before the search string
     */
    public boolean getAdditionalKeywordsAfterSearchString() {
    	return this.additionalKeywordsAfterSearchString;
    }
    
    /**
     * Returns the number of pages that should be returned by the search engine and
     * subsequently crawled.
     * 
     * @return 	the number of web pages
     */
   public int getNumberOfRequestedPages() {
    	return this.numberOfPages;
   }

   /**
    * Returns the root directory where all retrieved web pages are to be stored.
    * 
    * @return	a String containing the path where to retrieved pages should be stored. 
    */
   public String getPathStoreRetrievedPages() {
	   return this.pathStoreRetrievedPages;
   }
   
   /**
    * Returns the command needed to start the external crawler.
    * 
    * @return	a String containing the path to an external crawler.
    */
   public String getPathExternalCrawler() {
	   return this.pathExternalCrawler;
   }
   
   /**
    * Returns whether a list of all crawled URLs should be stored for every query term. 
    * 
	* @return <code>true</code> if a text file containing all crawled URLs is to be stored for every query term 
    * <code>false</code> if information of crawled URLs is to be discarded 
 	*/
   public boolean isStoreURLList() {
	   return this.isStoreURLList;
   }
   
}
