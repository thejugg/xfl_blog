package me.xfl.dragon.repository;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Archive date-Article repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Nov 9, 2011
 */
public interface ArchiveDateArticleRepository extends Repository {

	/**
	 * Gets archive date-article relations by the specified archive date id.
	 * 
	 * @param archiveDateId
	 *            the specified archive date id
	 * @param currentPageNum
	 *            the specified current page number, MUST greater then {@code 0}
	 * @param pageSize
	 *            the specified page size(count of a page contains objects),
	 *            MUST greater then {@code 0}
	 * @return for example
	 * 
	 *         <pre>
	 * {
	 *     "pagination": {
	 *       "paginationPageCount": 88250
	 *     },
	 *     "rslts": [{
	 *         "oId": "",
	 *         "archiveDate_oId": "",
	 *         "article_oId": ""
	 *     }, ....]
	 * }
	 * </pre>
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByArchiveDateId(final String archiveDateId, final int currentPageNum, final int pageSize) throws RepositoryException;

	/**
	 * Gets an archive date-article relations by the specified article id.
	 * 
	 * @param articleId
	 *            the specified article id
	 * @return for example
	 * 
	 *         <pre>
	 * {
	 *     "archiveDate_oId": "",
	 *     "article_oId": articleId
	 * }, returns {@code null} if not found
	 * </pre>
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByArticleId(final String articleId) throws RepositoryException;
}
