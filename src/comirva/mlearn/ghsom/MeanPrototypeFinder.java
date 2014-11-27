package comirva.mlearn.ghsom;

import java.util.Vector;

import comirva.exception.SizeMismatchException;
import comirva.mlearn.GHSOM;

public class MeanPrototypeFinder implements GhSomIndividualPrototypeFinder {

	private static final long serialVersionUID = 7460359597372237464L;

	@Override
	public String getPrototype(GHSOM currentSOM, int somUnitIndex) {
		int indexOfBestMatchingData = getIndexOfPrototype(currentSOM, somUnitIndex);
		if(indexOfBestMatchingData != -1) 
			return currentSOM.getLabel(indexOfBestMatchingData);
		return null;
	}
	
	private int getBestMatchingDataVector(GHSOM currentSOM, int somUnitIndex, Vector<Double> mean) {
		double minDist = Double.MAX_VALUE;		// minimum distance
		int indexMinDist = -1; 					// index of model vector with minimal distance to dataItem (BMU)
		double value;							// current value for Euclidean distance
		// calculate Euclidean distances between somUnit
		// and every model vector in the voronoiSet of the somUnit
		if (currentSOM.voronoiSet != null) {
			Vector<Integer> voronoiSet = currentSOM.voronoiSet.elementAt(somUnitIndex);
			/*
			if(temp.size() == 0)
				System.out.println("EMPTY");*/
			for (int i=0; i<voronoiSet.size(); i++) {
				// if new minimal distance was found, remember this distance and index of model vector
				try {
					Integer labelIndex = voronoiSet.elementAt(i);
					
					value = currentSOM.euclideanDistance(currentSOM.data.getRow(labelIndex.intValue()), mean);
					if (value < minDist) {
						minDist = value;
						indexMinDist = labelIndex.intValue();
					}
				} catch (SizeMismatchException sme) {
					return -1;
				}
			}
		}
		return indexMinDist;
	}

	@Override
	public int getIndexOfPrototype(GHSOM currentSOM, int somUnitIndex) {
		Vector<Integer> vorSet = currentSOM.voronoiSet.elementAt(somUnitIndex);
		Vector<Double> mean = currentSOM.calculateVoronoiMean(vorSet);
		return getBestMatchingDataVector(currentSOM, somUnitIndex, mean);
	}
}
