package me.xfl.jugg.cache.local.util;

import java.io.Serializable;

/**
 * This is a generic thread safe double linked map. It's very simple and all the
 * operations are so quick that course grained synchronization is more than
 * acceptable.
 * 
 * @param <K>
 *            the type of the key of this map's elements
 * @param <V>
 *            the type of the nodes of this map
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.5, Oct 26, 2010
 */
public final class DoubleLinkedMap<K, V> implements Serializable {

	/**
	 * Default serial version uid.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * record size to avoid having to iterate.
	 */
	private int size = 0;
	/**
	 * LRU double linked map head node.
	 */
	private DoubleLinkedMapNode<K, V> first;
	/**
	 * . LRU double linked map tail node.
	 */
	private DoubleLinkedMapNode<K, V> last;

	/**
	 * Removes a velue in the map by the specified key.
	 * 
	 * @param key
	 *            the specified key
	 * @return {@code true} if removed, {@code false} for not found
	 */
	public synchronized boolean remove(final K key) {
		final DoubleLinkedMapNode<K, V> node = getNode(key);
		if (node != null) {
			removeNode(node);

			return true;
		}

		return false;
	}

	/**
	 * Gets a value in the map by the specified key.
	 * 
	 * @param key
	 *            the specified key
	 * @return the value of the specified key, if not found, returns
	 *         <code>null</code>
	 */
	public synchronized V get(final K key) {
		if (first == null) {
			return null;
		} else {
			DoubleLinkedMapNode<K, V> current = first;
			while (current != null) {
				if (current.getKey().equals(key)) {
					return current.getValue();
				} else {
					current = current.getNext();
				}
			}
		}

		return null;
	}

	/**
	 * Adds a new value to the end of the linked map.
	 * 
	 * @param key
	 *            the key of the new value
	 * @param value
	 *            the new value
	 */
	public synchronized void addLast(final K key, final V value) {
		final DoubleLinkedMapNode<K, V> node = new DoubleLinkedMapNode<K, V>(key, value);
		addLastNode(node);
	}

	/**
	 * Adds a new value to the start of the linked map.
	 * 
	 * <p>
	 * Throws {@link IllegalArgumentException} if the specified key is null
	 * </p>
	 * 
	 * @param key
	 *            the key of the new value
	 * @param value
	 *            the new value
	 */
	public synchronized void addFirst(final K key, final V value) {
		if (null == key) {
			throw new IllegalArgumentException("Key is null!");
		}

		final DoubleLinkedMapNode<K, V> node = new DoubleLinkedMapNode<K, V>(key, value);
		addFirstNode(node);
	}

	/**
	 * Moves an existing node to the start of the linked map.
	 * 
	 * @param key
	 *            the key of the node to set as the head.
	 */
	public synchronized void makeFirst(final K key) {
		final DoubleLinkedMapNode<K, V> node = getNode(key);
		if (node.getPrev() == null) {
			// already the first node or not a node.
			return;
		}

		node.getPrev().setNext(node.getNext());

		if (node.getNext() == null) {
			// last but not the first.
			last = node.getPrev();
			last.setNext(null);
		} else {
			// neither the last nor the first.
			node.getNext().setPrev(node.getPrev());
		}

		first.setPrev(node);
		node.setNext(first);
		node.setPrev(null);
		first = node;
	}

	/**
	 * Remove all of the nodes of the linked map.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void removeAll() {
		for (DoubleLinkedMapNode<K, V> me = first; me != null;) {
			if (me.getPrev() != null) {
				me.setPrev(null);
			}

			final DoubleLinkedMapNode<K, V> next = me.getNext();
			me = next;
		}

		first = null;
		last = null;
		// make sure this will work, could be add while this is happening.
		size = 0;
	}

	/**
	 * Removes the last value.
	 * 
	 * @return last value removed if success, {@code null} otherwise
	 */
	public synchronized V removeLast() {
		final DoubleLinkedMapNode<K, V> lastNode = removeLastNode();

		if (null != lastNode) {
			return lastNode.getValue();
		}

		return null;
	}

	/**
	 * Returns the current size of the map.
	 * 
	 * @return the current size of the map
	 */
	public synchronized int size() {
		return size;
	}

	/**
	 * Removes the last node of the linked map.
	 * 
	 * @return the last node if there was one to be removed.
	 */
	private synchronized DoubleLinkedMapNode<K, V> removeLastNode() {
		final DoubleLinkedMapNode<K, V> ret = last;

		if (last != null) {
			removeNode(last);
		}

		return ret;
	}

	/**
	 * Gets a node in the map by the specified key.
	 * 
	 * @param key
	 *            the specified key
	 * @return a node of specified key, if not found, returns <code>null</code>
	 */
	private synchronized DoubleLinkedMapNode<K, V> getNode(final K key) {
		if (first == null) {
			return null;
		} else {
			DoubleLinkedMapNode<K, V> current = first;
			while (current != null) {
				if (current.getKey().equals(key)) {
					return current;
				} else {
					current = current.getNext();
				}
			}
		}

		return null;
	}

	/**
	 * Removes the specified node of the linked map.
	 * 
	 * @param node
	 *            the specified node to be removed
	 */
	private synchronized void removeNode(final DoubleLinkedMapNode<K, V> node) {
		if (node.getNext() == null) {
			if (node.getPrev() == null) {
				// make sure it really is the only node before setting head and
				// tail to null. It is possible that we will be passed a node
				// which has already been removed from the map, in which case
				// we should ignore it.
				if (node == first && node == last) {
					first = null;
					last = null;
				}

			} else {
				// last but not the first.
				last = node.getPrev();
				last.setNext(null);
				node.setPrev(null);
			}

		} else if (node.getPrev() == null) {
			// first but not the last.
			first = node.getNext();
			first.setPrev(null);
			node.setNext(null);
		} else {
			// neither the first nor the last.
			node.getPrev().setNext(node.getNext());
			node.getNext().setPrev(node.getPrev());
			node.setPrev(null);
			node.setNext(null);
		}

		size--;
	}

	/**
	 * Adds a new node to the end of the linked map.
	 * 
	 * @param node
	 *            the feature to be added to the end of the linked map
	 */
	private synchronized void addLastNode(final DoubleLinkedMapNode<K, V> node) {
		if (first == null) {
			// empty map.
			first = node;
		} else {
			last.setNext(node);
			node.setPrev(last);
		}

		last = node;
		size++;
	}

	/**
	 * Adds a new node to the start of the linked map.
	 * 
	 * @param node
	 *            the feature to be added to the start of the linked map
	 */
	private synchronized void addFirstNode(final DoubleLinkedMapNode<K, V> node) {
		if (last == null) {
			// empty map.
			last = node;
		} else {
			first.setPrev(node);
			node.setNext(first);
		}

		first = node;
		size++;
	}

}
