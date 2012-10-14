package me.xfl.dragon.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Common;
import me.xfl.dragon.model.Preference;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.UserRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.repository.impl.UserRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.FilterOperator;
import me.xfl.jugg.repository.PropertyFilter;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.repository.SortDirection;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.user.UserService;
import me.xfl.jugg.user.UserServiceFactory;
import me.xfl.jugg.util.CollectionUtils;
import me.xfl.jugg.util.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.8, May 6, 2012
 * @since 0.3.1
 */
public final class Articles {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Articles.class.getName());
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * User repository.
	 */
	private UserRepository userRepository = UserRepositoryImpl.getInstance();
	/**
	 * User service.
	 */
	private UserService userService = UserServiceFactory.getUserService();

	/**
	 * Builds article view password form parameters with the specified article.
	 * 
	 * @param article
	 *            the specified article
	 * @return parameters string, for example,
	 * 
	 *         <pre>
	 * &quot;?articleId=xxx&amp;articleTitle=xxx&amp;articlePermalink=xxx&amp;articleAbstract=xxx&quot;
	 * </pre>
	 * @throws UnsupportedEncodingException
	 *             if can not encode the arguments
	 */
	public String buildArticleViewPwdFormParameters(final JSONObject article) throws UnsupportedEncodingException {
		final StringBuilder parametersBuilder = new StringBuilder("?articleId=").append(article.optString(Keys.OBJECT_ID))
				.append("&articleTitle=").append(URLEncoder.encode(article.optString(Article.ARTICLE_TITLE), "UTF-8"))
				.append("&articlePermalink=").append(URLEncoder.encode(article.optString(Article.ARTICLE_PERMALINK), "UTF-8"))
				.append("&articleAbstract=").append(URLEncoder.encode(article.optString(Article.ARTICLE_ABSTRACT, " "), "UTF-8"));

		return parametersBuilder.toString();
	}

	/**
	 * Checks whether need password to view the specified article with the
	 * specified request.
	 * 
	 * <p>
	 * Checks session, if not represents, checks article property
	 * {@link Article#ARTICLE_VIEW_PWD view password}.
	 * </p>
	 * 
	 * <p>
	 * The blogger itself dose not need view password never.
	 * </p>
	 * 
	 * @param request
	 *            the specified request
	 * @param article
	 *            the specified article
	 * @return {@code true} if need, returns {@code false} otherwise
	 */
	public boolean needViewPwd(final HttpServletRequest request, final JSONObject article) {
		final String articleViewPwd = article.optString(Article.ARTICLE_VIEW_PWD);

		if (Strings.isEmptyOrNull(articleViewPwd)) {
			return false;
		}

		final HttpSession session = request.getSession(false);
		if (null != session) {
			@SuppressWarnings("unchecked")
			Map<String, String> viewPwds = (Map<String, String>) session.getAttribute(Common.ARTICLES_VIEW_PWD);
			if (null == viewPwds) {
				viewPwds = new HashMap<String, String>();
			}

			if (articleViewPwd.equals(viewPwds.get(article.optString(Keys.OBJECT_ID)))) {
				return false;
			}
		}

		if (null != userService.getCurrentUser(request)) {
			return false;
		}

		return true;
	}

	/**
	 * Gets time of the recent updated article.
	 * 
	 * @return time of the recent updated article, returns {@code 0} if not
	 *         found
	 * @throws ServiceException
	 *             service exception
	 */
	public long getRecentArticleTime() throws ServiceException {
		try {
			final List<JSONObject> recentArticles = articleRepository.getRecentArticles(1);
			if (recentArticles.isEmpty()) {
				return 0;
			}

			final JSONObject recentArticle = recentArticles.get(0);
			return ((Date) recentArticle.get(Article.ARTICLE_UPDATE_DATE)).getTime();
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new ServiceException("Gets recent article time failed");
		}
	}

	/**
	 * Gets the specified article's author.
	 * 
	 * <p>
	 * The specified article has a property
	 * {@value Article#ARTICLE_AUTHOR_EMAIL}, this method will use this property
	 * to get a user from users.
	 * </p>
	 * 
	 * <p>
	 * If can't find the specified article's author (i.e. the author has been
	 * removed by administrator), returns administrator.
	 * </p>
	 * 
	 * @param article
	 *            the specified article
	 * @return user, {@code null} if not found
	 * @throws ServiceException
	 *             service exception
	 */
	public JSONObject getAuthor(final JSONObject article) throws ServiceException {
		try {
			final String email = article.getString(Article.ARTICLE_AUTHOR_EMAIL);

			JSONObject ret = userRepository.getByEmail(email);

			if (null == ret) {
				LOGGER.log(Level.WARNING, "Gets author of article failed, assumes the administrator is the author of this article[id={0}]",
						article.getString(Keys.OBJECT_ID));
				// This author may be deleted by admin, use admin as the author
				// of this article
				ret = userRepository.getAdmin();
			}

			return ret;
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Gets author of article[id={0}] failed", article.optString(Keys.OBJECT_ID));
			throw new ServiceException(e);
		} catch (final JSONException e) {
			LOGGER.log(Level.SEVERE, "Gets author of article[id={0}] failed", article.optString(Keys.OBJECT_ID));
			throw new ServiceException(e);
		}
	}

	/**
	 * Article comment count +1 for an article specified by the given article
	 * id.
	 * 
	 * @param articleId
	 *            the given article id
	 * @throws JSONException
	 *             json exception
	 * @throws RepositoryException
	 *             repository exception
	 */
	public void incArticleCommentCount(final String articleId) throws JSONException, RepositoryException {
		final JSONObject article = articleRepository.get(articleId);
		final JSONObject newArticle = new JSONObject(article, JSONObject.getNames(article));
		final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
		newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt + 1);

		articleRepository.update(articleId, newArticle);
	}

	/**
	 * Gets the sign of an article specified by the sign id.
	 * 
	 * @param signId
	 *            the specified article id
	 * @param preference
	 *            the specified preference
	 * @return article sign, returns the default sign (which oId is "1") if not
	 *         found
	 * @throws RepositoryException
	 *             repository exception
	 * @throws JSONException
	 *             json exception
	 */
	public JSONObject getSign(final String signId, final JSONObject preference) throws JSONException, RepositoryException {
		final JSONArray signs = new JSONArray(preference.getString(Preference.SIGNS));

		JSONObject defaultSign = null;
		for (int i = 0; i < signs.length(); i++) {
			final JSONObject ret = signs.getJSONObject(i);
			if (signId.equals(ret.optString(Keys.OBJECT_ID))) {
				return ret;
			}

			if ("1".equals(ret.optString(Keys.OBJECT_ID))) {
				defaultSign = ret;
			}
		}

		LOGGER.log(Level.WARNING, "Can not find the sign[id={0}], returns a default sign[id=1]", signId);
		if (null == defaultSign) {
			throw new IllegalStateException("Can not find the default sign which id equals to 1");
		}

		return defaultSign;
	}

	/**
	 * Determines the specified article has updated.
	 * 
	 * @param article
	 *            the specified article
	 * @return {@code true} if it has updated, {@code false} otherwise
	 * @throws JSONException
	 *             json exception
	 */
	public boolean hasUpdated(final JSONObject article) throws JSONException {
		final Date updateDate = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
		final Date createDate = (Date) article.get(Article.ARTICLE_CREATE_DATE);

		return !createDate.equals(updateDate);
	}

	/**
	 * Determines the specified article had been published.
	 * 
	 * @param article
	 *            the specified article
	 * @return {@code true} if it had been published, {@code false} otherwise
	 * @throws JSONException
	 *             json exception
	 */
	public boolean hadBeenPublished(final JSONObject article) throws JSONException {
		return article.getBoolean(Article.ARTICLE_HAD_BEEN_PUBLISHED);
	}

	/**
	 * Gets all unpublished articles.
	 * 
	 * @return articles all unpublished articles
	 * @throws RepositoryException
	 *             repository exception
	 * @throws JSONException
	 *             json exception
	 */
	public List<JSONObject> getUnpublishedArticles() throws RepositoryException, JSONException {
		final Map<String, SortDirection> sorts = new HashMap<String, SortDirection>();
		sorts.put(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
		sorts.put(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
		final Query query = new Query().setFilter(new PropertyFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true));
		final JSONObject result = articleRepository.get(query);
		final JSONArray articles = result.getJSONArray(Keys.RESULTS);

		return CollectionUtils.jsonArrayToList(articles);
	}

	/**
	 * Gets the {@link Articles} singleton.
	 * 
	 * @return the singleton
	 */
	public static Articles getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Private default constructor.
	 */
	private Articles() {
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
		private static final Articles SINGLETON = new Articles();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
