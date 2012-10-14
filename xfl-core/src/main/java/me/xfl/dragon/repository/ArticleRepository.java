package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Article repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.9, Dec 31, 2011
 * @since 0.3.1
 */
public interface ArticleRepository extends Repository {

	/**
	 * Gets published articles by the specified author email, current page
	 * number and page size.
	 * 
	 * @param authorEmail
	 *            the specified author email
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
	 *         // article keys....
	 *     }, ....]
	 * }
	 * </pre>
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByAuthorEmail(final String authorEmail, final int currentPageNum, final int pageSize) throws RepositoryException;

	/**
	 * Gets an article by the specified permalink.
	 * 
	 * @param permalink
	 *            the specified permalink
	 * @return an article, returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByPermalink(final String permalink) throws RepositoryException;

	/**
	 * Gets post articles recently with the specified fetch size.
	 * 
	 * @param fetchSize
	 *            the specified fetch size
	 * @return a list of articles recently, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getRecentArticles(final int fetchSize) throws RepositoryException;

	/**
	 * Gets most commented and published articles with the specified number.
	 * 
	 * @param num
	 *            the specified number
	 * @return a list of most comment articles, returns an empty list if not
	 *         found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getMostCommentArticles(final int num) throws RepositoryException;

	/**
	 * Gets most view count and published articles with the specified number.
	 * 
	 * @param num
	 *            the specified number
	 * @return a list of most view count articles, returns an empty list if not
	 *         found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getMostViewCountArticles(final int num) throws RepositoryException;

	/**
	 * Gets the previous article(by create date) by the specified article id.
	 * 
	 * @param articleId
	 *            the specified article id
	 * @return the previous article,
	 * 
	 *         <pre>
	 * {
	 *     "articleTitle": "",
	 *     "articlePermalink": ""
	 * }
	 * </pre>
	 * 
	 *         returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getPreviousArticle(final String articleId) throws RepositoryException;

	/**
	 * Gets the next article(by create date, oId) by the specified article id.
	 * 
	 * @param articleId
	 *            the specified article id
	 * @return the next article,
	 * 
	 *         <pre>
	 * {
	 *     "articleTitle": "",
	 *     "articlePermalink": ""
	 * }
	 * </pre>
	 * 
	 *         returns {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getNextArticle(final String articleId) throws RepositoryException;

	/**
	 * Determines an article specified by the given article id is published.
	 * 
	 * @param articleId
	 *            the given article id
	 * @return {@code true} if it is published, {@code false} otherwise
	 * @throws RepositoryException
	 *             repository exception
	 */
	boolean isPublished(final String articleId) throws RepositoryException;
}
