package comirva.visu.epsgraphics.objects;

/** this class represents a general eps path.
 * An eps path consits of two arrays, one for the x values,
 * the other for the y values.
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsPath implements EpsObject {

	/** x coordinates of the points */
	protected double xpoints[];
	/** y coordinates of the points */
	protected double ypoints[];
	/** true if the path should be closed */
	protected boolean closed = false;
	/** true if the path should be filled */
	protected boolean filled = false;
	
	/** construct a default path (1 point, not closed, not filled */
	public EpsPath() {
		this(new double[1],new double[1],false,false);
	}
	
	/** construct a path from the given points, not closed, not filled */
	public EpsPath(double[] px, double[] py) {
		this(px,py,false);
	}
	
	/** construct a path from given points, not filled */
	public EpsPath(double[] px, double[] py, boolean closed) {
		this(px,py,closed,false);
	}
	
	/** construct a path from given points */
	public EpsPath(double[] px, double[] py, boolean closed, boolean filled) {
		super();
		xpoints = new double[px.length];
		for (int i = 0; i<px.length; i++) {
			xpoints[i]=px[i];
		}
		ypoints = new double[py.length];
		for (int i = 0; i<py.length; i++) {
			ypoints[i]=py[i];
		}
		this.closed=closed;
		this.filled=filled;
	}

	// @implement
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsPath\n");
		sb.append(xpoints[0]);
		sb.append(" ");
		sb.append(ypoints[0]);
		sb.append(" moveto\n");
		for (int i=1; i<xpoints.length; i++) {
			sb.append(xpoints[i]);
			sb.append(" ");
			sb.append(ypoints[i]);
			sb.append(" lineto\n");
		}
		if (closed) sb.append("closepath\n");
		if (filled) sb.append("fill\n");
			else sb.append("stroke\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsPath) {
			EpsPath other = (EpsPath)obj;
			return (this.closed==other.closed) &&
				(this.filled==other.filled) &&
				(this.xpoints.equals(other.xpoints)) &&
				(this.ypoints.equals(other.ypoints));
		}
		return false;
	}
}
