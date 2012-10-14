package me.xfl.jugg.event;

/**
 * Event.
 * 
 * @param <T>
 *            the type of the event data
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 16, 2010
 */
public final class Event<T> {

	/**
	 * Type of this event.
	 */
	private String type;
	/**
	 * Data of this event.
	 */
	private T data;

	/**
	 * Constructs a {@link Event} object with the specified type and data.
	 * 
	 * @param type
	 *            the specified type
	 * @param data
	 *            the specified data
	 */
	public Event(final String type, final T data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Gets the type of this event.
	 * 
	 * @return the type of this event
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the data of this event.
	 * 
	 * @return the data of this event
	 */
	public T getData() {
		return data;
	}
}
