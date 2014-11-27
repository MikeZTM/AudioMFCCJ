package comirva.visu.epsgraphics;

import java.awt.Canvas;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;

/**
 * An extension of the plain canvas to support postscript output. Attention! This is a heavyweight component. It will be above other components if it is used together with Swing leightweight components. If you are using SWING, use the JEpsCanvas instead.
 * @author  Florian Marchl
 * @version  1.0
 */
public class EpsCanvas extends Canvas {

	/** default serial version UID */
	private static final long serialVersionUID = 1L;
	
	/** instance of the EpsGraphics2D object. All objects that should
	 *  also appear in the postscript file must be drawn to this object.
	 *  It can be accessed via a getter method.
	 */
	private EpsGraphics2D epsGraphics;
	
	/** create default canvas */
	public EpsCanvas() {
		super();
		epsGraphics = new EpsGraphics2D();
	}

	/** create canvas using given graphics configuration */
	public EpsCanvas(GraphicsConfiguration gc) {
		super(gc);
		epsGraphics = new EpsGraphics2D();
	}
	
	/**
	 * this method return an eps graphics device.
	 * @return  epsGraphics2D
	 * @uml.property  name="epsGraphics"
	 */	
	public EpsGraphics2D getEpsGraphics() {
		return epsGraphics;
	}
	
	/** 
	 * return a default bounding box for this canvas
	 * @return default bounding box (canvas bounds)
	 */
	public Rectangle getBoundingBox() {
		return getBounds();
	}
}
