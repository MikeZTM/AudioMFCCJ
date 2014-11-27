/*
 * Created on 03.03.2006
 */
package comirva.visu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class implements a SOMGrid visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class SOMGridVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on

	/**
	 * Constructs a new SOMGridVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public SOMGridVisu(VisuPane vp) {
		super();
		this.vp = vp;
	}

	/**
	 * Constructs a new SOMGridVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public SOMGridVisu(VisuPane vp, boolean colorByPCA) {
		super();
		this.vp = vp;
	}

	/**
	 * Start drawing the visualization.
	 * A SOM-Grid containing a rectangle for every map unit is drawn on the screen.
	 * The labels of the data items which are mapped to a certain map unit are displayed
	 * within these rectangles.
	 * In other words, the Voronoi-Set of every map unit is printed to the rectangle of
	 * the map unit.
	 * If no labels are specified, the number of the data items is shown instead of the lables.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showSOMGrid();
	}

	/**
	 * Paints a SOM-Grid containing a rectangle for every map unit on the screen.
	 * The labels of the data items which are mapped to a certain map unit are displayed
	 * within these rectangles.
	 * In other words, the Voronoi-Set of every map unit is printed to the rectangle of
	 * the map unit.
	 * If no labels are specified, the number of the data items is shown instead of the lables.
	 */	
	private void showSOMGrid() {
		// do nothing, if given SOM-instance is empty
		if (vp.som != null) {
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
			// size of the border around the complete SOM-grid
			int borderSize = this.vp.visuPreferences.getBorderSize();
			// calculate the dimension of one grid-element
			double gridWidth = ((d.getWidth() - (double)borderSize*2) / (double)vp.som.getNumberOfColumns()) ;
			double gridHeight = ((d.getHeight() - (double)borderSize*2) / (double)vp.som.getNumberOfRows());		
			// color if desired
			if (vp.som.isColorByPCA()) {
				for (int i=0; i<vp.som.getNumberOfColumns(); i++) {		// for each column in codebook
					for (int j=0; j<vp.som.getNumberOfRows(); j++) {		// for each row in codebook	
						Color c = vp.som.getGridcolors()[i*vp.som.getNumberOfRows()+j];
						this.vp.big.setBackground(c);
						if (this.vp.eps != null) this.vp.eps.setBackground(c);
						this.vp.big.clearRect((int)(0.5+borderSize+i*gridWidth), (int)(0.5+borderSize+j*gridHeight), (int)(0.5+gridWidth), (int)(0.5+gridHeight));					
					}
				}
			}

			// draw SOM-grid
			for (int i=0; i<=vp.som.getNumberOfColumns(); i++) {
				// draw one grid-element to Canvas
				this.vp.big.draw(new Line2D.Double((borderSize+i*gridWidth), borderSize,
						(borderSize+i*gridWidth), d.height-borderSize));
				// draw one grid-element to EPS
				if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double((borderSize+i*gridWidth), borderSize,
						(borderSize+i*gridWidth), d.height-borderSize));
			}
			for (int j=0; j<=vp.som.getNumberOfRows(); j++) {
				// draw one grid-element to Canvas
				this.vp.big.draw(new Line2D.Double(borderSize, (borderSize+j*gridHeight),
						d.width-borderSize, (borderSize+j*gridHeight)));
				// draw one grid-element to EPS
				if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize, (borderSize+j*gridHeight),
						d.width-borderSize, (borderSize+j*gridHeight)));
			}
			// draw the labels
			this.drawLabels();
			// all drawing was previously made to the buffered image in order to enhance performance
			// so now, load Canvas with buffered image
			g.drawImage(this.vp.bi, 0, 0, this.vp);
			// next time, load Canvas content from buffered image
			this.vp.loadBufferedImage = true;
		}
	}

	/**
	 * Prints the labels of the SOM/SDH on the screen.
	 */
	private void drawLabels() {
		// get 2D-graphics
		Graphics2D g = (Graphics2D)this.vp.getGraphics();
		// get current size of visualization pane
		Dimension d = this.vp.getSize();
		// size of the border around the complete SOM-grid
		int borderSize = this.vp.visuPreferences.getBorderSize();
		// calculate the dimension of one grid-element
		double gridWidth = ((d.getWidth() - (double)(borderSize*2)) / (double)vp.som.getNumberOfColumns());
		double gridHeight = ((d.getHeight() - (double)(borderSize*2)) / (double)vp.som.getNumberOfRows());
		// set font for drawing labels
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		// print labels
		for (int i=0; i<vp.som.getNumberOfColumns(); i++) {		// for each column in codebook
			for (int j=0; j<vp.som.getNumberOfRows(); j++) {		// for each row in codebook
				// get Voronoi-Set for current map unit (if exists)
				if (vp.som.voronoiSet != null) {
					Vector vorSet = (Vector)vp.som.voronoiSet.elementAt(i*vp.som.getNumberOfRows()+j);
					// accumulate labels
					Hashtable<String, Integer> accLabels = new Hashtable<String, Integer>();
					for (int k=0; k<vorSet.size(); k++) {
						Integer labelIndex = (Integer)vorSet.elementAt(k);
						String label = vp.som.getLabel(labelIndex.intValue());
						Integer lcount = accLabels.get(label);
						int newlcount = 1;
						if (lcount != null) {
							newlcount = lcount.intValue()+1;
						}
						accLabels.put(label, newlcount);
					}

					// sort descending
					Vector<cp.util.helpers.ObjectComparablePair> sortLabels = new Vector<cp.util.helpers.ObjectComparablePair>();
					Iterator<String> keyit = accLabels.keySet().iterator();
					while (keyit.hasNext()) {
						String label = keyit.next();
						int c = accLabels.get(label).intValue();
						if (c>1)
							label += " (" + c + ")";
						sortLabels.addElement(new cp.util.helpers.ObjectComparablePair(label, new Integer(c)));
					}
					Collections.sort(sortLabels);
					Collections.reverse(sortLabels);

					// write display string
					int labelCounter = 0;
					String[] countlabels = new String[accLabels.size()];
					Iterator<cp.util.helpers.ObjectComparablePair> ocpit = sortLabels.iterator();
					while (ocpit.hasNext()) {
						String label = (String)(ocpit.next().getObject());
						countlabels[labelCounter++] = label;
					}
					// read all labels in Voronoi-Set of current map unit
					for (int k=0; k<countlabels.length; k++) {
						// print the labels
						String label = countlabels[k];
						// get the width of the label on the screen
						TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
						// get bounds of label text
						Rectangle2D bounds = layout.getBounds();
						// calc position for centering labels
						int centerLabelMargin = (int)Math.round(gridWidth/2.0f-bounds.getWidth()/2.0f);
						this.vp.big.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+this.vp.visuPreferences.getLabelFontSize()*(k+1)));
						if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+this.vp.visuPreferences.getLabelFontSize()*(k+1)));
					}
				}
			}
		}
	}


}
