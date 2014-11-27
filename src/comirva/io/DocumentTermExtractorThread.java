/*
 * Created on 09.01.2006
 */
package comirva.io;

import comirva.util.*;
import comirva.Workspace;

import java.io.*;
import java.util.Vector;
import javax.swing.*;

/**
 * This class implements a thread for extracting
 * terms from a directory of documents. 
 *
 * @author Markus Schedl
 */
public class DocumentTermExtractorThread extends Thread {

	// the directory where the documents reside
	private File dir;
	// for filtering the documents that are to be considered in the term extraction
	private FileFilter filter;
	// for adding the result (term list) to CoMIRVA's GUI
	private Workspace ws;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	
	/**
	 * Creates a DocumentTermExtractor for extracting a term list.
	 * 
	 * @param dir		a File pointing to the direcotry where the documents reside
	 * @param filter	a FileFilter for filtering the documents that are to be considered in the term extraction
	 * @param jl		the JLabel representing the status bar (for writing current loading progress)
	 * @param ws		the Workspace to which the output (term list) is to be added
	 */
	public DocumentTermExtractorThread(File dir, FileFilter filter, JLabel jl, Workspace ws) {
		this.dir =  dir;
		this.filter = filter;
		this.statusBar = jl;
		this.ws = ws;
	}

	/**
	 * This method is called when the thread is started.
	 * The term extraction process is performed.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// extract terms
		Vector<String> terms = TermProfileUtils.extractTermsFromDocuments(dir, filter, statusBar);
		// add term list to CoMIRVA's GUI
		ws.addMetaData(terms, "terms extracted from " + dir.getName() + " (" + terms.size() + ")");
	}
		
}