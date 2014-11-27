package comirva.visu.epsgraphics.objects;

/**
 * This interface marks a class as postscript object.
 * @author Florian Marchl
 * @version 1.0
 */
public interface EpsObject {
	
	/** returns a String that represents the object 
	 *  in postscript code */
	public String toEps();
}
