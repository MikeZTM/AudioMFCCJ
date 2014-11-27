/*
 * Created on 02.01.2006
 */
package comirva.io;

import comirva.util.*;
import comirva.io.filefilter.XMLFileFilter;

import java.io.*;
import javax.swing.*;

/**
 * This class implements a thread for updating 
 * the paths in XML-serialized Entity Term Profile(s).
 *
 * @author Markus Schedl
 */
public class ETPXMLPathUpdaterThread extends Thread {
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// the files/directories containing ETP-XML-file(s) 
	private File[] xmlFiles;
	
	/**
	 * Creates an ETPXMLPathUpdaterThread for updating the paths of
	 * one or more XML-serialized entity term profile(s) from file(s) or
	 * directory(ies) <code>xmlFiles</code>.
	 * 
	 * @param xmlFiles		a File[] which contains the XML-serialized ETPs
	 * @param jl			the JLabel representing the status bar (for writing current loading progress)
	 */
	public ETPXMLPathUpdaterThread(File[] xmlFiles, JLabel jl) {
		this.xmlFiles =  xmlFiles;
		this.statusBar = jl;
	}

	/**
	 * This method is called when the thread is started.
	 * The XML-file(s) is/are deserialized, the paths updated and the file
	 * saved.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
		// check if directory or files are passed,
		// filter out all ETP-XML-files and
		// run a path updater on them
		for (int i=0; i<xmlFiles.length; i++) {
			// get current XML-file
			File xmlFile = xmlFiles[i];
			// determine if source is directory or file
			if (xmlFile.isFile()) {					// is xml-file
				this.updateStatusBarFileStarted(xmlFile); 			// inform user
				TermProfileUtils.updatePathsInETP(xmlFile);			// do update
				this.updateStatusBarFileFinished(xmlFile); 			// inform 0128user
			} else if (xmlFile.isDirectory()) {		// is directory of xml-files
				// get list of XML-files in selected directory
				File[] xmlFiles = xmlFile.listFiles(new XMLFileFilter());
				// update paths in all XML-files in selected directory
				for (int j=0; j<xmlFiles.length; j++) {		
					if (xmlFiles[j].isFile()) {
						this.updateStatusBarFileStarted(xmlFiles[j]); 			// inform user
						TermProfileUtils.updatePathsInETP(xmlFiles[j]);			// do update
						this.updateStatusBarFileFinished(xmlFiles[j]); 			// inform 0128user
					}
				}
			}		
		}            	
	}
	
	// update the status bar when started processing afile
	private void updateStatusBarFileStarted(File f) {
		if (statusBar != null) {
			statusBar.setText("<html>Updating paths in Entity Term Profile: <b>"  +  f.getAbsoluteFile() + "</b></html>");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
	}
	// update the status bar when finished processing afile
	private void updateStatusBarFileFinished(File f) {
		if (statusBar != null) {
			statusBar.setText("<html>Finished path update of Entity Term Profile: <b>" + f.getAbsolutePath() + "</b></html>");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
	}
	
}