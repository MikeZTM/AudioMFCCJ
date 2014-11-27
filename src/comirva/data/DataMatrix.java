/*
 * Created on 21.10.2004
 */
package comirva.data;

import comirva.exception.*;
import comirva.config.DataMatrixNormalizeConfig;

import java.util.Vector;
import java.io.Serializable;

import Jama.*;

/**
 * This class implements a data matrix containing double values. 
 * The data structure is created using a Vector of Vectors.
 *
 * @author Markus Schedl
 */
public class DataMatrix implements Serializable, Cloneable {

	private static final long serialVersionUID = -3543115862944212076L;

	// name of the DataMatrix-instance
	private String name;
	// Vector containing the rows of the matrix
	private Vector<Vector<Double>> vecRows;
	// Vector for the current column
	private Vector<Double> vecColCurrent;
	
	/**
	 * Creates an empty DataMatrix-instance and initializes rows- and columns-Vectors.
	 */
	public DataMatrix() {
		// set no-name
		name = "";
		// initialize Vectors
		vecRows = new Vector<Vector<Double>>();
		vecColCurrent = new Vector<Double>();
		vecRows.addElement(vecColCurrent);		
	}
	/**
	 * Creates an empty DataMatrix-instance, initializes rows- and columns-Vectors,
	 * and sets the name of the DataMatrix to the <code>name</code> argument.
	 *
	 * @param name	a String representing the name of the DataMatrix	
	 */
	public DataMatrix(String name) {
		this.name = name;
		// initialize Vectors
		vecRows = new Vector<Vector<Double>>();
		vecColCurrent = new Vector<Double>();
		vecRows.addElement(vecColCurrent);
	}
	
	/**
	 * Converts a double[][] into a DataMatrix. 
	 * 
	 * @param data	the input as double[][]	
	 * @return		a DataMatrix holding the data
	 */
	public static DataMatrix doubleArrayToDataMatrix(double[][] data) {
		DataMatrix dm = new DataMatrix();
		for (int i=0; i<data.length; i++) {
			for (int j=0; j<data[0].length; j++) {
				dm.addValue(new Double(data[i][j]));
			}
			dm.startNewRow();
		}
		dm.removeLastAddedElement();
		return dm;
	}
	
	/**
	 * Converts a Jama Matrix into a DataMatrix. 
	 * 
	 * @param data	a Matrix holding the input data
	 * @return		a DataMatrix holding the data
	 */
	public static DataMatrix jamaMatrixToDataMatrix(Matrix m) {
		return DataMatrix.doubleArrayToDataMatrix(m.getArray());
	}
	
	/**
	 * Converts a DataMatrix to a Jama Matrix.
	 * 
	 * @param dm		a DataMatrix holding the input data
	 * @return a 		Matrix holding the data
	 */
	public static Matrix dataMatrixToJamaMatrix(DataMatrix dm) {
		return new Matrix(dm.toDoubleArray());
	}
	
	
	/**
	 * Creates an empty DataMatrix-instance and with <code>rows</code> rows and
	 * <code>cols</code> cols. So after the creation of the DataMatrix you can
	 * easily fill the matrix with several setValueAtPos()s.
	 *
	 * created by MSt */
	public DataMatrix(int rows, int cols, Double initValue) {
		// set no-name
		name = "";
		// initialize Vectors and Objects until the Matrix is as big as specified by rows/cols
		vecRows = new Vector<Vector<Double>>();
		
		Vector<Double> feature = null;
		for(int i=0; i<rows; i++) {
			feature = new Vector<Double>();
			for(int j=0; j<cols; j++){
				feature.add(initValue);
			}		
			vecRows.add(feature);
		}
		vecColCurrent = vecRows.elementAt(0);
	}
	
	/**
	 * Inserts the Double-instance <code>value</code> into the DataMatrix-instance at the current row.
	 *
	 * @param value			the Double-instance which should be inserted into the matrix
	 */
	public void addValue(Double value) {
		addValue(value, false);
	}
	/**
	 * Inserts the Double-instance <code>value</code> into the DataMatrix-instance.<br>
	 * If <code>boolNewRow</code> is <code>true</code>, a new row is created and <code>value</code> is inserted
	 * as first element into the new row. If <code>boolNewRow</code> is <code>false</code>, <code>value</code>
	 * is inserted into the current row.
	 *
	 * @param value		the Double-instance which should be inserted into the matrix
	 * @param boolNewRow	a boolean indicating if <code>value</code> should be inserted into a new row or into the current row
	 */
	public void addValue(Double value, boolean boolNewRow) {
		if (!boolNewRow) {		// new row does not start
			// simply add value to current row Vector
			vecColCurrent.addElement(value);
		} else {				// new row starts
			// add value to current row Vector
			vecColCurrent.addElement(value);
			startNewRow();
		}
	}
	/**
	 * Inserts the Double-instance <code>value</code> into the DataMatrix-instance 
	 * at row <code>row</row>. It is inserted in the Row-Vector.<br>
	 *
	 * @param row			the row into which the value is inserted
	 *
	 * created by MSt */
	public void addValue(Double value, int row) {
		Vector<Double> currRow = vecRows.elementAt(row);
		currRow.addElement(value);
		vecRows.setElementAt(currRow, row);
	}

	/**
	 * Starts a new row in the DataMatrix.
	 */
	@SuppressWarnings("unchecked")
	public void startNewRow() {
		// add clone of current row vector to matrix Vector 		
		vecRows.addElement((Vector<Double>) vecColCurrent.clone());
		// clear current column Vector
		vecColCurrent.clear();	
	}
	
	/**
	 * Starts <code>count</code> new rows in the DataMatrix.
	 * 
	 * @param count	the number of new rows to be inserted into the DataMatrix
	 *
	 * created by MS */
	public void startNewRow(int count) {
		for(int i=0; i<count; i++)
			startNewRow();
	}
	
	/**
	 * Removes the latest added Vector in the DataMatrix.<br>
	 * It is necessary in order to delete the empty element which
	 * is added by the <code>MatrixDataFileLoaderThread</code>. 
	 */
	public void removeLastAddedElement() {
		// remove the last element
		vecRows.removeElementAt(0);
	}
	
	/**
	 * Returns a double[][] representation of the DataMatrix.
	 * 
	 * @return	a double[][] containing the values of the DataMatrix
	 */
	public double[][] toDoubleArray() {
		double[][] data = new double[this.getNumberOfRows()][this.getNumberOfColumns()];
		for (int i=0; i<this.getNumberOfRows(); i++)
			for (int j=0; j<this.getNumberOfColumns(); j++)
				data[i][j] = this.getValueAtPos(i, j).doubleValue();
		return data;
	}
	
	/**
	 * Prints the DataMatrix-instance to <code>java.lang.System.out</code>.
	 */
	public void printMatrix() {
		System.out.println("Name of DataMatrix: " + getName());
		System.out.println("Rows: " + this.getNumberOfRows());
		System.out.println("Data:");
		for (int i=0; i<this.getNumberOfRows(); i++) {
			Vector<Double> vecTemp = this.vecRows.elementAt(i);
			for (int j=0; j<this.getNumberOfColumns(); j++) {
				System.out.print(vecTemp.elementAt(j).toString() + " ");
			}
			System.out.print("\n");
		}
	}
		
	/**
	 * Returns the value of the element that can be found at column <code>col</code> 
	 * and row <code>row</code> in the DataMatrix-instance.
	 *
	 * @param row	the row of the requested value in the matrix
	 * @param col	the column of the requested value in the matrix
	 * @return		a Double containing the value of the requested element
	 */
	public Double getValueAtPos(int row, int col) {
		// get Vector representing the requested row
		Vector<Double> vecTempRow = vecRows.elementAt(row);
		// get and return data item (Double) representing the requested value
		return vecTempRow.elementAt(col);
	}
	
	/**
	 * Returns the Vector of the row given by the argument <code>row</code>.
	 *
	 * @param row	the row in the matrix that should be returned as a Vector
	 * @return		a Vector representing the requested row in the matrix
	 */
	public Vector<Double> getRow(int row) {
		if (!vecRows.isEmpty() && (vecRows != null))
			return vecRows.elementAt(row);
		return null;
	}

	/**
	 * Returns the Vector of the column given by the argument <code>col</code>.
	 *
	 * @param col	the column in the matrix that should be returned as a Vector
	 * @return		a Vector representing the requested column in the matrix
	 */
	public Vector<Double> getColumn(int col) {
		if (!vecRows.isEmpty() && (vecRows != null)) {
			// create and return a Vector containing all values of the requested column
			Vector<Double> column = new Vector<Double>();
			for (int i=0; i<this.getNumberOfRows(); i++) {
				column.addElement(this.getValueAtPos(i, col));
			}
			return column;
		}
		return null;
	}

	/**
	 * Sets the value at a specific position of the DataMatrix-instance. 
	 * 
	 * @param value		a Double representing the value
	 * @param row		the row within the DataMatrix of the value that should be set
	 * @param col		the column within the DataMatrix of the value that should be set
	 */
	public void setValueAtPos(Double value, int row, int col) throws SizeMismatchException {
		if (value != null) {
			// test, if row and col values are valid
			if ((row >= this.getNumberOfRows()) || (col >= this.getNumberOfColumns()))			// no, not valid
				throw new SizeMismatchException();	

			// get correct row
			Vector<Double> rowModify = this.getRow(row);
			rowModify.setElementAt(value, col);

		}
	}
	
	/**
	 * Sets a specific row in the DataMatrix.
	 * The Vector <code>data</code> is inserted at row <code>row</code>.
	 * The original data is discarded.
	 * 
	 * @param data		a Vector containing the data to be inserted 
	 * @param row		the row where the data is inserted
	 * @throws SizeMismatchException
	 */
	public void setRowValues(Vector<Double> data, int row) throws SizeMismatchException {
		if (data.size() == this.getNumberOfColumns())
			vecRows.setElementAt(data, row);
		else
			throw new SizeMismatchException();
	}
	
	/**
	 * Adds the values of the Vector <code>data</code>
	 * to the row-values of the matrix indicated by the argument <code>row</code>.
	 *
	 * @param data	a Vector containing the values to be added
	 * @param row	the row indicating the position where the values are added
	 * @throws SizeMismatchException
	 */
	public void addRowValues(Vector<Double> data, int row) throws SizeMismatchException {
		if (data.size() == this.getNumberOfColumns()) {
			Vector<Double> tempVec = vecRows.elementAt(row);
			// calculate the new values
			for (int i=0; i<data.size(); i++) {				
				Double currentValue = tempVec.elementAt(i);
				Double addValue = data.elementAt(i);
				tempVec.setElementAt(new Double(currentValue.doubleValue() + addValue.doubleValue()), i);
			}
			// insert the new values into the corrent data row
			setRowValues(tempVec, row);
		} else
			throw new SizeMismatchException();
	}

	/**
	 * Normalizes the DataMatrix linearly using the complete data matrix as scope.
	 * 
	 * @param lowerBound	the lower bound of the projection range
	 * @param upperBound	the upper bound of the projection range
	 */
	public void normalize(double lowerBound, double upperBound) {
		this.normalize(lowerBound, upperBound, true, DataMatrixNormalizeConfig.SCOPE_MATRIX);
	}

	/**
	 * Normalizes the DataMatrix using the complete data matrix as scope.
	 * 
	 * @param lowerBound	the lower bound of the projection range
	 * @param upperBound	the upper bound of the projection range
	 * @param isLinear		linear or logarithmic normalization
	 */
	public void normalize(double lowerBound, double upperBound, boolean isLinear) {
		this.normalize(lowerBound, upperBound, isLinear, DataMatrixNormalizeConfig.SCOPE_MATRIX);
	}	

	/**
	 * Normalizes the DataMatrix.
	 * 
	 * @param lowerBound	the lower bound of the projection range
	 * @param upperBound	the upper bound of the projection range
	 * @param isLinear		linear or logarithmic normalization
	 * @param scope			indicates whether the normalization scope is the complete data matrix, normalization is performed for every row, or for every column separately
	 */
	public void normalize(double lowerBound, double upperBound, boolean isLinear, int scope) {
		double minValue, maxValue;			// variables to store minimum and maximum of original range
		// if normalization should be conducted logarithmically,
		// calculate log for every value
		if (!isLinear) {
			for (int i=0; i<this.getNumberOfRows(); i++) {
				for (int j=0; j<this.getNumberOfColumns(); j++) {
					try {
						this.setValueAtPos(new Double(Math.log(this.getValueAtPos(i, j).doubleValue())) ,i, j);
					} catch (SizeMismatchException sme) {
					}
				}
			}
		}
		// perform normalization according to scope
		switch (scope) {
			// scope is complete matrix
			case DataMatrixNormalizeConfig.SCOPE_MATRIX:
				// get smallest and largest value of the original range
				minValue = Double.MAX_VALUE;
				maxValue = minValue*-1;
				for (int i=0; i<this.getNumberOfRows(); i++) {
					for (int j=0; j<this.getNumberOfColumns(); j++) {
						double curValue = this.getValueAtPos(i, j).doubleValue();
//						System.out.println(curValue + "\t" + minValue + "\t" + maxValue);
						// ignore NaN, -Inf, and +Inf values
						if (curValue != Double.NaN && curValue != Double.NEGATIVE_INFINITY  && curValue != Double.POSITIVE_INFINITY) {
							if (curValue < minValue)
								minValue = curValue;
							if (curValue > maxValue)
								maxValue = curValue;							
						}
					}
				}
//				System.out.println(minValue + " " + maxValue);
				// perform normalization
				for (int i=0; i<this.getNumberOfRows(); i++) {
					for (int j=0; j<this.getNumberOfColumns(); j++) {
						// get original value
						double origValue = this.getValueAtPos(i, j).doubleValue();
						// project value from original range [minValue, maxValue] to normalization range [lowerBound, upperBound]
						double normValue = lowerBound + (upperBound-lowerBound)*((origValue-minValue)/(maxValue-minValue));	
						try {
							this.setValueAtPos(new Double(normValue) , i, j);
						} catch (SizeMismatchException sme) {
						}
					}
				}
				break;
			// scope is each row
			case DataMatrixNormalizeConfig.SCOPE_PER_ROW:
				// perform normalization for each row separately
				for (int k=0; k<this.getNumberOfRows(); k++) {
					// get smallest and largest value of current row
					minValue = Double.MAX_VALUE;
					maxValue = minValue*-1;
					for (int j=0; j<this.getNumberOfColumns(); j++) {
						double curValue = this.getValueAtPos(k, j).doubleValue();
						if (curValue < minValue)
							minValue = curValue;
						if (curValue > maxValue)
							maxValue = curValue;
					}
					// perform normalization
					for (int j=0; j<this.getNumberOfColumns(); j++) {
						// get original value
						double origValue = this.getValueAtPos(k, j).doubleValue();
						// project value from original range [minValue, maxValue] to normalization range [lowerBound, upperBound]
						double normValue = lowerBound + (upperBound-lowerBound)*((origValue-minValue)/(maxValue-minValue));	
						try {
							this.setValueAtPos(new Double(normValue), k, j);
						} catch (SizeMismatchException sme) {
						}
					}
				}
				break;
			// scope is each column
			case DataMatrixNormalizeConfig.SCOPE_PER_COLUMN:
				// perform normalization for each column separately
				for (int k=0; k<this.getNumberOfColumns(); k++) {
					// get smallest and largest value of current column
					minValue = Double.MAX_VALUE;
					maxValue = minValue*-1;
					for (int j=0; j<this.getNumberOfRows(); j++) {
						double curValue = this.getValueAtPos(j, k).doubleValue();
						if (curValue < minValue)
							minValue = curValue;
						if (curValue > maxValue)
							maxValue = curValue;
					}
					// perform normalization
					for (int j=0; j<this.getNumberOfRows(); j++) {
						// get original value
						double origValue = this.getValueAtPos(j, k).doubleValue();
						// project value from original range [minValue, maxValue] to normalization range [lowerBound, upperBound]
						double normValue = lowerBound + (upperBound-lowerBound)*((origValue-minValue)/(maxValue-minValue));	
						try {
							this.setValueAtPos(new Double(normValue), j, k);
						} catch (SizeMismatchException sme) {
						}
					}
				}				
				break;			
		}
	}
	
	/**
	 * Returns the number of rows in the DataMatrix.
	 *
	 * @return the number of rows in the matrix
	 */
	public int getNumberOfRows() {
		// return 0 if Vector instance has not been created
		if (vecRows != null)
			return vecRows.size();
		return 0;
	}
	
	/**
	 * Checks whether the DataMatrix contains only the
	 * values 0 and 1. If this is the case, <code>true</code> is returned, if not, <code>false</code>.
	 * 
	 * @return		<code>true</false> if the instance of DataMatrix only contains the values 0 and 1,
	 * 				<code>false</false> otherwise
	 */
	public boolean isBooleanMatrix() {
		// assume that matrix is boolean
		boolean isBoolean = true;
		// test every element in matrix for value of 0 or 1
		int rows = this.getNumberOfRows();
		int cols = this.getNumberOfColumns();
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				// if not 0 or 1, switch flag
				double value = this.getValueAtPos(i, j).doubleValue();
				if (value != 0.0 && value != 1.0)
					isBoolean = false;
			}
		}
		return isBoolean;
	}
	
	/**
	 * Returns the number of columns in the DataMatrix.
	 * 
	 * @return the number of columns in the matrix
	 */
	public int getNumberOfColumns() {
		// return the number of components in the first Vector
		// (that represents the first row of the DataMatrix)
		// if no first Vector instance was created, return 0
		if (vecRows.isEmpty() || (vecRows == null))
			return 0;
		return vecRows.firstElement().size();
	}
	
	/**
	 * Sets the name of DataMatrix to the value of the parameter <code>name</code>. 
	 *
	 * @param name	the name that is to be given to the matrix
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the DataMatrix.
	 *
	 * @return a String containing the name of the DataMatrix
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Implements the clone() function, so we can easily produce
	 * copies of DataMatrices. vecColCurrent set to the Vector which 
	 * represents the first row.
	 *
	 * created by MSt */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone(){
		DataMatrix cloned = null;
		try {
			cloned = (DataMatrix)super.clone();
        }
        catch (CloneNotSupportedException e) {
            // this should never happen
            throw new InternalError(e.toString());
        }
        cloned.name = name;
        cloned.vecRows = new Vector<Vector<Double>>();
		for(int i=0; i<vecRows.size(); i++)
			cloned.vecRows.add((Vector<Double>) vecRows.elementAt(i).clone());
		
		cloned.vecColCurrent = cloned.vecRows.elementAt(0);
		return cloned;
	}
	
	/**
	 * Inserts a row at the given index.
	 * @param data
	 * @param index has to be equal or greater than 0, and less than
	 * or equal to the number of rows.
	 * @throws SizeMismatchException
	 */
	public void insertRow(Vector<Double> data, int index) throws SizeMismatchException {
		// if no complete row is existing
		if(vecRows.size() == 1) {
			//problem: getNumberOfRows return always at least 1, 
			//even nothing has been written, so accept index 0 and 1
			if(index > 1)
				throw new ArrayIndexOutOfBoundsException();
			
			if(vecColCurrent.isEmpty()) {
				if(index == 0)
					vecRows.setElementAt(data, index);
				else
					vecRows.insertElementAt(data, index);
			} else {
				vecRows.setElementAt(vecColCurrent, 0);
				if(data.size() == this.getNumberOfColumns()) {
					vecRows.insertElementAt(data, 1);
				} else
					throw new SizeMismatchException();
			}
		} else if(data.size() == this.getNumberOfColumns()) 
			vecRows.insertElementAt(data, index);
		else
			throw new SizeMismatchException();	
	}
}