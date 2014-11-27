/*
 * Created on 10.04.2005
 */
package comirva.util;

import java.util.*;

import comirva.data.DataMatrix;

/**
 * This class implements a simple algorithm for solving
 * the travelling salesman problem (TSP).
 * 
 * @author Markus Schedl
 */
public class TSP {
	private DataMatrix distances;			// distance matrix in DataMatrix
	private int[] tour;						// indices of the tour
	
	/**
	 * Creates a TSP-instance where the distances between the cities are given
	 * by the data matrix <code>distances</code>. 
	 * 
	 * @param distances		a DataMatrix containing the distances between all cities
	 */
	public TSP(DataMatrix distances) {
		this.distances = distances;
	}
	
	/**
	 * Initializes the TSP using a random tour. To solve the TSP, an
	 * iterative heuristical algorithm that switches two randomly
	 * chosen cities and tests the new tour against the old one is used. 
	 * 
	 * @param iter		the number of itarations to be performed
	 * @return			an int[] containing the order of the passed vertices (cities according to TSP)
	 */
	public int[] startIterations(int iter) {
		// create a random tour
		this.tour = new int[distances.getNumberOfRows()];
		// get a Vector containing a random permutation of values [0, similarities.getNumberOfRows()]
		Vector temp = new Vector();			
		Vector perm = new Vector();			 
		for (int i=0; i<distances.getNumberOfRows(); i++) {
			temp.addElement(new Double(Math.random()));
			perm.addElement(Integer.toString(i));
		}
		VectorSort.sortWithMetaData(temp, perm);		// sort
		// insert tour into tour-int[]
		for (int i=0; i<perm.size(); i++) {
			this.tour[i] = (new Integer((String)perm.elementAt(i))).intValue();
		}
//		System.out.println(perm.toString());
		double tourLength = tourDistance();			// get current tour length
		Random rand = new Random();					// for randomly chose items to swap
		// start iterations
		for (int i=0; i<iter; i++) {
			// randomly select two vertices of tour and swap them 
			int swap1 = rand.nextInt(this.tour.length);
			int swap2 = rand.nextInt(this.tour.length);
			int tempVertex = this.tour[swap1];
			this.tour[swap1] = this.tour[swap2];
			this.tour[swap2] = tempVertex; 
			// calculate new distance
			double tourLengthNew = tourDistance();
			// if better, continue; else, reswap
			if (tourLengthNew < tourLength) {
				tourLength = tourLengthNew;
//				System.out.println(tourLength);
			} else {
				tempVertex = this.tour[swap1];
				this.tour[swap1] = this.tour[swap2];
				this.tour[swap2] = tempVertex; 
			}
		}
		return this.tour;
	}
	
	/**
	 * Returns the distance of the complete tour.
	 * 
	 * @return	the distance of the tour
	 */
	private double tourDistance() {
		double tourDist = 0;
		for (int i=0; i<this.tour.length-1; i++) {
			tourDist = tourDist + distances.getValueAtPos(this.tour[i], this.tour[i+1]).doubleValue();
		}
		// from last return to first
		tourDist = tourDist + distances.getValueAtPos(this.tour[this.tour.length-1], this.tour[0]).doubleValue();
		return tourDist;
	}

}