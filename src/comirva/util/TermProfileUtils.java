/*
 * Created on 10.12.2005
 */
package comirva.util;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import comirva.data.*;
import comirva.io.filefilter.HTMLFileFilter;
import cp.net.Webpage;
import cp.util.HashtableTool;

/**
 * This class implements simple utilities for
 * term profile creation, access, modification,
 * and conversion.
 * Furthermore, it implements some calculations
 * for term occurrence.
 *
 * @author Markus Schedl
 */
public class TermProfileUtils {

	/**
	 * Extracts from an XML-representation of an EntityTermProfile
	 * a term occurrence matrix (a DataMatrix whose rows represent the
	 * terms and whose columns represent the documents of the entity,
	 * the values are only 0 or 1 - according to the (non-)occurrence
	 * of the respective term in the respective document).
	 *
	 * @param xmlFile the XML-file representing the EntityTermProfile
	 */
	public static DataMatrix getOccurrenceMatrixFromETP(File xmlFile) {
		DataMatrix occMat = new DataMatrix();		// occurrence matrix
		// deserialize XML-file
		EntityTermProfile etp = getEntityTermProfileFromXML(xmlFile);
		// get term occurrences and number of documents
		Vector<Vector<Integer>> to = etp.getTermOccurrenceOnDocuments();
		int numberDocuments = etp.getNumberDocuments();
		// generate term occurrence matrix as DataMatrix
		for (int i=0; i<to.size(); i++) {
			// get Vector of documents containing the current term i
			Vector<Integer> docOccs = to.elementAt(i);
			for (int j=0; j<numberDocuments; j++) {
				if (docOccs.contains(new Integer(j)))		// current document j contains current term i
					occMat.addValue(new Double(1));		// add 1 at position (i,j) of occurrence matrix
				else										// current document j does not contain current term i
					occMat.addValue(new Double(0));		// add 0 at position (i,j) of occurrence matrix
			}
			occMat.startNewRow();			// new row
		}
		occMat.removeLastAddedElement();	// remove latest added row (which does not contain any data)
		// set name of DataMatrix and return it
		occMat.setName("term occurrences of "+xmlFile.getName()+" ("+occMat.getNumberOfRows()+"x"+occMat.getNumberOfColumns()+")");
		return occMat;
	}

	/**
	 * Extracts the term occurrence matrix from an EntityTermProfile.
	 * The term occurrence matrix is a DataMatrix whose rows represent the
	 * terms and whose columns represent the documents of the entity.
	 * Its values are only 0 or 1 - according to the (non-)occurrence
	 * of the respective term in the respective document).
	 *
	 * @param etp	the EntityTermProfile containing the data
	 */
	public static DataMatrix getOccurrenceMatrixFromETP(EntityTermProfile etp) {
		DataMatrix occMat = new DataMatrix();		// occurrence matrix
		// get term occurrences and number of documents
		Vector<Vector<Integer>> to = etp.getTermOccurrenceOnDocuments();
		int numberDocuments = etp.getNumberDocuments();
		// generate term occurrence matrix as DataMatrix
		for (int i=0; i<to.size(); i++) {
			// get Vector of documents containing the current term i
			Vector<Integer> docOccs = to.elementAt(i);
			for (int j=0; j<numberDocuments; j++) {
				if (docOccs.contains(new Integer(j)))		// current document j contains current term i
					occMat.addValue(new Double(1));		// add 1 at position (i,j) of occurrence matrix
				else										// current document j does not contain current term i
					occMat.addValue(new Double(0));		// add 0 at position (i,j) of occurrence matrix
			}
			occMat.startNewRow();			// new row
		}
		occMat.removeLastAddedElement();	// remove latest added row (which does not contain any data)
		// set name of DataMatrix and return it
		occMat.setName("term occurrences of "+etp.getEntityName()+" ("+occMat.getNumberOfRows()+"x"+occMat.getNumberOfColumns()+")");
		return occMat;
	}

	/**
	 * Reads the EntityTermProfile from an XML-representation.
	 *
	 * @param xmlFile the XML-file representing the EntityTermProfile
	 */
	public static EntityTermProfile getEntityTermProfileFromXML(File xmlFile) {
		// deserialize XML-file
		EntityTermProfile etp = new EntityTermProfile(xmlFile);
		InputStreamReader in;
		try {
			in = new InputStreamReader(new FileInputStream(xmlFile), "UTF8");
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader metaReader = factory.createXMLStreamReader(in);
			etp.readXML(metaReader);
			metaReader.close();
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return ETP
		return etp;
	}

	/**
	 * Given a root directory that contains subdirs (one for each entity)
	 * and a list of terms, this methods generates EntityTermProfiles for every
	 * subdir (entity) and serializes the information as XML-files using
	 * the classes EntityTermProfile and SingleTermList.
	 *
	 * @param rootDir	a File that points to the root directory
	 * @param terms		a Vector containing the term list
	 * @param statusBar	a JLabel representing the status bar (for updating CoMIRVA's UI)
	 */
	public static void generateEntityTermProfiles(File rootDir, Vector<String> terms, JLabel statusBar) {
		if (rootDir.isDirectory()) {
			long t = System.currentTimeMillis();
			// every entity (directory)
			File[] dirs = rootDir.listFiles();
			for (int i=0; i<dirs.length; i++) {
				// only directories allowed
				if (dirs[i].isDirectory() && dirs[i].getName() != "." && dirs[i].getName() != "") {
					File entityDir = dirs[i];			// path to entity's html-pages
//					System.out.println("processing "+entityDir.toString());
//					t = System.currentTimeMillis();
					// inform user
					if (statusBar != null) {
						statusBar.setText("<html>Creating ETP for <b>" + entityDir + "</b></html>");
						statusBar.validate();
						// force status bar to be repainted immediately
						statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
					}
					// create entity term profile
					EntityTermProfile etp = new EntityTermProfile(entityDir);
					etp.calculateOccurrences(terms, new HTMLFileFilter());
					etp.setEntityName(entityDir.toString());		// set entity name to current artist name
//					etp.setCrawlDetails("music");
					// serialize as XML-file
					String  xmlFileName = entityDir.getPath() + ".xml";
					File xmlFile = new File(xmlFileName);
					// inform user
					if (statusBar != null) {
						statusBar.setText("<html>Storing ETP for <b>" + entityDir + "</b> in <b>" + xmlFileName + "</b></html>");
						statusBar.validate();
						// force status bar to be repainted immediately
						statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
					}
					// write XML-file
					OutputStreamWriter out;
					try {
						out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF8");
						XMLOutputFactory factory = XMLOutputFactory.newInstance();
						XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
						etp.writeXML(xmlWriter);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
//					System.out.println("\ttime: "+(System.currentTimeMillis()-t)/1000);
				}
			}
		}
	}
	/**
	 * Given a root directory that contains subdirs (one for each entity)
	 * and a list of terms, this methods generates EntityTermProfiles for every
	 * subdir (entity) and serializes the information as XML-files using
	 * the classes EntityTermProfile and SingleTermList.
	 *
	 * @param rootDir	a File that points to the root directory
	 * @param terms		a Vector containing the term list
	 */
	public static void generateEntityTermProfiles(File rootDir, Vector<String> terms) {
		TermProfileUtils.generateEntityTermProfiles(rootDir, terms, null);
	}

	/**
	 * Determines those terms that have the
	 * highest document frequency (highest number
	 * of documents where they occur)
	 * A maximum of <code>max</code> terms are returned (those with
	 * hightest document frequency).
	 *
	 * @param toMatrix		the term occurrence matrix
	 * @param terms			a Vector containing the terms
	 * @param max			the maximum number of returned terms
	 * @return				a Hashtable with the most often occurring terms as keys and their occurrence as values
	 */
	public static Hashtable getTermsWithHighestOccurrence(DataMatrix toMatrix, Vector<String> terms, int max) {
		int[] docFreq = new int[toMatrix.getNumberOfRows()];	// document frequency
		Hashtable<String,Integer>mostOftenTerms = new Hashtable<String,Integer>();	// return vector
		Vector<Double> df = new Vector<Double>();				// temporary Vector for document frequency
		Vector<String> t = new Vector<String>();				// temporary Vector for terms
		// check if number of rows in terms and toMatrix equal
		if (toMatrix.getNumberOfRows() == terms.size() && toMatrix != null && toMatrix.getNumberOfColumns() != 0 && toMatrix.isBooleanMatrix()) {
			// calculate document frequencies
			for (int i=0; i<toMatrix.getNumberOfRows(); i++) {
				Vector rowVec = toMatrix.getRow(i);
				Enumeration e = rowVec.elements();
				while (e.hasMoreElements())
					docFreq[i] += ((Double)e.nextElement()).intValue();
			}
			for (int i=0; i<toMatrix.getNumberOfRows(); i++) {
				if (docFreq[i] != 0) {		// only add df value if it is greater than 0
					df.addElement(new Double(docFreq[i]));
					t.addElement(terms.elementAt(i));
				}
			}
			// sort remaining terms wrt document frequency
			VectorSort.sortWithMetaData(df, t);
			// remove all terms that do not have the max top-most document frequencies
			for (int i=0; i<max && i<t.size(); i++) {
				mostOftenTerms.put(t.elementAt(i), new Integer(df.elementAt(i).intValue()));
				// debug
//				System.out.println(t.elementAt(i) + "\t" + mostOftenTerms.get(t.elementAt(i)));
			}
		} else
			mostOftenTerms = null;
		return mostOftenTerms;
	}
	
	/**
	 * Determines those terms that have the
	 * highest TFxIDF values
	 * A maximum of <code>max</code> terms are returned (those with
	 * hightest TFxIDF).
	 *
	 * @param tfxidf		the TFxIDF values of all terms
	 * @param terms			a Vector containing the terms
	 * @param max			the maximum number of returned terms
	 * @return				a Hashtable with the most often occurring terms as keys and their occurrence as values
	 */
	public static Hashtable getTermsWithHighestTFxIDF(Vector<Double> tfxidf, Vector<String> terms, int max) {
		Hashtable<String,Double>mostOftenTerms = new Hashtable<String,Double>();	// return vector
		Vector<Double> tfidf = new Vector<Double>();				// temporary Vector for document frequency
		Vector<String> t = new Vector<String>();					// temporary Vector for terms
		// check if number of terms equal number of TFxIDF values
		if (tfxidf.size() == terms.size()) {
			// get TFxIDF values
			for (int i=0; i<tfxidf.size(); i++) {
				if (tfxidf.elementAt(i).doubleValue() != 0) {		// only add tfxidf value if it is greater than 0
					tfidf.addElement(new Double(tfxidf.elementAt(i)));
					t.addElement(terms.elementAt(i));
				}
			}
			// sort remaining terms wrt TFxIDF value
			VectorSort.sortWithMetaData(tfidf, t);
			// remove all terms that do not have the max top-most TFxIDF values
			for (int i=0; i<max && i<t.size(); i++) {
				mostOftenTerms.put(t.elementAt(i), new Double(tfidf.elementAt(i).doubleValue()));
				// debug
//				System.out.println(t.elementAt(i) + "\t" + mostOftenTerms.get(t.elementAt(i)));
			}
		} else
			mostOftenTerms = null;
		return mostOftenTerms;
	}

	/**
	 * Returns all terms as Vector for which the
	 * document frequency is greater than 0.
	 *
	 * @param toMatrix		the term occurrence matrix
	 * @param terms		a Vector containing the terms
	 * @return				a Hashtable with the most often occurring terms as keys and their occurrence as values
	 */
	public static Hashtable<String,Integer> getNonZeroOccurringTerms(DataMatrix toMatrix, Vector<String> terms) {
		return getTermsWithHighestOccurrence(toMatrix, terms, terms.size());
	}

	/**
	 * Given a term occurrence matrix (TOM) and a list of terms,
	 * this method extracts a subset of the TOM containing only
	 * those documents that contain all terms given by the
	 * parameter <code>filterTerms</code>.
	 *
	 * @param toMatrix							the term occurrence matrix
	 * @param terms								a Vector<String> representing the terms for which the TOM (DataMatrix) was created
	 * @param filterTerms						a Vector<String> containing the terms that must be included (all documents that do not include all of these terms will be filtered out)
	 * @param idxDocsContainingAllFilterTerms 	an empty Vector<Integer> that will contain the indices of the documents that contain the filter terms after execution of this method
	 * @return									a DataMatrix that does only contain the term occurrences for the
	 */
	public static DataMatrix getSubsetOfTermOccurrenceMatrix(DataMatrix toMatrix, Vector<String> terms, Vector<String> filterTerms, Vector<Integer> idxDocsContainingAllFilterTerms) {
		DataMatrix subsetDM = new DataMatrix();
		if (toMatrix != null && toMatrix.isBooleanMatrix() && filterTerms != null) {		// only proceed, if term occurrence matrix is boolean
			// convert DataMatrix to double[][] for faster access
			double[][] tom = toMatrix.toDoubleArray();
			int numberTerms = tom.length;
			int numberDocuments = tom[0].length;
			// to remember index of those documents that contain all filter terms
			if (idxDocsContainingAllFilterTerms == null)
				idxDocsContainingAllFilterTerms = new Vector<Integer>();
			// get indices of terms that should be tested
			int[] idxFilterTerms = new int[filterTerms.size()];
			for (int i=0; i<filterTerms.size(); i++)
				idxFilterTerms[i] = terms.indexOf(filterTerms.elementAt(i));
			// for every document
			for (int j=0; j<numberDocuments; j++) {
				// test, if current document contains all filter terms
				boolean containsAllFilterTerms = true;
				for (int i=0; i<idxFilterTerms.length; i++) {
					if (tom[idxFilterTerms[i]][j] != 1)
						containsAllFilterTerms = false;
				}
				// if current document does contain all filter terms,
				// remember it by storing its index in a Vector
				if (containsAllFilterTerms)
					idxDocsContainingAllFilterTerms.addElement(new Integer(j));
			}
			// idxDocsContainingAllFilterTerms now contains indices of the documents that
			// contain all terms in the filter term list
			// for every term in the term list
			for (int i=0; i<terms.size(); i++) {
				// for every document that contains all filter terms
				for (int j=0; j<idxDocsContainingAllFilterTerms.size(); j++) {
					subsetDM.addValue(new Double(tom[i][idxDocsContainingAllFilterTerms.elementAt(j).intValue()]));
				}
				subsetDM.startNewRow();
			}
			subsetDM.removeLastAddedElement();
//			System.out.println("documents containing "+filterTerms.toString()+": "+subsetDM.getNumberOfColumns());
		} else
			subsetDM = null;
		return subsetDM;
	}

	/**
	 * Given a term occurrence matrix (TOM) and a list of terms,
	 * this method extracts a subset of the TOM containing only
	 * those documents that contain all terms given by the
	 * parameter <code>filterTerms</code>.
	 *
	 * @param toMatrix			the term occurrence matrix
	 * @param terms				a Vector representing the terms for which the TOM (DataMatrix) was created
	 * @param filterTerms		a Vector containing the terms that must be included (all documents that do not include all of these terms will be filtered out)
	 * @return					a DataMatrix that does only contain the term occurrences for the
	 */
	public static DataMatrix getSubsetOfTermOccurrenceMatrix(DataMatrix toMatrix, Vector<String> terms, Vector<String> filterTerms) {
		return TermProfileUtils.getSubsetOfTermOccurrenceMatrix(toMatrix, terms, filterTerms, null);
	}

	/**
	 * Creates and returns a subset of a Vector<String>. The elements
	 * contained in the subset are defined by a Vector<Integer> of
	 * the indices.
	 *
	 * @param docPaths		a Vector<String> containing all document paths
	 * @param indices		a Vector<Integer> containing the indices of the document paths that should be returned
	 * @return
	 */
	public static Vector<String> getMaskedDocumentPaths(Vector<String> docPaths, Vector<Integer> indices) {
		Vector<String> subsetDocPaths = new Vector<String>();
		// check if arguments are not null
		if (docPaths != null && indices != null) {
			// for all elements in the indices Vector
			for (int i=0; i<indices.size(); i++) {
				// get index
				int idx = indices.elementAt(i).intValue();
				// if index is valid, add element to return-Vector
				if (idx >= 0 && idx < docPaths.size())
					subsetDocPaths.addElement(docPaths.elementAt(idx));
			}
		} else		// if one of arguments is null, return null
			subsetDocPaths = null;
		return subsetDocPaths;
	}

	/**
	 * Searches in the directory denoted by <code>dir</code> all files which
	 * match the file filter <code>filter</code> and extracts from
	 * all founded files a list of all occuring terms. Stopwords are automatically
	 * removed.
	 *
	 * @param 	dir			a File that represents the directory where the HTML-files reside
	 * @param 	filter		a FileFilter that filters the documents
 	 * @param 	statusBar	a JLabel representing the status bar (for updating CoMIRVA's UI)
	 * @return	a Vector<String> containing the extracted terms
	 */
	public static Vector<String> extractTermsFromDocuments(File dir, FileFilter filter, JLabel statusBar) {
		Vector<String> terms = new Vector<String>();		// list of terms to return
		// get files matching the file filter
		File[] files = dir.listFiles(filter);
		Hashtable tf = new Hashtable();
		// use Peter'S HashtableTool to extract list of terms
		for (int i=0; i<files.length; i++) {
			try {
				if (statusBar != null) {	// inform user
					statusBar.setText("<html>Extracting terms from document <b>" + files[i] + "</b></html>");
					statusBar.validate();
					// force status bar to be repainted immediately
					statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
				}
				Webpage wp = new Webpage(files[i]);
				String content = wp.getPlainText();
				HashtableTool.updateWordsOccurrences(content, tf, null, Webpage.delimiterstring);
//				System.out.println("processing "+ files[i].toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// put extracted terms in a Vector<String> and return it
		if (!tf.isEmpty()) {				// if at least 1 term could be extracted -> copy them to Vector<String>
			if (statusBar != null) {	// inform user
				statusBar.setText("<html>Preparing data of extracted terms.</html>");
				statusBar.validate();
				// force status bar to be repainted immediately
				statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
			}
			Enumeration e = tf.keys();
			while (e.hasMoreElements())
				terms.addElement((String)e.nextElement());
		} else				// if not a single term could be extracted -> return null
			terms = null;
		if (statusBar != null) {	// inform user
			statusBar.setText("<html>Extraction of terms from <b>" + dir + "</b> finished.</html>");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
		// return term list
		return terms;
	}
	/**
	 * Searches in the directory denoted by <code>dir</code> all files which
	 * match the file filter <code>filter</code> and extracts from
	 * all founded files a list of all occuring terms. Stopwords are automatically
	 * removed.
	 *
	 * @param 	dir			a File that represents the directory where the HTML-files reside
	 * @param 	filter		a FileFilter that filters the documents
	 * @return	a Vector<String> containing the extracted terms
	 */
	public static Vector<String> extractTermsFromDocuments(File dir, FileFilter filter) {
		return TermProfileUtils.extractTermsFromDocuments(dir, filter, null);
	}


	/**
	 * Updates the paths of an ETP and its STLs which are
	 * stored in an ETP-XML-file. For technical reasons, absolute
	 * paths must be stored in the XML-serialized ETP-files.
	 * Thus, moving the files to a new location requires updating
	 * the path information. That is what this function does.
	 * For this method to work it is vital that the directory of
	 * the ETP-XML-file contains a subdirectory with the corresponding
	 * STL-XML-files (and documents).
	 *
	 * @param xmlFile the XML-file representing the EntityTermProfile
	 */
	public static void updatePathsInETP(File xmlFile) {
		// load ETP
		EntityTermProfile etp = TermProfileUtils.getEntityTermProfileFromXML(xmlFile);
		// get ETP's local directory
		File dirLocal = etp.getDirLocal();
		String oldPath = dirLocal.getPath();
		// find deepest directory
		int idxPathSep = oldPath.lastIndexOf(File.separator);		// last occurrence of path separator character
		// if last index of path separator is at the very end of file name (e.g. C:\ETPs\ instead of C:\ETPs)
		// use the one before
		if (idxPathSep == oldPath.length()-1) {
			oldPath = oldPath.substring(0, oldPath.length()-1);
			idxPathSep = oldPath.lastIndexOf(File.separator);
		}
		String deepestDir = oldPath.substring(idxPathSep+1, oldPath.length());
		// create new path
		String newPath = xmlFile.getParent()+ File.separator + deepestDir + File.separator;
		// only proceed, if old path is different to new one
		File newPathFile = new File(newPath);
		File oldPathFile = new File(oldPath);
		if (!newPathFile.equals(oldPathFile)) {
			// replace any occurrence of old path with new one in ETP-XML-file
			String xmlFileContent = TermProfileUtils.getFileContent(xmlFile);		// read content of file
			String oldPathOrig = oldPath;		// save old path since it will be modified below and has to be restored
			// try to replace path with file separator at the end
			oldPath = oldPath.concat(File.separator);
			// try replacement with Unix-like and Windows-like separators (use other platform's separator)
			xmlFileContent = xmlFileContent.replace(oldPath, newPath);				// try replacement with file separator of current OS
			if (File.separator.equals(new String("\\")))
				oldPath = oldPath.replace("\\", "/");
			else if (File.separator.equals(new String("/")))
				oldPath = oldPath.replace("/", "\\");
			xmlFileContent = xmlFileContent.replace(oldPath, newPath);				// replace again with separator of other OS
			// try to replace path without file separator at the end
			oldPath = oldPathOrig;		// restore original path
			// try replacement with Unix-like and Windows-like separators (use other platform's separator)
			xmlFileContent = xmlFileContent.replace(oldPath, newPath);				// try replacement with file separator of current OS
			if (File.separator.equals(new String("\\")))
				oldPath = oldPath.replace("\\", "/");
			else if (File.separator.equals(new String("/")))
				oldPath = oldPath.replace("/", "\\");
			xmlFileContent = xmlFileContent.replace(oldPath, newPath);				// replace again with separator of other OS
			// write modified content back to ETP-XML-file
			TermProfileUtils.setFileContent(xmlFile, xmlFileContent);
			// reload ETP with new (correct) paths to STLs
			etp = TermProfileUtils.getEntityTermProfileFromXML(xmlFile);
			// update all STLs referred by the ETP
			Vector<SingleTermList> stls = etp.getSingleTermLists();
			for (int i=0; i<stls.size(); i++) {
				SingleTermList stl = stls.elementAt(i);
				// update STL (using new path concatenated with name of STL-XML-file)
				stl.setFileLocal(new File(newPath+stl.getFileLocal().getName()));
				// XML-serialize modified STL using the modified local file
				OutputStreamWriter out;
				try {
					out = new OutputStreamWriter(new FileOutputStream(new File(stl.getFileLocal().toString()+".xml")), "UTF8");
					XMLOutputFactory factory = XMLOutputFactory.newInstance();
					XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
					stl.writeXML(xmlWriter);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Fetch the entire content of a text file and return it in a String.
	 *
	 * @param textFile is a file which already exists and can be read
	 */
	static public String getFileContent(File textFile) {
	    StringBuffer content = new StringBuffer();
	    BufferedReader input = null;
	    try {
	    	input = new BufferedReader(new FileReader(textFile));
	    	String line = null; //not declared within while loop
	    	while ((line = input.readLine()) != null){
	    		content.append(line);
	    		content.append(System.getProperty("line.separator"));
	    	}
	    } catch (FileNotFoundException fnfe) {
	    	fnfe.printStackTrace();
	    } catch (IOException ioe){
	    	ioe.printStackTrace();
	    } finally {
	    	try {
	    		// flush and close file reader
	    		if (input != null)
	    			input.close();
	    	} catch (IOException ioe) {
	    		ioe.printStackTrace();
	    	}
	    }
	    return content.toString();
	}

	/**
	 * Change the contents of text file, overwriting any existing text.
	 *
	 * @param textFile 	is an existing file which can be written to
	 * @param content	a String representing the content that is to be written to the file
	 */
	static public void setFileContent(File textFile, String content) {
		Writer output = null;
	    try {
	      output = new BufferedWriter(new FileWriter(textFile));
	      output.write(content);
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	    finally {
	    	try {
	    		// flush and close file writer
	    		if (output != null)
	    			output.close();
	    	} catch (IOException ioe) {
	    		ioe.printStackTrace();
	    	}
	    }
	}

}








