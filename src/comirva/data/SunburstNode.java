/*
 * Created on 15.12.2005
 */
package comirva.data;

import java.util.*;

import comirva.util.TermProfileUtils;
import comirva.util.VectorSort;

/**
 * This class implements the data structure
 * for a node in a Sunburst visualization.
 * 
 * @author Markus Schedl
 */
public class SunburstNode {
	// term occurrence matrix
	private DataMatrix to = null;								
	// all terms for the term occurrence matrix 
	private Vector<String> allTerms = new Vector<String>();
	// term list of all nodes above current node (all terms that co-occur with term that is represented with current node)
	private Vector<String> coocTerms = new Vector<String>();	
	// TFxIDF values of terms
	private Vector<Double> TFxIDF = new Vector<Double>();	
	// the child notes situated one level below in the hierarchy
	private Vector<SunburstNode> childNodes = new Vector<SunburstNode>();
	// parent node
	private SunburstNode parentNode = null;
	// optional: a list of documents that are represented by this node (these are the documents that contain all terms of coocTerms)
	private Vector<String> documents = null;
	// document frequency of all co-occurring terms of the node
	private long docFreq = 0;
	// sum-normalized importance of current node relative to importance of all other nodes on same hierarchy level
	private double importance = 0;
	// maximum-normalized importance of current node relative to importance of all other nodes on same hierarchy level
	private double importanceMaxNorm = 0;
	// angular start position of node in degrees
	private double angularStartPosition = 0.0;
	// maximum number of items for this node
	private int maxItemsPerNode = 20;
	// maximum number of co-occuring terms (maximum depth of sunburst tree)
	private int maxDepth = 8;
	// minimum importance (child nodes with an importance below that threshold will be excluded from the sunburst)
	private double minImportance = 1.0/360.0;
	// depth of sunburst (only written in root node)
	private int depthSunburst = 0;
	
	// debug flag
	boolean isDebugMode = false;
	
	
	/**
	 * Creates a new SunburstNode.
	 * 
	 * @param to					a DataMatrix representing the term occurrences
	 * @param allTerms				a Vector<String> containing all terms that are shown by the term occurrence matrix 
	 * @param coocTerms				a Vector<String> of the co-occurring terms of the node
	 * @param importance			sum-normalized importance of current node relative to importance of all other nodes on same hierarchy level
	 * @param importanceMaxNorm		maximum-normalized importance of current node relative to importance of all other nodes on same hierarchy level
	 * @param maxItemsPerNode		maximum number of items per node
	 * @param maxDepth				maximum depth of tree (maximum number of co-occurring terms)
	 * @param minImportance			minimum importance (child nodes with an importance below that threshold will be excluded from the sunburst)
	 * @param parent				a SunburstNode pointing to the parent node of this instance
	 */
	public SunburstNode(DataMatrix to, Vector<String> allTerms, Vector<String> coocTerms, double importance, double importanceMaxNorm, double angularStartPosition, int maxItemsPerNode, int maxDepth, double minImportance, SunburstNode parent) {
		this.to = to;
		this.allTerms = allTerms;
		this.coocTerms = coocTerms;
		this.importance = importance;
		this.importanceMaxNorm = importanceMaxNorm;
		this.angularStartPosition = angularStartPosition;
		this.docFreq = this.to.getNumberOfColumns();
		this.maxItemsPerNode = maxItemsPerNode + 1;		// +1 because the term representing the node itself is eliminated from the co-ocs
		this.maxDepth = maxDepth;
		this.minImportance = minImportance;
		this.parentNode = parent;
		// write depth of sunburst to root node
		int depthCurrentNode = this.getDepth();
		SunburstNode rootNode = this.getRootNode();
		if (depthCurrentNode > rootNode.depthSunburst)
			rootNode.depthSunburst = depthCurrentNode;
		// debug
		if (this.isDebugMode)
			System.out.println("sunburst node for terms "+this.coocTerms.toString()+"\timportance="+this.importance+" ("+this.getAngularExtent()+"°)\tdf="+this.docFreq+"\tangular start position="+this.angularStartPosition);
	}
			
	/**
	 * Determines all nodes of the sunburst and
	 * calculates their document frequencies. 
	 */
	public void calculateSunburst() {
        // determine most often occurring terms
		// TFxIDF version
//		Hashtable<String,Double> maxOccTerms = TermProfileUtils.getTermsWithHighestTFxIDF(this.TFxIDF, this.allTerms, this.maxItemsPerNode);
		// DF version
		Hashtable<String,Integer> maxOccTerms = TermProfileUtils.getTermsWithHighestOccurrence(this.to, this.allTerms, this.maxItemsPerNode);
        // if terms were returned, order returned hashtable
		if (maxOccTerms != null) {
	        Vector<String> tmpTerm = new Vector<String>();
	        Vector<Double> tmpDF = new Vector<Double>();
	        long totalOccs = 0;
	        double maxOccs = 0;
	        Enumeration<String> e2 = maxOccTerms.keys();
	        while (e2.hasMoreElements()) {
	        	String term = e2.nextElement();
	        	Double df = new Double(maxOccTerms.get(term));
	        	// exclude terms that are already in co-oc term list of current node
	        	if (!this.coocTerms.contains(term)) {
	            	tmpTerm.addElement(term);
	            	tmpDF.addElement(df);
	            	totalOccs += df.intValue();
	            	if (df.doubleValue()>maxOccs)
	            		maxOccs=df.doubleValue();        		
	        	}
	        }
	        // continue only if terms could be extracted and maximum depth of sunburst tree not reached
	        if (tmpDF.size() != 0 && tmpTerm.size() != 0 && this.maxDepth > this.coocTerms.size()) {
	            // sort most often occurring terms wrt their document frequency
	            VectorSort.sortWithMetaData(tmpDF,tmpTerm);
	            double angularPosition = this.angularStartPosition;	// sum up angular extents
	            // generate child nodes
	            for (int i=0; i<tmpTerm.size(); i++) {
	            	// calculate importance of child node
	            	double importanceChild = this.importance*(tmpDF.elementAt(i).doubleValue()/totalOccs);
	            	// calculate max-normalized importance value
	            	double maxNormImportanceChild = this.importance*(tmpDF.elementAt(i).doubleValue()/maxOccs);
	            	// only proceed if importance of child is above threshold
	            	if (importanceChild >= this.minImportance) {
	                	// generate list of terms that must be contained in children at lower hierarchy level
	                	Vector<String> coocTermsChild = (Vector<String>)this.coocTerms.clone();			// clone co-oc term list of current node
	                    coocTermsChild.addElement(tmpTerm.elementAt(i));								// add new term from extracted most occurring term list 
	                   	// create a subset of the term occurrence matrix that contains only
	                    // those documents that contain all terms from coocTermsChild 
	    				Vector<Integer> idxDocPaths = new Vector<Integer>();	// to store indices of documents that contain the filter terms 
	                    DataMatrix sub = TermProfileUtils.getSubsetOfTermOccurrenceMatrix(this.to, this.allTerms, coocTermsChild, idxDocPaths);
	                    // if documents that contain all terms of the co-oc term list exist,
	                    // create a new sub node
	                    if (sub != null) {
	                       	SunburstNode childNode = new SunburstNode(sub, this.allTerms, coocTermsChild, importanceChild, maxNormImportanceChild, angularPosition, this.maxItemsPerNode, this.maxDepth, this.minImportance, this);
	                       	// set documents if available
	                       	if (this.getDocuments() != null)
	                       		childNode.setDocuments(TermProfileUtils.getMaskedDocumentPaths((Vector<String>)this.getDocuments().clone(), idxDocPaths));
	                       	else
	                       		childNode.setDocuments(null);
	                       	// set IFxIDF values
	                       	if (this.getTFxIDF() != null)
	                       		childNode.setTFxIDF(this.TFxIDF);
	                       	else
	                       		childNode.setTFxIDF(null);
	                       	// calculate child node properties
	                       	childNode.calculateSunburst();
	                       	this.childNodes.addElement(childNode);              		
	                    }            		
	            	}
	            	// calculate angular start position for next child
	            	angularPosition += importanceChild*360.0;
	            }        	
	        }			
		}
	}
	
	/**
	 * Returns the importance of this node expressed
	 * as angular extent of the node.
	 * 
	 * @return		a double representing the angular extent
	 */
	public double getAngularExtent() {
		return this.importance*360;
	}
	
	/**
	 * Returns the hierarchy level of the current node.
	 * Note that the root node has a depth of 1.
	 * 
	 * @return	the depth of the current node
	 */
	public int getDepth() {
		int depth = 1;
		// find root node and count iterations necessary -> that's the depth
		SunburstNode root = this;
		while (root.parentNode != null) {
			root = root.parentNode;
			depth++;
		}
		return depth;
	}
	
	/**
	 * Determines and returns the depth of
	 * the sunburst, i.e. the maximum hierarchy level. 
	 * 
	 * @return	the depth in hierarchy levels
	 */
	public int getSunburstDepth() {
		// the depth of the sunburst is stored in the root node
		return this.getRootNode().depthSunburst;
	}
	
	/**
	 * Finds and returns the root node.
	 */
	private SunburstNode getRootNode() {
		// find parent
		SunburstNode root = this;
		while (root.parentNode != null) {
			root = root.parentNode;
		}
		return root;
	}

	/**
	 * @return Returns the childNodes.
	 */
	public Vector<SunburstNode> getChildNodes() {
		return childNodes;
	}

	/**
	 * @return Returns the coocTerms.
	 */
	public Vector<String> getCoocTerms() {
		return coocTerms;
	}

	/**
	 * @return Returns the importance.
	 */
	public double getImportance() {
		return importance;
	}

	/**
	 * @return Returns the importanceMaxNorm.
	 */
	public double getImportanceMaxNorm() {
		return importanceMaxNorm;
	}

	/**
	 * @return Returns the angularStartPosition.
	 */
	public double getAngularStartPosition() {
		return angularStartPosition;
	}

	/**
	 * @return Returns the parentNode.
	 */
	public SunburstNode getParentNode() {
		return parentNode;
	}

	/**
	 * @return returns the document frequency
	 */
	public long getDocumentFrequency() {
		return docFreq;
	}

	/**
	 * @return returns the term occurrence matrix
	 */
	public DataMatrix getTermOccurrenceMatrix() {
		return to;
	}

	/**
	 * @return returns a Vector<String> containing all terms (corresponding to the term occurrence matrix)
	 */
	public Vector<String> getAllTerms() {
		return allTerms;
	}

	/**
	 * @return returns a Vector<String> of documents that contain all terms that are represented by this node
	 */
	public Vector<String> getDocuments() {
		return documents;
	}

	/**
	 * @param documents a Vector<String> that represents a list of documents that contain all terms that are represented by this node
	 */
	public void setDocuments(Vector<String> documents) {
		this.documents = documents;
	}

	/**
	 * @return Returns the TFxIDF.
	 */
	public Vector<Double> getTFxIDF() {
		return TFxIDF;
	}

	/**
	 * @param TFxIDF The TFxIDF to set.
	 */
	public void setTFxIDF(Vector<Double> TFxIDF) {
		this.TFxIDF = TFxIDF;
	}	
	
}