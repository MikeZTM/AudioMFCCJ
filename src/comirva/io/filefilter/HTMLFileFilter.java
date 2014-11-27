/*
 * Created on 01.12.2005
 */
package comirva.io.filefilter;

import java.io.*;

/**
 * This class implements a filter for HTML files.
 * 
 * @author Markus Schedl
 */
public class HTMLFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {
	
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
    	    if (extension != null && (extension.compareTo("html") == 0)) {
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
     	return "HTML Files (*.html)";
     }

}
