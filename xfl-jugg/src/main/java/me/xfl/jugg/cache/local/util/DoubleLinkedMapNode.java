package me.xfl.jugg.cache.local.util;

import java.io.Serializable;

/**
 * A node of {@link DoubleLinkedMap double linked map}.
 * 
 * @param <K>
 *            the type of the key of this node's element
 * @param <V>
 *            the type of this node
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.1, Aug 15, 2010
 */
final class DoubleLinkedMapNode<K, V> implements Serializable {

	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -5617593667027497669L;
	/**
	 * Payload of this node.
	 */
	private V value;
	/**
	 * Key of this node.
	 */
	private K key;
	/**
	 * Double linked map previous reference.
	 */
	private DoubleLinkedMapNode<K, V> prev;
	/**
	 * Double linked map next reference.
	 */
	private DoubleLinkedMapNode<K, V> next;

	/**
	 * Constructs a double linked map node with the specified key and value.
	 * 
	 * @param key
	 *            the key of the specified node's content instance
	 * @param value
	 *            the specified node's content instance
	 */
	public DoubleLinkedMapNode(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the key of this node.
	 * 
	 * @return the key of this node
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Gets the value of this node.
	 * 
	 * @return the node's content instance.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Gets the next node of this node.
	 * 
	 * @return the next node of this node
	 */
	protected DoubleLinkedMapNode<K, V> getNext() {
		return next;
	}

	/**
	 * Sets the next node of this node by the specified next node.
	 * 
	 * @param next
	 *            the specified next node
	 */
	protected void setNext(final DoubleLinkedMapNode<K, V> next) {
		this.next = next;
	}

	/**
	 * Gets the previous node of this node.
	 * 
	 * @return the previous node of this node
	 */
	protected DoubleLinkedMapNode<K, V> getPrev() {
		return prev;
	}

	/**
	 * Sets the previous node of this node by the specified previous node.
	 * 
	 * @param prev
	 *            the specified previous node
	 */
	protected void setPrev(final DoubleLinkedMapNode<K, V> prev) {
		this.prev = prev;
	}

}
