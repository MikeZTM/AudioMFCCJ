package comirva.visu.epsgraphics.objects;

import java.awt.Rectangle;
	
/** this class represents an eps rectangle
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsRectangle extends EpsShape {
		
	/** constructs eps rectangle using specified information
	 * @param rect the rectangle
	 */
	public EpsRectangle(Rectangle rect) {
		super(rect);
	}
	
	/** constructs an filled eps rectangle
	 * @param rect the rectangle
	 * @param filled specifies fill or stroke
	 */
	public EpsRectangle(Rectangle rect, boolean filled) {
		super(rect,filled);
	}

	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsRectangle\n");
		sb.append(super.toEps());
		return sb.toString();
	}
	
	//	 equals is implemented in super class
}
