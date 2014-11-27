package comirva.mlearn.ghsom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import comirva.data.DataMatrix;
import comirva.exception.NoMatrixException;
import comirva.io.MatrixDataFileLoaderThread;

/**
 * 
 * @author mdopler
 *
 *still under construction
 */

public class CoOccurrencePrototypeFinder implements Serializable {
	
	private static final long serialVersionUID = 5442761453102555739L;
	
	private List<String> labels;
	private DataMatrix coocData;
	private HashMap<String, Double> penalizationValues = new HashMap<String, Double>();
	
	public CoOccurrencePrototypeFinder(List<String> labels, DataMatrix coocData) {
		super();
		this.labels = labels;
		this.coocData = coocData;
		initPenalizationValues();
	}
	
	/**
	 * calculates the prototype on the basis of a cooccurence matrix
	 */
	public String getPrototypeOf(Collection<String> selections) {
		if(!labels.containsAll(selections))
			throw new IllegalArgumentException("Selection not found in metadata.");
		double maximum = Double.NEGATIVE_INFINITY;
		String name = null;
		double tempValue = 0;
		for(String selection: selections) {
			//System.out.print("bl/fl ratio of " + selection + ": ");
			//System.out.println(getBacklinks(selection, selections) + "/" + getForwardlinks(selection, selections) 
				//+ "/" + getNumberOfDifferentBacklinks(selection, selections) + "//" + rankingFunction(selection, selections) + "///" + penalizationValues.get(selection));
			tempValue = rankingFunction(selection, selections);
			if(tempValue > maximum) {
				maximum = tempValue;
				name = selection;
			}
		}		
		return name;
	}

	/**
	 * calculates the prototype on the basis of a cooccurence matrix
	 * accounts for the number of occurences of the selections
	 */
	public String getPrototypeOf(HashMap<String, Integer> selectionsWithNumberOfOccs) {
		if(!labels.containsAll(selectionsWithNumberOfOccs.keySet()))
			throw new IllegalArgumentException("Selection not found in metadata.");
		double maximum = Double.NEGATIVE_INFINITY;
		String name = null;
		double tempValue = 0;
		for(String selection: selectionsWithNumberOfOccs.keySet()) {
			tempValue = rankingFunction(selection, selectionsWithNumberOfOccs.keySet())
				* Math.log(selectionsWithNumberOfOccs.get(selection).intValue() + 2);
			if(tempValue > maximum) {
				maximum = tempValue;
				name = selection;
			}
		}		
		return name;
	}
	
	/**
	 * returns a map including all values of the rankingFunction
	 */
	public HashMap<String, Double> calcutateRankingsOf(Collection<String> selections) {
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		if(!labels.containsAll(selections))
			throw new IllegalArgumentException("Selection not found in metadata.");
		for(String selection: selections) 
			resultMap.put(selection, new Double(rankingFunction(selection, selections)));
		return resultMap;
	}
	
	private double rankingFunction(String candidate, Collection<String> selections) {
		//System.out.println("candidate: " + candidate);
		return (double) getBacklinks(candidate, selections)/(double)(getForwardlinks(candidate, selections) + 1) * Math.pow(penalizationValues.get(candidate).doubleValue(), 2);
	}
	
	
	private boolean isBackLink(String candidateA, String candidateB) {
		double cab = coocData.getValueAtPos(labels.indexOf(candidateA), labels.indexOf(candidateB)).doubleValue();
		double cba = coocData.getValueAtPos(labels.indexOf(candidateB), labels.indexOf(candidateA)).doubleValue();
		double caa = coocData.getValueAtPos(labels.indexOf(candidateA), labels.indexOf(candidateA)).doubleValue();
		double cbb = coocData.getValueAtPos(labels.indexOf(candidateB), labels.indexOf(candidateB)).doubleValue();
		if(cab/Math.max(1, caa) < cba/Math.max(1, cbb))
			return true;
		return false;
	}

	
	private int getForwardlinks(String candidate, Collection<String> selections) {
		int sum = 0;
		for(String selection: selections) {
			if(!selection.equals(candidate) && !isBackLink(candidate, selection))
				sum++;
		}
		return sum;
	}
	
	private int getBacklinks(String candidate, Collection<String> selections) {
		int sum = 0;
		for(String selection: selections) {
			if(!selection.equals(candidate) && isBackLink(candidate, selection))
				sum++;
		}
		return sum;
	}
	/*
	private int getNumberOfDifferentBacklinks(String candidate, Collection<String> selections) {
		int sum = 0;
		for(String selection: selections) {
			if(!selection.equals(candidate) && coocData.getValueAtPos(labels.indexOf(selection), labels.indexOf(candidate)).doubleValue() > 0)
				sum ++;
		}
		return sum;
	}*/
	
	private void initPenalizationValues() {
		for(String candidate: labels) 
			penalizationValues.put(candidate, 
					Double.valueOf(Math.log((double)getForwardlinks(candidate, labels) 
							/ (getBacklinks(candidate, labels) + 1))));
		double tempValue = 0;
		//normalize results
		//1.get smallest value
		double minimum = Double.POSITIVE_INFINITY;
		for(Double value: penalizationValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue < minimum)
				minimum = tempValue;
		}
		//subtract all values by the minimum
		for(String key: penalizationValues.keySet()) 
			penalizationValues.put(key, 
					Double.valueOf(penalizationValues.get(key).doubleValue() - minimum));
		//replace infinite number by 0
		for(String key: penalizationValues.keySet()) {
			tempValue = penalizationValues.get(key).doubleValue();
			if(tempValue == Double.NEGATIVE_INFINITY || tempValue == Double.POSITIVE_INFINITY)
				penalizationValues.put(key, new Double(0));
		}
		//normalise by division by the maximum value
		double maximum = Double.NEGATIVE_INFINITY;
		for(Double value: penalizationValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue > maximum)
				maximum = tempValue;
		}
		for(String key: penalizationValues.keySet()) 
			penalizationValues.put(key, 
					Double.valueOf(penalizationValues.get(key).doubleValue() / maximum));		
	}
	
	public static void main(String[] args) {
		try {
			List<String> metadata = new ArrayList<String>();
			
			Vector<String> selections = new Vector<String>();
			selections.add("subway to sally");
			selections.add("corvus corax");
			selections.add("nightwish");
			selections.add("in extremo");
			selections.add("the chieftains");
			selections.add("stratovarius");
			selections.add("lunasa");
			selections.add("evanescence");
			selections.add("adriano celentano");

			
			BufferedReader metadatareader = new BufferedReader(new FileReader("C:/2545/ordered_artists.dat"));
			while(metadatareader.ready())  {
				String s = metadatareader.readLine();
				metadata.add(s);
				//System.out.println(s);
			}
			Collections.sort(metadata);
			MatrixDataFileLoaderThread dfl = new MatrixDataFileLoaderThread(new File("C:/2545/cooccurrences150b.dat"));
			CoOccurrencePrototypeFinder bla = new CoOccurrencePrototypeFinder(metadata, dfl.getMatrixFromFile());
			System.out.println("result: " + bla.getPrototypeOf(metadata));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoMatrixException e) {
			e.printStackTrace();
		}	
	}
}
