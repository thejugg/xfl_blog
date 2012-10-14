package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Tag-Article repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Nov 8, 2011
 */
public interface TagArticleRepository extends Repository {

	/**
	 * Gets tag-article relations by the specified article id.
	 * 
	 * @param articleId
	 *            the specified article id
	 * @return for example
	 * 
	 *         <pre>
	 * [{
	 *         "oId": "",
	 *         "tag_oId": "",
	 *         "article_oId": articleId
	 * }, ....], returns an empty list if not found
	 * </pre>
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getByArticleId(final String articleId) throws RepositoryException;

	/**
	 * Gets tag-article relations by the specified tag id.
	 * 
	 * @param tagId
	 *            the specified tag id
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
	 *         "tag_oId": tagId,
	 *         "article_oId": ""
	 *     }, ....]
	 * }
	 * </pre>
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByTagId(final String tagId, final int currentPageNum, final int pageSize) throws RepositoryException;
}
