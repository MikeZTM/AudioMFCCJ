package comirva.visu;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class GHSOM_MIA extends MouseInputAdapter {
	
	private VisuPane vp;
	private Point lastMouseInput;
	
	public GHSOM_MIA(VisuPane vp) {
		this.vp = vp;
		this.lastMouseInput = new Point(0,0);
	}
	
	@Override
	public void mouseClicked(MouseEvent e)  {
		GHSOMGridVisu ghsomGridVisu = vp.getGHSOMVisalizer();
		lastMouseInput = e.getPoint();
		if(e.getButton() == MouseEvent.BUTTON3
			// && currentSOM.getParent() != null		// this is checked in zoomOut method
			&& e.isControlDown()) {
				ghsomGridVisu.zoomOut(e.getPoint());
		} else if(e.getButton() == MouseEvent.BUTTON1 
			&& e.isControlDown()) {
				ghsomGridVisu.zoomIn(e.getPoint());
		}
	}
	
	public Point getLastMouseInput() {
		return lastMouseInput;
	}
	
//	private int getClickedMapUnit(MouseEvent e, GHSOM currentSOM) {
//		// moved to GHSOMGridVisu
}
