/*
 * Created on 07.04.2005
 */
package comirva.util.external;

import java.io.*;
import java.util.*;

/**
 * This class implements a duplicate finder for entries in a text file. 
 * 
 * @author Markus Schedl
 */
public class DuplicateFinder {
	private Vector data = new Vector();
	
	public DuplicateFinder(File fileData) {
		// load data from file
		try {
			// create reader to access file
			BufferedReader readerFile = new BufferedReader(new FileReader(fileData));
			// read from file as long as no exception (EOF) is thrown
			while (true) {
				// read one line
				String strDataFileLine = new String(readerFile.readLine());
				data.add(strDataFileLine);
				// continue reading only if line is not empty
			} 
		} catch (EOFException eof) {
		} catch (NullPointerException npe) {
		} catch (IOException ioe) {
		}
		// check for duplicates in Vector data
		for (int i=0; i<data.size(); i++) {
			// get artist
			String artist1 = (String)data.elementAt(i);
			// check with all other artists
			for (int j=i+1; j<data.size(); j++) {
				String artist2 = (String)data.elementAt(j);
				// duplicate found
				if ((artist1.compareTo(artist2) == 0) && (artist1 != artist2)) {
					System.out.println("double entry: " + artist1 + ": " + i + ", " +j);
				}
			}
		}
		System.out.println("check finished");
	}

	public static void main(String[] args) {
		new DuplicateFinder(new File("E:/exalead/processed_idx.txt"));
	}
}