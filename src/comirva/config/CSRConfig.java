/*
 * Created on 02.12.2004
 */
package comirva.config;

/**
 * This class represents a configuration for a Continuous-Similarity-Ring-Visualization
 * It is used to pass a configuration to the visualization pane.
 * 
 * @author Markus Schedl
 */
public class CSRConfig {
	private int numberOfNeighborsPerPrototype;
	private int[] idxPrototypes;
	private int maxEdgeThickness;
	private int prototypesVertexDiameter;
	private int neighborsVertexDiameter;
	private int iterationsNeighborsPlacement;
	
	/**
	 * Creates a new instance of a CSR-Configuration.
	 * 
	 * @param numberOfNeighborsPerPrototype		the number of neighbors that should be displayed for each prototype
	 * @param idxPrototypes	an int[] containing the prototype indices
	 * @param maxEdgeThickness					the maximum thickness of the edges connecting prototypes and neighbors
	 * @param prototypesVertexDiameter			the diameter of the prototype-vertices
	 * @param neighborsVertexDiameter			the diameter of the neighbor-vertices
	 * @param iterationsNeighborsPlacement		the number of iterations for the vertex-placement-heuristic
	 */
	public CSRConfig(int numberOfNeighborsPerPrototype, int[] idxPrototypes, int maxEdgeThickness, int prototypesVertexDiameter, int neighborsVertexDiameter, int iterationsNeighborsPlacement){
		this.numberOfNeighborsPerPrototype = numberOfNeighborsPerPrototype;
		this.idxPrototypes = idxPrototypes;
		this.maxEdgeThickness = maxEdgeThickness;
		this.prototypesVertexDiameter = prototypesVertexDiameter;
		this.neighborsVertexDiameter = neighborsVertexDiameter;
		this.iterationsNeighborsPlacement = iterationsNeighborsPlacement;
	}
	
	/**
	 * Returns the number of neighboring data items per prototype.
	 * 
	 * @return	the number of neighboring data items for each prototype
	 */
	public int getNumberOfNeighborsPerPrototype() {
		return this.numberOfNeighborsPerPrototype;
	}
	
	/**
	 * Returns an int[] containing the indices of the data items
	 * to be used as prototypes.
	 * 
	 * @return	an int[] containing the prototype indices
	 */
	public int[] getPrototypeIndices() {
		return this.idxPrototypes;
	}

	/**
	 * Returns the maximum thickness for the edges connecting the
	 * prototypes with their neighbors.
	 * 
	 * @return the maximum thickness for edges
	 */
	public int getMaxEdgeThickness() {
		return this.maxEdgeThickness;
	}
	
   	/**
   	 * Returns the vertex diameter for the prototype vertices.
   	 * 
   	 * @return the vertex diameter for the prototype vertices
   	 */
   	public int getPrototypesVertexDiameter() {
   		return this.prototypesVertexDiameter;
   	}
   	
   	/**
   	 * Returns the vertex diameter for the neighbor vertices.
   	 * 
   	 * @return the vertex diameter for the neighbor vertices
   	 */
   	public int getNeighborsVertexDiameter() {
   		return this.neighborsVertexDiameter;
   	}
   	
   	/**
   	 * Returns the number of iterations for the heuristic that positions the vertices of the neighbors
   	 * 
   	 * @return the number of iterations for the heuristic that positions the vertices of the neighbors
   	 */
   	public int getIterationsNeighborsPlacement() {
   		return this.iterationsNeighborsPlacement;
   	}
   	
}
