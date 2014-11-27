// Peter's implementation of the PCA
package comirva.util.external;

import Jama.*;
import cp.util.*;

public class PCA {

	private Matrix inputdata;
	private Matrix w;
	private Matrix result;
	private double[] means;
	private double[] eigenvalues;

	public PCA(double[][] m) {
		inputdata = Matrix.constructWithCopy(m);
		
		int num_samples = inputdata.getRowDimension();
		int d = inputdata.getColumnDimension();
		
//		System.out.println("observations: "+num_samples);
//		System.out.println("dimensions: "+d);
		
		// remove mean for each dimension
		double[] means = new double[d];
		Matrix inputtrans = inputdata.transpose();
		double[][] mtrans = inputtrans.getArray();
		for (int i=0; i<d; i++) {
		    means[i] = Stat.mean(mtrans[i]);
//			System.out.println("mean no "+i+": "+means[i]);
		    for (int j=0; j<num_samples; j++) {
		    	double newval = inputdata.get(j, i)-means[i];
		    	inputdata.set(j, i, newval);
		    	inputtrans.set(i, j, newval);
		    	mtrans[i][j] = newval;
		    }
		}

		// calculate cov matrix between dimensions
		Matrix S = new Matrix(d, d);
		for (int i=0; i<d; i++) {
			for (int j=i; j<d; j++) {
				double covar = Stat.cov(mtrans[i], mtrans[j]);
				S.set(i, j, covar);
				S.set(j, i, covar);
			}
		}
		EigenvalueDecomposition eigen = S.eig();
		w = eigen.getV(); // eigenvectors
		Matrix eigenvals = eigen.getD();
		
		// get eigenvalues from diagonal
		eigenvalues = new double[d];
		for (int i=0; i<d; i++) {
			eigenvalues[i] = eigenvals.get(i, i);
		}
		// sort eigenvalues
		int[] sortidx = Vec.sort(eigenvalues, true);

		double[][] w_array = w.transpose().getArray();
		double[][] w_resorted = new double[w_array.length][w_array[0].length];
		// sort w according to eigenvalues
		for (int i=0; i<d; i++) {
			w_resorted[sortidx[i]] = w_array[i];
		}
		w = new Matrix(w_resorted);//.transpose();
		System.out.println("w="+TextTool.toMatlabFormat(w.getArray()));
		result = w.times(inputdata.transpose()).transpose();
	}
	
	public double[][] getPCATransformedData() {
		return result.getArray();
//		int d = inputdata.getColumnDimension();
//		if (numPCs<1 || numPCs>d)
//			return w.times(inputdata.transpose()).transpose().getArray();
		
//		int[] containsIdxs = new int[inputdata.getRowDimension()];
//		for (int i=0; i<containsIdxs.length; i++)
//			containsIdxs[i] = i;
//		Matrix prunedw = w.getMatrix(0, numPCs-1, containsIdxs);
//		return prunedw.times(inputdata.transpose()).transpose().getArray();
	}
	
	public Matrix getW() {
		return w;
	}
	
	public double[] getEigenvalues() {
		return eigenvalues;
	}
	
	public double[] getMeans() {
		return means;
	}
	
	public static void main(String[] args) {
		double[][] data = {{1,1,1,1}, {0,4,6,8}, {2,1,2,1}};//, {2,2,1,2}};
		System.out.println("observations: "+data.length);
		System.out.println("dimensions: "+data[0].length);
		PCA pca = new PCA(data);
		double[][] pcadata = pca.getPCATransformedData();
		System.out.println("observations: "+pcadata.length);
		System.out.println("dimensions: "+pcadata[0].length);
		System.out.println(TextTool.toMatlabFormat(pcadata));
	}
}
