/*
 * Created on 06.12.2005
 */
package comirva.io;

import comirva.data.*;
import comirva.util.*;
import comirva.config.ETPLoaderConfig;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * This class implements a thread for extracting 
 * data from an XML-file that contains 
 * an XML-serialized Entity Term Profile.
 *
 * @author Markus Schedl
 */
public class ETPXMLExtractorThread extends Thread {

	// the file containing the matrix data
	private File fileData;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// listMatrices is needed to add the name of the data matrix to the matrix list
	private DefaultListModel listMatrices;
	// Vector containing the loaded matrices
	private Vector matrixList;
	// listMatrices is needed to add the name of the data matrix to the matrix list
	private DefaultListModel listMetaData;
	// Vector containing the loaded meta-data list
	private Vector metaDataList;
	// the ETPLoaderConfig that specifies which data should be loaded
	private ETPLoaderConfig etplCfg;
	// the prefixes for the name of the extracted data instances
	// to be used in CoMIRVA's GUI
	// for data matrices/vectors
	public static String prefixTermOccurrences = "term occurrences of ";
	public static String prefixTermFrequencies = "term frequencies of ";
	public static String prefixDocumentFrequencies = "document frequencies of ";
	public static String prefixTFxIDFs = "TFxIDFs of ";
	// for meta-data
	public static String prefixTerms = "terms of ";
	public static String prefixDocumentPaths = "document paths of ";
	
	/**
	 * Creates an ETPXMLExtractorThread for loading an XML-serialized entity term profile from File <code>f</code>.
	 * 
	 * @param f			the File which contains the entity term profile
	 * @param ml		the Vector to which the name of the DataMatrix should be added after it has been loaded
	 * @param lm		the DefaultListModel to add the name of the matrix to the UI
	 * @param mdl		the Vector to which the term list should be added	
	 * @param lmd		the DefaultListModel to add the name of the term list to the UI
	 * @param jl		the JLabel representing the status bar (for writing current loading progress)
	 * @param etplCfg	the ETPLoaderConfig that specifies which data should be loaded
	 */
	public ETPXMLExtractorThread(File f, Vector ml, DefaultListModel lm, Vector mdl, DefaultListModel lmd, JLabel jl, ETPLoaderConfig etplCfg) {
		this.fileData =  f;
		this.matrixList = ml;
		this.statusBar = jl;
		this.listMatrices = lm;
		this.metaDataList = mdl;
		this.listMetaData = lmd;
		this.etplCfg = etplCfg;
	}

	/**
	 * This method is called when the thread is started.
	 * The XML-file is deserialized, the data read and extracted.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() { 
		DataMatrix dmTermOccurrences = new DataMatrix();		// the data matrix into which the file content is loaded
		DataMatrix dmTermFrequencies = new DataMatrix();		// the data matrix into which the term frequencies are loaded
		DataMatrix dmDocumentFrequencies = new DataMatrix();	// the data matrix into which the document frequencies are loaded
		DataMatrix dmTFxIDF = new DataMatrix();					// the data matrix into which the TFxIDF values are loaded		
		Vector mdTerms = new Vector();							// the Vector into which the term list is loaded 
		Vector mdDocuments = new Vector();						// the Vector into which the local paths of the documents are loaded
		// inform user
		if (statusBar != null) {
			statusBar.setText("<html>Extracting Entity Term Profile data from file: <b>"  +  fileData.getAbsoluteFile() + "</b></html>");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
		// read ETP from XML-file
		EntityTermProfile etp = TermProfileUtils.getEntityTermProfileFromXML(this.fileData);
		// extract/convert data from ETP (if indicated by ETPLoaderConfig)
		// terms must be loaded in any case!
		mdTerms = etp.getTerms();
		// term occurrences
		if (this.etplCfg.isLoadTO()) {
			dmTermOccurrences = TermProfileUtils.getOccurrenceMatrixFromETP(etp);
			dmTermOccurrences.setName(ETPXMLExtractorThread.prefixTermOccurrences + fileData.getName() + " (" + dmTermOccurrences.getNumberOfRows() + "x" + dmTermOccurrences.getNumberOfColumns() + ")");
		} else
			dmTermOccurrences = null;
		// term frequencies
		if (this.etplCfg.isLoadTF()) {
			Hashtable<String, Long> tf = etp.getTermFrequency();
			for (int i=0; i<mdTerms.size(); i++) {
				String term = (String)mdTerms.elementAt(i);
				if (tf.containsKey(term))
					dmTermFrequencies.addValue(new Double(tf.get(term)), true);
			}
			dmTermFrequencies.removeLastAddedElement();
			dmTermFrequencies.setName(ETPXMLExtractorThread.prefixTermFrequencies + fileData.getName() + " (" + dmTermFrequencies.getNumberOfRows() + "x" + dmTermFrequencies.getNumberOfColumns() + ")");			
		} else
			dmTermFrequencies = null;
		// document frequencies
		if (this.etplCfg.isLoadDF()) {
			Hashtable<String, Integer> df = etp.getDocumentFrequency();
			for (int i=0; i<mdTerms.size(); i++) {
				String term = (String)mdTerms.elementAt(i);
				if (df.containsKey(term))
					dmDocumentFrequencies.addValue(new Double(df.get(term)), true);
			}
			dmDocumentFrequencies.removeLastAddedElement();
			dmDocumentFrequencies.setName(ETPXMLExtractorThread.prefixDocumentFrequencies + fileData.getName() + " (" + dmDocumentFrequencies.getNumberOfRows() + "x" + dmDocumentFrequencies.getNumberOfColumns() + ")");			
		} else
			dmDocumentFrequencies = null;
		// TFxIDFs
		if (this.etplCfg.isLoadTFxIDF()) {
			Hashtable<String, Double> tfxidf = etp.getTFxIDF();
			for (int i=0; i<mdTerms.size(); i++) {
				String term = (String)mdTerms.elementAt(i);
				if (tfxidf.containsKey(term))
					dmTFxIDF.addValue(new Double(tfxidf.get(term)), true);
			}
			dmTFxIDF.removeLastAddedElement();
			dmTFxIDF.setName(ETPXMLExtractorThread.prefixTFxIDFs + fileData.getName() + " (" + dmTFxIDF.getNumberOfRows() + "x" + dmTFxIDF.getNumberOfColumns() + ")");			
		} else
			dmTFxIDF = null;
		// path to documents (extracted from SingleTermLists)
		if (this.etplCfg.isLoadDocPaths()) {			
			Vector<SingleTermList> stls = etp.getSingleTermLists();
			if (stls != null) {
				for (int i=0; i<stls.size(); i++) {
					File fileLocal = stls.elementAt(i).getFileLocal();
					if (fileLocal != null)
						mdDocuments.addElement(fileLocal.toString());
				}
			}
		} else
			mdDocuments = null;
		// add extracted data to UI	
		if (listMatrices != null) {
			if (dmTermOccurrences != null)
				listMatrices.addElement(dmTermOccurrences.getName());				// add name of matrix to matrix list in UI, if possible
			if (dmTermFrequencies != null)
				listMatrices.addElement(dmTermFrequencies.getName());
			if (dmDocumentFrequencies != null)
				listMatrices.addElement(dmDocumentFrequencies.getName());
			if (dmTFxIDF != null)
				listMatrices.addElement(dmTFxIDF.getName());
		}
		if (matrixList != null) {
			if (dmTermOccurrences != null)
				matrixList.addElement(dmTermOccurrences);							// add DataMatrix-instance to matrixList-Vector, if possible
			if (dmTermFrequencies != null)
				matrixList.addElement(dmTermFrequencies);
			if (dmDocumentFrequencies != null)
				matrixList.addElement(dmDocumentFrequencies);
			if (dmTFxIDF != null)
				matrixList.addElement(dmTFxIDF);
		}
		if (metaDataList != null) {	
			if (this.etplCfg.isLoadTerms())
				metaDataList.addElement(mdTerms);
			if (mdDocuments != null)
				metaDataList.addElement(mdDocuments);
		}
		// add Meta-Data Vector to Meta-Data List
		if (listMetaData != null) {
			if (this.etplCfg.isLoadTerms())
				listMetaData.addElement(ETPXMLExtractorThread.prefixTerms + fileData.getName() + " (" + mdTerms.size() + ")");		// add name of file to meta-data list in UI, if possible
			if (mdDocuments != null)
				listMetaData.addElement(ETPXMLExtractorThread.prefixDocumentPaths + fileData.getName() + " (" + mdDocuments.size() + ")");		
		}
		// inform user
		if (statusBar != null) {
			statusBar.setText("<html>Entity Term Profile data extracted from file: <b>" + fileData.getAbsolutePath() + "</b></html>");
			statusBar.validate();
			// force status bar to be repainted immediately
			statusBar.paintImmediately(0, 0, statusBar.getWidth(), statusBar.getHeight());
		}
	}
		
}