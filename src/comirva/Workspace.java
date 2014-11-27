/*
 * Created on 06.07.2005
 */
package comirva;

import comirva.data.DataMatrix;
import comirva.ui.model.VisuListItem;

import java.io.*;
import java.util.Vector;
import javax.swing.DefaultListModel;

/**
 * This class implements a workspace environment containing
 * data matrices and meta-data instances and the respective
 * lists. 
 * 
 * @author Markus Schedl
 */
public class Workspace implements Serializable {

	// ListModel for matrix names list
	protected DefaultListModel listMatrices = new DefaultListModel();
	// Vector for DataMatrix-instances
	protected Vector matrixList = new Vector();

	// a vector of additional Matrix name lists.
	protected Vector<DefaultListModel> additionalListMatrices = new Vector<DefaultListModel>();
	// a vector of additional DataMatrix-instances
	protected Vector<Vector> additionalMatrixList = new Vector<Vector>();
	// a vector of the tab names
	protected Vector<String> additionalMatrixNames = new Vector<String>();
	
	// ListModel for visualisation list - contains the visu names
	protected DefaultListModel listVisu = new DefaultListModel();
	// Vector for Visu-Instances
	protected Vector<VisuListItem> visuList = new Vector<VisuListItem>();
	
	// ListModel for meta-data names list - contains names of meta-data instances
	protected DefaultListModel listMetaData = new DefaultListModel();
	// Vector for MetaData-instances
	protected Vector metaDataList = new Vector();
	
	/**
	 * Creates a new empty instance of the Workspace.  
	 */
	public Workspace() {
	}
	
	/**
	 * Adds a meta-data Vector to the Workspace instance. 
	 * 
	 * @param meta	a Vector containing the meta-data as Strings
	 * @param name	a String containing a name for the meta-data which is shown in CoMIRVA's UI
	 */
	public void addMetaData(Vector meta, String name) {
		metaDataList.addElement(meta);
		listMetaData.addElement(name);
	}

	/**
	 * Adds a data matrix to the Workspace instance. 
	 * 
	 * @param dm	a DataMatrix representing the data matrix to be inserted
	 * @param name	a String containing a name for the data matrix which is shown in CoMIRVA's UI
	 */
	public void addMatrix(DataMatrix dm, String name) {
		matrixList.addElement(dm);
		listMatrices.addElement(name);
	}
	
	/** 
	 * Adds a visualisation to the Workspace instance
	 * 
	 * @param visu	a Visualisation object representing the visualisation
	 * @param name	a String containing a name for the Visualisation which is shown in CoMIRVA's UI
	 */
	public void addVisu(VisuListItem visu, String name) {
		visuList.addElement(visu);
		listVisu.addElement(name);
	}

	/** print the stored visualisation types to standard system output */
	public void printVisuTypes() {
		for (int i=0; i<visuList.size(); i++) {
			System.out.print(i + ": " + visuList.get(i).getClass().getName());
			System.out.print(" (" + listVisu.get(i) + ") ");
			System.out.println();
		}
	}
	
	/** 
	 * counts the amount of visus of class given by its class name.
	 * The given class name must be equal (in sense of the <code>equals()</code>
	 * method) to the name that is returned by
	 * the <code>getClass().getName()</code> method of the visualisation object 
	 * added to the visualisation list.
	 * @param className The name of the class that should be counted
	 * @return the amount of classes in the list
	 */
	public int countVisuTypes(String className) {
		int count = 0;
		for (int i=0; i<visuList.size(); i++) {
			if (visuList.get(i).getClass().getName().equals(className)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * creates a vector containing only the visualizations of the type
	 * given by the class name. The given class name must be equal 
	 * (in sense of the <code>equals()</code> method) to the
	 * name that is returned by the <code>getClass().getName()</code> method
	 * of the visualization object.
	 * @param className The name of the class  
	 * @return a list (vector) containing the elements of the given class
	 */
	public Vector<VisuListItem> getVisuListItems(String className) {
		Vector<VisuListItem> itemList = new Vector<VisuListItem>();
		for (int i=0; i<visuList.size(); i++) {
			VisuListItem item = visuList.get(i);
			if (item.getClass().getName().equals(className)) {
				itemList.add(item);
			}
		}
		return itemList;
	}
	
	/**
	 * creates a vector containing the names 
	 * @param className
	 * @return a vector containing the names of the visualizations of the desired type
	 */
	public Vector<String> getVisuListNames(String className) {
		Vector<String> nameList = new Vector<String>();
		for (int i=0; i<visuList.size(); i++) {
			if (visuList.get(i).getClass().getName().equals(className)) {
				nameList.add(listVisu.get(i).toString());
			}
		}
		return nameList;
	}
	
	/**
	 * This method returns a string array containing the names of visualisation
	 * of the type given by the class name. The given class name must be equal
	 * (in sense of the <code>equals()</method> method) to the name returned by the
	 * <code>getClass().getName()</code> method of the visualisation object.
	 * 
	 * This method calls the {@link #countVisuTypes(String)} method in order to
	 * determined the required size of the String array that will be returned.
	 * If you have done such a count before in your count (e.g. for existence
	 * testing or if you want to bring up an input dialog only if there are
	 * more than one possibilities) you may consider using
	 * {@link #getVisuListNamesArray(String, int)} instead.
	 * @param className The name of the class
	 * @return an array containing the names of the given type
	 */
	public String[] getVisuListNamesArray(String className) {
		return getVisuListNamesArray(className, countVisuTypes(className));	
	}
	
	/**
	 * This method returns a string array containing the names of visualisation
	 * of the type given by the class name. The given class name must be equal
	 * (in sense of the <code>equals()</code> method) to the name returned by the
	 * <code>getClass().getName()</code> method of the visualisation object.
	 * 
	 * This method may be used if you already know how many visualisations are 
	 * present in the list (by calling the {@link #countVisuTypes(String)} method,
	 * e.g. for checking if there is any visualisation of that type in the list
	 * or checking if there are more than one for showing an input dialog etc.).
	 * In this case you pass the amount as second parameter to save the call to
	 * the {@link #countVisuTypes(String)} method.
	 * 
	 * WARNING: There is no check on the count parameter. This may cause an
	 * Exception (mainly an {@link IndexOutOfBoundException}) if the value is
	 * not correct 
	 * @param className The name of the class
	 * @param count The amount of visualisations of that type in the list
	 * @return an array containing the names of the given type
	 */
	public String[] getVisuListNamesArray(String className, int count) {
		String[] nameList = new String[count];
		for (int i=0; i<visuList.size() && i<count; i++) {
			if (visuList.get(i).getClass().getName().equals(className)) {
				nameList[i] = listVisu.get(i).toString();
			}
		}
		return nameList;
	}
}
