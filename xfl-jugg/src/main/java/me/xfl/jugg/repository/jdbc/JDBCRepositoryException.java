package me.xfl.jugg.repository.jdbc;

import me.xfl.jugg.repository.RepositoryException;

/**
 * JDBC repository exception.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Feb 28, 2012
 */
public final class JDBCRepositoryException extends RepositoryException {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public default constructor.
	 */
	public JDBCRepositoryException() {
		super("JDBC repository exception!");
	}

	/**
	 * Public constructor with {@link Throwable}.
	 * 
	 * @param throwable
	 *            the specified throwable object
	 */
	public JDBCRepositoryException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * Public constructor with message.
	 * 
	 * @param msg
	 *            the specified message
	 */
	public JDBCRepositoryException(final String msg) {
		super(msg);
	}
}
