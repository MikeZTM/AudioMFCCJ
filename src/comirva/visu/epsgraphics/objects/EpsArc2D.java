package comirva.visu.epsgraphics.objects;

import java.awt.geom.Arc2D;

/** This class represents an EpsArc2D 
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsArc2D extends EpsShape {
	
	/** create an EpsArc2D using the given Arc2D
	 * @param arc information about the arc it should represent
	 */	
	public EpsArc2D(Arc2D arc) {
		super(arc);
	}
	
	/** creates an EpsArc2D using the given parameters
	 * @param arc the arc information
	 * @param filled wheter it should be filled or not
	 */
	public EpsArc2D(Arc2D arc, boolean filled) {
		super(arc,filled);
	}
	
	@Override
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsArc2D\n");
		sb.append(super.toEps());
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsArc2D) {
			return super.equals((EpsArc2D)obj);
		}
		return false;
	}
	
	
}
