package me.xfl.dragon.repository;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Link repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Nov 2, 2011
 * @since 0.3.1
 */
public interface LinkRepository extends Repository {

	/**
	 * Gets a link by the specified address.
	 * 
	 * @param address
	 *            the specified address
	 * @return link, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByAddress(final String address) throws RepositoryException;

	/**
	 * Gets the maximum order.
	 * 
	 * @return order number, returns {@code -1} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	int getMaxOrder() throws RepositoryException;

	/**
	 * Gets the upper link of the link specified by the given id.
	 * 
	 * @param id
	 *            the given id
	 * @return upper link, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getUpper(final String id) throws RepositoryException;

	/**
	 * Gets the under link of the link specified by the given id.
	 * 
	 * @param id
	 *            the given id
	 * @return under link, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getUnder(final String id) throws RepositoryException;

	/**
	 * Gets a link by the specified order.
	 * 
	 * @param order
	 *            the specified order
	 * @return link, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByOrder(final int order) throws RepositoryException;
}
