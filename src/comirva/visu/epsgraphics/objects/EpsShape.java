package comirva.visu.epsgraphics.objects;

import java.awt.Shape;
import java.awt.geom.PathIterator;

/** This class encapsulates a general eps shape object. A shape
 * object provides a path iterator which can be used for postscript
 * code creation
 * @author  Florian Marchl
 * @version 1.0
 */
public class EpsShape implements EpsObject {

	/** an array for the postscript codes */
	protected static final String[] code = { "moveto ", "lineto ", "curveto ",  "curveto ", "closepath " };
	/** an array specifying the amount of parameters for the codes */
	protected static final int[] params = { 2, 2, 4, 6, 0 };	
	
	/** specifies whether the shape should be filled or not */
	protected boolean filled;
	/** the shape that should be drawn */
	protected Shape shape;
	
	/** construct an eps shape. By default it is not filled */
	public EpsShape(Shape shape) {
		this(shape,false);
	}
	
	/** constructs an eps shape given by the parameters
	 * @param shape The shape representing the geometric object
	 * @param filled if true the shape will be filled, otherwise not
	 */
	public EpsShape(Shape shape, boolean filled) {
		super();
		this.shape=shape;
		this.filled=filled;
	}
	
	// @implements
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("newpath\n");
		PathIterator pi = shape.getPathIterator(null);
		while (!pi.isDone()) {
			double[] cs = new double[16];	// get command parameter;
			int t = pi.currentSegment(cs);	// get command type
			int len = params[t];			// how many parameter are valid
			if (t==PathIterator.SEG_QUADTO) {	// parameter cheating for quadto
				for (int i=5;i>=2;i--) {
					cs[i] = cs[i-2];
				}
				len = 6;
			}
			for (int i=0; i<len; i++) {
				double c=cs[i];
				sb.append(c);
				sb.append(" ");
			}
			sb.append(code[t]);
			pi.next();
			sb.append("\n");
		}
		if (filled ) sb.append("fill\n"); else sb.append("stroke\n");
		return sb.toString();
	}
	
	/**
	 * @return  Returns the filled.
	 * @uml.property  name="filled"
	 */
	public boolean isFilled() {
		return filled;
	}

	/**
	 * @param filled  Set whether the shape should be filled or not.
	 * @uml.property  name="filled"
	 */
	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	/**
	 * @return  Returns the shape.
	 * @uml.property  name="shape"
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * @param shape  The shape to set.
	 * @uml.property  name="shape"
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsShape) {
			EpsShape other = (EpsShape)obj;
			return this.shape.equals(other.shape) &&
				(this.filled == other.filled);
		}
		return false;
	}				
}
