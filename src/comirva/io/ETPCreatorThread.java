/*
 * Created on 14.12.2005
 */
package comirva.io;

import comirva.util.*;

import java.io.*;
import java.util.Vector;
import javax.swing.*;

/**
 * This class implements a thread for creating
 * entity term profiles given a root directory and 
 * a term list.
 *
 * @author Markus Schedl
 */
public class ETPCreatorThread extends Thread {

	// the root directory
	private File fileData;
	// term list
	private Vector<String> terms;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	
	/**
	 * Creates a EntityTermProfileCreatorThread for generating term profiles.
	 * 
	 * @param f		a File pointing to the root direcotry
	 * @param terms	a Vector<String> containing the term list based on which the term profile(s) are created
	 * @param jl		the JLabel representing the status bar (for writing current loading progress)
	 */
	public ETPCreatorThread(File f, Vector<String> terms, JLabel jl) {
		this.fileData =  f;
		this.terms = terms;
		this.statusBar = jl;
	}

	/**
	 * This method is called when the thread is started.
	 * The ETP-creation process is started.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (statusBar != null) {
			statusBar.setText("Creating and XML-Serializing Entity Term Profile(s). This may take a while (depending on the number and size of documents).");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
		TermProfileUtils.generateEntityTermProfiles(this.fileData, this.terms, this.statusBar);
		if (statusBar != null) {
			statusBar.setText("Creation of Entity Term Profile(s) finished.");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
	}
		
}