/*
 * Created on 22.10.2004
 */
package comirva.exception;

/**
 * The NoMatrixException is thrown when a file that should
 * be an ASCII-matrix file is read but the file turns out
 * not to contain a valid matrix.
 * 
 * @author Markus Schedl
 */
public class NoMatrixException extends Exception {
	
	/**
	 * Creates a new NoMatrixException-instance.
	 */
	public NoMatrixException() {
		super();
	}
	
	/**
	 * Creates a new NoMatrixException-instance.
	 * 
	 * @param description	a String containing a message or description of the occurred error
	 */
	public NoMatrixException(String description) {
		super(description);
	}

}
