/*
 * Created on 03.08.2007
 */
package comirva.util.external.retrieval;


import comirva.util.external.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class automatically queries exalead and stores
 * the resulting URLs in text files.
 * 
 * @author Markus Schedl
 */
public class ExaleadRetriever extends Thread {
	private static String STORAGE_PATH = "C:/Research/Data/amg-artists/exalead/M/rock";		// local path to store the crawled web pages
	private static File INPUT_FILE = new File("C:/Research/Data/amg-artists/windows-ansi/artists_rock.txt");	// file to read input from
	private static File ERROR_REPORT_FILE = new File("C:/Research/Data/amg-artists/exalead-error.txt");			// file to write errors to
	private static String EXALEAD_URL = "http://www.exalead.com/search?scm=never&dcm=never&q="; 	// URL to query
	private static String ADDITIONAL_KEYWORDS = " NEAR music"; 
	private static boolean SKIP_EXISTING_ARTISTS = true;						// do not perform exalead querying if artist directory already exists
	private static boolean STORE_IN_FIRSTLETTER_SUBDIRS = true;					// organizes the artist dirs in subdirs names a-z (due to too many dirs in one dir for some file systems)
	
	// cookie for basic retrieval settings (100 results per page, English as UI language, ...)
	private static String EXALEAD_COOKIE = "db_1_0=eJxtUu1uozAQfBVe4BK+IZHuR4mQWikNEdBWPSFFLiwEBWxkzJG+/dkYmsAFRAI7uzOzu4ZrA7SsAbM2URLlJK9E+fN09OPYP+lJkii74BCHwV68Jkr0HITx7i2OTpoIxKH/FL/6h1h+cvwYHKIgjGTpDdYlrDQUcqCAU5grtmdCWdqx9gh0X2IQ6ZaswSSErKSQsgD7V1QBynakFliOqhZkUgY56ioWQst/2/cSehEfTbWAaHreI1x0qODCQ5A0gMf0F3yA/qPEGekXtLQszmxHqq7Gfp5zCwu8rAUh93zk/yKkT5Ioh2iQfSWZREZGKXlXoqnqRNbwIeyhQOl3NM1DQIx28zafSQ3NWD4Wdy3QF8yA5iiFqVUBAb636lUovXj8KSjpcLZoh2+h97OSTepexxjBCw9D0jWtugz4pi7tkmPo2kPcTctQVcFSRA4+50v+JB2VQ/rfh+z+reWm9/AXKgH8Gofbl5ey4WcCLVZ6qxOH53a4hrIzY812ve77fvUNVUV6McB2lZJ6LeDPIZbISzlOpPlvx3VtdWPalmXrjqqZtmnO3Mr5Jz+7kVb0hWQPiJ2B/sjdfUsZzTQ0Vzcd3VBdy7Ad1XLsmY72UMdY6kyjWRFaDEofU0TquK5lcnpd3agbTXNc25q3oz+UMRcyX18pt77qLoOE5+0kuWVtDEtTLdXgt2tyCXdGbjwkt6bo9ExnzXyYrmy3W01zbc1w+UIMw/gHWf6ePw==";

	private Vector<String> searchWords;												// a list containing the search queries
	private String storagePath = ExaleadRetriever.STORAGE_PATH;

	/**
	 * Creates a new WebCrawling-instance.
	 * 
	 * @param wcCfg			a WebCrawlingConfig-instance containing the settings for the web crawl
	 * @param searchWords	a Vector containing all search queries
	 * @param statusBar		the JLabel of CoMIRVA's status bar to inform the user 
	 */
	public ExaleadRetriever(Vector searchWords) {
		// set parameters
		this.searchWords = searchWords;
	}

	/**
	 * Initializes and starts the retrieving process.
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		// retrieve URLs from exalead
		ArrayList<String> urls;
		for (int i=0; i<this.searchWords.size(); i++) {
			String searchWord = this.searchWords.elementAt(i);			// get current search term (artist)
			boolean doQuerying = true;		// current artist is to be queried?
			// if existing artists should not be requeried and artist's directory already exists -> do not query artist
			if (ExaleadRetriever.SKIP_EXISTING_ARTISTS) {
				File artistDir = new File(this.storagePath + (ExaleadRetriever.STORE_IN_FIRSTLETTER_SUBDIRS ? TextFormatTool.removeUnwantedChars(searchWord).substring(0, 1) + "/" : "") + TextFormatTool.removeUnwantedChars(searchWord));
//				System.out.println(artistDir.getAbsolutePath());
				if (artistDir.exists() && artistDir.isDirectory())
					doQuerying = false;
			}
			// perform querying exalead
			if (!doQuerying) {		// do not query artist
//				System.out.println("skipping artist " + searchWord);
			} else {				// query artist
				// call retrieveURLs for every element/artist of searchWords-Vector
				// and returns a list of URLs retrieved from exalead search engine
				System.out.print("retrieving URLs for artist " + searchWord + " (" + Math.round(((float)i/(float)this.searchWords.size())*1000)/10.0f + "%)");
				long startTime = System.currentTimeMillis();								// rember start time
				String content = this.retrieveExaleadResultPage(searchWord);
				long duration = System.currentTimeMillis() - startTime;						// calculate duration of retrieving result page from exalead
				if (content != null) {			// continue only if no error occurred
					urls = this.extractURLs(content);
					long pc = this.extractPageCount(content);
					System.out.println("\tpage count: " + pc);
					// create file in directory where retrieved pages of current search term has been stored
					try {
						// let thread sleep for 2 sec. or longer
						Thread.sleep(Math.max(10, 2000-duration));
						// create artist directory if not already existant
						String path = this.storagePath; 			// path where artist should be stored in
						if (STORE_IN_FIRSTLETTER_SUBDIRS) {			// if every artist should be inserted in directory according to his/her first letter, ensure that those "character"-dirs exist
							path += TextFormatTool.removeUnwantedChars(searchWord).substring(0, 1) + "/";
							File dir = new File(path);
							if (!dir.isDirectory())
								dir.mkdir();
						}
						File dir = new File(path + TextFormatTool.removeUnwantedChars(searchWord));						
						if (!dir.isDirectory())
							dir.mkdir();
						// write list of retrieved URLs to the urls.dat file		
						File fileURLList = new File(	path + 
								TextFormatTool.removeUnwantedChars(searchWord) + 
						"/urls.dat");
						BufferedWriter bw = new BufferedWriter(new FileWriter(fileURLList));
						for (int j=0; j<urls.size(); j++) {
							String url = urls.get(j);
							// write URL to file
							bw.write(url.toString()+"\n");
						}
						bw.flush();
						bw.close();
						// write additional info to info.dat
						File fileInfo = new File(	path + 
								TextFormatTool.removeUnwantedChars(searchWord) + 
						"/info.xml");
						if (!fileInfo.exists())		// create new file if not existant
							bw = new BufferedWriter(new FileWriter(fileInfo));
						else						// append to end of file if file exists
							bw = new BufferedWriter(new FileWriter(fileInfo, true));
						bw.append("<ExaleadRetrieval>\n");
						bw.append("<Timestamp>" + new Date(System.currentTimeMillis()) + "</Timestamp>\n");
						bw.append("\t<Query>" + this.createExaleadSearchURL(searchWord) + "</Query>\n");
						bw.append("\t<PageCount>" + Long.toString(pc) + "</PageCount>\n");				
						for (int j=0; j<urls.size(); j++) {
							String url = urls.get(j);
							// write URL to file
							bw.write("\t<URL rank=\"" + Integer.toString(j+1) + "\">" + url.toString()+"</URL>\n");
						}
						bw.append("</ExaleadRetrieval>");
						bw.flush();
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}



	/**
	 * Extracts the URLs returned by exalead (results from search engine exalead for query searchTerm) by
	 * parsing the HTML code.
	 * 
	 * @param content		the HTML code returned by exalead
	 * @return
	 */
	private ArrayList<String> extractURLs(String content) {
		ArrayList<String> urls = new ArrayList<String>();
		// extract URLs from returned HTML code
		String searchAnchor = "<a class=\"c307\" href=\""; 
		String extractedURL;
		int idxStartURL = content.indexOf(searchAnchor);
		while (idxStartURL != -1) {
			content = content.substring(idxStartURL+searchAnchor.length(), content.length());
			// find first " in remaining HTML content
			int idxEndURL = content.indexOf("\"");
			extractedURL = content.substring(0, idxEndURL);
			urls.add(extractedURL);
			// System.out.println(extractedURL);
			idxStartURL = content.indexOf(searchAnchor);
		}
		return urls; 
	}


	/**
	 * Extracts the page count for a query from the HTML content returned by exalead.
	 * 
	 * @param htmlContent		the HTML content of the result page
	 * @return
	 */
	private long extractPageCount(String htmlContent) {
		String searchTerm = "</b> of about <b>";
		int idxStartPC = htmlContent.indexOf(searchTerm);
		if (idxStartPC == -1)		// if no indication of page count is found, assume a page count of 0 
			return 0;
		else {
			String extractedPC = htmlContent.substring(idxStartPC+searchTerm.length(), idxStartPC+searchTerm.length()+50);
			searchTerm = "</b>";
			int idxEndPC = extractedPC.indexOf(searchTerm);
			extractedPC = extractedPC.substring(0, idxEndPC);
			// eliminate comma from page count
			extractedPC = extractedPC.replace(",", "");
			Long l = new Long(extractedPC);
			return l.longValue();
		}
	}


	/**
	 * Constructs the URL to be retrieved (exalead + query term). 
	 * 
	 * @param searchTerm		the query term to be submitted to exalead
	 * @return
	 */
	private URL createExaleadSearchURL(String searchTerm) {
		URL exaleadURL = null;
		try {
			exaleadURL = new URL(ExaleadRetriever.EXALEAD_URL+URLEncoder.encode("\""+searchTerm+"\""+ExaleadRetriever.ADDITIONAL_KEYWORDS, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exaleadURL; 
	}

	/**
	 * Queries exalead with the given <code>searchTerm</code> and returns the
	 * result page as String containing the HTML code.
	 * 
	 * @param searchTerm	the query term to be submitted to exalead
	 * @return				a String containing the HTML code of exalead's result page
	 */
	private String retrieveExaleadResultPage(String searchTerm) {
		String content = null;
		try {
			URL openURL = this.createExaleadSearchURL(searchTerm);
			// open HTTP connection
			URLConnection httpConnection = openURL.openConnection();
			// create and submit the cookie indicating that 100 pages per query should be displayed
			httpConnection.setRequestProperty("Cookie", ExaleadRetriever.EXALEAD_COOKIE);
			httpConnection.connect();
			InputStreamReader urlReader = new InputStreamReader(httpConnection.getInputStream());
			BufferedReader br = new BufferedReader(urlReader);
			StringBuffer htmlBuffer = new StringBuffer();					// content of retrieved web page
			// read content of web page
			String line = null;
			while ((line = br.readLine()) != null){
				htmlBuffer.append(line + System.getProperty("line.separator"));
			}
			br.close();
			urlReader.close();
			content = htmlBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("An error occurred while retrieving exalead's result page.");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(ExaleadRetriever.ERROR_REPORT_FILE, true));
				bw.append("Search Term: " + searchTerm + "\n");
				bw.append("Storage Path: " + STORAGE_PATH + "\n");
				bw.append("Error Message: " + e.getMessage() + "\n");
				bw.flush();
				bw.close();
			} catch (Exception ex) { }
			return null;
//			return retrieveExaleadResultPage(searchTerm);
		}
		return content;
	}

	public static void main(String[] args) {
		String content = comirva.util.TermProfileUtils.getFileContent(ExaleadRetriever.INPUT_FILE);
		StringTokenizer st = new StringTokenizer(content, "\n");
		Vector searchWords = new Vector();
		while (st.hasMoreElements()) {
			String artist = st.nextToken().trim();
			searchWords.addElement(artist);
		}
		ExaleadRetriever er = new ExaleadRetriever(searchWords);
		er.start();
	}

}
