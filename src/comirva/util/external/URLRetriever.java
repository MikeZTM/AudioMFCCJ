/*
 * Created on 19.04.2005
 */
package comirva.util.external;

import comirva.config.AnySearchConfig;
import comirva.io.web.AnySearch;

import java.io.*;
import java.net.*;
import java.util.*;
	
/**
 * This class implements a URL retrieval for queries to a search engine.
 * 
 * @author Markus Schedl
 */
public class URLRetriever {
	// number of requested web pages
	private static final int REQUESTED_PAGES = 200;
	private static final int MAX_RETURNED_PAGES = 100;														// maximum number of returned results by the search engine
	private static String STORAGE_PATH = "C:/Research/Data/amg-artists/M/reggae/";							// local path to store the crawled web pages
	private static String ENTITIES_TO_CRAWL = "C:/Research/Data/amg-artists/artists_reggae.txt";			// local file containing entities to crawls (e.g. artists)
	private static boolean RETRIEVE_PAGES = false;															// indicates whether the actual web pages should be retrieved (if false, only the URLs returned by the search engine are stored)
	
	private int lastIndex[];						// to store the index of the last accessible web page for each artist
	private Vector artistsUrls[];					// to store a list of already retrieved urls for every artist				
	
	/**
	 * Creates a new URLRetriever-instance.
	 * 
	 * @param startIndex	the index of the artist list where the crawl should start 
	 */
	public URLRetriever(int startIndex) {
		// create reader to access file
		Vector artists = new Vector();
		try {
			BufferedReader readerFile = new BufferedReader(new FileReader(URLRetriever.ENTITIES_TO_CRAWL));
			String artist = readerFile.readLine();
			while (artist != null) {
				artists.addElement(artist);
				artist = readerFile.readLine();
			}
			// make sure that storage path ends with a slash
			if (STORAGE_PATH.charAt(STORAGE_PATH.length()-1) != '/' && STORAGE_PATH.charAt(STORAGE_PATH.length()-1) != '\\')
				STORAGE_PATH+="/";
			// create output directory if it does not already exist
			File fileOutputPath = new File(STORAGE_PATH);
			if (!fileOutputPath.isDirectory())
				(new File(STORAGE_PATH)).mkdir();
			// init last index
			this.lastIndex = new int[artists.size()];
			// init vector of artists' urls
			this.artistsUrls = new Vector[artists.size()];
			for (int i=0; i<artists.size(); i++)
				this.artistsUrls[i] = new Vector();
		} catch (EOFException eofe) {
		} catch (IOException eofe) {
		}
		URLRetrieverConfig asCfg;			// web crawl configuration
		// multiple crawls if number of requested pages exceeds the
		// maximum number of returned pages from the search engine
		for (int i=0; i<(int)Math.floor((double)REQUESTED_PAGES/MAX_RETURNED_PAGES); i++) {
			// create configuration for web crawl
			asCfg = new URLRetrieverConfig(REQUESTED_PAGES, i*MAX_RETURNED_PAGES); 
			// start web crawl with index 0
			startCrawl(artists, asCfg, startIndex);
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 0)
			new URLRetriever(0);
		else
			new URLRetriever(Integer.valueOf(args[0]).intValue());
	}
	
	private void startCrawl(Vector artists, URLRetrieverConfig asCfg, int startIdx) {
		int curIdx = 0;		// to remember actual position in case of error
		try {
			for (int i=startIdx; i<artists.size(); i++) {
				curIdx = i;	// remember current position
				//************************** ******the query for the search engine
				String query = "\"" + (String)artists.elementAt(i) + "\"+music";
				//**************************************************************//
				AnySearch as = new AnySearch(asCfg, "http://www.google.com", query);
				System.out.println(i + ": issuing query " + query + "\tretrieving pages "+(asCfg.getFirstRequestedPageNumber()+1)+" to "+(asCfg.getFirstRequestedPageNumber()+MAX_RETURNED_PAGES));
				URL[] resultURLs = as.getResultURLs(MAX_RETURNED_PAGES);
				System.out.println("accessible web pages: "+resultURLs.length);
				if (resultURLs != null && resultURLs.length != 0)
					// create directory for current artist
					(new File(STORAGE_PATH + TextFormatTool.removeUnwantedChars((String)artists.elementAt(i)))).mkdir();
				// every url returned
				for (int j=0; j<resultURLs.length; j++) {
					// store current url in artists' url vector if it is not already contained
					if (this.artistsUrls[i].indexOf(resultURLs[j]) == -1) {
						// store new url in artists' url vector
						this.artistsUrls[i].addElement(resultURLs[j]);
						// retrieve
						if (RETRIEVE_PAGES) {
							Runtime.getRuntime().exec("wget -t 2 -T 2 -O " + STORAGE_PATH
									+ TextFormatTool.removeUnwantedChars((String)artists.elementAt(i)) + "/" 
									+ TextFormatTool.leadingDoubleZero(Integer.toString(this.lastIndex[i])) + ".html" + " " 
									+ resultURLs[j].toString());
//							System.out.println(j+"\t"+resultURLs[j].toString());
						}
						// update index of last accessible web page
						this.lastIndex[i]++;
					} else {		// current url is already contained in artists' url vector
//						System.out.println(resultURLs[i].toString() + " already in vector");
					}
				}
				// create file of URLs in directory where retrieved pages of current search term has been stored
				File fileURLList = new File(	STORAGE_PATH + 
						TextFormatTool.removeUnwantedChars((String)artists.elementAt(i)) + 
						"/urls.dat");
				// check if urls.dat already exists
				Vector urls = new Vector();
				if (fileURLList.exists()) {		// exists, read input (to be able to only add new urls)
					BufferedReader br = new BufferedReader(new FileReader(fileURLList));
					String url = br.readLine();
					while (url != null) {
						urls.addElement(url);
						url = br.readLine();
					}
					br.close();	
				}
				// open urls.dat for append
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileURLList, true));
				try {
					for (int j=0; j<this.artistsUrls[i].size(); j++) {
						// add url if not already in urls.dat
						String artistUrl = artistsUrls[i].elementAt(j).toString();
						if (urls != null && !urls.contains(artistUrl))
							bw.write(artistUrl+"\n");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					bw.flush();
					bw.close();
				}
				System.out.println(this.lastIndex[i]+" pages retrieved");
				System.gc();			// to avoid memory leaks
			}
		} catch (Exception e) {
			// restart crawl after position where error occurred
			startCrawl(artists, asCfg, curIdx+1);
		}
	
	}
}

class URLRetrieverConfig implements AnySearchConfig {
	private int firstRequestedPageNumber = 0;
	private int numberRequestedPages = 100;
	
	public URLRetrieverConfig(int numberRequestedPages, int firstRequestedPageNumber) {
		this.numberRequestedPages = numberRequestedPages;
		this.firstRequestedPageNumber = firstRequestedPageNumber;
	}
	/**
	 * Returns the number of retries in case of failure to raise a search query.
	 * 
	 * @return	the number of retries
	 */
	public int getNumberOfRetries() {
		return 2;
	}
   	/**
   	 * Returns the interval between two retries in case of failure (in seconds). 
   	 * 
   	 * @return		the interval between two retries
   	 */
   	public int getIntervalBetweenRetries() {
   		return 5;
   	}
   	/**
   	 * Returns the number of requested web pages per crawl.
   	 * 
   	 * @return	the number of requested web pages
   	 */
   	public int getNumberOfRequestedPages() {
   		return this.numberRequestedPages;
   	}
  	/**
   	 * Returns the number of the first requested page (usually 0)
   	 * 
   	 * @return the number of the first requested page
   	 */
   	public int getFirstRequestedPageNumber() {
   		return this.firstRequestedPageNumber;
   	}
   	
}
