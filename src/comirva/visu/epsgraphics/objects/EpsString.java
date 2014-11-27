package comirva.visu.epsgraphics.objects;

import java.awt.Point;

import comirva.visu.epsgraphics.objects.EpsObject;

/** This class represents an eps string.
 * The string is translated to postscript directly, so the
 * resulting document is searchable.
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsString implements EpsObject {

	/** the string position */
	protected Point pos;
	/** the text */
	protected String txt;
	
	/** constructs empty string */
	public EpsString() {
		this(0,0,"");
	}
	
	/** construct specified string at specified position
	 * @param position the position
	 * @param text the text
	 */
	public EpsString(Point position, String text) {
		super();
		pos = position;
		txt = text;
	}
	
	/** construct specified tring at the given position
	 * @param x the x position of the text
	 * @param y the y position of the text
	 * @param text the text itself
	 */
	public EpsString(double x, double y, String text) {
		super();
		pos = new Point();
		pos.setLocation(x,y);
		txt = text;
	}

	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsString\ngsave\n");
		sb.append(pos.x);
		sb.append(" ");
		sb.append(pos.y);
		sb.append(" moveto");
		// text must be mirrowed additionally
		sb.append("\n[1 0 0 -1 0 height] concat\n(");  
		// ^  'height' is defined in Postscript code
		sb.append(txt);
		sb.append(") show\ngrestore\n");		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsString) {
			EpsString other = (EpsString)obj;
			return this.pos.equals(other.pos) &&
				this.txt.equals(other.txt);
		}
		return false;
	}
}
