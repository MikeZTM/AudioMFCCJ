package comirva.visu.epsgraphics.objects;

import java.awt.Shape;

/** This class represents an EpsClip.
 * It is used to redefine the clipping region
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsClip extends EpsShape {

	/** stores the amount of "gsave" to prevent stack underflows
	 *  caused by "grestore"
	 */
	private static int saveCount = 0;
	
	/** determindes whether the stored shape should be intersected
	 *  with current clip or not 
	 */
	private boolean intersect = true;
	
	/** is set to true if this object is just for restoring a
	 *  previous clip
	 */
	private int reset = 0;
	
	/** construct a new eps clip region. The given shape is
	 * intersected whith the current clip region
	 * @param shape the intersection of this shape with the current
	 * 	clip region form together the new clip region
	 */
	public EpsClip(Shape shape) {
		this(shape,true);
	}

	/** construct a new eps clip region. 
	 * @param shape The shape of the new region
	 * @param intersect If this is true, the given shape is
	 * 		intersected with the current clip, if false a totally
	 * 		new clip region is defined
	 */
	public EpsClip(Shape shape, boolean intersect) {
		super(shape);
		this.intersect = intersect;
	}
	
	/** constructs an eps clip resetter. If this constructor is used
	 * it will just restore a clip region which was set previously
	 * (works only for new regions (not for intersections!)
	 * @param reset reset to previous clip. The number defines the
	 *  amount (technically it specifies the amount of "grestores"
	 *  that are inserted)
	 */
	public EpsClip(int reset) {
		super(null);
		this.reset = reset;
	}
	
	@Override
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		if (reset>0) {		// restore previous clip region
			while (reset>0 && saveCount>0) {
				sb.append("grestore\n");
				reset--;
				saveCount--; 
			}
		} else {			// define new clip region
			if (!intersect) {
				sb.append("gsave\n");
				saveCount++;
			}
			sb.append(super.toEps());	// postscriptify clip shape
			// replace "stroke" by nothing and add "clip":
			sb.replace(sb.lastIndexOf("stroke"), sb.length(), "");
			sb.append("clip\n");
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsClip) {
			EpsClip other = (EpsClip)obj;
			return super.equals(other) &&
				(this.intersect==other.intersect) &&
				(this.reset==other.reset);
		}
		return false;
	}
}
