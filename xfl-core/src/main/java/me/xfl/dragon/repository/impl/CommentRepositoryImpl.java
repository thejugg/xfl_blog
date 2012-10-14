package me.xfl.dragon.repository.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Comment;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.CommentRepository;
import me.xfl.jugg.Keys;
import me.xfl.jugg.cache.Cache;
import me.xfl.jugg.repository.AbstractRepository;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.SortDirection;
import me.xfl.jugg.util.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Comment repository.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Oct 18, 2011
 * @since 0.3.1
 */
public final class CommentRepositoryImpl extends AbstractRepository implements CommentRepository {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CommentRepositoryImpl.class.getName());
	/**
	 * Singleton.
	 */
	private static final CommentRepositoryImpl SINGLETON = new CommentRepositoryImpl(Comment.COMMENT);
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * Recent comments query results cache key.
	 */
	public static final String RECENT_CMTS_CACHE_KEY = "recentCMTs";

	@Override
	public int removeComments(final String onId) throws RepositoryException {
		final List<JSONObject> comments = getComments(onId, 1, Integer.MAX_VALUE);

		for (final JSONObject comment : comments) {
			final String commentId = comment.optString(Keys.OBJECT_ID);
			remove(commentId);
		}

		LOGGER.log(Level.FINER, "Removed comments[onId={0}, removedCnt={1}]", new Object[] { onId, comments.size() });

		return comments.size();
	}

	@Override
	public List<JSONObject> getComments(final String onId, final int currentPageNum, final int pageSize) throws RepositoryException {
		final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.DESCENDING)
				.setFilter(new PropertyFilter(Comment.COMMENT_ON_ID, FilterOperator.EQUAL, onId)).setCurrentPageNum(currentPageNum)
				.setPageSize(pageSize).setPageCount(1);

		final JSONObject result = get(query);

		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		return CollectionUtils.jsonArrayToList(array);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JSONObject> getRecentComments(final int num) throws RepositoryException {
		if (isCacheEnabled()) {
			final Cache<String, Serializable> cache = getCache();
			final Object ret = cache.get(RECENT_CMTS_CACHE_KEY);
			if (null != ret) {
				return (List<JSONObject>) ret;
			}
		}

		final Query query = new Query().addSort(Keys.OBJECT_ID, SortDirection.DESCENDING).setCurrentPageNum(1).setPageSize(num)
				.setPageCount(1);

		List<JSONObject> ret;
		final JSONObject result = get(query);

		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		ret = CollectionUtils.jsonArrayToList(array);

		// Removes unpublished article related comments
		removeForUnpublishedArticles(ret);

		if (isCacheEnabled()) {
			final Cache<String, Serializable> cache = getCache();
			cache.put(RECENT_CMTS_CACHE_KEY, (Serializable) ret);
		}

		return ret;
	}

	/**
	 * Removes comments of unpublished articles for the specified comments.
	 * 
	 * @param comments
	 *            the specified comments
	 * @throws RepositoryException
	 *             repository exception
	 */
	private void removeForUnpublishedArticles(final List<JSONObject> comments) throws RepositoryException {
		LOGGER.finer("Removing unpublished articles' comments....");
		final Iterator<JSONObject> iterator = comments.iterator();
		while (iterator.hasNext()) {
			final JSONObject comment = iterator.next();
			final String commentOnType = comment.optString(Comment.COMMENT_ON_TYPE);
			if (Article.ARTICLE.equals(commentOnType)) {
				final String articleId = comment.optString(Comment.COMMENT_ON_ID);

				if (!articleRepository.isPublished(articleId)) {
					iterator.remove();
				}
			}
		}

		LOGGER.finer("Removed unpublished articles' comments....");
	}

	/**
	 * Gets the {@link CommentRepositoryImpl} singleton.
	 * 
	 * @return the singleton
	 */
	public static CommentRepositoryImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * Private constructor.
	 * 
	 * @param name
	 *            the specified name
	 */
	private CommentRepositoryImpl(final String name) {
		super(name);
	}
}
