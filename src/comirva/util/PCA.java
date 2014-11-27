/*
 * Created on 15.11.2006
 */
package comirva.util;

import comirva.data.DataMatrix;

import java.io.*;
import java.util.StringTokenizer;

import Jama.*;


/**
 * This class implements the projection of a
 * data set with a Principal Components Analysis.
 * 
 * @author Markus Schedl, Peter Knees
 */
public class PCA {

	private double[] means;
	private double[] eigValues;
	private Matrix eigVecs;
	private Matrix pcaCompressed;
	
	/**
	 * Creates a new Principal Components Analysis (PCA) and calculates it using the
	 * Jama Matrix <code>m</code> as input. Furthermore, the projection of the input data to
	 * a space of dimensionality <code>dim</code> is performed.
	 * 
	 * @param m		a Matrix representing the input data
	 * @param dim		the number of dimensions onto which the input data is to be projected
	 */
	public PCA(Matrix m, int dim) {
		
		// calc mean-normalized data set
		int cols = m.getColumnDimension();
		int rows = m.getRowDimension();
		// get means
		means = new double[cols];
		for (int col=0; col<cols; col++) {
			for (int row=0; row<rows; row++) {
				means[col] += m.get(row, col);
			}
			means[col] /= m.getRowDimension();
//			System.out.println("mean of col "+col+": "+means[col]);
		}
		// mean normalize 
		double[][] meanNorm = new double[rows][cols];
		for (int col=0; col<cols; col++) {
			for (int row=0; row<rows; row++) {
				meanNorm[row][col] = m.get(row, col)-means[col];
			}
		}
		Matrix meanNormalizedData = new Matrix(meanNorm);
		meanNormalizedData = meanNormalizedData.transpose();
			
		// calc covariance matrix
		Matrix covMatrix = meanNormalizedData.times(meanNormalizedData.transpose());
		double[][] dimMinus1 = new double[covMatrix.getColumnDimension()][covMatrix.getColumnDimension()];
		for (int i=0; i<dimMinus1.length; i++)
			for (int j=0; j<dimMinus1.length; j++)
				dimMinus1[i][j] = covMatrix.getColumnDimension() - 1;
		covMatrix = covMatrix.arrayRightDivideEquals(new Matrix(dimMinus1));
		
		// Eigenvalue Decomposition
		EigenvalueDecomposition eigd = covMatrix.eig();
		eigVecs = eigd.getV();
		eigValues = eigd.getRealEigenvalues();

		// check requested dimensionality
		if (dim > eigVecs.getColumnDimension())
			dim = eigVecs.getColumnDimension();
		
		// extract those <code>dim</code> eigenvectors with highest eigenvalues
		Matrix firstDimEVecs = eigVecs.getMatrix(0, eigVecs.getRowDimension()-1, eigVecs.getColumnDimension()-dim, eigVecs.getColumnDimension()-1);
		
		// project data
		pcaCompressed = meanNormalizedData.transpose().times(firstDimEVecs);
				
//		pcaCompressed.print(5, 3);
//		for (int i=0; i<eigValues.length; i++)
//			System.out.println(eigValues[i]);
		
	}
	
	/**
	 * Creates a new Principal Components Analysis (PCA) and calculates it using the
	 * data matrix <code>data</code> as input. Furthermore, the projection of the input data to
	 * a space of dimensionality <code>dim</code> is performed.
	 * 
	 * @param data		a double[][] representing the input data
	 * @param dim		the number of dimensions onto which the input data is to be projected
	 */
	public PCA(double[][] data, int dim) {
		this(new Matrix(data), dim);
	}
	
	/**
	 * Creates a new Principal Components Analysis (PCA) and calculates it using the
	 * data matrix <code>data</code> as input. Furthermore, the projection of the input data to
	 * a space of dimensionality <code>dim</code> is performed.
	 * 
	 * @param data		a DataMatrix representing the input data
	 * @param dim		the number of dimensions onto which the input data is to be projected
	 */
	public PCA(DataMatrix data, int dim) {
		this(new Matrix(data.toDoubleArray()), dim);
	}
	
	public double[][] getPCATransformedDataAsDoubleArray() {
		return pcaCompressed.getArray();
	}
	public Matrix getPCATransformedDataAsMatrix() {
		return pcaCompressed;
	}
	public DataMatrix getPCATransformedDataAsDataMatrix() {
		DataMatrix dm = DataMatrix.jamaMatrixToDataMatrix(pcaCompressed);
		dm.setName("PCA-projection");
		return dm;
	}
	
	public double[][] getEigenvectorsAsDoubleArray() {
		return this.eigVecs.getArray();
	}
	public Matrix getEigenvectorsAsMatrix() {
		return this.eigVecs;
	}
	public DataMatrix getEigenvectorsAsDataMatrix() {
		DataMatrix dm = DataMatrix.jamaMatrixToDataMatrix(this.eigVecs);
		dm.setName("Eigenvectors for PCA");
		return dm;
	}
	
	public double[] getEigenvalues() {
		return this.eigValues;
	}
	public DataMatrix getEigenvaluesAsDataMatrix() {
		double[] temp = this.eigValues;
		DataMatrix dm = new DataMatrix("Eigenvalues for PCA");
		for (int i=0; i<temp.length; i++) {
			dm.addValue(new Double(temp[i]));
			dm.startNewRow();
		}
		dm.removeLastAddedElement();
		return dm;
	}
	
	public double[] getMeans() {
		return this.means;
	}
	public DataMatrix getMeansAsDataMatrix() {
		double[] temp = this.means;
		DataMatrix dm = new DataMatrix("Means for PCA");
		for (int i=0; i<temp.length; i++) {
			dm.addValue(new Double(temp[i]));
			dm.startNewRow();
		}
		dm.removeLastAddedElement();
		return dm;
	}
}
