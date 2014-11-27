package comirva.visu.epsgraphics.objects;

import java.awt.Font;

/** represents an EpsFont.
 * It is used to save every font selection (setFont)
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsFont implements EpsObject {

	/** the postscript name should be chosen for postscript output */
	public static final int TYPE_POSTSCRIPT = 0;
	/** the font name should be chosen for postscript output */
	public static final int TYPE_NAME = 1;
	/** the font family name should be chosen for postscript output */
	public static final int TYPE_FAMILY = 2;
	
	/** the font */
	Font f;
	int type;
	
	/** create eps font (type postscript)
	 * @param font the font
	 */
	public EpsFont(Font font) {
		this(font,TYPE_POSTSCRIPT);
	}

	/** create eps font with given type ({@link #TYPE_POSTSCRIPT} or
	 * 	{@link #TYPE_NAME} or {@link #TYPE_FAMILY})
	 * @param font the Font
	 * @param type the type
	 */
	public EpsFont(Font font, int type) {
		super();
		this.f = font;
		this.type = type;
	}
	
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsFont\n");
		/* font selection demo:
		//		/Courier findfont    % Schrift ausw�hlen
		//		24 scalefont         % auf Schriftgr��e 20 skalieren
		//		setfont              % zum aktuellen Font machen
		 */
		sb.append("/");
		switch (type) {
			case TYPE_POSTSCRIPT: sb.append(f.getPSName());	break; 	// get postscript name of this font
			case TYPE_NAME: sb.append(f.getName()); 		break;	// get font name
			case TYPE_FAMILY: sb.append(f.getFamily()); 	break;	// get font family name
		}		
		sb.append(" findfont\n");
		sb.append(f.getSize());
		sb.append(" scalefont\n");
		sb.append(" setfont\n");
		return sb.toString();
	}
	
	/** return the font object that is represented
	 * by this object
	 * @return the Font object
	 */
	public Font getFont() {
		return f;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsFont) {
			EpsFont other = (EpsFont)obj;
			return this.f.equals(other.f);
		}
		return false;
	}

}
