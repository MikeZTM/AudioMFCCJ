package comirva.visu.epsgraphics.objects;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/** This class encapsulates a eps rectangle with rounded corners.
 * @author  Florian Marchl
 * @version 1.0
 */
public class EpsRoundRectangle2D extends RoundRectangle2D implements EpsObject {
		
	/** array for the postscript code names */
	private static final String[] code = { "moveto ", "lineto ", "quadto ", "curveto ", "closepath " };
	/** the number of parameters returned by the PathIterator */
	private static final int[] params = { 2, 2, 6, 6, 0 };	
	/** contains the graphical information */
	protected RoundRectangle2D.Double doubleRect = new RoundRectangle2D.Double();
	
	/** determinds wether the rectangle should be filled or just stroked */
	private boolean filled = false;

	/** construct a default ps round rectangle */
	public EpsRoundRectangle2D() {
		this(new RoundRectangle2D.Double(),false);
	}

	/** construct a default filled eps round rectangle */
	public EpsRoundRectangle2D(boolean filled) {
		this(new RoundRectangle2D.Double(),filled);
	}

	/** construct a round rectangle given by the parameter
	 * @param d The rounded rectangle to be represented in ps
	 */
	public EpsRoundRectangle2D(RoundRectangle2D.Double d) {
		this(d,false);
	}
	
	/** constructs a rounded rectangle 
	 * @param d The rounded rectangle to be represented in ps
	 * @param filled whether fill or stroke it
	 */
	public EpsRoundRectangle2D(RoundRectangle2D.Double d, boolean filled) {
		super();
		doubleRect = d;
		this.filled = filled;
	}
	
	/** constructs a rounded rectangle.
	 * @param f The rounded rectangle to be represented in ps
	 */
	public EpsRoundRectangle2D(RoundRectangle2D.Float f) {
		this(f,false);
	}
	
	/** constructs a rounded rectangle 
	 * @param f The rounded rectangle to be represented in ps
	 * @param filled whether fill or stroke it
	 */	
	public EpsRoundRectangle2D(RoundRectangle2D.Float f, boolean filled) {
		this(new RoundRectangle2D.Double(f.getX(),f.getY(),f.getWidth(),f.getHeight(),f.getArcHeight(),f.getArcWidth()),filled);
	}

	/**
	 * @see at.jku.cp.epsgraphics.objects.EpsObject#toEps()
	 */
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsRoundRectangle\n");
		sb.append("newpath\n");
		PathIterator pi = super.getPathIterator(null);
		while (!pi.isDone()) {
			double[] cs = new double[6];	// get command parameter;
			int t = pi.currentSegment(cs);	// get command type
			int len = params[t];			// how many parameter are valid
			for (int i=0; i<len; i++) {
				double c=cs[i];
				sb.append(c);
				sb.append(" ");
			}
			sb.append(code[t]);			
			pi.next();
			sb.append("\n");
		}
		if (filled) sb.append("fill\n"); else sb.append("stroke\n");
		return sb.toString();
	}

	@Override
	public double getArcHeight() {
		return doubleRect.getArcHeight();
	}

	@Override
	public double getArcWidth() {
		return doubleRect.getArcWidth();
	}

	@Override
	public void setRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
		doubleRect.setRoundRect(x, y, w, h, arcWidth, arcHeight);
	}

	@Override
	public double getHeight() {
		return doubleRect.getHeight();
	}

	@Override
	public double getWidth() {
		return doubleRect.getWidth();
	}

	@Override
	public double getX() {
		return doubleRect.getX();
	}

	@Override
	public double getY() {
		return doubleRect.getY();
	}

	@Override
	public boolean isEmpty() {
		return doubleRect.isEmpty();
	}

	public Rectangle2D getBounds2D() {
		return doubleRect.getBounds2D();
	}
	
	//	 equals is implemented in super class
}
