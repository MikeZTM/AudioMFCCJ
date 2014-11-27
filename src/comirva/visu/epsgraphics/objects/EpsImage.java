package comirva.visu.epsgraphics.objects;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

/** this class represents an eps image.
 * The image data must be provided as RenderedImage (typically
 * it is a BufferedImage).
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsImage implements EpsObject {

	/** save rendered image data */
	protected RenderedImage img;	// BufferedImage is a subtype of RenderedImage!
	/** save image position */
	protected Point pos;
	
	/** create eps image
	 * 
	 * @param img the image data
	 * @param pos the image position
	 */
	public EpsImage(RenderedImage img, Point pos) {
		super();
		this.img = img;
		this.pos = pos;
	}

	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsImage\ngsave\n");
		int width = (int)Math.abs(img.getWidth());  	//img.getWidth(obs);	// ex. 24
		int height = (int)Math.abs(img.getHeight()); 	//img.getHeight(obs);	// ex. 34		
		Raster data = img.getData();
		// move to correct position
		sb.append(pos.getX());
		sb.append(" ");
		sb.append(pos.getY());
		sb.append(" translate\n");
		// scale to size		
		sb.append(width);
		sb.append(" ");
		sb.append(height);
		sb.append(" scale\n");
		// image
		sb.append(width);
		sb.append(" ");
		sb.append(height);
		sb.append(" 8 [");
		sb.append(width);
		sb.append(" 0 0 -");
		sb.append(height);
		sb.append(" 0 ");
		sb.append(height);
		sb.append("]\n{<\n");
		// pixel data		
		for (int y=data.getHeight()-1; y>=data.getMinX(); y--) {
			for (int x=data.getMinX(); x<data.getWidth(); x++) {
				int[] ia = new int[3];
				ia = data.getPixel(x,y,ia);
				for (int i=0; i<3; i++) {
					String hex = Integer.toHexString(ia[i]);
					while (hex.length()<2) hex = "0" + hex;
					sb.append(hex);			
				}
				sb.append(" ");
			}
			sb.append('\n');
		}		
		sb.append(">}\nfalse 3 colorimage\ngrestore\n");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsImage) {
			EpsImage other = (EpsImage)obj;
			return this.img.equals(other.img) &&
				this.pos.equals(other.pos);
		}
		return false;
	}
}