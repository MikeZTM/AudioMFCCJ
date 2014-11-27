/*
 * Created on 04.03.2006
 */
package comirva.visu;

import comirva.data.DataMatrix;
import comirva.data.SunburstNode;
import comirva.util.BrowserControl;
import comirva.util.TermProfileUtils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * This class implements a Sunburst visualization.
 * To avoid blocking the GUI, it is implemented as thread.
 * 
 * @author Markus Schedl
 */
public class SunburstVisu extends Thread {
	private VisuPane vp;						// the visualization pane to draw on
	private DataMatrix distMat;					// the distance/similarity matrix 
	
	// private JPopupMenu docPathMenu;
	
	/**
	 * Constructs a new SunburstVisu.
	 *
	 * @param vp	the VisuPane of CoMIRVA which is responsible for drawing
	 */
	public SunburstVisu(VisuPane vp) {
		super();
		this.vp = vp;
		this.distMat = this.vp.distMat;
		// this.docPathMenu = new JPopupMenu();
		//JMenuItem disabled = new JMenuItem("<html><i>(no documents available)</i></html>");
		//disabled.setEnabled(false);
		//this.docPathMenu.add(disabled);
		initMouseListener();
	}

	/**
	 * inits the mouse listeners that are needed for this visualisation.
	 */
	public void initMouseListener() {
		// initialize MouseListener
		SunburstVisu_MouseInputAdapter mia = new SunburstVisu_MouseInputAdapter(this);
		this.vp.addMouseListener(mia);
		this.vp.addMouseMotionListener(mia);
	}
	
/*	public JPopupMenu getPopupMenu() {
		return docPathMenu;
	}
*/	
	/**
	 * Start drawing the visualization.
	 * Paints a sunburst visualization of a given term
	 * occurrence matrix.
	 * For detailed information of sunburst visualizations,
	 * see "Focus+Context Display and Navigation Techniques for
	 * Enhancing Radial, Space-Filling Hierarchy Visualizations" by
	 * John Stasko and Eugene Zhang.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		this.showSunburst();
	}
	
	/**
	 * Paints a sunburst visualization of a given term
	 * occurrence matrix.
	 * For detailed information of sunburst visualizations,
	 * see "Focus+Context Display and Navigation Techniques for
	 * Enhancing Radial, Space-Filling Hierarchy Visualizations" by
	 * John Stasko and Eugene Zhang.
	 */
	protected void showSunburst() {
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
		// check if sunburst's root node was set
		if (this.vp.sbRoot != null) {
			// (re)initialize Vector to store position of elements for mouse over detection
			this.vp.moElements = new Vector();
			// draw sunburst
			this.drawSunburstNodes(this.vp.sbRoot);            	
		}
		
		// user info for using the mouse
		Font labelFont = new Font("SansSerif", Font.PLAIN, this.vp.visuPreferences.getLabelFontSize());
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		this.vp.big.setColor(Color.BLACK);
		if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
		if (this.vp.sbRoot.getDocuments() == null) {
			this.vp.big.drawString("Left mouse click on any arc to create new sunburst based on terms of this arc.", 7, (int)(d.height - 7));
		} else {
			this.vp.big.drawString("Left mouse click on any arc to create new sunburst based on terms of this arc.", 7, (int)(d.height - 10 - this.vp.visuPreferences.getLabelFontSize()));
			this.vp.big.drawString("Right mouse click on any arc to show a list of documents assigned to this arc.", 7, (int)(d.height - 7));
		}	// no mouse hints for eps output
		
		// all drawing was previously made to the buffered image in order to enhance performance
		// so now, load Canvas with buffered image
		g.drawImage(this.vp.bi, 0, 0, this.vp);
		// next time, load Canvas content from buffered image
		this.vp.loadBufferedImage = true;
	}
	
	/**
	 * Draws the nodes of a sunburst given the root node.
	 * 
	 * @param sn	a SunburstNode representing the root node of the sunburst 
	 */
	protected void drawSunburstNodes(SunburstNode sn) {            
		// get child nodes
		Vector<SunburstNode> childNodes = sn.getChildNodes();
		// recursively call method for all child nodes
		if (childNodes.size() != 0) {		// at leat one child exists -> recursive call
			for (int i=0; i<childNodes.size(); i++) {
				this.drawSunburstNodes(childNodes.elementAt(i));
			}
			this.drawSunburstNode(sn);
		} else {		//	no more children  -> draw node
			this.drawSunburstNode(sn);
		}
	}
	
	/**
	 * Draws one node of a sunburst. 
	 * 
	 * @param sn	the SunburstNode to be drawn
	 */
	private void drawSunburstNode(SunburstNode sn) {
		// determine some general parameters of view area
		Dimension d = this.vp.getSize();									// get current size of visualization pane
		int borderSize = this.vp.visuPreferences.getBorderSize();				// size of the border around the complete visualization area
		// determine some parameters necessary to draw arc for the node
		int sunburstDepth = sn.getSunburstDepth();						// depth of sunburst
		int heightArc = ((Math.min(d.width, d.height)-2*borderSize)/2)/sunburstDepth;	        // height for one arc
		double radius = heightArc*sn.getDepth();						// radius of arc
		double angularPosition = sn.getAngularStartPosition();			// angular start position of arc
		double angularExtent = sn.getAngularExtent();		            // angular extent of arc
		double value = sn.getImportanceMaxNorm();						// max-normalized importance value 
		String label;													// label to be displayed for current node
		Vector<String> coOcTerms = sn.getCoocTerms();					// co-occurring terms for current node
		if (coOcTerms.size() > 0 && sn.getParentNode() != null)	// node contains at least one co-oc term
			label = sn.getCoocTerms().lastElement()+" ("+sn.getDocumentFrequency()+")";					// label equals term at lowest hierarchy level (last one in co-oc term list)
		else if (coOcTerms.size() > 0 && sn.getParentNode() == null) {		// in special case of root node, add all co-oc terms to label
			label = "";
			Enumeration<String> e = coOcTerms.elements();
			while (e.hasMoreElements()) {
				String coOcTerm = e.nextElement();
				label += coOcTerm + ", ";
			}
			label = label.substring(0, label.length()-2);
			label += " ("+sn.getDocumentFrequency()+")";
		} else if (coOcTerms.size() == 0)
			label = "("+sn.getDocumentFrequency()+")";
		else
			label = "";
		
		// set font for drawing labels
		int minFontSize = 8;
		int maxFontSize = 18;
		if (this.vp.sbCfg != null) {	// if sunburst-config was set
			minFontSize = this.vp.sbCfg.getMinFontSize();
			maxFontSize = this.vp.sbCfg.getMaxFontSize();
		}
		int fontSize = (int)Math.min(Math.max(minFontSize, angularExtent), maxFontSize);
		Font labelFont = new Font("SansSerif", Font.PLAIN, fontSize);
		this.vp.big.setFont(labelFont);
		if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
		// set color wrt current colormap
		this.vp.big.setColor(this.vp.cm.getColor(value));
		if (this.vp.eps != null) this.vp.eps.setColor(this.vp.cm.getColor(value));
//		System.out.println("drawing arc "+sn.getCoocTerms().toString()+" at start angle "+angularPosition+" with angular extent of "+angularExtent);
		
		// special handling for root node
		if (sn.getParentNode() == null) {		// root node -> draw circle instead of arc
			// if root node does not contain any terms, draw it in background color
			if (sn.getCoocTerms().size() == 0) {
				this.vp.big.setColor(this.vp.visuPreferences.getBackgroundColor());
				if (this.vp.eps != null) this.vp.eps.setColor(this.vp.visuPreferences.getBackgroundColor());
			}
			this.vp.big.fill(new Ellipse2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius));
			if (this.vp.eps != null) this.vp.eps.fill(new Ellipse2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius));
			this.vp.big.setColor(this.vp.visuPreferences.getBackgroundColor());
			if (this.vp.eps != null) this.vp.eps.setColor(this.vp.visuPreferences.getBackgroundColor());
			this.vp.big.draw(new Ellipse2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius));
			if (this.vp.eps != null) this.vp.eps.draw(new Ellipse2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius));
			// estimate size of label
			fontSize = maxFontSize+2;
			labelFont = new Font("SansSerif", Font.PLAIN, fontSize);
			this.vp.big.setFont(labelFont);
			if (this.vp.eps != null) this.vp.eps.setFont(labelFont);
			TextLayout layout = new TextLayout(label, labelFont, this.vp.big.getFontRenderContext());	            // get the width of label on the screen
			Rectangle2D bounds = layout.getBounds();	    		// get bounds of label text
			// draw label
			this.vp.big.setColor(Color.BLACK);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
			this.vp.big.drawString(label, (int)(d.width/2-bounds.getWidth()/2), (int)(d.height/2+bounds.getHeight()/2));
			if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(d.width/2-bounds.getWidth()/2), (int)(d.height/2+bounds.getHeight()/2));
			// add root node to Vector of mouse event elements (to avoid clicking on root node)
			Arc2D.Double arc = new Arc2D.Double(new Rectangle2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius),
					0.0,
					360.0,
					Arc2D.PIE);
			this.vp.moElements.addElement(arc);
			this.vp.moElements.addElement(sn);
		} else {								// no root node -> draw arc
			Arc2D.Double arc = new Arc2D.Double(new Rectangle2D.Double((d.width/2-radius), (d.height/2-radius), 2*radius, 2*radius),
					angularPosition,
					angularExtent,
					Arc2D.PIE);
			this.vp.big.fill(arc);
			if (this.vp.eps != null) this.vp.eps.fill(arc);
			this.vp.big.setColor(this.vp.visuPreferences.getBackgroundColor());
			if (this.vp.eps != null) this.vp.eps.setColor(this.vp.visuPreferences.getBackgroundColor());
			this.vp.big.draw(arc);
			if (this.vp.eps != null) this.vp.eps.draw(arc);
			// rotate drawing area, draw label, and rotate back 
			this.vp.big.rotate(Math.toRadians(-(angularPosition+angularExtent/2)), d.width/2, d.height/2);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(-(angularPosition+angularExtent/2)), d.width/2, d.height/2);
			this.vp.big.setColor(Color.BLACK);
			if (this.vp.eps != null) this.vp.eps.setColor(Color.BLACK);
			this.vp.big.drawString(label, (int)(d.width/2+radius-heightArc+3), (int)(d.height/2+labelFont.getSize()/2));
			if (this.vp.eps != null) this.vp.eps.drawString(label, (int)(d.width/2+radius-heightArc+3), (int)(d.height/2+labelFont.getSize()/2));
			this.vp.big.rotate(Math.toRadians(angularPosition+angularExtent/2), d.width/2, d.height/2);
			if (this.vp.eps != null) this.vp.eps.rotate(Math.toRadians(angularPosition+angularExtent/2), d.width/2, d.height/2);
			// add arc to Vector of mouse event elements
			this.vp.moElements.addElement(arc);
			this.vp.moElements.addElement(sn);
		}
	}
	
	/**
	 * This class defines a MouseInputAdapter for the Sunburst visualization.
	 *
	 * @author Markus Schedl
	 */
	private class SunburstVisu_MouseInputAdapter extends MouseInputAdapter {
		SunburstVisu adaptee;
		
		/**
		 * Constructs a new instance of SunburstVisu_MouseInputAdapter.
		 *
		 * @param adaptee	the SunburstVisu that uses this MouseInputAdapter
		 */
		SunburstVisu_MouseInputAdapter(SunburstVisu adaptee) {
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
		 * Processes the mouse input. In case of left click, calculate
		 * new Sunburst using the clicked term as input. In case of right click,
		 * bring up a list of the documents represented by the selected arc. 
		 *
		 * @param e	the MouseEvent
		 */
		private void processMouseEvent(MouseEvent e) {
			boolean isInsideClickable = false;						// flag, indicating whether mouse was moved into a clickable region
			
			// test whether mouse is inside a clickable region
			for (int i=0; i<adaptee.vp.moElements.size(); i++) {
				// for "Sunburst"-visu only every second element of moElements is an Arc2D (test, if i mod 2 = 0)
				// mouse inside a clickable object?
				if ((i%2 == 0) && ((Arc2D.Double)adaptee.vp.moElements.elementAt(i)).contains(e.getX(), e.getY())) {
					isInsideClickable = true;			// flag, indicating whether mouse was moved into a clickable region
				}
			}
			// mouse not only over clickable compontent, but also clicked?
			if (SwingUtilities.isLeftMouseButton(e)) {
				// get inner-most sunburst node (arcs usually overlap with others!)
				SunburstNode sn = null;			// to store the clicked sunburst node
				Arc2D.Double rootArc = null;	// to store arc of root node (for avoiding clicking on it)
				// find root node
				for (int i=0; i<adaptee.vp.moElements.size(); i++) {
					if (i%2 == 0) {			// only every second element contains an Arc2D.Double
						Arc2D.Double arc = (Arc2D.Double)adaptee.vp.moElements.elementAt(i);
						sn = ((SunburstNode)adaptee.vp.moElements.elementAt(i+1));
						if (sn.getParentNode() == null) {			// root node?
							rootArc = (Arc2D.Double)arc.clone();	// remember root node
						}							
					}
				}
				sn = null;		// re-init sn
				// for every potentially clicked elements (find inner-most arc)
				for (int i=0; i<adaptee.vp.moElements.size(); i++) {
					// for "Sunburst"-visu only every second element of moElements is an Arc2D (test, if i mod 2 = 0)
					// mouse inside a clickable object?
					if ((i%2 == 0) && ((Arc2D.Double)adaptee.vp.moElements.elementAt(i)).contains(e.getX(), e.getY())) {
						// only use current SunburstNode if it is at higher hierarchy level than old one
						if (sn == null || sn.getDepth()>((SunburstNode)adaptee.vp.moElements.elementAt(i+1)).getDepth())
							// read SunburstNode of clicked arc(next element in moElements)
							sn = ((SunburstNode)adaptee.vp.moElements.elementAt(i+1));        				
					}
				}
				// if mouse is over arc of root node, do nothing
				if (rootArc != null && rootArc.contains(e.getX(), e.getY()))
					sn = null;
				// generate new sunburst
				if (sn != null) {
					double importanceRoot = 1.0;		// importance of root node
					double angularStartPosition = 0.0;	// angular start position of root node
					// make sure that term occurrence matrix complies with co-oc term Vector
					// by eliminating all documents from toMatrix that do not
					// contain all words that must be contained in root node
					Vector<Integer> idxDocPaths = new Vector<Integer>();	// to store indices of documents that contain the filter terms 
					DataMatrix toMatrix = TermProfileUtils.getSubsetOfTermOccurrenceMatrix(sn.getTermOccurrenceMatrix(), sn.getAllTerms(), sn.getCoocTerms(), idxDocPaths);    
					// generate root node of sunburst
					SunburstNode rootNode = new SunburstNode(	toMatrix, 
							sn.getAllTerms(), 
							sn.getCoocTerms(),
							importanceRoot, 
							importanceRoot, 
							angularStartPosition, 
							adaptee.vp.sbCfg.getMaxItemsPerNode(), 
							adaptee.vp.sbCfg.getMaxDepth(),
							adaptee.vp.sbCfg.getMinImportance(),
							null);
					// set paths to documents if available
					if (sn.getDocuments() != null)
						rootNode.setDocuments(TermProfileUtils.getMaskedDocumentPaths((Vector<String>)sn.getDocuments().clone(), idxDocPaths));
					else
						rootNode.setDocuments(null);
					// calculate all sunburst nodes
					rootNode.calculateSunburst();
					adaptee.vp.setTermOccurrenceMatrix(toMatrix);	// set term occurrence matrix in visu pane  	
					adaptee.vp.setSunburstRootNode(rootNode);		// set root node
					// draw new sunburst
					adaptee.drawSunburstNodes(rootNode);
					// repaint the visualization
					adaptee.vp.setLoadBufferedImage(false);
					adaptee.vp.repaint();
				}
			}
			// right mouse button pressed
			if (SwingUtilities.isRightMouseButton(e)) {
				// moved to initSunburstContextMenu(int x, int y)
				// (called by getSunburstContextMenu(int x, int y))
				// which is called by the VisuPane to add the special
				// menu to the context menu.
			}
			// mouse over a clickable object?
			if (isInsideClickable)		// yes -> change cursor to Hand
				adaptee.vp.setCursor(new Cursor(Cursor.HAND_CURSOR));
			else 						// no -> change cursor to Default
				adaptee.vp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 * This class implements an ActionListener for the Sunburst visualization. 
	 * It is used to react to user behaviors of clicking on an item of
	 * a popup menu which is displayed when the user clicks on an arc that
	 * represents a Sunburst node.
	 * 
	 * @author Markus Schedl
	 */
	private class SunburstVisu_PopupMenu_Action implements ActionListener {
		String docPath;			// the path to the document which is assigned to the popup menu (and should be opened)
		
		// constructor
		SunburstVisu_PopupMenu_Action(String docPath) {
			this.docPath = docPath;
		}
		
		// action
		public void actionPerformed(ActionEvent actionEvent) {
			// open browser with document that is referred by the name of the popup menu
			String openURL = new String(this.docPath);
			File test = new File(openURL); 	    	// test if document is file
			if (test.exists())		// document is file -> specify file-protocol to open doc in browser
				BrowserControl.displayURL("file://"+this.docPath);
			else					// document is not a file
				BrowserControl.displayURL(this.docPath);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	/** returns a sunburst visualisation specific context menu. The entries of the menu
	 *  are dependent on which position of the visualisation has been clicked so the
	 *  MouseEvent parameter must contain the coordinates of the current click which
	 *  triggered the context menu.
	 *  @param e the mouse event that triggers context menu display
	 */
	public JMenuItem[] getSunburstContextMenu(Point location) {
		System.out.println("Create sunburst context menu");
		return initSunburstContextMenu(location.x, location.y);		// update menu entries		
	}
	
	private JMenuItem[] initSunburstContextMenu(int x, int y) {
		JMenuItem[] menuDocs = new JMenuItem[0];
		// get inner-most sunburst node (arcs usually overlap with others!)
		SunburstNode sn = null;			// to store the clicked sunburst node
//		Arc2D.Double rootArc = null;	// to store arc of root node (for avoiding clicking on it)
//		// find root node
//		for (int i=0; i<adaptee.vp.moElements.size(); i++) {
//		if (i%2 == 0) {			// only every second element contains an Arc2D.Double
//		Arc2D.Double arc = (Arc2D.Double)adaptee.vp.moElements.elementAt(i);
//		sn = ((SunburstNode)adaptee.vp.moElements.elementAt(i+1));
//		if (sn.getParentNode() == null) {			// root node?
//		rootArc = (Arc2D.Double)arc.clone();	// remember root node
//		}							
//		}
//		}
//		sn = null;		// re-init sn
		// for every potentially clicked elements (find inner-most arc)
		for (int i=0; i<this.vp.moElements.size(); i++) {
			// for "Sunburst"-visu only every second element of moElements is an Arc2D (test, if i mod 2 = 0)
			// mouse inside a clickable object?
			if ((i%2 == 0) && ((Arc2D.Double)this.vp.moElements.elementAt(i)).contains(x,y)) {
				// only use current SunburstNode if it is at higher hierarchy level than old one
				if (sn == null || sn.getDepth()>((SunburstNode)this.vp.moElements.elementAt(i+1)).getDepth())
					// read SunburstNode of clicked arc(next element in moElements)
					sn = ((SunburstNode)this.vp.moElements.elementAt(i+1));        				
			}
		}
		// generate list of paths to documents (usually urls)
		if (sn != null && sn.getDocuments() != null) {
			Vector<String> docPaths = sn.getDocuments();
			// create popup-menu with all documents of selected node
			//this.docPathMenu = new JPopupMenu(); 	//JPopupMenu popupDocs = new JPopupMenu();
			// create menu-entries for popup-menu
			menuDocs = new JMenuItem[docPaths.size()];
			for (int i=0; i<docPaths.size(); i++) {
				menuDocs[i] = new JMenuItem(docPaths.elementAt(i));		// create menu item
				menuDocs[i].addActionListener(new SunburstVisu_PopupMenu_Action(docPaths.elementAt(i)));	// add action listener	
				// this.docPathMenu.add(menuDocs[i]);		// add menu items to popup-menu
			}
			// show popup-menu at position of mouse pointer
			// popupDocs.show(e.getComponent(), e.getX(), e.getY());
		}	
		if (menuDocs == null || menuDocs.length == 0) {
			// if the context menu is still empty
			// add a disabled dummy element
			JMenuItem disabled = new JMenuItem("<html><i>(no documents available)</i></html>");
			disabled.setEnabled(false);
			menuDocs = new JMenuItem[1];
			menuDocs[0] = disabled;
			//this.docPathMenu.add(disabled);
		}
		return menuDocs;
	}	
}
