package me.xfl.dragon.filter;

import java.io.IOException;
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
import me.xfl.dragon.model.Page;
import me.xfl.dragon.repository.ArticleRepository;
import me.xfl.dragon.repository.PageRepository;
import me.xfl.dragon.repository.impl.ArticleRepositoryImpl;
import me.xfl.dragon.repository.impl.PageRepositoryImpl;
import me.xfl.dragon.util.Articles;
import me.xfl.dragon.util.Permalinks;
import me.xfl.jugg.Juggs;
import me.xfl.jugg.Keys;
import me.xfl.jugg.repository.RepositoryException;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.HTTPRequestDispatcher;
import me.xfl.jugg.servlet.HTTPRequestMethod;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * Article/Page permalink filter.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.6, May 17, 2012
 * @since 0.3.1
 * @see org.b3log.solo.processor.ArticleProcessor#showArticle(org.b3log.latke.servlet.HTTPRequestContext,
 *      javax.servlet.http.HttpServletRequest,
 *      javax.servlet.http.HttpServletResponse)
 * @see org.b3log.solo.processor.PageProcessor#showPage(org.b3log.latke.servlet.HTTPRequestContext)
 */
public final class PermalinkFilter implements Filter {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PermalinkFilter.class.getName());
	/**
	 * Article repository.
	 */
	private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
	/**
	 * Page repository.
	 */
	private PageRepository pageRepository = PageRepositoryImpl.getInstance();
	/**
	 * Article utilities.
	 */
	private Articles articles = Articles.getInstance();

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Tries to dispatch request to article processor.
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
		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		final String requestURI = httpServletRequest.getRequestURI();
		LOGGER.log(Level.FINER, "Request URI[{0}]", requestURI);

		final String contextPath = Juggs.getContextPath();
		final String permalink = StringUtils.substringAfter(requestURI, contextPath);

		if (Permalinks.invalidPermalinkFormat(permalink)) {
			LOGGER.log(Level.FINER, "Skip filter request[URI={0}]", permalink);
			chain.doFilter(request, response);

			return;
		}

		JSONObject article;
		JSONObject page = null;
		try {
			article = articleRepository.getByPermalink(permalink);
			if (null == article) {
				page = pageRepository.getByPermalink(permalink);
			}

			if (null == page && null == article) {
				LOGGER.log(Level.FINER, "Not found article/page with permalink[{0}]", permalink);
				chain.doFilter(request, response);

				return;
			}
		} catch (final RepositoryException e) {
			LOGGER.log(Level.SEVERE, "Processes article permalink filter failed", e);
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		// If requests an article and the article need view passowrd, sends
		// redirect to the password form
		if (null != article && articles.needViewPwd(httpServletRequest, article)) {
			try {
				httpServletResponse.sendRedirect(Juggs.getServePath() + "/console/article-pwd"
						+ articles.buildArticleViewPwdFormParameters(article));
				return;
			} catch (final Exception e) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}

		dispatchToArticleOrPageProcessor(request, response, article, page);
	}

	/**
	 * Dispatches the specified request to the specified article or page
	 * processor with the specified response.
	 * 
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param article
	 *            the specified article
	 * @param page
	 *            the specified page
	 * @throws ServletException
	 *             servlet exception
	 * @throws IOException
	 *             io exception
	 * @see HTTPRequestDispatcher#dispatch(org.b3log.latke.servlet.HTTPRequestContext)
	 */
	private void dispatchToArticleOrPageProcessor(final ServletRequest request, final ServletResponse response, final JSONObject article,
			final JSONObject page) throws ServletException, IOException {
		final HTTPRequestContext context = new HTTPRequestContext();
		context.setRequest((HttpServletRequest) request);
		context.setResponse((HttpServletResponse) response);

		if (null != article) {
			request.setAttribute(Article.ARTICLE, article);
			request.setAttribute(Keys.HttpRequest.REQUEST_URI, Juggs.getContextPath() + "/article");
		} else {
			request.setAttribute(Page.PAGE, page);
			request.setAttribute(Keys.HttpRequest.REQUEST_URI, Juggs.getContextPath() + "/page");
		}

		request.setAttribute(Keys.HttpRequest.REQUEST_METHOD, HTTPRequestMethod.GET.name());

		HTTPRequestDispatcher.dispatch(context);
	}

	@Override
	public void destroy() {
	}
}
