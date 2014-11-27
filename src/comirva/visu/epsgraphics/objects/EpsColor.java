package comirva.visu.epsgraphics.objects;

import java.awt.Color;

/** This class represents an EpsColor
 * It is used to save paint color changes (setColor)
 * 
 * @author  Florian Marchl
 * @version 1.0
 */
public class EpsColor implements EpsObject {

	/** the foreground color */
	protected Color fg = null;
	
	public EpsColor() {
		this(new Color(0,0,0));
	}
	
	public EpsColor(Color foreground) {
		super();
		fg = foreground;
	}
	
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsColor\n");
		// 				/DeviceRGB setcolorspace r g b setcolor
		if (fg!=null) {
		sb.append("/DeviceRGB setcolorspace ");
		sb.append((double)fg.getRed()/255);		// max. 1 = 255/255
		sb.append(" ");
		sb.append((double)fg.getGreen()/255);
		sb.append(" ");
		sb.append((double)fg.getBlue()/255);
		sb.append(" setcolor\n");
		}
		return sb.toString();
	}

	/**
	 * @return  Returns the foreground color.
	 * @uml.property  name="fg"
	 */
	public Color getFg() {
		return fg;
	}

	/**
	 * @param fg  The foreground color to set.
	 * @uml.property  name="fg"
	 */
	public void setFg(Color fg) {
		this.fg = fg;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsColor) {
			EpsColor other = (EpsColor)obj;
			return this.fg.equals(other.fg);
		}
		return false;
	}

}
