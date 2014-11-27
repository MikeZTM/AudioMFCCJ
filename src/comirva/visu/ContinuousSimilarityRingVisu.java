/*
 * Created on 03.03.2006
 */
package comirva.visu;

import comirva.data.DataMatrix;
import comirva.util.TSP;
import comirva.util.VectorSort;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * This class implements a ContinuousSimilarityRing visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class ContinuousSimilarityRingVisu extends Thread {
	private VisuPane vp;							// the visualization pane to draw on
	private DataMatrix distMat;						// the distance/similarity matrix 
	
	/**
	 * Constructs a new ContinuousSimilarityRingVisu.
	 */
	public ContinuousSimilarityRingVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.distMat = this.vp.distMat;
	}
	
	/**
	 * Start drawing the visualization.
	 * Paints a continuous similarity ring (CSR) visualization.
	 * Prototypes are arranged along a circle by a TSP. Then, a fixed
	 * number k of nearest neighbors according to the similarity matrix is
	 * selected for each prototype. Those neighbors which only have one prototype
	 * are mapped to the outer-circle-area, those which are neighbors of more
	 * than one prototype are mapped and displayed inside the circle.
	 * The inner-circle-neighbors are placed by a simple heuristical
	 * algorithm that tries to preserve the original distances.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showCSR();
	}
	
	/**
	 * Paints a continuous similarity ring (CSR) visualization.
	 * Prototypes are arranged along a circle by a TSP. Then, a fixed
	 * number k of nearest neighbors according to the similarity matrix is
	 * selected for each prototype. Those neighbors which only have one prototype
	 * are mapped to the outer-circle-area, those which are neighbors of more
	 * than one prototype are mapped and displayed inside the circle.
	 * The inner-circle-neighbors are placed by a simple heuristical
	 * algorithm that tries to preserve the original distances.
	 */
	private void showCSR() {
		// get 2D-graphics
		Graphics2D g = (Graphics2D)this.vp.getGraphics();
		// get current size of visualization pane
		Dimension d = this.vp.getSize();
		// create new BufferedImage for buffering the visualization area
		// drawing is effected to the Canvas as well as to the BufferedImage for the first time
		// further times, the image is simply reloaded from the BufferedImage
		this.vp.bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		this.vp.bi = (BufferedImage)this.vp.createImage(d.width, d.height);
		this.vp.big = this.vp.bi.createGraphics();
		if (this.vp.eps != null) { this.vp.eps.create(); this.vp.eps.dispose(); }		// clear eps objects list	
		// clear drawing area
		g.clearRect(0, 0, this.vp.getSize().width, this.vp.getSize().height);
		// size of the border around the complete visualization area
		int borderSize = this.vp.visuPreferences.getBorderSize();
		
		// font for drawing labels
		Font labelFont = new Font("SansSerif", Font.BOLD, this.vp.visuPreferences.getLabelFontSize());
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		
		// get configuration from CSRConfig-dialog
		int[] idxPrototypes = this.vp.csrCfg.getPrototypeIndices();
		int maxEdgeThickness = this.vp.csrCfg.getMaxEdgeThickness();						// maximum thickness of edges
		double vertexDiameterPrototypes = this.vp.csrCfg.getPrototypesVertexDiameter();		// diameter of prototype circles
		double vertexDiameterNeighbors = this.vp.csrCfg.getNeighborsVertexDiameter();		// diameter of neighboring data items circles
		
		// use TSP to arrange the prototypes according to their similarity
		DataMatrix dmPrototypes = new DataMatrix();
		for (int i=0; i<idxPrototypes.length; i++) {
			for (int j=0; j<idxPrototypes.length; j++) {
				dmPrototypes.addValue(new Double(1-this.distMat.getValueAtPos(idxPrototypes[i],idxPrototypes[j]).doubleValue()));
			}
			dmPrototypes.startNewRow();
		}
		dmPrototypes.removeLastAddedElement();
		TSP tsp = new TSP(dmPrototypes);
		int[] tour = tsp.startIterations(20000);		// tour contains the indices of the found tour with (hopefully) minimal distance
		// rearrage idxPrototypes
		int[] newIdxPrototypes = new int[idxPrototypes.length];
		for (int i=0; i<idxPrototypes.length; i++) {
			newIdxPrototypes[i] = idxPrototypes[tour[i]];
		}
		idxPrototypes = newIdxPrototypes;
		
		// positions of the prototypes
		Point2D.Double[] posPrototypes = new Point2D.Double[idxPrototypes.length];
		
		int radius = (int)(Math.min(d.width, d.height)/3);				// radius of CSR-circle
		double angleInterval = 360/(double)idxPrototypes.length;		// angle between two prototype circles
		
		// calculate position of points for prototypes
		for (int i=0; i<idxPrototypes.length; i++) {
			double posX = d.width/2 + Math.cos(Math.toRadians(i*angleInterval))*radius;
			double posY = d.height/2  + Math.sin(Math.toRadians(i*angleInterval))*radius;
			// store screen position of current prototype
			posPrototypes[i] = new Point2D.Double(posX, posY);
		}
		
		// get neighbors for each prototype
		Vector[] neighbors = new Vector[idxPrototypes.length];				// Vector-Array to store neighbors of each prototype
		Vector[] neighborsSimilarity = new Vector[idxPrototypes.length];	// Vector-Array to store similarity of neighbors to prototype
		Vector[] singleNeighbors = new Vector[idxPrototypes.length];		// Vector-Array to store the data items which neighbors only one prototype
		for (int i=0; i<idxPrototypes.length; i++) {
			// init neighbors-Vector[]
			neighbors[i] = new Vector();
			neighborsSimilarity[i] = new Vector();
			singleNeighbors[i] = new Vector();
			// get similarities of current prototype
			Vector neighborsSim = (Vector)this.distMat.getRow(idxPrototypes[i]).clone();
			Vector neighborsLabels = (Vector)this.vp.labels.clone();
			// sort neighbors of current prototype wrt similarities
			VectorSort.sortWithMetaData(neighborsSim, neighborsLabels);
			// get most similar data items to current prototype
			for (int j=0; j<this.vp.csrCfg.getNumberOfNeighborsPerPrototype(); j++) {
				// generally, the 1st data item (the nearest neighbor to the current prototype) is not
				// included in the neighbor-Vector because usually it is the prototype itself
				// but due to errors (e.g. in the co-occurrence analysis) this assumption can be
				// false and hence the prototype could appear as neighbor of itself
				// to avoid this problem, the labels of neighbors and prototypes are compared and
				// if they equal, the 1st data item is taken instead of the prototype itself
				if ((((String)neighborsLabels.elementAt(j+1)).compareTo((String)this.vp.labels.elementAt(idxPrototypes[i]))) != 0) {		// if neighbor is not prototype itself, add to neighbors-Vector
					if (((Double)neighborsSim.elementAt(j+1)).doubleValue() > 0) { 		// only if similarity > 0, add neighbor to neighbors-Vector
						neighbors[i].addElement(neighborsLabels.elementAt(j+1));		// j+1 because 1st element is current prototype itself
						neighborsSimilarity[i].addElement(neighborsSim.elementAt(j+1));
					}
				} else {	// if neighbor is prototype itself, use first data item instead (which is obviously not the data item itself)
					if (((Double)neighborsSim.elementAt(0)).doubleValue() > 0) { 		// only if similarity > 0, add neighbor to neighbors-Vector
						neighbors[i].addElement(neighborsLabels.elementAt(0));
						neighborsSimilarity[i].addElement(neighborsSim.elementAt(0));
					}
				}
			}
		}
		
		// get single and combined neighbors of each prototype
		Hashtable combinedNeighbors = new Hashtable();			// hashtable to store all prototypes for each neighbor that has more than 1 prototype
		Hashtable combinedNeighborsSimilarity = new Hashtable();// hashtable to store the similarity values for each neighbor to its prototypes
		for (int i=0; i<idxPrototypes.length; i++) {			// for all prototypes
			for (int j=0; j<neighbors[i].size(); j++) {			// for all neighboring data items
				String curLabel = (String)neighbors[i].elementAt(j);
				// test for unambiguousness (compare with all following prototype's neighbors starting at i+1
				for (int k=i+1; k<idxPrototypes.length; k++) {
					for (int l=0; l<neighbors[k].size(); l++) {
						if (curLabel.compareTo((String)neighbors[k].elementAt(l)) == 0) {
							// look in combinedNeighbors-Hashtable if prototype entries for the data item have already been stored...
							if (!combinedNeighbors.containsKey(curLabel)) {						// ...no: first prototype to be stored in combinedNeighbors
								combinedNeighbors.put(curLabel, new Vector());					// enter new neighbor as key
								combinedNeighborsSimilarity.put(curLabel, new Vector());
							}
							// enter prototypes as values in Vector belonging to that key (but only if they do not already exist)
							if (!((Vector)combinedNeighbors.get(curLabel)).contains(Integer.toString(i))) {
								((Vector)combinedNeighbors.get(curLabel)).addElement(new Integer(i));
								((Vector)combinedNeighborsSimilarity.get(curLabel)).addElement(neighborsSimilarity[i].elementAt(j));
							}
							if (!((Vector)combinedNeighbors.get(curLabel)).contains(Integer.toString(k))) {
								((Vector)combinedNeighbors.get(curLabel)).addElement(new Integer(k));
								((Vector)combinedNeighborsSimilarity.get(curLabel)).addElement(neighborsSimilarity[k].elementAt(l));
							}
						}
					}
				}
				// current data item is single neighbor -> store in single neighbor Vector
				if (!combinedNeighbors.containsKey(curLabel)) {
					singleNeighbors[i].addElement(neighbors[i].elementAt(j));
				}
			}
		}
//		System.out.println(combinedNeighbors.toString());
		
		// draw neighbors with only one prototype
		for (int i=0; i<idxPrototypes.length; i++) {
			Vector labels = singleNeighbors[i];
			// some configuration
			int numberOfItems = labels.size();		     				// number of single neighbors for current prototype
			double angleExtent = Math.min(360, angleInterval*2); 		// the angular extent of the outer-circle-prototypes
			double angleDegrees = angleExtent/(double)numberOfItems	;	// angle between two lines
			double angleStart = i*angleInterval-angleExtent/2;			// angle for first line
			double angleEnd = i*angleInterval+angleExtent/2;			// angle for last line
			int barLength = (int)(Math.min(d.width, d.height)/7);		// length of outer-circle-connections
			Point2D.Double center = posPrototypes[i];					// position of the prototype (starting point for outer-circle-connections
			// determine the angle between two bars
			double angleInt = (double)Math.abs(angleEnd-angleStart)/(double)numberOfItems;
			// set font for drawing labels
			this.vp.big.setFont(labelFont);
			// draw connections
			for (int j=0; j<numberOfItems; j++) {
				// get similarity value
				Double value = (Double)neighborsSimilarity[i].elementAt(neighbors[i].indexOf(labels.elementAt(j)));
				// draw connections
				this.vp.big.setColor(this.vp.cm.getColor(value.doubleValue()));
				if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(value.doubleValue()));
				// calculate end points for connections
				double endPointX = center.getX() + Math.cos(Math.toRadians(angleStart+j*angleInt))*barLength;
				double endPointY = center.getY() + Math.sin(Math.toRadians(angleStart+j*angleInt))*barLength;
				// set thickness of connection according to similarity value
				int strokeWidth = Math.min(maxEdgeThickness, Math.max(1, (int)(maxEdgeThickness*value.doubleValue())));	// make sure that stroke width is positive but does not exceed maxEdgeThickness
				this.vp.big.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				// draw connection
				this.vp.big.draw(new Line2D.Double(center.getX(),
						center.getY(),
						endPointX,
						endPointY
				));
				if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(center.getX(),
						center.getY(),
						endPointX,
						endPointY
				));
				// draw circle for data item
				Ellipse2D.Double ell = new Ellipse2D.Double(endPointX-vertexDiameterNeighbors/2, endPointY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors);
				this.vp.big.fill(ell);
				if (this.vp.eps != null) this.vp.eps.fill(ell);
				// draw border around data item
				this.vp.big.setColor(Color.GRAY);
				if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
				this.vp.big.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				this.vp.big.draw(new Ellipse2D.Double(endPointX-vertexDiameterNeighbors/2, endPointY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors));
				if (this.vp.eps != null) this.vp.eps.draw(new Ellipse2D.Double(endPointX-vertexDiameterNeighbors/2, endPointY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors));
				this.vp.big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				// draw label
				// get label name
				String label = labels.elementAt(j).toString();
				// get the width of label on the screen
				TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
				// get bounds of label text
				Rectangle2D bounds = layout.getBounds();
				// set color according to similarity
				this.vp.big.setColor(this.vp.cm.getColor(value.doubleValue()));
				if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(value.doubleValue()));
				// draw label of center element
				this.vp.big.drawString(label, (int)(endPointX - bounds.getWidth()/2 - vertexDiameterNeighbors/2 + 4), (int)(endPointY + bounds.getHeight()/2 + vertexDiameterNeighbors/2 + 10));
				if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(endPointX - bounds.getWidth()/2 - vertexDiameterNeighbors/2 + 4), (int)(endPointY + bounds.getHeight()/2 + vertexDiameterNeighbors/2 + 10));
			}
		}
		
		// draw neighbors with combined prototypes
		Enumeration enumMultiplePrototypeNeighbors = combinedNeighbors.keys();				// enumeration over all combined neighbors
		while (enumMultiplePrototypeNeighbors.hasMoreElements()) {
			// get label name of current neighbor
			String label = (String)enumMultiplePrototypeNeighbors.nextElement();
			// get Vector containing all prototypes for current neighboring data item
			Vector curNeighbor = (Vector)combinedNeighbors.get(label);
			// get Vector containing the similarities
			Vector curSimilarities = (Vector)combinedNeighborsSimilarity.get(label);
			// only proceed, if at least two prototypes of current neighbor were specifie
			if (curNeighbor.size() >= 2) {
				Point2D.Double[] point = new Point2D.Double[curNeighbor.size()];		// positions of prototypes
				double[] origSim = new double[curNeighbor.size()];						// similarities between current neighbor and prototypes
				double origDistSum = curNeighbor.size();								// to store sum of original distances from similarity matrix, initialize with number of prototypes
				double screenDistSum;													// to store sum of screen distances
				double cost;															// to store costs for iterative cost-minimizing algorithm
				double maxSim = 0;														// maximum similarity (for normalization)
				double normalizeCorrectionRange = 0.7;									// normalization is performed to interval [0, normalizeCorrectionRange]
				// get positions and similarities to neighbor of all prototypes to be connected with current neighbor
				for (int i=0; i<curNeighbor.size(); i++) {
					point[i] = posPrototypes[((Integer)curNeighbor.elementAt(i)).intValue()];
					origSim[i] = ((Double)curSimilarities.elementAt(i)).doubleValue();
					if (origSim[i] > maxSim)
						maxSim = origSim[i];
				}
				// normalize similarities by dividing all of them by maximum value
				for (int i=0; i<curNeighbor.size(); i++) {
					origSim[i] = (origSim[i] / maxSim) * normalizeCorrectionRange; 		// correct normalization, otherwise the neighbor would be positioned directly next to the most similar prototype
					// subtract actual similarity from sum of distances
					origDistSum = origDistSum-origSim[i];
				}
				
				// simple iterative algorithm for vertex placement
				// vertex for data item is initialized at center position
				// difference between distances from similarity matrix and screen distances
				// is taken as cost function and tried to be minimized by randomly moving the vertex
				Random rand = new Random();				// initialize random object
				Point2D.Double vertex = new Point2D.Double(d.getWidth()/2, d.getHeight()/2);
				int iterations = this.vp.csrCfg.getIterationsNeighborsPlacement();
				int maxMove = 10;				// maximum movement of x- and y-coordinates of vertex
				// calculate current sum of screen distances and current costs
				screenDistSum = 0;
				cost = 0;
				for (int i=0; i<curNeighbor.size(); i++)
					screenDistSum = screenDistSum + vertex.distance(point[i]);
				for (int i=0; i<curNeighbor.size(); i++)
					cost = cost + Math.abs(((1-origSim[i])/origDistSum)-(vertex.distance(point[i])/screenDistSum));
				double costTolerance = cost/25;							// tolerance for costs for the benefit of screen distance minimization
				// perform iterative cost-minimizing algorithm
				for (int i=0; i<iterations; i++) {
					maxMove=(int)(100-(i/(iterations/100)));			// "learning rate" -> decreasing modification in movement
					// move the vertex according to random values
					int randMoveX = rand.nextInt(maxMove*2+1)-maxMove;
					int randMoveY = rand.nextInt(maxMove*2+1)-maxMove;
					// create new point with moved position
					Point2D.Double movedVertex = new Point2D.Double(vertex.getX()+randMoveX, vertex.getY()+randMoveY);
					// calculate new sum of screen distances and costs
					double movedScreenDistSum = 0;
					double movedCost = 0;
					for (int j=0; j<curNeighbor.size(); j++)
						movedScreenDistSum = movedScreenDistSum + movedVertex.distance(point[j]);
					for (int j=0; j<curNeighbor.size(); j++)
						movedCost = movedCost + Math.abs(((1-origSim[j])/origDistSum)-(movedVertex.distance(point[j])/screenDistSum));
					// if new vertex position has better cost than old one and new sum of screen distances is better than old one, use new
					if (movedCost < cost+costTolerance && movedScreenDistSum <= screenDistSum) {
						// set new position for vertex
						vertex.setLocation(vertex.getX()+randMoveX, vertex.getY()+randMoveY);
						cost = movedCost;
						screenDistSum = movedScreenDistSum;
//						System.out.println("costs: "+cost+"\tscreen distance: "+movedScreenDistSum+"\titeration: "+i);
					}
				}
//				System.out.println("\n");
				
				// set color according to color map and
				// draw connection lines
				for (int i=0; i<curNeighbor.size(); i++) {
					// set thickness of connection according to similarity value
					int strokeWidth = Math.min(maxEdgeThickness, Math.max(1, (int)(maxEdgeThickness*origSim[i])));	// make sure that stroke width is positive but does not exceed maxEdgeThickness
					this.vp.big.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					// draw
					this.vp.big.setColor(this.vp.cm.getColor(origSim[i]));
					if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(origSim[i]));
					this.vp.big.draw(new Line2D.Double((Point2D)point[i], (Point2D)vertex));
					if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double((Point2D)point[i], (Point2D)vertex));
				}
				// draw vertex
				double posX = vertex.x;
				double posY = vertex.y;
				// draw circle
				Ellipse2D.Double ell = new Ellipse2D.Double(posX-vertexDiameterNeighbors/2, posY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors);
				// color of neighbor is mean of similarities to adjacent prototypes
				this.vp.big.setColor(this.vp.cm.getColor((curNeighbor.size()-origDistSum)/curNeighbor.size()));
				if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor((curNeighbor.size()-origDistSum)/curNeighbor.size()));
				this.vp.big.fill(ell);
				if (this.vp.eps != null) this.vp.eps.fill(ell);
				// draw border around data item
				this.vp.big.setColor(Color.GRAY);
				if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
				this.vp.big.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				this.vp.big.draw(new Ellipse2D.Double(posX-vertexDiameterNeighbors/2, posY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors));
				if (this.vp.eps != null) this.vp.eps.draw(new Ellipse2D.Double(posX-vertexDiameterNeighbors/2, posY-vertexDiameterNeighbors/2, vertexDiameterNeighbors, vertexDiameterNeighbors));
				this.vp.big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				// draw label
				// get the width of label on the screen
				TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
				// get bounds of label text
				Rectangle2D bounds = layout.getBounds();
				// color of neighbor is mean of similarities to adjacent prototypes
				this.vp.big.setColor(this.vp.cm.getColor((curNeighbor.size()-origDistSum)/curNeighbor.size()));
				if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor((curNeighbor.size()-origDistSum)/curNeighbor.size()));
				// calc position for and draw label
				this.vp.big.drawString(label, (int)(vertex.x - vertexDiameterNeighbors/2 - bounds.getWidth()/2 + 4), (int)(vertex.y + vertexDiameterNeighbors/2 + bounds.getHeight()/2 + 10));
				if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(vertex.x - vertexDiameterNeighbors/2 - bounds.getWidth()/2 + 4), (int)(vertex.y + vertexDiameterNeighbors/2 + bounds.getHeight()/2 + 10));
			}
		}
		
		// draw and label prototypes
		for (int i=0; i<idxPrototypes.length; i++) {
			// get position of prototypes
			double posX = posPrototypes[i].x;
			double posY = posPrototypes[i].y;
			int textBoxBorder = 3;					// border for the box around prototype labels
			// draw circle
			Ellipse2D.Double ell = new Ellipse2D.Double(posX-vertexDiameterPrototypes/2, posY-vertexDiameterPrototypes/2, vertexDiameterPrototypes, vertexDiameterPrototypes);
			this.vp.big.setColor(Color.WHITE);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.WHITE);
//			big.setColor(Color.ORANGE);
			this.vp.big.fill(ell);
			if (this.vp.eps != null) this.vp.eps.fill(ell);
			// draw border around data item
			this.vp.big.setColor(Color.GRAY);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
			this.vp.big.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			this.vp.big.draw(new Ellipse2D.Double(posX-vertexDiameterPrototypes/2, posY-vertexDiameterPrototypes/2, vertexDiameterPrototypes, vertexDiameterPrototypes));
			if (this.vp.eps != null) this.vp.eps.draw(new Ellipse2D.Double(posX-vertexDiameterPrototypes/2, posY-vertexDiameterPrototypes/2, vertexDiameterPrototypes, vertexDiameterPrototypes));
			this.vp.big.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			// get current label name
			String label = this.vp.labels.elementAt(idxPrototypes[i]).toString();
			// get the width of label on the screen
			TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
			// get bounds of label text
			Rectangle2D bounds = layout.getBounds();
			// draw rectangle as background for label
			this.vp.big.setColor(Color.GRAY);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
			Rectangle2D.Double rect_label = new Rectangle2D.Double((int)(posX - vertexDiameterPrototypes/2 - bounds.getWidth()/2 - textBoxBorder + 4), (int)(posY + vertexDiameterPrototypes/2 - bounds.getHeight()/2 - textBoxBorder + 10), bounds.getWidth() + 2*textBoxBorder, bounds.getHeight() + 2*textBoxBorder);
			this.vp.big.fill(rect_label);
			if (this.vp.eps != null) this.vp.eps.fill(rect_label);
			// draw label
			this.vp.big.setColor(Color.WHITE);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.WHITE);
//			big.setColor(Color.ORANGE);
			this.vp.big.drawString(label, (int)(posX - vertexDiameterPrototypes/2 - bounds.getWidth()/2 + 4), (int)(posY + vertexDiameterPrototypes/2 + bounds.getHeight()/2 + 10));
			if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(posX - vertexDiameterPrototypes/2 - bounds.getWidth()/2 + 4), (int)(posY + vertexDiameterPrototypes/2 + bounds.getHeight()/2 + 10));
		}
		
		
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
	}
	
}
