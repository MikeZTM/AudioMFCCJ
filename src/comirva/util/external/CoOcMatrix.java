/*
 * Created on 06.05.2005
 */
package comirva.util.external;

import comirva.data.DataMatrix;

import java.io.*;
import java.util.*;

/**
 * This class implements a co-occurrence matrix
 * calculation using grep and already extracted
 * html-pages
 * 
 * @author Markus Schedl
 */
public class CoOcMatrix {

	private static final int MAX_NUMBER_OF_HTML_FILES = 100;			// restrict number of HTML files analyzed per artist
	
	
	public static void main_loadHash(String[] args) {
		// create reader to access file
		Vector artists = new Vector();
		try {
			BufferedReader readerFile = new BufferedReader(new FileReader("C:/Research/Data/co-occurrences/C224a/artists_224.txt"));
			String artist = readerFile.readLine();
			while (artist != null) {
				artists.addElement(artist);
				artist = readerFile.readLine();
			}
		} catch (EOFException eofe) {
		} catch (IOException eofe) {
		}

		// initialize CoOcMatrix
		int[][] cooc = new int[artists.size()][artists.size()];
		for (int i=0; i<artists.size(); i++)
			for (int j=0; j<artists.size(); j++)
				cooc[i][j] = 0;
		
		DataMatrix dm = new DataMatrix();
		
		for (int i=0; i<artists.size(); i++) {
			String artist = (String)artists.elementAt(i);
			System.out.println(artist);
			try {
				ObjectInputStream hashFile = new ObjectInputStream(new FileInputStream("C:/Research/Data/co-occurrences/C1816a/artist_cooc_hashes/" + TextFormatTool.removeUnwantedChars(artist) + "_cooc.hash"));
				Hashtable ht = (Hashtable)hashFile.readObject();
				for (int j=0; j<artists.size(); j++) {
					cooc[i][j] = ((Integer)ht.get((String)artists.elementAt(j))).intValue();
					dm.addValue(new Double(cooc[i][j]));
//					if (cooc[i][j] != 0)
//						System.out.println((String)artists.elementAt(j) + " on " + (String)artists.elementAt(i) + ": " + cooc[i][j]);
				}
				dm.startNewRow();
			} catch (Exception e) {
			}
		}
		dm.removeLastAddedElement();

		// write DataMatrix to File
		File fileData = new File("/home/mms/Research/Data/co-occurrences/C953a/co-oc.dat");
		// create BufferedWriter
		try {
			Writer ow = new BufferedWriter(new FileWriter(fileData));
			// for all rows
			for (int i=0; i<dm.getNumberOfRows(); i++) {
				// get row
				Vector row = dm.getRow(i);
				// get every element in row
				for (int j=0; j<row.size(); j++) {
					ow.write(row.elementAt(j).toString()+" "); 
				}
				// start new line after each row
				ow.write("\n");
			}
			ow.flush();
			ow.close();
		} catch (Exception e) {
		}
		
	}
	
	public static void main(String[] args) {
		// create reader to access file
		Vector artists = new Vector();
		try {
			BufferedReader readerFile = new BufferedReader(new FileReader("C:/Research/Data/co-occurrences/C224a/artists_224.txt"));
			String artist = readerFile.readLine();
			while (artist != null) {
				artists.addElement(artist);
				artist = readerFile.readLine();
			}
		} catch (EOFException eofe) {
		} catch (IOException eofe) {
		}

		String htmlDir = "C:/Research/Data/co-occurrences/C224a/crawl_1000_MR/";

		// initialize CoOcMatrix
		int[][] cooc = new int[artists.size()][artists.size()];
		for (int i=0; i<artists.size(); i++)
			for (int j=0; j<artists.size(); j++)
				cooc[i][j] = 0;
		
		// init occurrence-flag-array
		boolean[] occurs =	new boolean[artists.size()];
		for (int i=0; i<artists.size(); i++)
			occurs[i] = false;
		
		
		try {
			// through all artists
			for (int i=0; i<artists.size(); i++) {
				System.out.println("processing artist " + TextFormatTool.removeUnwantedChars((String)artists.elementAt(i)));
				// for all found web pages of artist i
				for (int k=0; k<MAX_NUMBER_OF_HTML_FILES; k++) {
					// only proceed, if file exists
					File crawlFile = new File(htmlDir + TextFormatTool.removeUnwantedChars((String)artists.elementAt(i)) + "/" + TextFormatTool.leadingDoubleZero(Integer.toString(k)) + ".html");
					if (crawlFile.exists()) {
						// reset occurrence-flas-array
						for (int j=0; j<artists.size(); j++)
							occurs[j] = false;
						// open html-file
						BufferedReader br = new BufferedReader(new FileReader(crawlFile));
						String line = br.readLine();
						while (line != null) {
							// extract occurrences of all artists j on web pages of artist i
							for (int j=0; j<artists.size(); j++) {
								if (line.indexOf((String)artists.elementAt(j)) != -1) {		// artist j occurs on current line
									occurs[j] = true;
								}
							}
							line = br.readLine();
						}
						br.close();
//						System.out.println((String)artists.elementAt(j) + " on wep pages containing " + artists.elementAt(i) + ": " + Integer.toString(count));
					}
					// pass document frequency from occurrence-flag-array to cooc-matrix
					for (int j=0; j<artists.size(); j++) {
						if (occurs[j])
							cooc[i][j]++; 
					}
				}
				for (int j=0; j<artists.size(); j++) {
					System.out.println((String)artists.elementAt(j) + "  " + cooc[i][j]); 
				}
			}
		} catch (IOException e) {
			System.out.println("IOException occurred!!!");
		}

		// write resulting co-oc matrix to a DataMatrix-instance
		DataMatrix dm = new DataMatrix();
		
		for (int i=0; i<artists.size(); i++) {
			for (int j=0; j<artists.size(); j++) {
				dm.addValue(new Double(cooc[i][j]));
			}
			dm.startNewRow();
		}
		dm.removeLastAddedElement();
		// write DataMatrix to File
		File fileData = new File("C:/Research/Data/co-occurrences/C224a/crawl_1000_MR_coocs.dat");
		// create BufferedWriter
		try {
			Writer ow = new BufferedWriter(new FileWriter(fileData));
			// for all rows
			for (int i=0; i<dm.getNumberOfRows(); i++) {
				// get row
				Vector row = dm.getRow(i);
				// get every element in row
				for (int j=0; j<row.size(); j++) {
					ow.write(row.elementAt(j).toString()+" "); 
				}
				// start new line after each row
				ow.write("\n");
			}
			ow.flush();
			ow.close();
		} catch (Exception e) {
		}	
		
	}

}
