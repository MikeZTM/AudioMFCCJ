package comirva.io.web;


import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * This class extracts information from the "Yahoo! Directory Artist Search". 
 * 
 * @author Markus Schedl
 */
public class YahooDirectoryCrawler extends Thread {
	private URL openURL;					// the URL to open
	private static String yahooURL = "http://audio.search.yahoo.com/search/audio?_&p="; // p=metallica&n=100&urp=artist";
	private File artistListFile;				// text file containing artist names to be searched
	private File outputFile;					// text file into which tracks and similar tracks should be written
	private Vector<String> artists = new Vector<String>();
	private Vector<String> tracks = new Vector<String>();		// to store tracks by current artist
	
	public YahooDirectoryCrawler(File artistList, File outputFile) {
		artistListFile = artistList;
		this.outputFile = outputFile;
	}

	public void run()  {
		// read artist list
	    try {
	    	BufferedReader input = new BufferedReader(new FileReader(this.artistListFile));
	    	String line = null;
	    	while ((line = input.readLine()) != null) {
	    		this.artists.addElement(line);
	    	}
	    	input.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		// crawl all artists in Yahoo! Directory
		for (int i=0; i<this.artists.size(); i++)			
			tracks = getTracksByArtist(this.artists.elementAt(i));
		
		// debug
		for (int i=0; i<this.artists.size(); i++) {
			System.out.print(this.artists.elementAt(i)+": ");
			if (tracks != null) {
				for (int j=0; j<tracks.size(); j++)
					System.out.print(tracks.elementAt(j).toString()+" ");
			}
			System.out.println("");
		}

	}
	
	/**
	 * @param artist	the artist for which the tracks should be determined.
	 * @return			Vector<String> containing the artist's tracks
	 */
	private Vector<String> getTracksByArtist(String artist) {
		Vector<String> tracks = new Vector<String>();
		Vector<URL> urlsToTracks = new Vector<URL>();
		
		int startIdx = 1;
		try {
			String contentTracks = "ein string", oldContentTracks = "ein anderer"; // content of block in web page that contains the tracks
			while (!contentTracks.equals(oldContentTracks)) {
				// get 100 results (one result page
				System.out.println("Determining tracks starting from " + Integer.toString(startIdx) + " for " + artist + ": ");
				

				URL openURL = new URL(yahooURL + URLEncoder.encode(artist, "UTF-8") + "&stype=uni&urp=artist&ei=UTF-8&n=100&b=" + Integer.toString(startIdx));
				URLConnection httpConnection = openURL.openConnection();
				InputStreamReader urlReader = new InputStreamReader(httpConnection.getInputStream());
				BufferedReader br = new BufferedReader(urlReader);
				StringBuffer content = new StringBuffer();					// content of retrieved web page

				// read content of web page
				String line = null;
				while ((line = br.readLine()) != null){
					content.append(line);
					content.append(System.getProperty("line.separator"));
				}
				br.close();
				urlReader.close();
				
				// pre-defined search terms
				String searchStringForResultBlockStart = "<ol start=" + Integer.toString(startIdx) + ">";
				String searchStringForResultBlockEnd = "</ol>";
				String searchStringForArtistStart = "<b>";
				String searchStringForArtistEnd = "</b>";
				String searchStringForTrackStart = "<span class=yschttl><a href=\"";
				String searchStringForTrackEnd = "</a></span>";
				String searchURLTrackSeparator = ">";
						
				// parses the current result page
				int startResults = content.indexOf(searchStringForResultBlockStart);
				int endResults = content.indexOf(searchStringForResultBlockEnd);
				// make sure that correct page was found
				if (startResults != -1 && endResults != -1) {
					oldContentTracks = contentTracks;		// store old content (for determining if web page changed)
					contentTracks = content.substring(startResults, endResults);
//					System.out.println(contentTracks.toString());

					// parse individual tracks
					int startArtistName = contentTracks.indexOf(searchStringForArtistStart);
					int endArtistName = contentTracks.indexOf(searchStringForArtistEnd);					
					String artistName = "", trackName = "", hrefToTrackName = "";		// to store retrieved data
					while (startArtistName != -1 && endArtistName != -1) {
						// search for artist name
//						System.out.println(startArtistName);
//						System.out.println(endArtistName);
						artistName = contentTracks.substring(startArtistName+searchStringForArtistStart.length(), endArtistName);
						contentTracks = contentTracks.substring(endArtistName);			// forward in content string to position after </b>
						if (artistName.equals(artist)) {		// only proceed when artist name on retrieved web page equal that in text file 
							// search for track name
							int startTrackName = contentTracks.indexOf(searchStringForTrackStart);
							if (startTrackName != -1) {								
								contentTracks = contentTracks.substring(startTrackName+searchStringForTrackStart.length());
								int endTrackName = contentTracks.indexOf(searchStringForTrackEnd);
								if (endTrackName != -1) {
									// extract string containing the link to the tracks web page and the track name
									String contentTrackNameRef = contentTracks.substring(0, endTrackName);
									// search for url/track separator
									int positionURLTrackSeparator = contentTrackNameRef.indexOf(searchURLTrackSeparator);
									if (positionURLTrackSeparator != -1) {
										hrefToTrackName = contentTrackNameRef.substring(0, positionURLTrackSeparator);
										trackName = contentTrackNameRef.substring(positionURLTrackSeparator+1);
									}
								}
							}
						} else {
							contentTracks = contentTracks.substring(searchStringForTrackStart.length());							
						}
						System.out.println(artistName + "\t" + trackName + "\t" + hrefToTrackName);
//						System.out.println("s: " + startArtistName + "\te: " + endArtistName);
						// search next
						startArtistName = contentTracks.indexOf(searchStringForArtistStart);
						endArtistName = contentTracks.indexOf(searchStringForArtistEnd);
					}

				} else {			// no correct page was found
					System.out.println("not found");
					return null;
				}
				
				startIdx += 100;		// retrieve web page with subsequent 100 results
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
		return tracks;
	}
	
	
	public static void main(String[] args) {
		YahooDirectoryCrawler amgc = new YahooDirectoryCrawler(new File("C:/Temp/artists.txt"), new File("C:/Temp/output.txt"));
		amgc.start();
	}
}
