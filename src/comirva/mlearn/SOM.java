 /*
 * Created on 28.10.2004
 */
package comirva.mlearn;

//using Jama 1.0.2: http://math.nist.gov/javanumerics/jama/
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import comirva.data.DataMatrix;
import comirva.exception.*;
import comirva.ui.model.VisuListItem;
import comirva.util.PCAProjectionToColor;
import comirva.util.VectorSort;

import java.awt.Color;
import java.io.*;
import java.util.*;

import javax.swing.JLabel;


/**
 * This class implements a Self-Organizing Map and
 * some useful algorithms for initializing and training.
 * 
 * @author Markus Schedl
 */
public class SOM implements Serializable, VisuListItem {

	private static final long serialVersionUID = -2174426537507018171L;

	// possible initialization methods
	public static final int INIT_RANDOM = 0;		// random
	public static final int INIT_GRADIENT = 1;		// linear gradient
	public static final int INIT_LINEAR = 2;		// linear initialization (kohonen)
	public static final int INIT_SLC = 3;			// initialization proposed by Su, Liu, Chang
	// possible training methods
	public static final int TRAIN_SEQ = 0;			// sequential
	public static final int TRAIN_BATCH = 1;		// batch SOM
	
	// DataMatrix containing the training data
	public DataMatrix data = new DataMatrix(); 
	// DataMatrix containing the codebook
	// The rows of the codebook instance represent the map units.
	// Each row of the codebook instance contains a model vector.
	protected DataMatrix codebook = new DataMatrix("SOM");
	// Vector containing the labels (one label for each data item)
	protected Vector<String> labels;
	// required for special operations (ghsom webcoOcc prototyping - band/song matching)
	protected Vector<String> altLabels;
	// optional (e.g. used for webcoOcc prototyping)
	protected DataMatrix coOccMatrix;
	// metadata required for coOccMatrix
	protected Vector<String> coOccMatrixLabels;
	// size of the SOM (number of map units in rows and columns)
	protected int intMURows;
	protected int intMUCols;
	// Vector representing the Voronoi-set
	public Vector<Vector<Integer>> voronoiSet;
	// training length in epochs
	protected int trainingLength = 1;			// by default train for 1 epoch
	protected int method = 0;					// by default seq training
	
	protected boolean circular = false;			
	// coloring
	private boolean colorByPCA = false;					// choose background color of grid cells according to PCA color assignment
	private Color[] gridcolors = null;
	
	// associated MDM
	private MDM mdm = null;
	
	/**
	 *  <code>statusBar</code> represents the status bar of the calling MainUI-instance and is used to update the status bar while performing training 
	 */
	public JLabel statusBar;

	/** 
	 * Creates a SOM-instance with the training data contained in the DataMatrix <code>trainData</code>.<br>
	 * The size of the SOM (the codebook) is determined with a heuristic function.
	 * 
	 * @param trainData		a DataMatrix containing the data for training the SOM
	 */
	public SOM(DataMatrix trainData) {
		this.data = trainData;
		// determine number of map units by using a heuristic function
		setSOMSizeEstimation();
	}
	/**
 	 * Creates a SOM-instance with the training data contained in the DataMatrix <code>trainData</code>.<br>
	 * The size of the SOM (the codebook) is set to the values specified by the arguments <code>rows</code> and <code>cols</code>. 
	 *
	 * @param trainData		a DataMatrix containing the data for training the SOM
	 * @param rows			the number of map units in vertical direction
	 * @param cols			the number of map units in horizontal direction
	 */
	public SOM(DataMatrix trainData, int rows, int cols) {
		this.data = trainData;
		this.intMURows = rows;
		this.intMUCols = cols;
	}
	
	/**
	 * Returns the number of map units of the SOM in vertical direction.
	 * 
	 * @return	the number of map units in vertical direction
	 */
	public int getNumberOfRows() {
		return this.intMURows;
	}
	
	/**
	 * Returns the number of map units of the SOM in horizontal direction.
	 * 
	 * @return	the number of map units in horizontal direction
	 */
	public int getNumberOfColumns() {
		return this.intMUCols;
	}
	
	/**
	 * Returns the number of data items in the training set.
	 *
	 * @return	the number of data items of the SOM
	 */
	public int getNumberOfDataItems() {
		return data.getNumberOfRows();
	}
	
	/**
	 * Sets the number of map units to an estimation.
	 * The heuristic function <code>#mus = 3*(#data_items^0.5+2)</code> is used.
	 */
	private void setSOMSizeEstimation() {
		// set number of map units to standard values
		this.intMURows = 6;
		this.intMUCols = 9;
		// ratio between number of horizontal and number of vertical map units
		double widthHeightRatio = 1.5;
		if (data != null) {
			double numberMUs = 3*(Math.sqrt(data.getNumberOfRows())+2);
			this.intMURows = (int)Math.ceil(Math.sqrt(numberMUs/widthHeightRatio));
			this.intMUCols =(int)Math.ceil(this.intMURows*widthHeightRatio); 
		}
	}
	
	/**
	 * Sets the number of map units in each row and each column to the argument values.
	 * 
	 * @param mapUnitsInRow		the number of map units per row
	 * @param mapUnitsInColumn	the number of map units per column
	 */
	public void setSOMSize(int mapUnitsInRow, int mapUnitsInColumn) {
		this.intMURows = mapUnitsInRow;
		this.intMUCols = mapUnitsInColumn;
	}
	
	/**
	 * Sets the labels, i.e. the descriptions for the data items, of the SOM.
	 * The argument <code>labels</code> must contain as many String-instances as
	 * there are data items in the training set.  
	 * 
	 * @param labels	a Vector containing one String for every data item in the traing set
	 * @throws SizeMismatchException
	 */
	@SuppressWarnings("unchecked")
	public void setLabels(Vector<String> labels) throws SizeMismatchException {
		if (labels.size() == data.getNumberOfRows()) {
			this.labels = new Vector<String>();
			// assign a clone of the selected meta-data Vector to the internal labels-Vector
			// assigning the real instance instead of a clone would lead to problems when labels are cleared 
			this.labels = (Vector<String>) labels.clone();
		} else
			throw (new SizeMismatchException());
	}
	
	/**
	 * Clears all labels assigned to the SOM. 
	 */
	public void clearLabels() {
		if (this.labels != null)
			this.labels.clear();
	}
	
	public String getAltLabel(int dataItemIndex) {
		if (altLabels != null && !altLabels.isEmpty() && (altLabels.elementAt(dataItemIndex) != null)) 
			return altLabels.elementAt(dataItemIndex);
		return Integer.toString(dataItemIndex); 
	}
	
	public Vector<String> getAltLabels() {
		return altLabels;
	}
	
	@SuppressWarnings("unchecked")
	public void setAltLabels(Vector<String> labels) throws SizeMismatchException {
		if (labels.size() == data.getNumberOfRows()) {
			this.altLabels = new Vector<String>();
			// assign a clone of the selected meta-data Vector to the internal labels-Vector
			// assigning the real instance instead of a clone would lead to problems when labels are cleared 
			this.altLabels = (Vector<String>) labels.clone();
		} else
			throw (new SizeMismatchException());
	}
	
	public void clearAltLabels() {
		if (this.altLabels != null)
			this.altLabels.clear();
	}
	
	/**
	 * Returns the label for the data item whose index is <code>dataItemIndex</code>.
	 * If not labels were specified, the <code>dataItemIndex</code> is returned as String.
	 * 
	 * @param dataItemIndex		the index of the data item for which the label is requested
	 * @return					a String containing the description (label) of the data item
	 */
	public String getLabel(int dataItemIndex) {
		if (labels != null && !labels.isEmpty() && (labels.elementAt(dataItemIndex) != null)) 
			return labels.elementAt(dataItemIndex);
		return Integer.toString(dataItemIndex); 
	}
	
	/**
	 * Returns the data set of the SOM (that is used for training). 
	 * 
	 * @return a DataMatrix representing the data set
	 */
	public DataMatrix getDataset() {
		return this.data;
	}
	
	public void init(int initMethod) {
		switch (initMethod) {
			case SOM.INIT_RANDOM:
				this.randomInit();
				break;
			case SOM.INIT_LINEAR:
				this.linearInit();
				break;
			case SOM.INIT_GRADIENT:
				this.gradientInit();
				break;
			case SOM.INIT_SLC:
				this.slcInit();
				break;
			default:
				this.linearInit();
				break;
			}
	}
	
	/**
	 * Initializes the SOM based on random values.<br>
	 * For this reason, the minimum and maximum value in the training data is determined and the codebook is
	 * filled with values in the range [<code>min</code>, <code>max</code>).
	 */
	public void randomInit() {
		// analyze data to get the range for initializing
		// determine minimum and maximum to estimate the
		// range of the values in the data matrix
		double min = Double.MAX_VALUE;
		double max = min*-1;
		double value;
		for(int i=0; i<data.getNumberOfRows(); i++) {
			for (int j=0; j<data.getNumberOfColumns(); j++) {
				value = data.getValueAtPos(i,j).doubleValue();
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
		}
		// create a new Random-instance for random values
		Random rand = new Random();
		// fill the codebook with random double values
		for (int i=0; i<intMURows*intMUCols; i++) {							// for every map unit in the codebook
			// create as many random values 
			for (int j=0; j<data.getNumberOfColumns(); j++) {				// for every dimension of data (training) vector
				// get random value in the range [0,1)
				double randValue = rand.nextDouble();
				// add random value in the range [min, max) to the model vector number i
				codebook.addValue(new Double(randValue * (max-min) + min));
			}
			// if not last model vector, start new row
			if (i < (intMURows*intMUCols-1))
				codebook.startNewRow();
		}
	}
	
	/**
	 * Initializes the SOM based on a gradient from min to max.<br>
	 * For every feature of the inputvectors the min and max is determined,
	 * and with these values 2 vectors are created:<br>
	 * <code>minVec</code> which contains all min-values and is written to the upper left;<br>
	 * <code>maxVec</code> which contains all max-values and is written to the lower right;<br>
	 * All the other vectors are then generated by interpolation.
	 */
	/* created by MSt */
	public void gradientInit() {
		// analyze data to get the min and max values for every feature 
		Vector<Double> minVec = new Vector<Double>();
		Vector<Double> maxVec = new Vector<Double>();
		double min = Double.MAX_VALUE;
		double max = min*-1;
		double value, range, newval;
		double minD, maxD;	// distance of the current codebook-field to the first (min)
							// and the last (max) element (pythagoras!)
		
		for (int i=0; i<data.getNumberOfColumns(); i++) {		// for every dimension of data (training) vector
			for (int j=0; j<data.getNumberOfRows(); j++) {		// for every input vector
				value = data.getValueAtPos(j,i).doubleValue();
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
			minVec.add(new Double(min));
			maxVec.add(new Double(max));
			min = Double.MAX_VALUE;
			max = min*-1;
			
		}
//		System.out.println("length of minVec: "+minVec.size()+" length of maxVec: "+maxVec.size());
		
		// create as many rows as there are elements in the logical array of vectors.
		codebook.startNewRow(intMUCols*intMURows-1); 
//		System.out.println("codebook holds "+codebook.getNumberOfRows()+" (emtpy) featurevectors.");
		// add first vector (minVec)
		for(int i=0; i<minVec.size(); i++)
			codebook.addValue(minVec.elementAt(i), 0);
//		System.out.println("the first vector of the codebook is of length "+((Vector)codebook.getRow(0)).size());
		// add the last vector (maxVec)
		for(int i=0; i<minVec.size(); i++)
			codebook.addValue(maxVec.elementAt(i), intMURows*intMUCols-1);		

		for(int i=0; i<data.getNumberOfColumns(); i++){ // for length of inputvectors
			max = maxVec.elementAt(i).doubleValue();
			min = minVec.elementAt(i).doubleValue();			
			range = max - min;
//			System.out.println("i: "+i+" min: "+min+" max: "+max+"range: "+range);
			for(int j=0; j<intMURows; j++){
				for(int k=0; k<intMUCols; k++){	
					//the vectors min and max are already in the codebook
					if((j==0 && k==0) || (j==intMURows-1 && k==intMUCols-1))
						continue;

					// calculate the distance with pythagoras
					minD = Math.sqrt(j*j + k*k);
					maxD = Math.sqrt(Math.pow(intMURows-j-1, 2) + Math.pow(intMUCols-k-1, 2));
					newval = min + range/2;
					double tmp = minD-maxD;
					// if 0 < tmp < minD -> the distance to max is smaller than the distance to min
					// if tmp == 0 -> min and max are equally far away
					// if -maxD < tmp < 0 -> the distance to min is smaller than the distance to max
					
					if(tmp < 0){ // current field is nearer to max than to min
						newval += (range/2 * tmp / maxD);
					} else if(tmp != 0.0d){  // current field is nearer to min than to max
						newval += (range/2 * tmp / minD);
					}
					codebook.addValue(new Double(newval), j+k*intMURows);
//					System.out.println("added val "+newval+" to Vec "+(j+k*intMURows));
				}
			}
		}
		
		// debug code (show all values of feature 0)
//		showCurrentFeatureState(0);
	}

	/**
	 * Initializes the SOM with the linear initialization algorithm as proposed by T. Kohonen.<br>
	 * First of all the (squarish) autocorrelation matrix of the input data is calculated 
	 */
	/* created by MSt */

	@SuppressWarnings("unchecked")
	public void linearInit() {
		// autocorrelation matrix is a square matrix, the size is defined by 
		// the number of dimensions of the feature vectors.
		Vector<double[][]> eigen = new Vector<double[][]>(); // used for creating the 2 biggest eigenvectors in eigenbig
		Matrix autocorr_jama, data_jama, eigenvectors, eigenvalues, eigenbig; // Jama matrices
		EigenvalueDecomposition decomp;

		long currentMillis = System.currentTimeMillis();
		System.out.println("linearInit. start: "+(System.currentTimeMillis()-currentMillis));

		// initialize the codebook with zeros
		codebook = new DataMatrix(intMURows*intMUCols, data.getNumberOfColumns(), new Double(0.0d));
		System.out.println("linearInit. codebook inited with zeros: "+(System.currentTimeMillis()-currentMillis));

		// convert inputvalues data to a Jama-Matrix
		data_jama = new Matrix(data.getNumberOfRows(), data.getNumberOfColumns());
		for(int i=0; i<data.getNumberOfRows(); i++){
			for(int j=0; j<data.getNumberOfColumns(); j++){
				data_jama.set(i, j, data.getValueAtPos(i, j));
			}
		}
		System.out.println("linearInit. codebook created, data convered to data_jama: "+(System.currentTimeMillis()-currentMillis));
	
		// calculate the mean for every col (=feature) of the input data for two reasons:
		// 1) to normalise the input data (needed for the autocorrelation matrix)
		// 2) to initialize the codebook-vectors with
		Vector<Double> codeBookInitRow = new Vector<Double>();
		double mean;
		for(int i=0; i<data.getNumberOfColumns(); i++){ // for every dimension
			// calculate the mean for every feature of the input data
			mean = 0.0d;
			for(int j=0; j<data.getNumberOfRows(); j++){ //for every inputvector
				mean += data.getValueAtPos(j, i).doubleValue();
			}
			mean /= data.getNumberOfRows();
			// add to the mean-feature for latter use in ad 2)
			codeBookInitRow.add(mean);
			
			// ad 1) subtract the calculated means from every data vector
			for(int j=0; j<data_jama.getRowDimension(); j++) //for every inputvector
				data_jama.set(j, i, data_jama.get(j, i)-mean);
		}
		// ad 2) now set the feature vector for every SOM-unit to the mean-feature
		for(int i=0; i<intMURows*intMUCols; i++){
			try {
				codebook.setRowValues((Vector<Double>) codeBookInitRow.clone(), i);
			} catch (SizeMismatchException e) {
				e.printStackTrace();
			}
		}
		System.out.println("linearInit. means calculated, codebook inited with them: "+(System.currentTimeMillis()-currentMillis));
		
		
		// calculate the scalar multiplication for every combination of the 
		// dimensions (features) of the normalised input data.
		// dimensions: (0*0; 0*1; ... 0*N-1; 0*N; 1*0; ... N*N;)
		// for the resulting vector c the sum of its scalars divided by its length
		// is calculated and stored in the autocorrelation matrix.
		autocorr_jama = new Matrix(data_jama.getColumnDimension(), data_jama.getColumnDimension());
		for(int i=0; i<data_jama.getColumnDimension(); i++){
			for(int j=i; j<data_jama.getColumnDimension(); j++){
				// dot product of a and b divided by the length of a (b)
				double sum = 0.0d;
				for(int k=0; k<data_jama.getRowDimension(); k++){
					sum += data_jama.get(k,i) * data_jama.get(k,j);
				}
				sum /= data_jama.getRowDimension();

				autocorr_jama.set(i, j, sum);
				autocorr_jama.set(j, i, sum);
			}
		}
		System.out.println("linearInit. autocorr ready: "+(System.currentTimeMillis()-currentMillis));
		// now the autocorrelation matrix is complete.
		
		// get the Eigenvectors and Eigenvalues
		decomp = new EigenvalueDecomposition(autocorr_jama);
		eigenvectors = decomp.getV();
		eigenvalues = decomp.getD();  //block diagional matrix with eigenvalues
		System.out.println("linearInit. eigen* calculated: "+(System.currentTimeMillis()-currentMillis));
		
//		System.out.println("the eigenvector-matrix:");
//		eigenvectors.print(7, 3);
//		System.out.println("the eigenvalues Matrix:");
//		eigenvalues.print(7, 3);
		
		// get position of the 2 eigenvectors with the greatest eigenvalue (2 because our SOM is 2-dimensional)
		int[] greatest = {0, 0};
		for(int i=0; i<eigenvalues.getRowDimension(); i++){
			double max=Double.NEGATIVE_INFINITY, lo_max =Double.NEGATIVE_INFINITY; 
			if(eigenvalues.get(i, i) > max){
				lo_max = max;
				greatest[1] = greatest[0];
				max = eigenvalues.get(i, i);
				greatest[0] = i;
			} else if(eigenvalues.get(i, i) > lo_max){
				lo_max = eigenvalues.get(i, i);
				greatest[1] = i;
			}
		}
		System.out.println("linearInit. found 2 greatest eigenvectors: "+(System.currentTimeMillis()-currentMillis));
		
//		System.out.println("greatest eigenvector: "+greatest[0]+" 2nd greatest: "+greatest[1]);
		
		// the eigenvectors are represented by the columns
		// now store get the two biggest eigenvectors as double[][] in Vector eigen.
		// (this is done because we need their singular value decomposition.. and
		// Jama expects a double[][] as input.)
		for(int i=0; i<2; i++){
			eigen.add(new double[eigenvalues.getRowDimension()][1]);
//			System.out.println("\n\neigenvector nr: "+i);
			for(int j=0; j<eigenvalues.getRowDimension(); j++){
				eigen.elementAt(i)[j][0] = (eigenvectors.get(j, greatest[i]));
//				System.out.println(eigenvectors.get(j, greatest[i]));
			}
		}
		
		System.out.println("linearInit. data conversion (to double[][]): "+(System.currentTimeMillis()-currentMillis));
		

		// here for every one of the two eigenvectors the following things are done:
		// 1) calculation of the singular value decomposition
		// 2) scalar multiplication of the eigenvector with the biggest element of 
		//    the SVD divided by the sqrt of the eigenvalue:
		//    eigenvector = (eigenvector * max(SVD)) / sqrt(eigenvalue)
		for(int i=0; i<2; i++){
			double max=Double.NEGATIVE_INFINITY; // max(SVD)

			// calculate the singular value decomposition for an eigenvector
			eigenbig = new Matrix(eigen.elementAt(i));
			double[] svd_eigenbig = (new Jama.SingularValueDecomposition(eigenbig)).getSingularValues();
			
			for(int j=0; j<svd_eigenbig.length; j++){
				double get = svd_eigenbig[j];
				if(get > max)
					max = get;
			}
//			System.out.println("\n\normalised neigenvector nr: "+i);
			for(int j=0; j<eigenbig.getRowDimension(); j++){
				double get = eigenbig.get(j,0);
				get /= max;
				get *= Math.sqrt(eigenvalues.get(greatest[i], greatest[i]));
				eigen.elementAt(i)[j][0] = get;
//				System.out.println(((double[][])eigen.elementAt(i))[j][0]);
			}
		}
		System.out.println("linearInit. singular value decomp ready: "+(System.currentTimeMillis()-currentMillis));
		
		// create coords. these values are later used to weight the
		// influence of the eigenvectors on the codebookvectors
		double[][] coords = new double[intMUCols*intMURows][2];
		for(int i=0; i<intMUCols*intMURows; i++){
			for(int j=0; j<2; j++){
				if(j==0)
					coords[i][j] = i%intMURows;
				else
					coords[i][j] = i/intMURows;
			}
		}
		
		//normalize the coords
		for(int i=0; i<2; i++){
			double max = Double.NEGATIVE_INFINITY;
			double min = Double.MAX_VALUE;
			double tmp;
			// find max and min values
			for(int j=0; j<intMUCols*intMURows; j++){
				tmp = coords[j][i];
				if(tmp > max)
					max = tmp;
				if(tmp < min)
					min = tmp;
			}
			// normalize the values
			if(max > min){
				for(int j=0; j<intMUCols*intMURows; j++){
					tmp = coords[j][i];
					// normalize to values between 0 and 1
					tmp = (tmp-min)/(max-min);
					// normalize to values between -1 and 1
					tmp = (tmp-0.5)*2;
					coords[j][i] = tmp;
				}
			}			
		}
		
//		System.out.println("linearInit. coords created: "+(System.currentTimeMillis()-currentMillis));
		
		
		// now calculate the final codebook values (multiplication of the codebook
		// values with the two weighted (through coords) eigenvectors:
		// for n = 1:munits,   
		//  for d = 1:mdim,
		//    sMap.codebook(n,:) = sMap.codebook(n,:)+Coords(n,d)*V(:, d)';
		//  end
		// end	
		for(int i=0; i<intMURows*intMUCols; i++){
			for(int j=0; j<2; j++){
				Vector<Double> row = codebook.getRow(i);
				for(int k=0; k<row.size(); k++){
					double val = row.elementAt(k);
					val += coords[i][j] * (eigen.elementAt(j))[k][0];
					row.setElementAt(val, k);
				}
				try{
					codebook.setRowValues(row, i);
				}catch(SizeMismatchException sme){sme.printStackTrace();};
			}
		}
		System.out.println("linearInit. finish: "+(System.currentTimeMillis()-currentMillis));
		
		System.out.println("linear initialization finished.");
//		codebook.printMatrix();
	}	
	
	/**
	 * The SOM is initiated by giving specific values to the corner units of the map. 
	 * The initiation values of the other map units are interpolated.
	 * This initialisation method can be used to give a map a specific orientation. (e.g. used for SubSOM-Orientation in HSOMs)
	 * In a one-dimensional case, only one vector of an "end" is used
	 */
	protected void initWithCorners(Vector<Double> upperLeft, Vector<Double> upperRight, Vector<Double> lowerLeft, Vector<Double> lowerRight) {
		codebook.startNewRow(intMUCols * intMURows - 1);
		for(int i = 0; i < intMUCols * intMURows; i++) 
			for(int j = 0; j < upperLeft.size(); j++)
				codebook.addValue(0d, i);		
		try {
			codebook.setRowValues(upperLeft, 0);
			codebook.setRowValues(upperRight, intMUCols - 1);
			codebook.setRowValues(lowerLeft, (intMURows - 1) * intMUCols);
			codebook.setRowValues(lowerRight, intMURows * intMUCols - 1);

			double upperLeftDist = 0d;
			double upperRightDist = 0d;
			double lowerLeftDist = 0d;
			double lowerRightDist = 0d;
			double sumReciprocalDist = 0d;
			double reciprocalUpperLeftDist = 0d;
			double reciprocalUpperRightDist = 0d;
			double reciprocalLowerLeftDist = 0d;
			double reciprocalLowerRightDist = 0d;
			double value = 0d;
			//interpolation
			for(int i = 0; i < intMURows; i++) {
				for(int j = 0; j < intMUCols; j++) {
					if((i == 0 || i == intMURows -1) && (j == 0 || j == intMUCols - 1))
						continue;
					
					upperLeftDist = Math.sqrt(i*i + j*j);
					upperRightDist = Math.sqrt(i*i + Math.pow(intMUCols-j-1, 2));
					lowerLeftDist = Math.sqrt(Math.pow(intMURows-i-1, 2) + j*j);
					lowerRightDist = Math.sqrt(Math.pow(intMURows-i-1, 2) + Math.pow(intMUCols-j-1, 2));
					
					//System.out.println("number " + (i * intMUCols + j) + ": ");
				
					//determines if the distance influences the value linearly or with a greater power ;)
					//the greater the power the greater the influence of the nearest corner
					int power = 4;
					
					reciprocalUpperLeftDist = 1/Math.pow(upperLeftDist, power);
					reciprocalUpperRightDist = 1/Math.pow(upperRightDist, power);
					reciprocalLowerLeftDist = 1/Math.pow(lowerLeftDist, power);
					reciprocalLowerRightDist = 1/Math.pow(lowerRightDist, power);
					sumReciprocalDist = reciprocalUpperLeftDist + reciprocalUpperRightDist 
										+ reciprocalLowerLeftDist + reciprocalLowerRightDist;
					/*
					System.out.println(upperLeftDist + "\t" + reciprocalUpperLeftDist / sumReciprocalDist);
					System.out.println(upperRightDist + "\t" + reciprocalUpperRightDist / sumReciprocalDist);
					System.out.println(lowerLeftDist + "\t" + reciprocalLowerLeftDist / sumReciprocalDist);
					System.out.println(lowerRightDist + "\t" + reciprocalLowerRightDist / sumReciprocalDist);*/
					
					for(int k = 0; k < data.getNumberOfColumns(); k++) {
						value = reciprocalUpperLeftDist / sumReciprocalDist * upperLeft.elementAt(k)  
								+ reciprocalUpperRightDist / sumReciprocalDist * upperRight.elementAt(k)
								+ reciprocalLowerLeftDist / sumReciprocalDist * lowerLeft.elementAt(k)
								+ reciprocalLowerRightDist / sumReciprocalDist * lowerRight.elementAt(k);
						codebook.setValueAtPos(value, i * intMUCols + j, k);
					}
				}
				
			}
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}	
		//showCurrentFeatureState();	
	}
	
	/**
	 * Initializes the SOM with the algorithm proposed by Su, Liu and Chang in
	 * "Improving the Self-Organizing Feature Map Algorithm Using an Efficient Initialization Scheme"
	 *
	 * created by MSt */
	public void slcInit() {	
		double[][] dist = new double[data.getNumberOfRows()][data.getNumberOfRows()];
		// contains the indexes for the first two corner-vectors
		double[] max = new double[3]; // [0]: euclidian value, [1]:i, [2]:j;
		// contains the index for the third corner-vector
		double[] max3 = new double[2]; // [0]: distance to max[1]+ d.t.max[2], [1]: index in dist;
		// contains the index for the fourth corner-vector
		double[] max4 = new double[2]; // [0]: d.t. max[1] + d.t.max[2] + d.t.max3[1], [1]: index in dist;

		max[0] = Double.NEGATIVE_INFINITY;
		max3[0] = Double.NEGATIVE_INFINITY;
		max4[0] = Double.NEGATIVE_INFINITY;

		// initialize the codebook with zeros
		codebook = new DataMatrix(intMURows*intMUCols, data.getNumberOfColumns(), new Double(0.0d));
		
		// calculate the euclidian distance for all data-vectors and simultaneously
		// get the two vectors with the greatest distance
		for (int i=0; i<data.getNumberOfRows(); i++) {		// for every dimension of data (training) vector
			for (int j=i; j<data.getNumberOfRows(); j++) {
				try {
					dist[i][j] = euclideanDistance(data.getRow(i), data.getRow(j));
				} catch (SizeMismatchException e) {
					e.printStackTrace();
				}
				if(max[0] < dist[i][j]){
					max[0] = dist[i][j];
					max[1] = i;
					max[2] = j;
				}
			}
		}
		
		// put the two greatest vectors to the lower left / upper right
		try {
			codebook.setRowValues(data.getRow((int)max[1]), (intMURows-1)*intMUCols);	// lower left
			codebook.setRowValues(data.getRow((int)max[2]), intMUCols-1);				// upper right
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}
		
		// get and set the vector with the greatest distance to the latter ones into the upper right
		for (int i=0; i<data.getNumberOfRows(); i++) {		// for every dimension of data (training) vector
			double d = 0.0d;
			d += (dist[i][(int)max[1]] + dist[i][(int)max[2]]);
			if(max3[0] < d){
				max3[0] = d;
				max3[1] = i;
			}
		}
		try {
			codebook.setRowValues(data.getRow((int)max3[1]), 0); // upper left
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}
		
		// get and set the vector with the greatest distance to the latter 3 ones into the upper left
		for (int i=0; i<data.getNumberOfRows(); i++) {		// for every dimension of data (training) vector
			double d = 0.0d;
			d += (dist[i][(int)max[1]] + dist[i][(int)max[2]] + dist[i][(int)max3[1]]);
			if(max4[0] < d){
				max4[0] = d;
				max4[1] = i;
			}
		}
		try {
			codebook.setRowValues(data.getRow((int)max4[1]), intMURows*intMUCols-1); // lower right
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}
		
		// now initialize all the other vectors (see formula 8 in the paper)
		for(int i=1; i<=intMUCols; i++){
			for(int j=1; j<=intMURows; j++){
				double s = 0.0; // scalar
				Vector<Double> lo_ri = codebook.getRow(intMURows*intMUCols -1);
				s = (double)((j-1)*(i-1)) / (double)((intMUCols-1)*(intMURows-1));
				lo_ri = doubleVecMult(lo_ri, s);
				
				Vector<Double> up_ri = codebook.getRow(intMUCols-1);
				s = (double)((j-1)*(intMURows-i)) / (double)((intMUCols-1)*(intMURows-1));
				up_ri = doubleVecMult(up_ri, s); //now up_ri is no longer a pointer to a codebook-row but an altered copy
				
				Vector<Double> lo_le = codebook.getRow((intMURows-1)*intMUCols);
				s = (double)((intMUCols-j)*(i-1)) / (double)((intMUCols-1)*(intMURows-1));
				lo_le = doubleVecMult(lo_le, s);
				
				Vector<Double> up_le = codebook.getRow(0);
				s = (double)((intMUCols-j)*(intMURows-i)) / (double)((intMUCols-1)*(intMURows-1));
				up_le = doubleVecMult(up_le, s);

				Vector<Double> result = new Vector<Double>();
				for(int k=0; k<lo_ri.size(); k++){
					double x = 	(Double)lo_ri.elementAt(k) +
								(Double)up_ri.elementAt(k) +
								(Double)lo_le.elementAt(k) +
								(Double)up_le.elementAt(k);
					result.add(new Double(x));
				}
				
				try {
					codebook.setRowValues(result, (j-1)*intMUCols + (i-1));
				} catch (SizeMismatchException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}
	
	private Vector<Double> doubleVecMult(Vector<Double> v, double scalar){
		Vector<Double> result = new Vector<Double>();
		for(int i=0; i<v.size(); i++){
			double x = v.elementAt(i) * scalar;
			result.add(new Double(x));
		}
		return result;
	}

	
	/** 
	 * Trains the SOM using the method given in the parameter <code>method</code>.
	 * 
	 * @param method	the training method used
	 * @param length	the training length in epochs
	 * @see comirva.mlearn.SOM#TRAIN_SEQ
	 * @see comirva.mlearn.SOM#TRAIN_BATCH
	 */
	public void train(int method, int length) {
		this.trainingLength = length;
		this.method = method;
		if (method == SOM.TRAIN_SEQ)
			this.trainSequential();
		else if (method == SOM.TRAIN_BATCH)
			this.trainBatch();
		
		if (colorByPCA)
			gridcolors = PCAProjectionToColor.getColorsForFeatures(codebook.toDoubleArray());
		else
			gridcolors = new Color[this.getNumberOfColumns()*this.getNumberOfRows()];
	}
	
	/**
	 * <p>Performs a very simple sequential training based on the equation:
	 * <b><code>mi(t+1) = mi(t) + alpha(t)*hbmu,i(t)*[x-mi(t)]</b></code>.</p>
	 * 
	 * The number of iterations equals the number of data items in the training data set.
	 * For the learning rate <code>alpha(t)</code> the formula <code>1 - current_iteration/iterations</code> is taken.
	 * The neighborhood-radius is calculated according to the formula:
	 * <b><code>hbmu,i(t) = exp(- ||rbmu, ri|| / 2*sigma(t)^2)</code></b> where the learning rate <code>alpha</code> is used for <code>sigma</code>.
	 */
	protected void trainSequential() {
		int iterations = trainingLength*data.getNumberOfRows();		// number of iterations
		double alpha = 1;											// learning rate, by default set to 1
		double alpha_decrease = 1/iterations;						// decrease of learning rate in every iteration;
		double radius = 1;											// the neighborhood radius
		Vector<Double> x;											// the current data item
		Vector<Double> m;											// the current model vector
		Random randItem = new Random();								// Random-instance for selecting data item
		// training time is one epoch, i.e. the number of
		// iterations equals the total number of data items
		for (int i=0; i<iterations; i++) {
			if (statusBar != null)							// display progress, if statusBar-instance valid
				statusBar.setText("Sequential Training, Iteration: " + (i+1) + "/" + iterations);
			// get random data item
			x = data.getRow(randItem.nextInt(data.getNumberOfRows()));
			// calculate the best matching unit (BMU) for current data item x
			int bmu = getBMU(x);
			// update every model vector in the codebook
			for (int j=0; j<codebook.getNumberOfRows(); j++) {
				// get model vector
				m = codebook.getRow(j);
				// calculate the neighborhood radius according to
				// the formula:
				//      hbmu,i(t) = exp(- ||rbmu, ri|| / 2*sigma(t)^2)
				// use learning rate alpha for sigma
				radius = Math.exp((mapunitDistance(bmu, j)*-1)/(2*alpha*alpha));
				// update the codebook
				try {
					// vectorDistanceMultiply(x, m, alpha*radius) calculates
					// the distance between current model vector m
					// and current data item x and multiplies it with alpha*radius
					codebook.addRowValues(vectorDistanceMultiply(x, m, alpha*radius), j);				
				} catch (SizeMismatchException sme) {
				}				
			}
			// decrease learning rate alpha
			alpha -= alpha_decrease;
		}
	}
	
	/**
	 * Performs batch training. Basically, the standard batch SOM algorithm is used.
	 * The update rule of it is:
	 * <b><code>mi(t+1) = sum(j=1..N, hbmu,i(t)*xi) / sum(j=1..N, hbmu,i(t)) </b></code>
	 *  
	 * The neighborhood-radius is calculated according to the formula:
	 * <b><code>hbmu,i(t) = exp(- ||rbmu, ri|| / 2*sigma(t)^2)</code></b>
	 */
	@SuppressWarnings("unused")
	protected void trainBatch() {
		int trainEpochs = trainingLength;	// iterations in epochs 
		Vector<Double> x;							// the current data item
		Vector<Double> m;							// the current model vector
		double sigma = 1;								// std. deviation of gaussian
		double sigma_decrease = 1/trainEpochs;			// decrease of sigma in every iteration;

		// calculate weighting mask that remains constant over the whole training process training
		double[][] weightingMask = new double[codebook.getNumberOfRows()][codebook.getNumberOfRows()];
		for (int i=0; i<codebook.getNumberOfRows(); i++) {
			for (int j=i; j<codebook.getNumberOfRows(); j++) {
				weightingMask[i][j] = mapunitDistance(i,j)*-1;
				weightingMask[j][i] = weightingMask[i][j];
			}
		}
		// perform training
		for (int l=0; l<trainEpochs; l++) {
			if (statusBar != null)							// display progress, if statusBar-instance valid
				statusBar.setText("Batch Training, Epoch: " + (l+1) + "/" + trainEpochs);
			// calculate Voronoi set
			this.createVoronoiSet();
			// calculate sum of all data items in Voronoi set of every map unit
			double[][] sumVoronoi = new double[this.codebook.getNumberOfRows()][this.data.getNumberOfColumns()];
			for (int i=0; i<this.voronoiSet.size(); i++) {
				Vector<Integer> currentMUVoronoi = this.voronoiSet.elementAt(i);
				Enumeration<Integer> e = currentMUVoronoi.elements();
				while (e.hasMoreElements()) {
					// determine index of data item contained in current map unit's Voronoi set
					int idxCurMUVoronoiDataItem = e.nextElement();
					// add values of data item to sumVoronoi
					for (int col=0; col<this.data.getNumberOfColumns(); col++)
						sumVoronoi[i][col]+= this.data.getValueAtPos(idxCurMUVoronoiDataItem, col).doubleValue();
				}
			}
			// update every model vector in the codebook
			for (int i=0; i<codebook.getNumberOfRows(); i++) {
				// get model vector
				m = codebook.getRow(i);
				// weighted sum of data items
				double[] weightedSumDataItems = new double[this.data.getNumberOfColumns()]; 
				double sumWeights = 0; 			// sum of weights
				for (int j=0; j<codebook.getNumberOfRows(); j++) {				// for every model vector
					// calculate the neighborhood radius according to
					// the formula:
					//      hbmu,i(t) = exp(- ||rbmu, ri|| / 2*sigma(t)^2)
					// but use the precalculated weighting mask
					double weight = Math.exp(weightingMask[i][j]/(2*sigma*sigma)); // (1/((mapunitDistance(i,j)+1)))*sigma; // Math.exp((mapunitDistance(j, i)*-1)/(2*sigma*sigma));
					// use following heuristic to get more performance:
					// if the weight of a map unit is below 5% of the maximum weight, exclude it from update
					// since the weight is in range [0,1] this is easy to determine
					if (weight > 0.00) { // 0.05
						// calculate weighted sum of Voronoi sets
						for (int k=0; k<this.data.getNumberOfColumns(); k++)
							weightedSumDataItems[k] += sumVoronoi[j][k]*weight;
						// accumulate weights
						sumWeights+=weight * this.voronoiSet.elementAt(j).size();
					}
				}
				// create update model vector
				Vector<Double> updatedMV = new Vector<Double>();
				for (int k=0; k<this.data.getNumberOfColumns(); k++)
					updatedMV.addElement(new Double(weightedSumDataItems[k]/sumWeights));
				// update the codebook
				try {
					codebook.setRowValues(updatedMV, i);				
				} catch (SizeMismatchException sme) {
				}				
			}
			// decrease time-varying parameter to determine size of neighborhood kernel
			sigma -= sigma_decrease;	
		}
	}
	
	/**
	 * Calculates the best matching unit for the data vector <code>dataItem</code> and
	 * returns its index in the codebook.
	 * 
	 * @param dataItem	the Vector containing the data item for which the BMU should be determined 
	 * @return			the index of the map unit which is the best matching unit for the <code>dataItem</code>
	 */
	public int getBMU(Vector<Double> dataItem) {
		double minDist = Double.MAX_VALUE;		// minimum distance
		int indexMinDist = -1; 					// index of model vector with minimal distance to dataItem (BMU)
		double value;							// current value for Euclidean distance
		// calculate Euclidean distances between dataItem
		// and every model vector in the codebook
		for (int i=0; i<codebook.getNumberOfRows(); i++) {
			// if new minimal distance was found, remember this distance and index of model vector
			try {
//				System.out.println(dataItem.size()+"\t"+codebook.getRow(i).size());
				value = euclideanDistance(dataItem, codebook.getRow(i));
				if (value < minDist) {
					minDist = value;
					indexMinDist = i;
				}
			} catch (SizeMismatchException sme) {
				return -1;
			}
		}
		//System.out.println("minimal distance: " + minDist);
		//System.out.println("index BMU:" + indexMinDist);
		// return index of best-matching unit
		return indexMinDist;
	}
	
//	public int[] getBMUs() {
//		// copy data items to double[][] for faster access
//		double[][] data = this.data.toDoubleArray();
//		// copy codebook to double[][] for faster access
//		double[][] codebook = this.codebook.toDoubleArray();
//		// allocate memory for BMU-array
//		int[] bmus = new int[this.data.getNumberOfRows()];
//		// for every data item
//		for (int i=0; i<this.data.getNumberOfRows(); i++) {
//			double minDist = Double.POSITIVE_INFINITY;					// minimum distance between data item i and arbitraty model vector
//			int muIdxMinDist = -1;										// index of map unit with minimum distance to current data item 
//			// for every model vector
//			for (int j=0; j<this.codebook.getNumberOfRows(); j++) {
//				// calc euclidean distance between current data item (i) and current model vector (j)
//				double dist = 0;
//				for (int k=0; k<this.data.getNumberOfColumns(); k++)
//					dist += (data[i][k] - codebook[j][k]) * (data[i][k] - codebook[j][k]);
//				dist = Math.sqrt(dist);
//				// check if distance to current model vector is smaller than minimum
//				if (dist<minDist) {
//					minDist = dist;
//					muIdxMinDist = j;
//				}
//			}
//			// insert found BMU in BMU-array
//			bmus[i] = muIdxMinDist;
//		}
//		return bmus;
//	}
	
	/**
	 * Calculates a set of best matching units for the data vector <code>dataItem</code> and
	 * returns the codebook-indices of these units.
	 * 
	 * @param dataItem	the Vector containing the data item for which the BMUs should be determined 
	 * @return			a TreeMap containing the indices of the map units which are the best matching units for the <code>dataItem</code>
	 */
	public TreeMap<Double, Integer> getOrderedBMUs(Vector<Double> dataItem) {
		// a TreeMap where the BMUs are stored sorted by their distance to the data item
		TreeMap<Double, Integer> sortedBMUs = new TreeMap<Double, Integer>();
		double value;							// current value for Euclidean distance
		// calculate Euclidean distances between dataItem
		// and every model vector in the codebook
		for (int i=0; i<codebook.getNumberOfRows(); i++) {
			// if new minimal distance was found, remember this distance and index of model vector
			try {
				// get Euclidean distance
				value = euclideanDistance(dataItem, codebook.getRow(i));
				// store distance and index of map unit in TreeMap
				sortedBMUs.put(new Double(value), new Integer(i));
			} catch (SizeMismatchException sme) {
				return null;
			}
		}
		return sortedBMUs;
	}
	
	/**
	 * Calculates for the given map unit its most "representative" data items.
	 * That means the data items with minimal distance to the map unit's model
	 * vector are calculated and returned in decreasing order.
	 * 
	 * @param idxMU			the index of the map unit
	 * @param maxNumber		the maximum number of returned data items
	 * @return				a Vector containing the labels of the data items which are most similar to the map unit's model vector (in decreasing order)
	 */
	public Vector<String> getPrototypesForMU(int idxMU, int maxNumber) {
		Vector<String> orderedPrototypeLabels = new Vector<String>();	// Vector to store the labels of the nearest data items to the map unit idxMU
		Vector<Double> distancePrototypes = new Vector<Double>();		// Vector to store the distances from model vector of map unit idxMU to its data items (Voronoi set)
		// get all data items mapped to map unit idxMU
		Vector<Integer> listMUitems = this.voronoiSet.elementAt(idxMU);
		Enumeration<Integer> e = listMUitems.elements();
		while (e.hasMoreElements()) {
			int idxDataItem = e.nextElement().intValue();
			try {
				distancePrototypes.addElement(new Double(this.euclideanDistance(this.codebook.getRow(idxMU), this.data.getRow(idxDataItem))));
				// insert label of current data item into output Vector
				if (this.labels == null)		// if labels are not given, use index instead
					orderedPrototypeLabels.addElement((new Integer(idxDataItem)).toString());
				else							// labels are given, use them
					orderedPrototypeLabels.addElement(this.labels.elementAt(idxDataItem));
			} catch (SizeMismatchException sme) {
			}
		}
		// only proceed if resulting Vector is not empty
		if (!orderedPrototypeLabels.isEmpty()) {
			// sort 
			VectorSort.sortWithMetaData(distancePrototypes, orderedPrototypeLabels);
			// bring in descending order
			Vector<String> orderedProtosTemp = new Vector<String>();
			Vector<Double> orderedDistancesTemp = new Vector<Double>();
			for (int i=orderedPrototypeLabels.size()-1; i>=0; i--) {
				orderedProtosTemp.addElement(orderedPrototypeLabels.elementAt(i));
				orderedDistancesTemp.addElement(distancePrototypes.elementAt(i));
			}
			// discard most unrepresentative items if maxNumber is greater than number of items in return Vector
			Vector<String> orderedProtos = new Vector<String>();
			Vector<Double> orderedDistances = new Vector<Double>();
			if (orderedProtosTemp.size() > maxNumber) {
				for (int i=0; i<maxNumber; i++) {
					orderedProtos.addElement(orderedProtosTemp.elementAt(i));
					orderedDistances.addElement(orderedDistancesTemp.elementAt(i));
				}
			} else {
				orderedProtos = orderedProtosTemp;
				orderedDistances = orderedDistancesTemp;
			}
//			for (int i=0; i<orderedProtos.size(); i++)
//				System.out.println((String)orderedProtos.elementAt(i) + " " + ((Double)orderedDistances.elementAt(i)).toString());
			return orderedProtos;
		} 
		// if resulting Vector is empty, return empty Vector
		return orderedPrototypeLabels;
	}
	
	/**
	 * Calculates and returns the Euclidean distance
	 * between the data vectors <code>item1</code> and <code>item2</code>.<br>
	 * <code>item1</code> and <code>item2</code> must contain the same number of Double-instances,
	 * otherwise a <code>SizeMismatchException</code> is thrown.
	 *
	 * @param item1		a Vector representing the first data vector
	 * @param item2		a Vector representing the second data vector
	 * @return			a double value which is the Euclidean distance between data vector <code>item1</code> and <code>item2</code> 
	 * @throws SizeMismatchException
	 */
	public static double euclideanDistance(Vector<Double> item1, Vector<Double> item2) throws SizeMismatchException {
		// if given Vectors differ in size, throw Exception
		if (item1.size() != item2.size())
			throw new SizeMismatchException();

		double sum = 0;					// sum takes the sum of the squared differences
		// calculate the distance
		for (int i=0;i<item1.size();i++) {
			// temporarely store values of data vectors
			Double item1Value = item1.elementAt(i);
			Double item2Value = item2.elementAt(i);
			// add the squared sum of the differences 
			sum += (item2Value.doubleValue() - item1Value.doubleValue()) * (item2Value.doubleValue() - item1Value.doubleValue());
		}
		// calc and return square root
		return Math.sqrt(sum);

	}
	
	/**
	 * Calculates and returns a Vector containing the pairwise distances
	 * between the data vectors <code>item1</code> and <code>item2</code>.
	 *
	 * @param item1		a Vector representing the first data vector
	 * @param item2		a Vector representing the second data vector
	 * @return			a Vector containing the result of the subtraction <code>item1</code> - <code>item2</code>
	 * @throws SizeMismatchException
	 */
	public Vector<Double> vectorDistance(Vector<Double> item1, Vector<Double> item2) throws SizeMismatchException {
		Vector<Double> distanceVector = new Vector<Double>();		// Vector containing the distances
		// if given Vectors differ in size, throw Exception
		if (item1.size() != item2.size())
			throw new SizeMismatchException();

		// calculate the distance
		for (int i=0;i<item1.size();i++) {
			// temporarely store values of data vectors
			Double item1Value = item1.elementAt(i);
			Double item2Value = item2.elementAt(i);
			// add a Double-instance containing the distance
			// to the return-Vector-instance
			distanceVector.addElement(new Double(item1Value.doubleValue() - item2Value.doubleValue()));
		}
		return distanceVector;
	}

	/**
	 * Calculates a Vector containing the pairwise distances
	 * between the data vectors <code>item1</code> and <code>item2</code>.
	 * The result is multiplied with <code>multi</code> before it is returned. 
	 *
	 * @param item1		a Vector representing the first data vector
	 * @param item2		a Vector representing the second data vector
	 * @param multi		the multiplier for (<code>item1</code> and <code>item2</code>)
	 * @return			a Vector containing the result of the calculation (<code>item1</code> - <code>item2</code>) * <code>multi</code>
	 * @throws SizeMismatchException
	 */
	public Vector<Double> vectorDistanceMultiply(Vector<Double> item1, Vector<Double> item2, double multi) throws SizeMismatchException {
		Vector<Double> distanceVector = new Vector<Double>();		// Vector containing the distances
		// if given Vectors differ in size, throw Exception
		if (item1.size() != item2.size())
			throw new SizeMismatchException();
		
		// calculate the distance
		for (int i=0;i<item1.size();i++) {
			// temporarely store values of data vectors
			Double item1Value = item1.elementAt(i);
			Double item2Value = item2.elementAt(i);
			// add a Double-instance containing the distance
			// to the return-Vector-instance
			distanceVector.addElement(new Double((item1Value.doubleValue() - item2Value.doubleValue()) * multi));
		}
		return distanceVector;

	}
	
	/**
	 * Calculates and returns the Euclidean distance between two map units in the output space,
	 * i.e. its distance on the SOM-grid. 
	 * 
	 * @param mu1	the codebook-index of the first map unit
	 * @param mu2	the codebook-index of the second map unit
	 * @return		a double value representing the Euclidean distance between the two map units <code>mu1</code> and <code>mu2</code> in the output space
	 */
	protected double mapunitDistance(int mu1, int mu2) {
		// get 2-dimensional position of mu1 and mu2 out of
		// the 1-dimensional position of the codebook-Vector
		int mu1_row = mu1 % intMURows;
		int mu1_col = (int)Math.floor(mu1 / intMURows);
		int mu2_row = mu2 % intMURows;
		int mu2_col = (int)Math.floor(mu2 / intMURows);
		// return Euclidean distance of the two map units on
		// the SOM-grid
		double squared_row_distance = (mu2_row - mu1_row)*(mu2_row - mu1_row);
		double squared_col_distance = (mu2_col - mu1_col)*(mu2_col - mu1_col);
		if(circular) {
			if(Math.abs(mu2_row - mu1_row) > intMURows/2) {
				double actualRowDistance = intMURows - Math.abs(mu2_row - mu1_row);
				squared_row_distance = actualRowDistance * actualRowDistance;
			}
			if(Math.abs(mu2_col - mu1_col) > intMUCols/2) {
				double actualColDistance = intMUCols - Math.abs(mu2_col - mu1_col);
				squared_col_distance = actualColDistance * actualColDistance;
			}
		}
		return Math.sqrt(squared_row_distance + squared_col_distance);
	}

	/**
	 * Calculates the Voronoi-Set of the SOM and stored the result in the internal <code>voronoiSet</code> Vector
	 * which contains a nested Vector containig Integers of mapped data item indices for each map unit. 
	 */
	public void createVoronoiSet() {
		// create Vector-instance
		this.voronoiSet = new Vector<Vector<Integer>>();
		// initiaize Voronoi-Set by adding a new Vector for every map unit
		for (int i=0; i<intMURows*intMUCols; i++) {
			this.voronoiSet.addElement(new Vector<Integer>());
		}
		// insert the Voronoi-data
		for (int i=0; i<data.getNumberOfRows(); i++) {
			Vector<Integer> currentElement = this.voronoiSet.elementAt(getBMU(data.getRow(i)));
			currentElement.addElement(new Integer(i));
		}
	}
	
	/**
	 * Calculates a boolean matrix containing, for each map unit, the data items which are mapped to the unit. 
	 * 
	 * @return 	a boolean[][] two-dimensional array that contains the codebox-indices in its first dimension and the data items in its second<br>
	 * 			If there is a mapping between the map unit and the data item, 
	 * 			the value in the boolean-matrix is <code>true</code>, otherwise <code>false</code>.
	 */
	public boolean[][] getVoronoiMatrix() {
		// create matrix with matches between BMUs and data items
		boolean[][] voronoiMatrix = new boolean[intMURows*intMUCols][data.getNumberOfRows()];
		for (int i=0; i<intMURows*intMUCols; i++) {
			for (int j=0; j<data.getNumberOfRows(); j++) {
				voronoiMatrix[i][j] = false;
			}
		}
		// fill matrix
		for (int i=0; i<data.getNumberOfRows(); i++) {
			voronoiMatrix[getBMU(data.getRow(i))][i] = true;
			//System.out.println("BMU for data item " + i + " is map unit " + getBMU(data.getRow(i)));
		}
		return voronoiMatrix;
	}
	
	/**
	 * Prints the Voronoi-set of all map units to <code>java.lang.System.out</code>.
	 * This set contains all data items that are mapped to a specific map unit. 
	 */
	public void printVoronoiSet() {
		// create matrix with matches between BMUs and data items
		boolean[][] voronoiMatrix = getVoronoiMatrix();
		// print Voronoi-set
		for (int i=0; i<intMURows*intMUCols; i++) {
			System.out.print("Voronoi-Set for Map Unit " + i + ": ");
			for (int j=0; j<data.getNumberOfRows(); j++) {
				if (voronoiMatrix[i][j] == true)
					System.out.print(j + " ");
			}
			System.out.println("");
		}
	}
	
	/**
	 * This method does the same as showCurrentFeatureState(int feature),
	 *  but it prints all available features.
	 *
	 * created by MSt */
	public void showCurrentFeatureState() {
		for(int i=0; i<data.getNumberOfRows(); i++){
			showCurrentFeatureState(i);
		}
	}
	
	/** 
	 * This method is useful for debugging SOM-initialisation-algorithms.
	 * It outputs the arrangement of the feature nr <code>feature</code> from the codebook.
	 * The visualised array shows the values of exactly this feature.
	 * 
	 * created by MSt */
	public void showCurrentFeatureState(int feature) {
		if(feature < 0 || feature > data.getNumberOfRows()-1){
			System.err.println("sorry. there is no feature nr "+feature+".");
			return;
		}
		System.out.println("arrangement of feature nr "+feature+":");
	    for(int i=0; i<intMURows; i++){
	    	for(int j=0; j<intMUCols; j++){
	    		Vector<Double> vals = codebook.getRow(i+j*intMURows);
	    		double val = vals.elementAt(feature).doubleValue();
	    		int cut = (int)(val * 1000d);
	    		val = cut/1000d;
	    		System.out.print("nr"+(i+j*intMURows)+": "+val+" | ");
	    	}
	    	System.out.println();
	    }
	}
	public Vector<String> getLabels() {
		return labels;
	}
	public DataMatrix getCodebook() {
		return codebook;
	}
	public MDM getMDM() {
		return mdm;
	}
	public void setMDM(MDM mdm) {
		this.mdm = mdm;
	}
	public boolean isColorByPCA() {
		return colorByPCA;
	}
	public Color[] getGridcolors() {
		return gridcolors;
	}
	public void setTrainingLength(int trainingLength) {
		this.trainingLength = trainingLength;
	}
	public void setCircular(boolean circular) {
		this.circular = circular;
	}
	public DataMatrix getCoOccMatrix() {
		return coOccMatrix;
	}
	public void setCoOccMatrix(DataMatrix coOccMatrix) {
		this.coOccMatrix = coOccMatrix;
	}
	public Vector<String> getCoOccMatrixLabels() {
		return coOccMatrixLabels;
	}
	public void setCoOccMatrixLabels(Vector<String> coOccMatrixLabels) {
		this.coOccMatrixLabels = coOccMatrixLabels;
	}
	
}
