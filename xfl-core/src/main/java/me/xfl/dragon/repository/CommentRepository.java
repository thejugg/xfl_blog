package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Comment repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Oct 26, 2011
 */
public interface CommentRepository extends Repository {

	/**
	 * Gets post comments recently with the specified fetch.
	 * 
	 * @param fetchSize
	 *            the specified fetch size
	 * @return a list of comments recently, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getRecentComments(final int fetchSize) throws RepositoryException;

	/**
	 * Gets comments with the specified on id, current page number and page
	 * size.
	 * 
	 * @param onId
	 *            the specified on id
	 * @param currentPageNum
	 *            the specified current page number
	 * @param pageSize
	 *            the specified page size
	 * @return a list of comments, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getComments(final String onId, final int currentPageNum, final int pageSize) throws RepositoryException;

	/**
	 * Removes comments with the specified on id.
	 * 
	 * @param onId
	 *            the specified on id
	 * @return removed count
	 * @throws RepositoryException
	 *             repository exception
	 */
	int removeComments(final String onId) throws RepositoryException;
}
