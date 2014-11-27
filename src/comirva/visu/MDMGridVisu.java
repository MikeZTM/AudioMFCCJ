/*
 * Created on 03.03.2006
 */
package comirva.visu;

import comirva.mlearn.*;
import comirva.util.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * This class implements a MDMGrid visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Peter Knees
 */
public class MDMGridVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on
	private SOM som;							// the SOM to visualize
	
	/**
	 * Constructs a new SOMGridVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public MDMGridVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.som = this.vp.som;
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
		this.showMDMGrid();
	}
	
	/**
	 * Paints a SOM-Grid containing a rectangle for every map unit on the screen.
	 * The labels of the data items which are mapped to a certain map unit are displayed
	 * within these rectangles.
	 * In other words, the Voronoi-Set of every map unit is printed to the rectangle of
	 * the map unit.
	 * If no labels are specified, the number of the data items is shown instead of the lables.
	 */	
	private void showMDMGrid() {
		// do nothing, if given SOM-instance is empty
		if (som != null) {
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
//			g.setBackground(Color.LIGHT_GRAY);
			g.clearRect(0, 0, this.vp.getSize().width, this.vp.getSize().height);
			// size of the border around the complete SOM-grid
			int borderSize = this.vp.visuPreferences.getBorderSize();
			// calculate the dimension of one grid-element
			double gridWidth = ((d.getWidth() - (double)borderSize*2) / (double)som.getNumberOfColumns()) ;
			double gridHeight = ((d.getHeight() - (double)borderSize*2) / (double)som.getNumberOfRows());
			// set grid color
			g.setColor(Color.GRAY);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.GRAY);
			// color if desired
			if (som.getMDM().isColorByPCA()) {
				for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook
					for (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook
						Color c = som.getMDM().getCellColors()[i*som.getNumberOfRows()+j];
						g.setBackground(c);
						this.vp.big.setBackground(c);
						if (this.vp.eps != null) this.vp.eps.setBackground(c);
						g.clearRect((int)(borderSize+i*gridWidth), (int)(borderSize+j*gridHeight), (int)(1.0+gridWidth), (int)(1.0+gridHeight));
						this.vp.big.clearRect((int)(borderSize+i*gridWidth), (int)(borderSize+j*gridHeight), (int)(1.0+gridWidth), (int)(1.0+gridHeight));
						if (this.vp.eps != null) this.vp.eps.clearRect((int)(borderSize+i*gridWidth), (int)(borderSize+j*gridHeight), (int)(1.0+gridWidth), (int)(1.0+gridHeight));
					}
				}
			}
			
			// draw SOM-grid
			g.draw(new Line2D.Double(borderSize, borderSize,
					borderSize, d.height-borderSize));
			this.vp.big.draw(new Line2D.Double(borderSize, borderSize,
					borderSize, d.height-borderSize));
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize, borderSize,
					borderSize, d.height-borderSize));
			g.draw(new Line2D.Double((borderSize+som.getNumberOfColumns()*gridWidth), borderSize,
					(borderSize+som.getNumberOfColumns()*gridWidth), d.height-borderSize));
			this.vp.big.draw(new Line2D.Double((borderSize+som.getNumberOfColumns()*gridWidth), borderSize,
					(borderSize+som.getNumberOfColumns()*gridWidth), d.height-borderSize));
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double((borderSize+som.getNumberOfColumns()*gridWidth), borderSize,
					(borderSize+som.getNumberOfColumns()*gridWidth), d.height-borderSize));
			g.draw(new Line2D.Double(borderSize, borderSize,
					d.width-borderSize, borderSize));
			this.vp.big.draw(new Line2D.Double(borderSize, borderSize,
					d.width-borderSize, borderSize));
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize, borderSize,
					d.width-borderSize, borderSize));
			g.draw(new Line2D.Double(borderSize, (borderSize+som.getNumberOfRows()*gridHeight),
					d.width-borderSize, (borderSize+som.getNumberOfRows()*gridHeight)));
			this.vp.big.draw(new Line2D.Double(borderSize, (borderSize+som.getNumberOfRows()*gridHeight),
					d.width-borderSize, (borderSize+som.getNumberOfRows()*gridHeight)));
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize, (borderSize+som.getNumberOfRows()*gridHeight),
					d.width-borderSize, (borderSize+som.getNumberOfRows()*gridHeight)));
			
			// draw cells where necessary
			int[] clustassoc = som.getMDM().getClusterAssociations();

			// collect cell sets for each cluster
			HashSet<Integer>[] clusterunits = new HashSet[clustassoc.length];
			for (int k=0; k<clusterunits.length; k++) {
				clusterunits[k] = new HashSet<Integer>();
				for (int l=0; l<clustassoc.length; l++) {
					if (clustassoc[l] == k) {
						clusterunits[k].add(new Integer(l));
					}
				}
			}
			// draw each cell individually
			// -> check whether cell below and cell right are in same cluster -> if not, draw line
			for (int k=0; k<som.getNumberOfColumns(); k++) {		// for each column in codebook
				for (int l=0; l<som.getNumberOfRows(); l++) {		// for each row in codebook
					int mappos = k*som.getNumberOfRows()+l;
					// check for cell below
					if (l < som.getNumberOfRows()-1) {
						if (clustassoc[mappos] != clustassoc[mappos+1]) {
							// draw line
							g.draw(new Line2D.Double(borderSize+(k)*gridWidth, (borderSize+(l+1)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
							// draw one grid-element to BufferedImage
							this.vp.big.draw(new Line2D.Double(borderSize+(k)*gridWidth, (borderSize+(l+1)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
							if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize+(k)*gridWidth, (borderSize+(l+1)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
						}
					}
//					 check for cell right
					if (k < som.getNumberOfColumns()-1) {
						if (clustassoc[mappos] != clustassoc[mappos+som.getNumberOfRows()]) {
							// draw line
							g.draw(new Line2D.Double(borderSize+(k+1)*gridWidth, (borderSize+(l)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
							// draw one grid-element to BufferedImage
							this.vp.big.draw(new Line2D.Double(borderSize+(k+1)*gridWidth, (borderSize+(l)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
							if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize+(k+1)*gridWidth, (borderSize+(l)*gridHeight),
									borderSize+(k+1)*gridWidth, (borderSize+(l+1)*gridHeight)));
						}
					}
				}
			}

			
			// draw the labels
			this.drawLabels();
			// next time, load Canvas content from buffered image
			this.vp.loadBufferedImage = true;
		}
	}
	
	/**
	 * Prints the labels of the MDM on the screen.
	 */
	private void drawLabels() {
		int[] clustassoc = som.getMDM().getClusterAssociations();

		// get 2D-graphics
		Graphics2D g = (Graphics2D)this.vp.getGraphics();
		// get current size of visualization pane
		Dimension d = this.vp.getSize();
		// size of the border around the complete SOM-grid
		int borderSize = this.vp.visuPreferences.getBorderSize();
		// calculate the dimension of one grid-element
		double gridWidth = ((d.getWidth() - (double)(borderSize*2)) / (double)som.getNumberOfColumns());
		double gridHeight = ((d.getHeight() - (double)(borderSize*2)) / (double)som.getNumberOfRows());
		// print labels
		for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook
			for (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook
				int labelHeightOffset = 0;
				// get Voronoi-Set for current map unit (if exists)
				if (som.voronoiSet != null && som.getMDM().getLabels() != null) {

					// only print words into centers of emergence
					if (clustassoc[i*som.getNumberOfRows()+j] != i*som.getNumberOfRows()+j)
						continue;
					// count number of units below from the same cluster
					int spacebelow = 0;
					while (j+spacebelow<som.getNumberOfRows() && 
							clustassoc[i*som.getNumberOfRows()+j+spacebelow] == clustassoc[i*som.getNumberOfRows()+j]) {
						
						spacebelow++;
					}
					double printheight = gridHeight*spacebelow;
					
					Vector<String> unitlabels = som.getMDM().getLabels().elementAt(i*som.getNumberOfRows()+j);
					
					Iterator<String> iter = unitlabels.iterator();
					// read all labels in Voronoi-Set of current map unit
					while (iter.hasNext()) {
						// print the labels
						String labelandvalue = iter.next();
						String label = labelandvalue.substring(0, labelandvalue.lastIndexOf("_"));
						double value = 0.;
						try {
							value = Double.parseDouble(labelandvalue.substring(labelandvalue.lastIndexOf("_")+1, labelandvalue.length()));
						} catch (NumberFormatException nfe) {}
						
						// determine fontsize between this.vp.visuPreferences.getLabelFontSize()-5 and this.vp.visuPreferences.getLabelFontSize()+5
						int fontsize = (int)(Math.round(value*10) + (this.vp.visuPreferences.getLabelFontSize() - 5));
						
						// set font for drawing labels
						Font labelFont = new Font("SansSerif", value>0.95?Font.BOLD:Font.PLAIN, fontsize);
						g.setFont(labelFont);
						this.vp.big.setFont(labelFont);
						if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
						// get the width of the label on the screen
						TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
						// get bounds of label text
						Rectangle2D bounds = layout.getBounds();
						// calc position for centering labels
						int centerLabelMargin = (int)Math.round(gridWidth/2.0f-bounds.getWidth()/2.0f);
						// discard entries that clutter units below
						if (labelHeightOffset+fontsize > printheight) {
							break;
						}
						g.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+labelHeightOffset+fontsize));
						this.vp.big.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+labelHeightOffset+fontsize));
						if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+labelHeightOffset+fontsize));
						labelHeightOffset += fontsize;
					}					
				}
			}
		}
	}
	
}
