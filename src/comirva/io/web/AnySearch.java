/*
 * Created on 25.09.2003
 */

package comirva.io.web;

import comirva.config.AnySearchConfig;
import comirva.exception.WebCrawlException;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;


/**
 * This class provides simple access to the results
 * of search engines using Google-like parameters.
 * It can be used directly to crawl the web by defining
 * a search engine's URL and a query.
 * 
 * @author Peter Knees, modified by Markus Schedl
 */

public class AnySearch {

	public static int RESULTS_TO_REQUEST = 50;
	public static int MAX_RETRIES = 5;
	public static int RETRY_INTERVAL = 10000;
	public static int MAX_WAITTIME = 60000; // 1 min time to retrieve content

	private final int MAX_REDIRECTS = 7;
	private String content = "";
	private String plaintext = "";
	private URL url;
	private Vector redirectStations = new Vector();
	private int redirects = 0;
	private StringBuffer contentBuffer = new StringBuffer();
	private boolean timeout = false;
	private ContentReceiver cr;
	
	
	/**
	 * Creates a new AnySearch-instance to crawl the web. 
	 * 
	 * @param asCfg					an AnySearchConfig containing the configuration
	 * @param engineURL				a String with the URL of the search engine to be used
	 * @param query					a String specifying the exact search query
	 * @throws WebCrawlException
	 */
	public AnySearch(AnySearchConfig asCfg, String engineURL, String query) throws WebCrawlException {

		// some basic configuration
		AnySearch.RESULTS_TO_REQUEST = asCfg.getNumberOfRequestedPages();
		AnySearch.MAX_RETRIES = asCfg.getNumberOfRetries();
		AnySearch.RETRY_INTERVAL = asCfg.getIntervalBetweenRetries()*1000;		// convert secs to msecs
	
		url = generateSearchURL(engineURL, query, asCfg.getFirstRequestedPageNumber());

		Runtime r = Runtime.getRuntime();
		for (int i=0; i<=MAX_RETRIES; i++) {
			if (i==MAX_RETRIES) throw new WebCrawlException(cr.getMessage());
			r.gc();
			cr = new ContentReceiver();
			cr.start();
			try {
				cr.join(MAX_WAITTIME);
			}
			catch (InterruptedException e) {	
			}
			if (cr.getStatus() == false) {
				try {
					Thread.sleep(RETRY_INTERVAL);
				}
				catch (InterruptedException ire) {
				}
			}
			else
				break;
		}
		r.gc();
		
	}
	
	/**
	 * Returns the number of web pages the search engine returned for the query.
	 * 
	 * @return the number of web pages found
	 */
	public int getPageCount() {
		// extract number of pages by parsing the content of the received results page
		// search for "results x-y of about z for ..."
		// z is the number we are interested in
		char numberSeparator = ','; 				// decimal separator
		// the following two variables are needed to extract the page count from the result page of the search engine
		String textBeforePageCount = "of about";	// text which is placed directly before the page count
		String textAfterPageCount  = "for";			// text which is placed directly after the page count
		int indexStart;
		int indexEnd;		
		// looking for first occurence of "about"
		indexStart = content.toLowerCase().indexOf(textBeforePageCount);
		// page counter found?
		if (indexStart != -1) {		
			String contentStartingAtPageCounter = content.substring(indexStart);
			// looking for first occurence of "for" in substring starting at "of about"
			indexEnd = contentStartingAtPageCounter.indexOf(textAfterPageCount);
			// page counter found
			if (indexEnd != -1) {
				String subStringPageCounterTemp = contentStartingAtPageCounter.substring(0, indexEnd);
				// now we have something like "about <b>z</b> as String
				// extract the text in the <b>-tag
				indexStart = subStringPageCounterTemp.indexOf("<b>");
				indexEnd = subStringPageCounterTemp.indexOf("</b>");
				if ((indexStart != -1) && (indexEnd != -1)) {
					String pageCounterTemp = contentStartingAtPageCounter.substring(indexStart+3, indexEnd);
					String pageCounter = "";
					// eliminate commas (,) in page counter
					for (int i=0; i<pageCounterTemp.length(); i++) {
						if (pageCounterTemp.charAt(i) != numberSeparator)
							pageCounter = pageCounter + String.valueOf(pageCounterTemp.charAt(i));
					}
					// convert to Integer and return
					Integer numberOfPages = new Integer(pageCounter);
					return numberOfPages.intValue();
				}
			}			
		}	
		// if this is reached, an error occured while parsing the content of the query -> return 0 pages
		return 0;
	}

	
	/**
	 * Returns an URL-array with the URLs that the query to the search engine yielded.
	 * 
	 * @param maxNumber		the maximum number of returned URLs (if more URLs than <code>maxNumber</code> were found, return only <code>maxNumber</code>)
	 * @return					a URL[] containing the URLs
	 */
	public URL[] getResultURLs(int maxNumber) {
		Vector urls = new Vector();
		int numberFound = 0;
		
		int index = 0;
		while ((index = content.toLowerCase().indexOf("<a", index)) != -1) {
			if ((index = content.toLowerCase().indexOf("href", index)) == -1) 
				break;
			if ((index = content.toLowerCase().indexOf("=", index)) == -1) 
				break;
			index++;
			
			String remaining = content.substring(index);
			StringTokenizer st 
				  = new StringTokenizer(remaining, "\t\n\r\">#");
			String strLink = st.nextToken();
			
			URL urlLink;
			try {
				urlLink = new URL(url, strLink);
				strLink = urlLink.toString();
			} catch (MalformedURLException e) {
				continue;
			}
			
			// let the search engine-specific object have a look at this
			if ((strLink = inspectURLString(strLink))== null)
				continue;
			try {
				urlLink = new URL(urlLink, strLink);
			} catch (MalformedURLException e) {
				continue;
			}
			// check to see if this URL is already going to be searched
			if ((!urls.contains(urlLink))) {
				urls.addElement(urlLink);
				numberFound++;
			}
			else {
				continue;
			}
			if (maxNumber > 0 && numberFound >= maxNumber)
				break;
		}
		if (urls.size() == 0) return null;
		URL[] foundUrls = new URL[urls.size()];
		for (int i=0; i<foundUrls.length; i++) {
			foundUrls[i] = (URL)(urls.elementAt(i));
		}
		return foundUrls;
	}
	
	/**
	 * Indicates whether the connection to retrieve the full page exceeded the time limit.
	 *  
	 * @return true, if a time out occurred, false otherwise
	 */
	public boolean timedOut() {
		return timeout;
	}
	
	/**
	 * Builds the URL for the search by converting special characters.
	 * 
	 * @param engineURL		a String representing the URL of the search engine
	 * @param query			a String containing the query to be raised
	 * @param start			the start index (for more than 100 pages to be returned)
	 * @return					the URL used for the web crawl
	 * @throws WebCrawlException
	 */
	private URL generateSearchURL(String engineURL, String query, int start) throws WebCrawlException {
		
		StringBuffer query1 = new StringBuffer();
		for (int i=0; i<query.length(); i++) {
			switch (query.charAt(i)) {
			case ' ': query1.append("+"); break;
			case '+': query1.append("%2B"); break;
			case '"': query1.append("%22"); break;
			case '&': query1.append("%26"); break;
			case '%': query1.append("%25"); break;
			case ',': query1.append("%2C"); break;
			case '.': query1.append("%2E"); break;
			default: query1.append(query.charAt(i));
			}
		}
		query=query1.toString();
		
		String searchString = engineURL+"/search?as_q=" + query + "&num="+RESULTS_TO_REQUEST+
								"&start="+Integer.toString(start);
//								+"&as_ft=e&as_filetype=&as_qdr=all&as_occt=any";//&hl=de
//		System.out.println(searchString);
		URL url = null;
		try { 
			url = new URL(searchString);
		}
		catch (MalformedURLException e) {
			throw new WebCrawlException("malformed URL: " + searchString);
		}
		return url;
	}

	/**
	 * Discards "garbage" from a returned URL.
	 * 
	 * @param url			a String holding the URL
	 * @return				the cleaned URL as String 
	 */
	private String inspectURLString(String url) {
		if (url.indexOf("google") != -1 ||
			url.indexOf("search?q=cache") != -1 || 
			url.endsWith(".pdf") || 
			url.endsWith(".ps") || 
			url.endsWith(".doc") || 
			url.endsWith(".rtf")|| 
			url.endsWith(".ppt") || 
			url.endsWith(".pps") || 
			url.endsWith(".xls")) {
			return null;
		}
		URL urlLink;
		try {
			urlLink = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		// only look at http links
		if (urlLink.getProtocol().compareTo("http") != 0)
			return null;
		return url;
	}
	
	private class ContentReceiver extends Thread {
		private boolean success = false;
		private String message = "retrieving page takes too long - skipping.";
		
		/**
		 * constructor
		 *
		 */
		public ContentReceiver() {
		}
		
		/**
		 * 
		 * @return status
		 */
		public boolean getStatus() {
			return success;
		}
		
		/**
		 * 
		 * @return msg
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * retrieval thread
		 */
		public void run() {
			Hashtable headerinfos = new Hashtable();
			// doesn't work - just take 80
			//int assumedport = url.getPort();
			//if (assumedport == -1) assumedport = 80;
			Socket s;
			try {
				 s = new Socket(url.getHost(), 80);
			}
			catch (UnknownHostException uhe) {
				message = uhe.getMessage();
				return;
			}
			catch (IOException ioe) {
				message = "failed to connect.";
				return;
			}
			try {
				// SET THE SOCKET TIMEOUT
				s.setSoTimeout(10000);
			}
			catch (SocketException se) {
				message = "failed to connect.";
				return;
			}
			OutputStream out;
			try {
				out = s.getOutputStream();
			}
			catch (IOException ioe) {
				message = "failed to connect.";
				return;
			}
			String query = "";
			if (url.getQuery() != null)
				query = "?" + url.getQuery();
			//HTTP1.0
			//byte[] bytestring = new String("GET " + url.getPath()+ query + " HTTP/1.0\r\n\r\n").getBytes();
			//HTTP1.1
			byte[] bytestring 
				= new String("GET " + url.getPath()+ query + " HTTP/1.1\r\n" + "host: " + url.getHost() + "\r\n\r\n").getBytes();
			try {
				out.write(bytestring);
				out.flush();
			}
			catch (IOException ioe) {
				message = "error while sending request.";
				return;
			}
			InputStream in;
			try {
				in = s.getInputStream();
			}
			catch (IOException ioe) {
				message = "error while trying to access stream.";
				return;
			}
			BufferedReader in2
			   = new BufferedReader(new InputStreamReader(in));
			try {
				String line=in2.readLine();
				if (line.matches("HTTP/1.[01] 20[0-9].*")) {
					while (!(line=in2.readLine()).equals("")) {
						int sep = line.indexOf(": ");
						if (sep==-1) continue;
						String key = line.substring(0, sep);
						String val = line.substring(sep+2);
						headerinfos.put(key.toLowerCase(), val.toLowerCase());
					}
					// something from HTTP 1.1 - ignore next line (some strange numbers)
					in2.readLine();
					String strLen = (String)headerinfos.get("content-length");
					if (strLen == null) {
						contentBuffer = new StringBuffer();
						try {
							// newline and 0 is also a reason to stop (?)
							// lets take 0 and newline too (?)
							boolean newline = false;
							boolean zeroline = false;
							while ((line=in2.readLine()) != null) {
								if (newline && line.equals("0"))
									break;
								else
									newline = false;
								if (line.equals("")) newline = true;
							
								if (zeroline && line.equals(""))
									break;
								else
									zeroline = false;
								if (line.equals("0")) zeroline = true;	
								contentBuffer.append(line);
							}
						}
						catch (OutOfMemoryError oome) {
						}
						content = contentBuffer.toString();//.toLowerCase();
					}
					else {
						try {
							int len = Integer.parseInt(strLen);
							byte[] data = new byte[len];
							int x = in.read(data);
							content = new String(data);//.toLowerCase();
						}
						catch (OutOfMemoryError oome) {
							content = "";
						}
					}
				}
				else if (line.matches("HTTP/1.[01] 30[0-9].*")) {
					if (redirects > MAX_REDIRECTS) {
						message = "too many redirects ... skipping";
						return;
					}
					//System.out.println("\tredirecting...");
					while (!(line=in2.readLine()).equals("")) {
						int sep = line.indexOf(": ");
						if (sep==-1) continue;
						String key = line.substring(0, sep);
						String val = line.substring(sep+2);
						headerinfos.put(key.toLowerCase(), val.toLowerCase());
					}
					String loc = (String)headerinfos.get("location");
					if (loc == null) {
						message = "redirection, but no alternative location.";
						return;
					}
					redirectStations.addElement(url);
					URL redirect;
					try {
						redirect = new URL(url, loc);
					}
					catch (MalformedURLException mue) {
						message = "malformed redirection URL - " + mue.getMessage();
						return;
					}
					if (redirectStations.contains(redirect)) { 
						message = "cyclic redirection.";
						return;
					}
					url = redirect;
					redirects++;
					in2.close();
					in.close();
					out.close();
					s.close();
					// retry the whole thing
					run();	
				}
				else {
					in2.close();
					in.close();
					out.close();
					s.close();
					message = line;
					return;
				}
			}
			catch (AccessControlException ace) {
				Permission perm = ace.getPermission();
				message = perm.getName() + " - " + perm.getActions();
				return;
			} 
			catch (SocketTimeoutException stoe) {
				if (content.equals(""))
					content = contentBuffer.toString().toLowerCase();
				if (!content.equals("")) {
					timeout = true;
				}
				else {
					message = stoe.getMessage() + " - no content retrieved";
					return;
				}
			}
			catch (NullPointerException npe) {
				message = "malformed response.";
				return;
			}
			catch (IOException ioe) {
				message = "couldn't open URL " + url.toString();
				return;
			}
			try {
				in2.close();
				in.close();
				out.close();
				s.close();
			}
			catch (IOException ioe) {
				message = ioe.getMessage();		
			}
			success = true;
		}
		
	}
}


