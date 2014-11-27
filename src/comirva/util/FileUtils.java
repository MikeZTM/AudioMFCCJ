/*
 * Created on 03.01.2006
 */
package comirva.util;

import java.io.*;
import java.util.*;

import comirva.io.ETPXMLExtractorThread;
import comirva.io.filefilter.XMLFileFilter;

/**
 * This class implements simple utilities
 * related to file processing.
 * 
 * @author Markus Schedl
 */
public class FileUtils {
	
	/**
	 * Creates and returns a File[] containing all files 
	 * in all subdirectories of the files given as argument
	 * <code>files</code>. Furthermore, a file filter can be
	 * specified.
	 * 
	 * @param files		the files and directories which are to be searched
	 * @param filter	a FileFilter to be applied to the search
	 * @return			a File[] containing all files in all subdirectories of <code>files</code>
	 */
	public static File[] getAllFilesRecursively(File[] files, FileFilter filter) {
		Vector<File> allFiles = new Vector<File>();
		// process all files in File[] files
		if (files != null) {
			for (int i=0; i<files.length; i++) {
				// get current file
				File file = files[i];
				// determine if source is directory or file
				if (file.isFile()) {					// file
					allFiles.addElement(file);
				} else if (file.isDirectory() && (file.getName() != ".") && (file.getName() != "")) {		// directory
					// get list of files in directory
					File[] dirFileList = getAllFilesRecursively(file.listFiles(filter), filter);		// recursive call
					for (int j=0; j<dirFileList.length; j++)
						allFiles.addElement(dirFileList[j]);
				}	
			}            						
		}
		// return the file list as File[]
		return allFiles.toArray(new File[0]);
	}
	
}
