/*
 * Created on 03.03.2006
 */
package comirva.visu;

import comirva.mlearn.SOM;
import comirva.mlearn.SDH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * This class implements an SDH visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class SDHVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on
	private SDH sdh;							// the SDH to visualize
	private SOM som;							// the SOM on which the SDH is based
	
	/**
	 * Constructs a new SDHVisu.
	 * 
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public SDHVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.sdh = this.vp.sdh;
		this.som = this.sdh.getSOM();
	}
	
	/**
	 * Start drawing the visualization.
	 * Paints a smoothed data histogram (SDH). Additionally, the SOM-grid and labels are displayed.
	 * Due to performance reasons, the first time an SDH is painted, it is only
	 * painted to a BufferedImage and then this image is loaded into the Canvas.
	 * Because of this, it can take a while, before something can be seen on the screen.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showSDH();
	}
	
	/**
	 * Paints a smoothed data histogram (SDH). Additionally, the SOM-grid and labels are displayed.
	 * Due to performance reasons, the first time an SDH is painted, it is only
	 * painted to a BufferedImage and then this image is loaded into the Canvas.
	 * Because of this, it can take a while, before something can be seen on the screen.
	 */
	private void showSDH() {
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
		this.vp.getGraphics().clearRect(0, 0, this.vp.getSize().width, this.vp.getSize().height);
		// clear drawing area
		g.clearRect(0, 0, this.vp.getSize().width, this.vp.getSize().height);
		// size of the border around the complete SOM-grid
		int borderSize = this.vp.visuPreferences.getBorderSize();
		// get interpolated voting matrix from SDH-instance
		double[][] sdhMatrix = sdh.getInterpolatedVotingMatrix();
		int interpRows = sdhMatrix.length;
		int interpCols = sdhMatrix[0].length;
		// calculate the dimension of one rectangle
		// use doubles here because ints would lead to numerical (rounding) errors (especially when voting matrix is interpolated more than once)
		double gridWidthSDH = ((double)(d.width - borderSize*2) / interpCols) ;
		double gridHeightSDH = ((double)(d.height - borderSize*2) / interpRows);
		for (int i=0; i<interpRows; i++) {		// for each row in interpolated voting matrix
			for (int j=0; j<interpCols; j++) {		// for each column in interpolated voting matrix
				// get votes for current map unit
				double votesForCurrentMU = sdhMatrix[i][j];
				// set drawing color according to colormap
				Color c = this.vp.cm.getColor(votesForCurrentMU);
				this.vp.big.setColor(c);
//				g.setColor(c);
				if (this.vp.eps != null) this.vp.eps.setColor(c);
				// use Math.floor and .ceil to prevent graphics-errors which may arise due to (int)-cast
				Rectangle r = new Rectangle(	(int)Math.floor(borderSize+j*gridWidthSDH),
						(int)Math.floor(borderSize+i*gridHeightSDH),
						(int)Math.ceil(gridWidthSDH), (int)Math.ceil(gridHeightSDH));
				this.vp.big.fill(r);
//				g.fill(r);
				if (this.vp.eps != null) this.vp.eps.fill(r);
			}
		}
		// show SOM-grid above SDH
		this.som = this.sdh.getSOM();
		this.vp.big.setColor(Color.BLACK);
//		g.setColor(Color.BLACK);
		if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
		// calculate the dimension of one grid-element
		double gridWidth = ((d.getWidth() - (double)borderSize*2) / (double)som.getNumberOfColumns()) ;
		double gridHeight = ((d.getHeight() - (double)borderSize*2) / (double)som.getNumberOfRows());
		// draw SOM-grid
		for (int i=0; i<=som.getNumberOfColumns(); i++) {
			// draw one grid-element to Canvas
//			g.draw(new Line2D.Double((borderSize+i*gridWidth), borderSize,
//					(borderSize+i*gridWidth), d.height-borderSize));
			// draw one grid-element to BufferedImage
			this.vp.big.draw(new Line2D.Double((borderSize+i*gridWidth), borderSize,
					(borderSize+i*gridWidth), d.height-borderSize));
			// draw one grid-element to EPS
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double((borderSize+i*gridWidth), borderSize,
					(borderSize+i*gridWidth), d.height-borderSize));
		}
		for (int j=0; j<=som.getNumberOfRows(); j++) {
			// draw one grid-element to Canvas
//			g.draw(new Line2D.Double(borderSize, (borderSize+j*gridHeight),
//					d.width-borderSize, (borderSize+j*gridHeight)));
			// draw one grid-element to BufferedImage
			this.vp.big.draw(new Line2D.Double(borderSize, (borderSize+j*gridHeight),
					d.width-borderSize, (borderSize+j*gridHeight)));
			// draw one grid-element to EPS
			if (this.vp.eps != null) this.vp.eps.draw(new Line2D.Double(borderSize, (borderSize+j*gridHeight),
					d.width-borderSize, (borderSize+j*gridHeight)));
		}
		// "drawLabel" uses the internal SOM-instance -> set internal SOM-instance to the one on which the SDH is based
		this.som = this.sdh.getSOM();
		// draw labels
		this.drawLabels();
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
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
		double gridWidth = ((d.getWidth() - (double)(borderSize*2)) / (double)som.getNumberOfColumns());
		double gridHeight = ((d.getHeight() - (double)(borderSize*2)) / (double)som.getNumberOfRows());
		// set font for drawing labels
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
//		g.setFont(labelFont);
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		// print labels
		for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook
			for (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook
				int labelCounter = 1;
				// get Voronoi-Set for current map unit (if exists)
				if (som.voronoiSet != null) {
					Vector temp = (Vector)som.voronoiSet.elementAt(i*som.getNumberOfRows()+j);
					// read all labels in Voronoi-Set of current map unit
					for (int k=0; k<temp.size(); k++) {
						Integer labelIndex = (Integer)temp.elementAt(k);
						// print the labels
						String label = som.getLabel(labelIndex.intValue());
						// get the width of the label on the screen
						TextLayout layout = new TextLayout(label, labelFont, g.getFontRenderContext());
						// get bounds of label text
						Rectangle2D bounds = layout.getBounds();
						// calc position for centering labels
						int centerLabelMargin = (int)Math.round(gridWidth/2.0f-bounds.getWidth()/2.0f);
//						g.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+this.vp.visuPreferences.getLabelFontSize()*labelCounter));
						this.vp.big.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+this.vp.visuPreferences.getLabelFontSize()*labelCounter));
						if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(borderSize+i*gridWidth+centerLabelMargin), (int)(borderSize+j*gridHeight+this.vp.visuPreferences.getLabelFontSize()*labelCounter));
						labelCounter++;
					}					
				}
			}
		}
	}
	
}
