package comirva.util.external.retrieval;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * This class implements the retrieval of a set of web pages generated by
 * the class CrawlListCreator. The external tool wget is used and some
 * stategies to prevent continuously retrieving pages from the same
 * web site are implemented.
 * 
 * @author mms
 */
public class GoldenRetriever extends Thread {
	// parameters related to storing the fetched URLs
	public static final File ROOT_DIR = new File("/media/AGMIS2/exalead_4th/M/");								// the root path where all artists dir reside and where the retrieved pages are stored 
	public static final File URL_FILE = new File("/media/AGMIS2/exalead_4th/crawling.txt");					// a text file containing all artist's URLs that should be retrieved
	public static final File PROCESSED_IDX_FILE = new File("/media/AGMIS2/exalead_4th/processed_idx.txt");		// a text file containing the indices of the URLs already retrieved (indices wrt file URL_FILE)
	public static final File WGET = new File("wget");															// complete path to wget
	public static final boolean CHECK_FOR_DIR_STRUCTURE = true;												// ensure that complete directory structure exists before retrieving URLs with wget; set to false if you are sure that the dir structure exists (this speeds up the whole process) 

	// parameters that restrict the files which are fetched
	public static int START_OFFSET = 0;													// the start offset (number of URLs to be skipped from the beginning of the URL_FILE) 
	public static int END_OFFSET = 99;													// the end offset 
	public static final int MAX_SKIP_URLS = 99;											// the number of URLs that are at most skipped in the random selection process - extracted from the URL_FILE which are processed at one run (since not all URLs can be held in memory)
	public static final int[] PAGE_NO_RANGE = {1,2,3,4,5,6,7,8,9,10};				 	// include only the given html-file-numbers (ranks returned by the search engine) in the fetching process
	public static final boolean USE_PAGE_NO_RANGE_FILTER = false;						// activate/deactivate the html-file-number-filter

	// parameters that control the network traffic
	public static final int MAX_PARALLEL_DOWNLOADS = 30;								// the maximum number of wget-instances that run at the same time
	public static final long WAIT_BETWEEN_RETRIEVALS_FROM_SAME_HOST = 2000;			// time in milliseconds that must be waited between two subsequent retrievals from the same host 
	public static final int EQUAL_HOST_LEVELS = 2;										// the number of the top-most domains in a host name that are analyzed to ensure a certain time between two queries to the "same" host. This parameter defines the equality of two host names (if set e.g. to 2, the two hosts pervasive.jku.at and www.cp.jku.at are considered equally wrt not querying them twice within the time specified by WAIT_BETWEEN_RETRIEVALS_FROM_SAME_HOST)
	public static final int MAX_URLS_IN_CRAWL_LIST = 600000;							// an approximate upper limit for elements in the crawl list (to prevent memory exceptions), this is just a soft limit, it is not guaranteed that it is not exceeded by TOTAL_NO_OF_URLS/MAX_SKIP_URLS

	// parameters for time measurement
	private static long startTime;															// time when the retrieval process was started
	private static long lastMeasureTime;													// time of the last time measure point (to calculate the time needed to retrieve the last 100 URLs)
	private static long retrievedURLs;														// number of retrieved URLs										
	private static final int MEASURE_INTERVAL = 100;										// perform a time measure every X URLs

	// variables for structuring the data
	public static Vector<RetrievalData> ri = new Vector<RetrievalData>();								// to store the data to be retrieved (URLs, Files, Indices)
	public static TreeSet<Integer> retrievedIdx = new TreeSet<Integer>();								// to store the indices of already retrieved URLs
	public static DownloadControlDataVector hosts = new DownloadControlDataVector();					// a list of hosts and the time when this host was queried the last time (to prevent excessive downloading from one and the same host)
	public static Vector<Integer> currentlyDownloadingIdx = new Vector<Integer>();						// a list of indices of URLs which are currently downloaded
	public static Vector<URL> blacklistSites = new Vector<URL>();										// list of hosts from which web pages are never retrieved
	private static GoldenRetriever[] threads;															// the threads that process the retrieval
	private static Process[] wgetProcesses = new Process[GoldenRetriever.MAX_PARALLEL_DOWNLOADS];		// a Vector that contains the wget processes

	// other variables
	private int threadNo; 																	// the number of the Thread
	private static BufferedWriter bwProcessedIdx;											// the BufferedWriter to which every thread writes indices of retrieved URLs

	
	/**
	 * Constructs a new instance of the GoldenRetriever.
	 */
	public GoldenRetriever(int threadNo) {
		super();
		this.threadNo = threadNo;
	}

	/** 
	 * Method to perform a "wait" for a process and return its exit value.
	 * This is a workaround for <CODE>process.waitFor()</CODE> never returning.
	 */
	public static int doWaitFor(Process p) {
		int exitValue = -1;  // returned to caller when p is finished
		try {
			InputStream in  = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; 		// set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						// get the output of the system call
						Character c = new Character((char)in.read());
//						System.out.print(c);
					}
					while (err.available() > 0) {
						// get the output of the system call
						Character c = new Character((char)err.read());
//						System.out.print(c);
					}
					// Ask the process for its exitValue. If the process
					// is not finished, an IllegalThreadStateException
					// is thrown. If it is finished, we fall through and
					// the variable finished is set to true.
					exitValue = p.exitValue();
					finished  = true;
				} 
				catch (IllegalThreadStateException e) {
					// Process is not finished yet;
					// Sleep a little to save on CPU cycles
					Thread.currentThread().sleep(50);
				}
			}
		} 
		catch (Exception e) {
			// unexpected exception!  print it out for debugging...
			System.err.println( "doWaitFor(): unexpected exception - " + e.getMessage());
		}
		// return completion status to caller
		return exitValue;
	}


	/** 
	 * Starts the retrieval process.
	 */
	public void run() {
		// unless list of URLs to retrieve is empty
		while (!GoldenRetriever.ri.isEmpty()) {
			try {
				int rndIdx = (int)Math.floor(Math.random()*(float)GoldenRetriever.ri.size());
				RetrievalData rd = GoldenRetriever.ri.get(rndIdx);
				// randomly select a URL to retrieve from GoldenRetriever.ri
				// but ensure that the selected URL has not already been retrieved
				// 		and is not currently or was recently retrieved by another thread
				// 		and no other thread is currently fetching a URL from the same web site 
				while (GoldenRetriever.retrievedIdx.contains(rd.getIndex()) || GoldenRetriever.currentlyDownloadingIdx.contains(rd.getIndex()) || GoldenRetriever.hosts.getTimeElapsed(rd.getUrl().getHost()) < GoldenRetriever.WAIT_BETWEEN_RETRIEVALS_FROM_SAME_HOST) { // || rd.getFile().exists()) { 
					rndIdx = (int)Math.floor(Math.random()*(float)GoldenRetriever.ri.size());
					if (rndIdx < GoldenRetriever.ri.size())
						rd = GoldenRetriever.ri.elementAt(rndIdx);
//					Thread.sleep(20);
//					System.out.println("Waiting for " + rd.getUrl().getHost() + "...  (" + GoldenRetriever.hosts.getTimeElapsed(rd.getUrl().getHost()) + " msec since last call)");
				}
				if (GoldenRetriever.ri.isEmpty()) {		// if another thread has empties the GoldenRetriever.ri in the meantime - exit thread
					return;
				}

				// add index to list of currently downloading URLs
				GoldenRetriever.currentlyDownloadingIdx.addElement(rd.getIndex());
				// update timestamp of host in DownloadControlDataVector
				GoldenRetriever.hosts.update(new DownloadControlData(rd.getUrl().getHost()));

				// ensure that directory structure given by rd.getFile() exists
				if (GoldenRetriever.CHECK_FOR_DIR_STRUCTURE)
					createDirectoryStructure(rd.getFile().getParentFile());

				// retrieve URL
				System.out.println("Thread " + this.threadNo + " is retrieving from offset " + (rd.getIndex() % (GoldenRetriever.MAX_SKIP_URLS+1)) + " " + rd.getUrl() + " (idx: " + rd.getIndex() + ") into " + rd.getFile().getAbsolutePath());
				String wgetCall = GoldenRetriever.WGET
				+ " --no-clobber --waitretry=20 --random-wait --no-cookies -e robots=on"
				+ " --user-agent=\"Mozilla/5.0\""  // (Windows;\\ U;\\ Windows NT 5.1;\\ en-US;\\ rv:1.8.0.1)\\ Gecko/20060111\\ Firefox/1.5.0.1\""
				+ " -t 1"
				+ " -T 5"
				+ " -O " + rd.getFile().getAbsolutePath() 
				+ " " + rd.getUrl();
//				System.out.println(wgetCall);
				Process p = Runtime.getRuntime().exec(wgetCall);
				wgetProcesses[this.threadNo] = p;
				// wait some time before next wget instance is created
				doWaitFor(p); 
				p.destroy();
//				Thread.sleep(100);
				// check whether file that should contain the retrieved data does exist
				if (rd.getFile().exists() && rd.getIndex() != null) {
					// update list of already retrieved URLs
					GoldenRetriever.retrievedIdx.add(rd.getIndex());
					// and corresponding file
					GoldenRetriever.bwProcessedIdx.append(rd.getIndex() + "\n");
					GoldenRetriever.bwProcessedIdx.flush();
//					bwProcessedIdx.close();
					// delete entry of retrieved URL from GoldenRetriever.ri
					if (GoldenRetriever.ri.contains(rd))
						GoldenRetriever.ri.removeElement(rd);
					// delete index of retrieved URL from GoldenRetriever.currentlyDownloadingIdx
					if (GoldenRetriever.currentlyDownloadingIdx.contains(rd.getIndex()))
						GoldenRetriever.currentlyDownloadingIdx.removeElement(rd.getIndex());
					// increase counter for retrieved URLs
					GoldenRetriever.retrievedURLs++;
				}
				// inform user about progess of retrieval every 100 retrieved URLs
				if ((GoldenRetriever.retrievedURLs % GoldenRetriever.MEASURE_INTERVAL) == 0) {
					int timeElapsedTotal = (int)(System.currentTimeMillis()-GoldenRetriever.startTime)/1000;
					int timeElapsedSinceLastMeasure = (int)(System.currentTimeMillis()-GoldenRetriever.lastMeasureTime)/1000;
					System.out.println(GoldenRetriever.retrievedURLs + " URLs successfully retrieved in " + timeElapsedTotal + " sec -> " + (float)GoldenRetriever.retrievedURLs/(float)timeElapsedTotal + " retrievals/sec");
					System.out.println("The last " + GoldenRetriever.MEASURE_INTERVAL + " URLs were successfully retrieved in " + timeElapsedSinceLastMeasure + " sec -> " + (float)GoldenRetriever.MEASURE_INTERVAL/(float)timeElapsedSinceLastMeasure + " retrievals/sec");
					GoldenRetriever.lastMeasureTime = System.currentTimeMillis();
					System.gc();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}


	/**
	 * Skips a fixed number of lines in a BufferedReader. 
	 * 
	 * @param br				the BufferedReader
	 * @param noLines			the maximum number of lines to be skipped
	 * @return					the actual number of lines skipped 
	 */
	private static int skipLines(BufferedReader br, int noLines) {		
		int i=0;
		try {
			for (i=0; i<noLines && br.readLine() != null; i++) {}
		} catch (Exception e) { e.printStackTrace(); }
		return i;
	}

	/**
	 * Skips a random number of lines in a BufferedReader. 
	 * 
	 * @param br				the BufferedReader
	 * @param noLines			the maximum number of lines to be skipped
	 * @return					the actual number of lines skipped 
	 */
	private static int skipLinesRandomly(BufferedReader br, int noLines) {
		int i = 0;
		try {
			int rnd = (int)Math.round(Math.random()*noLines);
			for (i=0; i<rnd && br.readLine() != null; i++) {}
		} catch (Exception e) { e.printStackTrace(); }
		return i;
	}

	/**
	 * Starts the retrieval process by instantiating a set of
	 * threads to process the wget fetches.
	 */
	private static void startRetrieval() {
		// fill list of URLs to fetch
		fillCrawlList();
		// start a number of threads that are running at the same time (and retrieving the URLs)
		GoldenRetriever.threads = new GoldenRetriever[GoldenRetriever.MAX_PARALLEL_DOWNLOADS];
		for (int i=0; i<GoldenRetriever.threads.length; i++) {
			GoldenRetriever gr = new GoldenRetriever(i);
			GoldenRetriever.threads[i] = gr;
			GoldenRetriever.threads[i].start();
		}
		// start the CrawlListManager that ensures that there are always enough URLs (with different hosts) to fetch
		CrawlListManager clm = new CrawlListManager(GoldenRetriever.ri, GoldenRetriever.hosts);
		clm.setPriority(Thread.NORM_PRIORITY-1);
		clm.start();
	}

	/**
	 * Inserts the first or next set of URLs to be fetched into the crawl list.
	 */
	protected static void fillCrawlList() {
		// create set of URLs to be retrieved (chose members either randomly using skipLinesRandomly or with a fixed offset using skipLines) 
		if (GoldenRetriever.START_OFFSET <= GoldenRetriever.END_OFFSET) {
			try {
				String line;
				int addedItems = 0;
				int currentIdx = 0;		// current index in list of URLs
				BufferedReader brUrl = new BufferedReader(new FileReader(GoldenRetriever.URL_FILE));							// all urls to retrieve
				System.out.println("Filling craw list with a set of URLs with offset " + GoldenRetriever.START_OFFSET);
				String genre, artist, pageno, pagenoTmp, url, file;			// the parts to be extracted from the URL_FILE
				int idx;
				currentIdx += skipLines(brUrl, GoldenRetriever.START_OFFSET);
//				currentIdx = GoldenRetriever.START_OFFSET;					// skip START_OFFSET lines
				while ((line = brUrl.readLine()) != null) {
					// only proceed if current url is not in list of already retrieved urls (identified by the index)
					if (!GoldenRetriever.retrievedIdx.contains(currentIdx)) {
						// extract information from the line
						// genre
						idx = line.indexOf(",");
						genre = line.substring(0,idx);
						line = line.substring(idx+1);
						// artist
						idx = line.indexOf(",");
						artist = line.substring(0,idx);
						line = line.substring(idx+1);
						// page number
						idx = line.indexOf(",");
						pagenoTmp = line.substring(0,idx);
						// in case an html-file-range-filter was specified (GoldenRetriever.PAGE_NO_RANGE) and activated, only proceed if pageno matches
						boolean includePage = true;
						if (GoldenRetriever.USE_PAGE_NO_RANGE_FILTER) {
							includePage = false;
							for (int i=0; i<GoldenRetriever.PAGE_NO_RANGE.length; i++) {
								if (pagenoTmp.equals(Integer.toString(GoldenRetriever.PAGE_NO_RANGE[i])))
									includePage = true;
							}

						}
						// continue adding the URL to fetch if either no file-range-filter was specified or
						// the file-number (search engine rank) falls into the specified range
						if (includePage) {
							switch (pagenoTmp.length()) {	
							case 1: pageno = "00" + pagenoTmp; break;
							case 2: pageno = "0" + pagenoTmp; break;
							default: pageno = pagenoTmp; break;					
							}
							// remaining part is url
							url = line.substring(idx+1);
							// construct file name from the parts extracted
							file = GoldenRetriever.ROOT_DIR.getAbsolutePath() + "/" + genre + "/" + artist.substring(0,1) + "/" + artist + "/" + pageno + ".html";				
//							System.out.println(currentIdx + ": " + file + ", " + url);
							// add file and url to vector (if host name is not in black list)
							URL urlU = new URL(url);
//							System.out.println(urlU.getHost());
							boolean excludeHost = false;
							for (int i=0; i<GoldenRetriever.blacklistSites.size(); i++) {
								if (GoldenRetriever.blacklistSites.elementAt(i).getHost().equals(urlU.getHost()))
									excludeHost = true;
							}
							if (excludeHost) {
								System.out.println("Excluding " + url + " because host is blacklisted.");
//								} else if (new File(file).exists()) {
//								System.out.println("Excluding " + url + " because corresponding file was already retrieved.");
							} else {
								GoldenRetriever.ri.addElement(new RetrievalData(urlU, file, currentIdx));					// add URL to the list of URLs to be fetched
								addedItems++;						
							}
						}
					}
					// skip some entries
					currentIdx += skipLines(brUrl, GoldenRetriever.MAX_SKIP_URLS)+1;
				}
				brUrl.close();
				System.out.println(addedItems + " URLs were added to the crawl list. The list now contains " + GoldenRetriever.ri.size() + " items.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("START_OFFSET reached END_OFFSET! No more items will be added to the crawl list.");
		}
	}

	/**
	 * Creates all directories from the path given by 
	 * the directory f to the root dir if they
	 * are not already existant.
	 * 
	 * @param f
	 */
	public static void createDirectoryStructure(File f) {
		if (f == null) 		// root directory
			return;
		else
			createDirectoryStructure(f.getParentFile());
		if (!f.exists())
			f.mkdir();
	}

	public static void main(String[] args) {
		String line;
		int noUrlsTotal = 0, noUrlsAlreadyRetrieved = 0;			// counters
		GoldenRetriever.startTime = System.currentTimeMillis();		// remember time when retrieval process was started
		GoldenRetriever.lastMeasureTime = GoldenRetriever.startTime;
		try {
			// construct black list
			GoldenRetriever.blacklistSites.addElement(new URL("http://www.downtownmusicgallery.com"));
			GoldenRetriever.blacklistSites.addElement(new URL("http://downtownmusicgallery.com"));

			// create empty file PROCESSED_IDX_FILE if it does not already exists
			GoldenRetriever.PROCESSED_IDX_FILE.createNewFile();

			// some buffered readers and writers		
			BufferedReader brUrl = new BufferedReader(new FileReader(GoldenRetriever.URL_FILE));							// all urls to retrieve
			BufferedReader brProcessedIdx = new BufferedReader(new FileReader(GoldenRetriever.PROCESSED_IDX_FILE));			// the indices of the urls that have already been retrieved

			// count the total number of URLs to retrieve
			System.out.println("Scanning " + GoldenRetriever.URL_FILE);
			while ((line=brUrl.readLine()) != null) {
				noUrlsTotal++;
			}
			System.out.println("Total number of URLs that must be retrieved: " + noUrlsTotal);
			brUrl.close();

			// read the indices of the URLs that were already retrieved
			System.out.println("Reading information on already retrieved URLs from " + GoldenRetriever.PROCESSED_IDX_FILE);
			while ((line=brProcessedIdx.readLine()) != null) {
				Integer idx = new Integer(line);
				if (!GoldenRetriever.retrievedIdx.contains(idx)) {
					GoldenRetriever.retrievedIdx.add(idx);
					noUrlsAlreadyRetrieved++;
				}
			}
			System.out.println("URLs already retrieved: " + noUrlsAlreadyRetrieved);
			brProcessedIdx.close();

			// open file that contains the retrieved URL indices for writing
			GoldenRetriever.bwProcessedIdx = new BufferedWriter(new FileWriter(GoldenRetriever.PROCESSED_IDX_FILE, true));	// the indices of the urls that have already been retrieved
			// intelligently process every URL in URL_FILE
			startRetrieval();
			// wait until all threads are finished
			for (int i=0; i<GoldenRetriever.threads.length; i++) {
				GoldenRetriever.threads[i].join();
			}

//			for (GoldenRetriever.START_OFFSET=0; GoldenRetriever.START_OFFSET<=GoldenRetriever.MAX_SKIP_URLS; GoldenRetriever.START_OFFSET++) {
//			System.out.println("Running golden retrieval process using a start offset of " + GoldenRetriever.START_OFFSET + " and a skipping offset of " + GoldenRetriever.MAX_SKIP_URLS);
//			startRetrieval();
//			// wait until all threads are finished
//			for (int i=0; i<GoldenRetriever.threads.length; i++) {
//			GoldenRetriever.threads[i].join();
//			}
//			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Ensure that all wget processes are terminated when program exists.
	 */
	protected void finalize() {
		try {
			GoldenRetriever.bwProcessedIdx.close();		// close file that contains the retrieved URL indices			
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i=0; i<this.wgetProcesses.length; i++) {
			Process p = this.wgetProcesses[i];
			if (p != null) {
				GoldenRetriever.doWaitFor(p);
				p.destroy();
			}
		}
	}

}

/**
 * Class to hold the data structure for the
 * retrieval of one URL into one file. 
 * 
 * @author mms
 */
class RetrievalData {
	private URL url;									// the url to be retrieved
	private File file;									// the file	into which the content is to be stored
	private Integer index;								// the index in URL_FILE that corresponds with the url

	public RetrievalData(URL url, File file, Integer index) {
		this.url = url;
		this.file = file;
		this.index = index;
	}
	public RetrievalData(URL url, File file, int index) {
		this(url, file, new Integer(index));
	}
	public RetrievalData(URL url, String file, Integer index) {
		this(url, new File(file), index);
	}
	public RetrievalData(URL url, String file, int index) {
		this(url, new File(file), new Integer(index));
	}
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
}

/**
 * Class to hold the data structure to
 * manage downloads (especially to ensure
 * minimal time limits between querying the
 * same host address).
 * 
 * @author mms
 */
class DownloadControlData {
	private String host;		// the host
	private Long timestamp;		// the time at which the host was accessed the last time

	public DownloadControlData(String host, Long timestamp) {
		this.host = host;
		this.timestamp = timestamp;
	}
	public DownloadControlData(String host) {
		this.host = host;
		this.timestamp = new Long(System.currentTimeMillis());
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = new Long(timestamp);
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}

/**
 * Extension to Vector to cope with special
 * requirements of the DownloadControlData.
 * 
 * @author mms
 */
class DownloadControlDataVector extends Vector<DownloadControlData> {
	public DownloadControlDataVector() {
		super();
	}

	/**
	 * Updates the timestamp of a host. Or inserts the host in
	 * the vector if it is not already contained.
	 * 
	 * @param dcd
	 */
	public void update(DownloadControlData dcd) {
		dcd.setHost(this.getLastNLevelsOfHost(dcd.getHost(), GoldenRetriever.EQUAL_HOST_LEVELS));			// use only the last N domains of the host name
		// search for host
		Enumeration<DownloadControlData> e = this.elements();
		while (e.hasMoreElements()) {
			DownloadControlData d = e.nextElement();
			if (d.getHost().equals(dcd.getHost())) {			// host found -> update timestamp
				d.setTimestamp(System.currentTimeMillis());
//				System.out.println("Setting new timestamp for host " + dcd.getHost());
				return;
			}
		}
		// host not found -> add
		this.addElement(dcd);
	}

	/**
	 * Returns the time elapsed since the given host was queried the last time.  
	 * 	
	 * @param host		the host of interest
	 * @return
	 */
	public long getTimeElapsed(String host) {
		host = this.getLastNLevelsOfHost(host, GoldenRetriever.EQUAL_HOST_LEVELS);
		// search for host
		Enumeration<DownloadControlData> e = this.elements();
		while (e.hasMoreElements()) {
			DownloadControlData d = e.nextElement();
			if (d.getHost().equals(host)) {			// host found -> update timestamp
				long timeElapsed = System.currentTimeMillis()-d.getTimestamp().longValue();
//				System.out.println(host + " " + timeElapsed);
				return timeElapsed;
			}
		}
		return Long.MAX_VALUE;			// if host was not found -> return a very large number
	}

	/**
	 * Extracts the last N levels of the host name (e.g. from www.cp.jku.at only
	 * jku.at will be returned). 
	 * 
	 * @param host	the host name
	 * @param N		the number of levels returned
	 * @return
	 */
	private String getLastNLevelsOfHost(String host, int N) {
		StringTokenizer st = new StringTokenizer(host, ".");
		int tokens = st.countTokens();
		if (tokens <= N)				// host name contains N or less levels
			return host;
		else {							// host name contains more than N levels -> extract the last N
			String prunedHost = "";
			for (int i=0; i<tokens; i++) { 
				if (i<tokens-N)
					st.nextElement();
				else if (i==(tokens-1))				// top-level domain
					prunedHost += st.nextElement();
				else											// not top-level, but within N
					prunedHost += st.nextElement() + ".";
			}
			return prunedHost;
		}
	}

	/**
	 * Calculates and returns the number of hosts that are not ready to
	 * be queried due to timing reasons (too few time passed since last access).
	 * 
	 * @return		the number of hosts
	 */
	public int getNumberOfBlockingHosts() {
		int noBlockingHosts = 0;
		Enumeration<DownloadControlData> e = this.elements();
		while (e.hasMoreElements()) {
			DownloadControlData d = e.nextElement();
			long timeElapsed = System.currentTimeMillis()-d.getTimestamp().longValue();
			if (timeElapsed < GoldenRetriever.WAIT_BETWEEN_RETRIEVALS_FROM_SAME_HOST) {
				noBlockingHosts++;
				System.out.println("Host " + d.getHost() + " blocking for " + timeElapsed + " msec");
			}
		}
		return noBlockingHosts;
	}
}


/**
 * This class manages the maintainance of the
 * list of URLs to fetch. 
 * In cases where the fetcher threads are waiting all 
 * the time for the same set of (a few) hosts due to timing
 * reasons, the crawl list is extended with new URLs.
 * 
 * @author mms
 */
class CrawlListManager extends Thread {
	private static int CHECK_INTERVAL = 10;			// check every X seconds

	private Vector<RetrievalData> crawlList;
	private DownloadControlDataVector downloadControl;

	public CrawlListManager(Vector<RetrievalData> crawlList, DownloadControlDataVector downloadControl) {
		this.crawlList = crawlList;
		this.downloadControl = downloadControl;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			while (GoldenRetriever.START_OFFSET < GoldenRetriever.END_OFFSET) { //!crawlList.isEmpty()) {
				if (((System.currentTimeMillis()-startTime) > CrawlListManager.CHECK_INTERVAL*1000) && GoldenRetriever.START_OFFSET<=GoldenRetriever.MAX_SKIP_URLS) {			// check only every CHECK_INTERVAL seconds and only when there are still URLs to be retrieved 
					startTime = System.currentTimeMillis();
					System.out.println("Lately added offset: " +GoldenRetriever.START_OFFSET);
					// if number of blocking hosts smaller than Threads executing fetches of URLs -> retrieval system runs at a small throughput
					int noBlockingHosts = downloadControl.getNumberOfBlockingHosts();
					System.out.println("Number of blocking hosts: " + noBlockingHosts);
					if (noBlockingHosts < GoldenRetriever.MAX_PARALLEL_DOWNLOADS) {
						if (GoldenRetriever.ri.size() < GoldenRetriever.MAX_URLS_IN_CRAWL_LIST && GoldenRetriever.START_OFFSET < GoldenRetriever.END_OFFSET){
							// add URLs to crawl list
							GoldenRetriever.START_OFFSET++;				// get next set of URLs and add them to the list of URLs to fetch
							GoldenRetriever.fillCrawlList();
						} else {
							System.out.println("The crawl list already contains " + GoldenRetriever.ri.size() + " items. No more additions are allowed.");
						}
					}
				}
				// wait some time
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}