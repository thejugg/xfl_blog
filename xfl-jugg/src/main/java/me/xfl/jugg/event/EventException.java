package me.xfl.jugg.event;

/**
 * Event exception.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 16, 2010
 */
public final class EventException extends Exception {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Public default constructor.
	 */
	public EventException() {
		super("Event exception!");
	}

	/**
	 * Public constructor with {@link Throwable}.
	 * 
	 * @param throwable
	 *            the specified throwable object
	 */
	public EventException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * Public constructor with message.
	 * 
	 * @param msg
	 *            the specified message
	 */
	public EventException(final String msg) {
		super(msg);
	}
}
