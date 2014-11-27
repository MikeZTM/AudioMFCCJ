/*
 * Created on 07.04.2005
 */
package comirva.config;

/**
 * This class represents a configuration for an SDH-Calculation.
 * It is used to pass a configuration to the SDH-instance.
 * 
 * @author Markus Schedl
 */
public class SDHConfig {
	private int spread;
	private int iterations;
	private int fractalComponent;
	
	/**
	 * Creates a new instance of an SDH-Configuration.
	 *  
	 * @param spread			the spread
	 * @param iterations		the number of interpolation iterations
	 * @param fractalComponent	the strength of the fractal component that unsharpens the SDH (for a more natural look)
	 */
	public SDHConfig(int spread, int iterations, int fractalComponent){
		this.spread = spread;
		this.iterations = iterations;
		this.fractalComponent = fractalComponent;
	}
	
	/**
	 * Returns the spread.
	 * 
	 * @return	the spread
	 */
	public int getSpread() {
		return this.spread;
	}
	/**
	 * Returns the number of interpolation iterations.
	 * 
	 * @return	the number of interpolation iterations
	 */
	public int getIterations() {
		return this.iterations;
	}

	/**
	 * Returns the strength of the fractal component used to unsharpen the SDH.
	 * 
	 * @return	the strength of the fractal component
	 */
	public int getFractalComponent() {
		return this.fractalComponent;
	}
}
