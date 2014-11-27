/*
 * Created on 04.03.2006
 */
package comirva.visu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * This class implements a ExperimentalVisu visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class ExperimentalVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on
	
	/**
	 * Constructs a new ExperimentalVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public ExperimentalVisu(VisuPane vp) {
		super();
		this.vp = vp;
	}
	
	/**
	 * Start drawing the visualization.
	 * Shows an experimental visualization. Can be implemented to test and
	 * experiment with various kinds of visualizations.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showExperimentalVisu();
	}
	

	/**
	 * Shows an experimental visualization. Can be implemented to test and
	 * experiment with various kinds of visualizations.
	 */
	public void showExperimentalVisu() {
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
        // code for visualization to be inserted here...
        // on this.vp.big (for display) and on this.vp.eps (for eps output)
        // ...


        // all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
        g.drawImage(this.vp.bi, 0, 0, this.vp);
        // next time, load Canvas content from buffered image
        this.vp.loadBufferedImage = true;
	}
	
}
