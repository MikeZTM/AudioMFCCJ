package comirva.visu.epsgraphics.objects;

import java.awt.Point;
import java.awt.font.GlyphVector;

/** This class represents an EpsGlyph. It encapsulates a GlyphVector.
 *  Use this class to make sure that the font in the resulting postscript
 *  file is the same as choosen. 
 *  Attention! The text is translated into Bezier Curves,
 *  so the resulting postscript file do not contain any text directly (not
 *  searchable!) and is usually a bit larger.
 *  Use EpsString objects, if you want to make the document searchable, but
 *  be careful, not all fonts are found on different systems!
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsGlyph extends EpsShape {
		
	/** glpyh position */
	protected Point pos;
	
	/** create eps glyph using given glyph vector.
	 * Default position is (0,0).
	 * @param glyphVector the glyph vector
	 */
	public EpsGlyph(GlyphVector glyphVector) {
		super(glyphVector.getOutline());
		this.pos=new Point(0,0);
	}
	
	/** create eps glyph using given glyph vector at given position. 
	 * 
	 * @param glyphVector the glyph vector
	 * @param position the position
	 */
	public EpsGlyph(GlyphVector glyphVector, Point position) {
		super(glyphVector.getOutline());
		this.pos=position;
	}
	
	/**  create eps glyph using the given information
	 * 
	 * @param glyphVector the glyph vector
	 * @param position the position
	 * @param filled specifies whether the glyphs should be filled or not
	 */
	public EpsGlyph(GlyphVector glyphVector, Point position, boolean filled) {
		super(glyphVector.getOutline(),filled);
		this.pos=position;
	}
	
	/** create eps glyph using the given information
	 * 
	 * @param glyphVector the glyph vector
	 * @param filled specifies whether the glyphs should be filled or not
	 */
	public EpsGlyph(GlyphVector glyphVector, boolean filled) {
		super(glyphVector.getOutline(),filled);
		this.pos=new Point(0,0);
	}

	public String toEps() {
		StringBuilder sb = new StringBuilder();
		sb.append("% Glyph (from Glyph Vector)\n");
		sb.append("gsave\n");
		// move to start position
		if (pos!=null) sb.append(pos.getX()); else sb.append(0);
		sb.append(" ");
		if (pos!=null) sb.append(pos.getY()); else sb.append(0);
		sb.append(" translate\n");
		// Glyph data
		sb.append(super.toEps());
		sb.append("grestore\n");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsGlyph) {
			EpsGlyph other = (EpsGlyph)obj;
			return super.equals(other) && 
					this.pos.equals(other.pos);
		}
		return false;
	}
}
