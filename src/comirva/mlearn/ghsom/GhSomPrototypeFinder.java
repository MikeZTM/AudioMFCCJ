package comirva.mlearn.ghsom;

import java.io.Serializable;

import comirva.mlearn.GHSOM;

public interface GhSomPrototypeFinder extends Serializable {
	
	public String getPrototype(GHSOM currentSOM, int somUnitIndex);
	
}
