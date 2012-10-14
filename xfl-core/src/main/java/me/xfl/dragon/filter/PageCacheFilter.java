package me.xfl.dragon.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.model.Article;
import me.xfl.dragon.model.Common;
import me.xfl.dragon.model.PageTypes;
import me.xfl.dragon.processor.util.TopBars;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.util.Articles;
import me.xfl.dragon.util.Statistics;
import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.cache.PageCaches;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.service.LangPropsService;
import me.xfl.jugg.service.ServiceException;
import me.xfl.jugg.util.StaticResources;
import me.xfl.jugg.util.Strings;

import org.apache.commons.lang.time.DateFormatUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page cache filter.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Jul 16, 2012
 * @since 0.3.1
 */
public final class PageCacheFilter implements Filter {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PageCacheFilter.class.getName());
	/**
	 * Statistic utilities.
	 */
	private Statistics statistics = Statistics.getInstance();
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * Language service.
	 */
	private LangPropsService langPropsService = LangPropsService.getInstance();
	/**
	 * Article utilities.
	 */
	private Articles articles = Articles.getInstance();

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Try to write response from cache.
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param chain
	 *            filter chain
	 * @throws IOException
	 *             io exception
	 * @throws ServletException
	 *             servlet exception
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
			ServletException {
		final long startTimeMillis = System.currentTimeMillis();
		request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);

		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final String requestURI = httpServletRequest.getRequestURI();
		LOGGER.log(Level.FINER, "Request URI[{0}]", requestURI);

		if (StaticResources.isStatic(httpServletRequest)) {
			final String path = httpServletRequest.getServletPath() + httpServletRequest.getPathInfo();
			LOGGER.log(Level.FINEST, "Requests a static resource, forwards to servlet[path={0}]", path);

			// 过滤器处理时，不进行后续过滤器链处理（FilterChain#doFilter()），而是直接转发请求给
			// Servlet（RequestDispatcher#forward()）。
			// 详见：http://88250.b3log.org/how-to-skip-filters-in-java
			request.getRequestDispatcher(path).forward(request, response);

			return;
		}

		if (!Juggs.isPageCacheEnabled()) {
			LOGGER.log(Level.FINEST, "Page cache is disabled");
			chain.doFilter(request, response);

			return;
		}

		final String skinDirName = (String) httpServletRequest.getAttribute(Keys.TEMAPLTE_DIR_NAME);
		if ("mobile".equals(skinDirName)) {
			// Mobile request, bypasses page caching
			chain.doFilter(request, response);

			return;
		}

		// 在HTTPRequestDispatcher中有重复设置
		String pageCacheKey;
		final String queryString = httpServletRequest.getQueryString();
		pageCacheKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);
		if (Strings.isEmptyOrNull(pageCacheKey)) {
			pageCacheKey = PageCaches.getPageCacheKey(requestURI, queryString);
			request.setAttribute(Keys.PAGE_CACHE_KEY, pageCacheKey);
		}

		// 除了第一访问之外，以后的访问都从缓存中读取页面（首页、文章、Tag等页面）内容，
		// 这样me.xfl.dragon.processor包中的xxxProcessor类就不在多次执行。
		// 这种缓存设计真COOL，赞一个！
		final JSONObject cachedPageContentObject = PageCaches.get(pageCacheKey, httpServletRequest, (HttpServletResponse) response);

		if (null == cachedPageContentObject) {
			LOGGER.log(Level.FINER, "Page cache miss for request URI[{0}]", requestURI);
			chain.doFilter(request, response);

			return;
		}

		final String cachedType = cachedPageContentObject.optString(PageCaches.CACHED_TYPE);

		try {
			// If cached an article that has view password, dispatches the
			// password form
			if (langPropsService.get(PageTypes.ARTICLE).equals(cachedType) && cachedPageContentObject.has(PageCaches.CACHED_PWD)) {
				JSONObject article = new JSONObject();

				final String articleId = cachedPageContentObject.optString(PageCaches.CACHED_OID);

				article.put(Keys.OBJECT_ID, articleId);
				article.put(Article.ARTICLE_VIEW_PWD, cachedPageContentObject.optString(PageCaches.CACHED_PWD));

				if (articles.needViewPwd(httpServletRequest, article)) {
					article = articleRepository.get(articleId); // Loads the
																// article
																// entity

					final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					try {
						httpServletResponse.sendRedirect(Juggs.getServePath() + "/console/article-pwd"
								+ articles.buildArticleViewPwdFormParameters(article));
						return;
					} catch (final Exception e) {
						httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
						return;
					}
				}
			}
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			chain.doFilter(request, response);
		}

		try {
			LOGGER.log(Level.FINEST, "Writes resposne for page[pageCacheKey={0}] from cache", pageCacheKey);
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			final PrintWriter writer = response.getWriter();
			String cachedPageContent = cachedPageContentObject.getString(PageCaches.CACHED_CONTENT);

			// 下面2行代码和FrontRenderer中有重复，不过此处是的TopBar需要随时更新。
			final String topBarHTML = TopBars.getTopBarHTML((HttpServletRequest) request, (HttpServletResponse) response);
			cachedPageContent = cachedPageContent.replace(Common.TOP_BAR_REPLACEMENT_FLAG, topBarHTML);

			final String cachedTitle = cachedPageContentObject.getString(PageCaches.CACHED_TITLE);
			LOGGER.log(Level.FINEST, "Cached value[key={0}, type={1}, title={2}]", new Object[] { pageCacheKey, cachedType, cachedTitle });

			statistics.incBlogViewCount((HttpServletRequest) request, (HttpServletResponse) response);

			final long endimeMillis = System.currentTimeMillis();
			final String dateString = DateFormatUtils.format(endimeMillis, "yyyy/MM/dd HH:mm:ss");
			final String msg = String.format("<!--Cached by XFL Dragon(%1$d ms), %2$s -->", endimeMillis - startTimeMillis, dateString);
			LOGGER.finer(msg);
			cachedPageContent += Strings.LINE_SEPARATOR + msg;
			writer.write(cachedPageContent);
			writer.flush();
			writer.close();
		} catch (final JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			chain.doFilter(request, response);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			chain.doFilter(request, response);
		} catch (final ServiceException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}
}
