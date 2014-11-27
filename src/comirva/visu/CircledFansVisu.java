/*
 * Created on 03.03.2006
 */
package comirva.visu;

import comirva.data.DataMatrix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;


/**
 * This class implements a CircledFans visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class CircledFansVisu extends Thread {
	private VisuPane vp;							// the visualization pane to draw on
	private DataMatrix distMat;						// the distance/similarity matrix 
	
	/**
	 * Constructs a new CircledFansVisu.
	 */
	public CircledFansVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.distMat = this.vp.distMat;
		initMouseListener();
	}

	/**
	 * 
	 */
	public void initMouseListener() {
		// initialize MouseListener
		CircledFansVisu_MouseInputAdapter mia = new CircledFansVisu_MouseInputAdapter(this);
		this.vp.addMouseListener(mia);
		this.vp.addMouseMotionListener(mia);
	}
		
	/**
	 * Start drawing the visualization.
	 * Paints a circled fans visualization for distance/similarity matrices.
	 * This visualization uses an inner circle with bars of fixed length but
	 * variable thickness. Starting at the end points of this inner circle (360� fan),
	 * fans with smaller angular extent form a second hierarchy level.<br>
	 * If no labels are specified, the number of the data item is shown
	 * instead of the lables. If the user has selected a meta-data vector
	 * of the same dimensionality as the distance vector,
	 * its contents is taken to represent the labels.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showCircledFans();
	}
	
	/**
	 * Paints a circled fans visualization for distance/similarity matrices.
	 * This visualization uses an inner circle with bars of fixed length but
	 * variable thickness. Starting at the end points of this inner circle (360� fan),
	 * fans with smaller angular extent form a second hierarchy level.<br>
	 * If no labels are specified, the number of the data item is shown
	 * instead of the lables. If the user has selected a meta-data vector
	 * of the same dimensionality as the distance vector,
	 * its contents is taken to represent the labels.
	 */
	private void showCircledFans() {
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
		// get index of data item in the center of the visualization
		int idx_center = this.vp.cfCfg.getIndexCenter();
		// get Vector containing data of center element
		Vector data_center = (Vector)this.distMat.getRow(idx_center).clone();
		// get labels for center element
		Vector labels_center;
		// Vector for storing indices of taken data items
		Vector indices_center = new Vector();
		for (int i=0; i<numberOfItems; i++) {
			indices_center.add(new Integer(i));
		}
		// test, if labels are specified
		if (this.vp.labels != null) {	// specified
			labels_center = (Vector)this.vp.labels.clone();
			// set label for center element
		} else {				// not specified -> create new label vector with number of data item as values
			labels_center = new Vector();
			for (int i=0; i<numberOfItems; i++) {
				labels_center.add(new String(Integer.toString(i)));
			}
		}
		// get label for centered element (the randomly chosen one)
		String label_center = (String)labels_center.elementAt(idx_center);
		// eliminate the chosen data item from distance matrix (distance to itself is always 1.0 -> gives no new information)
		data_center.remove(idx_center);
		labels_center.remove(idx_center);
		indices_center.remove(idx_center);
		numberOfItems--;
		// perform reduction of data set (selection)
		// first, eliminate all zero-value entries
		for (int i=0; i<numberOfItems; i++) {
			Double curValue = (Double)data_center.elementAt(i);
			if (curValue.doubleValue() == 0) {
				data_center.remove(i);
				labels_center.remove(i);
				indices_center.remove(i);
				// update number of data items
				numberOfItems -= 1;
			}
		}
		// second, retain only the topmost MAXIMUM_DATA_ITEMS items
		// therefore, successively remove the data_center.size - cfCfg.getMaxDataItemsL0() items
		// containing the lowest values
		while (numberOfItems > this.vp.cfCfg.getMaxDataItemsL0()) {
			// get index of minimum data item
			int idx_minDataItem = data_center.indexOf(Collections.min(data_center));
			// remove this data item from data and label vector
			data_center.remove(idx_minDataItem);
			labels_center.remove(idx_minDataItem);
			indices_center.remove(idx_minDataItem);
			// update number of data items
			numberOfItems -= 1;
		}
		// normalize remaining matrix, if this should be done
		Vector data_centerNormalized = new Vector();
		if (this.vp.cfCfg.isNormalizeData()) {
			// get maximum value
			double maxValue = ((Double)Collections.max(data_center)).doubleValue();
			// divide all vector elements by maximum value and insert in new Vector
			for (int i=0; i<numberOfItems; i++) {
				Double curValue = (Double)data_center.elementAt(i);
				data_centerNormalized.add(new Double(curValue.doubleValue()/maxValue));
			}
		} else {
			data_centerNormalized = data_center;
		}
		
		// create Font for drawing labels
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
		int textBoxBorder = 3;		// border size for box around labels
		
		// visualize level 1
		// this is done before level 0 in order to prevent labels of centered
		// data set from being hidden by bars of level 1 connections
		for (int j=0; j<data_centerNormalized.size(); j++) {
			// refresh number of data items
			numberOfItems = this.distMat.getNumberOfColumns();
			// get index of current item
			int idx_l1 = ((Integer)indices_center.elementAt(j)).intValue();
			// get Vector containing data of current item
			Vector data_l1 = (Vector)this.distMat.getRow(idx_l1).clone();
			// get labels for data item
			Vector labels_l1;
			// Vector for storing indices of taken data items
			Vector indices_l1 = new Vector();
			for (int i=0; i<numberOfItems; i++) {
				indices_l1.add(new Integer(i));
			}
			// test, if labels are specified
			if (this.vp.labels != null) {	// specified
				labels_l1 = (Vector)this.vp.labels.clone();
				// set label
			} else {				// not specified -> create new label vector with number of data item as values
				labels_l1 = new Vector();
				for (int i=0; i<numberOfItems; i++) {
					labels_l1.add(new String(Integer.toString(i)));
				}
			}
			// eliminate the chosen data item from distance matrix (distance to itself is always 1.0 -> gives no new information)
			data_l1.remove(idx_l1);
			labels_l1.remove(idx_l1);
			indices_l1.remove(idx_l1);
			numberOfItems--;
			// perform reduction of data set (selection)
			// by retaining only the topmost MAXIMUM_DATA_ITEMS items
			// therefore, successively remove the data_l1.size - cfCfg.getMaxDataItemsL1() items
			// containing the lowest values
			while (numberOfItems > this.vp.cfCfg.getMaxDataItemsL1()) {
				// get index of minimum data item
				int idx_minDataItem = data_l1.indexOf(Collections.min(data_l1));
				// remove this data item from data and label vector
				data_l1.remove(idx_minDataItem);
				labels_l1.remove(idx_minDataItem);
				indices_l1.remove(idx_minDataItem);
				// update number of data items
				numberOfItems -= 1;
			}
			// normalize remaining matrix, if this should be done
			Vector data_l1Normalized = new Vector();
			if (this.vp.cfCfg.isNormalizeData()) {
				// get maximum value
				double maxValue = ((Double)Collections.max(data_l1)).doubleValue();
				// divide all vector elements by maximum value and insert in new Vector
				for (int i=0; i<numberOfItems; i++) {
					Double curValue = (Double)data_l1.elementAt(i);
					data_l1Normalized.add(new Double(curValue.doubleValue()/maxValue));
				}
			} else {
				data_l1Normalized = data_l1;
			}
			// calculate starting point for bar
			double angleDegrees = (double)360/(data_centerNormalized.size());
			int startPointX = (int)(d.width/2 + Math.cos(Math.toRadians(j*angleDegrees))*(Math.min(d.width, d.height)-borderSize)/4);
			int startPointY = (int)(d.height/2 + Math.sin(Math.toRadians(j*angleDegrees))*(Math.min(d.width, d.height)-borderSize)/4);
			// set angular extent of fan in degrees
			double angleExtent = this.vp.cfCfg.getAngleFanL1();
			// visualize data set (level 1)
			drawFan(data_l1Normalized,
					labels_l1,
					indices_l1,
					labelFont,
					new Point(startPointX, startPointY),
					(int)((Math.min(d.width, d.height)-borderSize)/4),
					this.vp.cfCfg.getMaxBarThickness(),
					j*angleDegrees-angleExtent/2, j*angleDegrees+angleExtent/2,
					this.vp.moElements);
		}
		// visualize center data set (level 0)
		drawFan(data_centerNormalized,
				labels_center,
				indices_center,
				labelFont,
				new Point(d.width/2, d.height/2),
				(int)((Math.min(d.width, d.height)-borderSize)/4),
				this.vp.cfCfg.getMaxBarThickness(),
				0, 360-(360/data_centerNormalized.size()),
				this.vp.moElements);
		// add self-distance for center_label
		label_center += " (1.0)";
		// get the width of center label on the screen
		TextLayout layout = new TextLayout(label_center, labelFont, g.getFontRenderContext());
		// get bounds of label text
		Rectangle2D bounds = layout.getBounds();
		// draw background for label
		this.vp.big.setColor(Color.BLACK);
		if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
		this.vp.big.fill(new Rectangle2D.Double((int)((d.width - bounds.getWidth())/2 - textBoxBorder), (int)((d.height - bounds.getHeight())/2 - textBoxBorder), bounds.getWidth() + 2*textBoxBorder, bounds.getHeight() + 2*textBoxBorder));
		if (this.vp.eps != null) this.vp.eps.fill(new Rectangle2D.Double((int)((d.width - bounds.getWidth())/2 - textBoxBorder), (int)((d.height - bounds.getHeight())/2 - textBoxBorder), bounds.getWidth() + 2*textBoxBorder, bounds.getHeight() + 2*textBoxBorder));
		// calc position for centering label
		// draw label of center element
		this.vp.big.setColor(Color.YELLOW);
		if (this.vp.eps != null) this.vp.eps.setColor(Color.YELLOW);
		this.vp.big.drawString(label_center, (int)((d.width - bounds.getWidth())/2), (int)((d.height + bounds.getHeight())/2));
		if (this.vp.eps != null) this.vp.eps.drawString(label_center, (int)((d.width - bounds.getWidth())/2), (int)((d.height + bounds.getHeight())/2));
		
		// user info for using the mouse
		this.vp.big.setColor(Color.BLACK);
		this.vp.big.drawString("Left mouse click on any label brings it to the center.", 7, (int)(d.height - 7));
		// no user info in eps file
		
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
	}
	
	/**
	 * Paints a visualization of a fan on the visualization area.
	 * The fan comprises of a center and a number of bars beginning at the
	 * center and covering a certain angular extent on the screen. This
	 * extent can be set by the parameters <code>angleStart</code> and <code>angleEnd</code>.
	 *
	 * @param data				a Vector containing the data items (Doubles)
	 * @param labels			a Vector containing the labels
	 * @param indices			a Vector containing the indices of the data items in the original distance matrix
	 * @param labelFont			the Font used for the labels
	 * @param center			a Point indicating the center of the fan
	 * @param barLength			the length of the connecting lines (bars)
	 * @param maxBarThickness	the maximum thickness of the connecting lines (bars)
	 * @param angleStart		the angle at which the fan starts
	 * @param angleEnd			the angle at which the fan ends
	 * @param moLabels			Vector to store Rectangles around labels for mouse over detection
	 */
	private void drawFan(Vector data, Vector labels, Vector indices, Font labelFont, Point center, int barLength, int maxBarThickness, double angleStart, double angleEnd, Vector moLabels) {
		// get 2D-graphics
		Graphics2D g = (Graphics2D)this.vp.getGraphics();
		// get number of data items
		int numberOfItems = data.size();
		// determine the angle between two bars
		double angleInterval = (double)Math.abs(angleEnd-angleStart)/(double)(numberOfItems-1);
		int strokeWidth = 1;		// default stroke width
		int textBoxBorder = 3;		// border size for box around labels
		// set font for drawing labels
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		// variables for storing calculations (for later drawing of labels)
		double[] values = new double[numberOfItems];
		double[] endPointsX = new double[numberOfItems];
		double[] endPointsY = new double[numberOfItems];
		Double value;
		double endPointX, endPointY;
		// draw connections
		for (int i=0; i<numberOfItems; i++) {
			// get value
			value = (Double)data.elementAt(i);
			// draw connections
			this.vp.big.setColor(this.vp.cm.getColor(value.doubleValue()));
			if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(value.doubleValue()));
			strokeWidth = Math.min(maxBarThickness, Math.max(1, (int)(maxBarThickness*value.doubleValue())));	// make sure that stroke width is positive but does not exceed maxEdgeThickness
			this.vp.big.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (this.vp.eps != null) this.vp.eps.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			// calculate end points for connections
			endPointX = center.getX() + Math.cos(Math.toRadians(angleStart+i*angleInterval))*barLength;
			endPointY = center.getY() + Math.sin(Math.toRadians(angleStart+i*angleInterval))*barLength;
			// remember values for later use (labels)
			values[i] = value;
			endPointsX[i] = endPointX;
			endPointsY[i] = endPointY;
			// draw
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
		}
		// draw labels
		for (int i=0; i<numberOfItems; i++) {
			// get value
			value = values[i];
			endPointX = endPointsX[i];
			endPointY = endPointsY[i];
			// get label name
			String label = labels.elementAt(i).toString();
			// add value to label
			long valueFix = (long)(value.doubleValue()*1000);
			float valueFlt = (float)valueFix/1000;
			label += " (" + valueFlt + ")";
			// get the width of label on the screen
			TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
			// get bounds of label text
			Rectangle2D bounds = layout.getBounds();
			// draw rectangle as background for label
			this.vp.big.setColor(Color.BLACK);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
			Rectangle2D.Double rect_label = new Rectangle2D.Double((int)(endPointX - bounds.getWidth()/2 - textBoxBorder), (int)(endPointY - bounds.getHeight()/2 - textBoxBorder), bounds.getWidth() + 2*textBoxBorder, bounds.getHeight() + 2*textBoxBorder);
			this.vp.big.fill(rect_label);
			if (this.vp.eps != null) this.vp.eps.fill(rect_label);
			// add mouse event catching information to Vector
			moLabels.addElement(rect_label);			// position as a Rectangle2D-instance
			moLabels.addElement(indices.elementAt(i));	// index of the associated data item
			// calc position for centering label
			// draw label of center element
			this.vp.big.setColor(Color.YELLOW);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.YELLOW);
			this.vp.big.drawString(label, (int)(endPointX - bounds.getWidth()/2), (int)(endPointY + bounds.getHeight()/2));
			if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(endPointX - bounds.getWidth()/2), (int)(endPointY + bounds.getHeight()/2));
		}
	}
	
	
	/**
	 * This class defines a MouseInputAdapter for the CircledFans visualization.
	 *
	 * @author Markus Schedl
	 */
	private class CircledFansVisu_MouseInputAdapter extends MouseInputAdapter {
		CircledFansVisu adaptee;
		Rectangle2D updateRegion = new Rectangle2D.Double();				// region that needs to be repainted after a popup-box has been drawn
		
		/**
		 * Constructs a new instance of VisuPane_MouseInputAdapter.
		 *
		 * @param adaptee	the CircledFansVisu that uses this MouseInputAdapter
		 */
		CircledFansVisu_MouseInputAdapter(CircledFansVisu adaptee) {
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
		 * Processes the mouse input.
		 *
		 * @param e	the MouseEvent
		 */
		private void processMouseEvent(MouseEvent e) {
			Graphics2D g = (Graphics2D)adaptee.vp.getGraphics();		// Graphics2D-instance of visualization area
			boolean isInsideClickable = false;						// flag, indicating whether mouse was moved into a clickable region
			for (int i=0; i<adaptee.vp.moElements.size(); i++) {
				// for "Circled Fans"-visu only every second element of moElements is a Rectangle (test, if i mod 2 = 0)
				// mouse inside a clickable object?
				if ((i%2 == 0) && ((Rectangle2D)adaptee.vp.moElements.elementAt(i)).contains(e.getX(), e.getY())) {
					isInsideClickable = true;
					// read index of element in distance matrix (next element in moElements)
					int idx_clicked = ((Integer)adaptee.vp.moElements.elementAt(i+1)).intValue();
					// mouse not only over compontent, but also clicked?
					if (SwingUtilities.isLeftMouseButton(e)) {
						// store index of clicked element in the CircledFans-Configuration
						adaptee.vp.cfCfg.setIndexCenter(idx_clicked);
						// repaint the visualization
						adaptee.vp.setLoadBufferedImage(false);
						adaptee.vp.repaint();
					}
				}
			}
			// mouse over a clickable object?
			if (isInsideClickable)		// yes -> change cursor to Hand
				adaptee.vp.setCursor(new Cursor(Cursor.HAND_CURSOR));
			else						// no -> change cursor to Default
				adaptee.vp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
		}
	}
	
}
