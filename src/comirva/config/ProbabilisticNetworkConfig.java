/*
 * Created on 11.03.2005
 */
package comirva.config;

/**
 * This class represents a configuration for a Probabilistic-Network-Visualization.
 * It is used to pass a configuration to the visualization pane.
 * 
 * @author Markus Schedl
 */
public class ProbabilisticNetworkConfig {
	private int maxEdgeThickness;
	private int maxDistReduction;
	private int maxVertexDiameter;
	private int minVertexDiameter;
	private double probCorrection;
	private int adaptationRunsEpochs;
	private double adaptationThreshold;
	private int gridSize;
	
	/**
	 * Creates a new instance of a Probabilistic-Network-Configuration.
	 *  
     * @param maxEdgeThickness		the maximum thickness of an edge
	 * @param maxDistReduction		the maximum distance reduction between two vertices in each adaptation iteration
	 * @param maxVertexDiameter		the maximum diameter for the data points (vertices)
	 * @param minVertexDiameter		the minimum diameter for the data points (vertices)
	 * @param probCorrection		the probability correction (PC) for drawing edges; edge(i,j) is drawn if similarity(i,j) > randomValue[0,1]*PC		
	 * @param adaptationRunsEpochs	the number of adaptation iterations in epochs (1 epoch = number of data items ^ 2 runs)
	 * @param adaptationThreshold	adaptation threshold (AT); distance(i,j) is adapted only if similarity(i,j) > AT
	 * @param gridSize				the grid size in pixels; data points are snapped to the grid automatically
	 */
	public ProbabilisticNetworkConfig(int maxEdgeThickness, int maxDistReduction, int maxVertexDiameter, int minVertexDiameter, double probCorrection, int adaptationRunsEpochs, double adaptationThreshold, int gridSize) {
		this.maxEdgeThickness = maxEdgeThickness;
		this.maxDistReduction = maxDistReduction;
		this.maxVertexDiameter = maxVertexDiameter;
		this.minVertexDiameter = minVertexDiameter;
		this.probCorrection = probCorrection;
		this.adaptationRunsEpochs = adaptationRunsEpochs;
		this.adaptationThreshold = adaptationThreshold;
		this.gridSize = gridSize;
	}
	
  	/**
   	 * Returns the maximum thickness for an edge.
   	 * 
   	 * @return the maximum thickness for an edge
   	 */
   	public int getMaxEdgeThickness() {
   		return maxEdgeThickness;
   	}
   	
   	/**
   	 * Returns the maximum distance reduction between two data points in the adaptation process.
   	 * 
   	 * @return the maximum distance reduction
   	 */
   	public int getMaxDistReduction() {
   		return maxDistReduction;
   	}
   	
   	/**
   	 * Returns the maximum vertex diameter for a data point.
   	 * 
   	 * @return the maximum diameter for a vertex
   	 */
   	public int getMaxVertexDiameter() {
   		return maxVertexDiameter;
   	}
   	
   	/**
   	 * Returns the minimum vertex diameter for a data point.
   	 * 
   	 * @return the minimum diameter for a vertex
   	 */
   	public int getMinVertexDiameter() {
   		return minVertexDiameter;
   	}
   	
   	/**
   	 * Returns the probability correction for drawing edges.
   	 * An edge between data point (vertex) <code>i</code> and <code>j</code> is drawn
   	 * with a probability that equals the similarity between <code>i</code> and <code>j</code> multiplied
   	 * with the probability correction.
   	 * 
   	 * @return the probability correction
   	 */
   	public double getProbCorrection() {
   		return probCorrection;
   	}
   	
   	/**
   	 * Returns the number of iterations in epochs the adaptation process is performed.
   	 * One epoch means that, on average, each pair of data items is selected for adaptation once.
   	 * Thus, one epoch means that the adaptation is iterated <code>numberOfDataItems^2</code> times.
   	 * 
   	 * @return the number of epochs the adaptation process is performed
   	 */
   	public int getAdaptationRunsEpochs() {
   		return adaptationRunsEpochs;
   	}
   	
   	/**
   	 * Returns the adaptation threshold.
   	 * The output distance is adapted only for those data items <code>i, j</code> that have a similarity 
   	 * greater than the adaptation threshold.
   	 * 
   	 * @return the adaptation threshold
   	 */
   	public double getAdaptationThreshold() {
   		return adaptationThreshold;
   	}
   	
   	/**
   	 * Returns the grid size used for vertex placement.
   	 * 
   	 * @return the grid size in pixels
   	 */
   	public int getGridSize() {
   		return gridSize;
   	}

}
