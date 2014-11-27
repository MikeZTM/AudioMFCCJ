package comirva.visu.epsgraphics.objects;

/** This class provides the possibility to add comments to the resulting
 * eps file. It is also used for the output of error messages (e.g. if
 * the Shape object given to drawShape() cannot be converted to an EpsObject).
 * 
 * @author Florian Marchl
 * @version 1.0
 */
public class EpsComment implements EpsObject {
	
	/** the comment text */
	private String comment;
	
	/** create a comment with the specified text
	 * The coment indicator '%' is automatically
	 * inserted.
	 * @param comment the comment text
	 */
	public EpsComment(String comment) {
		super();
		StringBuffer sb = new StringBuffer();
		sb.append("%");
		for (int i=0; i<comment.length(); i++) {	// '%' is only valid until next '\n'
			sb.append(comment.charAt(i));		
			if (comment.charAt(i)=='\n') {
				sb.append("%");						// insert '%' at beginning of new line
			}
		}
		this.comment = sb.toString();
	}

	public String toEps() {
		// comment already contains valid postscript comment
		return comment;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this==obj) return true;
		if (obj instanceof EpsComment) {
			EpsComment other = (EpsComment)obj;
			return this.comment.equals(other.comment);
		}
		return false;
	}

}
