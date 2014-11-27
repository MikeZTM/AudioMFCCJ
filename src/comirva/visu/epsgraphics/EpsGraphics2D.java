package comirva.visu.epsgraphics;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.io.*;
import java.text.*;
import java.util.*;

import comirva.visu.epsgraphics.objects.*;

/** This class provides postscript generation.
 *  Use this class just like the {@link java.awt.Graphics2D} class
 *  to draw geometric figures.
 *  
 *  Usage: Process every drawing operation (except redrawing after
 *  resizing/restoring the window), coordinate system transformation,
 *  color or font setting, clipping, stroke type changing etc.
 *  that you perform on the standard graphics object (for screen
 *  output) on this class too. This class will save every information
 *  necessary for postscript output. Moreover, you can add comments
 *  to the postscript source file. If you want to generate the
 *  output just call either the {@link #toPS()} or {@link #toEPS()} 
 *  method.
 *  
 * @author  Florian Marchl
 * @version 1.0
 */
public class EpsGraphics2D extends Graphics2D {
	
	/** stores rendering hints */
	private RenderingHints renderingHint = new RenderingHints(null);	
	/** a list of the objects drawn on the canvas */
	private LinkedList<EpsObject> objects = new LinkedList<EpsObject>();		
	/** this field is false until the first time a font is set */
	private boolean strocc = false;	
	/** current font */
	private Font font;	
	/** affine transform */
	private AffineTransform at = AffineTransform.getRotateInstance(0.0);	
	/** current paint */
	private Paint paint;	
	/** current background color */
	private Color background = new Color(255,255,255);	
	/** current clipping shape */
	private Shape clip;
	/** current stroke */
	private Stroke stroke;
	/** current composite */
	private Composite composite;
	/** current xor color */
	private Color xor;
	/** determine xor mode or normal paint mode */
	private boolean paintmode;
	
	/* no existence check for the following objects:
	 * 		- EpsFont (after setting font to FontB you may switch back to FontA)
	 * 		- EpsColor (the same)
	 * 		- EpsTransform (you may do the same transform several times
	 * 		- EpsClip (you may want to reset Clipping regions)
	 * 		- EpsComments (you may want to add the same comment several times)
	 * 		- EpsStroke (you may switch back to a previous stroke)
	 */
	
	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		super.draw3DRect(x, y, width, height, raised);
	}
	
	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		if (!strocc) setFont(getFont());
		EpsString str = new EpsString(x,y,new String(data));
		if (objects.isEmpty() || !objects.contains(str)) objects.add(str);
		strocc = true;
	}
	
	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if (!strocc) setFont(getFont());
		EpsString str = new EpsString(x,y,new String(data));
		if (objects.isEmpty() || !objects.contains(str)) objects.add(str);
		strocc = true;
	}
	
	@Override
	public void drawPolygon(Polygon p) {
		this.drawPolygon(p.xpoints,p.ypoints,p.npoints);
	}

	@Override
	public void draw(Shape shp) {
		EpsShape eshp= new EpsShape(shp);
		eshp.setFilled(false);
		if (objects != null && (objects.isEmpty() || !objects.contains(eshp))) objects.add(eshp);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		BufferedImage bi = new BufferedImage(img.getWidth(obs), img.getHeight(obs), BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,obs);
		EpsImage eimg = new EpsImage(bi,new Point(0,0)); 
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		return true;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		EpsImage eimg = new EpsImage(img,new Point(x,y));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		EpsImage eimg = new EpsImage(img,new Point(0,0)); 
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		EpsImage eimg = new EpsImage(img.createDefaultRendering(),new Point(0,0)); 
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
	}

	@Override
	public void drawString(String str, int x, int y) {
		// a font must be selected before the first string can be drawn!
		if (!strocc) setFont(getFont()); 	// add font object for current font
		EpsString estr = new EpsString((double)x,(double)y,str);
//		if (objects.isEmpty() || !objects.contains(estr)) 
			objects.add(estr);
		strocc = true;						// font is set
	}

	@Override
	public void drawString(String str, float x, float y) {
		// a font must be selected before the first string can be drawn!
		if (!strocc) setFont(getFont());
		EpsString estr = new EpsString(x,y,str);
//		if (objects.isEmpty() || !objects.contains(estr)) 
			objects.add(estr);
		strocc = true;
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// a font must be selected before the first string can be drawn!
		if (!strocc) setFont(getFont());
		StringBuilder sb = new StringBuilder();		// Build String from Iterator
		char ch = iterator.next();
		while (ch!=CharacterIterator.DONE) {
			sb.append(ch);
			ch=iterator.next();
		}
		EpsString estr = new EpsString(x,y,sb.toString()); 
		if (objects.isEmpty() || !objects.contains(estr)) objects.add(estr);
		strocc = true;	// font is set
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// a font must be selected before the first string can be drawn!
		if (!strocc) setFont(getFont());
		StringBuilder sb = new StringBuilder();
		char ch = iterator.next();
		while (ch!=CharacterIterator.DONE) {
			sb.append(ch);
			ch=iterator.next();
		}
		EpsString estr = new EpsString(x,y,sb.toString()); 
		if (objects.isEmpty() || !objects.contains(estr)) objects.add(estr);
		strocc = true;		
	}

	@Override
	/** The glyph vector describs the glyphs to be drawn
	 *  by bezier curves. Therefore the resulting document will not
	 *  contain text
	 */
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		EpsGlyph glyph = new EpsGlyph(g,new Point((int)x,(int)y),true); 
		if (objects.isEmpty() || !objects.contains(glyph)) objects.add(glyph);
	}

	@Override
	public void fill(Shape shp) {
		EpsShape eshp = new EpsShape(shp,true);
		if (shp==null || eshp==null) {
			System.out.println((shp==null) + " " + (eshp==null) + " "
					+ (objects==null) + " -- null value ignored");
			return;
		}
// 		if(objects.isEmpty() || !objects.contains(eshp)) 
		// no occurence check due to performance problems with sdh visu
		objects.add(eshp);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// it is hit if there exists an intersection
		return s.intersects(rect);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		this.composite = comp;
	}

	@Override
	public void setPaint(Paint paint) {
		this.paint = paint;		
	}

	@Override
	public void setStroke(Stroke s) {		
		stroke = s;
		if (s instanceof BasicStroke) {
			EpsStroke es = new EpsStroke((BasicStroke)s);
			if ((objects!=null) && (objects.size()>0) &&
				(objects.getLast() instanceof EpsStroke)) {
				// avoid objects with no effect
				objects.removeLast();
				objects.add(new EpsComment("Stroke change removed"));
			}
			// no other occurence checking
			objects.add(es);
		}			
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		renderingHint.putAll(hints);
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		RenderingHints newHints = new RenderingHints(null);
		newHints.putAll(hints);
		renderingHint.add(newHints);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return renderingHint;
	}

	@Override
	public void translate(int x, int y) {
		at.translate(x,y);
		EpsTransform et = new EpsTransform(EpsTransform.TYPE_TRANSLATE,x,y);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void translate(double x, double y) {
		at.translate(x,y);
		EpsTransform et = new EpsTransform(EpsTransform.TYPE_TRANSLATE,x,y);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void rotate(double theta) {
		at.rotate(theta);
		EpsTransform et = new EpsTransform(theta);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	/** just the same as the {@link #rotate(double)} method, 
	 * except that the parameter is treated as degree value (0..360)
	 * @param theta angle in degree
	 * @see #rotate(double)
	 */
	public void rotateGrade(double theta) {
		at.rotate(Math.toRadians(theta));
		EpsTransform et = new EpsTransform(theta);
		et.setToDegree();
		objects.add(et);
	}
	
	@Override
	public void rotate(double theta, double x, double y) {
		at.rotate(theta,x,y);
		// is equivivalent to:
		// translate(x,y);
		// rotate(theta);
		// translate(-x,-y);
		// -- done internally!
		EpsTransform et = new EpsTransform(theta, x, y); 
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}
	
	/** just the same as the {@link #rotate(double, double, double)} method,
	 * 	except that the angle parameter is treated as a degree value (0..360)
	 * @param theta angle in degree
	 * @param x the x position
	 * @param y the y position
	 * @see #rotate(double, double, double)
	 */
	public void rotateGrade(double theta, double x, double y) {
		at.rotate(Math.toRadians(theta),x,y);
		EpsTransform et = new EpsTransform(theta, x, y);
		et.setToDegree();
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void scale(double sx, double sy) {
		at.scale(sx,sy);
		EpsTransform et = new EpsTransform(EpsTransform.TYPE_SCALE, sx, sy);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void shear(double shx, double shy) {
		at.shear(shx,shy);
		EpsTransform et = new EpsTransform(EpsTransform.TYPE_SHEAR, shx, shy);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void transform(AffineTransform tx) {
		at.concatenate(tx);
		EpsTransform et = new EpsTransform(tx);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public void setTransform(AffineTransform tx) {
		at = tx!=null?tx:AffineTransform.getTranslateInstance(0.0,0.0);
		EpsTransform et = new EpsTransform(tx);
		if (objects.isEmpty() || !objects.contains(et)) objects.add(et);
	}

	@Override
	public AffineTransform getTransform() {
		return at;
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public Composite getComposite() {
		//return AlphaComposite.getInstance(AlphaComposite.DST_IN);
		return this.composite;
	}

	@Override
	public void setBackground(Color bgcolor) {
		// the default postscript background color is white.
		this.background = bgcolor!=null ? bgcolor : Color.WHITE;
	}

	@Override
	public Color getBackground() {
		// the default postscript background color is white.
		return background;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void clip(Shape s) {
		Area ca = new Area(clip);
		Area sa = new Area(s);
		ca.intersect(sa);
		clip = ca;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContext(at, false, false);
	}

	@Override
	public Graphics create() {
		if (objects==null) objects = new LinkedList<EpsObject>();
		paintmode = true;
		return this;
	}

	@Override
	public Color getColor() {
		Color c = new Color(255,255,255);
		for (EpsObject o: objects) {
			if (o instanceof EpsColor) c =((EpsColor)o).getFg();
		}
		return c;
	}

	/** If no other operation has been performed since the last call
	 *  to <code>setColor()</code>, the last color object will be replaced
	 *  by that one given now. 
	 */
	@Override
	public void setColor(Color c) {
		EpsColor ec = new EpsColor(c);
		if ((objects!=null) && (objects.size()>0) &&
			(objects.getLast() instanceof EpsColor)) {
			// avoid objects with no effect
			Color old = ((EpsColor)objects.removeLast()).getFg();
			objects.add(new EpsComment("removed color: " + Integer.toHexString(old.getRGB())));
		}
		// no other existance checks for colors
		objects.add(ec);
	}

	@Override
	public void setPaintMode() {
		// set paint mode to standard paint mode 
		// (color is replaced by foreground color)
		if (xor==null) xor = new Color(0,0,0);
		else xor = Color.BLACK;
		paintmode = true;
	}

	@Override
	public void setXORMode(Color cl) {
		// set xor mode color
		xor = cl;
		paintmode = false;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font f) {
		if (f==null) f=new Font("Helvetica",Font.PLAIN,10);
		font = f;
		if ((objects!=null) && (objects.size()>0) && 
			(objects.getLast() instanceof EpsFont)) {
			// remove objects with no effect
			Font old = ((EpsFont)objects.removeLast()).getFont();
			objects.add(new EpsComment("font setting for '" + old.getFontName() + "' removed"));
		}
		EpsFont ef = new EpsFont(font,EpsFont.TYPE_NAME);
		// no other existance checks for fonts
		objects.add(ef);
		strocc = true;		// we can do this, because a font selection will be added
	}

	/** sets the current font to f. In postscript output the
	 * PS Name (from {@link java.awt.Font#getPSName()}) will be used
	 * in the <code>findfont</code> command.
	 * @param f the new font
	 */
	public void setPSFont(Font f) {
		if (f==null) f=new Font("Helvetica",Font.PLAIN,10);
		font = f;
		if ((objects!=null) && (objects.size()>0) && 
			(objects.getLast() instanceof EpsFont)) {
			// remove objects with no effect
			Font old = ((EpsFont)objects.removeLast()).getFont();
			objects.add(new EpsComment("font setting for '" + old.getFontName() + "' removed"));
		}
		EpsFont ef = new EpsFont(font);
		// no other existance checks for fonts
		objects.add(ef);
		strocc = true;		// we can do this, because a font selection will be added
	}
	
	/** sets the current font to f. In postscript output the
	 * Font Family Name (from {@link java.awt.Font#getFamily()}) will be used
	 * in the <code>findfont</code> command.
	 * @param f the new font
	 */
	public void setFamilyFont(Font f) {
		if (f==null) f=new Font("Helvetica",Font.PLAIN,10);
		font = f;
		if ((objects!=null) && (objects.size()>0) && 
			(objects.getLast() instanceof EpsFont)) {
			// remove objects with no effect
			Font old = ((EpsFont)objects.removeLast()).getFont();
			objects.add(new EpsComment("font setting for '" + old.getFontName() + "' removed"));
		}
		EpsFont ef = new EpsFont(font,EpsFont.TYPE_FAMILY);
		// no other existance checks for fonts
		objects.add(ef);
		strocc = true;		// we can do this, because a font selection will be added
	}
	
	@Override
	public FontMetrics getFontMetrics(Font f) {
		// no font metrics are available here, because they *should* be equal
		// to the ones available from the "real" graphics thing.
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		return clip.getBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// intersect with current clip region
		Area clp = new Area(clip);
		Area par = new Area(new Rectangle(x,y,width,height));
		clp.intersect(par);
		clip = clp;
		EpsClip ec = new EpsClip(par,true);
		// no existance checks for clips 
		objects.add(ec);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		setClip(new Rectangle(x,y,width,height));
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void setClip(Shape clip) {		
		// set totally new clip region
		this.clip = clip;
		EpsClip ec = new EpsClip(clip,false);
		// no existance checks for clips 
		objects.add(ec);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		Area src = new Area(new Rectangle(x,y,width,height));
		Area dst = new Area();
		for (EpsObject obj: objects) {
			if (obj instanceof EpsShape) {
				Area tmp = new Area(((EpsShape) obj).getShape());
				tmp.intersect(src);
				if (!tmp.isEmpty()) {
					dst.add((Area)tmp.clone());
				}
			}			
		}
		dst.transform(AffineTransform.getTranslateInstance(dx, dy));
		EpsShape eshp = new EpsShape(dst);
		if (objects.isEmpty() || !objects.contains(eshp)) objects.add(eshp);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		double[] x = new double[2];
		double[] y = new double[2];
		x[0]=x1; x[1]=x2;
		y[0]=y1; y[1]=y2;
		EpsPath p = new EpsPath(x,y);
		if (objects.isEmpty() || !objects.contains(p)) objects.add(p);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		EpsRectangle r = new EpsRectangle(new Rectangle(x,y,width,height),true);
		if (objects.isEmpty() || !objects.contains(r)) objects.add(r);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// overwrite specified rectangle with background color
		objects.add(new EpsColor(background));
		objects.add(new EpsRectangle(new Rectangle(x,y,width,height),true));
		objects.add(new EpsColor(getColor()));
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		EpsRoundRectangle2D roundrect = new EpsRoundRectangle2D();
		roundrect.setRoundRect(x,y,width,height,arcWidth,arcHeight);
		if (objects.isEmpty() || !objects.contains(roundrect)) objects.add(roundrect);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		EpsRoundRectangle2D roundrect = new EpsRoundRectangle2D(true);
		roundrect.setRoundRect(x,y,width,height,arcWidth,arcHeight);
		if (objects.isEmpty() || !objects.contains(roundrect)) objects.add(roundrect);		
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		Arc2D arc2d = new Arc2D.Double((double)x,(double)y,(double)width,(double)height,0.0,360.0,Arc2D.OPEN);
		EpsArc2D arc = new EpsArc2D(arc2d);
		if (objects.isEmpty() || !objects.contains(arc)) objects.add(arc);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		Arc2D arc2d = new Arc2D.Double((double)x,(double)y,(double)width,(double)height,0.0,360.0,Arc2D.OPEN);
		EpsArc2D arc = new EpsArc2D(arc2d,true);
		if (objects.isEmpty() || !objects.contains(arc)) objects.add(arc);
	}

	@Override
	public void drawArc(int xpos, int ypos, int width, int height, int start, int extend) {
		Arc2D arc2d = new Arc2D.Double((double)xpos,(double)ypos,(double)width,(double)height,(double)start,(double)extend,Arc2D.OPEN);
		EpsArc2D arc = new EpsArc2D(arc2d);
		if (objects.isEmpty() || !objects.contains(arc)) objects.add(arc);
	}

	@Override
	public void fillArc(int xpos, int ypos, int width, int height, int start, int extend) {
		Arc2D arc2d = new Arc2D.Double((double)xpos,(double)ypos,(double)width,(double)height,(double)start,(double)extend,Arc2D.PIE);
		EpsArc2D arc = new EpsArc2D(arc2d,true);
		if (objects.isEmpty() || !objects.contains(arc)) objects.add(arc);
	}

	@Override
	public void drawPolyline(int[] x, int[] y, int n) {
		double[] xd = new double[n];
		double[] yd = new double[n];
		for (int i=0; i<n; i++) {
			xd[i] = (double)x[i];
			yd[i] = (double)y[i];
		}
		EpsPath path = new EpsPath(xd,yd,false);
		if (objects.isEmpty() || !objects.contains(path)) objects.add(path);
	}

	@Override
	public void drawPolygon(int[] x, int[] y, int n) {
		double[] xd = new double[n];
		double[] yd = new double[n];
		for (int i=0; i<n; i++) {
			xd[i] = (double)x[i];
			yd[i] = (double)y[i];
		}
		EpsPath path = new EpsPath(xd,yd,true);
		if (objects.isEmpty() || !objects.contains(path)) objects.add(path);
	}

	@Override
	public void fillPolygon(int[] x, int[] y, int n) {
		double[] xd = new double[n];
		double[] yd = new double[n];
		for (int i=0; i<n; i++) {
			xd[i] = (double)x[i];
			yd[i] = (double)y[i];
		}
		EpsPath path = new EpsPath(xd,yd,true,true);
		if (objects.isEmpty() || !objects.contains(path)) objects.add(path);	
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver obs) {
		BufferedImage bi = new BufferedImage(Math.abs(img.getWidth(obs)), Math.abs(img.getHeight(obs)), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(img,0,0,null);
		EpsImage eimg = new EpsImage(bi,new Point(x,y));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		g.dispose();
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver obs) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,obs);
		EpsImage eimg = new EpsImage(bi,new Point(x,y));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		BufferedImage bi = new BufferedImage(img.getWidth(observer), img.getHeight(observer), BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,observer);
		EpsImage eimg = new EpsImage(bi,new Point(0,0));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);		
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,observer);
		EpsImage eimg = new EpsImage(bi,new Point(x,y));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		BufferedImage bi = new BufferedImage(dx2-dx1, dy2-dy1, BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,observer);
		EpsImage eimg = new EpsImage(bi,new Point(dx1,dy1)); 
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		BufferedImage bi = new BufferedImage(dx2-dx1, dy2-dy1, BufferedImage.TYPE_INT_RGB);
		bi.getGraphics().drawImage(img,0,0,observer);
		EpsImage eimg = new EpsImage(bi,new Point(dx1,dy1));
		if (objects.isEmpty() || !objects.contains(eimg)) objects.add(eimg);
		return true;
	}

	@Override
	public void dispose() {
		at.setToIdentity();				// reset transformation to identity
		background = Color.WHITE;		// standard background
		if (clip != null) clip = null;	// no clip region
		xor = new Color(0,0,0);			// default xor color
		paintmode = true;				// default paint mode
		if (objects != null) objects.clear();		// clear objects list
	}
	
	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		renderingHint.put(hintKey,hintValue);
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return renderingHint.get(hintKey);
	}
	
	/** Adds an eps comment. It can be used for documentation purposes.
	 * 
	 * @param comment The comment text. The comment indicator '%' is automatically
	 * added in every new line. You do not have to include it in the String parameter!
	 */
	public void addComment(String comment) {
		EpsComment ec = new EpsComment(comment);
		// no existance check for comments
		objects.add(ec);
	}
	
	/** Constructs PS string that represents the current graphics
	 *  as postscript data. The postscript commands are in the same
	 *   order as the corresponding methods have been called.
	 * @return postscript code representing the graphics.
	 * @throws IOException 
	 */
	public String toPS() throws IOException {						// pageheight DIN A4 = 842pt
		return toPS(null);
	}
	/** Constructs PS string that represents the current graphics
	 *  as postscript data. The postscript commands are in the same
	 *   order as the corresponding methods have been called.
	 * @param w the writer that the code should be printed to
	 * @return postscript code representing the graphics.
	 * @throws IOException 
	 */	
	public String toPS(Writer w) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("%!\r\n");
		sb.append("[1 0 0 -1 0 792] concat\ngsave\n");		// Java coordinates to Postscript coordinates		
		sb.append(generateCode(null,w));
		sb.append("showpage\n");
		return sb.toString();		
	}

	/** Constructs a string representing the current graphics as 
	 *  encapsulated postscript data. Technically, the eps header is added
	 *  before the postscript data returned by toEPS().
	 * @param title the Document title. If null an empty title is added
	 * @param filename the name of the file the code is going to be written to. 
	 * 		If <code>null</code> no filename comment will be added 
	 * @param boundingBox the bounding box information. If <code>null</code>,
	 * 		a DIN A4 page (612x792 mm) is assumed as bounding box.
	 * @return the eps representation of the current graphics
	 * @throws IOException 
	 */
	public String toEPS(String title, String filename, Rectangle boundingBox, Writer w) throws IOException {
		Calendar now = Calendar.getInstance();		// Timestamp
		String[] am_pm = { "AM", "PM" };
		StringBuffer sb = new StringBuffer();
			sb.append("%!PS-Adobe-3.0 EPSF-3.0\n");
			// default: %%BoundingBox: 1 1 612 792
			if (boundingBox==null) boundingBox=new Rectangle(1,1,612,792);
			sb.append("%%BoundingBox: ");
			sb.append((int)boundingBox.getX());			sb.append(" ");
			sb.append((int)boundingBox.getY());			sb.append(" ");
			sb.append((int)boundingBox.getWidth());		sb.append(" ");
			sb.append((int)boundingBox.getHeight());	sb.append("\n");
			sb.append("%%Title: (");
			if (title!=null) sb.append(title); else sb.append("untitled");
			sb.append(")\n%%Creator: epsgraphics (by Florian Marchl)");
			sb.append("\n%%CreationDate: (");
			sb.append(now.get(Calendar.DAY_OF_MONTH));	sb.append("/");
			sb.append(now.get(Calendar.MONTH));			sb.append("/");
			sb.append(now.get(Calendar.YEAR));			sb.append(") (");
			sb.append(now.get(Calendar.HOUR));			sb.append(":");
			sb.append(now.get(Calendar.MINUTE));		sb.append(" ");
			sb.append(am_pm[now.get(Calendar.AM_PM)]);	sb.append(")\n");
			sb.append("%%Pages: 1\n");
			sb.append("%%EndComments\n");
			sb.append("%%Page: 1 1\n");
			if (filename!=null) {			// append Filename
				sb.append("%%BeginDocument: ");
				sb.append(filename);
				sb.append("\n");
			}
			sb.append("[1 0 0 -1 0 ");		// Height from BoundingBox
			sb.append((int)(boundingBox.getY()+boundingBox.getHeight()));
			sb.append("] concat\ngsave\n");
			if (w!=null) {
				w.write(sb.toString());
				generateCode(boundingBox,w);
				w.write("showpage\n%%EOF");
			} else {
				sb.append(generateCode(boundingBox,null));
				sb.append("showpage\n%%EOF");
			}
		return sb.toString();
	}
	
	/** Creates the eps code representing the current graphics
	 * and using the given parameters
	 * @param title graphics title
	 * @param filename the filename
	 * @param boundingBox the bounding box
	 * @return the code
	 * @throws IOException
	 */
	public String toEPS(String title, String filename, Rectangle boundingBox) throws IOException {		
		return toEPS(title, filename, boundingBox, null);
	}
	
	/** Creates the eps code representing the current graphics.
	 * As BoundingBox a DIN A4 page is assumed
	 * @param w the output stream that the code should be printed to
	 * @return parts of the code but not all
	 * @throws IOException
	 */
	public String toEPS(Writer w) throws IOException {
		return toEPS(null, null, null, w);
	}
	
	/** Creates the eps code representing the current graphics.
	 * As BoundingBox a DIN A4 page is assumed
	 * @return eps representation
	 * @throws IOException 
	 * @see #toEPS(String title, String filename, Rectangle boundingBox)
	 */
	public String toEPS() throws IOException {
		return toEPS(null, null, null, null);
	}

	/** Creates the eps code representing the current graphics.
	 * 
	 * @param boundingBox The bounding box of the graphics
	 * @return eps code
	 * @throws IOException 
	 * @see #toEPS(String title, String filename, Rectangle boundingBox)
	 */
	public String toEPS(Rectangle boundingBox) throws IOException {
		return toEPS(null, null, boundingBox, null);
	}
	
	/** Creates the eps code representing the current graphics.
	 * 
	 * @param boundingBox The bounding box of the graphics
	 * @param w the outputstream that the code should be printed to
	 * @return eps code
	 * @throws IOException 
	 * @see #toEPS(String title, String filename, Rectangle boundingBox)
	 */
	public String toEPS(Rectangle boundingBox, Writer w) throws IOException {
		return toEPS(null, null, boundingBox, w);
	}
	
	/** Creates the eps code representing the current graphics.
	 * 
	 * @param title The title of the graphic
	 * @param boundingBox The bounding box
	 * @return eps code
	 * @throws IOException 
	 * @see #toEPS(String title, String filename, Rectangle boundingBox)
	 */
	public String toEps(String title, Rectangle boundingBox) throws IOException {
		return toEPS(title, null, boundingBox, null);
	}

	/** Creates the eps code representing the current graphics.
	 * 
	 * @param title The title of the graphic
	 * @param boundingBox The bounding box
	 * @param w The outputstream the code should be printed to
	 * @return parts of the eps code
	 * @throws IOException 
	 * @see #toEPS(String title, String filename, Rectangle boundingBox)
	 */
	public String toEps(String title, Rectangle boundingBox, Writer w) throws IOException {
		return toEPS(title, null, boundingBox, w);
	}
	/** private method that generates the main postscript code (graphics)
	 * @param bb the BoundingBox
	 * @return the code
	 */
	private String generateCode(Rectangle bb, Writer w) throws IOException {
		StringBuffer sb = new StringBuffer();	
		if (w!=null) {
			// draw a page-sized rectangle as background color
			if (!background.equals(Color.WHITE)) {
				w.write("%\n% background color\n");
				w.write(new EpsColor(background).toEps());
				w.write(new EpsRectangle(new Rectangle(0,0,595,842),true).toEps());
				if (!(objects.getFirst() instanceof EpsColor)) {
					// set color to default foreground color only if
					// it is not overwritten by the first object in list
					w.write(new EpsColor(Color.BLACK).toEps());
				}
			}
			// append foreground objects
			w.write("%\n% foreground objects\n");
			int height = 792;
			if (bb!=null) height = bb.height; 
			// define 'height' in postscript for use in EpsString
			w.write("/height { " + height + " } def\n");
			w.write("/rad { 360 3.14159 div mul } def\n");
			for (EpsObject o: objects) {
				if (o!=null) {
					w.write(o.toEps());
					w.write("\n");
				} else  {		// write comment to eps file
					EpsComment err = new EpsComment("null object ignored");
					w.write(err.toEps());
				}
			}
			w.write("grestore\n");			
		} else {	
			// draw a page-sized rectangle as background color
			if (!background.equals(Color.WHITE)) {
				sb.append("%\n% background color\n");
				sb.append(new EpsColor(background).toEps());
				sb.append(new EpsRectangle(new Rectangle(0,0,595,842),true).toEps());
				if (!(objects.getFirst() instanceof EpsColor)) {
					// set color to default foreground color only if
					// it is not overwritten by the first object in list
					sb.append(new EpsColor(Color.BLACK).toEps());
				}
			}
			// append foreground objects
			sb.append("%\n% foreground objects\n");
			int height = 792;
			if (bb!=null) height = bb.height; 
			// define 'height' in postscript for use in EpsString
			sb.append("/height { " + height + " } def\n");	
			for (EpsObject o: objects) {
				if (o!=null) {
					sb.append(o.toEps());
					sb.append("\n");
				} else  {		// write comment to eps file
					EpsComment err = new EpsComment("null object ignored");
					sb.append(err.toEps());
				}
			}
			sb.append("grestore\n");
		}
		return sb.toString();
	}
	
	/**
	 * @see java.awt.Graphics#drawRect(int, int, int, int)
	 */
	@Override
	public void drawRect(int x, int y, int width, int height) {
		super.drawRect(x, y, width, height);
		EpsRectangle er = new EpsRectangle(new Rectangle(x,y,width,height),false);
		if (!objects.contains(er)) objects.add(er);
	}
	
	/** 
	 * @return xor color
	 */
	public Color getXORColor() {
		return xor;
	}
	
	/**
	 * @return paint mode: <code>true</code> for standard paint mode
	 *  <code>false</code> for xor mode.
	 */
	 public boolean paintMode() {
		 return paintmode;
	 }
}