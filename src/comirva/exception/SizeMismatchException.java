/*
 * Created on 28.10.2004
 */
package comirva.exception;

/**
 * The SizeMismatchException is thrown when two Vectors that
 * should have the same size (e.g. for calculating the
 * Euclidean distance) actually have not.
 *
 * @author Markus Schedl
 */
public class SizeMismatchException extends Exception {

	/**
	 * Creates a new SizeMismatchException-instance.
	 */
	public SizeMismatchException() {
		super();
	}

	/**
	 * Creates a new SizeMismatchException-instance.
	 * 
	 * @param description	 a String containing a message or description of the occurred error
	 */
	public SizeMismatchException(String description) {
		super(description);
	}

}
