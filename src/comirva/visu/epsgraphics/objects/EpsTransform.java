package comirva.visu.epsgraphics.objects;

import java.awt.geom.AffineTransform;

/** this class represents an eps transformation.
 * It is used to represent affine transformations
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsTransform implements EpsObject {

	/** user defined transformation */
	public static final int TYPE_TRANSFORM = 0;
	/** scaling */
	public static final int TYPE_SCALE = 1;
	/** translation */
	public static final int TYPE_TRANSLATE = 2;
	/** rotation */
	public static final int TYPE_ROTATE = 3;
	/** "extended rotation" consisting of translation, rotation,
	 * and reverse translation */
	public static final int TYPE_ROTATE2 = 4;
	/** performs shearing */
	public static final int TYPE_SHEAR = 5;
	
	/** postscript code definition */
	protected static final String[] code = { 
		" concat", " scale", " translate", " rotate", " rotate", " concat"
	};
	
	/** transformation types. Must be one of 
	 *  {@link #TYPE_SCALE}, {@link #TYPE_ROTATE}, {@link #TYPE_ROTATE2}
	 *  {@link #TYPE_SHEAR} and {@link #TYPE_TRANSFORM}
	 */
	protected int type;
	/** x parameter for points/vectors */
	protected double x;
	/** y parameter for points/vectors */
	protected double y;
	/** theta parameter for angles (in degree)*/
	protected double theta;
	/** user defined transformations */
	protected AffineTransform at = AffineTransform.getTranslateInstance(0,0);
	/** boolean defining whether given theta is in degree or radiant, default: rad */
	protected boolean rad = true;
	
	/** 
	 * Constructs an Eps Transformation object which is equivalent
	 * by the given AffineTransform
	 * @param at The transformation which should be represented by this object
	 */	
	public EpsTransform(AffineTransform at) {	// transform
		super();
		this.at = at!=null?at:AffineTransform.getTranslateInstance(0.0,0.0);
	}
	
	/** 
	 * Constructs an Eps Transformation object which performs
	 * a transformation given by the parameters. The meaning of the
	 * x and y parameters depends on the given type but it is
	 * equivalent when using an AffineTransform.
	 * @param type The type of the transformation. 
	 * 	May be {@link #TYPE_TRANSLATE}, {@link #TYPE_SCALE} or {@link #TYPE_SHEAR}.
	 * @param x The x parameter for the transformation
	 * @param y The y coordinate for the transformation
	 */
	public EpsTransform(int type, double x, double y) {	// translate/scale/shear
		super();
		this.type=type;
		this.x=x;
		this.y=y;
	}

	/**
	 * Constructs an Eps Transformation object which performs
	 * a rotation by a given angle theta in degree.
	 * @param theta the rotation angle in degree (0-360).
	 */
	public EpsTransform(double theta) {		// rotation
		super();
		this.type = TYPE_ROTATE;
		this.theta=theta;
	}
	
	/**
	 * Constructs an Eps Transfomration object which performs
	 * an "extended rotation": Calling this method is equivalent to
	 * {@code translate(x,y);
	 * 		  rotate(theta);
	 *        translate(-x,-y);}
	 * @param theta The rotation angle in degree
	 * @param x The x coordinate for translation
	 * @param y The y coordinate for translation
	 */
	public EpsTransform(double theta, double x, double y) {		// rotation2
		super();
		this.type = TYPE_ROTATE2;
		this.theta=theta;
		this.x=x;
		this.y=y;
	}	
	
	/** specify theta angles to be radiant values.
	 *  the given theta values are regarded to be radiant values
	 *  and therefore multiplied by 360/PI.
	 */
	public void setToRadiant() {
		rad=true;
	}
	
	/** specify theta angles to be degree values (range 0-360)
	 *  the given values are not changed.
	 *
	 */
	public void setToDegree() {
		rad=false;
	}
	
	/** ability to check if angles are treated as
	 *  radiant or degree values
	 * @return rad (true if radiant, false if degree)
	 */
	public boolean isRadiant() {
		return rad;
	}
	
	public String toEps() {
		StringBuffer sb = new StringBuffer();
		sb.append("% EpsTransform\n");
		if (rad) theta = Math.toDegrees(theta);	// rad2degree
		switch (type) {
		case TYPE_TRANSLATE: case TYPE_SCALE:		// translate / scale
			sb.append(x);
			sb.append(" ");
			sb.append(y);
			sb.append(code[type]);
			break;
		case TYPE_ROTATE:							// rotate
			sb.append(theta);
			sb.append(code[type]);
			break;
		case TYPE_ROTATE2:				// translate - rotate - translate back
			sb.append(x);
			sb.append(" ");
			sb.append(y);
			sb.append(code[TYPE_TRANSLATE]);
			sb.append("\n");
			sb.append(theta);
			sb.append(code[type]);
			sb.append("\n");
			sb.append(-x);
			sb.append(" ");
			sb.append(-y);
			sb.append(code[TYPE_TRANSLATE]);
			break;
		case TYPE_SHEAR:					// shear - just a special matrix
			at = new AffineTransform();
			at.setToShear(x,y);
			// no break!	
		case TYPE_TRANSFORM:						// apply given matrix
			double[] matrix = new double[6];
			at.getMatrix(matrix);
			for (double x: matrix) {
				sb.append(x);
				sb.append(" ");
			}
			sb.append(code[type]);
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsTransform) {
			EpsTransform other = (EpsTransform)obj;
			return this.at.equals(other.at) &&
				(this.rad==other.rad) &&
				(this.theta==other.theta) &&
				(this.type==other.type) &&
				(this.x==other.x) &&
				(this.y==other.y);
		}
		return false;
	}
	
}
