package comirva.io.web;


import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

import comirva.util.external.PlainTextExtractor;

import cp.net.Webpage;

/**
 * This class extracts information from the "all music guide". 
 * 
 * @author Markus Schedl
 */
public class AMGCrawler extends Thread {
	private URL openURL;					// the URL to open
	private static String amgURL = "http://www.allmusic.com/cg/amg.dll?p=amg&sql=1:";
	private File artistListFile;				// text file containing artist names
	private Vector<String> artists = new Vector<String>();
	private Vector<Vector<Integer>> yoda = new Vector<Vector<Integer>>();
	private File outputFile = new File("/home/mms/amg.txt"); ///Research/Data/band_members/Punk_members.txt");
	
	public AMGCrawler(File artistList) {
		artistListFile = artistList;
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
		
		// get yoda for all artists
		for (int i=0; i<this.artists.size(); i++) {		
//			yoda.addElement(getYearsActive(this.artists.elementAt(i)));
			getBandMembers(this.artists.elementAt(i));
		}
		
//		// debug YODA
//		for (int i=0; i<this.artists.size(); i++) {
//			System.out.print(this.artists.elementAt(i)+": ");
//			Vector<Integer> currentYODA = this.yoda.elementAt(i);
//			if (currentYODA != null) {
//				for (int j=0; j<currentYODA.size(); j++)
//					System.out.print(currentYODA.elementAt(j).toString()+" ");
//			}
//			System.out.println("");
//		}

	}
	
	/**
	 * Extracts the band members of a band.
	 * 
	 * @param artist	the band for which band members should be determined.
	 */
	private void getBandMembers(String artist) {		
		System.out.println("Band members of " + artist + ": ");
		try {
			// prepare writing results to file
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));
			bw.write("---" + artist + "\n");
			bw.close();
			
			// retrieve AMG page
			URL openURL = new URL(amgURL + artist);
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
		
			
//			// parse result
			int startBM = content.indexOf("Group Members");
			int endBM = content.indexOf("Similar Artists");
			// make sure that correct page was found 
			if (startBM != -1 && endBM != -1) {
				String bandMembersContent = content.substring(startBM, endBM);

				// extract band member names
				String searchFor = "<span class=\"libg\"><a href=\"";
				String closeTag = "</a>";
				String bandMemberName = ""; 
				int startLink = -1;
				int endLink = -1;
				int clTag = -1;
				while (startBM != -1) {
					startLink = bandMembersContent.indexOf(searchFor);
					bandMembersContent = bandMembersContent.substring(startLink+searchFor.length());
					endLink = bandMembersContent.indexOf(">");
					bandMembersContent = bandMembersContent.substring(endLink+1);
					clTag = bandMembersContent.indexOf(closeTag);
					bandMemberName = bandMembersContent.substring(0, clTag);
					
					Vector<String> instruments = getInstruments(bandMemberName.replace(" ", "_"));
					
					// write band member to file
					bw = new BufferedWriter(new FileWriter(outputFile, true));
					bw.write(bandMemberName + ": ");
					
					System.out.print(bandMemberName + ": ");
					
					if (instruments != null) {
						for (int i=0; i<instruments.size(); i++) {
							bw.write(instruments.elementAt(i)+ " ");
							System.out.print(instruments.elementAt(i)+ " ");
						}
					}
					bw.write("\n");
					System.out.println("");
					bw.close();
						
					// find next band member
					startBM = bandMembersContent.indexOf(searchFor);
					
				}
			} else {			// no correct page was found
				System.out.println("not found");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
	}
	
	private Vector<String> getInstruments(String bandMemberName) {
		Vector<String> instruments = new Vector<String>();		
		// extract instruments (get group member's web page)
		try {
			URL openURL = new URL(amgURL + bandMemberName);
			URLConnection httpConnection = openURL.openConnection();
			InputStreamReader urlReader = new InputStreamReader(httpConnection.getInputStream());
			BufferedReader br = new BufferedReader(urlReader);
			StringBuffer content = new StringBuffer();					// content of retrieved web page
			String line = null;
			// read content of web page
			while ((line = br.readLine()) != null){
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			br.close();
			urlReader.close();
			
			// parse result
			int startInstruments = content.indexOf("<!--Instruments Listing--><td");
			int endInstruments = content.indexOf("<!--Instruments Listing--></tr>");
			if (startInstruments != -1 && endInstruments != -1) {
				String instrumentContent = content.substring(startInstruments, endInstruments);
				
				// extract instruments
				String searchFor = "<a href=\"";
				String closeTag = "</a>";
				String instrumentName = ""; 
				int startLink = -1;
				int endLink = -1;
				int clTag = -1;
				while (startInstruments != -1) {
					startLink = instrumentContent.indexOf(searchFor);
					instrumentContent = instrumentContent.substring(startLink+searchFor.length());
					endLink = instrumentContent.indexOf(">");
					instrumentContent = instrumentContent.substring(endLink+1);
					clTag = instrumentContent.indexOf(closeTag);
					instrumentName = instrumentContent.substring(0, clTag);
					
					instruments.addElement(instrumentName);
// System.out.println(instrumentName);
					
					// find next band member
					startInstruments = instrumentContent.indexOf(searchFor);				
				}
				
			} else {			// no correct page was found
//				System.out.println("not found");
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return instruments;
	}
	
	
	/**
	 * Extracts the decades of activity of an artist.
	 * 
	 * @param artist	the artist for which yoda should be determined.
	 * @return			the decades the artist was active
	 */
	private Vector<Integer> getYearsActive(String artist) {
		Vector<Integer> yoda = new Vector<Integer>();
		
		System.out.print("Determining YODA for " + artist + ": ");
		
		try {
			URL openURL = new URL(amgURL + artist);
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
			
			// parse result
			int startYearsActive = content.indexOf("<span>Years Active</span>");
			int endYearsActive = content.indexOf("<!--Years Active-->");
			// make sure that correct page was found 
			if (startYearsActive != -1 && endYearsActive != -1) {
				String yearsActive = content.substring(startYearsActive, endYearsActive	);
				
				// find active decades
				String searchFor = "<div class=\"timeline-sub-active\">";
				int startDecadeActive = yearsActive.indexOf(searchFor);
				while (startDecadeActive != -1) {
					yearsActive = yearsActive.substring(startDecadeActive+searchFor.length());
					if (yearsActive.startsWith("1910") || yearsActive.startsWith("2000"))
						yoda.addElement(new Integer(yearsActive.substring(0,4)));
					else if (yearsActive.startsWith("20") || 
							yearsActive.startsWith("30") ||
							yearsActive.startsWith("40") ||
							yearsActive.startsWith("50") ||
							yearsActive.startsWith("60") ||
							yearsActive.startsWith("70") ||
							yearsActive.startsWith("80") ||
							yearsActive.startsWith("90"))
						yoda.addElement(new Integer("19"+yearsActive.substring(0,2)));
					System.out.print(yoda.lastElement() + " ");
					// find next active decade
					startDecadeActive = yearsActive.indexOf(searchFor);
				}
			} else {			// no correct page was found
				System.out.println("not found");
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("");
		return yoda;
	}
	
	
	public static void main(String[] args) {
		AMGCrawler amgc = new AMGCrawler(new File("/Research/Data/band_members/Punk_artists_.txt")); //"c:/temp/test.txt")); // "C:/Research/Data/yoda/C1995a_artists.txt"));
		amgc.start();
	}
}
