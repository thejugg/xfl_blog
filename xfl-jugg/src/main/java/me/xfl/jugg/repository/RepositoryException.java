package me.xfl.jugg.repository;

/**
 * Repository exception.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Feb 28, 2012
 */
public class RepositoryException extends Exception {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public default constructor.
	 */
	public RepositoryException() {
		super("Repository exception!");
	}

	/**
	 * Public constructor with {@link Throwable}.
	 * 
	 * @param throwable
	 *            the specified throwable object
	 */
	public RepositoryException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * Public constructor with message.
	 * 
	 * @param msg
	 *            the specified message
	 */
	public RepositoryException(final String msg) {
		super(msg);
	}
}
