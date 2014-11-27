/*
 * Created on 15.12.2006
 */
package comirva.config;

/**
 * This class represents a configuration for a PCA-Calculation.
 * 
 * @author Markus Schedl
 */
public class PCAConfig {
	private int usedEigenvectors;
	
	/**
	 * Creates a new instance of an PCA-Configuration.
	 *  
	 * @param usedEigenvectors			the number of used Eigenvectors for PCA-projection
	 */
	public PCAConfig(int usedEigenvectors){
		this.usedEigenvectors = usedEigenvectors;
	}
	
	/**
	 * Returns the number of used Eigenvectors for the PCA-projection.
	 * 
	 * @return	the number of used Eigenvectors for the PCA-projection
	 */
	public int getUsedEigenvectors() {
		return this.usedEigenvectors;
	}

}
