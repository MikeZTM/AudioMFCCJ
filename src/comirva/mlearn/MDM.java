package comirva.mlearn;

import java.awt.Color;
import java.util.*;

import Jama.*;

import comirva.io.web.*;
import comirva.util.*;
import cp.util.*;
import cp.util.helpers.ObjectComparablePair;
/**
 * 
 * @author peter knees
 *
 */


public class MDM extends Thread implements ThreadListener {
	private SOM som;
	private WebTermExtractionThread wtet;
	
	private Vector<ThreadListener> threadlisteners = new Vector<ThreadListener>();
	
	// settings
	private String queryconstraint = "music style";
	private int minTermsPerUnit = 3;
	private int maxTermsPerUnit = 30;
	
	// for predefined artistsets
	Hashtable<String, int[]> artterms = null;
	
	private Vector<Vector<String>> mdmlabels = new Vector<Vector<String>>();
	private int[] mdmClusterAssociations = null;
	private int[][] mdmNeighborhood = null;
	private double[][] clusterlabelfeatures = null;
	
	private boolean colorByPCA = false;
	private Color[] cellColors;
	
	public MDM (SOM som) {
		this.som=som;
	}
	
	public MDM (SOM som, Hashtable<String, int[]> artistTermvectors) {
		this.som=som;
		this.artterms = artistTermvectors;
	}
	
	public MDM (SOM som, int minTermsPerUnit, int maxTermsPerUnit) {
		this.som=som;
		this.minTermsPerUnit = minTermsPerUnit;
		this.maxTermsPerUnit = maxTermsPerUnit;
	}
	
	public MDM (SOM som, Hashtable<String, int[]> artistTermvectors, int minTermsPerUnit, int maxTermsPerUnit) {
		this.som=som;
		this.artterms = artistTermvectors;
		this.minTermsPerUnit = minTermsPerUnit;
		this.maxTermsPerUnit = maxTermsPerUnit;
	}
	
	public MDM (SOM som, boolean colorByPCA) {
		this.som=som;
		this.colorByPCA = colorByPCA;
	}
	
	public MDM (SOM som, Hashtable<String, int[]> artistTermvectors, boolean colorByPCA) {
		this.som=som;
		this.artterms = artistTermvectors;
		this.colorByPCA = colorByPCA;
	}
	
	public MDM (SOM som, int minTermsPerUnit, int maxTermsPerUnit, boolean colorByPCA) {
		this.som=som;
		this.minTermsPerUnit = minTermsPerUnit;
		this.maxTermsPerUnit = maxTermsPerUnit;
		this.colorByPCA = colorByPCA;
	}
	
	public MDM (SOM som, Hashtable<String, int[]> artistTermvectors, int minTermsPerUnit, int maxTermsPerUnit, boolean colorByPCA) {
		this.som=som;
		this.artterms = artistTermvectors;
		this.minTermsPerUnit = minTermsPerUnit;
		this.maxTermsPerUnit = maxTermsPerUnit;
		this.colorByPCA = colorByPCA;
	}
	
	public void run() {
		if (som==null || ((som.getLabels()== null || som.voronoiSet == null) && artterms == null))
			return;
		// check if termvector information is available -> if not: crawl
		if (artterms == null) {
//			System.out.println("do the crawl...");
			// artists are contained in labels (-> collect)
			Set<String> artistset = new HashSet<String>();
			for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook
				for
				
				 (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook
					Vector temp = (Vector)som.voronoiSet.elementAt(i*som.getNumberOfRows()+j);
					// read all labels in Voronoi-Set of current map unit
					for (int k=0; k<temp.size(); k++) {
						Integer labelIndex = (Integer)temp.elementAt(k);
						artistset.add(som.getLabel(labelIndex.intValue()));
					}			
				}
			}
			String[] artists = artistset.toArray(new String[artistset.size()]);
			wtet = new WebTermExtractionThread(artists, queryconstraint);
			wtet.addThreadListener(this);
			wtet.start(); // -> threadEnded
		}
		else {
			System.out.println("data already present.");
			this.threadEnded();
		}
	}

	public void threadEnded() {
		if (wtet != null && artterms == null)
			artterms = wtet.getArtistsAndTermVectors();
		
		int cols = som.getNumberOfColumns(),
		rows = som.getNumberOfRows();
		
		// use lagus-kaski labelling technique
		// generate summed tf vector per cluster
		int[][] clustervecs = new int[cols*rows][MusicDictionary.getDictionary().size()];
		int[] clustersize = new int[cols*rows];
		int[] vecsum = new int[MusicDictionary.getDictionary().size()];
		
		// for lagus kaski G2 determine r0 and r1 zone indices
		int[][] r0elements = new int[cols*rows][5];
		int[][] r1elements = new int[cols*rows][8];
		
		for (int i=0; i<cols; i++) {		// for each column in codebook
			for (int j=0; j<rows; j++) {		// for each row in codebook
				int mappos = i*som.getNumberOfRows()+j;
				// get Voronoi-Set for current map unit
				Vector temp = (Vector)som.voronoiSet.elementAt(mappos);
				clustersize[mappos] = temp.size();
				// get this set ordered
				temp = som.getPrototypesForMU(mappos, clustersize[mappos]);
				int[] clustterms = new int[MusicDictionary.getDictionary().size()];
				for (int k=0; k<clustersize[mappos]; k++) {
					String artist = (String)temp.elementAt(k);
					int[] termvec = artterms.get(artist);
					if (termvec==null) {
						System.err.println("no term vector for artist "+artist);
						continue;
					}
					Vec.addTo(clustterms, termvec);
				}
				// remove all terms with tf < 3 in cluster
				for (int k=0; k<clustterms.length; k++) {
					if (clustterms[k]<Math.min(3, clustersize[mappos]))
						clustterms[k]=0;
				}
				Vec.addTo(vecsum, clustterms);
				
				//				System.out.println("total "+Stat.sum(clustterms)+" terms in cluster "+mappos);
				clustervecs[mappos] = clustterms;//Vec.divide(clustterms, (count>0)?count:1);
				
				// finally, determine r0 and r1 zone elements
				// for r0
				r0elements[mappos][0] = mappos;
				r0elements[mappos][1] = i>0?(i-1)*rows+j:-1;
				r0elements[mappos][2] = i+1<cols?(i+1)*rows+j:-1;
				r0elements[mappos][3] = j>0?i*rows+j-1:-1;
				r0elements[mappos][4] = j+1<rows?i*rows+j+1:-1;
				// for r1
				r1elements[mappos][0] = i>1?(i-2)*rows+j:-1;
				r1elements[mappos][1] = i+2<cols?(i+2)*rows+j:-1;
				r1elements[mappos][2] = j>1?i*rows+j-2:-1;
				r1elements[mappos][3] = j+2<rows?i*rows+j+2:-1;
				r1elements[mappos][4] = i>0 && j>0?(i-1)*rows+j-1:-1;
				r1elements[mappos][5] = i+1<cols && j>0?(i+1)*rows+j-1:-1;
				r1elements[mappos][6] = i>0 && j+1<rows?(i-1)*rows+j+1:-1;
				r1elements[mappos][7] = i+1<cols && j+1<rows?(i+1)*rows+j+1:-1;
			}
		}
		
		double[] summedterms = new double[MusicDictionary.getDictionary().size()];
		int[] clustertermsums = new int[cols*rows];
		for (int i=0; i<clustervecs.length; i++) {
			clustertermsums[i] = Stat.sum(clustervecs[i]);
//			for (int j=0; j<clustervecs[i].length; j++) {
//				summedterms[j] += clustertermsums[i]>0?clustervecs[i][j]/clustertermsums[i]:0;
//			}
			if (clustertermsums[i]==0)
				continue;
			Vec.addTo(summedterms, Vec.divide(clustervecs[i], clustertermsums[i]));
		}
		double[][] lkvalues = new double[cols*rows][MusicDictionary.getDictionary().size()];
		// determine min and max laguskaski values for value normalization between 0 and 1
		double minlk = 0.01d;
		double maxlk = 0.;
		// for each term in each cluster
		for (int i=0; i<clustervecs.length; i++) {
			
			// create r0 zone sum vector
			// modification to reflect number of entries
			double[] r0sum = new double[MusicDictionary.getDictionary().size()];
			for (int j=0; j<r0elements[i].length; j++) {
				if (r0elements[i][j]==-1) continue;
				if (clustertermsums[r0elements[i][j]]==0)
					continue;
				Vec.addTo(r0sum, Vec.divide(clustervecs[r0elements[i][j]], clustertermsums[r0elements[i][j]]));
			}
			// create non-r1 zone sum vector
			double[] nonr1sum = Vec.cloneVector(summedterms);
			for (int j=0; j<r1elements[i].length; j++) {
				if (r1elements[i][j]==-1) continue;
				if (clustertermsums[r1elements[i][j]]==0)
					continue;
				Vec.subtractFrom(nonr1sum, Vec.divide(clustervecs[r1elements[i][j]], clustertermsums[r1elements[i][j]]));
			}
				
			for (int j=0; j<clustervecs[i].length; j++) {
//				// lagus kaski G0
//				double fclust = clustertermsums[i]>0?clustervecs[i][j]/clustertermsums[i]:0;
//				double fpen = summedterms[j];
				
				// lagus kaski G2
				double fclust = r0sum[j];
				double fpen = nonr1sum[j];
				
				
				if (clustervecs[i][j]==0  // only accept words, that were on the island before G2
						|| fclust==0. || fpen==0.)
					continue;
				Double ftc = new Double(fclust*fclust/fpen);
				
				// find max for normalization
				if (ftc > maxlk)
					maxlk = ftc;
				
				// smooth -> only values with score >= minlk
				if (ftc>=minlk)
				lkvalues[i][j] = ftc;
			}
		}
		
//		// calculate all pairwise distances matrix of cos norm vectors
//		Vector<Double> dists = new Vector<Double>();
//		for (int i=0; i<lkvalues.length-1; i++) {
//			for (int j=i+1; j<lkvalues.length; j++) {
//				double[] cosNormA = Vec.cosineNormalize(Vec.cloneVector(lkvalues[i]));
//				double[] cosNormB = Vec.cosineNormalize(Vec.cloneVector(lkvalues[j]));
//				dists.addElement(new Double(Vec.euclDist(cosNormA, cosNormB)));
//			}
//		}
//		Collections.sort(dists);
//		System.out.println("sorted pairwise distances of all clusters");
//		Iterator<Double> dit = dists.iterator();
//		while (dit.hasNext()) {
//			System.out.println(dit.next());
//		}

		// init cluster formation map -> every unit is its own cluster
		int[] clusterassociations = new int[cols*rows];
		for (int i=0; i<clusterassociations.length; i++) {
			clusterassociations[i] = i;
		}

		// copy original lkvalues (for coloring later)
		double[][] origlkvalues = new double[cols*rows][MusicDictionary.getDictionary().size()];
		for (int i=0; i<cols*rows; i++) {
			for (int j=0; j<MusicDictionary.getDictionary().size(); j++) {
				origlkvalues[i][j] = lkvalues[i][j];
			}
		}
		
		// find coherent regions on SOM
		findUnitClusters(clusterassociations, lkvalues, r0elements);
		
//		// print clusterassoc map
//		System.out.println("clusterassociations");
//		for (int j=0; j<rows; j++) {		// for each row in codebook
//			for (int i=0; i<cols; i++) {		// for each column in codebook
//				int mappos = i*som.getNumberOfRows()+j;
//				System.out.print(clusterassociations[mappos]+" ");
//			}
//			System.out.println("");
//		}
		
		Vector[] clusterterms = new Vector[cols*rows];
		for (int i=0; i<lkvalues.length; i++) {
			clusterterms[i] = new Vector();
			for (int j=0; j<lkvalues[i].length; j++) {
				if (lkvalues[i][j] > minlk) {
					ObjectComparablePair ocp = new ObjectComparablePair(
							MusicDictionary.getDictionary().elementAt(j),
							new Double(Math.min(maxlk, lkvalues[i][j]))); // use old max as upper bound for all value
					clusterterms[i].addElement(ocp);
				}
				//if (lkvalues[i][j] > maxlk) maxlk = lkvalues[i][j];
			}
			Collections.sort(clusterterms[i]);
			Collections.reverse(clusterterms[i]);
		}
		
		Vector<Vector<String>> mdmlabels = new Vector<Vector<String>>();
		// calc normalized lagus kaski
		for (int i=0; i<cols; i++) {		// for each column in codebook
			for (int j=0; j<rows; j++) {		// for each row in codebook
				int mappos = i*som.getNumberOfRows()+j;
				
				Vector<String> unitterms = new Vector<String>();
				// get terms for current map unit
				for (int k=0; k<clusterterms[mappos].size() && k<maxTermsPerUnit; k++) {
					ObjectComparablePair ocp = (ObjectComparablePair)(clusterterms[mappos].elementAt(k));
					double laguskaski = ((Double)(ocp.getComparable())).doubleValue();
					String word = (String)(ocp.getObject());
					if (k<minTermsPerUnit || laguskaski > minlk) {
						double normlk = (laguskaski-minlk)/(maxlk-minlk);
						String wordandval = word+"_"+normlk;//TextTool.doubleToString(laguskaski, 3)+")";
						unitterms.addElement(wordandval);
						
					}
				}
				mdmlabels.addElement(unitterms);
			}
		}
		
		som.setMDM(this);
		this.setLabels(mdmlabels);
		this.setClusterAssociations(clusterassociations);
		this.setNeighborhood(r0elements);
		
		if (colorByPCA) {
			Vector[] colorclusterterms = new Vector[cols*rows];
			for (int i=0; i<origlkvalues.length; i++) {
				colorclusterterms[i] = new Vector();
				for (int j=0; j<origlkvalues[i].length; j++) {
					if (origlkvalues[i][j] > minlk) {
						ObjectComparablePair ocp = new ObjectComparablePair(
								MusicDictionary.getDictionary().elementAt(j),
								new Double(Math.min(maxlk, origlkvalues[i][j]))); // use old max as upper bound for all value
						colorclusterterms[i].addElement(ocp);
					}
				}
				Collections.sort(colorclusterterms[i]);
				Collections.reverse(colorclusterterms[i]);
			}
			
			HashSet<String> remainingwords = new HashSet<String>();
			Hashtable<String, Double>[] mdmvalues = new Hashtable[cols*rows];
			// calc normalized lagus kaski
			for (int i=0; i<cols; i++) {		// for each column in codebook
				for (int j=0; j<rows; j++) {		// for each row in codebook
					int mappos = i*som.getNumberOfRows()+j;
					
					mdmvalues[mappos] = new Hashtable<String, Double>();
					Vector<String> unitterms = new Vector<String>();
					// get terms for current map unit
					for (int k=0; k<colorclusterterms[mappos].size() && k<maxTermsPerUnit; k++) {
						ObjectComparablePair ocp = (ObjectComparablePair)(colorclusterterms[mappos].elementAt(k));
						double laguskaski = ((Double)(ocp.getComparable())).doubleValue();
						String word = (String)(ocp.getObject());
						if (k<minTermsPerUnit || laguskaski > minlk) {
							double normlk = (laguskaski-minlk)/(maxlk-minlk);
							
//							remainingwords.add(word);
//							mdmvalues[mappos].put(word, new Double(laguskaski));
						}
						if (k<4) {
							remainingwords.add(word);
							mdmvalues[mappos].put(word, new Double(laguskaski));
						}
					}
					mdmlabels.addElement(unitterms);
				}
			}
			
			
//			// remove all words that never occur
//			Vector<double[]> relevantDimensions = new Vector<double[]>();
//			double[][] featuredims = new Matrix(lkvalues).transpose().getArray();
//			for (int i=0; i<featuredims.length; i++) {
//				if (Stat.sum(featuredims[i]) > 0.1) {
//					relevantDimensions.addElement(featuredims[i]);
//				}
//			}
//			double[][] reduceddims = new double[relevantDimensions.size()][lkvalues.length];
//			for (int i=0; i<relevantDimensions.size(); i++) {
//				reduceddims[i] = relevantDimensions.elementAt(i);
//			}
//			cellColors = PCAProjectionToColor.getColorsForFeatures(new Matrix(reduceddims).transpose().getArray());
//			---------------------------
			
			// construct new vocabulary vector from remaining words
			// ignore all empty vectors (discard cells w/o entries)
			String[] remainingvocab = remainingwords.toArray(new String[0]);
			Hashtable<Integer, double[]> reducedVectorMapping = new Hashtable<Integer, double[]>();
			int[] colorarraymapping = new int[cols*rows];
			int usefulfeats = 0;
			for (int i=0; i<cols*rows; i++) {
				double[] nufeat = HashtableTool.getDoubleVectorRepresentation(mdmvalues[i], remainingvocab);
				if (Stat.max(nufeat) == 0.) {
					colorarraymapping[i] = -1;
				}
				else {
					reducedVectorMapping.put(new Integer(usefulfeats), nufeat);
					colorarraymapping[i] = usefulfeats;
					usefulfeats++;
				}
			}
			// recreate nufeature set
			double[][] nufeatures = new double[usefulfeats][remainingvocab.length];
			for (int i=0; i<nufeatures.length; i++) {
				nufeatures[i] = Vec.cosineNormalize(reducedVectorMapping.get(new Integer(i)));
			}
//			System.out.println(TextTool.toMatlabFormat(nufeatures));
			
			PCA pca = new PCA(nufeatures, 20);
			Color[] reducedSetCellColors = SammonsMappingToColor.getColorsForFeatures(pca.getPCATransformedDataAsDoubleArray());
			
			// recreate Color assignment
			cellColors = new Color[colorarraymapping.length];
			for (int i=0; i<cellColors.length; i++) {
				if (colorarraymapping[i] == -1)
					cellColors[i] = Color.white;
				else
					cellColors[i] = reducedSetCellColors[colorarraymapping[i]];
			}
			
//			cellColors = SammonsMappingToColor.getColorsForFeatures(new PCA(lkvalues, 20).getPCATransformedDataAsDoubleArray());
		}
		else {
			cellColors = new Color[cols*rows];
		}
		
		
		for (Enumeration e=threadlisteners.elements(); e.hasMoreElements(); ) {
			ThreadListener tl = (ThreadListener)(e.nextElement());
			tl.threadEnded();
		}
	}
	
	public void addThreadListener(ThreadListener threadlistener) {
		threadlisteners.addElement(threadlistener);
	}
	
	
	private void findUnitClusters(int[] clusterassoc, double[][] lkvalues, int[][] neighbors) {
		Vector<ObjectComparablePair> topunitterms = new Vector<ObjectComparablePair>();
		// sort units according to their highest value -> order to find clusters
		for (int i=0; i<lkvalues.length; i++) {
			double max = Stat.max(lkvalues[i]);
			if (max <= 0.)
				continue;
			topunitterms.addElement(new ObjectComparablePair(new Integer(i), new Double(max)));
		}
		Collections.sort(topunitterms);
		Collections.reverse(topunitterms);
		
		Iterator<ObjectComparablePair> ocpit = topunitterms.iterator();
		while (ocpit.hasNext()) {
			int curunit = ((Integer)(ocpit.next().getObject())).intValue();
			floodFillSimilarClusters(curunit, clusterassoc, lkvalues, neighbors, curunit);
		}
		
		// join all empty units
		int firstemptyindex = -1;
		for (int i=0; i<lkvalues.length; i++) {
			if (Stat.max(lkvalues[i]) <= 0.) {
				if (firstemptyindex == -1)
					firstemptyindex = i;
				else
					clusterassoc[i] = firstemptyindex;
			}
		}
	}
	
	private void floodFillSimilarClusters(int unitpos, int[] clusterassoc, double[][] lkvalues, int[][] neighbors, int startunitpos) {
		// check if already associated to other cluster (end of recursion)
		if (clusterassoc[unitpos] != unitpos && clusterassoc[unitpos] != startunitpos)
			return;
		
		Vector<Integer> joinedunits = new Vector<Integer>();
		// check neighbors (starts with index 1) for join candidates
		for (int i=1; i<neighbors[unitpos].length; i++) {
			if (neighbors[unitpos][i] != -1 && // on the som
				clusterassoc[neighbors[unitpos][i]] != startunitpos && // not already in cluster
				clusterassoc[neighbors[unitpos][i]] == neighbors[unitpos][i]) { // not part of any other cluster
				
				// join two units iff summed up vector differs below threshold from both original vectors
				double[] cosNormA = Vec.cosineNormalize(Vec.cloneVector(lkvalues[unitpos]));
				double[] cosNormB = Vec.cosineNormalize(Vec.cloneVector(lkvalues[neighbors[unitpos][i]]));
				double[] sumAB = Vec.add(lkvalues[unitpos], lkvalues[neighbors[unitpos][i]]);
				double[] cosNormAB = Vec.cosineNormalize(Vec.cloneVector(sumAB));
				
				if (Vec.euclDist(cosNormA, cosNormAB) < 0.6 && Vec.euclDist(cosNormB, cosNormAB) < 0.6) {
					// similar units -> join
					clusterassoc[neighbors[unitpos][i]] = startunitpos;
					
					// write new vector to all associated
					for (int k=0; k<clusterassoc.length; k++) {
						if (clusterassoc[k] == startunitpos) {
							lkvalues[k] = sumAB;
						}
					}
//					Vec.copyTo(lkvalues[unitpos], sumAB);
//					Vec.copyTo(lkvalues[neighbors[unitpos][i]], sumAB);
					
					// add to list for recursion
					joinedunits.addElement(new Integer(neighbors[unitpos][i]));
				}
				
			}
		}
		// recursion over joined units
		Iterator<Integer> iit = joinedunits.iterator();
		while (iit.hasNext()) {
			floodFillSimilarClusters(iit.next().intValue(), clusterassoc, lkvalues, neighbors, startunitpos);
		}
	}

	private void setLabels(Vector<Vector<String>> mdmlabels) {
		this.mdmlabels = mdmlabels;
	}
	private void setClusterAssociations(int[] mdmClusterAssociations) {
		this.mdmClusterAssociations = mdmClusterAssociations;
	}
	private void setNeighborhood(int[][] mdmNeighborhood) {
		this.mdmNeighborhood = mdmNeighborhood;
	}
//	private void setClusterLabelFeatures(double[][] clusterlabelfeatures) {
//		this.clusterlabelfeatures = clusterlabelfeatures;
//	}
	
	
	public Vector<Vector<String>> getLabels() {
		return mdmlabels;
	}
	public int[] getClusterAssociations() {
		return mdmClusterAssociations;
	}
	public int[][] getNeighborhood() {
		return mdmNeighborhood;
	}

	public Color[] getCellColors() {
		return cellColors;
	}

	public boolean isColorByPCA() {
		return colorByPCA;
	}

//	public double[][] getClusterLabelFeatures() {
//		return clusterlabelfeatures;
//	}


}
