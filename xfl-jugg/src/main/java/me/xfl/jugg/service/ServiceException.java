package me.xfl.jugg.service;

/**
 * Service exception.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 25, 2011
 */
public final class ServiceException extends Exception {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public default constructor.
	 */
	public ServiceException() {
		super("Service exception!");
	}

	/**
	 * Public constructor with {@link Throwable}.
	 * 
	 * @param throwable
	 *            the specified throwable object
	 */
	public ServiceException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * Public constructor with message.
	 * 
	 * @param msg
	 *            the specified message
	 */
	public ServiceException(final String msg) {
		super(msg);
	}
}
