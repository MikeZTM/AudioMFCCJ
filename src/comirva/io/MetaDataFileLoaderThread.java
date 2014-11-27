/*
 * Created on 03.11.2004
 */
package comirva.io;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;

/**
 * This class implements a thread for loading meta-data files (e.g. SOM-labels).
 * The meta-data files must contain ASCII-characters only.
 *
 * @author Markus Schedl
 */
public class MetaDataFileLoaderThread extends Thread {

	// the file containing the matrix data
	private File fileData;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// listMatrices is needed to add the name of the data matrix to the matrix list
	private DefaultListModel listMetaData;
	// a Vector into which the file content is loaded 
	private Vector md = new Vector();
	// Vector containing the loaded meta-data list
	private Vector metaDataList;
	// the number of lines read (data items)
	private int lines;
	
	/**
	 * Creates a MetaDataFileLoaderThread for loading meta-data from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 * @param ml	the Vector to which the name of the DataMatrix should be added after it has been loaded
	 * @param jl	the JLabel representing the status bar (for writing current loading progress)
	 * @param lm	the DefaultListModel to add the name of the matrix to the UI
	 */
	public MetaDataFileLoaderThread(File f, Vector ml, JLabel jl, DefaultListModel lm) {
		this.fileData =  f;
		this.metaDataList = ml;
		this.statusBar = jl;
		this.listMetaData = lm;
	}
	/**
	 * Creates a MetaDataFileLoaderThread for loading meta-data from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 * @param ml	the Vector to which the name of the DataMatrix should be added after it has been loaded
	 */
	public MetaDataFileLoaderThread(File f, Vector ml) {
		this.fileData =  f;
		this.metaDataList = ml;
	}
	/**
	 *  Creates a MetaDataFileLoaderThread for loading meta-data from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 */
	public MetaDataFileLoaderThread(File f) {
		this.fileData = f;
	}

	/**
	 * This method is called when the thread is started.
	 * The file is opened, the data read and extracted.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// read data and return a Vector-instance
		Vector md = getMetaDataFromFile();												// load meta-data
	}
	
	/**
	 * Opens the file and loads its content into a Vector-instance which is returned thereafter.
	 * 
	 * @return a Vector containing the loaded meta-data as Strings
	 */
	public Vector getMetaDataFromFile() {
		// create a Vector-instance
		Vector md = new Vector();
		
		// counter for read lines
		this.lines = 0;
		try {
			// create reader to access file
			BufferedReader readerFile = new BufferedReader(new FileReader(fileData));
			// read from file as long as no exception (EOF) is thrown
			while (true) {
				// read one line
				String strDataFileLine = new String(readerFile.readLine());
				// ensure that read line is not empty
				if (strDataFileLine != null) {
					// get tokens from the line read
					StringTokenizer tokenizerMatrixFile = new StringTokenizer(strDataFileLine);
					// continue reading only if line is not empty
					if (tokenizerMatrixFile.countTokens() > 0) {
						md.addElement(strDataFileLine);
						// increase line-counter
						this.lines++;
					}
					if (statusBar != null)
						statusBar.setText("Loading meta-data from file: "  +  fileData.getAbsoluteFile() + " (" + this.lines + " data items)");					
				}
			} 
		} catch (EOFException eof) {
			// end of file reached
			if (statusBar != null)
				statusBar.setText("Meta-Data (" + this.lines + " data items) extracted from file: "  +  fileData.getAbsolutePath());
			if (metaDataList != null)	
				metaDataList.addElement(md);												// add Meta-Data Vector to Meta-Data List
			if (listMetaData != null)
				listMetaData.addElement(fileData.getName() + " (" + this.lines + ")");		// add name of file to meta-data list in UI, if possible
			return md;
		} catch (NullPointerException npe) {
			// empty line read
			if (statusBar != null)
				statusBar.setText("Meta-Data (" + this.lines + " data items) extracted from file: "  +  fileData.getAbsolutePath());
			if (metaDataList != null)	
				metaDataList.addElement(md);												// add Meta-Data Vector to Meta-Data List
			if (listMetaData != null)
				listMetaData.addElement(fileData.getName() + " (" + this.lines + ")");		// add name of file to meta-data list in UI, if possible
			return md;
		} catch (IOException ioe) {
			if (statusBar != null)
				statusBar.setText("I/O-Exception while accessing file " + fileData.getAbsolutePath());
		}
//		dm.setName(fileData.getName() + " (" + matrixRows + "x" + matrixCols + ")");
//		if (metaDataList != null)	
//			metaDataList.addElement(md);												// add Meta-Data Vector to Meta-Data List
//		if (listMetaData != null)
//			listMetaData.addElement(fileData.getName() + " (" + this.lines + ")");		// add name of file to meta-data list in UI, if possible
		return md;
	}
	
	/**
	 * Returns the Vector-instance into which the file content is loaded.
	 *
	 * @return the Vector containing the loaded meta-data
	 */
	public Vector getMetaDataVector() {
		return this.md;
	}

}