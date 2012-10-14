package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Page repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Dec 31, 2011
 * @since 0.3.1
 */
public interface PageRepository extends Repository {

	/**
	 * Gets a page by the specified permalink.
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return page, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByPermalink(final String permalink) throws RepositoryException;

	/**
	 * Gets the maximum order.
	 * 
	 * @return order number, returns {@code -1} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	int getMaxOrder() throws RepositoryException;

	/**
	 * Gets the upper page of the page specified by the given id.
	 * 
	 * @param id
	 *            the given id
	 * @return upper page, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getUpper(final String id) throws RepositoryException;

	/**
	 * Gets the under page of the page specified by the given id.
	 * 
	 * @param id
	 *            the given id
	 * @return under page, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getUnder(final String id) throws RepositoryException;

	/**
	 * Gets a page by the specified order.
	 * 
	 * @param order
	 *            the specified order
	 * @return page, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByOrder(final int order) throws RepositoryException;

	/**
	 * Gets pages.
	 * 
	 * @return a list of pages, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getPages() throws RepositoryException;
}
