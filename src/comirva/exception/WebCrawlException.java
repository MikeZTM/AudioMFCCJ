/*
 * Created on 31.03.2005
 */
package comirva.exception;

/**
 * The WebCrawlException is thrown when an error occurred
 * while accessing the Internet and retrieving the content
 * of web pages.
 * 
 * @author Peter Knees, Markus Schedl
 */
public class WebCrawlException extends Exception {
 
	/**
	 * Creates a new WebCrawlException-instance.
	 */
	public WebCrawlException() {
		super();
	}
	
	/** 
	 * Creates a new WebCrawlException-instance.
	 * 
	 * @param description 	a String containing a message or description of the occurred error
	 */
	public WebCrawlException(String description) {
			super(description);
	}

}
