/*
 * Created on 30.03.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a PageCountsRetrieval
 * It is used to pass a configuration to the PageCountsRetriever-instance.
 * 
 * @author Markus Schedl
 */
public class PageCountsRetrieverConfig implements AnySearchConfig {
	private String searchEngineURL;
	private int numberOfRetries;
	private int intervalBetweenRetries;
	private String additionalKeywords;
	private boolean additionalKeywordsAfterSearchString;
	private boolean queryForAllPairs;
	
	/**
	 * Creates a new instance of an PageCountsRetriever-Configuration.
	 *  
	 * @param searchEngineURL							a String containing the URL of the search engine
	 * @param numberOfRetries							the number of retries in case of failure
	 * @param intervalBetweenRetries					the interval between two retries (in seconds)
	 * @param additionalKeywords						additional keywords in the query
	 * @param additionalKeywordsAfterSearchString		whether the additional keywords are to be placed after (or before) the search string 
	 */
	public PageCountsRetrieverConfig(String searchEngineURL, int numberOfRetries, int intervalBetweenRetries, String additionalKeywords, boolean additionalKeywordsAfterSearchString, boolean queryForAllPairs){
		this.searchEngineURL = searchEngineURL;
		this.numberOfRetries = numberOfRetries;
		this.intervalBetweenRetries = intervalBetweenRetries;
		this.additionalKeywords = additionalKeywords;
		this.additionalKeywordsAfterSearchString = additionalKeywordsAfterSearchString;
		this.queryForAllPairs = queryForAllPairs;
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
   	 * Returns the number of the first requested page (usually 0)
   	 * 
   	 * @return 0 since the value of this parameter does not matter in page count retrieval
   	 */
    public int getFirstRequestedPageNumber() {
    	return 0;		// always start with page number 0 (we are only interested in the page counts, so the value of this parameter does not matter)
    }
   	/**
   	 * Returns the number of requested web pages per web crawl. 
   	 * 
   	 * @return	1 since we are only interested in the page counts
   	 */
   	public int getNumberOfRequestedPages() {
   		return 1;	 // always request only one page (since we are only interested in the page counts)
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
     * Returns whether queries should be raised for all pairs of the strings 
     * in the meta-data vector or the selected meta-data vector is processed 
     * sequentially as a list that is queried.
     * 
     * @return	<code>true</code> if all pairwise combinations of the elements in the
     * selected meta-data vector should be queried, <code>false</code> if each item in the
     * meta-data vector is queried independently of the others
     */
    public boolean getQueryForAllPairs() {
    	return this.queryForAllPairs;
    }

}
