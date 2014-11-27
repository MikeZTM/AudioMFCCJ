/*
 * Created on 03.03.2006
 */
package comirva.visu;

import comirva.data.DataMatrix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * This class implements a CircledBars visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class CircledBarsVisu extends Thread {
	private VisuPane vp;							// the visualization pane to draw on
	private DataMatrix distMat;						// the distance/similarity matrix 
	
	/**
	 * Constructs a new CircledBarsVisu.
	 */
	public CircledBarsVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.distMat = this.vp.distMat;
	}
	
	/**
	 * Start drawing the visualization.
	 * Paints a visualization for distance vectors that visualizes
	 * the given distances by bars of different length which are
	 * arranged in a circle.<br>
	 * If no labels are specified, the number of the data item is shown
	 * instead of the lables. If the user has selected a meta-data vector
	 * of the same dimensionality as the distance vector,
	 * its contents is taken to represent the labels.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showCircledBars();
	}
	
	/**
	 * Paints a visualization for distance vectors that visualizes
	 * the given distances by bars of different length which are
	 * arranged in a circle.<br>
	 * If no labels are specified, the number of the data item is shown
	 * instead of the lables. If the user has selected a meta-data vector
	 * of the same dimensionality as the distance vector,
	 * its contents is taken to represent the labels.
	 */
	private void showCircledBars() {
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
//		this.vp.big.clearRect(0, 0, this.vp.getSize().width, this.vp.getSize().height);
		// size of the border around the complete visualization area
		int borderSize = this.vp.visuPreferences.getBorderSize();
		// get data to visualize
		int rowIndex = 0;
		Vector data = (Vector)this.distMat.getRow(rowIndex).clone();
		int numberOfItems = this.distMat.getNumberOfColumns();
		// normalize remaining matrix
		double maxValue = Double.NEGATIVE_INFINITY;
		// get maximum value
		for (int i=0; i<numberOfItems; i++) {
			Double curValue = (Double)data.elementAt(i);
			if (curValue.doubleValue()>maxValue)
				maxValue = curValue.doubleValue();
		}
		// divide all vector elements by maximum value and insert in new Vector
		Vector dataNormalized = new Vector();
		for (int i=0; i<numberOfItems; i++) {
			Double curValue = (Double)data.elementAt(i);
			dataNormalized.add(new Double(curValue.doubleValue()/maxValue));
		}
		// determine the angle for one bar
		double anglePerSection = (double)360/(double)numberOfItems;
		// visualize distance
		for (int i=0; i<numberOfItems; i++) {
			// get value
			Double value;
			value = (Double)dataNormalized.elementAt(i);
			// draw arc
			this.vp.big.setColor(this.vp.cm.getColor(value.doubleValue()));
			if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(value.doubleValue()));
			double circleSize = (Math.min(d.width, d.height)-2*borderSize)*value.doubleValue();
			this.vp.big.fill(new Arc2D.Double(new Rectangle2D.Double((d.width-circleSize)/2, (d.height-circleSize)/2, circleSize, circleSize),
					i*anglePerSection,
					anglePerSection,
					Arc2D.PIE));
			if (this.vp.eps != null) this.vp.eps.fill(new Arc2D.Double(new Rectangle2D.Double((d.width-circleSize)/2, (d.height-circleSize)/2, circleSize, circleSize),
					i*anglePerSection,
					anglePerSection,
					Arc2D.PIE));
			//			// calculate a (hopefully) adequate font size
			int fontSize = this.vp.visuPreferences.getLabelFontSize();
//			fontSize = 18 - Math.round(distMat.getNumberOfColumns()/20);
//			if (fontSize<10)
//			fontSize = 10;
			// set font for drawing labels
			Font labelFont = new Font("SansSerif", Font.PLAIN, fontSize);
			this.vp.big.setFont(labelFont);
			if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
			if (value.doubleValue() > 0.5) {
				this.vp.big.setColor(Color.BLACK);
				if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
			}
			// display labels if available
			// get label
			String label;
			if (this.vp.labels != null)
				// get label name
				label = this.vp.labels.elementAt(i).toString();
			else	// if no labels specified, draw numbers
				label = Integer.toString(i);
			// add value to label
			long valueFix = (long)(value.doubleValue()*1000);
			float valueFlt = (float)valueFix/1000;
			label += " (" + valueFlt + ")";
			// rotate drawing area, draw, and rotate back
			// label
			this.vp.big.rotate(Math.toRadians(-(i*anglePerSection+anglePerSection/2)), d.width/2, d.height/2);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(-(i*anglePerSection+anglePerSection/2)), d.width/2, d.height/2);
			this.vp.big.drawString(label, (int)(d.width/2+Math.min(d.width, d.height)/4), (int)(d.height/2+labelFont.getSize()/2));
			if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(d.width/2+Math.min(d.width, d.height)/4), (int)(d.height/2+labelFont.getSize()/2));
			this.vp.big.rotate(Math.toRadians(i*anglePerSection+anglePerSection/2), d.width/2, d.height/2);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(i*anglePerSection+anglePerSection/2), d.width/2, d.height/2);
			// separation line
			this.vp.big.rotate(Math.toRadians(-(i*anglePerSection)), d.width/2, d.height/2);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(-(i*anglePerSection)), d.width/2, d.height/2);
//			this.vp.eps.rotateGrade(-(i*anglePerSection), d.width/2, d.height/2);
			this.vp.big.setColor(new Color(160, 160, 160));
			if (this.vp.eps != null) this.vp.eps.setColor(new Color(160, 160, 160));
			this.vp.big.draw(new Line2D.Double(d.width/2+50, d.height/2, d.width/2+Math.min(d.width, d.height)/2-borderSize, d.height/2));
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(d.width/2+50, d.height/2, d.width/2+Math.min(d.width, d.height)/2-borderSize, d.height/2));
			this.vp.big.rotate(Math.toRadians(i*anglePerSection), d.width/2, d.height/2);
//			double eps_correction = 0.0025;	// correction of mysterious numerical errors in eps file; original value: 0.005
//			this.vp.eps.rotateGrade(i*anglePerSection, d.width/2-eps_correction, d.height/2+2*eps_correction);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(i*anglePerSection), d.width/2, d.height/2);
			
		}
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
	}
	
}
