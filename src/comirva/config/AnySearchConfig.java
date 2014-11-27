/*
 * Created on 30.03.2005
 */
package comirva.config;

/**
 * This interface defines general configuration settings
 * for web crawls using the AnySearch-class of the package comirva.io.searchengine.
 * 
 * @author Markus Schedl
 */
public interface AnySearchConfig {
	/**
	 * Returns the number of retries in case of failure to raise a search query.
	 * 
	 * @return	the number of retries
	 */
	public int getNumberOfRetries();
   	/**
   	 * Returns the interval between two retries in case of failure (in seconds). 
   	 * 
   	 * @return		the interval between two retries
   	 */
   	public int getIntervalBetweenRetries();
   	/**
   	 * Returns the number of requested web pages per crawl.
   	 * 
   	 * @return	the number of requested web pages
   	 */
   	public int getNumberOfRequestedPages();
   	/**
  	 * Returns the number of the first requested page (usually 0)
   	 * 
   	 * @return the number of the first requested page
   	 */
   	public int getFirstRequestedPageNumber();
}
