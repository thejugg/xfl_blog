package me.xfl.dragon.repository;

import java.util.List;

import me.xfl.jugg.repository.Repository;
import me.xfl.jugg.repository.RepositoryException;

import org.json.JSONObject;

/**
 * Tag repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 12, 2010
 */
public interface TagRepository extends Repository {

	/**
	 * Gets tags of an article specified by the article id.
	 * 
	 * @param articleId
	 *            the specified article id
	 * @return a list of tags of the specified article, returns an empty list if
	 *         not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getByArticleId(final String articleId) throws RepositoryException;

	/**
	 * Gets a tag by the specified tag title.
	 * 
	 * @param tagTitle
	 *            the specified tag title
	 * @return a tag, {@code null} if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	JSONObject getByTitle(final String tagTitle) throws RepositoryException;

	/**
	 * Gets most used tags with the specified number.
	 * 
	 * @param num
	 *            the specified number
	 * @return a list of most used tags, returns an empty list if not found
	 * @throws RepositoryException
	 *             repository exception
	 */
	List<JSONObject> getMostUsedTags(final int num) throws RepositoryException;
}
