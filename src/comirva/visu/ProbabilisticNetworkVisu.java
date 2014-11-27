/*
 * Created on 04.03.2006
 */
package comirva.visu;

import comirva.data.DataMatrix;
import comirva.data.SunburstNode;
import comirva.util.TermProfileUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;
import javax.swing.event.MouseInputAdapter;

/**
 * This class implements a ProbabilisticNetworkVisu visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class ProbabilisticNetworkVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on
	private DataMatrix distMat;					// the distance/similarity matrix 
	
	/**
	 * Constructs a new ProbabilisticNetworkVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public ProbabilisticNetworkVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.distMat = this.vp.distMat;
		initMouseListener();
	}

	/**
	 * inits the mouse listeners that are needed for this visualisation.
	 */
	public void initMouseListener() {
		// initialize MouseListener
		ProbabilisticNetworkVisu_MouseInputAdapter mia = new ProbabilisticNetworkVisu_MouseInputAdapter(this);
		this.vp.addMouseListener(mia);
		this.vp.addMouseMotionListener(mia);
	}
	
	/**
	 * Start drawing the visualization.
	 * Paints a probablistic network visualization based on a random graph (Erd�s-R�nyi).
	 * At first, the data items are positioned randomly on the screen and are shown as
	 * circles with a diameter equal to the similarity to all other data items.
	 * Thus, data items with high similarity to other data items are visualized by bigger circles.
	 * In an adaptation step, then, data points <code>i,j</code> with a "strong" connection,
	 * meaning that their distance is low (their similiarity is high), are moved closer to each other.
	 * Finally, connections between data points are drawn with a probability that equals their similarity
	 * multiplied with a probability correction.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showProbabilisticNetwork();
	}
	
	/**
	 * Paints a probablistic network visualization based on a random graph (Erd�s-R�nyi).
	 * At first, the data items are positioned randomly on the screen and are shown as
	 * circles with a diameter equal to the similarity to all other data items.
	 * Thus, data items with high similarity to other data items are visualized by bigger circles.
	 * In an adaptation step, then, data points <code>i,j</code> with a "strong" connection,
	 * meaning that their distance is low (their similiarity is high), are moved closer to each other.
	 * Finally, connections between data points are drawn with a probability that equals their similarity
	 * multiplied with a probability correction.
	 */
	private void showProbabilisticNetwork() {
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
		// (re)initialize Vector to store position of elements for mouse over detection
		this.vp.moElements = new Vector();
		
		// it is assumed that higher values in the distance matrix
		// represent lower distances between the data items and vice versa
		// -> similarity matrix is assumed
		int numberOfItems = this.distMat.getNumberOfColumns();
		// get labels for data item
		Vector labels_net;
		// test, if labels are specified
		if (this.vp.labels != null) {	// specified
			labels_net = (Vector)this.vp.labels.clone();
			// set label
		} else {				// not specified -> create new label vector with number of data item as values
			labels_net = new Vector();
			for (int i=0; i<numberOfItems; i++) {
				labels_net.add(new String(Integer.toString(i)));
			}
		}
		this.vp.big.setColor(Color.BLACK);					// set drawing color to black
		
		// load the configuration passed from the Probabilistic-Network-Configuration-dialog
		int maxEdgeThickness = this.vp.pnCfg.getMaxEdgeThickness();					// maximum thickness of edges
		int maxDistanceReduction = this.vp.pnCfg.getMaxDistReduction();				// maximum distance reduction
		int maxVertexDiameter = this.vp.pnCfg.getMaxVertexDiameter();				// maximum diameter of data point vertices
		int minVertexDiameter = this.vp.pnCfg.getMinVertexDiameter();				// minimum diameter of data point vertices
		double probCorrection = this.vp.pnCfg.getProbCorrection();					// correction multiplier for probability (edge is drawn if distance > randomValue*probCorrection)
		double adaptationThreshold = this.vp.pnCfg.getAdaptationThreshold();		// adapt (reduce) output distance only for those data items i, j that have a similarity > thresholdAdaptation
		int adaptationRunsEpochs = this.vp.pnCfg.getAdaptationRunsEpochs();			// number of iterations the adaptation process is performed (1 epoch = number of data items squared
		boolean useGridForVertexPlacement = true;							// allow placement of vertices only at defined positions (use a grid)
		int gridSize = this.vp.pnCfg.getGridSize();									// grid size in pixels - default = 1 pixel (i.e. no grid is used)
		int adaptationRuns = adaptationRunsEpochs*numberOfItems*numberOfItems;
		
		// create random positions for data items
		Random rand = new Random();
		Vector dataPoints = new Vector();
		for (int i=0; i<numberOfItems; i++) {
			int posX = 0, posY = 0;
			if (!useGridForVertexPlacement) {		// place data points randomly without using a grid
				posX = borderSize+rand.nextInt(d.width-this.vp.visuPreferences.getBorderSize()*2);
				posY = borderSize+rand.nextInt(d.height-this.vp.visuPreferences.getBorderSize()*2);
			} else {		// place data points only at grid-positions (the size between two grid-elements is maxVertexDiameter
				// calculate number of grid elements
				int numberOfGridElementsX = (d.width-this.vp.visuPreferences.getBorderSize()*2)/gridSize;
				int numberOfGridElementsY = (d.height-this.vp.visuPreferences.getBorderSize()*2)/gridSize;
				posX = borderSize+rand.nextInt(numberOfGridElementsX+1)*gridSize;
				posY = borderSize+rand.nextInt(numberOfGridElementsY+1)*gridSize;
			}
			Point p = new Point(posX, posY);
			// store points in Vector
			dataPoints.addElement(p);
//			draw randomly placed data points
//			Ellipse2D.Double ell = new Ellipse2D.Double(posX-3, posY-3, 6, 6);
//			big.setColor(Color.LIGHT_GRAY);
//			big.fill(ell);
		}
		
		// calculate sum of distances for every data item (to adapt size of vertices)
		Vector sumOfDistances = new Vector();			// Vector to store sum of distances to all other data items for every data item
		double sumDist;
		for (int i=0; i<numberOfItems; i++) {
			sumDist=0;
			// sum up distances for every data item
			for (int j=0; j<numberOfItems; j++) {
				// since distance matrix can be asymmetric, calculate sum in both directions (horizontally and vertically)
				// sum up all columns for row i
				double value = this.distMat.getValueAtPos(i, j).doubleValue();
				if (i!=j)		// ignore self-distance
					sumDist=sumDist+value;
				// sum up all rows for column i
				value = this.distMat.getValueAtPos(j, i).doubleValue();
				if (i!=j)		// ignore self-distance
					sumDist=sumDist+value;
			}
			// sumDist contains the sum of 2*(numberOfItems-1) distances
			sumOfDistances.addElement(new Double(sumDist));
		}
		
		// draw edges
		// adapt distance between similar data items
		for (int i=0; i<adaptationRuns; i++) {
			// randomly select two data items
			int dataItem1Idx = rand.nextInt(numberOfItems);
			int dataItem2Idx = rand.nextInt(numberOfItems);
			double value = this.distMat.getValueAtPos(dataItem1Idx, dataItem2Idx).doubleValue();
			// adapt only connections with similarity exceeding a fixed threshold and eliminate connections from one item to itself
			if ((dataItem1Idx != dataItem2Idx) && (value > adaptationThreshold)) {
				double startX = ((Point)dataPoints.elementAt(dataItem1Idx)).getX();
				double startY = ((Point)dataPoints.elementAt(dataItem1Idx)).getY();
				double endX = ((Point)dataPoints.elementAt(dataItem2Idx)).getX();
				double endY = ((Point)dataPoints.elementAt(dataItem2Idx)).getY();
				double distX = endX-startX;
				double distY = endY-startY;
				double vecXY = distY/distX;
				double vecYX = distX/distY;
				// calculate distance between the two points
				double dist = Math.sqrt(distX*distX+distY*distY);
				// move points closer to each other
				double learnRate = (double)i/adaptationRuns; //1-i/numberOfItems;
				// calc distance reduction
				double distRed = (((value-dist)*(value-dist))/value)*learnRate; ///dist*value*learnRate;
				if (distRed > maxDistanceReduction)
					distRed = maxDistanceReduction;									// reduction diagonal
				double distRedX = Math.sqrt((distRed*distRed)/(1+vecXY*vecXY));	// reduction in x-direction
				double distRedY = Math.abs(distRedX*vecXY);						// reduction in y-direction
				// reduce distance
				if (startX > endX)
					distRedX = distRedX*-1;
				if (startY > endY)
					distRedY = distRedY*-1;
				if (dist > distRed)	{	// only if current distance between data items greater than distance reduction
					// and new position within borders
					if ((startX+distRedX > this.vp.visuPreferences.getBorderSize()+maxVertexDiameter/2) && (startX+distRedX < d.width-this.vp.visuPreferences.getBorderSize()-maxVertexDiameter/2) &&
							(startY+distRedY > this.vp.visuPreferences.getBorderSize()+maxVertexDiameter/2) && (startY+distRedY < d.height-this.vp.visuPreferences.getBorderSize()-maxVertexDiameter/2)) {
						// if data points are to be placed only at grid-positions, calculate valid positions for data points
						if (useGridForVertexPlacement) {
							// calculate nearest possible grid element
							// get distance to nearest left grid element
							int relativePosToLeftGrid = (((int)(startX+distRedX)-this.vp.visuPreferences.getBorderSize()) % gridSize);
							// get distance to nearest upper grid element
							int relativePosToUpperGrid = (((int)(startY+distRedY)-this.vp.visuPreferences.getBorderSize()) % gridSize);
							// adapt x-coordinate
							if (relativePosToLeftGrid < (int)gridSize/2) 	// nearest grid element is to the left in horizontal direction
								distRedX=distRedX-relativePosToLeftGrid;
							else											// nearest grid element is to the right in horizontal direction
								distRedX=distRedX+(gridSize-relativePosToLeftGrid);
							// adapt y-coordinate
							if (relativePosToUpperGrid < (int)gridSize/2) 	// nearest grid element is to the upper in horizontal direction
								distRedY=distRedY-relativePosToUpperGrid;
							else											// nearest grid element is to the lower in horizontal direction
								distRedY=distRedY+(gridSize-relativePosToUpperGrid);
						}
						// set adapted positions of data points in the dataPoints-Vector
						((Point)dataPoints.elementAt(dataItem1Idx)).setLocation(startX+distRedX, startY+distRedY);
					}
				}
			}
		}
		
		// read distance matrix and draw connections
		for (int i=0; i<numberOfItems; i++) {
			// get connections
			for (int j=0; j<numberOfItems; j++) {
				double value = this.distMat.getValueAtPos(i, j).doubleValue();
				// draw connection between data item i and j with probability p that equals the similarity between i and j
				if ((i != j) && (value > rand.nextDouble()*probCorrection)) {
					// draw connections
					Color c = this.vp.cm.getColor(value);
					this.vp.big.setColor(c);
					if (this.vp.eps != null) this.vp.eps.setColor(c);
					int strokeWidth = Math.min(Math.max(1, (int)(maxEdgeThickness*value)), maxEdgeThickness);	// make sure that stroke width is positive but does not exceed maxEdgeThickness
					BasicStroke stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
					this.vp.big.setStroke(stroke);
					if (this.vp.eps != null) this.vp.eps.setStroke(stroke);
					// calculate start and end points for connections
					double startX = ((Point)dataPoints.elementAt(i)).getX();
					double startY = ((Point)dataPoints.elementAt(i)).getY();
					double endX = ((Point)dataPoints.elementAt(j)).getX();
					double endY = ((Point)dataPoints.elementAt(j)).getY();
					// draw line
					this.vp.big.draw(new Line2D.Double(startX, startY, endX, endY));
					if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(startX, startY, endX, endY));
				}
			}
		}
		
		// normalize sum of distances to [0,1]
		// find maximum element
		double maxValue = 0;
		for (int i=0; i<sumOfDistances.size(); i++)
			maxValue = Math.max(maxValue, ((Double)sumOfDistances.elementAt(i)).doubleValue());
		// write normalized values back to Vector
		for (int i=0; i<sumOfDistances.size(); i++)
			sumOfDistances.setElementAt(new Double(((Double)sumOfDistances.elementAt(i)).doubleValue()/maxValue), i);
		
		// draw vertices (data points)
		for (int i=0; i<numberOfItems; i++) {
			double posX = ((Point)dataPoints.elementAt(i)).getX();
			double posY = ((Point)dataPoints.elementAt(i)).getY();
			// set size according to normalized sum of distances and config variables
			double vertexDiameter = minVertexDiameter+(maxVertexDiameter-minVertexDiameter)*((Double)sumOfDistances.elementAt(i)).doubleValue();
			// draw vertices
			Ellipse2D.Double ell = new Ellipse2D.Double(posX-vertexDiameter/2, posY-vertexDiameter/2, vertexDiameter, vertexDiameter);
			this.vp.big.setColor(Color.ORANGE);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.ORANGE);
			this.vp.big.fill(ell);
			if (this.vp.eps != null) this.vp.eps.fill(ell);
			// draw border around data points
			this.vp.big.setColor(Color.GRAY);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
			BasicStroke stroke = new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			this.vp.big.setStroke(stroke);
			if (this.vp.eps != null) this.vp.eps.setStroke(stroke);
			this.vp.big.draw(new Ellipse2D.Double(posX-vertexDiameter/2, posY-vertexDiameter/2, vertexDiameter, vertexDiameter));
			if (this.vp.eps != null) this.vp.eps.draw(new Ellipse2D.Double(posX-vertexDiameter/2, posY-vertexDiameter/2, vertexDiameter, vertexDiameter));
			// add point to Vector of mouse event elements
			this.vp.moElements.addElement(ell);
			this.vp.moElements.addElement(labels_net.elementAt(i));
		}
		
		// user info for using the mouse
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
		this.vp.big.setFont(labelFont);
		this.vp.big.setColor(Color.BLACK);
		this.vp.big.drawString("Move mouse over any vertex to show the assigned label.", 7, (int)(d.height - 7));
		// no user info in eps file
		
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
	}
	
	/**
	 * This class defines a MouseInputAdapter for the Probabilistic Network visualization.	 *
	 * @author Markus Schedl
	 */
	private class ProbabilisticNetworkVisu_MouseInputAdapter extends MouseInputAdapter {
		ProbabilisticNetworkVisu adaptee;
		Rectangle2D updateRegion = new Rectangle2D.Double();				// region that needs to be repainted after a popup-box has been drawn
		
		/**
		 * Constructs a new instance of ProbabilisticNetworkVisu_MouseInputAdapter.
		 *
		 * @param adaptee	the ProbabilisticNetworkVisu that uses this MouseInputAdapter
		 */
		ProbabilisticNetworkVisu_MouseInputAdapter(ProbabilisticNetworkVisu adaptee) {
			this.adaptee = adaptee;
		}
		
		/**
		 * Tests, if the mouse is moved over a clickable object.
		 * If this is the case, the mouse cursor is changed.
		 *
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
			this.processMouseEvent(e);
		}
		
		/**
		 * Tests, if the mouse is moved over a clickable object and
		 * this object is clicked.
		 *
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			this.processMouseEvent(e);
		}
		
		public void mousePressed(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		
		/**
		 * Processes the mouse input according to the visualization type.
		 *
		 * @param e	the MouseEvent
		 */
		private void processMouseEvent(MouseEvent e) {
			Graphics2D g = (Graphics2D)adaptee.vp.getGraphics();		// Graphics2D-instance of visualization area
			boolean isInsideClickable = false;						// flag, indicating whether mouse was moved into a clickable region
			for (int i=0; i<adaptee.vp.moElements.size(); i++) {
				// for "Network"-visu only every second element of moElements is a Rectangle (test, if i mod 2 = 0)
				// mouse inside a clickable object?
				if ((i%2 == 0) && ((Shape)adaptee.vp.moElements.elementAt(i)).contains(e.getX(), e.getY())) {
					isInsideClickable = true;
					// read index of element in distance matrix (next element in moElements)
					String label = ((String)adaptee.vp.moElements.elementAt(i+1));
					// get the width of the label on the screen
					Font labelFont = new Font("SansSerif", Font.PLAIN, this.adaptee.vp.visuPreferences.getLabelFontSize());
					g.setFont(labelFont);
					TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
					// get bounds of label text
					Rectangle2D bounds = layout.getBounds();
					// draw directly to graphics and not to buffered image
					g.drawString(label, (int)((Ellipse2D)adaptee.vp.moElements.elementAt(i)).getX(), (int)((Ellipse2D)adaptee.vp.moElements.elementAt(i)).getY());
					// store changed region
					updateRegion = new Rectangle2D.Double((int)((Ellipse2D)adaptee.vp.moElements.elementAt(i)).getX(), (int)((Ellipse2D)adaptee.vp.moElements.elementAt(i)).getY(), bounds.getWidth(), bounds.getHeight());
				}
			}	
			// mouse over a clickable object?
			if (isInsideClickable)		// yes -> change cursor to Hand
				adaptee.vp.setCursor(new Cursor(Cursor.HAND_CURSOR));
			else {						// no -> change cursor to Default
				adaptee.vp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				// repaint image to hide the label (but only the part of the image where the label was painted)
				adaptee.vp.repaint((int)(updateRegion.getX()-updateRegion.getWidth()), (int)(updateRegion.getY()-updateRegion.getHeight()*2), (int)updateRegion.getWidth()*3, (int)updateRegion.getHeight()*3);
			}
		}
	}
	
}
