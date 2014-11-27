package comirva.mlearn.ghsom;

import java.util.HashMap;
import java.util.Vector;

import comirva.mlearn.GHSOM;

public class WebCoocGroupPrototypeFinder implements GhSomPrototypeFinder {

	private static final long serialVersionUID = 3416995066403678779L;

	public final static int CALC_TYPE_WEBCOOC = 0;
	public final static int CALC_TYPE_WEBCOOC_NUMBER_OF_OCCS = 1;
	
	private CoOccurrencePrototypeFinder coOccurrencePrototypeFinder = null;
	private int calculationType;
	
	public WebCoocGroupPrototypeFinder(int calculationType) {
		this.calculationType = calculationType;
	}
	
	@Override
	public String getPrototype(GHSOM currentSOM, int somUnitIndex) {
		if(coOccurrencePrototypeFinder == null)
			coOccurrencePrototypeFinder = new CoOccurrencePrototypeFinder(currentSOM.getCoOccMatrixLabels(), currentSOM.getCoOccMatrix());
		if(calculationType == CALC_TYPE_WEBCOOC)
			return coOccurrencePrototypeFinder.getPrototypeOf(getBandNamesOfMapUnit(currentSOM, somUnitIndex).keySet());
		return coOccurrencePrototypeFinder.getPrototypeOf(getBandNamesOfMapUnit(currentSOM, somUnitIndex));
	}
	
	//requires altLabels (bandLabels)
	private HashMap<String, Integer> getBandNamesOfMapUnit(GHSOM currentSOM, int somUnitIndex) {
		HashMap<String, Integer> names = new HashMap<String, Integer>(); 
		Vector<Integer> vorSet = currentSOM.voronoiSet.elementAt(somUnitIndex);
		String label = null;
		for(Integer i: vorSet) {
			label = currentSOM.getAltLabel(i.intValue());
			if(names.get(label) == null)
				names.put(label, new Integer(1));
			else
				names.put(label, new Integer(names.get(label).intValue() + 1));
		}
		return names;
	}

	public CoOccurrencePrototypeFinder getCoOccurrencePrototypeFinder() {
		return coOccurrencePrototypeFinder;
	}

	public void setCoOccurrencePrototypeFinder(
			CoOccurrencePrototypeFinder coOccurrencePrototypeFinder) {
		this.coOccurrencePrototypeFinder = coOccurrencePrototypeFinder;
	}

}
