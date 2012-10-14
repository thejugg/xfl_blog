package me.xfl.dragon.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.xfl.dragon.DragonServletListener;
import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Comment;
import me.xfl.dragon.model.Common;
import me.xfl.dragon.model.Page;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.CommentRepository;
import me.xfl.dragon.repository.PageRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.repository.impl.CommentRepositoryImpl;
import me.xfl.dragon.repository.impl.PageRepositoryImpl;
import me.xfl.jugg.Keys;
import me.xfl.jugg.model.Pagination;
import me.xfl.jugg.repository.Query;
import me.xfl.jugg.repository.SortDirection;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.util.Paginator;
import me.xfl.jugg.util.Strings;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Comment query service.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Dec 20, 2011
 * @since 0.3.5
 */
public final class CommentQueryService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CommentQueryService.class.getName());
	/**
	 * Comment repository.
	 */
	private CommentRepository commentRepository = CommentRepositoryImpl.getInstance();
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * Page repository.
	 */
	private PageRepository pageRepository = PageRepositoryImpl.getInstance();

	/**
	 * Gets comments with the specified request json object, request and
	 * response.
	 * 
	 * @param requestJSONObject
	 *            the specified request json object, for example,
	 * 
	 *            <pre>
	 * {
	 *     "paginationCurrentPageNum": 1,
	 *     "paginationPageSize": 20,
	 *     "paginationWindowSize": 10
	 * }, see {@link Pagination} for more details
	 * </pre>
	 * @return for example,
	 * 
	 *         <pre>
	 * {
	 *     "comments": [{
	 *         "oId": "",
	 *         "commentTitle": "",
	 *         "commentName": "",
	 *         "commentEmail": "",
	 *         "thumbnailUrl": "",
	 *         "commentURL": "",
	 *         "commentContent": "",
	 *         "commentTime": long,
	 *         "commentSharpURL": ""
	 *      }, ....]
	 *     "sc": "GET_COMMENTS_SUCC"
	 * }
	 * </pre>
	 * @throws ServiceException
	 *             service exception
	 * @see Pagination
	 */
	public JSONObject getComments(final JSONObject requestJSONObject) throws ServiceException {
		try {
			final JSONObject ret = new JSONObject();

			final int currentPageNum = requestJSONObject.getInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
			final int pageSize = requestJSONObject.getInt(Pagination.PAGINATION_PAGE_SIZE);
			final int windowSize = requestJSONObject.getInt(Pagination.PAGINATION_WINDOW_SIZE);

			final Query query = new Query().setCurrentPageNum(currentPageNum).setPageSize(pageSize)
					.addSort(Comment.COMMENT_DATE, SortDirection.DESCENDING);
			final JSONObject result = commentRepository.get(query);
			final JSONArray comments = result.getJSONArray(Keys.RESULTS);

			// Sets comment title and content escaping
			for (int i = 0; i < comments.length(); i++) {
				final JSONObject comment = comments.getJSONObject(i);
				String title;

				final String onType = comment.getString(Comment.COMMENT_ON_TYPE);
				final String onId = comment.getString(Comment.COMMENT_ON_ID);
				if (Article.ARTICLE.equals(onType)) {
					final JSONObject article = articleRepository.get(onId);
					title = article.getString(Article.ARTICLE_TITLE);
					comment.put(Common.TYPE, Common.ARTICLE_COMMENT_TYPE);
				} else { // It's a comment of page
					final JSONObject page = pageRepository.get(onId);
					title = page.getString(Page.PAGE_TITLE);
					comment.put(Common.TYPE, Common.PAGE_COMMENT_TYPE);
				}

				comment.put(Common.COMMENT_TITLE, title);

				comment.put(Comment.COMMENT_TIME, ((Date) comment.get(Comment.COMMENT_DATE)).getTime());
				comment.remove(Comment.COMMENT_DATE);

				final String content = comment.getString(Comment.COMMENT_CONTENT).replaceAll(DragonServletListener.ENTER_ESC, "<br/>");
				comment.put(Comment.COMMENT_CONTENT, content);
			}

			final int pageCount = result.getJSONObject(Pagination.PAGINATION).getInt(Pagination.PAGINATION_PAGE_COUNT);
			final JSONObject pagination = new JSONObject();
			final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
			pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
			pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

			ret.put(Comment.COMMENTS, comments);
			ret.put(Pagination.PAGINATION, pagination);

			return ret;
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Gets comments failed", e);

			throw new ServiceException(e);
		}
	}

	/**
	 * Gets comments of an article or page specified by the on id.
	 * 
	 * @param onId
	 *            the specified on id
	 * @return a list of comments, returns an empty list if not found
	 * @throws ServiceException
	 *             repository exception
	 */
	public List<JSONObject> getComments(final String onId) throws ServiceException {
		try {
			final List<JSONObject> ret = new ArrayList<JSONObject>();

			final List<JSONObject> comments = commentRepository.getComments(onId, 1, Integer.MAX_VALUE);
			for (final JSONObject comment : comments) {
				final String content = comment.getString(Comment.COMMENT_CONTENT).replaceAll(DragonServletListener.ENTER_ESC, "<br/>");
				comment.put(Comment.COMMENT_CONTENT, content);
				comment.put(Comment.COMMENT_TIME, ((Date) comment.get(Comment.COMMENT_DATE)).getTime());
				comment.put(Comment.COMMENT_NAME, StringEscapeUtils.escapeHtml(comment.getString(Comment.COMMENT_NAME)));
				comment.put(Comment.COMMENT_URL, StringEscapeUtils.escapeHtml(comment.getString(Comment.COMMENT_URL)));
				comment.put(Common.IS_REPLY, false); // Assumes this comment is
														// not a reply

				if (!Strings.isEmptyOrNull(comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID))) {
					// This comment is a reply
					comment.put(Common.IS_REPLY, true);
				}

				ret.add(comment);
			}

			return ret;
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Gets comments failed", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * Private constructor.
	 */
	private CommentQueryService() {
	}

	/**
	 * Gets the {@link CommentQueryService} singleton.
	 * 
	 * @return the singleton
	 */
	public static CommentQueryService getInstance() {
		return SingletonHolder.SINGLETON;
	}

	/**
	 * Singleton holder.
	 * 
	 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
	 * @version 1.0.0.0, Oct 18, 2011
	 */
	private static final class SingletonHolder {

		/**
		 * Singleton.
		 */
		private static final CommentQueryService SINGLETON = new CommentQueryService();

		/**
		 * Private default constructor.
		 */
		private SingletonHolder() {
		}
	}
}
