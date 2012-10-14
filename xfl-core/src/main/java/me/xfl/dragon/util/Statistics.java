package me.xfl.dragon.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Statistic;
import me.xfl.dragon.repository.StatisticRepository;
import me.xfl.dragon.repository.impl.StatisticRepositoryImpl;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.util.Requests;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Statistic utilities.
 * 
 * <p>
 * <b>Note</b>: The {@link #onlineVisitorCount online visitor counting} is NOT
 * cluster-safe.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.0, Jul 16, 2012
 * @since 0.3.1
 */
public final class Statistics {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Statistics.class.getName());
	/**
	 * Statistic repository.
	 */
	private StatisticRepository statisticRepository = StatisticRepositoryImpl.getInstance();
	/**
	 * Repository cache prefix, refers to GAERepository#CACHE_KEY_PREFIX.
	 */
	public static final String REPOSITORY_CACHE_KEY_PREFIX = "repository";
	/**
	 * Online visitor cache.
	 * 
	 * <p>
	 * &lt;ip, recentTime&gt;
	 * </p>
	 */
	private static final Map<String, Long> ONLINE_VISITORS = new HashMap<String, Long>();
	/**
	 * Online visitor expiration in 5 minutes.
	 */
	private static final int ONLINE_VISITOR_EXPIRATION = 300000;

	/**
	 * Gets the online visitor count.
	 * 
	 * @return online visitor count
	 */
	public static int getOnlineVisitorCount() {
		return ONLINE_VISITORS.size();
	}

	/**
	 * Refreshes online visitor count for the specified request.
	 * 
	 * @param request
	 *            the specified request
	 */
	public static void onlineVisitorCount(final HttpServletRequest request) {
		ONLINE_VISITORS.put(request.getRemoteAddr(), System.currentTimeMillis());
		LOGGER.log(Level.INFO, "Current online visitor count [{0}]", ONLINE_VISITORS.size());
	}

	/**
	 * Removes the expired online visitor.
	 */
	public static void removeExpiredOnlineVisitor() {
		final long currentTimeMillis = System.currentTimeMillis();

		final Iterator<Entry<String, Long>> iterator = ONLINE_VISITORS.entrySet().iterator();
		while (iterator.hasNext()) {
			final Entry<String, Long> onlineVisitor = iterator.next();

			if (currentTimeMillis > (onlineVisitor.getValue() + ONLINE_VISITOR_EXPIRATION)) {
				iterator.remove();
				LOGGER.log(Level.FINEST, "Removed online visitor[ip={0}]", onlineVisitor.getKey());
			}
		}

		LOGGER.log(Level.INFO, "Current online visitor count [{0}]", ONLINE_VISITORS.size());
	}

	/**
	 * Get blog comment count.
	 * 
	 * @return blog comment count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public int getBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		return statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT);
	}

	/**
	 * Get blog comment(published article) count.
	 * 
	 * @return blog comment count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public int getPublishedBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		return statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
	}

	/**
	 * Sets blog comment count with the specified count.
	 * 
	 * @param count
	 *            the specified count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void setBlogCommentCount(final int count) throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, count);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Sets blog comment(published article) count with the specified count.
	 * 
	 * @param count
	 *            the specified count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void setPublishedBlogCommentCount(final int count) throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, count);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Gets blog statistic published article count.
	 * 
	 * @return published blog article count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public int getPublishedBlogArticleCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		return statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT);
	}

	/**
	 * Gets blog statistic article count.
	 * 
	 * @return blog article count
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public int getBlogArticleCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		return statistic.getInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT);
	}

	/**
	 * Blog statistic view count +1.
	 * 
	 * <p>
	 * If it is a search engine bot made the specified request, will NOT
	 * increment blog statistic view count.
	 * </p>
	 * 
	 * <p>
	 * There is a cron job (/console/stat/viewcnt) to flush the blog view count
	 * from cache to datastore.
	 * </p>
	 * 
	 * @param request
	 *            the specified request
	 * @throws RepositoryException
	 *             repository exception
	 * @throws JSONException
	 *             json exception
	 * @see Requests#searchEngineBotRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void incBlogViewCount(final HttpServletRequest request,final HttpServletResponse response) throws RepositoryException, JSONException {
		if (Requests.searchEngineBotRequest(request)) {
			return;
		}
		
		if (Requests.hasBeenServed(request, response)) {
            return;
        }

		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			return;
		}

		LOGGER.log(Level.FINEST, "Before inc blog view count[statistic={0}]", statistic);

		int blogViewCnt = statistic.getInt(Statistic.STATISTIC_BLOG_VIEW_COUNT);
		++blogViewCnt;
		statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, blogViewCnt);

		// Repository cache prefix, Refers to GAERepository#CACHE_KEY_PREFIX
		statisticRepository.getCache().putAsync(REPOSITORY_CACHE_KEY_PREFIX + Statistic.STATISTIC, statistic);

		LOGGER.log(Level.FINER, "Inced blog view count[statistic={0}]", statistic);
	}

	/**
	 * Blog statistic article count +1.
	 * 
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void incBlogArticleCount() throws RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, statistic.optInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT) + 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic published article count +1.
	 * 
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void incPublishedBlogArticleCount() throws RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic.optInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) + 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic article count -1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void decBlogArticleCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT) - 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic published article count -1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void decPublishedBlogArticleCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, statistic.getInt(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) - 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic comment count +1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void incBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}
		statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT) + 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic comment(published article) count +1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void incPublishedBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}
		statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
				statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) + 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic comment count -1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void decBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, statistic.getInt(Statistic.STATISTIC_BLOG_COMMENT_COUNT) - 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Blog statistic comment(published article) count -1.
	 * 
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void decPublishedBlogCommentCount() throws JSONException, RepositoryException {
		final JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
		if (null == statistic) {
			throw new RepositoryException("Not found statistic");
		}

		statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
				statistic.getInt(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) - 1);
		statisticRepository.update(Statistic.STATISTIC, statistic);
	}

	/**
	 * Gets the {@link Statistics} singleton.
	 * 
	 * @return the singleton
	 */
	public static Statistics getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private Statistics() {
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Jan 12, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final Statistics SINGLETON = new Statistics();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
