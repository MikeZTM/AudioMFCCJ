/*
 * Created on 10.11.2004
 */
package comirva.mlearn;

import java.io.*;
import java.util.*;

import comirva.ui.model.VisuListItem;

/**
 * This class implements a Smoothed Data Histogram (SDH) for a SOM.
 * An SDH in its simplest form visualizes the number of data items
 * that are mapped to each map unit (<code>spread</code>=1).
 * Alternatively, it is possible that each data item votes for more
 * than one map unit and the resulting distribution is visualized.
 * In this case, the parameter <code>spread</code> equals the number
 * of map units each data items votes for.
 * 
 * @author Markus Schedl
 */
public class SDH implements Serializable, VisuListItem {
	// the SOM where the data comes from
	private SOM som;
	// size of the SOM
	private int intMURows;
	private int intMUCols;
	// voting matrix
	private double[][] votes;
	// number of iterations, the interpolation of the voting matrix is performed for visualization
	private int iterInterp = 2;
	// the interpolated voting matrix
	private double[][] interpVotes;
	// empty "map units" which form a border around the actual SDH 
	public final int borderUnits = 2;
	// flag indicating whether the border area around the data should be included when interpolating the voting matrix
	private boolean includeBordersInInterpolation = true;
	
	/**
	 * Creates a new SDH-instance based on the passed SOM.
	 * 
	 * @param som the SOM for which the SDH is to be calculated
	 */
	public SDH(SOM som) {
		this.som = som;
        // initialize voting matrix
        this.intMURows = som.getNumberOfRows();
        this.intMUCols = som.getNumberOfColumns();
        this.votes = new double[intMURows][intMUCols];
	}
	/**
	 * Creates a new SDH-instance based on the passed SOM.
	 * The number of iterations the interpolation of the voting matrix 
	 * is performed is set to <code>iter</code>. 
	 * 
	 * @param som 	the SOM for which the SDH is to be calculated
	 * @param iter 	the number of iterations, the voting matrix is interpolated 
	 */
	public SDH(SOM som, int iter) {
		this.som = som;
        // initialize voting matrix
        this.intMURows = som.getNumberOfRows();
        this.intMUCols = som.getNumberOfColumns();
        this.votes = new double[intMURows][intMUCols];
        this.iterInterp = iter;
	}
	
	/**
	 * Returns the voting matrix of the SDH. 
	 * 
	 * @return	the double[][] voting matrix
	 */
	public double[][] getVotingMatrix() {
		return this.votes;
	}
	
	/**
	 * Returns the interpolated voting matrix of the SDH. 
	 * 
	 * @return	the double[][] interpolated voting matrix
	 */
	public double[][] getInterpolatedVotingMatrix() {
		return this.interpVotes;
	}
	
	/**
	 * Returns the SOM-instance for which the SDH is calculated.
	 * 
	 * @return	the SOM-instance on which the SDH is based
	 */
	public SOM getSOM() {
		return this.som;
	}
	
	/**
	 * Calculates an SDH with the specified spread.
	 * 
	 * @param spread	the number of map units each data item votes for
	 */
	public void calcSDH(int spread) {
		// if spread > number of map units -> set spread to number of map units
		if (spread>intMURows*intMUCols)
			spread = intMURows*intMUCols;
	    // get the "spread"-most BMUs for all data items
        for (int i=0; i<som.getNumberOfDataItems(); i++) {
        	// get sorted list of BMUs
        	TreeMap BMUs = som.getOrderedBMUs((Vector)som.getDataset().getRow(i));
        	// in case of a SizeMismatchException, getOrderedBMUs returns null
        	if (BMUs != null) {
        		// get the indices in correct order out of the TreeMap
        		Collection indicesBMUs = BMUs.values();
        		// get iterator for the indices list
        		Iterator iter = indicesBMUs.iterator();
        		// get the "spread"-most BMUs out of the list containing the indices of the BMUs
        		for (int j=0; j<spread; j++) {
        			// be sure, that iterator over the BMU-indices-collection is not empty (otherwise an internal error occured)
        			if (iter.hasNext()) {
        				Integer indexCurrentBMU = (Integer)iter.next();
        				int indexBMU = indexCurrentBMU.intValue();
        	          	// get 2-dimensional coordinates out of 1-dimensional index
        	        	int mu_row = indexBMU % intMURows;
        	    		int mu_col = (int)Math.floor(indexBMU / intMURows);
        	        	// add spread-j votes to current map unit
        	    		votes[mu_row][mu_col] += (spread-j);
        			}
        		}
        	}
        }
	}

	/**
	 * Normalizes the voting matrix by dividing every element 
	 * by the maximum number of votes a map unit got.
	 */
	public void normalizeVotingMatrix() {
		// normalize voting matrix
        // get maximum of votes
        double maxVotes = 1;
        for (int i=0; i<intMURows; i++) {
        	for (int j=0; j<intMUCols; j++) {
        		if (this.votes[i][j] > maxVotes)
        			maxVotes = votes[i][j];
        	}
        }
        // divide the voting counter of every map unit by the maximum voting counter 
        for (int i=0; i<intMURows; i++) {
        	for (int j=0; j<intMUCols; j++) {
            	this.votes[i][j] = this.votes[i][j]/maxVotes;
        	}
        }		
	}
	
	/**
	 * Prepares the SDH-instance for visualization by interpolating
	 * the voting matrix. At first, the voting matrix is interpolated once.
	 * Then, a border around it is inserted. Finally, interpolation is performed
	 * <code>iter</code> times.
	 * 
	 * @param iter	the number of iterations that are performed
	 */
	public void interpolateVotingMatrix(int iter) {
    	this.interpVotes = this.votes;
    	// create new matrix filling the borders around the outer map units
        int interpRows = this.interpVotes.length;
        int interpCols = this.interpVotes[0].length;
        // create temporary matrix containing original voting matrix and border around it
        double[][] sdhMatrix = new double[interpRows+2][interpCols+2];
        for (int i=0; i<interpRows+2; i++) {
        	for (int j=0; j<interpCols+2; j++) {
        		// if border region, insert zeros
        		if ((i==0) || (j==0) || (i==interpRows+1) || (j==interpCols+1))
        			sdhMatrix[i][j] = 0;
        		else	// else, insert interpolating voting matrix values
        			sdhMatrix[i][j] = this.interpVotes[i-1][j-1];
        	}
        }
        // set interpolated votes matrix to temporary matrix
        this.interpVotes = sdhMatrix;
        // interpolate again (as ofter as specified in parameter iter)
        for (int i=0; i<iter; i++) {
        	this.interpVotes = SDH.interpolateMatrix(this.interpVotes);	
        }
	}
	
	
	/** 
	 * Interpolates the double[][] matrix <code>votes</code> linearly by inserting
	 * a data item containing the medium of the neighboring values
	 * between every array element. The resulting matrix has size <code>(oldNumberOfRows*2)-1 x (oldNumberOfColumns*2)-1</code>.
	 * 
	 * @param votes	the double[][] matrix to be interpolated
	 * @return	a double[][] matrix containing the interpolated values
	 */
	public static double[][] interpolateMatrix(double[][] votes) {
        // interpolate values
        // x11  *  x12
        //  *   *   *
        // x21  *  x22
        // create a new matrix with size (oldNumberOfRows*2)-1 x (oldNumberOfColumns*2)-1
    	int intMURows = votes.length;
    	int intMUCols = votes[0].length;
    	int interpRows = intMURows*2-1;
        int interpCols = intMUCols*2-1;
        double[][] interpVotes = new double[interpRows][interpCols];
        // insert original values (in every second row and column)
        for (int i=0; i<intMURows; i++) {
        	for (int j=0; j<intMUCols; j++) {
        		interpVotes[i*2][j*2] = votes[i][j];
        	}
        }
        // insert "horizontal" interpolations -> x11 * x12
        for (int i=0; i<intMURows; i++) {
        	for (int j=0; j<intMUCols-1; j++) {
        		interpVotes[i*2][j*2+1] = (votes[i][j] + votes[i][j+1]) / 2;
        	}
        }
        // inser "vertical" interpolations
        for (int i=0; i<intMURows-1; i++) {
        	for (int j=0; j<intMUCols; j++) {
        		interpVotes[i*2+1][j*2] = (votes[i][j] + votes[i+1][j]) / 2;
        	}
        }
        // insert "diagonal" interpolations
        for (int i=0; i<intMURows-1; i++) {
        	for (int j=0; j<intMUCols-1; j++) {
        		interpVotes[i*2+1][j*2+1] = (votes[i][j] + votes[i+1][j] + votes[i][j+1] + votes[i+1][j+1]) / 4;
        	}
        }
        // unsharpen the SDH
        Random rand = new Random();		// randomly unsharpen
        int unsharpenFactor = 250;		// the higher, the less influence the randomly inserted noise has 
        for (int i=0; i<interpVotes.length; i++) {
        	for (int j=0; j<interpVotes[i].length; j++) {
        		interpVotes[i][j] = interpVotes[i][j]  + (rand.nextDouble()-0.5)/unsharpenFactor; 
        	}
        }
        // return the interpolated votes matrix
        return interpVotes;
    }

	/**
	 * "Unsharpens" the SDH by adding a fractal component to its voting matrix.
	 * To every element of the voting matrix a random value between <code>([-0.5, 0.5]/1000)*fractalComponent</code> is added.
	 * 
	 * @param fractalComponent	the strength of the fractal component (should be in [0, 100]))
	 */
	public void addFractalComponent(int fractalComponent) {
		// unsharpen the SDH
	    Random rand = new Random();					// randomly unsharpen 
	    for (int i=0; i<interpVotes.length; i++) {
	    	for (int j=0; j<interpVotes[i].length; j++) {
	    		interpVotes[i][j] = interpVotes[i][j]  + ((rand.nextDouble()-0.5)/1000)*fractalComponent; 
	    	}
	    }		
	}
}
