/*
 * Created on 06.09.2005
 */
package comirva.io.filefilter;

import java.io.*;


/**
 * This class implements a FileFilter for *.png and *.jpg files
 * which are graphic files and used to store the content of the
 * visualization pane.
 *
 * @author Markus Schedl
 */
public class GraphicFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {

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
    	    if (extension != null && (extension.compareTo("png") == 0 || (extension.compareTo("jpg") == 0))) {
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
     	return "Graphic Files (*.png, *.jpg)";
    }
	
}