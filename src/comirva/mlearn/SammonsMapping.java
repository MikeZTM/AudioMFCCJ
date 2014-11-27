package comirva.mlearn;

import comirva.util.*;
import cp.util.*;

public class SammonsMapping {
	// input space
	private double[][] dists;
	private double distssum;
	
	// output space (2-dim)
	private double[][] lowdimdists;
	private double[][] lowcoords;
	
	private double currentError;
	
	private final double verySmallValue = 0.000000000000000000001;
	
	public SammonsMapping(double[][] datadistances) {
		if (datadistances.length < 2)
			throw new IllegalArgumentException("need more than 2 points");
		if (datadistances.length != datadistances[0].length)
			throw new IllegalArgumentException("distance matrix needs to be quadratic.");
		dists = datadistances;
		
		// calc dist sums
		// replace all zero distances with min value
		distssum = 0.;
		for (int i=0; i<dists.length; i++ ) {
			for (int j=i+1; j<dists.length; j++) {
				if (dists[i][j]==0.) {
					dists[i][j] = verySmallValue;
					dists[j][i] = verySmallValue;
				}
				distssum += dists[i][j];
			}
		}
//		System.out.println("distsum: "+distssum);
		
		// init low dimensional projection via PCA to 2-dim
		PCA initpca = new PCA(dists, 2);
		lowcoords = initpca.getPCATransformedDataAsDoubleArray();
		// calc dists in low dim space
		lowdimdists = Vec.getDistanceMatrix(lowcoords);
		
		// calc closeness
		double E = 0.0;
		for (int i=0; i<dists.length; i++) {
			for (int j=i+1; j<dists.length; j++) {
				E += (dists[i][j]-lowdimdists[i][j])*(dists[i][j]-lowdimdists[i][j])/dists[i][j];
				if (Double.isInfinite(E)) {
					System.out.println(i+","+j+": dist: "+dists[i][j]+", lowdimdist: "+lowdimdists[i][j]);
				}
			}
		}
		currentError = E/distssum;
	}
	
	/**
	 * iterates until one of the given constraints is satisfied
	 * @param iterations maximum number of iteration
	 * @param threshold decrease of error between 2 subsequent iterations is below this value
	 * @param milliseconds time tolerated
	 */
	public void iterate(int iterations, double threshold, long milliseconds) {
		double oldError;
		long startTime = System.currentTimeMillis();
		for (int it=0; it<iterations && System.currentTimeMillis()-startTime<milliseconds; it++) {
//		for (int it=0; it<iterations; it++) {
//			System.out.println("Iteration "+(it+1)+", last err: "+getError()+", time elapsed: "+(System.currentTimeMillis()-startTime));
			double[][] newlowcoords = new double[lowcoords.length][2];
			for (int m=0; m<dists.length; m++) {
				double[] lowcm = lowcoords[m];
				double psum0 = 0.0;
				double psum1 = 0.0;
				for (int i=0; i<dists.length; i++) {
					if (i==m)
						continue;
					double[] lowci = lowcoords[i];
					psum0 += ((dists[i][m]-lowdimdists[i][m])*(lowci[0]-lowcm[0])/dists[i][m]/Math.max(verySmallValue, lowdimdists[i][m]));
					psum1 += ((dists[i][m]-lowdimdists[i][m])*(lowci[1]-lowcm[1])/dists[i][m]/Math.max(verySmallValue, lowdimdists[i][m]));
				}
				newlowcoords[m][0] = lowcm[0] - 6*psum0/distssum;
				newlowcoords[m][1] = lowcm[1] - 6*psum1/distssum;
			}
			// update coords and distances
			lowcoords = newlowcoords;
			lowdimdists = Vec.getDistanceMatrix(lowcoords);

			// update closeness
			oldError = currentError;
			double E = 0.0;
			for (int i=0; i<dists.length; i++) {
				for (int j=i+1; j<dists.length; j++) {
					E += (dists[i][j]-lowdimdists[i][j])*(dists[i][j]-lowdimdists[i][j])/dists[i][j];
					if (Double.isInfinite(E) || Double.isNaN(E)) {
						System.out.println(i+","+j+": dist: "+dists[i][j]+", lowdimdist: "+lowdimdists[i][j]);
					}
				}
			}
			currentError = E/distssum;
			
			if (oldError-currentError<threshold)
				break;
		}
	}

	public double getError() {
		return currentError;
	}

	public double[][] getLowcoords() {
		return lowcoords;
	}
	
	

}
