/*
 * Created on 09.11.2004
 */
package comirva.visu.colormap;

import java.awt.*;

/**
 * This class implements a colormap, i.e. a mapping from a normalized range [0,1]
 * to a defined set of colors.
 * 
 * @author Markus Schedl
 */
public class ColorMap {
	// the number of points for which a color can be defined (sampling points)
	private int granularity = 64;
	// the color mapping 
	private Color[] mapping = new Color[granularity];
	// a vector marking all fixed sampling points
	private boolean[] samplingPoint = new boolean[granularity];
	
	/**
	 * Creates a ColorMap with defined mapping colors for
	 * the lower and upper margin of the value range.
	 * 
	 * @param lowerMargin	the Color for the value 0
	 * @param upperMargin	the Color for the value 1
	 */
	public ColorMap(Color lowerMargin, Color upperMargin) {
		// set lower and upper mapping
		this.mapping[0] = lowerMargin;
		this.mapping[granularity-1] = upperMargin;
		// set all indices to "no sampling point"
		for (int i=0; i<granularity; i++)
			this.samplingPoint[i] = false;
		// add indices of sampling points (0 and granularity-1)
		this.samplingPoint[0] = true;
		this.samplingPoint[granularity-1] = true;
		// interpolate remaining values
		interpolateMap();
	}
	/**
	 * Creates a ColorMap with defined mapping colors for
	 * the lower and upper margin of the value range and the specified granularity.
	 * 
	 * @param lowerMargin	the Color for the value 0
	 * @param upperMargin	the Color for the value 1
	 * @param granularity	the number of different Colors available in the ColorMap
	 */
	public ColorMap(Color lowerMargin, Color upperMargin, int granularity) {
		// set granularity and lower and upper mapping
		this.granularity = granularity;
		// re-initialize sampling points
		this.samplingPoint = new boolean[granularity];
		// set all indices to "no sampling point"
		for (int i=0; i<granularity; i++)
			this.samplingPoint[i] = false;
		// add indices of sampling points (0 and granularity-1)
		this.samplingPoint[0] = true;
		this.samplingPoint[granularity-1] = true;
		// re-initialize mapping array
		this.mapping = new Color[granularity];
		// set mapping for lower and upper margin
		this.mapping[0] = lowerMargin;
		this.mapping[granularity-1] = upperMargin;
		// interpolate remaining values
		interpolateMap();
	}
	
	/**
	 * Defines a new sampling point of the ColorMap.
	 * Between each pair of sampling points, the colors are interpolated. 
	 * 
	 * @param col		the Color to be added
	 * @param position	the index in the range <code>[0, granularity-1]</code> where the sampling point is to be inserted
	 */
	public void addColor(Color col, int position) {
		// position must be less then total number of Colors available in the ColorMap
		if (position < granularity) {
			// set mapping and samplingPoint
			this.mapping[position] = col;
			this.samplingPoint[position] = true;
			// interpolate color values
			interpolateMap();
		}
	}
	
	/**
	 * Interpolates the color values between the sampling points (e.g. lower and upper margin).
	 */
	private void interpolateMap() {
		// index of first sampling point is 0
		int currentSP = 0;
		// travers all elements of mapping
		while (currentSP < granularity-1) {
			// difference to next sample point
			int difToNextSP = 1;
			// get index of next sampling point
			while (this.samplingPoint[currentSP+difToNextSP] == false)
				difToNextSP++;
			// interpolate values between sampling points
//			System.out.println("Interpolation between sampling points " + currentSP + " and " + (currentSP+difToNextSP) + ": ");
			for (int i=currentSP; i<currentSP+difToNextSP; i++) {
				// get color limits between which the interpolation should take place
				Color start = mapping[currentSP];
				Color end = mapping[currentSP+difToNextSP];
				// calculate weighting
				double weighting = (double)(i-currentSP)/(double)(difToNextSP);				
				// interpolate colors (red, green, and blue components)
				double newRed = Math.floor(start.getRed()*(1-weighting) + end.getRed()*weighting);
				double newGreen = Math.floor(start.getGreen()*(1-weighting) + end.getGreen()*weighting);
				double newBlue = Math.floor(start.getBlue()*(1-weighting) + end.getBlue()*weighting);
//				System.out.println("weighting: " + weighting + "\t" + i + ":\t" + newRed + "\t" + newGreen + "\t" + newBlue);
				mapping[i] = new Color((int)newRed, (int)newGreen, (int)newBlue);
			}
			// next sampling point...
			currentSP += difToNextSP;
		}
	}
	
	/**
	 * Returns the color defined in the ColorMap for the specified value, 
	 * where the argument <code>value</code> must be normalized, i.e. in the range [0,1]. 
	 * 
	 * @param value	the normalized double value for which the color should be returned
	 * @return	the Color for the <code>value</code> passed as argument 
	 */
	public Color getColor(double value) {
		if (value<0)			// value outside lower range border -> return color for lowest value
			return this.mapping[0];
		if (value>1)			// value outside upper range border -> return color for highest value
			return this.mapping[this.granularity-1];
		else					// value inside range
			return this.mapping[(int)Math.round(value*(this.granularity-1))];
	}
	
	/**
	 * Inverts each color of the ColorMap.
	 */
	public void invert() {
		// create new inverted mapping
		Color[] mappingInverted = new Color[granularity];
		// copy the elements from mapping to mappingInverted in reversed order
		for (int i=0; i<granularity; i++) {
			mappingInverted[i] = mapping[granularity-i-1];
		}
		// use inverted mapping
		mapping = mappingInverted;
	}
}