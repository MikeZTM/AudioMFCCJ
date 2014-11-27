/*
 * Created on 06.07.2005
 */
package comirva.io.filefilter;

import java.io.*;

/**
 * This class implements a FileFilter for *.cws files
 * which are "CoMIRVA Workspace" files used to store
 * a Workspace-object (containing data matrices and meta-data)
 * 
 * @author Markus Schedl
 */
public class WorkspaceFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {

	/**
     * Returns <code>true</code> if the File <code>f</code> should be shown in the directory pane,
     * <code>false</code> if this is not the case.<br>
     * Files that begin with "." are ignored.
     *
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
    	if (f != null) {
    	    if (f.isDirectory()) {
    	    	return true;
    	    }
    	    String extension = getExtension(f);
    	    if (extension != null && (extension.compareTo("cws") == 0)) {
    	    	return true;
    	    }
    	}
    	return false;
	}

	 /**
      * Returns the extension of the file's name.
      *
      * @param f		the File for which the extension should be returned
      * @return		a String containing the extension of the File <code>f</code>
      */
     public String getExtension(File f) {
     	if (f != null) {	
     		String filename = f.getName();
     		int i = filename.lastIndexOf('.');
     		if (i>0 && i<filename.length()-1) {
     			return filename.substring(i+1).toLowerCase();
     		}
     	}
     	return null;
     }
     
     /**
      * Returns the description of the filter. 
      *
      * @see javax.swing.filechooser.FileFilter#getDescription()
      */
     public String getDescription() {
     	return "CoMIRVA Workspace Files (*.cws)";
     }
	
}