package comirva.visu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import comirva.mlearn.GHSOM;

public class GHSOMGridVisu extends Thread  {
	private VisuPane vp;
	private GHSOM currentSOM;

	private ZoomInAction zoomIn;
	private ZoomOutAction zoomOut;
	
	private final static Color SUB_SOM_COLOR = new Color(184,207,229);

	public GHSOMGridVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.currentSOM = vp.ghsom.getSubSOM(0);
		this.zoomIn = new ZoomInAction("Zoom In");
		this.zoomOut = new ZoomOutAction("Zoom Out");
	}

	public void zoomIn(Point pos) {
		zoomIn.actionPerformed(new ActionEvent(pos, 0, "zoom in"));
	}
	
	public void zoomOut(Point pos) {
		zoomOut.actionPerformed(new ActionEvent(pos, 0, "zoom out"));
	}
	
	@Override
	public void run() {
		this.showGHSOMGrid();
	}

	private void showGHSOMGrid() {
		// do nothing, if given SOM-instance is empty
		if (currentSOM != null) {
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
			double gridWidth = ((d.getWidth() - (double)borderSize*2) / currentSOM.getNumberOfColumns()) ;
			double gridHeight = ((d.getHeight() - (double)borderSize*2) / currentSOM.getNumberOfRows());		

			// draw SOM-grid
			for (int i=0; i<=currentSOM.getNumberOfColumns(); i++) {
				// draw one grid-element to Canvas
				this.vp.big.drawLine((int)(borderSize+i*gridWidth), 
						borderSize, (int)(borderSize+i*gridWidth), d.height-borderSize);
				// draw one grid-element to EPS
				if (this.vp.eps != null) 
					this.vp.eps.drawLine((int)(borderSize+i*gridWidth), 
							borderSize, (int)(borderSize+i*gridWidth), d.height-borderSize);
			}
			for (int j=0; j<=currentSOM.getNumberOfRows(); j++) {
				// draw one grid-element to Canvas
				this.vp.big.drawLine(borderSize, (int)(borderSize+j*gridHeight), 
						d.width-borderSize, (int)(borderSize+j*gridHeight));
				// draw one grid-element to EPS
				if (this.vp.eps != null) 
					this.vp.eps.drawLine(borderSize, (int)(borderSize+j*gridHeight), 
							d.width-borderSize, (int)(borderSize+j*gridHeight));
			}

			// user info for using the mouse
			this.vp.big.setColor(Color.BLACK);
			this.vp.big.drawString("CTRL+Left mouse click zooms in.", 7, d.height - 10 - this.vp.visuPreferences.getLabelFontSize());
			this.vp.big.drawString("CTRL+Right mouse click zooms out.", 7, d.height - 7);	
			// no user info in eps file

			// draw the labels
			this.drawLabels();
			//this.drawSubmaps();
			// all drawing was previously made to the buffered image in order to enhance performance
			// so now, load Canvas with buffered image
			g.drawImage(this.vp.bi, 0, 0, this.vp);
			// next time, load Canvas content from buffered image
			this.vp.loadBufferedImage = true;
		}
	}

	public GHSOM getCurrentSOM() {
		return currentSOM;
	}

	public void setCurrentSOM(GHSOM currentSOM) {
		this.currentSOM = currentSOM;
	}

	private void drawLabels() {
		// get 2D-graphics
		Graphics2D g = (Graphics2D)this.vp.getGraphics();
		// get current size of visualization pane
		Dimension d = this.vp.getSize();
		// size of the border around the complete SOM-grid
		double borderSize = this.vp.visuPreferences.getBorderSize();
		double gridWidth = ((d.getWidth() - borderSize*2) / currentSOM.getNumberOfColumns());
		double gridHeight = ((d.getHeight() - borderSize*2) / currentSOM.getNumberOfRows());
		// set font for drawing labels
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		// print labels
		for (int i=0; i<currentSOM.getNumberOfColumns(); i++) {		// for each column in codebook
			for (int j=0; j<currentSOM.getNumberOfRows(); j++) {		// for each row in codebook
				if (currentSOM.getSubSOM(i*currentSOM.getNumberOfRows()+j) != null) {

					int fillBoundsWidth = (int)(borderSize + (i + 1)*gridWidth) - (int)(borderSize + i*gridWidth) - 1;
					int fillBoundsHeight = (int)(borderSize + (j + 1)*gridHeight) - (int)(borderSize + j*gridHeight) - 1;

					Color colorBefore = this.vp.big.getColor();
					this.vp.big.setColor(SUB_SOM_COLOR);
					this.vp.big.fillRect((int)(borderSize + i*gridWidth) + 1, (int)(borderSize + j*gridHeight) + 1, fillBoundsWidth, fillBoundsHeight);
					this.vp.big.setColor(colorBefore);
					if(this.vp.eps != null) {
						colorBefore = this.vp.eps.getColor();
						this.vp.eps.setColor(SUB_SOM_COLOR);
						this.vp.eps.fillRect((int)(borderSize + i*gridWidth) + 1, (int)(borderSize + j*gridHeight) + 1, fillBoundsWidth, fillBoundsHeight);
						this.vp.eps.setColor(colorBefore);
					}
				}
				// get Voronoi-Set for current map unit (if exists)
				if (currentSOM.voronoiSet != null) {
					// accumulate labels
					Hashtable<String, Integer> accLabels = new Hashtable<String, Integer>();
					if(currentSOM.getPrototypor() != null) {
						String prototype = currentSOM.getPrototypor().getPrototype(currentSOM,i*currentSOM.getNumberOfRows()+j);
						if(prototype != null) 
							accLabels.put(prototype, new Integer(1));
					} else {
						Vector<Integer> vorSet = currentSOM.voronoiSet.elementAt(i*currentSOM.getNumberOfRows()+j);
						for (int k=0; k<vorSet.size(); k++) {
							Integer labelIndex = vorSet.elementAt(k);
							String label = currentSOM.getLabel(labelIndex.intValue());
							Integer lcount = accLabels.get(label);
							int newlcount = 1;
							if (lcount != null) {
								newlcount = lcount.intValue()+1;
							}
							accLabels.put(label, new Integer(newlcount));
						}
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
					List<String> countlabels = new ArrayList<String>();
					Iterator<cp.util.helpers.ObjectComparablePair> ocpit = sortLabels.iterator();
					while (ocpit.hasNext()) {
						String label = (String)(ocpit.next().getObject());
						countlabels.add(label);
					}
					// determine how many labels can be placed to the visualization
					int placeableLabels = (int) (gridHeight / this.vp.visuPreferences.getLabelFontSize());

					// read all labels in Voronoi-Set of current map unit
					for (int k=0; k<countlabels.size(); k++) {
						if(k < placeableLabels) {
							// print the labels
							String label = countlabels.get(k);
							// if last placeable label
							if(k == placeableLabels - 1 && countlabels.size() > placeableLabels)
								label += " ...";
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

	/**
	 * calculates which map unit of the given GHSOM the given point belongs to
	 * @param e the point which map unit should be retrieved (usually a mouse click position or similar)
	 * @param currentSOM the GHSOM for which the map unit should be determind
	 * @return the map unit the given point belongs to.
	 */
	private int getClickedMapUnit(Point e, GHSOM currentSOM) {
		int result = -1;
		Dimension d = this.vp.getSize();
		int borderSize = this.vp.visuPreferences.getBorderSize();		
		double gridWidth = ((d.getWidth() - (double)borderSize*2) / currentSOM.getNumberOfColumns()) ;
		double gridHeight = ((d.getHeight() - (double)borderSize*2) / currentSOM.getNumberOfRows());

		if(e.getX() >= borderSize && e.getX() < (d.getWidth() - borderSize)
				&& e.getY() >= borderSize && e.getY() < (d.getHeight() - borderSize)) {
			int relClickWidth = (int)e.getX() - borderSize;
			int relClickHeight = (int)e.getY() - borderSize;
			int clickedCol = (int) Math.floor(relClickWidth / gridWidth);
			int clickedRow = (int) Math.floor(relClickHeight / gridHeight);
			result = clickedCol* currentSOM.getNumberOfRows() + clickedRow;
		}
		return result;
	}

	/** Action for "zooming in"
	 * @author Florian Marchl
	 *
	 */
	private class ZoomInAction extends AbstractAction {
		private Point position;
		
		/** Defines an {@link Action} object with a default description string and default icon.
		 *  @see AbstractAction#AbstractAction()
		 */
		public ZoomInAction() {
			super();
		}

		/** Defines an {@link Action} object with the specified description string and a the specified icon.
		 * @param name The name for the action
		 * @param icon The icon for the action
		 * @see AbstractAction#AbstractAction(String, Icon)
		 */
		public ZoomInAction(String name, Icon icon) {
			super(name, icon);
		}

		/** Defines an {@link Action} object with the specified description string and a default icon.
		 * @param name The name for the action
		 * @see AbstractAction#AbstractAction(String)
		 */
		public ZoomInAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GHSOMGridVisu ghsomGridVisu = vp.getGHSOMVisalizer();
			GHSOM currentSOM =  ghsomGridVisu.getCurrentSOM();			
			Object source = e.getSource();
			Point p = this.position;
			
			if (source instanceof Point) {
				p = (Point)source;
			}
			if (p == null) {
				p = new Point(0,0);
			}
			int unit = getClickedMapUnit(p,currentSOM);
			if(unit!= -1 && currentSOM.getSubSOM(unit) != null) {
				ghsomGridVisu.setCurrentSOM(currentSOM.getSubSOM(unit));
				vp.loadBufferedImage = false;
				vp.repaint();
			}
		}		
		
		public void setPosition(Point position) {
			this.position = position;
		}		
	}

	/** Action for "zooming out"
	 * @author Florian Marchl
	 *
	 */
	private class ZoomOutAction extends AbstractAction {
		
		/** Defines an {@link Action} object with a default description string and default icon.
		 *  @see AbstractAction#AbstractAction()
		 */
		public ZoomOutAction() {
			super();
		}

		/** Defines an {@link Action} object with the specified description string and a the specified icon.
		 * @param name The name for the action
		 * @param icon The icon for the action
		 * @see AbstractAction#AbstractAction(String, Icon)
		 */
		public ZoomOutAction(String name, Icon icon) {
			super(name, icon);
		}

		/** Defines an {@link Action} object with the specified description string and a default icon.
		 * @param name The name for the action
		 * @see AbstractAction#AbstractAction(String)
		 */
		public ZoomOutAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GHSOMGridVisu ghsomGridVisu = vp.getGHSOMVisalizer();
			GHSOM currentSOM =  ghsomGridVisu.getCurrentSOM();
			GHSOM parent = currentSOM.getParent();
			
			if (parent != null) {
				ghsomGridVisu.setCurrentSOM(parent);
				vp.loadBufferedImage = false;
				vp.repaint();
			}
		}
	}

	/** create a context menu item array 
	 *  containing special commands for this visualization
	 *  @return an array of menu items
	 */
	public JMenuItem[] getGHSOMGridContextMenu(Point position) {
		zoomIn.setPosition(position);
		JMenuItem[] menu = new JMenuItem[2];
		menu[0] = new JMenuItem("Zoom In");
		menu[0].setAction(zoomIn);
		menu[1] = new JMenuItem("Zoom Out");
		menu[1].setAction(zoomOut);		
		return menu;
	}	
}
