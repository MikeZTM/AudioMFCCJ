package comirva.visu.epsgraphics.objects;

import java.awt.BasicStroke;

/** this class encapsulates an eps stroke. It provides the
 *  functionality of a BasicStroke for ps output.
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsStroke extends BasicStroke implements EpsObject {
	 	
	/** construct a new eps stroke based on given basic stroke */
	public EpsStroke(BasicStroke stroke) {
		super(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
	}
	
	/** Constructs a new BasicStroke with defaults for all attributes. 
	 *  @see java.awt.BasicStroke#BasicStroke()
	 */  
	public EpsStroke() {
		super();
	}

	/** Constructs a new BasicStroke with the specified attributes.
	 *  @see java.awt.BasicStroke#BasicStroke(float width, int cap, int join, float miterlimit, float[] dash, float dash_phase)
    */
	public EpsStroke(float width, int cap, int join, float miterlimit, float[] dash, float dash_phase) {
		super(width, cap, join, miterlimit, dash, dash_phase);
	}
	
	/** Constructs a solid BasicStroke with the specified attributes.
	 * @see java.awt.BasicStroke#BasicStroke(float width, int cap, int join, float miterlimit)
	 */
	public EpsStroke(float width, int cap, int join, float miterlimit) {
		super(width, cap, join, miterlimit);
	}

	/** Constructs a solid BasicStroke with the specified attributes.
	 * @see java.awt.BasicStroke#BasicStroke(float width, int cap, int join)
	 */
	public EpsStroke(float width, int cap, int join) {
		super(width, cap, join);
	}

	/** Constructs a solid BasicStroke with the specified line width and with default values for the cap and join styles.
	 *  @see java.awt.BasicStroke#BasicStroke(float width)
	 */
	public EpsStroke(float width) {
		super(width);
	}

	// @implements
	public String toEps() {
		StringBuffer sb = new StringBuffer();	
		sb.append("% eps stroke\n");
		// Linewidth		
		sb.append((int)super.getLineWidth());
		sb.append(" setlinewidth\n");		
		// Linecap
		sb.append(super.getEndCap());
		sb.append(" setlinecap\n");
		// Linejoin
		sb.append(super.getLineJoin());
		sb.append(" setlinejoin\n");
		// MiterLimit
		sb.append(super.getMiterLimit());
		sb.append(" setmiterlimit\n");
		// Dash
		float[] dasharray = super.getDashArray();
		if (dasharray != null) {
			sb.append("[ ");
			for (int i=0; i<dasharray.length; i++) {
				sb.append((int) dasharray[i]);
				sb.append(" ");
			}
			sb.append("] ");
			sb.append((int)super.getDashPhase());
			sb.append(" setdash\n");
		}
		// ---
		return sb.toString();
	}
	
	// equals is implemented in super class
}
