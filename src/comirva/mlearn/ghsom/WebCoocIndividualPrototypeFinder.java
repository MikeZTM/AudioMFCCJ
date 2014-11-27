package comirva.mlearn.ghsom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import comirva.exception.SizeMismatchException;
import comirva.mlearn.GHSOM;
import comirva.mlearn.SOM;

/**
 * Tries to find a song prototype for a given map unit of a GHSOM.
 * Uses the cooccurence information of the bands and the proximity
 * of the bands to the map unit to calcutate the prototype.
 */
//TODO: cache already calculated results
public class WebCoocIndividualPrototypeFinder implements GhSomIndividualPrototypeFinder {

	private static final long serialVersionUID = -4906359084568577031L;

	private CoOccurrencePrototypeFinder coOccurrencePrototypeFinder = null;
	
	@Override
	public int getIndexOfPrototype(GHSOM currentSOM, int somUnitIndex) {
		if(coOccurrencePrototypeFinder == null)
			coOccurrencePrototypeFinder = new CoOccurrencePrototypeFinder(currentSOM.getCoOccMatrixLabels(), currentSOM.getCoOccMatrix());
		HashMap<Integer, Double> songId_Result = new HashMap<Integer, Double>();
		Set<String> bandNames = getBandNamesOfMapUnit(currentSOM, somUnitIndex);
		HashMap<String, Double> coocRankingValues = coOccurrencePrototypeFinder.calcutateRankingsOf(bandNames);
		HashMap<Integer, Double> songProximityValues = calculateSongProximityValue(currentSOM, somUnitIndex);
		
		for(Integer songId: currentSOM.voronoiSet.elementAt(somUnitIndex)) 
			songId_Result.put(songId, new Double(
					coocRankingValues.get(currentSOM.getAltLabel(songId.intValue())).doubleValue() *
					songProximityValues.get(songId).doubleValue()));
		
		int result = -1;
		double maximum = Double.NEGATIVE_INFINITY;
		double temp = 0.0;
		for(Integer songId: songId_Result.keySet()) {
			temp = songId_Result.get(songId).doubleValue();
			if(temp > maximum) {
				maximum = temp;
				result = songId.intValue();
			}
		}
		return result;
	}
	
	public List<Integer> getRankOfAllSongs(GHSOM currentSOM, int somUnitIndex) {
		if(coOccurrencePrototypeFinder == null)
			coOccurrencePrototypeFinder = new CoOccurrencePrototypeFinder(currentSOM.getCoOccMatrixLabels(), currentSOM.getCoOccMatrix());
		
		Set<String> bandNames = getBandNamesOfMapUnit(currentSOM, somUnitIndex);
		HashMap<String, Double> coocRankingValues = coOccurrencePrototypeFinder.calcutateRankingsOf(bandNames);
		HashMap<Integer, Double> songProximityValues = calculateSongProximityValue(currentSOM, somUnitIndex);

		TreeMap<Double, Integer> songId_Result = new TreeMap<Double, Integer>();
		for(Integer songId: currentSOM.voronoiSet.elementAt(somUnitIndex)) {
			//workaround to guarantee distinct values
			Double songValue = Double.valueOf(
					coocRankingValues.get(currentSOM.getAltLabel(songId.intValue())).doubleValue() *
					songProximityValues.get(songId).doubleValue());
			while(songId_Result.containsKey(songValue))
				songValue = Double.valueOf(songValue.doubleValue() + 0.000001);
			
			songId_Result.put(songValue, songId);
		}
		
		List<Integer> result = new ArrayList<Integer>(); 
		result.addAll(songId_Result.values());
		Collections.reverse(result);
		return result;
	}
	
	@Override
	public String getPrototype(GHSOM currentSOM, int somUnitIndex) {
		String result = null;
		int index = getIndexOfPrototype(currentSOM, somUnitIndex);
		if(index != -1)
			result = currentSOM.getLabel(index);
		return result;
	}
	
	public static HashMap<Integer, Double> calculateSongProximityValue(GHSOM currentSOM, int somUnitIndex) {
		Vector<Integer> vorSet = currentSOM.voronoiSet.elementAt(somUnitIndex);
		Vector<Double> mean = currentSOM.calculateVoronoiMean(vorSet);
		HashMap<Integer, Vector<Double>> songData = new HashMap<Integer, Vector<Double>>();
		for(Integer songId: vorSet) 
			songData.put(songId, currentSOM.data.getRow(songId.intValue()));
		return calculateSongProximityValue(mean, songData);
	}
	
	public static HashMap<Integer, Double> calculateSongProximityValue(Vector<Double> mean, HashMap<Integer, Vector<Double>> songData) {
		HashMap<Integer, Double> songProximityValues = new HashMap<Integer, Double>();
		
		for(Integer songId: songData.keySet()) {
			try {
				songProximityValues.put(songId, new Double(
					 1 / (1 + Math.log(1 + SOM.euclideanDistance(songData.get(songId), mean)))));
			} catch(SizeMismatchException e) {
				e.printStackTrace();
				songProximityValues.put(songId, new Double(Double.NEGATIVE_INFINITY));
			}
		}
		return normalize(songProximityValues);
	}

	private static HashMap<Integer, Double> normalize(HashMap<Integer, Double> songProximityValues) {
		if(!valuesOkForNormalization(songProximityValues.values())) {
			for(Integer key: songProximityValues.keySet()) 
				songProximityValues.put(key, new Double(1));
			return songProximityValues;
		}
		double tempValue = 0;
		//normalize results
		//1.get smallest value
		double minimum = Double.POSITIVE_INFINITY;
		for(Double value: songProximityValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue < minimum)
				minimum = tempValue;
		}
		//subtract all values by the minimum
		for(Integer key: songProximityValues.keySet()) 
			songProximityValues.put(key, 
					Double.valueOf(songProximityValues.get(key).doubleValue() - minimum));
		//normalise by division by the maximum value
		double maximum = Double.NEGATIVE_INFINITY;
		for(Double value: songProximityValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue > maximum)
				maximum = tempValue;
		}
		for(Integer key: songProximityValues.keySet()) 
			songProximityValues.put(key, 
					Double.valueOf(1 + songProximityValues.get(key).doubleValue() / maximum));	
		return songProximityValues;
	}
	
	private static boolean valuesOkForNormalization(Collection<Double> values) {
		if(values.size() == 1)
			return false;
		boolean allValuesTheSame = true;
		Double reference = null;
		for(Double value: values) {
			if(reference == null)
				reference = value;
			else if(!reference.equals(value))
				allValuesTheSame = false;
		}		
		return !allValuesTheSame;
	}

	//requires altLabels (bandLabels)
	private Set<String> getBandNamesOfMapUnit(GHSOM currentSOM, int somUnitIndex) {
		Set<String> names = new HashSet<String>(); 
		for(Integer songIndex: currentSOM.voronoiSet.elementAt(somUnitIndex)) 
			names.add(currentSOM.getAltLabel(songIndex.intValue()));
		return names;
	}

}
