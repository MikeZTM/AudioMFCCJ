package comirva.mlearn.ghsom;

import comirva.mlearn.GHSOM;

public interface GhSomIndividualPrototypeFinder extends GhSomPrototypeFinder {
	
	public int getIndexOfPrototype(GHSOM currentSOM, int somUnitIndex);
	
}
